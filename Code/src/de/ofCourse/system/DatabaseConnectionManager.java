/**
 *This package represents system functionality. 
 */
package de.ofCourse.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import de.ofCourse.exception.InvalidDBTransferException;
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
     * number of connections to the database
     */
    private static int numberOfConnection;

    /**
     * Flag that is used for debugging with JUnit.<br>
     * If this flag is true, the DatabaseConnectionManager is sealed off from
     * all other classes like for e.g. the PropertyManager.
     */
    private static boolean debug = false;

    /**
     * Enables or disables the debugging mode.
     * 
     * @param debugging
     *            whether the debug mode is set
     */
    protected static void debugMode(boolean debugging) {
	debug = debugging;
    }

    /**
     * Constructor of the class DatabaseConnectionManager
     */
    private DatabaseConnectionManager() {
	freeConnections = Collections
		.synchronizedList(new LinkedList<Connection>());
	try {
	    Class.forName(dbDriver);
	} catch (ClassNotFoundException e) {
	    if (debug) {
		System.out.println("LOGGING MESSAGE:   "
			+ "Error occoured during"
			+ " loading the database driver!");
	    } else {
		LogHandler.getInstance().error(
			"Error occoured during"
				+ " loading the database driver!");
	    }
	}

    }

    /**
     * Returns the connection you need for database access.
     * 
     * @return connection for database access
     */
    public synchronized Connection getConnection() {
	Connection connection = null;
	int indexLastElement;

	// There's a free connection
	if (!this.freeConnections.isEmpty()) {
	    indexLastElement = freeConnections.size() - 1;
	    connection = freeConnections.get(indexLastElement);
	    freeConnections.remove(indexLastElement);
	} else {
	    // There's no free connection
	    try {
		wait(1000);
	    } catch (InterruptedException e) {
		if (debug) {
		    System.out.println("LOGGING MESSAGE:   "
			    + "Error occured during "
			    + "waiting for a connection.");
		} else {
		    LogHandler.getInstance().error(
			    "Error occured during waiting"
				    + " for a connection.");
		}
	    }

	}

	/*
	 * Calculates if there are as much as connections in use as granted by
	 * the configuration
	 */
	int difference = numberOfConnection
		- (freeConnections.size() + numberOfConnectionsInUse);

	/*
	 * If there's no active connection in <code>the freeConnections<\code>
	 * list and not the full number of connections are active
	 */
	if (!this.isConnectionActive(connection) && difference > 0) {
	    connection = establishConnection();
	    if (debug) {
		System.out.println("LOGGING MESSAGE:   "
			+ "New Connection established.");
	    } else {
		LogHandler.getInstance().debug("New Connection established.");
	    }
	}

	// Check the new connection before giving it free
	if (!this.isConnectionActive(connection)) {
	    if (debug) {
		System.out.println("LOGGING MESSAGE:   "
			+ "Not able to get a active "
			+ "connection to the database.");
	    } else {
		LogHandler.getInstance().error(
			"Not able to get a active connection to the database.");
	    }
	    throw new InvalidDBTransferException();
	}

	++numberOfConnectionsInUse;
	if (debug) {
	    System.out.println("LOGGING MESSAGE:   " + "Connection returned.");
	} else {
	    LogHandler.getInstance().debug("Connection returned.");
	}

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
		if (debug) {
		    System.out.println("LOGGING MESSAGE:   "
			    + "Connection released.");
		} else {
		    LogHandler.getInstance().debug("Connection released.");
		}
	    }
	} catch (SQLException e) {
	    if (debug) {
		System.out.println("LOGGING MESSAGE:   "
			+ "Error occured during releasing the connection.");
	    } else {
		LogHandler.getInstance().error(
			"Error occured during releasing the connection.");
	    }
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

	    if (debug) {
		numberOfConnection = 3;
	    } else {
		numberOfConnection = Integer.parseInt(PropertyManager
			.getInstance()
			.getPropertyConfiguration("dbconnections"));
		System.out.println(numberOfConnection);
	    }

	    for (int i = 0; i < numberOfConnection; ++i) {
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

	if (debug) {
	    try {
		connection = DriverManager.getConnection(
			"jdbc:postgresql://localhost:12345/fuchstob",
			"fuchstob", "eX4Cooth");
		connection.setAutoCommit(false);
	    } catch (SQLException e) {
		System.out.println("LOGGING MESSAGE:   "
			+ "Error occured during establishing a database"
			+ "connection. Please check whether the login"
			+ " credentials are set correctly and the"
			+ " connection to the database is alive.");
	    }
	} else {
	    try {
		connection = DriverManager.getConnection(
			"jdbc:postgresql://"
				+ PropertyManager.getInstance()
					.getPropertyConfiguration("dbhost")
				+ ":"
				+ PropertyManager.getInstance()
					.getPropertyConfiguration("dbport")
				+ "/"
				+ PropertyManager.getInstance()
					.getPropertyConfiguration("dbname"),
			PropertyManager.getInstance().getPropertyConfiguration(
				"dbuser"), PropertyManager.getInstance()
				.getPropertyConfiguration("dbpassword"));
		connection.setAutoCommit(false);
	    } catch (SQLException e) {
		LogHandler.getInstance().error(
			"Error occured during establishing a database"
				+ "connection. Please check whether the login"
				+ " credentials are set correctly  and the"
				+ " connection to the database is alive.");

	    }
	}
	return connection;
    }

    /**
     * If the JVM will crash, this method executes last statements and closes
     * all connections.
     */
    public void shutDown() {
	ListIterator<Connection> it = freeConnections.listIterator(0);

	while (it.hasNext()) {
	    Connection connection = it.next();
	    if (connection != null) {
		try {
		    connection.close();
		    if (debug) {
			System.out.println("LOGGING MESSAGE:   "
				+ "Connection closed.");
		    } else {
			LogHandler.getInstance().error("Connection closed.");
		    }
		} catch (SQLException e) {
		    if (debug) {
			System.out.println("LOGGING MESSAGE:   "
				+ "Error occured during closing"
				+ " the connections to the database.");
		    } else {
			LogHandler.getInstance().error(
				"Error occured during closing"
					+ " the connections to the database.");
		    }
		}
	    }
	}
	freeConnections.clear();
    }

    /**
     * Checks whether the connection to the database is active.
     * 
     * @param connection
     *            connection to the database
     * @return true, if the connection is active<br>
     *         false, otherwise
     */
    private boolean isConnectionActive(Connection connection) {
	boolean active = false;

	if (connection != null) {
	    PreparedStatement stmt = null;
	    String query = "SELECT 0;";

	    try {
		stmt = connection.prepareStatement(query);
		stmt.execute();
		stmt.close();
		active = true;
	    } catch (SQLException e) {
		this.freeConnections.remove(connection);
		active = false;
	    }

	}
	return active;
    }

}