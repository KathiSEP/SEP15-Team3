/**
 * This package represents the Data Access Objects of the ofCourse system.
 */
package de.ofCourse.Database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Address;
import de.ofCourse.model.Course;
import de.ofCourse.model.PaginationData;
import de.ofCourse.model.User;
import de.ofCourse.model.UserRole;
import de.ofCourse.model.UserStatus;
import de.ofCourse.system.Transaction;

/**
 * Provides methods for transactions with the users stored in the database such
 * as creating, retrieving, updating or deleting users. Also retrieves a course
 * leader's list of courses.
 * 
 * <p>
 * Each method has a Transaction parameter, which contains the SQL connection
 * to the database, in order to assure that multiple, consecutive method calls
 * within a certain method use the same connection.
 * </p>
 * 
 * <p>
 * This class is required in the business logic of the system, more precisely
 * in the ManagedBeans of the package <code>de.ofCourse.action</code>.
 * </p>
 * 
 * @author Patrick Cretu
 *
 */
public class UserDAO {

    /**
     * Adds a new user to the list of users in the database.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
	 *            database
     * @param user
     *            the user to be added
     * @throws InvalidDBTransferException if any error occurred during the
     * execution of the method
     */
    public static void createUser(Transaction trans, User user)
    		throws InvalidDBTransferException {
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
     * @throws InvalidDBTransferException if any error occurred during the
     * execution of the method
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
     * @throws InvalidDBTransferException if any error occurred during the
     * execution of the method
     */
    public static List<User> getUsers(Transaction trans,
    		PaginationData pagination,String searchParam, String searchString)
    				throws InvalidDBTransferException {
	return null;
    }

    /**
     * Returns a list of users which titles contain the search term the user
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
     * @return the list of users ordered by the passed parameter, or null if no
     * users were retrieved
     * @throws InvalidDBTransferException if any error occurred during the
     * execution of the method
     */
    public static List<User> getUsersOrdered(Transaction trans,
    		PaginationData pagination,String searchParam, String searchString, String orderParam)
	    throws InvalidDBTransferException {
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
     * found
     * @throws InvalidDBTransferException if any error occurred during the
     * execution of the method
     */
    public static User getUser(Transaction trans, int userID)
    		throws InvalidDBTransferException {
	return null;
    }

    /**
     * Returns 0, if username or password is wrong or the inserted user does not 
     * exist in the database. Otherwise returns the id of the user.
     * 
     * @param username
     * 		the username inserted by the user
     * @param passwordHash
     * 		the password inserted by the user, which is hashed in the bean
     * @return the id of the user 
     * 		<br>or 0 if the username or password is wrong
     * @throws InvalidDBTransferException if any error occurred during the
     * execution of the method
     */
    public static int proveLogin(String username, String passwordHash)
    	throws InvalidDBTransferException{
	return 0;
    }
    
    
    
    
    public static User getUser(Transaction trans, String username)
		throws InvalidDBTransferException {
	
	//Neues Userobjekt erstellen und mit dem Benutzernamen f�llen.  
	//Neues Adressobjekt erstellen.
	User user = new User();
	user.setUsername(username);
	Address address = new Address();
	
	//SQL- Abfrage vorbereiten und Connection zur Datenbank erstellen.
	PreparedStatement pS = null;
	Connection con = (Connection) trans.conn;
	
	//Datenbankabfrage
	String sql = "SELECT * FROM users WHERE nickname=?";
	
	
	//m�gliche SQL-Injektion abfangen
	try {
	    pS = con.prepareStatement(sql);	    
	    pS.setString(1, username);
	    
	    //preparedStatement ausf�hren, gibt resultSet als Liste zur�ck (hier
	    //ein Eintrag in der Liste, da Benutzername einzigartig).
	    ResultSet res = pS.executeQuery();
	    
	    //N�chten Eintrag aufrufen, gibt true zur�ck, falls es weiteren 
	    //Eintrag gibt, ansonsten null.
	    if(res.next()) {
		
		//Userobjekt mit Werten aus der Datenbank bef�llen.
		user.setUserId(res.getInt("id"));
        	user.setFirstname(res.getString("first_name"));
        	user.setLastname(res.getString("name"));
        	user.setEmail(res.getString("mail"));
        	user.setDateOfBirth(res.getDate("date_of_bith"));
        	user.setSalutation(res.getString("form_of_adress"));
        	user.setProfilImage(res.getString("profile_image"));
        	String userRole = res.getString("role");
        		switch(userRole) {
        		case "registered_user":
        		    user.setUserRole(UserRole.REGISTERED_USER);
        		case "course_instructor":
        		    user.setUserRole(UserRole.COURSE_LEADER);
        		case "administrator":
        		    user.setUserRole(UserRole.SYSTEM_ADMINISTRATOR);
        		default:
        		     //TODO Fehlermeldung n�tig ? 
        		}
        	
        	String userStatus =res.getString("status");
        		switch(userStatus){
        		case "anonymous":
        		    user.setUserStatus(UserStatus.ANONYMOUS);
        		case "not_activated":
        		    user.setUserStatus(UserStatus.NOT_ACTIVATED);
        		case "registered":
        		    user.setUserStatus(UserStatus.REGISTERED);
        		case "inactive":
        		    user.setUserStatus(UserStatus.INACTIVE);
        		default:
        		    //TODO Fehlermeldung n�tig ?
        		}

        	//neue Datenbankabfrage f�r die Adresse des Benutzers
        	sql = "SELECT * FROM addresses WHERE user=?";
        	pS = con.prepareStatement(sql);	    
        	pS.setInt(1, user.getUserID()); 
        	
        	res = pS.executeQuery();
        	if(res.next()) {
        	    address.setId(res.getInt("id"));
        	    address.setCity(res.getString("city"));
        	    address.setCountry(res.getString("country"));
        	    address.setZipCode(res.getInt("zip_code"));
        	    address.setStreet(res.getString("street"));
        	    address.setHouseNumber(res.getInt("house_nr"));
        	} else {
        	    //Fehler
        	}
        	//dem Userobjekt das Adressobjekt zuweisen
    	    	user.setAddress(address);
	    }
	    else
	    {
		//TODO Fehler, kein Benutzer mit diesem Benutzernamen
		return null;
	    }

	} catch (SQLException e) {
	    throw new InvalidDBTransferException();
	    //TODO Logging message
	}	
	// gibt das bef�llte Userobjekt zur�ck.
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
     * @throws InvalidDBTransferException if any error occurred during the
     * execution of the method
     */
    public static int getUserID(Transaction trans, String username)
	    throws InvalidDBTransferException {
	
	//Neues Integer id erstellen.
	int id =0;
	
	//SQL- Abfrage vorbereiten und Connection zur Datenbank erstellen.
	PreparedStatement pS = null;
	Connection con = (Connection) trans.conn;
	
	//Datenbankabfrage
	String sql = "SELECT id FROM users WHERE nickname=?";
	
	//m�gliche SQL-Injektion abfangen
	try {
	    pS = con.prepareStatement(sql);	    
	    pS.setString(1, username);
	    
	    //preparedStatement ausf�hren, gibt resultSet als Liste zur�ck (hier
	    //ein Eintrag in der Liste, da Benutzername einzigartig).
	    ResultSet res = pS.executeQuery();
	    
	   //N�chten Eintrag aufrufen, gibt true zur�ck, falls es weiteren 
	    //Eintrag gibt, ansonsten 0.
	    if(res.next()) {
		//id mit zugeh�rigem Wert aus der Datenbank f�llen.
		id = res.getInt("id");
		
    	    	//TODO Attribute belegen
	    }
	    else
	    {
		//TODO Fehler, kein Benutzer mit diesem Benutzernamen
		return 0;
	    }

	} catch (SQLException e) {
	    throw new InvalidDBTransferException();
	    //TODO Logging message
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
     * @throws InvalidDBTransferException if any error occurred during the
     * execution of the method
     */
    public static void updateUser(Transaction trans, User user)
    		throws InvalidDBTransferException {
    }

    /**
     * Deletes a user which is assigned to the passed ID.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
	 *            database
     * @param userID
     *            the ID of the user to be deleted
     * @throws InvalidDBTransferException if any error occurred during the
     * execution of the method
     */
    public static void deleteUser(Transaction trans, int userID)
    		throws InvalidDBTransferException {
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
     * @return the list of courses which the user is leader of, or null if
     * the user isn't leader of any course
     * @throws InvalidDBTransferException if any error occurred during the
     * execution of the method
     */
    public static List<Course> getCoursesLeadedBy(Transaction trans, int userID,
	    PaginationData pagination) throws InvalidDBTransferException {
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
     * Returns an ID assigned to the user which has been added last to the
     * database.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
	 *            database
     * @return the user's ID
     * @throws InvalidDBTransferException if any error occurred during the
     * execution of the method
     */
    public static int getNewUserID(Transaction trans)
    		throws InvalidDBTransferException {
	return 0;

    }
}
