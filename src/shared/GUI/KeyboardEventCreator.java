package shared.GUI;

import org.lwjgl.input.Keyboard;

/**
 * Receiving keyboard input
 * @author Thomas
 *
 */
public class KeyboardEventCreator {
	
	private static boolean shiftDown = false;
	private static boolean controlDown = false;
	
	/**
	 * Creates new input event for keyboard
	 * @param key key pressed
	 * @return
	 */
	public static String getKeyboardInputEvent(final int key) {
		String cha = Keyboard.getKeyName(key);
		char[] chaArr = cha.toCharArray();
		if(key == Keyboard.KEY_PERIOD) {
			cha = ".";
		} else if(key == Keyboard.KEY_BACK) {
			cha = "-";
		}
		return cha;
		
	}
	
	//****Getters****

	
	public static boolean isShiftDown(final int key) {
		return Keyboard.isKeyDown(key) && isShiftKey(key);
	}
	
	public static boolean isShiftUp(final int key) {
		return !Keyboard.isKeyDown(key) && isShiftKey(key);
	}

	private static boolean isShiftKey(final int key) {
		return key == Keyboard.KEY_LSHIFT || key == Keyboard.KEY_RSHIFT;
	}
	
	public static boolean isControlDown(final int key) {
		return Keyboard.isKeyDown(key) && isControlKey(key);
	}
	
	public static boolean isControlUp(final int key) {
		return !Keyboard.isKeyDown(key) && isControlKey(key);
	}

	private static boolean isControlKey(final int key) {
	    return key == Keyboard.KEY_RCONTROL || key == Keyboard.KEY_LCONTROL || key == Keyboard.KEY_LMETA || key == Keyboard.KEY_RMETA;
	}
}
