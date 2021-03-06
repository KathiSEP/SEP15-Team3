/**
 *This package represents system functionality. 
 */
package de.ofCourse.system;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import java.sql.Connection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Tests the DatabaseConnectionManager. <br>
 * Checks whether connections are fetched and released correctly and if the
 * connections are closed correctly in case of a shutdown.
 * <p>
 * For the test the DatabaseConnectionManager runs in a debug mode to seal it
 * off from other classes like e.g. the PropertyManager.
 * 
 * @author Tobias Fuchs
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ LogHandler.class})
public class DatabaseConnectionManagerTest{
    
    
    /**
     * Attribute that represents the mocked logger
     */
    private LogHandler myLogger;
    
    /**
     * Tests the connection manager.
     */
    @Test
    public void test() {		
	
	// Mock LogHandler
	PowerMockito.mockStatic(LogHandler.class);
	myLogger = mock(LogHandler.class);
	Mockito.when(LogHandler.getInstance()).thenReturn(myLogger);
	

	// Enables the debug mode of the database connection manager to ensure
	// that it is sealed of from other classes that can produce errors(like
	// e.g. PropertyManager) and preserve database resources.
	DatabaseConnectionManager.debugMode(true);

	// Checks whether the DatabaseConnectionsManager is no null
	assertNotNull(DatabaseConnectionManager.getInstance());
	
	
	// Checks whether the DatabaseConnectionManager is unique and fulfills
	// the Singleton characteristic
	assertSame(DatabaseConnectionManager.getInstance(),
		DatabaseConnectionManager.getInstance());

	// Checks whether the DatabaseConnectionsManager is no null
	Connection connectionTestNull = DatabaseConnectionManager.getInstance()
		.getConnection();
	assertNotNull(connectionTestNull);
	
	DatabaseConnectionManager.getInstance().releaseConnection(
		connectionTestNull);

	// Checks whether the returned connection is a sql connection
	Connection connection = DatabaseConnectionManager.getInstance()
		.getConnection();
	assertTrue(connection instanceof java.sql.Connection);
	DatabaseConnectionManager.getInstance().releaseConnection(connection);

	// Checks whether the connections are fetched and released correctly
	Connection connection1 = DatabaseConnectionManager.getInstance()
		.getConnection();
	assertTrue(connection1 instanceof java.sql.Connection);

	Connection connection2 = DatabaseConnectionManager.getInstance()
		.getConnection();
	assertTrue(connection2 instanceof java.sql.Connection);

	Connection connection3 = DatabaseConnectionManager.getInstance()
		.getConnection();
	assertTrue(connection3 instanceof java.sql.Connection);

	DatabaseConnectionManager.getInstance().releaseConnection(connection);
	DatabaseConnectionManager.getInstance().releaseConnection(connection1);
	DatabaseConnectionManager.getInstance().releaseConnection(connection2);

	// Checks whether the connections are closed correctly in case of a
	// calling shutdown
	DatabaseConnectionManager.getInstance().shutDown();

	// Disables the debugging mode
	DatabaseConnectionManager.debugMode(false);
    }
}
