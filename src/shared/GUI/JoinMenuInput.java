package shared.GUI;

import org.lwjgl.input.Mouse;

import shared.GameWindow;
import shared.GUI.Components.Button;
import shared.GUI.Components.TexturedButton;
import shared.GUI.MenuInput.MenuInput;
import shared.GUI.Menus.MenuTemplate;

/**
 * Menu for joining a server
 * @author Thomas
 *
 */
public class JoinMenuInput extends MenuInput {

	private GameWindow gameWindow;
	private JoinMenu joinMenu;
	
	//menu buttons
	private TexturedButton[] buttons;

	private Button _confirmation, _back;
	int mouseX,mouseY;

	/**
	 * Registers input for join menu
	 * @param gameWindow GameWindow access
	 * @param joinMenu JoinMenu access
	 */
	public JoinMenuInput(GameWindow gameWindow, JoinMenu joinMenu){

		//this.game = game;
		this.joinMenu = joinMenu;
		this.gameWindow = gameWindow;

		//navigation buttons
		_confirmation = joinMenu.getConfirmation();
		_back = joinMenu.getBack();

	}

	@Override
	public void update() {
		//mouse input
		getMouseInput(buttons);

	}

	@Override
	public void getMouseInput(Button[] _buttons) {
		checkNavigation();
	}

	/**
	 * Checks input for navigation buttons
	 */
	public void checkNavigation(){

		mouseX = Mouse.getX();
		mouseY = gameWindow.mouseToScreenY(Mouse.getY());

		//confirmation button
		if(_confirmation.inside(mouseX, mouseY)){

			if(Mouse.isButtonDown(0)){
				_confirmation.click();
				//if not clicked	
			}else{

				_confirmation.hover();

			}
		}else{
			//mouse exited
			_confirmation.setColor(MenuTemplate.getBackgroundColor());
		}

		//back button
		if(_back.inside(mouseX, mouseY)){

			if(Mouse.isButtonDown(0)){
				_back.click();
				//if not clicked	
			}else{

				_back.hover();
			}
		}else{
			//mouse exited
			_back.setColor(MenuTemplate.getBackgroundColor());
		}

	}

}



