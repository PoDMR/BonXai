/*
 * Created on Mar 5, 2009
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package eu.fox7.math;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class InvalidBinDefinitionException extends Exception {

    private static final long serialVersionUID = -2782353659120824507L;
    protected String msg;

    public InvalidBinDefinitionException(String msg) {
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return msg;
    }

}
