package shared.GUI.Menus;

import java.util.ArrayList;

import org.newdawn.slick.Color;

import shared.GameWindow;
import shared.GUI.Components.LabelledRectangle;
import shared.GUI.Components.RectangleComponent;

/**
 * Player list for lobby
 * @author Arun
 *
 */
public class PlayerList extends MenuTemplate {

	Color backgroundColour = MenuTemplate.getBackgroundColor();
	Color hoverColour = MenuTemplate.getHoverColour();

	private static  int listWidth = 370;
	private  int ySpacing = 15;

	private LabelledRectangle[] teamUOB = new LabelledRectangle[4];
	private LabelledRectangle[] teamAI = new LabelledRectangle[4];

	private LabelledRectangle[] teamSelected = new LabelledRectangle[4];
	private GameWindow gameWindow;
	private LobbyMenu lobby;


	public PlayerList(GameWindow gameWindow,LobbyMenu lobby){

		super(0, listWidth, 0);

		this.gameWindow = gameWindow;
		this.lobby = lobby;

		setList();

	}


	public void setList(){

		createMenuButtons(0, 0, 0,this.ySpacing,null);

	}

	@Override
	protected void createMenuButtons(int xOrigin, int yOrigin, int xSpacing,
			int ySpacing, String[] labels) {


		//UoB
		int _UOBX = (gameWindow.getWidth()/2) +15;
		int _UOBY = lobby.get_UOB().getOriginY() + lobby.get_UOB().getHeight() + ySpacing;
		addTeam(_UOBX, _UOBY, teamUOB);


		//AI
		int _AIX = _UOBX;
		int _AIY = lobby.get_AI().getOriginY() + lobby.get_AI().getHeight() + ySpacing ;
		addTeam(_AIX, _AIY, teamAI);

	}

	public void addTeam(int x, int y,RectangleComponent[] teamToAdd){


		Color background;
		int height = 30;


		for(int i = 0; i < teamToAdd.length; i++){

			if(i % 2 ==0){

				background = backgroundColour;

			}else{

				background = hoverColour;

			}

			teamToAdd[i] = new LabelledRectangle(gameWindow, x, y, listWidth, height,background,"",20);
			teamToAdd[i].setTransparency(0.6f);

			y += height;

		}


	}

	public boolean isOccupied(LabelledRectangle position){

		if(position.getLabel().getLabelText().equals("")){

			return false;

		}else{

			return true;
		}
	}


	public void addPlayer(LabelledRectangle[] teamToAdd){

		teamSelected = teamToAdd;
		
		for(LabelledRectangle r: teamToAdd){

			if(!isOccupied(r)){

				r.getLabel().setLabelText("Your player");
				r.getLabel().centreLabel(r);
				
				return;
			}

		}


	}


	public void render(){

		drawMenuButtons();
	}


	public void update(int delta){

	}


	@Override
	protected void drawMenuButtons() {

		for(int i = 0; i < teamUOB.length; i++){

			teamUOB[i].drawComponent();
			teamAI[i].drawComponent();
		}
	}


	public void setDefault(){
		
		for(int i = 0; i < teamAI.length; i++){
			
			teamAI[i].getLabel().setLabelText("");
			teamUOB[i].getLabel().setLabelText("");

			teamSelected = null;
		}
		
	}

	@Override
	protected void addMenuListeners() {	}



	public LabelledRectangle[] getTeamUOB() {
		return teamUOB;
	}
	
	public LabelledRectangle[] getTeamAI(){
		
		return teamAI;
	}
	
	public void setTeamSelected(LabelledRectangle[] teamSelected) {
		this.teamSelected = teamSelected;
	}





}
