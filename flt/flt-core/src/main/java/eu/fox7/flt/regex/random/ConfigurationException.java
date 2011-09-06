/*
 * Created on Feb 22, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.regex.random;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class ConfigurationException extends Exception {

    private static final long serialVersionUID = 1L;
    protected Exception exception;
    protected String message;

    public ConfigurationException(String msg) {
        this.message = msg;
    }

    public ConfigurationException(Exception e) {
        this.exception = e;
    }

    public Exception getException() {
        return exception;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
