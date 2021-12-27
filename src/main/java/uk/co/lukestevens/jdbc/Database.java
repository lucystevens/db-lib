package uk.co.lukestevens.jdbc;

import uk.co.lukestevens.db.DatabaseResult;

import java.sql.SQLException;
import java.util.Optional;

/**
 * An interface representing a raw database on
 * which queries can be called
 * 
 * @author luke.stevens
 */
public interface Database {
	
	/**
	 * Query the database to get a result
	 * @param query The SQL query to use
	 * @param params A variable array of parameters to substitute
	 * into the SQL query
	 * @return A database result wrapping both the connection and resultset
	 * @throws SQLException If there is an exception querying the database
	 */
	public DatabaseResult query(String query, Object...params) throws SQLException;

	/**
	 * Send an update query to the database
	 * @param query The SQL query to use
	 * @param params A variable array of parameters to substitute
	 * into the SQL query
	 * @return The id of the inserted or updated object
	 * @throws SQLException If there is an exception querying the database
	 */
	public Optional<Long> update(String query, Object...params) throws SQLException;

}
