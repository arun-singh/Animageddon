package shared.GUI.Components;

import java.util.ArrayList;

import org.newdawn.slick.Color;

import shared.GameWindow;

/**
 * Super class for all components
 * @author Arun
 *
 */
public abstract class Component {

	// game window
	private GameWindow gameWindow;
	// component properties
	private int originX,originY,width,height;
	//border
	private Border border;
	//transparency
	protected float alpha;
	protected boolean transparency = false;
	// colour 
	protected Color color;


	/**
	 * Component class provides base functionality
	 * for other components to extend on
	 * @param gameWindow Game window access
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param width width of component
	 * @param height height of component
	 */
	public Component(GameWindow gameWindow, int x, int y, int width, int height){

		// initialize global variables 
		this.gameWindow = gameWindow;
		this.originX = x;
		this.originY = y;
		this.width = width;
		this.height = height;
	}

	
	/**
	 * Set colour of component
	 * @param color colour object
	 */
	public void setColor(Color color) {
		this.color = color;
	}
	
	/**
	 * Called when component is rendered
	 */
	public abstract void drawComponent();


	/**
	 * Checks if mouse clicks are within component bounds
	 * @param X Mouse x coordinate
	 * @param Y Mouse y coordinate
	 * @return if mouse has been clicked inside
	 */
	public  boolean inside(int X, int Y) {

		int w = width;
		int h = height;

		//checks for negative dimensions
		if ((w | h) < 0) {
			return false;
		}
		//sets coordinates
		int x = originX;
		int y = originY;

		int originPlusWidth = originX + width;
		int originPlusHeight = originY + height;

		return((X >= originX && X <= originPlusWidth)
				&& (Y >= originY && Y <= originPlusHeight));

	}

	/**
	 * If transparency is needed when rendering the component <p>
	 * Alpha value between 0 and 1 (0 being opaque)
	 * @param alpha value of transparency 
	 */
	public void setTransparency(float alpha){

		transparency = true;

		this.alpha = alpha;

	}
	
	/**
	 * Used to compare to components
	 * Based on their x and y origins
	 */
	@Override
	public boolean equals(Object other){
		if (other == null) return false;
		if (other == this) return true;
		if (!(other instanceof Component))return false;

		Component otherComponent = (Component)other;

		if(this.getOriginX() == otherComponent.getOriginX()
				&& this.getOriginY() == otherComponent.getOriginY()){

			return true;

		}else{

			return false;

		}

	}

	//****Getters****
	
	/**
	 * Gets x coordinate
	 * @return x coordinate of component
	 */
	public int getOriginX() {
		return originX;
	}
	
	/**
	 * Gets y coordinate
	 * @return y coordinate of component
	 */
	public int getOriginY() {
		return originY;
	}
	
	/**
	 * Gets the width of component
	 * @return component width
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Gets the height of the component
	 * @return height of the component
	 */
	public int getHeight() {
		return height;
	}
	

	public Border getBorder() {
		return border;
	}

	//****Setters****
	
	/**
	 * Set x coordinate of component
	 * @param originX number to set x coordinate to
	 */
	public void setOriginX(int originX) {
		this.originX = originX;
	}

	/**
	 * Set y coordinate of component
	 * @param originY number to set y coordinate to 
	 */
	public void setOriginY(int originY) {
		this.originY = originY;
	}

	/**
	 * Sets width of component
	 * @param width size of width to be set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Sets height of component
	 * @param height size of height to be set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Sets a border to the component
	 * @param border border to be set
	 */
	public void setBorder(Border border) {
		this.border = border;
	}

}
