package shared.GUI.Components;
import static org.lwjgl.opengl.GL11.*;

import java.awt.Font;
import java.awt.Rectangle;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

import shared.GameWindow;

/**
 * Rectangle component
 * @author Arun, Thomas
 *
 */
public class RectangleComponent extends Component{

	/**
	 * Creates a Rectangle
	 * @param x coordinate
	 * @param y coordinate 
	 * @param width width of the Rectangle
	 * @param height height of the Rectangle
	 * @param colour colour of the Rectangle
	 */
	public RectangleComponent(GameWindow gameWindow, int x, int y, int width, int height, Color colour){
		//invokes super constructor
		super(gameWindow, x, y, width, height);
	
		setColor(colour);
	}

	@Override
	public void drawComponent() {

		// Draw button background
		if(!transparency){
			GL11.glColor3f(color.r, color.g, color.b);
		}else{
			GL11.glColor4f(color.r, color.g, color.b, alpha);
		}
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glVertex2f(getOriginX(), getOriginY());
		GL11.glVertex2f(getOriginX(), getOriginY() + getHeight());
		GL11.glVertex2f(getOriginX() + getWidth(), getOriginY() + getHeight());
		GL11.glVertex2f(getOriginX() + getWidth(), getOriginY());
		GL11.glEnd();

	}


}

