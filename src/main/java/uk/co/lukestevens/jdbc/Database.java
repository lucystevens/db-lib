package uk.co.lukestevens.jdbc;

import java.sql.SQLException;

import uk.co.lukestevens.jdbc.result.DatabaseResult;

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
	 * @return Either the id of the inserted object or 0 if no appropriate value should be returned
	 * @throws SQLException If there is an exception querying the database
	 */
	public int update(String query, Object...params) throws SQLException;

}
