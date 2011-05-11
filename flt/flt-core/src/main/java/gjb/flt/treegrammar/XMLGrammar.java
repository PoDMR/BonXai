/*
 * Created on Jun 20, 2005
 * Modified on $Date: 2009-11-03 15:48:34 $
 */
package gjb.flt.treegrammar;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * <p> XMLGrammar is a model for an XML schema that can generate instances.
 * The underlyinng data model is a regular tree automaton so it supports arbitrary
 * regular expressions for content models and typed elements.  Hence it is
 * equivalent to RNG and a proper superset of XSD (and DTDs).  Each of the
 * quantification operators as well as the choice operator can be decorated 
 * with a probability distribution to guide the generation process.  Default 
 * distributions are used in case none is specified, the uniform distribution 
 * for choice and zero/one, the exponential distribution for zero/more and 
 * one/more.  The root element of the documents should be specified as well. 
 * The depth of the document is governed by a probability distribution that can
 * be specified in the root clause of the grammar. </p>
 * <p> To generate the contents of text elements and attribute values, these
 * can be decorated with DataGenerators. </p>
 * <p> The model can be specified entirely via API call, but can also be
 * written as a grammar.  An example of the syntax is given here:
 * <pre>
 * root = a#root-type[depth]
 * a#root-type -> q#CDATA?; (|[skewed] (b#) (* (a#node-type)))
 * a#node-type -> ; (?[skewed] (. (a#node-type) (a#root-type)))
 * b# -> r#CDATA, s#CDATA?; (#PCDATA)
 * </pre>
 * Here e.g. 'a#node-type' indicates an element with qname 'a' and of type
 * 'node-type'.  The root of the documents to be generated is specified on 
 * the first line of the example: an 'a' element of type 'root-type'.  The 
 * 'depth' decoration indicates that a distribution named 'depth' governs the
 * depth of the generated documents.  A grammar rule consists of two parts: 
 * the attribute declaration and the content model.  The latter is an 
 * optionally decorated regular expression that is arbitrary, i.e. there is no 
 * ambiguity constraint.  The atoms of the regular expression are typed qnames,
 * the operators can be decorated with probability distributions. </p>
 * <p> See the Javadoc of gjb.xml.model.UserObject for the file format to
 * specify DataGenerators and ProbabilityDistributions. </p>
 * </p>
 * @author gjb
 * @version $Revision: 1.2 $
 * 
 */
public class XMLGrammar {

    /**
     * Default maximum depth of tenerated example documents
     */
    protected static final int MAX_EXAMPLE_DEPTH = 250;
    /**
     * Constants that determine the user objects and grammar files' format
     */
    protected static final String MAX_DEPTH_DEF = "max-depth";
    protected static final String DISTRIBUTION_ID = "distribution";
    protected static final String DISTRIBUTION_INTERFACE = "gjb.math.ProbabilityDistribution";
    protected static final String GENERATOR_ID = "generator";
    protected static final String GENERATOR_INTERFACE = "gjb.xml.model.data.DataGenerator";
    protected static final String DEFINITION_PATTERN = "^\\s*(?:" +
                                                       DISTRIBUTION_ID + "|" +
                                                       GENERATOR_ID + ")\\s*:.+$";
    protected static final String ATTR_CONTENT_MODEL_SEP_PATTERN = "\\s*;\\s*";
    protected static final String ATTR_CONTENT_MODEL_SEP = "; ";
    protected static final String ATTR_DEF_SEP_PATTERN = "\\s*,\\s*";
    protected static final String ATTR_DEF_SEP = ", ";
    public static final String QNAME_TYPE_SEPARATOR = "#";
    protected static final String LHS_RHS_RULE_SEP_PATTERN = "\\s*->\\s*";
    protected static final String LHS_RHS_RULE_SEP = " -> ";
    protected static final String ROOT_DEF_PATTERN = "^\\s*root\\s*=.*$";
    public static final String ROOT_DEF = "root = ";
    public static final String COMMENT_STRING = "#";
    protected static final String COMMENT_PATTERN = "^\\s*" + COMMENT_STRING + ".*$";
    protected static final String ATTR_DISTR_PATTERN = "^(\\w+)(?:(\\?)(?:\\[([^\\]]+)\\])?)?$";
    protected static Pattern attrDistrPattern = Pattern.compile(ATTR_DISTR_PATTERN);
    /**
     * Name of the document's root
     */
    protected String rootName;
    /**
     * Maps a name/type string to an XMLElement
     */
    protected Map<String,XMLElementDefinition> elementMap = new HashMap<String,XMLElementDefinition>();

