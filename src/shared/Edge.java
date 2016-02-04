package shared;

import org.lwjgl.util.vector.Vector2f;

/**
 * Represents an edge, and provides basic information about it
 * @author Barney
 */
public class Edge {
	// The vertices describing the edge
	private final Vector2f vertex1;
	private final Vector2f vertex2;

	/**
	 * Constructs the Edge using it's two vertices
	 * @param vertex1 The first vertex
	 * @param vertex2 The second vertex
	 */
	public Edge(Vector2f vertex1, Vector2f vertex2) {
		// Store vertex with the highest y value in vertex1 (to make the gradient calculations below correct)
		if (vertex1.getY() > vertex2.getY()) {
			this.vertex1 = vertex1;
			this.vertex2 = vertex2;
		} else {
			this.vertex1 = vertex2;
			this.vertex2 = vertex1;
		}
	}

	/**
	 * Gets the midpoint of the edge
	 * @return The midpoint
	 */
	public Vector2f midPoint() {
		float x = (this.vertex1.getX() + this.vertex2.getX()) / 2;
		float y = (this.vertex1.getY() + this.vertex2.getY()) / 2;
		return new Vector2f(x, y);
	}

	/**
	 * Gets the length of the edge
	 * @return The length
	 */
	public float length() {
		// Subtract the vectors from each other
		Vector2f result = Vector2f.sub(this.vertex1, this.vertex2, null);

		// And return the length squared
		return result.length();
	}

	/**
	 * A test to see whether two edges connect i.e. one of the edges is a part of the other edge
	 * @param e1 The first edge
	 * @param e2 The second edge
	 * @return True if the edges do connect and false if they don't
	 */
	public static boolean edgesConnect(Edge e1, Edge e2) {
		// First we check that the gradients are equal
		if (e1.gradient() != e2.gradient()) {
			return false;
		}

		// Secondly check whether we have two vertices that lie on both edges
		int numberOfTouches = 0;
		if (e2.touches(e1.getFirstVertice())) {
			numberOfTouches++;
		}
		if (e2.touches(e1.getSecondVertice())) {
			numberOfTouches++;
		}
		if (e1.touches(e2.getFirstVertice())) {
			numberOfTouches++;
		}
		if (e1.touches(e2.getSecondVertice())) {
			numberOfTouches++;
		}

		// If the edges share a point then numberOfTouches must be >=3 if they connect, otherwise it must be >=2
		if (Edge.edgesShareVertex(e1, e2)) {
			return numberOfTouches >= 3;
		} else {
			return numberOfTouches >= 2;
		}
	}

	/**
	 * Tests two edges to see whether they share a vertex or not
	 * @param e1 The first edge
	 * @param e2 The second edge
	 * @return True if the edges share a vertex and false otherwise
	 */
	public static boolean edgesShareVertex(Edge e1, Edge e2) {
		Vector2f e1v1 = e1.getFirstVertice();
		Vector2f e1v2 = e1.getSecondVertice();
		Vector2f e2v1 = e2.getFirstVertice();
		Vector2f e2v2 = e2.getSecondVertice();

		if (e1v1.equals(e2v1) || e1v1.equals(e2v2)) {
			return true;
		}
		if (e1v2.equals(e2v1) || e1v2.equals(e2v2)) {
			return true;
		}

		return false;
	}

	/**
	 * Gets the gradient of the line forming the edge
	 * @return The gradient of the edge
	 */
	public float gradient() {
		float changeInY = this.getSecondVertice().getY() - this.getFirstVertice().getY();
		float changeInX = this.getSecondVertice().getX() - this.getFirstVertice().getX();
		return changeInY/changeInX;
	}

	/**
	 * A test to see if a point lies on the edge or not
	 * @param point The point used in the test
	 * @return True if the point does lie on the edge and false if not
	 */
	public boolean touches(Vector2f point) {
		// First check whether the point is equal to one of the vertices, if it is then the gradient test below would not work
		if (point.equals(this.getFirstVertice()) || point.equals(this.getSecondVertice())) {
			return true;
		}

		// Check the gradient of ab equals the gradient of ac, which means that c must be on the same infinite line with a and b on it
		float abGradient = this.gradient();
		float acGradient = (new Edge(this.getFirstVertice(), point)).gradient();
		if (abGradient != acGradient) {
			return false;
		}

		// Next check that the x and y values of the point are in between the x and y values of the edge
		float highestX = (this.getFirstVertice().getX() > this.getSecondVertice().getX()) ? this.getFirstVertice().getX() : this.getSecondVertice().getX();
		float lowestX = (this.getFirstVertice().getX() < this.getSecondVertice().getX()) ? this.getFirstVertice().getX() : this.getSecondVertice().getX();
		float highestY = (this.getFirstVertice().getY() > this.getSecondVertice().getY()) ? this.getFirstVertice().getY() : this.getSecondVertice().getY();
		float lowestY = (this.getFirstVertice().getY() < this.getSecondVertice().getY()) ? this.getFirstVertice().getY() : this.getSecondVertice().getY();
		if (point.getX() <= highestX && point.getX() >= lowestX && point.getY() <= highestY && point.getY() >= lowestY) {
			return true;
		} else {
			return false;
		}
	}

	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}

		if (!(o instanceof Edge)) {
			return false;
		}

		Edge that = (Edge)o;
		return (this.getFirstVertice().equals(that.getFirstVertice()) && this.getSecondVertice().equals(that.getSecondVertice())) ||
		       (this.getSecondVertice().equals(that.getFirstVertice()) && this.getFirstVertice().equals(that.getSecondVertice()));
	}

	/**
	 * Gets the first vertex
	 * @return The first vertex
	 */
	public Vector2f getFirstVertice() {
		return this.vertex1;
	}

	/**
	 * Gets the second vertex
	 * @return The second vertex
	 */
	public Vector2f getSecondVertice() {
		return this.vertex2;
	}
}
