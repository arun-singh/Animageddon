package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import client.net.NetworkClient;
import server.NetworkPlayer;
import server.ServerWorld;
import shared.Entity;
import shared.GameInformation;
import shared.GameWindow;
import shared.Physics;
import shared.Team;
import shared.World;
import shared.GUI.Menus.InGameHUD;
import shared.entities.FlagCaptureRegion;
import shared.entities.Player;
import shared.net.NetworkedEntityField;

/**
 * The client implementation of a game world.
 * @author Chris
 *
 */
public class ClientWorld extends World {
	
	
	//****Class variables****
	

	// Local player entity
	private Player localPlayer;

	// Used to draw world
	private WorldView view;

	// Handles local player input
	private InputHandler playerInputHandler;

	// Renders game information to screen
	private GameInformation info;
	//In Game HUD
	private InGameHUD gameHUD;

	// Whether or not the world has been loaded from the server
	private boolean worldLoaded = false;

	// TEMP for client->server communication
	public NetworkPlayer localNetworkPlayer;

	private NetworkClient networkClient;

	private String backgroundTexture;

	private long serverTime = 0;
	private long lastServerTimeUpdate = 0;

	private int localPlayerID = -1;
	
	
	//****Constructors****
	
	
	public ClientWorld(GameWindow gameWindow) {
		
		this.gameWindow = gameWindow;
		
		// Create physics manager
		this.physics = new Physics(this.entities, this);
			
	}
	
	
	//****Class methods****


	/**
	 * shutdowns the client world
	 */
	public void shutdown() {
		
		System.out.println("shutdown client");
		
		if (this.networkClient != null)
			this.networkClient.shutdown();
		
	}
	
	/**
	 * Initiator for a remote server game.
	 * @param networkClient
	 */
	public void init(NetworkClient networkClient) {
			
		this.networkClient = networkClient;
		this.networkClient.connectToServer();
		
		// Create game information
		this.info = new GameInformation(gameWindow,this.teams);
		this.gameHUD = new InGameHUD(gameWindow);
		
	}
	
	/**
	 * This method is called to update the client world
	 */
	@Override
	public void update(int delta) throws IOException {		
		
		if (!isLoaded()) {
		
			if (this.localPlayerID == -1) {
			
				System.out.println("waiting for local player ID");
				return;
				
			}
			
			// Check for local player entity
			Entity localPlayerEntity = this.getEntityByID(this.localPlayerID);
			
			if (localPlayerEntity != null) {
			
				setLocalPlayerEntity(localPlayerEntity);
				System.out.println("set local player entity");
				
			} else {
				
				System.out.println("still no local player entity");
				
			}

			return;
			
		}
			
		// Simulate physics
		physics.update(delta);

		synchronized (entities) {
		
			// Update entities
			for (Entity entity : entities) {
			
				entity.update(delta);
				
			}
			
		}
		
		// Remove entities that are marked to be deleted
		deleteDeadEntities();
			
		// Handle player input
		playerInputHandler.update();
		
		// Update game info
		info.update(delta);
		
	}

	/**
	 * World render method.
	 * Draws the world map, entities and game info.
	 */
	public void render() {
		
		if (!isLoaded()) {
		
			//System.out.println("world not loaded yet");
			return;
			
		}
		
		// Draw map background
		this.view.drawMapBackground();

		// Draw entities
		synchronized (this.getEntities()) {
			
			for (Entity entity : this.getEntities()) {	
			
				entity.render(view);
				//entity.drawBoundingBox(view);
				
			}
			
		}
			
		// Draw on screen information (score)
		this.info.render();
		this.gameHUD.render();
		
	}

	/**
	 * Adds an entity to the entity list.
	 * @param entity entity to add
	 */
	@Override
	public void registerEntity(Entity entity) {
		
		this.entities.add(entity);
		
	}
	
	/**
	 * Called by the server to let the client know their world is loaded.
	 */
	public void onWorldFinishedLoading() {
		
		// Create world view
		this.view = new WorldView(this, this.localPlayer, this.backgroundTexture);

		// Create player input handler
		this.playerInputHandler = new InputHandler(this, this.localPlayer);
		
		this.worldLoaded = true;

		System.out.println("world loaded");
		
	}

