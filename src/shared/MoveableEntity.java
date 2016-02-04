package shared;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.lwjgl.util.vector.Vector2f;

import shared.net.NetworkConstants;
import shared.net.NetworkedEntityField;
import shared.net.NetworkedVelocityInfo;

/**
 * A game entity that can have velocity.
 * @author Chris
 *
 */
public abstract class MoveableEntity extends Entity {
	
	
	//****Class variables****
	
	
	private Vector2f velocity;
	
	// Used for prediction
	public long velocitySequence;
	
	public NetworkedVelocityInfo networkedVelocityInfo;
	public Queue<VelocityInfo> pendingVelocityInfo = new LinkedList<VelocityInfo>();

	private VelocityInfo lastReceivedVelocityInfo = null;
	private VelocityInfo activeVelocityInfo;

	private Vector2f predictionError;

	//****Constructors****
	
	
	/**
	 * Constructor used to initialise entity from coordinates
	 * @param world The world that this entity will exist in
	 * @param x The x-coordinate of the entity
	 * @param y The y-coordinate of the entity
	 */
	public MoveableEntity(World world, float x, float y) {
		
		super(world, x, y);
		
		velocity = new Vector2f(0.0f, 0.0f);
		velocitySequence = 0;
		
		networkedVelocityInfo = new NetworkedVelocityInfo(this);
		
	}

	/**
	 * Constructor used to initialise entity over the network
	 * @param world The world that this entity will exist in
	 * @param in the network stream to read the entity information from
	 * @throws IOException error reading network stream
	 */
	public MoveableEntity(World world, DataInputStream in) throws IOException {
		
		super(world, in);
	
		networkedVelocityInfo = new NetworkedVelocityInfo(this);
		networkedVelocityInfo.readFromNetStream(in);

		velocity = networkedVelocityInfo.info.velocity;
		velocitySequence = networkedVelocityInfo.info.sequence;
		
	}
	
	
	//****Class methods****
	
	
	/**
	 * Serialises the entity properties for networking.
	 * @param out the stream to write to
	 * @throws IOException error writing to stream
	 */
	@Override
	public void writeToNetStream(DataOutputStream out) throws IOException {
		
		super.writeToNetStream(out);
		
		networkedVelocityInfo.writeToNetStream(out);
		
	}

	/**
	 * Method called to update the entity.
	 * @param delta time passed since last update
	 */
	@Override
	public void update(int delta) {
		
		if (this.world.isClient()) {
		
			updateVelocity();
			
		}
		
	}

	/**
	 * Updates the entity's velocity using networked info.
	 */
	private void updateVelocity() {
		if (shouldClearVelocityInfo()) {
			clearVelocityInfo();
			return;
		}
		
		if (shouldUpdateActiveVelocityInfo()) {	
			updateActiveVelocityInfo();
		}
		
		if (hasActiveVelocityInfo()) {
			if (isPredicted()) {
				compareActiveVelocityInfoWithPredictedInfo();
				
				setActiveVelocityInfoToNextPendingVelocity();
			} else {
				if (hasReachedActiveVelocityGoal()) {
					if (hasPendingVelocityInfo()) {
						setActiveVelocityInfoToNextPendingVelocity();
						initiateActiveVelocityInfo();			
					} else {
						clearActiveVelocity();
						clearLastReceivedVelocity();							
					}
				}
			}
			
		}
		
	}

	/**
	 * Checks if there is any pending velocity info available.
	 * @return whether or not there is any pending velocity info available
	 */
	private boolean hasPendingVelocityInfo() {
		return this.pendingVelocityInfo.size() > 0;
	}

	/**
	 * Checks if the entity has reached the active
	 * velocity's goal position. Only used for non-predicted entities.
	 * @return whether or not goal has been reached
	 */
	private boolean hasReachedActiveVelocityGoal() {
		if (getActiveVelocityGoal() == null)
			return false;
		
		// Get distance from current position to goal position
		Vector2f goalDirection = new Vector2f();
		Vector2f.sub(getActiveVelocityGoal(), this.getPosition(), goalDirection);
		
		return ((this.getWorld().getTime() > this.activeVelocityInfo.goalReachTimeLimit)
			|| (goalDirection.length() == 0 || this.getVelocity().length() == 0) // at goal
			|| (Math.abs(goalDirection.length()) < NetworkConstants.VELOCITY_INFO_GOAL_DISTANCE_THRESHOLD) // close enough to goal
			|| (goalDirection.getX() > 0 && this.getVelocity().getX() <= 0) // past goal
			|| (goalDirection.getX() <= 0 && this.getVelocity().getX() > 0) // past goal
			|| (goalDirection.getY() > 0 && this.getVelocity().getY() <= 0) // past goal
			|| (goalDirection.getY() <= 0 && this.getVelocity().getY() > 0)); // past goal
	}

