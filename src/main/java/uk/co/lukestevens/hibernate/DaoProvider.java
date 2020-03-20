package uk.co.lukestevens.hibernate;

import java.io.IOException;

/**
 * Interface defining a provider used for getting DAOs for classes
 * 
 * @author Luke Stevens
 */
public interface DaoProvider {
	
	/**
	 * Construct a new DAO for a class of object
	 * @param type The object type to construct the dao for
	 * @return A new DAO
	 * @throws IOException If the session factory cannot be built
	 */
	public <T> Dao<T> getDao(Class<T> type) throws IOException;

}
