package uk.co.lukestevens.hibernate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import uk.co.lukestevens.hibernate.CloseableSession.SessionOperation;
import uk.co.lukestevens.hibernate.mapping.PersistantFieldMapper;
import uk.co.lukestevens.jdbc.filter.QueryFilter;
import uk.co.lukestevens.utils.Wrapper;

/**
 * A generic Dao class that uses hibernate to retrieve and persist objects
 * 
 * @author luke.stevens
 *
 * @param <T> The type of objects this dao should interact with
 */
public class HibernateDao<T> implements Dao<T> {
	
	private final SessionFactory factory;
	private final Class<T> type;
	private final List<PersistantFieldMapper> mappers;

	/**
	 * Creates a new dao. THis should only be called by the HibernateController.
	 * @param factory The SessionFactory to use to open sessions.
	 * @param type The type of object this dao should interact with
	 * @param mappers A list of persistent field mappers to use when persisting and fetching
	 */
	protected HibernateDao(SessionFactory factory, Class<T> type, List<PersistantFieldMapper> mappers) {
		this.factory = factory;
		this.type = type;
		this.mappers = mappers;
	}
	
	/**
	 * Open a new closeable session
	 * @return a new session
	 */
	protected CloseableSession openSession() {
		return new CloseableSession(factory.openSession());
	}
	
	/**
	 * @return The base query String for this type
	 */
	protected String getQueryString() {
		return "FROM " + type.getSimpleName();
	}

	@Override
	public T get(QueryFilter filter) throws IOException {
		List<T> result = this.list(filter);
		if(result.isEmpty()) {
			return null;
		}
		else {
			return result.get(0);
		}
	}

	@Override
	public List<T> list() throws IOException {
		Wrapper<List<T>> result = new Wrapper<>(new ArrayList<>());
		
		// Create query
		try(CloseableSession session = this.openSession()){
			SessionOperation operation = s -> result.set(s.createQuery(this.getQueryString(), type).list());
			session.performOperation(operation);
		}
		
		// Do the post fetch mapping 
		List<T> results = result.get();
		this.mapForRetrieve(results);
		return results;
	}

	@Override
	public List<T> list(QueryFilter filter) throws IOException {
		Wrapper<List<T>> result = new Wrapper<>(new ArrayList<>());
		
		// Create query with parameters
		try(CloseableSession session = this.openSession()){
			SessionOperation operation = s -> {
				Query<T> query = s.createQuery(this.getQueryString() + " WHERE " + filter.getSQL(), type);
				filter.getParams().entrySet().forEach(entry -> query.setParameter(entry.getKey(), entry.getValue()));
				result.set(query.list());
			};
			session.performOperation(operation);
		}
		
		// Do the post fetch mapping 
		List<T> results = result.get();
		this.mapForRetrieve(results);
		return results;
	}

	@Override
	public void save(T t) throws IOException {
		this.mapForPersist(t);
		try(CloseableSession session = this.openSession()){
			session.performOperation(s -> s.saveOrUpdate(t));
		}
		this.mapForRetrieve(t);
	}

	@Override
	public void delete(T t) throws IOException {
		try(CloseableSession session = this.openSession()){
			session.performOperation(s -> s.delete(t));
		}
	}
	
	/**
	 * Performs all pre-persist mappings on a object
	 * @param t The object to map
	 * @throws IOException If there is an exception when performing the mapping
	 */
	protected void mapForPersist(T t) throws IOException {
		for(PersistantFieldMapper mapper : this.mappers) {
			mapper.prePersist(t);
		}
	}
	
	/**
	 * Performs all post-fetch mappings on all objects in a list
	 * @param results The list of objects to map
	 * @throws IOException If there is an exception when performing the mapping
	 */
	protected void mapForRetrieve(List<T> results) throws IOException {
		for(T t : results) {
			this.mapForRetrieve(t);
		}
	}
	
	/**
	 * Performs all post-fetch mappings on a object
	 * @param t The object to map
	 * @throws IOException If there is an exception when performing the mapping
	 */
	protected void mapForRetrieve(T t) throws IOException {
		for(PersistantFieldMapper mapper : this.mappers) {
			mapper.postFetch(t);
		}
	}

}
