/**
 * This package represents the Data Access Objects of the ofCourse system.
 */
package de.ofCourse.Database.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.ofCourse.exception.InvalidDBTransferException;
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
 * @author Tobias Fuchs
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
     * @author Tobias Fuchs
     */
    public static void createCourseUnit(Transaction trans,
	    CourseUnit courseUnit, int courseID, boolean regular)
	    throws InvalidDBTransferException {
	String query;

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();
	PreparedStatement stmt = null;
	ResultSet res = null;
	try {
	    if (regular) {
		query = "INSERT INTO \"course_units\""
			+ " (course_id, max_participants, titel,"
			+ " min_participants, fee, start_time, end_time, description, cycle_id)"
			+ " VALUES (?, ?, ?::TEXT, ?, ?, ?, ?, ?::TEXT, ?) RETURNING id";
		stmt = conn.prepareStatement(query);
		stmt.setInt(9, courseUnit.getCycle().getCycleID());
	    } else {
		query = "INSERT INTO \"course_units\""
			+ " (course_id, max_participants, titel,"
			+ " min_participants, fee, start_time, end_time, description)"
			+ " VALUES (?, ?, ?::TEXT, ?, ?, ?, ?, ?::TEXT) RETURNING id";

		stmt = conn.prepareStatement(query);
	    }

	    stmt.setInt(1, courseID);
	    stmt.setInt(2, courseUnit.getMaxUsers());
	    if (courseUnit.getTitle().length() < 1
		    || courseUnit.getTitle() == null) {
		stmt.setString(3, null);
	    } else {
		stmt.setString(3, courseUnit.getTitle());
	    }
	    stmt.setInt(4, courseUnit.getMinUsers());
	    stmt.setFloat(5, courseUnit.getPrice());
	    stmt.setTimestamp(6, new java.sql.Timestamp(courseUnit
		    .getStartime().getTime()));
	    stmt.setTimestamp(7, new java.sql.Timestamp(courseUnit.getEndtime()
		    .getTime()));
	    if (courseUnit.getDescription().length() < 1
		    || courseUnit.getDescription() == null) {
		stmt.setString(8, null);
	    } else {
		stmt.setString(8, courseUnit.getDescription());
	    }
	    res = stmt.executeQuery();
	    res.next();
	    courseUnit.setCourseUnitID(res.getInt("id"));
	    res.close();
	    // Create the corresponding course unit address
	    createCourseUnitAddress(trans, courseUnit);
	    stmt.close();
	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "Error occured during creating a new course unit");
	    e.printStackTrace();
	    throw new InvalidDBTransferException();
	}
    }

    /**
     * Creates the course unit address for a given course unit in the database.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param unit
     *            the corresponding course unit
     * @throws SQLException
     *             if any error occurred during the execution of the method
     * @author Tobias Fuchs
     */
    private static void createCourseUnitAddress(Transaction trans,
	    CourseUnit unit) throws SQLException {
	String queryAddress = "INSERT INTO \"course_unit_addresses\""
		+ " (course_unit_id, country, city,"
		+ " zip_code, street, house_nr, location)"
		+ " VALUES (?, ?, ?, ?, ?, ?, ?::TEXT)";

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();
	PreparedStatement stmt = null;
	try {
	    stmt = conn.prepareStatement(queryAddress);
	    stmt.setInt(1, unit.getCourseUnitID());
	    stmt.setString(2, unit.getAddress().getCountry());
	    stmt.setString(3, unit.getAddress().getCity());
	    stmt.setString(4, unit.getAddress().getZipCode().toString());
	    if (unit.getAddress().getStreet().length() < 1
		    || unit.getAddress().getStreet() == null) {
		stmt.setString(5, null);
	    } else {
		stmt.setString(5, unit.getAddress().getStreet());
	    }
	    stmt.setInt(6, unit.getAddress().getHouseNumber());
	    if (unit.getLocation().length() < 1 || unit.getLocation() == null) {
		stmt.setString(7, null);
	    } else {
		stmt.setString(7, unit.getLocation());
	    }
	    stmt.executeUpdate();
	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "Error occured during creating a new course unit address.");
	    e.printStackTrace();
	    throw new SQLException();
	}

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
	String updateUnitQuery = "UPDATE \"course_units\" course_id=?, "
		+ "cycle_id=?, max_participants=?, titel=?::TEXT,"
		+ " min_participants=?, fee=?, start_time=?,"
		+ " end_time=?, description=?::TEXT WHERE id=?";
	String updateUnitAddressQuery = "UPDATE \"course_unit_addresses\" "
		+ "course_unit_id=?, country=?, city_?, zip_code=?,"
		+ " street=?, house_nr=?, location=?::TEXT";

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();
	PreparedStatement stmt = null;

	try {
	    stmt = conn.prepareStatement(updateUnitQuery);
	   
	    stmt.executeUpdate();

	    stmt = conn.prepareStatement(updateUnitAddressQuery);

	    stmt.executeUpdate();
	    stmt.close();
	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "Error occured during updating a course unit.");

	}
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
     * @author Tobias Fuchs
     */
    public static void deleteCourseUnit(Transaction trans, int courseUnitID)
	    throws InvalidDBTransferException {
	String queryUnit = "DELETE FROM \"course_units\" "
		+ "WHERE course_units.id=?";
	String queryAddress = "DELETE FROM \"course_unit_addresses\" "
		+ "WHERE course_unit_id=?";

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();
	PreparedStatement stmt = null;

	try {
	    stmt = conn.prepareStatement(queryUnit);
	    stmt.setInt(1, courseUnitID);
	    stmt.executeUpdate();

	    stmt = conn.prepareStatement(queryAddress);
	    stmt.setInt(1, courseUnitID);
	    stmt.executeUpdate();

	    stmt.close();
	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "Error occoured during deleting a course unit.");
	    throw new InvalidDBTransferException();
	}
    }

    /**
     * Returns a list of course unit ids that belong to the same cycle as the
     * given course unit.<br>
     * If there are no other course units in this cycle the list only contains
     * the given id.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param courseUnitID
     *            the ID of the course unit to be deleted
     * @return the list with the course unit ids that belong to a cycle
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * @author Tobias Fuchs
     */
    public List<Integer> getIdsCourseUnitsOfCycle(Transaction trans,
	    int courseUnitId) {
	ArrayList<Integer> ids = new ArrayList<Integer>();
	// The queries to execute
	String queryCycleId = "SELECT cycle_id FROM \"course_units\" WHERE id=?";
	String queryUnitIds = "SELECT id FROM \"course_units\" WHERE cycle_id=?";

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();
	PreparedStatement stmt = null;
	ResultSet res = null;
	int cycle_id;
	try {
	    stmt = conn.prepareStatement(queryCycleId);
	    stmt.setInt(1, courseUnitId);
	    res = stmt.executeQuery();
	    res.next();
	    cycle_id = res.getInt("cycle_id");

	    stmt = conn.prepareStatement(queryUnitIds);
	    stmt.setInt(1, cycle_id);
	    res = stmt.executeQuery();
	    while (res.next()) {
		ids.add(res.getInt("id"));
	    }
	    stmt.close();
	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "Error occured during fetching the id of "
			    + "course units which belong to the same cycle.");
	    throw new InvalidDBTransferException();
	}

	// Adds the given course unit id if it's not already in the list.
	if (!ids.contains(courseUnitId)) {
	    ids.add(courseUnitId);
	}
	return ids;
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
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	String addUserToCourseUnit = "INSERT INTO \"course_unit_participants\""
		+ " (participant_id,course_unit_id) VALUES (?,?)";

	try {
	    CourseDAO.setRelationMethode(userID, courseUnitID, conn,
		    addUserToCourseUnit);
	    LogHandler.getInstance().debug(
		    "User:" + userID + "succesfully added to CourseUnit:"
			    + courseUnitID);
	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "User:" + userID + "couldnt be added to CourseUnit:"
			    + courseUnitID);
	    throw new InvalidDBTransferException();
	}

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
        
        Connection connection = (Connection) trans;
        java.sql.Connection conn = connection.getConn();

        String removeUserFromCourse = "DELETE FROM \"course_unit_participants\" WHERE participant_id = ? AND course_unit_id = ?";

        try {
            CourseDAO.setRelationMethode(userID, courseUnitID, conn, removeUserFromCourse);
            LogHandler.getInstance().error(
                    "Deleting User:" + userID + " from course:" + courseUnitID
                            + "was succesfull");
        } catch (SQLException e) {
            LogHandler.getInstance().error(
                    "Error occured while trying to delete User:" + userID
                            + " from course:" + courseUnitID);
            throw new InvalidDBTransferException();
        }
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
	    PaginationData pagination, int courseUnitId, boolean all)
	    throws InvalidDBTransferException {
	ArrayList<User> participants = new ArrayList<User>();
	String query = "SELECT id, name, first_name FROM"
		+ " users WHERE users.id IN"
		+ " (SELECT participant_id FROM course_unit_participants"
		+ " WHERE course_unit_id = ?) ORDER BY ? "
		+ getSortDirection(pagination.isSortAsc())
		+ " LIMIT ? OFFSET ?";

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	int limit;
	int offset;

	if (all) {
	    limit = CourseUnitDAO.getNumberOfParticipants(trans, courseUnitId);
	    offset = 0;
	} else {
	    limit = pagination.getElementsPerPage();
	    offset = calculateOffset(pagination);
	}
	PreparedStatement stmt = null;
	try {

	    stmt = conn.prepareStatement(query);
	    stmt.setInt(1, courseUnitId);
	    stmt.setString(2, "name");
	    stmt.setInt(3, limit);
	    stmt.setInt(4, offset);
	    ResultSet fetchedParticipants = stmt.executeQuery();

	    while (fetchedParticipants.next()) {
		User fetchedUser = new User();
		fetchedUser.setUserID(fetchedParticipants.getInt("id"));
		if (fetchedParticipants.getString("name") != null) {
		    fetchedUser.setLastname(fetchedParticipants
			    .getString("name"));
		} else {
		    fetchedUser.setLastname("Nicht angegeben");
		}

		if (fetchedParticipants.getString("first_name") != null) {
		    fetchedUser.setFirstname(fetchedParticipants
			    .getString("first_name"));
		} else {
		    fetchedUser.setFirstname("Nicht angegeben");
		}
		participants.add(fetchedUser);
	    }
	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "Error occoured during fetching the"
			    + " next set of participants of a course.");
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
