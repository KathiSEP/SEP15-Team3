/**
 * This package represents the business logic of the ofCourse system.
 */
package de.ofCourse.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.servlet.http.HttpSession;

import de.ofCourse.databaseDAO.CourseDAO;
import de.ofCourse.databaseDAO.UserDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Language;
import de.ofCourse.model.PaginationData;
import de.ofCourse.model.SortColumn;
import de.ofCourse.model.SortDirection;
import de.ofCourse.model.User;
import de.ofCourse.system.Connection;
import de.ofCourse.system.LogHandler;
import de.ofCourse.system.Transaction;
import de.ofCourse.utilities.LanguageManager;

/**
 * Displays the participants that attend a certain course and offers the course
 * leader the possibility to delete users from on of his managed courses.
 * <p>
 * 
 * Supports pagination to display the users.
 * 
 * <p>
 * This class is ManagedBean and controller of the facelet
 * <code>listParticipients</code>.
 * 
 * @author Katharina Hölzl
 *
 */
@ManagedBean
@ViewScoped
public class ListParticipantsBean implements Pagination {
    
    /**
     * Stores the transaction that is used for database interaction.
     */
    private Transaction transaction;
    
    /**
     * Stores the elements per page
     */
    private static final int elementsPerPage = 10;
    
    /**
     * Stores the columns to sort
     */
    private String sortColumn;
    
    /**
     * Stores the course ID
     */
    private int courseID;

    /**
     * Stores the current page
     */
    private int currentPage;
    
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
    
    
    /**
     * Stores the the list of participants that is displayed on the page.
     */
    private DataModel<User> participants;

    /**
     * Stores the the list of users that are to removed from the course.
     */
    private List<User> usersToDelete;

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
    private void init() {
        this.participants = new ListDataModel<User>();
        this.setCourseID(-1);
        
        //Fetch the parameter language with the value DE, EN ore BAY out of the 
        //Session Map
        Map<String, Object> sessionMap = FacesContext
                .getCurrentInstance().getExternalContext().getSessionMap();
        
        //New language object
        Language lang = null;
        
        //Check if the parameter language exists in the session. 
        if(sessionMap.containsKey("lang")) {
            //Convert the string (DE, EN, BAY) into a language object
            lang = Language.fromString(sessionMap.get("lang").toString());
            
        } else {
            //Set the language to German (DE) an write the parameter into the 
            //session
            lang = Language.DE;
            HttpSession httpSession = (HttpSession) FacesContext
                    .getCurrentInstance().getExternalContext()
                    .getSession(true);
            httpSession.setAttribute("lang", lang.toString());
        }
        
        try {
            this.setCourseID(Integer.parseInt(FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getRequestParameterMap()
                    .get("courseID")));
            
            pagination = new PaginationData(elementsPerPage, 0, 
                                        SortColumn.NICKNAME, SortDirection.ASC);
        }
        catch (Exception e) {
            // FacesMessage: ' The page was called without a parameter'
            FacesMessageCreator.createFacesMessage(
                    "parameter",  
                    LanguageManager.getInstance().
                                    getProperty(
                                            "listParticipantsBean.facesMessage."
                                            + "NoParameter", 
                                            lang));
        }
        
        if(this.getCourseID() < 0) {
            //FacesMessage: 'The page does not exist'
            FacesMessageCreator.createFacesMessage(
                    "parameter",  
                    LanguageManager.getInstance().
                                    getProperty(
                                            "listParticipantsBean.facesMessage."
                                            + "PageNotExist", lang));
        } else {       
            transaction = Connection.create();
            transaction.start();
            
            try {
                this.pagination.refreshNumberOfPages(CourseDAO
                      .getNumberOfParticipants(transaction, this.getCourseID()));
                this.participants.setWrappedData
                        (UserDAO.getParticipantsOfCourse
                                                        (this.transaction,  
                                                         this.getPagination(), 
                                                         this.getCourseID()));
                
                this.transaction.commit();
                
            } catch (InvalidDBTransferException e) {
                LogHandler.getInstance().error(
                        "Error occured during updating the"
                                + " page with elements from database.");
                this.transaction.rollback();
            }
        }
    }

