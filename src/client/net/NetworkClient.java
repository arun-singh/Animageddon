package client.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

import client.ClientWorld;

import shared.EntityIntersectionInfo;
import shared.net.NetworkConstants;

/**
 * Handles networking on the client.
 * @author Chris
 *
 */
public class NetworkClient {
	
	
	//****Class variables****
	

	private Socket socket;
	private final ClientWorld clientWorld;
	private DataOutputStream out;
	private Thread clientThread;
	public boolean error;
	
	
	//****Constructor****
	
	
	public NetworkClient(final ClientWorld world, String hostName, int portNumber) throws IOException, ConnectException {	
		
		this.clientWorld = world;
        this.socket = new Socket(hostName, portNumber);
        //this.socket.setTcpNoDelay(true);
        
		// Read error
		this.error = false;
			
        Runnable clientTask = new Runnable() {
        	
			@Override
			public void run() {	        
		    
				// Listen for packets
				DataInputStream in = null;
				
				try {
				
					in = new DataInputStream(socket.getInputStream());
					
					while (!error) {					
					
						int messageTypeID = in.readInt();
						
						//System.out.println("client: received message type " + messageTypeID);
		            	
						if (messageTypeID < 0 || messageTypeID >= NetworkConstants.S2C.values().length) {
						
							System.out.println("unknown message type " + messageTypeID);
		            		error = true;
		            		break;
		            		
						}
						
						NetworkConstants.S2C messageType = NetworkConstants.S2C.values()[messageTypeID];
		            	
		            	switch (messageType) {
		            	case CREATE_ENTITIES: // create entities
		            		error = !clientWorld.onCreateEntitiesMessage(in);
		            		break;
		            	case DELETE_ENTITIES: // delete entities
		            		error = !clientWorld.onDeleteEntitiesMessage(in);
		            		break;
		            	case UPDATE_ENTITY_FIELD: // entity update
		            		error = !clientWorld.onEntityUpdateMessage(in);
		            		break;
		            	case SET_BACKGROUND_TEXTURE: // background texture         		
		            		error = !clientWorld.onBackgroundMessage(in);
		            		break;
		            	case SET_PLAYER_ID: // player id
		            		error = !clientWorld.onPlayerIDMessage(in);
		            		break;
		            	case SET_SERVER_TIME:
		            		error = !clientWorld.onServerTimeMessage(in);
		            		break;
		            	case CREATE_TEAM:
		            		error = !clientWorld.onCreateTeamMessage(in);
		            		break;
		            	case TEAM_SCORE_CHANGE:
		            		error = !clientWorld.onTeamScoreChangeMessage(in);
		            		break;
		            	}
					}
            	} catch (IOException e) {
            		
            		//e.printStackTrace();
            		
            		System.err.println("NetworkClient: connection lost to server");
            		            		
            		error = true;
            		
            	}
            	finally {
            		
            		if (in != null) {
            		
            			try {
						
            				in.close();
						
            			} catch (IOException e) {}
            		}
            		
            	}
				
			}
			
        };
        
        this.clientThread = new Thread(clientTask);
        this.clientThread.start();
        
        this.out = new DataOutputStream(socket.getOutputStream());
        
	}
	
	
	//****Class methods****
	

	/**
	 * 
	 */
	public void connectToServer() {
		
		try {
			
			synchronized (out) {
			
				// Message type
				out.writeInt(NetworkConstants.C2S.CONNECT.ordinal());
				
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
			System.exit(-1);
			
		}   
		
	}

	public void sendKeyboardInput(int eventKey, boolean pressed) throws IOException {
	
		synchronized (out) {	
		
			// Message type
			out.writeInt(NetworkConstants.C2S.KEYBOARD_INPUT.ordinal());
			// Event key
			out.writeInt(eventKey);
			// Key pressed or not
			out.writeBoolean(pressed);
			
		}
		
	}

	/**
	 * Sends the server the client's mouse position in map coordinates.
	 * @param mouseMapPos mouse position
	 * @throws IOException
	 */
	public void sendMousePosition(Vector2f mouseMapPos) throws IOException {
		synchronized (out) {
			// Message type
			out.writeInt(NetworkConstants.C2S.MOUSE_POSITION.ordinal());
			// Position
			out.writeFloat(mouseMapPos.getX());
			out.writeFloat(mouseMapPos.getY());
		}		
	}
	
	public void sendGunShotInfo(ArrayList<EntityIntersectionInfo> intersectionInfoList) throws IOException {

		// Get the mouse coordinates relative to the window
        Vector2f mousePos = new Vector2f(Mouse.getX(), Mouse.getY());
        // Convert the mouse coordinates so that they are relative to the map
        Vector2f mouseMapPos = clientWorld.getView().mousePosToWorldPos(mousePos); 
    	
		synchronized (out) {
		
			// Message type
			out.writeInt(NetworkConstants.C2S.GUN_SHOT_INFO.ordinal());
			
			// Aim vector
			out.writeFloat(mouseMapPos.getX());
			out.writeFloat(mouseMapPos.getY());
			
			// Number of intersections
			out.writeInt(intersectionInfoList == null ? 0 : intersectionInfoList.size());
			
			// Intersection info
			if (intersectionInfoList != null) {
			
				for (EntityIntersectionInfo info : intersectionInfoList) {
				
					// Intersected entity ID
					out.writeInt(info.getEntity().getID());
					
					// Number of intersection points
					out.writeInt(info.getIntersectionPoints().size());
					
					// Intersection points
					for (Vector2f p : info.getIntersectionPoints()) {
					
						out.writeFloat(p.getX());
						out.writeFloat(p.getY());
						
					}
					
				}
				
			}
			
		}	
		
	}

	
	public void shutdown() {
		
		this.error = true;
		
		try {
		
			this.socket.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
	}
	
}
