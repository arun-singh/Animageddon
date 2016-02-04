package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.lwjgl.Sys;
import org.lwjgl.util.vector.Vector2f;

import server.net.ClientAcceptor;
import shared.Entity;
import shared.EntityIntersectionInfo;
import shared.GameWindow;
import shared.InvalidMapException;
import shared.MapFile;
import shared.Physics;
import shared.Team;
import shared.World;
import shared.entities.Player;
import shared.entities.AIPlayer;
import shared.net.NetworkConstants;
import shared.net.NetworkedEntityField;

/**
 * The server implementation of a game world.
 * @author Chris, Barney
 *
 */
public class ServerWorld extends World {
	
	
	//****Class variables****
	

	private static final int TEST_AI_COUNT = 2;

	private int nextEntityID = 0;
	
	private List<NetworkPlayer> networkPlayers = Collections.synchronizedList(new ArrayList<NetworkPlayer>());

	private ArrayList<NetworkPlayer> deadNetworkPlayers = new ArrayList<NetworkPlayer>();
	private ArrayList<NetworkPlayer> newNetworkPlayers = new ArrayList<NetworkPlayer>();

	private List<NetworkedEntityField> entityFieldsToBroadcast = Collections.synchronizedList(new ArrayList<NetworkedEntityField>());
	private List<Entity> newEntities = Collections.synchronizedList(new ArrayList<Entity>());

	private ClientAcceptor clientAcceptor;
	
	
	//****Constructors****
	 

	/**
	 * Constructor for non-dedicated server.
	 * @param mapFileName map to load
	 * @param gameWindow window containing world
	 * @throws InvalidMapException
	 */
	public ServerWorld(String mapFileName, GameWindow gameWindow) throws InvalidMapException {
		
		// Store game window
		this.gameWindow = gameWindow;

		// Initialise the map with the information in the map file
		this.mapFile = new MapFile(mapFileName, this);

		// Create physics manager
		this.physics = new Physics(this.entities, this);
		
	}

	/**
	 * Constructor for dedicated server.
	 * @param mapFileName map to load
	 * @throws InvalidMapException
	 */
	public ServerWorld(String mapFileName) throws InvalidMapException {
		
		this(mapFileName, null);
		
	}
	
	
	//****Class methods****
	

	/**
	 * Closes all conections and shuts down the server
	 */
	public void shutdown() {
		
		System.out.println("shutdown server");
		
		for (NetworkPlayer networkPlayer : this.getNetworkPlayers()) {
		
			Socket s = networkPlayer.getSocket();
			
			if (s == null)
				continue;
			
			try {
		
				s.close();
				
			} catch (IOException e) {
				
				e.printStackTrace();
				
			}
			
		}
		
		if (clientAcceptor != null)
			clientAcceptor.shutdown();
		
	}
	
	/**
	 * Initialises the server
	 * @param clientAcceptor	Accepts client connections
	 * @throws InvalidMapException	If there is an error loading from the map
	 */
	public void init(ClientAcceptor clientAcceptor) throws InvalidMapException {	
		
		this.clientAcceptor = clientAcceptor;
		
		// Add static entities from map to entity list
		// We could just append the arraylist to the end of the current structure, but calling Entity.register means that this code won't need to change in the future
		ArrayList<Entity> mapEntities = this.mapFile.getEntities();
		for (Entity e : mapEntities)
			e.register();

		// Register teams
		ArrayList<Team> mapTeams = this.mapFile.getTeams();
		for (Team t : mapTeams) {
			registerTeam(t);
		}

		for (int i = 0; i < TEST_AI_COUNT; i++) {
		
			// TODO: Remove hardcoded capture region
//			TestAI testAI = new TestAI(this, 0, 0, this.mapFile.getMainFlag(), this.mapFile.getMainFlagCaptureRegion().get(1), this.mapFile);
						
			AIPlayer testAI = new AIPlayer(this, 0, 0, this.mapFile.getMainFlag(), this.mapFile);
			testAI.setPlayerClass(getRandomAnimalClass());
			
			// Add the AI player to the AI team
			Team team = getTeamToJoin();
			team.addPlayer(testAI);
			
			// Send to spawnpoint
			testAI.setPosition(team.getSpawnPointCopy());
			
			// Register AI entity
			testAI.register();
			
		}
		
	}

