package shared;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import shared.entities.Player;

/**
 * Manages world physics and collisions.
 * @author Chris
 *
 */
public class Physics {

	// Used to scale physics with time
	public static final float TIME_SCALE = 15.0f;

	// List of all world entities
	private List<Entity> entities;
	
	// World
	private World world;
	
	/**
	 * Constructor.
	 * @param entities list of all world entities
	 */
	public Physics(List<Entity> entities, World world) {
		this.entities = entities;
		this.world = world;
	}

	public boolean intersectionCheck(Vector2f e1Pos, int e1Width, int e1Height, Vector2f e2Pos, int e2Width, int e2Height) {
		return (Math.abs(e1Pos.getX()-e2Pos.getX())*2.0f <= (float)(e1Width+e2Width) &&
				Math.abs(e1Pos.getY()-e2Pos.getY())*2.0f <= (float)(e1Height+e2Height));		
	}
		
	/**
	 * Update method to check for collisions, touches, etc.
	 * @param delta time passed since last update
	 */
	public void update(int delta) {
		synchronized (entities) {
			for (Entity entity1 : entities) {
				if (!entity1.isActive())
					continue;
				
				int entityDelta = delta;
				
				Vector2f newPos = simulateEntityMovement(entity1, entityDelta);
				
				if (newPos != null) {
					entity1.setLocalPosition(newPos);
				}
			}
		}
	}

	public Vector2f simulateEntityMovement(Entity entity, float delta) {
		
		if (!entity.isMoveable()) {
			return null;
		}
		
		MoveableEntity moveableEntity1 = (MoveableEntity)entity;
		
		// Get entity position		
		Vector2f startPosition = new Vector2f(moveableEntity1.getPosition());

		// Get entity velocity
		Vector2f velocity = new Vector2f(moveableEntity1.getVelocity());
		
		// Get new position
		Vector2f newPosition = simulateEntityMovement(entity, startPosition, velocity, delta);
		
		return newPosition;

	}
	
	public Vector2f simulateEntityMovement(Entity entity, Vector2f startPosition, Vector2f entityVelocity, float delta) {
		if (entityVelocity.getX() == 0.0f && entityVelocity.getY() == 0.0f)
			return startPosition;

		entityVelocity.scale(delta/TIME_SCALE);

		// Velocity
		Vector2f velocity = new Vector2f(entityVelocity);

		// Collision information
		boolean translateX = true;
		boolean translateY = true;

		// Step positions
		Vector2f ent1NewPosX = new Vector2f(startPosition);
		Vector2f ent1NewPosY = new Vector2f(startPosition);

		float MAX_MOVEMENT_STEP = 3.0f;
	
		while (velocity.length() > 0) {
			float dx = velocity.getX() >= 0 ? Math.min(velocity.getX(), MAX_MOVEMENT_STEP) : Math.max(velocity.getX(), -MAX_MOVEMENT_STEP);
			float dy = velocity.getY() >= 0 ? Math.min(velocity.getY(), MAX_MOVEMENT_STEP) : Math.max(velocity.getY(), -MAX_MOVEMENT_STEP);
			
			ent1NewPosX.translate(dx, 0);
			ent1NewPosY.translate(0, dy);
			
			// Check for touches and collisions
			for (Entity entity2 : entities) {
				if (entity == entity2)
					continue;
				
				Vector2f ent2Pos = entity2.getPosition();
				
				int ent1Width = entity.getBoundingBoxWidth();
				int ent1Height = entity.getBoundingBoxHeight();
				int ent2Width = entity2.getBoundingBoxWidth();
				int ent2Height = entity2.getBoundingBoxHeight();
				
				// Bounding box intersection check
				
				boolean xIntersection = intersectionCheck(ent1NewPosX, ent1Width, ent1Height, ent2Pos, ent2Width, ent2Height);
				boolean yIntersection = intersectionCheck(ent1NewPosY, ent1Width, ent1Height, ent2Pos, ent2Width, ent2Height);
				
				if (xIntersection || yIntersection) {						
					if (entity2.isTouchable()) {
						if (this.world.isServer()) {
							// Touch events
							entity.onTouch(entity2);
							entity2.onTouch(entity);
						}
					}
					
					if (entity.isSolid() && entity2.isSolid()) {
						// No player collisions (temp, shouldn't be here)
						if (entity instanceof Player && entity2 instanceof Player)
							continue;
	
						if (xIntersection)
							translateX = false;
						
						if (yIntersection)
							translateY = false;
						
						if (!translateX && !translateY)
							break;
					}
				}
			}

			if (translateX && translateY) {
				startPosition.translate(dx, dy);
			} else if (translateX) {
				startPosition.translate(dx, 0);
			} else if (translateY) {
				startPosition.translate(0, dy);
			} else {
				return startPosition;
			}
			
			//ent1NewPosX.translate(0, dy);
			//ent1NewPosY.translate(dx, 0);
	
			velocity.translate(-dx, -dy);
		}
		
		return startPosition;
	}

