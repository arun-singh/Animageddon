package shared.entities;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Color;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.opengl.Texture;

import client.ClientWorld;
import client.WorldView;
import server.ServerWorld;
import shared.Entity;
import shared.EntityIntersectionInfo;
import shared.MoveableEntity;
import shared.Team;
import shared.World;
import shared.GUI.Components.Label;
import shared.GUI.Components.RectangleComponent;
import shared.entities.animals.AnimalClass;
import shared.net.NetworkConstants;
import shared.net.NetworkedBoolean;
import shared.net.NetworkedEntity;
import shared.net.NetworkedEntityField;
import shared.net.NetworkedFloat;
import shared.net.NetworkedLong;
import shared.net.NetworkedString;
import shared.net.NetworkedTeam;
import shared.net.NetworkedVector;

/**
 * The player entity.
 * Each client has a single player entity.
 * @author Chris, Richard, Barney
 *
 */
public class Player extends MoveableEntity {
	
	
	//****Class variables****
	
	private static final long PLAYER_RESPAWN_TIME_MS = 2000;

	protected final int WIDTH = 40;
	protected final int HEIGHT = 40;

	private static final int HEALTH_BAR_WIDTH = 50;
	private static final int HEALTH_BAR_HEIGHT = 5;
	private static final int HEALTH_BAR_OFFSET_Y = 40;
	
	private static final int TEAM_NAME_OFFSET_X = (int)(-HEALTH_BAR_WIDTH/2.0f);
	private static final int TEAM_NAME_OFFSET_Y = HEALTH_BAR_OFFSET_Y+15;

	private static final int PLAYER_INFO_BG_WIDTH = HEALTH_BAR_WIDTH;
	private static final int PLAYER_INFO_BG_HEIGHT = 10;
	private static final int PLAYER_INFO_BG_OFFSET_X = (int)(-PLAYER_INFO_BG_WIDTH/2.0f);
	private static final int PLAYER_INFO_BG_OFFSET_Y = HEALTH_BAR_OFFSET_Y+PLAYER_INFO_BG_HEIGHT;
	private static final Color PLAYER_INFO_BG_COLOR = new Color(0.0f, 0.0f, 0.0f);
	
			
	private static final Color TEAM_MEMBER_INFO_COLOR = new Color(0.0f, 1.0f, 0.0f);
	private static final Color ENEMY_INFO_COLOR = new Color(1.0f, 0.0f, 0.0f);
	
	private Texture texture;
	private String imageName;
	
	private Texture gunTexture;
	private String gunTextureName;
	
	private Texture muzzleFlashTexture;
	private String muzzleFlashTextureName;
	
	private AnimalClass playerClass;
	
	private boolean isLocalPlayer = false;
	
	private Vector2f mousePos;
	
	private float gunOrientation;
	
	// Predicted values
	private float clientOrientation;
	private Vector2f clientAimVec = new Vector2f();
	
	// Networked values
	protected NetworkedEntity heldFlag;
	private NetworkedEntityField<Float> orientation;
	private NetworkedVector aimVec;
	private NetworkedBoolean networkedMuzzleFlash;
	private NetworkedBoolean isDead;
	private NetworkedLong health;
	private NetworkedString playerClassName;
	private NetworkedTeam team;
	
	private boolean drawMuzzleFlash;
	private long muzzleFlashRemoveTime;

	private long respawnTime = 0;
	private long nextShootTime = 0;
	
	private float speed;
	private int weaponDamage;
	private int weaponFireDelay;
	private float weaponFireRange;

	private Label teamLabel;
	
	private HashMap<Integer, Boolean> keysPressed = new HashMap<Integer, Boolean>();
	
	public static Audio gunShot;
	public static Audio deathSound;
	public static Audio animalSounds;
	
	private boolean playGunShot = false;
	private boolean playDeathSound = false;
	
