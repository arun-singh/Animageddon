package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import shared.InvalidMapException;
import shared.net.NetworkConstants;

import server.net.ClientAcceptor;;

/**
 * Creates a server instance that clients can join.
 * @author Chris
 *
 */
public class DedicatedServer {
	
	
	//****Class variables****

	private static final long FRAME_INTERVAL_MS = 17; // ~60fps
	
	private ServerWorld serverWorld;
	private int lastUpdateTime = 0;
	
	private ServerSocket serverSocket;
	private ClientAcceptor clientAcceptor;
	
	
	//****Class methods****
	
	
	private void start() {
		
		// Initiate server socket
		try {
		
			serverSocket = new ServerSocket(NetworkConstants.SERVER_PORT);
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
			return;
			
		}
		
		// Initiate server world
		try {
			
			serverWorld = new ServerWorld("maps/original.xml");
			
		} catch (InvalidMapException e) {
			
			e.printStackTrace();
			
			return;
			
		}
		
		// Start listening for clients
		try {
			
			clientAcceptor = new ClientAcceptor(serverSocket, serverWorld);
			clientAcceptor.startListening();
			
			try {
			
				serverWorld.init(clientAcceptor);
				
			} catch (InvalidMapException e) {
				
				e.printStackTrace();
				
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
			return;
			
		}

		System.out.println("Server initiated");
		
		while (true) {
			
			int delta = 0;
			int curTime = (int)serverWorld.getTime();
			
			if (lastUpdateTime > 0) {
			
				delta = curTime - lastUpdateTime;
				
			}
			
			lastUpdateTime = curTime;
			
			serverWorld.update(delta);
			
			try {
				
				Thread.sleep(FRAME_INTERVAL_MS);
				
			} catch (InterruptedException e) {
				
				e.printStackTrace();
				
			}
			
		}
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		DedicatedServer dedicatedServer = new DedicatedServer();
		dedicatedServer.start();
		
	}

}
