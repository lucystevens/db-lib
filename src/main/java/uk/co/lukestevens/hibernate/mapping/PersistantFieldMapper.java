package uk.co.lukestevens.hibernate.mapping;

import java.io.IOException;

/**
 * An interface to define a persistent field mapper with methods to
 * be applied to objects before they are persisted to the database and
 * after they are fetched.
 * 
 * @author luke.stevens
 */
public interface PersistantFieldMapper {
	
	/**
	 * Map fields on this object to be stored in the database.<br>
	 * <i>Note: This method should be the direct inverse of {@link #postFetch(Object)}.
	 * e.g. x = postFetch(prePersist(x));
	 * @param o The object to map
	 * @throws IOException If there is an exception whilst mapping the object
	 */
	public void prePersist(Object o) throws IOException;
	
	/**
	 * Map fields on this object after being retrieved from the database<br>
	 * <i>Note: This method should be the direct inverse of {@link #prePersist(Object)}.
	 * e.g. x = prePersist(postFetch(x));
	 * @param o The object to map
	 * @throws IOException If there is an exception whilst mapping the object
	 */
	public void postFetch(Object o) throws IOException;

}
