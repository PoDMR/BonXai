/*
 * Created on Jun 27, 2005
 * Modified on $Date: 2009-10-29 13:35:07 $
 */
package gjb.flt.treegrammar;

/**
 * <p> Exception denoting that a UserObject was not previously defined. </p>
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class UserObjectNotDefinedException extends Exception {

    /**
     * for serialization
     */
    private static final long serialVersionUID = 1L;
    protected String name;

    /**
     * Constructor with a message
     * @param message String
     */
   public UserObjectNotDefinedException(String name) {
        this.name = name;
    }

   /**
    * Method to retrieve the exception's message
    * @return String message
    */
    public String getMessage() {
        return "user object '" + name + "' not defined";
    }

}
