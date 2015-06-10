/**
 * This package contains classes required at the launch of the system
 */
package de.ofCourse.Database.DatabaseGeneral;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.system.Connection;
import de.ofCourse.system.Transaction;

/**
 * Checks at the launch of the system if the database tables have been
 * initialized. If not, the tables are created and initialized.
 * 
 * <p>
 * This class is only required in the class
 * <code>de.ofCourse.system.LaunchSystem</code>.
 * </p>
 * 
 * @author Patrick Cretu
 *
 */
public class DatabaseTableCreator {
	
	private static final String CREATE_FORM_OF_ADDRESS =
			"CREATE TYPE form_of_address AS ENUM (" +
					"'MR', 'MS'" +
    		")";
    
    private static final String CREATE_ROLE =
    		"CREATE TYPE role AS ENUM (" +
    			"'REGISTERED_USER', 'COURSE_LEADER', 'SYSTEM_ADMINISTRATOR'" +
    		")";
    
    private static final String CREATE_STATUS =
    		"CREATE TYPE status AS ENUM (" +
    			"'ANONYMOUS', 'NOT_ACTIVATED', 'REGISTERED', 'INACTIVE'" +
    		")";
    
    private static final String CREATE_PERIOD =
    		"CREATE TYPE period AS ENUM (" +
    			"'MONTHS', 'WEEKS', 'DAYS', 'HOURS'" +
    		")";
    
    private static final String CREATE_ACTIVATION =
    		"CREATE TYPE activation AS ENUM (" +
    			"'EMAIL', 'ADMIN', 'COMPLETE'" +
    		")";
    
    private static final String CREATE_USERS =
    		"CREATE TABLE users (" +
				"id SERIAL PRIMARY KEY," +
				"first_name VARCHAR(100)," +
				"name VARCHAR(100)," +
				"nickname VARCHAR(100) NOT NULL UNIQUE," +
				"email VARCHAR(319) NOT NULL UNIQUE," +
				"pw_hash VARCHAR(256) NOT NULL," +
				"pw_salt VARCHAR(256) NOT NULL," +
				"date_of_birth DATE," +
				"form_of_address FORM_OF_ADDRESS," +
				"credit_balance DECIMAL(10, 2) NOT NULL," +
				"email_verification BOOLEAN NOT NULL," +
				"admin_verification BOOLEAN NOT NULL," +
				"veri_string VARCHAR(130) UNIQUE," +
				"profile_image BYTEA," +
				"role ROLE NOT NULL," +
				"status STATUS NOT NULL" +
			");" +
			"ALTER SEQUENCE users_id_seq RESTART WITH 10000";
    
    private static final String CREATE_COURSES =
    		"CREATE TABLE courses (" +
    			"id SERIAL PRIMARY KEY," +
    			"titel TEXT," +
    			"max_participants INTEGER CHECK (max_participants > 0)," +
    			"start_date DATE NOT NULL," +
    			"end_date DATE NOT NULL," +
    			"description TEXT," +
    			"image BYTEA" +
    		");" +
    		"ALTER SEQUENCE courses_id_seq RESTART WITH 10000";
    
    private static final String CREATE_COURSE_UNITS =
    		"CREATE TABLE course_units (" +
    			"id SERIAL PRIMARY KEY," +
    			"course_id INTEGER REFERENCES \"courses\"(id) ON DELETE CASCADE NOT NULL," +
    			"cycle_id INTEGER REFERENCES \"cycles\"(id) ON DELETE SET NULL," +
    			"course_instructor_id INTEGER REFERENCES \"course_instructors\"(id) ON DELETE CASCADE NOT NULL," +
    			"max_participants INTEGER NOT NULL " +
    			"CHECK (max_participants > 0)," +
    			"titel TEXT," +
    			"min_participants INTEGER CHECK (min_participants > 0)," +
    			"fee DECIMAL(6,2) CHECK (fee >= 0)," +
    			"start_time TIMESTAMP NOT NULL," +
    			"end_time TIMESTAMP NOT NULL," +
    			"description TEXT" +
    		");" +
    		"ALTER SEQUENCE course_units_id_seq RESTART WITH 10000";
    
