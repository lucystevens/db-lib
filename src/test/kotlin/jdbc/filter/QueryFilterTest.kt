package jdbc.filter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import uk.co.lukestevens.jdbc.filter.QueryFilter
import uk.co.lukestevens.jdbc.filter.QueryFilters.and
import uk.co.lukestevens.jdbc.filter.QueryFilters.column
import uk.co.lukestevens.jdbc.filter.QueryFilters.or

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
    fun testEqualsFilter() {
        val equalsFilter = column("name") isEqualTo "test"
        assertMatches("name = :[a-zA-z]+", equalsFilter.sql)
        assertEquals(1, equalsFilter.params.size)
        assertEquals("test", getParamValue(equalsFilter, 0))
    }

    @Test
    fun testGreaterThanFilter() {
        val greaterThanFilter = column("value") isGreaterThan 12
        assertMatches("value > :[a-zA-z]+", greaterThanFilter.sql)
        assertEquals(1, greaterThanFilter.params.size)
        assertEquals(12, getParamValue(greaterThanFilter, 0))
    }

    @Test
    fun testInFilter() {
        val inFilter = column("site") isIn listOf("mailer", "dashboard", "auth")
        assertMatches("site IN \\(:[a-zA-z]+\\)", inFilter.sql)

        val params = getParamValue(inFilter, 0) as List<*>
        assertEquals(3, params.size)
        assertEquals("mailer", params[0])
        assertEquals("dashboard", params[1])
        assertEquals("auth", params[2])
    }

    @Test
    fun testAndFilter() {
        val andFilter = and(
            column("name") isEqualTo "test",
            column("value") isGreaterThan 12
        )

        assertMatches("\\(name = :[a-zA-z]+ AND value > :[a-zA-z]+\\)", andFilter.sql)
        assertEquals(2, andFilter.params.size)
        assertEquals("test", getParamValue(andFilter, 0))
        assertEquals(12, getParamValue(andFilter, 1))
    }

    @Test
    fun testOrFilter() {
        val orFilter = or(
            column("name") isEqualTo "test",
            column("site") isIn listOf("mailer", "dashboard", "auth"),
            column("value") isGreaterThan 12,
        )
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
        val complexFilter = or(
            and(
                column("name") isEqualTo "test",
                column("value") isGreaterThan 12
            ),
            and(
                column("char") isIn listOf("a", "b", "c"),
                column("key").isNotNull
            ),
            column("count") isLessThanOrEqualTo 4
        )


        assertMatches(
            "\\(\\(name = :[a-zA-z]+ AND value > :[a-zA-z]+\\) OR "
                    + "\\(char IN \\(:[a-zA-z]+\\) AND key IS NOT NULL\\) OR "
                    + "count <= :[a-zA-z]+\\)",
            complexFilter.sql
        )
        assertEquals(4, complexFilter.params.size)
        assertEquals("test", getParamValue(complexFilter, 0))
        assertEquals(12, getParamValue(complexFilter, 1))
        val params = getParamValue(complexFilter, 2) as List<*>
        assertEquals("a", params[0])
        assertEquals("b", params[1])
        assertEquals("c", params[2])
        assertEquals(4, getParamValue(complexFilter, 3))
    }

    @Test
    fun testEmptyInFilter() {
        val inFilter = column("site") isIn listOf()
        assertMatches("site IN \\(:[a-zA-z]+\\)", inFilter.sql)
        assertEquals(1, inFilter.params.size)

        val params = getParamValue(inFilter, 0) as List<*>
        assertTrue(params.isEmpty())
    }

    @Test
    fun testEmptyOrFilter() {
        val emptyOrFilter = or()
        assertEquals("", emptyOrFilter.sql)
        assertEquals(0, emptyOrFilter.params.size)
    }

    @Test
    fun testComplexFilterWithEmptyFilters() {
        val andFilter = and(
            or(),
            or(
                column("name") isEqualTo "test" ,
                column("value") isGreaterThan 12
            )
        )
        assertMatches("\\(name = :[a-zA-z]+ OR value > :[a-zA-z]+\\)", andFilter.sql)
        assertEquals(2, andFilter.params.size)
        assertEquals("test", getParamValue(andFilter, 0))
        assertEquals(12, getParamValue(andFilter, 1))
    }

    companion object {
        fun assertMatches(expectedRegex: String, actual: String) {
            assertTrue(actual.matches(Regex(expectedRegex)), "Expected to match $expectedRegex but was: <$actual>")
        }
    }
}