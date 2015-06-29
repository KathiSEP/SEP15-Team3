/**
 * This package represents the business logic of the ofCourse system.
 */
package de.ofCourse.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import de.ofCourse.databaseDAO.CourseDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Course;
import de.ofCourse.model.PaginationData;
import de.ofCourse.model.SortColumn;
import de.ofCourse.model.SortDirection;
import de.ofCourse.system.Connection;
import de.ofCourse.system.LogHandler;
import de.ofCourse.system.Transaction;

/**
 * Provides the service of displaying the course offer of the system and the
 * service of searching for courses in the system.<br>
 * The user can search for courses by certain parameters and a entered
 * respective search term. Furthermore this class realizes the functionality to
 * display the complete course offer of the system. In addition it is possible
 * to constrain the displayed course offer.<br>
 * This class supports pagination to display the found courses.
 * 
 * <p>
 * This class is ManagedBean and controller of the facelet <code>search</code>.
 * </p>
 * 
 * @author Patrick Cretu
 *
 */
@ManagedBean
@ViewScoped
public class SearchCourseBean implements Pagination {
    
	/**
     * Stores the number of elements that are displayed by pagination at once
     */
    private final int ELEMENTS_PER_PAGE = 10;
	
    /**
     * Stores the transaction that is used for database interaction.
     */
    private Transaction transaction;

    /**
     * Stores the display period that was selected by the user. With the display
     * period the user can constrain the time period in which the displayed
     * courses take place.
     */
    private String displayPeriod;

    /**
     * Stores the the search result that is displayed on the page. In this case
     * it's a list of users.
     */
    private List<Course> searchResult;
    
    /**
     * The parameter required for column sort
     */
    private String orderParam;
    
    /**
     * The currently displayed page
     */
    private int currentPage;
     
    /**
     * Stores the search parameter that was selected by the user
     */
    private String searchParam;

    /**
     * Stores the search term that was entered by the user
     */
    private String searchString;
    
    /**
     * Checks if the result table should to displayed
     */
    private boolean renderTable;
    
    /**
     * Checks if the user's search request contained text input
     */
    private boolean pagingSearchTerm;
    
    /**
     * Checks if the user requested a column sort
     */
    private boolean columnSort;
    
    /**
     * Stores the time period which the user selected in case the user changes
     * the parameter during column sort or pagination
     */
    private String orderPeriod;
    
    /**
     * Stores the search parameter which the user selected in case the user
     * changes the parameter during column sort or pagination
     */
    private String orderSearchParam;
    
    /**
     * Stores the text input which the user entered in case the user inputs new
     * text during column sort or pagination
     */
    private String orderSearchString;

    /**
     * This attribute represents a pagination object. It stores all the
     * information that is necessary for pagination, e.g. the number of elements
     * per page.
     */
    private PaginationData pagination;

    /**
     * This ManagedProperty represents the actual session of a user. It stores
     * the id, the userRole, the userStatus of the user and the selected
     * language.
     */
    @ManagedProperty("#{sessionUser}")
    private SessionUserBean sessionUser;
    
    @PostConstruct
    public void init() {
    	displayPeriod = "total";
    	searchParam = "title";
    	pagination = new PaginationData();
    	pagination.setElementsPerPage(ELEMENTS_PER_PAGE);
    	transaction = Connection.create();
    }

    /**
     * Displays the course offer of the system.<br>
     * The user can constrain by selecting a display period in which time period
     * the shown courses take place. Furthermore it is also possible to display
     * the complete course offer of the system. The courses are displayed by
     * using pagination.
     * 
     */
    public void displayCoursesInPeriod() {
    	transaction.start();
    	
    	pagination.setCurrentPageNumber(0);
    	pagination.setSortDirection(SortDirection.ASC);
    	pagination.setSortColumn(SortColumn.ID);
    	
    	try {
    		pagination.refreshNumberOfPages(CourseDAO.getNumberOfCourses(
    				transaction, displayPeriod, searchString));
	    	List<Course> result = CourseDAO.getCourses(transaction, pagination,
	    			displayPeriod);
	    	
	    	if (result != null) {
	    		setResultParams(result, false);
	    		transaction.commit();
	    	} else {
	    		FacesMessageCreator.createFacesMessage(null,
						sessionUser.getLabel("search.message.result"));
	    		setRenderTable(false);
	    		transaction.rollback();
	    	}
    	} catch (InvalidDBTransferException e) {
    		LogHandler
            .getInstance()
            .error("SQL Exception occoured during executing "
                    + "createUser()");
    		transaction.rollback();
    	}
    }
    
