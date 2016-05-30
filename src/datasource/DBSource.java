package datasource;

import java.sql.Connection;
import java.sql.SQLException;

import cache.DataSourceProxy;
import cache.Scanner;
import db.Database;
import db.element.Row;
import db.element.Table;
import db.exception.MissingPKsException;
import db.instance.DatabaseInstance;
import db.instance.generic.wrapper.TableWrapper;

/**
 * @author Daniel J. Rivers
 *         2014
 *
 * Created: Jan 3, 2014, 11:56:04 PM 
 */
public class DBSource implements DataSource {
	
	private DatabaseInstance db;
	
	private Scanner scan;
	
	public DBSource() {
		scan = new Scanner( DataSourceProxy.getInstance() );
	}
	
	@Override
	public void insert( Table t, Row r ) {
		try {
			TableWrapper.insertRow( t, r, getConnection() );
		} catch ( ClassNotFoundException | SQLException e ) {
			System.err.println( r.toString() );
			e.printStackTrace();
		}
	}
	
	@Override
	public void update( Table t, Row r ) {
		try {
			TableWrapper.updateRow( t, r, getConnection() );
		} catch ( ClassNotFoundException | SQLException | MissingPKsException e ) {
			System.err.println( r.toString() );
			e.printStackTrace();
		}
	}
	
	@Override
	public void delete( Table t, Row r ) {
		try {
			TableWrapper.deleteRow( t, r, getConnection() );
		} catch ( ClassNotFoundException | SQLException | MissingPKsException e ) {
			System.err.println( r.toString() );
			e.printStackTrace();
		}
	}
	
	@Override
	public Row get( Table t, Row r ) {
		Row ret = null;
		try {
			ret = TableWrapper.getRow( t, r, getConnection() );
		} catch ( ClassNotFoundException | SQLException | MissingPKsException e ) {
			System.err.println( r.toString() );
			e.printStackTrace();
		}
		return ret;
	}
	
	public DatabaseInstance getDatabase() {
		return db;
	}
	
	public void createDatabase( DatabaseInstance type )  {
		db = Database.create( type );
	}
	
	public Connection getConnection() throws ClassNotFoundException, SQLException {
		return db.getDatabaseWrapper().getConnection();
	}
	
	public Scanner getScanner() {
		return scan;
	}
	
	public void startScanner( boolean scanData ) {
		scan.scanStructure();
		if ( scanData ) {
			scan.scanData();
		}
	}
	
	public void stopScanner() {
		scan.stop();
	}
}