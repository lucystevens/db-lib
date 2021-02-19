package uk.co.lukestevens.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Sets;

import uk.co.lukestevens.config.ApplicationProperties;
import uk.co.lukestevens.config.Config;
import uk.co.lukestevens.hibernate.entities.AnnotatedEntity;
import uk.co.lukestevens.hibernate.entities.NonAnnotatedEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HibernateControllerTest {
	
	Config config = mock(Config.class);
	ApplicationProperties appProperties = mock(ApplicationProperties.class);
	
	Configuration hibernateConfig = mock(Configuration.class);
	
	// Spy on the controller so we can mock hibernate objects
	HibernateController controller = spy(new HibernateController(config, appProperties));
	
	@Test
	public void testGetAnnotatedClasses() {
		Set<Class<?>> classes = controller.getEntityClasses("uk.co.lukestevens.hibernate");
		assertEquals(1, classes.size());
		assertTrue(classes.contains(AnnotatedEntity.class));
	}
	
	@Test
	public void testBuildFactoryWhenDriverClassDefined() throws IOException {
		// mock these methods
		when(controller.createConfiguration()).thenReturn(hibernateConfig);
		when(controller.getEntityClasses("uk.co.lukestevens"))
			.thenReturn(Sets.newHashSet(AnnotatedEntity.class, NonAnnotatedEntity.class));
		when(appProperties.getApplicationGroup()).thenReturn("uk.co.lukestevens");
		
		// mock config
		when(config.getAsString("database.url")).thenReturn("jdbc:localhost:9999");
		when(config.getAsString("database.username")).thenReturn("admin");
		when(config.getAsString("database.password")).thenReturn("password123");
		when(config.getAsStringOrDefault("database.driver_class", null)).thenReturn("com.jdbc.Driver");
		when(config.entrySet()).thenReturn(new HashSet<>());
		
		controller.buildFactory();
		
		verify(hibernateConfig).setProperty("hibernate.connection.url", "jdbc:localhost:9999");
		verify(hibernateConfig).setProperty("hibernate.connection.username", "admin");
		verify(hibernateConfig).setProperty("hibernate.connection.password", "password123");
		verify(hibernateConfig).setProperty("hibernate.connection.driver_class", "com.jdbc.Driver");
		
		verify(hibernateConfig).addAnnotatedClass(AnnotatedEntity.class);
		verify(hibernateConfig).addAnnotatedClass(NonAnnotatedEntity.class);
		verify(hibernateConfig).buildSessionFactory();
	}
	
	@Test
	public void testBuildFactoryWithAdditionalHibernateConfig() throws IOException {
		// mock these methods
		when(controller.createConfiguration()).thenReturn(hibernateConfig);
		when(controller.getEntityClasses("uk.co.lukestevens")).thenReturn(new HashSet<>());
		when(appProperties.getApplicationGroup()).thenReturn("uk.co.lukestevens");
		
		// mock config
		when(config.getAsString("database.url")).thenReturn("jdbc:localhost:9999");
		when(config.getAsString("database.username")).thenReturn("admin");
		when(config.getAsString("database.password")).thenReturn("password123");
		
		Map<Object, Object> map = new HashMap<>();
		map.put("test.key", "some-value");
		map.put("hibernate.test.key", "hibernate-value");
		when(config.entrySet()).thenReturn(map.entrySet());
		
		controller.buildFactory();
		
		verify(hibernateConfig, never()).setProperty(eq("test.key"), any());
		verify(hibernateConfig).setProperty("hibernate.test.key", "hibernate-value");
		verify(hibernateConfig).buildSessionFactory();
	}
	
	@Test
	public void testBuildFactoryWhenDriverClassNotDefined() throws IOException {
		// mock these methods
		when(controller.createConfiguration()).thenReturn(hibernateConfig);
		when(controller.getEntityClasses("uk.co.lukestevens")).thenReturn(new HashSet<>());
		when(appProperties.getApplicationGroup()).thenReturn("uk.co.lukestevens");
		
		// mock config
		when(config.getAsString("database.url")).thenReturn("jdbc:localhost:9999");
		when(config.getAsString("database.username")).thenReturn("admin");
		when(config.getAsString("database.password")).thenReturn("password123");
		when(config.entrySet()).thenReturn(new HashSet<>());
		
		controller.buildFactory();
		
		verify(hibernateConfig, never()).setProperty(eq("hibernate.connection.driver_class"), any());
		verify(hibernateConfig).buildSessionFactory();
	}
	
	@Test
	public void testGetFactoryBuildsFactoryWhenNull() throws IOException {
		SessionFactory expected = mock(SessionFactory.class);
		when(controller.createConfiguration()).thenReturn(hibernateConfig);
		when(hibernateConfig.buildSessionFactory()).thenReturn(expected);
		when(controller.getEntityClasses("uk.co.lukestevens")).thenReturn(new HashSet<>());
		when(appProperties.getApplicationGroup()).thenReturn("uk.co.lukestevens");
		
		// mock config
		when(config.getAsString("database.url")).thenReturn("jdbc:localhost:9999");
		when(config.getAsString("database.username")).thenReturn("admin");
		when(config.getAsString("database.password")).thenReturn("password123");
		when(config.entrySet()).thenReturn(new HashSet<>());
		
		controller.buildFactory();
		
		SessionFactory actual = controller.getFactory();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetFactoryReturnsExistingFactory() throws IOException {
		controller.factory = mock(SessionFactory.class);
		controller.getFactory();
		verify(controller, never()).buildFactory();
	}
	
	@Test
	public void testGetDao() {
		SessionFactory expected = mock(SessionFactory.class);
		controller.factory = expected;
		HibernateDao<AnnotatedEntity> dao = (HibernateDao<AnnotatedEntity>)
				controller.getDao(AnnotatedEntity.class);
		assertEquals(expected, dao.factory);
	}
}
