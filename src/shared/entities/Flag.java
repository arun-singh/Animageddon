package shared.entities;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

import client.WorldView;

import shared.Entity;
import shared.MoveableEntity;
import shared.World;

import org.w3c.dom.Node;
import shared.InvalidMapException;
import shared.XMLParsing;
import shared.net.NetworkedPlayer;

/**
 * The flag entity.
 * @author Chris, Barney
 *
 */
public class Flag extends MoveableEntity {
	
	
	//****Class variables****
	

	private final String FLAG_TEXTURE = "flag";

	private final int WIDTH = 16;
	private final int HEIGHT = 64;
	
	private NetworkedPlayer flagHolder;
	
	private Texture texture;

	private Vector2f origin;
	
	
	//****Constructors****
	
	
	/**
	 * Constructor used to initialise the flag from coordinates
	 * @param world	the world the flag  will exist in
	 * @param x	x-coordinate of the flag
	 * @param y	y-coordinate of the flag
	 */
	public Flag(World world, int x, int y) {
		
		super(world, x, y);
		
		this.origin = new Vector2f(x, y);
		
		createNetworkFields();
		
	}

	/**
	 * Constructor used to initialise the flag from an xml node i.e. from the map file
	 * @param world	the world the flag will exist in
	 * @param node	the xml node
	 * @throws InvalidMapException	thrown when this entity does not support loading via xml node
	 */
	public Flag(World world, Node node) throws InvalidMapException {
		this(world, XMLParsing.parseChildNodeInt(node, "x"), XMLParsing.parseChildNodeInt(node, "y"));
	}
	
	/**
	 * Constructor used to initialise flag over the network
	 * @param world The world that the flag will exist in
	 * @param in the network stream to read the flag information from
	 * @throws IOException error reading network stream
	 */
	public Flag(World world, DataInputStream in) throws IOException {
		
		super(world, in);
		
		createNetworkFields();
		readNetworkFields(in);
		
	}
	
	
	//****Class methods****
	
	private void createNetworkFields() {
		this.flagHolder = new NetworkedPlayer(this);
	}
	
	private void readNetworkFields(DataInputStream in) throws IOException {
		this.flagHolder.readFromNetStream(in);
	}
	
	/**
	 * Serialises the flag properties for networking.
	 * @param out the stream to write to
	 * @throws IOException error writing to stream
	 */
	@Override
	public void writeToNetStream(DataOutputStream out) throws IOException {
		
		super.writeToNetStream(out);
		
		this.flagHolder.writeToNetStream(out);
		
	}
	
	/**
	 * Resets the flag to it's original position with no flag holder
	 */
	public void reset() {
		
		flagHolder.set(null);

		position.set(this.origin);
		
	}
	
	/**
	 * Called when an entity touches this entity or this entity touches another entity.
	 * @param entity the other entity in involved in the touch event
	 */
	public void onTouch(Entity entity) {
		
		if (entity instanceof Player) {	//player touched flag
			
			Player player = (Player)entity;

			//Set player as new flag holder
			setFlagHolder(player);
			
			//Set flag as held by player
			player.setHeldFlag(this);
			
		}
		
	}

	/**
	 * Method called to update the entity.
	 * @param delta time passed since last update
	 */
	@Override
	public void update(int delta) {
		
		super.update(delta);
		
		boolean flagHolderExists = flagHolder.get() != null;
		
		if (flagHolderExists) {	//flag holder present
		
			//Set flag's position to player's
			Vector2f holderPos = flagHolder.get().getPosition();
			setLocalPosition(holderPos);
			
		}
		
		if (this.getWorld().isServer()) {
			
			if (flagHolderExists && !flagHolder.get().isValid()) {	//flag holder is not a valid entity
			
				//Reset flag
				flagHolder.set(null);
				
			}
			
		}
		
	}

	/**
	 * Method called to render the entity.
	 * @param view the world view to render with
	 */
	@Override
	public void render(WorldView view) {
		
		if (texture == null)
			texture = getPNGTexture(FLAG_TEXTURE);
		
		Vector2f p = getPosition();		
		view.drawWorldRectangleCentered(p.getX(), p.getY(), WIDTH, HEIGHT, texture);
		
	}
	
	
	//****Getters****
	
	
	/**
	 * Gets the width of the flag
	 */
	public int getBoundingBoxWidth() {
	
		return WIDTH;
		
	}

	/**
	 * Gets the height of the flag
	 */
	public int getBoundingBoxHeight() {
		
		return HEIGHT;
		
	}
	
	/**
	 * Gets the current flag holder
	 * @return	the flag holder
	 */
	public Player getFlagHolder() {
		
		return flagHolder.get();
		
	}

	/**
	 * States whether or not the flag is solid
	 */
	public boolean isSolid() {
		
		return false;
		
	}
	
	/**
	 * states whether or not the flag is touchable
	 * true if there is no flag holder, false otherwise
	 */
	public boolean isTouchable() {
		
		return flagHolder.get() == null;	
		
	}
	
	
	//****Setters****
	
	
	/**
	 * Sets a new flag holder i.e. when a player picks up the flag
	 * @param player	the new flag holder
	 */
	public void setFlagHolder(Player player) {
		
		this.flagHolder.set(player);

		if (player != null) {
		
			// Set flag position to player position
			Vector2f holderPos = flagHolder.get().getPosition();
			setLocalPosition(holderPos);
			
		} else {
			
			setPosition(new Vector2f(this.getPosition()));
			
		}
		
	}

}
