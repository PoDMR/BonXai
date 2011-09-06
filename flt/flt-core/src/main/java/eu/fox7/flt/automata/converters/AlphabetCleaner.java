/**
 * Created on Oct 12, 2009
 * Modified on $Date: 2009-11-10 14:01:29 $
 */
package eu.fox7.flt.automata.converters;

import java.util.HashSet;
import java.util.Set;

import eu.fox7.flt.automata.NoSuchSymbolException;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.Symbol;


/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
 */
public class AlphabetCleaner {

	public void clean(SparseNFA nfa) {
        Set<Symbol> usedSymbols = new HashSet<Symbol>(nfa.getSymbols());
        Set<Symbol> unusedSymbols = new HashSet<Symbol>();
        for (Symbol symbol : nfa.getSymbols()) {
            if (!usedSymbols.contains(symbol)) {
                unusedSymbols.add(symbol);
            }
        }
	        for (Symbol symbol : unusedSymbols)
	        	try {
	        		nfa.removeSymbol(symbol);
	        	} catch (NoSuchSymbolException e) {
	        		throw new RuntimeException("symbol '" + symbol.toString() + "' does not exist");
	        	}
	}

}
