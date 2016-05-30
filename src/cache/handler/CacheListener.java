package cache.handler;

import db.element.Row;

/**
 * @author Daniel J. Rivers
 *         2013
 *
 * Created: Oct 2, 2013, 12:30:21 PM 
 */
public interface CacheListener {

	public void created( Row r );

	public void deleted( Row r );

	public void updated( Row r );
		
}
