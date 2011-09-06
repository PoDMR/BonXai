/*
 * Created on Aug 28, 2008
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.regex.random;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class MutatorCreationException extends Exception {

    private static final long serialVersionUID = -3694378059242692427L;
    protected Exception exception;

    public MutatorCreationException(Exception e) {
        this.exception = e;
    }

    public Exception getException() {
        return exception;
    }

}
