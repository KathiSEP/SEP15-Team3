/**
 *This package represents system functionality. 
 */
package de.ofCourse.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.ListIterator;

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
public class DatabaseConnectionManager implements Runnable {

    /**
     * List of free connections
     */
    private final Deque<Connection> freeConnections;

    /**
     * List of used connections
     */
    private final List<Connection> usedConnections;

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
     * Constructor of the class DatabaseConnectionManager
     */
    private DatabaseConnectionManager() {
	freeConnections = new ArrayDeque<Connection>();
	usedConnections = new ArrayList<Connection>();
	try {
	    Class.forName(dbDriver);
	} catch (ClassNotFoundException e) {
	    LogHandler.getInstance().error(
		    "Error occoured during" 
	            + " loading the database driver!");
	}

    }

    /**
     * Returns the connection you need for database access.
     * 
     * @return connection for database access
     */
    public synchronized Connection getConnection() {
	Connection connection = null;

	/*
	 * Calculates if there are as much as connections in use as granted by
	 * the configuration
	 */
	int difference = numberOfConnection
		- (freeConnections.size() + usedConnections.size());

	// There's a free connection
	if (!freeConnections.isEmpty()) {
	    
	    //Get the connection from freeConnections
	    connection  = freeConnections.pop();
	        
	        //If the connection is not active, it is already removed from 
	        // freeConnections, so there are not the number of connections
	        // established as determined
	        // All waiting threads a notified and in the next run of 
	        // getConnection() a new connection is established to replace the
	        // inactive one
		if (!isConnectionActive(connection)) {
		    
		    try{
			connection.close();
		    }
		    catch(SQLException e){
			LogHandler.getInstance().error("Error during closing connection!");
		    }
		    notifyAll();
		    return(getConnection());
		  } else {
		    // If the connection is active, it is returned
		    usedConnections.add(connection);
		    LogHandler.getInstance().debug("Connection returned");
		    return connection;
		  }
	   
	} else {
	    //If there are not as much connections established as determined
	    for (int i = 0; i < difference; ++i) {
		createConnectionThread();
	    }

	    //Wait there's a free connection
	    try {
	        wait();
	      } catch(InterruptedException ie) {
		  throw new RuntimeException();
	      }
	     
	      // A connection was released or a new one created
	      return getConnection();  
	}
    }
	

    
    
    
    /**
     * Starts a new thread, which has the job to create a new connection
     */
    private void createConnectionThread() {
	try {
	    Thread thread = new Thread(this);
	    thread.start();
	} catch (OutOfMemoryError e) {
	    LogHandler.getInstance().error("The connection create thread" 
	                                    + " is out of memory.");
	}
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
	try {
	    Connection connection = establishConnection();
	    synchronized (this) {
		freeConnections.push(connection);
		notifyAll();
		LogHandler.getInstance().debug("Created");
	    }
	} catch (Exception e) { 
	    LogHandler.getInstance().error("Error occured during running " 
	                                   + "connection thread");
	}
    }

    /**
     * Releases the connection after it has been used.
     */
    public synchronized void releaseConnection(Connection connection) {
	try {
	    if (!connection.isClosed() && connection != null) {

		usedConnections.remove(connection);
		freeConnections.push(connection);
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
    public synchronized static DatabaseConnectionManager getInstance() {
	if (databaseConnectionManager == null) {
	    databaseConnectionManager = new DatabaseConnectionManager();

	    // In case of debug only three connections are established
	    // to preserve database ressources
	    if (debug) {
		numberOfConnection = 3;
	    } else {
		numberOfConnection = Integer.parseInt(PropertyManager
			.getInstance()
			.getPropertyConfiguration("dbconnections"));
	    }

	    for (int i = 0; i < numberOfConnection; ++i) {
		Connection conn = establishConnection();
		if (conn != null) {
		    databaseConnectionManager.freeConnections.push(conn);
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

	// In case of tests it is attempted to establish a database connection
	// to
	// test database to preserve the working database from unnecessary
	// demands
	if (debug) {
	    try {

		connection = DriverManager.getConnection(
			"jdbc:postgresql://localhost:12345/fuchstob",
			"fuchstob", "eX4Cooth");
		connection.setAutoCommit(false);

	    } catch (SQLException e) {
		LogHandler.getInstance().fatal(
			"Error occured during establishing a database"
				+ "connection. Please check whether the login"
				+ " credentials are set correctly  and the"
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
		LogHandler.getInstance().fatal(
			"Error occured during establishing a database"
				+ "connection. Please check whether the login"
				+ " credentials are set correctly  and the"
				+ " connection to the database is alive.");
		throw new RuntimeException();

	    }
	}
	return connection;
    }

    /**
     * If the JVM will crash, this method executes last statements and closes
     * all connections.
     */
    public void shutDown() {
	while (!freeConnections.isEmpty()) {
	    Connection connection = freeConnections.pop();

	    if (connection != null) {
		try {
		    connection.close();
		    LogHandler.getInstance().debug("Connection closed.");
		} catch (SQLException e) {
		    LogHandler.getInstance().error(
			    "Error occured during closing"
				    + " the connections to the database.");
		}

	    }
	}
	freeConnections.clear();
	ListIterator<Connection> it = usedConnections.listIterator(0);

	while (it.hasNext()) {
	    
	    Connection connection = it.next();
	    if (connection != null) {
		
		try {
		    connection.close();
		    LogHandler.getInstance().debug("Connection closed.");
		} catch (SQLException e) {  
			LogHandler.getInstance().error(
				"Error occured during closing"
				+ " the connections to the database.");
		}
		
	    }
	}
	usedConnections.clear();
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
	String query = "SELECT 0;";

	try {
	    if (connection != null && !connection.isClosed()) {

	        try (PreparedStatement stmt = connection.prepareStatement(query)) {
	    	stmt.execute();
	    	active = true;
	        } catch (SQLException e) {
	    	active = false;
	        }
	    }
	} catch (SQLException e) {
	    LogHandler.getInstance().error("Connection is not active.");
	}
	return active;
    }

    /**
     * Enables or disables the debugging mode.
     * 
     * @param debugging
     *            whether the debug mode is set
     */
    protected static void debugMode(boolean debugging) {
	debug = debugging;
    }
}