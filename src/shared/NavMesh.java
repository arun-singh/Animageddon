package shared;

import org.lwjgl.util.vector.Vector2f;
import java.util.ArrayList;
import java.util.PriorityQueue;
import client.WorldView;
import java.util.HashMap;
import java.util.Collections;
import javax.xml.parsers.*;
import java.io.File;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import java.io.IOException;

/**
 * Represents a navigation mesh for a map file
 * @author Barney
 */
public class NavMesh {
	// Contains the list of navmesh polygons
	private final ArrayList<Polygon> polygons;

	/**
	 * Constructor for the NavMesh class, builds the navigation mesh from a file
	 * @param navMeshFileName The name of the file containing the navigation mesh
	 */
	public NavMesh(String navMeshFileName) throws InvalidMapException {
		// Read from the navmesh file
		File navMeshFile = new File(navMeshFileName);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		Document doc = null;
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(navMeshFile);
		} catch (ParserConfigurationException ex) {
			// We should never get here because the configuration settings are always the same
			throw new InvalidMapException("NavMesh Exception: " + ex.getMessage());
		} catch (SAXException ex) {
			throw new InvalidMapException("NavMesh Exception: " + ex.getMessage());
		} catch (IOException ex) {
			throw new InvalidMapException("NavMesh Exception: " + ex.getMessage());
		}

		// Combine any text nodes and remove empty ones
		doc.getDocumentElement().normalize();

