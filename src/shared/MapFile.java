package shared;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;


// Map file related imports
import javax.xml.parsers.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.File;
import java.io.IOException;
import org.xml.sax.SAXException;
import java.util.ArrayList;
import shared.Entity;

import shared.entities.Player;
import shared.entities.Flag;
import shared.entities.FlagCaptureRegion;
import shared.entities.Boundary;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Reads and loads in an XML map file to initialise the game world.
 * @author Barney, Chris, Thomas
 */
public class MapFile {

	// World
	private World world;
	
	// Local game player
	private Player player;

	// Stores the xml representation of the map
	private Element mapNode;

	// The texture of the map background
	private String mapTexture;

	// ArrayList's storing objects created using the information in the map file
	private ArrayList<Entity> entities;
	private ArrayList<Team> teams;

	// Store entities required by AI entities
	private Flag mainFlag;
	private FlagCaptureRegion mainFlagCaptureRegion;

	// Spawnpoints for the main player
	private int spawnPointX;
	private int spawnPointY;

	// Navigation mesh for the map, used by the AI
	private NavMesh navMesh;

	// Width and height of the map
	private int width;
	private int height;

	/**
	 * Gets the ArrayList of team objects created from the information in the map file
	 */
	public ArrayList<Team> getTeams() {
		return this.teams;
	}

	private Node findChild(Node node, String tagName) {
		// Get children
		NodeList children = node.getChildNodes();

		// Iterate through until we find the element
		int i = 0;
		for (; i < children.getLength(); i++) {
			if (children.item(i).getNodeName().equals(tagName))
				break;
		}

		// Returns null if not a valid index
		return children.item(i);
	}

	/**
	 * Sets the player used by the drawWorldRectangle method to draw the map relative to
	 * @param player The player to draw the map relative to
	 */
	public void drawRelativeTo(Player player) {
		this.player = player;
	}
	
	/**
	 * Gets the list of entities described by the map file
	 */
	public ArrayList<Entity> getEntities() {
		return this.entities;
	}

	/**
	 * Gets the main flag entity
	 */
	public Flag getMainFlag() {
		return this.mainFlag;
	}

	/**
	 * Parses the map file and fetches information about the teams used on the map, creates the appropriate objects to reflect this
	 * @throws InvalidMapException When the teams node has no children of the team type or the children are ill formed
	 */
	private void parseTeams() throws InvalidMapException {
		// Get a list of all the children of the teams node
		NodeList teamsChildren = parseChildNodesOfRequiredNode("teams");
		
		// Loop through all the children, checking whether they are team nodes and constructing them if they are
		for(int i = 0; i < teamsChildren.getLength(); i++) {
			Node teamNode = teamsChildren.item(i);

			// Skip to the next child if the node is not of the team type
			if (!teamNode.getNodeName().equals("team"))
				continue;

			// Get team attributes
			String name = this.parseChildNodeString(teamNode, "name");
			int id = this.parseChildNodeInt(teamNode, "id");
			float spawnpointX = (float)this.parseChildNodeInt(teamNode, "spawnpointX");
			float spawnpointY = (float)this.parseChildNodeInt(teamNode, "spawnpointY");
			
			int capturepointX = this.parseChildNodeInt(teamNode, "capturepointX");
			int capturepointY = this.parseChildNodeInt(teamNode, "capturepointY");
			
			// Create capture region
			FlagCaptureRegion captureRegion = new FlagCaptureRegion(this.world, capturepointX, capturepointY);
			this.entities.add(captureRegion);
			
			// Create team object and add it to the teams list
			Team team = new Team(this.world, id, name, new Vector2f(spawnpointX, spawnpointY), captureRegion);
			
			// Link capture region with team
			captureRegion.setOwnerTeam(team);
			
			this.teams.add(team);
		}
	
		// We must have at least one team or the game wouldn't be playable
		if (this.teams.size() < 1)
			throw new InvalidMapException("The map file has no teams");
	}

