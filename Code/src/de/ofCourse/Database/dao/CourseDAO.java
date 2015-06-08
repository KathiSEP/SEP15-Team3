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

        // SQL- INSERT vorbereiten und Connection zur Datenbank erstellen.
        PreparedStatement pS = null;
        Connection connection = (Connection) trans;
        java.sql.Connection conn = connection.getConn();

        String sql = "Insert into \"courses\" (titel, max_participants, "
                + "start_date, end_date, description, image) "
                + "values ({?}, ?, ?, ?, {?}, ?)";

        // mögliche SQL-Injektion abfangen
        try {

            // PreparedStatement befüllen, bei optionalen Feldern überprüfen,
            // ob der Benutzer die Daten angegeben hat oder ob in die
            // Datenbank null-Werte geschrieben werden müssen.
            pS = conn.prepareStatement(sql);
            if (course.getTitle() == null || course.getTitle().length() < 1) {
                pS.setString(1, null);
            } else {
                pS.setString(1, course.getTitle());
            }
            pS.setInt(2, course.getMaxUsers());
            pS.setDate(3, new java.sql.Date(course.getStartdate().getTime()));
            pS.setDate(4, new java.sql.Date(course.getEnddate().getTime()));
            if (course.getDescription() == null
                    || course.getDescription().length() < 1) {
                pS.setString(5, null);
            } else {
                pS.setString(5, course.getDescription());
            }
            if (course.getCourseImage() == null
                    || course.getCourseImage().length() < 1) {
                pS.setString(6, null);
            } else {
                pS.setString(6, course.getCourseImage());
            }

            pS.executeUpdate();

        } catch (SQLException e) {
            LogHandler
                    .getInstance()
                    .error("SQL Exception occoured during executing createUser(Transaction trans, User user, String pwHash)");
            throw new InvalidDBTransferException();

        }
    }

    public static int getNumberOfCourses(Transaction trans, String param,
            String searchString) {
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

    private static int countCourses(java.sql.Connection conn, String query) {
        Statement stmt = null;
        ResultSet rst = null;
        int numCourses = 0;

        try {
            stmt = conn.createStatement();
            rst = stmt.executeQuery(query);
            rst.next();
            numCourses = rst.getInt(1);
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
        return numCourses;
    }

    private static int countCourses(java.sql.Connection conn, String query,
            String searchString) {
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
        return numCourses;
    }

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
                + "WHERE \"course_units\".start_time::date = current_date "
                + "AND \"course_units\".course_id = \"courses\".id ORDER BY ? "
                + dir + " LIMIT ? OFFSET ?";
        String currentWeekCourses = "SELECT courses.id, courses.titel, courses.max_participants, courses.start_date,"
                + "courses.end_date FROM \"courses\", \"course_units\" "
                + "WHERE \"course_units\".start_time::date between current_date AND current_date + integer '6' ORDER BY ? "
                + dir + " LIMIT ? OFFSET ?";
        String getAllCourses = "SELECT * FROM \"courses\" ORDER BY ? " + dir
                + " LIMIT ? OFFSET ?";

        switch (period) {
        case "day":
            result = getCoursesInPeriod(conn, limit, offset, orderParam,
                    currentDateCourses);
            break;
        case "week":
            result = getCoursesInPeriod(conn, limit, offset, orderParam,
                    currentWeekCourses);
            break;
        case "total":
            result = getCoursesInPeriod(conn, limit, offset, orderParam,
                    getAllCourses);
            break;
        default:
            ;
        }
        return result;
    }

    private static List<Course> getCoursesInPeriod(java.sql.Connection conn,
            int limit, int offset, String orderParam, String query) {
        PreparedStatement stmt = null;
        ResultSet rst = null;
        List<Course> result = null;

        try {
            stmt = conn.prepareStatement(query);
            stmt.setString(1, orderParam);
            stmt.setInt(2, limit);
            stmt.setInt(3, offset);
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

    private static List<Course> getCourses(java.sql.Connection conn, int limit,
            int offset, String searchString, String query) {
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
                + getSortDirection(pagination.isSortAsc())
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

        // SQL- Abfrage vorbereiten und Connection zur Datenbank erstellen.
        PreparedStatement pS = null;
        Connection connection = (Connection) trans;
        java.sql.Connection conn = connection.getConn();

        String sql = "DELETE FROM \"courses\" WHERE id = ?";
        // mögliche SQL-Injektion abfangen
        try {
            pS = conn.prepareStatement(sql);
            pS.setInt(1, courseID);

            pS.executeUpdate();

        } catch (SQLException e) {
            LogHandler
                    .getInstance()
                    .error("SQL Exception occoured during executing emailExists(Transaction trans, String email)");
            throw new InvalidDBTransferException();

        }
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
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     */
    public static void addLeaderToCourse(Transaction trans, int userID,
            int courseID) throws InvalidDBTransferException {
        // SQL- INSERT vorbereiten und Connection zur Datenbank erstellen.

        Connection connection = (Connection) trans;
        java.sql.Connection conn = connection.getConn();

        String sql = "Insert into \"course_instructors\" (course_instructor_id, course_id) "
                + "values (?, ?)";

        // mögliche SQL-Injektion abfangen
        try {

            setRelationMethode(courseID, userID, conn, sql);

        } catch (SQLException e) {
            LogHandler
                    .getInstance()
                    .error("SQL Exception occoured during executing createUser(Transaction trans, User user, String pwHash)");
            throw new InvalidDBTransferException();

        }
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
            int courseID) throws InvalidDBTransferException {

        // SQL- Abfrage vorbereiten und Connection zur Datenbank erstellen.

        Connection connection = (Connection) trans;
        java.sql.Connection conn = connection.getConn();

        String sql = "DELETE FROM \"course_instructors\" WHERE course_id = ? AND course_instructor_id = ?";
        // mögliche SQL-Injektion abfangen
        try {
            setRelationMethode(courseID, userID, conn, sql);

        } catch (SQLException e) {
            LogHandler
                    .getInstance()
                    .error("SQL Exception occoured during executing emailExists(Transaction trans, String email)");
            throw new InvalidDBTransferException();

        }
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
            pS.close();
            resultSet.close();
            LogHandler.getInstance().debug(
                    "Methode getNumberOfParticipants was succesfull");
            return resultSet.getInt(1);

        } catch (SQLException e) {
            // TODO Error Handling
            LogHandler.getInstance().error(
                    "Exception occured during getNumberOfParticipants");
        }
        return -1;

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

        String addUserToInformUser = "INSERT INTO \"inform_user\" (user_id,course_id) VALUES (?,?)";
        try {
            setRelationMethode(userID, courseID, conn, addUserToInformUser);
            LogHandler.getInstance().debug(
                    "Methode addUserToInformUser was succesfull");
        } catch (SQLException e) {
            // Error Handling
            LogHandler.getInstance().error(
                    "Exception occured during addUserToInformUser");
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
    private static void setRelationMethode(int userID, int courseID,
            java.sql.Connection conn, String preparedStmt) throws SQLException {
        PreparedStatement pS;
        pS = conn.prepareStatement(preparedStmt);
        pS.setInt(1, userID);
        pS.setInt(2, courseID);
        pS.executeUpdate();
    }

}