/*
 * Created on Jun 17, 2005
 * Modified on $Date: 2009-11-12 22:18:04 $
 */
package gjb.flt.treegrammar;

import gjb.flt.FLTRuntimeException;
import gjb.flt.regex.NoRegularExpressionDefinedException;
import gjb.flt.regex.Regex;
import gjb.flt.regex.RegexException;
import gjb.flt.regex.UnknownOperatorException;
import gjb.flt.regex.generators.LanguageGenerator;
import gjb.flt.regex.measures.ContainsEpsilonTest;
import gjb.util.tree.Node;
import gjb.util.tree.SExpressionParseException;
import gjb.util.tree.Tree;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.transform.TransformerConfigurationException;

/**
 * <p> Class to represent an XML element in a model.  The element's content model
 * is specified as a decorated regular expression. </p>
 * @author gjb
 * @version $Revision: 1.3 $
 * 
 */
public class XMLElementDefinition {

    /**
     * XMLDocument the element is part of
     */
    protected XMLGrammar document;
    /**
     * The element's qualified name
     */
    protected String qName;
    /**
     * The element's type
     */
    protected String type;
    /**
     * List of XMLAttributes for this element
     */
    protected List<XMLAttributeDefinition> attributes;
    /**
     * Content model for this element
     */
    protected Regex contentModel;
    /**
     * String representation of the content model, mainly kept for debugging purposes
     */
    protected String contentModelStr;
    /**
     * boolean that is true if the element's content model can be empty, false
     * otherwise
     */
    public boolean acceptsEpsilon;

    /**
     * Constructor for XMLElement modeling an element in an XML document
     * @param doc
     *            XMLDocument the XMLElement is part of
     * @param qName
     *            String element's qualified name
     * @param type
     *            String element's type
     * @param attributes
     *            List of XMLAttributes belonging to the element
     * @param contentModelStr
     *            String representation of the content model, a decorated regular
     *            expression
     * @throws SyntaxException 
     * @throws TransformerConfigurationException 
     * @throws SExpressionParseException
     *            thrown if the content model contains a syntax error
     * @throws UnknownOperatorException
     *            thrown if the content model contains an unknown operator
     * @throws NoRegularExpressionDefinedException 
     */
    public XMLElementDefinition(XMLGrammar doc, String qName, String type,
                                List<XMLAttributeDefinition> attributes,
                                String contentModelStr) 
            throws SyntaxException, TransformerConfigurationException {
        this.document = doc;
        this.qName = qName;
        this.type = type;
        this.attributes = attributes;
        this.contentModelStr = contentModelStr;
        try {
        	LanguageGenerator generator = new LanguageGenerator(contentModelStr);
	        this.contentModel = new Regex(generator.getTree());
        } catch (UnknownOperatorException e) {
	        throw new SyntaxException("unknown operator", e);
        } catch (SExpressionParseException e) {
        	throw new SyntaxException("parse exception", e);
        }
        try {
        	ContainsEpsilonTest test = new ContainsEpsilonTest();
	        this.acceptsEpsilon = test.test(contentModel.getTree().getRoot());
        } catch (RegexException e) {
        	e.printStackTrace();
        	throw new FLTRuntimeException("this shouldn't happen", e);
        }
    }

    /**
     * Method to retrieve the element's full name, i.e., qname#type
     * @return String full name
     */
    public String getName() {
    	return XMLGrammar.computeElementName(getQName(), getType());
    }

    /**
     * Method to retrieve the element's name
     * @return String qualified name
     */
    public String getQName() {
        return qName;
    }

    /**
     * Method to retrieve the element's type
     * @return String type
     */
    public String getType() {
        return type;
    }

    /**
     * Method to retrieve the element's List of attributes
     * @return List of attributes
     */
    public List<XMLAttributeDefinition> getAttributes() {
        return attributes;
    }

    /**
     * Method to retrieve the XMLElement's content model
     * @return Regex that represents the element's content model
     */
    public Regex getContentModel() {
        return this.contentModel;
    }

    /**
     * Method to retrieve the original content model string
     * @return String representation of the content model
     */
    public String getContentModelString() {
        return this.contentModelStr;
    }

    /**
     * Method to retrieve the names of the elements that can be children of this
     * element according to its content model
     * @return Set<String> child element names
     */
    public Set<String> getChildElementNames() {
    	Set<String> childNames = new HashSet<String>();
    	if (!isTextType(getName())) {
    		Tree tree = this.contentModel.getTree();
    		for (Iterator<Node> it = tree.leaves(); it.hasNext(); ) {
    			String symbol = it.next().getKey();
    			if (!this.contentModel.emptySymbol().equals(symbol) &&
    					!this.contentModel.epsilonSymbol().equals(symbol) &&
    					!isTextType(symbol))
    				childNames.add(symbol);
    		}
    	}
    	return childNames;
    }

    /**
     * Convenience method to determine whether a content item is text
     * @param name
     *            String content item name
     * @return boolean true if the nnme represents a text item, false otherwise
     */
    public static boolean isTextType(String name) {
        return name.startsWith(XMLGrammar.QNAME_TYPE_SEPARATOR);
    }

    /**
     * Method to compute the string representation of the XMLElement for debugging
     * purposes.
     */
    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append(XMLGrammar.computeElementName(getQName(), getType()));
        str.append(XMLGrammar.LHS_RHS_RULE_SEP);
        for (Iterator<XMLAttributeDefinition> it = attributes.iterator(); it.hasNext();) {
            XMLAttributeDefinition attr = it.next();
            str.append(attr.toString());
            if (it.hasNext()) str.append(XMLGrammar.ATTR_DEF_SEP);
        }
        str.append(XMLGrammar.ATTR_CONTENT_MODEL_SEP);
        str.append(contentModelStr);
        return str.toString();
    }

}
