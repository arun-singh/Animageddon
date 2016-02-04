package shared;

/**
 * Saves the details of each point
 * @author Thomas
 *
 */
public class Score {
	
	private int value;
	private MoveableEntity player;

	/**
	 * Constructor for the class
	 * @param player	The player who made the Score
	 * @param value		The value of the Score
	 */
	public Score(MoveableEntity player, int value) {
		this.setPlayer(player);
		this.setValue(value);
	}

	/**
	 * Get the score value
	 * @return The score
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Set the score value
	 * @param value
	 */
	public void setValue(int value) {
		this.value = value;
	}

	public MoveableEntity getPlayer() {
		return player;
	}

	public void setPlayer(MoveableEntity player) {
		this.player = player;
	}

}
