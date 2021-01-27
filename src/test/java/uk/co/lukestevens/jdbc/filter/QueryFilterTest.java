package uk.co.lukestevens.jdbc.filter;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import uk.co.lukestevens.jdbc.filter.QueryFilter;
import uk.co.lukestevens.jdbc.filter.QueryFilters;

public class QueryFilterTest {
	
	public Object getParamValue(QueryFilter filter, int index) {
		List<String> keys = this.getParamKeys(filter.getSQL());
		String key = keys.get(index);
		return filter.getParams().get(key);
	}
	
	public List<String> getParamKeys(String sql) {
		String groups = sql.replaceAll(".+?:([a-zA-z0-9]+)", "$1#");
		String[] keys = groups.split("#");
		return Arrays.asList(keys);
	}
	
	public static void assertMatches(String expectedRegex, String actual) {
		assertTrue(actual.matches(expectedRegex), "Expected match but was: <" + actual + ">");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleFilter() {
		QueryFilter equalsFilter = QueryFilters.column("name").isEqualTo("test");
		assertMatches("name = :[a-zA-z0-9]+", equalsFilter.getSQL());
		assertEquals(1, equalsFilter.getParams().size());
		assertEquals("test", this.getParamValue(equalsFilter, 0));
		
		QueryFilter greaterThanFilter = QueryFilters.column("value").isGreaterThan(12);
		assertMatches("value > :[a-zA-z0-9]+", greaterThanFilter.getSQL());
		assertEquals(1, greaterThanFilter.getParams().size());
		assertEquals(12, this.getParamValue(greaterThanFilter, 0));
		
		QueryFilter inFilter = QueryFilters.column("site").isIn("mailer", "dashboard", "auth");
		assertMatches("site IN \\(:[a-zA-z0-9]+\\)", inFilter.getSQL());
		
		List<String> params = (List<String>) this.getParamValue(inFilter, 0);
		assertEquals(3, params.size());
		assertEquals("mailer", params.get(0));
		assertEquals("dashboard", params.get(1));
		assertEquals("auth", params.get(2));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testFilterGroup() {
		QueryFilter equalsFilter = QueryFilters.column("name").isEqualTo("test");
		QueryFilter greaterThanFilter = QueryFilters.column("value").isGreaterThan(12);
		
		QueryFilter andFilter = QueryFilters.and(equalsFilter, greaterThanFilter);
		assertMatches("\\(name = :[a-zA-z0-9]+ AND value > :[a-zA-z0-9]+\\)", andFilter.getSQL());
		assertEquals(2, andFilter.getParams().size());
		assertEquals("test", this.getParamValue(andFilter, 0));
		assertEquals(12, this.getParamValue(andFilter, 1));
		
		QueryFilter inFilter = QueryFilters.column("site").isIn("mailer", "dashboard", "auth");
		QueryFilter orFilter = QueryFilters.or(equalsFilter, inFilter, greaterThanFilter);
		assertMatches("\\(name = :[a-zA-z0-9]+ OR site IN \\(:[a-zA-z0-9]+\\) OR value > :[a-zA-z0-9]+\\)", orFilter.getSQL());
		assertEquals(3, orFilter.getParams().size());
		
		assertEquals("test", this.getParamValue(orFilter, 0));
		
		List<String> params = (List<String>) this.getParamValue(orFilter, 1);
		assertEquals("mailer", params.get(0));
		assertEquals("dashboard", params.get(1));
		assertEquals("auth", params.get(2));
		
		assertEquals(12, this.getParamValue(orFilter, 2));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testComplexFilter() {
		QueryFilter equalsFilter = QueryFilters.column("name").isEqualTo("test");
		QueryFilter greaterThanFilter = QueryFilters.column("value").isGreaterThan(12);	
		QueryFilter andFilter = QueryFilters.and(equalsFilter, greaterThanFilter);
		
		QueryFilter inFilter = QueryFilters.column("site").isIn("mailer", "dashboard", "auth");
		QueryFilter notNullFilter = QueryFilters.column("key").isNotNull();
		QueryFilter andFilter2 = QueryFilters.and(inFilter, notNullFilter);
		
		QueryFilter lessThanFilter = QueryFilters.column("count").isLessThanOrEqualTo(4);
		
		QueryFilter complexFilter = QueryFilters.or(andFilter, andFilter2, lessThanFilter);
		assertMatches(
				"\\(\\(name = :[a-zA-z0-9]+ AND value > :[a-zA-z0-9]+\\) OR "
				+ "\\(site IN \\(:[a-zA-z0-9]+\\) AND key IS NOT NULL\\) OR "
				+ "count <= :[a-zA-z0-9]+\\)",
				complexFilter.getSQL());
		
		assertEquals(4, complexFilter.getParams().size());
		assertEquals("test", this.getParamValue(complexFilter, 0));
		assertEquals(12, this.getParamValue(complexFilter, 1));
		
		List<String> params = (List<String>) this.getParamValue(complexFilter, 2);
		assertEquals("mailer", params.get(0));
		assertEquals("dashboard", params.get(1));
		assertEquals("auth", params.get(2));
		
		assertEquals(4, this.getParamValue(complexFilter, 3));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEmptyVarargs() {
		QueryFilter inFilter = QueryFilters.column("site").isIn();
		assertMatches("site IN \\(:[a-zA-z0-9]+\\)", inFilter.getSQL());
		assertEquals(1, inFilter.getParams().size());
		
		List<String> params = (List<String>) this.getParamValue(inFilter, 0);
		assertTrue(params.isEmpty());
		
		QueryFilter emptyOrFilter = QueryFilters.or();
		assertEquals("", emptyOrFilter.getSQL());
		assertEquals(0, emptyOrFilter.getParams().size());
		
		QueryFilter equalsFilter = QueryFilters.column("name").isEqualTo("test");
		QueryFilter greaterThanFilter = QueryFilters.column("value").isGreaterThan(12);	
		QueryFilter orFilter = QueryFilters.or(equalsFilter, greaterThanFilter);
		
		QueryFilter andFilter = QueryFilters.and(emptyOrFilter, orFilter);
		assertMatches("\\(name = :[a-zA-z0-9]+ OR value > :[a-zA-z0-9]+\\)", andFilter.getSQL());
		assertEquals(2, andFilter.getParams().size());
		assertEquals("test", this.getParamValue(andFilter, 0));
		assertEquals(12, this.getParamValue(andFilter, 1));
	}

}

