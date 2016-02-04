package shared.GUI.Components;

import org.newdawn.slick.Color;

import shared.GameWindow;

/**
 * Labeled rectangle component
 * @author Arun
 *
 */
public class LabelledRectangle extends RectangleComponent {

	private Label label;
	
	public LabelledRectangle(GameWindow gameWindow, int x, int y, int width,int height, Color colour,String labelText,int fontSize) {
		super(gameWindow, x, y, width, height, colour);
		// TODO Auto-generated constructor stub
		
		label = new Label(gameWindow, x, y, labelText, Color.white, fontSize);
	}

	@Override
	public void drawComponent(){
		
		 super.drawComponent();
		 label.drawComponent();
	}
	
	public Label getLabel() {
		return label;
	}
	
}
