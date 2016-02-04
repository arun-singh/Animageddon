package shared.GUI.Menus;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

import shared.GameWindow;
import shared.Team;
import shared.TextureOperations;
import shared.GUI.Components.Border;
import shared.GUI.Components.Button;
import shared.GUI.Components.Label;
import shared.GUI.Components.LabelledRectangle;
import shared.GUI.Components.RectangleComponent;
import shared.GUI.Components.TexturedButton;
import shared.GUI.MenuInput.LobbyMenuInput;
import shared.GUI.UserInput.MouseListener;

/**
 * Lobby Menu
 * @author Arun
 *
 */
public class LobbyMenu extends MenuTemplate {

	private GameWindow gameWindow;


	private int windowWidth,windowHeight;
	private Label teamHeading,playerHeading,teamListHeading;
	private LobbyMenuInput lobbyInput;
	private PlayerSelectMenu playerSelect;

	private boolean hasChosenTeam = false;

	private Color backgroundColour = getBackgroundColor();
	private Color hoverColour = getHoverColour();

	//texture properties
	private String imageName = "InGameMenuBackground";
	private Texture bTexture = TextureOperations.getTexture(imageName, "JPG",".jpg");

	//player
	private TexturedButton selectedPlayer;
	//Attributes for chosen player
	private Label[] attributes = new Label[4];

	//team info
	private String[] teamNames = {"UoB", "BCU"};
	private String defaultTeam = "...";
	private String selectedTeam = defaultTeam;

	//buttons
	private Button chooseStatus,changePlayer;
	private Button _UOB,_AI;

	//non functional components
	private RectangleComponent backgroundTransparency;
	private RectangleComponent teamUnderline, screenDivider;

	//player list
	private PlayerList teamInformation;

	/**
	 * Constructs a lobby menu
	 * @param gameWindow GameWindow access
	 * @param playerSelect PlayerSelect to get selected player
	 * @param noOfButtons number of menu buttons
	 * @param width width of menu
	 * @param height height of menu
	 */
	public LobbyMenu(GameWindow gameWindow, PlayerSelectMenu playerSelect,int noOfButtons, int width, int height) {
		super(noOfButtons, width, height);

		//initialization
		this.gameWindow = gameWindow;
		windowWidth = gameWindow.getWidth();
		windowHeight = gameWindow.getHeight();

		//creation
		createView();

		//player select information
		this.playerSelect = playerSelect;
		//input
		lobbyInput = new LobbyMenuInput(this);
	}

	public void render(){

		drawBackground();
		drawLabelHeadings();
		selectedPlayer.drawComponent();
		drawAttributes();
		drawMenuButtons();
		drawNonFunctionComponents();

		teamInformation.render();

	}

	public void update(int delta){

		lobbyInput.update();
	}

	/**
	 * Calls all create methods
	 */
	public void createView(){

		createLabels();
		createBackground();
		createMenuButtons(0, 0, 0, 0, null);
		createAttributes();
		createSeperators();
		createPlayerList();

	}


	/**
	 * Called on confirmation
	 */
	public void confirmation(){


		//reset player if user navigates back to main menu
		playerSelect.setDefault();
		setDefault();

		//start game
		gameWindow.createGame();
		gameWindow.joinLocalGame();


	}

	/**
	 * Creates player list that displays teams
	 */
	public void createPlayerList(){

		teamInformation = new PlayerList(gameWindow, this);

	}

	/**
	 * Creates labels
	 */
	public void createLabels(){

		int teamHeadingX = windowWidth/(windowWidth/20); //800 / (80)
		int teamHeadingY = gameWindow.mouseToScreenY(windowHeight - (windowHeight/32));
		teamHeading = new Label(gameWindow, teamHeadingX, teamHeadingY, "Team " + selectedTeam, Color.black, 50);

		int playerHeadingX = teamHeadingX;
		int playerHeadingY = teamHeadingY + (windowWidth/8);
		playerHeading = new Label(gameWindow, playerHeadingX, playerHeadingY, "Your Player", Color.black, 35);

		int teamListX = (gameWindow.getWidth()/2) + 15;
		int teamListY = playerHeadingY;
		teamListHeading = new Label(gameWindow, teamListX, teamListY, "Choose Your Team", Color.black, 35);

	}

