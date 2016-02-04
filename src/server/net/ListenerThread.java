package server.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import server.ServerWorld;
import shared.net.NetworkConstants;

/**
 * Listens for and handles messages from a client connected to the server.
 * @author Chris
 *
 */
public class ListenerThread extends Thread {
	
	
	//****Class variables****
	

	private Socket socket = null;
	private ServerWorld serverWorld;

	
	//****Constructors****
	
	
    public ListenerThread(Socket socket, ServerWorld serverWorld) throws SocketException {
    
    	super("Server ListenerThread created");
        this.socket = socket;
        this.serverWorld = serverWorld;
        
        //this.socket.setTcpNoDelay(true);
        
    }
    
    
    //****Class methods****
    
    
    public void run() {
    	
    	DataInputStream in = null;
    	DataOutputStream out = null;
        
    	try {
        	
        	in = new DataInputStream(socket.getInputStream());
        	out = new DataOutputStream(socket.getOutputStream());
        			
            while (true) {
	    
            	int messageTypeID = in.readInt();
				
	        	//System.out.println("server: received message type " + messageTypeID);
	        	
            	if (messageTypeID < 0 || messageTypeID >= NetworkConstants.C2S.values().length) {
				
            		System.out.println("unknown message type " + messageTypeID);
            		break;
            		
				}
				
            	NetworkConstants.C2S messageType = NetworkConstants.C2S.values()[messageTypeID];
	            
	            switch (messageType) {
	            case CONNECT:
	            	serverWorld.onConnectReceived(socket);
	            	break;
	            case KEYBOARD_INPUT:
	            	serverWorld.onKeyInputReceived(socket);
	            	break;
	            case GUN_SHOT_INFO:
	            	serverWorld.onGunShotInfo(socket);
	            	break;
	            case MOUSE_POSITION:
	            	serverWorld.onMousePosition(socket);
	            	break;
	            }
	            
            }
            
        } catch (IOException e) {
        	
        	e.printStackTrace();
        	
        	System.err.println("Listener thread: server lost connection to client");
        	
        }
    	
        finally {
        	
        	if(in != null) {
        	
        		try {
				
        			in.close();
        			
				} catch (IOException e) {}
        		
        	}
        	
        	if(out != null) {
        		
        		try {
				
        			out.close();
        			
				} catch (IOException e) {}
        		
        	}
        	
        }
  	
        this.serverWorld.onSocketDisconnect(socket);
    }
    
}
