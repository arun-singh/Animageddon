package JUnitTesting;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import server.ServerWorld;
import shared.GameWindow;

/**
 * Test for the server world
 * @author Richard
 *
 */
public class ServerWorldTest {

	GameWindow testWindow = null;
	
	ServerWorld testWorld = null;
	
	
	@Before
	public void setUp() throws Exception {
		
		testWindow = new GameWindow();
		
		testWorld = new ServerWorld("maps/original.xml", testWindow);
		
	}

	@Test
	public void testIsClient() {
		assertFalse("Check the is client method returns false for the server world", testWorld.isClient());
	}

	@Test
	public void testIsServer() {
		assertTrue("Check the is server method returns true for the server world", testWorld.isServer());
	}

}
