package uk.co.lukestevens.hibernate

import uk.co.lukestevens.jdbc.filter.QueryFilter

/**
 * An interface to be extended by generic daos for
 * fetching objects from the database
 *
 * @author luke.stevens
 *
 * @param <T> The type of object to fetch
</T> */
interface Dao<T> {
    /**
     * @param filter The filter to apply to this query
     * @return The first object of this type in the database that
     * matches the filter criteria, or null if none exist
     */
    fun fetch(filter: QueryFilter): T?

    /**
     * @return A list of all objects of this type in the database
     */
    fun list(): List<T>

    /**
     * @param filter The filter to apply to this query
     * @return A list of all objects of this type in the database that
     * match the filter criteria.
     */
    fun list(filter: QueryFilter): List<T>

    /**
     * Saves an object to the database, or updates it if it
     * already exists
     * @param t The object to save
     */
    fun save(t: T)

    /**
     * Deletes an object from the database if it exists
     * @param t The object to delete
     */
    fun delete(t: T)
}