package shared.net;

/**
 * Defines constant values used for networking.
 * @author Chris
 *
 */
public class NetworkConstants {

	// TEMP: server address used for "Join Game"
//	public static final String HHHH = "127.0.0.1";
	
	// Port the server listens on
	public static final int SERVER_PORT = 56500;

	// Interval in milliseconds between client->server mouse position messages
	// Smaller interval gives more precise gun orientation but adds more bandwidth
	public static final long MOUSE_POSITION_SEND_INTERVAL_MS = 10;

	// Time to wait before displaying network movement
	// Used to account for network delays
	public static final long NET_MOVEMENT_DELAY_MS = 300;

	// Threshold value used for immediate position correction
	// rather than waiting until the next velocity change
	public static final float PREDICTION_ERROR_THRESHOLD = 30.0f;

	// Maximum pending velocity values a client can hold when displaying
	// past movement
	public static final int MAX_PENDING_VELOCITIES = 10;

	// Distance from goal position that is considered as negligible
	public static final float VELOCITY_INFO_GOAL_DISTANCE_THRESHOLD = 1;

	// Enables or disables client-side prediction for the local player
	public static final boolean LOCAL_PLAYER_PREDICTION_ENABLED = true;

	// Server -> client message types
	public enum S2C {
		CREATE_ENTITIES,
		DELETE_ENTITIES,
		UPDATE_ENTITY_FIELD,
		SET_PLAYER_ID,
		SET_BACKGROUND_TEXTURE,
		SET_SERVER_TIME,
		CREATE_TEAM,
		TEAM_SCORE_CHANGE,
	};
	
	// Client -> server message types
	public enum C2S {
		CONNECT,
		KEYBOARD_INPUT,
		GUN_SHOT_INFO,
		MOUSE_POSITION,
	};
	
}
