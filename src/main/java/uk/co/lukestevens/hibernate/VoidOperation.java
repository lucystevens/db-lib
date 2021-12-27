package uk.co.lukestevens.hibernate;

import org.hibernate.Session;

import java.io.IOException;

/**
 * An interface to defined actions that can be performed on a session
 * 
 * @author Luke Stevens
 */
@FunctionalInterface
public interface VoidOperation<T> {
	
	void execute(T arg) throws IOException;
	
}
