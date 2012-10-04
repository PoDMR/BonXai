/**
 * Created on Oct 15, 2009
 * Modified on $Date: 2009-10-27 14:14:00 $
 */
package eu.fox7.flt.automata.factories.sparse;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.regex.Glushkov;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.util.tree.SExpressionParseException;
import eu.fox7.util.tree.Tree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class GlushkovFactory {

	protected Glushkov glushkov;

	public GlushkovFactory() {
		this.glushkov = new Glushkov();
	}

	public GlushkovFactory(Properties properties) {
		this.glushkov = new Glushkov(properties);
	}

    /**
     * method that computes a Glushkov automaton from a regular expression
     * @param regex
     *            a <code>String</code> value representing a regular
     *            expression
     * @return a <code>NFA</code> value representing the Glushkov automaton
     * @exception SExpressionParseException
     *                if the S-expression representing the regular expression is
     *                syntactilly invalid
     * @exception UnknownOperatorException
     *                if an unknown operator symbol is encountered
     * @exception FeatureNotSupportedException
     *                if a feature such as intersection is not supported
     */
    public SparseNFA create(String regex) throws SExpressionParseException,
            UnknownOperatorException, FeatureNotSupportedException {
        SparseNFA nfa = new SparseNFA();
        return create(nfa, this.glushkov.regex().getTree(regex));
    }

    public SparseNFA create(SparseNFA nfa, Tree tree) throws UnknownOperatorException, FeatureNotSupportedException {
    	return create(nfa, tree, false);
    }

    public SparseNFA create(SparseNFA nfa, Tree tree, boolean isMarked)
            throws UnknownOperatorException, FeatureNotSupportedException {
        Tree markedTree = isMarked?tree:glushkov.mark(tree);
        Set<String> pi = glushkov.symbols(markedTree);
        Set<String> sigma = new HashSet<String>();
        if (isMarked)
        	for (String symbol: pi)
        		sigma.add(Glushkov.unmark(symbol));
        else
            sigma = glushkov.symbols(tree);
        
        Set<String> firstSet = glushkov.first(markedTree);
        Set<String> lastSet = glushkov.last(markedTree);
        Map<String,Set<String>> followMap = new HashMap<String,Set<String>>();
        for (String symbol : pi)
            followMap.put(symbol, glushkov.follow(markedTree, symbol));
        for (String symbol : sigma) {
            Set<String> match = glushkov.matchMark(firstSet, symbol);
            for (String toState : match)
                nfa.addTransition(symbol, Glushkov.INITIAL_STATE, toState);
            for (String fromState : pi) {
                match = glushkov.matchMark(followMap.get(fromState), symbol);
                for (String toState : match)
                    nfa.addTransition(symbol, fromState, toState);
            }
        }
        nfa.setInitialState(Glushkov.INITIAL_STATE);
        for (String state : lastSet)
            nfa.addFinalState(state);
        if (glushkov.isOptional(tree)) {
            nfa.addFinalState(Glushkov.INITIAL_STATE);
        }
        return nfa;
    }


}