	/**
	 * This method is called to update the server world
	 */
	@Override
	public void update(int delta) {
		
		// Remove network players that are marked to be deleted and add new ones
		updateNetworkPlayers();
		
		// Do physics
		physics.update(delta);
		
		// Update entities
		synchronized (this.getEntities()) {
			
			for (Entity entity : this.getEntities()) {
			
				entity.update(delta);
				
			}
			
		}
				
		// Do networking
		synchronized (this.networkPlayers) {
			
			for (NetworkPlayer networkPlayer : this.networkPlayers) {		
				Socket socket = networkPlayer.getSocket();
					
				if (socket == null)
					continue;
		
				try {
			
					broadcastServerTime(networkPlayer);
	
					if (this.deadEntities.size() > 0)
						broadcastDeadEntities(networkPlayer);
					
					if (this.newEntities.size() > 0)
						broadcastNewEntities(networkPlayer);
					
					if (this.entityFieldsToBroadcast.size() > 0)
						broadcastEntityUpdates(networkPlayer);
					
				} catch (IOException e) {
					
					e.printStackTrace();
					
					removeNetworkPlayer(networkPlayer);
					
				}
				
			}
			
		}
			
		// Remove entities that are marked to be deleted
		deleteDeadEntities();
	
		//this.deadEntities.clear();
		this.newEntities.clear();
		this.entityFieldsToBroadcast.clear();
		
	}
	
	/**
	 * Removes a network player from the entity list
	 * @param networkPlayer	the player to be removed
	 */
	private void removeNetworkPlayer(NetworkPlayer networkPlayer) {
		
		deadNetworkPlayers.add(networkPlayer);
		addDeadEntity(networkPlayer.getPlayerEntity());
		
	}

	/**
	 * Sends the information of dead entities to the network player
	 * @param networkPlayer	the player being contacted
	 * @throws IOException	if there is an error writing to the output stream
	 */
	private void broadcastDeadEntities(NetworkPlayer networkPlayer) throws IOException {
		
		DataOutputStream out = networkPlayer.getOutStream();
		
		synchronized (out) {
		
			// Message type
			out.writeInt(NetworkConstants.S2C.DELETE_ENTITIES.ordinal());
			
			synchronized (this.deadEntities) {
			
				// Number of entities
				out.writeInt(this.deadEntities.size());
				
				for (Entity entity : this.deadEntities) {
				
					// entity id
					out.writeInt(entity.getID());
					
				}
				
			}
			
		}
		
	}
	
	/**
	 * Sends the new entity information to the network player
	 * @param networkPlayer	the player being contacted
	 * @throws IOException	if there is an error writing to the output stream
	 */
	private void broadcastNewEntities(NetworkPlayer networkPlayer) throws IOException {	
		
		System.out.println("sending new entities");
		
		DataOutputStream out = networkPlayer.getOutStream();
		
		synchronized (out) {
		
			// Message type
			out.writeInt(NetworkConstants.S2C.CREATE_ENTITIES.ordinal());
			
			synchronized (this.newEntities) {
			
				// Number of entities
				out.writeInt(this.newEntities.size());
	
				for (Entity entity : this.newEntities) {
					
					// entity class name (without shared.entities. prefix)
					out.writeUTF(entity.getClass().getSimpleName());
					// entity properties
					entity.writeToNetStream(out);
					
				}
				
			}	
			
		}
		
	}
	
	/**
	 * Sends the entity update information to the network player
	 * @param networkPlayer	the player being contacted
	 * @throws IOException	if there is an error writing to the output stream
	 */
	private void broadcastEntityUpdates(NetworkPlayer networkPlayer) throws IOException {		
		
		DataOutputStream out = networkPlayer.getOutStream();
	
		synchronized (out) {
		
			synchronized (this.entityFieldsToBroadcast) {
			
				for (NetworkedEntityField entityField : this.entityFieldsToBroadcast) {
				
					// Message type
					out.writeInt(NetworkConstants.S2C.UPDATE_ENTITY_FIELD.ordinal());
					// Entity ID
					out.writeInt(entityField.getParentEntityID());
					// Field ID
					out.writeInt(entityField.getFieldID());
					// Field value
					entityField.writeToNetStream(out);
					
				}
				
			}
			
		}
		
	}
	
	/**
	 * Sends the server time to the network player
	 * @param networkPlayer	the player being contacted
	 * @throws IOException	if there is an error writing to the output stream
	 */
	private void broadcastServerTime(NetworkPlayer networkPlayer) throws IOException {
		
		DataOutputStream out = networkPlayer.getOutStream();
		
		synchronized (out) {
		
			// Message type
			out.writeInt(NetworkConstants.S2C.SET_SERVER_TIME.ordinal());
			// Server time
			out.writeLong(getTime());
			
		}
		
	}

