package shared.GUI.MenuInput;

import org.lwjgl.input.Mouse;

import shared.GameWindow;
import shared.GUI.Components.Button;
import shared.GUI.Menus.LobbyMenu;
import shared.GUI.Menus.MenuTemplate;

/**
 * Input for lobby menu
 * @author Arun
 *
 */
public class LobbyMenuInput extends MenuInput {

	//lobby access
	private LobbyMenu lobbyMenu;
	//buttons
	private Button confirmedStatus,changePlayer;
	private Button _UOBTeam,_AITeam;
	//game window
	private GameWindow gameWindow;
	private int mouseX,mouseY;

	public LobbyMenuInput(LobbyMenu lobbyMenu){

		//intialize variables
		this.lobbyMenu = lobbyMenu;
		this.gameWindow = lobbyMenu.getGameWindow();
		confirmedStatus = lobbyMenu.getChooseStatus();
		changePlayer = lobbyMenu.getChangePlayer();
		_UOBTeam = lobbyMenu.get_UOB();
		_AITeam = lobbyMenu.get_AI();
	}

	@Override
	public void update() {

		getMouseInput(null);

	}

	@Override
	public void getMouseInput(Button[] buttons) {

		mouseX = Mouse.getX();
		mouseY = gameWindow.mouseToScreenY(Mouse.getY());

		checkGameConfirmation(mouseX, mouseY);
		checkTeamSelect(mouseX, mouseY);
		changePlayer(mouseX, mouseY);


	}

	/**
	 * Check if confirmation is being accessed
	 * @param mouseX mouse x coordinate
	 * @param mouseY mouse y coordinate
	 */
	public void checkGameConfirmation(int mouseX, int mouseY){

		if(confirmedStatus.inside(mouseX, mouseY)){

			if(Mouse.isButtonDown(0)){

				confirmedStatus.click();
			}else{

				confirmedStatus.hover();
			}

		}else{

			MenuTemplate.defaultHover(confirmedStatus);
		}

	}

	/**
	 * Check if user wants to change player
	 * @param mouseX mouse x coordinate
	 * @param mouseY mouse y coordinate
	 */
	public void changePlayer(int mouseX, int mouseY){

		if(changePlayer.inside(mouseX, mouseY)){

			if(Mouse.isButtonDown(0)){

				changePlayer.click();

			}else{

				changePlayer.hover();
			}
		}else{

			MenuTemplate.defaultHover(changePlayer);
			
		}

	}

	/**
	 * Check if team is being selected
	 * @param mouseX mouse x coordinate
	 * @param mouseY mouse y coordinate
	 */
	public void checkTeamSelect(int mouseX,int mouseY){

		if(!lobbyMenu.hasChosenTeam()){

			if(_UOBTeam.inside(mouseX, mouseY)){

				if(Mouse.isButtonDown(0)){
					_UOBTeam.click();

				}else{

					_UOBTeam.hover();

				}
			}else{
				
				MenuTemplate.defaultHover(_UOBTeam);

			}

			if(_AITeam.inside(mouseX, mouseY)){

				if(Mouse.isButtonDown(0)){
					_AITeam.click();

				}else{

					_AITeam.hover();
					
				}
			}else{
				
				MenuTemplate.defaultHover(_AITeam);

			}

		}
	}

}
