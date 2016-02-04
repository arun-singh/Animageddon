package shared.GUI.Components;
import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import shared.GameWindow;
import shared.GUI.UserInput.MouseListener;


/**
 * Button Component
 * @author Arun
 *
 */
public class Button extends RectangleComponent{

	//label properties
	private Label label;

	//to hold listeners
	private ArrayList<MouseListener> listeners = new ArrayList<MouseListener>();


	/**
	 * 
	 * @param gameWindow GameWindow access
	 * @param x  x coordinate
	 * @param y y coordinate 
	 * @param width width of the button
	 * @param height height of the button
	 * @param text label text for button
	 * @param colour colour of button
	 */
	public Button(GameWindow gameWindow, int x, int y, int width, int height, String text,Color colour){
		//invokes super constructor
		super(gameWindow, x, y, width, height, colour);
		
		//Initialize label	
		label = new Label(gameWindow, x, y, text, Color.white,12f);
		//set label position
		label.centreLabel(this);
	}
	
	@Override
	public void drawComponent() {
		super.drawComponent();
		
		label.drawComponent();
	}
	
	/**
	 * Add listener to button 
	 * @param add MouseListener to be added
	 */
	public void addListener(MouseListener add){
		
		listeners.add(add);
	}
	
	/**
	 * Executes hover method in each listener
	 */
	public void hover(){
		
		for(MouseListener m: listeners){
			
			m.mouseEntered();
		}
	}
	
	/**
	 * Executes click method in each listener
	 */
	public void click(){
		
	for(MouseListener m: listeners){
			
			m.mouseClicked();
		}
		
	}
	
	/**
	 * Provides access to button label
	 * @return Label of button
	 */
	public Label getLabel() {
		return label;
	}
}

