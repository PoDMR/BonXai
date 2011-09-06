package eu.fox7.bonxai.tools;

/**
 * Status logger for information, warnings and error-messages
 * @author Lars Schmidt
 */
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Constructor of class StatusLogger
 * @author Lars Schmidt
 */
public class StatusLogger {

    // Static Logger object that is used by all methods.
    public static final Logger logger = Logger.getLogger("BonxaiLog");

    // Initialize a FileHandler as message handler and set the default log level
    // and format
    static {
        try {
            FileHandler filehandler = new FileHandler("bonxai.log", true);
            //filehandler.setFormatter(new XMLFormatter());
            StatusFormatter statusFormatter = new StatusFormatter();
            filehandler.setFormatter(statusFormatter);
            logger.addHandler(filehandler);
            logger.setLevel(Level.ALL);
            logger.setUseParentHandlers(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Log an info-message.
     * This is a normal status message and should describe the current task
     * @param location      Location of the caller: "XSD2RNG", ...
     * @param msg           Message content
     */
    public static void logInfo(String location, String msg) {
        logger.log(Level.INFO, "{0}: {1}", new String[]{location, msg});
    }

    /**
     * Log a warning-message.
     * This is a warning message and should warn the user about something that
     * happened in a component (approximation or something)
     * @param location      Location of the caller: "XSD2RNG", ...
     * @param msg           Message content
     */
    public static void logWarning(String location, String msg) {
        logger.log(Level.WARNING, "{0}: {1}", new String[]{location, msg});
    }

    /**
     * Log an error-message.
     * This is an error status message and should describe the found problem
     * @param location      Location of the caller: "XSD2RNG", ...
     * @param msg           Message content
     */
    public static void logError(String location, String msg) {
        logger.log(Level.SEVERE, "{0}: {1}", new String[]{location, msg});
    }

    /**
     * Log the last info-message.
     * This is a normal status message and should describe the current task.
     * The difference to the logInfo-method is, that a linebreak is added after
     * the message content.
     * @param location      Location of the caller: "XSD2RNG", ...
     * @param msg           Message content
     */
    public static void logLastInfoMessage(String location, String msg) {
        logger.log(Level.INFO, "{0}: {1}\n", new String[]{location, msg});
    }
}
