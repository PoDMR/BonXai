/*
 * Created on Jun 24, 2007
 * Modified on $Date: 2009-11-10 14:01:29 $
 */
package gjb.flt.automata.converters;

import gjb.flt.automata.NoSuchStateException;
import gjb.flt.automata.NoSuchTransitionException;
import gjb.flt.automata.impl.sparse.ModifiableStateNFA;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.Transition;
import gjb.util.Collections;
import gjb.util.EquivalenceRelation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class AbstractMinimizer implements Minimizer {

    protected EquivalenceRelation<State> relation;

    public AbstractMinimizer() {
        super();
    }

    public void minimize(ModifiableStateNFA nfa) {
        initRelation(nfa);
        Set<Set<State>> classes = relation.getClasses(nfa.getStates());
        for (Set<State> cl : classes)
            merge(nfa, cl);
    }

    protected abstract void initRelation(ModifiableStateNFA nfa);

    protected void merge(ModifiableStateNFA nfa, Set<State> cl) {
        if (cl.size() >= 2) {
            State state1 = Collections.takeOne(cl);
            while (!cl.isEmpty()) {
                State state2 = Collections.takeOne(cl);
                state1 = merge(nfa, state1, state2);
            }
        }
    }

    protected State merge(ModifiableStateNFA nfa, State state1, State state2) {
        String newStateValue = nfa.getStateValue(state1);
        boolean isFinal = nfa.isFinalState(state1);
        boolean isInitial = ( nfa.isInitialState(state1) || nfa.isInitialState(state2) );
        List<String[]> transitions = new ArrayList<String[]>();
        for (Transition transition : nfa.getIncomingTransitions(state1))
            transitions.add(new String[] {transition.getSymbol().toString(),
                                          nfa.getStateValue(transition.getFromState()),
                                          nfa.getStateValue(transition.getToState())});
        for (Transition transition : nfa.getOutgoingTransitions(state1))
            transitions.add(new String[] {transition.getSymbol().toString(),
                    nfa.getStateValue(transition.getFromState()),
                    nfa.getStateValue(transition.getToState())});
        for (Transition transition : nfa.getIncomingTransitions(state2))
            transitions.add(new String[] {transition.getSymbol().toString(),
                    nfa.getStateValue(transition.getFromState()),
                    nfa.getStateValue(transition.getToState())});
        for (Transition transition : nfa.getOutgoingTransitions(state2))
            transitions.add(new String[] {transition.getSymbol().toString(),
                    nfa.getStateValue(transition.getFromState()),
                    nfa.getStateValue(transition.getToState())});
        String stateValue1 = nfa.getStateValue(state1);
        String stateValue2 = nfa.getStateValue(state2);
        for (String[] transitionStr : transitions) {
            try {
                nfa.removeTransition(transitionStr[0], transitionStr[1], transitionStr[2]);
            } catch (NoSuchTransitionException e) {}
        }
        try {
	        nfa.removeState(state1);
        } catch (NoSuchStateException e) {
        	throw new RuntimeException("state '" + state1.toString() + "' does not exist");
        }
        try {
	        nfa.removeState(state2);
        } catch (NoSuchStateException e) {
        	throw new RuntimeException("state '" + state2.toString() + "' does not exist");
        }
        if (isFinal)
            nfa.addFinalState(newStateValue);
        if (isInitial)
        	nfa.setInitialState(newStateValue);
        for (String[] transitionStr : transitions) {
            convert(transitionStr, stateValue1, stateValue2, newStateValue);
            nfa.addTransition(transitionStr[0], transitionStr[1], transitionStr[2]);
        }
        return nfa.getState(newStateValue);
    }

    protected void convert(String[] transitionStr, String stateValue1, 
                           String stateValue2, String newStateValue) {
        if (transitionStr[1].equals(stateValue1) ||
        		transitionStr[1].equals(stateValue2))
            transitionStr[1] = newStateValue;
        if (transitionStr[2].equals(stateValue1) ||
        		transitionStr[2].equals(stateValue2))
            transitionStr[2] = newStateValue;
    }

}