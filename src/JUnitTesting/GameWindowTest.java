package JUnitTesting;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import shared.GameWindow;

/**
 * Test for the game window
 * @author Richard
 *
 */
public class GameWindowTest {
	
	GameWindow testWindow = null;

	@Before
	public void setUp() throws Exception {
		
		testWindow = new GameWindow();
		
		assertNotNull("Check the game window was created successfully in the set up method of the test class", testWindow);
		
	}

	@Test
	public void testCreateGame() {
		
		assertNull("The server world should be null before testing", testWindow.getServerWorld());
		
		testWindow.createGame();
		
		assertNotNull("The server world should be initialised following the create game method", testWindow.getServerWorld());
		
	}

	@Test
	public void testMouseToScreenY() {
		
		Random random = new Random();
		
		int mouseY = random.nextInt();
		
		int screenY = testWindow.getHeight() - mouseY;

		assertEquals("The screen-Y should be the screen height minus the mouse-Y", screenY, testWindow.mouseToScreenY(mouseY));
		
	}

	@Test
	public void testConnectServer() throws ConnectException, IOException {
		
		assertNull("The client world should be null before testing", testWindow.getClientWorld());
		assertNull("The network client should be null before testing", testWindow.getNetworkClient());
		
		testWindow.connectServer("127.0.0.1", 56500);
		
		assertNotNull("The client world should be initialised following the connect server method", testWindow.getClientWorld());
		assertNotNull("The network client should be initialised following the connect server method", testWindow.getNetworkClient());
		
	}

}