	/**
	 * Returns the goal position of the active velocity info.
	 * This is the position that the velocity should be simulated
	 * until. Once the goal is reached, the velocity info should
	 * change. Only used for non-predicted entities.
	 * @return goal position or null if not received yet
	 */
	private Vector2f getActiveVelocityGoal() {
		return this.activeVelocityInfo.goalPosition;
	}

	/**
	 * Compares active velocity info sent by server with
	 * predicted info (position, velocity) calculated on client
	 * for predicted entities. Corrects client info if it's too far
	 * off from the server values. Ignores active server info if it doesn't
	 * correspond to the active client info by comparing the sequence values.
	 */
	private void compareActiveVelocityInfoWithPredictedInfo() {
		// Check velocity info from server matches predicted velocity info
		// for predicted entities if the server sequence is greater than
		// or equal to the client's predicted sequence
		if (this.activeVelocityInfo.sequence >= this.velocitySequence) {
			
			// Time passed since the velocity was set on the server
			float timePassed = this.getWorld().getTime()-this.activeVelocityInfo.velocitySetTime;
			// Position of the entity on the server
			Vector2f serverPosition = this.getWorld().physics.simulateEntityMovement(this, this.activeVelocityInfo.velocityStartPosition, this.activeVelocityInfo.velocity, timePassed);
			
			// Difference between client's predicted position
			// and server's position
			Vector2f predictedDif = new Vector2f();
			Vector2f.sub(serverPosition, this.getPosition(), predictedDif);
			
			//System.out.println("predictedDif: " + predictedDif.length());
			
			// Correct difference immediately if it's too large
			// otherwise store for correction later
			if (predictedDif.length() > NetworkConstants.PREDICTION_ERROR_THRESHOLD) {
		
				System.out.println("prediction error threshold exceeded");
				setPosition(serverPosition);
				
			} else {
				
				setPredictionError(predictedDif);
				
			}
		}
	}

	private boolean hasActiveVelocityInfo() {
		return this.activeVelocityInfo != null;
	}

	/**
	 * Updates the entity's active velocity info.
	 * For predicted entities, this simply stores
	 * the latest pending velocity info to use for
	 * comparing predicted info with server info.
	 * For non-predicted entities, this will
	 * set and use the velocity info if the
	 * required amount of time has passed
	 * since the velocity was set.
	 */
	private void updateActiveVelocityInfo() {

		// Peek info to check it's ready for use
		VelocityInfo info = this.pendingVelocityInfo.peek();
		
		if (isPredicted()) {
			
			setActiveVelocityInfoToNextPendingVelocity();
			
		} else {
			
			// Update velocity info for non-predicted entities
			// only if the required time has passed
			if (this.getWorld().getTime() - info.velocitySetTime > NetworkConstants.NET_MOVEMENT_DELAY_MS) {						
			
				setActiveVelocityInfoToNextPendingVelocity();
				initiateActiveVelocityInfo();
				
			}
			
		}
	
	}

	/**
	 * Clears active and pending velocity info.
	 * Sets the current velocity and position using the
	 * last info in the pending queue, if available.
	 */
	private void clearVelocityInfo() {
		VelocityInfo pendingInfo = null;
		
		// Get last pending velocity in queue if present
		while (hasPendingVelocityInfo()) {
			pendingInfo = this.pendingVelocityInfo.remove();				
		}

		// Set velocity/position immediately using last info received
		if (pendingInfo != null) {
			this.setVelocity(pendingInfo.velocity);
			this.setPosition(pendingInfo.velocityStartPosition);
			
			if (isPredicted())
				this.velocitySequence = pendingInfo.sequence;
		}
		
		clearActiveVelocity();		
	}

	/**
	 * Determines whether or not the entity's active and pending velocity should be cleared.
	 * @return should be cleared
	 */
	private boolean shouldClearVelocityInfo() {
		return !this.isActive() || this.pendingVelocityInfo.size() > NetworkConstants.MAX_PENDING_VELOCITIES;
	}

