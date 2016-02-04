package server.net;

import java.io.IOException;
import java.net.ServerSocket;

import server.ServerWorld;

/**
 * Listens for players trying to join the server.
 * @author Chris
 *
 */
public class ClientAcceptor {
	
	
	//****Class variables****
	
	
	private ServerSocket serverSocket;
	private boolean listening;
	private Runnable serverTask;
	private Thread serverThread;
    
	
	//****Constructors****
	
	
	public ClientAcceptor(final ServerSocket serverSocket, final ServerWorld serverWorld) throws IOException {		
	
		this.serverSocket = serverSocket;
		
		this.serverTask = new Runnable() {
		
			@Override
			public void run() {
		    
				while (listening) {
		        
					try {
		        	
						// TODO: use thread pool
						new ListenerThread(serverSocket.accept(), serverWorld).start();
						
					} catch (IOException e) {
						
						//e.printStackTrace();
						listening = false;
						
					}
					
		        }
		        

				System.err.println("ClientAcceptor: finished listening");
				
			}
			
		};
		
    }
	
	
	//****Class methods****
	
	
	/**
	 * Start listening for connections
	 */
	public void startListening() {
		
		this.listening = true;
		
		this.serverThread = new Thread(this.serverTask);
		this.serverThread.start();
		
	}

	/**
	 * Shut down the client acceptor
	 */
	public void shutdown() {
		
		this.listening = false;

		try {
		
			this.serverSocket.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}	
		
	}
	
}
