package shared.GUI;

import org.lwjgl.input.Keyboard;

/**
 * Receiving keyboard input
 * @author Thomas
 *
 */
public class JoinMenuTextInput {
	
	private JoinMenu joinMenu;

	/**
	 * Registers input for IP input
	 * @param joinMenu JoinMenu access
	 */
	public JoinMenuTextInput(JoinMenu joinMenu) {
		this.joinMenu = joinMenu;
	}
	
	/**
	 * Called each time key is pressed
	 */
	public void update() {
		while(Keyboard.next()) {
			if(Keyboard.getEventKeyState()) {
				int key = Keyboard.getEventKey();
				String cha = KeyboardEventCreator.getKeyboardInputEvent(key);
				char[] chaArr = cha.toCharArray();
				if(!Character.isDigit(chaArr[0]) && !(cha == ".") && !(cha == "-")) {
					cha = "";
				}
				//update IP
				String currentText = this.joinMenu.getIPLabel().getLabelText();
				//check if valid IP
				if(currentText.length() == 45) {
					continue;
				}
				
				
				String newText;
				if(cha == "-") {
					//backspace
					if(currentText.length() != 0) {
						newText = currentText.substring(0, currentText.length() - 1);
					} else {
						newText = "";
					}
				} else {
					//add to string
					newText = currentText + cha;
				}
				//set new IP
				this.joinMenu.getIPLabel().setLabelText(newText);
			}
		}
	}
}
