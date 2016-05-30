package cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import cache.handler.CacheHandler;
import db.element.Row;
import db.element.Table;
import db.element.column.ColumnData;
import db.element.utils.RowUtils;

/**
 * @author Daniel J. Rivers
 *         2013
 *
 * Created: Sep 25, 2013, 11:56:08 AM 
 */
public class Cache {

	private Map<String, Table> definitions = new HashMap<String, Table>();

	private Map<String, Vector<Row>> rows = new HashMap<String, Vector<Row>>();

	private static Map<String, CacheHandler> handlers = new HashMap<String, CacheHandler>();
	
	private int tableCount = 0;
	
	private int rowCount = 0;
	
	public void clear() {
		definitions.clear();
		rows.clear();
		tableCount = 0;
		rowCount = 0;
	}

	public void addHandler( String s, CacheHandler handler ) {
		removeHandler( s );
		handlers.put( s, handler );
	}

	public CacheHandler getHandler( String s ) {
		return handlers.get( s );
	}

	public void removeHandler( String s ) {
		CacheHandler h = handlers.get( s );
		if ( h != null ) {
			h.destroyHandler();
		}
		handlers.remove( s );
	}

	public void addTableDefinition( Table t ) {
		definitions.put( t.getName(), t );
		tableCount++;
		if ( handlers.get( t.getName() ) == null ) {
			handlers.put( t.getName(), new CacheHandler( t.getName(), this ) );
		}
	}

	public Table getTableDefinition( String table ) {
		return definitions.get( table );
	}

	public synchronized void mergeRow( Row row, boolean sync ) {
		mergeRow( row.getName(), row, sync );
	}

	public synchronized void mergeRow( String table, Row row, boolean sync ) {
		Table t = definitions.get( table );
		Vector<Row> rs = new Vector<Row>();
		if ( rows.containsKey( table ) ) {
			rs = rows.get( table );
		}
		int i = 0;
		boolean found = false;
		boolean update = false;
		row = RowUtils.completeRow( row, t );
		for ( ; i < rs.size(); i++ ) {
			if ( RowUtils.pkEqual( rs.get( i ), row, t ) ) {
				found = true;
				if ( !RowUtils.valEqual( rs.get( i ), row ) ) {
					update = true;
				}
				break;
			}
		}
		CacheHandler h = handlers.get( table );
		if ( found ) {
			if ( update ) {
				rs.remove( i );
				rs.add( row );
				rows.put( table, rs );
				if ( h != null ) {
					h.updated( row );
					if ( sync ) {
						DataSourceProxy.getInstance().getDataSource().update( t, row );
					}
				}
			}
		} else {
			rs.add( row );
			rows.put( table, rs );
			rowCount++;
			if ( h != null ) {
				h.created( row );
				if ( sync ) {
					DataSourceProxy.getInstance().getDataSource().insert( t, row );
				}
			}
		}

	}

	public synchronized void deleteRow( Row row, boolean sync ) {
		deleteRow( row.getName(), row, sync );
	}

	public synchronized void deleteRow( String table, Row row, boolean sync ) {
		Table t = definitions.get( table );
		Vector<Row> rs = new Vector<Row>();
		if ( rows.containsKey( table ) ) {
			rs = rows.get( table );
		}
		int i = 0;
		boolean found = false;
		for ( ; i < rs.size(); i++ ) {
			if ( RowUtils.pkEqual( rs.get( i ), row, t ) ) {
				found = true;
				break;
			}
		}
		boolean delete = false;
		if ( found ) {
			rowCount--;
			rs.remove( i );
			delete = true;
		}
		rows.put( table, rs );
		CacheHandler h = handlers.get( table );
		if ( h != null ) {
			if ( delete ) {
				h.deleted( row );
				if ( sync ) {
					DataSourceProxy.getInstance().getDataSource().delete( t, row );
				}
			}
		}
	}

	public synchronized Vector<Row> getAllRows( String table ) {
		Vector<Row> ret = new Vector<Row>();
		if ( rows.containsKey( table ) ) {
			Vector<Row> r = rows.get( table );
			for ( Row row : r ) {
				ret.add( row.clone() );		
			}
		}
		return ret;
	}
	
	public synchronized Vector<Row> getRows( String table, List<ColumnData> columns ) {
		Vector<Row> ret = new Vector<Row>();
		if ( rows.containsKey( table ) ) {
			Vector<Row> r = rows.get( table );
			for ( Row row : r ) {
				boolean include = true;
				for ( ColumnData d : columns ) {
//					System.out.println( d );
					if ( row.getColumn( d.getName() ) != null ) {
//						System.out.println( row.getColumn( d.getName() ) );
						if ( !row.getColumn( d.getName() ).equals( d ) ) {
							include = false;
//							System.out.println( "FALSE" );
							break;
						}
					}
				}
				if ( include ) {
					ret.add( row.clone() );
				}
			}
		}
		return ret;
	}

	public Collection<Table> getTables() {
		return definitions.values();
	}

	public void printCache() {
		for ( Table t : definitions.values() ) {
			System.out.println( headerLine() );
			System.out.println( t );
			System.out.println( headerLine() );
			Vector<Row> rs = rows.get( t.getName() );
			if ( rs != null ) {
				for ( Row r : rs ) {
					System.out.println();
					System.out.println( r );
					System.out.println( spacerLine() );
				}
			}
			System.out.println( "\n\n\n" );
		}
	}
	
	public int getTableCount() {
		return tableCount;
	}
	
	public int getRowCount() {
		return rowCount;
	}

	private String headerLine() {
		return "===============================================================";
	}

	private String spacerLine() {
		return "...............................................................";
	}
}