/**
 * Created on Oct 28, 2009
 * Modified on $Date: 2009-10-28 15:37:29 $
 */
package eu.fox7.flt.automata.factories.sparse;

import eu.fox7.flt.FLTRuntimeException;
import eu.fox7.flt.automata.NoSuchStateException;
import eu.fox7.flt.automata.NoSuchTransitionException;
import eu.fox7.flt.automata.impl.sparse.SupportNFA;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.automata.impl.sparse.Transition;
import eu.fox7.flt.regex.Glushkov;

import java.util.HashSet;
import java.util.Set;

/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
 * class that implements an IncrementalNFAFactory and that produces an Single
 * Occurrence Automaton that is annotated with the sample's support that is
 * used to generate it.  The support of a transition is the number of examples
 * in the sample that contain at least one witness of this transition.  The
 * empty string support is the number of times the empty string occurred in
 * the sample.  The final state support of a state is the number of examples for
 * which this state was the final state while traversing the example.
 * 
 */
public class SupportSOAFactory extends AbstractIncrementalNFAFactory<SupportNFA> {

	Set<Transition> visited;

	public SupportSOAFactory() {
        super();
        nfa = new SupportNFA();
        nfa.setInitialState(Glushkov.INITIAL_STATE);
        visited = new HashSet<Transition>();
	}

	@Override
    public AbstractIncrementalNFAFactory<SupportNFA> newInstance() {
	    return new SupportSOAFactory();
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

}
