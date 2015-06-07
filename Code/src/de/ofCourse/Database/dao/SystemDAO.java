/**
 * This package represents the Data Access Objects of the ofCourse system.
 */
package de.ofCourse.Database.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Activation;
import de.ofCourse.model.Salutation;
import de.ofCourse.system.Connection;
import de.ofCourse.system.LogHandler;
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
 * @author Tobias Fuchs
 */
public class SystemDAO {

    /**
     * Returns the value of the overdraft credit stored in the database.
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
	return 0;

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
     */
    public static void setOverdraftCredit(Transaction trans, int credit)
	    throws InvalidDBTransferException {

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
     */
    public static Activation getActivationType(Transaction trans)
	    throws InvalidDBTransferException {

	Activation activation = null;

	// SQL- Abfrage vorbereiten und Connection zur Datenbank erstellen.
	PreparedStatement pS = null;
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	// Datenbankabfrage
	String sql = "SELECT activation_type FROM \"systemAttributes\"";

	// mögliche SQL-Injektion abfangen
	try {
	    pS = conn.prepareStatement(sql);

	    // preparedStatement ausführen, gibt resultSet als Liste zurück
	    // (hier ein Eintrag in der Liste, da Aktivierung einzigartig).
	    ResultSet res = pS.executeQuery();

	    // Nächten Eintrag aufrufen, gibt true zurück, falls es weiteren
	    // Eintrag gibt, ansonsten null.
	    if (res.next()) {

		String activationString = res.getString("activation_type");
		switch (activationString) {
		case "EMAIL":
		    activation = Activation.EMAIL;
		case "ADMIN":
		    activation = Activation.ADMIN;
		case "COMPLETE":
		    activation = Activation.COMPLETE;
		break;
		}

	    } else {
		return null;
	    }

	} catch (SQLException e) {
	    LogHandler.getInstance().error("SQL Exception occoured during executing getActivationType(Transaction trans)");
	    throw new InvalidDBTransferException();
	   
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
     */
    public static void setActivationType(Transaction trans, String type)
	    throws InvalidDBTransferException {

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
	return 0;

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
     */
    public static void setSignOffLimit(Transaction trans, int limit)
	    throws InvalidDBTransferException {

    }

}
