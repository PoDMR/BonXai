/*
 * Created on Oct 29, 2007
 * Modified on $Date: 2009-11-12 22:18:04 $
 */
package gjb.flt.regex.converters;

import gjb.flt.regex.Regex;
import gjb.flt.regex.UnknownOperatorException;
import gjb.util.tree.Node;
import gjb.util.tree.SExpressionParseException;

/**
 * @author gjb
 * @version $Revision: 1.2 $
 * 
 */
public class InfixConverter implements Converter {

	/* (non-Javadoc)
     * @see gjb.util.regex.converter.Converter#convert(gjb.util.regex.Regex)
     */
    public String convert(Regex regex) {
        return toInfixString(regex);
    }

    public String convert(String regexStr)
            throws SExpressionParseException, UnknownOperatorException {
    	Regex regex = new Regex(regexStr);
    	return convert(regex);
    }

    /**
     * method to convert a regular expression to infix format
     * @return String infix representation of the regular expression
     */
    protected String toInfixString(Regex regex) {
        if (regex.getTree() == null)
            return null;
        else if (regex.getTree().getRoot() == null)
            return regex.emptySymbol();
        else
            return formatNode(regex.getTree().getRoot(), regex);
    }

    /**
     * recursive method that transforms a tree into an infix regular expression
     * @param node Node to start from
     * @param regex 
     * @return String infix representation of this Node and its children
     */
    protected String formatNode(Node node, Regex regex) {
        if (node.isLeaf()) {
            return node.key();
        } else if (node.getNumberOfChildren() == 1) {
            return formatNode(node.child(0), regex) + node.key();
        } else {
            StringBuffer str = new StringBuffer();
            if (node.hasParent())
                str.append(regex.leftBracket());
            for (int i = 0; i < node.getNumberOfChildren(); i++) {
                if (i > 0) {
                    str.append(regex.getProperties().get("infixSep"));
                    str.append(node.key());
                    str.append(regex.getProperties().get("infixSep"));
                }
                str.append(formatNode(node.child(i), regex));
            }
            if (node.hasParent())
                str.append(regex.rightBracket());
            return str.toString();
        }
    }

}
