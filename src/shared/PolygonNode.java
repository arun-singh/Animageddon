package shared;

import java.util.ArrayList;
import org.lwjgl.util.vector.Vector2f;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A node, based on a polygon, used in a pathfinding algorithm
 * @author Barney
 */
class PolygonNode {
	// The polygon to wrap the A* node functionality around
	private final Polygon polygon;

	// The cost required to get to this node from the start node
	private float g;

	// The estimated cost of the path via this node
	private float f;

	/**
	 * Constructor simply acts as a wrapper around the supplied polygon, allowing it to be used as an A* search node more easily
	 * @param p The polygon to wrap the node functionality around
	 */
	public PolygonNode(Polygon p) {
		// Store the polygon
		this.polygon = p;
	}

	/**
	 * Gets the polygon, which this class acts as a wrapper around
	 * @return The polygon
	 */
	public Polygon getPolygon() {
		return this.polygon;
	}

	/**
	 * Gets the neighbour polygon nodes of this node
	 * @return A list of the neighbouring polygon nodes
	 */
	public ArrayList<PolygonNode> getNeighbours() {
		// Get the list of neighbours from the polygon and construct an arraylist of polygon nodes of the same size
		ArrayList<Polygon> neighbours = this.getPolygon().getNeighbours();
		ArrayList<PolygonNode> neighbourNodes = new ArrayList<PolygonNode>(neighbours.size());

		// Wrap each polygon in a PolygonNode class and add it to the list of neighbour nodes
		for (Polygon p : neighbours) {
			neighbourNodes.add(new PolygonNode(p));
		}

		// Return the list
		return neighbourNodes;
	}

	/**
	 * Checks whether an object is equal to this PolygonNode, it is equal when the polygons they wrap around are the same
	 * @param obj The object to check for equality with
	 * @return True if the other object is a PolygonNode and wraps around the same polygon and false otherwise
	 */
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof PolygonNode) {
			PolygonNode otherNode = (PolygonNode)obj;
			if (otherNode.getPolygon().equals(this.getPolygon())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Quick equals type method that can be used to check whether two objects are equal, used by the HashMap as well as some of the other Maps
	 * @return Returns a hashcode, a code that identifies this object
	 */
	public int hashCode() {
		// Use the hashcodebuilder class to help us build a hashcode
		// Seed with two random prime numbers
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);

		// Make every vertice of the polygon affect the hashcode
		ArrayList<Vector2f> vs = this.getPolygon().getVertices();
		for (Vector2f v : vs) {
			// Append the floats
			hcb.append(v.getX());
			hcb.append(v.getY());
		}

		// Return the generated hashcode
		return hcb.toHashCode();
	}

	/**
	 * Gets the g value, which describes the actual cost to get to this node from the start of the path
	 * @return The value of g
	 */
	public float getG() {
		return this.g;
	}

	/**
	 * Sets the value of g for this class, which describes the actual cost to get to this node from the start of the path
	 * @param g The new value of g
	 */
	public void setG(float g) {
		this.g = g;
	}

	/**
	 * Gets the value of f for this class, which describes the estimated cost of the entire path via this node
	 * @return The value of f
	 */
	public float getF() {
		return this.f;
	}

	/**
	 * Sets the value of f for this class, which describes the estimated cost of the entire path via this node
	 * @param f The new value of f
	 */
	public void setF(float f) {
		this.f = f;
	}
}
