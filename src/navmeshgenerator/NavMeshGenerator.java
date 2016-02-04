package navmeshgenerator;

import java.util.ArrayList;
import org.lwjgl.util.vector.Vector2f;
import java.util.HashMap;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

import shared.MapFile;
import shared.Entity;
import shared.InvalidMapException;
import server.ServerWorld;
import shared.entities.Boundary;
import shared.NavMesh;
import shared.PolygonOnTheFly;
import shared.Polygon;
import shared.Edge;

/**
 * Generates a navigation mesh for a map file
 * @author barney
 */
public class NavMeshGenerator extends JFrame {
	// The panel for drawing on
	private final DrawingPanel drawingPanel;
	private final JScrollPane scrollPane;

	// The map file that describes the map
	private final MapFile mapFile;

	// The lists of objects that should be drawn on the screen
	private final ArrayList<Entity> obstacles;
	private final ArrayList<Entity> obstaclesToBeDrawn;
	private final ArrayList<Polygon> polygons;

	/**
	 * Creates a navigation mesh along with a gui to show the process in real time
	 * @param mapFileName The name of the map file to generate a mesh file for
	 */
	public static void createNavMesh(String mapFileName) {
		NavMeshGenerator navMeshGenerator = null;
		NavMesh navMesh = null;

		System.out.println("Generating the navigation mesh for " + mapFileName + "...");
		try {
		navMeshGenerator = new NavMeshGenerator(mapFileName);
		navMesh = navMeshGenerator.generateNavMeshFor(mapFileName);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		// Work out what the map file name should be
		String navMeshFileName;
		if (!mapFileName.endsWith(".xml")) {
			navMeshFileName = mapFileName + ".navmesh";
		} else {
			// Remove the 'xml' part
			navMeshFileName = mapFileName.substring(0, mapFileName.length() - 3) + "navmesh";
		}

		// Store the navmesh
		System.out.print("Storing the navigation mesh...");
		NavMeshGenerator.storeNavMesh(navMesh, navMeshFileName);
		System.out.println("Done.");
		System.out.println("Exiting");
//		System.exit(0);
	}

	/**
	 * Constructor, constructs the frame, of the correct size, with the navmeshgenerator title
	 * @param mapFileName The map file name
	 */
	public NavMeshGenerator(String mapFileName) throws InvalidMapException {
		// Call the super constructor and set the JFrame's title
		super("Navigation Mesh Generator");

		// Create and store the map file
		ServerWorld sw = new ServerWorld(mapFileName);
		this.mapFile = new MapFile(mapFileName, sw);

		// Allow the x to close the screen
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and store the lists of items that need to be drawn
		this.obstacles = new ArrayList<Entity>();
		this.obstaclesToBeDrawn = new ArrayList<Entity>();
		this.polygons = new ArrayList<Polygon>();

		// Create the drawing panel
		this.drawingPanel = new DrawingPanel(this);
		this.drawingPanel.setPreferredSize(new Dimension(mapFile.getWidth(), mapFile.getHeight()));
		this.scrollPane = new JScrollPane(this.drawingPanel);
		this.setContentPane(scrollPane);

		// Set some last minute configurations
		this.pack();
		this.setVisible(true);
	}

	/**
	 * Stores a navigation mesh object in a file
	 * @param navMesh The navigation mesh object to store
	 * @param navMeshFileName The file to store the mesh in
	 */
	public static void storeNavMesh(NavMesh navMesh, String navMeshFileName) {
		BufferedWriter writer = null;
		try {
			// Open the map file for overwriting if it exists, else create a new file
			writer = Files.newBufferedWriter(Paths.get(navMeshFileName), StandardCharsets.UTF_8);

			// Write the root node
			writer.write("<navmesh>");
			writer.newLine();

			// Write each polygon
			for (Polygon p : navMesh.getPolygons()) {
				writer.write("	<polygon id=\""+p.getID()+"\">");
				writer.newLine();

				// Write each vector in the polygon
				ArrayList<Vector2f> vs = p.getVertices();
				for (Vector2f v : vs) {
					writer.write("		<vertice>");
					writer.newLine();

					writer.write("			<x>"+v.getX()+"</x>");
					writer.newLine();
					writer.write("			<y>"+v.getY()+"</y>");
					writer.newLine();

					writer.write("		</vertice>");
					writer.newLine();
				}

				// Write neighbour information
				for (Polygon n : p.getNeighbours()) {
					writer.write("		<neighbour id=\""+n.getID()+"\">");
					writer.newLine();

					writer.write("			<connectingedge>");
					writer.newLine();

					Edge edge = p.getEdgeConnectingPolygon(n);

					writer.write("				<vertice>");
					writer.newLine();

					writer.write("					<x>"+edge.getFirstVertice().getX()+"</x>");
					writer.newLine();
					writer.write("					<y>"+edge.getFirstVertice().getY()+"</y>");
					writer.newLine();

					writer.write("				</vertice>");
					writer.newLine();

					writer.write("				<vertice>");
					writer.newLine();

					writer.write("					<x>"+edge.getSecondVertice().getX()+"</x>");
					writer.newLine();
					writer.write("					<y>"+edge.getSecondVertice().getY()+"</y>");
					writer.newLine();

					writer.write("				</vertice>");
					writer.newLine();

					writer.write("			</connectingedge>");
					writer.newLine();

					writer.write("		</neighbour>");
					writer.newLine();
				}

				writer.write("	</polygon>");
				writer.newLine();
			}

			// Close the root node
			writer.write("</navmesh>");

			// Flush all the data to the file
			writer.flush();
		} catch (IOException e) {
			System.out.println("Could not write to file:");
			System.out.println(e.getMessage());
		} finally {
			// Make sure that we close the writer
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {}
		}
	}

	/**
	 * Constructs a navigation mesh for the specified map
	 * @param mapFileName The file name of the map
	 * @return A navigation mesh for the specified map
	 */
	private NavMesh generateNavMeshFor(String mapFileName) throws InvalidMapException {
		// Get the list of entities from the map file
		System.out.print("	Generating list of obstacles...");
		ArrayList<Entity> entities = this.getMapFile().getEntities();

		// Cut it down to just the list of obstacles
		// Exclude the map boundary entities as the navigation mesh doesn't need to navigate around them
		for (Entity e : entities) {
			if (e.isSolid() && !(e instanceof Boundary)) {
				this.getObstacles().add(e);
			}
		}
		System.out.println("Done.");

		// Construct the navigation mesh around the obstacles
		return this.generateNavMeshForObstacles();
	}

	/**
	 * Constructs a navigation mesh using the width and height of the map as well as the obstacles on it
	 * @return A navigation mesh of the specified width and height that avoids the obstacles given
	 */
	public NavMesh generateNavMeshForObstacles() {
		// Create the initial polygon respresenting the map
		System.out.print("	Creating initial navmesh representation...");
		int width = this.getMapFile().getWidth();
		int height = this.getMapFile().getHeight();
		this.getPolygons().add(new PolygonOnTheFly(-(width/2), height/2, width, height));
		System.out.println("Done.");

		// Work out the changes that each obstacle causes
		for (Entity obstacle : this.getObstacles()) {
			// Draw the panel
			this.scrollPane.repaint();

			this.getObstaclesToBeDrawn().add(obstacle);
			System.out.println("	Propagating changes for next obstacle...");
			// Calculate the entity vertices
			float x = obstacle.getX();
			float y = obstacle.getY();
			float obstacleWidth = obstacle.getBoundingBoxWidth();
			float obstacleHeight = obstacle.getBoundingBoxHeight();
			ArrayList<Vector2f> vs = new ArrayList<Vector2f>();
			vs.add(new Vector2f(x - obstacleWidth/2, y + obstacleHeight/2));
			vs.add(new Vector2f(x - obstacleWidth/2, y - obstacleHeight/2));
			vs.add(new Vector2f(x + obstacleWidth/2, y - obstacleHeight/2));
			vs.add(new Vector2f(x + obstacleWidth/2, y + obstacleHeight/2));

			// Check which polygons contain the vertices
			for (int i = 0; i < this.getPolygons().size(); i++) {
				// Get the polygon
				Polygon p = this.getPolygons().get(i);

				// Initialise the list of obstacle vertices that are contained by p
				ArrayList<Vector2f> obstacleVerticesContainedByP = new ArrayList<Vector2f>(4);
				for (int c = 0; c < 4; c++) {
					obstacleVerticesContainedByP.add(null);
				}

				// Make a list of the polygons that contain each vertice
				int numberOfObstacleVerticesContained = 0;
				for (int j = 0; j < vs.size(); j++) {
					Vector2f v = vs.get(j);
					if (p.contains(v)) {
						obstacleVerticesContainedByP.set(j, v);
						numberOfObstacleVerticesContained++;
					}
				}

				// If the polygon doesn't contain the obstacle then perhaps the obstacle contains the polygon?
				if (numberOfObstacleVerticesContained == 0) {
					// Check whether the polygon is inside the obstacle or has the same shape and location, if it does then we can simply remove it
					Polygon obstaclePolygon = new PolygonOnTheFly(vs);
					if (this.obstacleContainsOrEqualsPolygon(obstaclePolygon, p)) {
						// Remove the polygon and reset the iterator to reflect this
						this.getPolygons().remove(p);
						i--;
					}

					// Check whether the polygon overlaps the obstacle, if it does then we need to remove a chunk of it
					else if (this.polygonOverlapsObstacle(p, obstaclePolygon)) {
						ArrayList<Polygon> splitPolygons = this.removeOverlappingChunk(p, obstaclePolygon);
						this.getPolygons().remove(p);
						this.getPolygons().addAll(i, splitPolygons);
						// We've added splitPolygons.size() elements and removed one polygon, update the value of i accordingly
						i += splitPolygons.size() - 1;
					}
				}
				
				// Otherwise we must 'extend' out the obstacles edges to split up the polygon
				else {
					ArrayList<Polygon> splitPolygons = extendEdgesInSamePolygon(obstacleVerticesContainedByP, p);
					this.getPolygons().remove(p);
					this.getPolygons().addAll(i, splitPolygons);
					// We've added splitPolygons.size() elements and removed one polygon, update the value of i accordingly
					i += splitPolygons.size() - 1;
				}
			}

			// Draw the panel
			this.scrollPane.repaint();

			// Combine polygons that can be combined
			this.combinePolygons();
		}

		System.out.print("	Adding polygon neighbour information to navmesh...");
		this.addNeighbourInfoToPolygonList();
		System.out.println("Done.");

		// Draw the panel one last time
		this.drawingPanel.repaint();

		// Set the ids of the polygons
		for (int i = 0; i < this.getPolygons().size(); i++) {
			this.getPolygons().get(i).setID(""+i);
		}

		// Construct and return the navigation mesh
		return new NavMesh(polygons);
	}

	/**
	 * Adds neighbour information to a list of polygons so that each knows who it's neighbour is
	 */
	private void addNeighbourInfoToPolygonList() {
		// For every polygon
		for (Polygon p : this.getPolygons()) {
			for (Polygon q : this.getPolygons()) {
				// If the polygons are not the same polygon
				if (p.equals(q)) {
					continue;
				}

				// Check that we haven't already added the neighbour information to the polygons
				if (p.getNeighbours().contains(q)) {
					continue;
				}
				
				// Check whether they are neighbours
				if (p.getEdgeConnectingPolygon(q) != null) {
					// If they are then add the neighbour information to the polygons
					p.addNeighbour(q);
					q.addNeighbour(p);
				}
			}
		}
	}

	/**
	 * Combines polygons that share the same edge
	 */
	private void combinePolygons() {
		// Check every two polygons
		for (int i = 0; i < this.getPolygons().size(); i++) {
			for (int j = 0; j < this.getPolygons().size(); j++) {
				Polygon p = this.getPolygons().get(i);
				Polygon q = this.getPolygons().get(j);

				// Check whether the two polygons are equal, if they are then we can just continue
				if (p.equals(q)) {
					continue;
				}

				// If the two polygons share an edge then we can combine them
				if (this.polygonsShareEdge(p, q)) {
					// Initialise a set of vertices for the new polygon
					ArrayList<Vector2f> vs = new ArrayList<Vector2f>(4);

					// Work out which side the shared edge is
					if (p.getLeft() < q.getLeft()) {
						// p is on left, q is on right
						vs.add(p.getTopLeft());
						vs.add(p.getBottomLeft());
						vs.add(q.getBottomRight());
						vs.add(q.getTopRight());
					}
					else if (p.getRight() > q.getRight()) {
						// p is on right, q is on left
						vs.add(q.getTopLeft());
						vs.add(q.getBottomLeft());
						vs.add(p.getBottomRight());
						vs.add(p.getTopRight());
					}
					else if (p.getTop() > q.getTop()) {
						// p is on top, q is on bottom
						vs.add(p.getTopLeft());
						vs.add(q.getBottomLeft());
						vs.add(q.getBottomRight());
						vs.add(p.getTopRight());
					}
					else if (p.getBottom() < q.getBottom()) {
						// p is on bottom, q is on top
						vs.add(q.getTopLeft());
						vs.add(p.getBottomLeft());
						vs.add(p.getBottomRight());
						vs.add(q.getTopRight());
					}

					// Create the new polygon and add it to the list
					Polygon combinedPolygon = new PolygonOnTheFly(vs);
					this.getPolygons().set(i, combinedPolygon);
					this.getPolygons().remove(j);
					j--;
				}
			}
		}
	}

	/**
	 * Tests to polygons to see if they share an edge
	 * @param p The first polygon
	 * @param q The second polygon
	 * @return True if the polygons do share an edge and false otherwise
	 */
	private boolean polygonsShareEdge(Polygon p, Polygon q) {
		for (int i = 0, j = p.getVertices().size() - 1; i < p.getVertices().size(); j=i++) {
			Vector2f e1v1 = p.getVertices().get(i);
			Vector2f e1v2 = p.getVertices().get(j);
			Edge e1 = new Edge(e1v1, e1v2);
			for (int m = 0, n = q.getVertices().size() - 1; m < q.getVertices().size(); n=m++) {
				Vector2f e2v1 = q.getVertices().get(m);
				Vector2f e2v2 = q.getVertices().get(n);
				Edge e2 = new Edge(e2v1, e2v2);

				if (e1.equals(e2)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Splits a polygon, that overlaps an obstacle, into one or more pieces that no longer overlap the obstacle
	 * @param p The polygon
	 * @param obstaclePolygon The obstacle, represented as a polygon
	 * @return The list of resulting polygons
	 */
	private ArrayList<Polygon> removeOverlappingChunk(Polygon p, Polygon obstaclePolygon) {
		// There are 4 possible polygons that can result from removing the overlapping chunk, a top polygon, a bottom polygon, a left polygon and a right polygon
		// Only left and right, or top and bottom polygons can exist at the same time

		// Initialise the results list
		ArrayList<Polygon> results = new ArrayList<Polygon>();
		
		// Calculate the left polygon, if it exists
		if (p.getLeft() < obstaclePolygon.getLeft()) {
			ArrayList<Vector2f> vs = new ArrayList<Vector2f>(4);
			vs.add(p.getTopLeft());
			vs.add(p.getBottomLeft());
			vs.add(new Vector2f(obstaclePolygon.getLeft(), p.getBottom()));
			vs.add(new Vector2f(obstaclePolygon.getLeft(), p.getTop()));
			results.add(new PolygonOnTheFly(vs));
		}

		// Calculate the bottom polygon, if it exists
		if (p.getBottom() < obstaclePolygon.getBottom()) {
			ArrayList<Vector2f> vs = new ArrayList<Vector2f>(4);
			vs.add(new Vector2f(p.getLeft(), obstaclePolygon.getBottom()));
			vs.add(p.getBottomLeft());
			vs.add(p.getBottomRight());
			vs.add(new Vector2f(p.getRight(), obstaclePolygon.getBottom()));
			results.add(new PolygonOnTheFly(vs));
		}

		// Calculate the right polygon if it exists
		if (p.getRight() > obstaclePolygon.getRight()) {
			ArrayList<Vector2f> vs = new ArrayList<Vector2f>(4);
			vs.add(new Vector2f(obstaclePolygon.getRight(), p.getTop()));
			vs.add(new Vector2f(obstaclePolygon.getRight(), p.getBottom()));
			vs.add(p.getBottomRight());
			vs.add(p.getTopRight());
			results.add(new PolygonOnTheFly(vs));
		}

		// Calculate the top polygon if it exists
		if (p.getTop() > obstaclePolygon.getTop()) {
			ArrayList<Vector2f> vs = new ArrayList<Vector2f>(4);
			vs.add(p.getTopLeft());
			vs.add(new Vector2f(p.getLeft(), obstaclePolygon.getTop()));
			vs.add(new Vector2f(p.getRight(), obstaclePolygon.getTop()));
			vs.add(p.getTopRight());
			results.add(new PolygonOnTheFly(vs));
		}

		// Return the list of polygons
		return results;
	}

	/**
	 * Checks whether a polygon overlaps an obstacle
	 * @param p The polygon
	 * @param obstacle The obstacle, represented as a polygon
	 * @return True if the polygon overlaps the obstacle and false otherwise
	 */
	private boolean polygonOverlapsObstacle(Polygon p, Polygon obstacle) {
		// A polygon overlaps an obstacle if it's left and/or right coordinates are within the obstacles x range or (exclusive) it's top and/or bottom coordinates are within the obstacles y range
		boolean pLeftWithinXRange = p.getLeft() >= obstacle.getLeft() && p.getLeft() < obstacle.getRight();
		boolean pRightWithinXRange = p.getRight() > obstacle.getLeft() && p.getRight() <= obstacle.getRight();
		boolean pTopWithinYRange = p.getTop() <= obstacle.getTop() && p.getTop() > obstacle.getBottom();
		boolean pBottomWithinYRange = p.getBottom() < obstacle.getTop() && p.getBottom() >= obstacle.getBottom();

		boolean pCoversXRange = p.getLeft() <= obstacle.getLeft() && p.getRight() >= obstacle.getRight();
		boolean pCoversYRange = p.getTop() >= obstacle.getTop() && p.getBottom() <= obstacle.getBottom();
		boolean xOverlaps = pLeftWithinXRange || pRightWithinXRange || pCoversXRange;
		boolean yOverlaps = pTopWithinYRange || pBottomWithinYRange || pCoversYRange;

		return xOverlaps && yOverlaps;
	}

	/**
	 * Checks whether an obstacle contains a polygon or equals it
	 * @param obstaclePolygon The obstacle represented as a polygon
	 * @param p The polygon
	 * @return True if the obstacle equals or contains the polygon and false otherwise
	 */
	private boolean obstacleContainsOrEqualsPolygon(Polygon obstaclePolygon, Polygon p) {
		return (p.getLeft() >= obstaclePolygon.getLeft() &&
			p.getRight() <= obstaclePolygon.getRight() &&
			p.getBottom() >= obstaclePolygon.getBottom() &&
			p.getTop() <= obstaclePolygon.getTop());
	}

	/**
	 * Splits a set of polygons within a navigation mesh to account for an added obstacle, by extending the obstacle edges in order to split up the polygons
	 * @param polygonsAffected A list of the polygons affected by the obstacle
	 * @param polygonsToVertices A mapping from a polygon to the list of vertices that affect the polygon. The list of vertices should be of length 4, with one slot for each obstacle vertice in counter clockwise order starting from the top-left. If the vertice does not affect the polygon it's entry in the list should be null.
	 * @return The resulting list of new polygons, which avoid the obstacle
	 */
	private ArrayList<Polygon> extendEdges(ArrayList<Polygon> polygonsAffected, HashMap<Polygon, ArrayList<Vector2f>> polygonsToVertices) {
		// Resulting list of polygons
		ArrayList<Polygon> results = new ArrayList<Polygon>();

		// For each polygon
		for (Polygon p : polygonsAffected) {
			// Extend the edges in it
			ArrayList<Vector2f> vs = polygonsToVertices.get(p);
			results.addAll(extendEdgesInSamePolygon(vs, p));
		}

		// Return the list of polygons
		return results;
	}

	/**
	 * Splits a polygon in a navigation mesh to account for an added obstacle, by extending the obstacles edges in order to split up the polygon
	 * @param vs The vertices of the obstacle, which must be a rectangle, in counter clockwise order. If the vertices do not affect the obstacle then they should be set to null
	 * @param p The polygon to split
	 * @return The resulting set of polygons
	 */
	private ArrayList<Polygon> extendEdgesInSamePolygon(ArrayList<Vector2f> vs, Polygon p) {
		// Initialise the split variables
		Vector2f leftSplit = null;
		Vector2f bottomSplit = null;
		Vector2f rightSplit = null;
		Vector2f topSplit = null;

		// The first vertice should cut left
		Vector2f workingVertice = vs.get(0);
		if (workingVertice != null) {
			leftSplit = new Vector2f(p.getLeft(), workingVertice.getY());
		}

		// The second vertice should cut down
		workingVertice = vs.get(1);
		if (workingVertice != null) {
			bottomSplit = new Vector2f(workingVertice.getX(), p.getBottom());
		}

		// The third vertice should cut right
		workingVertice = vs.get(2);
		if (workingVertice != null) {
			rightSplit = new Vector2f(p.getRight(), workingVertice.getY());
		}
	
		// The fourth vertice should cut up
		workingVertice = vs.get(3);
		if (workingVertice != null) {
			topSplit = new Vector2f(workingVertice.getX(), p.getTop());
		}

		// Create some data structures used to construct and hold the polygons
		ArrayList<Polygon> resultPolygons = new ArrayList<Polygon>();
		ArrayList<Vector2f> resultVertices = null;

		// Calculate the top left polygon, if it is required
		Polygon topLeft = extendEdgesTopLeft(leftSplit, topSplit, p, vs.get(3));
		if (topLeft != null) {
			resultPolygons.add(topLeft);
		}

		// Calculate the bottom left polygon, if it is required
		Polygon bottomLeft = extendEdgesBottomLeft(bottomSplit, leftSplit, p, vs.get(0));
		if (bottomLeft != null) {
			resultPolygons.add(bottomLeft);
		}

		// Calculate the bottom right polygon, if it is required
		Polygon bottomRight = extendEdgesBottomRight(rightSplit, bottomSplit, p, vs.get(1));
		if (bottomRight != null) {
			resultPolygons.add(bottomRight);
		}

		// Calculate the top right polygon if it is required
		Polygon topRight = extendEdgesTopRight(topSplit, rightSplit, p, vs.get(2));
		if (topRight != null) {
			resultPolygons.add(topRight);
		}

		// Return the results
		return resultPolygons;
	}

	/**
	 * Create the top left polygon that results from splitting a polygon by extending the edges of an obstructing obstacle
	 * @param leftSplit The point where the splitting line intersects the left side of the polygon
	 * @param topSplit The point where the splitting line intersects the top side of the polygon
	 * @param p The polygon to be split
	 * @param topRightObstacleVertex The top right vertex of the obstacle causing the split
	 * @return The top left polygon
	 */
	private Polygon extendEdgesTopLeft(Vector2f leftSplit, Vector2f topSplit, Polygon p, Vector2f topRightObstacleVertex) {
		if (leftSplit != null || topSplit != null) {
			ArrayList<Vector2f> resultVertices = new ArrayList<Vector2f>();
			resultVertices.add(p.getTopLeft());

			if (leftSplit != null) {
				resultVertices.add(leftSplit);
			}
			else {
				resultVertices.add(new Vector2f(p.getLeft(), topRightObstacleVertex.getY()));
			}			

			if (topSplit != null) {
				resultVertices.add(topRightObstacleVertex);
				resultVertices.add(topSplit);
			}
			else {
				resultVertices.add(new Vector2f(p.getRight(), leftSplit.getY()));
				resultVertices.add(p.getTopRight());
			}

			// Add the polygon to the results list
			return new PolygonOnTheFly(resultVertices);
		}

		// If the polygon is not required
		return null;
	}

	/**
	 * Create the bottom left polygon that results from splitting a polygon by extending the edges of an obstructing obstacle
	 * @param bottomSplit The point where the splitting line intersects the bottom side of the polygon
	 * @param leftSplit The point where the splitting line intersects the left side of the polygon
	 * @param p The polygon to be split
	 * @param topLeftObstacleVertex The top left vertex of the obstacle that is causing the split
	 * @return The bottom left polygon
	 */
	private Polygon extendEdgesBottomLeft(Vector2f bottomSplit, Vector2f leftSplit, Polygon p, Vector2f topLeftObstacleVertex) {
		if (bottomSplit != null || leftSplit != null) {
			ArrayList<Vector2f> resultVertices = new ArrayList<Vector2f>();

			// Two cases occur: 1) When both the bottom left and top left vertices of the obstacle are in the polygon 2) When only the bottom left vertice of the obstacle is in the polygon
			if (leftSplit != null) {
				resultVertices.add(leftSplit);
			}
			else {
				resultVertices.add(p.getTopLeft());
			}

			resultVertices.add(p.getBottomLeft());

			if (bottomSplit != null) {
				resultVertices.add(bottomSplit);
			}
			else {
				resultVertices.add(new Vector2f(topLeftObstacleVertex.getX(), p.getBottom()));
			}

			if (leftSplit != null) {
				resultVertices.add(topLeftObstacleVertex);
			}
			else {
				resultVertices.add(new Vector2f(bottomSplit.getX(), p.getTop()));
			}

			// Add the polygon to the results list
			return new PolygonOnTheFly(resultVertices);
		}

		// If it is not required
		return null;
	}

	/**
	 * Calculate the bottom right polygon that results from splitting a polygon by extending the edges of an obstructing obstacle
	 * @param rightSplit The point where the splitting line intersects the right side of the original polygon
	 * @param bottomSplit The point where the splitting line intersects the bottom side of the original polygon
	 * @param p The polygon to split
	 * @param bottomLeftObstacleVertex The bottom left vertex of the obstructing obstacle
	 * @return The bottom right polygon
	 */
	public Polygon extendEdgesBottomRight(Vector2f rightSplit, Vector2f bottomSplit, Polygon p, Vector2f bottomLeftObstacleVertex) {
		if (rightSplit != null || bottomSplit != null) {
			ArrayList<Vector2f> resultVertices = new ArrayList<Vector2f>();
			
			if (bottomSplit != null) {
				resultVertices.add(bottomLeftObstacleVertex);
				resultVertices.add(bottomSplit);
			}
			else {
				resultVertices.add(new Vector2f(p.getLeft(), rightSplit.getY()));
				resultVertices.add(p.getBottomLeft());
			}

			resultVertices.add(p.getBottomRight());

			if (rightSplit != null) {
				resultVertices.add(rightSplit);
			} else {
				resultVertices.add(new Vector2f(p.getRight(), bottomLeftObstacleVertex.getY()));
			}

			// return the pollygon
			return new PolygonOnTheFly(resultVertices);
		}

		// If the polygon is not required
		return null;
	}

	/**
	 * Creates the top right polygon that results from splitting a polygon by extending the edges of an obstructing obstacle
	 * @param topSplit The point where the splitting line intersects the top side of the polygon
	 * @param rightSplit The point where the splitting line intersects the right side of the polygon
	 * @param p The polygon to split
	 * @param bottomRightObstacleVertex The bottom right vertex of the obstructing obstacle
	 * @return The top right polygon
	 */
	public Polygon extendEdgesTopRight(Vector2f topSplit, Vector2f rightSplit, Polygon p, Vector2f bottomRightObstacleVertex) {
		if (topSplit != null || rightSplit != null) {
			ArrayList<Vector2f> resultVertices = new ArrayList<Vector2f>();

			if (topSplit != null) {
				resultVertices.add(topSplit);
			}
			else {
				resultVertices.add(new Vector2f(bottomRightObstacleVertex.getX(), p.getTop()));
			}
			
			if (rightSplit != null) {
				resultVertices.add(bottomRightObstacleVertex);
				resultVertices.add(rightSplit);
			}
			else {
				resultVertices.add(new Vector2f(topSplit.getX(), p.getBottom()));
				resultVertices.add(p.getBottomRight());
			}

			resultVertices.add(p.getTopRight());

			// Return the polygon
			return new PolygonOnTheFly(resultVertices);
		}
	
		// If the polygon is not required
		return null;
	}

	/**
	 * Gets the map file
	 * @return The map file
	 */
	private MapFile getMapFile() {
		return this.mapFile;
	}

	/**
	 * Gets the list of obstacles
	 * @return The list of obstacles
	 */
	private ArrayList<Entity> getObstacles() {
		return this.obstacles;
	}

	/**
	 * Gets the list of polygons
	 * @return The list of polygons
	 */
	public synchronized ArrayList<Polygon> getPolygons() {
		return this.polygons;
	}

	/**
	 * Gets the list of obstacles to be drawn
	 * @return The list of obstacles to be drawn
	 */
	public synchronized ArrayList<Entity> getObstaclesToBeDrawn() {
		return this.obstaclesToBeDrawn;
	}

	public static void main(String[] args) {
		// Check that we've been given at least one map file to generate a navigation mesh for
		if (args.length != 1) {
			System.out.println("Usage:");
			System.out.println("NavMeshGenerator mapfileName");
		}

		// Generate the navmesh
		final String mapFileName = args[0];
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				NavMeshGenerator.createNavMesh(mapFileName);
			}
		});
	}
}
