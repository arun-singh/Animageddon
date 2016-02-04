package shared.entities;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;
import org.w3c.dom.Node;

import client.WorldView;
import shared.Entity;
import shared.InvalidMapException;
import shared.World;
import shared.XMLParsing;

/**
 * A basic solid textured block entity.
 * @author Chris, Barney, Thomas
 *
 */
public class TexturedBlock extends Entity {

	
	//****Class variables****
	
	
	private int width;
	private int height;
	private String textureName;
	private Texture texture;

	
	//****Constructors****
	
	
	/**
	 * Constructor used to initialise the textured block from coordinates, width, height and name
	 * @param world	The world the block will exist in
	 * @param x		the x-coordinate of the block
	 * @param y		the y-coordinate of the block
	 * @param width	the width of the block
	 * @param height	the height of the block
	 * @param textureName	the name of the block
	 */
	public TexturedBlock(World world, float x, float y, int width, int height, String textureName) {
		
		super(world, x, y);
		
		this.width = width;
		this.height = height;
		this.textureName = textureName;
		
	}
	
	/**
	 * Constructor used to initialise the textured block over the network
	 * @param world	the world the block will exist in
	 * @param in	the network stream to read the entity information from
	 * @throws IOException	error reading network stream
	 */
	public TexturedBlock(World world, DataInputStream in) throws IOException {
		
		super(world, in);
		
		this.width = in.readInt();
		this.height = in.readInt();
		this.textureName = in.readUTF();
		
	}

	/**
	 * Constructor used to initialise the textured block from an xml node i.e. from the map file
	 * @param world the world the block will exist in
	 * @param node the xml node
	 * @throws InvalidMapException thrown when this entity does not support loading via xml node
	 */
	public TexturedBlock(World world, Node node) throws InvalidMapException {
		
		this(world,
				XMLParsing.parseChildNodeInt(node, "x"),
				XMLParsing.parseChildNodeInt(node, "y"),
				XMLParsing.parseChildNodeInt(node, "width"),
				XMLParsing.parseChildNodeInt(node, "height"),
				XMLParsing.parseChildNodeString(node, "texture")
		);
		
	}
	
	
	//****Class methods****
	

	/**
	 * Serialises the entity properties for networking.
	 * @param out the stream to write to
	 * @throws IOException error writing to stream
	 */
	@Override
	public void writeToNetStream(DataOutputStream out) throws IOException {
		
		super.writeToNetStream(out);
		
		out.writeInt(width);
		out.writeInt(height);
		out.writeUTF(textureName);
		
	}
	
	/**
	 * Method called to update the entity.
	 * @param delta time passed since last update
	 */
	@Override
	public void update(int delta) {
	}

	/**
	 * Method called to render the entity.
	 * @param view the world view to render with
	 */
	@Override
	public void render(WorldView view) {
		
		// Moved out of constructor temporarily because
		// it was causing problems for the server
		if (texture == null)
			texture = getPNGTexture(textureName);
		
		Vector2f p = getPosition();		
		view.drawWorldRectangleCentered(p.getX(), p.getY(), this.width, this.height, texture);
		
	}
	
	
	//****Getters****
	
	/**
	 * Gets the texture's width
	 */
	public int getBoundingBoxWidth() {
		
		return this.width;
		
	}
	
	/**
	 * Gets the texture's height
	 */
	public int getBoundingBoxHeight() {
		
		return this.height;
		
	}

}
