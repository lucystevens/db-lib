package uk.co.lukestevens.jdbc.result

import java.io.Closeable
import java.sql.Connection
import java.sql.ResultSet

interface DatabaseResult : Closeable {
    val connection: Connection
    val resultSet: ResultSet

    fun processResultSet(action: (ResultSet) -> Unit)
    fun <T> parseResultSet(parser: (ResultSet) -> T): List<T>
}