	@Override
	public void createMenuButtons(int xOrigin, int yOrigin, int xSpacing,
			int ySpacing, String[] labels) {

		int statusX = windowWidth - 120;
		int statusY = gameWindow.mouseToScreenY(70);

		chooseStatus = new Button(gameWindow, statusX, statusY, 100, 50, "Click if ready", backgroundColour);
		Border statusBorder = new Border(gameWindow, statusX, statusY, 100, 50, 2, 1.0f);
		statusBorder.setColor(Color.black);
		chooseStatus.setBorder(statusBorder);

		int changeX = playerHeading.getOriginX() + playerHeading.getWidth() + 50;
		int changeY = playerHeading.getOriginY() + 10;

		changePlayer = new Button(gameWindow, changeX, changeY, 100, 30, "Change Player",backgroundColour);
		changePlayer.setTransparency(0.8f);

		int selectedPlayerY = playerHeading.getOriginY() + (windowWidth/10);
		int selectedPlayerX = playerHeading.getOriginX();

		selectedPlayer = new TexturedButton(gameWindow, selectedPlayerX, selectedPlayerY, 170, 170, "", null);

		createTeamButtons();

		addMenuListeners();
	}

	/**
	 * Create team buttons
	 */
	public void createTeamButtons(){

		int buttonWidth = (windowWidth/8) + (windowWidth/16);
		int buttonHeight = (windowHeight/(20));
		
		int _UOBX = teamListHeading.getOriginX();
		int _UOBY = teamListHeading.getOriginY() + teamListHeading.getHeight() + 5;
		_UOB = new Button(gameWindow, _UOBX, _UOBY, buttonWidth, buttonHeight, "CLICK TO JOIN UOB", backgroundColour);
		_UOB.getLabel().setColor(Color.black);
		_UOB.setTransparency(0.4f);

		int _AIX = teamListHeading.getOriginX();
		int _AIY = _UOBY + 180;
		_AI = new Button(gameWindow, _AIX, _AIY, buttonWidth, buttonHeight, "CLICK TO JOIN BCU", backgroundColour);
		_AI.getLabel().setColor(Color.black);
		_AI.setTransparency(0.4f);

	}

	/**
	 * Create background
	 */
	public void createBackground(){

		backgroundTransparency = new RectangleComponent(gameWindow, 0, 0, windowWidth, windowHeight, Color.white);
		backgroundTransparency.setTransparency(0.5f);
	}

	/**
	 * Attributes for selected player
	 */
	public void createAttributes(){

		//set coordinates
		int attrX = selectedPlayer.getOriginX() + selectedPlayer.getWidth() + 50;
		int attrY = selectedPlayer.getOriginY();

		//for each attribute
		for(int i = 0; i < attributes.length; i++){

			attributes[i] = new Label(gameWindow, attrX, attrY, "", Color.black, 20);

			//repositioning
			attrY += (selectedPlayer.getHeight()/4);
		}

	}

	/**
	 * Create separators for layout
	 */
	public void createSeperators(){

		//team heading underline properties
		int underlineX = playerHeading.getOriginX();
		int underlineY = teamHeading.getOriginY() + teamHeading.getHeight() ;
		int underlineWidth = playerHeading.getWidth() + 150;
		teamUnderline = new RectangleComponent(gameWindow, underlineX, underlineY, underlineWidth, 3, backgroundColour);

		//screen divider properties
		int dividerX = gameWindow.getWidth()/2;
		int dividerY = playerHeading.getOriginY();
		screenDivider = new RectangleComponent(gameWindow, dividerX, dividerY, 3, 400,  backgroundColour);
	}

	@Override
	public void drawMenuButtons() {

		chooseStatus.drawComponent();
		chooseStatus.getBorder().drawComponent();
		changePlayer.drawComponent();


		_UOB.drawComponent();
		_AI.drawComponent();
	}

	@Override
	public void addMenuListeners() {

		navigationListeners();
		teamSelectListeners();

	}

