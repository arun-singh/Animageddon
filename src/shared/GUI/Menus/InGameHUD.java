package shared.GUI.Menus;

import java.util.ArrayList;

import javax.swing.JPopupMenu.Separator;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import shared.GameWindow;
import shared.GUI.Components.Button;
import shared.GUI.Components.Component;
import shared.GUI.Components.Label;
import shared.GUI.Components.RectangleComponent;

/**
 * In game heads up display
 * @author Arun
 *
 */
public class InGameHUD {

	//game window access
	private GameWindow gameWindow;
	//coordinate of the HUD
	private int upperY;
	private int lowerY;
	private int centre;

	//HUD background
	private Button background;
	private final int backgroundHeight = 40;
	private final int headingTextSize = 14;
	private final int infoTextSize = 15;

	//background colour
	Color backgroundColour = MenuTemplate.getBackgroundColor();

	//info label colour
	Color  infoColour = new Color(0.824f, 0.804f, 0.804f);
	Color separatorColour  = new Color(	0.047f, 0.29f, 0.031f);

	//separators
	private int separatorLength = backgroundHeight - 7;

	float transparency = 0.3f;
	private Color labelColour = Color.black;

	//spacing
	private final int infoSpacing = 5;
	private final int headingSpacing = 28;
	private final int separatorSpacing = 7;

	//HUD Label heading
	private Label healthHeading,killHeading,deathHeading,flagsCaptured,flagsReturned,score,rank;
	private ArrayList<Label> headings = new ArrayList<Label>();
	private ArrayList<RectangleComponent> allSeperators = new ArrayList<RectangleComponent>();
	//HUD Label info
	private Label[] information = new Label[6];

	//temp separator
	private RectangleComponent separatorOne,separatorTwo,separatorThree,separatorFour;

	public InGameHUD(GameWindow gameWindow){

		this.gameWindow = gameWindow;

		//upper and lower bounds of HUD
		upperY = gameWindow.mouseToScreenY(40); 
		lowerY = upperY + backgroundHeight;  

		createHUD();

	}

	/**
	 * Called in main game loop to render view
	 */
	public void render(){

		background.drawComponent();
		drawHeadings();
		drawLabels();
		drawSeparators();

	}

	/**
	 * Call create methods
	 */
	public void createHUD(){

		createBackground();
		createLabelHeadings();
		createLabelInfo();
		separator();

	}

	/**
	 * Calculate spacing for HUD component
	 * based off another component
	 * @param c Component applied to
	 * @param spacing distance
	 * @return new spacing
	 */
	public int spacing(Component c, int spacing){

		return c.getOriginX() + c.getWidth() + spacing;

	}

	/**
	 * Create label headings
	 */
	public void createLabelHeadings(){

		int healtHeadingX = background.getOriginX() + infoSpacing;
		healthHeading = new Label(gameWindow, healtHeadingX, 0, "Health", labelColour, headingTextSize);
		headings.add(healthHeading);

		int killHeadingX = spacing(healthHeading,60);
		killHeading = new Label(gameWindow, killHeadingX, 0, "Kills", labelColour, headingTextSize); 
		headings.add(killHeading);

		int deathHeadingX = spacing(killHeading,headingSpacing);
		deathHeading = new Label(gameWindow, deathHeadingX, 0, "Deaths", labelColour, headingTextSize);
		headings.add(deathHeading);

		int flagsCapturedX = spacing(deathHeading,35);
		flagsCaptured = new Label(gameWindow, flagsCapturedX, 0, "Flags Captured", labelColour, headingTextSize);
		headings.add(flagsCaptured);

		int scoreX = spacing(flagsCaptured,35);
		score = new Label(gameWindow, scoreX, 0, "Score", labelColour, headingTextSize);
		headings.add(score);

		int rankX = spacing(score, 35);
		rank = new Label(gameWindow, rankX, 0, "Individual Rank", labelColour, headingTextSize);
		headings.add(rank);

		//set properties for labels
		for(Label l:headings){

			setLabelProperties(l);

		}
	}

	/**
	 * Label information creation
	 */
	public void createLabelInfo(){

		int x;

		//for each piece of information
		for(int i = 0; i < information.length; i++){
			//calculate spacing
			x = spacing(headings.get(i),infoSpacing);
			//apply to designated labels
			if(i == 0){

				information[i] = new Label(gameWindow, x, 0, "100%", infoColour, infoTextSize);

			}else{

				information[i] = new Label(gameWindow, x, 0, "10", infoColour, infoTextSize);

			}

			setLabelProperties(information[i]);
		}

	}

	/**
	 * Center label y coordinate
	 * @param label to be repositioned
	 */
	public void setLabelProperties(Label label){

		label.centreLabelY(background);

	}

	/**
	 * Create background
	 */
	public void createBackground(){

		background = new Button(gameWindow, 0, upperY, gameWindow.getWidth() - 100, backgroundHeight, "",backgroundColour);
		background.setTransparency(transparency);


	}

	/**
	 * Calculates y coordinate of separator
	 * @return value of y coordinate 
	 */
	public int separatorY(){

		return upperY + ((backgroundHeight - separatorLength)/2);

	}

	/**
	 * Create separators 
	 */
	public void separator(){

		//healthInfo spacing
		int x = spacing(information[0], separatorSpacing);
		separatorOne = new Button(gameWindow, x, separatorY(), 1, separatorLength, "", separatorColour);
		allSeperators.add(separatorOne);

		//deathInfo spacing
		x = spacing(information[2],separatorSpacing);
		separatorTwo = new Button(gameWindow, x, separatorY(), 1, separatorLength, "", separatorColour);
		allSeperators.add(separatorTwo);

		//flagsCaptured spacing
		x = spacing(information[3], separatorSpacing);
		separatorThree = new Button(gameWindow, x, separatorY(), 1, separatorLength, "", separatorColour);
		allSeperators.add(separatorThree);

		//scoreInfo spacing
		x = spacing(information[4], separatorSpacing);
		separatorFour = new Button(gameWindow, x, separatorY(), 1, separatorLength, "", separatorColour);	
		allSeperators.add(separatorFour);
	}


	/**
	 * Draw separators
	 */
	public void drawSeparators(){

		for(RectangleComponent seperator: allSeperators){

			seperator.drawComponent();

		}

	}

	/**
	 * Draw headings
	 */
	public void drawHeadings(){

		for(Label _heading:headings){

			_heading.drawComponent();

		}

	}

	/**
	 * Draw labels
	 */
	public void drawLabels(){

		for(Label _info:information){

			_info.drawComponent();

		}

	}


}
