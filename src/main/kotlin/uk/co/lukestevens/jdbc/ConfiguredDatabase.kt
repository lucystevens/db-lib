package uk.co.lukestevens.jdbc

import uk.co.lukestevens.jdbc.result.DatabaseResult
import uk.co.lukestevens.jdbc.result.WrappedDatabaseResult
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Statement
import java.sql.Types
import java.util.*
import javax.sql.DataSource

/**
 * A further implementation of the Database interface
 * that only leaves the connection to be defined by
 * overriding classes.
 *
 * @author luke.stevens
 */
class ConfiguredDatabase(private val dataSource: DataSource) : Database {
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
    fun prepareStatement(conn: Connection, query: String, vararg params: Any): PreparedStatement {
        return conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS).apply {
            for (i in params.indices) {
                var o = params[i]
                if (o.javaClass.isEnum) {
                    o = o.toString()
                }
                if (o is Date) {
                    setObject(i + 1, o, Types.TIMESTAMP)
                } else {
                    setObject(i + 1, o)
                }
            }
        }
    }

    override fun query(query: String, vararg params: Any): DatabaseResult {
        val conn = dataSource.connection
        val rs = prepareStatement(conn, query, *params).executeQuery()
        return WrappedDatabaseResult(conn, rs)
    }

    override fun update(query: String, vararg params: Any): Long? {
        return dataSource.connection.use { conn ->
            val stmt = prepareStatement(conn, query, *params)
            stmt.executeUpdate()
            stmt.generatedKeys.let {
                if(it.next()) it.getLong(1)
                else null
            }
        }
    }
}