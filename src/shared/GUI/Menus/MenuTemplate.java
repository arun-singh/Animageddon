package shared.GUI.Menus;

import org.newdawn.slick.Color;

import shared.GUI.View;
import shared.GUI.Components.Button;

/**
 * Template for menus
 * @author Arun
 *
 */
public abstract class MenuTemplate extends View {
	
	
	//****Class variables****
	
	
	//menu properties
	protected  int noOfButtons, width, height;
	
	//game window properties
	protected int windowWidth,  windowHeight;
	
	//****Constructors****
	

	/**
	 * Template in which other menus can inherit from
	 * @param noOfButtons number of buttons menu requires
	 * @param width width of the menu
	 * @param height height of the menu
	 */
	public MenuTemplate(int noOfButtons, int width, int height){
		
		//initialize global variables
		this.noOfButtons = noOfButtons;
		this.width = width;
		this.height = height;			
		//colour

	}
	
	
	//****Abstract methods****
	
	
	/**
	 * Creates the menu buttons
	 * @param xOrigin X coordinate of button at 'bottom' of menu
	 * @param yOrigin Y coordinate of button at 'bottom' of menu
	 * @param xSpacing spacing between x coordinate of each button
	 * @param ySpacing spacing between y coorindate of each button
	 * @param labels contains button labels
	 */
	protected abstract void createMenuButtons(int xOrigin, int yOrigin, int xSpacing, int ySpacing, String[] labels);

	/**
	 * Draw menu buttons
	 */
	protected abstract void drawMenuButtons();
	
	/**
	 * Add listeners to menu buttons
	 */
	protected abstract void addMenuListeners();
	
	//****Static methods****

	/**
	 * On menu button hover set hover colour
	 * @param button button being hovered over
	 */
	public static void onHover(Button button){

		button.setColor(hoverColour);

	}

	/**
	 * When user has finished hovering over button
	 * @param button button to set default background
	 */
	public static void defaultHover(Button button){

		button.setColor(backgroundColour);

	}

	//****Getters****
	
	/**
	 * Get width of menu
	 * @return menu width
	 */
	public int getMenuWidth() {
	
		// TODO Auto-generated method stub
		return width;
		
	}
	
	/**
	 * Gets height of menu
	 * @return menu height
	 */
	public int getMenuHeight() {
		
		// TODO Auto-generated method stub
		return height;
		
	}
	
	/**
	 * Gets the background colour
	 * @return	background colour
	 */
	public static Color getBackgroundColor() {
		
		return backgroundColour;
		
	}
	
	/**
	 * Gets the hover colour
	 * @return	hover colour
	 */
	public static Color getHoverColour() {
		
		return hoverColour;
		
	}
	
	//****Setters****
	
	/**
	 * Sets the background colour
	 * @param backgroundColor the colour to be set
	 */
	public void setBackgroundColor(Color backgroundColor) {
		
		MenuTemplate.backgroundColour = backgroundColor;
		
	}

	/**
	 * Sets the hover colour
	 * @param hoverColour	the colour to be set
	 */
	public void setHoverColour(Color hoverColour) {
		
		MenuTemplate.hoverColour = hoverColour;
		
	}
	
	
}
