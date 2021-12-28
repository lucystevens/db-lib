package uk.co.lukestevens.jdbc.result

import java.io.IOException
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

/**
 * An ease-of-use wrapper class that encompasses
 * both the result set and connection, allowing for
 * easy result parsing and use of try-with-resources
 * statements.
 *
 * @author luke.stevens
 */
class WrappedDatabaseResult(
    override val connection: Connection, override val resultSet: ResultSet
) : DatabaseResult {

    override fun processResultSet(action: (ResultSet) -> Unit) {
        while (resultSet.next()) {
            action.invoke(resultSet)
        }
    }

    override fun <T> parseResultSet(parser: (ResultSet) -> T): List<T> {
        return mutableListOf<T>().apply {
            processResultSet { add(parser.invoke(it)) }
        }
    }

    override fun close() {
        try {
            connection.close()
        } catch (e: SQLException) {
            throw IOException(e)
        }
    }
}