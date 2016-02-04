package shared.GUI.MenuInput;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import shared.GameWindow;
import shared.InvalidMapException;
import shared.GUI.Components.Button;
import shared.GUI.Menus.MainMenu;
import shared.GUI.Menus.MenuTemplate;
import shared.GUI.Menus.PlayerSelectMenu;


/**
 * Input for main menu
 * @author Arun
 *
 */
public class MainMenuInput extends MenuInput {

	//game window needed to start game
	private GameWindow gameWindow;
	//to manipulate main menu
	private MainMenu menu;
	//menu buttons
	private Button[] buttons;
	private PlayerSelectMenu playerSelect;

	/**
	 * Contains methods to control main menu
	 * @param gameWindow game window to begin game
	 * @param menu menu to be manipulated 
	 */
	public MainMenuInput(GameWindow gameWindow, MainMenu menu) {
		//initialize global variables
		this.gameWindow = gameWindow;
		this.menu = menu;
		//get menu buttons
		buttons = menu.getButtons();
	}


	@Override
	public void update() {
		//mouse input
		getMouseInput(buttons);

	}


	@Override
	public void getMouseInput(Button[] _buttons) {
		
		for(int i = 0; i < buttons.length; i++){
			
			//get mouse coordinates
			int mouseX = Mouse.getX();
			int mouseY = gameWindow.mouseToScreenY(Mouse.getY());
			
			if(buttons[i].inside(mouseX, mouseY)){
				//if mouse id clicked
				
				if(Mouse.isButtonDown(0)){
					//pass to action
					buttons[i].click();
					//if not clicked
				}else{
					//check for hover
					buttons[i].hover();
				}
				
				
			}else{
				
				MenuTemplate.defaultHover(buttons[i]);

			}
			
		}
	}
}
