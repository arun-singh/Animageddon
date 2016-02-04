package shared.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import shared.Entity;

/**
 * A networked floating point value entity field.
 * @author Chris
 *
 */
public class NetworkedFloat extends NetworkedEntityField<Float> {

	public NetworkedFloat(Entity entity, float f) {
		super(entity, f);
	}
	
	public NetworkedFloat(Entity entity) {
		super(entity, 0.0f);
	}

	@Override
	public void readFromNetStream(DataInputStream in) throws IOException {
		set(in.readFloat());
	}
	
	@Override
	public void writeToNetStream(DataOutputStream out) throws IOException {
		out.writeFloat(get());
	}

}
