package uk.co.lukestevens.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import uk.co.lukestevens.annotations.SetupConfig;
import uk.co.lukestevens.config.Config;

/**
 * A database implementation using {@link AppConfig}
 * properties to connect to the database.
 * 
 * @author luke.stevens
 */
public class ConfiguredDatabase extends AbstractDatabase {

	private final String url;
	private final String username;
	private final String password;
	
	/**
	 * Creates a new configured database from a set of 
	 * AppConfig properties
	 * @param config The Config instance. The following properties must be specified:
	 * <ul>
	 * 	<li><code>database.url</code></li>
	 * 	<li><code>database.username</code></li>
	 * 	<li><code>database.password</code></li>
	 * </ul>
	 * 
	 */
	public ConfiguredDatabase(@SetupConfig Config config) {
		this.url = config.getAsString("database.url");
		this.username = config.getAsString("database.url");
		this.password = config.getAsString("database.url");
	}

	@Override
	protected Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}



}
