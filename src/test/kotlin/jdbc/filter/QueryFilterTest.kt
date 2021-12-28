package jdbc.filter

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import uk.co.lukestevens.jdbc.filter.QueryFilter
import uk.co.lukestevens.jdbc.filter.QueryFilters.and
import uk.co.lukestevens.jdbc.filter.QueryFilters.column
import uk.co.lukestevens.jdbc.filter.QueryFilters.or
import java.util.*

class QueryFilterTest {
    private fun getParamValue(filter: QueryFilter, index: Int): Any {
        val keys = getParamKeys(filter.sql)
        val key = keys[index]
        return filter.params[key]!!
    }

    private fun getParamKeys(sql: String): List<String> {
        return sql.replace(".+?:([a-zA-z]+)".toRegex(), "$1#")
            .split("#")
    }

    @Test
    fun testSimpleFilter() {
        val equalsFilter = column("name").isEqualTo("test")
        assertMatches("name = :[a-zA-z]+", equalsFilter.sql)
        assertEquals(1, equalsFilter.params.size)
        assertEquals("test", getParamValue(equalsFilter, 0))

        val greaterThanFilter = column("value").isGreaterThan(12)
        assertMatches("value > :[a-zA-z]+", greaterThanFilter.sql)
        assertEquals(1, greaterThanFilter.params.size)
        assertEquals(12, getParamValue(greaterThanFilter, 0))

        val inFilter = column("site").isIn("mailer", "dashboard", "auth")
        assertMatches("site IN \\(:[a-zA-z]+\\)", inFilter.sql)

        val params = getParamValue(inFilter, 0) as List<*>
        assertEquals(3, params.size)
        assertEquals("mailer", params[0])
        assertEquals("dashboard", params[1])
        assertEquals("auth", params[2])
    }

    @Test
    fun testFilterGroup() {
        val equalsFilter = column("name").isEqualTo("test")
        val greaterThanFilter = column("value").isGreaterThan(12)
        val andFilter = and(equalsFilter, greaterThanFilter)

        assertMatches("\\(name = :[a-zA-z]+ AND value > :[a-zA-z]+\\)", andFilter.sql)
        assertEquals(2, andFilter.params.size)
        assertEquals("test", getParamValue(andFilter, 0))
        assertEquals(12, getParamValue(andFilter, 1))

        val inFilter = column("site").isIn("mailer", "dashboard", "auth")
        val orFilter = or(equalsFilter, inFilter, greaterThanFilter)
        assertMatches(
            "\\(name = :[a-zA-z]+ OR site IN \\(:[a-zA-z]+\\) OR value > :[a-zA-z]+\\)",
            orFilter.sql
        )
        assertEquals(3, orFilter.params.size)
        assertEquals("test", getParamValue(orFilter, 0))

        val params = getParamValue(orFilter, 1) as List<*>
        assertEquals("mailer", params[0])
        assertEquals("dashboard", params[1])
        assertEquals("auth", params[2])
        assertEquals(12, getParamValue(orFilter, 2))
    }

    @Test
    fun testComplexFilter() {
        val equalsFilter = column("name").isEqualTo("test")
        val greaterThanFilter = column("value").isGreaterThan(12)
        val andFilter = and(equalsFilter, greaterThanFilter)
        val inFilter = column("site").isIn("mailer", "dashboard", "auth")
        val notNullFilter = column("key").isNotNull
        val andFilter2 = and(inFilter, notNullFilter)
        val lessThanFilter = column("count").isLessThanOrEqualTo(4)
        val complexFilter = or(andFilter, andFilter2, lessThanFilter)

        assertMatches(
            "\\(\\(name = :[a-zA-z]+ AND value > :[a-zA-z]+\\) OR "
                    + "\\(site IN \\(:[a-zA-z]+\\) AND key IS NOT NULL\\) OR "
                    + "count <= :[a-zA-z]+\\)",
            complexFilter.sql
        )
        assertEquals(4, complexFilter.params.size)
        assertEquals("test", getParamValue(complexFilter, 0))
        assertEquals(12, getParamValue(complexFilter, 1))
        val params = getParamValue(complexFilter, 2) as List<*>
        assertEquals("mailer", params[0])
        assertEquals("dashboard", params[1])
        assertEquals("auth", params[2])
        assertEquals(4, getParamValue(complexFilter, 3))
    }

    @Test
    fun testEmptyVarargs() {
        val inFilter = column("site").isIn()
        assertMatches("site IN \\(:[a-zA-z]+\\)", inFilter.sql)
        assertEquals(1, inFilter.params.size)

        val params = getParamValue(inFilter, 0) as List<*>
        Assertions.assertTrue(params.isEmpty())

        val emptyOrFilter = or()
        assertEquals("", emptyOrFilter.sql)
        assertEquals(0, emptyOrFilter.params.size)

        val equalsFilter = column("name").isEqualTo("test")
        val greaterThanFilter = column("value").isGreaterThan(12)
        val orFilter = or(equalsFilter, greaterThanFilter)
        val andFilter = and(emptyOrFilter, orFilter)
        assertMatches("\\(name = :[a-zA-z]+ OR value > :[a-zA-z]+\\)", andFilter.sql)
        assertEquals(2, andFilter.params.size)
        assertEquals("test", getParamValue(andFilter, 0))
        assertEquals(12, getParamValue(andFilter, 1))
    }

    companion object {
        fun assertMatches(expectedRegex: String, actual: String) {
            Assertions.assertTrue(actual.matches(Regex(expectedRegex)), "Expected to match $expectedRegex but was: <$actual>")
        }
    }
}