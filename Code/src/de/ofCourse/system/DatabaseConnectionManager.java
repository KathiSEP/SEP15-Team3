/**
 *This package represents system functionality. 
 */
package de.ofCourse.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.ofCourse.utilities.PropertyManager;

/**
 * Sets up a fixed number of connections to the database, stores them and
 * handles the assignment of the connections to the requesting threads and in
 * case of occurring errors it executes last statements and closes the
 * connections to leave the database in a valid state.
 * 
 * <p>
 * The DatabaseConnectionManager represents the pool, in which the connections
 * are hold. If a thread requests for a connection and there is a free
 * connections, the DatabaseConnectionManager gives it to him. If there's no
 * free connection at the time, the thread has to wait and the
 * DatabaseConnectionManager notifies the waiting thread as soon as there is a
 * free one.
 * 
 * <p>
 * In case of a JVM crash, the DatabaseConnectionManager executes last
 * statements and closes all connections to the database with the regard to
 * leave the database in a valid state and not to occupy resources.
 * 
 * @author Tobias Fuchs
 *
 */
public class DatabaseConnectionManager {

    /**
     * List of free connections
     */
    private final List<Connection> freeConnections;

    /**
     * Stores the number of connections that are currently in use
     */
    private int numberOfConnectionsInUse = 0;

    /**
     * Singleton-object of the DatabaseConnectionManager class
     */
    private static DatabaseConnectionManager databaseConnectionManager;

    /**
     * JDBC-Driver
     */
    public static final String dbDriver = "org.postgresql.Driver";

    /**
     * Number of allowed connections
     */
    private static final String NUMBER_OF_CONNECTIONS = PropertyManager
	    .getInstance().getPropertyConfig("dbconnections");

    /**
     * Constructor of the class DatabaseConnectionManager
     */
    private DatabaseConnectionManager() {
	freeConnections = Collections
		.synchronizedList(new LinkedList<Connection>());
	loadDBDriver();
    }

    /**
     * Returns the connection you need for database access.
     * 
     * @return connection for database access
     */
    public synchronized Connection getConnection() {
	// If there are not the number of connections active as determined in
	// the configuration file
	int numberOfConnections = Integer.parseInt(NUMBER_OF_CONNECTIONS);
	int difference = numberOfConnections
		- (freeConnections.size() + numberOfConnectionsInUse);

	if (difference > 0) {
	    for (int i = 0; i < difference; ++i) {
		freeConnections.add(establishConnection());
	    }
	}
	// As long there's no free connection to the database
	while (freeConnections.isEmpty()) {
	    try {
		wait();
	    } catch (InterruptedException e) {
		LogHandler.getInstance().error(
			"Error occured during waiting for a connection.");
	    }
	}
	// There's a free connection
	int indexLastElement = freeConnections.size() - 1;
	Connection connection = freeConnections.get(indexLastElement);
	freeConnections.remove(indexLastElement);
	++numberOfConnectionsInUse;
	LogHandler.getInstance().debug("Connection returned.");
	return connection;
    }

    /**
     * Releases the connection after it has been used.
     */
    public synchronized void releaseConnection(Connection connection) {
	try {
	    if (!connection.isClosed() && connection != null) {
		--numberOfConnectionsInUse;
		freeConnections.add(connection);
		LogHandler.getInstance().debug("Connection released.");
	    }
	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "Error occured during releasing the connection.");
	}
	// Notifies all waiting threads that there's a free connection
	notifyAll();
    }

    /**
     * Returns an instance of the DatabaseConnectionManager class.
     * 
     * @return instance of the DatabaseConnectionManager
     */
    public static DatabaseConnectionManager getInstance() {
	if (databaseConnectionManager == null) {
	    databaseConnectionManager = new DatabaseConnectionManager();
	    int numberOfConnections = Integer.parseInt(NUMBER_OF_CONNECTIONS);

	    for (int i = 0; i < numberOfConnections; ++i) {
		Connection conn = establishConnection();
		if (conn != null) {
		    databaseConnectionManager.freeConnections.add(conn);
		}
	    }
	}
	return databaseConnectionManager;
    }

    /**
     * Returns a established connection to the database.
     * 
     * @return if the connection was established correctly, the connection to
     *         the database
     * @throws SQLException
     *             if an exception occurs during establishing the database
     *             connection
     */
    private static Connection establishConnection() {
	Connection connection = null;
	System.out.println("counter");
	try {
	    connection = DriverManager.getConnection(
		    "jdbc:postgresql://"
			    + PropertyManager.getInstance().getPropertyConfig(
				    "dbhost")
			    + ":"
			    + PropertyManager.getInstance().getPropertyConfig(
				    "dbport")
			    + "/"
			    + PropertyManager.getInstance().getPropertyConfig(
				    "dbname"), PropertyManager.getInstance()
			    .getPropertyConfig("dbuser"), PropertyManager
			    .getInstance().getPropertyConfig("dbpassword"));
	    connection.setAutoCommit(false);
	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "Error occured during establishing a database"
			    + "connection. Please check whether the login"
			    + " credentials a set correct.");
	}
	LogHandler.getInstance().debug("Connection established.");
	return connection;
    }

    /**
     * If the JVM will crash, this method executes last statements and closes
     * all connections.
     */
    public void shutDown() {
	for (Connection connection : this.freeConnections) {
	    if (connection != null) {
		try {
		    connection.close();
		} catch (SQLException e) {
		    LogHandler.getInstance().error(
			    "Error occured during closing"
				    + " the connections to the database.");
		}
	    }
	}
    }

    /**
     * Loads the database driver.
     */
    private static void loadDBDriver() {
	try {
	    Class.forName(dbDriver);
	} catch (ClassNotFoundException e) {
	    LogHandler.getInstance().error(
		    "Error occoured during" + " loading the database driver!");
	}
    }
}