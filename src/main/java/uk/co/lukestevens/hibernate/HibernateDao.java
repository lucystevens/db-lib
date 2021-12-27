package uk.co.lukestevens.hibernate;

import org.hibernate.Session;
import org.hibernate.query.Query;
import uk.co.lukestevens.jdbc.filter.QueryFilter;

import java.util.List;
import java.util.Optional;

/**
 * A generic Dao class that uses hibernate to retrieve and persist objects
 * 
 * @author luke.stevens
 *
 * @param <T> The type of objects this dao should interact with
 */
public class HibernateDao<T> implements Dao<T> {
	
	final Session session;
	final Class<T> type;
	final String queryString;

	/**
	 * Creates a new dao. THis should only be called by the HibernateController.
	 * @param session The current Session.
	 * @param type The type of object this dao should interact with
	 */
	protected HibernateDao(Session session, Class<T> type) {
		this.session = session;
		this.type = type;
		this.queryString = "FROM " + type.getSimpleName();
	}

	@Override
	public Optional<T> get(QueryFilter filter) {
		List<T> result = this.list(filter);
		return result.isEmpty()? Optional.empty() : Optional.of(result.get(0));
	}

	@Override
	public List<T> list() {
		return session.createQuery(queryString, type).list();
	}

	@Override
	public List<T> list(QueryFilter filter) {
		Query<T> query = session.createQuery(queryString + " WHERE " + filter.getSQL(), type);
		filter.getParams().forEach(query::setParameter);
		return query.list();
	}

	@Override
	public void save(T t) {
		session.saveOrUpdate(t);
	}

	@Override
	public void delete(T t) {
		session.delete(t);
	}

}
