package shared;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Defines the basic properties of a game world common to the server
 * and the client.
 * @author Chris
 *
 */
public abstract class World {
	
	
	//****Class variables****
	

	// Game window containing the world
	protected GameWindow gameWindow;

	// Map file the world is loaded from
	protected MapFile mapFile;

	// World entities
	protected List<Entity> entities = Collections.synchronizedList(new ArrayList<Entity>());

	// Entities to be deleted
	// Deletion cannot happen on update
	// as entities are being iterated through
	protected ArrayList<Entity> deadEntities = new ArrayList<Entity>();

	// Manages physics/collisions
	protected Physics physics;

	// Player teams
	protected ArrayList<Team> teams = new ArrayList<Team>();

	public abstract boolean isClient();
	
	public abstract boolean isServer();
	
	
	//****Class methods****
	
	
	/**
	 * World update method. Updates entities and physics.
	 * @param delta time passed since last update
	 * @throws IOException network error
	 */
	public abstract void update(int delta) throws IOException;

	/**
	 * Marks an entity to be deleted.
	 * @param entity entity to be deleted
	 */
	public void addDeadEntity(Entity entity) {
		
		deadEntities.add(entity);
		
	}

	/**
	 * Removes entities that are marked to be deleted.
	 */
	protected void deleteDeadEntities() {
		
		synchronized (this.entities){ 
		
			for (Entity entity : deadEntities) {
			
				entity.setValid(false);
				entities.remove(entity);
				
			}
			
		}
		
		deadEntities.clear();
	}
	
	/**
	 * Adds a team to the list of teams.
	 * @param team team to add
	 */
	public void registerTeam(Team team) {
		
		teams.add(team);
		
	}
	
	
	//****Abstract methods****
	
	
	/**
	 * Registers an entity in the list of world entities
	 * @param entity the entity to be registered
	 */
	public abstract void registerEntity(Entity entity);
	
	/**
	 * Gets current world time in milliseconds.
	 * Equal to system time on server.
	 * Synchronised with server on client.
	 * @return the current world time
	 */
	public abstract long getTime();

	/**
	 * Gets the local time in milliseconds
	 * @return	the local time
	 */
	public abstract long getLocalTime();
	
	
	//****Getters****
	
	
	/**
	 * Gets the physics instance used by the world.
	 * @return physics instance
	 */
	public Physics getPhysics() {
		
		return this.physics;
		
	}
	
	/**
	 * Gets the mapfile instance used by the world.
	 * @return mapfile instance
	 */
	public MapFile getMapFile() {
		
		return this.mapFile;
		
	}
	
	/**
	 * Gets the game window containing the world.
	 * @return game window
	 */
	public GameWindow getGameWindow() {
		
		return this.gameWindow;
		
	}
	
	/**
	 * Finds an entity by its ID.
	 * @param id the entity id
	 * @return the entity
	 */
	public Entity getEntityByID(int id) {
		
		if (id == -1)
			return null;
		
		synchronized (this.entities) {
		
			for (Entity entity : this.entities) {
			
				if (entity.getID() == id)
					return entity;
			}
			
		}
			
		return null;
		
	}
	
	/**
	 * Gets a team by its ID.
	 * @param id the team id
	 * @return the team
	 */
	public Team getTeamByID(int id) {
		
		for (Team team : this.teams) {
		
			if (team.getID() == id)
				return team;
			
		}
		
		return null;
		
	}
	
	/**
	 * Returns the player teams.
	 * @return player teams
	 */
	public ArrayList<Team> getTeams() {
		
		return teams;
		
	}
	
	/**
	 * Gets the list of world entities.
	 * @return world entities
	 */
	public List<Entity> getEntities() {
		
		return this.entities;
		
	}
	
}
