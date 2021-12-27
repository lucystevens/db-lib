package uk.co.lukestevens.jdbc.result;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

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
	void execute(ResultSet rs) throws SQLException, IOException;
}
