package db.element;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import db.element.column.ColumnData;

/**
 * @author Daniel J. Rivers
 *         2013
 *
 * Created: Sep 18, 2013, 11:15:24 AM 
 */
public class Table implements Serializable {
	
	private static final long serialVersionUID = 1L;

	protected String name;
	
	protected Row definition;
	
	protected List<String> pks = new Vector<String>();
	
	public Table( String name, List<ColumnData> definition ) {
		this.name = name;
		this.definition = new Row( definition );
	}
	
	public Table( String name, ColumnData[] definition ) {
		this.name = name;
		this.definition = new Row( definition );
	}
	
	public void setPKs( List<String> pks ) {
		this.pks = pks;
	}
	
	public void setPKs( String[] pks ) {
		this.pks = Arrays.asList( pks );
	}
	
	public List<String> getPKs() {
		return pks;
	}
	
	public String getName() {
		return name;
	}
	
	public Row getDefinition() {
		return definition;
	}
	
	public String toCreateSQL() {
		String ret = "(";
		boolean once = false;
		ArrayList<String> l = new ArrayList<String>( definition.getValues().keySet() );
		Collections.sort( l );
		for ( String s : l ) {
		ColumnData c = definition.getValues().get( s );
//		for ( ColumnData c : definition.getValues().values() ) {
			if ( once ) {
				ret += ", ";
			}
			ret += c.getName() + " " + c.getType();
			once = true;
		}
		once = false;
		if ( !pks.isEmpty() ) {
			ret += ", PRIMARY KEY (";
			for ( String s : pks ) {
				if ( once ) {
					ret += ", ";
				}
				ret += s;
				once = true;
			}
			ret += ")";
		}
		ret += ")";
		return ret;
	}
	
	public String toString() {
		return name + "    " + toCreateSQL();
	}
}