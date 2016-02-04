package shared.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import shared.Entity;
import shared.Team;
import shared.entities.Player;

/**
 * A networked team entity field.
 * @author Chris
 *
 */
public class NetworkedTeam extends NetworkedEntityField<Team> {

	private int teamID = -1;
	
	public NetworkedTeam(Player player, Team teamField) {
		super(player, teamField);
	}

	public NetworkedTeam(Player player) {
		super(player, null);
	}

	@Override
	public void readFromNetStream(DataInputStream in) throws IOException {
		this.teamID = in.readInt();
		
		if (teamID == -1) {
			set(null);
			return;
		}
		
		Team team = this.entity.getWorld().getTeamByID(teamID);
		
		if (team != null)
			set(team);
		else
			System.out.println("null team");
	}
	
	@Override
	public void writeToNetStream(DataOutputStream out) throws IOException {
		if (get() != null)
		out.writeInt(get().getID());
	}
	
	public Team get() {
		Team team = super.get();
		
		if (team != null || teamID == -1)
			return team;
		
		team = this.entity.getWorld().getTeamByID(teamID);
		
		if (team != null) {
			set(team);
			return team;
		}
		
		return null;
	}
	
	public void set(Team team) {
		if (team == null)
			this.teamID = -1;
		else
			this.teamID = team.getID();
		
		super.set(team);
	}

}
