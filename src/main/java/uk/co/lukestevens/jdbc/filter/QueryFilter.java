package uk.co.lukestevens.jdbc.filter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A class representing a parameterised SQL query filter
 * to be applied in the WHERE clause
 * 
 * @author luke.stevens
 */
public class QueryFilter {
	
	protected final String sql;
	protected final Map<String, Object> parameters;
	
	/**
	 * Create a new QueryFilter with no parameters
	 * @param sql The sql for this filter
	 */
	protected QueryFilter(String sql) {
		this(sql, new HashMap<>());
	}
	
	/**
	 * Create a new QueryFilter with a single parameter
	 * @param sql The sql for this filter
	 * @param parameter The single parameter
	 */
	protected QueryFilter(String sql, String paramName, Object parameter) {
		this(sql, Collections.singletonMap(paramName, parameter));
	}
	
	/**
	 * Create a new QueryFilter with a list of parameters
	 * @param sql The sql for this filter
	 * @param parameters The list of parameters
	 */
	protected QueryFilter(String sql, Map<String, Object> parameters) {
		this.sql = sql;
		this.parameters = parameters;
	}
	
	/**
	 * Create a query filter that represents an and group
	 * containing this and another filter 
	 * @param filter The filter to AND with this one
	 * @return A new query filter
	 */
	public QueryFilter and(QueryFilter filter) {
		return QueryFilters.and(this, filter);
	}
	
	/**
	 * Create a query filter that represents an or group
	 * containing this and another filter 
	 * @param filterThe filter to OR with this one
	 * @return A new query filter
	 */
	public QueryFilter or(QueryFilter filter) {
		return QueryFilters.or(this, filter);
	}

	/**
	 * @return All parameters to be included in the query
	 */
	public Map<String, Object> getParams() {
		return parameters;
	}

	/**
	 * @return The SQL clause for this filter
	 */
	public String getSQL() {
		return sql;
	}
	
}
