package shared;

import org.w3c.dom.*;
import java.util.ArrayList;
import java.util.HashMap;
import org.lwjgl.util.vector.Vector2f;

/**
 * Represents a polygon loaded from a file
 * @author Barney
 */
public class PolygonFromMap extends Polygon {
	private HashMap<Polygon, Edge> edgesConnectingNeighbours;

	/**
	 * Construct a polygon from a DOM node in a navmesh file
	 * @param node The polygon node from the file
	 */
	public PolygonFromMap(Element node) throws InvalidMapException {
		super(parseVertices(getChildNodesWithTagName(node, "vertice")));

		// Work out the min and max x and y co-ordinates
		calculateMinMaxFromVertices();

		// Initialise the edges connecting neighbours map
		this.edgesConnectingNeighbours = new HashMap<Polygon, Edge>();

		// Get the id from the node
		this.setID(node.getAttribute("id"));
	}

	/**
	 * Gets the list of nodes that are direct children of a given node, and have the specified tagname
	 * @param node The node to look for vertice nodes under
	 * @param tagName The tag name to look for
	 * @return The list of vertices nodes
	 */
	private static ArrayList<Element> getChildNodesWithTagName(Element node, String tagName) {
		ArrayList<Element> results = new ArrayList<Element>();

		for (int i = 0; i < node.getChildNodes().getLength(); i++) {
			Node child = node.getChildNodes().item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element)child;
				if (childElement.getTagName().equals(tagName)) {
					// Add to node list
					results.add(childElement);
				}
			}
		}
		return results;
	}

	/**
	 * Converts a node to an element if it is an element
	 * @return The Element object if the node is of the element type, if not, returns null
	 */
	private static Element convertNodeToElement(Node n) {
		if (n.getNodeType() == Node.ELEMENT_NODE) {
			return (Element)n;
		} else {
			return null;
		}
	}

	/**
	 * Parses a list of vertice nodes and returns the constructed list of Vector2f's
	 * @param vNodes The list of vertice nodes
	 * @return The arraylist of vertices, represented by Vector2f's
	 */
	private static ArrayList<Vector2f> parseVertices(ArrayList<Element> vNodes) throws InvalidMapException {
		ArrayList<Vector2f> vs = new ArrayList<Vector2f>();

		for (int i = 0; i < vNodes.size(); i++) {
			Node vNode = vNodes.get(i);

			// Check it is a vertice node
			Element vElement = convertNodeToElement(vNode);
			if (vElement == null) {
				throw new InvalidMapException("NavMesh Exception: Node was not an Element");
			}
			if (vElement.getTagName() != "vertice") {
				throw new InvalidMapException("NavMesh Exception: Element did not have vertice tag name");
			}

			float x, y;
			try {
				// Get x
				Node xNode = vElement.getElementsByTagName("x").item(0);
				String xAsString = xNode.getTextContent();
				x = Float.parseFloat(xAsString);

				// Get y
				Node yNode = vElement.getElementsByTagName("y").item(0);
				String yAsString = yNode.getTextContent();
				y = Float.parseFloat(yAsString);
			} catch (Exception ex) {
				throw new InvalidMapException("NavMesh Exception: " + ex.getMessage());
			}

			Vector2f v = new Vector2f(x, y);
			vs.add(v);
		}

		return vs;
	}

	public Edge getEdgeConnectingPolygon(Polygon p) {
		return this.getEdgesConnectingNeighbours().get(p);
	}

	/**
	 * Gets the map containing the information about edges connecting the polygon to it's neighbours
	 * @return The map
	 */
	private HashMap<Polygon, Edge> getEdgesConnectingNeighbours() {
		return this.edgesConnectingNeighbours;
	}

	/**
	 * Add a neighbour and it's connecting edge to the polygons information
	 * @param neighbour The neighbour polygon
	 * @param connectingEdge The connecting edge between this polygon and the neighbour polygon
	 */
	public void addNeighbourAndConnectingEdge(Polygon neighbour, Edge connectingEdge) {
		// Add the neighbour
		this.addNeighbour(neighbour);

		// Add the connecting edge information
		this.getEdgesConnectingNeighbours().put(neighbour, connectingEdge);
	}
}
