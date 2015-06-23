/**
 * This package represents the business logic of the ofCourse system.
 */
package de.ofCourse.action;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import de.ofCourse.Database.dao.UserDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Language;
import de.ofCourse.model.User;
import de.ofCourse.system.Connection;
import de.ofCourse.system.LogHandler;
import de.ofCourse.system.Transaction;
import de.ofCourse.utilities.PasswordHash;

/**
 * Provides the function that a user can log in the system by entering his
 * user name and his password.<br>
 * <p>
 * It is checked whether the user is registered in the system.<br>
 * In case of this is true, the user is logged in the system. Otherwise the
 * access to the system isn't granted.<br>
 * 
 * This class is ManagedBean and controller of the facelet
 * <code>authenticate</code>.
 * 
 * @author Katharina Hölzl
 *
 */
@ManagedBean
@RequestScoped
public class AuthenticateUserBean {

    /**
     * Represents a user object. It stores the entered user name and the entered
     * password, which are needed to log in.
     */
    private User loginUser;

    /**
     * This managed Property represents the mail bean.
     */
    @ManagedProperty("#{mailBean}")
    private MailBean mailBean;

    /**
     * Returns the ManagedProperty <code>MailBean</code>.
     * 
     * @return the mailBean
     */
    public MailBean getMailBean() {
        return mailBean;
    }

    /**
     * Sets the ManagedProperty <code>MailBean</code>.
     * 
     * @param mailBean
     *            the mailBean to set
     */
    public void setMailBean(MailBean mailBean) {
        this.mailBean = mailBean;
    }

    /**
     * The password which was inserted by the user.
     */
    private String loginPassword;

    /**
     * Stores the transaction that is used for database interaction.
     */
    private Transaction transaction;

    /**
     * This ManagedProperty represents the actual session of a user. It stores
     * the id, the userRole, the userStatus of the user and the selected
     * language.
     */
    @ManagedProperty("#{sessionUser}")
    private SessionUserBean sessionUser;

    /**
     * Initialize new session, because nothing could be logged in at the login 
     * page yet.
     */
    @PostConstruct
    private void init() {
        loginUser = new User();
    }

    /**
     * Returns the link to the <code>myCourses</code> page if the entered
     * user name and the respective password are valid.
     * <p>
     * It is checked whether the entered user name and the respective password
     * belong to a user that is registered in the system. If this is true, the
     * log in action is successful and the user is directed to his
     * <code>myCourses</code> page.<br>
     * Otherwise there's displayed an error message an the user stays on this
     * page.
     * 
     * @return link to the next page
     */
    public String login() {

        final int usernameOrPasswordWrong = -1;
        final int accountNotActivated = -2;
        final int dbErrorOccured = -3;

        int id = dbErrorOccured;

        // Create a new transaction object for the database connection. 
        this.transaction = Connection.create();
        this.transaction.start();
        try {
            // hash inserted password
            String salt = UserDAO.getPWSalt(this.transaction, this.getLoginUser()
                        .getUsername());
            if(salt == null) {
                FacesMessageCreator.createFacesMessage(null,
                        "Benutzername oder Passwort falsch!");
            } else {
                String passwordHash = PasswordHash.hash(this.loginPassword, salt);
                // Checks if the username and the password are valid.
                id = UserDAO.proveLogin(this.transaction, this.getLoginUser()
                        .getUsername(), passwordHash);
    
                // Method proveLogin returns -1, if user name ore password are 
                // wrong.
                // Returns -2 if the user account is not activated yet.
                // Else it returns the id of the user.
                if (id == usernameOrPasswordWrong) {
                    // Throwing error message into the faces context.
                    FacesMessageCreator.createFacesMessage(null,
                            "Benutzername oder Passwort falsch!");
    
                    // Return to the login page again, because the login went 
                    // wrong.
                    this.transaction.rollback();
                    return "/facelets/open/authenticate.xhtml?faces-redirect=false";
                } else if (id == accountNotActivated) {
                    // Throwing error message into the faces context
                    FacesMessageCreator.createFacesMessage(null,
                            "Benutzerkonto nicht aktiv!");
    
                    // Return to the login page again, because the login went 
                    // wrong.
                    this.transaction.rollback();
                    return "/facelets/open/authenticate.xhtml?faces-redirect=false";
                } else if (id == dbErrorOccured) {
                    // Throwing error message into the faces context.
                    FacesMessageCreator.createFacesMessage(null,
                            "Benutzerkonto nicht aktiv!");
    
                    // Return to the login page again, because the login went 
                    // wrong.
                    this.transaction.rollback();
                    return "/facelets/open/authenticate.xhtml?faces-redirect=false";
                } else {
                    // Filling the session object with the user data, 
                    // interrogate not yet available data from the database 
                    // by using the user id.
                    sessionUser.setLanguage(Language.DE);
                    sessionUser.setUserID(id);
                    sessionUser.setUserRole(UserDAO.getUserRole(this.transaction,
                            id));
                    sessionUser.setUserStatus(UserDAO.getUserStatus(
                            this.transaction, id));
    
                    // Filling the HTTP-Session with the user data for the 
                    // PhaseListener.
                    HttpSession session = (HttpSession) FacesContext
                            .getCurrentInstance().getExternalContext()
                            .getSession(true);
                    session.setAttribute("loggedin", true);
                    session.setAttribute("userID", id);
                    session.setAttribute("userRole", sessionUser.getUserRole());
    
                    // Forwarding to the page myCourses, because the login was 
                    // successful.
                    this.transaction.commit();
                    return "/facelets/user/registeredUser/myCourses.xhtml?faces-redirect=true";
                }
            }

        } catch (InvalidDBTransferException e) {
            LogHandler.getInstance().error(e.getMessage());
            this.transaction.rollback();
        }

        return "/facelets/open/authenticate.xhtml?faces-redirect=false";
    }

    /**
     * Returns the value of the attribute <code>loginUser</code>.
     * 
     * @return the user who wants to log in
     */
    public User getLoginUser() {
        return loginUser;
    }

    /**
     * Sets the value of the attribute <code>loginUser</code>.
     * 
     * @param userToLogIn
     *            the users who wants to log in
     */
    public void setLoginUser(User loginUser) {
        this.loginUser = loginUser;
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
     * Returns the value of the attribute <code>loginPassword</code>.
     * 
     * @return the inserted password
     */
    public String getLoginPassword() {
        return loginPassword;
    }

    /**
     * Sets the value of the attribute <code>loginPassword</code>.
     * 
     * @param loginPassword
     *            inserted Password
     */
    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

}