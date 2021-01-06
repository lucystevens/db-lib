package uk.co.lukestevens.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.inject.Inject;

import uk.co.lukestevens.annotations.SetupConfig;
import uk.co.lukestevens.config.Config;

/**
 * A database implementation using {@link AppConfig}
 * properties to connect to the database.
 * 
 * @author luke.stevens
 */
public class ConfiguredDatabase extends AbstractDatabase {

	private final Config config;
	
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
	@Inject
	public ConfiguredDatabase(@SetupConfig Config config) {
		this.config = config;
	}

	@Override
	protected Connection getConnection() throws SQLException {
		return DriverManager.getConnection(
				config.getAsString("database.url"), 
				config.getAsString("database.username"), 
				config.getAsString("database.password"));
	}



}