    /**
     * Deletes the selected users from the course and with it automatically from
     * all course units of this course and transfers the paid price for the
     * course units automatically back to the accounts of the participants.<br>
     * Deleting means the users are removed from the participants list and the
     * database entry of the course and the respective course units are updated.
     */
    public void deleteUsersFromCourse() {
        
        @SuppressWarnings("unchecked")
        
        List<User> allUsers = (List<User>) this.participants.getWrappedData();
        this.usersToDelete = new ArrayList<User>();
        
        for(User user : allUsers) {
            if(user.isSelected()) {
                this.usersToDelete.add(user);
            }
        }
        transaction.start();
        
        try {
            //Check if remove participants from course was successful
            if(UserDAO.removeParticipantsFromCourse
                    (this.transaction, this.getCourseID(), 
                                           this.getUsersToDelete()) == false) {
                LogHandler.getInstance().error(
                        "Error occured during deleteUsersFromCourse().");
            } else {
                //Refresh page content
                this.pagination.refreshNumberOfPages(CourseDAO
                        .getNumberOfParticipants(transaction, 
                                                           this.getCourseID()));
                this.participants.setWrappedData(UserDAO.
                        getParticipantsOfCourse(
                                this.transaction, 
                                this.getPagination(),
                                this.getCourseID()));
                
                //FacesMessage: 'Deleting successful'
                FacesMessageCreator.createFacesMessage(
                        null, 
                        sessionUser.getLabel(
                                "listParticipantsBean.facesMessage."
                                + "DeletingSuccessful"));
            }
            this.transaction.commit();
        } catch (InvalidDBTransferException e) {
            LogHandler.getInstance().error(
                    "Error occured during deleteUsersFromCourse().");
            this.transaction.rollback();
        }

    }

    /**
     * Returns the value of the attribute <code>usersToActivate</code> that
     * stores the participants which are to deleted from the course.
     * 
     * @return list of users who shall be deleted from a course
     */
    public List<User> getUsersToDelete() {
	return usersToDelete;
    }

    /**
     * Sets the value of the attribute <code>usersToDelete</code> that stores
     * the participants which are to deleted from the course.
     * 
     * @param usersToDelete
     *            the new list of users who shall be deleted from a course
     */
    public void setUsersToDelete(List<User> usersToDelete) {
    }

    /**
     * Redirects the user back to the courseDetail - page of the course.
     * 
     * @return the link to the courseDetail - page
     */
    public String backToDetails() {
        System.out.println("hier");
        return "/facelets/open/courses/courseDetail.xhtml?courseID=" + 
                this.getCourseID() + "faces-redirect=true";
    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public void goToSpecificPage() {
        
	this.pagination.setCurrentPageNumber(this.currentPage);
        transaction.start();
        
        try {
            this.participants.setWrappedData(
                  UserDAO.getParticipantsOfCourse(
                                                 this.transaction, 
                                                 this.getPagination(),
                                                 this.getCourseID()));
            this.transaction.commit();
            
        } catch (InvalidDBTransferException e) {
            LogHandler.getInstance().error(
                    "Error occured during fetching data for pagination.");
            this.transaction.rollback();
        }
    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public void sortBySpecificColumn() {
        
        if(this.getPagination().getSortColumn().equals(
                                     SortColumn.fromString(this.sortColumn))) {
            this.getPagination().changeSortDirection();
            
        } else {
            this.getPagination().setSortColumn(
                                        SortColumn.fromString(this.sortColumn));
        }
        transaction.start();
        
        try {
            this.participants.setWrappedData(
                    UserDAO.getParticipantsOfCourse(
                                                    this.transaction, 
                                                    this.getPagination(),
                                                    this.getCourseID()));
            
            this.transaction.commit();
            
        } catch (InvalidDBTransferException e) {
            LogHandler.getInstance().error(
                    "Error occured during fetching data for pagination.");
            this.transaction.rollback();
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
    public void setSessionUser(SessionUserBean userSession) {
        this.sessionUser = userSession;
    }

  
    /**
     * @return the courseID
     */
    public int getCourseID() {
        return courseID;
    }

    /**
     * @param courseID the courseID to set
     */
    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    /**
     * @return the sortColumn
     */
    public String getSortColumn() {
        return sortColumn;
    }

    /**
     * @param sortColumn 
     *                 the sortColumn to set
     */
    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }

    /**
     * 
     * @return the participants
     */
    public DataModel<User> getParticipants() {
        return participants;
    }

    /**
     * 
     * @param participants 
     *                  the participants of the course to set
     */
    public void setParticipants(DataModel<User> participants) {
        this.participants = participants;
    }

    

}
