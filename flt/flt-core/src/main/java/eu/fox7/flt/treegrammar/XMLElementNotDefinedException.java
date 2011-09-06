/*
 * Created on Jun 27, 2005
 * Modified on $Date: 2009-10-29 13:35:07 $
 */
package eu.fox7.flt.treegrammar;

/**
 * <p> Exception denoting that an XMLElement was not previously defined. </p>
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class XMLElementNotDefinedException extends Exception {

    /**
     * for serialization
     */
    private static final long serialVersionUID = 1L;
    protected String elementName;

    /**
     * Constructor with a message
     * @param message String
     */
    public XMLElementNotDefinedException(String elementName) {
        this.elementName = elementName;
    }

    /**
     * Method to retrieve the exception's message
     * @return String message
     */
    public String getMessage() {
        return "element '" + elementName + "' not defined";
    }
    
}
