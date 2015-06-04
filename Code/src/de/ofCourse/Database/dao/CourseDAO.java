/**
 * This package represents the Data Access Objects of the ofCourse system.
 */
package de.ofCourse.Database.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
 * @author Patrick Cretu
 *
 */
public class CourseDAO {

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
     */
    public static void createCourse(Transaction trans, Course course)
	    throws InvalidDBTransferException {
    }

    /**
     * Returns a list of courses which take place on the current date.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param pagination
     *            the Pagination object which contains the amount of elements
     *            which are to be retrieved
     * @return the list of courses, or null if no courses were retrieved
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     */
    public static List<Course> getCourses(Transaction trans,
	    PaginationData pagination) throws InvalidDBTransferException {
	return null;
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
     */
    public static List<Course> getCourses(Transaction trans,
	    PaginationData pagination, String searchParam, String searchString)
	    throws InvalidDBTransferException {
    	Connection connection = (Connection) trans;
    	java.sql.Connection conn = connection.getConn();
    	List<Course> result = new ArrayList<Course>();
    	
    	switch (searchParam) {
    		case "courseID":
    			try {
    				int courseID = Integer.parseInt(searchString);
    				result = getCoursesByID(conn, courseID);
    			} catch (NumberFormatException e) {
    				LogHandler.getInstance().error("Error occoured when parsing the search string to an integer value.");
    				e.printStackTrace();
    			}
    			break;
    		case "title":
    			result = getCoursesByTitle(conn, searchString);
    			break;
    		case "leader":
    			result = getCoursesOfLeader(conn, searchString);
    			break;
    		default:
    			;
    	}
		return null;
    }
    
    private static void setProperties(Course course, List<Object> tuple) {
		course.setCourseID((Integer) tuple.get(0));
		course.setTitle((String) tuple.get(1));
		course.setMaxUsers((Integer) tuple.get(2));
		course.setStartdate((Date) tuple.get(3));
		course.setEnddate((Date) tuple.get(4));
	}
    
    private static List<Course> getResult(ResultSet rst) {
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
			e.printStackTrace();
		}
    	return null;
    }
    
    private static List<Course> getCoursesByID (java.sql.Connection conn, int courseID) {
    	String getCoursesQuery = "SELECT * FROM \"courses\" " +
    			"WHERE CAST(id AS TEXT) LIKE '%?%'";
    	PreparedStatement stmt = null;
    	ResultSet rst = null;
    	List<Course> result = null;
    	
    	try {
			stmt = conn.prepareStatement(getCoursesQuery);
			stmt.setInt(1, courseID);
			rst = stmt.executeQuery();
			result = getResult(rst);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
    	return result;
    }
    
    private static List<Course> getCoursesByTitle (java.sql.Connection conn, String title) {
    	String getCoursesQuery = "SELECT * FROM \"courses\" " +
				"WHERE titel ILIKE LOWER('%?%')";
    	PreparedStatement stmt = null;
    	ResultSet rst = null;
    	List<Course> result = null;

    	try {
			stmt = conn.prepareStatement(getCoursesQuery);
			stmt.setString(1, title);
			rst = stmt.executeQuery();
			result = getResult(rst);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
    	return result;
    }
    
    private static List<Course> getCoursesOfLeader (java.sql.Connection conn, String name) {
    	String getCoursesQuery = "";
    	PreparedStatement stmt = null;
    	
    	ResultSet rst = null;
    	List<Course> result = null;

    	try {
			stmt = conn.prepareStatement(getCoursesQuery);
			stmt.setString(1, name);
			rst = stmt.executeQuery();
			result = getResult(rst);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
    	return result;
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
     */
    public static List<User> getLeaders(Transaction trans, int courseID)
	    throws InvalidDBTransferException {
	return null;
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
     */
    public static Course getCourse(Transaction trans, int courseID)
	    throws InvalidDBTransferException {
    	return null;
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

	ArrayList<Course> coursesOf = new ArrayList<Course>();
	String getCourseQuery = "SELECT id, titel FROM \"courses\" "
		+ "WHERE courses.id IN (SELECT course_id FROM \"course_participants\" "
		+ "WHERE participant_id = ?) ORDER BY ? "
		+ getSortDirectionAsString(pagination.isSortAsc())
		+ " LIMIT ? OFFSET ?";
	String getCourseLeadersQuery = "SELECT nickname FROM \"users\" "
		+ "WHERE users.id IN " + "(SELECT course_instructor_id "
		+ "FROM \"course_instructors\" WHERE course_id = ?)";
	String getNextCourseUnitQuery = "SELECT start_time, location "
		+ "FROM \"course_units\" WHERE course_units.course_id = ? "
		+ "AND course_units.start_time >= CURRENT_DATE "
		+ "ORDER BY course_units.start_time ASC LIMIT 1";

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	// ////////////////////////////////////////////////////////////////
	// Überprüfen mit ifs ob weitergemacht werden soll oder nicht
	// ///////////////////////////////////////////////////////////////

	int calculateOffset = pagination.getElementsPerPage()
		* pagination.getCurrentPageNumber();

	PreparedStatement stmt = null;
	try {
	    stmt = conn.prepareStatement(getCourseQuery);
	    stmt.setInt(1, userID);
	    stmt.setString(2, "'titel'");
	    stmt.setInt(3, pagination.getElementsPerPage());
	    stmt.setInt(4, calculateOffset);
	    ResultSet fetchedCourses = stmt.executeQuery();

	    // Fills the list coursesOf with courses from the database.
	    // At this time only id an title is set
	    while (fetchedCourses.next()) {
		Course fetchedCourse = new Course();
		fetchedCourse.setCourseAdmins((List) new ArrayList<Course>());
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
		ResultSet fetchedLeaders;
		for (int i = 0; i < coursesOf.size(); ++i) {
		    stmt = conn.prepareStatement(getCourseLeadersQuery);
		    stmt.setInt(1, coursesOf.get(i).getCourseID());
		    fetchedLeaders = stmt.executeQuery();
		    while (fetchedLeaders.next()) {
			User courseAdmin = new User();
			courseAdmin.setUsername(fetchedLeaders
				.getString("nickname"));
			coursesOf.get(i).getCourseAdmins().add(courseAdmin);
		    }
		}

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
			courseUnit.setStarttime(date);
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
    private static String getSortDirectionAsString(boolean isSortAsc) {
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
     */
    public static void updateCourse(Transaction trans, Course course)
	    throws InvalidDBTransferException {
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
     */
    public static void deleteCourse(Transaction trans, int courseID)
	    throws InvalidDBTransferException {
    }

    /**
     * Adds a user to a course's list of participants.
     * <p>
     * A tuple of the user's ID and the course's ID is added to the table
     * containing course participants in the database.
     * </p>
     * 
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
    }

    /**
     * Removes a user from a course's list of participants.
     * <p>
     * The tuple of the user's ID and the course's ID is removed from the table
     * of course participants in the database.
     * </p>
     * 
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
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     */
    public static void addLeaderToCourse(Transaction trans, int userID,
	    int courseID) throws InvalidDBTransferException {
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
     *             if any error occurred during the execution of the method
     */
    public static void removeLeaderFromCourse(Transaction trans, int userID,
	    int course) throws InvalidDBTransferException {
    }
}