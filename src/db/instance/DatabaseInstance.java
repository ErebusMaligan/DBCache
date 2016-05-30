package db.instance;

import db.instance.generic.column.ColumnTypes;
import db.instance.generic.wrapper.DatabaseWrapper;

/**
 * @author Daniel J. Rivers
 *         2013
 *
 * Created: Sep 18, 2013, 11:57:31 AM 
 */
public interface DatabaseInstance {

	public ColumnTypes getColumnTypes();
	
	public DatabaseWrapper getDatabaseWrapper();
}