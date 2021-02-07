package uk.co.lukestevens.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Date;
import java.util.Optional;

import uk.co.lukestevens.db.Database;
import uk.co.lukestevens.db.DatabaseResult;
import uk.co.lukestevens.jdbc.result.WrappedDatabaseResult;

/**
 * A further implementation of the Database interface
 * that only leaves the connection to be defined by
 * overriding classes.
 * 
 * @author luke.stevens
 */
public abstract class AbstractDatabase implements Database {
	
	/**
	 * @return Gets a connection to the database
	 * @throws SQLException If a database error occurs
	 */
	protected abstract Connection getConnection() throws SQLException;

	/**
	 * Prepare a parameterised statement from a raw SQL
	 * query and parameters
	 * @param conn The database connection this statement will be 
	 * used for.
	 * @param query The SQL query to prepare
	 * @param params A variable array of parameters to substitute
	 * into the SQL query
	 * @return A prepared statement that can be used to query the database
	 * @throws SQLException If a database error occurs or this method is
	 * called with a closed connection
	 */
	protected PreparedStatement prepareStatement(Connection conn, String query, Object...params) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		for (int i = 0; i < params.length; i++) {
			Object o = params[i];
			if(o != null && o.getClass().isEnum()) {
				o = o.toString();
			}
			
			if(Date.class.isInstance(o)) {
				stmt.setObject(i + 1, o, Types.TIMESTAMP);
			}
			else {
				stmt.setObject(i + 1, o);
			}
		}
		return stmt;
	}

	@Override
	public DatabaseResult query(String query, Object...params) throws SQLException {
		Connection conn = this.getConnection();
		PreparedStatement stmt = prepareStatement(conn, query, params);
		ResultSet rs = stmt.executeQuery();
		return new WrappedDatabaseResult(conn, rs);
	}

	@Override
	public Optional<Long> update(String query, Object...params) throws SQLException {
		Connection conn = this.getConnection();
		PreparedStatement stmt = prepareStatement(conn, query, params);
		stmt.executeUpdate();
		
		ResultSet rs = stmt.getGeneratedKeys();
		return rs.next()?
				Optional.of(rs.getLong(1)) :
				Optional.empty();
	}

}
