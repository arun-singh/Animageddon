package shared;

import org.lwjgl.util.vector.Vector2f;

/**
 * Stores information about an entity's velocity.
 * Used for networking.
 * @author Chris
 *
 */
public class VelocityInfo implements Cloneable {

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public Vector2f velocityStartPosition;
	public Vector2f velocity;
	public long velocitySetTime;
	public int sequence;

	public Vector2f goalPosition;
	public long goalReachTimeLimit;
	
}
