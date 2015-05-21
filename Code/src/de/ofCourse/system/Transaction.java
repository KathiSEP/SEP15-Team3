/**
 * This package represents system functionality. 
 */
package de.ofCourse.system;

/**
 * Handles the transaction management to prevent any mistakes with the database communication.
 * 
 * @author Sebastian
 */
public interface Transaction {
    
    /**
     * Stores a instance of the wrapper class Connection
     */
    public Connection conn = null;
    
    /**
     * Starts a new transaction and establishes the connection.
     */
    public void start();
    
    /**
     * Makes all changes made since the previous commit/rollback permanent and releases any database locks
     * and the connection.
     */
    public void commit(); 
    /**
     * Undoes all changes made in the current transaction and releases any database locks and the connection.
     */
    public void rollback();

}