	//****Constructors****
	
	
	/**
	 * Constructor used to initialise player on the server.
	 * @param world The world that this player will exist in
	 * @param x The x-coordinate of the player
	 * @param y The y-coordinate of the player
	 */
	public Player(World world, int x, int y) {
		
		super(world, x, y);
	
		createNetworkFields();
		
		this.mousePos = new Vector2f();
			
		calculateAimVec(new Vector2f(1.0f, 1.0f), world);
		
	}
	
	/**
	 * Constructor used to initialise player over the network on the client.
	 * @param world The world that this player will exist in
	 * @param in the network stream to read the player information from
	 * @throws IOException error reading network stream
	 */
	public Player(World world, DataInputStream in) throws IOException {
		
		super(world, in);
		
		createNetworkFields();
		readNetworkFields(in);
		
		this.setPlayerClass(playerClassName.get());
	
	}
	
	
	//****Class methods****
	
	private void createNetworkFields() {
		this.heldFlag = new NetworkedEntity(this);
		this.orientation = new NetworkedFloat(this);
		this.aimVec = new NetworkedVector(this);
		this.networkedMuzzleFlash = new NetworkedBoolean(this);
		this.isDead = new NetworkedBoolean(this);
		this.health = new NetworkedLong(this);
		this.playerClassName = new NetworkedString(this);
		this.team = new NetworkedTeam(this);
	}
	
	private void readNetworkFields(DataInputStream in) throws IOException {
		this.heldFlag.readFromNetStream(in);
		this.orientation.readFromNetStream(in);
		this.aimVec.readFromNetStream(in);
		this.networkedMuzzleFlash.readFromNetStream(in);
		this.isDead.readFromNetStream(in);
		this.health.readFromNetStream(in);
		this.playerClassName.readFromNetStream(in);
		this.team.readFromNetStream(in);
	}

	/**
	 * Serialises the player properties for networking.
	 * @param out the stream to write to
	 * @throws IOException error writing to stream
	 */
	@Override
	public void writeToNetStream(DataOutputStream out) throws IOException {
		
		super.writeToNetStream(out);
	
		this.heldFlag.writeToNetStream(out);
		this.orientation.writeToNetStream(out);
		this.aimVec.writeToNetStream(out);
		this.networkedMuzzleFlash.writeToNetStream(out);
		this.isDead.writeToNetStream(out);
		this.health.writeToNetStream(out);
		this.playerClassName.writeToNetStream(out);
		this.team.writeToNetStream(out);
		
	}
	
	/**
	 * Marks the player as dead.
	 */
	public void die() {
		
		setIsDead(true);
		
		setVelocity(new Vector2f(0.0f, 0.0f));
		
		//Drop flag if player is holding one
			
		Flag heldFlag = getHeldFlag();
		
		if (heldFlag != null) {
		
			setHeldFlag(null);
			heldFlag.setFlagHolder(null);
			
		}
		
	}
	
	/**
	 * Marks the player as dead and schedules them to respawn.
	 */
	public void dieAndRespawn() {
		
		this.respawnTime = this.getWorld().getTime() + PLAYER_RESPAWN_TIME_MS;
	
		die();
	

	}
	
	/**
	 * Re-spawns the player.
	 */
	public void respawn() {
	
		setPlayerClass(ServerWorld.getRandomAnimalClass());
		
		//reset health
		setHealth(getPlayerClass().getMaxHealth());
		
		// TODO: send to spawnpoint
		setPosition(getTeam().getSpawnPointCopy());
		
		setIsDead(false);
		
		processKeyboardInput();
		
	}
	
	/**
	 * Called when an entity touches this entity or this entity touches another entity.
	 * @param entity the other entity in involved in the touch event
	 */
	public void onTouch(Entity entity) {
			
		
		/*if (entity instanceof Player) {
		 
			Player player = (Player)entity;
			Flag playerFlag = player.getHeldFlag();
			
			if (playerFlag != null) {
			
				// Give player time to get away
				if (getWorld().getTime() - playerFlag.getLastStolenTime() > 500) {
				
					// Give flag to new player
					this.setHeldFlag(playerFlag);
					playerFlag.setFlagHolder(this);
					
					// Remove flag from old player
					player.setHeldFlag(null);
					
				}
				
			}
			
		}*/
		
	}
	
