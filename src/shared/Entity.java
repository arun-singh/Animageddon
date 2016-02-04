package shared;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import client.WorldView;

import org.w3c.dom.Node;

import shared.InvalidMapException;
import shared.net.NetworkedEntityField;
import shared.net.NetworkedVector;

/**
 * A thing/object/entity a game world.
 * Each entity has an ID and position.
 * This class should be extended to implement
 * the functionality of the game object.
 * @author Chris, Barney
 *
 */
public abstract class Entity {
	
	
	//****Class Variables****
	
	
	// Entity identifier
	protected int id;
	
	// Stores the co-ordinates of the entity
	protected NetworkedVector position;
	//protected Vector2f position;
	
	// The world the entity belongs to
	protected World world;

	private boolean valid = true;

	private ArrayList<NetworkedEntityField> networkedEntityFields;
	private int nextFieldID = 0;
	
	//****Constructors****
	
	
	/**
	 * Constructor used to initialise entity from coordinates
	 * @param world The world that this entity will exist in
	 * @param x The x-coordinate of the entity
	 * @param y The y-coordinate of the entity
	 */
	public Entity(World world, float x, float y) {		
		this.world = world;
		this.position = new NetworkedVector(this, new Vector2f(x, y));
	}
	
	/**
	 * Constructor used to initialise entity from an xml node ie from the map file
	 * @param world The world that this entity will exist in
	 * @param node The xml node
	 * @throws InvalidMapException Thrown when this entity does not support loading via xml node
	 */
	public Entity(World world, Node node) throws InvalidMapException {
		throw new InvalidMapException(this.getClass().getSimpleName() + " entity cannot be loaded by the map file");
	}
	
	/**
	 * Constructor used to initialise entity over the network
	 * @param world The world that this entity will exist in
	 * @param in the network stream to read the entity information from
	 * @throws IOException error reading network stream
	 */
	public Entity(World world, DataInputStream in) throws IOException {
		this.world = world;

		this.id = in.readInt();
		this.position = new NetworkedVector(this, null);
		
		//this.position = new Vector2f(in.readFloat(), in.readFloat());
		setPosition(new Vector2f(in.readFloat(), in.readFloat()));
	}
	
	
	//****Class methods****
		
	/**
	 * Serialises the entity properties for networking.
	 * @param out the stream to write to
	 * @throws IOException error writing to stream
	 */
	public void writeToNetStream(DataOutputStream out) throws IOException {
		out.writeInt(getID());
		out.writeFloat(getPosition().getX());
		out.writeFloat(getPosition().getY());
	}

	/**
	 * Registers the entity in the world.
	 * This means it's ready for updating and rendering.
	 */
	public void register() {
		getWorld().registerEntity(this);
	}
	
	/**
	 * Gets a texture object from an image name.
	 * Note: This should be moved
	 * @param imageName the texture image name
	 * @return the texture object
	 */
	public static Texture getPNGTexture(String imageName) {
		try {
			return TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/images/" + imageName + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Called when an entity touches this entity or this entity touches another entity.
	 * @param entity the other entity in involved in the touch event
	 */
	public void onTouch(Entity entity) {
	}
	
	/**
	 * Draws the bounding box around the entity.
	 * Useful for testing physics collisions.
	 * @param view the world view to draw to
	 */
	public void drawBoundingBox(WorldView view) {
		Vector2f p = getPosition();
		int w = getBoundingBoxWidth();
		int h = getBoundingBoxHeight();
		
		view.drawWorldRectangleCentered(p.getX(), p.getY(), w, h, 0.0f, 1.0f, 0.0f, false);	
	}
	
	/**
	 * Called when a networked entity field is changed.
	 */
	@SuppressWarnings("rawtypes")
	public void onNetworkFieldChange(NetworkedEntityField field) {	
	}

	/**
	 * Returns whether or not the entity is active.
	 * For moveable entities, this determines whether they should be updated or not.
	 * @return active or not
	 */
	public boolean isActive() {
		return true;
	}
	
	
	//****Abstract methods****
	
	
	/**
	 * Method called to update the entity.
	 * @param delta time passed since last update
	 */
	public abstract void update(int delta);

	/**
	 * Method called to render the entity.
	 * @param view the world view to render with
	 */
	public abstract void render(WorldView view);
	
	
	//****Getters****
	
	
	/**
	 * Gets the entity ID.
	 * @return entity id
	 */
	public int getID() {
		return this.id;
	}
	
	/**
	 * Gets the entity's world position.
	 * @return position
	 */
	public Vector2f getPosition() {
		return this.position.get();
	}

	/**
	 * Gets the entity's world x coordinate.
	 * @return x coordinate
	 */
	public float getX() {
		return getPosition().x;
	}

	/**
	 * Gets the entity's world y coordinate.
	 * @return y coordinate
	 */
	public float getY() {
		return getPosition().y;
	}

	/**
	 * Gets the world the entity belongs to
	 * @return the world
	 */
	public World getWorld() {
		return this.world;
	}
	
	/**
	 * Gets the width of bounding box for physics collisions.
	 * @return bounding box width
	 */
	public int getBoundingBoxWidth() {
		return 0;
	}
	
	/**
	 * Gets the height of bounding box for physics collisions.
	 * @return bounding box height
	 */
	public int getBoundingBoxHeight() {
		return 0;
	}
	
	/**
	 * Gets the list of networked entity fields.
	 * @return list of networked entity fields
	 */
	public ArrayList<NetworkedEntityField> getNetworkedEntityFields() {
		if (this.networkedEntityFields == null)
			this.networkedEntityFields = new ArrayList<NetworkedEntityField>();
		
		return this.networkedEntityFields;
	}
	
	/**
	 * Gets next available field ID for the entity.
	 * @return the field ID
	 */
	public int getNextFieldID() {
		return this.nextFieldID++;
	}
	
	/**
	 * Determines if the entity uses client-side prediction.
	 * @return true/false
	 */
	public boolean isPredicted() {
		return false;
	}
	
	/**
	 * Determines whether or not the entity is moveable, i.e. it can have velocity.
	 * @return whether or not the entity is moveable
	 */
	public boolean isMoveable() {
		return false;
	}
	
	/**
	 * Determines whether or not the entity is solid, i.e. it collides with other entities.
	 * @return whether or not the entity is solid
	 */
	public boolean isSolid() {
		return true;
	}

	/**
	 * Determines whether or not the entity is touchable, i.e. it can be touched by other entities and trigger the touch event.
	 * @return whether or not the entity is touchable
	 */
	public boolean isTouchable() {
		return true;
	}
	
	/**
	 * Determines if this is a valid entity
	 * @return whether or not the entity is valid
	 */
	public boolean isValid() {
		return this.valid;
	}
	
	
	//****Setters****
	
	
	/**
	 * Sets the entity ID.
	 * @param id entity ID
	 */
	public void setID(int id) {
		this.id = id;
	}
	
	/**
	 * Sets the world the entity belongs to.
	 * @param world the world the entity belongs to
	 */
	public void setWorld(World world) {
		this.world = world;
	}
	
		
	/**
	 * Sets the entity's world position.
	 * @param p position
	 */
	public void setPosition(Vector2f p) {
		this.position.set(p);
	}
	
	/**
	 * Sets the entity's position without broadcasting it to clients.
	 * @param p position
	 */
	public void setLocalPosition(Vector2f p) {
		this.position.setNoBroadcast(p);
	}
	
	public void setValid(boolean valid) {
		this.valid  = valid;
	}
	
}
