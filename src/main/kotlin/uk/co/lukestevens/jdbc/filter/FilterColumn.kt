package uk.co.lukestevens.jdbc.filter

import kotlin.random.Random

data class FilterColumn(val name: String){
    
    /**
     * @return A filter representing values in the given column that are null
     */
    val isNull: QueryFilter
        get() = QueryFilter("$name IS NULL")

    /**
     * @return A filter representing values in the given column that are not null
     */
    val isNotNull: QueryFilter
        get() = QueryFilter("$name IS NOT NULL")

    /**
     * @param values The values to compare
     * @return A filter representing values in the given column that are
     * contained within the given array
     */
    infix fun isIn(values: List<Any>): QueryFilter =
        uniqueParameterId().let { 
            QueryFilter("$name IN (:$it)", it, values) 
        }

    /**
     * @param value The value to compare
     * @return A filter representing values in the given column that are
     * equal to the given value
     */
    infix fun isEqualTo(value: Any): QueryFilter =
        uniqueParameterId().let {
            QueryFilter("$name = :$it", it, value)
        }

    /**
     * @param value The value to compare
     * @return A filter representing values in the given column that are
     * not equal to the given value
     */
    infix fun isNotEqualTo(value: Any): QueryFilter =
        uniqueParameterId().let {
            QueryFilter("$name != :$it", it, value)
        }

    /**
     * @param value The value to compare
     * @return A filter representing values in the given column that are
     * like the given value
     */
    infix fun isLike(value: String): QueryFilter =
        uniqueParameterId().let {
            QueryFilter("$name LIKE :$it", it, value)
        }

    /**
     * @param value The value to compare
     * @return A filter representing values in the given column that are
     * greater than the given value
     */
    infix fun isGreaterThan(value: Any): QueryFilter =
        uniqueParameterId().let {
            QueryFilter("$name > :$it", it, value)
        }

    /**
     * @param value The value to compare
     * @return A filter representing values in the given column that are
     * less than the given value
     */
    infix fun isLessThan(value: Any): QueryFilter =
        uniqueParameterId().let {
            QueryFilter("$name < :$it", it, value)
    }

    /**
     * @param value The value to compare
     * @return A filter representing values in the given column that are
     * greater than or equal to the given value
     */
    infix fun isGreaterThanOrEqualTo(value: Any): QueryFilter =
        uniqueParameterId().let {
            QueryFilter("$name >= :$it", it, value)
    }

    /**
     * @param value The value to compare
     * @return A filter representing values in the given column that are
     * less than or equal to the given value
     */
    infix fun isLessThanOrEqualTo(value: Any): QueryFilter =
        uniqueParameterId().let {
            QueryFilter("$name <= :$it", it, value)
    }

    private val charPool : List<Char> = ('a'..'z') + ('A'..'Z')

    private fun uniqueParameterId(): String {
        return (1..32)
            .map { Random.nextInt(0, charPool.size) }
            .map { charPool[it] }
            .joinToString("")
    }
}