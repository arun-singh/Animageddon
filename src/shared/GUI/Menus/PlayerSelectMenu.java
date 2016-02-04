package shared.GUI.Menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

import shared.GameWindow;
import shared.TextureOperations;
import shared.GUI.Components.Border;
import shared.GUI.Components.Button;
import shared.GUI.Components.Label;
import shared.GUI.Components.RectangleComponent;
import shared.GUI.Components.TexturedButton;
import shared.GUI.MenuInput.PlayerSelectInput;
import shared.GUI.UserInput.MouseListener;
import shared.entities.animals.Elephant;
import shared.entities.animals.Goat;
import shared.entities.animals.Monkey;
import shared.entities.animals.Zebra;
import client.InputHandler;

/**
 * Menu for player select
 * @author Arun
 *
 */
public class PlayerSelectMenu extends MenuTemplate{

	private TexturedButton[] buttons;

	private boolean hoveredButton = false;
	private TexturedButton clickedButton = null;

	//labels only needed for textured button
	private String[] buttonLabels = {"goat", "zebra", "elephant", "moneky"};
	//images
	private String[] imageNames = {"goat","zebra","elephant", "monkey"};
	//borders
	private Border[] imageBorders = new Border[4];
	private Border previewBorder;
	//textures
	private Texture defaultPreview = TextureOperations.getTexture("chooseplayer","PNG",".png");
	private Texture background = TextureOperations.getTexture("playerselectbackground", "JPG",".jpg");
	private TexturedButton playerPreview;
	//info buttons
	private Button confirmation,back;
	private RectangleComponent seperator;
	private PlayerSelectInput input;
	private int dimension;
	//game
	private GameWindow gameWindow;
	//info labels
	private Label heading;
	//flags - needed to tell player to select a character
	private boolean selectNotification = false;

	//transparency for background
	RectangleComponent backgroundTransparency;

	//test labels
	private Label[] selectedAttributes;

	private Label[] defaultLabel = new Label[4];
	private Label[] goatLabel = new Label[4];
	private Label[] zebraLabel = new Label[4];
	private Label[] elephantLabel = new Label[4];
	private Label[] monkeyLabel = new Label[4];

	//array list for labels
	private ArrayList<Label[]> testLabels = new ArrayList<Label[]>();

	//players
	Goat g = new Goat();
	Zebra z = new Zebra();
	Elephant e = new Elephant();
	Monkey m = new Monkey();

	//test attributes
	private final String[] headings = {"Health", "Speed","Range","Damage"};
	private String[] defaultAttributes = {headings[0] + " ?", headings[1] + " ?",headings[2] + " ?",headings[3] + " ?"};
	//animal attributes
	private String[] goatAttributes = {headings[0] + " " + g.getMaxHealth(), headings[1] + " " + g.getSpeed(), headings[2] + " " + g.getWeaponRange(),headings[3] + " " + g.getWeaponDamage()};
	private String[] zebraAttributes = {headings[0] + " " + z.getMaxHealth(), headings[1] + " " + z.getSpeed(), headings[2] + " " + z.getWeaponRange(),headings[3] + " " + z.getWeaponDamage()};
	private String[] elephantAttributes = {headings[0] + " " + e.getMaxHealth(), headings[1] + " " + e.getSpeed(), headings[2] + " " + e.getWeaponRange(),headings[3] + " " + e.getWeaponDamage()};
	private String[] monkeyAttributes = {headings[0] + " " + m.getMaxHealth(), headings[1] +  " " + m.getSpeed(), headings[2] +  " " + m.getWeaponRange(),headings[3] + " " + m.getWeaponDamage()};

	/**
	 * Constructs player select menu
	 * @param gameWindow GameWindow access
	 * @param noOfButtons number of buttons required
	 * @param width width of menu
	 * @param height height of menu
	 */
	public PlayerSelectMenu(GameWindow gameWindow, int noOfButtons, int width, int height) {

		super(noOfButtons, width, height);

		this.gameWindow =gameWindow;
		windowHeight = gameWindow.getHeight();
		windowWidth = gameWindow.getWidth();
		//set number of buttons
		buttons = new TexturedButton[noOfButtons];
		dimension = windowWidth/8;
		//create menu
		createMenu();
		//menu input
		input = new PlayerSelectInput(gameWindow, this);
		selectedAttributes = defaultLabel;

	}

	public void render(){
		//background
		menuBackground();
		//borders
		drawBorders();
		//call draw method
		drawMenuButtons();
		//draw preview button
		drawInfoButtons();
		//draw labels
		drawInfoLabels();
		//draw attributes
		drawPlayerAttributes();
	}

