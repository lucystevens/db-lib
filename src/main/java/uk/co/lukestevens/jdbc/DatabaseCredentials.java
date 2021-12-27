package uk.co.lukestevens.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * A class to store database credentials
 * 
 * @author luke.stevens
 */
public class DatabaseCredentials {

	private final String url;
	private final String username;
	private final String password;

	/**
	 * Creates a new database credentials instance using the required parameters
	 * @param url The url of the database
	 * @param username The username of the user to use to connect to the database
	 * @param password The password to connect to the database with
	 */
	public DatabaseCredentials(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public DataSource asDataSource(){
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		dataSource.setJdbcUrl(url);
		return dataSource;
	}

}
