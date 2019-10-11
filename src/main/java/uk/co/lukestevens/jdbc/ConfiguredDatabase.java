package uk.co.lukestevens.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import uk.co.lukestevens.config.Config;
import uk.co.lukestevens.encryption.EncryptionService;

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
	 * @param config The Config instance
	 * @param encryption The encryption service used to decrypt the 
	 * database password
	 * @param dbAlias an alias used to determine which properties should be 
	 * fetched for this database. The following properties must be specified:
	 * <ul>
	 * 	<li><code><i>dbAlias</i>.db.url</code></li>
	 * 	<li><code><i>dbAlias</i>.db.username</code></li>
	 * 	<li><code><i>dbAlias</i>.db.password</code> (encrypted)</li>
	 * </ul>
	 * 
	 */
	public ConfiguredDatabase(Config config, EncryptionService encryption, String dbAlias) {
		this.url = config.getAsString(dbAlias + ".db.url");
		this.username = config.getAsString(dbAlias + ".db.username");
		this.password = config.getEncrypted(dbAlias + ".db.password", encryption);
	}

	@Override
	protected Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}



}