	public void update(int delta) {
		//updates input
		input.update();
	}

	/**
	 * Calls method to create buttons
	 */
	public void createMenu(){

		//attributes
		createPlayerAttributes(Color.black, 16);
		createInfoButtons();
		//create buttons
		createMenuButtons(155, gameWindow.mouseToScreenY(130), 20, 0, buttonLabels);
		//labels
		createInfoLabels();
		createBackgroundTransparency();

	}

	/**
	 * Background transparency settings
	 */
	public void createBackgroundTransparency(){
		//based on rectangle covering whole screen
		backgroundTransparency = new RectangleComponent(gameWindow, 0, 0, gameWindow.getWidth(), gameWindow.getHeight(), Color.white);
		backgroundTransparency.setTransparency(0.3f);

	}

	/**
	 * Creates information buttons
	 */
	public void createInfoButtons(){

		////////////////player preview

		int previewX = 212;
		int previewY = 320;

		playerPreview = new TexturedButton(gameWindow,previewX, previewY, 150, 150, "Select character", null);
		previewBorder = new Border(gameWindow, previewX, previewY, playerPreview.getWidth(), playerPreview.getHeight(), 4,5.0f);
		previewBorder.setColor(backgroundColour);

		//////////////confirmation button

		int confirmationY = gameWindow.mouseToScreenY(70);
		int confirmationX = 375;

		confirmation = new Button(gameWindow,confirmationX, confirmationY, 200, 50, "Continue",backgroundColour);
		confirmation.addListener(new MouseListener() {

			@Override
			public void mouseEntered() {
				// TODO Auto-generated method stub
				confirmation.setColor(hoverColour);
			}

			@Override
			public void mouseClicked() {

				if(clickedButton != null){


					gameWindow.enterLobbyMenu();

				}else{

					selectNotification = true;
				}
			}
		});

		///////////////exit

		int backX = confirmation.getOriginX() + confirmation.getWidth() +25;
		back = new Button(gameWindow, backX, confirmationY, 100, 50, "Back",backgroundColour);

		back.addListener(new MouseListener() {

			@Override
			public void mouseEntered() {

				back.setColor(hoverColour);
			}

			@Override
			public void mouseClicked() {
				setDefault();
				gameWindow.setMainView();
			}
		});


		/////////////////////Separator

		seperator = new RectangleComponent(gameWindow, 100, 250, 550, 3, backgroundColour);
	}

	/**
	 * Draws information buttons
	 */
	public void drawInfoButtons(){

		playerPreview.drawComponent();
		confirmation.drawComponent();
		back.drawComponent();
		seperator.drawComponent();

	}

	/**
	 * Draws border on hover and confirmation
	 */
	public void drawBorders(){

		if(clickedButton != null){
			//draw selected border
			clickedButton.getBorder().drawComponent();

		}

		//if no player selected notify user
		if(selectNotification && clickedButton == null){
			previewBorder.drawComponent();
		}

	}

	/**
	 * Temporary player attributes
	 * @param fontColour colour of attribute font
	 * @param fontSize size of attribute font
	 */
	public void createPlayerAttributes(Color fontColour, int fontSize){

		int x = windowWidth/2;
		int y = gameWindow.mouseToScreenY(275);

		for(int i = 0; i < goatAttributes.length; i++){

			defaultLabel[i] = new Label(gameWindow, x, y, defaultAttributes[i], fontColour, fontSize);
			goatLabel[i] = new Label(gameWindow, x, y, goatAttributes[i], fontColour, fontSize);
			zebraLabel[i] = new Label(gameWindow, x, y, zebraAttributes[i], fontColour, fontSize);
			elephantLabel[i] = new Label(gameWindow, x, y, elephantAttributes[i], fontColour, fontSize);
			monkeyLabel[i] = new Label(gameWindow, x, y, monkeyAttributes[i], fontColour, fontSize);

			y += (windowWidth/20);

		}

		//for iteration purposes
		testLabels.add(goatLabel);
		testLabels.add(zebraLabel);
		testLabels.add(elephantLabel);
		testLabels.add(monkeyLabel);
	}

	/**
	 * Draw test attributes
	 */
	public void drawPlayerAttributes(){

		for(Label labels: selectedAttributes){

			labels.drawComponent();
		}
	}