	/**
	 * Called by the server to send the background texture name
	 * @param in	the input stream from the server
	 * @return	true to signal a successful transfer
	 * @throws IOException	if there is an error reading from the input stream
	 */
	public boolean onBackgroundMessage(DataInputStream in) throws IOException {
 		
		String backgroundTexture = in.readUTF();
		
		System.out.println("Map background:" + backgroundTexture);
	
		this.backgroundTexture = backgroundTexture;
		
		return true;
		
	}

	/**
	 * Called by the server to send the entity information
	 * @param in	the input stream from the server
	 * @return	true if successful, false otherwise
	 * @throws IOException	if there was an error reading from the input stream
	 */
	public boolean onCreateEntitiesMessage(DataInputStream in) throws IOException {
		
		int numberOfEntities = in.readInt();
		System.out.println("ent count: " + numberOfEntities);
		
		for (int i = 0; i < numberOfEntities; i++) {
		
			String entityType = in.readUTF();

			try {
			
				Class classFromName = Class.forName("shared.entities." + entityType);
				
				// We want to use the constructor that takes one DataInputStream as an argument
				Constructor entityConstructor = classFromName.getDeclaredConstructor(new Class[] {World.class, DataInputStream.class});
					
				// Now we can actually construct the object
				Entity constructedObject = (Entity)entityConstructor.newInstance(new Object[] {this, in});
				
				System.out.println("created entity: " + constructedObject + "," + constructedObject.getID());

				if (this.getEntityByID(constructedObject.getID()) == null)
					this.registerEntity(constructedObject);
				else
					System.out.println("create entity msg for entity already registered");
				
			} catch (ClassNotFoundException e) {
				
				e.printStackTrace();
				return false;
				
			} catch (NoSuchMethodException e) {
				
				e.printStackTrace();
				return false;
				
			} catch (InstantiationException e) {
				
				e.printStackTrace();
				return false;
				
			} catch(IllegalAccessException e) {
				
				e.printStackTrace();
				return false;
				
			} catch (InvocationTargetException e) {
				
				e.printStackTrace();
				return false;
				
			}
			
		}
		
		return true;
	}

	/**
	 * Called by the server to send the local player ID
	 * @param in	the input stream from the server
	 * @return	true if successful
	 * @throws IOException	if there was an error reading from the input stream
	 */
	public boolean onPlayerIDMessage(DataInputStream in) throws IOException {
		
		System.out.println("onPlayerIDMessage");
		
		// Local player id
		this.localPlayerID = in.readInt();
		
		// Local player entity
		Entity localPlayerEntity = this.getEntityByID(this.localPlayerID);
		
		if (localPlayerEntity != null) {
		
			setLocalPlayerEntity(localPlayerEntity);
			System.out.println("set local player entity in player ID message");
			
		} else {
			
			System.out.println("stored local player ID for later");
			
		}
		
		return true;
	}

	/**
	 * Called by the server to send entity updates
	 * @param in	the input stream from the server
	 * @return	true if successful
	 * @throws IOException	if there was an error reading from the input stream
	 */
	public boolean onEntityUpdateMessage(DataInputStream in) throws IOException {
		
		synchronized (this.entities) {
		
			//System.out.println("onEntityUpdateMessage");
			int entityID = in.readInt();
			int fieldID = in.readInt();
			//System.out.println("update: " + entityID + "," + fieldID);
			
			Entity entity = getEntityByID(entityID);
			
			if (entity == null) {
				System.err.println("Entity update received for non-existent entity: " + entityID + "," + fieldID);	
				return false;
			}
			
			for (NetworkedEntityField f : entity.getNetworkedEntityFields()) {
			
				//System.out.println(f.getParentEntityID() + "," + f.getFieldID());
				
				if (f.getFieldID() == fieldID) {
				
					f.readFromNetStream(in);
					f.getParentEntity().onNetworkFieldChange(f);
					
					return true;
					
				}
				
			}
		
			System.err.println("Entity update received for non-existent entity field: " + entityID + "," + fieldID);
			System.out.println("entity fields:");
			for (NetworkedEntityField f : entity.getNetworkedEntityFields()) {
				
				System.out.println(f.getParentEntityID() + "," + f.getFieldID());
				
			}
			
		}
		
		return true;
		
	}

	/**
	 * Called by the server to send deleted entity information
	 * @param in	the input stream from the server
	 * @return	true if successful
	 * @throws IOException	if there was an error reading from the input stream
	 */
	public boolean onDeleteEntitiesMessage(DataInputStream in) throws IOException {
		
		int numberOfEntities = in.readInt();
		System.out.println("(del) ent count: " + numberOfEntities);
		
		for (int i = 0; i < numberOfEntities; i++) {
		
			int entityID = in.readInt();
			
			for (Entity entity : this.getEntities()) {
			
				if (entity.getID() == entityID) {
					
					addDeadEntity(entity);
					
					break;
					
				}
			}
			
		}
		
		return true;
		
	}
	