	/**
	 * Determines whether or not the entity's active velocity should be updated.
	 * @return should be updated
	 */
	private boolean shouldUpdateActiveVelocityInfo() {
		return this.activeVelocityInfo == null && hasPendingVelocityInfo();
	}

	/**
	 * Sets the last received velocity info to null.
	 */
	private void clearLastReceivedVelocity() {
		this.lastReceivedVelocityInfo = null;	
	}

	/**
	 * Called when a new active velocity is set for
	 * non-predicted entities.
	 */
	private void initiateActiveVelocityInfo() {
		setVelocity(activeVelocityInfo.velocity);
		setPosition(activeVelocityInfo.velocityStartPosition);	
	}

	/**
	 * Sets the active velocity to null.
	 */
	private void clearActiveVelocity() {
		this.activeVelocityInfo = null;
	}

	/**
	 * Sets the active velocity to the next pending velocity available.
	 * Uses null if no pending info is available.
	 */
	private void setActiveVelocityInfoToNextPendingVelocity() {
		if (hasPendingVelocityInfo()) {
			
			this.activeVelocityInfo = this.pendingVelocityInfo.remove();
			
		} else {
			
			this.activeVelocityInfo = null;
			
		}	
	}

	/**
	 * Called when a networked entity field is changed.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void onNetworkFieldChange(NetworkedEntityField field) {
		
		if (this.networkedVelocityInfo != null && this.networkedVelocityInfo.getFieldID() == field.getFieldID()) {
			
			try {

				// Set goal info for last received velocity info
				// using velocity info just received
				
				if (this.networkedVelocityInfo.info.velocity.length() == 0) {
					
					// For zero velocity, goal and start is the same
					this.networkedVelocityInfo.info.goalPosition = new Vector2f(this.networkedVelocityInfo.info.velocityStartPosition);
					this.networkedVelocityInfo.info.goalReachTimeLimit = this.networkedVelocityInfo.info.velocitySetTime + NetworkConstants.NET_MOVEMENT_DELAY_MS;
					
				}
				

				if (this.lastReceivedVelocityInfo != null && this.lastReceivedVelocityInfo.goalPosition == null) {
				
					this.lastReceivedVelocityInfo.goalPosition = new Vector2f(this.networkedVelocityInfo.info.velocityStartPosition);
					this.lastReceivedVelocityInfo.goalReachTimeLimit = this.networkedVelocityInfo.info.velocitySetTime + NetworkConstants.NET_MOVEMENT_DELAY_MS;
					
				}
				
				this.lastReceivedVelocityInfo = (VelocityInfo)this.networkedVelocityInfo.info.clone();
				this.pendingVelocityInfo.add(this.lastReceivedVelocityInfo);
				
			} catch (CloneNotSupportedException e) {
				
				e.printStackTrace();
				
			}
			
			this.networkedVelocityInfo.changed = false;
		}
	}
	
	
	//****Getters****
	
	
	/**
	 * Gets the entity's velocity
	 * @return velocity
	 */
	public Vector2f getVelocity() {
		
		return this.velocity;
		
	}
	
	/**
	 * Gets the entity's world position.
	 * Used for prediction.
	 * @return position
	 */
	public Vector2f getPosition() {

		return this.position.get();
		
	}
	
	/**
	 * States whether the entity is moveable or not
	 */
	@Override
	public boolean isMoveable() {
		
		return true;
		
	}
	
	
	//****Setters****
	
	
	/**
	 * Sets the entity's velocity.
	 * @param newVelocity velocity
	 */
	public void setVelocity(Vector2f newVelocity) {
		
		if (this.getWorld().isServer()) {
			
			this.networkedVelocityInfo.update(this.getPosition(), newVelocity);
			
		}
		
		if (this.isPredicted()) {
			
			this.velocitySequence++;
			
			if (this.predictionError != null) {
			
				this.getPosition().translate(predictionError.getX(), predictionError.getY());
				
				this.predictionError = null;
				
			}
		}
		
		this.velocity = newVelocity;
	}
	
	/**
	 * Set's the entity's prediction error
	 * @param err The error to be set
	 */
	private void setPredictionError(Vector2f err) {
		
		this.predictionError = err;
		
	}
	
}
