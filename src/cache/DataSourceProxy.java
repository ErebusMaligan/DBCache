package cache;

import cache.handler.CacheHandler;
import datasource.DataSource;

/**
 * @author Daniel J. Rivers
 *         2013
 *
 * Created: Sep 25, 2013, 12:02:42 PM 
 */
public class DataSourceProxy {
	
	private static DataSourceProxy instance;
	
	private Cache cache;
	
	private DataSource ds;
	
	private DataSourceProxy() {
		cache = new Cache();
	}
	
	public static DataSourceProxy getInstance() {
		if ( instance == null ) {
			instance = new DataSourceProxy(); 
		}
		return instance;
	}
	
	public CacheHandler getHandler( String s ) {
		return cache.getHandler( s );
	}
	
	public void setDataSource( DataSource ds ) {
		this.ds = ds;
	}
	
	public DataSource getDataSource() {
		return ds;
	}
	
	public Cache getCache() {
		return cache;
	}
}