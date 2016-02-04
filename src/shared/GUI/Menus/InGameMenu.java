package shared.GUI.Menus;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

import shared.GameWindow;
import shared.TextureOperations;
import shared.GUI.Components.Button;
import shared.GUI.MenuInput.InGameMenuInput;
import shared.GUI.UserInput.MouseListener;

/**
 * In game menu
 * @author Arun
 *
 */
public class InGameMenu extends MenuTemplate {

	//input
	private InGameMenuInput menuInput;
	
	private GameWindow gameWindow;

	//Button properties
	private Button[] buttons;
	private String[] labels = {"Exit","Instructions", "Main Menu","Resume Game"};

	/**
	 * Constructs the in game menu
	 * @param gameWindow Game Window access
	 * @param noOfButtons number of buttons required
	 * @param width width of menu
	 * @param height height if menu
	 */
	public InGameMenu(GameWindow gameWindow, int noOfButtons, int width, int height) {
		
		super(noOfButtons, width, height);
		//game window
		this.gameWindow = gameWindow;
		windowHeight = gameWindow.getHeight();
		windowWidth = gameWindow.getWidth();
		//sets buttons
		buttons = new Button[noOfButtons];
		//creation
		createMenu();
		//register input
		menuInput = new InGameMenuInput(gameWindow, this);
	}
	

	public void render(){

		//menuBackground();
		drawMenuButtons();

	}

	public void update(int delta){

		menuInput.update();

	}

	@Override
	public void createMenuButtons(int xOrigin, int yOrigin, int xSpacing,int ySpacing, String[] labels) {

		//calculate button height
		int buttonHeight = height/noOfButtons;
		//local variables
		int x =  xOrigin;
		int y = gameWindow.mouseToScreenY(yOrigin) - buttonHeight;
		//for each button
		for(int i = 0; i < noOfButtons; i++){
			//calculate spacing
			if (i != 0) {
				x += xSpacing*i;
				y -= ySpacing;
			}
			//create button
			buttons[i] = new Button(gameWindow, x, y, width, buttonHeight, labels[i],backgroundColour);
			buttons[i].getLabel().customFont(14f);
			//set button colour
			buttons[i].setTransparency(0.5f);

			//modify variable that sets each button height
			y -= buttonHeight;

		}

		addMenuListeners();
	}
	
	/**
	 * Calls method to create buttons
	 */
	public void createMenu(){
		
		int xOrigin = (windowWidth/4) + (windowWidth/16);
		int yOrigin = (windowHeight/6) + (windowHeight/24);
		createMenuButtons(xOrigin, yOrigin, 0, 10, labels);

	}

	@Override
	public void drawMenuButtons() {

		for(Button b: buttons){

			b.drawComponent();

		}

	}

	@Override
	public void addMenuListeners() {

		//resume game
		buttons[3].addListener(new MouseListener() {

			@Override
			public void mouseEntered() {

				onHover(buttons[3]);


			}

			@Override
			public void mouseClicked() {

				gameWindow.resumeGame();
			}
		});

		//MainMenu
		buttons[2].addListener(new MouseListener() {

			@Override
			public void mouseEntered() {

				onHover(buttons[2]);

			}

			@Override
			public void mouseClicked() {

				gameWindow.setMainView();
				gameWindow.delay(200);

			}
		});

		//options
		buttons[1].addListener(new MouseListener() {

			@Override
			public void mouseEntered() {

				onHover(buttons[1]);


			}

			@Override
			public void mouseClicked() {

				gameWindow.setInstructionPage();
				
			}
		});

		//exit
		buttons[0].addListener(new MouseListener() {

			@Override
			public void mouseEntered() {

				onHover(buttons[0]);

			}

			@Override
			public void mouseClicked() {

				System.exit(0);

			}
		});


	}


	/**
	 * Gets the array of buttons
	 * @return menu buttons
	 */
	public Button[] getButtons() {
		return buttons;
	}
	
}
