package uk.co.lukestevens.hibernate;

import uk.co.lukestevens.jdbc.filter.QueryFilter;

import java.util.List;
import java.util.Optional;


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
	Optional<T> get(QueryFilter filter);
	
	/**
	 * @return A list of all objects of this type in the database
	 */
	List<T> list();
	
	/**
	 * @param filter The filter to apply to this query
	 * @return A list of all objects of this type in the database that
	 * match the filter criteria.
	 */
	List<T> list(QueryFilter filter);
	
	/**
	 * Saves an object to the database, or updates it if it
	 * already exists
	 * @param t The object to save
	 */
	void save(T t);
	
	/**
	 * Deletes an object from the database if it exists
	 * @param t The object to delete
	 */
	void delete(T t);

}