	/**
	 * Method called to update the entity.
	 * @param delta time passed since last update
	 */
	@Override
	public void update(int delta) {
		
		World thisWorld = this.getWorld();
		
		super.update(delta);

		if (getIsDead()) {	//player is dead

			if (thisWorld.isClient()) {
				if (this.playDeathSound) {
					deathSound.playAsSoundEffect(1f, 1f, false);
					this.playDeathSound = false;
				}
			} else {	//on server
				
				if (this.getWorld().getTime() > this.respawnTime) {	//respawn delay has elapsed
				
					respawn();
					
				}
				
			}
			
			return;
		}
		
		if (thisWorld.isClient()) {	//on client
			
			ClientWorld clientWorld = (ClientWorld)thisWorld;
			
			if (getIsLocalPlayer()) {	//this is the local player
				
				// Get the mouse coordinates relative to the window
		        Vector2f mousePos = new Vector2f(Mouse.getX(), Mouse.getY());
		        
		        // Convert the mouse coordinates so that they are relative to the map
		        Vector2f mouseMapPos = clientWorld.getView().mousePosToWorldPos(mousePos);
		        
		        //Calculate the aim vector
		        calculateAimVec(mouseMapPos, thisWorld);
		        
			}
	        
	        updateGunOrientation();
	        
			updateMuzzleFlash(thisWorld);
				
			if (this.playGunShot) {			
				gunShot.playAsSoundEffect(1f, 1f, false);
				
				this.playGunShot = false;
			}
		}
		
	}
	
	/**
	 * Monitors the muzzle flash timing
	 */
	private void updateMuzzleFlash(World thisWorld) {
		
		if (this.drawMuzzleFlash) {	//muzzle flash is being drawn
		
			if (thisWorld.getTime() >= this.muzzleFlashRemoveTime) {	//muzzle flash remove time has elapsed
			
				//Stop drawing muzzle flash
				this.drawMuzzleFlash = false;
				
			}
			
		}
		
	}

	/**
	 * Updates the orientation of the gun based on the aim vector
	 */
	private void updateGunOrientation() {
		
		//Calculates the gun orientation based on the aim vector
		float gunOrientation = (float)Math.toDegrees(Vector2f.angle(getAimVector(), new Vector2f(0.0f, 1.0f)));
		
		if (getAimVector().getX() < 0.0f)
			gunOrientation = 360.0f-gunOrientation;
		
		if (!Float.isNaN(gunOrientation)) 
			setGunOrientation(gunOrientation);
		
	}

	/**
	 * Calculates the aim vector which represents where the player is aiming
	 * @param mouseMapPos	the mouse position relative to the map
	 * @param thisWorld 	the world the player exists in
	 */
	public void calculateAimVec(Vector2f mouseMapPos, World thisWorld) {
		
		if (thisWorld.isClient()) {	//on client
		
			Vector2f.sub(mouseMapPos, getPosition(), clientAimVec);
	        clientAimVec.normalise();
	        
		} else {	//on server
			
			Vector2f.sub(mouseMapPos, getPosition(), aimVec.get());
	        aimVec.get().normalise();
	        
	        //Broadcast the aim vector to clients
	        aimVec.broadcast();
	        
		}
		
	}

