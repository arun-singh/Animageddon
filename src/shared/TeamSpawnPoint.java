package shared;

import org.lwjgl.util.vector.Vector2f;

/**
 * Stores information about a team's spawn/start point.
 * @author Chris, Thomas
 *
 */
public class TeamSpawnPoint {

	private Vector2f position;
	private int teamID;
	
	public TeamSpawnPoint(float x, float y, int teamID) {
		this.position = new Vector2f(x, y);
		this.teamID = teamID;
	}
	
	public Vector2f getPosition() {
		return position;
	}
	
	public int getTeamID() {
		return teamID;
	}
	
}
