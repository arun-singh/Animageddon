package shared;

import java.util.ArrayList;
import java.util.ListIterator;

import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import shared.GUI.Components.Border;
import shared.GUI.Components.Button;
import shared.GUI.Components.Label;
import shared.GUI.Menus.MenuTemplate;
import shared.GUI.UserInput.MouseListener;
import static org.lwjgl.opengl.GL11.*;

/**
 * For game information
 * @author Tom, Arun
 *
 */
public class GameInformation {

	/**
	 * Keeps a list of the on screen components
	 */
	private ArrayList<Label> screenComponents = new ArrayList<Label>();

	/**
	 * Team information
	 */
	private ArrayList<Team> teams;

	/**
	 * GameWindow
	 */
	private GameWindow game;

	public GameInformation(GameWindow game, ArrayList<Team> team){
		this.teams = team;
		this.game = game;
		createMenuButton();
		scoreLabel();
	}

	/**
	 * Loops thorugh component array list and draws components
	 */
	public void render(){		
		
		for(Team team : teams){
			team.getScoreLabel().drawComponent();
		}

		drawMenuButton();
	}

	public void update(int delta) {

		menuButton.click();

	}

	/**
	 * Creates score label
	 */
	public void scoreLabel(){
		// TODO: Set the position of the label
	}

	//////////////////Menu Button
	//TODO Can move to different class
	
	private Button menuButton;

	Color menuColour  = new Color(0.047f, 0.29f, 0.031f);

	public void createMenuButton(){

		int y = game.mouseToScreenY(40);

		menuButton = new Button(game, game.getWidth() - 100, y, 100, 40, "Menu",menuColour);
		
		menuButton.setTransparency(0.5f);
		
		addListener();


	}

	public void drawMenuButton(){

		menuButton.drawComponent();

	}

	public void addListener(){

		menuButton.addListener(new MouseListener() {

			@Override
			public void mouseEntered() {}

			@Override
			public void mouseClicked() {
				
				int x = Mouse.getX();
				int y = game.mouseToScreenY(Mouse.getY());

				if(menuButton.inside(x, y) && Mouse.isButtonDown(0)){

					menuButton.setColor(MenuTemplate.getHoverColour());
					game.setInGameMenu();

				}else{
					
					menuButton.setColor(menuColour);
					
				}
			}
		});

	}



}
