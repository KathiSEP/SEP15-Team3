/**
 * This package represents the business logic of the ofCourse system.
 */
package de.ofCourse.action;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
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
import de.ofCourse.system.Transaction;
import de.ofCourse.utilities.PasswordHash;

/**
 * Provides the function that a user can log in the system by entering his
 * username and his password.<br>
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
     * Represents a user object. It stores the entered username and the entered
     * password, which are needed to log in.
     */
    private User loginUser;

    @ManagedProperty("#{mailBean}")
    private MailBean mailBean;

    /**
     * @return the mailBean
     */
    public MailBean getMailBean() {
        return mailBean;
    }

    /**
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

    @PostConstruct
    private void init() {
        // Session neu initialisieren, da auf der Login-Seite noch keiner
        // eingeloggt sein kann.
        loginUser = new User();
    }

    /**
     * Returns the link to the <code>myCourses</code> page if the entered
     * username and the respective password are valid.
     * <p>
     * It is checked whether the entered username and the respective password
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

        // Neues Transaction Objekt erstellen für die Datenbankverbindung
        this.transaction = Connection.create();
        this.transaction.start();
        try {
            // Eingegebenes Passwort hashen
            String salt = UserDAO.getPWSalt(this.transaction, this.getLoginUser()
                        .getUsername());
            String passwordHash = PasswordHash.hash(this.loginPassword, salt);
            // Überprüfen, ob Benutzername und Passwort gültig sind passwordHash
            // oder this.loginPassword
            id = UserDAO.proveLogin(this.transaction, this.getLoginUser()
                    .getUsername(), passwordHash);

            // Methode proveLogin gibt -1 zurück, wenn der Benutzername oder das
            // Passwort falsch sind
            // -2 wird zurückgegeben, wenn das Benutzerkonto noch nicht
            // aktiviert
            // wurde.
            // Ansonsten wird die ID des Benutzers zurückgegeben
            if (id == usernameOrPasswordWrong) {
                // Fehlermeldung in den FacesContext werfen.
                FacesMessageCreator.createFacesMessage(null,
                        "Benutzername oder Passwort falsch!");

                // Wieder auf die Loginseite leiten, da der Login-Vorgang ja
                // fehlgeschlagen ist.
                this.transaction.rollback();
                return "/facelets/open/authenticate.xhtml?faces-redirect=false";
            } else if (id == accountNotActivated) {
                // Fehlermeldung in den FacesContext werfen.
                FacesMessageCreator.createFacesMessage(null,
                        "Benutzerkonto nicht aktiv!");

                // Wieder auf die Loginseite leiten, da der Login-Vorgang ja
                // fehlgeschlagen ist.
                this.transaction.rollback();
                return "/facelets/open/authenticate.xhtml?faces-redirect=false";
            } else if (id == dbErrorOccured) {
                // Fehlermeldung in den FacesContext werfen.
                FacesMessageCreator.createFacesMessage(null,
                        "Benutzerkonto nicht aktiv!");

                // Wieder auf die Loginseite leiten, da der Login-Vorgang ja
                // fehlgeschlagen ist.
                this.transaction.rollback();
                return "/facelets/open/authenticate.xhtml?faces-redirect=false";
            } else {
                // Sessionobjekt mit Benutzerdaten füllen, noch nicht vorhandene
                // Daten mittels Benutzerid von der Datenbank abfragen.
                sessionUser.setLanguage(Language.DE);
                sessionUser.setUserID(id);
                sessionUser.setUserRole(UserDAO.getUserRole(this.transaction,
                        id));
                sessionUser.setUserStatus(UserDAO.getUserStatus(
                        this.transaction, id));

                // HTTP-Session mit Benutzerdaten füllen für PhaseListener
                HttpSession session = (HttpSession) FacesContext
                        .getCurrentInstance().getExternalContext()
                        .getSession(true);
                session.setAttribute("loggedin", true);
                session.setAttribute("userID", id);
                session.setAttribute("userRole", sessionUser.getUserRole());

                // Auf myCourses Seite weiterleiten, da der Login-Vorgang
                // erfolgreich war.
                this.transaction.commit();
                return "/facelets/user/registeredUser/myCourses.xhtml?faces-redirect=true";
            }

        } catch (InvalidDBTransferException e) {
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