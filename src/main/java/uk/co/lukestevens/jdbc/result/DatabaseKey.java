package uk.co.lukestevens.jdbc.result;

import java.sql.Connection;


/**
 * A simple wrapper to wrap a database connection, and
 * the key returned from an update
 * 
 * @author Luke Stevens
 */
public class DatabaseKey extends AbstractDatabaseResult {
	
	private final int key;

	/**
	 * Creates a new DatabaseKey using the connection and key
	 * @param con The database connection
	 * @param key The key returned from the update query
	 */
	public DatabaseKey(Connection con, int key) {
		super(con);
		this.key = key;
	}

	/**
	 * @return The key returned from the database update query
	 */
	public int getKey() {
		return key;
	}

}
