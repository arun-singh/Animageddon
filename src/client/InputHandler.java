package client;
import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

import shared.entities.Player;
import shared.net.NetworkConstants;

/**
 * Handles local player input on the client.
 * @author Chris, Richard
 *
 */
public class InputHandler {
	
	
	//****Class variables****
	
	
	private ClientWorld clientWorld;
	private Player player;
	private long lastPositionSendTime = 0;
	
	
	//****Constructor****
	
	
	public InputHandler(ClientWorld clientWorld, Player player) {	
		this.clientWorld = clientWorld;
		this.player = player;
	}
	
	
	//****Class methods****
	
	
	/**
	 * Method to retrieve and process user input
	 * @throws IOException network error
	 */
	public void update() throws IOException {
		handleKeyboardInput();
		handleMouseInput();	
	}
	
	// TODO clear initial events from main menu.
	/**
	 * Retrieves and handles input from the keyboard.
	 */
	private void handleKeyboardInput() {
		//Cycle through received keyboard events
		while (Keyboard.next()) {
			
			// Get the key pressed
			int eventKey = Keyboard.getEventKey();
	
			// Get if key is being pressed or released
			boolean pressed = Keyboard.getEventKeyState();

			// Store key info
			this.player.updateKeyboardInput(eventKey, pressed);
			
			try {
				// Send to server
				this.clientWorld.sendKeyboardInput(eventKey, pressed);
			} catch (IOException e) {
				System.out.println("Exception sending input");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Retrieves and handles the mouse input
	 * @throws IOException network error
	 */
	private void handleMouseInput() throws IOException {
		if (!clientWorld.isLoaded())
			return;

		// Ignore mouse input if player is dead
		if (clientWorld.getLocalPlayer().getIsDead())
			return;
		
      	// Get the mouse coordinates relative to the window
        Vector2f mousePos = new Vector2f(Mouse.getX(), Mouse.getY());

        // Convert the mouse coordinates so that they are relative to the map
        Vector2f mouseMapPos = clientWorld.getView().mousePosToWorldPos(mousePos); 
        
        // Send position to server if interval has passed
        if (clientWorld.getTime() - this.lastPositionSendTime > NetworkConstants.MOUSE_POSITION_SEND_INTERVAL_MS) {
        	this.clientWorld.getNetworkClient().sendMousePosition(mouseMapPos);
        	
        	this.lastPositionSendTime = clientWorld.getTime();
        }
        
        // Local player entity
    	Player player = clientWorld.getLocalPlayer();

    	// Handle mouse presses
		while (Mouse.next()){
			
			int eventButton = Mouse.getEventButton();
			boolean pressed = Mouse.getEventButtonState();
			        	
            if (eventButton == 0) {
    	        if (pressed) { //Left button pressed 
    	        	player.shoot();
    	        } else {
    	        	//player.stopShooting();
    	        }
    	    } else if (eventButton == -1) {
    	    	//if (player.isShooting()) {
    	    	//	player.setMousePos(mouseMapPos);
    	    	//}
    	    }
            
            // Send to server
            //if (this.clientWorld.localNetworkPlayer != null)
            //	this.clientWorld.localNetworkPlayer.inputHandler.handleMouseInput(eventButton, pressed, mousePos, mouseMapPos);
		}
	}
	
	
	
}
