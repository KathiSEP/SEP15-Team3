/**
 * This package represents the Data Access Objects of the ofCourse system.
 */
package de.ofCourse.Database.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.Part;

import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Course;
import de.ofCourse.model.CourseUnit;
import de.ofCourse.model.PaginationData;
import de.ofCourse.model.User;
import de.ofCourse.model.UserRole;
import de.ofCourse.system.Connection;
import de.ofCourse.system.LogHandler;
import de.ofCourse.system.Transaction;

/**
 * Provides methods for transactions with the courses stored in the database
 * such as creating, retrieving, updating or deleting courses. Also adds or
 * removes users and course leaders to a course.
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
 * @author Patrick Cretu, Katharina Hölzl
 *
 */
public class CourseDAO {

    /**
     * Checks, whether the inserted id of the course leader exists in the system
     * or not.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param courseLeaderID
     *            ID of the course leader
     * @return true if the id of the course leader exists in the system, else
     *         false
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * 
     * @author Katharina Hölzl
     */
    public static boolean courseLeaderExists(Transaction trans,
	    int courseLeaderID) throws InvalidDBTransferException {

	boolean courseLeaderExists = false;

	// PRepare SQL- INSERT and database connection
	PreparedStatement pS = null;
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	String sql = "SELECT * FROM users WHERE id = ? AND role != ?::role";

	// mögliche SQL-Injektion abfangen
	try {

	    // PreparedStatement befüllen, bei optionalen Feldern überprüfen,
	    // ob der Benutzer die Daten angegeben hat oder ob in die
	    // Datenbank null-Werte geschrieben werden müssen.
	    pS = conn.prepareStatement(sql);
	    pS.setInt(1, courseLeaderID);
	    pS.setString(2, UserRole.REGISTERED_USER.toString());

	    ResultSet res = pS.executeQuery();
	    courseLeaderExists = res.next();

	    pS.close();
	    res.close();
	} catch (SQLException e) {
	    LogHandler
		    .getInstance()
		    .error("SQL Exception occoured during executing createUser(Transaction trans, User user, String pwHash)");
	    throw new InvalidDBTransferException();

	}
	return courseLeaderExists;
    }

    /**
     * Adds a new course to the list of courses in the database.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param course
     *            the course to be added
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * 
     * @author Katharina Hölzl
     */
    public static int createCourse(Transaction trans, Course course, Part courseImage)
	    throws InvalidDBTransferException {

	final int errorGeneratingCourse = -1;

	int generatedCourseID = errorGeneratingCourse;

	// Prepare SQL- INSERT and database connection.
	PreparedStatement pS = null;
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	String sql = "Insert into \"courses\" (titel, max_participants, "
		+ "start_date, end_date, description, image) "
		+ "values (?, ?, ?, ?, ?, ?) RETURNING id";

	//  catch potential SQL-Injection
	try {

	    // Filling PreparedStatement, check in optional fields if the user 
            // has inserted the data or if the value null must be written into
            // the database.
	    pS = conn.prepareStatement(sql);
	    if (course.getTitle() == null || course.getTitle().length() < 1) {
		pS.setString(1, null);
	    } else {
		pS.setString(1, course.getTitle());
	    }
	    if (course.getMaxUsers() == null) {
		pS.setInt(2, 1);
	    } else {
		pS.setInt(2, course.getMaxUsers());
	    }
	    pS.setDate(3, new java.sql.Date(course.getStartdate().getTime()));
	    pS.setDate(4, new java.sql.Date(course.getEnddate().getTime()));
	    if (course.getDescription() == null
		    || course.getDescription().length() < 1) {
		pS.setString(5, null);
	    } else {
		pS.setString(5, course.getDescription());
	    }
	    InputStream inputStream = courseImage.getInputStream();
            pS.setBinaryStream(6, inputStream, courseImage.getSize());

	    ResultSet res = pS.executeQuery();
	    res.next();
	    generatedCourseID = res.getInt("id");
	    pS.close();
	    res.close();
	} catch (SQLException e) {
	    LogHandler
		    .getInstance()
		    .error("SQL Exception occoured during executing "
                            + "createCourse(Transaction trans, Course course, "
                            + "Part courseImage)");
	    throw new InvalidDBTransferException();

	} catch (IOException e) {
            LogHandler
            .getInstance()
            .error("SQL Exception occoured during executing "
                    + "createCourse(Transaction trans, Course course, "
                    + "Part courseImage)");
            throw new InvalidDBTransferException();
    }
	return generatedCourseID;
    }

