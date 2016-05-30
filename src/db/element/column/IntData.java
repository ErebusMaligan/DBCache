package db.element.column;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.w3c.dom.Node;

import db.Database;

/**
 * @author Daniel J. Rivers
 *         2013
 *
 * Created: Sep 18, 2013, 11:16:14 AM 
 */
public class IntData extends ColumnData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int value;
	
	private static int nullV = -12341234;
	
	public IntData( String name, int value ) {
		this.name = name;
		this.value = value;
		this.type = Database.getInstance().getColumnTypes().getIntTypeName();
	}
	
	public IntData( String name ) {
		this( name, nullV );
	}
	
	@Override
	public String toString() {
		return toSQL();
	}

	@Override
	public void fromString( String s ) {
		value = Integer.parseInt( s );
	}

	@Override
	public Node toXML() {
		return null;
	}

	@Override
	public void fromXML( Node n ) {
		//not done
	}
	
	@Override
	public Object getValue() {
		Object ret = nullSQL;
		if ( value != nullV ) {
			ret = value;
		}
		return ret;
	}
	
	@Override
	public String toSQL() {
		String ret = nullSQL; 
		if ( value != nullV ) {
			ret = Integer.toString( value );
		}
		return ret;
	}

	@Override
	public ColumnData clone() {
		return new IntData( new String( name ), value );
	}

	@Override
	public void fromRS( ResultSet rs ) throws SQLException {
		value = rs.getInt( name );
		if ( rs.wasNull() ) {
			value = nullV;
		}
	}

	@Override
	public void setNull() {
		value = nullV;
	}

	@Override
	public boolean isNull() {
		return value == nullV;
	}
}