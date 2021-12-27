package uk.co.lukestevens.hibernate;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
	 */
	public Optional<T> get(QueryFilter filter);
	
	/**
	 * @return A list of all objects of this type in the database
	 */
	public List<T> list();
	
	/**
	 * @param filter The filter to apply to this query
	 * @return A list of all objects of this type in the database that
	 * match the filter criteria.
	 */
	public List<T> list(QueryFilter filter);
	
	/**
	 * Saves an object to the database, or updates it if it
	 * already exists
	 * @param t The object to save
	 */
	public void save(T t);
	
	/**
	 * Deletes an object from the database if it exists
	 * @param t The object to delete
	 */
	public void delete(T t);

}
