/**
 * Created on Mar 20, 2009
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.regex.converters;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.util.SymbolIterator;
import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.SExpressionParseException;
import eu.fox7.util.tree.Tree;


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
