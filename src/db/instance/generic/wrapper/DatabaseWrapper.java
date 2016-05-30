package db.instance.generic.wrapper;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Daniel J. Rivers
 *         2013
 *
 * Created: Sep 18, 2013, 11:00:44 AM 
 */
public interface DatabaseWrapper {

	public void setName( String name );
	
	public Connection getConnection() throws ClassNotFoundException, SQLException;
}