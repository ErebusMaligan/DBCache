package db.element;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import db.element.column.ColumnData;

/**
 * @author Daniel J. Rivers
 *         2013
 *
 * Created: Sep 18, 2013, 11:20:35 AM 
 */
public class Row implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String name;
	
	private Map<String, ColumnData> values = new HashMap<String, ColumnData>();
	
	public Row( String name, List<ColumnData> values ) {
		this( values );
		this.name = name;
	}
	
	public Row( String name, ColumnData[] values ) {
		this( values );
		this.name = name;
	}
	
	public Row( List<ColumnData> values ) {
		for ( ColumnData c : values ) {
			this.values.put( c.getName(), c );
		}
	}
	
	public Row( ColumnData[] values ) {
		this( Arrays.asList( values ) );
	}
	
	public Map<String, ColumnData> getValues() {
		return values;
	}
	
	public ColumnData getColumn( String name ) {
		return values.get( name );
	}
	
	public void setColumn( String name, ColumnData cd ) {
		values.put( name, cd );
	}
	
	public String toString() {
		String ret = "From table: " + name + "\n\n";
		ArrayList<String> l = new ArrayList<String>( values.keySet() );
		Collections.sort( l );
		for ( String s : l ) {
			ColumnData c = values.get( s );
			ret += c.getName() + " = " + c.getValue() + "\n";
		}
		return ret;
	}
		
	public String getName() {
		return name;
	}
	
	public void setName( String name ) {
		this.name = name;
	}
	
	@Override
	public Row clone() {
		Vector<ColumnData> d = new Vector<ColumnData>();
		for ( String s : values.keySet() ) {
			d.add( values.get( s ).clone() );
		}
		return new Row( new String( name ), d );
	}
}
