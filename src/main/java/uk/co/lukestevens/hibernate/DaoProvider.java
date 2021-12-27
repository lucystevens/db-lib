package uk.co.lukestevens.hibernate;

public interface DaoProvider {
    <T> HibernateDao<T> getDao(Class<T> type);
}
