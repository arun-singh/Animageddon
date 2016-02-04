package shared.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import shared.Entity;

/**
 * A networked entity entity field.
 * @author Chris
 *
 */
public class NetworkedEntity extends NetworkedEntityField<Entity> {

	private int entityID = -1;
	
	public NetworkedEntity(Entity entity, Entity entityField) {
		super(entity, entityField);
	}

	public NetworkedEntity(Entity entity) {
		super(entity, null);
	}

	@Override
	public void readFromNetStream(DataInputStream in) throws IOException {
		this.entityID = in.readInt();
		
		if (entityID == -1) {
			set(null);
			return;
		}
		
		for (Entity worldEntity : this.entity.getWorld().getEntities()) {
			if (worldEntity.getID() == entityID) {
				set(worldEntity);
				break;
			}
		}
	}
	
	@Override
	public void writeToNetStream(DataOutputStream out) throws IOException {
		out.writeInt(getEntityFieldEntityID());
	}

	public int getEntityFieldEntityID() {
		return entityID;		
	}
	
	public Entity get() {
		Entity entity = super.get();
		
		if (entity != null || entityID == -1)
			return entity;
		
		// Check if entity exists in world here as well
		// in case the entity just didn't exist during init
		for (Entity worldEntity : this.entity.getWorld().getEntities()) {
			if (worldEntity.getID() == entityID) {
				set(worldEntity);
				return worldEntity;
			}
		}
		
		return null;
	}
	
	public void set(Entity entity) {
		if (entity == null)
			this.entityID = -1;
		else
			this.entityID = entity.getID();
		
		super.set(entity);
	}
}
