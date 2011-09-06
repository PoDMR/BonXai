/*
 * Created on Aug 27, 2008
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.regex.random;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class NoMutationFoundException extends Exception {

    private static final long serialVersionUID = -4869397791765227320L;
    protected String msg;

    public NoMutationFoundException() {
        super();
    }

    public NoMutationFoundException(String msg) {
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return msg;
    }

}
