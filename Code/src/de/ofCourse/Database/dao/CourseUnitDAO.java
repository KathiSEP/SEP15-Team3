/**
 * This package represents the Data Access Objects of the ofCourse system.
 */
package de.ofCourse.Database.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Address;
import de.ofCourse.model.CourseUnit;
import de.ofCourse.model.Cycle;
import de.ofCourse.model.PaginationData;
import de.ofCourse.model.User;
import de.ofCourse.system.Connection;
import de.ofCourse.system.LogHandler;
import de.ofCourse.system.Transaction;

/**
 * Provides methods for transactions with the course units stored in the
 * database such as creating, retrieving, updating or deleting course units.
 * Also adds or removes users to a course unit .
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
			+ " min_participants, fee, start_time, end_time, description, course_instructor_id, cycle_id)"
			+ " VALUES (?, ?, ?::TEXT, ?, ?, ?, ?, ?::TEXT, ?, ?) RETURNING id";
		stmt = conn.prepareStatement(query);
		stmt.setInt(10, courseUnit.getCycle().getCycleID());
	    } else {
		query = "INSERT INTO \"course_units\""
			+ " (course_id, max_participants, titel,"
			+ " min_participants, fee, start_time, end_time, description, course_instructor_id)"
			+ " VALUES (?, ?, ?::TEXT, ?, ?, ?, ?, ?::TEXT, ?) RETURNING id";

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
	    stmt.setInt(9, courseUnit.getCourseAdmin().getUserID());
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
	    if (unit.getAddress().getLocation().length() < 1
		    || unit.getAddress().getLocation() == null) {
		stmt.setString(7, null);
	    } else {
		stmt.setString(7, unit.getAddress().getLocation());
	    }
	    stmt.executeUpdate();
	    stmt.close();
	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "Error occured during creating a new course unit address.");
	    throw new SQLException();
	}

    }

    /**
     * @param trans
     * @param userId
     * @return
     * @author Sebastian Schwarz
     */
    public static boolean userWantsToBeInformed(Transaction trans, int userID,
	    int courseID) {
	boolean wantsToBeInformed = false;

	String query = "SELECT * FROM \"inform_users\" WHERE user_id=? AND course_id=?";

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();
	PreparedStatement stmt = null;

	try {
	    stmt = conn.prepareStatement(query);
	    stmt.setInt(1, userID);
	    stmt.setInt(2, courseID);
	    return stmt.execute();

	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "Error occoured during checking whether"
			    + " a user wants to be informed in case"
			    + " of changes of the course unit.");
	    e.printStackTrace();
	    throw new InvalidDBTransferException();
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
     * @author Sebastian Schwarz
     */
    public static CourseUnit getCourseUnit(Transaction trans, int courseUnitID)
	    throws InvalidDBTransferException {
	CourseUnit requestedCourseUnit = new CourseUnit();
	Address courseUnitAddress = new Address();
	Cycle courseCycle = new Cycle();
	User admin = new User();

	int adminID;
	Timestamp stamp;
	Date date;

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	String courseUnitRequest = "SELECT * FROM \"course_units\" WHERE id=?";

	try {
	    PreparedStatement pS = conn.prepareStatement(courseUnitRequest);
	    pS.setInt(1, courseUnitID);
	    ResultSet resultSet = pS.executeQuery();
	    if (resultSet.next()) {

		// Setting attributs of courseUnit
		requestedCourseUnit.setCourseID(resultSet.getInt("course_ID"));
		requestedCourseUnit.setCourseUnitID(courseUnitID);
		requestedCourseUnit.setDescription(resultSet
			.getString("description"));
		stamp = resultSet.getTimestamp("end_time");
		date = new Date(stamp.getYear(), stamp.getMonth(),
			stamp.getDate(), stamp.getHours(), stamp.getMinutes());
		requestedCourseUnit.setEndtime(date);
		stamp = resultSet.getTimestamp("start_time");
		date = new Date(stamp.getYear(), stamp.getMonth(),
			stamp.getDate(), stamp.getHours(), stamp.getMinutes());
		requestedCourseUnit.setStartime(date);
		requestedCourseUnit.setMaxUsers(resultSet
			.getInt("max_participants"));
		requestedCourseUnit.setMinUsers(resultSet
			.getInt("min_participants"));
		requestedCourseUnit.setPrice(resultSet.getInt("fee"));
		requestedCourseUnit.setTitle(resultSet.getString("titel"));
		adminID = resultSet.getInt("course_instructor_id");
		LogHandler.getInstance().debug(
			"Initialisieren von Kurseinheiten abgeschlossen");

		// This is needed because the Database can return a Object null
		Integer cycleIDexists = (Integer) resultSet
			.getObject("cycle_id");
		if (cycleIDexists != null) {
		    int cycleID = resultSet.getInt("cycle_id");
		    pS.close();
		    // resultSet.close();

		    String cycleRequest = "SELECT * FROM \"cycles\" WHERE id=?";
		    PreparedStatement pSCycle = conn
			    .prepareStatement(cycleRequest);
		    pSCycle.setInt(1, cycleID);
		    ResultSet resultSetCycle = pSCycle.executeQuery();
		    if (resultSetCycle.next()) {
			courseCycle.setCourseID(resultSetCycle
				.getInt("course_id"));
			courseCycle.setCycleID(cycleID);
			courseCycle.setNumberOfUnits(resultSetCycle
				.getInt("cycle_end"));

			// TODO EMUMS anpassen EVTL
			String period = resultSetCycle.getString("period");
			switch (period) {
			case "DAYS":
			    courseCycle.setTurnus(1);
			    break;
			case "WEEKS":
			    courseCycle.setTurnus(7);
			    break;
			}
		    }
		    pSCycle.close();
		    // resultSetCycle.close();
		    requestedCourseUnit.setCycle(courseCycle);
		    LogHandler.getInstance().debug(
			    "Initialisieren von Cycle abgeschlossen");
		} // TODO evtl ein else fall mit cycle -1 init

		String addressRequest = "SELECT * FROM \"course_unit_addresses\" WHERE course_unit_id=?";

		PreparedStatement pSAddress = conn
			.prepareStatement(addressRequest);
		pSAddress.setInt(1, courseUnitID);
		ResultSet resultSetAddress = pSAddress.executeQuery();

		if (resultSetAddress.next()) {
		    courseUnitAddress.setCity(resultSetAddress
			    .getString("city"));
		    courseUnitAddress.setCountry(resultSetAddress
			    .getString("country"));
		    courseUnitAddress.setHouseNumber(resultSetAddress
			    .getInt("house_nr"));
		    courseUnitAddress.setId(resultSetAddress.getInt("id"));
		    courseUnitAddress.setStreet(resultSetAddress
			    .getString("street"));
		    courseUnitAddress.setZipCode(Integer
			    .parseInt(resultSetAddress.getString("zip_code")));
		    courseUnitAddress.setLocation(resultSetAddress
			    .getString("location"));
		    LogHandler.getInstance().debug(
			    "Initialisieren von Addresse abgeschlossen");

		    requestedCourseUnit.setAddress(courseUnitAddress);
		    LogHandler.getInstance().debug(
			    "Initialisieren von Addresse abgeschlossen");
		}
		admin = UserDAO.getUser(trans, adminID);
		requestedCourseUnit.setCourseAdmin(admin);
	    }
	    return requestedCourseUnit;
	} catch (SQLException e) {
	    LogHandler.getInstance()
		    .error("Error occured during getCourseUnit");
	    throw new InvalidDBTransferException();
	}

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
	String direction = getSortDirection(pagination.isSortAsc());
	ArrayList<CourseUnit> courseUnits = new ArrayList<CourseUnit>();

	String courseUnitsQuery = "SELECT * FROM \"course_units\", \"course_unit_addresses\" WHERE "
		+ "course_units.course_id = ? AND course_units.id = course_unit_addresses.course_unit_id "
		+ " ORDER BY %s %s LIMIT ? OFFSET ?";

	int offset = pagination.getElementsPerPage()
		* pagination.getCurrentPageNumber();

	courseUnitsQuery = String.format(courseUnitsQuery, "titel", direction);
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();
	PreparedStatement statement = null;
	ResultSet resultSet;

	try {
	    statement = conn.prepareStatement(courseUnitsQuery);
	    statement.setInt(1, courseID);
	    statement.setInt(2, pagination.getElementsPerPage());
	    statement.setInt(3, offset);
	    resultSet = statement.executeQuery();

	    while (resultSet.next()) {
		CourseUnit unit = new CourseUnit();

		unit.setCourseID(courseID);
		unit.setCourseUnitID(resultSet.getInt("id"));

		if (resultSet.getString("titel") != null) {
		    unit.setTitle(resultSet.getString("titel"));
		} else {
		    unit.setTitle("Ohne Titel");
		}

		if (resultSet.getString("description") != null) {
		    unit.setDescription(resultSet.getString("description"));
		} else {
		    unit.setDescription("Ohne Beschreibung");
		}

		unit.setMaxUsers(resultSet.getInt("max_participants"));
		unit.setMinUsers(resultSet.getInt("min_participants"));
		unit.setPrice(resultSet.getFloat("fee"));
		unit.setStartime(resultSet.getDate("start_time"));
		unit.setEndtime(resultSet.getDate("end_time"));
		unit.setNumberOfUsers(getNumberOfParticipants(trans,
			unit.getCourseUnitID()));

		Address unitAddress = new Address();

		unitAddress.setCity(resultSet.getString("city"));
		unitAddress.setCountry(resultSet.getString("country"));
		unitAddress.setZipCode(resultSet.getInt("zip_code"));

		if (resultSet.getString("street") != null) {
		    unitAddress.setStreet(resultSet.getString("street"));
		} else {
		    unitAddress.setStreet("Ohne Straße");
		}

		if (resultSet.getString("location") != null) {
		    unitAddress.setLocation(resultSet.getString("location"));
		} else {
		    unitAddress.setLocation("Ohne Ort");
		}

		if (resultSet.getInt("house_nr") > 0) {
		    unitAddress.setHouseNumber(resultSet.getInt("house_nr"));
		} else {
		    unitAddress.setHouseNumber(0);
		}

		unit.setAddress(unitAddress);
		courseUnits.add(unit);
	    }
	    statement.close();
	    resultSet.close();
	} catch (SQLException e) {
	    LogHandler
		    .getInstance()
		    .error("Error occoured in CourseUnitFromCourse from CourseUnitDAO");
	    throw new InvalidDBTransferException();
	}
	return courseUnits;
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
     * @author Tobias Fuchs
     */
    public static void updateCourseUnit(Transaction trans, CourseUnit courseUnit)
	    throws InvalidDBTransferException {
	String updateUnitQuery = "UPDATE \"course_units\" SET course_id=?,"
		+ " max_participants=?, titel=?::TEXT,"
		+ " min_participants=?, fee=?, start_time=?,"
		+ " end_time=?, description=?::TEXT, course_instructor_id=?"
		+ " WHERE id=?";
	String updateUnitAddressQuery = "UPDATE \"course_unit_addresses\" SET "
		+ "country=?, city=?, zip_code=?,"
		+ " street=?, house_nr=?, location=?::TEXT WHERE course_unit_id=?";

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();
	PreparedStatement stmt = null;

	try {
	    stmt = conn.prepareStatement(updateUnitQuery);
	    stmt.setInt(1, courseUnit.getCourseID());
	    stmt.setInt(2, courseUnit.getMaxUsers());
	    stmt.setString(3, courseUnit.getTitle());
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
	    stmt.setInt(9, courseUnit.getCourseAdmin().getUserID());
	    stmt.setInt(10, courseUnit.getCourseUnitID());
	    stmt.executeUpdate();

	    stmt = conn.prepareStatement(updateUnitAddressQuery);
	    stmt.setString(1, courseUnit.getAddress().getCountry());
	    stmt.setString(2, courseUnit.getAddress().getCity());
	    stmt.setString(3, courseUnit.getAddress().getZipCode().toString());
	    if (courseUnit.getAddress().getStreet().length() < 1
		    || courseUnit.getAddress().getStreet() == null) {
		stmt.setString(4, null);
	    } else {
		stmt.setString(4, courseUnit.getAddress().getStreet());
	    }
	    stmt.setInt(5, courseUnit.getAddress().getHouseNumber());
	    if (courseUnit.getAddress().getLocation().length() < 1
		    || courseUnit.getAddress().getLocation() == null) {
		stmt.setString(6, null);
	    } else {
		stmt.setString(6, courseUnit.getAddress().getLocation());
	    }
	    stmt.setInt(7, courseUnit.getCourseUnitID());
	    stmt.executeUpdate();
	    stmt.close();
	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "Error occured during updating a course unit.");
	    throw new InvalidDBTransferException();
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
    public static List<Integer> getIdsCourseUnitsOfCycle(Transaction trans,
	    int courseUnitId) {
	ArrayList<Integer> ids = new ArrayList<Integer>();
	// The queries to execute
	String queryCycleId = "SELECT cycle_id FROM \"course_units\" WHERE"
		+ " id=?";
	String queryUnitIds = "SELECT id FROM \"course_units\" WHERE"
		+ " cycle_id=? AND course_units.start_time >= CURRENT_DATE";

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
     * Returns the price of a course unit.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param unitId
     *            the id of the unit
     * @return the price of the unit<br>
     *         -1, if there goes something wrong
     * @author Tobias Fuchs
     */
    public static float getPriceOfUnit(Transaction trans, int unitId) {
	String query = "SELECT fee FROM \"course_units\" WHERE id=?";
	float price = -1;
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();
	PreparedStatement stmt = null;
	ResultSet res = null;
	try {
	    stmt = conn.prepareStatement(query);
	    stmt.setInt(1, unitId);
	    res = stmt.executeQuery();

	    res.next();
	    price = res.getFloat("fee");
	    stmt.close();
	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "Error during fetching " + "the price of course unit.");
	    throw new InvalidDBTransferException();
	}
	return price;

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
     * @author Sebastian Schwarz
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
     * @author Sebastian Schwarz
     */
    public static void removeUserFromCourseUnit(Transaction trans, int userID,
	    int courseUnitID) throws InvalidDBTransferException {

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	String deleteFromCourseUnit = "DELETE FROM \"course_unit_participants\" WHERE participant_id = ? AND course_unit_id = ?";

	try {
	    CourseDAO.setRelationMethode(userID, courseUnitID, conn,
		    deleteFromCourseUnit);
	    LogHandler.getInstance().debug(
		    "User:" + userID + "succesfully deleted from CourseUnit:"
			    + courseUnitID);
	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "User:" + userID + "couldnt be deleted from CourseUnit:"
			    + courseUnitID);
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
     * 
     * @author Patrick Cretu
     */
    public static List<CourseUnit> getCourseUnitsOf(Transaction trans,
	    int userID) throws InvalidDBTransferException {
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();
	PreparedStatement stmt = null;
	ResultSet rst = null;
	List<CourseUnit> result = null;
	String getCourseUnits = "SELECT \"course_units\".id, \"course_units\".course_id, \"course_units\".titel, \"course_units\".fee "
		+ "FROM \"course_units\", \"users\", \"course_unit_participants\" "
		+ "WHERE \"users\".id = \"course_unit_participants\".participant_id "
		+ "AND \"course_unit_participants\".course_unit_id = \"course_units\".id "
		+ "AND \"users\".id = ?";

	try {
	    stmt = conn.prepareStatement(getCourseUnits);
	    stmt.setInt(1, userID);

	    System.out.println(stmt.toString());

	    rst = stmt.executeQuery();
	    result = getResult(rst);
	} catch (SQLException e) {
	    LogHandler
		    .getInstance()
		    .error("SQL Exception occoured during getCoursesInPeriod(java.sql.Connection conn, int limit, int offset, String orderParam, String query)");
	    throw new InvalidDBTransferException();
	} finally {
	    if (rst != null) {
		try {
		    rst.close();
		} catch (SQLException e) {
		    LogHandler
			    .getInstance()
			    .error("SQL Exception occoured during getCoursesInPeriod(java.sql.Connection conn, int limit, int offset, String orderParam, String query)");
		    throw new InvalidDBTransferException();
		}
	    }
	    if (stmt != null) {
		try {
		    stmt.close();
		} catch (SQLException e) {
		    LogHandler
			    .getInstance()
			    .error("SQL Exception occoured during getCoursesInPeriod(java.sql.Connection conn, int limit, int offset, String orderParam, String query)");
		    throw new InvalidDBTransferException();
		}
	    }
	}
	return result;
    }

    /**
     * 
     * @param rst
     * @return
     * @throws InvalidDBTransferException
     * 
     * @author Patrick Cretu
     */
    private static List<CourseUnit> getResult(ResultSet rst)
	    throws InvalidDBTransferException {
	List<CourseUnit> result = new ArrayList<CourseUnit>();
	try {
	    int cols = rst.getMetaData().getColumnCount();

	    while (rst.next()) {
		int i = 1;
		List<Object> tuple = new ArrayList<Object>();
		CourseUnit unit = new CourseUnit();
		while (i <= cols) {
		    Object o = rst.getObject(i);
		    tuple.add(o);
		    i++;
		}
		setProperties(unit, tuple);
		result.add(unit);
	    }
	    if (!result.isEmpty()) {
		return result;
	    }
	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "SQL Exception occoured during getResult(ResultSet rst)");
	    throw new InvalidDBTransferException();
	}
	return null;
    }

    /**
     * 
     * @param unit
     * @param tuple
     * 
     * @author Patrick Cretu
     */
    private static void setProperties(CourseUnit unit, List<Object> tuple) {
	unit.setCourseUnitID((Integer) tuple.get(0));
	unit.setCourseID((Integer) tuple.get(1));
	unit.setTitle((String) tuple.get(2));
	BigDecimal bg = (BigDecimal) tuple.get(3);
	unit.setPrice(bg.floatValue());
    }

    /**
     * Counts the number of course units of a course
     * 
     * @param trans
     * @param courseID
     * @return
     * @throws InvalidDBTransferException
     * @author Ricky Strohmeier
     */
    public static int getNumberOfCourseUnits(Transaction trans, int courseID)
	    throws InvalidDBTransferException {
	int numberOfCourseUnits = 0;
	String courseUnitQuery = "SELECT COUNT(*) FROM \"course_units\" WHERE course_id = ?";

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	PreparedStatement statement = null;
	try {
	    statement = conn.prepareStatement(courseUnitQuery);
	    statement.setInt(1, courseID);
	    ResultSet resultSet = statement.executeQuery();
	    resultSet.next();
	    numberOfCourseUnits = resultSet.getInt(1);
	} catch (SQLException e) {
	    LogHandler
		    .getInstance()
		    .error("Error occoured during fetching the number of courses of a certain user.");
	    throw new InvalidDBTransferException();
	}
	return numberOfCourseUnits;
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
	String direction = getSortDirection(pagination.isSortAsc());
	ArrayList<User> participants = new ArrayList<User>();
	String query = "SELECT id, name, first_name, nickname FROM"
		+ " users WHERE users.id IN"
		+ " (SELECT participant_id FROM course_unit_participants"
		+ " WHERE course_unit_id = ?) ORDER BY %s %s"
		+ " LIMIT ? OFFSET ?";

	query = String.format(query, pagination.getSortColumn(), direction);
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
	    stmt.setInt(2, limit);
	    stmt.setInt(3, offset);
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
		fetchedUser.setUsername(fetchedParticipants
			.getString("nickname"));
		participants.add(fetchedUser);
	    }
	    stmt.close();
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
