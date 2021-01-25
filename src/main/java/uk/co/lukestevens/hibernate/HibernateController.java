package uk.co.lukestevens.hibernate;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.Entity;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.reflections.Reflections;

import uk.co.lukestevens.config.ApplicationProperties;
import uk.co.lukestevens.config.Config;

/**
 * A controller to manage the hibernate session factory,
 * as well as the creation of Daos
 * 
 * @author luke.stevens
 */
@Singleton
public class HibernateController implements DaoProvider{
	
	final Config config;
	final ApplicationProperties appProperties;
	
	SessionFactory factory;

	/**
	 * Create a new hibernate controller using the application config.
	 * Hibernate uses the following properties;
	 * <ul>
	 *     <li><code>database.driver_class</code> - The driver class to use to connect to the database (optional)</li>
	 *     <li><code>database.url</code> - The url to use to connect to the database</li>
	 *     <li><code>database.username</code> - The username to use to connect to the database</li>
	 *     <li><code>database.password</code> - The password to use to connect to the database</li>
	 * </ul>
	 * The hibernate configuration will also include any other properties that begin with <i>hibernate</i>
	 * @param config The config to use to configure hibernate
	 */
	@Inject
	public HibernateController(Config config, ApplicationProperties appProperties) {
		this.config = config;
		this.appProperties = appProperties;
	}

	/**
	 * Builds a new session factory using the supplied properties, and scans the classpath
	 * for entities.
	 * @return The constructed session factory
	 * @throws IOException
	 */
	protected SessionFactory buildFactory() throws IOException {
		String[] props = {"url", "username", "password"};
		
		// Load the mandatory configuration
		Configuration cfg = new Configuration();
		for(String property : props) {
			String value = config.getAsString("database." + property);
			cfg.setProperty("hibernate.connection." + property, value);
		}
		
		// Optional driver class property
		String driverClass = config.getAsStringOrDefault("database.driver_class", null);
		if(driverClass != null) {
			cfg.setProperty("hibernate.connection.driver_class", driverClass);
		}
		
		// Find other optional hibernate configs
		for(Entry<Object, Object> property: config.entrySet()) {
			String key = property.getKey().toString();
			if(key.startsWith("hibernate")) {
				cfg.setProperty(key, property.getValue().toString());
			}
		}
		
		// Add all Entity classes
		String packageName = appProperties.getApplicationGroup();
		Reflections reflections = new Reflections(packageName);
		for(Class<?> c : reflections.getTypesAnnotatedWith(Entity.class)) {
			cfg.addAnnotatedClass(c);
		}
		
		// Build the session factory
		this.factory = cfg.buildSessionFactory();
		return factory;
	}
	
	@Override
	public <T> Dao<T> getDao(Class<T> type) {
		try {
			return new HibernateDao<>(this.getFactory(), type);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * @return The SessionFactory instance managed by this controller,
	 * or a new instance if none is available.
	 * @throws IOException If the session factory cannot be built
	 */
	public SessionFactory getFactory() throws IOException {
		if(this.factory == null) {
			this.buildFactory();
		}
		return this.factory;
	}

}
