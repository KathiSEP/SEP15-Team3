/**
 * This package contains classes required at the launch of the system
 */
package de.ofCourse.databaseGeneral;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
    			"'EMAIL', 'EMAIL_ADMIN', 'EMAIL_COURSE_LEADER'" +
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
    			"title TEXT," +
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
    			"course_id INTEGER REFERENCES \"courses\"(id) " +
    			"ON DELETE CASCADE NOT NULL," +
    			"cycle_id INTEGER REFERENCES \"cycles\"(id) " +
    			"ON DELETE SET NULL," +
    			"course_instructor_id INTEGER REFERENCES \"users\"(id) " +
    			"ON DELETE CASCADE NOT NULL," +
    			"max_participants INTEGER NOT NULL " +
    			"CHECK (max_participants > 0)," +
    			"title TEXT," +
    			"min_participants INTEGER CHECK (min_participants > 0)," +
    			"fee DECIMAL(6,2) NOT NULL CHECK (fee >= 0)," +
    			"start_time TIMESTAMP NOT NULL," +
    			"end_time TIMESTAMP NOT NULL," +
    			"description TEXT" +
    		");" +
    		"ALTER SEQUENCE course_units_id_seq RESTART WITH 10000";
    
    private static final String CREATE_USER_ADDRESSES =
    		"CREATE TABLE user_addresses (" +
    			"id SERIAL PRIMARY KEY," +
    			"user_id INTEGER REFERENCES \"users\"(id) " +
    			"ON DELETE CASCADE NOT NULL," +
    			"country VARCHAR(100)," +
    			"city VARCHAR(100)," +
    			"zip_code VARCHAR(10)," +
    			"street VARCHAR(100)," +
    			"house_nr INTEGER" +
    		")";
    
    private static final String CREATE_COURSE_UNIT_ADDRESSES =
    		"CREATE TABLE course_unit_addresses (" +
    			"id SERIAL PRIMARY KEY," +
    			"course_unit_id INTEGER REFERENCES \"course_units\"(id) " +
    			"ON DELETE CASCADE NOT NULL," +
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
    			"course_id INTEGER REFERENCES \"courses\"(id) " +
    			"ON DELETE CASCADE NOT NULL," +
    			"period PERIOD," +
    			"cycle_end INTEGER NOT NULL" +
    		")";

    private static final String CREATE_INFORM_USERS =
    		"CREATE TABLE inform_users (" +
    			"user_id INTEGER REFERENCES \"users\"(id) ON DELETE CASCADE," +
    			"course_id INTEGER REFERENCES \"courses\"(id) " +
    			"ON DELETE CASCADE," +
    			"PRIMARY KEY (user_id, course_id)" +
    		")";

    private static final String CREATE_COURSE_INSTRUCTORS =
    		"CREATE TABLE course_instructors (" +
    			"course_instructor_id INTEGER REFERENCES \"users\"(id) " +
    			"ON DELETE CASCADE," +
    			"course_id INTEGER REFERENCES \"courses\"(id) " +
    			"ON DELETE CASCADE," +
    			"PRIMARY KEY (course_instructor_id, course_id)" +
    		")";

    private static final String CREATE_COURSE_PARTICIPANTS =
    		"CREATE TABLE course_participants (" +
    			"participant_id INTEGER REFERENCES \"users\"(id) " +
    			"ON DELETE CASCADE," +
    			"course_id INTEGER REFERENCES \"courses\"(id) " +
    			"ON DELETE CASCADE," +
    			"PRIMARY KEY (participant_id, course_id)" +
    		")";

    private static final String CREATE_COURSE_UNIT_PARTICIPANTS =
    		"CREATE TABLE course_unit_participants (" +
    			"participant_id INTEGER REFERENCES \"users\"(id) " +
    			"ON DELETE CASCADE," +
    			"course_unit_id INTEGER REFERENCES \"course_units\"(id) " +
    			"ON DELETE CASCADE," +
    			"PRIMARY KEY (participant_id, course_unit_id)" +
    		")";

    private static final String CREATE_SYSTEM_ATTRIBUTES =
    		"CREATE TABLE system_attributes (" +
    			"row_lock CHAR(1) PRIMARY KEY CHECK (row_lock = 'X')," +
    			"activation_type ACTIVATION NOT NULL," +
    			"withdrawal_hours INTEGER NOT NULL," +
    			"overdraft_credit INTEGER NOT NULL" +
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
    	List<String> createStatements = new ArrayList<String>();
    	createStatements.add(CREATE_FORM_OF_ADDRESS);
    	createStatements.add(CREATE_ROLE);
    	createStatements.add(CREATE_STATUS);
    	createStatements.add(CREATE_PERIOD);
    	createStatements.add(CREATE_ACTIVATION);
    	createStatements.add(CREATE_USERS);
    	createStatements.add(CREATE_COURSES);
    	createStatements.add(CREATE_CYCLES);
    	createStatements.add(CREATE_COURSE_INSTRUCTORS);
    	createStatements.add(CREATE_COURSE_UNITS);
    	createStatements.add(CREATE_USER_ADDRESSES);
    	createStatements.add(CREATE_COURSE_UNIT_ADDRESSES);
    	createStatements.add(CREATE_INFORM_USERS);
    	createStatements.add(CREATE_COURSE_PARTICIPANTS);
    	createStatements.add(CREATE_COURSE_UNIT_PARTICIPANTS);
    	createStatements.add(CREATE_SYSTEM_ATTRIBUTES);
    	
    	String checkTables = "SELECT COUNT(*) FROM information_schema.tables " +
    			"WHERE table_schema = 'public'";
    	Transaction trans = Connection.create();
    	trans.start();
    	Connection connection = (Connection) trans;
    	java.sql.Connection conn = connection.getConn();
    	
    	try (Statement check = conn.createStatement()) {
    		try (ResultSet count = check.executeQuery(checkTables)) {
    			count.next();
    			Long numTables = (Long) count.getObject(1);
    			
    			if (numTables == 0) {
					for (int i = 0; i < createStatements.size(); i++) {
						Statement stmt = conn.createStatement();
						stmt.execute(createStatements.get(i));
						
						if (i < createStatements.size() - 1) {
							conn.commit();
						} else {
							trans.commit();
						}
					}
				} else {
					trans.rollback();
				}
    		}
		} catch (SQLException e) {
			trans.rollback();
			throw new InvalidDBTransferException(
					"SQL Exception occoured during buildUpDatabase()", e);
		}
    } 
    
}
