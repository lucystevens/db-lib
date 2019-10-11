package uk.co.lukestevens.hibernate.mapping;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import uk.co.lukestevens.encryption.EncryptionService;

/**
 * A persistent field mapper implementation to encrypt values 
 * annotated with {@link Encrypted} when they are persisted, and
 * decrypt them when fetched.
 * 
 * @author luke.stevens
 *
 */
public class FieldEncryptionMapper implements PersistantFieldMapper{

	private final EncryptionService service;
	
	// Interface to represent encryption/decryption
	private static interface EncryptionMethod {
		String apply(String s) throws IOException;
	}

	/**
	 * Create a new mapper with the encryption service to use
	 * @param service The encryption service to use to encrypt/decrypt values
	 */
	public FieldEncryptionMapper(EncryptionService service) {
		this.service = service;
	}
	
	@Override
	public void prePersist(Object o) throws IOException {
		try {
			this.modifyFields(o, service::encrypt);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public void postFetch(Object o) throws IOException {
		try {
			this.modifyFields(o, service::decrypt);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new IOException(e);
		}
	}
	
	/**
	 * Modify all fields annotated with {@link Encrypted} on a given object
	 * @param o The object to modify
	 * @param method The method (encrypt or decrypt) to apply to the field
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws IOException
	 */
	protected void modifyFields(Object o, EncryptionMethod method) throws IllegalArgumentException, IllegalAccessException, IOException {
		for(Field field : this.getEncryptedFields(o)) {
			field.setAccessible(true);
			String value = field.get(o).toString();
			String newValue = method.apply(value);
			field.set(o, newValue);
		}
	}
	
	/**
	 * Gets all fields on an object which are annotated with {@link Encrypted} and
	 * of type String.
	 * @param o The object to get encrypted fields for
	 * @return A list of fields
	 */
	protected List<Field> getEncryptedFields(Object o) {
		return Stream.of(o.getClass().getDeclaredFields())
				     .filter(field -> field.getAnnotation(Encrypted.class) != null)
				     .filter(field -> field.getType() == String.class)
				     .collect(Collectors.toList());
	}

}
