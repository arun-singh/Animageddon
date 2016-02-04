package shared.entities.animals;

import org.newdawn.slick.opengl.Texture;

/**
 * The elephant animal class.
 * @author Chris
 *
 */
public class Elephant implements AnimalClass {

	@Override
	public float getSpeed() {
		return 2.0f;
	}

	@Override
	public long getMaxHealth() {
		return 300;
	}

	@Override
	public String getTextureImage() {
		return "elephant-top";
	}
	
	@Override
	public int getWeaponDamage() {
		return 50;
	}

	@Override
	public int getWeaponFireDelay() {
		return 500;
	}

	@Override
	public float getWeaponRange() {
		return 200;
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
		return 25;
	}

	@Override
	public int getMuzzleFlashDurationMS() {
		return 100;
	}


}
