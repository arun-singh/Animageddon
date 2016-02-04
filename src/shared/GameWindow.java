package shared;

import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.ServerSocket;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

import server.ServerWorld;
import server.net.ClientAcceptor;
import shared.GUI.InstructionPage;
import shared.GUI.JoinMenu;
import shared.GUI.View;
import shared.GUI.Menus.InGameMenu;
import shared.GUI.Menus.LobbyMenu;
import shared.GUI.Menus.MainMenu;
import shared.GUI.Menus.PlayerSelectMenu;
import shared.entities.Player;
import shared.net.NetworkConstants;
import client.ClientWorld;
import client.InputHandler;
import client.net.NetworkClient;

/**
 * The game window.
 * @author Chris, Richard, Thomas
 *
 */
public class GameWindow {
	
	
	//****Main method****
	

	public static void main(String[] argv) {
		GameWindow gameWindow = new GameWindow();
		gameWindow.start();
	}
	
	
	//****Class variables****
	
	
	//Audio for the main menu
	public static Audio menuTheme;	
	
	//Audio for the game
	public static Audio inGameTheme;

	// Window resolution
	// Constant for now
	private final int WINDOW_WIDTH = 800;
	private final int WINDOW_HEIGHT = 600;

	// time at last frame
	private long lastFrame;
	// frames per second
	private int fps;
	// last fps time
	private long lastFPS;

	//Menus
	private MainMenu mainMenu;
	private PlayerSelectMenu playerSelect;
	private InGameMenu inGameMenu;
	private JoinMenu joinMenu;
	private LobbyMenu lobbyMenu;

	//Instructions
	private InstructionPage instructionPage;

	// Client world for playing the game
	private ClientWorld clientWorld;
	// Server world
	private ServerWorld serverWorld;

	private boolean inGame;
	
	private View currentView;
	private NetworkClient networkClient;

	
	//****Class methods****
	

	/**
	 * Initialises LWJGL and creates the main menu.
	 */
	public void start() {
		
		initDisplay();
		initOpenGL();

		// Initialise lastFrame for time deltas
		getDelta();
		
		// Initialise fps timer
		lastFPS = getTime();

		// Create menu
		mainMenu = new MainMenu(this, 4, 300, 300);
		
		try {
			
			menuTheme = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/Sound/DST-Muses.wav"));
			
			inGameTheme = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/Sound/DST-BreakOut.wav"));
			
			Player.gunShot = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/Sound/GunShot.wav"));
			Player.deathSound = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/Sound/DeathSound.wav"));
			Player.animalSounds = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/Sound/AnimalSounds.wav"));
			
		} catch (IOException e) {
			
			System.out.println("Error loading audio");
			e.printStackTrace();
			System.exit(0);
			
		}
		
		menuTheme.playAsMusic(1f, 1f, true);
		
		currentView = mainMenu;
		
		//player select menu
		playerSelect = new PlayerSelectMenu(this, 4, 480, 100);
		
		//inGameMenu
		inGameMenu = new InGameMenu(this, 4, 300, 300);
		
		//joinMenu
		joinMenu = new JoinMenu(this);
		
		//lobby menu
		lobbyMenu =new LobbyMenu(this, playerSelect, 1, 1,1);
		
		//instruction page
		instructionPage = new InstructionPage(this);
		
		// Game loop
		while (!Display.isCloseRequested()) {
			
			int delta = getDelta();

			update(delta);
			render();
			
		}

		if (clientWorld != null)
			clientWorld.shutdown();

		if (serverWorld != null)
			serverWorld.shutdown();

		//Destroy Audio Loader
		AL.destroy();
		
		// Termination
		Display.destroy();	

		// Exit
		System.exit(-1);
	}


	/**
	 * Initiate display.
	 */
	public void initDisplay() {
		
		try {
		
			Display.setDisplayMode(new DisplayMode(getWidth(), getHeight()));
			Display.create();
			
		} catch (LWJGLException e) {
			
			e.printStackTrace();
			System.exit(1);
			
		}
		
	}

