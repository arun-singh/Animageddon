package shared.net;

import shared.Entity;
import shared.entities.Player;

/**
 * A networked player entity entity field.
 * @author Chris
 *
 */
public class NetworkedPlayer extends NetworkedEntity {

	public NetworkedPlayer(Entity entity, Player playerField) {
		super(entity, playerField);
	}
	
	public NetworkedPlayer(Entity entity) {
		super(entity, null);
	}
	
	public Player get() {
		return (Player)super.get();
	}

}
