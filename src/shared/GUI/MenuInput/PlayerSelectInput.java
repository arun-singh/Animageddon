package shared.GUI.MenuInput;

import java.util.ArrayList;

import javax.security.auth.callback.ConfirmationCallback;
import javax.swing.text.html.HTMLDocument.Iterator;

import org.lwjgl.input.Mouse;

import shared.GameWindow;
import shared.GUI.Components.Button;
import shared.GUI.Components.TexturedButton;
import shared.GUI.Menus.MenuTemplate;
import shared.GUI.Menus.PlayerSelectMenu;

/**
 * Input for player select
 * @author Arun
 *
 */
public class PlayerSelectInput extends MenuInput {

	private GameWindow gameWindow;
	private PlayerSelectMenu playerMenu;
	//menu buttons
	private TexturedButton[] buttons;
	private TexturedButton playerPreview;
	private String selected;

	private Button _confirmation, _back;
	int mouseX,mouseY;
	private TexturedButton hover;
	ArrayList<TexturedButton> ab;

	public PlayerSelectInput(GameWindow gameWindow, PlayerSelectMenu playerMenu){

		//this.game = game;
		this.playerMenu = playerMenu;
		this.gameWindow = gameWindow;

		buttons = playerMenu.getButtons();
		playerPreview = playerMenu.getPlayerPreview();
		_confirmation = playerMenu.getConfirmation();
		_back = playerMenu.getBack();

	}

	@Override
	public void update() {
		//mouse input
		getMouseInput(buttons);

	}


	@Override
	public void getMouseInput(Button[] _buttons) {

		checkNavigation();
		
		//for loop non functional for player select
		mouseX = Mouse.getX();
		mouseY = gameWindow.mouseToScreenY(Mouse.getY());

		//for each button check for click and hover
		if(buttons[0].inside(mouseX, mouseY)){

			if(Mouse.isButtonDown(0)){

				buttons[0].click();
				//if not clicked	

			}else{

				buttons[0].hover();

			}

		}else if(buttons[1].inside(mouseX, mouseY)) {

			if(Mouse.isButtonDown(0)){

				buttons[1].click();
				//if not clicked	

			}else{

				buttons[1].hover();

			}

		}else if(buttons[2].inside(mouseX, mouseY)) {

			if(Mouse.isButtonDown(0)){

				buttons[2].click();
				//if not clicked	

			}else{

				buttons[2].hover();

			}

		}else if(buttons[3].inside(mouseX, mouseY)) {

			if(Mouse.isButtonDown(0)){

				buttons[3].click();
				//if not clicked	

			}else{

				buttons[3].hover();

			}

		}
		else{

			playerMenu.setPreview();
		}

	}


	/**
	 * Check navigation buttons for input
	 */
	public void checkNavigation(){

		mouseX = Mouse.getX();
		mouseY = gameWindow.mouseToScreenY(Mouse.getY());

		if(_confirmation.inside(mouseX, mouseY)){

			if(Mouse.isButtonDown(0)){
				_confirmation.click();
				//if not clicked	
			}else{

				_confirmation.hover();

			}
		}else{

			_confirmation.setColor(MenuTemplate.getBackgroundColor());
		}

		if(_back.inside(mouseX, mouseY)){

			if(Mouse.isButtonDown(0)){
				_back.click();
				//if not clicked	
			}else{

				_back.hover();

			}
		}else{

			_back.setColor(MenuTemplate.getBackgroundColor());
		}

	}





}



