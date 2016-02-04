/**
 * @author Richard Maskell 1238287
 */
package JUnitTesting;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

import client.ClientWorld;
import shared.entities.Player;

/**
 * Test for the entity class
 * @author Richard
 *
 */
public class EntityTest {
	
	Player playerEntity;	
	
	int xPos;
	
	int yPos;
	
	Random random = new Random();
	
	@Test
	public void testPlayerWorldIntInt() {
		
		ClientWorld clientWorld = new ClientWorld(null);
		
		xPos = random.nextInt();
		
		yPos = random.nextInt();
		
		playerEntity = new Player(clientWorld, xPos, yPos);
		
		assertNotNull("Checks the player entity was created successfully by the test class' setup method", playerEntity);

	}


}
