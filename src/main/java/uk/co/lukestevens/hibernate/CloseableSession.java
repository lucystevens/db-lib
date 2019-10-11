package uk.co.lukestevens.hibernate;

import java.io.Closeable;
import java.io.IOException;

import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * A {@link Closeable} wrapper around a hibernate {@link Session}
 * 
 * @author luke.stevens
 *
 */
public class CloseableSession implements Closeable {
	
	// An interface to defined actions that can be performed on a session
	@FunctionalInterface
	public interface SessionOperation {
		void execute(Session session) throws IOException;
	}
	
	private final Session session;
	
	private Transaction tx;

	/**
	 * Create a new CloseableSession, wrapping an
	 * existing hibernate session
	 * @param session The hibernate session to wrap
	 */
	public CloseableSession(Session session) {
		this.session = session;
	}

	/**
	 * Perform an operation on a session within a transaction. If the opaeration
	 * fails then the transaction will be rolled back, before the exception is thrown.
	 * @param operation The operation to perform on the session
	 * @throws IOException If there is an error performing the operation
	 */
	public void performOperation(SessionOperation operation) throws IOException {
		this.tx = session.beginTransaction();
		try {
			operation.execute(session);
		} catch (IOException e) {
			if(this.tx != null && this.tx.isActive()) {
				this.tx.rollback();
			}
			throw e;
		}
		this.tx.commit();
	}


	@Override
	public void close() throws IOException {
		session.close();
	}

}
