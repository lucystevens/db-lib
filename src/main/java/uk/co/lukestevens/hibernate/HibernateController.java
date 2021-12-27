package uk.co.lukestevens.hibernate;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import uk.co.lukestevens.utils.Wrapper;

import java.io.IOException;

/**
 * A controller to manage the hibernate session factory,
 * as well as the creation of Daos
 * 
 * @author luke.stevens
 */
@Singleton
public class HibernateController implements JPAController {

	private final SessionFactory factory;
	private final ThreadLocal<Session> session = new ThreadLocal<>();

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
	 * @param hibernateConfig The config to use to configure hibernate
	 */
	@Inject
	public HibernateController(HibernateConfig hibernateConfig) {
		this.factory = hibernateConfig.apply(new Configuration())
				.buildSessionFactory();
	}

	@Override
	public <T> T useSession(ReturningOperation<T, Session> operation) throws IOException {
		try (Session session = this.getSession()) {
			return operation.execute(session);
		}
	}

	@Override
	public void useSession(VoidOperation<Session> operation) throws IOException {
		useSession(session -> {
			operation.execute(session);
			return true;
		});
	}

	@Override
	public <T> T useTransaction(ReturningOperation<T, Session> operation) throws IOException {
		Session session = this.getSession();
		Transaction tx = session.beginTransaction();
		Wrapper<T> result = new Wrapper<>();
		try {
			result.set(operation.execute(session));
		} catch (IOException e) {
			if(tx.isActive()) {
				tx.rollback();
			}
			throw e;
		}
		tx.commit();
		return result.get();
	}

	@Override
	public void useTransaction(VoidOperation<Session> operation) throws IOException {
		useTransaction(session -> {
			operation.execute(session);
			return true;
		});
	}

	@Override
	public <T> HibernateDao<T> getDao(Class<T> type){
		return new HibernateDao<>(getSession(), type);
	}

	@Override
	public Session getSession(){
		if(session.get() == null){
			session.set(factory.openSession());
		}
		return session.get();
	}

	@Override
	public SessionFactory getFactory() {
		return this.factory;
	}

}
