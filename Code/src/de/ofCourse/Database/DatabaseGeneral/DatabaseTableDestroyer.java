package de.ofCourse.Database.DatabaseGeneral;

import java.sql.SQLException;
import java.sql.Statement;

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
	private static final String DROP_CUSTOMIZATION_DATA =
			"DROP TABLE customization_data";
	
	public static void dropTables() throws InvalidDBTransferException {
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
    	
    	try {
			
			userAddresses = conn.createStatement();
			userAddresses.execute(DROP_USER_ADDRESSES);
			conn.commit();
			
			courseUnitAddresses = conn.createStatement();
			courseUnitAddresses.execute(DROP_COURSE_UNIT_ADDRESSES);
			conn.commit();
			
			informUsers = conn.createStatement();
			informUsers.execute(DROP_INFORM_USERS);
			conn.commit();
			
			courseInstructors = conn.createStatement();
			courseInstructors.execute(DROP_COURSE_INSTRUCTORS);
			conn.commit();
			
			courseParticipants = conn.createStatement();
			courseParticipants.execute(DROP_COURSE_PARTICIPANTS);
			conn.commit();
			
			courseUnitParticipants = conn.createStatement();
			courseUnitParticipants.execute(DROP_COURSE_UNIT_PARTICIPANTS);
			conn.commit();
			
			systemAttributes = conn.createStatement();
			systemAttributes.execute(DROP_SYSTEM_ATTRIBUTES);
			conn.commit();
			
			customizationData = conn.createStatement();
			customizationData.execute(DROP_CUSTOMIZATION_DATA);
			conn.commit();
			
			courseUnits = conn.createStatement();
			courseUnits.execute(DROP_COURSE_UNITS);
			conn.commit();
			
			cycles = conn.createStatement();
			cycles.execute(DROP_CYCLES);
			conn.commit();
			
			users = conn.createStatement();
			users.execute(DROP_USERS);
			conn.commit();
			
			courses = conn.createStatement();
			courses.execute(DROP_COURSES);
			conn.commit();
			
			formOfAddress = conn.createStatement();
    		formOfAddress.execute(DROP_FORM_OF_ADDRESS);
			conn.commit();
			
			role = conn.createStatement();
			role.execute(DROP_ROLE);
			conn.commit();
			
			status = conn.createStatement();
			status.execute(DROP_STATUS);
			conn.commit();
			
			period = conn.createStatement();
			period.execute(DROP_PERIOD);
			conn.commit();
			
			activation = conn.createStatement();
			activation.execute(DROP_ACTIVATION);
			trans.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
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
		}
    	
	}
	
}
