/**
 * This package represents system functionality.
 */
package de.ofCourse.system;

import java.util.Timer;
import java.util.TimerTask;

import de.ofCourse.databaseDAO.CourseDAO;
import de.ofCourse.exception.InvalidDBTransferException;

/**
 * Provides the maintenance service of the system which is responsible for
 * deleting courses which are out of date.<br>
 * 
 * <p>
 * The class Maintenance implements the interface <code>Runnable</code>.
 * 
 * @author Katharina H�lzl
 *
 */
public class Maintenance implements Runnable {

    /**
     * Singleton-object of the Maintenance class 
     */
    private static Maintenance maintenance;
    
    /**
     * Stores the period in which the method should be executed (24h)
     */
    private static final long timerPeriod = 82400000L;
    
    /**
     * Stores the timer
     */
    private Timer timer;

    /**
     * Represents whether the maintenance thread is running
     */
    private boolean maintenaceStopped;

    class MaintenanceTask extends TimerTask {
        
        private Transaction transaction;
        
        @Override
        public void run() {
            this.transaction = Connection.create();
            this.transaction.start();
            try {
                //Checks if the method do course maintenance was successful
                if(CourseDAO.doCourseMaintenance(this.transaction) == true) {
                    LogHandler
                    .getInstance()
                    .error("Maintenance erfolgreich ausgef�hrt!");
                } else {
                    LogHandler
                    .getInstance()
                    .error("Fehler!");
                }
                this.transaction.commit();
            } catch (InvalidDBTransferException e) {
                this.transaction.rollback();
            }
        }
        
    }
    
    /**
     * Returns <code>true</code> if the maintenance thread is already running.
     * 
     * @return <code>true</code> if the the maintenance thread is running;
     *         <code>false</code> otherwise
     */
    public synchronized boolean isMaintenaceStopped() {
	return this.maintenaceStopped;
    }

    /**
     * Stops the maintenance thread.
     */
    public synchronized void shutDown() {
        this.timer.cancel();
        this.maintenaceStopped = true;
    }

    /**
     * Returns an instance of the Maintenance class.
     * 
     * @return instance of the class Maintenance
     */
    public static Maintenance getInstance() {
        if (maintenance == null) {
            maintenance = new Maintenance();
        }

        return maintenance;
    }

    /**
     * Starts and controls the maintenance thread.<br>
     * Particularly it's used to tell the thread what it should do and how long
     * it should sleep.
     */
    @Override
    public void run() {
	this.maintenaceStopped = false;
	this.timer = new Timer();
        this.timer.schedule(new MaintenanceTask(), 0, timerPeriod);
    }

}
