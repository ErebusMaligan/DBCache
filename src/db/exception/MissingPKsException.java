package db.exception;

/**
 * @author Daniel J. Rivers
 *         2013
 *
 * Created: Dec 31, 2013, 6:11:55 AM 
 */
public class MissingPKsException extends Exception {

	private static final long serialVersionUID = 1L;

	public MissingPKsException( String message ) {
		super( message );
	}	
}