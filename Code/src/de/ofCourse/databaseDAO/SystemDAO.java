/**
 * This package represents the Data Access Objects of the ofCourse system.
 */
package de.ofCourse.databaseDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Activation;
import de.ofCourse.system.Connection;
import de.ofCourse.system.Transaction;

/**
 * Fetches data concerning the system settings like e.g. the granted overdraft
 * credit from the database and updates this data if it is edited by the
 * administrator.
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
 */
public class SystemDAO {
    
    private final static String SET_SIGN_OFF_LIMIT = 
	    "UPDATE \"system_attributes\" SET withdrawal_hours=?";
    
    private final static String SET_OVERDRAFT_CREDIT = 
	    "UPDATE \"system_attributes\" " 
            + "SET overdraft_credit=?";
    
    private final static String SET_ACTIVATION_TYPE =
	    "UPDATE \"system_attributes\" "
	    + "SET activation_type=?::activation";
    
    private final static String GET_OVERDRAFT_CREDIT = 
	    "SELECT overdraft_credit FROM \"system_attributes\"";
    
    private final static String GET_SIGN_OFF_LIMIT =
	    "SELECT withdrawal_hours FROM \"system_attributes\"";

    /**
     * Returns the value of the overdraft credit stored in the database.
     * 
     * @author Sebastian
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @return the overdraft credit
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     */
    public static int getOverdraftCredit(Transaction trans)
	    throws InvalidDBTransferException {
        int overdraftCredit = 0;
	
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();
	
	try(PreparedStatement stmt = conn.prepareStatement(GET_OVERDRAFT_CREDIT)){
	    
	    try(ResultSet res = stmt.executeQuery()){
		res.next();
		overdraftCredit = res.getInt("overdraft_credit");
	    }

	}catch(SQLException e){
	    throw new InvalidDBTransferException(
		    "Error occured during fetching the" 
	            + " overdraft credit from database", e);
	}
	return overdraftCredit;
    }

    /**
     * Updates the value of the overdraft credit in the database.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param credit
     *            the new value of the granted overdraft credit
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * @author Fuchs Tobias
     */
    public static void setOverdraftCredit(Transaction trans, float credit)
	    throws InvalidDBTransferException {
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	try (PreparedStatement stmt = conn.prepareStatement(SET_OVERDRAFT_CREDIT)) {

	    // Update the account activation in the database
	    stmt.setFloat(1, credit);
	    stmt.executeUpdate();

	} catch (SQLException e) {
	    throw new InvalidDBTransferException(
		    "Error occured during setting overdraft credit.", e);
	}
    }

    /**
     * Returns the current type of activation from the database.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @return type of activation
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * 
     * @author Katharina Hölzl
     */
    public static Activation getActivationType(Transaction trans)
	    throws InvalidDBTransferException {

	Activation activation = null;

	// SQL- Abfrage vorbereiten und Connection zur Datenbank erstellen.
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	// Datenbankabfrage
	String sql = "SELECT activation_type FROM \"system_attributes\"";

	// mögliche SQL-Injektion abfangen
	try (PreparedStatement pS = conn.prepareStatement(sql)) {

	    // preparedStatement ausführen, gibt resultSet als Liste zurück
	    // (hier ein Eintrag in der Liste, da Aktivierung einzigartig).
	    try(ResultSet res = pS.executeQuery()){

        	    // Nächten Eintrag aufrufen, gibt true zurück, falls es 
        	    // weiteren Eintrag gibt, ansonsten null. 
        	    if (res.next()) {
        	        activation = Activation.fromString(
        	                             res.getString("activation_type"));
        	    } else {
        		return null;
        	    }
	    }catch (SQLException e) {
                throw new SQLException();
	    }
	} catch (SQLException e) {
	    throw new InvalidDBTransferException(
                                "Error occured during getActivationType", e);
	}
	// gibt die Aktivierungsmethode zurück.
	return activation;
    }

    /**
     * Updates the current type of activation in the database.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param type
     *            type of activation
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * @author Fuchs Tobias
     */
    public static void setActivationType(Transaction trans, Activation type)
	    throws InvalidDBTransferException {
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	try (PreparedStatement stmt = conn.prepareStatement(SET_ACTIVATION_TYPE)) {

	    // Update the overdraft credit in the database
	    stmt.setString(1, type.toString());
	    stmt.executeUpdate();
	} catch (SQLException e) {
	    throw new InvalidDBTransferException(
		    "Error occured during setting activation type.", e);
	}
    }

    /**
     * Returns the limit in hours until that a user can signOff from a course.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @return the sign off limit
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     */
    public static int getSignOffLimit(Transaction trans)
	    throws InvalidDBTransferException {
	//Default value
	int signOffLimit = 3;
	
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();
	
	try(PreparedStatement stmt = conn.prepareStatement(GET_SIGN_OFF_LIMIT)){
	    
	    try(ResultSet res = stmt.executeQuery()){
		res.next();
		signOffLimit = res.getInt("withdrawal_hours");
	    }

	}catch(SQLException e){
	    throw new InvalidDBTransferException(
		    "Error occured during fetching the" 
	            + " sign off limit from database", e);
	}
	return signOffLimit;

    }

    /**
     * Sets the sign off limit in hours until that a user can signOff from a
     * course.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param limit
     *            the new sign off limit
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * @author Fuchs Tobias            
     */
    public static void setSignOffLimit(Transaction trans, int limit)
	    throws InvalidDBTransferException {
	
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	try (PreparedStatement stmt = conn.prepareStatement(SET_SIGN_OFF_LIMIT)) {

	    // Update the sign off limit in the database
	    stmt.setInt(1, limit);
	    stmt.executeUpdate();
	} catch (SQLException e) {
	    throw new InvalidDBTransferException(
		    "Error occured during setting the sign off limit.", e);
	}
    }
}