    private static final String CREATE_USER_ADDRESSES =
    		"CREATE TABLE user_addresses (" +
    			"id SERIAL PRIMARY KEY," +
    			"user_id INTEGER REFERENCES \"users\"(id) ON DELETE CASCADE NOT NULL," +
    			"country VARCHAR(100) NOT NULL," +
    			"city VARCHAR(100) NOT NULL," +
    			"zip_code VARCHAR(10) NOT NULL," +
    			"street VARCHAR(100)," +
    			"house_nr INTEGER" +
    		")";
    
    private static final String CREATE_COURSE_UNIT_ADDRESSES =
    		"CREATE TABLE course_unit_addresses (" +
    			"id SERIAL PRIMARY KEY," +
    			"course_unit_id INTEGER REFERENCES \"course_units\"(id) ON DELETE CASCADE NOT NULL," +
    			"country VARCHAR(100) NOT NULL," +
    			"city VARCHAR(100) NOT NULL," +
    			"zip_code VARCHAR(10) NOT NULL," +
    			"street VARCHAR(100)," +
    			"house_nr INTEGER," +
    			"location TEXT" +
    		")";

    private static final String CREATE_CYCLES =
    		"CREATE TABLE cycles (" +
    			"id SERIAL PRIMARY KEY," +
    			"course_id INTEGER REFERENCES \"courses\"(id) ON DELETE CASCADE NOT NULL," +
    			"period PERIOD," +
    			"cycle_end INTEGER NOT NULL" +
    		")";

    private static final String CREATE_INFORM_USERS =
    		"CREATE TABLE inform_users (" +
    			"user_id INTEGER REFERENCES \"users\"(id) ON DELETE CASCADE," +
    			"course_id INTEGER REFERENCES \"courses\"(id) ON DELETE CASCADE," +
    			"PRIMARY KEY (user_id, course_id)" +
    		")";

    private static final String CREATE_COURSE_INSTRUCTORS =
    		"CREATE TABLE course_instructors (" +
    			"course_instructor_id INTEGER REFERENCES \"users\"(id) ON DELETE CASCADE," +
    			"course_id INTEGER REFERENCES \"courses\"(id) ON DELETE CASCADE," +
    			"PRIMARY KEY (course_instructor_id, course_id)" +
    		")";

    private static final String CREATE_COURSE_PARTICIPANTS =
    		"CREATE TABLE course_participants (" +
    			"participant_id INTEGER REFERENCES \"users\"(id) ON DELETE CASCADE," +
    			"course_id INTEGER REFERENCES \"courses\"(id) ON DELETE CASCADE," +
    			"PRIMARY KEY (participant_id, course_id)" +
    		")";

    private static final String CREATE_COURSE_UNIT_PARTICIPANTS =
    		"CREATE TABLE course_unit_participants (" +
    			"participant_id INTEGER REFERENCES \"users\"(id) ON DELETE CASCADE," +
    			"course_unit_id INTEGER REFERENCES \"course_units\"(id) ON DELETE CASCADE," +
    			"PRIMARY KEY (participant_id, course_unit_id)" +
    		")";

    private static final String CREATE_SYSTEM_ATTRIBUTES =
    		"CREATE TABLE system_attributes (" +
    			"row_lock CHAR(1) PRIMARY KEY DEFAULT 'X' CHECK (row_lock = 'X')," +
    			"activation_type ACTIVATION NOT NULL," +
    			"withdrawal_hours INTEGER NOT NULL," +
    			"application_hours INTEGER NOT NULL," +
    			"overdraft INTEGER NOT NULL" +
    		")";
    
    private static final String CREATE_CUSTOMIZATION_DATA =
    		"CREATE TABLE customization_data (" +
    			"row_lock CHAR(1) PRIMARY KEY DEFAULT 'X' CHECK (row_lock = 'X')," +
    			"css VARCHAR(30) NOT NULL," +
    			"title VARCHAR(30) NOT NULL" +
    		")";
	