		// Parse the file
		this.polygons = this.parsePolygons(doc.getDocumentElement());
		this.parseNeighbours(doc.getDocumentElement());
	}

	/**
	 * Constructs the navigation mesh from a list of polygons describing it
	 * @param polygons A list of polygons that describe the navigation mesh
	 */
	public NavMesh(ArrayList<Polygon> polygons) {
		this.polygons = polygons;
	}

	/**
	 * Parses the navmesh file and adds the polygons neighbours using the information from the file
	 * @param documentElement The document element to parse
	 */
	private void parseNeighbours(Element documentElement) throws InvalidMapException {
		NodeList polygonsNodeList = documentElement.getElementsByTagName("polygon");
		for (int i = 0; i < polygonsNodeList.getLength(); i++) {
			Element polygonNode = (Element)polygonsNodeList.item(i);
			String polygonID = polygonNode.getAttribute("id");
			Polygon polygon = this.getPolygonByID(polygonID);
			if (polygon == null) {
				throw new InvalidMapException("NavMesh Exception: Cannot find polygon with id " + polygonID);
			}

			NodeList neighboursNodeList = polygonNode.getElementsByTagName("neighbour");
			for (int j = 0; j < neighboursNodeList.getLength(); j++) {
				Element neighbourElement = (Element)neighboursNodeList.item(j);
				String neighbourID = neighbourElement.getAttribute("id");
				Polygon neighbour = this.getPolygonByID(neighbourID);
				if (neighbour == null) {
					throw new InvalidMapException("NavMesh Exception: Cannot find polygon with id " + neighbourID);
				}

				// Add the neighbour and the connecting edge information
				if (polygon instanceof PolygonFromMap) {
					// Get the connecting edge information from the file too
					Edge connectingEdge = parseConnectingEdge(neighbourElement);

					((PolygonFromMap)polygon).addNeighbourAndConnectingEdge(neighbour, connectingEdge);
				} else {
					polygon.addNeighbour(neighbour);
				}
			}
		}
	}

	/**
	 * Parse the connecting edge from the neighbour file
	 * @param neighbourElement The neighbour element
	 * @return The connecting edge between the polygon and it's neighbour
	 */
	private Edge parseConnectingEdge(Element neighbourElement) {
		try {
			Element connectingEdgeNode = (Element)neighbourElement.getElementsByTagName("connectingedge").item(0);
			Element firstVerticeNode = (Element)connectingEdgeNode.getElementsByTagName("vertice").item(0);
			Element secondVerticeNode = (Element)connectingEdgeNode.getElementsByTagName("vertice").item(1);
			Element firstVerticeXNode = (Element)firstVerticeNode.getElementsByTagName("x").item(0);
			Element firstVerticeYNode = (Element)firstVerticeNode.getElementsByTagName("y").item(0);
			Element secondVerticeXNode = (Element)secondVerticeNode.getElementsByTagName("x").item(0);
			Element secondVerticeYNode = (Element)secondVerticeNode.getElementsByTagName("y").item(0);
			float firstVerticeX = Float.parseFloat(firstVerticeXNode.getTextContent());
			float firstVerticeY = Float.parseFloat(firstVerticeYNode.getTextContent());
			float secondVerticeX = Float.parseFloat(secondVerticeXNode.getTextContent());
			float secondVerticeY = Float.parseFloat(secondVerticeYNode.getTextContent());
			Vector2f firstVertice = new Vector2f(firstVerticeX, firstVerticeY);
			Vector2f secondVertice = new Vector2f(secondVerticeX, secondVerticeY);
			return new Edge(firstVertice, secondVertice);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get a polygon from the list by it's id
	 * @param id The id
	 * @return The polygon if it exists, else null
	 */
	private Polygon getPolygonByID(String id) {
		for (Polygon p : this.getPolygons()) {
			if (p.getID().equals(id)) {
				return p;
			}
		}

		return null;
	}

	/**
	 * Parses a documentElement and constructs a list of polygons from the information within the file
	 * @param documentElement The document element to parse
	 * @return The list of polygons constructed
	 */
	private ArrayList<Polygon> parsePolygons(Element documentElement) throws InvalidMapException {
		// Initialise the list of polygons
		ArrayList<Polygon> ps = new ArrayList<Polygon>();

		// Get the list of polygon nodes
		NodeList polygonsNodeList = documentElement.getElementsByTagName("polygon");

		// Parse each polygon node in turn
		for (int i = 0; i < polygonsNodeList.getLength(); i++) {
			Element n = (Element)polygonsNodeList.item(i);
			ps.add(new PolygonFromMap(n));
		}

		return ps;
	}

	/**
	 * Performs an A* search, using the polygons, which form the navigation mesh, as nodes.
	 * @param start The polygon to start the search from
	 * @param goal The polygon to find a path to
	 * @return The path from the start polygon to the end polygon
	 */
	public ArrayList<Polygon> aStar(Vector2f start, Vector2f goal) {
		// Find the polygons containing the start and goal points
		Polygon startPolygon = getPolygonContaining(start);
		Polygon goalPolygon = getPolygonContaining(goal);

		// Convert the polygons to nodes so that we can use them for A* search
		PolygonNode startNode = new PolygonNode(startPolygon);
		PolygonNode goalNode = new PolygonNode(goalPolygon);

		// The set of nodes already evaluated
		ArrayList<PolygonNode> closedSet = new ArrayList<PolygonNode>();

		// The set of tentative nodes to be evaluated, initially containing the start node
		PriorityQueue<PolygonNode> openSet = new PriorityQueue<PolygonNode>(this.getPolygons().size(), new PolygonNodeComparator());
		openSet.add(startNode);

		// Maps a node to the previous node in the shortest path
		HashMap<PolygonNode,PolygonNode> cameFrom = new HashMap<PolygonNode,PolygonNode>();

		// Cost from start along best known path
		startNode.setG(0);

		// Estimated total cost from start to goal
		startNode.setF(startNode.getG() + h(start, goal));

		// While we still have nodes available to look at
		while (!openSet.isEmpty()) {
			// Get the next node to look at from the open set
			PolygonNode current = openSet.poll();

			// Check whether it is the goal or not
// TODO: Override equals methods in the Polygon and PolygonNode classes
			if (current.equals(goalNode)) {
				// If it is, simply return the path
				return reconstructPath(cameFrom, goalNode);
			}

			// Move the node to the closed set
			closedSet.add(current);

			// Look at the neighbours of the current node and add them to the open set if we haven't seen them before
			for (PolygonNode neighbour : current.getNeighbours()) {
				// Skip over any nodes that we've already looked at
				if (closedSet.contains(neighbour)) {
					continue;
				}

				// Work out the g score for the neighbour via the current path
				float distance;
				if (cameFrom.get(current) == null) {
					distance = distanceBetween(start, current, neighbour);
				} else {
					distance = distanceBetween(cameFrom.get(current), current, neighbour);
				}
				float tentativeGScore = current.getG() + distance;

				// If we haven't looked at this neighbour yet or we've found a shorter route
				if (!openSet.contains(neighbour) || tentativeGScore < neighbour.getG()) {
					// Update the map to indicate how we got to the neighbour node
					cameFrom.put(neighbour, current);
					if (neighbour.equals(goalNode)) {
						PolygonNode test = cameFrom.get(goalNode);
					}

					// Update the g and f scores for the neighbour
					neighbour.setG(tentativeGScore);
					neighbour.setF(neighbour.getG() + h(getConnectingEdgeMidPoint(current, neighbour), goal));

					// Add the neighbour to the open set if it's not already present
					if (!openSet.contains(neighbour)) {
						openSet.add(neighbour);
					}
				}
			}
		}

		// No paths exist, this should never happen with a navigation mesh
		return null;
	}

	/**
	 * Reconstructs the path from the start node to the goal node
	 * @param cameFrom A map describing the previous node in the path of a specified node
	 * @param goalNode The goal node
	 * @return A list of polygons describing the path from the start polygon to the end polygon
	 */
	private ArrayList<Polygon> reconstructPath(HashMap<PolygonNode,PolygonNode> cameFrom, PolygonNode goalNode) {
		// Initialise the reversed list with the goal node
		ArrayList<Polygon> pathInReverse = new ArrayList<Polygon>();
		pathInReverse.add(goalNode.getPolygon());

		// Work out the reverse path and add the nodes to the pathInReverse list
		for(PolygonNode previous = cameFrom.get(goalNode); previous != null; previous = cameFrom.get(previous)) {
			pathInReverse.add(previous.getPolygon());
		}

		// Reverse the path
		ArrayList<Polygon> path = pathInReverse;
		Collections.reverse(path);

		for (int i = 0; i < path.size(); i++) {
			Polygon p = path.get(i);
			ArrayList<Vector2f> vertices = p.getVertices();
			for (int j = 0; j < vertices.size(); j++) {
				Vector2f v = vertices.get(j);
			}
		}
		return path;
	}

	/**
	 * Works out the distance betwen the current and neighbour nodes, by working out the distance between the edge midpoints
	 * @param previous The node in the path before the current node. Required in order to use the edge midpoints (3 nodes = 2 edges).
	 * @param current The current node, the start
	 * @param neighbour The neighbour node, the end
	 */
	private float distanceBetween(PolygonNode previous, PolygonNode current, PolygonNode neighbour) {
		// Calculate the 2 edge midpoints between the 3 polygons
		Vector2f midPoint1 = getConnectingEdgeMidPoint(previous, current);
		Vector2f midPoint2 = getConnectingEdgeMidPoint(current, neighbour);

		// Return the length between the two midpoints
		return lengthOfLineBetween(midPoint1, midPoint2);
	}

	/**
	 * Works out the distance betwen the current and neighbour nodes, by working out the distance between the start point and next edge midpoint
	 * @param start The start point of the path
	 * @param startNode The current node, the start
	 * @param neighbour The neighbour node, the end
	 */
	private float distanceBetween(Vector2f start, PolygonNode startNode, PolygonNode neighbour) {
		ArrayList<Vector2f> startVertices = startNode.getPolygon().getVertices();
		ArrayList<Vector2f> neighbourVertices = neighbour.getPolygon().getVertices();

		// Work out the edge midpoint connecting the start node and neighbour nodes
		Vector2f edgeMidPoint = getConnectingEdgeMidPoint(startNode, neighbour);

		// Return the length of the line between the points
		return lengthOfLineBetween(start, edgeMidPoint);
	}

	/**
	 * Finds the midpoint of the edge connecting the two polygons together
	 * @param p1 The first polygon
	 * @param p2 The second polygon
	 * @return The midpoint of the edge connecting the polygons
	 */
	private Vector2f getConnectingEdgeMidPoint(Polygon p1, Polygon p2) {
		Edge edge = p1.getEdgeConnectingPolygon(p2);
		return edge.midPoint();
	}

	/**
	 * Finds the midpoint of the edge connecting the two polygon nodes together
	 * @param p1 The first polygon node
	 * @param p2 The second polygon node
	 * @return The midpoint of the edge connecting the polygons
	 */
	private Vector2f getConnectingEdgeMidPoint(PolygonNode p1, PolygonNode p2) {
		return this.getConnectingEdgeMidPoint(p1.getPolygon(), p2.getPolygon());
	}

	/**
	 * Calculates the length of the straight line between two points
	 * @param p1 The first end point of the line
	 * @param p2 The second end point of the line
	 * @return The length of the straight line between the points
	 */
	private float lengthOfLineBetween(Vector2f p1, Vector2f p2) {
		// Subtract the vectors
		Vector2f result = Vector2f.sub(p1, p2, null);

		// Work out the length
		float distance = result.length();
		return distance;
	}

	/**
	 * Heuristic function used to estimate the cost from a node to the goal
	 * @param estimateStart The point we are estimating the distance to the goal from
	 * @param goal The goal point
	 * @return The cost estimate of the distance from the node to the goal
	 */
	private float h(Vector2f estimateStart, Vector2f goal) {
		return lengthOfLineBetween(estimateStart, goal);
	} 

	/**
	 * Gets the polygon in the navmesh that contains the coordinates specififed by the position
	 * @param position The position contained by the returned polygon
	 * @return The polygon containing the position
	 */
	public Polygon getPolygonContaining(Vector2f position) {
		for (Polygon p : this.getPolygons()) {
			for (Vector2f v : p.getVertices()) {
			}
			if (p.contains(position)) {
				return p;
			}
		}

		return null;
	}	

	/**
	 * Converts a path of polygons into a path of points along the polygon path
	 * @param polygonPath The polygon path
	 * @return The point path along the polygon path
	 */
	public ArrayList<Vector2f> polygonPathToPointPath(ArrayList<Polygon> polygonPath) {
		// Initialise the point path
		ArrayList<Vector2f> pointPath = new ArrayList<Vector2f>();

		// We need to work out the edge midpoints between all of the polygons
		// And add them to the list that we will return
		for (int i = 0; i < polygonPath.size() - 1; i++) {
			Vector2f midPoint = getConnectingEdgeMidPoint(polygonPath.get(i), polygonPath.get(i + 1));
			pointPath.add(midPoint);
		}

		// Return the point path
		return pointPath;
	}

	/**
	 * Gets the list of polygons
	 * @return The list of polygons
	 */
	public ArrayList<Polygon> getPolygons() {
		return this.polygons;
	}
}
