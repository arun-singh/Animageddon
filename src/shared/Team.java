package shared;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Color;

import server.ServerWorld;
import shared.GUI.Components.Label;
import shared.entities.FlagCaptureRegion;
import shared.entities.Player;
import shared.net.NetworkedEntity;
import client.ClientWorld;

/**
 * Stores information about a game team.
 * Contains players on team, team name, team ID, etc.
 * @author Chris, Thomas
 *
 */
public class Team {	
	private static final int TEAM_LABELS_ORIGIN_X = 30;
	private static final int TEAM_LABELS_ORIGIN_Y = 0;
	private static final int TEAM_LABELS_SPACING_Y = 30;
	
	private World world;
	
	private int teamID;
	private String teamName;
	private ArrayList<Player> players;
	//private ArrayList<Score> teamScore;
	private int teamScore;
	private Label scoreLabel;
	private int labelXPos;
	private int labelYPos;
	private String scoreLabelText;
	
	private Vector2f spawnpoint;
	private FlagCaptureRegion flagCaptureRegion;
	
	/**
	 * Constructor for the class.
	 * Sets the team ID and name.
	 */
	public Team(World world, int teamID, String teamName) {
		this.world = world;
		
		this.teamID = teamID;
		
		this.players = new ArrayList<Player>();
		this.teamScore = 0;
		//teamScore = new ArrayList<Score>();
		setTeamName(teamName);
	}
	
	/**
	 * Constructor for the class.
	 * Sets the team ID, name and spawn point.
	 */
	public Team(World world, int teamID, String teamName, Vector2f spawnpoint, FlagCaptureRegion flagCaptureRegion) {
		this(world, teamID, teamName);
		
		this.spawnpoint = spawnpoint;
		this.flagCaptureRegion = flagCaptureRegion;
	}
	
	public Vector2f getSpawnPointCopy() {
		return new Vector2f(this.spawnpoint);
	}
	
	public void createScoreLabel(int xPos, int yPos) {
		
		scoreLabel = new Label(world.getGameWindow(), xPos, yPos, getTeamName() + ": " + getTeamScore(), Color.white, 16f);

	}
	
	/**
	 * Return an ArrayList of the team players
	 * @return Return an ArrayList of the team players
	 */
	public ArrayList<Player> getPlayers() {
		return this.players;
	}
	
	
	
	/**
	 * Add a new player to the team
	 * @param player	The new player.
	 */
	public void addPlayer(Player player) {
		if (player == null)
			throw new NullPointerException("Tried to add null player to team");
		
		this.players.add(player);
		
		player.setTeam(this);
	}
	
	/**
	 * Get the number of players in the team
	 * @return	Returns the team size
	 */
	public int getTeamSize() {
		return this.players.size();
	}
	
	/**
	 * Get a single players contribution to the team
	 * @param player	The player to check
	 * @return			The value of the players score
	 */
	public int getPlayerScore(MoveableEntity player) {
		/*int playerScoreValue = 0;
		for(Score score : teamScore) {
			if(player.equals(score.getPlayer())) {
				playerScoreValue = playerScoreValue + score.getValue();
			}
		}
		return playerScoreValue;*/
		return 0;
	}
	/**
	 * Adds a Score to the team
	 * @param player	The player who made the Score
	 * @param value		The value of the Score
	 */
	public void addScore(MoveableEntity player, int value) {
		//teamScore.add(new Score(player, value));
		setTeamScore(teamScore+value);
	}
	
	/**
	 * Return the value of score the team has.
	 * @return	Returns the team score in a int
	 */
	public int getTeamScore() {
		/*int teamScoreValue = 0;
		for(Score score : teamScore) {
			teamScoreValue = teamScoreValue + score.getValue();
		}
		return teamScoreValue;*/
		
		return this.teamScore;
	}
	
	public void setTeamScore(int teamScore) {
		this.teamScore = teamScore;
		
		if (this.world.isServer()) {
			ServerWorld serverWorld = (ServerWorld)this.world;
			serverWorld.onTeamScoreChanged(this.getID(), this.teamScore);
		} else {
			setScoreLabelText(getTeamName() + ": " + getTeamScore());	
		}
	}
	
	private void setScoreLabelText(String string) {
		this.scoreLabelText = string;
	}

	/**
	 * CHecks to see if a player is in the team. Useful for point scoring
	 * @param playerToCheck	  The player to check
	 * @return				  True if in team, false otherwise
	 */
	public boolean inTeam(Player playerToCheck) {	
		for (Player player : this.players) {
			if (player.equals(playerToCheck)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * The team name
	 * @return	Returns the team name
	 */
	public String getTeamName() {
		return teamName;
	}
	
	/**
	 * CHange the team name
	 * @param teamName	The new team name
	 */
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public Label getScoreLabel() {
		if (scoreLabel == null) {
			createScoreLabel(TEAM_LABELS_ORIGIN_X, TEAM_LABELS_ORIGIN_Y+teamID*TEAM_LABELS_SPACING_Y);
		}
		
		scoreLabel.setLabelText(scoreLabelText);
		
		return scoreLabel;
	}
	
	public int getID() {
		return this.teamID;
	}

	public FlagCaptureRegion getFlagCaptureRegion() {
		return this.flagCaptureRegion;
	}

	public void setFlagCaptureRegion(FlagCaptureRegion flagCaptureRegion) {
		this.flagCaptureRegion = flagCaptureRegion;
	}

	/**
	 * Removes a player from the team.
	 * @param playerEntity player to remove
	 * @return whether or not player was removed from team
	 */
	public boolean removePlayer(Player playerEntity) {
		for (Player player : this.players) {
			if (player == playerEntity) {
				this.players.remove(playerEntity);
				return true;
			}
		}
		
		return false;
	}

}
