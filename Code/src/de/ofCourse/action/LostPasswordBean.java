/**
 * This package represents the business logic of the ofCourse system.
 */
package de.ofCourse.action;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import de.ofCourse.databaseDAO.UserDAO;
import de.ofCourse.exception.InvalidDBTransferException;
import de.ofCourse.model.User;
import de.ofCourse.system.Connection;
import de.ofCourse.system.LogHandler;
import de.ofCourse.system.Transaction;
import de.ofCourse.utilities.PasswordHash;

import java.util.Random;

/**
 * Provides the functionality to reset a lost password by entering a e-mail
 * address, generating automatically a new one and sending this new generated
 * password to the entered email address.
 * <p>
 * The old password is reseted, a new one generated and sent to the email address.
 * <p>
 * This class is ManagedBean of the facelet <code>authenticate</code>.
 * 
 * @author Ricky Strohmeier
 */
@ManagedBean
@RequestScoped
public class LostPasswordBean {

	private static final String BIGSIGNS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String SMALLSIGNS = "abcdefghijklmnopqrstuvwxyz";
	private static final String NUMBERS = "0123456789";
	private static final String SPECIAL = "!@#$%^&*_=+-/";
	private static final int passwordLength = 8;

    @ManagedProperty("#{mailBean}")
    private MailBean mailBean;

    /**
     * Stores the entered e-mail address to which the new password should be
     * sent.
     */
    private String email;
    
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
     * Resets the password of the user who forgot his password and sends a new
     * generated password to an entered email address.<br>
     * The new generated password is only sent to the entered email address if
     * the address exists in the system. The new password is stored in the
     * database.
     */
    public void resetPassword() {
        transaction = Connection.create();
        transaction.start();
        
        User userWhoLostPW = UserDAO.getUserPerMail(transaction, email);
        String salt = UserDAO.getPWSalt(transaction, userWhoLostPW.getUsername() );
        String newPassword = generateNewPassword();
    	String newHashedPassword = PasswordHash.hash(newPassword, salt);

    	try {
    	    UserDAO.overridePassword(transaction, email, newHashedPassword);
    	} catch (InvalidDBTransferException e) {
    	    transaction.rollback();
    	    LogHandler.getInstance().error("Error occured during resetPassword in LostPasswordBean");
    	}
        mailBean.sendMailForLostPassword(newPassword, email);
    	transaction.commit();
    }

    /**
     * Returns the value of the attribute
     * <code>emailAddressToResetPassword</code>.
     * 
     * @return the entered email address of the user who forgot his password
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the attribute <code>emailAddressToResetPassword</code>.
     * 
     * @param emailToResetPassword
     *            the new entered email address of the user who forgot his
     *            password
     */
    public void setEmail(String email) {
        this.email = email;
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

    private String generateNewPassword() {
    	String newPassword;
    	Random random = new Random();
    	int length = random.nextInt(1) + passwordLength;
    	char[] password = new char[length];
    	int iterator = 0;

    	for (int i = 0; i < 2; i++){
    		iterator = getNextPosition(random, length, password);
    		password[iterator] = BIGSIGNS.charAt(random.nextInt(BIGSIGNS.length()));
	    }
		for (int i = 0; i < 2; i++) {
			iterator = getNextPosition(random, length, password);
			password[iterator] = NUMBERS.charAt(random.nextInt(NUMBERS.length()));
		}
		for (int i = 0; i < 2; i++) {
			iterator = getNextPosition(random, length, password);
			password[iterator] = SPECIAL.charAt(random.nextInt(SPECIAL.length()));
		}
		for(int i = 0; i < length; i++) {
			if(password[i] == 0) {
				password[i] = SMALLSIGNS.charAt(random.nextInt(SMALLSIGNS.length()));
			}
		}
		newPassword = new String(password);
    	return newPassword;
    }

    private int getNextPosition(Random random, int length, char[] password) {
    	int iterator = random.nextInt(length);

    	while(password[iterator = random.nextInt(length)] != 0);
    	return iterator;
    }

    public MailBean getMailBean() {
        return mailBean;
    }
    
    public void setMailBean(MailBean mailBean) {
        this.mailBean = mailBean;
    }
}
