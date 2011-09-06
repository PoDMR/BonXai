/*
 * Created on Jan 30, 2006
 * Modified on $Date: 2009-11-12 09:10:15 $
 */
package eu.fox7.flt.automata.factories.sparse;

import eu.fox7.flt.automata.NoSuchTransitionException;
import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.impl.sparse.AnnotatedSparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.regex.Glushkov;

/**
 * @author eu.fox7
 * @version $Revision: 1.4 $
 * 
 */
public class AnnotatedPSAFactory
        extends AbstractPSAFactory<AnnotatedSparseNFA<Integer,Integer>> {

    public AnnotatedPSAFactory() {
        super();
        nfa = new AnnotatedSparseNFA<Integer,Integer>();
        nfa.setInitialState(Glushkov.INITIAL_STATE);
    }

    @Override
    public AnnotatedPSAFactory newInstance() {
    	return new AnnotatedPSAFactory();
    }

    @Override
    public void add(String[] example) {
        State fromState = nfa.getInitialState();
        for (int i = 0; i < example.length; i++) {
            String symbolValue = example[i];
            Symbol symbol = Symbol.create(symbolValue);
            String fromValue = nfa.getStateValue(fromState);
            try {
                if (nfa.getNextStates(symbol, fromState).isEmpty()) {
                    String toValue = example[i];
                    if (!stateCounter.containsKey(toValue)) {
                        stateCounter.put(toValue, 0);
                    }
                    stateCounter.put(toValue, stateCounter.get(toValue) + 1);
                    toValue += STATE_DECORATOR + stateCounter.get(toValue);
                    nfa.addTransition(symbolValue, fromValue, toValue);
                    nfa.annotate(symbolValue, fromValue, toValue, 0);
                }
                State toState = nfa.getNextState(symbol, fromState);
                nfa.annotate(symbol, fromState, toState,
                             nfa.getAnnotation(symbol, fromState, toState) + 1);
                fromState = toState;
            } catch (NoSuchTransitionException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (NotDFAException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        nfa.addFinalState(fromState);
    }

}
