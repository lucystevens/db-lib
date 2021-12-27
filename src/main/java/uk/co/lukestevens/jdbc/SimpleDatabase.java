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

	private final DatabaseCredentials databaseCredentials;

	/**
	 * Creates a new database instance using database credentials
	 * @param databaseCredentials databaseCredentials
	 */
	public SimpleDatabase(DatabaseCredentials databaseCredentials) {
		this.databaseCredentials = databaseCredentials;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(
				databaseCredentials.getUrl(),
				databaseCredentials.getUsername(),
				databaseCredentials.getPassword());
	}

}
