package uk.co.lukestevens.jdbc.result;

import uk.co.lukestevens.db.ResultParser;
import uk.co.lukestevens.db.ResultSetAction;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface DatabaseResult extends Closeable {
    Connection getConnection();

    ResultSet getResultSet();

    void processResultSet(ResultSetAction var1) throws SQLException, IOException;

    <T> List<T> parseResultSet(ResultParser<T> var1) throws SQLException, IOException;
}
