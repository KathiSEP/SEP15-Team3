package de.ofCourse.system;

import java.io.IOException;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import de.ofCourse.utilities.PropertyManager;

/**
 * @author blacky
 *
 */
public class LogHandler {
    /**
     * 
     */
    private static final Logger logger = LogManager.getLogger("OfCourse");

    private static LogHandler LogHandler;

    /**
     * @throws IOException
     */
    LogHandler() {
        try{
            logSetup(); 
        } catch (IOException e){
            
        }
        
        
    }

    public static LogHandler getInstance() {
        if (LogHandler == null) {
            LogHandler = new LogHandler();
        }
        return LogHandler;
    }

    /**
     * @throws IOException
     */
    private void logSetup() throws IOException {
        
        PatternLayout layout = new PatternLayout("%-5p [%t]: %m%n");
        DailyRollingFileAppender fileAppender = new DailyRollingFileAppender(
                layout, "C:/Temp/MyLog", "'.'yyyy-MM-dd_HH-mm");
        
        logger.addAppender(fileAppender);
        
        // Sets the level input from properties
        
        logger.setLevel(Level.toLevel(PropertyManager.getInstance()
                .getPropertyLogger("loglvl")));
        // TODO Error Handling
    }

    /**
     * @param log
     */
    public void fatal(String log) {
        logger.fatal(log);
    }

    public void error(String log) {
        logger.error(log);
    }

    public void debug(String log) {
        logger.debug(log);
    }
}
