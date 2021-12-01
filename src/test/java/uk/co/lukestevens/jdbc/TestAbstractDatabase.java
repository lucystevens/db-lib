package uk.co.lukestevens.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.co.lukestevens.db.DatabaseResult;

public class TestAbstractDatabase {
	
	static enum TestEnum {
		ENUM1,
		ENUM2,
		ENUM3
	}
	
	static DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	ResultSet rs = mock(ResultSet.class);
	PreparedStatement stmt = mock(PreparedStatement.class);
	Connection connection = mock(Connection.class);
	AbstractDatabase database = mock(AbstractDatabase.class, CALLS_REAL_METHODS);
	
	@BeforeEach
	public void setup() throws SQLException {
		when(database.getConnection()).thenReturn(connection);
		when(connection.prepareStatement("SELECT 1", Statement.RETURN_GENERATED_KEYS))
			.thenReturn(stmt);
	}
	
	
	@Test
	public void testPrepareStatement() throws SQLException, ParseException {
		Date date = df.parse("2021/02/14 21:05:34");
		PreparedStatement statement = database
				.prepareStatement(connection, "SELECT 1", 37, TestEnum.ENUM2, "astring", date);
		
		verify(stmt).setObject(1, 37);
		verify(stmt).setObject(2, "ENUM2");
		verify(stmt).setObject(3, "astring");
		verify(stmt).setObject(4, date, Types.TIMESTAMP);
		assertEquals(statement, stmt);
	}
	
	public void testPrepareStatementNoParams() throws SQLException, ParseException {
		PreparedStatement statement = database
				.prepareStatement(connection, "SELECT 1");
		
		verify(stmt, never()).setObject(any(), any());
		assertEquals(statement, stmt);
	}
	
	@Test
	public void testQuery() throws SQLException {
		when(stmt.executeQuery()).thenReturn(rs);
		DatabaseResult dbr = database.query("SELECT 1");
		assertEquals(connection, dbr.getConnection());
		assertEquals(rs, dbr.getResultSet());
	}
	
	@Test
	public void testUpdateWithGeneratedKey() throws SQLException {
		when(stmt.getGeneratedKeys()).thenReturn(rs);
		when(rs.next()).thenReturn(true);
		when(rs.getLong(1)).thenReturn(19L);
		
		Optional<Long> key = database.update("SELECT 1");
		assertEquals(19, key.get());
		
		verify(stmt).executeUpdate();
		verify(connection).close();
	}
	
	@Test
	public void testUpdateWithoutGeneratedKey() throws SQLException {
		when(stmt.getGeneratedKeys()).thenReturn(rs);
		when(rs.next()).thenReturn(false);
		
		Optional<Long> key = database.update("SELECT 1");
		assertFalse(key.isPresent());
		
		verify(stmt).executeUpdate();
		verify(connection).close();
	}

}
