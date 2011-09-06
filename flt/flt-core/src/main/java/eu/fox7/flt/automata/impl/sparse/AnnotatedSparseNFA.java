/*
 * Created on Dec 20, 2005
 * Modified on $Date: 2009-11-10 14:01:29 $
 */
package eu.fox7.flt.automata.impl.sparse;

import eu.fox7.flt.automata.NoSuchStateException;
import eu.fox7.flt.automata.NoSuchTransitionException;
import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.misc.StateRemapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.bidimap.DualHashBidiMap;

/**
 * @author eu.fox7
 * @version $Revision: 1.2 $
 * 
 */
public class AnnotatedSparseNFA<StateT,TransitionT> extends SparseNFA
        implements AnnotatedStateNFA<StateT,TransitionT>,
                   ModifiableAnnotatedStateNFA<StateT,TransitionT> {

    protected Map<State,StateT> stateAnnotation = new HashMap<State,StateT>();
    protected Map<Transition,TransitionT> transitionAnnotation = new HashMap<Transition,TransitionT>();

    public AnnotatedSparseNFA() {
        super();
    }

    public AnnotatedSparseNFA(StateNFA nfa) {
    	this(nfa,StateRemapper.stateRemapping(nfa));
    }
    
    @SuppressWarnings({ "unchecked", "deprecation" })
    public AnnotatedSparseNFA(StateNFA nfa, Map<State,State> stateRemap) {
        this();
        this.alphabet = new HashMap(nfa.getAlphabet());
        this.states = new DualHashBidiMap();
        for (Iterator it = stateRemap.keySet().iterator(); it.hasNext(); ) {
            State originalState = (State) it.next();
            State state = stateRemap.get(originalState);
            this.states.put(nfa.getStateValue(originalState), state);
        }
        if (nfa.getInitialState() != null) {
            this.initialState = stateRemap.get(nfa.getInitialState());
        } else {
            this.initialState = null;
        }
        try {
            this.transitions = new TransitionMap(nfa.getTransitionMap(), stateRemap);
        } catch (NotDFAException e) {
            if (isIntendedDFA())
                throw new RuntimeException(e);
        }
        this.finalStates = new HashSet();
        for (Iterator it = nfa.getFinalStates().iterator(); it.hasNext();) {
            State state = (State) it.next();
            this.finalStates.add(stateRemap.get(state));
        }
        this.stateValueComparator = new StateValueComparator(this);
    }

    /* (non-Javadoc)
     * @see eu.fox7.flt.automata.ModifiableAnnotatedNFA#annotate(java.lang.String, StateT)
     */
    public void annotate(String stateValue, StateT annotation)
            throws NoSuchStateException {
        State state = getState(stateValue);
        if (state == null) throw new NoSuchStateException(stateValue);
        annotate(state, annotation);
    }

    /* (non-Javadoc)
     * @see eu.fox7.flt.automata.ModifiableAnnotatedNFA#annotate(eu.fox7.flt.automata.State, StateT)
     */
    public void annotate(State state, StateT annotation) {
        stateAnnotation.put(state, annotation);
    }

    /* (non-Javadoc)
     * @see eu.fox7.flt.automata.AnnotatedNFA#getAnnotation(java.lang.String)
     */
    public StateT getAnnotation(String stateValue) {
        State state = getState(stateValue);
        return getAnnotation(state);
    }

    /* (non-Javadoc)
     * @see eu.fox7.flt.automata.AnnotatedNFA#getAnnotation(eu.fox7.flt.automata.State)
     */
    public StateT getAnnotation(State state) {
        return stateAnnotation.get(state);
    }

    /* (non-Javadoc)
     * @see eu.fox7.flt.automata.AnnotatedNFA#hasAnnotation(java.lang.String)
     */
    public boolean hasAnnotation(String stateValue) {
        State state = getState(stateValue);
        return hasAnnotation(state);
    }

    /* (non-Javadoc)
     * @see eu.fox7.flt.automata.AnnotatedNFA#hasAnnotation(eu.fox7.flt.automata.State)
     */
    public boolean hasAnnotation(State state) {
        return stateAnnotation.containsKey(state);
    }

    /* (non-Javadoc)
     * @see eu.fox7.flt.automata.ModifiableAnnotatedNFA#annotate(eu.fox7.flt.automata.Transition, TransitionT)
     */
    public void annotate(Transition transition, TransitionT annotation)
            throws NoSuchTransitionException {
        if (!getTransitionMap().contains(transition))
            throw new NoSuchTransitionException(transition);
        transitionAnnotation.put(transition, annotation);
    }

    /* (non-Javadoc)
     * @see eu.fox7.flt.automata.ModifiableAnnotatedNFA#annotate(eu.fox7.flt.automata.Symbol, eu.fox7.flt.automata.State, eu.fox7.flt.automata.State, TransitionT)
     */
    public void annotate(Symbol symbol, State fromState, State toState,
                         TransitionT annotation)
            throws NoSuchTransitionException {
        Transition transition = new Transition(symbol, fromState, toState);
        annotate(transition, annotation);
    }

    /* (non-Javadoc)
     * @see eu.fox7.flt.automata.ModifiableAnnotatedNFA#annotate(java.lang.String, java.lang.String, java.lang.String, TransitionT)
     */
    public void annotate(String symbolValue, String fromStateValue,
                         String toStateValue, TransitionT annotation)
            throws NoSuchTransitionException {
        annotate(Symbol.create(symbolValue),
                      getState(fromStateValue),
                      getState(toStateValue), annotation);
    }

    /* (non-Javadoc)
     * @see eu.fox7.flt.automata.AnnotatedNFA#getAnnotation(eu.fox7.flt.automata.Transition)
     */
    public TransitionT getAnnotation(Transition transition) {
        return transitionAnnotation.get(transition);
    }

    /* (non-Javadoc)
     * @see eu.fox7.flt.automata.AnnotatedNFA#getAnnotation(eu.fox7.flt.automata.Symbol, eu.fox7.flt.automata.State, eu.fox7.flt.automata.State)
     */
    public TransitionT getAnnotation(Symbol symbol, State fromState,
                                     State toState) {
        Transition transition = new Transition(symbol, fromState, toState);
        return getAnnotation(transition);
    }

    /* (non-Javadoc)
     * @see eu.fox7.flt.automata.AnnotatedNFA#getAnnotation(java.lang.String, java.lang.String, java.lang.String)
     */
    public TransitionT getAnnotation(String symbolValue, String fromStateValue,
                                     String toStateValue) {
        Transition transition = new Transition(Symbol.create(symbolValue),
                                               getState(fromStateValue),
                                               getState(toStateValue));
        return getAnnotation(transition);
    }

    /* (non-Javadoc)
     * @see eu.fox7.flt.automata.AnnotatedNFA#hasAnnotation(eu.fox7.flt.automata.Transition)
     */
    public boolean hasAnnotation(Transition transition) {
        if (!getTransitionMap().contains(transition))
            return false;
        return transitionAnnotation.containsKey(transition);
    }
    
    /* (non-Javadoc)
     * @see eu.fox7.flt.automata.AnnotatedNFA#hasAnnotation(eu.fox7.flt.automata.Symbol, eu.fox7.flt.automata.State, eu.fox7.flt.automata.State)
     */
    public boolean hasAnnotation(Symbol symbol, State fromState,
                                 State toState) {
        Transition transition = new Transition(symbol, fromState, toState);
        return hasAnnotation(transition);
    }

    /* (non-Javadoc)
     * @see eu.fox7.flt.automata.AnnotatedNFA#hasAnnotation(java.lang.String, java.lang.String, java.lang.String)
     */
    public boolean hasAnnotation(String symbolValue, String fromStateValue,
                                 String toStateValue) {
        return hasAnnotation(Symbol.create(symbolValue),
                             getState(fromStateValue),
                             getState(toStateValue));
    }

    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append("alphabet: ").append(getSymbolValues().toString()).append("\n");
        str.append("[");
        for (Iterator<String> stateIt = getStateValues().iterator(); stateIt.hasNext(); ) {
            String stateValue = stateIt.next();
            str.append(stateValue);
            if (hasAnnotation(stateValue))
                str.append(" (").append(getAnnotation(stateValue).toString()).append(")");
            if (stateIt.hasNext()) str.append(", ");
        }
        str.append("]\n");
        str.append("states: ").append(getStateValues().toString()).append("\n");
        str.append("nr. of states: ").append(states.size()).append("\n");
        str.append("initial state: ").append(getStateValue(getInitialState())).append("\n");
        for (Transition t : getTransitionMap().getTransitions()) {
            String fromStateValue = getStateValue(t.getFromState());
            String symbolValue = t.getSymbol().toString();
            String toStateValue = getStateValue(t.getToState());
            str.append(fromStateValue).append(", ").append(symbolValue);
            str.append(" -> ").append(toStateValue);
            if (hasAnnotation(t))
                str.append(" (").append(getAnnotation(t).toString()).append(")");
            str.append("\n");
        }
        Set<String> finalStateStrings = new HashSet<String>();
        for (State finalState : getFinalStates()) {
            finalStateStrings.add(getStateValue(finalState));
        }
        str.append("final states: ").append(finalStateStrings.toString()).append("\n");
        return str.toString();
    }

}
