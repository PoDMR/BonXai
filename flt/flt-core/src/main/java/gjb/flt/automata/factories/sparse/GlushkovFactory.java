/**
 * Created on Oct 15, 2009
 * Modified on $Date: 2009-10-27 14:14:00 $
 */
package gjb.flt.automata.factories.sparse;

import gjb.flt.automata.FeatureNotSupportedException;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.regex.Glushkov;
import gjb.flt.regex.UnknownOperatorException;
import gjb.util.tree.SExpressionParseException;
import gjb.util.tree.Tree;

import java.util.HashMap;
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

    public SparseNFA create(SparseNFA nfa, Tree tree)
            throws UnknownOperatorException, FeatureNotSupportedException {
        Set<String> sigma = glushkov.symbols(tree);
        Tree markedTree = glushkov.mark(tree);
        Set<String> pi = glushkov.symbols(markedTree);
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
