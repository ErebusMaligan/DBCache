package db.instance.generic.wrapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;

import db.element.Row;
import db.element.Table;
import db.element.column.ColumnData;
import db.element.utils.RowUtils;
import db.exception.MissingPKsException;

/**
 * @author Daniel J. Rivers
 *         2013
 *
 * Created: Sep 18, 2013, 11:00:07 AM 
 */
public class TableWrapper {
	
	public static void create( Table table, Connection c ) throws SQLException {
		create( table, false, c );
	}
	
	public static void create( Table table, boolean drop, Connection c ) throws SQLException {
		Statement s = c.createStatement();
		if ( drop ) {
			s.executeUpdate( "DROP TABLE IF EXISTS " + table.getName() );
		}
		s.executeUpdate( "CREATE TABLE " + table.getName() + " " + table.toCreateSQL() );
	}
	
	public static void insertRow( Row row, Connection c ) throws SQLException {
		if ( row.getName() != null ) {
			insertRow( row.getName(), row, c );
		} else {
			throw new SQLException( "No table name associated with given row" );
		}
	}
	
	public static void insertRow( Table table, Row row, Connection c ) throws SQLException {
		insertRow( table.getName(), row, c );
	}
	
	public static void insertRow( String table, Row row, Connection c ) throws SQLException {
//		System.err.println( "INSERT INTO " + table + RowUtils.getInserString( row ) );
		PreparedStatement s = c.prepareStatement( "INSERT INTO " + table + RowUtils.getInserString( row ) );
		int i = 1;
		for ( ColumnData d : RowUtils.getInsertValues( row ) ) {
			s.setObject( i++, d == null || d.isNull() ? null : d.getValue() );
		}
		s.executeUpdate();
		s.close();
	}

	
	public static void updateRow( Table table, Row row, Connection c ) throws SQLException, MissingPKsException {
//		System.err.println( "UPDATE " + table.getName() + " SET " + RowUtils.getUpdateString( row, table ) );
		PreparedStatement s = c.prepareStatement( "UPDATE " + table.getName() + " SET " + RowUtils.getUpdateString( row, table ) );
		int i = 1;
		for ( ColumnData d : RowUtils.getUpdateValues( row, table ) ) {
				s.setObject( i++, d == null || d.isNull() ? null : d.getValue() );
		}
		System.out.println( s.toString() );
		s.executeUpdate();
		s.close();
	}
	
	public static void deleteRow( Table table, Row row, Connection c ) throws SQLException, MissingPKsException {
//		System.err.println( "DELETE FROM " + table.getName() + " WHERE " + RowUtils.getDeleteString( row, table )  );
		PreparedStatement s = c.prepareStatement( "DELETE FROM " + table.getName() + " WHERE " + RowUtils.getDeleteString( row, table ) );
		int i = 1;
		for ( ColumnData d : RowUtils.getDeleteValues( row, table ) ) {
			s.setObject( i++, d == null || d.isNull() ? null : d.getValue() );
		}
		s.executeUpdate();
		s.close();
	}
	
	public static Row getRow( Table table, Row row, Connection c ) throws SQLException, MissingPKsException {
		Row ret = null;
		PreparedStatement s = c.prepareStatement( "SELECT * FROM " + table.getName() + " WHERE " + RowUtils.getDeleteString( row, table ) );
		int i = 1;
		for ( ColumnData d : RowUtils.getDeleteValues( row, table ) ) {
			s.setObject( i++, d == null || d.isNull() ? null : d.getValue() );
		}
		ResultSet rs = s.executeQuery();
		if ( rs.next() ) {
			ret = RowUtils.getRowFromRS( rs, table );
		}
		rs.close();
		return ret;
	}
	
	public static List<Row> getAllRows( Table table, Connection c ) throws SQLException {
		Vector<Row> rows = new Vector<Row>();
		Statement s = c.createStatement();
		ResultSet rs = s.executeQuery( "SELECT * FROM " + table.getName() );
		while ( rs.next() ) {
			rows.add( RowUtils.getRowFromRS( rs, table ) );
		}
		rs.close();
		return rows;
	}
	
}