    /**
     * 
     * @param trans
     * @param param
     * @param searchString
     * @return
     * @throws InvalidDBTransferException
     * 
     * @author Patrick Cretu
     */
    public static int getNumberOfCourses(Transaction trans, String param,
	    String searchString) throws InvalidDBTransferException {
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();
	String query = null;
	switch (param) {
	case "day":
	    query = "SELECT COUNT(*) FROM \"courses\", \"course_units\" "
		    + "WHERE \"course_units\".start_time::date = current_date "
		    + "AND \"course_units\".course_id = \"courses\".id";
	    return countCourses(conn, query);
	case "week":
	    query = "SELECT COUNT(*) FROM \"courses\", \"course_units\" "
		    + "WHERE \"course_units\".start_time::date between current_date AND current_date + integer '6'";
	    return countCourses(conn, query);
	case "total":
	    query = "SELECT COUNT(*) FROM \"courses\"";
	    return countCourses(conn, query);
	case "courseID":
	    query = "SELECT COUNT(*) FROM \"courses\" "
		    + "WHERE CAST(id AS TEXT) LIKE ?";
	    return countCourses(conn, query, searchString);
	case "title":
	    query = "SELECT COUNT(*) FROM \"courses\" "
		    + "WHERE LOWER(titel) LIKE LOWER(?)";
	    return countCourses(conn, query, searchString);
	case "leader":
	    query = "SELECT COUNT(*) FROM \"courses\", \"users\", \"course_instructors\" "
		    + "WHERE LOWER(\"users\".name) LIKE LOWER(?) "
		    + "AND \"users\".id = \"course_instructors\".course_instructor_id "
		    + "AND \"course_instructors\".course_id = courses.id";
	    return countCourses(conn, query, searchString);
	}
	return 0;
    }

    /**
     * 
     * @param conn
     * @param query
     * @return
     * @throws InvalidDBTransferException
     * 
     * @author Patrick Cretu
     */
    private static int countCourses(java.sql.Connection conn, String query)
	    throws InvalidDBTransferException {
	Statement stmt = null;
	ResultSet rst = null;
	int numCourses = 0;

	try {
	    stmt = conn.createStatement();
	    rst = stmt.executeQuery(query);
	    rst.next();
	    numCourses = rst.getInt(1);
	} catch (SQLException e) {
	    LogHandler
		    .getInstance()
		    .error("SQL Exception occoured during countCourses(java.sql.Connection conn, String query)");
	    throw new InvalidDBTransferException();
	} finally {
	    if (rst != null) {
		try {
		    rst.close();
		} catch (SQLException e) {
		    LogHandler
			    .getInstance()
			    .error("SQL Exception occoured during countCourses(java.sql.Connection conn, String query)");
		    throw new InvalidDBTransferException();
		}
	    }
	    if (stmt != null) {
		try {
		    stmt.close();
		} catch (SQLException e) {
		    LogHandler
			    .getInstance()
			    .error("SQL Exception occoured during countCourses(java.sql.Connection conn, String query)");
		    throw new InvalidDBTransferException();
		}
	    }
	}
	return numCourses;
    }

    /**
     * 
     * @param conn
     * @param query
     * @param searchString
     * @return
     * @throws InvalidDBTransferException
     * 
     * @author Patrick Cretu
     */
    private static int countCourses(java.sql.Connection conn, String query,
	    String searchString) throws InvalidDBTransferException {
	PreparedStatement stmt = null;
	ResultSet rst = null;
	int numCourses = 0;

	try {
	    stmt = conn.prepareStatement(query);
	    stmt.setString(1, searchString);
	    rst = stmt.executeQuery();
	    rst.next();
	    numCourses = rst.getInt(1);
	} catch (SQLException e) {
	    LogHandler
		    .getInstance()
		    .error("SQL Exception occoured during countCourses(java.sql.Connection conn, String query, String searchString)");
	    throw new InvalidDBTransferException();
	} finally {
	    if (rst != null) {
		try {
		    rst.close();
		} catch (SQLException e) {
		    LogHandler
			    .getInstance()
			    .error("SQL Exception occoured during countCourses(java.sql.Connection conn, String query, String searchString)");
		    throw new InvalidDBTransferException();
		}
	    }
	    if (stmt != null) {
		try {
		    stmt.close();
		} catch (SQLException e) {
		    LogHandler
			    .getInstance()
			    .error("SQL Exception occoured during countCourses(java.sql.Connection conn, String query, String searchString)");
		    throw new InvalidDBTransferException();
		}
	    }
	}
	return numCourses;
    }

