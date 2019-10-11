package uk.co.lukestevens.hibernate;

import java.io.IOException;
import java.util.List;

import uk.co.lukestevens.jdbc.filter.QueryFilter;


/**
 * An interface to be extended by generic daos for
 * fetching objects from the database
 * 
 * @author luke.stevens
 *
 * @param <T> The type of object to fetch
 */
public interface Dao<T> {
	
	/**
	 * @param filter The filter to apply to this query
	 * @return The first object of this type in the database that
	 * matches the filter criteria, or null if none exist
	 * @throws IOException If there is an error connecting to the database
	 */
	public T get(QueryFilter filter) throws IOException;
	
	/**
	 * @return A list of all objects of this type in the database
	 * @throws IOException If there is an error connecting to the database
	 */
	public List<T> list() throws IOException;
	
	/**
	 * @param filter The filter to apply to this query
	 * @return A list of all objects of this type in the database that
	 * match the filter criteria.
	 * @throws IOException If there is an error connecting to the database
	 */
	public List<T> list(QueryFilter filter) throws IOException;
	
	/**
	 * Saves an object to the database, or updates it if it
	 * already exists
	 * @param t The object to save
	 * @throws IOException If there is an error connecting to the database 
	 */
	public void save(T t) throws IOException;
	
	/**
	 * Deletes an object from the database if it exists
	 * @param t The object to delete
	 * @throws IOException If there is an error connecting to the database 
	 */
	public void delete(T t) throws IOException;

}
