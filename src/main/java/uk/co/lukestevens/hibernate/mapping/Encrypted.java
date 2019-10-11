package uk.co.lukestevens.hibernate.mapping;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * An annotation to define a field that should be
 * encrypted when stored in the database
 * but decrypted when fetched.
 * 
 * @author luke.stevens
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Encrypted {

}
