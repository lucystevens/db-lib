package uk.co.lukestevens.jdbc.result;


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
public class WrappedDatabaseResult implements DatabaseResult {

	private final Connection con;
	private final ResultSet rs;
	
	/**
	 * Creates a new DatabaseResult, wrapping
	 * the ResultSet and Connection
	 * @param con The database connection
	 * @param rs the database ResultSet
	 */
	public WrappedDatabaseResult(Connection con, ResultSet rs) {
		this.con = con;
		this.rs = rs;
	}

	@Override
	public ResultSet getResultSet() {
		return rs;
	}

	/**
	 * @return The database connection
	 */
	@Override
	public Connection getConnection() {
		return con;
	}
	
	@Override
	public void processResultSet(ResultSetAction action) throws SQLException, IOException {
		while(rs.next()) {
			action.execute(rs);
		}
	}
	
	@Override
	public <T> List<T> parseResultSet(ResultParser<T> parser) throws SQLException, IOException {
		List<T> list = new ArrayList<>();
		this.processResultSet(rs -> list.add(parser.parse(rs)));
		return list;
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
