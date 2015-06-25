/**
 * This package represents the Data Access Objects of the ofCourse system.
 */
package de.ofCourse.databaseDAO;

import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.Part;

import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Address;
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

    private final static String NUM_COURSES_DAY = "SELECT COUNT(DISTINCT \"courses\".id) FROM \"courses\", \"course_units\" "
	    + "WHERE \"course_units\".start_time::date = current_date "
	    + "AND \"course_units\".course_id = \"courses\".id";

    private final static String NUM_COURSES_WEEK = "SELECT COUNT(DISTINCT \"courses\".id) FROM \"courses\", \"course_units\" "
	    + "WHERE \"course_units\".start_time::date between current_date AND current_date + integer '6'";

    private final static String NUM_COURSES_TOTAL = "SELECT COUNT(DISTINCT \"courses\".id) FROM \"courses\"";

    private final static String NUM_COURSES_ID = "SELECT COUNT(DISTINCT \"courses\".id) FROM \"courses\" "
	    + "WHERE CAST(id AS TEXT) LIKE ?";

    private final static String NUM_COURSES_TITLE = "SELECT COUNT(DISTINCT \"courses\".id) FROM \"courses\" "
	    + "WHERE LOWER(title) LIKE LOWER(?)";

    private final static String NUM_COURSES_LEADER = "SELECT COUNT(DISTINCT \"courses\".id) FROM \"courses\", \"users\", \"course_instructors\" "
	    + "WHERE LOWER(\"users\".name) LIKE LOWER(?) "
	    + "AND \"users\".id = \"course_instructors\".course_instructor_id "
	    + "AND \"course_instructors\".course_id = courses.id";

    private final static String NUM_COURSES_DATE = "SELECT COUNT(DISTINCT \"courses\".id) FROM \"courses\", \"course_units\" "
	    + "WHERE \"courses\".id = \"course_units\".course_id "
	    + "AND \"course_units\".start_time::date = ?";

    private final static String CURRENT_DATE_COURSES = "SELECT DISTINCT courses.id, courses.title, courses.max_participants, courses.start_date,"
	    + "courses.end_date FROM \"courses\", \"course_units\" "
	    + "WHERE \"course_units\".start_time::date = current_date "
	    + "AND \"course_units\".course_id = \"courses\".id ORDER BY %s %s LIMIT ? OFFSET ?";

    private final static String CURRENT_WEEK_COURSES = "SELECT DISTINCT courses.id, courses.title, courses.max_participants, courses.start_date,"
	    + "courses.end_date FROM \"courses\", \"course_units\" "
	    + "WHERE \"course_units\".start_time::date BETWEEN current_date AND current_date + integer '6' "
	    + "AND \"course_units\".course_id = \"courses\".id ORDER BY %s %s LIMIT ? OFFSET ?";

    private final static String ALL_COURSES = "SELECT * FROM \"courses\" ORDER BY %s %s"
	    + " LIMIT ? OFFSET ?";

    private final static String GET_COURSES_BY_ID = "SELECT * FROM \"courses\" "
	    + "WHERE CAST(id AS TEXT) LIKE ? ORDER BY %s %s LIMIT ? OFFSET ?";

    private final static String GET_COURSES_BY_TITLE = "SELECT * FROM \"courses\" "
	    + "WHERE LOWER(title) LIKE LOWER(?) ORDER BY %s %s LIMIT ? OFFSET ?";

    private final static String GET_COURSES_BY_LEADER = "SELECT courses.id, courses.title, courses.max_participants, courses.start_date,"
	    + "courses.end_date FROM \"courses\", \"users\", \"course_instructors\" "
	    + "WHERE LOWER(\"users\".name) LIKE LOWER(?) "
	    + "AND \"users\".id = \"course_instructors\".course_instructor_id "
	    + "AND \"course_instructors\".course_id = courses.id ORDER BY %s %s LIMIT ? OFFSET ?";

    private final static String GET_COURSES_BY_DATE = "SELECT DISTINCT courses.id, courses.title, courses.max_participants, courses.start_date,"
	    + "courses.end_date FROM \"courses\", \"course_units\" "
	    + "WHERE \"courses\".id = \"course_units\".course_id "
	    + "AND \"course_units\".start_time::date = ? ORDER BY %s %s LIMIT ? OFFSET ?";
    
    
    private final static String GET_MY_COURSES  = "SELECT DISTINCT courses.id, courses.title, (SELECT course_units.start_time " 
            + "FROM course_units WHERE courses.id = course_units.course_id AND course_units.start_time > CURRENT_TIMESTAMP " 
	    + "ORDER BY course_units.start_time LIMIT 1), (SELECT course_unit_addresses.location FROM course_units, " 
            + "course_unit_addresses WHERE course_units.id = course_unit_addresses.course_unit_id "
	    + "AND courses.id = course_units.course_id AND course_units.start_time > CURRENT_TIMESTAMP ORDER BY course_units.start_time LIMIT 1) " 
            + "FROM courses WHERE courses.id IN (SELECT course_id FROM course_participants WHERE participant_id = ?) "
	    + "ORDER BY %s %s LIMIT ? OFFSET ?";

    private final static String COUNT_MY_COURSES =  "SELECT COUNT(*) FROM \"courses\" WHERE courses.id IN (SELECT course_id " 
	    + "FROM \"course_participants\" WHERE participant_id = ?)";
    
    /**
     * Deletes all the courses from the system where the end date is before 
     * the actual date
     * 
     * @param trans
     *          the Transaction object which contains the connection to the
     *          database
     * @return true if the maintenance was successful, else false
     * @throws InvalidDBTransferException
     *                          if any error occurred during the execution of 
     *                          the method
     * @author Katharina Hölzl
     */
    public static boolean doCourseMaintenance(Transaction trans)
	    throws InvalidDBTransferException {
	boolean success = false;

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	String sql = "DELETE FROM courses "
	           + "WHERE end_date < CURRENT_TIMESTAMP";

	// catch potential SQL-Injection
	try (PreparedStatement pS = conn.prepareStatement(sql)) {
	    pS.executeUpdate();
	} catch (SQLException e) {
	    throw new InvalidDBTransferException(
	                        "Error occured during doCourseMaintenance", e);

	}
	return success;
    }

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

	// Prepare SQL- INSERT and database connection
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	String sql = "SELECT * "
	           + "FROM users "
	           + "WHERE id = ? AND role != ?::role";

	// catch potential SQL-Injection
	try (PreparedStatement pS = conn.prepareStatement(sql)){

	    // Filling PreparedStatement.
	    pS.setInt(1, courseLeaderID);
	    pS.setString(2, UserRole.REGISTERED_USER.toString());

	    try(ResultSet res = pS.executeQuery()){
	        courseLeaderExists = res.next();

	    }catch (SQLException e) {
	            throw new SQLException();
	        }
	    
	} catch (SQLException e) {
	    throw new InvalidDBTransferException(
	                        "Error occured during courseLeaderExists", e);
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
    public static int createCourse(Transaction trans, Course course,
	    Part courseImage) throws InvalidDBTransferException {

	final int errorGeneratingCourse = -1;

	int generatedCourseID = errorGeneratingCourse;

	// Prepare SQL- INSERT and database connection.
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	String sql = "Insert into \"courses\" (title, max_participants, "
	                                    + "start_date, end_date, "
	                                    + "description, image) "
	           + "values (?, ?, ?, ?, ?, ?) "
	           + "RETURNING id";

	// catch potential SQL-Injection
	try (PreparedStatement pS = conn.prepareStatement(sql)){

	    // Filling PreparedStatement, check in optional fields if the user
	    // has inserted the data or if the value null must be written into
	    // the database.
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
	    
	    if (courseImage != null && courseImage.getInputStream() != null) {
		InputStream inputStream = courseImage.getInputStream();
		pS.setBinaryStream(6, inputStream, courseImage.getSize());
	    } else {
		pS.setBinaryStream(6, null, 0);
	    }
	    
	    try(ResultSet res = pS.executeQuery()){
	        res.next();
	        generatedCourseID = res.getInt("id");
	    } catch (SQLException e) {
                throw new SQLException("Error occured during createCourse", e);
            }
	    
	} catch (SQLException e) {
	    throw new InvalidDBTransferException(
	                                "Error occured during createCourse", e);
	} catch (IOException e) {
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
		    query = NUM_COURSES_DAY;
		    return countCourses(conn, query, null, false);
		case "week":
		    query = NUM_COURSES_WEEK;
		    return countCourses(conn, query, null, false);
		case "total":
		    query = NUM_COURSES_TOTAL;
		    return countCourses(conn, query, null, false);
		case "id":
		    query = NUM_COURSES_ID;
		    return countCourses(conn, query, searchString, false);
		case "title":
		    query = NUM_COURSES_TITLE;
		    return countCourses(conn, query, searchString, false);
		case "leader":
		    query = NUM_COURSES_LEADER;
		    return countCourses(conn, query, searchString, false);
		case "start_date":
		    query = NUM_COURSES_DATE;
		    return countCourses(conn, query, searchString, true);
		}
		return 0;
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
	    String searchString, boolean isDate)
	    throws InvalidDBTransferException {
		int numCourses = 0;
	
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
		    if (searchString != null) {
			    if (isDate) {
				    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
				    Date parsed;
				    try {
				    	parsed = format.parse(searchString);
				    } catch (ParseException e) {
				    	throw new InvalidDBTransferException("SQL Exception occoured during countCourses(java.sql.Connection conn,"
							+ "String query, String searchString, boolean isDate)");
				    }
				    java.sql.Date date = new java.sql.Date(parsed.getTime());
				    stmt.setDate(1, date);
				} else {
				    stmt.setString(1, searchString);
				}
		    }
	
		    try (ResultSet rst = stmt.executeQuery()) {
		    	rst.next();
		    	numCourses = rst.getInt(1);
		    }
		} catch (SQLException e) {
		    throw new InvalidDBTransferException("SQL Exception occoured during " +
		    		"countCourses(java.sql.Connection conn, String query, String searchString)", e);
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
		String orderParam = pagination.getSortColumn().toString();
		String dir = pagination.getSortDirection().toString();
		String query;
	
		switch (period) {
		case "day":
		    query = CURRENT_DATE_COURSES;
		    break;
		case "week":
		    query = CURRENT_WEEK_COURSES;
		    break;
		default:
		    query = ALL_COURSES;
		    break;
		}
		return getCoursesInPeriod(conn, limit, offset,
			String.format(query, orderParam, dir));
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
		List<Course> result = null;
		
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
		    stmt.setInt(1, limit);
		    stmt.setInt(2, offset);
	
		    try (ResultSet rst = stmt.executeQuery()) {
		    	result = getResult(rst);
		    }
		} catch (SQLException e) {
		    throw new InvalidDBTransferException("SQL Exception occoured during " +
		    		"getCoursesInPeriod(java.sql.Connection conn, int limit, int offset, String orderParam, String query)", e);
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
		String orderParam = pagination.getSortColumn().getSortColumn().toString();
		String dir = pagination.getSortDirection().toString();
		String query = null;
		boolean isDate = false;
	
		switch (searchParam) {
		case "title":
		    query = GET_COURSES_BY_TITLE;
		    break;
		case "leader":
		    query = GET_COURSES_BY_LEADER;
		    break;
		case "start_date":
		    query = GET_COURSES_BY_DATE;
		    isDate = true;
		    break;
		default:
		    query = GET_COURSES_BY_ID;
		}
		return getCourses(conn, limit, offset, searchString,
			String.format(query, orderParam, dir), isDate);
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
	    int offset, String searchString, String query, boolean isDate)
	    throws InvalidDBTransferException {
		List<Course> result = null;
	
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
	
		    if (isDate) {
				SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
				Date parsed = format.parse(searchString);
				java.sql.Date date = new java.sql.Date(parsed.getTime());
				stmt.setDate(1, date);
		    } else {
				String search = "%" + searchString + "%";
				stmt.setString(1, search);
		    }
		    stmt.setInt(2, limit);
		    stmt.setInt(3, offset);
	
		    try (ResultSet rst = stmt.executeQuery()) {
		    	result = getResult(rst);
		    }
		    
		} catch (SQLException e) {
		    throw new InvalidDBTransferException("SQL Exception occoured during " +
		    		"getCoursesInPeriod(java.sql.Connection conn, int limit, int offset, String searchString, String query)", e);
		} catch (ParseException e) {
		    throw new InvalidDBTransferException("SQL Exception occoured during countCourses(java.sql.Connection conn,"
					+ "String query, String searchString, boolean isDate)");
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
		    throw new InvalidDBTransferException("SQL Exception occoured during getResult(ResultSet rst)", e);
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
	    LogHandler.getInstance().error(
		    "Error occoured in getLeaders from CourseDAO");
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
    	    course.setTitle(resultSet.getString("title"));
    	    course.setMaxUsers(resultSet.getInt("max_participants"));
    	    course.setStartdate(resultSet.getDate("start_date"));
    	    course.setEnddate(resultSet.getDate("end_date"));
    	    course.setDescription(resultSet.getString("description"));
    	    course.setCourseID(resultSet.getInt("id"));
    	    course.setCourseImage(resultSet.getBytes("image"));
    	    statement.close();
    	    resultSet.close();
    	} catch (SQLException e) {
    	    LogHandler.getInstance().error(
    		    "Error occoured in getCourse from CourseDAO");
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
     *            
     * @author Fuchs Tobias
     */
    @SuppressWarnings("deprecation")
    public static List<Course> getCoursesOf(Transaction trans,
	    PaginationData pagination, int userID)
	    throws InvalidDBTransferException {
	
	String direction = pagination.getSortDirection().toString();
	List<Course> coursesOf = new ArrayList<Course>();
	
	//Formats the SQL - Statement
	String getMyCoursesQuery = String.format(GET_MY_COURSES,
		pagination.getSortColumn(), direction);
	
	
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	//Calculates the offset
	int calculateOffset = pagination.getElementsPerPage()
		* pagination.getCurrentPageNumber();
	
	//Fetch the courses of a user
	try (PreparedStatement stmt = conn.prepareStatement(getMyCoursesQuery)) {

	    stmt.setInt(1, userID);
	    stmt.setInt(2, pagination.getElementsPerPage());
	    stmt.setInt(3, calculateOffset);
	   
	    try(ResultSet fetchedCourses = stmt.executeQuery()){
	    
		Timestamp stamp;
		Date date;
		Course fetchedCourse;
		CourseUnit courseUnit;
		Address unitAddress;
		// Fills the list coursesOf with courses from the database.
		// At this time only id an title is set
		while (fetchedCourses.next()) {
		    fetchedCourse = new Course();
		    courseUnit = new CourseUnit();
		    
		    fetchedCourse.setCourseID(fetchedCourses.getInt("id"));
		
		    if (fetchedCourses.getString("title") != null) {
			fetchedCourse.setTitle(fetchedCourses.getString("title"));
		    } else {
			fetchedCourse.setTitle("Nicht angegeben");
		    }
		
		    
		    stamp = fetchedCourses.getTimestamp("start_time");
		    if (stamp != null) {
			date = new Date(stamp.getYear(),
				stamp.getMonth(),
				stamp.getDate(), 
				stamp.getHours(),
				stamp.getMinutes());
		    courseUnit.setStartime(date);
		    }
		    
		    unitAddress = new Address();
		    if(fetchedCourses.getString("location")!= null){
			    unitAddress.setLocation(
				    fetchedCourses.getString("location"));
		    }
		    else{
			    unitAddress.setLocation("Nicht angegeben");
		    }
		courseUnit.setAddress(unitAddress);
		fetchedCourse.setNextCourseUnit(courseUnit);
		coursesOf.add(fetchedCourse);
	    }
	  }
	} catch (SQLException e) {
	    LogHandler.getInstance().error("Error occoured during fetching" 
	                     + " the next set of courses of a user.");
	    throw new InvalidDBTransferException();
	}
	return coursesOf;
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
     *             
     * @author Fuchs Tobias
     */
    public static int getNumberOfMyCourses(Transaction trans, int userID)
	    throws InvalidDBTransferException {
	int numberOfCourses = 0;
	
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	try (PreparedStatement  stmt = conn.prepareStatement(COUNT_MY_COURSES)) {
	    stmt.setInt(1, userID);
	    
	    try(ResultSet resultSet = stmt.executeQuery()){
	    resultSet.next();
	    numberOfCourses = resultSet.getInt(1);
	    }
	    
	} catch (SQLException e) {
	    LogHandler
		    .getInstance()
		    .error("Error occoured during fetching the number of" 
			    + " courses of a certain user.");
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
        String emptyString = new String("nothing entered");
        java.sql.Date emptyDate = new java.sql.Date(0);
        int emptyNumber = 0;

    	Connection connection = (Connection) trans;
    	java.sql.Connection conn = connection.getConn();

    	String courseQuery = "UPDATE \"courses\" "
    		+ "SET description = ?, max_participants = ?, start_date = ?, end_date = ? "
    		+ "WHERE id = ?";
    
    	try(PreparedStatement statement = conn.prepareStatement(courseQuery)) {
    	    if (!course.getDescription().equals(null)) {
    	        statement.setString(1, course.getDescription());    	        
    	    } else {
    	        statement.setString(1, emptyString);
    	    }

    	    if (course.getMaxUsers() != null) {
    	        statement.setInt(2, course.getMaxUsers());    	        
    	    } else {
    	        statement.setInt(2, emptyNumber);
    	    }

    	    if (course.getStartdate() != null) {
    	        java.sql.Date startDate = new java.sql.Date(course.getStartdate().getTime());
    	        statement.setDate(3, startDate);    	        
    	    } else {
    	        statement.setDate(3, emptyDate);
    	    }

    	    if (course.getEnddate() != null) {
        	    java.sql.Date endDate = new java.sql.Date(course.getEnddate().getTime());
        	    statement.setDate(4, endDate);
    	    } else {
    	        statement.setDate(4, emptyDate);
    	    }

    	    if (course.getCourseID() != 0) {
    	        statement.setInt(5, course.getCourseID());
    	    } else {
    	        statement.setInt(5, emptyNumber);
    	    }

    	    statement.executeUpdate();
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
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	String sql = "DELETE FROM \"courses\" "
	           + " WHERE id = ?";
	// catch potential SQL-Injection
	try (PreparedStatement pS = conn.prepareStatement(sql)) {
	    pS.setInt(1, courseID);

	    // execute preparedStatement, return resultSet as a list
	    // (here one entry in the list because the user id is unique)
	    if (pS.executeUpdate() == 1) {
		successful = true;
	    } else {
		successful = false;
	    }
	} catch (SQLException e) {
	    throw new InvalidDBTransferException(
	                                "Error occured during deleteCourse", e);

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

	        } catch (SQLException e) {
	    
	            throw new InvalidDBTransferException("Error occured during addUserToCOurse", e);
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

        } catch (SQLException e) {           
            throw new InvalidDBTransferException("Error occured while trying to delete User:" + userID
                + " from course:" + courseID, e);
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

	// Prepare SQL- INSERT and database connection.

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	String sql = "INSERT into \"course_instructors\" "
	            + "(course_instructor_id, course_id) " 
	            + "values (?, ?)";

	// catch potential SQL-Injection
	try {

	    if (setRelationMethode(userID, courseID, conn, sql) == 1) {
		successful = true;
	    } else {
		successful = false;
	    }

	} catch (SQLException e) {
	    throw new InvalidDBTransferException(
	                          "Error occured during addLeaderToCourse", e);

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
     * @return true if the course leader could be removed, else false. if any
     *         error occurred during the execution of the method
     * 
     * @author Katharina Hölzl
     */
    public static boolean removeLeaderFromCourse(Transaction trans, int userID,
	    int courseID) throws InvalidDBTransferException {

	boolean successful = false;
	// Prepare SQL- request and database connection.

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	String sql = "DELETE FROM \"course_instructors\" "
	           + "WHERE course_id = ? "
	           + "AND course_instructor_id = ?";
	
	// catch potential SQL-Injection
	try {
	    if (setRelationMethode(courseID, userID, conn, sql) == 1) {
		successful = true;
	    } else {
		successful = false;
	    }

	} catch (SQLException e) {
	    throw new InvalidDBTransferException(
	                      "Error occured during removeLeaderFromCourse", e);

	}
	return successful;
    }

    /**
     * Returns the Number of Participants of that Course
     * 
     * @author Sebastian
     * @param trans
     * @param courseID
     * @return Number of Participants as Integer
     */
    public static Integer getNumberOfParticipants(Transaction trans,
	    int courseID) {
        
        Connection connection = (Connection) trans;
        java.sql.Connection conn = connection.getConn();
        String countSQLstat = "SELECT COUNT(*) FROM \"course_participants\" WHERE course_id=?";
        
        try(PreparedStatement pS = conn.prepareStatement(countSQLstat)) {
	    
            pS.setInt(1, courseID);           
            try(ResultSet resultSet = pS.executeQuery()){
                
                resultSet.next();
                int numberOfParticipants = resultSet.getInt(1);
                return numberOfParticipants;  
            }
        } catch (SQLException e) {
            
            throw new InvalidDBTransferException("Exception occured during getNumberOfParticipants", e);
        }

    }

    /**
     * 
     * Adds a User to the User_Inform Table of the Database. 
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

        
        } catch (SQLException e) {
            throw new InvalidDBTransferException("Exception occured during addUserToInformUser", e);
        }
    }

    /**
     * This DAO Methode removes a User from The Inform_Users Table.(For example when the Users
     * leaves the course where he signed up for Course News)
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

        String removeUserToInformUser = "DELETE FROM \"inform_users\" WHERE user_id=? AND course_id=?";
	
        try {
            setRelationMethode(userID, courseID, conn, removeUserToInformUser);
            
	    } catch (SQLException e) {	    
	        throw new InvalidDBTransferException("Exception occured during RemoveUserToInformUser", e);
	    }        
    }

    /**
     * This is a extracted Methode which sets the values and excute a simple sql command.
     * 
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
	
        try(PreparedStatement pS = conn.prepareStatement(preparedStmt)){
            
            pS.setInt(1, userID);
            pS.setInt(2, courseID);
            int success = pS.executeUpdate();
            
            return success;
        }			
    }

    
    /**
     * Returns the Picture as Byte Array from the Database
     * 
     * 
     * @author Sebastian Schwarz
     * @param trans
     * @param courseID
     * @return picture 
     *              as byte Array
     */
    public static byte[] getImage(Transaction trans, int courseID) {
	
        Connection connection = (Connection) trans;
        java.sql.Connection conn = connection.getConn();
        byte[] picture;

        String selectImage = "SELECT image FROM \"courses\" WHERE id=?";

        try(PreparedStatement pS = conn.prepareStatement(selectImage)) {
	    
            pS.setInt(1, courseID);
	    
            try(ResultSet resultSet = pS.executeQuery()){
	        
                if (resultSet.next()) {
                    
                    picture = resultSet.getBytes("image");  
                    return picture;
	        } else {
	        	
	            //Returns null so that the HTTP Serlvet can load the dummy picutre
	            return null;
	        }  
	    }
	    
	} catch (SQLException e) {
	    
	    throw new InvalidDBTransferException("Exception occured during "
	            + "loading course Picture from Database", e);
	    }
    }

}