package shared.GUI.Menus;

import org.newdawn.slick.opengl.Texture;

import shared.GameWindow;
import shared.InvalidMapException;
import shared.TextureOperations;
import shared.GUI.Components.Button;
import shared.GUI.MenuInput.MainMenuInput;
import shared.GUI.UserInput.MouseListener;

/**
 * Main menu
 * @author Arun
 *
 */
public class MainMenu extends MenuTemplate {
	
	
	//****Class variables****
	

	//needed for update
	private GameWindow gameWindow;
	//handles menu input 
	private MainMenuInput mainMenuInputHandler;
	//Health names
	private String[] buttonNames = {"Exit", "Instructions", "Join Game", "Create Game"};
	//To hold buttons
	private Button[] buttons;

	//texture properties
	private String imageName = "menubackground";
	private Texture bTexture = TextureOperations.getTexture(imageName, "JPG",".jpg");

	
	//****Constructors****
	

	/**
	 * Creates a main menu 
	 * @param gameWindow game window for menu input		
	 * @param noOfButtons number of buttons
	 * @param menuWidth width of menu
	 * @param menuHeight height of menu
	 */
	public MainMenu(GameWindow gameWindow, int noOfButtons, int menuWidth, int menuHeight) {
	
		//invokes super constructor
		super(noOfButtons, menuWidth, menuHeight);
		
		//initialize global variables
		this.gameWindow = gameWindow;
		windowHeight = gameWindow.getHeight();
		windowWidth = gameWindow.getWidth();
		
		//sets buttons
		buttons = new Button[noOfButtons];
		
		//creation
		createMenu();

		this.mainMenuInputHandler = new MainMenuInput(gameWindow, this);  //for input
		
	}
	
	public void render(){

		menuBackground();
		//call draw method
		drawMenuButtons();

	}

	public void update(int delta) {
		
		//updates input
		mainMenuInputHandler.update();
		
	}

	/**
	 * Calls method to create buttons
	 */
	private void createMenu(){
		
		int xOrigin = (windowWidth/4) + (windowWidth/16);
		int yOrigin = (windowHeight/6) + (windowHeight/24);
		createMenuButtons(xOrigin, yOrigin, 0, 10, buttonNames);
		
	}

	/**
	 * Formats and adds the buttons on the menu
	 */
	@Override
	protected void createMenuButtons(int xOrigin, int yOrigin, int xSpacing, int ySpacing, String[] labels){
		
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
			
			//modify variable that sets each button height
			y -= buttonHeight;

		}

		addMenuListeners();

	}

	/**
	 * Adds the mouse listeners for the buttons
	 */
	@Override
	protected void addMenuListeners(){
		
		addCreateGameListener();
		
		addJoinGameListener();
		
		addInstructionsListener();
		
		addExitListener();

	}

	/**
	 * Adds the mouse listener for the create game button
	 */
	private void addCreateGameListener() {

		buttons[3].addListener(new MouseListener() {

			@Override
			public void mouseEntered() {
						
				onHover(buttons[3]);						
			}

			@Override
			public void mouseClicked() {
				
				//player select
				gameWindow.setPlayerView();
				gameWindow.delay(100);
			
			}
					
		});
		
	}
	
	/**
	 * Adds the mouse listener for the join game button
	 */
	private void addJoinGameListener() {
		
		buttons[2].addListener(new MouseListener() {

			@Override
			public void mouseEntered() {

				onHover(buttons[2]);				
			}

			@Override
			public void mouseClicked() {
				
				//join game
				gameWindow.setJoinGameMenu();

			}
		});
		
	}
	
	/**
	 * Adds the mouse listener for the instructions button
	 */
	private void addInstructionsListener() {

		buttons[1].addListener(new MouseListener() {

			@Override
			public void mouseEntered() {
				
				onHover(buttons[1]);				
			}

			@Override
			public void mouseClicked() {
				
				// TODO Auto-generated method stub
				gameWindow.setInstructionPage();
			}
			
		});
		
	}
	
	/**
	 * Adds the mouse listener for the exit button
	 */
	private void addExitListener() {
		
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
	 * Draws the buttons on the menu
	 */
	@Override
	protected void drawMenuButtons() {
	
		//for each button
		for(int i = 0; i < noOfButtons; i++){
		
			//draw each button
			buttons[i].drawComponent();

		}	
		
	}
	
	/**
	 * Creates the background image
	 */
	private void menuBackground(){

		
		TextureOperations.drawTexturedQuad(bTexture, 1500, 1200);

	}	

	
	//****Getters****
	
	
	/**
	 * Gets the array of buttons
	 * @return menu buttons
	 */
	public Button[] getButtons() {
		
		return buttons;
		
	}

}