	@Override
	public void createMenuButtons(int xOrigin, int yOrigin, int xSpacing,
			int ySpacing, String[] labels){

		int x =  xOrigin;
		int y = gameWindow.mouseToScreenY(yOrigin);

		for(int i = 0; i < noOfButtons; i++){

			//calculate spacing
			if (i != 0) {
				x += xSpacing;
				y -= ySpacing;
			}

			//texture buttons
			buttons[i] = new TexturedButton(gameWindow, x, y, dimension, dimension, labels[i], imageNames[i]);
			//borders
			imageBorders[i] = new Border(gameWindow, x, y, dimension, dimension, 6,1.0f);
			imageBorders[i].setColor(backgroundColour);
			//set border
			buttons[i].setBorder(imageBorders[i]);

			//spacing
			x += buttons[i].getWidth();

		}

		addMenuListeners();

	}

	@Override
	public void drawMenuButtons() {
		// TODO Auto-generated method stub
		//for each button
		for(int i = 0; i < noOfButtons; i++){
			//draw each button
			buttons[i].drawComponent();

		}	

	}


	@Override
	public void addMenuListeners(){

		for(final TexturedButton b: buttons){

			b.addListener(new MouseListener() {

				@Override
				public void mouseEntered() {

					onHover(b);

				}

				@Override
				public void mouseClicked() {

					onClick(b);
				}
			});
		}
	}



	/**
	 * Set clicked button
	 * @param clickedButton character clicked
	 */
	public void onClick(TexturedButton clickedButton){

		this.clickedButton = clickedButton;

	}

	/**
	 * Set hovered button
	 * @param hoveredButton character hovered over
	 */
	public void onHover(TexturedButton hoveredButton){

		playerSelectHover(hoveredButton);
		setAttributes(hoveredButton);

	}

	/**
	 * When player hovers over character change preview
	 * @param hoveredButton character being hovered over
	 */
	public void playerSelectHover(TexturedButton button){
		hoveredButton = true;
		//get texture
		Texture previewTexture = button.getButtonTexture();
		//set texture of preview
		playerPreview.setButtonTexture(previewTexture);
		selectNotification = false;

	}

	/**
	 * Creates information labels
	 */
	public void createInfoLabels(){

		int xOrigin = (windowWidth/4) + (windowWidth/16);
		int yOrigin = windowHeight/12;
		heading = new Label(gameWindow, xOrigin, yOrigin, "Choose your player", Color.black, 30);

	}

	/**
	 * Draws information labels
	 */
	public void drawInfoLabels(){

		heading.drawComponent();

	}

	/**
	 * Creates the background image
	 */
	public void menuBackground(){

		TextureOperations.drawTexturedQuad(background, windowWidth, windowHeight);
		backgroundTransparency.drawComponent();
	}

	//****Getters****

	public TexturedButton[] getButtons() {
		return buttons;
	}

	public TexturedButton getPlayerPreview() {
		return playerPreview;
	}

	public Button getConfirmation(){

		return confirmation;
	}

	public Button getBack() {
		return back;
	}

	public Texture getDefaultPreview() {
		return defaultPreview;
	}

	public TexturedButton getClickedButton() {
		return clickedButton;
	}

	public Label[] getSelectedAttributes() {
		return selectedAttributes;
	}

	//****Setters****

	/**
	 * Checks the hovered button so correct
	 * attributes displayed
	 * @param hoveredButton
	 */
	public void setAttributes(TexturedButton hoveredButton){

		for(int i = 0; i < buttons.length; i++){
			//if hovered button
			if(hoveredButton.equals(buttons[i])){
				//set attributes to be displayed
				selectedAttributes = testLabels.get(i);
			}
		}
	}

	/**
	 * Set defaults for preview
	 */
	public  void setPreview(){

		if(clickedButton == null){
			//				//display default 
			playerPreview.setButtonTexture(defaultPreview);
			selectedAttributes = defaultLabel;

		}else{
			//display clicked character
			playerPreview.setButtonTexture(clickedButton.getButtonTexture());

		}

	}



	/**
	 * If back button is selected default view is set
	 */
	public void setDefault(){

		playerPreview.setButtonTexture(defaultPreview);
		selectedAttributes = defaultLabel;
		clickedButton = null;
		selectNotification = false;
		hoveredButton = false;
	}

	/**
	 * Current player clicked
	 * @param clickedButton player clicked
	 */
	public void setClickedButton(TexturedButton clickedButton) {
		this.clickedButton = clickedButton;
	}


	public boolean isHoveredButton() {
		return hoveredButton;
	}

	public void setHoveredButton(boolean hoveredButton) {
		this.hoveredButton = hoveredButton;
	}


}


