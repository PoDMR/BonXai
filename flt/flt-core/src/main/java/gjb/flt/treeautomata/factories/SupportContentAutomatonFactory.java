/**
 * Created on Nov 5, 2009
 * Modified on $Date: 2009-11-05 13:53:06 $
 */
package gjb.flt.treeautomata.factories;

import gjb.flt.FLTRuntimeException;
import gjb.flt.automata.NoSuchStateException;
import gjb.flt.automata.NoSuchTransitionException;
import gjb.flt.automata.factories.sparse.AbstractIncrementalNFAFactory;
import gjb.flt.automata.impl.sparse.Symbol;
import gjb.flt.automata.impl.sparse.Transition;
import gjb.flt.regex.Glushkov;
import gjb.flt.treeautomata.impl.ContentAutomaton;

import java.util.HashSet;
import java.util.Set;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class SupportContentAutomatonFactory
        extends AbstractIncrementalNFAFactory<ContentAutomaton>
        implements ContentAutomatonFactory {

	Set<Transition> visited;

	public SupportContentAutomatonFactory() {
        super();
        nfa = new ContentAutomaton();
        nfa.setInitialState(Glushkov.INITIAL_STATE);
        visited = new HashSet<Transition>();
	}

	@Override
    public void add(String[] example) {
    	visited.clear();
        String fromValue = nfa.getStateValue(nfa.getInitialState());
        for (int i = 0; i < example.length; i++) {
            String symbolValue = example[i];
            String toValue = example[i];
            try {
                if (!nfa.hasTransition(symbolValue, fromValue, toValue)) {
                    nfa.addTransition(symbolValue, fromValue, toValue);
                    nfa.annotate(symbolValue, fromValue, toValue, 0);
                }
                Transition transition = new Transition(Symbol.create(symbolValue), nfa.getState(fromValue), nfa.getState(toValue));
                if (!visited.contains(transition)) {
                	nfa.annotate(symbolValue, fromValue, toValue,
                	             nfa.getAnnotation(symbolValue, fromValue, toValue) + 1);
                	visited.add(transition);
                }
                fromValue = toValue;
            } catch (NoSuchTransitionException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        nfa.addFinalState(fromValue);
        try {
	        if (!nfa.hasAnnotation(fromValue))
	        	nfa.annotate(fromValue, 0);
	        nfa.annotate(fromValue, nfa.getAnnotation(fromValue) + 1);
        } catch (NoSuchStateException e) {
        	throw new FLTRuntimeException("this shouldn't happen", e);
        }
    }

	@Override
    public AbstractIncrementalNFAFactory<ContentAutomaton> newInstance() {
	    return new SupportContentAutomatonFactory();
    }

}
