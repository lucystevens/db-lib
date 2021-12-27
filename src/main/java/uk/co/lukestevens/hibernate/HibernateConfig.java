package uk.co.lukestevens.hibernate;

import org.hibernate.cfg.Configuration;
import uk.co.lukestevens.jdbc.DatabaseCredentials;

import java.util.*;

/**
 * A class to store database credentials
 * 
 * @author luke.stevens
 */
public class HibernateConfig {

	private final DatabaseCredentials databaseCredentials;
	private final String driverClass;
	private final Map<String, String> additionalProperties;
	private final List<Class<?>> entityClasses;


	public HibernateConfig(DatabaseCredentials databaseCredentials) {
		this.databaseCredentials = databaseCredentials;
		this.driverClass = null;
		this.additionalProperties = new HashMap<>();
		this.entityClasses = new ArrayList<>();
	}

	public DatabaseCredentials getDatabaseCredentials() {
		return databaseCredentials;
	}

	public Optional<String> getDriverClass() {
		return driverClass == null? Optional.empty() : Optional.of(driverClass);
	}

	public Map<String, String> getAdditionalProperties() {
		return additionalProperties;
	}

	public List<Class<?>> getEntityClasses() {
		return entityClasses;
	}

	public Configuration apply(Configuration cfg){
		cfg.setProperty("hibernate.connection.url", databaseCredentials.getUrl());
		cfg.setProperty("hibernate.connection.username", databaseCredentials.getUsername());
		cfg.setProperty("hibernate.connection.password", databaseCredentials.getPassword());

		// Optional driver class property
		if(driverClass != null) {
			cfg.setProperty("hibernate.connection.driver_class", driverClass);
		}

		// Add additional properties
		additionalProperties.forEach(cfg::setProperty);

		// Add all Entity classes
		entityClasses.forEach(cfg::addAnnotatedClass);
		return cfg;
	}
}
