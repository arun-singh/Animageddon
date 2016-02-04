package shared.GUI.Components;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import shared.GameWindow;

/**
 * Textured button component
 * @author Arun
 *
 */
public class TexturedButton extends Button {
	
	private String imageName;
	private Texture buttonTexture;

	/**
	 * Constructs a textured button
	 * @param gameWindow GameWindow access
	 * @param x x coordinate of textured button
	 * @param y y coordinate of textured button
	 * @param width width of textured button
	 * @param height height of textured button
	 * @param text text of button
	 * @param imageName name of image to be binded
	 */
	public TexturedButton(GameWindow gameWindow, int x, int y, int width,
			int height, String text, String imageName) {
		
		super(gameWindow, x, y, width, height, text,Color.white);
		
		//checks if no image to bind
		if (imageName != null) {
			this.imageName = imageName;
			buttonTexture = getTexture(imageName);
		}	
	}
	
	@Override
	public void drawComponent(){
		
		if (buttonTexture == null)
			return;

		GL11.glEnable(GL11.GL_TEXTURE_2D);

		Color.white.bind();
		buttonTexture.bind();  //binds texture

		//draws quad and texture coordinates
		GL11.glBegin(GL11.GL_QUADS);
		
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2f(getOriginX(), getOriginY());

		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2f(getOriginX() + getWidth(), getOriginY());

		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2f(getOriginX() + getWidth(), getOriginY() + getHeight());

		GL11.glTexCoord2f(0, 1);

		GL11.glVertex2f(getOriginX(), getOriginY() + getHeight());
	
		GL11.glEnd();

		GL11.glDisable(GL11.GL_TEXTURE_2D);
			
	}
	
	//****Getters****
	
	/**
	 * Loads texture
	 * @param imageName image name of background
	 * @return loaded texture
	 */
	public static Texture getTexture(String imageName) {
		try {
			return TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/images/" + imageName + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get name if image to be used
	 * @return image name
	 */
	public String getImageName() {
		return imageName;
	}
	
	/**
	 * Get name of texture to be bond
	 * @return texture to be bound
	 */
	public Texture getButtonTexture() {

		return buttonTexture;
	}

	//****Setters****

	/**
	 * Set image to be used
	 * @param imageName name of image
	 */
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	/**
	 * Set texture to be bound
	 * @param buttonTexture texture to be bound
	 */
	public void setButtonTexture(Texture buttonTexture) {
		this.buttonTexture = buttonTexture;
	}


}
