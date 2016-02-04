package server;

import org.lwjgl.util.vector.Vector2f;

import shared.entities.Player;

/**
 * Handles player input on the server.
 * @author Chris, Richard
 *
 */
public class InputHandler {

	private Player player;
		
	public InputHandler(Player player) {
		this.player = player;
	}
	
	/**
	 * Called on key input to update which keys are pressed/released and process the new state.
	 */
	public void handleKeyboardInput(int eventKey, boolean pressed) {

		player.updateKeyboardInput(eventKey, pressed);
		
	}

	/**
	 * Handles the mouse input.
	 */
	public void handleMouseInput(int eventButton, boolean pressed, Vector2f mousePos, Vector2f mouseMapPos) {
		
	    /*if (eventButton == 0) {
	        if (pressed) { //Left button pressed 
	            //Start shooting
	            player.startShooting(mouseMapPos);
	        } else {
	        	player.stopShooting();
	        }
	    } else if (eventButton == -1) {
	    	if (player.isShooting()) {
	    		player.setMousePos(mouseMapPos);
	    	}
	    }*/
	}
	
	
	
}
