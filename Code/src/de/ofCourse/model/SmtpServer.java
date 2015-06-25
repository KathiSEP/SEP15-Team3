/**
 * This package represents all the models which are used
 */
package de.ofCourse.model;

/**
 * Contains all Information for the SMTP Server which is needed for
 * the E-Mail dispatch
 * 
 * @author Ricky Strohmeier
 *
 */
public class SmtpServer {

    /**
     * Stores the Hostaddress to the E-Mail Server
     * 
     */
    private String hostaddr;
    /**
     * Stores the password for the Login on the E-Mail Server
     * 
     */
    private String password;
    /**
     * Stores the portnumber which is needed for the connection with the E-Mail
     * Server
     * 
     */
    private int port;
    /**
     *  Stores the username for the Login on the E-Mail Server
     * 
     */
    private String username;
    
    /**
     *  Stores whether the connection to the server runs with ssl protection or not
     */
    private boolean ssl;

    /**
     * Returns the value of the attribute <code>ssl</code>.
     * 
     * @return if the connection runs with ssl protection
     */
    public boolean isSsl() {
        return ssl;
    }

    /**
     * Sets the value of the attribute <code>ssl</code>.
     * 
     * @param ssl 
     *          new boolean to set
     */
    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    /**
     * Returns the value of the attribute <code>hostaddr</code>.
     * 
     * @return the address of the E-mailhost
     */
    public String getHostaddr() {
        return hostaddr;
    }

    /**
     * Sets the value of the attribute <code>hostaddr</code>.
     * 
     * @param hostaddr
     *            the hostaddr to set
     */
    public void setHostaddr(String hostaddr) {
        this.hostaddr = hostaddr;
    }

    /**
     * Returns the value of the attribute <code>password</code>.
     * 
     * @return the password of the user
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the attribute <code>password</code>.
     * 
     * @param password
     *            the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the value of the attribute <code>address</code>.
     * 
     * @return the port from the E-mailhost.
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the value of the attribute <code>port</code>.
     * 
     * @param port
     *            the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Returns the value of the attribute <code>username</code>.
     * 
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the attribute <code>username</code>.
     * 
     * @param username
     *            the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

}
