package shared;

/**
 * An exception that is thrown when the map file that is being loaded is invalid
 * @author Barney
 */
public class InvalidMapException extends Exception {
	public InvalidMapException(String message) {
		super("InvalidMapException: " + message);
	}
}
