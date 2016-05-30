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
public class StringData extends ColumnData {

	private static final long serialVersionUID = 1L;

	protected String value;
	
	public StringData( String name ) {
		this( name, nullSQL );
	}
	
	public StringData( String name, String value ) {
		this.name = name;
		this.value = value == null ? nullSQL : value;
		this.type = Database.getInstance().getColumnTypes().getStringTypeName();
	}
	
	@Override
	public String toString() {
		String ret = nullSQL; 
		if ( !value.equals( nullSQL ) ) {
			ret = value;
		}
		return ret;
	}

	@Override
	public void fromString( String s ) {
		value = s;
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
		if ( !value.equals( nullSQL ) ) {
			ret = value;
		}
		return ret;
	}
	
	@Override
	public String toSQL() {
		String ret = nullSQL; 
		if ( !value.equals( nullSQL ) ) {
			ret = "'" + value + "'";
		}
		return ret;
	}

	@Override
	public ColumnData clone() {
		return new StringData( new String( name ), new String( value ) );
	}

	@Override
	public void fromRS( ResultSet rs ) throws SQLException {
		value = rs.getString( name );
		if ( rs.wasNull() ) {
			value = nullSQL;
		}
	}

	@Override
	public void setNull() {
		value = nullSQL;
	}

	@Override
	public boolean isNull() {
		return value == nullSQL;
	}	
}