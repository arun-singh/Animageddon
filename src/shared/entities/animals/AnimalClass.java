package shared.entities.animals;

import org.newdawn.slick.opengl.Texture;

/**
 * Defines the properties of an animal class.
 * @author Chris, Thomas
 *
 */
public interface AnimalClass {
	
	public float getSpeed();
	
	public long getMaxHealth();
	
	public String getTextureImage();
		
	public int getWeaponDamage();
	
	public int getWeaponFireDelay();
	
	public float getWeaponRange();

	public String getWeaponImage();

	public String getMuzzleFlashImage();
	
	// width and height in pixels
	public int getWeaponSize();
	
	public int getMuzzleFlashDurationMS();
	
}
