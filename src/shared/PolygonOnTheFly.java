package shared;

import client.WorldView;
import org.lwjgl.util.vector.Vector2f;
import java.util.ArrayList;

/**
 * A polygon created at run time
 * @author Barney
 */
public class PolygonOnTheFly extends Polygon {
	/**
	 * Constructs a rectangle using left, top, width and height dimensions
	 * @param left The left x co-ordinate of the rectangle
	 * @param top The top y co-ordinate of the rectangle
	 * @param width The width of the rectangle
	 * @param height The height of the rectangle
	 */
	public PolygonOnTheFly(float left, float top, int width, int height) {
		super();

		// Construct the vertices of the rectangle
		// They must be counter-clockwise
		this.vertices.add(new Vector2f(left, top));
		this.vertices.add(new Vector2f(left, top - height));
		this.vertices.add(new Vector2f(left + width, top - height));
		this.vertices.add(new Vector2f(left + width, top));

		// Calculate the minimum and maximum x and y values of the rectangle
		this.minX = left;
		this.maxX = left + width;
		this.minY = top - height;
		this.maxY = top;
	}

	/**
	 * Constructs the polygon given the vertices that make it up
	 * @param vertices The list of vertices that make up the polygon, they must be given in a counter clockwise order
	 */
	public PolygonOnTheFly(ArrayList<Vector2f> vertices) {
		super(vertices);

		// Work out the min and max x and y co-ordinates
		calculateMinMaxFromVertices();
	}

	/**
	 * Add neigbours to the polygons internal list of it.
	 * @param neighbour The new neighbour
	 */
	public void addNeighbour(Polygon neighbour) {
		this.getNeighbours().add(neighbour);
	}

	/**
	 * Determines whether the polygon contains the position or not
	 * @return Returns true if the polygon does contain the position and false otherwise, if the position lies on one of the edges false will be returned
	 */
	public boolean contains(Vector2f position) {
		// Preliminary check to see if the point is outside the polygon
		// Minimal cost that could save us from having to perform the more expensive algorithm below
		if (position.getX() <= this.minX || position.getX() >= this.maxX || position.getY() <=this.minY || position.getY() >= this.maxY) {
			return false;
		}

		// Now we can perform the well-known point in polygon raycasting algorithm
		// The for loop simply generates the indexes of two adjacent vertices
		boolean result = false;
		for (int i = 0, j = this.getVertices().size() - 1; i < this.getVertices().size(); j = i++) {
			Vector2f vector1 = this.getVertices().get(i);
			Vector2f vector2 = this.getVertices().get(j);

			// The first test checks whether the y value of the position is within the range vertex1.y - vertex2.y
			if ((vector1.getY() > position.getY()) != (vector2.getY() > position.getY())) {
				// The second test checks whether the x value of the position is below the linear line connecting the adjacent vectors
				if (position.getX() < (vector2.getX() - vector1.getX()) * (position.getY() - vector1.getY()) / (vector2.getY() - vector1.getY()) + vector1.getX()) {
					// The raycast must have passed through an edge
					// We toggle the result because if we pass through an odd number of edges then we should return true else false
					result = !result;
				}
			}
		}

		return result;
	}

	/**
	 * Works out the edge that connects two polygons together
	 * @param p The second polygon
	 * @return The edge connecting the two polygons together, or null if no such edge exists
	 */
	public Edge getEdgeConnectingPolygon(Polygon p) {
		// For every edge of the first polygon
		for (int i1 = 0, j1 = this.getVertices().size() - 1; i1 < this.getVertices().size(); j1 = i1++) { 
			// For every edge of the second polygon
			for (int i2 = 0, j2 = p.getVertices().size() - 1; i2 < p.getVertices().size(); j2 = i2++) {
				// If the edges connect then return the shorter of the two edges
				Edge e1 = new Edge(this.getVertices().get(i1), this.getVertices().get(j1));
				Edge e2 = new Edge(p.getVertices().get(i2), p.getVertices().get(j2));
				if (Edge.edgesConnect(e1, e2)) {
					if (e1.length() < e2.length()) {
						return e1;
					} else {
						return e2;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Tests an object to see if it is equal to this object
	 * @param obj The object to test
	 * @return True if the objects share the same vertices and false otherwise
	 */
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof Polygon) {
			Polygon otherPolygon = (Polygon)obj;

			// Check whether all the vertices are the same
			if (this.getVertices().equals(otherPolygon.getVertices())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Gets the left x coordinate of the polygon
	 * @return The left x coordinate
	 */
	public float getLeft() {
		return this.minX;
	}

	/**
	 * Gets the bottom y coordinate of the polygon
	 * @return The bottom y coordinate
	 */
	public float getBottom() {
		return this.minY;
	}

	/**
	 * Gets the right x coordinate of the polygon
	 * @return The right x coordinate
	 */
	public float getRight() {
		return this.maxX;
	}

	/**
	 * Gets the top y coordinate of the polygon
	 * @return The top y coordinate
	 */
	public float getTop() {
		return this.maxY;
	}

	/**
	 * Gets the top left coordinate of the polygon
	 * @return The top left coordinate
	 */
	public Vector2f getTopLeft() {
		return new Vector2f(this.getLeft(), this.getTop());
	}

	/**
	 * Gets the bottom left coordinate of the polygon
	 * @return The bottom left coordinate
	 */
	public Vector2f getBottomLeft() {
		return new Vector2f(this.getLeft(), this.getBottom());
	}

	/**
	 * Gets the bottom right coordinate of the polygon
	 * @return The bottom right coordinate
	 */
	public Vector2f getBottomRight() {
		return new Vector2f(this.getRight(), this.getBottom());
	}

	/**
	 * Gets the top right coordinate of the polygon
	 * @return The top right coordinate
	 */
	public Vector2f getTopRight() {
		return new Vector2f(this.getRight(), this.getTop());
	}

	/**
	 * Gets the width of the polygon
	 * @return The width
	 */
	public int getWidth() {
		return (int)this.getRight() - (int)this.getLeft();
	}

	/**
	 * Gets the height of the polygon
	 * @return The height
	 */
	public int getHeight() {
		return (int)this.getTop() - (int)this.getBottom();
	}

	/**
	 * Gets the counter clockwise list of vertices making up the polygon
	 * @return The list of vertices
	 */
	public ArrayList<Vector2f> getVertices() {
		return this.vertices;
	}

	/**
	 * Gets the list of the polygons neighbouring polygons
	 * @return The list of neighbours
	 */
	public ArrayList<Polygon> getNeighbours() {
		return this.neighbours;
	}

	/**
	 * Gets the polygons id
	 * @return The polygons ID
	 */
	public String getID() {
		return this.id;
	}

	/**
	 * Sets the polygons ID
	 * @param id The id
	 */
	public void setID(String id) {
		this.id = id;
	}
}