    /**
     * Constructor that should only be called if the XMLDocument will be specified
     * via API calls.
     */
    public XMLGrammar() {
        super();
    }

    /**
     * Method to specify the qname and type of the element to serve as root for the
     * document
     * @param qName
     *            String qualified name
     * @param type
     *            String type
     */
    public void setRootElement(String qName, String type) {
        this.rootName = computeElementName(qName, type);
    }

    /**
     * Method to retrieve the XMLDocument's root XMLElement
     * @return XMLElement that is the root
     */
    public XMLElementDefinition getRootElement() {
        return elementMap.get(rootName);
    }

    /**
     * Method to add a grammar rule to the XMLDocument
     * @param qName
     *            String qualified name of the element to be defined
     * @param type
     *            String type of the element to be defined
     * @param attributes
     *            List of XMLAttributes for the element
     * @param contentModel
     *            String representation of the element's content model, a (possibly
     *            decorated) regular expression
     * @return 
     * @throws SyntaxException
     *            thrown when the content model string contains a syntax error
     */
    public XMLElementDefinition addGrammarRule(String qName, String type,
                                               List<XMLAttributeDefinition> attributes,
                                               String contentModel)
                throws SyntaxException {
            String elementName = computeElementName(qName, type);
            if (!elementMap.containsKey(elementName))
                try {
                    XMLElementDefinition xmlElementDefinition = new XMLElementDefinition(this, qName, type,
                                                                                         attributes, contentModel);
					elementMap.put(elementName, xmlElementDefinition);
					return xmlElementDefinition;
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new SyntaxException("syntax error in '" + contentModel + "'", e);
                }
            else
                throw new SyntaxException("a syntax rule for '" + elementName + "' was already specified");
    }

    /**
     * method that returns the number of grammar rules, i.e., the number of
     * elements, taking into account disparate types
     * @return int number of rules
     */
    public int getNumberOfGrammarRules() {
        return elementMap.size();
    }

    /**
     * method that returns an Iterator over the element names in de document
     * @return Iterator<String> over element names
     */
    public Iterator<String> getElementNameIterator() {
        return elementMap.keySet().iterator();
    }

    /**
     * Method to retrieve an XMLElement by its name (note: qname/type combination)
     * @param elementName
     *            String qname/type combination
     * @return XMLElement with the given name
     * @throws XMLElementNotDefinedException
     *             thrown when no XMLElement with this name was defined
     */
    public XMLElementDefinition getElement(String elementName)
            throws XMLElementNotDefinedException {
        if (elementMap.containsKey(elementName)) { 
            return elementMap.get(elementName);
        } else {
            throw new XMLElementNotDefinedException(elementName);
        }
    }

    /**
     * Convenience method to construct an XMLElement's unique name from its qname
     * and type
     * @param qName
     *            String qualified name
     * @param type
     *            String type
     * @return String
     */
    public static String computeElementName(String qName, String type) {
        return qName + QNAME_TYPE_SEPARATOR + type;
    }

    /**
     * Convenience method to deconstruct an XMLElement's unique name into its qname
     * and type
     * @param name
     * @return String[] containing qname and type
     */
    public static String[] decomposeElementName(String name) {
        int pos = name.indexOf(QNAME_TYPE_SEPARATOR);
        return new String[] {
                name.substring(0, pos),
                name.substring(pos + 1)
        };
    }

    /**
     * Convenience method to construct an XMLAttribute's unique name from its qname
     * and type
     * @param qName
     *            String qualified name
     * @param type
     *            String type
     * @return String
     */
   protected static String computeAttributeName(String qName, String type) {
        return qName + QNAME_TYPE_SEPARATOR + type;
    }

    /**
     * Method to visualize the grammar, mainly for debugging purposes
     * @return String representation of the grammar
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (XMLElementDefinition element : elementMap.values()) {
            str.append(element.toString()).append("\n");
        }
        return str.toString();
    }

}
