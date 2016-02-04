package shared.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import shared.Entity;

/**
 * A networked long value entity field.
 * @author Chris
 *
 */
public class NetworkedLong extends NetworkedEntityField<Long> {

	public NetworkedLong(Entity entity, long l) {
		super(entity, l);
	}
	
	public NetworkedLong(Entity entity) {
		super(entity, (long)0);
	}

	@Override
	public void readFromNetStream(DataInputStream in) throws IOException {
		set(in.readLong());
	}
	
	@Override
	public void writeToNetStream(DataOutputStream out) throws IOException {
		out.writeLong(get());
	}

}
