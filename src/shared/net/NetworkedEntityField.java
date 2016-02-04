package shared.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import server.ServerWorld;
import shared.Entity;
import shared.World;

public abstract class NetworkedEntityField<T> implements Cloneable {

	protected Entity entity;
	protected int fieldID;
	protected T value;
	
	protected boolean valid;
	
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public NetworkedEntityField(Entity entity, T initialValue) {
		this.entity = entity;
		this.fieldID = entity.getNextFieldID();
		this.value = initialValue;
		
		markValid();
		
		if (entity.getWorld().isClient()) {
			entity.getNetworkedEntityFields().add(this);
		}
	}

	public NetworkedEntityField(Entity entity) {
		this(entity, null);
	}

	public NetworkedEntityField() {
	}

	public Entity getParentEntity() {
		return this.entity;
	}
	
	public int getParentEntityID() {
		if (getParentEntity() == null)
			return -1;
		
		return getParentEntity().getID();
	}
	
	public int getFieldID() {
		return this.fieldID;
	}
	
	public T get() {
		return this.value;
	}
		
	public void set(T newValue) {
		//System.out.println("set field: " + (newValue == null ? "null" : newValue.toString()));
		
		setNoBroadcast(newValue);

		markValid();
		
		if (entity.getWorld().isServer())	
			broadcast();
	}
	
	public void setNoBroadcast(T newValue) {
		this.value = newValue;
	}
	
	public void broadcast() {
		ServerWorld serverWorld = (ServerWorld)entity.getWorld();
		serverWorld.broadcastEntityUpdate(this);		
	}
	
	public abstract void readFromNetStream(DataInputStream in) throws IOException;
	public abstract void writeToNetStream(DataOutputStream out) throws IOException;

	/**
	 * Marks the field as valid, i.e. can be used.
	 * Fields are marked as valid automatically when set.
	 */
	public void markValid() {
		this.valid = true;
	}

	/**
	 * Marks the field as invalid, i.e. can't be used.
	 * Used to wait for a new value to be sent.
	 */
	public void markInvalid() {
		this.valid = false;
	}
	
	/**
	 * Returns whether or not the field is valid/ready for use.
	 * @return valid
	 */
	public boolean isValid() {
		return this.valid;
	}
}
