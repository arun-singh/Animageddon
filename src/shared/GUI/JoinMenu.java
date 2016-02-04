package shared.GUI;

import java.io.IOException;
import java.net.ConnectException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

import shared.GameWindow;
import shared.TextureOperations;
import shared.GUI.Components.Button;
import shared.GUI.Components.Label;
import shared.GUI.UserInput.MouseListener;
import shared.net.NetworkConstants;

/**
 * Menu for joining a server
 * @author Thomas
 *
 */
public class JoinMenu extends View{
	
	private Texture background = TextureOperations.getTexture("playerselectbackground", "JPG",".jpg");
	private Button confirmation, textLine, back;
	private JoinMenuInput input;
	private GameWindow game;
	private Label heading;
	private JoinMenuTextInput keyboard;
	private Label IPLabel;
	
	/**
	 * This is the classes constructor, where it initialises the mouse input, and keyboard input
	 * 
	 * @param game	The game window to maneuver between views
	 */
	public JoinMenu(GameWindow game) {
		this.game = game;
		
		createView();
		
		input = new JoinMenuInput(game, this);
		keyboard = new JoinMenuTextInput(this);
	}
	
	public void render(){
		
		background();
		drawButtons();
		drawHeading();
		drawTextbox();

	}

	public void update(int delta) {
		input.update();
		keyboard.update();
	}
	
	/**
	 * Creates all the GUI components ready for rendering
	 */
	public void createView(){
		createTextbox();
		createButtons();
		createHeading();
	}

	/**
	 * Creates the navigation buttons
	 */
	public void createButtons(){
		int confirmationY = game.mouseToScreenY(70);
		int confirmationX = 375;

		confirmation = new Button(game,confirmationX, confirmationY, 200, 50, "Continue",backgroundColour);
		confirmation.addListener(new MouseListener() {

			@Override
			public void mouseEntered() {
				confirmation.setColor(hoverColour);
			}

			@Override
			public void mouseClicked() {
				// Create a game on the IP inputed
				try {
					game.connectServer(IPLabel.getLabelText(), NetworkConstants.SERVER_PORT);
					game.joinGame();
				} catch(ConnectException e) {
					System.out.println("Server not found!");
					game.setJoinGameMenu();
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(-1);
				}
			}
		});

		///////////////exit

		int backX = confirmation.getOriginX() + confirmation.getWidth() +25;
		back = new Button(game, backX, confirmationY, 100, 50, "Back",backgroundColour);

		back.addListener(new MouseListener() {

			@Override
			public void mouseEntered() {

				back.setColor(hoverColour);
			}

			@Override
			public void mouseClicked() {
				game.setMainView();
			}
		});
	}
	
	/**
	 * Renders the navigation buttons
	 */
	public void drawButtons(){
		confirmation.drawComponent();
		back.drawComponent();
	}
	
	/**
	 * Create the textbox to render
	 */
	public void createTextbox() {
		IPLabel = new Label(game, 100, 225, "", Color.black, 20);
		textLine = new Button(game, 100, 250, 550, 3, "",backgroundColour);
	}
	
	/**
	 * Render the textbox
	 */
	public void drawTextbox() {
		IPLabel.drawComponent();
		textLine.drawComponent();
	}

	/**
	 * Creates information labels
	 */
	public void createHeading(){
		heading = new Label(game, 250, 50, "Input Host Server IP", Color.black, 30);
	}

	/**
	 * Draws information labels
	 */
	public void drawHeading(){
		heading.drawComponent();
	}

	/**
	 * Creates the background image
	 */
	public void background(){
		TextureOperations.drawTexturedQuad(background, game.getWidth(), game.getHeight());
	}

	//****Getters****
	
	/**
	 * Access to confirmation button
	 * @return confirmation button
	 */
	public Button getConfirmation(){
		return confirmation;
	}

	/**
	 * Access to back button
	 * @return back button
	 */
	public Button getBack() {
		return back;
	}

	/**
	 * Access to IP label
	 * @return IP label
	 */
	public Label getIPLabel() {
		return this.IPLabel;
	}
}


