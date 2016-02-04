package shared.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import shared.Entity;

/**
 * A networked string value entity field.
 * @author Chris
 *
 */
public class NetworkedString extends NetworkedEntityField<String> {

	public NetworkedString(Entity entity, String s) {
		super(entity, s);
	}
	
	public NetworkedString(Entity entity) {
		super(entity, "");
	}

	@Override
	public void readFromNetStream(DataInputStream in) throws IOException {
		set(in.readUTF());
	}
	
	@Override
	public void writeToNetStream(DataOutputStream out) throws IOException {
		out.writeUTF(get());
	}

}
