package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import shared.entities.Player;
import client.ClientWorld;

/**
 * Links a client's network socket with their player entity.
 * @author Chris
 *
 */
public class NetworkPlayer {
	
	
	//****Class variables****

	
	// NOTE: this is temporary
	// needs to be a network socket or something eventually
	public ClientWorld clientWorld;
	
	// Client socket
	private Socket socket;
	
	// Player entity
	private Player player;
	
	// Input handler
	public InputHandler inputHandler;

	private DataOutputStream outStream;
	
	
	//****Constructors****
	
	
	/**
	 * Used to initialise the network player using the client world and player
	 * @param clientWorld	the world the network player exists in
	 * @param player	the player corresponding to the network player
	 */
	public NetworkPlayer(ClientWorld clientWorld, Player player) {

		this.clientWorld = clientWorld;
		this.player = player;
		this.inputHandler = new InputHandler(player);
		
	}
	
	/**
	 * Used to initialise the network player using the socket and player
	 * @param socket	the socket belonging to the player
	 * @param player	the player corresponding to the network player
	 */
	public NetworkPlayer(Socket socket, Player player) {
		
		this.socket = socket;
		
		try {
		
			this.outStream = new DataOutputStream(socket.getOutputStream());
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
		this.player = player;
		this.inputHandler = new InputHandler(player);
		
	}
	
	
	//****Getters****
	

	/**
	 * Gets the socket corresponding to the player
	 * @return	the socket
	 */
	public Socket getSocket() {
		
		return this.socket;
		
	}
	
	/**
	 * Gets the player corresponding to this network player
	 * @return	the player entity
	 */
	public Player getPlayerEntity() {
		
		return this.player;
		
	}

	/**
	 * Gets the output stream corresponding to the player
	 * @return	the output stream
	 */
	public DataOutputStream getOutStream() {
		
		return this.outStream;
		
	}
	
}
