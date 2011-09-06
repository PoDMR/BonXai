/**
 * Created on Apr 15, 2009
 * Modified on $Date: 2009-11-12 22:18:04 $
 */
package eu.fox7.flt.regex.measures;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.regex.Regex.Counting;
import eu.fox7.flt.regex.measures.LanguageTest;
import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.SExpressionParseException;
import eu.fox7.util.tree.Tree;

/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
 */
public class ContainsEpsilonTest implements LanguageTest {

	protected Regex regex;

	public ContainsEpsilonTest() {
		super();
		this.regex = new Regex();
	}

	public ContainsEpsilonTest(Regex regex) {
		super();
		this.regex = regex;
	}

	public boolean test(String regexStr)
	        throws SExpressionParseException, UnknownOperatorException,
	               FeatureNotSupportedException {
		Tree tree = regex.getTree(regexStr);
		return test(tree.getRoot());
	}

	public boolean test(Node node) throws UnknownOperatorException {
		String symbol = node.getKey();
		if (symbol.equals(regex.emptySymbol())) {
			return false;
		} else if (symbol.equals(regex.epsilonSymbol())) {
			return true;
		} else if (symbol.equals(regex.zeroOrOneOperator())) {
			return true;
		} else if (symbol.equals(regex.zeroOrMoreOperator())) {
			return true;
		} else if (symbol.equals(regex.oneOrMoreOperator())) {
			return test(node.getChild(0));
		} else if (symbol.matches(regex.mToNOperator())) {
			Counting count = regex.parseMtoN(symbol);
			if (count.getM() == 0)
				return true;
			else
				return test(node.getChild(0));
		} else if (symbol.equals(regex.unionOperator())) {
			for (int i = 0; i < node.getNumberOfChildren(); i++)
				if (test(node.getChild(i)))
					return true;
			return false;
		} else if (symbol.equals(regex.concatOperator())) {
			for (int i = 0; i < node.getNumberOfChildren(); i++)
				if (!test(node.getChild(i)))
					return false;
			return true;
		} else if (symbol.equals(regex.interleaveOperator())) {
			for (int i = 0; i < node.getNumberOfChildren(); i++)
				if (!test(node.getChild(i)))
					return false;
			return true;
		} else {
			return false;
		}
	}

}