    /**
     * 
     * @param trans
     * @param pagination
     * @param period
     * @return
     * @throws InvalidDBTransferException
     * 
     * @author Patrick Cretu
     */
    public static List<Course> getCourses(Transaction trans,
	    PaginationData pagination, String period)
	    throws InvalidDBTransferException {
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();
	int limit = pagination.getElementsPerPage();
	int offset = limit * pagination.getCurrentPageNumber();
	String orderParam = getOrderParam(pagination.getSortColumn());
	String dir = getSortDirection(pagination.isSortAsc());
	List<Course> result = null;
	String currentDateCourses = "SELECT courses.id, courses.titel, courses.max_participants, courses.start_date,"
		+ "courses.end_date FROM \"courses\", \"course_units\" "
		+ "WHERE \"course_units\".start_time = current_date "
		+ "AND \"course_units\".course_id = \"courses\".id ORDER BY %s %s LIMIT ? OFFSET ?";
	String currentWeekCourses = "SELECT courses.id, courses.titel, courses.max_participants, courses.start_date,"
		+ "courses.end_date FROM \"courses\", \"course_units\" "
		+ "WHERE \"course_units\".start_time BETWEEN current_date AND current_date + integer '6' "
		+ "AND \"course_units\".course_id = \"courses\".id ORDER BY %s %s LIMIT ? OFFSET ?";
	String getAllCourses = "SELECT * FROM \"courses\" ORDER BY %s %s"
		+ " LIMIT ? OFFSET ?";

	switch (period) {
	case "day":
	    result = getCoursesInPeriod(conn, limit, offset, String.format(currentDateCourses, orderParam, dir));
	    break;
	case "week":
	    // result = getCoursesInPeriod(conn, limit, offset, orderParam,
	    // currentWeekCourses);
	    result = getCoursesInPeriod(conn, limit, offset, String.format(currentWeekCourses, orderParam, dir));
	    break;
	case "total":
	    result = getCoursesInPeriod(conn, limit, offset, String.format(getAllCourses, orderParam, dir));
	    break;
	default:
	    ;
	}
	return result;
    }

