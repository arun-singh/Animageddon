package shared.GUI.MenuInput;

import shared.GUI.Components.Button;

/**
 * Template for menu input
 * @author Arun
 *
 */
public abstract class MenuInput {
	
	
	/**
	 * Called in update method of main game class
	 */
	public abstract void update();
	
	/**
	 * Gets the mouse coordinates and calls the action method
	 * @param buttons
	 */
	public abstract void getMouseInput(Button[] buttons);
	
	
}
