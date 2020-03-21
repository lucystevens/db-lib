package uk.co.lukestevens.hibernate;

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
	 */
	public <T> Dao<T> getDao(Class<T> type);

}
