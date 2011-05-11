/**
 * Created on Jun 17, 2009
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.regex.converters;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import gjb.flt.regex.Regex;
import gjb.flt.regex.UnknownOperatorException;
import gjb.util.tree.Node;
import gjb.util.tree.NodeTransformException;
import gjb.util.tree.NodeVisitor;
import gjb.util.tree.SExpressionParseException;
import gjb.util.tree.Tree;
import gjb.util.tree.TreeVisitor;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class DTDConverter implements Converter {

    public static final String LEFT_BRACKET = "(";
    public static final String RIGHT_BRACKET = ")";
    public static final String UNION_OPERATOR = "|";
    public static final String CONCAT_OPERATOR = ",";
    public static final String ZERO_OR_ONE_OPERATOR = "?";
    public static final String ZERO_OR_MORE_OPERATOR = "*";
    public static final String ONE_OR_MORE_OPERATOR = "+";
    public static final String EPSILON_SYMBOL = "EMPTY";
    public static final String INFIX_SEP = " ";
	protected static Properties properties = new Properties();
	protected static Converter infixConverter = new InfixConverter();

	public DTDConverter() {
		super();
		properties = new Properties();
		properties.setProperty("zeroOrOne", ZERO_OR_ONE_OPERATOR);
		properties.setProperty("zeroOrMore", ZERO_OR_MORE_OPERATOR);
		properties.setProperty("oneOrMore", ONE_OR_MORE_OPERATOR);
		properties.setProperty("concat", CONCAT_OPERATOR);
		properties.setProperty("union", UNION_OPERATOR);
		properties.setProperty("epsilon", EPSILON_SYMBOL);
        properties.setProperty("leftBracket", LEFT_BRACKET);
        properties.setProperty("rightBracket", RIGHT_BRACKET);
        properties.setProperty("infixSep", INFIX_SEP);
	}

	/* (non-Javadoc)
	 * @see gjb.util.regex.converters.Converter#convert(gjb.util.regex.Regex)
	 */
	public String convert(Regex regex) throws ConversionException {
		substitute(new Tree(regex.getTree()), regex.getProperties(), properties);
		return regex.leftBracket() + infixConverter.convert(regex) + regex.rightBracket();
	}

	/* (non-Javadoc)
	 * @see gjb.util.regex.converters.Converter#convert(java.lang.String)
	 */
	public String convert(String regexStr) throws SExpressionParseException,
			UnknownOperatorException, ConversionException {
		Regex regex = new Regex(regexStr);
		substitute(regex.getTree(), regex.getProperties(), properties);
		return regex.leftBracket() + infixConverter.convert(regex) + regex.rightBracket();
	}

	protected void substitute(Tree tree, Properties origP, Properties newP) 
	        throws ConversionException {
		Map<String,String> substitutions = new HashMap<String,String>();
		for (Enumeration<?> e = newP.propertyNames(); e.hasMoreElements(); ) {
			String name = (String) e.nextElement();
			substitutions.put(origP.getProperty(name), newP.getProperty(name));
		}
		TreeVisitor visitor = new TreeVisitor(tree);
		try {
			visitor.visit(new SubstitutionVisitor(substitutions), null);
		} catch (NodeTransformException e) {
			throw new ConversionException("node transformation failure", e);
		}
	}

	/**
	 * @author lucg5005
	 * @version $Revision: 1.1 $
	 *
	 */
	public static class SubstitutionVisitor implements NodeVisitor {

		protected Map<String,String> substitutions;

		public SubstitutionVisitor(Map<String,String> substitutions) {
			this.substitutions = substitutions;
		}
		
		/* (non-Javadoc)
		 * @see gjb.util.tree.NodeVisitor#visit(gjb.util.tree.Node, java.util.Map)
		 */
		public void visit(Node node, Map<String, Object> params)
				throws NodeTransformException {
			if (substitutions.containsKey(node.getKey()))
				node.setKey(substitutions.get(node.getKey()));
		}
	
	}

}
