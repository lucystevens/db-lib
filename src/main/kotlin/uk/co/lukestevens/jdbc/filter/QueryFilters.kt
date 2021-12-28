package uk.co.lukestevens.jdbc.filter

/**
 * A static utilities class for building
 * QueryFilters
 *
 * @author luke.stevens
 */
object QueryFilters {
    /**
     * Creates a FilterColumn of the given column
     * @param column The column to filter against
     * @return A FilterColumn, used to build a QueryFilter
     */
	@JvmStatic
	fun column(column: String): FilterColumn {
        return FilterColumn(column)
    }

    /**
     * Combine multiple QueryFilters in an AND group to
     * create a new filter
     * @param filters Filters that should have all criteria evaluated as true
     * @return A QueryFilter representing the AND of all the given filters
     */
	@JvmStatic
	fun and(vararg filters: QueryFilter): QueryFilter {
        return group("AND", filters.toList())
    }

    /**
     * Combine multiple QueryFilters in an AND group to
     * create a new filter
     * @param filters Filters that should have all criteria evaluated as true
     * @return A QueryFilter representing the AND of all the given filters
     */
    @JvmStatic
    fun and(filters: List<QueryFilter>): QueryFilter {
        return group("AND", filters)
    }

    /**
     * Combine multiple QueryFilters in an OR group to
     * create a new filter
     * @param filters Filters that should have at least one criteria evaluated as true
     * @return A QueryFilter representing the OR of all the given filters
     */
	@JvmStatic
	fun or(vararg filters: QueryFilter): QueryFilter {
        return group("OR", filters.toList())
    }

    /**
     * Combine multiple QueryFilters in an OR group to
     * create a new filter
     * @param filters Filters that should have at least one criteria evaluated as true
     * @return A QueryFilter representing the OR of all the given filters
     */
    @JvmStatic
    fun or(filters: List<QueryFilter>): QueryFilter {
        return group("OR", filters)
    }

    /**
     * Combine multiple QueryFilters to create a new filter
     * @param operator The operator (AND/OR)
     * @param filters The filter to combine
     * @return A QueryFilter representing the combination of the given filters
     */
    private fun group(operator: String, filters: List<QueryFilter>): QueryFilter {
        val params = mutableMapOf<String, Any>()
        for (filter in filters) {
            params.putAll(filter.params)
        }
        val sql = filters.map { it.sql }.filterNot { it.isEmpty() }
        return when(sql.size){
            0 -> empty()
            1 -> QueryFilter(sql[0], params)
            else -> QueryFilter("(${sql.joinToString(" $operator ")})", params)
        }
    }

    /**
     * @return An empty filter
     */
    fun empty(): QueryFilter {
        return QueryFilter("")
    }
}