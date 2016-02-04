/**
 * @author Richard Maskell 1238287
 */
package JUnitTesting;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import shared.entities.Player;
import client.ClientWorld;

/**
 * Test for the moveable entity
 * @author Richard
 *
 */
public class MoveableEntityTest {
	
	Player playerEntity;

	int xPos;

	int yPos;

	Random random = new Random();

	@Before
	public void setUp() {

		ClientWorld clientWorld = new ClientWorld(null);

		xPos = random.nextInt();

		yPos = random.nextInt();

		playerEntity = new Player(clientWorld, xPos, yPos);

		assertNotNull("Checks the player entity was created successfully by the test class' setup method", playerEntity);

	}
	

	@Test
	public void testIsMoveable() {
		
		assertTrue("Checks that the isMoveable method returns true for a moveable entity", playerEntity.isMoveable());
		
	}

}