	/**
	 * Draw player texture.
	 * Drawn in the center of the screen for the local player.
	 * Drawn in relative position for other players.
	 */
	@Override
	public void render(WorldView view) {
		
		/*if (getIsDead()) {	//do not draw if player is dead
		
			if (world.isClient() && isLocalPlayer) {
				
				if (!deathSoundPlayed) {
					deathSound.playAsSoundEffect(1f, 1f, false);
					deathSoundPlayed = true;
				}
				
			}
			
			return;
			
		}
		
		deathSoundPlayed = false;*/
		
		if (getIsDead()) {
			return;
		}
		
		prepareTextures();
			
		//get player orientation
		float playerOrientation = getOrientation();
		
		Vector2f playerPos = drawPlayer(view, playerOrientation);
		
		drawHealthBar(view, playerPos);
		drawPlayerInfo(view, playerPos);

		if (drawMuzzleFlash) {
			
			drawMuzzleFlash(view);

		}

		drawGun(view);

	}
	
	/**
	 * Prepares textures for rendering
	 */
	private void prepareTextures() {

		if (texture == null)
			texture = getPNGTexture(imageName);
		
		if (gunTexture == null)
			gunTexture = getPNGTexture(gunTextureName);
		
		if (muzzleFlashTexture == null)
			muzzleFlashTexture = getPNGTexture(muzzleFlashTextureName);
		
	}
	
	
	/**
	 * Draws the player
	 * @param view	the view the player will be drawn on
	 * @param playerOrientation	the player's orientation
	 * @return	the player's  position
	 */
	private Vector2f drawPlayer(WorldView view, float playerOrientation) {
		
		Vector2f pos = getPosition();

		view.drawWorldRectangleCentered(pos.getX(), pos.getY(), WIDTH, HEIGHT, texture, playerOrientation);
		
		return pos;
		
	}
	
	/**
	 * Draws the player's health bar
	 * @param view	the view the health bar will be drawn on
	 * @param pos	the player's position
	 */
	private void drawHealthBar(WorldView view, Vector2f pos) {

		float red;
		
		float green;
		
		float blue;
		
		
		if (isLocalPlayer) { //local player health - yello
			
			red = 0f;
			
			green = 1f;
			
			blue = 0f;
					
		} else if (world instanceof ClientWorld) {
			
			if (getTeam() != null) {
			
				if (getTeam() == ((ClientWorld) world).getLocalPlayer().getTeam()) {	//players on same team - green
	
					red = 0f;
					
					green = 1.0f;
					
					blue = 0f;
					
				} else {	//enemies - red
					
					red = 1f;
					
					green = 0f;
					
					blue = 0f;
					
				}
			
			} else {
				
				System.out.println("No team found for: " + this.toString());
				
				red = 0f;
				
				green = 0f;
				
				blue = 0f;
				
			}
			
		} else {
			
			red = 1f;
			
			green = 0f;
			
			blue = 0f;
			
		}

		// Draw the health bar
		float healthBarX = pos.getX()-HEALTH_BAR_WIDTH/2.0f;
		float playerHealthRatio = this.health.get()/((float)playerClass.getMaxHealth());
		int greenWidth = (int)(playerHealthRatio*HEALTH_BAR_WIDTH);
		
		// Background bar
		view.drawWorldRectangle(healthBarX, pos.getY()+HEALTH_BAR_OFFSET_Y, (int)HEALTH_BAR_WIDTH, (int)HEALTH_BAR_HEIGHT, 0.5f, 0f, 0f, true, null);
		
		// Health bar
		view.drawWorldRectangle(healthBarX, pos.getY()+HEALTH_BAR_OFFSET_Y, greenWidth, (int)HEALTH_BAR_HEIGHT, red, green, blue, true, null);
		
	}

