package de.ofCourse.Database.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Cycle;
import de.ofCourse.system.Connection;
import de.ofCourse.system.LogHandler;
import de.ofCourse.system.Transaction;

/**
 * Provides methods for transactions with the cycles stored in the database such
 * as creating, retrieving or updating cycles.
 * 
 * <p>
 * Each method has a Transaction parameter, which contains the SQL connection to
 * the database, in order to assure that multiple, consecutive method calls
 * within a certain method use the same connection.
 * </p>
 * 
 * <p>
 * This class is required in the business logic of the system, more precisely in
 * the ManagedBeans of the package <code>de.ofCourse.action</code>.
 * </p>
 * 
 * @author Tobias Fuchs
 *
 */
public class CycleDAO {

    private static final String createQuery = "INSERT INTO \"cycles\""
		+ " (course_id, period, cycle_end)" + " VALUES"
		+ " (?, ?::period, ?) RETURNING id";
    
    /**
     * Creates a new cycle an returns the id of the created cycle.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param courseID
     *            the id of the course the cycle belongs to
     * @param cycle
     *            the cycle to create
     * @return the id of the created cycle
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * @author Tobias Fuchs
     */
    public static int createCycle(Transaction trans, int courseID, Cycle cycle)
	    throws InvalidDBTransferException {
	int cycleID = 0;
	

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();
	


	try (PreparedStatement stmt = conn.prepareStatement(createQuery)) {
	    stmt.setInt(1, courseID);
	    if (cycle.getTurnus() == 1) {
		stmt.setString(2, "DAYS");
	    } else if (cycle.getTurnus() == 7) {
		stmt.setString(2, "WEEKS");
	    }
	    stmt.setInt(3, cycle.getNumberOfUnits());
	    try(ResultSet res =  stmt.executeQuery()){
	    
	    res.next();
	    cycleID = res.getInt("id");
	  
	    }
	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "Error occured during creating a new cycle.");
	    throw new InvalidDBTransferException();
	}
	return cycleID;
    }

    /**
     * Fetches the id of a cycle by a passed course unit id.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param courseUnitId
     *            the id of the course unit
     * @return the id of the cycle
     * @author Tobias Fuchs
     */
    public static int getCycleId(Transaction trans, int courseUnitId)
	    throws InvalidDBTransferException {
	int cycleID = 0;
	String query = "SELECT cycle_id FROM \"course_units\" WHERE id=?";
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	ResultSet res2 = null;

	try (PreparedStatement stmt = conn.prepareStatement(query)) {
	    stmt.setInt(1, courseUnitId);

	    res2 = stmt.executeQuery();
	    res2.next();
	    cycleID = res2.getInt(1);

	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "Error occured during fetching the cycle id.");
	    throw new InvalidDBTransferException();
	}
	return cycleID;
    }

    /**
     * @param trans
     * @param cycleId
     * @throws InvalidDBTransferException
     */
    public static void deleteCycle(Transaction trans, int cycleId)
	    throws InvalidDBTransferException {

	String deleteQuery = "DELETE FROM \"cycles\" WHERE id=?";

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	try (PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

	    stmt.setInt(1, cycleId);
	    stmt.executeUpdate();

	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "Error occured during deleting cycle!");
	    throw new InvalidDBTransferException();
	}
    }
}
