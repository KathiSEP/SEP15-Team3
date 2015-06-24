/**
 * This package contains classes required at the launch of the system
 */
package de.ofCourse.databaseGeneral;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.system.Connection;
import de.ofCourse.system.LogHandler;
import de.ofCourse.system.Transaction;
import de.ofCourse.utilities.PasswordHash;

/**
 * Checks at the launch of the system if an administrator has been initially
 * added to the database. If not, a new administrator is created and added to
 * the database.
 * 
 * <p>
 * This class is only required in the class
 * <code>de.ofCourse.system.LaunchSystem</code>.
 * </p>
 * 
 * @author Patrick Cretu
 *
 */
public class SetupAdmin {
	
	private static final String CHECK_ADMIN =
			"SELECT COUNT(*) FROM \"users\" WHERE role = 'SYSTEM_ADMINISTRATOR'";
	
	private static final String INIT_ADMIN =
			"INSERT INTO \"users\"(nickname, email, pw_hash, pw_salt, credit_balance," +
			"email_verification, admin_verification, veri_string, role, status) " +
			"VALUES ('admin1', 'bazinga@gmail.com', %s, %s, 0, TRUE, " +
			"TRUE, 'veristring', 'SYSTEM_ADMINISTRATOR', 'REGISTERED')";
	
	private static final String CHECK_ATTRIBUTES = "SELECT COUNT(*) FROM \"system_attributes\"";
	
	private static final String INIT_ATTRIBUTES = "INSERT INTO \"system_attributes\"(row_lock, activation_type, " +
			"withdrawal_hours, overdraft_credit) " +
			"VALUES('X', 'EMAIL', 3, 0)";
	
    /**
     * Checks whether or not the table of users in the database contains an
     * administrator. If not, a new administrator is created and added to the
     * table containing users in the database.
     * 
     * @throws InvalidDBTransferException if any error occurred during the
     * execution of the method
     */
    public static void createInitialAdmin() throws InvalidDBTransferException {
    	String salt = PasswordHash.getSalt();
    	String pw = PasswordHash.hash("Password!123", salt);
    	
    	salt = "'" + salt + "'";
    	pw = "'" + pw + "'";
    	
    	executeInitialization(CHECK_ADMIN, String.format(INIT_ADMIN, pw, salt));
    }
    
    public static void setSystemAttributes() throws InvalidDBTransferException {
    	executeInitialization(CHECK_ATTRIBUTES, INIT_ATTRIBUTES);
    }
    
    private static void executeInitialization(String checkQuery, String initQuery) {
    	Transaction trans = new Connection();
    	trans.start();
    	Connection connection = (Connection) trans;
    	java.sql.Connection conn = connection.getConn();
    	
    	ResultSet rst = null;
    	Statement check = null;
    	Statement init = null;
    	
    	try {
    		check = conn.createStatement();
    		rst = check.executeQuery(checkQuery);
    		rst.next();
    		
    		if ((Long) rst.getObject(1) < 1) {
    			init = conn.createStatement();
    			init.execute(initQuery);
    		}
    		trans.commit();
		} catch (SQLException e) {
			LogHandler
		    .getInstance()
		    .error("SQL Exception occoured during executeInitialization(String checkQuery, String initQuery)");
			throw new InvalidDBTransferException();
		} finally {
			if (rst != null) {
				try {
					rst.close();
				} catch (SQLException e) {
					LogHandler
				    .getInstance()
				    .error("SQL Exception occoured during closing ResultSet in executeInitialization(String checkQuery, String initQuery)");
					throw new InvalidDBTransferException();
				}
			}
			
			if (check != null) {
				try {
					check.close();
				} catch (SQLException e) {
					LogHandler
				    .getInstance()
				    .error("SQL Exception occoured during closing PreparedStatement in executeInitialization(String checkQuery, String initQuery)");
					throw new InvalidDBTransferException();
				}
			}
			
			if (init != null) {
				try {
					init.close();
				} catch (SQLException e) {
					LogHandler
				    .getInstance()
				    .error("SQL Exception occoured during closing PreparedStatement in executeInitialization(String checkQuery, String initQuery)");
					throw new InvalidDBTransferException();
				}
			}
		}
    }
}
