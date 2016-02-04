package JUnitTesting;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import shared.Team;
import shared.entities.Player;

import client.ClientWorld;

/**
 * Test for the team
 * @author Richard, Thomas
 *
 */
public class TeamTest {
	
	Team team;
	String teamName;
	ClientWorld clientWorld;
	Player playerEntity;
	
	@Before
	public void setup() {
		clientWorld = new ClientWorld(null);
		teamName = "test";
		team = new Team(clientWorld, 1, teamName);
		assertNotNull("Checks the team was created successfully by the test class' setup method", team);
		
		// Add one player to team
		playerEntity = new Player(clientWorld, 0, 0);
		team.addPlayer(playerEntity);
	}
	
	@Test
	public void testTeam() {
		boolean teamNameTest = (teamName == team.getTeamName());
		assertTrue("Testing the team name was set correctly", teamNameTest);
	}

	@Test
	public void testGetTeamSize() {
		boolean teamSize = (team.getTeamSize() == 1);
		assertTrue("Test the team size calculated is correct", teamSize);
	}

	@Test
	public void testAddScore() {
		team.addScore(playerEntity, 1);
		boolean scoreTest = (team.getTeamScore() == 1);
		assertTrue("Testing that the score is set correctly", scoreTest);
	}

	@Test
	public void testInTeam() {
		// Check that the player is counted as in the team
		assertTrue("Checking playerEntity is in the team", (team.inTeam(playerEntity)));
		
		// Create player not in team & check
		Player playerNotTeam = new Player(clientWorld, 0, 0);
		assertFalse("Checking playerNotTeam is not in the team", (team.inTeam(playerNotTeam)));
	}
}
