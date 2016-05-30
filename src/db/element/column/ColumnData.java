package db.element.column;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.w3c.dom.Node;

/**
 * @author Daniel J. Rivers
 *         2013
 *
 * Created: Sep 18, 2013, 11:18:00 AM 
 */
public abstract class ColumnData implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public static String nullSQL = "NULL";
	
	protected String name;
	
	protected String type;
	
	public abstract Object getValue();
	
	public abstract String toString();
	
	public abstract void fromString( String s );
	
	public abstract Node toXML();
	
	public abstract void fromXML( Node n );
	
	public abstract String toSQL();

	public abstract ColumnData clone();
	
	public abstract void fromRS( ResultSet rs ) throws SQLException;
	
	public abstract void setNull();
	
	public abstract boolean isNull();
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public boolean equals( ColumnData d ) {
//		System.out.println( this.getName() + " : " + d.getName() );
//		System.out.println( this.getType() + " : " + d.getType() );
//		System.out.println( this.getValue() + " : " + d.getValue() );
		return this.getName().equals( d.getName() ) && this.getType().equals( d.getType() ) && this.getValue().equals( d.getValue() );
	}
}