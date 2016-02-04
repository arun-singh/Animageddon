package shared.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.lwjgl.util.vector.Vector2f;

import shared.Entity;

/**
 * A networked Vector2f entity field.
 * @author Chris
 *
 */
public class NetworkedVector extends NetworkedEntityField<Vector2f> {

	public NetworkedVector(Entity entity, Vector2f vec) {
		super(entity, vec);
	}

	public NetworkedVector(Entity entity) {
		super(entity, new Vector2f(0.0f, 0.0f));
	}

	@Override
	public void readFromNetStream(DataInputStream in) throws IOException {
		//Vector2f vec = get();
		//vec.set(in.readFloat(), in.readFloat());
		set(new Vector2f(in.readFloat(), in.readFloat()));
	}

	@Override
	public void writeToNetStream(DataOutputStream out) throws IOException {
		out.writeFloat(get().getX());
		out.writeFloat(get().getY());
	}

}