	/**
	 * Parses the map file, extracts known entities, and initialises them
	 */
	private void parseEntities() throws InvalidMapException {
		// Get a list of all the children of the entities node
		NodeList entitiesChildren = parseChildNodesOfRequiredNode("entities");

		// Loop through all the entities, selecting only the flag nodes
		for (int i = 0; i < entitiesChildren.getLength(); i++) {
			// Get entity type
			String entityType = entitiesChildren.item(i).getNodeName();

			// Ignore "#text" nodes
			// Chris: not sure what these are but they were causing
			// problems
			if (entityType.equals("#text"))
				continue;
			
			try {
				// We want to declare a class of type entityType
				Class classFromName = Class.forName("shared.entities." + entityType);

				// We want to use the constructor that takes one Node object as an argument
				Constructor entityConstructor = classFromName.getDeclaredConstructor(new Class[] {World.class, Node.class});

				// Now we can actually construct the object
				Object constructedObject = entityConstructor.newInstance(new Object[] {this.world, entitiesChildren.item(i)});
				if (constructedObject instanceof Entity) {
					this.entities.add((Entity)constructedObject);
				}

				// Store the Flag and FlagCaptureRegion
				if (constructedObject instanceof Flag) {
					this.mainFlag = (Flag)constructedObject;
				}
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
				continue;
			}
			catch (NoSuchMethodException e) {
				e.printStackTrace();
				continue;
			}
			catch (InstantiationException e) {
				e.printStackTrace();
				continue;
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
				continue;
			}
			catch (InvocationTargetException e) {
				e.printStackTrace();
				continue;
			}
		}
	}

	/**
	 * Gets child nodes of required node, whose name is specified
	 * @param nodeName The name of the node containing the child nodes
	 * @return The list of nodes who are direct descendants of the node specified by nodeName
	 * @throws InvalidMapException When node specified by nodeName does not exist
	 */
	private NodeList parseChildNodesOfRequiredNode(String nodeName) throws InvalidMapException {
		// Fetch the node
		Node node = this.mapNode.getElementsByTagName(nodeName).item(0);

		// The node is required, check whether it exists
		if (node == null)
			throw new InvalidMapException("Map file does not have a " + nodeName + " node");

		// Return the list of children
		return node.getChildNodes();
	}

	/**
	 * Gets string value of child node
	 * @param node The parent node
	 * @param childNodeName The tag name of the child node containing the string value
	 * @throws InvalidMapException If node or child node does not exist or is not a string
	 */
	private String parseChildNodeString(Node node, String childNodeName) throws InvalidMapException {
		return parseChildNodeValueAsString(node, childNodeName);
	}

	/**
	 * Gets integer value of child node
	 * @param node The parent node
	 * @param childNodeName The tag name of the child node containing the int value
	 * @throws InvalidMapException If node or child node does not exist or is not an int
	 */
	private int parseChildNodeInt(Node node, String childNodeName) throws InvalidMapException {
		String valueAsString = parseChildNodeValueAsString(node, childNodeName);
		int value;
		try {
			// Convert value to an int type
			value = Integer.parseInt(valueAsString);
		} catch (NumberFormatException e) {
			throw new InvalidMapException(node.getNodeName() + " node " + childNodeName + " value was not an int.");
		}

		return value;
	}

	/**
	 * Gets value of child node as a string
	 * @param node The parent node
	 * @param childNodeName The tag name of the child node containing the int value
	 * @throws InvalidMapException If the child node does not exist
	 */
	private String parseChildNodeValueAsString(Node node, String childNodeName) throws InvalidMapException {
		// Get child node
		Node childNode = findChild(node, childNodeName);
		if (childNode == null)
			throw new InvalidMapException(node.getNodeName() + " node did not have " + childNodeName + " value.");
		return childNode.getTextContent();
	}

	/**
	 * Parses the map file, retrieves the specified texture for the map, and sets it
	 */
	private void parseTexture() throws InvalidMapException {
		// Get the texture node	
		Node textureNode = this.mapNode.getElementsByTagName("texture").item(0);
		if (textureNode == null)
			throw new InvalidMapException("Map file has no texture node");

		String textureName = textureNode.getTextContent();
		this.mapTexture = textureName;
	}

	/**
	 * Parses the map file and stores the spawn points declared in it
	 */
	/*private void parseSpawnPoints() throws InvalidMapException {
		// Get the spawnpoints node
		Node spawnPointsNode = this.mapNode.getElementsByTagName("spawnpoints").item(0);

		// According to the documentation, the file must have a set of spawnpoints
		if (spawnPointsNode == null)
			throw new InvalidMapException("Map has no list of spawn points");

		// Get the spawn point node
		Node spawnPointNode = findChild(spawnPointsNode, "spawnpoint");
		if (spawnPointNode == null)
			throw new InvalidMapException("Map has no spawn points.");

		float x = (float)parseChildNodeInt(spawnPointNode, "x");
		float y = (float)parseChildNodeInt(spawnPointNode, "y");
	}*/