	/**
	 * Initiate open GL.
	 */
	public void initOpenGL() {
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, getWidth(), getHeight(), 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
	}

	/**
	 * Create a new game.
	 */
	public void createGame() {
		
		try {
			
			ServerSocket serverSocket;

			// Initiate server socket
			try {

				serverSocket = new ServerSocket(NetworkConstants.SERVER_PORT);
				
			} catch (BindException e) {

				System.out.println("Error creating server. Check no other instances are running?");
				System.exit(1);
				return;
				
			} catch (IOException e) {
				
				e.printStackTrace();
				return;
				
			}

			// Initiate server world
			serverWorld = new ServerWorld("maps/original.xml", this);

			// Start listening for clients
			try {
				
				ClientAcceptor clientAcceptor = new ClientAcceptor(serverSocket, serverWorld);
				clientAcceptor.startListening();
				
				serverWorld.init(clientAcceptor);
				
				System.out.println("Server initiated");
				
			} catch (IOException e) {
				
				e.printStackTrace();

				return;
				
			}
		
		} catch (InvalidMapException e) {
		
			System.out.println(e.getMessage());
			
		}
		
	}

	/**
	 * Updates the game window.
	 * @param delta time passed since last update
	 */
	private void update(int delta) {	
		
		if (inGame) {
			
			if (serverWorld != null) {
		
				serverWorld.update(delta);
				
			}

			if (clientWorld != null) {
				
				if (clientWorld.getNetworkClient().error) {
				
					endGame();
					
				} else {
					
					try {
					
						clientWorld.update(delta);
						
					} catch (IOException e) {
						
						e.printStackTrace();
						endGame();
						
					}
					
				}
				
			}
			
			// Update FPS
			updateFPS();

		}
		
		if (currentView != null) {

			currentView.update(delta);
			
		}
		
	}

	/**
	 * Renders the game window.
	 * All rendering should be done in this method.
	 */
	private void render() {
		
		// Clear the screen and depth buffer
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);	

		if (inGame) {
		
			if (clientWorld != null) {
			
				// Render world
				clientWorld.render();
				
			}
			
		}

		if (currentView != null) {
			
			currentView.render();
			
		}
		
