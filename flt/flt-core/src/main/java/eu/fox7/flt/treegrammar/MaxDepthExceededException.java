/*
 * Created on Jun 30, 2005
 * Modified on $Date: 2009-10-29 13:35:07 $
 */
package eu.fox7.flt.treegrammar;

/**
 * <p> Exception denoting that during the example generation process the maximum
 * recursion depth has been exceeded. </p>
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class MaxDepthExceededException extends Exception {

    /**
     * for serialization
     */
    private static final long serialVersionUID = 1L;
    protected int depth;

    /**
     * Constructor with a message
     * @param depth int depth at which the violation was detected
     */
    public MaxDepthExceededException(int depth) {
        this.depth = depth;
    }

    /**
     * Method to retrieve the exception's message
     * @return String message
     */
    public String getMessage() {
        return "maximum recursion depth of " + depth + " has been exceeded";
    }

}
