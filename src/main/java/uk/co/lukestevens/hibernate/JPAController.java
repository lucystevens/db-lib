package uk.co.lukestevens.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.IOException;

public interface JPAController {

    <T> T useSession(ReturningOperation<T, Session> operation) throws IOException;

    void useSession(VoidOperation<Session> operation) throws IOException;

    <T> T useTransaction(ReturningOperation<T, Session> operation) throws IOException;

    void useTransaction(VoidOperation<Session> operation) throws IOException;

    <T> Dao<T> getDao(Class<T> type);

    Session getSession();

    /**
     * @return The SessionFactory instance managed by this controller,
     */
    SessionFactory getFactory();

}