	/**
	 * Draws the player's information above their character (e.g. name, team, etc.)
	 * @param view	the view the info will be drawn on
	 * @param pos	the player's position
	 */
	private void drawPlayerInfo(WorldView view, Vector2f pos) {
		//if (playerInfoBg == null)
		//	playerInfoBg = new RectangleComponent(this.getWorld().getGameWindow(), 0, 0, PLAYER_INFO_BG_WIDTH, PLAYER_INFO_BG_HEIGHT, PLAYER_INFO_BG_COLOR);
		if (teamLabel == null)
			teamLabel = new Label(this.getWorld().getGameWindow(), 0, 0, "", null, 9.0f);
	
		ClientWorld world = (ClientWorld)this.getWorld();
		Player localPlayer = world.getLocalPlayer();
		Team localPlayerTeam = localPlayer.getTeam();
		
		if (this.getIsLocalPlayer()) {
			this.teamLabel.setColor(TEAM_MEMBER_INFO_COLOR);
			this.teamLabel.setLabelText(this.getTeam().getTeamName() + " (You)");	
		} else if (this.getTeam().getID() == localPlayerTeam.getID()) {
			this.teamLabel.setColor(TEAM_MEMBER_INFO_COLOR);
			this.teamLabel.setLabelText(this.getTeam().getTeamName() + " (Friend)");
		} else {
			this.teamLabel.setColor(ENEMY_INFO_COLOR);
			this.teamLabel.setLabelText(this.getTeam().getTeamName() + " (Enemy)");
		}
		
		//view.drawGUIComponent(this.playerInfoBg, pos.getX()+PLAYER_INFO_BG_OFFSET_X, pos.getY()+PLAYER_INFO_BG_OFFSET_Y);
		view.drawGUIComponent(this.teamLabel, pos.getX()+TEAM_NAME_OFFSET_X, pos.getY()+TEAM_NAME_OFFSET_Y);
	}
	
	/**
	 * Draws the player's gun
	 * @param view	the view the gun will be drawn on
	 */
	private void drawGun(WorldView view) {

		Vector2f gunPos = getGunPosition();
		view.drawWorldRectangleCentered(gunPos.getX(), gunPos.getY(), playerClass.getWeaponSize(), playerClass.getWeaponSize(), gunTexture, gunOrientation);
		
	}

	/**
	 * Draws the muzzle flash
	 * @param view the view to be drawn on
	 */
	private void drawMuzzleFlash(WorldView view) {
		
		Vector2f muzzlePos = getGunPosition();
		view.drawWorldRectangleCentered(muzzlePos.getX(), muzzlePos.getY(), playerClass.getWeaponSize(), playerClass.getWeaponSize(), muzzleFlashTexture, gunOrientation);
		
	}

	/**
	 * Shoots the player's weapon.
	 */
	public void shoot() {
		
		if (this.getIsDead())	//player is dead
			return;
		
		if (this.getWorld().getTime() < this.nextShootTime )	//the world has not reached the next time to shoot
			return;
				
		//Set the next time to shoot
		this.nextShootTime = this.getWorld().getTime() + getWeaponFireDelay();
		
		//Get the aim vector
		Vector2f _aimVec = new Vector2f(getAimVector());
		_aimVec.scale(getWeaponFireRange());
		
		//Look for obstacles
		ArrayList<EntityIntersectionInfo> intersectionInfoList = this.getWorld().getPhysics().entityIntersectionTest(getPosition(), _aimVec, this);

		fireGun();

		if (getWorld().isClient()) {	//on client
			
			ClientWorld world = (ClientWorld)getWorld();
			
			try {
			
				world.getNetworkClient().sendGunShotInfo(intersectionInfoList);
				
			} catch (IOException e) {
				
				System.out.println("Error sending gun shot info...");
				
				e.printStackTrace();
				
			}
			
		} else {	//on server
			
			ServerWorld world = (ServerWorld)getWorld();
			
			world.handleGunShot(this, intersectionInfoList);
			
		}
	}	
	
	/**
	 * Does damage to a player
	 * @param damage	the damage to be done
	 */
	public void doDamage(long damage) {
		
		//decrease health
		setHealth(getHealth()-damage);
		
		//Test for death
		if (getHealth() <= 0) {
		
			dieAndRespawn();
			
		}
		
	}

