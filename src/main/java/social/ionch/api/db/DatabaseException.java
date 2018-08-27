package social.ionch.api.db;

/**
 * Exception which indicates that something went wrong with database access.
 */
public class DatabaseException extends Exception {
	private static final long serialVersionUID = 6377236655555739690L;
	public DatabaseException() {}
	public DatabaseException(String message) { super(message); }
	public DatabaseException(Throwable cause) { super(cause); }
	public DatabaseException(String message, Throwable cause) { super(message, cause); }
}