    /**
     * Sets the view's outcome parameters after a result has been retrieved.
     * 
     * @param result
     *             the list containing the requested courses
     * @param pagingSearchTerm
     *                       the value whether or not the user searched by text
     *                       input or by selected period parameters
     */
    private void setResultParams(List<Course> result,
    		boolean pagingSearchTerm) {
    	searchResult = result;
		setPagingSearchTerm(pagingSearchTerm);
		setRenderTable(true);
		columnSort = false;
		orderPeriod = displayPeriod;
		orderSearchParam = searchParam;
		orderSearchString = searchString;
    }
    
    /**
     * Returns the value of the attribute <code>displayPeriod</code> that stores
     * the selected display period.
     * 
     * @return the selected display period
     */
    public String getDisplayPeriod() {
    	return displayPeriod;
    }

    /**
     * Sets the value of the attribute code>displayPeriod</code> that stores the
     * selected display period.
     * 
     * @param displayPeriod
     *            the selected display period
     */
    public void setDisplayPeriod(String displayPeriod) {
    	this.displayPeriod = displayPeriod;
    }
    
    /**
     * Searches for courses in the system according to the selected search
     * parameter and the respective search term. <br>
     * It performs a database request with the entered search parameter and
     * search term. The search result is stored locally so it can be displayed
     * in the facelet.
     */
    public void search() {
    	if (!searchString.isEmpty()) {
    		if (searchParam.equals("start_date")) {
    			if (isValidDate(searchString)) {
    				executeSearch();
    			} else {
    				FacesMessageCreator.createFacesMessage(null,
    						sessionUser.getLabel("search.message"));
    			}
    		} else {
    			executeSearch();
    		}
    	} else {
    		setRenderTable(false);
    	}
    }
    
    /**
     * Checks if the user input has a valid date format.
     * 
     * @param date
     *           the entered date text
     * @return true, if the entered date format is valid, otherwise false
     */
    private boolean isValidDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateFormat.setLenient(true);
        
