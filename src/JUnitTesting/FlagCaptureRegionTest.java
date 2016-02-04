/**
 * @author Richard Maskell 1238287
 */
package JUnitTesting;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import server.ServerWorld;
import shared.Team;
import shared.entities.Flag;
import shared.entities.FlagCaptureRegion;
import shared.entities.Player;

/**
 * Test for the flag capture region
 * @author Richard
 *
 */
public class FlagCaptureRegionTest {

	FlagCaptureRegion testRegion = null;	
	
	ServerWorld serverWorld = null;
	
	Team testTeam = null;
	
	int xPos;
	
	int yPos;
	
	Random random = new Random();
	
	@Before
	public void setUp() throws Exception {
	
		serverWorld = new ServerWorld("maps/original.xml");
		
		testTeam = new Team(serverWorld, 1, "test");
		
		xPos = random.nextInt();
		yPos = random.nextInt();
		
		testRegion = new FlagCaptureRegion(serverWorld, xPos, yPos);
		testRegion.setOwnerTeam(testTeam);
		
		assertNotNull("Checks the region entity was created successfully by the test class' setup method", testRegion);

	}

	@Test
	public void testOnTouch() {
		
		//Create test flag
		xPos = random.nextInt();
		yPos = random.nextInt();
		
		Flag testFlag = new Flag(serverWorld, xPos, yPos);
		
		//Create test player
		xPos = random.nextInt();
		yPos = random.nextInt();
		
		Player testPlayer = new Player(serverWorld, xPos, yPos);
		
		//Add player to team
		testPlayer.setTeam(testTeam);
		testTeam.addPlayer(testPlayer);
		
		//Give player flag
		testPlayer.setHeldFlag(testFlag);	
		
		assertNotNull("The player should initially be holding a flag", testPlayer.getHeldFlag());
		
		testRegion.onTouch(testPlayer);
		
		assertNull("The flag should be removed from the player following onTouch", testPlayer.getHeldFlag());
		
		assertEquals("The score should be 1 following onTouch with a player holding a flag", 1, testTeam.getTeamScore());
		
		
		
	}
	
}