	/**
	 * Updates network players list.
	 * Adds newly connected players and removes disconnected players.
	 */
	private void updateNetworkPlayers() {
		
		synchronized (this.networkPlayers) {
		
			for (NetworkPlayer networkPlayer : this.newNetworkPlayers) {
			
				this.networkPlayers.add(networkPlayer);
			}
			
			
			for (NetworkPlayer networkPlayer : this.deadNetworkPlayers) {
			
				// Remove player from team list
				Team playerTeam = networkPlayer.getPlayerEntity().getTeam();
				if (playerTeam != null)
					playerTeam.removePlayer(networkPlayer.getPlayerEntity());
				
				// Remove from network players list
				this.networkPlayers.remove(networkPlayer);
			}
			
		}
			
		this.newNetworkPlayers.clear();
		this.deadNetworkPlayers.clear();
		
	}

	/**
	 * Adds an entity to the list of world entities.
	 * @param entity entity to add
	 */
	@Override
	public void registerEntity(Entity entity) {
		
		entity.setID(this.nextEntityID++);
		
		this.entities.add(entity);
		
		if (this.networkPlayers.size() > 0 || this.newNetworkPlayers.size() > 0)
			this.newEntities.add(entity);
		
	}
	
	/**
	 * Called when a team's score changes.
	 * @param teamID the team id
	 * @param teamScore the team score
	 */
	public void onTeamScoreChanged(int teamID, int teamScore) {
		
		//System.out.println("server: onTeamScoreChanged");
		
		synchronized (getNetworkPlayers()) {
		
			for (NetworkPlayer networkPlayer : getNetworkPlayers()) {
			
				try {
				
					DataOutputStream out = networkPlayer.getOutStream();
					
					synchronized (out) {
					
						// Message type
						out.writeInt(NetworkConstants.S2C.TEAM_SCORE_CHANGE.ordinal());
						// Team ID
						out.writeInt(teamID);
						// Team score
						out.writeInt(teamScore);
						
					}	
					
				} catch (IOException e) {
					
					removeNetworkPlayer(networkPlayer);
					continue;
					
				}
				
			}
			
		}
		
	}

	/**
	 * Called when a player connects to the server.
	 * @param socket player socket
	 * @throws IOException
	 */
	public void onConnectReceived(Socket socket) throws IOException {
		
		System.out.println("Received connect, send info");
		
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		
		synchronized (out) {

			// Create player object
			Player player = new Player(this, 0, 0);
			player.setPlayerClass(getRandomAnimalClass());
	
			// Add to team
			Team team = getTeamToJoin();
			team.addPlayer(player);

			// Send to spawnpoint
			player.setPosition(team.getSpawnPointCopy());

			// Register player
			player.register();

			// Store player+socket
			NetworkPlayer networkPlayer = new NetworkPlayer(socket, player);
			this.newNetworkPlayers.add(networkPlayer);
			
			// Message type
			out.writeInt(NetworkConstants.S2C.CREATE_ENTITIES.ordinal());
			// Number of entities
			out.writeInt(getEntities().size());
				
			// Entities
			synchronized (getEntities()) {
			
				for (Entity e : getEntities()) {
				
					// entity class name (without shared.entities. prefix)
					out.writeUTF(e.getClass().getSimpleName());
					// entity properties
					e.writeToNetStream(out);
					
				}
				
			}			
				
			// Send teams info
			out.writeInt(NetworkConstants.S2C.CREATE_TEAM.ordinal());
			out.writeInt(getTeams().size());
			for (Team _team : getTeams()) {
				out.writeInt(_team.getID());
				out.writeUTF(_team.getTeamName());
				out.writeInt(_team.getTeamScore());
				out.writeInt(_team.getFlagCaptureRegion().getID());
			}
			
			// Send background texture
			out.writeInt(NetworkConstants.S2C.SET_BACKGROUND_TEXTURE.ordinal());
			out.writeUTF(mapFile.getBackgroundTexture());
			
			// Send server time
			out.writeInt(NetworkConstants.S2C.SET_SERVER_TIME.ordinal());
			out.writeLong(getTime());
			
			// Send player entity ID
			out.writeInt(NetworkConstants.S2C.SET_PLAYER_ID.ordinal());
			out.writeInt(player.getID());
			
		}
		
	}
	
