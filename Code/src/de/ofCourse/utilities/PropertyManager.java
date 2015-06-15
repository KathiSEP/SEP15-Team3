/**
 * This package provides utility functionality for the ofCourse system.
 */
package de.ofCourse.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.faces.context.FacesContext;

import de.ofCourse.system.LogHandler;

/**
 * Provides the functionality to read out a property file, which contains system
 * configuration data.
 * 
 * @author Tobias Fuchs
 *
 */
public class PropertyManager {

    /**
     * Singleton-object of the PropertyManager class
     */
    private static PropertyManager propertyManager;

    /**
     * Name of the general configuration file
     */
    private final static String CONFIGURATIONFILE = "ofCourse.properties";

    /**
     * Path of the general configuration file
     */
    private final static String CONFIGURATIONPATH = "/WEB-INF/config/";

    /**
     * Indicates whether the general configuration property was correctly loaded
     */
    private static boolean configurationRead = false;

    /**
     * The general configuration property file to read from
     */
    private static Properties ofCourse = null;

    /**
     * Name of the logging configuration file
     */
    private final static String LOGGINGFILE = "logging.properties";

    /**
     * Path of the logging configuration file
     */
    private final static String LOGGINGPATH = "/WEB-INF/config/";

    /**
     * Indicates whether the logging configuration property was correctly loaded
     */
    private static boolean loggingRead = false;

    /**
     * The logging configuration property file to read from
     */
    private static Properties logging = null;

    /**
     * Name of the mail configuration file
     */
    private final static String EMAILFILE = "mail.properties";

    /**
     * Path of the mail configuration file
     */
    private final static String EMAILPATH = "/WEB-INF/config/";

    /**
     * Indicates whether the mail configuration property was correctly loaded
     */
    private static boolean mailRead = false;

    /**
     * The mail configuration property file to read from
     */
    private static Properties mail = null;

    /**
     * Returns an instance of the PropertyManager class.
     * 
     * @return instance of the PropertyManager
     */
    public static PropertyManager getInstance() {
	if (propertyManager == null) {
	    propertyManager = new PropertyManager();
	}
	return propertyManager;
    }

    /**
     * Returns a property value to a given line from the configuration file.
     * 
     * @param line
     *            the line to determine the property value
     * 
     * @return the value of the found property
     */
    public String getPropertyConfiguration(String line) {
	String readValue = null;
	ofCourse = loadConfigurationProperty();

	if (configurationRead && !ofCourse.equals(null)) {
	    readValue = ofCourse.getProperty(line).toString();
	}
	return readValue;
    }

    /**
     * Returns a property value to a given line from the logging configuration
     * file.
     * 
     * @param line
     *            the line to determine the property value
     * 
     * @return the value of the found property 
     */
    public String getPropertyLogger(String line) {
	String readValue = null;
	logging = loadLoggingConfigProperty();

	if (loggingRead && !logging.equals(null)) {
	    readValue = logging.getProperty(line).toString();
	}
	return readValue;
    }

    /**
     * Returns a property value to a given line from the mail configuration
     * file.
     * 
     * @param line
     *            the line to determine the property value
     * 
     * @return the value of the found property
     */
    public String getPropertyMail(String line) {
	String readValue = null;
	mail = loadMailConfigProperty();

	if (mailRead && !mail.equals(null)) {
	    readValue = mail.getProperty(line).toString();
	}
	return readValue;
    }

    /**
     * Returns the loaded general configuration property. <br>
     * Tries to load the property file with name <code>CONFIGFILENAME</code>
     * which contains the general configuration information from the
     * <code>CONFIGFILEPATH</code>.
     * 
     * @return the loaded property, if the loading was successful<br>
     *         null, otherwise
     */
    private static Properties loadConfigurationProperty() {
	Properties property = new Properties();
	InputStream readInput = null;

	try {
	    readInput = FacesContext
		    .getCurrentInstance()
		    .getExternalContext()
		    .getResourceAsStream(
			    CONFIGURATIONPATH + CONFIGURATIONFILE);
	    property.load(readInput);
	    configurationRead = true;
	} catch (IOException e) {
	    configurationRead = false;
	    LogHandler.getInstance().error(
		    "Error during loading the config property.");
	} finally {
	    if (readInput != null) {
		try {
		    readInput.close();
		} catch (IOException e) {
		    LogHandler.getInstance().fatal(
			    "Error occured during"
				    + " loading configuration property.");
		}
	    }
	}
	return property;
    }

    /**
     * Returns the loaded logging configuration property. <br>
     * Tries to load the property file with name <code>LOGGINGFILENAME</code>
     * that contains the logging configuration information from the
     * <code>LOGGINGFILEPATH</code>.
     * 
     * @return the loaded property, if the loading was successful<br>
     *         null, otherwise
     */
    private static Properties loadLoggingConfigProperty() {
	Properties property = new Properties();
	InputStream readInput = null;

	try {

	    readInput = FacesContext.getCurrentInstance().getExternalContext()
		    .getResourceAsStream(LOGGINGPATH + LOGGINGFILE);
	    property.load(readInput);

	    loggingRead = true;
	} catch (IOException e) {
	    loggingRead = false;
	    LogHandler.getInstance().error(
		    "Error during loading the logging property.");
	} finally {
	    if (readInput != null) {
		try {
		    readInput.close();
		} catch (IOException e) {
		    LogHandler.getInstance().fatal(
			    "Error occured during"
				    + " loading logging property.");
		}
	    }
	}
	return property;
    }

    /**
     * Returns the loaded mail configuration property. <br>
     * Tries to load the property file with name <code>EMAILFILENAME</code> that
     * contains the mail configuration information from the
     * <code>EMAILFILEPATH</code>.
     * 
     * @return the loaded property, if the loading was successful<br>
     *         null, otherwise
     */
    private static Properties loadMailConfigProperty() {
	Properties property = new Properties();
	InputStream readInput = null;
	try {
	    readInput = FacesContext.getCurrentInstance().getExternalContext()
		    .getResourceAsStream(EMAILPATH + EMAILFILE);
	    property.load(readInput);
	    mailRead = true;
	} catch (IOException e) {
	    mailRead = false;
	    LogHandler.getInstance().error(
		    "Error during loading the mail property.");
	} finally {
	    if (readInput != null) {
		try {
		    readInput.close();
		} catch (IOException e) {
		    LogHandler.getInstance().fatal(
			    "Error occured during" + " loading mail property.");
		}
	    }
	}
	return property;
    }

}