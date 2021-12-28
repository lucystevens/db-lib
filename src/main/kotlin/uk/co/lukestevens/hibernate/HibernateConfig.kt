package uk.co.lukestevens.hibernate

import org.hibernate.cfg.Configuration
import uk.co.lukestevens.jdbc.DatabaseCredentials

/**
 * A class to store database credentials
 *
 * @author luke.stevens
 */
data class HibernateConfig(
    val databaseCredentials: DatabaseCredentials,
    val driverClass: String? = null,
    val additionalProperties: Map<String, String> = mutableMapOf(),
    val entityClasses: List<Class<*>> = mutableListOf()
) {
    fun apply(cfg: Configuration): Configuration {
        return cfg.apply {
            setProperty("hibernate.connection.url", databaseCredentials.url)
            setProperty("hibernate.connection.username", databaseCredentials.username)
            setProperty("hibernate.connection.password", databaseCredentials.password)

            // Optional driver class property
            driverClass?.let{
                setProperty("hibernate.connection.driver_class", it)
            }

            // Add additional properties
            additionalProperties.forEach { setProperty(it.key, it.value) }

            // Add all Entity classes
            entityClasses.forEach{ addAnnotatedClass(it) }
        }
    }
}