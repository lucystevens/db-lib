package uk.co.lukestevens.hibernate

import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A controller to manage the hibernate session factory,
 * as well as the creation of Daos
 *
 * @author luke.stevens
 */
@Singleton
class HibernateController @Inject constructor(hibernateConfig: HibernateConfig) : JPAController {

    override val factory: SessionFactory = hibernateConfig.apply(Configuration())
        .buildSessionFactory()

    private val session = ThreadLocal<Session>()

    override fun <T> useSession(operation: (Session) -> T): T {
        return getSession().use { operation.invoke(it) }
    }

    override fun useSession(operation: (Session) -> Unit) {
        useSession<Boolean> {
            operation.invoke(it)
            true
        }
    }

    override fun <T> useTransaction(operation: (Session) -> T): T {
        val session = getSession()
        val tx = session.beginTransaction()
        val result = try {
            operation.invoke(session)
        } catch (e: IOException) {
            if (tx.isActive) {
                tx.rollback()
            }
            throw e
        }
        tx.commit()
        return result
    }

    override fun useTransaction(operation: (Session) -> Unit) {
        useTransaction<Boolean> {
            operation.invoke(it)
            true
        }
    }

    override fun <T> getDao(type: Class<T>): HibernateDao<T> {
        return HibernateDao(getSession(), type)
    }

    override fun getSession(): Session {
        if (session.get() == null) {
            session.set(factory.openSession())
        }
        return session.get()
    }

}