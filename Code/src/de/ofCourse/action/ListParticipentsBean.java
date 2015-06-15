/**
 * This package represents the business logic of the ofCourse system.
 */
package de.ofCourse.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import de.ofCourse.Database.dao.CourseDAO;
import de.ofCourse.Database.dao.UserDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.PaginationData;
import de.ofCourse.model.User;
import de.ofCourse.system.Connection;
import de.ofCourse.system.LogHandler;
import de.ofCourse.system.Transaction;

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
 * @author Katharina H�lzl
 *
 */
@ManagedBean
@ViewScoped
public class ListParticipentsBean implements Pagination {
    
    /**
     * Stores the transaction that is used for database interaction.
     */
    private Transaction transaction;
    
    private static final int elementsPerPage = 5;
    
    private int courseID;

    /**
     * Stores the the list of participants that is displayed on the page.
     */
    private List<User> participients;

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
        this.setCourseID(Integer.parseInt(FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap().get("courseID")));
        pagination = new PaginationData(elementsPerPage, 0, "title", true);
        
        transaction = Connection.create();
        transaction.start();
        try {
            this.pagination.actualizeNumberOfPages(CourseDAO
                    .getNumberOfParticipants(transaction, this.getCourseID()));
            this.setParticipients((ArrayList<User>) UserDAO.getParticipantsOfCourse(this.transaction, this.getPagination(),
                            this.getCourseID()));
            this.transaction.commit();
        } catch (InvalidDBTransferException e) {
            LogHandler.getInstance().error(
                    "Error occured during updating the"
                            + " page with elements from database.");
            this.transaction.rollback();
        }

    }
    
    /**
     * Returns a list of users who are attending the course.
     * 
     * @return list of participants
     */
    public List<User> getParticipients() {
	return participients;
    }

    /**
     * Sets the value of the attribute <code>participants</code> of a course.
     * 
     * @param participients
     *            the new participants list
     */
    public void setParticipients(List<User> participients) {
    }

    /**
     * Initializes the <code>listParticipants</code> page with the participants
     * of the course.
     */
    public void initializeParticipantsOfCourse() {
    }

    /**
     * Deletes the selected users from the course and with it automatically from
     * all course units of this course and transfers the paid price for the
     * course units automatically back to the accounts of the participants.<br>
     * Deleting means the users are removed from the participants list and the
     * database entry of the course and the respective course units are updated.
     */
    public void deleteUsersFromCourse() {
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
    public String cancel() {
	return null;
    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public void goToSpecificPage() {
    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public void sortBySpecificColumn() {
     // Not needed in MyCoursesBean

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

    

}
