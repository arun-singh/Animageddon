package JUnitTesting;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import shared.GameWindow;
import shared.Team;
import shared.entities.Flag;
import client.ClientWorld;

/**
 * Test for the client world
 * @author Richard
 *
 */
public class ClientWorldTest {

	GameWindow testWindow = null;
	
	ClientWorld testWorld = null;
	
	@Before
	public void setUp() throws Exception {
		
		testWindow = new GameWindow();

		assertNotNull("Check the game window was created successfully in the test class' setup method", testWindow);
		
		testWorld = new ClientWorld(testWindow);
		
		assertNotNull("Check the client world was created successfully in the test class' setup method", testWorld);		
		
	}

	@Test
	public void testIsClient() {

		assertTrue(testWorld.isClient());
		
	}

	@Test
	public void testIsServer() {
		
		assertFalse(testWorld.isServer());
		
	}

	@Test
	public void testRegisterEntity() {
		
		Flag testEntity = new Flag(testWorld, 0, 0);
		
		assertNull("Check the entity does not already exist in the world's list of entities", testWorld.getEntityByID(testEntity.getID()));
		
		testWorld.registerEntity(testEntity);
		
		assertEquals("Check the entity was added to the world's list of enitities", testEntity, testWorld.getEntityByID(testEntity.getID()));
		
	}

	@Test
	public void testOnWorldFinishedLoading() {

		assertFalse("Check the is loaded method returns false before testing", testWorld.isLoaded());
		
		testWorld.onWorldFinishedLoading();
		
		assertTrue("Check the is loaded method returns true following the on world finished loading method", testWorld.isLoaded());
		
	}

	@Test
	public void testRegisterTeam() {
		
		assertTrue("Check there are no teams before testing", testWorld.getTeams().isEmpty());
		
		Team testTeam = new Team(testWorld, 0, null);
		
		testWorld.registerTeam(testTeam);
		
		assertEquals("Check the team was added to the world's list of teams", testTeam, testWorld.getTeamByID(0)); 
		
	}

}
