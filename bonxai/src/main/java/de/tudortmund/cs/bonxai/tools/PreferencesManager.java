package de.tudortmund.cs.bonxai.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.Properties;
import java.util.Enumeration;

/**
 * Manager for application preferences
 * Load settings from config-file and provide them in form of String, boolean
 * or int
 *
 * @author Lars Schmidt
 */
public class PreferencesManager {

    private Properties properties;
    private static PreferencesManager preferencesManager=null;

    /**
     * Constructor of class PreferencesManager
     */
    private PreferencesManager() {

        this.properties = new Properties();

        String filename = "bonxai.conf";

        File file = new File(filename);
        if (file.exists()) {
            try {
                this.properties.load(new FileInputStream(filename));
            } catch (Exception ex) {
            }
        }
    }
    
    /** 
     * get preferencesManager
     * 
     * @return PreferencesManager	preferences Manager
     */
    public static PreferencesManager getPreferencesManager() {
    	if (preferencesManager == null) {
    		preferencesManager = new PreferencesManager();
    	}
    	return preferencesManager;
    }
    
    public String[] getSettingNames() {
    	Enumeration<String> names = (Enumeration<String>) properties.propertyNames();
    	java.util.Vector<String> nameVector = new java.util.Vector<String>();
    	while (names.hasMoreElements()) {
    		nameVector.add(names.nextElement());
    	}
    	return nameVector.toArray(new String[]{});
    }
    
    public void showSettings(PrintStream out) {
    	properties.list(out);
    }

    /**
     * Fetch a setting according to the given setting name.
     * If the setting is not present in the file, the default value will be
     * returned.
     * @param settingName       name of the setting
     * @param defaultString     default value String
     * @return String           setting value
     */
    public String getSetting(String settingName, String defaultString) {
//        System.out.println(settingName + "=" + defaultString);
        String settingString = this.properties.getProperty(settingName);
        if (settingString != null) {
            return settingString;
        } else {
            return defaultString;
        }
    }

    /**
     * Fetch a setting according to the given setting name.
     * If the setting is not present in the file, the default value will be
     * returned.
     * @param settingName       name of the setting
     * @param defaultBoolean    default value boolean
     * @return boolean          setting value
     */
    public boolean getBooleanSetting(String settingName, boolean defaultBoolean) {
//        System.out.println(settingName + "=" + defaultBoolean);
        String settingBoolean = this.properties.getProperty(settingName);
        if (settingBoolean != null) {
            if (settingBoolean.toLowerCase().equals("true") || settingBoolean.equals("1") || settingBoolean.toLowerCase().equals("yes")) {
                return true;
            } else {
                return false;
            }
        } else {
            return defaultBoolean;
        }
    }

    /**
     * Fetch a setting according to the given setting name.
     * If the setting is not present in the file, the default value will be
     * returned.
     * @param settingName    name of the setting
     * @param defaultInt     default value int
     * @return int           setting value
     */
    public int getIntegerSetting(String settingName, int defaultInt) {
//        System.out.println(settingName + "=" + defaultInt);
        String settingInt = this.properties.getProperty(settingName);
        if (settingInt != null) {
            try {
                return Integer.parseInt(settingInt);
            } catch (NumberFormatException e) {
                return defaultInt;
            }
        } else {
            return defaultInt;
        }
    }
    
    /**
     * Set a setting.
     * @param settingName    name of the setting
     * @param settingValue   value of the setting
     */
    public void setSetting(String settingName, String settingValue) {
    	this.properties.setProperty(settingName, settingValue);
    }
    
}
