package shared;

import java.awt.geom.Point2D;
import java.io.DataOutputStream;
import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;

/**
 * Stores information about an intersection with an Entity.
 * Used for player shooting.
 * @author Chris
 *
 */
public class EntityIntersectionInfo {

	private Entity entity;
	private ArrayList<Vector2f> intersectionPoints;

	public EntityIntersectionInfo(Entity e) {
		this.entity = e;
		this.intersectionPoints = new ArrayList<Vector2f>();
	}

	public Entity getEntity() {
		return entity;
	}
	
	public void addIntersectionPoint(Vector2f p) {
		this.intersectionPoints.add(p);
	}
	
	public void addIntersectionPoint(Point2D p) {
		addIntersectionPoint(new Vector2f((float)p.getX(), (float)p.getY()));
	}

	public ArrayList<Vector2f> getIntersectionPoints() {
		return this.intersectionPoints;
	}

}