	/**
	 * Gets the x co-ordinate of the players spawn point
	 */
	/*public int getSpawnPointX() throws InvalidMapException {
		return this.spawnPointX;
	}*/

	/**
	 * Gets the y co-ordinate of the players spawn point
	 */
	/*public int getSpawnPointY() throws InvalidMapException {
		return this.spawnPointY;
	}*/

	/**
	 * Parses the map file, fetches the map size, and constructs entities in order to implement the size
	 */
	private void parseSize() throws InvalidMapException {
		Node sizeNode = this.mapNode.getElementsByTagName("size").item(0);

		// We will allow for infinite maps until we decide otherwise
		if (sizeNode == null)
			return;

		// Get the map width and height
		this.width = parseChildNodeInt(sizeNode, "width");
		this.height = parseChildNodeInt(sizeNode, "height");

		// Size of the boundary blocks
		// boundarySize is half the screen size, so that it appears to be never ending
		// TODO:Get actual screen width and height from somewhere
		int screenWidth = 800;
		int screenHeight = 600;
		int boundaryWidth = screenWidth/2;
		int boundaryHeight = screenHeight/2;

		// Initialise boundary entities to reflect the width and height
		int offsetHeight = (this.height/2) + (boundaryHeight/2);
		int offsetWidth = (this.width/2) + (boundaryWidth/2);
		Boundary top    = new Boundary(this.world, 0, offsetHeight, this.width + (2*boundaryWidth), boundaryHeight);
		Boundary bottom = new Boundary(this.world, 0, -offsetHeight, this.width + (2*boundaryWidth), boundaryHeight);
		Boundary right  = new Boundary(this.world, offsetWidth, 0, boundaryWidth, this.height);
		Boundary left   = new Boundary(this.world, -offsetWidth, 0, boundaryWidth, this.height);

		// Add the entities to the list
		this.entities.add(top);
		this.entities.add(bottom);
		this.entities.add(right);
		this.entities.add(left);
	}

	/**
	 * Parses the map file and extracts the info
	 * @param mapFileName The file containing the information about the map, such as the spawn point co-ordinates and size of the map
	 */
	private void parseMapFile(String mapFileName) throws InvalidMapException {
		// Use java's built in libraries to parse the map file
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringElementContentWhitespace(true);
		factory.setIgnoringComments(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			// Load the file and parse it
			File mapFile = new File(mapFileName);
			this.mapNode = builder.parse(mapFile).getDocumentElement();
		}
		catch (ParserConfigurationException e) {
			// This exception will never get thrown unless the configuration above is changed in some erroneous way
		}
		catch (SAXException e) {
			// The map was invalid, throw a more meaningful exception	
			throw new InvalidMapException("Could not parse map file");
		}
		catch (IOException e) {
			// Couldn't access the map file
			throw new InvalidMapException("Could not read map file");
		}

		// Set up arraylists to store the objects created from the information in the map file
		this.entities = new ArrayList<Entity>();
		this.teams = new ArrayList<Team>();

		// Parse the next level of nodes in the map file and create the code/data that represents them
		this.parseTexture();
		this.parseSize();
		this.parseTeams();
		this.parseEntities();
	}

	/**
	 * Gets the navmesh for the map related to this class.
	 * @return The navmesh
	 */
	public NavMesh getNavMesh() {
		return this.navMesh;	
	}

	/**
	 * Constructor.
	 * @param mapFileName The file containing the information about the map, such as the spawn point co-ordinates and size of the map
	 * @param world The world the entities should be created in
	 */
	public MapFile(String mapFileName, World world) throws InvalidMapException {
		this.world = world;

		// Replace the .xml extension with .navmesh
		String navMeshFileName;
		if (!mapFileName.endsWith(".xml")) {
			navMeshFileName = mapFileName + ".navmesh";
		} else {
			// Remove the 'xml' part
			navMeshFileName = mapFileName.substring(0, mapFileName.length() - 3) + "navmesh";
		}

		// Initialise the navigation mesh
		this.navMesh = new NavMesh(navMeshFileName);
		
		// Parse the info from the map file
		parseMapFile(mapFileName);
	}

	public String getBackgroundTexture() {
		return this.mapTexture;
	}

	/**
	 * Gets the width of the map
	 * @return The width
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * Gets the height of the map
	 * @return The height
	 */
	public int getHeight() {
		return this.height;
	}
}
