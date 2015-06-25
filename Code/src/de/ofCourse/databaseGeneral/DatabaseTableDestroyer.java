package de.ofCourse.databaseGeneral;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.system.Connection;
import de.ofCourse.system.Transaction;

/**
 * 
 * @author Patrick Cretu
 *
 */
public class DatabaseTableDestroyer {
	
	private static final String DROP_FORM_OF_ADDRESS = "DROP TYPE form_of_address";
	
	private static final String DROP_ROLE = "DROP TYPE role";
	
	private static final String DROP_STATUS = "DROP TYPE status";
	
	private static final String DROP_PERIOD = "DROP TYPE period";
	
	private static final String DROP_ACTIVATION = "DROP TYPE activation";
	
	private static final String DROP_USERS = "DROP TABLE users";
	
	private static final String DROP_COURSES = "DROP TABLE courses";
	
	private static final String DROP_COURSE_UNITS = "DROP TABLE course_units";
	
	private static final String DROP_USER_ADDRESSES = "DROP TABLE user_addresses";
	
	private static final String DROP_COURSE_UNIT_ADDRESSES = "DROP TABLE course_unit_addresses";
	
	private static final String DROP_CYCLES = "DROP TABLE cycles";
	
	private static final String DROP_INFORM_USERS = "DROP TABLE inform_users";
	
	private static final String DROP_COURSE_INSTRUCTORS =
			"DROP TABLE course_instructors";
	
	private static final String DROP_COURSE_PARTICIPANTS =
			"DROP TABLE course_participants";
	
	private static final String DROP_COURSE_UNIT_PARTICIPANTS =
			"DROP TABLE course_unit_participants";
	
	private static final String DROP_SYSTEM_ATTRIBUTES =
			"DROP TABLE system_attributes";
	
	public static void dropTables() throws InvalidDBTransferException {
		List<String> dropStatements = new ArrayList<String>();
    	dropStatements.add(DROP_SYSTEM_ATTRIBUTES);
    	dropStatements.add(DROP_INFORM_USERS);
    	dropStatements.add(DROP_COURSE_UNIT_ADDRESSES);
    	dropStatements.add(DROP_USER_ADDRESSES);
    	dropStatements.add(DROP_COURSE_PARTICIPANTS);
    	dropStatements.add(DROP_COURSE_UNIT_PARTICIPANTS);
    	dropStatements.add(DROP_COURSE_UNITS);
    	dropStatements.add(DROP_COURSE_INSTRUCTORS);
    	dropStatements.add(DROP_CYCLES);
    	dropStatements.add(DROP_COURSES);
    	dropStatements.add(DROP_USERS);
    	dropStatements.add(DROP_ACTIVATION);
    	dropStatements.add(DROP_PERIOD);
    	dropStatements.add(DROP_STATUS);
    	dropStatements.add(DROP_ROLE);
    	dropStatements.add(DROP_FORM_OF_ADDRESS);
		
		Transaction trans = Connection.create();
    	trans.start();
    	Connection connection = (Connection) trans;
    	java.sql.Connection conn = connection.getConn();
    	
    	try {
			for (int i = 0; i < dropStatements.size(); i++) {
				Statement stmt = conn.createStatement();
				stmt.execute(dropStatements.get(i));
				
				if (i < dropStatements.size() - 1) {
					conn.commit();
				} else {
					trans.commit();
				}
			}
		} catch (SQLException e) {
			trans.rollback();
			throw new InvalidDBTransferException("SQL Exception occoured during dropTables()", e);
		}
	}
}
