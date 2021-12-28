package jdbc

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import uk.co.lukestevens.jdbc.ConfiguredDatabase
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import java.sql.Types
import java.text.DateFormat
import java.text.SimpleDateFormat
import javax.sql.DataSource

class TestConfiguredDatabase {
    internal enum class TestEnum {
        ENUM1, ENUM2, ENUM3
    }

    private val df: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

    private var rs = mockk<ResultSet>()
    private var stmt = mockk<PreparedStatement>(relaxed = true)
    private var connection = mockk<Connection>(relaxed = true)
    private var dataSource = mockk<DataSource>()
    private var database = ConfiguredDatabase(dataSource)

    @BeforeEach
    fun setup() {
        every { dataSource.connection } returns connection
        every {
            connection.prepareStatement("SELECT 1", Statement.RETURN_GENERATED_KEYS)
        } returns stmt
    }

    @Test
    fun testPrepareStatement() {
        val date = df.parse("2021/02/14 21:05:34")
        val statement = database.prepareStatement(
            connection, "SELECT 1",
            37, TestEnum.ENUM2, "astring", date)

        verify {
            stmt.setObject(1, 37)
            stmt.setObject(2, "ENUM2")
            stmt.setObject(3, "astring")
            stmt.setObject(4, date, Types.TIMESTAMP)
        }
        assertEquals(statement, stmt)
    }

    @Test
    fun testPrepareStatementNoParams() {
        val statement = database
            .prepareStatement(connection, "SELECT 1")
        verify(exactly = 0) {  stmt.setObject(any(), any()) }
        assertEquals(statement, stmt)
    }

    @Test
    fun testQuery() {
        every { stmt.executeQuery() } returns rs
        val dbr = database.query("SELECT 1")
        assertEquals(connection, dbr.connection)
        assertEquals(rs, dbr.resultSet)
    }

    @Test
    fun testUpdateWithGeneratedKey() {
        every { stmt.generatedKeys } returns rs
        every { rs.next() } returns true
        every { rs.getLong(1) } returns 19L

        val key = database.update("SELECT 1")
        assertEquals(19L, key)

        verify {
            stmt.executeUpdate()
            connection.close()
        }
    }

    @Test
    fun testUpdateWithoutGeneratedKey() {
        every { stmt.generatedKeys } returns rs
        every { rs.next() } returns false

        val key = database.update("SELECT 1")
        assertNull(key)

        verify {
            stmt.executeUpdate()
            connection.close()
        }
    }
}