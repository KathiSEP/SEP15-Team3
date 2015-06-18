/**
 /**
 * This package represents the Data Access Objects of the ofCourse system.
 */
package de.ofCourse.Database.dao;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.Part;

import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Activation;
import de.ofCourse.model.Address;
import de.ofCourse.model.Course;
import de.ofCourse.model.PaginationData;
import de.ofCourse.model.Salutation;
import de.ofCourse.model.User;
import de.ofCourse.model.UserRole;
import de.ofCourse.model.UserStatus;
import de.ofCourse.system.Connection;
import de.ofCourse.system.LogHandler;
import de.ofCourse.system.Transaction;

/**
 * Provides methods for transactions with the users stored in the database such
 * as creating, retrieving, updating or deleting users. Also retrieves a course
 * leader's list of courses.
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
public class UserDAO {

    private final static String ALL_USERS = "SELECT * FROM \"users\" ORDER BY %s %s LIMIT ? OFFSET ?";

    private final static String GET_USERS_BY_NAME = "SELECT * FROM \"users\" WHERE NAME = ? ORDER BY %s %s LIMIT ? OFFSET ?";

    private final static String GET_USER_BY_EMAIL = "SELECT * FROM \"users\" WHERE EMAIL = ? ORDER BY %s %s LIMIT ? OFFSET ?";

    private final static String GET_USER_BY_NICKNAME = "SELECT * FROM \"users\" WHERE NICKNAME = ? ORDER BY %s %s LIMIT ? OFFSET ?";

    /**
     * Returns the password salt of a user assigned to the passed user name.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param username
     *            the user's name
     * @return the user's password salt
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * 
     * @author Katharina Hölzl
     */
    public static String getPWSalt(Transaction trans, String username)
	    throws InvalidDBTransferException {

	String pwSalt = "";

	// Prepare SQL- Request and database connection.
	PreparedStatement pS = null;
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	// database request
	String sql = "SELECT pw_salt FROM \"users\" WHERE nickname=?";

	// catch potential SQL-Injection
	try {
	    pS = conn.prepareStatement(sql);
	    pS.setString(1, username);
	    // execute preparedStatement, return resultSet as a list
	    // (here one entry in the list because the user name is unique)
	    ResultSet res = pS.executeQuery();

	    // execute next entry, return true if there is another entry,
	    // else false.
	    if (res.next()) {
		// Fill the id with the associated value from the database.
		pwSalt = res.getString("pw_salt");
	    } else {
		pwSalt = null;
	    }
	    pS.close();
	    res.close();
	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "SQL Exception occoured during executing "
			    + "getPWSalt(Transaction trans, String username)");
	    throw new InvalidDBTransferException();
	}

	return pwSalt;
    }

    /**
     * Checks if the inserted mail address already exists in the database.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param email
     *            inserted e-mail
     * @return true if the mail address already exists in the system, else
     *         return false
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * 
     * @author Katharina Hölzl
     */
    public static boolean emailExists(Transaction trans, String email)
	    throws InvalidDBTransferException {
	boolean exists = false;

	// prepare SQL- request and database connection.
	PreparedStatement pS = null;
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	String sql = "SELECT id FROM \"users\" WHERE email=?";
	// catch potential SQL-Injection
	try {
	    pS = conn.prepareStatement(sql);
	    pS.setString(1, email);

	    // execute preparedStatement, return resultSet as a list
	    // (here one entry in the list because the email is unique)
	    ResultSet res = pS.executeQuery();

	    // execute next entry, return true if there is another entry,
	    // else false.
	    if (res.next()) {
		exists = true;
	    } else {
		exists = false;
	    }
	    pS.close();
	    res.close();
	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "SQL Exception occoured during executing "
			    + "emailExists(Transaction trans, String email)");
	    throw new InvalidDBTransferException();
	}
	return exists;
    }

    /**
     * 
     * @param trans
     * @param nickName
     * @return
     * 
     * @author Patrick Cretu
     */
    public static boolean nickTaken(Transaction trans, String nickname)
	    throws InvalidDBTransferException {
	boolean exists = true;
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();
	PreparedStatement stmt = null;
	ResultSet rst = null;
	int check;
	String query = "SELECT COUNT(*) FROM \"users\" WHERE nickname = ?";

	try {
	    stmt = conn.prepareStatement(query);
	    stmt.setString(1, nickname);
	    rst = stmt.executeQuery();
	    rst.next();
	    check = rst.getInt(1);

	    if (check == 0) {
		exists = false;
	    }
	} catch (SQLException e) {
	    LogHandler
		    .getInstance()
		    .error("SQL Exception occoured during executing nickTaken(Transaction trans, String nickname)");
	    throw new InvalidDBTransferException();
	} finally {
	    if (stmt != null) {
		try {
		    stmt.close();
		} catch (SQLException e) {
		    LogHandler
			    .getInstance()
			    .error("SQL Exception occoured during closing PreparedStatement in nickTaken(Transaction trans, String nickname)");
		    throw new InvalidDBTransferException();
		}
	    }
	    if (rst != null) {
		try {
		    rst.close();
		} catch (SQLException e) {
		    LogHandler
			    .getInstance()
			    .error("SQL Exception occoured during closing ResultSet in nickTaken(Transaction trans, String nickname)");
		    throw new InvalidDBTransferException();
		}
	    }
	}
	return exists;
    }

    /**
     * 
     * @param trans
     * @param userID
     * @param image
     * @throws InvalidDBTransferException
     * 
     * @author Patrick Cretu
     */
    public static void uploadImage(Transaction trans, int userID, Part image)
	    throws InvalidDBTransferException {
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();
	PreparedStatement stmt = null;
	try {
	    InputStream inputStream = image.getInputStream();
	    String query = "UPDATE \"users\" SET profile_image = ? WHERE id = ?";
	    stmt = conn.prepareStatement(query);
	    stmt.setBinaryStream(1, inputStream, image.getSize());
	    stmt.setInt(2, userID);

	    stmt.executeUpdate();
	} catch (SQLException e) {
	    LogHandler
		    .getInstance()
		    .error("SQL Exception occoured during executing uploadImage(Transaction trans, int userID, Part image)");
	    throw new InvalidDBTransferException();
	} catch (IOException e) {
	    LogHandler
		    .getInstance()
		    .error("SQL Exception occoured during executing uploadImage(Transaction trans, int userID, Part image)");
	    throw new InvalidDBTransferException();
	} finally {
	    if (stmt != null) {
		try {
		    stmt.close();
		} catch (SQLException e) {
		    LogHandler
			    .getInstance()
			    .error("SQL Exception occoured during executing uploadImage(Transaction trans, int userID, Part image)");
		    throw new InvalidDBTransferException();
		}
	    }
	}
    }

    /**
     * Adds a new user to the list of users in the database.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param user
     *            the user to be added
     * @param pwHash
     *            the hashed password
     * @return the created id of the user
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * 
     * @author Katharina Hölzl
     */
    public static String createUser(Transaction trans, User user,
	    String pwHash, String salt) throws InvalidDBTransferException {

	String veriString = "";

	// Prepare SQL- INSERT and database connection.
	PreparedStatement pS = null;
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	// catch potential SQL-Injection
	try {
	    ResultSet res = null;

	    String sql = "SELECT id FROM \"users\" WHERE veri_string = ?";
	    do {
		SecureRandom random = new SecureRandom();
		veriString = new BigInteger(130, random).toString();
		pS = conn.prepareStatement(sql);
		pS.setString(1, veriString);
		res = pS.executeQuery();
	    } while (res.next());

	    sql = "Insert into \"users\" (first_name, name, nickname, email, "
		    + "pw_hash, date_of_birth, form_of_address, credit_balance, "
		    + "email_verification, admin_verification, role, status, "
		    + "veri_string, pw_salt) "
		    + "values (?, ?, ?, ?, ?, ?, ?::form_of_address, ?, ?, ?, "
		    + "?::role, ?::status, ?, ?)";

	    // Filling PreparedStatement, check in optional fields if the user
	    // has inserted the data or if the value null must be written into
	    // the database.
	    pS = conn.prepareStatement(sql);
	    if (user.getFirstname() == null || user.getFirstname().length() < 1) {
		pS.setString(1, null);
	    } else {
		pS.setString(1, user.getFirstname());
	    }
	    if (user.getLastname() == null || user.getLastname().length() < 1) {
		pS.setString(2, null);
	    } else {
		pS.setString(2, user.getLastname());
	    }
	    pS.setString(3, user.getUsername());
	    pS.setString(4, user.getEmail());
	    pS.setString(5, pwHash);
	    if (user.getDateOfBirth() == null) {
		pS.setDate(6, null);
	    } else {
		java.sql.Date sqlDate = new java.sql.Date(user.getDateOfBirth()
			.getTime());
		pS.setDate(6, sqlDate);
	    }
	    if (user.getSalutation() == null) {
		pS.setString(7, null);
	    } else {
		pS.setString(7, user.getSalutation().toString());
	    }
	    pS.setDouble(8, user.getAccountBalance());
	    pS.setBoolean(9, false);
	    pS.setBoolean(10, false);
	    pS.setString(11, UserRole.REGISTERED_USER.toString());
	    pS.setString(12, UserStatus.NOT_ACTIVATED.toString());
	    pS.setString(13, veriString);
	    pS.setString(14, salt);
	    pS.executeUpdate();

	    sql = "Insert into \"user_addresses\" (user_id, country, "
		    + "city, zip_code, street, house_nr) "
		    + "values (?, ?, ?, ?, ?, ?)";
	    pS = conn.prepareStatement(sql);
	    pS.setInt(1, UserDAO.getUserID(trans, user.getUsername()));
	    pS.setString(2, user.getAddress().getCountry());
	    pS.setString(3, user.getAddress().getCity());
	    pS.setInt(4, user.getAddress().getZipCode());
	    if(user.getAddress().getStreet() == null) {
	        pS.setString(5, null);
	    } else {
	        pS.setString(5, user.getAddress().getStreet());
	    }
	    if(user.getAddress().getHouseNumber() == null) {
	        pS.setNull(6, java.sql.Types.INTEGER);
	    } else {
	        pS.setInt(6, user.getAddress().getHouseNumber());
	    }

	    pS.executeUpdate();

	    pS.close();
	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "SQL Exception occoured during executing "
			    + "createUser(Transaction trans, User user, "
			    + "String pwHash, String salt)");
	    throw new InvalidDBTransferException();
	}

	return veriString;
    }

    /**
     * Returns a list containing all users stored in the database.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param pagination
     *            the Pagination object which contains the amount of elements
     *            which are to be retrieved
     * @return the list of users, or null if no users were retrieved
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     */
    public static ArrayList<User> getUsers(Transaction trans,
	    PaginationData pagination, String searchParam, String searchString)
	    throws InvalidDBTransferException {

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	int limit = pagination.getElementsPerPage();
	int offset = limit * pagination.getCurrentPageNumber();
	String orderParam = getOrderParam(pagination.getSortColumn());
	String dir = pagination.getSQLSortDirection();
	String query = null;

	switch (searchParam) {
	case "name":
	    query = GET_USERS_BY_NAME;
	    break;

	case "email":
	    query = GET_USER_BY_EMAIL;
	    break;

	case "nickname":
	    query = GET_USER_BY_NICKNAME;
	    break;

	default:
	    query = ALL_USERS;
	    return getAllUsers(conn, pagination,
		    String.format(query, orderParam, dir), limit, offset);

	}

	return getUsers(conn, pagination,
		String.format(query, orderParam, dir), searchString, limit,
		offset);
    }

    /**
     * @param conn
     * @param pagination
     * @param query
     * @param limit
     * @param offset
     * @return
     * @throws InvalidDBTransferException
     */
    private static ArrayList<User> getAllUsers(java.sql.Connection conn,
	    PaginationData pagination, String query, int limit, int offset)
	    throws InvalidDBTransferException {
	ArrayList<User> result = null;
	ResultSet resultSet = null;

	try (PreparedStatement pS = conn.prepareStatement(query)) {

	    pS.setInt(1, limit);
	    pS.setInt(2, offset);

	    resultSet = pS.executeQuery();
	    result = getResult(resultSet);

	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "Error occured during GetAllUsers Methode");
	    throw new InvalidDBTransferException();
	}
	return result;
    }

    /**
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private static ArrayList<User> getResult(ResultSet resultSet)
	    throws SQLException {

	ArrayList<User> result = new ArrayList<User>();

	try {

	    while (resultSet.next()) {
		User userToAdd = new User();
		if (resultSet.getString("first_name") == null) {
		    userToAdd.setFirstname("Nicht angegeben");
		} else {
		    userToAdd.setFirstname(resultSet.getString("first_name"));
		}
		if (resultSet.getString("name") == null) {
		    userToAdd.setLastname("Nicht angegeben");
		} else {
		    userToAdd.setLastname(resultSet.getString("name"));
		}
		if (resultSet.getDate("date_of_birth") == null) {
		    SimpleDateFormat dateformat = new SimpleDateFormat(
			    "dd/MM/yyyy");
		    Date jesusBirth = dateformat.parse("25/12/0001");
		    userToAdd.setDateOfBirth(jesusBirth);
		} else {
		    userToAdd.setDateOfBirth(new java.util.Date(resultSet
			    .getDate("date_of_birth").getTime()));
		}
		userToAdd.setEmail(resultSet.getString("email"));

		userToAdd.setUsername(resultSet.getString("nickname"));
		userToAdd.setUserID(resultSet.getInt("id"));
		result.add(userToAdd);
	    }

	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "Error occured during GetResult Methode");
	    throw new SQLException();
	} catch (ParseException e) {
	    LogHandler.getInstance().error(
		    "Error occured during Parsing Jesus Birthday");
	}
	return result;
    }

    /**
     * @param orderParam
     * @return
     */
    private static String getOrderParam(String orderParam) {

	switch (orderParam) {

	case "nickname":
	    return "nickname";

	case "name":
	    return "name";

	case "first_name":
	    return "first_name";

	case "date_of_birth":
	    return "date_of_birth";

	case "email":
	    return "email";

	}
	return null;
    }

    /**
     * Returns a list of users which names contain the search term the user has
     * entered.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param pagination
     *            the Pagination object which contains the amount of elements
     *            which are to be retrieved
     * @param searchString
     *            the user's search term
     * @param offset
     * @param limit
     * @return the list of users, or null if no users were retrieved
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * 
     */
    public static ArrayList<User> getUsers(java.sql.Connection conn,
	    PaginationData pagination, String query, String searchString,
	    int limit, int offset) throws InvalidDBTransferException {

	ArrayList<User> result = null;
	ResultSet resultSet = null;

	try (PreparedStatement pS = conn.prepareStatement(query)) {
	    pS.setString(1, searchString);
	    pS.setInt(2, limit);
	    pS.setInt(3, offset);

	    resultSet = pS.executeQuery();
	    result = getResult(resultSet);
	    return result;
	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "Error occured during GetUsers(connection) Methode");
	    throw new InvalidDBTransferException();
	}

    }

    /**
     * Returns a list of users which titles contain the search term the user has
     * entered. The list is ordered by the passed parameter.
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
     * @return the list of users ordered by the passed parameter, or null if no
     *         users were retrieved
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     */
    public static List<User> getUsersOrdered(Transaction trans,
	    PaginationData pagination, String searchParam, String searchString,
	    String orderParam) throws InvalidDBTransferException {

	return null;
    }

    /**
     * Returns a user assigned to the specified ID.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param userID
     *            the user's ID
     * @return the user assigned to the user ID, or null if no such user was
     *         found
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * 
     * @author Patrick Cretu
     */
    public static User getUser(Transaction trans, int userID)
	    throws InvalidDBTransferException {
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();
	User user = null;
	PreparedStatement stmt = null;
	ResultSet rst = null;
	String query = "SELECT nickname FROM \"users\" WHERE id=?";

	try {
	    stmt = conn.prepareStatement(query);
	    stmt.setInt(1, userID);

	    rst = stmt.executeQuery();
	    if (rst.next()) {
		user = getUser(trans, rst.getString(1));
	    }
	} catch (SQLException e) {
	    LogHandler
		    .getInstance()
		    .error("SQL Exception occoured during executing getUser(Transaction trans, int userID)");
	    throw new InvalidDBTransferException();
	}
	return user;
    }

    /**
     * Returns the status of the user to a specified ID
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param userID
     *            the user's ID
     * @return the status of the user
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * 
     * @author Katharina Hölzl
     */
    public static UserStatus getUserStatus(Transaction trans, int userID)
	    throws InvalidDBTransferException {

	UserStatus userStatus = null;

	// Prepare SQL- Request and database connection.
	PreparedStatement pS = null;
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	String sql = "SELECT status FROM \"users\" WHERE id=?";
	// catch potential SQL-Injection
	try {
	    pS = conn.prepareStatement(sql);
	    pS.setInt(1, userID);

	    // /execute preparedStatement, return resultSet as a list
	    // (here one entry in the list because the id is unique).
	    ResultSet res = pS.executeQuery();

	    // Execute next entry, return true if there is another entry,
	    // else false.
	    if (res.next()) {
		String userStatusString = res.getString("status");
		switch (userStatusString) {
		case "ANONYMOUS":
		    userStatus = UserStatus.ANONYMOUS;
		    break;
		case "NOT_ACTIVATED":
		    userStatus = UserStatus.NOT_ACTIVATED;
		    break;
		case "REGISTERED":
		    userStatus = UserStatus.REGISTERED;
		    break;
		case "INACTIVE":
		    userStatus = UserStatus.INACTIVE;
		    break;
		}
	    } else {
		userStatus = null;
	    }
	    pS.close();
	    res.close();

	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "SQL Exception occoured during executing "
			    + "getUserStatus(Transaction trans, int userID)");
	    throw new InvalidDBTransferException();
	}
	return userStatus;
    }

    /**
     * Returns the role of the user to a specified ID
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param userID
     *            the user's ID
     * @return the role of the user
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * 
     * @author Katharina Hölzl
     */
    public static UserRole getUserRole(Transaction trans, int userID)
	    throws InvalidDBTransferException {
	UserRole userRole = null;

	// Prepare SQL- Request and database connection.
	PreparedStatement pS = null;
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	String sql = "SELECT role FROM \"users\" WHERE id=?";
	// catch potential SQL-Injection
	try {
	    pS = conn.prepareStatement(sql);
	    pS.setInt(1, userID);

	    // /execute preparedStatement, return resultSet as a list
	    // (here one entry in the list because the id is unique).
	    ResultSet res = pS.executeQuery();

	    // Execute next entry, return true if there is another entry,
	    // else false.
	    if (res.next()) {
		String userRoleString = res.getString("role");
		switch (userRoleString) {
		case "REGISTERED_USER":
		    userRole = UserRole.REGISTERED_USER;
		    break;
		case "COURSE_LEADER":
		    userRole = UserRole.COURSE_LEADER;
		    break;
		case "SYSTEM_ADMINISTRATOR":
		    userRole = UserRole.SYSTEM_ADMINISTRATOR;
		    break;
		}
	    } else {
		userRole = null;
	    }
	    pS.close();
	    res.close();

	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "SQL Exception occoured during executing "
			    + "getUserRole(Transaction trans, int userID)");
	    throw new InvalidDBTransferException();
	}
	return userRole;
    }

    /**
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param veriString
     * 
     * @return true if it succeed, else false
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * 
     * @author Katharina Hölzl
     */
    public static boolean verifyUser(Transaction trans, String veriString)
	    throws InvalidDBTransferException {
	boolean success = false;

	PreparedStatement pS = null;
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	String sql = "UPDATE \"users\" SET email_verification=?, "
		+ "veri_string=?, status=?::status WHERE veri_string=?";
	// catch potential SQL-Injection
	try {
	    pS = conn.prepareStatement(sql);
	    pS.setBoolean(1, true);
	    pS.setString(2, null);
	    pS.setString(3, UserStatus.REGISTERED.toString());
	    pS.setString(4, veriString);

	    if (pS.executeUpdate() == 1) {
		success = true;
	    } else {
		success = false;
	    }
	    pS.close();
	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "SQL Exception occoured during executing "
			    + "verifyUser(Transaction trans, "
			    + "String veriString)");
	    throw new InvalidDBTransferException();
	}

	return success;
    }

    /**
     * Returns 0, if username or password is wrong or the inserted user does not
     * exist in the database. Otherwise returns the id of the user.
     * 
     * 
     * @param trans
     *            transaction object
     * @param username
     *            the username inserted by the user
     * @param passwordHash
     *            the password inserted by the user, which is hashed in the bean
     * @return the id of the user <br>
     *         or 0 if the username or password is wrong
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * 
     * @author Katharina Hölzl
     */
    public static int proveLogin(Transaction trans, String username,
	    String passwordHash) throws InvalidDBTransferException {

	final int wrongUsernameOrPassword = -1;
	final int accountNotActivated = -2;

	int id = wrongUsernameOrPassword;

	// Prepare SQL- Request and database connection.
	PreparedStatement pS = null;
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	String sql = "SELECT id, nickname, pw_hash, status, email_verification, admin_verification FROM \"users\" "
		+ " WHERE nickname=?";
	// catch potential SQL-Injection
	try {
	    pS = conn.prepareStatement(sql);
	    pS.setString(1, username);

	    // execute preparedStatement, return resultSet as a list
	    // (here one entry in the list because the user name is unique)
	    ResultSet res = pS.executeQuery();

	    // execute next entry, return true if there is another entry,
	    // else false.
	    if (res.next()) {
		// Execute passwort from the Resultset.
		String pwHashFromDB = res.getString("pw_hash");

		// Compare the saved password with the inserted one.
		if (passwordHash.equals(pwHashFromDB)) {

		    boolean emailVerification = res
			    .getBoolean("email_verification");
		    boolean adminVerification = res
			    .getBoolean("admin_verification");

		    // Check if the user is activated.
		    if (res.getString("status").equals(
			    UserStatus.REGISTERED.toString())) {
			if (emailVerification == true) {
			    if (SystemDAO.getActivationType(trans).equals(
				    Activation.EMAIL)) {
				id = res.getInt("id");
			    } else {
				if (adminVerification == true) {
				    id = res.getInt("id");
				} else {
				    id = accountNotActivated;
				}
			    }
			} else {
			    id = accountNotActivated;
			}
		    } else {
			id = accountNotActivated;
		    }
		} else {
		    id = wrongUsernameOrPassword;
		}
	    } else {
		id = wrongUsernameOrPassword;
	    }
	    pS.close();
	    res.close();
	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "SQL Exception occoured during executing "
			    + "proveLogin(Transaction trans, String username, "
			    + "String passwordHash)");
	    throw new InvalidDBTransferException();

	}
	return id;
    }

    /**
     * Returns all attributes of a user assigned to the passed user name.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param username
     *            the user's name
     * @return the user
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * 
     * @author Katharina Hölzl
     */

    public static User getUser(Transaction trans, String username)
	    throws InvalidDBTransferException {

	// Generate a new user object and filling with the user name.
	// Generate a new address object.
	User user = new User();
	user.setUsername(username);
	Address address = new Address();

	// Prepare SQL- Request and database connection.
	PreparedStatement pS = null;
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	String sql = "SELECT * FROM \"users\" WHERE nickname=?";

	// catch potential SQL-Injection
	try {
	    pS = conn.prepareStatement(sql);
	    pS.setString(1, username);

	    // execute preparedStatement, return resultSet as a list
	    // (here one entry in the list because the user name is unique)
	    ResultSet res = pS.executeQuery();
	    if (res.next()) {

		// Fill the user object with values from the database.
		user.setUserId(res.getInt("id"));
		user.setFirstname(res.getString("first_name"));
		user.setLastname(res.getString("name"));
		user.setEmail(res.getString("email"));
		if(res.getDate("date_of_birth") == null) {
		    user.setDateOfBirth(null);
		} else {
		    user.setDateOfBirth(new java.util.Date(res.getDate(
			"date_of_birth").getTime()));
		}

		String salutation = res.getString("form_of_address");
		switch (salutation) {
		case "MR":
		    user.setSalutation(Salutation.MR);
		    break;
		case "MS":
		    user.setSalutation(Salutation.MS);
		    break;
		}
		float accountBalance = res.getFloat("credit_balance");
		user.setAccountBalance(accountBalance);

		String userRole = res.getString("role");
		switch (userRole) {
		case "REGISTERED_USER":
		    user.setUserRole(UserRole.REGISTERED_USER);
		    break;
		case "COURSE_LEADER":
		    user.setUserRole(UserRole.COURSE_LEADER);
		    break;
		case "SYSTEM_ADMINISTRATOR":
		    user.setUserRole(UserRole.SYSTEM_ADMINISTRATOR);
		    break;
		}

		String userStatus = res.getString("status");
		switch (userStatus) {
		case "ANONYMOUS":
		    user.setUserStatus(UserStatus.ANONYMOUS);
		    break;
		case "NOT_ACTIVATED":
		    user.setUserStatus(UserStatus.NOT_ACTIVATED);
		    break;
		case "REGISTERED":
		    user.setUserStatus(UserStatus.REGISTERED);
		    break;
		case "INACTIVE":
		    user.setUserStatus(UserStatus.INACTIVE);
		    break;
		}

		conn.commit();

		sql = "SELECT * FROM \"user_addresses\" WHERE user_id=?";
		PreparedStatement pr = null;
		pr = conn.prepareStatement(sql);
		pr.setInt(1, user.getUserID());

		ResultSet res2 = pr.executeQuery();

		if (res2.next()) {
		    address.setId(res2.getInt("id"));
		    address.setCity(res2.getString("city"));
		    address.setCountry(res2.getString("country"));
		    address.setZipCode(res2.getInt("zip_code"));
		    address.setStreet(res2.getString("street"));
		    address.setHouseNumber(res2.getInt("house_nr"));
		} else {
		    address = null;
		}
		// Assign the address object to the user object.
		user.setAddress(address);
	    } else {

		user = null;
	    }
	    pS.close();
	    res.close();
	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "SQL Exception occoured during executing "
			    + "getUser(Transaction trans, String username)");
	    throw new InvalidDBTransferException();

	}
	// Returns the filled user object.
	return user;
    }

    /**
     * Returns the ID of a user assigned to the passed user name.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param username
     *            the user's name
     * @return the user's ID
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * 
     * @author Katharina Hölzl
     */
    public static int getUserID(Transaction trans, String username)
	    throws InvalidDBTransferException {

	final int userDoesNotExist = -1;

	int id = userDoesNotExist;

	PreparedStatement pS = null;
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	String sql = "SELECT id FROM \"users\" WHERE nickname=?";

	try {
	    pS = conn.prepareStatement(sql);
	    pS.setString(1, username);
	    ResultSet res = pS.executeQuery();
	    if (res.next()) {
		id = res.getInt("id");
	    } else {
		id = userDoesNotExist;
	    }
	    pS.close();
	    res.close();
	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "SQL Exception occoured during executing "
			    + "getUserID(Transaction trans, String username)");
	    throw new InvalidDBTransferException();
	}

	return id;
    }

    /**
     * Updates a user stored in the database. The user's attributes are replaced
     * by the ones of the passed user.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param user
     *            the user to be updated
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * @author Ricky Strohmeier
     */
    public static void updateUser(Transaction trans, User user, String pwHash, String salt)
	    throws InvalidDBTransferException {
    	PreparedStatement statement = null;
    	Connection connection = (Connection) trans;
    	java.sql.Connection conn = connection.getConn();
    
    	String updateQuery = "UPDATE \"users\" "
    		+ "SET first_name = ?, name = ?, nickname = ?, email = ?, pw_hash = ?, "
    		+ "pw_salt = ?, form_of_address = ?::form_of_address, date_of_birth = ? "
    		+ "WHERE id = ?;"
    		+ "UPDATE \"user_addresses\" "
    		+ "SET country = ?, city = ?, zip_code = ?, street = ?, house_nr = ? "
    		+ "WHERE user_id = ?";
    
    	try {
    	    //Update User Dates
    	    statement = conn.prepareStatement(updateQuery);
    	    statement.setString(1, user.getFirstname());
    	    statement.setString(2, user.getLastname());
    	    statement.setString(3, user.getUsername());
    	    statement.setString(4, user.getEmail());
    	    statement.setString(5, pwHash);
    	    statement.setString(6, salt);
    	    statement.setString(7, user.getSalutation().toString());

    	    java.sql.Date birthday = new java.sql.Date(user.getDateOfBirth()
    	            .getTime());
    	    
    	    statement.setDate(8, birthday);

    	    statement.setInt(9, user.getUserID());

    	    //Update Address
    	    statement.setString(10, user.getAddress().getCountry());
    	    statement.setString(11, user.getAddress().getCity());
    	    statement.setInt(12, user.getAddress().getZipCode());
    	    statement.setString(13, user.getAddress().getStreet());
    	    statement.setInt(14, user.getAddress().getHouseNumber());
    	    statement.setInt(15, user.getUserID());

    	    statement.executeUpdate();
    	    statement.close();
    	} catch (SQLException e) {
    	    LogHandler.getInstance().error(
    		    "SQL Exception occoured in updateUser from UserDAO");
    	    throw new InvalidDBTransferException();
    	} finally {
    	    try {
    		statement.close();
    	    } catch (SQLException e) {
    		LogHandler
    			.getInstance()
    			.error("Exception occoured in updateUser statment.close from UserDAO");
    	    }
    	}
    }

    /**
     * Overrides a user's password.
     * 
     * @param trans
     *            the transaction object which contains the database connection
     * @param mail
     *            the mail address of the user to be updated
     * @throws InvalidDBTransferException
     * @author Ricky Strohmeier
     */
    public static void overridePassword(Transaction trans, String mail,
	    String password) throws InvalidDBTransferException {
    	PreparedStatement statement = null;
    	Connection connection = (Connection) trans;
    	java.sql.Connection conn = connection.getConn();
    
    	String sql = "UPDATE \"users\" " + "SET pw_hash = ? "
    		+ "WHERE email = ?";
    
    	try {
    	    statement = conn.prepareStatement(sql);
    	    statement.setString(1, password);
    	    statement.setString(2, mail);
    	    statement.executeUpdate();
    	    statement.close();
    	} catch (SQLException e) {
    	    LogHandler
    		    .getInstance()
    		    .error("SQL Exception occoured during executing overridePassword(Transaction trans, String mail, String password");
    	    throw new InvalidDBTransferException();
    	}
    }

    /**
     * Deletes a user which is assigned to the passed ID.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param userID
     *            the ID of the user to be deleted
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * 
     * @author Patrick Cretu
     */
    public static void delete(Transaction trans, int userID, boolean deleteUser)
	    throws InvalidDBTransferException {
		Connection connection = (Connection) trans;
		java.sql.Connection conn = connection.getConn();
		PreparedStatement stmt = null;
		String query = null;
		String deleteUserQuery = "DELETE FROM \"users\" WHERE id = ?";
		String deleteImageQuery = "UPDATE \"users\" SET profile_image = NULL WHERE id = ?";
	
		try {
			if (deleteUser) {
				query = deleteUserQuery;
			} else {
				query = deleteImageQuery;
			}
			
		    stmt = conn.prepareStatement(query);
		    stmt.setInt(1, userID);
		    stmt.executeUpdate();
		} catch (SQLException e) {
		    LogHandler
			    .getInstance()
			    .error("SQL Exception occoured during executing uploadImage(Transaction trans, int userID, Part image)");
		    throw new InvalidDBTransferException();
		} finally {
		    if (stmt != null) {
			try {
			    stmt.close();
			} catch (SQLException e) {
			    LogHandler
				    .getInstance()
				    .error("SQL Exception occoured during executing uploadImage(Transaction trans, int userID, Part image)");
			    throw new InvalidDBTransferException();
			}
		    }
		}
    }

    /**
     * Returns a list of courses of which the user assigned to the passed ID is
     * leader of.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param userID
     *            the course leader's ID
     * @param pagination
     *            the Pagination object which contains the amount of elements
     *            which are to be retrieved
     * @return the list of courses which the user is leader of, or null if the
     *         user isn't leader of any course
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     */
    public static List<Course> getCoursesLeadedBy(Transaction trans,
	    int userID, PaginationData pagination)
	    throws InvalidDBTransferException {
	return null;
    }

    /**
     * Returns a list of participants of the the selected course.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param pagination
     *            the Pagination object which contains the amount of elements
     *            which are to be retrieved
     * @param courseID
     *            the id of the course from which you want to show the
     *            participants
     * @return list of participants
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * @author Katharina Hölzl
     */
    public static List<User> getParticipantsOfCourse(Transaction trans,
	    PaginationData pagination, int courseID)
	    throws InvalidDBTransferException {

	List<User> userList = new ArrayList<User>();

	// prepare SQL- request and database connection.
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	String sql = "SELECT DISTINCT u.id, u.nickname, u.email, u.profile_image, "
		+ "(SELECT EXISTS(SELECT * FROM inform_users WHERE user_id = cP.participant_id AND course_id = cP.course_id)) "
		+ "AS courseNews FROM course_participants cP, users u, inform_users iU "
		+ "WHERE cP.course_id = ? AND u.id = cP.participant_id "
		+ "ORDER BY %s %s LIMIT ? OFFSET ?;";

	sql = String.format(sql, getSortColumn(pagination.getSortColumn()),
		pagination.getSQLSortDirection());

	try (PreparedStatement pS = conn.prepareStatement(sql)) {
	    pS.setInt(1, courseID);
	    pS.setInt(2, pagination.getElementsPerPage());
	    pS.setInt(
		    3,
		    pagination.getCurrentPageNumber()
			    * pagination.getElementsPerPage());

	    ResultSet res = pS.executeQuery();
	    while (res.next()) {
		User user = new User();
		user.setUserId(res.getInt("id"));
		user.setEmail(res.getString("email"));
		user.setUsername(res.getString("nickname"));
		user.setCourseNewsSubscribed(res.getBoolean("courseNews"));
		user.setProfilImage(res.getBytes("profile_image"));
		userList.add(user);
	    }
	    res.close();
	} catch (SQLException e) {
	    LogHandler
                .getInstance()
                .error("SQL Exception occoured during executing "
                    + "getParticipantsOfCourse(Transaction trans, "
                    + "PaginationData pagination, int courseID)");
            throw new InvalidDBTransferException();
	}

	return userList;
    }

    /**
     * Delete the selected participants from the course.
     * 
     * @param trans
     *          the Transaction object which contains the connection to the
     *          database
     * @param courseID
     *          the id of the course from which the users must be deleted
     * @param usersToRemove
     *          list of users which must be deleted
     * @return true if deleting was successful, else false
     * @author Katharina Hölzl
     */
    public static boolean removeParticipantsFromCourse(Transaction trans,
	    int courseID, List<User> usersToRemove) {
        
	boolean success = false;
	
	if (usersToRemove != null && usersToRemove.size() > 0) {
	    
	    // prepare SQL- request and database connection.
	    Connection connection = (Connection) trans;
	    java.sql.Connection conn = connection.getConn();

	    String sql = "DELETE FROM course_participants WHERE course_id = ? "
	            + "and participant_id IN (?";
	    for (int i = 1; i < usersToRemove.size(); i++) {
		sql += ",?";
	    }
	    sql += ");";

	    try (PreparedStatement pS = conn.prepareStatement(sql)) {
		pS.setInt(1, courseID);
		int counter = 2;
		for (User user : usersToRemove) {
		    pS.setInt(counter, user.getUserID());
		    counter++;
		}
		System.out.println(pS.toString());
		if (pS.executeUpdate() > 0) {
		    success = true;
		} else {
		    success = false;
		}

	    } catch (SQLException e) {
	        LogHandler
                    .getInstance()
                    .error("SQL Exception occoured during executing "
                            + "removeParticipantsFromCourse(Transaction trans, "
                            + "int courseID, List<User> usersToRemove)");
	        throw new InvalidDBTransferException();
	    }
	} else {
	    success = true;
	}
	return success;
    }

    /**
     * 
     * Returns the number of participants of the course. This method is used 
     * for pagination to get the number of needed sites.
     * 
     * @param trans
     *          the Transaction object which contains the connection to the
     *          database
     * @param courseID
     *          the id of the course from which the participants should be 
     *          displayed
     * @return the number of participants of the course
     * @author Katharina Hölzl
     */
    public static int getNumberOfParticipants(Transaction trans, int courseID) {

	int numberOfParticipants = 0;

	// prepare SQL- request and database connection.
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	String sql = "SELECT COUNT (*) as courseParticipantNumber FROM "
		+ "(SELECT DISTINCT * FROM course_participants cP, users u "
		+ "WHERE cP.course_id = ? AND u.id = cP.participant_id) "
		+ "AS courseParticipants;";

	try (PreparedStatement pS = conn.prepareStatement(sql)) {
	    pS.setInt(1, courseID);
	    ResultSet res = pS.executeQuery();
	    res.next();
	    numberOfParticipants = res.getInt("courseParticipantNumber");
	    res.close();
	} catch (SQLException e) {
	    LogHandler
                .getInstance()
                .error("SQL Exception occoured during executing "
                        + "getNumberOfParticipants(Transaction trans, "
                        + "int courseID)");
            throw new InvalidDBTransferException();
	}

	return numberOfParticipants;
    }

    /**
     * Avoids SQL-Injection with a switch case
     * 
     * @param sortColumn
     *                the selected column  
     * @return the name of the selected column, if no column is selected 
     *         return 'nickname'
     * @author Katharina Hölzl
     */
    private static String getSortColumn(String sortColumn) {
	switch (sortColumn) {
	
	case "nickname":
	    return "nickname";
	case "email":
	    return "email";
	case "courseNews":
	    return "courseNews";
	case "id":
	    return "id";
	case "first_name":
	    return "first_name";
	case "name":
	    return "name";
	case "date_of_birth":
	    return "date_of_birth";
	default:
	    return "nickname";
	}
    }

    public static List<User> getParticipiantsOfCourseUnit(Transaction trans,
	    PaginationData pagination, int courseUnitId)
	    throws InvalidDBTransferException {
	return null;
    }

    /**
     * TODO
     * 
     * @author Sebastian
     * @param trans
     * @param userID
     * @param courseID
     * @return
     */
    /**
     * @param trans
     * @param userID
     * @param courseID
     * @return
     * @throws InvalidDBTransferException
     */
    public static boolean userWantsToBeInformed(Transaction trans, int userID,
	    int courseID) throws InvalidDBTransferException {
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	String searchUserInUserToInform = "SELECT * FROM \"inform_users\" WHERE user_id=? AND course_id=?";

	try {
	    boolean returnStatment = isInTable(userID, courseID, conn,
		    searchUserInUserToInform);
	    LogHandler.getInstance().debug(
		    "UserWantsToBeInformed methode was succesfull");
	    return returnStatment;

	} catch (SQLException e) {
	    LogHandler.getInstance().debug(
		    "Error occured during UserWantsToBeInformed methode ");
	    throw new InvalidDBTransferException();
	}
    }

    /**
     * 
     * @author Sebastian
     * @param userID
     * @param courseID
     * @param conn
     * @param searchUserInUserToInform
     * @return
     * @throws SQLException
     */
    private static boolean isInTable(int userID, int courseID,
	    java.sql.Connection conn, String searchUserInUserToInform)
	    throws SQLException {
	PreparedStatement pS;
	ResultSet resultSet;
	pS = conn.prepareStatement(searchUserInUserToInform);
	pS.setInt(1, userID);
	pS.setInt(2, courseID);
	resultSet = pS.executeQuery();

	boolean returnStatment = resultSet.next();
	resultSet.close();
	pS.close();
	return returnStatment;
    }

    /**
     * 
     * @author Sebastian
     * @param trans
     * @param userID
     * @param courseID
     * @return
     * @throws InvalidDBTransferException
     */
    public static boolean userIsParticpant(Transaction trans, int userID,
	    int courseID) throws InvalidDBTransferException {
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	String searchUserCourse = "SELECT * FROM \"course_participants\" WHERE participant_id=? AND course_id=?";

	try {
	    boolean returnStatement = isInTable(userID, courseID, conn,
		    searchUserCourse);
	    LogHandler.getInstance().debug(
		    "UserIsParticipant methode was succesfull");
	    return returnStatement;
	} catch (SQLException e) {
	    LogHandler.getInstance().debug(
		    "Error occured during UserIsParticipant methode ");
	    throw new InvalidDBTransferException();
	}
    }

    /**
     * @author Sebastian Schwarz
     * @param trans
     * @param userID
     * @param newAccountBalance
     */
    public static void updateAccountBalance(Transaction trans, int userID,
	    float newAccountBalance) {
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	// TODO spalte evlt noch nciht richtig

	String updateAccountBalance = "UPDATE \"users\" SET credit_balance = ? WHERE id = ?";

	try {
	    PreparedStatement pS = conn.prepareStatement(updateAccountBalance);
	    pS.setFloat(1, newAccountBalance);
	    pS.setInt(2, userID);
	    pS.executeUpdate();
	    pS.close();
	    LogHandler.getInstance().debug(
		    "AccountBalance succesfully update of User:" + userID);
	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "Error acurred while updating Account Balance of User:"
			    + userID);
	    throw new InvalidDBTransferException();
	}
    }

    /**
     * @author Sebastian Schwarz
     * @param trans
     * @param userID
     * @param courseUnitID
     * @return
     */
    public static boolean userIsParticipantInCourseUnit(Transaction trans,
	    int userID, int courseUnitID) throws InvalidDBTransferException {
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	String searchUserCourse = "SELECT * FROM \"course_unit_participants\" WHERE participant_id=? AND course_unit_id=?";

	try {
	    boolean returnStatement = isInTable(userID, courseUnitID, conn,
		    searchUserCourse);
	    LogHandler.getInstance().debug(
		    "UserIsParticipantInCourseUnit methode was succesfull");
	    return returnStatement;
	} catch (SQLException e) {
	    LogHandler
		    .getInstance()
		    .debug("Error occured during UserIsParticipantInCourseUnit methode ");
	    throw new InvalidDBTransferException();
	}
    }

    /**
     * 
     * @author Sebastian Schwarz
     * @param trans
     * @param courseID
     * @return
     */
    public static byte[] getImage(Transaction trans, int userID)
	    throws InvalidDBTransferException {
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();
	byte[] picture;

	String selectImage = "SELECT profile_image FROM \"users\" WHERE id=?";

	try {
	    PreparedStatement pS = conn.prepareStatement(selectImage);
	    pS.setInt(1, userID);
	    ResultSet resultSet = pS.executeQuery();

	    if (resultSet.next()) {
		picture = resultSet.getBytes("profile_image");
		LogHandler.getInstance().debug(
			"User Picture succesfully loaded");
		pS.close();
		return picture;
	    } else {
		LogHandler.getInstance().debug("No User Picture found");
		pS.close();
		return null;
	    }

	} catch (SQLException e) {
	    // Error Handling
	    LogHandler
		    .getInstance()
		    .error("Exception occured during loading User Picture from Database");
	    throw new InvalidDBTransferException();
	}
    }

    /**
     * 
     * @author Sebastian Schwarz
     * @param trans
     * @param email
     * @return
     */
    public static User getUserPerMail(Transaction trans, String email) {
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();
	User user = null;
	PreparedStatement stmt = null;
	ResultSet rst = null;
	String query = "SELECT nickname FROM \"users\" WHERE email=?";
	try {
	    stmt = conn.prepareStatement(query);
	    stmt.setString(1, email);

	    rst = stmt.executeQuery();
	    if (rst.next()) {
		user = getUser(trans, rst.getString(1));
	    }
	} catch (SQLException e) {
	    LogHandler
		    .getInstance()
		    .error("SQL Exception occoured during executing getUser(Transaction trans, email)");
	    throw new InvalidDBTransferException();
	}
	return user;
    }

    /**
     * @param transaction
     * @return
     */
    public static int getNumberOfUsers(Transaction transaction) {
	Connection connection = (Connection) transaction;
	java.sql.Connection conn = connection.getConn();

	ResultSet resultSet = null;
	String sql = "SELECT COUNT (*) FROM \"users\"";

	try (PreparedStatement pS = conn.prepareStatement(sql)) {
	    resultSet = pS.executeQuery();
	    if (resultSet.next()) {
		return resultSet.getInt(1);
	    }

	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "SQL Exception occoured during executing getNumberOfUsers");
	    throw new InvalidDBTransferException();
	}

	return 0;
    }

    /**
     * @param transaction
     * @param searchParam
     * @return
     */
    public static int getNumberOfUsersWithThisName(Transaction transaction,
	    String searchParam) {
	Connection connection = (Connection) transaction;
	java.sql.Connection conn = connection.getConn();

	ResultSet resultSet = null;
	String sql = "SELECT COUNT (*) FROM \"users\" where name = ?";

	try (PreparedStatement pS = conn.prepareStatement(sql)) {
	    pS.setString(1, searchParam);
	    resultSet = pS.executeQuery();
	    if (resultSet.next()) {
		return resultSet.getInt(1);
	    }

	} catch (SQLException e) {
	    LogHandler
		    .getInstance()
		    .error("SQL Exception occoured during executing getNumberOfUsersWithThisName");
	    throw new InvalidDBTransferException();
	}

	return 0;
    }

    /**
     * Return the number of users which are not yet activated by a 
     * administrator
     * 
     * @param trans
     *          the Transaction object which contains the connection to the
     *          database
     * @return the number of users which are not yet activated by a 
     *          administrator or course leader, return -1 if every user is yet 
     *          activated by a administrator
     * @throws InvalidDBTransferException
     *                          if any error occurred during the execution 
     *                          of the method
     * @author Katharina Hölzl
     */
    public static int getNumberOfNotAdminActivatedUsers(Transaction trans)
	    throws InvalidDBTransferException {
	int numberOfNotAdminActivatedUsers = -1;

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	ResultSet res = null;
	String sql = "SELECT COUNT (*) AS numberOfNotAdminActivatedUsers FROM "
	        + "users where admin_verification = ?";

	try (PreparedStatement pS = conn.prepareStatement(sql)) {
	    pS.setBoolean(1, false);

	    res = pS.executeQuery();
	    if (res.next()) {
		numberOfNotAdminActivatedUsers = res.getInt(1);
	    } else {
		numberOfNotAdminActivatedUsers = -1;
	    }

	} catch (SQLException e) {
	    LogHandler
		    .getInstance()
		    .error("SQL Exception occoured during executing "
		            + "getNumberOfNotAdminActivatedUsers(Transaction "
		            + "trans)");
	    throw new InvalidDBTransferException();
	}
	return numberOfNotAdminActivatedUsers;
    }

    /**
     * Returns the list of the users which are not yet activated by a 
     * administrator or a course leader
     * 
     * @param trans
     *          the Transaction object which contains the connection to the
     *          database
     * @param pagination
     *          the Pagination object which contains the amount of elements
     *          which are to be retrieved
     * @return a list of the users which are not yet activated by a 
     *         administrator
     * @throws InvalidDBTransferException
     *                          if any error occurred during the execution of 
     *                          the method
     * @author Katharina Hölzl
     */
    public static List<User> getNotAdminActivatedUsers(Transaction trans,
	    PaginationData pagination) throws InvalidDBTransferException {
        
	List<User> notAdminActivatedUsers = new ArrayList<User>();

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	ResultSet res = null;
	String sql = "SELECT id, first_name, name, email, nickname, date_of_birth "
		+ "FROM users WHERE admin_verification = ? "
		+ "ORDER BY %s %s LIMIT ? OFFSET ?;";

	sql = String.format(sql, getSortColumn(pagination.getSortColumn()),
		pagination.getSQLSortDirection());

	try (PreparedStatement pS = conn.prepareStatement(sql)) {
	    pS.setBoolean(1, false);
	    pS.setInt(2, pagination.getElementsPerPage());
	    pS.setInt(3, pagination.getCurrentPageNumber()
			    * pagination.getElementsPerPage());

	    res = pS.executeQuery();

	    while (res.next()) {
		User user = new User();
		user.setUserId(res.getInt("id"));
		user.setFirstname(res.getString("first_name"));
		user.setLastname(res.getString("name"));
		user.setEmail(res.getString("email"));
		user.setUsername(res.getString("nickname"));
		
		if(res.getDate("date_of_birth") == null) {
		    user.setDateOfBirth(null);
		} else {
		    user.setDateOfBirth(new java.util.Date(res.getDate(
			"date_of_birth").getTime()));
		}
		notAdminActivatedUsers.add(user);
	    }

	} catch (SQLException e) {
	    LogHandler
		    .getInstance()
		    .error("SQL Exception occoured during executing "
		            + "getNotAdminActivatedUsers(Transaction trans, "
		            + "PaginationData pagination)");
	    throw new InvalidDBTransferException();
	}
	return notAdminActivatedUsers;
    }

    /**
     * @param trans
     * @param userID
     * @param amount
     * @throws InvalidDBTransferException
     * @author evtl TF
     */
    public static void updateAccountBalanceInOne(Transaction trans, int userID,
	    float amount) throws InvalidDBTransferException {

	String balanceQuery = "UPDATE users SET credit_balance = credit_balance + ? WHERE id=?";

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	try (PreparedStatement stmt = conn.prepareStatement(balanceQuery)) {

	    stmt.setFloat(1, amount);
	    stmt.setInt(2, userID);
	    stmt.executeUpdate();

	} catch (SQLException e) {
	    LogHandler.getInstance().error(
		    "Error occured during updating account balance of "
			    + userID + ".");
	    throw new InvalidDBTransferException();
	}
    }
    
    
    /**
     * Activates the selected users.
     * 
     * @param trans
     *          the Transaction object which contains the connection to the
     *          database
     * @param usersToActivate
     *                  list of the users which are selected to activate
     * @return true if the activation of the users was successful, else false
     * @throws InvalidDBTransferException
     *                          if any error occurred during the execution of 
     *                          the method
     * @author Katharina Hölzl
     */
    public static boolean AdminActivateUsers(Transaction trans,
            List<User> usersToActivate) throws InvalidDBTransferException {
        
        boolean success = false;
        
        if (usersToActivate != null && usersToActivate.size() > 0) {
            
            // prepare SQL- request and database connection.
            Connection connection = (Connection) trans;
            java.sql.Connection conn = connection.getConn();

            String sql = "UPDATE users SET admin_verification = ? WHERE id "
                    + "IN (?";
            
            for (int i = 1; i < usersToActivate.size(); i++) {
                sql += ",?";
            }
            sql += ");";

            try (PreparedStatement pS = conn.prepareStatement(sql)) {
                pS.setBoolean(1, true);
                int counter = 2;
                
                for (User user : usersToActivate) {
                    pS.setInt(counter, user.getUserID());
                    counter++;
                }
                
                if (pS.executeUpdate() > 0) {
                    success = true;
                } else {
                    success = false;
                }

            } catch (SQLException e) {
                LogHandler
                    .getInstance()
                    .error("SQL Exception occoured during executing "
                            + "AdminActivateUsers(Transaction trans, "
                            + "List<User> usersToActivate)");
                throw new InvalidDBTransferException();
            }
        } else {
            success = true;
        }
        return success;
    }
    

}