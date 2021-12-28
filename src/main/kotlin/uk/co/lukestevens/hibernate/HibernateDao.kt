package uk.co.lukestevens.hibernate

import org.hibernate.Session
import uk.co.lukestevens.jdbc.filter.QueryFilter

/**
 * A generic Dao class that uses hibernate to retrieve and persist objects
 *
 * @author luke.stevens
 *
 * @param <T> The type of objects this dao should interact with
</T> */
class HibernateDao<T>(private val session: Session, private val type: Class<T>) : Dao<T> {
    private val queryString: String = "FROM ${type.simpleName}"

    override fun fetch(filter: QueryFilter): T? {
        return list(filter).firstOrNull()
    }

    override fun list(): List<T> {
        return session.createQuery(queryString, type).list()
    }

    override fun list(filter: QueryFilter): List<T> {
        return session.createQuery("$queryString WHERE ${filter.sql}", type).apply {
            filter.params.forEach { (name, value) -> setParameter(name, value) }
        }.list()
    }

    override fun save(t: T) {
        session.saveOrUpdate(t)
    }

    override fun delete(t: T) {
        session.delete(t)
    }
}