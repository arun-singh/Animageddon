package shared.entities;
import java.io.DataInputStream;
import java.io.IOException;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.openal.Audio;

import client.ClientWorld;
import client.WorldView;
import shared.Entity;
import shared.Team;
import shared.World;

import org.w3c.dom.Node;

import shared.InvalidMapException;
import shared.XMLParsing;

/**
 * The flag capture region entity.
 * This is the entity that players bring the flag back to to obtain points.
 * @author Chris, Barney
 *
 */
public class FlagCaptureRegion extends Entity {

	
	//****Class variables****
	
	
	private final int WIDTH = 100;
	private final int HEIGHT = 100;
	private Team team;
	private Audio flagCapture;
	
	
	//****Constructors****
	
	
	/**
	 * Constructor used to initialise flag capture region from coordinates and team
	 * @param world	the world the region will exist in
	 * @param x	x-coordinate of the region
	 * @param y	y-coordinate of the region
	 */
	public FlagCaptureRegion(World world, int x, int y) {
		
		super(world, x, y);
		
	}

	/**
	 * Constructor used to initialise flag capture region over the network
	 * @param world	the world the region will exist in
	 * @param in	the network stream to read the region information from
	 * @throws IOException	error reading network stream
	 */
	public FlagCaptureRegion(World world, DataInputStream in) throws IOException {
		
		super(world, in);

	}

	/**
	 * Constructor used to initialise flag capture region from an xml node ie from the map file
	 * @param world The world that this region will exist in
	 * @param node The xml node
	 * @throws InvalidMapException Thrown when this region does not support loading via xml node
	 */
	public FlagCaptureRegion(World world, Node node) throws InvalidMapException {
		
		super(world, XMLParsing.parseChildNodeInt(node, "x"), XMLParsing.parseChildNodeInt(node, "y"));
		
	}
	
	
	//****Class methods****
	

	/**
	 * Called when an entity touches this entity or this entity touches another entity.
	 * @param entity the other entity in involved in the touch event
	 */
	public void onTouch(Entity entity) {
	
		if (this.team == null)
			return;
		
		if (entity instanceof Player) {	//player touched flag capture region
		
			Player player = (Player)entity;
			
			Flag flagEntity = player.getHeldFlag();
			
			if (flagEntity != null) {	//player holding flag
				
				if(team.inTeam(player)) {	//player is a member of the region's team
					
					scoreFlag(player, flagEntity);
					
				}
				
			}
			
		}
		
	}
	
	/**
	 * Called when a player reaches the region with a flag
	 * @param player	the player who scored
	 * @param flagEntity the flag held by the player
	 */
	private void scoreFlag(Player player, Flag flagEntity){
		
		//remove flag from player
		player.setHeldFlag(null);
		
		//add Score
		if (this.getWorld().isServer()) {
			team.addScore(player, 1);
		}
		
		//reset flag
		flagEntity.reset();
		
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
		
		float r, g, b;
		
		ClientWorld world = (ClientWorld)this.getWorld();
		
		if (world.getLocalPlayer().getTeam().getID() == this.team.getID()) {
			r = 0.0f;
			g = 1.0f;
			b = 0.0f;
		} else {
			r = 1.0f;
			g = 0.0f;
			b = 0.0f;
		}
		
		Vector2f p = getPosition();
		view.drawWorldRectangleCentered(p.getX(), p.getY(), WIDTH, HEIGHT, r, g, b, true);
		
	}

	//***Setters****
	
	/**
	 * @param team	the team the region will belong to
	 */
	public void setOwnerTeam(Team team) {
		this.team = team;
	}
	
	//***Getters****
	
	
	/**
	 * Gets the width of the region
	 */
	public int getBoundingBoxWidth() {
		
		return WIDTH;
		
	}

	/**
	 * Gets the height of the region
	 */
	public int getBoundingBoxHeight() {
		
		return HEIGHT;
		
	}
	
	/**
	 * States whether the region is solid or not
	 */
	public boolean isSolid() {
		
		return false;
		
	}
	
	public Team getOwnerTeam() {
	
		return team;
		
	}

}
