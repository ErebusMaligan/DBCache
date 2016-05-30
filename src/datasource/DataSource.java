package datasource;

import db.element.Row;
import db.element.Table;

/**
 * @author Daniel J. Rivers
 *         2014
 *
 * Created: Jan 3, 2014, 11:46:37 PM 
 */
public interface DataSource {
	
	public void insert( Table t, Row r );
	
	public void update( Table t, Row r );
	
	public void delete( Table t, Row r );
	
	public Row get( Table t, Row r );

}