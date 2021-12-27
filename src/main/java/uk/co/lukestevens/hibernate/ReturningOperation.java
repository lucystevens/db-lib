package uk.co.lukestevens.hibernate;

import java.io.IOException;

import org.hibernate.Session;

/**
 * An interface to defined actions that can be performed on a session
 * 
 * @author Luke Stevens
 */
@FunctionalInterface
public interface ReturningOperation<R, T> {
	
	R execute(T arg) throws IOException;
	
}