        try {
        	dateFormat.parse(date.trim());
        } catch (ParseException pe) {
        	return false;
        }
        return true;
    }
    
    /**
     * Sets the required pagination values and executes the course search
     */
    private void executeSearch() {
		transaction.start();
		
		pagination.setCurrentPageNumber(0);
		pagination.setSortDirection(SortDirection.ASC);
		pagination.setSortColumn(SortColumn.fromString(searchParam));
		try {
    		pagination.refreshNumberOfPages(CourseDAO.getNumberOfCourses(
    				transaction, searchParam, searchString));
    		List<Course> result = CourseDAO.getCourses(transaction, pagination,
    			searchParam, searchString);
    		
    		if (result != null) {
        		setResultParams(result, true);
        		transaction.commit();
    		} else {
    			FacesMessageCreator.createFacesMessage(null,
						sessionUser.getLabel("search.message.result"));
    			setRenderTable(false);
    			transaction.rollback();
    		}
    	} catch (InvalidDBTransferException e) {
    		LogHandler
            .getInstance()
            .error("SQL Exception occoured during executing "
                    + "createUser()");
    		transaction.rollback();
    	}
    }

    /**
     * Returns the value of the attribute <code>searchResult</code> that stores
     * the found database entries which are to be displayed.
     * 
     * @return list of found courses
     */
    public List<Course> getSearchResult() {
    	return searchResult;
    }

    /**
     * Sets the value of the attribute <code>searchResult</code> that stores the
     * found database entries which are to be displayed.
     * 
     * @param searchResult
     *            list of users found in the database for the actual search
     */
    public void setSearchResult(List<Course> searchResult) {
    	this.searchResult = searchResult;
    }
    
    /**
     * 
     * @return the parameter required for column sort
     */
	public String getOrderParam() {
		return orderParam;
	}

	/**
	 * Sets the parameter required for column sort
	 * 
	 * @param orderParam
	 *                 the selected order parameter
	 */
	public void setOrderParam(String orderParam) {
		this.orderParam = orderParam;
	}

	/**
     * Returns the value of the attribute <code>searchParam</code> that stores
     * the selected search parameter.
     * 
     * @return the selected search parameter
     */
    public String getSearchParam() {
    	return searchParam;
    }

    /**
     * Sets the value of the attribute <code>searchParam</code> that stores the
     * selected search parameter.
     * 
     * @param searchParam
     *            the selected search parameter
     */
    public void setSearchParam(String searchParam) {
    	this.searchParam = searchParam;
    }

    /**
     * Sets the value of the attribute <code>searchTerm</code> that stores the
     * entered search term.
     * 
     * @param searchTerm
     *            the entered search term
     */
    public void setSearchString(String searchString) {
    	this.searchString = searchString;
    }

    /**
     * 
     * @return the user's text input
     */
    public String getSearchString() {
		return searchString;
	}

    /**
     * 
     * @return true, if the result table should be rendered, false otherwise
     */
	public boolean isRenderTable() {
		return renderTable;
	}
	
	/**
	 * Sets the value, if the result table should be rendered or not
	 * 
	 * @param renderTable
	 *                  the boolean value
	 */
	public void setRenderTable(boolean renderTable) {
		this.renderTable = renderTable;
	}

	/**
	 * 
	 * @return true, if the user searched by text input, false otherwise
	 */
	public boolean isPagingSearchTerm() {
		return pagingSearchTerm;
	}

	/**
	 * Sets the value, if the user searched by text input
	 * 
	 * @param pagingSearchTerm
	 *                       the boolean value
	 */
	public void setPagingSearchTerm(boolean pagingSearchTerm) {
		this.pagingSearchTerm = pagingSearchTerm;
	}
	
    /**
     * {@inheritDoc}
     */
    @Override
    public void sortBySpecificColumn() {
	    transaction.start();
    	
    	if (pagination.getSortColumn().toString().equals(orderParam)) {
    		pagination.changeSortDirection();
    	} else {
    		pagination.setSortDirection(SortDirection.ASC);
    	}
    	pagination.setSortColumn(SortColumn.fromString(orderParam));
    	columnSort = true;
    	List<Course> result;
    	
    	try {
    		if (pagingSearchTerm) {
	    		result = CourseDAO.getCourses(transaction, pagination,
	        			orderSearchParam, orderSearchString);
			} else {
		    	result = CourseDAO.getCourses(transaction, pagination,
		    			orderPeriod);
			}
    		
    		if (result != null) {
	    		searchResult = result;
	    		transaction.commit();
	    	} else {
	    		FacesMessageCreator.createFacesMessage(null,
						sessionUser.getLabel("search.message.result"));
	    		setRenderTable(false);
	    		transaction.rollback();
	    	}
    	} catch (InvalidDBTransferException e) {
    		LogHandler
            .getInstance()
            .error("SQL Exception occoured during executing "
                    + "createUser()");
    		transaction.rollback();
    	}
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void goToSpecificPage() {
	    transaction.start();
	    
	    try {
	    	this.pagination.setCurrentPageNumber(this.currentPage);
	    	String period;
	    	String param;
	    	String term;
	    	List<Course> result;
	    	
	    	if (columnSort) {
	    		period = orderPeriod;
	    		param = orderSearchParam;
	    		term = orderSearchString;
	    	} else {
	    		period = displayPeriod;
	    		param = searchParam;
	    		term = searchString;
	    	}
			
			if (pagingSearchTerm) {
				result = CourseDAO.getCourses(transaction, pagination,
	        			param, term);
			} else {
				result = CourseDAO.getCourses(transaction, pagination,
		    			period);
			}
			
			if (result != null) {
	    		searchResult = result;
	    		transaction.commit();
	    	} else {
	    		FacesMessageCreator.createFacesMessage(null,
						sessionUser.getLabel("search.message.result"));
	    		setRenderTable(false);
	    		transaction.rollback();
	    	}
    	} catch (InvalidDBTransferException e) {
    		LogHandler
            .getInstance()
            .error("SQL Exception occoured during executing "
                    + "createUser()");
    		transaction.rollback();
    	}
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public PaginationData getPagination() {
    	return pagination;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPagination(PaginationData pagination) {
    	this.pagination = pagination;
    }
    
    /**
     * Returns the ManagedProperty <code>SessionUser</code>.
     * 
     * @return the session of the user
     */
    public SessionUserBean getSessionUser() {
    	return sessionUser;
    }

    /**
     * Sets the ManagedProperty <code>SessionUser</code>.
     * 
     * @param userSession
     *            session of the user
     */
    public void setSessionUser(SessionUserBean sessionUser) {
    	this.sessionUser = sessionUser;
    }

    /**
     * @return the currentPage
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * @param currentPage the currentPage to set
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
    
}
