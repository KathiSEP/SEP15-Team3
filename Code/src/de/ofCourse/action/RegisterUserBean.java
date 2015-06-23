/**
 * This package represents the business logic of the ofCourse system.
 */
package de.ofCourse.action;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import de.ofCourse.Database.dao.UserDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.Address;
import de.ofCourse.model.Salutation;
import de.ofCourse.model.User;
import de.ofCourse.system.Connection;
import de.ofCourse.system.Transaction;
import de.ofCourse.utilities.PasswordHash;

/**
 * Provides the functionality to register in the the system by entering the
 * required user data.<br>
 * After the registration a verification link is sent to the entered email
 * address to ensure that it is a valid address.
 * <p>
 * Whether the user can log on the system after verifying the entered email
 * address depends on the type of account activation determined by the
 * administrator.
 * 
 * <p>
 * This class is ManagedBean and controller of the facelet
 * <code>authenticate</code>.
 * 
 * @author Katharina Hölzl
 *
 */
@ManagedBean
@RequestScoped
public class RegisterUserBean {

    /**
     * Stores the transaction that is used for database interaction. 
     */
    private Transaction transaction;

    /**
     * Represents a user object. It stores the data which is entered by the user
     * who wants to register.
     */
    private User userToRegistrate;

    /**
     * The Salutation which was selected by the user.
     */
    private String saluString;

    /**
     * Sets the value of the attribute <code>userToRegistrate</code>.
     * 
     * @param userToRegistrate
     *            the userToRegistrate to set
     */
    public void setUserToRegistrate(User userToRegistrate) {
        this.userToRegistrate = userToRegistrate;
    }
    
    private boolean agbAccepted;

    /**
     * The password which was inserted by the user.
     */
    private String registerPassword;

    /**
     * The password which was inserted by the user to confirm the first 
     * inserted password.
     */
    private String registerConfirmPassword;

    /**
     * This ManagedProperty represents the actual session of a user. It stores
     * the id, the userRole, the userStatus of the user and the selected
     * language.
     */
    @ManagedProperty("#{sessionUser}")
    private SessionUserBean sessionUser;

    /**
     * This ManagedProperty represents the mail bean.
     */
    @ManagedProperty("#{mailBean}")
    private MailBean mailBean;

    /**
     * Returns the ManagedProperty <code>MailBean</code>.
     * 
     * @return the mail
     */
    public MailBean getMailBean() {
        return mailBean;
    }

    /**
     * Sets the ManagedProperty <code>MailBean</code>.
     * 
     * @param mail
     *            the mail to set
     */
    public void setMailBean(MailBean mailBean) {
        this.mailBean = mailBean;
    }

    /**
     * Checks if the URL of the index page contains the parameter 'veri'.
     */
    @PostConstruct
    private void init() {
        HttpServletRequest request = (HttpServletRequest) FacesContext
                .getCurrentInstance().getExternalContext().getRequest();
        String veriString = request.getParameter("veri");

        if (veriString != null && veriString.length() > 0) {
            this.transaction = Connection.create();
            transaction.start();
            if (UserDAO.verifyUser(this.transaction, veriString)) {
                FacesMessageCreator.createFacesMessage("verifizierungString", "Ihr Account wurde erfolgreich freigeschaltet!");
            } else {
                FacesMessageCreator.createFacesMessage("verifizierungString", "Der Verifizierungsstring existiert nicht!");
            }
            this.transaction.commit();
        }
        this.userToRegistrate = new User();
        this.userToRegistrate.setAddress(new Address());
    }
    
    /**
     * Registers a new user with the entered user data in the system.<br>
     * It creates with the entered data a new database entry and sends a mail
     * with a verification link to the entered email address which is used to
     * ensure that the entered email address really exists.<br>
     * If there goes something wrong during registration, e.g. the chosen
     * user name is already in use, a error message is displayed.
     */
    public String registerUser() {
        if(agbAccepted == true) {
            this.getUserToRegistrate().setSalutation(Salutation.fromString(this.saluString));
        
            String veriString = "";
            
        
            // Initialize database connection
            this.transaction = Connection.create();
            transaction.start();
            try {
                // Hash the inserted password.
                // Generate the salt.
                String salt = PasswordHash.getSalt();
                String passwordHash = PasswordHash.hash(this.getRegisterPassword(),
                        salt);
                // Check if the inserted mail already exists in the system.
                if (UserDAO.emailExists(transaction, this.getUserToRegistrate()
                        .getEmail())) {
        
                    // Throwing error message into the faces context if the 
                    // mail already exists.
                    FacesMessageCreator.createFacesMessage(null,
                            "E-Mail existiert bereits!");
        
                    this.transaction.rollback();
                    return "/facelets/open/authenticate.xhtml?faces-redirect=false";
                } else {
        
                    // If the inserted mail doesn't already exist, create a 
                    //new user.
                    veriString = UserDAO.createUser(this.transaction,
                            this.getUserToRegistrate(), passwordHash, salt);
        
                    int userID = UserDAO.getUserID(this.transaction, this
                            .getUserToRegistrate().getUsername());
        
                    this.transaction.commit();
        
                    mailBean.sendAuthentificationMessage(userID, veriString);
        
                }
            } catch (InvalidDBTransferException e) {
                this.transaction.rollback();
            }
        
            // Throwing success message into the faces context.
            FacesMessageCreator
                    .createFacesMessage(
                            null,
                            "Sie haben sich erfolgreich im System registriert. "
                            + "Bitte bestätigen Sie den Aktivierungslink aus der "
                            + "Verifizierungsmail!");
            return "/facelets/open/index.xhtml?faces-redirect=true";
        } else {
            FacesMessageCreator
            .createFacesMessage(
                    null,
                    "Bitte AGBs bestätigen!");
            return "/facelets/open/authenticate.xhtml?faces-redirect=false";
        }
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
    }

    /**
     * Returns the value of the attribute <code>registerPassword</code>.
     * 
     * @return the inserted password
     */
    public String getRegisterPassword() {
        return registerPassword;
    }

    /**
     * Sets the value of the attribute <code>registerPassword</code>.
     * 
     * @param registerPassword
     *            inserted password
     */
    public void setRegisterPassword(String registerPassword) {
        this.registerPassword = registerPassword;
    }

    /**
     * Returns the value of the attribute <code>registerCondirmPassword</code>.
     * 
     * @return the inserted confim password
     */
    public String getRegisterConfirmPassword() {
        return registerConfirmPassword;
    }

    /**
     * Sets the value of the attribute <code>registerConfirmPassword</code>.
     * 
     * @param registerConfirmPassword
     *            inserted Password to confirm
     */
    public void setRegisterConfirmPassword(String registerConfirmPassword) {
        this.registerConfirmPassword = registerConfirmPassword;
    }

    /**
     * Returns the value of the attribute <code>userToRegistrate</code>.
     * 
     * @return the user to registrate
     */
    public User getUserToRegistrate() {
        return userToRegistrate;
    }

    /**
     * Returns the value of the attribute <code>SaluString</code>.
     * 
     * @return the verification string
     */
    public String getSaluString() {
        return saluString;
    }

    /**
     * Sets the value of the attribute <code>SaluString</code>.
     * 
     * @param saluString
     *            String to verify
     */
    public void setSaluString(String saluString) {
        this.saluString = saluString;
    }

    /**
     * @return the agbAccepted
     */
    public boolean isAgbAccepted() {
        return agbAccepted;
    }

    /**
     * @param agbAccepted the agbAccepted to set
     */
    public void setAgbAccepted(boolean agbAccepted) {
        this.agbAccepted = agbAccepted;
    }

}