	/**
	 * Gets a random animal class string.
	 * @return class name
	 */
	public static String getRandomAnimalClass() {
		switch ((int)((Math.random()*100) % 4)) {
		case 0:
			return "Elephant";
		case 1:
			return "Goat";
		case 2:
			return "Monkey";
		case 3:
			return "Zebra";
		}
		
		return null;
	}
	
	/**
	 * Returns a team to join.
	 * Chooses based on which team has the least players currently.
	 * @return the team to join
	 */
	public Team getTeamToJoin() {
		Team teamToJoin = null;
		
		for (Team team : mapFile.getTeams()) {
			if (teamToJoin == null || team.getPlayers().size() < teamToJoin.getPlayers().size()) {
				teamToJoin = team;
			}
		}
		
		return teamToJoin;
	}

	/**
	 * Adds an updated entity to the list to be broadcasted
	 * @param entityField	the updated entity
	 */
	public void broadcastEntityUpdate(NetworkedEntityField entityField) {
		
		/*synchronized (entityFieldsToBroadcast) {
			// Remove old updates if any
			for (NetworkedEntityField f : entityFieldsToBroadcast) {
				if (f.getParentEntityID() == entityField.getParentEntityID() && f.getFieldID() == entityField.getFieldID()) {
					entityFieldsToBroadcast.remove(entityField);
					break;
				}
			}
		}*/

		// Add updated field
		try {
		
			entityFieldsToBroadcast.add((NetworkedEntityField)entityField.clone());
			
		} catch (CloneNotSupportedException e) {
			
			e.printStackTrace();
			
		}
		
	}

	/**
	 * Gets the network player associated with a socket
	 * @param socket	the socket
	 * @return	the network player
	 */
	public NetworkPlayer socketToNetworkPlayer(Socket socket) {
		
		for (NetworkPlayer networkPlayer : this.networkPlayers) {			
		
			if (socket == networkPlayer.getSocket()) {
			
				return networkPlayer;
				
			}
			
		}
		
		return null;
		
	}
	
	/**
	 * Called when a key input is received - handles the input
	 * @param socket	the socket where the input originated
	 * @throws IOException	if there was an error reading from the socket
	 */
	public void onKeyInputReceived(Socket socket) throws IOException {
		
		DataInputStream in = new DataInputStream(socket.getInputStream());
		
		int eventKey = in.readInt();
		boolean pressed = in.readBoolean();
		
		NetworkPlayer networkPlayer = socketToNetworkPlayer(socket);
		networkPlayer.inputHandler.handleKeyboardInput(eventKey, pressed);
		
		//System.out.println("received input from client: " + eventKey);
		
	}

	/**
	 * Called when a client fires their gun.
	 * @param socket client socket
	 * @throws IOException if there was an error reading from the socket
	 */
	public void onGunShotInfo(Socket socket) throws IOException {
	
		NetworkPlayer networkPlayer = socketToNetworkPlayer(socket);
		Player networkPlayerEntity = networkPlayer.getPlayerEntity();

		DataInputStream in = new DataInputStream(socket.getInputStream());
	
		// Current mouse position in map coordinates
		float mouseX = in.readFloat();
		float mouseY = in.readFloat();
		Vector2f mouseMapPos = new Vector2f(mouseX, mouseY);
		
		// List of intersection info
		int intersectionCount = in.readInt();
		ArrayList<EntityIntersectionInfo> intersectionInfoList = null;
		
		if (intersectionCount > 0) {
		
			intersectionInfoList = new ArrayList<EntityIntersectionInfo>(intersectionCount);
			
			// Read intersection info
			for (int i = 0; i < intersectionCount; i++) {
				boolean ignore = false;
				
				Entity hitEntity = getEntityByID(in.readInt());
				
				if (hitEntity == null) {
					System.out.println("Invalid intersection info received");
					ignore = true;
					//throw new IOException("Invalid intersection info received");
				}
				
				EntityIntersectionInfo info = new EntityIntersectionInfo(hitEntity);
				
				// List of intersection points
				int intersectionPointCount = in.readInt();
				
				for (int j = 0; j < intersectionPointCount; j++) {
				
					float x = in.readFloat();
					float y = in.readFloat();
					
					Vector2f p = new Vector2f(x, y);
					
					info.addIntersectionPoint(p);
					
				}
				
				if (!ignore) {
					// Store intersection info
					intersectionInfoList.add(info);
				}
			}
			
		}

		if (networkPlayerEntity.getIsDead()) {
			
			System.out.println("gun shot when player is dead");
			
			return;
			
		}

		// Network gun shot to other players
		networkPlayerEntity.calculateAimVec(mouseMapPos, this);
		
		//networkPlayerEntity.shoot();
		
		networkPlayerEntity.fireGun();
		handleGunShot(networkPlayerEntity, intersectionInfoList);
		
	}

