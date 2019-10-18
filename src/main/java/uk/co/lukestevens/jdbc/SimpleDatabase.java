package uk.co.lukestevens.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * A database implementation using string parameters to connect to
 * the database.
 * 
 * @author luke.stevens
 */
public class SimpleDatabase extends AbstractDatabase {

	private final String url;
	private final String username;
	private final String password;

	/**
	 * Creates a new database instance using the required parameters
	 * @param url The url of the database
	 * @param username The username of the user to sue to connect to the database
	 * @param password The password to connect to the database with
	 */
	public SimpleDatabase(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}

	@Override
	protected Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}

}
