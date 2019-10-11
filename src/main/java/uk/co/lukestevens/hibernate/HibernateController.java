package uk.co.lukestevens.hibernate;

import java.io.IOException;
import java.util.Map.Entry;

import javax.persistence.Entity;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.reflections.Reflections;

import uk.co.lukestevens.hibernate.mapping.PersistantFieldMapper;
import uk.co.lukestevens.config.Config;
import uk.co.lukestevens.encryption.EncryptionService;

/**
 * A controller to manage the hibernate session factory,
 * as well as the creation of Daos
 * 
 * @author luke.stevens
 */
public class HibernateController{
	
	private final Config config;
	private final EncryptionService encryption;
	
	private SessionFactory factory;
	private List<PersistantFieldMapper> mappers = new ArrayList<>();

	/**
	 * Create a new hibernate controller using the application config.
	 * Hibernate uses the following properties;
	 * <ul><li><code>hibernate.db.alias</code> - The alias used to determine the
	 *         hibernate database properties. Defaults to <i>hibernate</i>.</li>
	 *     <li><code><i>{alias}</i>.db.driver_class</code> - The driver class to use to connect to the database</li>
	 *     <li><code><i>{alias}</i>.db.url</code> - The url to use to connect to the database</li>
	 *     <li><code><i>{alias}</i>.db.username</code> - The username to use to connect to the database</li>
	 *     <li><code><i>{alias}</i>.db.password</code> - The <i>encrypted</i> password to use to connect to the database</li>
	 * </ul>
	 * The hibernate configuration will also include any other properties that begin with <i>hibernate</i>
	 * @param config The application config to use to configure hibernate
	 * @param encryption The encryption service to decrypt the database password
	 */
	public HibernateController(Config config, EncryptionService encryption) {
		this.config = config;
		this.encryption = encryption;
	}

	/**
	 * Builds a new session factory using the supplied properties, and scans the classpath
	 * for entities.
	 * @return The constructed session factory
	 * @throws IOException
	 */
	protected SessionFactory buildFactory() throws IOException {
		String dbAlias = config.getAsStringOrDefault("hibernate.db.alias", "hibernate");
		
		String[] props = {"driver_class", "url", "username"};
		
		// Load the mandatory configuration
		Configuration cfg = new Configuration();
		for(String property : props) {
			String value = config.getAsString(dbAlias + ".db." + property);
			cfg.setProperty("hibernate.connection." + property, value);
		}
		
		// Decrypt the database password
		String password = config.getEncrypted(dbAlias + ".db.password", encryption);
		cfg.setProperty("hibernate.connection.password", password);
		
		// Find other optional hibernate configs
		for(Entry<Object, Object> property: config.entrySet()) {
			String key = property.getKey().toString();
			if(key.startsWith("hibernate")) {
				cfg.setProperty(key, property.getValue().toString());
			}
		}
		
		// Add all Entity classes
		String packageName = config.getAsStringOrDefault("app.group", "uk.co.lukestevens");
		Reflections reflections = new Reflections(packageName);
		for(Class<?> c : reflections.getTypesAnnotatedWith(Entity.class)) {
			cfg.addAnnotatedClass(c);
		}
		
		// Build the session factory
		this.factory = cfg.buildSessionFactory();
		return factory;
	}
	
	/**
	 * Register a persistent field mapper to be added to all
	 * daos constructed by this controller.
	 * @param mapper A persistent field mapper to do additional mapping
	 * on certain fields before it is persisted to the database, and after
	 * it is retrieved.
	 */
	public void registerMapper(PersistantFieldMapper mapper) {
		this.mappers.add(mapper);
	}
	
	/**
	 * Construct a new DAO for a class of object
	 * @param type The object type to construct the dao for
	 * @return A new DAO
	 * @throws IOException If the session factory cannot be built
	 */
	public <T> HibernateDao<T> getDao(Class<T> type) throws IOException{
		return new HibernateDao<>(this.getFactory(), type, mappers);
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
