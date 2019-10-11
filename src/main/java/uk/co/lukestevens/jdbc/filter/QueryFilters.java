package uk.co.lukestevens.jdbc.filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A static utilities class for building
 * QueryFilters
 * 
 * @author luke.stevens
 */
public class QueryFilters {
	
	/**
	 * Creates a QueryFilterBuilder of the given column
	 * @param column The column to filter against
	 * @return A QueryFilterBuilder, used to build a QueryFilter
	 */
	public static QueryFilterBuilder column(String column) {
		return new QueryFilterBuilder(column);
	}
	
	/**
	 * Combine multiple QueryFilters in an AND group to
	 * create a new filter
	 * @param filters Filters that should have all criteria evaluated as true
	 * @return A QueryFilter representing the AND of all the given filters
	 */
	public static QueryFilter and(QueryFilter...filters) {
		return QueryFilters.group("AND", filters);
	}
	
	/**
	 * Combine multiple QueryFilters in an OR group to
	 * create a new filter
	 * @param filters Filters that should have at least one criteria evaluated as true
	 * @return A QueryFilter representing the OR of all the given filters
	 */
	public static QueryFilter or(QueryFilter...filters) {
		return QueryFilters.group("OR", filters);
	}
	
	/**
	 * Combine multiple QueryFilters to create a new filter
	 * @param operator The operator (AND/OR)
	 * @param filters The filter to combine
	 * @return A QueryFilter representing the combination of the given filters
	 */
	private static QueryFilter group(String operator, QueryFilter...filters) {
		Map<String, Object> params = new HashMap<>();
		for(QueryFilter filter : filters) {
			params.putAll(filter.getParams());
		}
		List<String> sql = Stream.of(filters).map(QueryFilter::getSQL).filter(s -> !s.isEmpty()).collect(Collectors.toList());
		
		if(sql.isEmpty()) {
			return empty();
		}
		else if(sql.size() == 1) {
			return new QueryFilter(sql.get(0), params);
		}
		else {
			return new QueryFilter("(" + String.join(" " + operator + " ", sql) + ")", params);
		}
	}
	
	/**
	 * @return An empty filter
	 */
	public static QueryFilter empty() {
		return new QueryFilter(""); 
	}

}
