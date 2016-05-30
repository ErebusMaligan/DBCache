package db;

import java.sql.ResultSet;
import java.sql.SQLException;

import db.instance.DatabaseInstance;

/**
 * @author Daniel J. Rivers
 *         2013
 *
 * Created: Sep 18, 2013, 11:56:05 AM 
 */
public class Database {

	private static DatabaseInstance instance;
	
	public static DatabaseInstance getInstance() {
		return instance;
	}
	
	public static DatabaseInstance create( DatabaseInstance db ) {
		instance = db;
		return instance;
	}
	
	public static void close( ResultSet rs ) {
		try {
			rs.close();
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
	}
	
}