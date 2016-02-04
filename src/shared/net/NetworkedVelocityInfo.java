package shared.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.lwjgl.util.vector.Vector2f;

import shared.Entity;
import shared.VelocityInfo;

/**
 * A networked VelocityInfo entity field.
 * @author Chris
 *
 */
public class NetworkedVelocityInfo extends NetworkedEntityField {

	public VelocityInfo info;
	public boolean changed;

	public NetworkedVelocityInfo(Entity entity) {
		super(entity);
		
		this.info = new VelocityInfo();
		this.info.velocityStartPosition = new Vector2f(0.0f, 0.0f);
		this.info.velocity = new Vector2f(0.0f, 0.0f);
		this.info.velocitySetTime = 0;
		this.info.sequence = 0;
		this.changed = false;
	}

	@Override
	public void readFromNetStream(DataInputStream in) throws IOException {
		this.info = new VelocityInfo();
		this.info.velocityStartPosition = new Vector2f(in.readFloat(), in.readFloat());
		this.info.velocity = new Vector2f(in.readFloat(), in.readFloat());
		this.info.velocitySetTime = in.readLong();
		this.info.sequence = in.readInt();
		this.changed = in.readBoolean();
	}

	@Override
	public void writeToNetStream(DataOutputStream out) throws IOException {
		out.writeFloat(this.info.velocityStartPosition.getX());
		out.writeFloat(this.info.velocityStartPosition.getY());
		out.writeFloat(this.info.velocity.getX());
		out.writeFloat(this.info.velocity.getY());
		out.writeLong(this.info.velocitySetTime);
		out.writeInt(this.info.sequence);
		out.writeBoolean(this.changed);
	}
	
	public void update(Vector2f position, Vector2f velocity) {
		this.info.velocityStartPosition = new Vector2f(position);
		this.info.velocity = new Vector2f(velocity);
		this.info.sequence++;
		this.info.velocitySetTime = this.getParentEntity().getWorld().getTime();
		this.changed = true;
		
		this.broadcast();
	}

}