	// Source: https://community.oracle.com/thread/1264395?start=0&tstart=0
	public Point2D getIntersectionPoint(Line2D line1, Line2D line2) {
		if (!line1.intersectsLine(line2))
			return null;
		
		double px = line1.getX1();
		double py = line1.getY1();
		double rx = line1.getX2()-px;
		double ry = line1.getY2()-py;
		
		double qx = line2.getX1();
		double qy = line2.getY1();
	    double sx = line2.getX2()-qx;
	    double sy = line2.getY2()-qy;

	    double det = sx*ry - sy*rx;
	    
	    if (det == 0) {
	    	return null;
	    } else {
	    	double z = (sx*(qy-py)+sy*(px-qx))/det;
	        
	    	if (z==0 || z==1)
	    		return null; // intersection at end point!
	    	
	    	return new Point2D.Float((float)(px+z*rx), (float)(py+z*ry));
	    }
	}
  
	/**
	 * Tests for entity intersections with a given position and direction vector.
	 * @param pos start position
	 * @param dir direction vector
	 * @param ignore to ignore for intersection test
	 * @return list of intersections described by an entity and the points that were intersected
	 */
	public ArrayList<EntityIntersectionInfo> entityIntersectionTest(Vector2f pos, Vector2f dir, Entity ignore) {
		float x1 = pos.getX();
		float y1 = pos.getY();
		float x2 = x1+dir.getX();
		float y2 = y1+dir.getY();
		
		Line2D intersectionLine = new Line2D.Float(x1, y1, x2, y2);
		
		ArrayList<EntityIntersectionInfo> intersectionInfoList = null;
		
		for (Entity e : this.world.getEntities()) {
			if (e.equals(ignore))
				continue;
			
			if (!e.isSolid())
				continue;
			
			float xMin = e.getX()-e.getBoundingBoxWidth()/2;
			float xMax = e.getX()+e.getBoundingBoxWidth()/2;
			float yMin = e.getY()-e.getBoundingBoxHeight()/2;
			float yMax = e.getY()+e.getBoundingBoxHeight()/2;
			
			//Rectangle2D rect = new Rectangle2D.Float(xMin, yMin	, e.getBoundingBoxWidth(), e.getBoundingBoxHeight());
			
			Line2D l1 = new Line2D.Float(xMin, yMin, xMin, yMax); // left line
			Line2D l2 = new Line2D.Float(xMin, yMin, xMax, yMin); // bottom line
			Line2D l3 = new Line2D.Float(xMax, yMin, xMax, yMax); // right line
			Line2D l4 = new Line2D.Float(xMin, yMax, xMax, yMax); // top line
			
			Point2D p1 = getIntersectionPoint(intersectionLine, l1);
			Point2D p2 = getIntersectionPoint(intersectionLine, l2);
			Point2D p3 = getIntersectionPoint(intersectionLine, l3);
			Point2D p4 = getIntersectionPoint(intersectionLine, l4);
			
			if (p1 == null && p2 == null && p3 == null && p4 == null)
				continue; // no intersection
			
			EntityIntersectionInfo entityIntersectionInfo = new EntityIntersectionInfo(e);
			
			if (p1 != null) entityIntersectionInfo.addIntersectionPoint(p1);
			if (p2 != null) entityIntersectionInfo.addIntersectionPoint(p2);
			if (p3 != null) entityIntersectionInfo.addIntersectionPoint(p3);
			if (p4 != null) entityIntersectionInfo.addIntersectionPoint(p4);

			if (intersectionInfoList == null)
				intersectionInfoList = new ArrayList<EntityIntersectionInfo>();
			
			intersectionInfoList.add(entityIntersectionInfo);
			
			/*if (intersectionLine.intersects(rect)) {
				//System.out.println("intersection found with " + e);
				
				if (entityList == null)
					entityList = new ArrayList<Entity>();
				
				entityList.add(e);
			}*/
			
		}
		
		return intersectionInfoList;
	}
}