	/**
	 * Rotates the player based on the new velocity
	 * @param newVelocity The new velocity following user input
	 */
	private void rotatePlayer(Vector2f newVelocity) {
		if (newVelocity != null) {
			
			//Calculate the new orientation
			float orientation = (float)Math.toDegrees(Vector2f.angle(newVelocity, new Vector2f(0.0f, 1.0f)));
			
			if (newVelocity.getX() < 0.0f) {
				
				orientation = 360.0f-orientation;
				
			}
			
			if (!Float.isNaN(orientation)) {
			
				setOrientation(orientation);
				
			}
		}
		
	}	

	/**
	 * Lets the program know that the player is firing
	 */
	public void fireGun() {
		
		
		if (this.getWorld().isClient()) {	//on client
			//gunShot.playAsSoundEffect(1f, 1f, false);
			this.playGunShot = true;
			this.drawMuzzleFlash = true;
			this.muzzleFlashRemoveTime = this.getWorld().getTime() + playerClass.getMuzzleFlashDurationMS();		
		} else {	//on server
			
			this.networkedMuzzleFlash.set(true);
			
		}
	}
	
	/**
	 * Called when a network entity field is changed,
	 * calls superclass method,
	 * handles a change* in the muzzle flash network field
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void onNetworkFieldChange(NetworkedEntityField field) {
		
		super.onNetworkFieldChange(field);
		
		if (field.getFieldID() == networkedMuzzleFlash.getFieldID()) {	//muzzleFlash field changed
		
			fireGun();
			networkedMuzzleFlash.set(false);
			
		} else if (field.getFieldID() == playerClassName.getFieldID()) {
			this.setPlayerClass(playerClassName.get());
		} else if (field.getFieldID() == isDead.getFieldID()) {
			if (this.getIsLocalPlayer()) {
				if (this.getIsDead()) { 
					this.playDeathSound = true;
				}
			}
			
			this.processKeyboardInput();
			
		}
	}

	/**
	 * Updates stored information about keys pressed.
	 * @param eventKey key pressed/released
	 * @param pressed press (true) or release (false)
	 */
	public void	updateKeyboardInput(int eventKey, boolean pressed) {
		keysPressed.put(eventKey, pressed);
		
		processKeyboardInput();
	}
	
	/**
	 * Processes keyboard input. Checks which keys are pressed and handles appropriately.
	 */
	public void processKeyboardInput() {
		// Ignore keyboard input if player is dead
		if (getIsDead())
			return;
	
		if (NetworkConstants.LOCAL_PLAYER_PREDICTION_ENABLED) {
	
			//Speed of the player
			float playerSpeed = getSpeed();
				
			//The new velocity to be set
			Vector2f newVelocity = new Vector2f(0.0f, 0.0f);
			
			if (isKeyDown(Keyboard.KEY_A))
				newVelocity.translate(-playerSpeed, 0.0f);
			
			if (isKeyDown(Keyboard.KEY_D))
				newVelocity.translate(playerSpeed, 0.0f);
				
			if (isKeyDown(Keyboard.KEY_W))
				newVelocity.translate(0.0f, playerSpeed);
			
			if (isKeyDown(Keyboard.KEY_S))
				newVelocity.translate(0.0f, -playerSpeed);
			
			setVelocity(newVelocity);
		}
	
		if (this.getWorld().isServer()) {
			if (isKeyDown(Keyboard.KEY_K))
				dieAndRespawn();
		}
	}
	
	//****Getters****
	
	
	/**
	 * Gets the player's health
	 * @return	health
	 */
	public long getHealth() {
		
		return this.health.get();
		
	}
	
	/**
	 * Gets whether or not this Player entity belongs to the local player.
	 * @return whether or not the Player entity is the local player
	 */
	public boolean getIsLocalPlayer() {
		
		return this.isLocalPlayer;
		
	}
	
	/**
	 * Gets whether or not the player is dead.
	 * @return true for dead, false for not dead (alive)
	 */
	public boolean getIsDead() {
	
		return this.isDead.get();
		
	}
	
