/*
 * Created on Jun 22, 2005
 * Modified on $Date: 2009-11-04 09:00:06 $
 */
package eu.fox7.flt.treegrammar;


/**
 * <p> Class representing an XML attribute in a model.  It will generate the
 * attribute's XML representation as well as the appropriate value.  The
 * XMLAttribute can be optional, and if so, its occurence is governed by a
 * ProbabilityDistribution.  If the latter's getNext() returns 1 the attribute
 * is added, in all other cases it is omitted. </p>
 * @author eu.fox7
 * @version $Revision: 1.2 $
 * 
 */
public class XMLAttributeDefinition {

    /**
     * The attribute's qualifieed name
     */
    protected String qName;
    /**
     * The attribute's type that determines the DataGenerator to use for its value
     */
    protected String type;
    /**
     * boolean flag to indicate that the attribute is optional
     */
    protected boolean isOptional;

    /**
     * Constructor for the XMLAttribute
     * @param doc
     *            XMLDocument the attribute is part of
     * @param qName
     *            String attribute's qualified name
     * @param type
     *            String attribute's type to determine the generator
     * @param isOptional
     *            boolean, true if the attribute is optional, false otherwise
     * @param distrName
     *            String name of the distribution to use if the attribute is optional
     * @throws UserObjectNotDefinedException
     *            thrown if the generator or the distribution is undefined
     */
    public XMLAttributeDefinition(XMLGrammar doc, String qName, String type,
                                  boolean isOptional)
            throws UserObjectNotDefinedException {
        this.qName = qName;
        this.type = type;
        this.isOptional = isOptional;
    }

    /**
     * Method to retrieve the attribute's qname
     * @return String attribute's qualified name
     */
    public String getQName() {
        return qName;
    }

    /**
     * Method to retrieve the attribute's type
     * @return String representing the attribute's type
     */
    public String getType() {
        return type;
    }

    /**
     * Method to check whether the attribute is optional
     * @return boolean true if the attribute is optional, false otherwise
     */
    public boolean isOptional() {
        return isOptional;
    }

	/**
     * Method to compute the string representation of the XMLAttribute for debugging
     * purposes.
     */
    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append(XMLGrammar.computeAttributeName(getQName(), getType()));
        if (isOptional()) {
            str.append("?");
        }
        return str.toString();
    }

}
