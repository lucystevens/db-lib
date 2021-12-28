package uk.co.lukestevens.jdbc

import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

/**
 * A class to store database credentials
 *
 * @author luke.stevens
 */
data class DatabaseCredentials(val url: String, val username: String, val password: String) {
    fun asDataSource(): DataSource {
        return HikariDataSource().apply {
            username = username
            password = password
            jdbcUrl = url
        }
    }
}