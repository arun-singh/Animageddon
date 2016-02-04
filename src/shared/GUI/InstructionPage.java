package shared.GUI;

import java.awt.MouseInfo;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

import shared.GameWindow;
import shared.TextureOperations;
import shared.GUI.Components.Button;
import shared.GUI.Components.Label;
import shared.GUI.Components.RectangleComponent;
import shared.GUI.Menus.MenuTemplate;
import shared.GUI.UserInput.MouseListener;

/**
 * Instruction page
 * @author Arun
 *
 */
public class InstructionPage extends View {

	//Access to gameWindow
	private GameWindow gameWindow;
	private int windowWidth,windowHeight;

	//background properties
	private String imageName = "InGameMenuBackground";
	private Texture bTexture = TextureOperations.getTexture(imageName, "JPG",".jpg");
	private RectangleComponent backgroundTransparency;

	//Labels
	private Label instructionHeading;
	private Label settingUpGameHeading,settingUpGameInfo;
	private Label controlHeading,controlInfo;
	private Label objectiveHeading,objectiveInfo;

	//Information Text
	private String settingUpGame = "For single player mode, select 'Create Game' from the main menu. This will allow you to play against and with\n"
			+ "AI players. For multiplayer mode, select 'Join Menu'; enter the host IP address to join the designated game.";

	private String controlString = "MOVEMENT  - WASD keys to move up,left,down,right respectively.\n"
			+  "AIM GUN   - Use your mouse/trackpad.\n"
			+  "SHOOT GUN - Left mouse click.\n"
			+  "SUICIDE   - 'K' keyboard button.";

	private String objectString =  "The objective of the game is to capture more flags than the opposing team. To do this you must first grab the\n"
			+ "flag and return to your base (which is marked as a green square). Once the flag has been captured it will return\n"
			+ "to the original flag position. If you are killed then you respawn back at your base and if you are shot whilst\n"
			+ "you are the flag bearer then the flag will be dropped in the position you were killed. If the opposing player\n"
			+ "has the flag then you must kill them to take it off them.";

	//Seperators
	private RectangleComponent headingUnderline;

	//Colours
	private Color mainColour = Color.black;
	private Color subHeadingColour =  MenuTemplate.backgroundColour;

	//Font sizes
	int subHeadingSize = 30;
	int instructSize = 14;

	//X coordinate for all components
	private int xPosition;

	//Main menu button
	private Button backButton;
	private int mouseX,mouseY;

	/**
	 * Constructs new instruction page
	 * @param gameWindow GameWindow access
	 */
	public InstructionPage(GameWindow gameWindow){

		this.gameWindow = gameWindow;
		windowWidth = gameWindow.getWidth();
		windowHeight = gameWindow.getHeight();

		//starting x position for other components to compare to
		xPosition = (windowWidth/40);

		createPage();
	}

	/**
	 * Called in main game loop
	 */
	public void render(){

		drawBackground();
		drawHeadings();
		drawInstructions();
		headingUnderline.drawComponent();
		backButton.drawComponent();

	}

	/**
	 * Called in main game loop
	 * @param delta time passed since last frame
	 */
	public void update(int delta){

		//back button
		input();

	}
	
	//****Trivial creation of components (each position based on coordinates of component above it)****

	/**
	 * Creation of page calls other create emthods
	 */
	public void createPage(){

		createLabel();
		createMenuButton();
		createBackground();
		createSeperators();
		settingUpGameInfo();
		controlInfo();
		objectiveInfo();
	}


	/**
	 * Creates background with transparenxy
	 */
	public void createBackground(){

		backgroundTransparency = new RectangleComponent(gameWindow, 0, 0, windowWidth, windowHeight, Color.white);
		backgroundTransparency.setTransparency(0.7f);
	}

	/**
	 * Create labels
	 */
	public void createLabel(){

		int instructionY = gameWindow.mouseToScreenY(windowHeight - 30);
		instructionHeading = new Label(gameWindow, xPosition, instructionY, "Instructions", mainColour, 50);

	}

	/**
	 * Create separators
	 */
	public void createSeperators(){

		int underlineY = instructionHeading.getOriginY() + instructionHeading.getHeight();
		int underlineWidth = instructionHeading.getWidth();
		headingUnderline = new RectangleComponent(gameWindow, xPosition, underlineY, underlineWidth, 3, mainColour);

	}

	/**
	 * Game information
	 */
	public void settingUpGameInfo(){

		int headingY = headingUnderline.getOriginY() + (windowWidth/80);
		settingUpGameHeading = new Label(gameWindow, xPosition, headingY, "Setting up a game", subHeadingColour, subHeadingSize);

		headingY += settingUpGameHeading.getHeight();
		settingUpGameInfo = new Label(gameWindow, xPosition, headingY, settingUpGame, mainColour, instructSize);

	}

	/**
	 * Control information
	 */
	public void controlInfo(){

		int headingY = settingUpGameHeading.getOriginY() + settingUpGameHeading.getHeight() + 55;
		controlHeading = new Label(gameWindow, xPosition, headingY, "Controls", subHeadingColour, subHeadingSize);

		headingY += controlHeading.getHeight();
		controlInfo = new Label(gameWindow, xPosition, headingY, controlString, mainColour, instructSize);

	}

	/**
	 * Objective information
	 */
	public void objectiveInfo(){

		int headingY = controlInfo.getOriginY() + controlInfo.getHeight() + 75;
		objectiveHeading = new Label(gameWindow, xPosition, headingY, "How to Play", subHeadingColour, subHeadingSize);

		headingY += objectiveHeading.getHeight();
		objectiveInfo = new Label(gameWindow, xPosition, headingY, objectString, mainColour, instructSize);
	}

	/**
	 * Create back button
	 */
	public void createMenuButton(){

		int buttonX = windowWidth - 120;
		int buttonY = instructionHeading.getOriginY();
		backButton = new Button(gameWindow, buttonX, buttonY, 100, 40, "Back", backgroundColour);

		addListener();

	}

	/**
	 * Listeners for back button
	 */
	public void addListener(){

		backButton.addListener(new MouseListener() {

			@Override
			public void mouseEntered() {

				backButton.setColor(hoverColour);
			}

			@Override
			public void mouseClicked() {
				if(!gameWindow.isInGame()){

					gameWindow.setMainView();

				}else{

					gameWindow.setInGameMenu();
				}
			}
		});

	}

	/**
	 * Register input for back button
	 */
	public void input(){

		mouseX = Mouse.getX();
		mouseY = gameWindow.mouseToScreenY(Mouse.getY());


		if(backButton.inside(mouseX, mouseY)){

			if(Mouse.isButtonDown(0)){

				backButton.click();

			}else{

				backButton.hover();
			}

		}else{
			//default
			backButton.setColor(backgroundColour);

		}


	}

	/**
	 * Render background
	 */
	public void drawBackground(){

		TextureOperations.drawTexturedQuad(bTexture, windowWidth, windowHeight);
		backgroundTransparency.drawComponent();

	}

	/**
	 * Render headings
	 */
	public void drawHeadings(){

		instructionHeading.drawComponent();
		settingUpGameHeading.drawComponent();
		controlHeading.drawComponent();
		objectiveHeading.drawComponent();

	}

	/**
	 * Render instructions
	 */
	public void drawInstructions(){

		settingUpGameInfo.drawComponent();
		controlInfo.drawComponent();
		objectiveInfo.drawComponent();
	}

}