		Display.update();
		Display.sync(60); // cap fps to 60fps
		
	}

	/**
	 * Calculate the FPS and set it in the title bar
	 */
	private void updateFPS() {
		
		if (getTime() - lastFPS > 1000) {
		
			Display.setTitle("FPS: " + fps);
			fps = 0;
			lastFPS += 1000;
			
		}
		
		fps++;
		
	}

	/**
	 * Converts the y coordinate for mouse to screen coordinate system.
	 * @param y coordinate to be converted
	 * @return The converted coordinate
	 */
	public int mouseToScreenY(int y) {
		
		return getHeight() - y;
		
	}
	
	/**
	 * Enter Lobby
	 */
	public void enterLobbyMenu(){
		
		currentView = lobbyMenu;
		lobbyMenu.getSelectedPlayer();
		
	}
	
	/**
	 * Sets up a connection to a server using the host address and port number
	 * @param hostAddress	the host ip address
	 * @param portNumber	the port number to use
	 * @throws ConnectException	if the connection is refused
	 * @throws IOException	if there is an error reading from the host
	 */
	public void connectServer(String hostAddress, int portNumber) throws ConnectException, IOException {
		
		clientWorld = new ClientWorld(this);
		
		// Create network client
		networkClient = new NetworkClient(clientWorld, hostAddress, portNumber);
		
	}

	/**
	 * Connects to the server and starts the game
	 * @throws IOException	if there is an error reading from the server
	 * @throws ConnectException	if the connection is refused	
	 */
	public void joinGame() throws IOException, ConnectException {
		
		if (menuTheme != null)
			menuTheme.stop();
		
		inGameTheme.playAsMusic(1f, 1f, true);
		
		if (inGame)
			return;
		
		currentView = null;
		inGame = true;

		// Initiate world (connects to server)
		clientWorld.init(networkClient);
		
	}
	
	/**
	 * Joins a local game on the client's PC
	 */
	public void joinLocalGame() {
		
		if (menuTheme != null)
			menuTheme.stop();
		
		inGameTheme.playAsMusic(1f, 1f, true);
		
		try {
		
			//Setup local address
			connectServer("127.0.0.1", NetworkConstants.SERVER_PORT);
			
			joinGame();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	/**
	 * Shuts down the game and return to the main menu
	 */
	private void endGame() {
		
		if (!inGame)
			return;
		
		System.out.println("end game");
		
		inGame = false;
		
		if (clientWorld != null) {
			
			clientWorld.shutdown();
			clientWorld = null;
			
		}
		
		if (serverWorld != null) {
		
			serverWorld.shutdown();
			serverWorld = null;
			
		}
		
		inGameTheme.stop();
		
		// Return to main menu
		setMainView();
	}
	
	
	
	/**
	 * Resumes the game
	 */
	public void resumeGame(){
		
		currentView = null;
		
	}
	
	/**
	 * Used when button is clicked to prevent
	 * mouse input being registered on next window
	 */
	public void delay(long delay){
		
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

	
	
	/**
	 * Returns the width of the game window.
	 * @return width of the game window
	 */
	public int getWidth() {
	
		return WINDOW_WIDTH;
		
	}

	/**
	 * Returns the height of the game window.
	 * @return height of the game window
	 */
	public int getHeight() {
		
		return WINDOW_HEIGHT;

	}

	/** 
	 * Calculate how many milliseconds have passed 
	 * since last frame.
	 * 
	 * @return milliseconds passed since last frame 
	 */
	private int getDelta() {
		
		long time = getTime();
		int delta = (int) (time - lastFrame);
		lastFrame = time;

		return delta;
		
	}

	/**
	 * Get the time. Returns synchronised server time if in-game, otherwise local system time.
	 * 
	 * @return The time in milliseconds
	 */
	private long getTime() {
		
		if (inGame)
			return clientWorld.getTime();
		else
			return getLocalTime();
		
	}
	
	/**
	 * Get the accurate system time
	 * 
	 * @return The system time in milliseconds
	 */
	public long getLocalTime() {
		
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
		
	}

	/**
	 * Gets the client world instance.
	 * @return client world
	 */
	public World getClientWorld() {
		
		return clientWorld;
		
	}
	
	/**
	 * Gets the server world instance - used for testing
	 * @return	server world
	 */
	public World getServerWorld() {
		
		return serverWorld;
		
	}
	
	/**
	 * Gets the network client - used for testing
	 * @return	network client
	 */
	public NetworkClient getNetworkClient() {
		
		return networkClient;
		
	}
	
	/**
	 * Checks if user is in game
	 * @return True if the user is in the game and false otherwise
	 */
	public boolean isInGame() {
		return inGame;
	}

	
	
	//****Setters****
	
	
	/**
	 * Sets the current view to the main menu
	 */
	public void setMainView() {
		
		if (!menuTheme.isPlaying()) {		
		
			menuTheme.playAsMusic(1.0f, 1.0f, true);
		
		}
		
		
		currentView = mainMenu;
		endGame();
		
	}
	
	/**
	 * Setswwwww the current view to the player select menu
	 */
	public void setPlayerView() {
		
		currentView = playerSelect;
		endGame();
		
	}
	
	/**
	 * Sets the current view to the game menu
	 */
	public void setInGameMenu(){
		
		currentView = inGameMenu;
		
	}
	
	/**
	 * Sets the current view to the join game menu
	 */
	public void setJoinGameMenu() {
		
		currentView = joinMenu;
		
	}
	
	/**
	 * Sets the current view to the instruction page
	 */
	public void setInstructionPage(){
		
		currentView = instructionPage;
		
	}
	
}
