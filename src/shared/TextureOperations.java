package shared;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class TextureOperations {

	/**
	 * Loads texture
	 * @param imageName image name of background
	 * @return loaded texture
	 */
	public static Texture getTexture(String imageName, String fileType, String fileExtension) {
		try {
			return TextureLoader.getTexture(fileType, ResourceLoader.getResourceAsStream("res/images/" + imageName + fileExtension));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Draws a textured quad at the origin.
	 * @param texture texture to use
	 * @param quadWidth width of quad
	 * @param quadHeight height of quad
	 */
	public static void drawTexturedQuad(Texture texture, int quadWidth, int quadHeight){

		GL11.glEnable(GL11.GL_TEXTURE_2D);

		Color.white.bind();
		texture.bind();  //binds texture

		//draws quad and texture coordinates
		GL11.glBegin(GL11.GL_QUADS);

		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2f(0, 0);

		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2f(quadWidth, 0);

		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2f(quadWidth, quadHeight);

		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2f(0, quadHeight);

		GL11.glEnd();

		GL11.glDisable(GL11.GL_TEXTURE_2D);


	}
	
}
