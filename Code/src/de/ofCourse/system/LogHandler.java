package de.ofCourse.system;

import java.io.IOException;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import de.ofCourse.utilities.PropertyManager;

/**
 * This class handels Log4j-Logger and initialize it. This class provides a getInstance Method
 * and is for all classes reachable.
 * 
 * @author Sebastian Schwarz
 *
 */
public class LogHandler {
    /**
     * Sets the name
     */
    private static final Logger logger = LogManager.getLogger("OfCourse");

    
    /**
     * 
     */
    private static LogHandler LogHandler;

    /**
     * Constructor, can throw IO Exceptions if something wrong with the input values which will
     * be readed by the Property Manager 
     * 
     * @throws IOException
     */
    LogHandler() {
        
        try{
            logSetup(); 
        } catch (IOException e){
            //TODO how do we handle The exception here Syso/Printstack?
        }
        
        
    }
    

    /**
     * Checks whether a Instance of LogHandler already exists and returns the instance
     * 
     * @return
     */
    public static LogHandler getInstance() {
        if (LogHandler == null) {
            LogHandler = new LogHandler();
        }
        return LogHandler;
    }

    /**
     * Sets up the path and the logging level. The Values will be readed out of the logging.properties file
     * 
     * @throws IOException
     */
    private void logSetup() throws IOException {
        
        //Provies the Pattern with which the Loging will write his messages
        PatternLayout layout = new PatternLayout("%-5p [%t]: %m%n");
        
        //
        DailyRollingFileAppender fileAppender = new DailyRollingFileAppender(
                layout, PropertyManager.getInstance()
                .getPropertyLogger("logfilepath")+ "/MyLog" , "'.'yyyy-MM-dd_HH-mm");
        
        System.out.println(PropertyManager.getInstance().getPropertyLogger("logfilepath")+ "/MyLog");
        logger.addAppender(fileAppender);
        
        // Sets the level input from properties
        
        logger.setLevel(Level.toLevel(PropertyManager.getInstance()
                .getPropertyLogger("loglvl")));
        // TODO Error Handling
    }

    /**
     * This methode will be used for loging fatal errors, System has crashed
     * @param log
     */
    public void fatal(String log) {
        logger.fatal(log);
    }

    /**
     * This methode will be used for loging Erros, System will still run
     * @param log
     */
    public void error(String log) {
        logger.error(log);
    }

    /**
     * This Methode will be used for Loging if a methode runs with out a failure
     * @param log
     */
    public void debug(String log) {
        logger.debug(log);
    }
}
