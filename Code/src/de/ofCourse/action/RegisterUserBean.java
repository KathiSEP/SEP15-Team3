
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
    
    private boolean testing = false;
    
    /**
     * Represents a user object. It stores the data which is entered by the user
     * who wants to register.
     */
    private User userToRegistrate;
    
    private String saluString;
    
    /**
     * @param userToRegistrate the userToRegistrate to set
     */
    public void setUserToRegistrate(User userToRegistrate) {
        this.userToRegistrate = userToRegistrate;
    }
    /**
     * Sets a flag so that the database query won't be executed
     */
    public void activateTesting() {
	this.testing = true;
    }

    private String registerPassword;
    
    private String registerConfirmPassword;

    /**
     * This ManagedProperty represents the actual session of a user. It stores
     * the id, the userRole, the userStatus of the user and the selected
     * language.
     */
    @ManagedProperty("#{sessionUser}")
    private SessionUserBean sessionUser;
    
    @ManagedProperty("#{mailBean}")
    private MailBean mailBean;
    
    /**
     * @return the mail
     */
    public MailBean getMailBean() {
        return mailBean;
    }

    /**
     * @param mail the mail to set
     */
    public void setMailBean(MailBean mailBean) {
        this.mailBean = mailBean;
    }

    @PostConstruct
    private void init(){
	 HttpServletRequest request = (HttpServletRequest) FacesContext
	      .getCurrentInstance().getExternalContext().getRequest();
	 String veriString = request.getParameter("veri");
	 
	 mailBean = new MailBean();
		
	 if(veriString != null && veriString.length() > 0) {
	     this.transaction = new Connection();
	     transaction.start();
	     if(UserDAO.verifyUser(this.transaction, veriString)) {
		// Erfolgsmeldung in den FacesContext werfen.
                 FacesContext facesContext = FacesContext.getCurrentInstance();
                 FacesMessage msg = new FacesMessage("Ihr Konto wurde aktiviert.");
                 msg.setSeverity(FacesMessage.SEVERITY_INFO);
                 facesContext.addMessage(null, msg);
                 facesContext.renderResponse();
	     } else {
		// Fehlermeldung in den FacesContext werfen.
                 FacesContext facesContext = FacesContext.getCurrentInstance();
                 FacesMessage msg = new FacesMessage("Bitte überprüfen Sie den Aktivierungslink.");
                 msg.setSeverity(FacesMessage.SEVERITY_INFO);
                 facesContext.addMessage(null, msg);
                 facesContext.renderResponse();
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
     * username is already in use, a error message is displayed.
     */
    public String registerUser() {
	
	if(this.saluString.equals("mr.")) {
	    this.getUserToRegistrate().setSalutation(Salutation.MR);
	} else if(this.saluString.equals("ms.")) {
	    this.getUserToRegistrate().setSalutation(Salutation.MS);
	} else {
	    this.getUserToRegistrate().setSalutation(null);
	}
	
	String veriString = "";
	
	// Eingegebenes Passwort hashen
	String salt = "";
	String passwordHash = PasswordHash.hash(this.getRegisterPassword(), salt);
	
	if(testing) {
	    return "/facelets/open/index.xhtml?faces-redirect=false";
	} else {
	// Datenbankverbindung initialisieren
	this.transaction = new Connection();
	transaction.start();
	try {
            	// Überprüfen, ob die eingegebene E-Mail-Adresse im System bereits existiert.
            	if(UserDAO.emailExists(transaction, this.getUserToRegistrate().getEmail())) {
            	    
            	    // Fehlermeldung in den FacesContext werfen, wenn die Mail schon existiert.
                        FacesContext facesContext = FacesContext.getCurrentInstance();
                        FacesMessage msg = new FacesMessage("E-Mail existiert bereits!");
                        msg.setSeverity(FacesMessage.SEVERITY_INFO);
                        facesContext.addMessage(null, msg);
                        facesContext.renderResponse();
                        
                        this.transaction.rollback();
                        return "/facelets/open/authenticate.xhtml?faces-redirect=false";
            	} else {	
            	    
            	    // Gibt es die angegebene E-Mail-Adresse noch nicht, erstelle einen
            	    // neuen Benutzer.
            	    veriString = UserDAO.createUser(this.transaction, this.getUserToRegistrate(), passwordHash);
            
            	    int userID = UserDAO.getUserID(this.transaction, this.getUserToRegistrate().getUsername());
                    
            	               	    
            	    this.transaction.commit(); 
            	    mailBean.sendAuthentificationMessage(userID, veriString);
            	    
            	}
	} catch (InvalidDBTransferException e) {
	    this.transaction.rollback();
	}
	}
	
	
	// Erfolgsmeldung in den FacesContext werfen.
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage msg = new FacesMessage("Sie haben sich erfolgreich im System registriert. Bitte bestätigen Sie den Aktivierungslink aus der Verifizierungsmail");
        msg.setSeverity(FacesMessage.SEVERITY_INFO);
        facesContext.addMessage(null, msg);
        facesContext.renderResponse();
	return "/facelets/open/index.xhtml?faces-redirect=false";
	
	
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
     * 			inserted password
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
     * @param registerConfirmPassword
     * 			inserted Password to confirm
     */
    public void setRegisterConfirmPassword(String registerConfirmPassword) {
        this.registerConfirmPassword = registerConfirmPassword;
    }

    /**
     * Returns the value of the attribute <code>userToRegistrate</code>.
     * @return the user to registrate
     */
    public User getUserToRegistrate() {
        return userToRegistrate;
    }

    /**
     * Returns the value of the attribute <code>SaluString</code>.
     * @return the verification string
     */
    public String getSaluString() {
	return saluString;
    }

    /**
     * Sets the value of the attribute <code>SaluString</code>.
     * @param saluString
     * 			String to verify
     */			
    public void setSaluString(String saluString) {
	this.saluString = saluString;
    }

}
