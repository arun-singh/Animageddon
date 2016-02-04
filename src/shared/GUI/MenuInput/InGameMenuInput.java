package shared.GUI.MenuInput;

import org.lwjgl.input.Mouse;

import shared.GameWindow;
import shared.GUI.Components.Button;
import shared.GUI.Menus.InGameMenu;
import shared.GUI.Menus.MenuTemplate;

/**
 * Register input for in game menu
 * @author Arun
 *
 */
public class InGameMenuInput extends MenuInput{
	
	private GameWindow gameWindow;
	private InGameMenu inGameMenu;
	//menu buttons
	private Button[] buttons;
	private int mouseX,mouseY;
	
	/**
	 * Registers input for in-game menu
	 * @param gameWindow GameWindow access
	 * @param inGameMenu InGameMenu access
	 */
	public InGameMenuInput(GameWindow gameWindow, InGameMenu inGameMenu){
		
		this.gameWindow = gameWindow;
		this.inGameMenu = inGameMenu;
		//get buttons
		buttons = inGameMenu.getButtons();
		
	}

	@Override
	public void update() {
		getMouseInput(buttons);
	}

	@Override
	public void getMouseInput(Button[] buttons) {

		for(int i = 0; i < buttons.length; i++){
			
			//get mouse coordinates
			mouseX = Mouse.getX();
			mouseY = gameWindow.mouseToScreenY(Mouse.getY());
			
			if(buttons[i].inside(mouseX, mouseY)){
				//if mouse id clicked
				
				if(Mouse.isButtonDown(0)){
					
					buttons[i].click();
					//if not clicked
				}else{
					//check for hover
					buttons[i].hover();
				}
				
			}else{
				
				//mouse exited
				MenuTemplate.defaultHover(buttons[i]);

			}	
		}
	}
}