	/**
	 * Set listeners for navigation
	 */
	public void navigationListeners(){

		chooseStatus.addListener(new MouseListener() {

			@Override
			public void mouseEntered() {

				onHover(chooseStatus);
			}

			@Override
			public void mouseClicked() {

				confirmation();


			}
		});

		changePlayer.addListener(new MouseListener() {

			@Override
			public void mouseEntered() {

				onHover(changePlayer);
			}

			@Override
			public void mouseClicked() {

				//default teams
				setDefault();
				gameWindow.setPlayerView();
				//swallow mouse input
				gameWindow.delay(100);

			}
		});

	}

	/**
	 * Set listeners for team select
	 */
	public void teamSelectListeners(){

		_UOB.addListener(new MouseListener() {

			@Override
			public void mouseEntered() {

				onHover(_UOB);
			}

			@Override
			public void mouseClicked() {

				hasChosenTeam = true;
				onTeamSelect(teamNames[0], teamInformation.getTeamUOB());

			}
		});

		_AI.addListener(new MouseListener() {

			@Override
			public void mouseEntered() {

				onHover(_AI);
			}

			@Override
			public void mouseClicked() {

				hasChosenTeam = true;
				onTeamSelect(teamNames[1], teamInformation.getTeamAI());

			}
		});

	}

	/**
	 * When team is selected
	 * @param teamChosen team chosen
	 * @param playerList PlayerList to be updated
	 */
	public void onTeamSelect(String teamChosen, LabelledRectangle[] playerList){

		selectedTeam = teamChosen;
		teamHeading.setLabelText("Team " + selectedTeam);
		teamInformation.addPlayer(playerList);

		//to not choose a team twice
		gameWindow.delay(200);

	}

	/**
	 * Draw background
	 */
	public void drawBackground(){

		TextureOperations.drawTexturedQuad(bTexture, windowWidth, windowHeight);
		backgroundTransparency.drawComponent();

	}

	/**
	 * Draw attributes
	 */
	public void drawAttributes(){

		for(Label attr:attributes){

			if(attr!=null){
				attr.drawComponent();
			}
		}
	}

	/**
	 * Draw label headings
	 */
	public void drawLabelHeadings(){

		teamHeading.drawComponent();
		playerHeading.drawComponent();
		teamListHeading.drawComponent();

	}

	/**
	 * Draw non functional components
	 */
	public void drawNonFunctionComponents(){

		//playerBorder.drawComponent();
		teamUnderline.drawComponent();
		screenDivider.drawComponent();
	}

	/**
	 * Gets selected player from player select menu
	 */
	public void getSelectedPlayer(){

		TexturedButton chosenPlayer = playerSelect.getClickedButton();

		//get texture
		selectedPlayer.setButtonTexture(chosenPlayer.getButtonTexture());
		//get attributes
		Label[] selectedAttr = playerSelect.getSelectedAttributes();

		//for each attribute
		for(int i = 0; i < attributes.length; i++){

			attributes[i].setLabelText(selectedAttr[i].getLabelText());

		}

	}
	
	//****Getters****
	

	public Button getChooseStatus() {
		return chooseStatus;
	}

	public Button getChangePlayer() {
		return changePlayer;
	}

	public GameWindow getGameWindow() {
		return gameWindow;
	}

	public Label getTeamListHeading(){

		return teamListHeading;
	}

	public String[] getTeamNames(){

		return teamNames;

	}
	public Label[] getAttributes(){

		return attributes;
	}

	public Button get_UOB() {
		return _UOB;
	}

	public Button get_AI() {
		return _AI;
	}
	
	
	public boolean hasChosenTeam() {
		return hasChosenTeam;
	}
	
	//****Setters****

	/**
	 * Sets default team after navigating
	 * from lobby
	 */
	public void setDefault(){

		selectedTeam = "...";
		teamHeading.setLabelText("Team " + selectedTeam);

		teamInformation.setDefault();

	}

	public void setHasChosenTeam(boolean hasChosenTeam) {

		this.hasChosenTeam = hasChosenTeam;
	}


}