	/**
	 * Gets the player's current animal class.
	 * @return animal class
	 */
	private AnimalClass getPlayerClass() {
		
		return this.playerClass;
		
	}

	/**
	 * Gets the Flag entity held by the Player.
	 * @return Flag entity if held, otherwise null
	 */
	public Flag getHeldFlag() {
		
		return (Flag)heldFlag.get();
		
	}
	
	/**
	 * Gets the team the player is currently on.
	 * @return player's team
	 */
	public Team getTeam() {
	
		return this.team.get();
		
	}
	
	/**
	 * Get's the mouse position
	 * @return mouse position
	 */
	public Vector2f getMousePos() {
	
		return mousePos;
		
	}
	
	/**
	 * Gets the position of the player's gun.
	 * @return position
	 */
	private Vector2f getGunPosition() {
		
		Vector2f p = new Vector2f(getPosition());	
		p.translate(getAimVector().getX()*(WIDTH/2+playerClass.getWeaponSize()/2), getAimVector().getY()*(HEIGHT/2+playerClass.getWeaponSize()/2));
		
		return p;
		
	}

	/**
	 * Gets the vector representing where the player is aiming
	 * @return	the aim vector
	 */
	private Vector2f getAimVector() {
		
		if (getIsLocalPlayer()) {
			
			return this.clientAimVec;
			
		} else {
			
			return this.aimVec.get();
			
		}
		
	}
	
	/**
	 * Gets the player's width
	 */
	public int getBoundingBoxWidth() {

		return WIDTH;
		
	}

	/**
	 * Gets the player's height
	 */
	public int getBoundingBoxHeight() {
		
		return HEIGHT;
		
	}

	/**
	 * Gets the player's orientation
	 * @return The player's orientation
	 */
	private float getOrientation() {
		
		if (getIsLocalPlayer()) {	//local player
			
			return clientOrientation;
			
		} else {	//not local player
			
			return this.orientation.get();
			
		}
		
	}
	
	/**
	 * Checks if a player has a key down.
	 * @param key the key to check
	 * @return whether or not the key is down
	 */
	public boolean isKeyDown(int key) {
		Boolean b = keysPressed.get(key);
		
		return b != null && b == true;
	}

	/**
	 * States whether the player is solid or not
	 */
	@Override
	public boolean isSolid() {
		return !getIsDead();
	}
	
	/**
	 * States whether the player is touchable or not
	 */
	@Override
	public boolean isTouchable() {
		return !getIsDead();
	}
	
	/**
	 * States whether the player is active or not
	 */
	@Override
	public boolean isActive() {
		return !getIsDead();
	}
	
	/**
	 * States whether the player is predicted or not
	 * true when this is the local player, false otherwise
	 */
	@Override
	public boolean isPredicted() {
		
		if (this.getWorld().isClient() && NetworkConstants.LOCAL_PLAYER_PREDICTION_ENABLED) {	//on client
			
			ClientWorld clientWorld = (ClientWorld)this.getWorld();
			
			return this == clientWorld.getLocalPlayer();
			
		}
		
		return false;
	}
	
	
	//****Setters****
	
	
	/**
	 * Sets the player's health
	 * @param health
	 */
	public void setHealth(long health) {
		
		this.health.set(health);
		
	}

	/**
	 * Sets the player's class
	 * @param animal the class to be set
	 */
	public void setPlayerClass(String animal) {
		
		if (this.getWorld().isServer())
			playerClassName.set(animal);
		
		// TODO: Make this set ingame
		// TODO: Tidy up
		
		Class<?> classFromName;
		
		try {
			
			classFromName = Class.forName("shared.entities.animals." + animal);
			playerClass = (AnimalClass) classFromName.newInstance();
				
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}		
				
		// Set class properties here
		setHealth(playerClass.getMaxHealth());
		setSpeed(playerClass.getSpeed());
		setWeaponDamage(playerClass.getWeaponDamage());
		setWeaponFireDelay(playerClass.getWeaponFireDelay());
		setWeaponFireRange(playerClass.getWeaponRange());
		
		this.imageName = playerClass.getTextureImage();
		this.texture = null;
		this.gunTextureName = playerClass.getWeaponImage();
		this.gunTexture = null;
		this.muzzleFlashTextureName = playerClass.getMuzzleFlashImage();
		this.muzzleFlashTexture = null;
	
	}
	
