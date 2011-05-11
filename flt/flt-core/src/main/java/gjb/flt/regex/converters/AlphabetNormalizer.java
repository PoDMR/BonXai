/**
 * Created on Mar 20, 2009
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.regex.converters;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import gjb.flt.regex.Regex;
import gjb.flt.regex.UnknownOperatorException;
import gjb.util.SymbolIterator;
import gjb.util.tree.Node;
import gjb.util.tree.SExpressionParseException;
import gjb.util.tree.Tree;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class AlphabetNormalizer implements Converter, Normalizer {

	protected Regex regex;

	public AlphabetNormalizer() {
		super();
	}

	public AlphabetNormalizer(Regex regex) {
		this.regex = regex;
	}

	public String convert(Regex regex) {
		return normalize(regex).toString();
	}

	public String convert(String regexStr)
	        throws SExpressionParseException, UnknownOperatorException {
		return normalize(regexStr);
	}

	public Regex normalize(Regex regex) {
		Tree tree = regex.getTree();
		SymbolIterator symbolIt = new SymbolIterator();
		Map<String,String> alphabet = new HashMap<String,String>();
		for (Iterator<Node> it = tree.leaves(); it.hasNext(); ) {
			Node node = it.next();
			String symbol = node.getKey();
			if (symbol.equals(regex.epsilonSymbol()) ||
					symbol.equals(regex.emptySymbol()))
				continue;
			if (!alphabet.containsKey(symbol))
				alphabet.put(symbol, symbolIt.next());
			node.setKey(alphabet.get(symbol));
		}
		return regex;
	}

	public String normalize(String regexStr)
	        throws SExpressionParseException, UnknownOperatorException {
		if (this.regex == null)
			this.regex = new Regex();
		Tree tree = regex.getTree(regexStr);
		SymbolIterator symbolIt = new SymbolIterator();
		Map<String,String> alphabet = new HashMap<String,String>();
		for (Iterator<Node> it = tree.leaves(); it.hasNext(); ) {
			Node node = it.next();
			String symbol = node.getKey();
			if (symbol.equals(regex.epsilonSymbol()) ||
					symbol.equals(regex.emptySymbol()))
				continue;
			if (!alphabet.containsKey(symbol))
				alphabet.put(symbol, symbolIt.next());
			node.setKey(alphabet.get(symbol));
		}
		Regex newRegex = new Regex(tree, regex.getProperties());
		return newRegex.toString();
	}
	
}
