package shared.entities.animals;

import org.newdawn.slick.opengl.Texture;

/**
 * The goat animal class.
 * @author Chris
 *
 */
public class Goat implements AnimalClass {

	@Override
	public float getSpeed() {
		return 5.0f;
	}

	@Override
	public long getMaxHealth() {
		return 150;
	}

	@Override
	public String getTextureImage() {
		return "goat-top";
	}
	
	@Override
	public int getWeaponDamage() {
		return 20;
	}

	@Override
	public int getWeaponFireDelay() {
		return 500;
	}

	@Override
	public float getWeaponRange() {
		return 500;
	}

	@Override
	public String getWeaponImage() {
		return "elephant-gun";
	}

	@Override
	public String getMuzzleFlashImage() {
		return "elephant-gun-mf";
	}
	
	@Override
	public int getWeaponSize() {
		return 16;
	}

	@Override
	public int getMuzzleFlashDurationMS() {
		return 50;
	}

}
 