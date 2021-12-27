package uk.co.lukestevens.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import uk.co.lukestevens.utils.Wrapper;

import java.io.IOException;

public interface JPAController {

    public <T> T useSession(ReturningOperation<T, Session> operation) throws IOException;

    public void useSession(VoidOperation<Session> operation) throws IOException;

    public <T> T useTransaction(ReturningOperation<T, Session> operation) throws IOException;

    public void useTransaction(VoidOperation<Session> operation) throws IOException;

    public Session getSession();

    /**
     * @return The SessionFactory instance managed by this controller,
     */
    public SessionFactory getFactory();

}
