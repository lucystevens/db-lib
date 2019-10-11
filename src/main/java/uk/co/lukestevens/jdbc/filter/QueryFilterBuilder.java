package uk.co.lukestevens.jdbc.filter;

import java.util.Arrays;
import net.bytebuddy.utility.RandomString;

/**
 * A class to use for building a {@link QueryFilter}
 * 
 * @author luke.stevens
 */
public class QueryFilterBuilder {
	
	private String column;

	/**
	 * Create new builder
	 * @param column The column to filter by
	 */
	protected QueryFilterBuilder(String column) {
		this.column = column;
	}
	
	/**
	 * @return A filter representing values in the given column that are null
	 */
	public QueryFilter isNull() {
		return new QueryFilter(column + " IS NULL");
	}
	
	/**
	 * @return A filter representing values in the given column that are not null
	 */
	public QueryFilter isNotNull() {
		return new QueryFilter(column + " IS NOT NULL");
	}
	
	/**
	 * @param values The values to compare
	 * @return A filter representing values in the given column that are
	 * contained within the given array
	 */
	public QueryFilter isIn(Object...values) {
		String id = this.uniqueParameterId();
		return new QueryFilter(column + " IN (:" + id + ")", id, Arrays.asList(values));
	}
	
	/**
	 * @param value The value to compare
	 * @return A filter representing values in the given column that are
	 * equal to the given value
	 */
	public QueryFilter isEqualTo(Object value) {
		String id = this.uniqueParameterId();
		return new QueryFilter(column + " = :" + id, id, value);
	}
	
	/**
	 * @param value The value to compare
	 * @return A filter representing values in the given column that are
	 * not equal to the given value
	 */
	public QueryFilter isNotEqualTo(Object value) {
		String id = this.uniqueParameterId();
		return new QueryFilter(column + " != :" + id, id, value);
	}
	
	/**
	 * @param value The value to compare
	 * @return A filter representing values in the given column that are
	 * like the given value
	 */
	public QueryFilter isLike(String value) {
		String id = this.uniqueParameterId();
		return new QueryFilter(column + " LIKE :" + id, id, value);
	}
	
	/**
	 * @param value The value to compare
	 * @return A filter representing values in the given column that are
	 * greater than the given value
	 */
	public QueryFilter isGreaterThan(Object value) {
		String id = this.uniqueParameterId();
		return new QueryFilter(column + " > :" + id, id, value);
	}
	
	/**
	 * @param value The value to compare
	 * @return A filter representing values in the given column that are
	 * less than the given value
	 */
	public QueryFilter isLessThan(Object value) {
		String id = this.uniqueParameterId();
		return new QueryFilter(column + " < :" + id, id, value);
	}
	
	/**
	 * @param value The value to compare
	 * @return A filter representing values in the given column that are
	 * greater than or equal to the given value
	 */
	public QueryFilter isGreaterThanOrEqualTo(Object value) {
		String id = this.uniqueParameterId();
		return new QueryFilter(column + " >= :" + id, id, value);
	}
	
	/**
	 * @param value The value to compare
	 * @return A filter representing values in the given column that are
	 * less than or equal to the given value
	 */
	public QueryFilter isLessThanOrEqualTo(Object value) {
		String id = this.uniqueParameterId();
		return new QueryFilter(column + " <= :" + id, id, value);
	}
	
	String uniqueParameterId() {
		return RandomString.make(32).replaceAll("[0-9]", "q");
	}

}
