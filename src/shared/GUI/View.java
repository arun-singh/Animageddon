package shared.GUI;

import org.newdawn.slick.Color;

/**
 * Class to be inherited for the menu views
 * @author Thomas
 *
 */
public class View {
	
	
	//****Class Variables****
	
	
	protected static Color backgroundColour = new Color(0.235f, 0.702f, 0.443f);
	protected static Color hoverColour = new Color(0.408f, 0.776f, 0.435f);
	
	
	//****Class methods****
	
	/**
	 * Called in main game loop to render view
	 */
	public void render() {}

	/**
	 * Called in main game loop to update view
	 * @param delta time since last frame
	 */
	public void update(int delta) {}

}
