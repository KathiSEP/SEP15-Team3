/**
 * This package provides utility functionality for the ofCourse system.
 */
package de.ofCourse.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.faces.context.FacesContext;

import de.ofCourse.model.Language;
import de.ofCourse.system.LogHandler;

/**
 * Represents a LanguageManager that handles the available languages and is
 * responsible to read out the language properties.<br>
 * Uses specified parameters in faces-config.xml and uses the property files
 * language_de.properties and language_en.properties under the path
 * <ServletContext-Path>/languages/
 * 
 * @author Patrick Cretu
 *
 */
public class LanguageManager {
	
	/**
     * Path of the general configuration file
     */
	private final String LANGUAGEPATH = "/WEB-INF/config/";
	
	/**
     * The general configuration property file to read from
     */
	private Properties languageProperty;
	
	/**
     * Indicates whether or not the configuration property was correctly loaded
     */
	private boolean propertyRead;

    /**
     * Singleton-object of the LanguageManager class
     */
    private static LanguageManager languageManager;

    /**
     * The supported languages
     */
    private Map<String, Object> availableLanguages;

    /**
     * Returns an instance of the LanguageManager class.
     * 
     * @return instance of the LanguageManager
     */
    public static LanguageManager getInstance() {
    	if (languageManager == null) {
    	    languageManager = new LanguageManager();
    	}
    	return languageManager;
    }

    /**
     * Returns a property value to a given key.
     * 
     * @param key
     *            the key to determine the property value
     * 
     * @return the value of the found property
     */
    public String getProperty(String key, Language language) {
    	String readValue = null;
    	languageProperty = loadLanguageProperty(language);

    	if (propertyRead && !languageProperty.equals(null)) {
    	    readValue = languageProperty.getProperty(key).toString();
    	}
    	return readValue;
    }
    
    /**
     * Returns the loaded language configuration property. <br>
     * Tries to load the requested property file which contains the language
     * configuration information from the <code>LANGUAGEPATH</code>.
     * 
     * @return the loaded property, if the loading was successful<br>
     *         null, otherwise
     */
    private Properties loadLanguageProperty(Language language) {
    	Properties property = new Properties();
    	InputStream readInput = null;
    
    	try {
    	    readInput = FacesContext
    		    .getCurrentInstance()
    		    .getExternalContext()
    		    .getResourceAsStream(
    			    LANGUAGEPATH + getLanguageFile(language));
    	    property.load(readInput);
    	    propertyRead = true;
    	} catch (IOException e) {
    	    propertyRead = false;
    	    LogHandler.getInstance().error(
    		    "Error during loading the config property.");
    	} finally {
    	    if (readInput != null) {
	    		try {
	    		    readInput.close();
	    		} catch (IOException e) {
	    		    LogHandler.getInstance().fatal(
	    			    "Error occured during"
	    				    + " loading language property.");
	    		}
    	    }
    	}
    	return property;
    }

    /**
     * Returns the requested property name as a string value
     * 
     * @param language
     *               the selected language
     * @return the requested property name
     */
    private String getLanguageFile(Language language) {
		if (language == Language.EN) {
			return "language_en.properties";
		} else if (language == Language.BAY) {
		    return "language_bay.properties";
		}
		return "language_de.properties";
	}
    
}
