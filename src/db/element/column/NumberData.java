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
public class NumberData extends ColumnData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private float value;
	
	private static float nullV = -12341234f;
	
	public NumberData( String name, float value ) {
		this.name = name;
		this.value = value;
		this.type = Database.getInstance().getColumnTypes().getNumberTypeName();
	}
	
	public NumberData( String name, int value ) {
		this( name, (float)value );
	}
	
	public NumberData( String name ) {
		this( name, nullV );
	}
	
	@Override
	public String toString() {
		return toSQL();
	}

	@Override
	public void fromString( String s ) {
		value = Float.parseFloat( s );
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
			ret = Float.toString( value );
		}
		return ret;
	}

	@Override
	public ColumnData clone() {
		return new NumberData( new String( name ), value );
	}

	@Override
	public void fromRS( ResultSet rs ) throws SQLException {
		value = rs.getFloat( name );
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