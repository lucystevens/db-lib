package uk.co.lukestevens.jdbc.result;


import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * An ease-of-use wrapper class that encompasses
 * both the result set and connection, allowing for
 * easy result parsing and use of try-with-resources
 * statements.
 * 
 * @author luke.stevens
 */
public class DatabaseResult implements Closeable {
	
	/**
	 * A functional interface representing an
	 * action to be performed on a simple element
	 * in a resultset.
	 * 
	 * @author luke.stevens
	 *
	 */
	@FunctionalInterface
	public interface ResultSetAction{
		public void execute(ResultSet rs) throws SQLException, IOException;
	}
	
	public Connection con;
	public ResultSet rs;
	
	/**
	 * Creates a new DatabaseResult, wrapping
	 * the ResultSet and Connection
	 * @param con The database connection
	 * @param rs the database ResultSet
	 */
	public DatabaseResult(Connection con, ResultSet rs) {
		this.con = con;
		this.rs = rs;
	}
	
	/**
	 * @return The database connection
	 */
	public Connection getConnection() {
		return con;
	}

	/**
	 * @return The database result set
	 */
	public ResultSet getResultSet() {
		return rs;
	}

	@Override
	public void close() throws IOException {
		try {
			con.close();
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}
	
	/**
	 * Processes this result by executing the given
	 * action on each element of the database result set.
	 * @param action The action to execute
	 * @throws SQLException if the result set has already been closed
	 * @throws IOException if there is an error during the action's execution
	 */
	public void processResultSet(ResultSetAction action) throws SQLException, IOException {
		while(rs.next()) {
			action.execute(rs);
		}
	}
	
	/**
	 * Parses this result using the given parser to convert
	 * each result set element into an object
	 * @param parser The parser to use to parse the result set
	 * @return A list of parsed objects
	 * @throws SQLException if the result set has already been closed
	 * @throws IOException if there is an error during the parser's execution
	 */
	public <T> List<T> parseResultSet(ResultParser<T> parser) throws SQLException, IOException {
		List<T> list = new ArrayList<>();
		this.processResultSet(rs -> list.add(parser.parse(rs)));
		return list;
	}

}