    /**
     * Checks whether or not the required tables in the database have been
     * initialized. If not, the necessary SQL statements are executed in order
     * to create and initialize the tables in the database.
     * 
     * @throws InvalidDBTransferException if any error occurred during the
     * execution of the method
     */
    public static void buildUpDatabase() throws InvalidDBTransferException{
    	String checkTables = "SELECT COUNT(*) FROM information_schema.tables " +
    			"WHERE table_schema = 'public'";
    	Transaction trans = new Connection();
    	trans.start();
    	Connection connection = (Connection) trans;
    	java.sql.Connection conn = connection.getConn();
    	
    	Statement formOfAddress = null;
    	Statement role = null;
    	Statement status = null;
    	Statement period = null;
    	Statement activation = null;
    	Statement users = null;
    	Statement courses = null;
    	Statement courseUnits = null;
    	Statement userAddresses = null;
    	Statement courseUnitAddresses = null;
    	Statement cycles = null;
    	Statement informUsers = null;
    	Statement courseInstructors = null;
    	Statement courseParticipants = null;
    	Statement courseUnitParticipants = null;
    	Statement systemAttributes = null;
    	Statement customizationData = null;
    	Statement check = null;
    	ResultSet count = null;
    	try {
			check = conn.createStatement();
			count = check.executeQuery(checkTables);
			count.next();
			Long numTables = (Long) count.getObject(1);
			
			if (numTables == 0) {
				formOfAddress = conn.createStatement();
				formOfAddress.execute(CREATE_FORM_OF_ADDRESS);
				conn.commit();
				
				role = conn.createStatement();
				role.execute(CREATE_ROLE);
				conn.commit();
				
				status = conn.createStatement();
				status.execute(CREATE_STATUS);
				conn.commit();
				
				period = conn.createStatement();
				period.execute(CREATE_PERIOD);
				conn.commit();
				
				activation = conn.createStatement();
				activation.execute(CREATE_ACTIVATION);
				conn.commit();
				
				users = conn.createStatement();
				users.execute(CREATE_USERS);
				conn.commit();
				
				courses = conn.createStatement();
				courses.execute(CREATE_COURSES);
				conn.commit();
				
				cycles = conn.createStatement();
				cycles.execute(CREATE_CYCLES);
				conn.commit();
				
				courseUnits = conn.createStatement();
				courseUnits.execute(CREATE_COURSE_UNITS);
				conn.commit();

				userAddresses = conn.createStatement();
				userAddresses.execute(CREATE_USER_ADDRESSES);
				conn.commit();
				
				courseUnitAddresses = conn.createStatement();
				courseUnitAddresses.execute(CREATE_COURSE_UNIT_ADDRESSES);
				conn.commit();
				
				informUsers = conn.createStatement();
				informUsers.execute(CREATE_INFORM_USERS);
				conn.commit();
				
				courseInstructors = conn.createStatement();
				courseInstructors.execute(CREATE_COURSE_INSTRUCTORS);
				conn.commit();
				
				courseParticipants = conn.createStatement();
				courseParticipants.execute(CREATE_COURSE_PARTICIPANTS);
				conn.commit();
				
				courseUnitParticipants = conn.createStatement();
				courseUnitParticipants.
					execute(CREATE_COURSE_UNIT_PARTICIPANTS);
				conn.commit();
				
				systemAttributes = conn.createStatement();
				systemAttributes.execute(CREATE_SYSTEM_ATTRIBUTES);
				conn.commit();

				customizationData = conn.createStatement();
				customizationData.execute(CREATE_CUSTOMIZATION_DATA);
				trans.commit();
				
				System.out.println("Erstellen der Datenbank fertig");
			} else {
				trans.rollback();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (count != null) {
				try {
					count.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			
			if (check != null) {
				try {
					check.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}	
			}
			
			if (formOfAddress != null) {
				try {
					formOfAddress.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if (role != null) {
				try {
					role.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if (status != null) {
				try {
					status.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if (period != null) {
				try {
					period.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if (activation != null) {
				try {
					activation.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if (users != null) {
				try {
					users.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if (courses != null) {
				try {
					courses.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if (courseUnits != null) {
				try {
					courseUnits.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if (userAddresses != null) {
				try {
					userAddresses.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if (courseUnitAddresses != null) {
				try {
					courseUnitAddresses.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if (cycles != null) {
				try {
					cycles.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if (informUsers != null) {
				try {
					informUsers.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if (courseInstructors != null) {
				try {
					courseInstructors.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if (courseParticipants != null) {
				try {
					courseParticipants.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if (courseUnitParticipants != null) {
				try {
					courseUnitParticipants.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if (systemAttributes != null) {
				try {
					systemAttributes.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if (customizationData != null) {
				try {
					customizationData.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
    } 
    
}