	/**
	 * Called when there is a change in mouse position
	 * @param socket	the socket where the change originated
	 * @throws IOException	if there was an error reading from the socket
	 */
	public void onMousePosition(Socket socket) throws IOException {
		
		NetworkPlayer networkPlayer = socketToNetworkPlayer(socket);
		Player networkPlayerEntity = networkPlayer.getPlayerEntity();

		DataInputStream in = new DataInputStream(socket.getInputStream());
		
		float mouseX = in.readFloat();
		float mouseY = in.readFloat();
		Vector2f mouseMapPos = new Vector2f(mouseX, mouseY);
			
		networkPlayerEntity.calculateAimVec(mouseMapPos, this);
		
		//System.out.println("onMousePosition: " + networkPlayer.getPlayerEntity().getID() + "," + mouseMapPos);
		
	}

	/**
	 * Called when a client disconnects - removes the player from the server
	 * @param socket	the socket associated with the disconnected player
	 */
	public void onSocketDisconnect(Socket socket) {
		
		for (NetworkPlayer networkPlayer : this.getNetworkPlayers()) {
    	
			if (networkPlayer.getSocket() == socket) {
    			
				removeNetworkPlayer(networkPlayer);
    			break;
    			
    		}
			
    	}
		
	}

	/**
	 * Performs a gun shot and deals damage if necessary
	 * @param player	the player who is shooting
	 * @param intersectionInfoList	any intersections along the bullet path
	 */
	public void handleGunShot(Player player, ArrayList<EntityIntersectionInfo> intersectionInfoList) {
		
		if (intersectionInfoList != null) {
		
			// Find nearest intersection to the player
			Entity nearestIntersectingEntity = null;
			float nearestIntersectionDistance = 0.0f;
			
			for (EntityIntersectionInfo info : intersectionInfoList) {
				
				// Ignore intersections with players on your team
				if (info.getEntity() instanceof Player) {
				
					Player _player = (Player)info.getEntity();
					
					if (player.getTeam().getID() == _player.getTeam().getID())
						continue;
					
				}
				
				// Check intersection points
				for (Vector2f p : info.getIntersectionPoints()) {
					
					// Calculate distance between intersection point and player
					Vector2f dif = new Vector2f();
					Vector2f.sub(player.getPosition(), p, dif);
					
					float len = dif.length();
					
					// Update nearest intersection entity
					if (nearestIntersectionDistance == 0.0f || len < nearestIntersectionDistance) {
						
						nearestIntersectionDistance = len;
						nearestIntersectingEntity = info.getEntity();
						
					}
					
				}	
				
			}
			
			// Check if nearest intersecting entity is player and do damage if so
			if (nearestIntersectingEntity != null) {								
				
				if (nearestIntersectingEntity instanceof Player) {
					Player hitPlayer = (Player)nearestIntersectingEntity;
					
					//System.out.println("hit player");
					
					if (hitPlayer.getTeam().getID() != player.getTeam().getID()) {
				
						// TODO: move damage into method or constant
						hitPlayer.doDamage(player.getWeaponDamage());
						
					}
					
				}
				
			}
			
		}	
		
	}
	
	
	//****Getters****
	
	
	/**
	 * Gets the server time in milliseconds
	 */
	@Override
	public long getTime() {
		
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
		
	}
	
	/**
	 * Gets the local time in milliseconds
	 */
	@Override
	public long getLocalTime() {
		
		return getTime();
		
	}

	/**
	 * Gets the map's background texture
	 * @return background texture
	 */
	public String getMapTexture() {
	
		return mapFile.getBackgroundTexture();
		
	}

	/**
	 * Gets the network players
	 * @return network players
	 */
	public List<NetworkPlayer> getNetworkPlayers() {
		
		return this.networkPlayers;
		
	}
	
	/**
	 * States whether the world is a client or not
	 */
	@Override
	public boolean isClient() {
		return false;
	}
	
	/**
	 * States whether the world is a server or not
	 */
	@Override
	public boolean isServer() {
		return true;
	}
	
}
