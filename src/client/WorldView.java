package client;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import shared.GUI.Components.Component;
import shared.GUI.Components.Label;
import shared.entities.Player;

/**
 * Class used to draw the map and world entities to the game window.
 * Drawing is relative to the player position.
 * @author Chris
 *
 */
public class WorldView {
	
	
	//****Class variables****
	

	private final int BACKGROUND_TEXTURE_WIDTH = 128;
	private final int BACKGROUND_TEXTURE_HEIGHT = 128;
	
	private ClientWorld world;
	private Player player;
	private String mapTextureName;
	
	private Texture mapTexture;
	
	
	//****Constructors****
	
	
	public WorldView(ClientWorld world, Player player, String mapTextureName) {
		this.world = world;
		this.player = player;
		this.mapTextureName = mapTextureName;
	}
	
	
	//****Class methods****
	
	
	/**
	 * Loads a JPG texture file.
	 * @param imageName The image name
	 */
	public Texture getTextureJPG(String imageName) {
		try {
			return TextureLoader.getTexture("JPG", ResourceLoader.getResourceAsStream("res/images/" + imageName + ".jpg"));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Draws a rectangle in the specified world position relative
	 * to the top-down map view/player position. As the player moves
	 * right, rectangles will be drawn further left and vice-versa.
	 * (x,y) is the center of the rectangle.
	 * @param x world position x
	 * @param y world position y
	 * @param width width of rectangle
	 * @param height height of rectangle
	 * @param r red colour component (0.0f-1.0f)
	 * @param g green colour component (0.0f-1.0f)
	 * @param b blue colour component (0.0f-1.0f)
	 * @param fill determines whether the rectangle should be colour filled or just an outline
	 * @param texture texture
	 * @param zRotation the z angle to rotate
	 */
	public void drawWorldRectangleCentered(float x, float y, int width, int height, float r, float g, float b, boolean fill, Texture texture, float zRotation) {
		boolean textured = (texture != null);

		if (textured) {
			GL11.glEnable(GL11.GL_TEXTURE_2D);

			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);

			Color.white.bind();
			texture.bind();
		}

		Vector2f p = player.getPosition();
		
		int resWidth = world.getGameWindow().getWidth();
		int resHeight = world.getGameWindow().getHeight();
		int mapOffsetX = (int)-p.getX()+resWidth/2;
		int mapOffsetY = (int)p.getY()+resHeight/2;

		GL11.glPushMatrix();
		GL11.glTranslatef(mapOffsetX+x, mapOffsetY-y, 0);
		
		if (zRotation != 0.0f) {
			GL11.glRotatef(zRotation, 0, 0, 1);
		}
	
		GL11.glColor3f(r, g, b);
		GL11.glBegin(fill ? GL11.GL_QUADS : GL11.GL_LINE_LOOP);
			if (textured) GL11.glTexCoord2f(0, 0);
			GL11.glVertex2f(-width/2, -height/2);
			if (textured) GL11.glTexCoord2f(1, 0);
			GL11.glVertex2f(width/2, -height/2);
			if (textured) GL11.glTexCoord2f(1, 1);
			GL11.glVertex2f(width/2, height/2);
			if (textured) GL11.glTexCoord2f(0, 1);
			GL11.glVertex2f(-width/2, height/2);
		GL11.glEnd();
		
		GL11.glPopMatrix();
		
		if (textured) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}
	}
	
	public void drawWorldRectangleCentered(float x, float y, int width, int height, float r, float g, float b, boolean fill) {
		drawWorldRectangleCentered(x, y, width, height, r, g, b, fill, null, 0.0f);
	}

	public void drawWorldRectangleCentered(float x, float y, int width, int height, Texture texture) {
		drawWorldRectangleCentered(x, y, width, height, 1.0f, 1.0f, 1.0f, true, texture, 0.0f);
	}

	public void drawWorldRectangleCentered(float x, float y, int width, int height, Texture texture, float zRotation) {
		drawWorldRectangleCentered(x, y, width, height, 1.0f, 1.0f, 1.0f, true, texture, zRotation);
	}