	/**
	 * Called by the server to send the server time
	 * @param in	the input stream from the server
	 * @return	true if successful
	 * @throws IOException	if there was an error reading from the input stream
	 */
	public boolean onServerTimeMessage(DataInputStream in) throws IOException {
		
		long curTime = in.readLong();
	
		this.serverTime = curTime;
		this.lastServerTimeUpdate = getLocalTime();
	
		return true;
		
	}

	/**
	 * Called by the server to send any keyboard input
	 * @param eventKey	the key that triggered the event
	 * @param pressed	whether the key was pressed or not
	 * @throws IOException	see network client's send keyboard input method
	 */
	public void sendKeyboardInput(int eventKey, boolean pressed) throws IOException {
		
		this.networkClient.sendKeyboardInput(eventKey, pressed);
		
	}

	/**
	 * Called by the server to send team creation information
	 * @param in	the input stream from the server
	 * @return	true if successful
	 * @throws IOException	if there was an error reading from the input stream
	 */
	public boolean onCreateTeamMessage(DataInputStream in) throws IOException {
		
		int teamCount = in.readInt();
		
		for (int i = 0; i < teamCount; i++) {
		
			int teamID = in.readInt();
			String teamName = in.readUTF();
			int teamScore = in.readInt();
			int captureRegionID = in.readInt();
			
			Team team = new Team(this, teamID, teamName);
			team.setTeamScore(teamScore);
			
			FlagCaptureRegion flagCaptureRegion = (FlagCaptureRegion)getEntityByID(captureRegionID);
			team.setFlagCaptureRegion(flagCaptureRegion);

			registerTeam(team);
			
			flagCaptureRegion.setOwnerTeam(team);
			
		}

		return true;
		
	}
	
	/**
	 * Called by the server to send changes in team scores
	 * @param in	the input stream from the server
	 * @return	true if successful
	 * @throws IOException	if there was an error reading from the input stream
	 */
	public boolean onTeamScoreChangeMessage(DataInputStream in) throws IOException{
		
		int teamID = in.readInt();
		int teamScore = in.readInt();
		
		//System.out.println("onTeamScoreChangeMessage: " + teamID + "," + teamScore);
		
		for (Team team : getTeams()) {
		
			if (team.getID() == teamID) {
			
				team.setTeamScore(teamScore);
				
				return true;
				
			}
			
		}

		System.out.println("unknown team score received");
		
		return true;
		
	}
	
	
	//****Getters****
	
	
	/**
	 * Gets the server time in milliseconds
	 */
	@Override
	public long getTime() {
		
		return this.serverTime+(this.getLocalTime()-this.lastServerTimeUpdate);
		
	}

	/**
	 * Gets the local time in milliseconds
	 */
	@Override
	public long getLocalTime() {
		
		return getGameWindow().getLocalTime();
		
	}

	/**
	 * Gets the world's view object.
	 * @return world view object
	 */
	public WorldView getView() {
		
		return this.view;
		
	}
	
	/**
	 * Gets the local player entity.
	 * @return the local player entity
	 */
	public Player getLocalPlayer() {
		
		return this.localPlayer;
		
	}
	
	/**
	 * Gets the network client
	 * @return	the network client
	 */
	public NetworkClient getNetworkClient() {
		
		return this.networkClient;
		
	}
	
	/**
	 * States whether this is the client or not
	 */
	@Override
	public boolean isClient() {
		
		return true;
		
	}
	
	/**
	 * States whether the world is loaded or not
	 * @return whether the world is loaded or not
	 */
	public boolean isLoaded() {
		
		return this.worldLoaded;
		
	}
	
	/**
	 * States whether this is the server or not
	 */
	@Override
	public boolean isServer() {
		
		return false;
		
	}
	
	
	//****Setters****
	
	
	/**
	 * Called by the server to let the client know which entity is their player object.
	 */
	public void setLocalPlayerEntity(Entity entity) {
		
		if (this.localPlayer != null)
			return;
		
		this.localPlayer = (Player)entity;
		this.localPlayer.setIsLocalPlayer(true);
		
		onWorldFinishedLoading();
		
	}	
	
}