    /**
     * 
     * @param conn
     * @param limit
     * @param offset
     * @param orderParam
     * @param query
     * @return
     * @throws InvalidDBTransferException
     * 
     * @author Patrick Cretu
     */
    private static List<Course> getCoursesInPeriod(java.sql.Connection conn,
	    int limit, int offset, String query)
	    throws InvalidDBTransferException {
	PreparedStatement stmt = null;
	ResultSet rst = null;
	List<Course> result = null;

	try {
	    stmt = conn.prepareStatement(query);
	    stmt.setInt(1, limit);
	    stmt.setInt(2, offset);

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
     * Returns a list of courses which titles contain the search term the user
     * has entered.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param pagination
     *            the Pagination object which contains the amount of elements
     *            which are to be retrieved
     * @param searchString
     *            the user's search term
     * @return the list of courses, or null if no courses were retrieved
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * 
     * @author Patrick Cretu
     */
    public static List<Course> getCourses(Transaction trans,
	    PaginationData pagination, String searchParam, String searchString)
	    throws InvalidDBTransferException {
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();
	int limit = pagination.getElementsPerPage();
	int offset = limit * pagination.getCurrentPageNumber();
	String orderParam = getOrderParam(pagination.getSortColumn());
	String dir = getSortDirection(pagination.isSortAsc());
	List<Course> result = null;

	switch (searchParam) {
	case "courseID":
	    String getCoursesByID = "SELECT * FROM \"courses\" "
		    + "WHERE CAST(id AS TEXT) LIKE ? ORDER BY " + orderParam
		    + " " + dir + " LIMIT ? OFFSET ?";
	    result = getCourses(conn, limit, offset, searchString,
		    getCoursesByID);

	    break;
	case "title":
	    String getCoursesByTitel = "SELECT * FROM \"courses\" "
		    + "WHERE LOWER(titel) LIKE LOWER(?) ORDER BY " + orderParam
		    + " " + dir + " LIMIT ? OFFSET ?";
	    result = getCourses(conn, limit, offset, searchString,
		    getCoursesByTitel);
	    break;
	case "leader":
	    String getCoursesQuery = "SELECT courses.id, courses.titel, courses.max_participants, courses.start_date,"
		    + "courses.end_date FROM \"courses\", \"users\", \"course_instructors\" "
		    + "WHERE LOWER(\"users\".name) LIKE LOWER(?) "
		    + "AND \"users\".id = \"course_instructors\".course_instructor_id "
		    + "AND \"course_instructors\".course_id = courses.id ORDER BY "
		    + orderParam + " " + dir + " LIMIT ? OFFSET ?";
	    result = getCourses(conn, limit, offset, searchString,
		    getCoursesQuery);
	    break;
	default:
	    ;
	}
	return result;
    }

    /**
     * 
     * @param orderParam
     * @return
     * 
     * @author Patrick Cretu
     */
    private static String getOrderParam(String orderParam) {
	switch (orderParam) {
	case "title":
	    return "titel";
	case "maxUsers":
	    return "max_participants";
	case "starts":
	    return "start_date";
	case "ends":
	    return "end_date";
	default:
	    return "id";
	}
    }

    /**
     * 
     * @param conn
     * @param limit
     * @param offset
     * @param searchString
     * @param query
     * @return
     * @throws InvalidDBTransferException
     * 
     * @author Patrick Cretu
     */
    private static List<Course> getCourses(java.sql.Connection conn, int limit,
	    int offset, String searchString, String query)
	    throws InvalidDBTransferException {
	PreparedStatement stmt = null;
	ResultSet rst = null;
	List<Course> result = null;
	String search = "%" + searchString + "%";

	try {
	    stmt = conn.prepareStatement(query);
	    stmt.setString(1, search);
	    stmt.setInt(2, limit);
	    stmt.setInt(3, offset);

	    rst = stmt.executeQuery();
	    result = getResult(rst);
	} catch (SQLException e) {
	    LogHandler
		    .getInstance()
		    .error("SQL Exception occoured during getCoursesInPeriod(java.sql.Connection conn, int limit, int offset, String searchString, String query)");
	    throw new InvalidDBTransferException();
	} finally {
	    if (rst != null) {
		try {
		    rst.close();
		} catch (SQLException e) {
		    LogHandler
			    .getInstance()
			    .error("SQL Exception occoured during getCoursesInPeriod(java.sql.Connection conn, int limit, int offset, String searchString, String query)");
		    throw new InvalidDBTransferException();
		}
	    }
	    if (stmt != null) {
		try {
		    stmt.close();
		} catch (SQLException e) {
		    LogHandler
			    .getInstance()
			    .error("SQL Exception occoured during getCoursesInPeriod(java.sql.Connection conn, int limit, int offset, String searchString, String query)");
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
    private static List<Course> getResult(ResultSet rst)
	    throws InvalidDBTransferException {
	List<Course> result = new ArrayList<Course>();
	try {
	    int cols = rst.getMetaData().getColumnCount();

	    while (rst.next()) {
		int i = 1;
		List<Object> tuple = new ArrayList<Object>();
		Course course = new Course();
		while (i <= cols) {
		    Object o = rst.getObject(i);
		    tuple.add(o);
		    i++;
		}
		setProperties(course, tuple);
		result.add(course);
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
     * @param course
     * @param tuple
     * 
     * @author Patrick Cretu
     */
    private static void setProperties(Course course, List<Object> tuple) {
	course.setCourseID((Integer) tuple.get(0));
	course.setTitle((String) tuple.get(1));
	course.setMaxUsers((Integer) tuple.get(2));
	course.setStartdate((Date) tuple.get(3));
	course.setEnddate((Date) tuple.get(4));
    }

    /**
     * Returns a list of courses which titles contain the search term the user
     * has entered. The list is ordered by the passed parameter.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param pagination
     *            the Pagination object which contains the amount of elements
     *            which are to be retrieved
     * @param searchString
     *            the user's search term
     * @param orderParam
     *            the parameter the list is to be ordered by
     * @return the list of courses ordered by the passed parameter, or null if
     *         no courses were retrieved
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     */
    public static List<Course> getCoursesOrdered(Transaction trans,
	    PaginationData pagination, String searchString, String searchParam,
	    String orderParam) throws InvalidDBTransferException {
	return null;
    }

    /**
     * Returns a list of users which are leaders of the course with the passed
     * course ID.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param courseID
     *            the course's ID
     * @return the list of users containing the course's leaders, or null if the
     *         course has no course leaders
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * @author Ricky Strohmeier
     */
    public static List<User> getLeaders(Transaction trans, int courseID)
	    throws InvalidDBTransferException {
        ArrayList<User> leaders = new ArrayList<User>();

        String leadersQuery = "SELECT \"name\", \"first_name\", \"email\", \"id\" FROM "
            + "\"users\" WHERE id IN "
            + "(SELECT course_instructor_id FROM \"course_instructors\" "
            + "WHERE course_id = ?)";

        Connection connection = (Connection) trans;
        java.sql.Connection conn = connection.getConn();
        PreparedStatement statement = null;
        ResultSet resultSet;

        try {
            statement = conn.prepareStatement(leadersQuery);
            statement.setInt(1, courseID);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
            User leader = new User();

            if (resultSet.getString("name") != null) {
                leader.setLastname(resultSet.getString("name"));
            } else {
                leader.setLastname("geheim");
            }

            if (resultSet.getString("first_name") != null) {
                leader.setFirstname(resultSet.getString("first_name"));
            } else {
                leader.setFirstname("geheim");
            }
            leader.setEmail(resultSet.getString("email"));
            leader.setUserId(resultSet.getInt("id"));
            leaders.add(leader);
            }
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            LogHandler.getInstance().error("Error occoured in getLeaders from CourseDAO");
            throw new InvalidDBTransferException();
        }
        return leaders;
    }

    /**
     * Returns a course assigned to the specified ID.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param courseID
     *            the course's ID
     * @return the course assigned to the course ID, or null if no such course
     *         was found
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * @author Ricky Strohmeier
     */
    public static Course getCourse(Transaction trans, int courseID)
            throws InvalidDBTransferException {
        Course course = new Course();
        String courseQuery = "SELECT * FROM \"courses\" WHERE id = ?";

        Connection connection = (Connection) trans;
        java.sql.Connection conn = connection.getConn();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = conn.prepareStatement(courseQuery);
            statement.setInt(1, courseID);
            resultSet = statement.executeQuery();
            resultSet.next();
            course.setTitle(resultSet.getString("titel"));
            course.setMaxUsers(resultSet.getInt("max_participants"));
            course.setStartdate(resultSet.getDate("start_date"));
            course.setEnddate(resultSet.getDate("end_date"));
            course.setDescription(resultSet.getString("description"));
            course.setCourseID(resultSet.getInt("id"));
            course.setCourseImage(resultSet.getBytes("image"));
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            LogHandler.getInstance().error("Error occoured in getCourse from CourseDAO");
            throw new InvalidDBTransferException();
        }
        return course;
    }

    /**
     * Returns a list of courses which the user with the passed ID participates
     * in.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param userID
     *            the user's ID
     * @return the list of courses which the user participates in, or null if
     *         the user doesn't participate in any course
     * 
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     */
    public static List<Course> getCoursesOf(Transaction trans,
	    PaginationData pagination, int userID)
	    throws InvalidDBTransferException {
	String direction = getSortDirection(pagination.isSortAsc());
	ArrayList<Course> coursesOf = new ArrayList<Course>();
	String getCourseQuery = "SELECT id, titel FROM \"courses\" "
		+ "WHERE courses.id IN (SELECT course_id FROM"
		+ " \"course_participants\" "
		+ "WHERE participant_id = ?) ORDER BY %s %s"
		+ " LIMIT ? OFFSET ?";
	String getNextCourseUnitQuery = "SELECT course_units.start_time,"
		+ " course_unit_addresses.location "
		+ "FROM \"course_units\",\"course_unit_addresses\" "
		+ "WHERE course_units.course_id = ? "
		+ "AND course_units.start_time >= CURRENT_DATE AND"
		+ " course_unit_addresses.course_unit_id = course_units.id "
		+ "ORDER BY course_units.start_time ASC LIMIT 1";

	getCourseQuery = String.format(getCourseQuery, "titel", direction);
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	int calculateOffset = pagination.getElementsPerPage()
		* pagination.getCurrentPageNumber();

	PreparedStatement stmt = null;
	try {
	    stmt = conn.prepareStatement(getCourseQuery);
	    stmt.setInt(1, userID);
	    stmt.setInt(2, pagination.getElementsPerPage());
	    stmt.setInt(3, calculateOffset);
	    ResultSet fetchedCourses = stmt.executeQuery();

	    // Fills the list coursesOf with courses from the database.
	    // At this time only id an title is set
	    while (fetchedCourses.next()) {
		Course fetchedCourse = new Course();
		fetchedCourse.setCourseID(fetchedCourses.getInt("id"));

		if (fetchedCourses.getString("titel") != null) {
		    fetchedCourse.setTitle(fetchedCourses.getString("titel"));
		} else {
		    fetchedCourse.setTitle("Nicht angegeben");
		}
		coursesOf.add(fetchedCourse);
	    }

	    if (coursesOf.size() > 0) {

		// Fetches the leaders of a course
		ResultSet fetchedNextUnit;
		Timestamp stamp;
		Date date;
		CourseUnit courseUnit;

		for (int i = 0; i < coursesOf.size(); ++i) {
		    stmt = conn.prepareStatement(getNextCourseUnitQuery);
		    stmt.setInt(1, coursesOf.get(i).getCourseID());
		    fetchedNextUnit = stmt.executeQuery();
		    while (fetchedNextUnit.next()) {
			courseUnit = new CourseUnit();
			stamp = fetchedNextUnit.getTimestamp("start_time");
			date = new Date(stamp.getYear(), stamp.getMonth(),
				stamp.getDate(), stamp.getHours(),
				stamp.getMinutes());
			courseUnit.setStartime(date);
			if (fetchedNextUnit.getString("location") != null) {
			    courseUnit.setLocation(fetchedNextUnit
				    .getString("location"));
			} else {
			    courseUnit.setLocation("Nicht angegeben");
			}
			coursesOf.get(i).setNextCourseUnit(courseUnit);
		    }
		}
	    }
	} catch (SQLException e) {
	    LogHandler
		    .getInstance()
		    .error("Error occoured during fetching the next set of courses of a user.");
	    e.printStackTrace();
	    throw new InvalidDBTransferException();
	}
	return coursesOf;
    }

    /**
     * Returns the sort direction as String so it can easiley be added to the
     * SQL statement.
     * 
     * @param isSortAsc
     *            whether the sort direction is ascending order
     * @return the sort direction as String
     */
    private static String getSortDirection(boolean isSortAsc) {
	if (isSortAsc) {
	    return "ASC";
	} else {
	    return "DESC";
	}
    }

    /**
     * Returns the number of courses which the user with the passed ID
     * participates in.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param userID
     *            the user's ID
     * @return the number of courses the user participates in
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     */
    public static int getNumberOfMyCourses(Transaction trans, int userID)
	    throws InvalidDBTransferException {
	int numberOfCourses = 0;
	String countQuery = "SELECT COUNT(*) FROM \"courses\" WHERE courses.id IN "
		+ "(SELECT course_id FROM \"course_participants\" WHERE participant_id = ?)";

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	PreparedStatement stmt = null;
	try {
	    stmt = conn.prepareStatement(countQuery);
	    stmt.setInt(1, userID);
	    ResultSet resultSet = stmt.executeQuery();
	    resultSet.next();
	    numberOfCourses = resultSet.getInt(1);
	} catch (SQLException e) {
	    LogHandler
		    .getInstance()
		    .error("Error occoured during fetching the number of courses of a certain user.");
	    e.printStackTrace();
	    throw new InvalidDBTransferException();
	}
	return numberOfCourses;
    }

    /**
     * Updates a course stored in the database. The course's attributes are
     * replaced by the ones of the passed course.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param course
     *            the course to be updated
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * @author Ricky Strohmeier
     */
    public static void updateCourse(Transaction trans, Course course)
            throws InvalidDBTransferException {

        Connection connection = (Connection) trans;
        java.sql.Connection conn = connection.getConn();
        PreparedStatement statement = null;
        
        String courseQuery = "UPDATE \"courses\" "
                + "SET description = ?, max_participants = ?, start_date = ?, end_date = ? "
                + "WHERE id = ?";
        
        try {
            statement = conn.prepareStatement(courseQuery);
            statement.setString(1, course.getDescription());
            statement.setInt(2, course.getMaxUsers());
            java.sql.Date startDate = new java.sql.Date(course.getStartdate().getTime());
            statement.setDate(3, startDate);
            java.sql.Date endDate = new java.sql.Date(course.getEnddate().getTime());
            statement.setDate(4, endDate);
            statement.setInt(5, course.getCourseID());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            LogHandler.getInstance().error("SQL Exception occoured in updateCourse of CourseDAO");
            throw new InvalidDBTransferException();
        }
    }

    /**
     * Deletes a course which is assigned to the passed ID.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param courseID
     *            the ID of the course to be deleted
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * 
     * @author Katharina Hölzl
     */
    public static boolean deleteCourse(Transaction trans, int courseID)
	    throws InvalidDBTransferException {
            
        boolean successful = false;
        
	// Prepare SQL- Request and database connection.
	PreparedStatement pS = null;
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	String sql = "DELETE FROM \"courses\" WHERE id = ?";
	// catch potential SQL-Injection
	try {
	    pS = conn.prepareStatement(sql);
	    pS.setInt(1, courseID);

	    // execute preparedStatement, return resultSet as a list 
            // (here one entry in the list because the user id is unique)
            if (pS.executeUpdate() == 1) {
                successful = true;
            } else {
                successful = false;
            }
            pS.close();
	} catch (SQLException e) {
	    LogHandler
		    .getInstance()
		    .error("SQL Exception occoured during executing "
		            + "deleteCourse(Transaction trans, int courseID)");
	    throw new InvalidDBTransferException();

	}
    return successful;
    }

    /**
     * Adds a user to a course's list of participants.
     * <p>
     * A tuple of the user's ID and the course's ID is added to the table
     * containing course participants in the database.
     * </p>
     * 
     * 
     * @author Sebastian
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param userID
     *            the user's ID
     * @param courseID
     *            the course's ID
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     */
    public static void addUserToCourse(Transaction trans, int userID,
	    int courseID) throws InvalidDBTransferException {

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	String addUserToCourse = "INSERT INTO \"course_participants\" (participant_id,course_id) VALUES (?,?)";

	try {
	    setRelationMethode(userID, courseID, conn, addUserToCourse);

	    LogHandler.getInstance().debug(
		    "Methode addUserToCourse was succesfull");

	} catch (SQLException e) {
	    // TODO fehler auffangen
	    LogHandler.getInstance().error(
		    "Error occured during addUserToCOurse");
	    throw new InvalidDBTransferException();
	}
    }

    /**
     * Removes a user from a course's list of participants.
     * <p>
     * The tuple of the user's ID and the course's ID is removed from the table
     * of course participants in the database.
     * </p>
     * 
     * @author Sebastian
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param userID
     *            the user's ID
     * @param courseID
     *            the course's ID
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     */
    public static void removeUserFromCourse(Transaction trans, int userID,
	    int courseID) throws InvalidDBTransferException {

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	String removeUserFromCourse = "DELETE FROM \"course_participants\" WHERE participant_id = ? AND course_id = ?";

	try {
	    setRelationMethode(userID, courseID, conn, removeUserFromCourse);
	    LogHandler.getInstance().error(
		    "Deleting User:" + userID + " from course:" + courseID
			    + "was succesfull");
	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "Error occured while trying to delete User:" + userID
			    + " from course:" + courseID);
	    throw new InvalidDBTransferException();
	}

    }

    /**
     * Adds a user to a course's list of course leaders.
     * <p>
     * A tuple of the user's ID and the course's ID is added to the table
     * containing course leaders in the database.
     * </p>
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param userID
     *            the user's ID
     * @param courseID
     *            the course's ID
     * @return true if the course leader could be add to the course, else false
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * 
     * @author Katharina Hölzl
     */
    public static boolean addLeaderToCourse(Transaction trans, int userID, 
	    int courseID) throws InvalidDBTransferException {
        
        boolean successful = false;
        
	//  Prepare SQL- INSERT and database connection.
      
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	String sql = "Insert into \"course_instructors\" (course_instructor_id, course_id) "
		+ "values (?, ?)";

	// catch potential SQL-Injection
	try {

	    
	    if (setRelationMethode(courseID, userID, conn, sql) == 1){
	        successful = true;
            } else {
                successful = false; 
	    }

	} catch (SQLException e) {
	    LogHandler
		    .getInstance()
		    .error("SQL Exception occoured during executing "
		            + "addLeaderToCourse(Transaction trans, "
		            + "int userID, int courseID)");
	    throw new InvalidDBTransferException();

	}
	return successful;
    }

    /**
     * Removes a course leader from a course's list of course leaders.
     * <p>
     * The tuple of the course leader's ID and the course's ID is removed from
     * the table of course leaders in the database.
     * </p>
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param userID
     *            the course leader's ID
     * @param courseID
     *            the course's ID
     * @throws InvalidDBTransferException
     * @return true if the course leader could be removed, else false.
     *             if any error occurred during the execution of the method
     * 
     * @author Katharina Hölzl
     */
    public static boolean removeLeaderFromCourse(Transaction trans, int userID,
	    int courseID) throws InvalidDBTransferException {

        boolean successful = false;
	// Prepare SQL- request and database connection.

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	String sql = "DELETE FROM \"course_instructors\" WHERE course_id = ? AND course_instructor_id = ?";
	// catch potential SQL-Injection
	try {
	    if (setRelationMethode(courseID, userID, conn, sql) == 1){
                successful = true;
            } else {
                successful = false; 
            }

	} catch (SQLException e) {
	    LogHandler
		    .getInstance()
		    .error("SQL Exception occoured during executing "
		            + "removeLeaderFromCourse(Transaction trans, "
		            + "int userID,int courseID)");
	    throw new InvalidDBTransferException();

	}
	return successful;
    }

    /**
     * 
     * @author Sebastian
     * @param trans
     * @param courseID
     * @return
     */
    public static Integer getNumberOfParticipants(Transaction trans,
	    int courseID) {

	PreparedStatement pS = null;
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	String countSQLstat = "SELECT COUNT(*) FROM \"course_participants\" WHERE course_id=?";
	ResultSet resultSet = null;

	try {
	    pS = conn.prepareStatement(countSQLstat);
	    pS.setInt(1, courseID);

	    resultSet = pS.executeQuery();

	    resultSet.next();
	    int numberOfParticipants = resultSet.getInt(1);
	    resultSet.close();
	    pS.close();
	    LogHandler.getInstance().debug(
		    "Methode getNumberOfParticipants was succesfull");
	    return numberOfParticipants;

	} catch (SQLException e) {
	    // TODO Error Handling
	    LogHandler.getInstance().error(
		    "Exception occured during getNumberOfParticipants");
	    throw new InvalidDBTransferException();
	}

    }

    /**
     * 
     * @author Sebastian
     * @param trans
     * @param userID
     * @param courseID
     */
    public static void addUserToInformUser(Transaction trans, int userID,
	    int courseID) {

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	String addUserToInformUser = "INSERT INTO \"inform_users\" (user_id,course_id) VALUES (?,?)";
	try {
	    setRelationMethode(userID, courseID, conn, addUserToInformUser);
	    LogHandler.getInstance().debug(
		    "Methode addUserToInformUser was succesfull");
	} catch (SQLException e) {
	    // Error Handling
	    LogHandler.getInstance().error(
		    "Exception occured during addUserToInformUser");
	    throw new InvalidDBTransferException();
	}
    }
    
    
    
    
	/**
	 * 
	 * @author Sebastian Schwarz
	 * @param trans
	 * @param userID
	 * @param courseID
	 */
	public static void removeUserToInformUser(Transaction trans, int userID,
	        int courseID) {

	    Connection connection = (Connection) trans;
	    java.sql.Connection conn = connection.getConn();

	    String removeUserToInformUser = "DELETE FROM \"inform_users\" (user_id,course_id) VALUES (?,?)";
	    try {
	        setRelationMethode(userID, courseID, conn, removeUserToInformUser);
	        LogHandler.getInstance().debug(
	            "Methode RemoveUserToInformUser was succesfull");
	    } catch (SQLException e) {
	        // Error Handling
	        LogHandler.getInstance().error(
	            "Exception occured during RemoveUserToInformUser");
	        throw new InvalidDBTransferException();
	    }

    }

    /**
     * 
     * @author Sebastian
     * @param userID
     * @param courseID
     * @param conn
     * @param preparedStmt
     * @throws SQLException
     */
    static int setRelationMethode(int userID, int courseID,
	    java.sql.Connection conn, String preparedStmt) throws SQLException {
	PreparedStatement pS;
	pS = conn.prepareStatement(preparedStmt);
	pS.setInt(1, userID);
	pS.setInt(2, courseID);
	return pS.executeUpdate();
	
    }
    
    public static byte[] getImage(Transaction trans, int courseID){
        Connection connection = (Connection) trans;
        java.sql.Connection conn = connection.getConn();
        byte[] picture;
        
        String selectImage= "SELECT image FROM \"courses\" WHERE id=?";
        
        try{
            PreparedStatement pS = conn.prepareStatement(selectImage);
            pS.setInt(1, courseID);
            ResultSet resultSet = pS.executeQuery();
            pS.close();
            
            if(resultSet.next()){
                picture = resultSet.getBytes("image");
                LogHandler.getInstance().debug(
                        "Course Picture succesfully loaded");
                return picture;
            }else{
                LogHandler.getInstance().debug(
                        "No course Picture found");
                return null;
            }
        
        } catch (SQLException e) {
            // Error Handling
            LogHandler.getInstance().error(
                "Exception occured during loading course Picture from Database");
            throw new InvalidDBTransferException();
        }
    }
     
}