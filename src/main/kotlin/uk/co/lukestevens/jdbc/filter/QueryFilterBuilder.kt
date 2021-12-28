package uk.co.lukestevens.jdbc.filter

import kotlin.random.Random

/**
 * A class to use for building a [QueryFilter]
 *
 * @author luke.stevens
 */
class QueryFilterBuilder(private val column: String) {
    /**
     * @return A filter representing values in the given column that are null
     */
    val isNull: QueryFilter
        get() = QueryFilter("$column IS NULL")

    /**
     * @return A filter representing values in the given column that are not null
     */
    val isNotNull: QueryFilter
        get() = QueryFilter("$column IS NOT NULL")

    /**
     * @param values The values to compare
     * @return A filter representing values in the given column that are
     * contained within the given array
     */
    fun isIn(vararg values: Any): QueryFilter {
        val id = uniqueParameterId()
        return QueryFilter("$column IN (:$id)", id, values.toList())
    }

    /**
     * @param value The value to compare
     * @return A filter representing values in the given column that are
     * equal to the given value
     */
    fun isEqualTo(value: Any): QueryFilter {
        val id = uniqueParameterId()
        return QueryFilter("$column = :$id", id, value)
    }

    /**
     * @param value The value to compare
     * @return A filter representing values in the given column that are
     * not equal to the given value
     */
    fun isNotEqualTo(value: Any): QueryFilter {
        val id = uniqueParameterId()
        return QueryFilter("$column != :$id", id, value)
    }

    /**
     * @param value The value to compare
     * @return A filter representing values in the given column that are
     * like the given value
     */
    fun isLike(value: String): QueryFilter {
        val id = uniqueParameterId()
        return QueryFilter("$column LIKE :$id", id, value)
    }

    /**
     * @param value The value to compare
     * @return A filter representing values in the given column that are
     * greater than the given value
     */
    fun isGreaterThan(value: Any): QueryFilter {
        val id = uniqueParameterId()
        return QueryFilter("$column > :$id", id, value)
    }

    /**
     * @param value The value to compare
     * @return A filter representing values in the given column that are
     * less than the given value
     */
    fun isLessThan(value: Any): QueryFilter {
        val id = uniqueParameterId()
        return QueryFilter("$column < :$id", id, value)
    }

    /**
     * @param value The value to compare
     * @return A filter representing values in the given column that are
     * greater than or equal to the given value
     */
    fun isGreaterThanOrEqualTo(value: Any): QueryFilter {
        val id = uniqueParameterId()
        return QueryFilter("$column >= :$id", id, value)
    }

    /**
     * @param value The value to compare
     * @return A filter representing values in the given column that are
     * less than or equal to the given value
     */
    fun isLessThanOrEqualTo(value: Any): QueryFilter {
        val id = uniqueParameterId()
        return QueryFilter("$column <= :$id", id, value)
    }

    private val charPool : List<Char> = ('a'..'z') + ('A'..'Z')

    fun uniqueParameterId(): String {
        return (1..32)
            .map { Random.nextInt(0, charPool.size) }
            .map { charPool[it] }
            .joinToString("")
    }
}