package shared.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import shared.Entity;

/**
 * A networked boolean entity field.
 * @author Chris
 *
 */
public class NetworkedBoolean extends NetworkedEntityField<Boolean> {

	public NetworkedBoolean(Entity entity, boolean b) {
		super(entity, b);
	}
	
	public NetworkedBoolean(Entity entity) {
		super(entity, false);
	}

	@Override
	public void readFromNetStream(DataInputStream in) throws IOException {
		set(in.readBoolean());
	}
	
	@Override
	public void writeToNetStream(DataOutputStream out) throws IOException {
		out.writeBoolean(get());
	}

}
