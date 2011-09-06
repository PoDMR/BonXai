/*
 * Created on Jun 21, 2005
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package eu.fox7.math;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class IllDefinedDistributionException extends Exception {

    private static final long serialVersionUID = 1L;
    protected Exception exception;
    protected String messange;

    public IllDefinedDistributionException(Exception e) {
        this();
        this.exception = e;
    }

    public IllDefinedDistributionException() {
        super();
    }

    public IllDefinedDistributionException(String message) {
        this();
        this.messange = message;
    }

    public Exception getException() {
        return exception;
    }

    public String getMessange() {
        return messange;
    }

}
