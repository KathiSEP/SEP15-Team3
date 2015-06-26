/**
 * This package represents the Data Access Objects of the ofCourse system.
 */
package de.ofCourse.databaseDAO;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Address;
import de.ofCourse.model.CourseUnit;
import de.ofCourse.model.Cycle;
import de.ofCourse.model.PaginationData;
import de.ofCourse.model.Period;
import de.ofCourse.model.User;
import de.ofCourse.system.Connection;
import de.ofCourse.system.LogHandler;
import de.ofCourse.system.Transaction;

/**
 * Provides methods for transactions with the course units stored in the
 * database such as creating, retrieving, updating or deleting course units.
 * Also adds or removes users to a course unit .
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
public class CourseUnitDAO {
	
	/**
	 * @author Patrick Cretu
	 */
	private static final String GET_WEEKLY_UNITS = "SELECT " +
			"\"course_units\".id, \"course_units\".course_id, " +
			"\"course_units\".title, \"course_units\".fee, " +
			"\"course_units\".start_time, \"course_units\".end_time " +
			"FROM \"course_units\", \"course_unit_participants\", " +
			"\"users\" " +
			"WHERE \"course_units\".id = " +
			"\"course_unit_participants\".course_unit_id " + 
			"AND \"course_unit_participants\".participant_id = \"users\".id " + 
			"AND \"users\".id = ? " +
			"AND \"course_units\".start_time::date BETWEEN ? " +
			"AND ?::date + integer '6' " +
			"ORDER BY \"course_units\".start_time";
	
	/**
	 * @author Patrick Cretu
	 */
	private static final String GET_UNITS_OF = "SELECT \"course_units\".id, " +
			"\"course_units\".course_id, \"course_units\".title, " +
			"\"course_units\".fee, " +
			"\"course_units\".start_time, \"course_units\".end_time " +
			"FROM \"course_units\", \"users\", \"course_unit_participants\" " +
			"WHERE \"users\".id = " +
			"\"course_unit_participants\".participant_id " +
			"AND \"course_unit_participants\".course_unit_id = " +
			"\"course_units\".id " +
			"AND \"users\".id = ?";

	/**
	 * @author Tobias Fuchs
	 */
	private final static String GET_PARTICIPANTS_OF = 
		"SELECT id, name, first_name, nickname, credit_balance, email FROM"
		+ " users WHERE users.id IN"
		+ " (SELECT participant_id FROM course_unit_participants"
		+ " WHERE course_unit_id = ?) ORDER BY %s %s"
		+ " LIMIT ? OFFSET ?";
	
	/**
	 * @author Tobias Fuchs
	 */
	private final static String GET_UNIT_IDS_OF_CYCLE = 
		"SELECT id FROM \"course_units\" WHERE"
		+ " cycle_id=? AND course_units.start_time >= CURRENT_DATE";
	
	/**
	 * @author Tobias Fuchs
	 */
	private final static String COUNT_NUMBER_OF_PARTICIPANTS = 
		"SELECT COUNT(*) FROM \"course_unit_participants\" "
		+ "WHERE course_unit_id = ?";
	
	/**
	 * @author Tobias Fuchs
	 */
	private final static String GET_PRICE_OF_UNIT = 
		"SELECT fee FROM \"course_units\" WHERE id=?";
	
	/**
	 * @author Tobias Fuchs
	 */
	private final static String DELETE_UNIT = "DELETE FROM \"course_units\" "
		+ "WHERE course_units.id=?";
	
	/**
	 * @author Tobias Fuchs
	 */
	private final static String DELETE_UNIT_ADDRESS = 
		"DELETE FROM \"course_unit_addresses\" "
		+ "WHERE course_unit_id=?";
	
	
	/**
	 * @author Tobias Fuchs
	 */
	private final static String UPDATE_UNIT = 
		"UPDATE \"course_units\" SET course_id=?,"
		+ " max_participants=?, title=?::TEXT,"
		+ " min_participants=?, fee=?, start_time=?,"
		+ " end_time=?, description=?::TEXT, course_instructor_id=?"
		+ " WHERE id=?";
	
	/**
	 * @author Tobias Fuchs
	 */
	private final static String UPDATE_UNIT_ADDRESS =
		"UPDATE \"course_unit_addresses\" SET "
		+ "country=?, city=?, zip_code=?,"
		+ " street=?, house_nr=?, location=?::TEXT WHERE course_unit_id=?";
	
	/**
	 * @author Tobias Fuchs
	 */
	private final static String CREATE_UNIT_ADDRESS = 
		"INSERT INTO \"course_unit_addresses\""
		+ " (course_unit_id, country, city,"
		+ " zip_code, street, house_nr, location)"
		+ " VALUES (?, ?, ?, ?, ?, ?, ?::TEXT)";
	
	/**
	 * @author Tobias Fuchs
	 */
	private final static String CREATE_UNIT = 
		"INSERT INTO \"course_units\""
		+ " (course_id, max_participants, title,"
		+ " min_participants, fee, start_time, end_time, description, " 
		+ "course_instructor_id, cycle_id)"
		+ " VALUES (?, ?, ?::TEXT, ?, ?, ?, ?, ?::TEXT, ?, ?) RETURNING id";
	
	/**
	 * @author Tobias Fuchs
	 */
	private final static String CREATE_UNIT_IRREG =
		"INSERT INTO \"course_units\""
		+ " (course_id, max_participants, title,"
		+ " min_participants, fee, start_time, end_time," 
		+ " description, course_instructor_id)"
		+ " VALUES (?, ?, ?::TEXT, ?, ?, ?, ?, ?::TEXT, ?) RETURNING id";
	
	
    /**
     * Adds a course unit to the list of course units in the database. A course
     * unit contains the course's ID which it is assigned to.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param courseUnit
     *            the course unit to be added
     * @param courseID
     *            the course's ID which the course unit is assigned to
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * @author Tobias Fuchs
     */
    public static void createCourseUnit(Transaction trans,
	    CourseUnit courseUnit, int courseID, boolean regular)
	    throws InvalidDBTransferException {
	String query;
	
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();
	
	//Select the needed SQL - command
	if(regular){
	    query = CREATE_UNIT;
	}else{
	    query = CREATE_UNIT_IRREG;
	}
	
	try (PreparedStatement stmt = conn.prepareStatement(query)) {
	    stmt.setInt(1, courseID);
	    stmt.setInt(2, courseUnit.getMaxUsers());
	    stmt.setString(3, courseUnit.getTitle());
	    stmt.setInt(4, courseUnit.getMinUsers());
	    stmt.setFloat(5, courseUnit.getPrice());
	    stmt.setTimestamp(6, 
		    new java.sql.Timestamp(courseUnit.getStartime().getTime()));
	    stmt.setTimestamp(7, 
		    new java.sql.Timestamp(courseUnit.getEndtime().getTime()));
	    stmt.setString(8, courseUnit.getDescription());
	    
	    stmt.setInt(9, courseUnit.getCourseAdmin().getUserID());
	    if(regular){
		stmt.setInt(10, courseUnit.getCycle().getCycleID());
	    }
	    
	    try(ResultSet res = stmt.executeQuery()){
		res.next();
		courseUnit.setCourseUnitID(res.getInt("id"));
	    
		// Create the corresponding course unit address
		createCourseUnitAddress(trans, courseUnit);
	    }
	} catch (SQLException e) {
	    throw new InvalidDBTransferException("Error occured during " 
		    + "creating a new course unit", e);
	}
    }

    /**
     * Creates the course unit address for a given course unit in the database.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param unit
     *            the corresponding course unit
     * @throws SQLException
     *             if any error occurred during the execution of the method
     * @author Tobias Fuchs
     */
    private static void createCourseUnitAddress(Transaction trans,
	    CourseUnit unit) throws SQLException {

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	try(PreparedStatement stmt = conn.prepareStatement(CREATE_UNIT_ADDRESS)) {
	    stmt.setInt(1, unit.getCourseUnitID());
	    stmt.setString(2, unit.getAddress().getCountry());
	    stmt.setString(3, unit.getAddress().getCity());
	    stmt.setString(4, unit.getAddress().getZipCode().toString());
	    stmt.setString(5, unit.getAddress().getStreet());
	    stmt.setInt(6, unit.getAddress().getHouseNumber());
	    stmt.setString(7, unit.getAddress().getLocation());
	    
	    stmt.executeUpdate();
	} catch (SQLException e) {
	    throw new SQLException("Error occured during creating" 
		    + " a new course unit address.", e);
	}
    }

    /**
     * Checks whether a User wants CourseNew or Not for this CourseUnit
     * 
     * @param trans
     * @param userId
     * @return true or false
     * @author Sebastian Schwarz
     */
    public static boolean userWantsToBeInformed(Transaction trans, int userID,
	    int courseID) {

		String query = "SELECT * FROM \"inform_users\" WHERE user_id=? AND course_id=?";
	
		Connection connection = (Connection) trans;
		java.sql.Connection conn = connection.getConn();
	
		try (PreparedStatement stmt = conn.prepareStatement(query)){
		    
		    stmt.setInt(1, userID);
		    stmt.setInt(2, courseID);
		    return stmt.execute();
	
		} catch (SQLException e) {
		    throw new InvalidDBTransferException("Error occoured during checking whether"
			    + " a user wants to be informed in case"
			    + " of changes of the course unit.", e);
		}	
	}

    /**
     * Returns a course unit assigned to the specified ID.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param courseUnitID
     *            the course unit's ID
     * @return the course unit assigned to the course ID, or null if no such
     *         course unit was found
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * @author Sebastian Schwarz
     */
    public static CourseUnit getCourseUnit(Transaction trans, int courseUnitID)
	    throws InvalidDBTransferException {
		
    	CourseUnit requestedCourseUnit = new CourseUnit();
		Address courseUnitAddress = new Address();
		Cycle courseCycle = new Cycle();
		User admin = new User();
	
		int adminID;
	
		Connection connection = (Connection) trans;
		java.sql.Connection conn = connection.getConn();
	
		String courseUnitRequest = "SELECT * FROM \"course_units\" WHERE id=?";
	
		try (PreparedStatement pS = conn.prepareStatement(courseUnitRequest)){
		    
		    pS.setInt(1, courseUnitID);
		    try(ResultSet resultSet = pS.executeQuery()){
		    	
			    if (resultSet.next()) {
		            
					requestedCourseUnit = setCourseUnitData(courseUnitID, requestedCourseUnit,
							resultSet);
					
					adminID = resultSet.getInt("course_instructor_id");

					// This is needed because the Database can return a Object null
					Integer cycleIDexists = (Integer) resultSet
						.getObject("cycle_id");
					
					if (cycleIDexists != null) {
					    int cycleID = resultSet.getInt("cycle_id");
			
					    String cycleRequest = "SELECT * FROM \"cycles\" WHERE id=?";
					    
					    try(PreparedStatement pSCycle = conn
							    .prepareStatement(cycleRequest);){
					    	
					    	pSCycle.setInt(1, cycleID);
					    	
					    	try(ResultSet resultSetCycle = pSCycle.executeQuery();){
					    		if (resultSetCycle.next()) {
									courseCycle.setCourseID(resultSetCycle
										.getInt("course_id"));
									courseCycle.setCycleID(cycleID);
									courseCycle.setNumberOfUnits(resultSetCycle
										.getInt("cycle_end"));
								    courseCycle.setTurnus(Period.fromString(resultSetCycle.getString("period")));	
					    		}
					    	}				  
					    }

				    requestedCourseUnit.setCycle(courseCycle);
				} 
		
				String addressRequest = "SELECT * FROM \"course_unit_addresses\" WHERE course_unit_id=?";

				try(PreparedStatement pSAddress = conn
						.prepareStatement(addressRequest);){
					pSAddress.setInt(1, courseUnitID);
					
					try(ResultSet resultSetAddress = pSAddress.executeQuery()){
						if (resultSetAddress.next()) {
						    courseUnitAddress.setCity(resultSetAddress
							    .getString("city"));
						    courseUnitAddress.setCountry(resultSetAddress
							    .getString("country"));
						    courseUnitAddress.setHouseNumber(resultSetAddress
							    .getInt("house_nr"));
						    courseUnitAddress.setId(resultSetAddress.getInt("id"));
						    courseUnitAddress.setStreet(resultSetAddress
							    .getString("street"));
						    courseUnitAddress.setZipCode(Integer
							    .parseInt(resultSetAddress.getString("zip_code")));
						    courseUnitAddress.setLocation(resultSetAddress
							    .getString("location"));
				
						    requestedCourseUnit.setAddress(courseUnitAddress);
						}
					}
				}
				
				admin = UserDAO.getUser(trans, adminID);
				requestedCourseUnit.setCourseAdmin(admin);
			    }
			    return requestedCourseUnit;
		    }
		    
		} catch (SQLException e) {
		    LogHandler.getInstance()
			    .error("Error occured during getCourseUnit");
		    throw new InvalidDBTransferException();
		}

    }

	/**
	 * Sets the Attributes of CourseUnit from the Result sets
	 * 
	 * @author Schwarz Sebastian
	 * @param courseUnitID
	 * @param requestedCourseUnit
	 * @param resultSet
	 * @return initalized CourseUnit
	 * @throws SQLException
	 */
	@SuppressWarnings("deprecation")
	private static CourseUnit setCourseUnitData(int courseUnitID,
			CourseUnit requestedCourseUnit, ResultSet resultSet)
			throws SQLException {
		Timestamp stamp;
		Date date;
		
		// Setting attributs of courseUnit
		requestedCourseUnit.setCourseID(resultSet.getInt("course_ID"));
		requestedCourseUnit.setCourseUnitID(courseUnitID);
		requestedCourseUnit.setDescription(resultSet
			.getString("description"));
		
		stamp = resultSet.getTimestamp("end_time");
		date = new Date(stamp.getYear(), stamp.getMonth(),
			stamp.getDate(), stamp.getHours(), stamp.getMinutes());
		requestedCourseUnit.setEndtime(date);
		stamp = resultSet.getTimestamp("start_time");
		date = new Date(stamp.getYear(), stamp.getMonth(),
			stamp.getDate(), stamp.getHours(), stamp.getMinutes());
		
		requestedCourseUnit.setStartime(date);
		requestedCourseUnit.setMaxUsers(resultSet
			.getInt("max_participants"));
		requestedCourseUnit.setMinUsers(resultSet
			.getInt("min_participants"));
		requestedCourseUnit.setPrice(resultSet.getInt("fee"));
		requestedCourseUnit.setTitle(resultSet.getString("title"));
		
		return requestedCourseUnit;
	}

    /**
     * Returns a list of a course's course units.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param courseID
     *            the course's ID
     * @param pagination
     *            the Pagination object which contains the amount of elements
     *            which are to be retrieved
     * @return the course's list of course units, or null if the course doesn't
     *         contain any course units
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * @author Ricky Strohmeier
     */
    public static List<CourseUnit> getCourseUnitsFromCourse(Transaction trans,
	    int courseID, PaginationData pagination)
	    throws InvalidDBTransferException {
    	ArrayList<CourseUnit> courseUnits = new ArrayList<CourseUnit>();
    
    	String courseUnitsQuery = "SELECT * FROM \"course_units\", \"course_unit_addresses\" WHERE "
    		+ "course_units.course_id = ? AND course_units.id = course_unit_addresses.course_unit_id "
    		+ " ORDER BY %s %s LIMIT ? OFFSET ?";
    
    	int offset = pagination.getElementsPerPage() * pagination.getCurrentPageNumber();
    
    	courseUnitsQuery = String.format(courseUnitsQuery, pagination.getSortColumn().toString(),
    	                                                pagination.getSortDirection().toString());
    	Connection connection = (Connection) trans;
    	java.sql.Connection conn = connection.getConn();
    
    	try(PreparedStatement statement = conn.prepareStatement(courseUnitsQuery)) {
    	    statement.setInt(1, courseID);
    	    statement.setInt(2, pagination.getElementsPerPage());
    	    statement.setInt(3, offset);

    	    try(ResultSet resultSet = statement.executeQuery()) {
    	        while (resultSet.next()) {
    	            CourseUnit unit = new CourseUnit();
    	            Timestamp startStamp, endStamp;
    	            Date startDate, endDate;
    	            
    	            unit.setCourseID(courseID);
    	            unit.setCourseUnitID(resultSet.getInt("id"));
    	            
    	            if (resultSet.getString("title") != null) {
    	                unit.setTitle(resultSet.getString("title"));
    	            } else {
    	                unit.setTitle("Ohne Titel");
    	            }
    	            
    	            if (resultSet.getString("description") != null) {
    	                unit.setDescription(resultSet.getString("description"));
    	            } else {
    	                unit.setDescription("Ohne Beschreibung");
    	            }
    	            
    	            unit.setMaxUsers(resultSet.getInt("max_participants"));
    	            unit.setMinUsers(resultSet.getInt("min_participants"));
    	            unit.setPrice(resultSet.getFloat("fee"));
    	            
    	            startStamp = resultSet.getTimestamp("start_time");
    	            if (startStamp != null) {
    	                startDate = new Date(
    	                        startStamp.getYear(),
    	                        startStamp.getMonth(),
    	                        startStamp.getDate(), 
    	                        startStamp.getHours(),
    	                        startStamp.getMinutes()
    	                        );
    	                unit.setStartime(startDate);
    	            }
    	            
    	            endStamp = resultSet.getTimestamp("end_time");
    	            if (endStamp != null) {
    	                endDate = new Date(
    	                        endStamp.getYear(),
    	                        endStamp.getMonth(),
    	                        endStamp.getDate(), 
    	                        endStamp.getHours(),
    	                        endStamp.getMinutes()
    	                        );
    	                unit.setEndtime(endDate);
    	            }
    	            
    	            unit.setNumberOfUsers(getNumberOfParticipants(trans, unit.getCourseUnitID()));
    	            
    	            Address unitAddress = new Address();
    	            
    	            unitAddress.setCity(resultSet.getString("city"));
    	            unitAddress.setCountry(resultSet.getString("country"));
    	            unitAddress.setZipCode(resultSet.getInt("zip_code"));
    	            
    	            if (resultSet.getString("street") != null) {
    	                unitAddress.setStreet(resultSet.getString("street"));
    	            } else {
    	                unitAddress.setStreet("Ohne Straße");
    	            }
    	            
    	            if (resultSet.getString("location") != null) {
    	                unitAddress.setLocation(resultSet.getString("location"));
    	            } else {
    	                unitAddress.setLocation("Ohne Ort");
    	            }
    	            
    	            if (resultSet.getInt("house_nr") > 0) {
    	                unitAddress.setHouseNumber(resultSet.getInt("house_nr"));
    	            } else {
    	                unitAddress.setHouseNumber(0);
    	            }
    	            
    	            unit.setAddress(unitAddress);
    	            courseUnits.add(unit);
	            }
    	    }
    	} catch (SQLException e) {
    	    LogHandler.getInstance().error("Error occoured in CourseUnitFromCourse from CourseUnitDAO");
    	    throw new InvalidDBTransferException();
    	}
    	return courseUnits;
    }

    /**
     * Updates a course unit stored in the database. The course unit's
     * attributes are replaced by the ones of the passed course unit.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param courseUnit
     *            the course unit to be updated
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * @author Tobias Fuchs
     */
    public static void updateCourseUnit(Transaction trans, CourseUnit courseUnit)
	    throws InvalidDBTransferException {
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	try (PreparedStatement stmt = conn.prepareStatement(UPDATE_UNIT);
	     PreparedStatement stmt1 = conn.prepareStatement(UPDATE_UNIT_ADDRESS)) {
	    
	    //Updates unit
	    stmt.setInt(1, courseUnit.getCourseID());
	    stmt.setInt(2, courseUnit.getMaxUsers());
	    stmt.setString(3, courseUnit.getTitle());
	    stmt.setInt(4, courseUnit.getMinUsers());
	    stmt.setFloat(5, courseUnit.getPrice());
	    stmt.setTimestamp(6, 
		    new java.sql.Timestamp(courseUnit.getStartime().getTime()));
	    stmt.setTimestamp(7, 
		    new java.sql.Timestamp(courseUnit.getEndtime().getTime()));
	    stmt.setString(8, courseUnit.getDescription());
	    stmt.setInt(9, courseUnit.getCourseAdmin().getUserID());
	    stmt.setInt(10, courseUnit.getCourseUnitID());
	    
	    stmt.executeUpdate();

	    //Updates unit address
	    stmt1.setString(1, courseUnit.getAddress().getCountry());
	    stmt1.setString(2, courseUnit.getAddress().getCity());
	    stmt1.setString(3, courseUnit.getAddress().getZipCode().toString());
	    stmt1.setString(4, courseUnit.getAddress().getStreet());
	    stmt1.setInt(5, courseUnit.getAddress().getHouseNumber());
	    stmt1.setString(6, courseUnit.getAddress().getLocation());
	    stmt1.setInt(7, courseUnit.getCourseUnitID());
	    
	    stmt1.executeUpdate();
	} catch (SQLException e) {
	    throw new InvalidDBTransferException(
		    "Error occured during updating a course unit.", e);
	}
    }

    /**
     * Deletes a course unit which is assigned to the passed ID.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param courseUnitID
     *            the ID of the course unit to be deleted
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * @author Tobias Fuchs
     */
    public static void deleteCourseUnit(Transaction trans, int courseUnitID)
	    throws InvalidDBTransferException {
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	try(PreparedStatement stmt = conn.prepareStatement(DELETE_UNIT);
	    PreparedStatement stmt1 = conn.prepareStatement(DELETE_UNIT_ADDRESS)) {
	    stmt.setInt(1, courseUnitID);
	    stmt.executeUpdate();

	    stmt1.setInt(1, courseUnitID);
	    stmt1.executeUpdate();

	} catch (SQLException e) {
	    throw new InvalidDBTransferException(
		    "Error occoured during deleting a course unit.", e);
	}
    }
    

    /**
     * Returns a list of course unit ids that belong to the same cycle as the
     * given course unit.<br>
     * If there are no other course units in this cycle the list only contains
     * the given id.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param courseUnitID
     *            the ID of the course unit to be deleted
     * @return the list with the course unit ids that belong to a cycle
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * @author Tobias Fuchs
     */
    public static List<Integer> getIdsCourseUnitsOfCycle(Transaction trans,
	    int courseUnitId) {
	List<Integer> ids = new ArrayList<Integer>();
	int cycle_id;
	
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();
	
	try (PreparedStatement stmt = conn.prepareStatement(GET_UNIT_IDS_OF_CYCLE)) {
	    cycle_id = CycleDAO.getCycleId(trans, courseUnitId);
	    stmt.setInt(1, cycle_id);
	    
	    try(ResultSet  res = stmt.executeQuery()){
		while (res.next()) {
		    ids.add(res.getInt("id"));
		}
	    }
	} catch (SQLException e) {
	    throw new InvalidDBTransferException("Error occured during fetching" 
		    + " the id of course units which belong "
		    + "to the same cycle.", e);
	}

	// Adds the given course unit id if it's not already in the list.
	if (!ids.contains(courseUnitId)) {
	    ids.add(courseUnitId);
	}
	return ids;
    }
    

    /**
     * Returns the price of a course unit.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param unitId
     *            the id of the unit
     * @return the price of the unit<br>
     *         -1, if there goes something wrong
     * @author Tobias Fuchs
     */
    public static float getPriceOfUnit(Transaction trans, int unitId) {
	float price = -1;
	
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();
	
	try (PreparedStatement stmt = conn.prepareStatement(GET_PRICE_OF_UNIT)){
	    stmt.setInt(1, unitId);
	    
	    try(ResultSet result = stmt.executeQuery()){
		result.next();
		price = result.getFloat("fee");
	    }
	} catch (SQLException e) {
	    throw new InvalidDBTransferException("Error during fetching " 
		    + "the price of course unit.", e);
	}
	return price;

    }

    /**
     * Adds a user to a course unit's list of participants.
     * <p>
     * A tuple of the user's ID and the course unit's ID is added to the table
     * containing course unit participants in the database.
     * </p>
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param userID
     *            the user's ID
     * @param courseUnitID
     *            the course unit's ID
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * @author Sebastian Schwarz
     */
    public static void addUserToCourseUnit(Transaction trans, int userID,
	    int courseUnitID) throws InvalidDBTransferException {
		
    	Connection connection = (Connection) trans;
		java.sql.Connection conn = connection.getConn();
	
		String addUserToCourseUnit = "INSERT INTO \"course_unit_participants\""
			+ " (participant_id,course_unit_id) VALUES (?,?)";
	
		try {
		    CourseDAO.setRelationMethode(userID, courseUnitID, conn,
			    addUserToCourseUnit);
		} catch (SQLException e) {
		    throw new InvalidDBTransferException("User:" + userID + "couldnt be added to CourseUnit:"
				    + courseUnitID, e);
		}

    }

    /**
     * Removes a user from a course unit's list of participants.
     * <p>
     * The tuple of the user's ID and the course unit's ID is removed from the
     * table of course unit participants in the database.
     * </p>
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param userID
     *            the user's ID
     * @param courseUnitID
     *            the course unit's ID
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * @author Sebastian Schwarz
     */
    public static void removeUserFromCourseUnit(Transaction trans, int userID,
	    int courseUnitID) throws InvalidDBTransferException {

		Connection connection = (Connection) trans;
		java.sql.Connection conn = connection.getConn();
	
		String deleteFromCourseUnit = "DELETE FROM \"course_unit_participants\" WHERE participant_id = ? AND course_unit_id = ?";
	
		try {
		    CourseDAO.setRelationMethode(userID, courseUnitID, conn,
			    deleteFromCourseUnit);

		} catch (SQLException e) {
		    throw new InvalidDBTransferException("User:" + userID + "couldnt be deleted from CourseUnit:"
				    + courseUnitID, e);
		}
    }

    /**
     * Returns a list of course units which the user with the passed ID
     * participates in.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param userID
     *            the user's ID
     * @return the list of course units which the user participates in, or null
     *         if the user doesn't participate in any course unit
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * 
     * @author Patrick Cretu
     */
    public static List<CourseUnit> getCourseUnitsOf(Transaction trans,
	    int userID) throws InvalidDBTransferException {
    	
		Connection connection = (Connection) trans;
		java.sql.Connection conn = connection.getConn();
		List<CourseUnit> result = new ArrayList<CourseUnit>();
		
		try (PreparedStatement stmt = conn.prepareStatement(GET_UNITS_OF)) {
		    stmt.setInt(1, userID);
		    
		    try (ResultSet rst = stmt.executeQuery()) {
		    	result = getResult(rst);
		    }
	
		} catch (SQLException e) {
		    throw new InvalidDBTransferException("SQL Exception occoured " +
		    		"during getCoursesInPeriod(java.sql.Connection conn, " +
		    		"int limit, int offset, String orderParam, " +
		    		"String query)", e);
		}
		return result;
    }

    /**
     * 
     * @param rst
     * @return
     * @throws InvalidDBTransferException
     * 
     * @author Patrick Cretu
     */
    private static List<CourseUnit> getResult(ResultSet rst)
	    throws InvalidDBTransferException {
		List<CourseUnit> result = new ArrayList<CourseUnit>();
		try {
		    int cols = rst.getMetaData().getColumnCount();
	
		    while (rst.next()) {
				int i = 1;
				List<Object> tuple = new ArrayList<Object>();
				CourseUnit unit = new CourseUnit();
				while (i <= cols) {
				    Object o = rst.getObject(i);
				    tuple.add(o);
				    i++;
				}
				setProperties(unit, tuple);
				result.add(unit);
		    }
		    if (!result.isEmpty()) {
		    	return result;
		    }
		} catch (SQLException e) {
		    throw new InvalidDBTransferException("SQL Exception occoured " +
		    		"during getResult(ResultSet rst)", e);
		}
		return result;
    }

    /**
     * 
     * @param unit
     * @param tuple
     * 
     * @author Patrick Cretu
     */
    private static void setProperties(CourseUnit unit, List<Object> tuple) {
		unit.setCourseUnitID((Integer) tuple.get(0));
		unit.setCourseID((Integer) tuple.get(1));
		unit.setTitle((String) tuple.get(2));
		BigDecimal bg = (BigDecimal) tuple.get(3);
		unit.setPrice(bg.floatValue());
		unit.setStartime((Date) tuple.get(4));
		unit.setEndtime((Date) tuple.get(5));
    }
    
    /**
     * 
     * @param trans
     * @return
     * 
     * @author Patrick Cretu
     */
    public static String getCurrentWeekDay(Transaction trans) {
    	
    	Connection connection = (Connection) trans;
    	java.sql.Connection conn = connection.getConn();
    	String currentDay = null;
    	String getDay = "SELECT timeofday()";
    	
    	try (Statement stmt = conn.createStatement()) {
    		
    		try (ResultSet rst = stmt.executeQuery(getDay)) {
    			rst.next();
    			currentDay = rst.getString(1);
    		}
			
		} catch (SQLException e) {
			throw new InvalidDBTransferException("SQL Exception occoured " +
					"during getCurrentWeekDay(Transaction trans)", e);
		}
    	return currentDay;
    }

    /**
     * 
     * @param trans
     * @param gap
     * @return
     * 
     * @author Patrick Cretu
     */
    public static java.sql.Date getCurrentMonday(Transaction trans, int gap) {
    	
    	Connection connection = (Connection) trans;
    	java.sql.Connection conn = connection.getConn();
    	java.sql.Date currentMonday = null;
    	String getMonday = "SELECT current_date - integer '" + gap + "'";
    	
    	try (Statement stmt = conn.createStatement()) {
    		
    		try (ResultSet rst = stmt.executeQuery(getMonday)) {
    			rst.next();
			currentMonday = rst.getDate(1);
    		}
    		
		} catch (SQLException e) {
			throw new InvalidDBTransferException("SQL Exception occoured " +
					"during getCurrentMonday(Transaction trans, int gap)", e);
		}
    	return currentMonday;
    }
    
    /**
     * 
     * @param trans
     * @param pagination
     * @param userID
     * @param monday
     * @return
     * 
     * @author Patrick Cretu
     */
    public static List<CourseUnit> getWeeklyCourseUnitsOf(Transaction trans,
    		int userID, java.sql.Date monday) {
    	
    	Connection connection = (Connection) trans;
    	java.sql.Connection conn = connection.getConn();
    	List<CourseUnit> result = null;
    	
    	try (PreparedStatement stmt = conn.prepareStatement(GET_WEEKLY_UNITS)) {
			stmt.setInt(1, userID);
			stmt.setDate(2, monday);
			stmt.setDate(3, monday);
			
			try (ResultSet rst = stmt.executeQuery()) {
				result = getResult(rst);
			}
			
		} catch (SQLException e) {
			throw new InvalidDBTransferException("SQL Exception occoured " +
					"during getWeeklyCourseUnitsOf(Transaction trans, " +
					"int userID, java.sql.Date monday)", e);
		}
    	return result;
    }
    
    /**
     * Counts the number of course units of a course
     * 
     * @param trans
     * @param courseID
     * @return the number of course units
     * @throws InvalidDBTransferException
     * @author Ricky Strohmeier
     */
    public static int getNumberOfCourseUnits(Transaction trans, int courseID)
	    throws InvalidDBTransferException {
    	int numberOfCourseUnits = 0;
    	String courseUnitQuery = "SELECT COUNT(*) FROM \"course_units\" WHERE course_id = ?";
    
    	Connection connection = (Connection) trans;
    	java.sql.Connection conn = connection.getConn();

    	try(PreparedStatement statement = conn.prepareStatement(courseUnitQuery)) {
    	    statement.setInt(1, courseID);

    	    try(ResultSet resultSet = statement.executeQuery()) {
    	        resultSet.next();
    	        numberOfCourseUnits = resultSet.getInt(1);    	        
    	    }
    	} catch (SQLException e) {
    	    LogHandler
    		    .getInstance().error("Error occoured during fetching the number of courses of a certain user.");
    	    throw new InvalidDBTransferException();
    	}
    	return numberOfCourseUnits;
    }

    /**
     * Returns the number of participants that attend the course unit with the
     * passed ID.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param courseUnitID
     *            the ID of the course unit
     * @return the number of participants of the course unit
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * @author Tobias Fuchs
     */
    public static int getNumberOfParticipants(Transaction trans,
	    int courseUnitId) throws InvalidDBTransferException {
	int numberParticipants = 0;

	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();
	
	try (PreparedStatement stmt = conn.prepareStatement(COUNT_NUMBER_OF_PARTICIPANTS)) {
	    stmt.setInt(1, courseUnitId);
	    
	    try(ResultSet resultSet = stmt.executeQuery();){
		resultSet.next();
		numberParticipants = resultSet.getInt(1);
	    }  
	} catch (SQLException e) {
	    throw new InvalidDBTransferException( "Error occoured during " 
	            + "fetching the number of particpants of course unit with"
		    + "id: "
		    + courseUnitId + ".", e);
	}
	return numberParticipants;
    }

    /**
     * Returns the offset calculated from the pagination information.
     * 
     * @param pagination
     *            contains the information for pagination
     * @return the calculated offset
     * @author Tobias Fuchs
     */
    private static int calculateOffset(PaginationData pagination) {
	int calculatedOffset = pagination.getElementsPerPage()
		* pagination.getCurrentPageNumber();
	
	return calculatedOffset;
    }

    /**
     * Returns a list of users which participate in the course unit with the
     * passed ID.
     * 
     * @param trans
     *            the Transaction object which contains the connection to the
     *            database
     * @param pagination
     *            contains the information for pagination
     * @param courseUnitId
     *            the ID of the course unit the list of users which participate
     *            in the course unit, or a empty list if the no user attends
     *            this course unit
     * 
     * @throws InvalidDBTransferException
     *             if any error occurred during the execution of the method
     * @author Tobias Fuchs
     */
    public static List<User> getParticipiantsOfCourseUnit(Transaction trans,
	    PaginationData pagination, int courseUnitId, boolean all)
	    throws InvalidDBTransferException {
	String direction = pagination.getSortDirection().toString();
	List<User> participants = new ArrayList<User>();
	
	String query = String.format(
		GET_PARTICIPANTS_OF, 
		pagination.getSortColumn(),
		direction);
	
	Connection connection = (Connection) trans;
	java.sql.Connection conn = connection.getConn();

	int limit;
	int offset;

	// Initializes limit and offset according to the fact whether 
	// all participants are to be fetched or only the ones for the next page
	if (all) {
	    limit = CourseUnitDAO.getNumberOfParticipants(trans, courseUnitId);
	    offset = 0;
	} else {
	    limit = pagination.getElementsPerPage();
	    offset = calculateOffset(pagination);
	}

	try (PreparedStatement stmt = conn.prepareStatement(query)){
	    stmt.setInt(1, courseUnitId);
	    stmt.setInt(2, limit);
	    stmt.setInt(3, offset);

            try (ResultSet fetchedParticipants = stmt.executeQuery()){
        	
        	while (fetchedParticipants.next()) {
        	    User fetchedUser = new User();
        	    fetchedUser.setUserID(fetchedParticipants.getInt("id"));
        	    if (fetchedParticipants.getString("name") != null) {
        		fetchedUser.setLastname(
        			fetchedParticipants.getString("name"));
        	    } else {
        		fetchedUser.setLastname("Nicht angegeben");
        	    }

        	    if (fetchedParticipants.getString("first_name") != null) {
        		fetchedUser.setFirstname(
        			fetchedParticipants.getString("first_name"));
        	    } else {
        		fetchedUser.setFirstname("Nicht angegeben");
        	    }
        	    
        	    fetchedUser.setUsername(
        		    fetchedParticipants.getString("nickname"));
        	    fetchedUser.setAccountBalance(
        		    fetchedParticipants.getFloat("credit_balance"));
        	    fetchedUser.setEmail(fetchedParticipants.getString("email"));
        	    
        	    participants.add(fetchedUser);
	    }
          }
	} catch (SQLException e) {
	    throw new InvalidDBTransferException("Error occoured during "
	              + "fetching the next set of participants of a course.", e);
	}
	return participants;
    }
}
