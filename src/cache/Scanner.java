package cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import datasource.DBSource;
import db.Database;
import db.element.Row;
import db.element.Table;
import db.element.column.ColumnData;
import db.element.utils.RowUtils;
import db.instance.generic.wrapper.TableWrapper;

/**
 * @author Daniel J. Rivers
 *         2013
 *
 * Created: Sep 25, 2013, 11:57:55 AM 
 */
public class Scanner {
	
	private DataSourceProxy proxy;
	
	private String schema = null;
	
	private boolean stop = false;
	
	public Scanner( DataSourceProxy proxy ) {
		this.proxy = proxy;
	}
	
	public void scanStructure() {
		ResultSet rs = null;
		try {
			Connection c = ( (DBSource)proxy.getDataSource() ).getConnection();
			rs = c.getMetaData().getTables( null, schema, "%", new String[] { "TABLE" } );
			while ( rs.next() ) {
				String tableName = rs.getString( "TABLE_NAME" );
//				System.out.println( "T: " + tableName );
				Vector<String> pk = new Vector<String>();
				Vector<ColumnData> columns = new Vector<ColumnData>();
				
				//pks
				ResultSet p =( (DBSource)proxy.getDataSource() ).getConnection().getMetaData().getPrimaryKeys( null, schema, tableName );
				while ( p.next() ) {
					String columnName = p.getString( "COLUMN_NAME" ).toUpperCase();
					pk.add( columnName );
//					System.out.println( "C: " + columnName );
				}
				Database.close( p );
				
				//all columns
				ResultSet cr = c.getMetaData().getColumns( null, schema, tableName, "%" );
				while ( cr.next() ) {
					columns.add( ( (DBSource)proxy.getDataSource() ).getDatabase().getColumnTypes().getTypeByNumber( cr.getString( "COLUMN_NAME" ), cr.getInt( "DATA_TYPE" ) ) );
				}
				Database.close( cr );
				
				Table t = new Table( tableName, columns );
				t.setPKs( pk );
				proxy.getCache().addTableDefinition( t );
			}
			c.close();
		} catch ( ClassNotFoundException | SQLException e ) {
			e.printStackTrace();
		}
	}
	
	public synchronized void scanData() {
		List<Thread> threads = new ArrayList<Thread>();
    	for ( Table t : proxy.getCache().getTables() ) {
    		threads.add( new Thread( () -> {
//    			System.out.println( t.getName() + " scan started" );
    			try {
	        		List<Row> allRows = TableWrapper.getAllRows( t, ( (DBSource)proxy.getDataSource() ).getConnection() );	
	        		//handle adds and updates
	        		for ( Row r : allRows ) {
						proxy.getCache().mergeRow( r, false );
					}
	        		//handle deletes - TODO: Test this optimization
	//				Vector<Row> rem = new Vector<Row>();
					for ( Row r : proxy.getCache().getAllRows( t.getName() ) ) {
						boolean exists = false;
						for ( Row x : allRows ) {
							if ( RowUtils.pkEqual( x, r, t ) ) {
								exists = true;
								break;
							}
						}
						if ( !exists ) {
	//						rem.add( r );
							proxy.getCache().deleteRow( r, false );
						}
					}
	//				for ( Row r : rem ) {
	//					proxy.getCache().deleteRow( r, false );
	//				}
    			} catch ( ClassNotFoundException | SQLException e ) {
    				e.printStackTrace();
    			}
//    			System.out.println( t.getName() + " scan finished" );
        	} ) );
    	}
    	threads.forEach( t -> t.start() );
    	try {
			for ( Thread t : threads ) { t.join(); };
//			System.out.println( "Scan threads joined" );
		} catch ( InterruptedException e ) {
			e.printStackTrace();
		}
	}
	
	public void startPolling( final long rate ) {
		new Thread() {
			public void run() {
				while( !stop ) {
					try {
						Thread.sleep( rate );
					} catch ( InterruptedException e ) {
						e.printStackTrace();
					}
					scanData();
				}
			}
		}.start();
	}
	
	public void stop() {
		stop = true;
	}
}