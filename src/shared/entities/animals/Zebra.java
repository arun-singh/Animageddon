package shared.entities.animals;

import org.newdawn.slick.opengl.Texture;

/**
 * The zebra animal class.
 * @author Chris
 *
 */
public class Zebra implements AnimalClass {

	@Override
	public float getSpeed() {
		return 3.0f;
	}

	@Override
	public long getMaxHealth() {
		return 100;
	}

	@Override
	public String getTextureImage() {
		return "zebra-top";
	}
	
	@Override
	public int getWeaponDamage() {
		return 10;
	}

	@Override
	public int getWeaponFireDelay() {
		return 500;
	}

	@Override
	public float getWeaponRange() {
		return 1000;
	}

	@Override
	public String getWeaponImage() {
		return "zebra-gun";
	}

	@Override
	public String getMuzzleFlashImage() {
		return "zebra-gun-mf";
	}
	
	@Override
	public int getWeaponSize() {
		return 32;
	}

	@Override
	public int getMuzzleFlashDurationMS() {
		return 50;
	}

}
