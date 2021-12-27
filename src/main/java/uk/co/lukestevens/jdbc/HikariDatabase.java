package uk.co.lukestevens.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public class HikariDatabase extends AbstractDatabase {
    @Override
    public Connection getConnection() throws SQLException {
        return null;
    }
}
