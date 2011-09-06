/*
 * Created on Jul 19, 2006
 * Modified on $Date: 2009-11-12 09:10:15 $
 */
package eu.fox7.flt.automata.factories.sparse;

import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.Transition;
import eu.fox7.flt.regex.Glushkov;

/**
 * Class containing a static method to reverse a given Glushkov automaton.  If
 * A is the original automaton and L(A) is the language it accepts, then A^r is
 * the automaton that accepts all strings s = s_1 ... s_n such that s_n ... s_1
 * is in L(A).
 * As the class name implies the implementation is for Glushkov automata only.
 * 
 * @author eu.fox7
 * @version $Revision: 1.3 $
 * 
 */
public class GlushkovReverser {

    public static SparseNFA reverse(SparseNFA nfa) {
        SparseNFA newNFA = new SparseNFA();
        newNFA.setInitialState(Glushkov.INITIAL_STATE);
        for (Transition transition : nfa.getTransitionMap().getTransitions()) {
            if (transition.getFromState() != nfa.getInitialState()) {
                String symbolValue = Glushkov.unmark(nfa.getStateValue(transition.getFromState()));
                newNFA.addTransition(symbolValue,
                                     nfa.getStateValue(transition.getToState()),
                                     nfa.getStateValue(transition.getFromState()));
            }
        }
        for (State state : nfa.getFinalStates()) {
            String stateValue = nfa.getStateValue(state);
            if (state != nfa.getInitialState()) {
                String symbolValue = Glushkov.unmark(stateValue);
                newNFA.addTransition(symbolValue,
                                     Glushkov.INITIAL_STATE,
                                     stateValue);
            } else
                newNFA.addFinalState(stateValue);
        }
        for (Transition transition : nfa.getOutgoingTransitions(nfa.getInitialState()))
            newNFA.addFinalState(nfa.getStateValue(transition.getToState()));
        return newNFA;
    }

}
