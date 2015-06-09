package de.ofCourse.Database.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Cycle;
import de.ofCourse.system.Connection;
import de.ofCourse.system.LogHandler;
import de.ofCourse.system.Transaction;

public class CycleDAO {

    /**
     * @param trans
     * @param courseID
     * @param cycle
     * @return
     * @throws InvalidDBTransferException
     */
    public static int createCycle(Transaction trans, int courseID, Cycle cycle)
	    throws InvalidDBTransferException {
	int cycleID = 0;

	String queryCreate = "INSERT INTO \"cycles\""
		+ " (course_id, period, cycle_end)" + " VALUES (?, ?::period, ?)";

	String querySelect = "SELECT id FROM \"cycles\" WHERE"
		+ " course_id=? AND period=?::period AND cycle_end=?";

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();
	PreparedStatement stmt = null;
	ResultSet res = null;

	try {
	    stmt = conn.prepareStatement(queryCreate);
	    stmt.setInt(1, courseID);
	    if (cycle.getTurnus() == 1) {
		stmt.setString(2, "DAYS");
	    } else if (cycle.getTurnus() == 7) {
		stmt.setString(2, "WEEKS");
	    }
	    stmt.setInt(3, cycle.getNumberOfUnits());
	    stmt.executeUpdate();

	    stmt = conn.prepareStatement(querySelect);
	    stmt.setInt(1, courseID);
	    if (cycle.getTurnus() == 1) {
		stmt.setString(2, "DAYS");
	    } else if (cycle.getTurnus() == 7) {
		stmt.setString(2, "WEEKS");
	    }
	    stmt.setInt(3, cycle.getNumberOfUnits());

	    res = stmt.executeQuery();

	    res.next();
	    cycleID = res.getInt("id");
	    stmt.close();
	    
	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "Error occured during creating a new cycle.");
	    e.printStackTrace();
	    throw new InvalidDBTransferException();
	}

	return cycleID;
    }

    /**
     * @param trans
     * @param cycle
     */
    public void updateCycle(Transaction trans, Cycle cycle) {

	String query = "UPDATE \"cycles\" "
		+ "SET course=? period=? cycle_end=? " + "WHERE id = ?";

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();
	PreparedStatement stmt = null;

	try {
	    stmt = conn.prepareStatement(query);
	    stmt.setInt(1, cycle.getCourseID());
	    if (cycle.getTurnus() == 1) {
		stmt.setString(2, "DAYS");
	    } else if (cycle.getTurnus() == 7) {
		stmt.setString(2, "WEEKS");
	    }
	    stmt.setInt(3, cycle.getNumberOfUnits());
	    stmt.executeUpdate();
	    stmt.close();
	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "Error occured during updating a cycle.");
	    e.printStackTrace();
	    throw new InvalidDBTransferException();
	}

    }

    /**
     * @param trans
     * @param courseUnitId
     * @return
     */
    public int getCycleId(Transaction trans, int courseUnitId) {

	int cycleID = 0;
	String query = "SELECT cycle_id FROM course_units WHERE id=?";
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();
	PreparedStatement stmt = null;
	ResultSet res = null;

	try {
	    stmt = conn.prepareStatement(query);
	    stmt.setInt(1, courseUnitId);

	    res = stmt.executeQuery();
	    stmt.close();

	    res.next();
	    cycleID = res.getInt("id");
	    
	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "Error occured during fetching the cycle id.");
	    e.printStackTrace();
	    throw new InvalidDBTransferException();
	}

	return cycleID;
    }
}
