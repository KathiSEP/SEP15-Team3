/**
 /**
 * This package represents the Data Access Objects of the ofCourse system.
 */
package de.ofCourse.Database.dao;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.Part;

import de.ofCourse.exception.InvalidDBTransferException;
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
            //execute preparedStatement, return resultSet as a list 
            //(here one entry in the list because the user name is unique)
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
            LogHandler
                    .getInstance()
                    .error("SQL Exception occoured during executing "
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

	    //execute preparedStatement, return resultSet as a list 
            //(here one entry in the list because the email is unique)
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
	    LogHandler
		    .getInstance()
		    .error("SQL Exception occoured during executing "
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
    public static boolean nickTaken(Transaction trans, String nickname) throws InvalidDBTransferException {
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
			LogHandler.getInstance().
				error("SQL Exception occoured during executing existingNick(Transaction trans, String nickname)");
			throw new InvalidDBTransferException();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					LogHandler.getInstance()
				    .error("SQL Exception occoured during closing PreparedStatement in nickTaken(Transaction trans, String nickname)");
					throw new InvalidDBTransferException();
				}
			}
			if (rst != null) {
				try {
					rst.close();
				} catch (SQLException e) {
					LogHandler.getInstance()
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
    public static void uploadImage(Transaction trans, int userID, Part image) throws InvalidDBTransferException {
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
			LogHandler.getInstance().error("SQL Exception occoured during executing uploadImage(Transaction trans, int userID, Part image)");
			throw new InvalidDBTransferException();
		} catch (IOException e) {
			LogHandler.getInstance().error("SQL Exception occoured during executing uploadImage(Transaction trans, int userID, Part image)");
			throw new InvalidDBTransferException();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					LogHandler.getInstance().error("SQL Exception occoured during executing uploadImage(Transaction trans, int userID, Part image)");
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
                                    String pwHash, String salt)
	    throws InvalidDBTransferException {

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
	    pS.setString(5, user.getAddress().getStreet());
	    pS.setInt(6, user.getAddress().getHouseNumber());
	    
	    pS.executeUpdate();

	    pS.close();
	} catch (SQLException e) {
	    LogHandler
		    .getInstance()
		    .error("SQL Exception occoured during executing "
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
    public static List<User> getUsers(Transaction trans,
	    PaginationData pagination) throws InvalidDBTransferException {
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
     * @return the list of users, or null if no users were retrieved
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     */
    public static List<User> getUsers(Transaction trans,
	    PaginationData pagination, String searchParam, String searchString)
	    throws InvalidDBTransferException {
	return null;
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
			LogHandler.getInstance()
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

	    ///execute preparedStatement, return resultSet as a list 
            //(here one entry in the list because the id is unique).
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
	    LogHandler
		    .getInstance()
		    .error("SQL Exception occoured during executing "
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

	  ///execute preparedStatement, return resultSet as a list 
            //(here one entry in the list because the id is unique).
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
	    LogHandler
		    .getInstance()
		    .error("SQL Exception occoured during executing "
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
	    LogHandler
		    .getInstance()
		    .error("SQL Exception occoured during executing "
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

	String sql = "SELECT id, nickname, pw_hash, status FROM \"users\" "
	            + " WHERE nickname=?";
	// catch potential SQL-Injection
	try {
	    pS = conn.prepareStatement(sql);
	    pS.setString(1, username);

	    //execute preparedStatement, return resultSet as a list 
            //(here one entry in the list because the user name is unique)
	    ResultSet res = pS.executeQuery();

	    // execute next entry, return true if there is another entry, 
            // else false.
	    if (res.next()) {
		// Execute passwort from the Resultset.
		String pwHashFromDB = res.getString("pw_hash");

		// Compare the saved password with the inserted one.
		if (passwordHash.equals(pwHashFromDB)) {

		    // Check if the user is activated.
		    if (res.getString("status").equals(
			    UserStatus.REGISTERED.toString())) {
			id = res.getInt("id");
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
	    LogHandler
		    .getInstance()
		    .error("SQL Exception occoured during executing "
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

	    //execute preparedStatement, return resultSet as a list 
            //(here one entry in the list because the user name is unique)
	    ResultSet res = pS.executeQuery();
	    if (res.next()) {

		// Fill the user object with values from the database.
		user.setUserId(res.getInt("id"));
		user.setFirstname(res.getString("first_name"));
		user.setLastname(res.getString("name"));
		user.setEmail(res.getString("email"));
		user.setDateOfBirth(new java.util.Date(res.getDate(
			"date_of_birth").getTime()));

		String salutation = res.getString("form_of_address");
		switch (salutation) {
		case "MR":
		    user.setSalutation(Salutation.MR);
		case "MS":
		    user.setSalutation(Salutation.MS);
		    break;
		}

		user.setProfilImage(res.getBytes("profile_image"));

		String userRole = res.getString("role");
		switch (userRole) {
		case "REGISTERED_USER":
		    user.setUserRole(UserRole.REGISTERED_USER);
		case "COURSE_LEADER":
		    user.setUserRole(UserRole.COURSE_LEADER);
		case "SYSTEM_ADMINISTRATOR":
		    user.setUserRole(UserRole.SYSTEM_ADMINISTRATOR);
		    break;
		}

		String userStatus = res.getString("status");
		switch (userStatus) {
		case "ANONYMOUS":
		    user.setUserStatus(UserStatus.ANONYMOUS);
		case "NOT_ACTIVATED":
		    user.setUserStatus(UserStatus.NOT_ACTIVATED);
		case "REGISTERED":
		    user.setUserStatus(UserStatus.REGISTERED);
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
	    LogHandler
		    .getInstance()
		    .error("SQL Exception occoured during executing "
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
	    LogHandler
		    .getInstance()
		    .error("SQL Exception occoured during executing "
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
    public static void updateUser(Transaction trans, User user, String pwHash)
	    throws InvalidDBTransferException {
    	PreparedStatement statement = null;
    	Connection connection = (Connection) trans;
    	java.sql.Connection conn = connection.getConn();
    
    	String sql = "UPDATE \"users\" "
    		+ "SET first_name = ?, name = ?, email = ?, pw_hash = ?, "
    		+ "date_of_birth = ?, form_of_address = ?, nickname = ? "
    		+ "WHERE id = ?";
    
    	try {
    	    statement = conn.prepareStatement(sql);
    		statement.setString(1, user.getFirstname());
    		statement.setString(2, user.getLastname());
    		statement.setString(3, user.getEmail());
    	    statement.setString(4, pwHash);
    		statement.setDate(5, (Date) user.getDateOfBirth());
    		statement.setString(6, user.getSalutation().toString());
    	    statement.setString(7, user.getUsername());
    	    statement.setInt(8, user.getUserID());
    	    statement.executeUpdate();
    	    statement.close();
    	} catch (SQLException e) {
    	    LogHandler.getInstance().error("SQL Exception occoured in updateUser from UserDAO");
    	    throw new InvalidDBTransferException();
        } finally {
            try {
                statement.close();                
            } catch(SQLException e) {
                LogHandler.getInstance().error("Exception occoured in updateUser statment.close from UserDAO");
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
    public static void deleteUser(Transaction trans, int userID)
	    throws InvalidDBTransferException {
    	Connection connection = (Connection) trans;
        java.sql.Connection conn = connection.getConn();
        PreparedStatement stmt = null;
    	String query = "DELETE FROM \"users\" WHERE id = ?";
    	
        try {
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, userID);
            stmt.executeUpdate();
		} catch (SQLException e) {
			LogHandler.getInstance().error("SQL Exception occoured during executing uploadImage(Transaction trans, int userID, Part image)");
			throw new InvalidDBTransferException();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					LogHandler.getInstance().error("SQL Exception occoured during executing uploadImage(Transaction trans, int userID, Part image)");
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

    public static List<User> getParticipiantsOfCourse(Transaction trans,
	    PaginationData pagination, int courseID)
	    throws InvalidDBTransferException {
	return null;
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
    public static void updateAccountBalance(Transaction trans, int userID, float newAccountBalance ){
        Connection connection = (Connection) trans;
        java.sql.Connection conn = connection.getConn();
        
        //TODO spalte evlt noch nciht richtig
        
        String updateAccountBalance = "UPDATE \"users\" SET credit_balance = ? WHERE id = ?";
        
        try{
            PreparedStatement pS = conn.prepareStatement(updateAccountBalance);
            pS.setFloat(1, newAccountBalance);
            pS.setInt(2, userID);
            pS.executeUpdate();
            pS.close();
            LogHandler.getInstance().debug("AccountBalance succesfully update of User:" + userID);
        }catch(SQLException e){
            LogHandler.getInstance().error("Error acurred while updating Account Balance of User:" + userID);
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
    private static boolean userIsParticipantInCourseUnit(Transaction trans, int userID, int courseUnitID) throws InvalidDBTransferException{
        Connection connection = (Connection) trans;
        java.sql.Connection conn = connection.getConn();
        
        String searchUserCourse = "SELECT * FROM \"course_unit_participants\" WHERE participant_id=? AND course_unit_id=?";
        
        try{
            boolean returnStatement = isInTable(userID, courseUnitID, conn, searchUserCourse);
            LogHandler.getInstance().debug(
                    "UserIsParticipantInCourseUnit methode was succesfull");
                return returnStatement;
            } catch (SQLException e) {
                LogHandler.getInstance().debug(
                    "Error occured during UserIsParticipantInCourseUnit methode ");
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
    public static byte[] getImage(Transaction trans, int userID) throws InvalidDBTransferException{
        Connection connection = (Connection) trans;
        java.sql.Connection conn = connection.getConn();
        byte[] picture;
        
        String selectImage= "SELECT profile_image FROM \"users\" WHERE id=?";
        
        try{
            PreparedStatement pS = conn.prepareStatement(selectImage);
            pS.setInt(1, userID);
            ResultSet resultSet = pS.executeQuery();
            
            
            if(resultSet.next()){
                picture = resultSet.getBytes("profile_image");
                LogHandler.getInstance().debug(
                        "User Picture succesfully loaded");
                pS.close();
                return picture;
            }else{
                LogHandler.getInstance().debug(
                        "No User Picture found");
                pS.close();
                return null;
            }
            
            
        } catch (SQLException e) {
            // Error Handling
            LogHandler.getInstance().error(
                "Exception occured during loading User Picture from Database");
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
            LogHandler.getInstance()
                .error("SQL Exception occoured during executing getUser(Transaction trans, email)");
            throw new InvalidDBTransferException();
        }
        return user;
    }

}