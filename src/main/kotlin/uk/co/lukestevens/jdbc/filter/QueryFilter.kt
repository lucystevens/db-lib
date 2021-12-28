package uk.co.lukestevens.jdbc.filter

/**
 * A class representing a parameterised SQL query filter
 * to be applied in the WHERE clause
 *
 * @author luke.stevens
 */
data class QueryFilter(val sql: String, val params: Map<String, Any> = mapOf()) {

    /**
     * Create a new QueryFilter with a single parameter
     * @param sql The sql for this filter
     * @param parameter The single parameter
     */
    constructor(sql: String, paramName: String, parameter: Any) : this(
        sql, mapOf(paramName to parameter)
    )

    /**
     * Create a query filter that represents an and group
     * containing this and another filter
     * @param filter The filter to AND with this one
     * @return A new query filter
     */
    fun and(filter: QueryFilter): QueryFilter {
        return QueryFilters.and(this, filter)
    }

    /**
     * Create a query filter that represents an or group
     * containing this and another filter
     * @param filter The filter to OR with this one
     * @return A new query filter
     */
    fun or(filter: QueryFilter): QueryFilter {
        return QueryFilters.or(this, filter)
    }
}