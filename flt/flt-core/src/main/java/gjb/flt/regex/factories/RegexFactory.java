/**
 * Created on Nov 12, 2009
 * Modified on $Date: 2009-11-12 22:18:25 $
 */
package gjb.flt.regex.factories;

import gjb.flt.FLTRuntimeException;
import gjb.flt.regex.Regex;
import gjb.flt.regex.UnknownOperatorException;
import gjb.util.tree.Node;
import gjb.util.tree.SExpressionParseException;
import gjb.util.tree.Tree;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class RegexFactory {

	protected Regex regex;
	public static final int INFINITY = Integer.MAX_VALUE;

	public RegexFactory() {
		this(new Regex());
	}

	public RegexFactory(Regex regex) {
		this.regex = regex;
	}

	public Regex createEmpty() {
		try {
	        return new Regex(regex.leftBracket() + regex.emptySymbol() + regex.rightBracket());
        } catch (UnknownOperatorException e) {
	        e.printStackTrace();
	        throw new FLTRuntimeException("this can't happend", e);
        } catch (SExpressionParseException e) {
	        e.printStackTrace();
	        throw new FLTRuntimeException("this can't happend", e);
        }
	}
	
	public Regex createEpsilon() {
		try {
			return new Regex(regex.leftBracket() + regex.epsilonSymbol() + regex.rightBracket());
		} catch (UnknownOperatorException e) {
			e.printStackTrace();
			throw new FLTRuntimeException("this can't happend", e);
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			throw new FLTRuntimeException("this can't happend", e);
		}
	}

	public Regex createSymbol(String symbol) {
		try {
			return pack(symbol);
		} catch (UnknownOperatorException e) {
			e.printStackTrace();
			throw new FLTRuntimeException("this can't happend", e);
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			throw new FLTRuntimeException("this can't happend", e);
		}
	}

	public Regex createUnion(Regex...regexs) {
		String operator = regex.unionOperator();
        return compose(operator, regexs);
	}

	public Regex createConcatenation(Regex...regexs) {
		String operator = regex.concatOperator();
		return compose(operator, regexs);
	}
	
	public Regex createInterleave(Regex...regexs) {
		String operator = regex.interleaveOperator();
		return compose(operator, regexs);
	}
	
	public Regex createIntersection(Regex...regexs) {
		String operator = regex.intersectionOperator();
		return compose(operator, regexs);
	}

	public Regex createMultiplicity(Regex regex, int minOccur, int maxOccur) {
		String operator = null;
		if (minOccur == 0 && maxOccur == 1) {
			operator = regex.zeroOrOneOperator();
		} else if (minOccur == 0 && maxOccur == INFINITY) {
			operator = regex.zeroOrMoreOperator();
		} else if (minOccur == 1 && maxOccur == INFINITY) {
			operator = regex.oneOrMoreOperator();
		} else {
			operator = regex.mToNOperator(minOccur, maxOccur);
		}
		return compose(operator, regex);
	}

	public Regex createZeroOrOne(Regex regex) {
		return createMultiplicity(regex, 0, 1);
	}

	public Regex createZeroOrMore(Regex regex) {
		return createMultiplicity(regex, 0, INFINITY);
	}
	
	public Regex createOneOrMore(Regex regex) {
		return createMultiplicity(regex, 1, INFINITY);
	}
	
	protected Regex compose(String operator, Regex... regexs) {
	    try {
        	Tree tree = new Tree();
			Node root = new Node(operator);
			tree.setRoot(root);
        	for (Regex childRegex : regexs)
        		root.addChild(childRegex.getTree().getRoot().deepClone());
        	return new Regex(tree, regex.getProperties());
        } catch (UnknownOperatorException e) {
	        e.printStackTrace();
	        throw new FLTRuntimeException("this can't happend", e);
        }
    }

	protected Regex pack(String symbol) throws SExpressionParseException,
            UnknownOperatorException {
	    return new Regex(regex.leftBracket() + symbol + regex.rightBracket());
    }

}
