/**
 * This package represents the Data Access Objects of the ofCourse system.
 */
package de.ofCourse.Database.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Course;
import de.ofCourse.model.CourseUnit;
import de.ofCourse.model.PaginationData;
import de.ofCourse.model.User;
import de.ofCourse.system.Connection;
import de.ofCourse.system.LogHandler;
import de.ofCourse.system.Transaction;

/**
 * Provides methods for transactions with the course units stored in the
 * database such as creating, retrieving, updating or deleting course units.
 * Also adds or removes users to a course unit.
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
 * @author Patrick Cretu
 *
 */
public class CourseUnitDAO {

    /**
     * Adds a course unit to the list of course units in the database. A course
     * unit contains the course's ID which it is assigned to.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param courseUnit
     *            the course unit to be added
     * @param courseID
     *            the course's ID which the course unit is assigned to
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     */
    public static void createCourseUnit(Transaction trans,
	    CourseUnit courseUnit, int courseID)
	    throws InvalidDBTransferException {
    }

    /**
     * Returns a course unit assigned to the specified ID.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param courseUnitID
     *            the course unit's ID
     * @return the course unit assigned to the course ID, or null if no such
     *         course unit was found
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     */
    public static CourseUnit getCourseUnit(Transaction trans, int courseUnitID)
	    throws InvalidDBTransferException {
	return null;
    }

    /**
     * Returns a list of a course's course units.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param courseID
     *            the course's ID
     * @param pagination
     *            the Pagination object which contains the amount of elements
     *            which are to be retrieved
     * @return the course's list of course units, or null if the course doesn't
     *         contain any course units
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     */
    public static List<CourseUnit> getCourseUnitsFromCourse(Transaction trans,
	    int courseID, PaginationData pagination)
	    throws InvalidDBTransferException {
	return null;
    }

    /**
     * Updates a course unit stored in the database. The course unit's
     * attributes are replaced by the ones of the passed course unit.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param courseUnit
     *            the course unit to be updated
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     */
    public static void updateCourseUnit(Transaction trans, CourseUnit courseUnit)
	    throws InvalidDBTransferException {
    }

    /**
     * Deletes a course unit which is assigned to the passed ID.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param courseUnitID
     *            the ID of the course unit to be deleted
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     */
    public static void deleteCourseUnit(Transaction trans, int courseUnitID)
	    throws InvalidDBTransferException {
    }

    /**
     * Adds a user to a course unit's list of participants.
     * <p>
     * A tuple of the user's ID and the course unit's ID is added to the table
     * containing course unit participants in the database.
     * </p>
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param userID
     *            the user's ID
     * @param courseUnitID
     *            the course unit's ID
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     */
    public static void addUserToCourseUnit(Transaction trans, int userID,
	    int courseUnitID) throws InvalidDBTransferException {
    }

    /**
     * Removes a user from a course unit's list of participants.
     * <p>
     * The tuple of the user's ID and the course unit's ID is removed from the
     * table of course unit participants in the database.
     * </p>
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param userID
     *            the user's ID
     * @param courseUnitID
     *            the course unit's ID
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     */
    public static void removeUserFromCourseUnit(Transaction trans, int userID,
	    int courseUnitID) throws InvalidDBTransferException {
    }

    /**
     * Returns a list of course units which the user with the passed ID
     * participates in.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param userID
     *            the user's ID
     * @return the list of course units which the user participates in, or null
     *         if the user doesn't participate in any course unit
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     */
    public static List<CourseUnit> getCourseUnitsOf(Transaction trans,
	    int userID) throws InvalidDBTransferException {
	return null;
    }

    /**
     * Returns the number of participants that attend the course unit with the
     * passed ID.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param courseUnitID
     *            the ID of the course unit
     * @return the number of participants of the course unit
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * @author Tobias Fuchs
     */
    public static int getNumberOfParticipants(Transaction trans,
	    int courseUnitId) throws InvalidDBTransferException {
	int numberParticipants = 0;
	String countQuery = "SELECT COUNT(*) FROM \"course_unit_participants\" "
		+ "WHERE course_unit_id = ?";

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	PreparedStatement stmt = null;
	try {
	    stmt = conn.prepareStatement(countQuery);
	    stmt.setInt(1, courseUnitId);
	    ResultSet resultSet = stmt.executeQuery();
	    resultSet.next();
	    numberParticipants = resultSet.getInt(1);
	    stmt.close();
	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "Error occoured during fetching the number"
			    + " of particpants of course unit with id: "
			    + courseUnitId + ".");
	    throw new InvalidDBTransferException();
	}
	return numberParticipants;
    }

    /**
     * Returns the offset calculated from the pagination information.
     * 
     * @param pagination
     *            contains the information for pagination
     * @return the calculated offset
     * @author Tobias Fuchs
     */
    private static int calculateOffset(PaginationData pagination) {
	int calculatedOffset = pagination.getElementsPerPage()
		* pagination.getCurrentPageNumber();
	return calculatedOffset;

    }

    /**
     * Returns a list of users which participate in the course unit with the
     * passed ID.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param pagination
     *            contains the information for pagination
     * @param courseUnitId
     *            the ID of the course unit the list of users which participate
     *            in the course unit, or a empty list if the no user attends
     *            this course unit
     * 
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * @author Tobias Fuchs
     */
    public static List<User> getParticipiantsOfCourseUnit(Transaction trans,
	    PaginationData pagination, int courseUnitId)
	    throws InvalidDBTransferException {
	ArrayList<User> participants = new ArrayList<User>();
	String query = "SELECT id, lastname, fistname FROM"
		+ " users WHERE users.id IN"
		+ " (SELECT participant_id FROM course_unit_participants"
		+ " WHERE course_id = ?) ORDER BY ? "
		+ getSortDirection(pagination.isSortAsc())
		+ " LIMIT ? OFFSET ?";

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	int offset = calculateOffset(pagination);
	PreparedStatement stmt = null;
	try {
	    
	    stmt = conn.prepareStatement(query);
	    stmt.setInt(1, courseUnitId);
	    stmt.setString(2, "'name'");
	    stmt.setInt(3, pagination.getElementsPerPage());
	    stmt.setInt(4, offset);
	    ResultSet fetchedParticipants = stmt.executeQuery();

	    // Fills the list coursesOf with courses from the database.
	    // At this time only id an title is set
	    while (fetchedParticipants.next()) {
		User fetchedUser = new User();
		
		fetchedUser.setUserID(fetchedParticipants.getInt("id"));

		if (fetchedCourses.getString("titel") != null) {
		    fetchedCourse.setTitle(fetchedCourses.getString("titel"));
		} else {
		    fetchedCourse.setTitle("Nicht angegeben");
		}
		coursesOf.add(fetchedCourse);
	    }

	   
	    
	} catch (SQLException e) {
	    LogHandler
		    .getInstance()
		    .error("Error occoured during fetching the next set of courses of a user.");
	    e.printStackTrace();
	    throw new InvalidDBTransferException();
	}
	return participants;
    }

    /**
     * Returns the sort direction as String so it can easiley be added to the
     * SQL statement.
     * 
     * @param isSortAsc
     *            whether the sort direction is ascending order
     * @return the sort direction as String
     * @author Tobias Fuchs
     */
    private static String getSortDirection(boolean isSortAsc) {
	if (isSortAsc) {
	    return "ASC";
	} else {
	    return "DESC";
	}
    }
}
