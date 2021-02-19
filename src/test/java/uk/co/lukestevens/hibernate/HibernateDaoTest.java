package uk.co.lukestevens.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.lukestevens.hibernate.entities.AnnotatedEntity;
import uk.co.lukestevens.jdbc.filter.QueryFilter;

@SuppressWarnings("unchecked")
public class HibernateDaoTest {
	
	Session session = mock(Session.class);
	Transaction tx = mock(Transaction.class);
	Query<AnnotatedEntity> query = mock(Query.class);
	
	SessionFactory sessionFactory = mock(SessionFactory.class);
	HibernateDao<AnnotatedEntity> dao = 
			new HibernateDao<AnnotatedEntity>(sessionFactory, AnnotatedEntity.class);

	@BeforeEach
	public void setup() {
		when(sessionFactory.openSession()).thenReturn(session);
		when(session.beginTransaction()).thenReturn(tx);
		when(session.createQuery(any(), eq(AnnotatedEntity.class))).thenReturn(query);
	}
	
	@Test
	public void testOpenSession() {
		CloseableSession session = dao.openSession();
		
		assertNotNull(session);
		verify(sessionFactory).openSession();
	}
	
	@Test
	public void testGetQueryString() {
		String sql = dao.getQueryString();
		assertEquals("FROM AnnotatedEntity", sql);
	}
	
	@Test
	public void testGetWithFilterWhenResultExists() throws IOException {
		QueryFilter filter = mock(QueryFilter.class);
		
		when(filter.getParams()).thenReturn(new HashMap<>());
		when(filter.getSQL()).thenReturn("x");
		
		List<AnnotatedEntity> expected = Arrays.asList(
				new AnnotatedEntity(1),
				new AnnotatedEntity(2));
		
		when(query.list()).thenReturn(expected);
		AnnotatedEntity actual = dao.get(filter);
		
		assertEquals(expected.get(0), actual);
		verify(session).createQuery(
				"FROM AnnotatedEntity WHERE x",
				AnnotatedEntity.class);
		verify(session).close();
	}
	
	@Test
	public void testGetWithFilterWhenResultIsMissing() throws IOException {
		QueryFilter filter = mock(QueryFilter.class);
		
		when(filter.getParams()).thenReturn(new HashMap<>());
		when(filter.getSQL()).thenReturn("");
		
		when(query.list()).thenReturn(new ArrayList<>());
		AnnotatedEntity actual = dao.get(filter);
		
		assertNull(actual);
		verify(session).close();
	}
	
	@Test
	public void testList() throws IOException {
		List<AnnotatedEntity> expected = Arrays.asList(
				new AnnotatedEntity(1),
				new AnnotatedEntity(2));
		
		when(query.list()).thenReturn(expected);
		List<AnnotatedEntity> actual = dao.list();
		
		assertEquals(expected, actual);
		verify(session).createQuery("FROM AnnotatedEntity", AnnotatedEntity.class);
		verify(session).close();
	}
	
	@Test
	public void testListWithQueryFilter() throws IOException {
		QueryFilter filter = mock(QueryFilter.class);
		
		Map<String, Object> params = new HashMap<>();
		params.put("param1", 23);
		params.put("param2", "value");
		when(filter.getParams()).thenReturn(params);
		when(filter.getSQL()).thenReturn("param1 = :param1 AND param2 != :param2");
		
		List<AnnotatedEntity> expected = Arrays.asList(
				new AnnotatedEntity(1),
				new AnnotatedEntity(2));
		
		when(query.list()).thenReturn(expected);
		List<AnnotatedEntity> actual = dao.list(filter);
		
		assertEquals(expected, actual);
		verify(session).createQuery(
				"FROM AnnotatedEntity WHERE param1 = :param1 AND param2 != :param2",
				AnnotatedEntity.class);
		verify(query).setParameter("param1", 23);
		verify(query).setParameter("param2", "value");
		verify(session).close();
	}
	
	@Test
	public void testSave() throws IOException {
		AnnotatedEntity entity = new AnnotatedEntity(5);
		dao.save(entity);
		verify(session).saveOrUpdate(entity);
	}
	
	@Test
	public void testDelete() throws IOException {
		AnnotatedEntity entity = new AnnotatedEntity(6);
		dao.delete(entity);
		verify(session).delete(entity);
	}

}
