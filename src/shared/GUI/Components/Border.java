package shared.GUI.Components;

import org.lwjgl.opengl.Drawable;
import org.lwjgl.opengl.GL11;

import shared.GameWindow;

/**
 * Border Component
 * @author Arun
 *
 */
public class Border extends Component{

	//Border attributes
	private int borderX, borderY, borderWidth, borderHeight;
	private float lineThickness;
	private float defaultThickness = 1.0f;

	/**
	 * Constructs a border
	 * @param gameWindow The game window
	 * @param x coordinate of border
	 * @param y coordinate of border
	 * @param width width of border
	 * @param height height of border
	 * @param borderDistance distance of border from component
	 * @param lineThickness thickness of border line
	 */
	public Border(GameWindow gameWindow, int x, int y, int width, int height, int borderDistance,float lineThickness) {
		
		super(gameWindow, x, y, width, height);

		this.borderX = x;
		this.borderY = y;
		this.borderWidth = width;
		this.borderHeight = height;

		this.lineThickness = lineThickness;

		//if applied to a component
		calculateBorder(borderDistance);
	}

	@Override
	public void drawComponent() {
		
		//set colour
		GL11.glColor3f(color.r, color.g, color.b);
		//set line thickness
		GL11.glLineWidth(lineThickness);
		
		//render lines
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex2i(borderX, borderY);
		GL11.glVertex2i(borderX + borderWidth, borderY);
		GL11.glEnd();

		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex2i(borderX, borderY);
		GL11.glVertex2i(borderX, borderY + borderHeight);
		GL11.glEnd();

		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex2i(borderX + borderWidth, borderY);
		GL11.glVertex2i(borderX + borderWidth, borderY + borderHeight);
		GL11.glEnd();

		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex2i(borderX, borderY + borderHeight);
		GL11.glVertex2i(borderX + borderWidth, borderY + borderHeight);
		GL11.glEnd();

		GL11.glDisable(GL11.GL_TEXTURE_2D);

		GL11.glLineWidth(defaultThickness);


	}

	/**
	 * If Border is applied to component then the dimensions
	 * are recalculated depending on border distance and component
	 * dimensions
	 */
	public void calculateBorder(int borderDistance){

		borderX = borderX - borderDistance;
		borderY = borderY - borderDistance;
		borderWidth = borderWidth + (borderDistance * 2);
		borderHeight = borderHeight + (borderDistance * 2);

	}

}