	/**
	 * Draws a rectangle in the specified world position relative
	 * to the top-down map view/player position. As the player moves
	 * right, rectangles will be drawn further left and vice-versa.
	 * (x,y) is the top left point of the rectangle.
	 * @param x world position x
	 * @param y world position y
	 * @param width width of rectangle
	 * @param height height of rectangle
	 * @param r red colour component (0.0f-1.0f)
	 * @param g green colour component (0.0f-1.0f)
	 * @param b blue colour component (0.0f-1.0f)
	 * @param fill determines whether the rectangle should be colour filled or just an outline
	 * @param texture texture
	 */
	public void drawWorldRectangle(float x, float y, int width, int height, float r, float g, float b, boolean fill, Texture texture) {
		boolean textured = (texture != null);

		if (textured) {
			GL11.glEnable(GL11.GL_TEXTURE_2D);

			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);

			Color.white.bind();
			texture.bind();
		}

		Vector2f p = player.getPosition();
		
		int resWidth = world.getGameWindow().getWidth();
		int resHeight = world.getGameWindow().getHeight();
		int mapOffsetX = (int)-p.getX()+resWidth/2;
		int mapOffsetY = (int)p.getY()+resHeight/2;

		GL11.glPushMatrix();
		GL11.glTranslatef(mapOffsetX+x, mapOffsetY-y, 0);
			
		GL11.glColor3f(r, g, b);
		GL11.glBegin(fill ? GL11.GL_QUADS : GL11.GL_LINE_LOOP);
			if (textured) GL11.glTexCoord2f(0, 0);
			GL11.glVertex2f(0, 0);
			if (textured) GL11.glTexCoord2f(1, 0);
			GL11.glVertex2f(width, 0);
			if (textured) GL11.glTexCoord2f(1, 1);
			GL11.glVertex2f(width, height);
			if (textured) GL11.glTexCoord2f(0, 1);
			GL11.glVertex2f(0, height);
		GL11.glEnd();
		
		GL11.glPopMatrix();
		
		if (textured) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}
	}
		
	/**
	 * Draws map background image relative to top-down map view/player position.
	 */
	public void drawMapBackground() {
		if (this.mapTexture == null)
			this.mapTexture = getTextureJPG(mapTextureName);
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		Color.white.bind();
		mapTexture.bind();
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		
		int resWidth = world.getGameWindow().getWidth();
		int resHeight = world.getGameWindow().getHeight();		
		float textureWidth = ((float)resWidth)/BACKGROUND_TEXTURE_WIDTH;
		float textureHeight = ((float)resHeight)/BACKGROUND_TEXTURE_HEIGHT;
		
		// Move with player
		Vector2f p = player.getPosition();
		float textureOffsetX = p.getX()/BACKGROUND_TEXTURE_WIDTH;
		float textureOffsetY = -p.getY()/BACKGROUND_TEXTURE_HEIGHT;
		
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(textureOffsetX, textureOffsetY);
			GL11.glVertex2f(0, 0);
			
			GL11.glTexCoord2f(textureWidth+textureOffsetX, textureOffsetY);
			GL11.glVertex2f(resWidth, 0);
			
			GL11.glTexCoord2f(textureWidth+textureOffsetX, textureHeight+textureOffsetY);
			GL11.glVertex2f(resWidth, resHeight);
			
			GL11.glTexCoord2f(textureOffsetX, textureHeight+textureOffsetY);
			GL11.glVertex2f(0, resHeight);
		GL11.glEnd();
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
	public void drawGUIComponent(Component component, float x, float y) {
		Vector2f p = player.getPosition();
		
		int resWidth = world.getGameWindow().getWidth();
		int resHeight = world.getGameWindow().getHeight();
		int mapOffsetX = (int)-p.getX()+resWidth/2;
		int mapOffsetY = (int)p.getY()+resHeight/2;

		component.setOriginX(mapOffsetX+(int)x);
		component.setOriginY(mapOffsetY-(int)y);
		
		component.drawComponent();
	}

	/**
	 * Converts a mouse coordinate to the corresponding world coordinate.
	 * @param mousePos mouse coordinate
	 * @return world coordinate
	 */
	public Vector2f mousePosToWorldPos(Vector2f mousePos) {
		Vector2f playerPos = player.getPosition();

		int resWidth = world.getGameWindow().getWidth();
		int resHeight = world.getGameWindow().getHeight();
		float x = mousePos.getX()+playerPos.getX()-resWidth/2;
		float y = mousePos.getY()+playerPos.getY()-resHeight/2;
		
		return new Vector2f(x, y);
	}
	


}
