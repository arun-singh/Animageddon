package navmeshgenerator;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import shared.Entity;
import shared.Polygon;

import org.lwjgl.util.vector.Vector2f;

/**
 * Draws a navigation mesh onto a JPanel
 * @author Barney
 */
public class DrawingPanel extends JPanel {
	// The navigation mesh generator that stores the list of obstacles that need to be drawn
	private final NavMeshGenerator navMeshGenerator;

	/**
	 * Construct the drawing panel
	 * @param navMeshGenerator The navigation mesh generator that this class is drawing
	 */
	public DrawingPanel(NavMeshGenerator navMeshGenerator) {
		this.navMeshGenerator = navMeshGenerator;
	}

	public void paintComponent(Graphics g) {
		// Call the super constructor
		super.paintComponent(g);

		// Split up the work and pass it on
		this.paintObstacles(g);
		this.paintPolygons(g);
	}

	/**
	 * Paint the obstacles onto the graphic object
	 * @param g The graphic to draw on
	 */
	private void paintObstacles(Graphics g) {
		// Draw each obstacle
		for (Entity o : this.getNavMeshGenerator().getObstaclesToBeDrawn()) {
			int width = o.getBoundingBoxWidth();
			int height = o.getBoundingBoxHeight();
			int top = (int)(o.getY()) + (height/2);
			int left = (int)(o.getX()) - (width/2);

			Vector2f topLeftWorld = new Vector2f(left, top);

			// Set the colour to blue and draw the obstacle
			g.setColor(Color.BLUE);
			this.paintWorldRectangle(topLeftWorld, width, height, false, g);

			// Set the colour to black and draw the outline
			g.setColor(Color.BLACK);
			this.paintWorldRectangle(topLeftWorld, width, height, true, g);
		}
	}

	/**
	 * Paint the polygons onto the graphic object
	 * @param g The graphic to draw on
	 */
	private void paintPolygons(Graphics g) {
		// Set the colour to red
		g.setColor(Color.RED);

		// Draw each polygon
		for (Polygon p : this.getNavMeshGenerator().getPolygons()) {
			this.paintWorldRectangle(p.getTopLeft(), p.getWidth(), p.getHeight(), true, g);
		}
	}

	/**
	 * Paints a rectangle, given in world coordinates, onto a graphic
	 * @param topLeftWorld The top left coordinate of the rectangle, in world coordinates
	 * @param width The width of the rectangle
	 * @param height The height of the rectangle
	 * @param outline Set to true to draw the outline only, and false to draw a filled rectangle
	 * @param g The graphic to draw onto
	 */
	private void paintWorldRectangle(Vector2f topLeftWorld, int width, int height, boolean outline, Graphics g) {
		Vector2f topLeftGraphic = this.translateFromWorldToGraphic(topLeftWorld);
		if (outline) {
			g.drawRect((int)topLeftGraphic.getX(), (int)topLeftGraphic.getY(), width, height);
		} else {
			g.fillRect((int)topLeftGraphic.getX(), (int)topLeftGraphic.getY(), width, height);
		}
	}

	/**
	 * Translates a coordinate in the world system to a coordinate in the graphics system
	 * @param original The world coordinate
	 * @return The graphics coordinate
	 */
	private Vector2f translateFromWorldToGraphic(Vector2f original) {
		// Do the translation
		float x = original.getX() + 500;
		float y = (original.getY() * -1) + 500;

		// Return the translated coordinates
		return new Vector2f(x, y);
	}

	/**
	 * Gets the navigation mesh generator
	 * @return The navigation mesh generator
	 */
	private NavMeshGenerator getNavMeshGenerator() {
		return this.navMeshGenerator;
	}
}