	/**
	 * Gets the damage the player's weapon does to other players.
	 * @return weapon damage
	 */
	public int getWeaponDamage() {
		return this.weaponDamage;
	}
	
	/**
	 * Sets the damage the player's weapon does to other players.
	 * @param damage weapon damage
	 */
	public void setWeaponDamage(int damage) {
		this.weaponDamage = damage;
	}
	
	/**
	 * Gets the delay between each shot of the player's weapon.
	 * @return the delay
	 */
	public int getWeaponFireDelay() {
		return this.weaponFireDelay;
	}
	
	/**
	 * Sets the delay between each shot of the player's weapon.
	 * @param delay the delay
	 */
	public void setWeaponFireDelay(int delay) {
		this.weaponFireDelay = delay;
	}
	
	/**
	 * Gets the range of the player's weapon.
	 * @return the range
	 */
	public float getWeaponFireRange() {
		return this.weaponFireRange;
	}
	
	/**
	 * Sets the range of the player's weapon.
	 * @param range the range
	 */
	public void setWeaponFireRange(float range) {
		this.weaponFireRange = range;
	}

	/**
	 * Gets the player's movement speed.
	 * @return speed
	 */
	public float getSpeed() {
		return this.speed;
	}
	
	/**
	 * Sets the player's movement speed.
	 * @param speed speed
	 */
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	/**
	 * Sets whether or not this Player entity belongs to the local player.
	 * Used on the client.
	 * @param b true for local player, false otherwise
	 */
	public void setIsLocalPlayer(boolean b) {
	
		this.isLocalPlayer = b;
		
		if (b) {
			animalSounds.playAsSoundEffect(1f, 1f, false);
		}
		
	}
	
	/**
	 * Sets the player's dead/alive status.
	 * @param b true for dead, false for not dead (alive)
	 */
	public void setIsDead(boolean b) {
		
		this.isDead.set(b);
		
	}
	
	/**
	 * Sets the team the player is currently on..
	 * @param team the team
	 */
	public void setTeam(Team team) {
		
		this.team.set(team);
		
	}
	
	/**
	 * Sets the Flag entity held by the Player.
	 * @param flag the Flag entity
	 */
	public void setHeldFlag(Flag flag) {
		
		//heldFlag.setDelayedBroadcast(flag, NetworkConstants.NET_MOVEMENT_DELAY_MS);
		heldFlag.set(flag);
		
	}
	
	/**
	 * Sets the mouse position
	 * @param mousePos mouse position to be set
	 */
	public void setMousePos(Vector2f mousePos) {
		
		this.mousePos = mousePos;
		
	}
	
	/**
	 * Sets the player velocity
	 * @param newVelocity the velocity to be set
	 */
	@Override
	public void setVelocity(Vector2f newVelocity) {
		
		super.setVelocity(newVelocity);
		
		rotatePlayer(newVelocity);
		
	}
	
	/**
	 * Sets the player's orientation
	 * @param orientation	the orientation of the player
	 */
	public void setOrientation(float orientation) {
		
		if (getIsLocalPlayer()) {
		
			this.clientOrientation = orientation;
			
		} else {
			
			this.orientation.setNoBroadcast(orientation);
			
		}
		
	}
	
	/**
	 * Sets the player's gun's orientation
	 * @param orientation the orientation of the player's gun
	 */
	public void setGunOrientation(float orientation) {
		
		this.gunOrientation = orientation;
		
	}
	
}
