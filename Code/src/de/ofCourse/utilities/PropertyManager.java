/**
 * This package provides utility functionality for the ofCourse system.
 */
package de.ofCourse.utilities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.faces.context.FacesContext;

import de.ofCourse.system.LogHandler;

/**
 * Provides the functionality to read out a property file, which contains system
 * configuration data. The config-file have to exist under the
 * <ServletContext-Path>/config/ and is called ofCourse.properties.
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
    private static String CONFIGFILENAME = "ofCourse.properties";

    /**
     * Path of the general configuration file
     */
    private static String CONFIGFILEPATH = "/WEB-INF/config/";

    /**
     * Indicates whether the general configuration property was correctly loaded
     */
    private static boolean configFileLoaded = false;

    /**
     * The general configuration property file to read from
     */
    private static Properties configProperty = null;

    /**
     * Name of the logging configuration file
     */
    private static String LOGGINGFILENAME = "logging.properties";

    /**
     * Path of the logging configuration file
     */
    private static String LOGGINGFILEPATH = "/WEB-INF/config/";

    /**
     * Indicates whether the logging configuration property was correctly loaded
     */
    private static boolean loggingConfigFileLoaded = false;

    /**
     * The logging configuration property file to read from
     */
    private static Properties loggingConfigProperty = null;

    /**
     * Name of the mail configuration file
     */
    private static String EMAILFILENAME = "mail.properties";

    /**
     * Path of the mail configuration file
     */
    private static String EMAILFILEPATH = "/WEB-INF/config/";

    /**
     * Indicates whether the mail configuration property was correctly loaded
     */
    private static boolean mailConfigFileLoaded = false;

    /**
     * The mail configuration property file to read from
     */
    private static Properties mailConfigProperty = null;

    /**
     * Default constructor for a new PropertyManager object.
     */
    private PropertyManager() {
    }

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
     * Returns a property value to a given key from the configuration file.
     * 
     * @param key
     *            the key to determine the property value
     * 
     * @return the value of the found property
     */
    public String getPropertyConfig(String key) {
	String readValue = null;
	configProperty = loadConfigProperty();

	if (configFileLoaded && !configProperty.equals(null)) {
	    readValue = configProperty.getProperty(key).toString();
	}
	return readValue;
    }

    /**
     * Returns a property value to a given key from the logging configuration
     * file.
     * 
     * @param key
     *            the key to determine the property value
     * 
     * @return the value of the found property
     */
    public String getPropertyLogger(String key) {
	String readValue = null;
	loggingConfigProperty = loadLoggingConfigProperty();

	if (loggingConfigFileLoaded && !loggingConfigProperty.equals(null)) {
	    readValue = loggingConfigProperty.getProperty(key).toString();
	}
	return readValue;
    }

    /**
     * Returns a property value to a given key from the mail configuration file.
     * 
     * @param key
     *            the key to determine the property value
     * 
     * @return the value of the found property
     */
    public String getPropertyMail(String key) {
	String readValue = null;
	mailConfigProperty = loadMailConfigProperty();

	if (mailConfigFileLoaded && !mailConfigProperty.equals(null)) {
	    readValue = mailConfigProperty.getProperty(key).toString();
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
    private static Properties loadConfigProperty() {
	Properties property = new Properties();

	try {
	    property.load(FacesContext.getCurrentInstance()
		    .getExternalContext()
		    .getResourceAsStream(CONFIGFILEPATH + CONFIGFILENAME));
	    configFileLoaded = true;
	} catch (FileNotFoundException e) {
	    configFileLoaded = false;
	    LogHandler.getInstance().error("Error during loading the config property. Config poperty not found.");
	} catch (IOException e) {
	    configFileLoaded = false;
	    LogHandler.getInstance().error("Error during loading the config property.");
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

	try {
	    property.load(FacesContext.getCurrentInstance()
		    .getExternalContext()
		    .getResourceAsStream(LOGGINGFILEPATH + LOGGINGFILENAME));
	    loggingConfigFileLoaded = true;
	} catch (FileNotFoundException e) {
	    loggingConfigFileLoaded = false;
	    LogHandler.getInstance().error("Error during loading the logging property. Logging poperty not found.");
	} catch (IOException e) {
	    loggingConfigFileLoaded = false;
	    LogHandler.getInstance().error("Error during loading the logging property.");
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

	try {
	    property.load(FacesContext.getCurrentInstance()
		    .getExternalContext()
		    .getResourceAsStream(EMAILFILEPATH + EMAILFILENAME));
	    mailConfigFileLoaded = true;
	} catch (FileNotFoundException e) {
	    mailConfigFileLoaded = false;
	    LogHandler.getInstance().error("Error during loading the mail property. Mail poperty not found.");
	} catch (IOException e) {
	    mailConfigFileLoaded = false;
	    LogHandler.getInstance().error("Error during loading the mail property.");
	}
	return property;
    }
}