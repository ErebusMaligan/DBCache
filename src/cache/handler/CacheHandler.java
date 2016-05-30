package cache.handler;

import java.util.List;
import java.util.Vector;

import cache.Cache;
import cache.DataSourceProxy;
import db.element.Row;
import db.element.column.ColumnData;

/**
 * @author Daniel J. Rivers
 *         2013
 *
 * Created: Oct 2, 2013, 1:31:17 PM 
 */
public class CacheHandler implements CacheListener {
	
	private Vector<CacheListener> listeners = new Vector<CacheListener>();
	
	private String name;
	
	private Cache cache;
	
	public CacheHandler( String name, Cache cache ) {
		this.name = name;
		this.cache = cache;
	}
	
	@Override
	public synchronized void created( Row r ) {
		for ( CacheListener l : listeners ) {
			l.created( r );
		}
	}

	@Override
	public synchronized void deleted( Row r ) {
		for ( CacheListener l : listeners ) {
			l.deleted( r );
		}
	}
	
	@Override
	public synchronized void updated( Row r ) {
		for ( CacheListener l : listeners ) {
			l.updated( r );
		}
	}
	
	public String getName() {
		return name;
	}
	
	public Vector<Row> getAllRows() {
		return DataSourceProxy.getInstance().getCache().getAllRows( name );
	}
	
	public Vector<Row> getRows( List<ColumnData> columns ) {
		return DataSourceProxy.getInstance().getCache().getRows( name, columns );
	}
	
	public Row getRow( List<ColumnData> columns ) {
		List<Row> rows = getRows( columns );
		return rows.size() > 0 ? rows.get( 0 ) : null;
	}
	
	public synchronized void destroyHandler() {
		listeners.clear();
	}
	
	public synchronized void addListener( CacheListener l ) {
		listeners.add( l );
	}
	
	public synchronized void removeListener( CacheListener l ) {
		listeners.remove( l );
	}
	
	public void merge( Row r ) {
		r.setName( name );
		cache.mergeRow( name, r, true );
	}
	
	public void delete( Row r ) {
		r.setName( name );
		cache.deleteRow( name, r, true );
	}
	
	public List<ColumnData> getColumnData( String columnName, boolean allowDuplicates ) {
		List<ColumnData> ret = new Vector<>();
		for ( Row r : getAllRows() ) {
			boolean add = true;
			ColumnData cd = r.getColumn( columnName );
			if ( cd != null ) {
				if ( !allowDuplicates ) {
					for ( ColumnData d : ret ) {
						if ( d != null ) {
							if ( d.getValue().equals( cd.getValue() ) ) {
								add = false;
								break;
							}
						} else {
							add = false;
						}
					}
				}
				if ( add ) {
					ret.add( cd );
				}
			}
		}
		return ret;
	}
}