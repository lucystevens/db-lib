package uk.co.lukestevens.hibernate;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.io.IOException;


public class CloseableSessionTest {
	
	Session session = mock(Session.class);
	Transaction tx = mock(Transaction.class);
	SessionOperation operation = mock(SessionOperation.class);
	
	@BeforeEach
	public void setup() {
		when(session.beginTransaction()).thenReturn(tx);
	}
	
	@Test
	public void testPerformOperationWhenOperationSucceeds() throws IOException {
		CloseableSession closeableSession = new CloseableSession(session);
		closeableSession.performOperation(operation);
		
		verify(operation).execute(session);
		verify(tx).commit();
		
		closeableSession.close();
		verify(session).close();
	}
	
	@Test
	public void testPerformOperationWhenOperationFails() throws IOException {
		CloseableSession closeableSession = new CloseableSession(session);
		
		when(tx.isActive()).thenReturn(true);
		doThrow(IOException.class).when(operation).execute(session);
		
		assertThrows(IOException.class,
				() -> closeableSession.performOperation(operation));
		
		verify(tx).rollback();
		
		closeableSession.close();
		verify(session).close();
	}

}
