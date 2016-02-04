package shared.GUI.Components;

import java.awt.Color;
import java.awt.Font;
import java.io.InputStream;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;

import shared.GameWindow;

/**
 * Label Component
 * @author Arun
 *
 */
public class Label  extends Component{

	//font
	private TrueTypeFont customLabel;
	//label text
	private String text;
	//text colour
	private org.newdawn.slick.Color colour;
	//label width
	private int labelWidth, labelHeight;
	//original x
	private int originalY;

	/**
	 * Creates a label with desire text and position
	 * @param x X coordinate 
	 * @param y Y coordinate
	 * @param text label text
	 * @param colour text colour
	 * @param fontSize font size
	 */
	public Label(GameWindow gameWindow, int x, int y, String text, org.newdawn.slick.Color colour, float fontSize){
		//passes information to super constructor
		super(gameWindow, x, y, 0, 0);
		
		//initialize global variables
		this.text = text;
		this.colour = colour;
		this.originalY = y;
		
		//label properties set
		setColor(colour);
		customFont(fontSize);
		setLabelText(text);
	}

	/**
	 * Creates font for the label
	 * @param fontSize size of font
	 */
	public void customFont(float fontSize){

		try {
			//gets custom font file
			InputStream inputStream	= ResourceLoader.getResourceAsStream("res/Fonts/OpenSans-Bold.ttf");
			//creates font
			Font awtFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
			awtFont = awtFont.deriveFont(fontSize); // set font size
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			//applies font to label
			customLabel = new TrueTypeFont(awtFont, true);
			GL11.glDisable(GL11.GL_TEXTURE_2D);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Calculates positioning if label is to be applied to 
	 * another component
	 * @param c component that label will appear on
	 */
	public  void centreLabel(Component c){

		//Calculate label position
		//(leftEdge + rightEdge - textWidth)/2
		int x = (c.getOriginX() + (c.getOriginX() + c.getWidth()) - labelWidth)/2;
		int y = (c.getOriginY() + (c.getOriginY() + c.getHeight()) - labelHeight)/2;
		//set positions in relation to component
		setOriginX(x);
		setOriginY(y);

	}

	/**
	 * Just to calaculate y coordinate based on 
	 * another component
	 * @param c Component that label will appear on
	 */
	public void centreLabelY(Component c){

		int y = (c.getOriginY() + (c.getOriginY() + c.getHeight()) - labelHeight)/2;
		setOriginY(y);

	}

	@Override
	public void drawComponent() {
		//draw label

		if(!multiLineCheck()){
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			customLabel.drawString(getOriginX(), getOriginY(), text, colour);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}else{
			
			parseString(originalY);

		}

	}


	/**
	 * Check if label text consists of multiple lines
	 * @return true if text has multiple lines
	 */
	public boolean multiLineCheck(){

		if(text.contains("\n")){

			return true;
		}

		return false;
	}

	/**
	 * Parses string with multiple lines
	 * @param originalY original y coorindate for re-rendering
	 */
	public void parseString(int originalY){

		//for each line
		for(String line: text.split("\n")){

			//draw label
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			customLabel.drawString(getOriginX(), getOriginY(), line, colour);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			//set new line Y
			setOriginY(getOriginY() + getHeight());
		}
		setOriginY(originalY);

	}
	
	//****Getters****
	
	@Override
	public int getWidth(){

		return labelWidth;
	}

	@Override
	public int getHeight(){

		return labelHeight;
		
	}

	/**
	 * Gets label text
	 * @return label text
	 */
	public String getLabelText(){
		return text;
	}
	
	//****Setters****

	/**
	 * Sets label text and can be used to get
	 * the label width
	 * @param _text label text
	 */
	public void setLabelText(String _text){
		//initialize text
		this.text = _text;
		//initialize label width
		this.labelWidth = customLabel.getWidth(_text);
		this.labelHeight = customLabel.getHeight(_text);

	}
	
	@Override
	public void setColor(org.newdawn.slick.Color colour) {
		this.colour = colour;
	}




}
