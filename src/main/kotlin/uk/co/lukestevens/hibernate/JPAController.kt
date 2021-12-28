package uk.co.lukestevens.hibernate

import org.hibernate.Session
import org.hibernate.SessionFactory

interface JPAController {

    fun <T> useSession(operation: (Session) -> T): T
    fun useSession(operation: (Session) -> Unit)
    fun <T> useTransaction(operation: (Session) -> T): T
    fun useTransaction(operation: (Session) -> Unit)
    fun <T> getDao(type: Class<T>): Dao<T>
    fun getSession(): Session

    /**
     * @return The SessionFactory instance managed by this controller,
     */
    val factory: SessionFactory
}