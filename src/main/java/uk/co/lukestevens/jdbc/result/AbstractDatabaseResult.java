package uk.co.lukestevens.jdbc.result;


import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * An abstract wrapper class that encompasses
 * a database connection, allowing for
 * easy result parsing and use of try-with-resources
 * statements.
 * 
 * @author luke.stevens
 */
public abstract class AbstractDatabaseResult implements Closeable {
	
	final Connection con;
	
	/**
	 * Creates a new AbstractDatabaseResult, wrapping
	 * the database Connection
	 * @param con The database connection
	 */
	public AbstractDatabaseResult(Connection con) {
		this.con = con;
	}
	
	/**
	 * @return The database connection
	 */
	public Connection getConnection() {
		return con;
	}

	@Override
	public void close() throws IOException {
		try {
			con.close();
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

}
