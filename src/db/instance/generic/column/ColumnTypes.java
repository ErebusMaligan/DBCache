package db.instance.generic.column;

import db.element.column.ColumnData;

/**
 * @author Daniel J. Rivers
 *         2013
 *
 * Created: Sep 18, 2013, 11:45:38 AM 
 */
public interface ColumnTypes {
	
	public String getStringTypeName();
	
	public String getNumberTypeName();
	
	public String getIntTypeName();
	
	public ColumnData getTypeByNumber( String name, int num );
	
}
