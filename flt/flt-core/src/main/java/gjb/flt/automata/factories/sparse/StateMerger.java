/*
 * Created on Dec 28, 2005
 * Modified on $Date: 2009-11-12 09:10:15 $
 */
package gjb.flt.automata.factories.sparse;

import gjb.flt.automata.NoSuchStateException;
import gjb.flt.automata.NoSuchTransitionException;
import gjb.flt.automata.impl.sparse.AnnotatedSparseNFA;
import gjb.flt.automata.impl.sparse.AnnotatedStateNFA;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.Transition;
import gjb.util.AbstractEquivalenceRelation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * @author gjb
 * @version $Revision: 1.3 $
 * 
 */
public class StateMerger<StateT,TransitionT> {

    protected StateSetRenamer<StateT,TransitionT> renamer = new DefaultStateSetRenamer();
    protected AnnotationMerger<StateT,TransitionT> annotationMerger = new DefaultAnnotationMerger();

    public AnnotatedStateNFA<StateT,TransitionT> merge(AnnotatedStateNFA<StateT,TransitionT> nfa,
                                                       AbstractEquivalenceRelation<State> relation) {
        Set<Set<State>> classes = relation.getClasses(nfa.getStates());
        /* create a map that defines a name for each set */
        Map<Set<State>,String> nameMap = new HashMap<Set<State>,String>();
        for (Set<State> set : classes)
            nameMap.put(set, renamer.rename(nfa, set));
        /* construct the new NFA */
        AnnotatedSparseNFA<StateT,TransitionT> newNFA = new AnnotatedSparseNFA<StateT,TransitionT>();
        newNFA.setInitialState(searchClassName(classes, nameMap,
                                               nfa.getInitialState()));
        for (Transition transition : nfa.getTransitionMap().getTransitions()) {
            String symbolValue = transition.getSymbol().toString();
            String fromStateValue = searchClassName(classes, nameMap,
                                                    transition.getFromState());
            String toStateValue = searchClassName(classes, nameMap,
                                                  transition.getToState());
            newNFA.addTransition(symbolValue, fromStateValue, toStateValue);
        }
        for (State state : nfa.getFinalStates())
            newNFA.addFinalState(searchClassName(classes, nameMap, state));
        /* construct a map that associates a state of the new NFA with
         * the set of states in the old NFA that it represents */
        Map<State,Set<State>> stateMap = new HashMap<State,Set<State>>();
        for (Set<State> set : nameMap.keySet()) {
            stateMap.put(newNFA.getState(nameMap.get(set)), set);
        }
        annotationMerger.merge(nfa, newNFA, stateMap);
        return newNFA;
    }

    public State merge(AnnotatedSparseNFA<StateT,TransitionT> nfa,
                       State state1, State state2)  {
        List<String[]> transitions = new ArrayList<String[]>();
        Map<String,TransitionT> annotations = new HashMap<String,TransitionT>();
        String newStateValue = getRenamer().rename(nfa, state1, state2);
        StateT newStateAnnotation = getAnnotationMerger().mergeStateAnnotations(nfa.getAnnotation(state1), nfa.getAnnotation(state2));
        for (Transition transition : nfa.getIncomingTransitions(state1)) {
            updateChanges(nfa, state1, state2, transitions, annotations,
                          newStateValue, transition);
        }
        for (Transition transition : nfa.getOutgoingTransitions(state1)) {
            if (transition.getToState() == state1) continue;
            updateChanges(nfa, state1, state2, transitions, annotations,
                          newStateValue, transition);
        }
        for (Transition transition : nfa.getIncomingTransitions(state2)) {
            if (transition.getFromState() == state1) continue;
            updateChanges(nfa, state1, state2, transitions, annotations,
                          newStateValue, transition);
        }
        for (Transition transition : nfa.getOutgoingTransitions(state2)) {
            if (transition.getToState() == state1) continue;
            if (transition.getToState() == state2) continue;
            updateChanges(nfa, state1, state2, transitions, annotations,
                          newStateValue, transition);
        }
        boolean isNewFinalState = nfa.isFinalState(state1) || nfa.isFinalState(state2);
        try {
	        nfa.removeState(state1);
        } catch (NoSuchStateException e1) {
        	throw new RuntimeException("state '" + state1.toString() + "' does not exist");
        }
        try {
	        nfa.removeState(state2);
        } catch (NoSuchStateException e1) {
        	throw new RuntimeException("state '" + state2.toString() + "' does not exist");
        }
        try {
            for (int i = 0; i < transitions.size(); i++) {
                String[] transition = transitions.get(i);
                nfa.addTransition(transition[0], transition[1], transition[2]);
                nfa.annotate(transition[0], transition[1], transition[2],
                             annotations.get(computeSignatureString(transition)));
            }
            if (isNewFinalState)
                nfa.addFinalState(newStateValue);
            nfa.annotate(newStateValue, newStateAnnotation);
            return nfa.getState(newStateValue);
        } catch (NoSuchTransitionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (NoSuchStateException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public State addMergeState(AnnotatedSparseNFA<StateT,TransitionT> nfa,
                               State state1, State state2) {
        assert nfa.hasState(state1);
        assert nfa.hasState(state2);
        String newStateValue = getRenamer().rename(nfa, state1, state2);
        nfa.addState(newStateValue);
        StateT newStateAnnotation = getAnnotationMerger().mergeStateAnnotations(nfa.getAnnotation(state1),
                                                                                nfa.getAnnotation(state2));
        assert newStateAnnotation != null : "merger of " + nfa.getStateValue(state1) + " and " +
                                            nfa.getStateValue(state2) + " produced null";
        boolean isNewFinalState = nfa.isFinalState(state1) || nfa.isFinalState(state2);
        try {
            if (isNewFinalState)
                nfa.addFinalState(newStateValue);
            nfa.annotate(newStateValue, newStateAnnotation);
            return nfa.getState(newStateValue);
        } catch (NoSuchStateException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void mergeStates(AnnotatedSparseNFA<StateT,TransitionT> nfa,
                            Map<State,State> stateMap) {
        Map<String,TransitionInfo> transitions = new HashMap<String,TransitionInfo>();
        for (State state : stateMap.keySet()) {
            Set<Transition> allTransitions = new HashSet<Transition>();
            allTransitions.addAll(nfa.getIncomingTransitions(state));
            allTransitions.addAll(nfa.getOutgoingTransitions(state));
            for (Transition transition : allTransitions) {
                TransitionInfo info = new TransitionInfo(nfa, transition, stateMap);
                String signature = info.getSignature();
                if (!transitions.containsKey(signature))
                    transitions.put(signature, info);
                transitions.get(signature).addAnnotation(transition,
                                                         nfa.getAnnotation(transition));
            }
        }
        removeStates(nfa, stateMap.keySet());
        for (TransitionInfo info : transitions.values()) {
            try {
                nfa.addTransition(info.symbolValue, info.fromStateValue, info.toStateValue);
            } catch (RuntimeException e) {
                System.err.println(info.toString());
                for (State state : stateMap.values())
                    System.err.println(nfa.getStateValue(state) + ", ");
                throw e;
//                continue;
            }
            try {
                nfa.annotate(info.symbolValue, info.fromStateValue, info.toStateValue,
                             info.getAnnotation());
            } catch (NoSuchTransitionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected void removeStates(AnnotatedSparseNFA<StateT,TransitionT> nfa,
                                Set<State> states) {
        for (State state : states) {
            Set<Transition> allTransitions = new HashSet<Transition>();
            allTransitions.addAll(nfa.getIncomingTransitions(state));
            allTransitions.addAll(nfa.getOutgoingTransitions(state));
            for (Transition transition : allTransitions) {
                String symbolValue = transition.getSymbol().toString();
                String fromStateValue = nfa.getStateValue(transition.getFromState());
                String toStateValue = nfa.getStateValue(transition.getToState());
                if (nfa.hasTransition(symbolValue, fromStateValue, toStateValue)) {
                    try {
                        nfa.removeTransition(symbolValue, fromStateValue, toStateValue);
                    } catch (NoSuchTransitionException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    throw new RuntimeException("transition " + fromStateValue + "[" +
                                               symbolValue + "] -> " + toStateValue +
                                               " doesn't exist");
                }
            }
        }
        for (State state : states)
	        try {
	            nfa.removeState(state);
            } catch (NoSuchStateException e) {
            	throw new RuntimeException("state '" + state.toString() + "' does not exist");
            }
    }

    protected static Map<State,State> computeReverseLookup(Map<State,State[]> stateMap) {
        Map<State,State> reverseMap = new HashMap<State,State>();
        for (State mergeState : stateMap.keySet()) {
            State origState1 = stateMap.get(mergeState)[0];
            State origState2 = stateMap.get(mergeState)[1];
            reverseMap.put(origState1, mergeState);
            reverseMap.put(origState2, mergeState);
        }
        return reverseMap;
    }

    protected void updateChanges(AnnotatedSparseNFA<StateT, TransitionT> nfa,
                                 State state1, State state2,
                                 List<String[]> transitions,
                                 Map<String,TransitionT> annotations,
                                 String newStateValue, Transition transition) {
        String[] signature = computeSignature(nfa, transition,
                                              newStateValue, state1, state2);
        transitions.add(signature);
        String signStr = computeSignatureString(signature);
        if (!annotations.containsKey(signStr))
            annotations.put(signStr, nfa.getAnnotation(transition));
        else {
            TransitionT ann1 = annotations.get(signStr);
            TransitionT ann2 = nfa.getAnnotation(transition);
            TransitionT newAnn = getAnnotationMerger().mergeTransitionAnnotations(ann1, ann2);
            annotations.put(signStr, newAnn);
        }
    }

    protected String[] computeSignature(SparseNFA nfa, Transition transition,
                                        String newStateValue,
                                        State state1, State state2) {
        
        String[] signature = new String[3];
        signature[0] = transition.getSymbol().toString();
        if (transition.getFromState() == state1 || transition.getFromState() == state2)
            signature[1] = newStateValue;
        else
            signature[1] = nfa.getStateValue(transition.getFromState());
        if (transition.getToState() == state1 || transition.getToState() == state2)
            signature[2] = newStateValue;
        else
            signature[2] = nfa.getStateValue(transition.getToState());
        return signature;
    }

    protected String computeSignatureString(String[] str) {
        return StringUtils.join(str, "-");
    }

    public StateSetRenamer<StateT, TransitionT> getRenamer() {
        return renamer;
    }

    public void setRenamer(StateSetRenamer<StateT,TransitionT> renamer) {
        this.renamer = renamer;
    }

    public AnnotationMerger<StateT, TransitionT> getAnnotationMerger() {
        return annotationMerger;
    }

    public void setAnnotationMerger(AnnotationMerger<StateT,TransitionT> merger) {
        this.annotationMerger = merger;
    }

    protected static String searchClassName(Set<Set<State>> classes,
                                            Map<Set<State>,String> nameMap,
                                            State state) {
        for (Set<State> set : classes)
            if (set.contains(state))
                return nameMap.get(set);
        return null;
    }

    public class DefaultStateSetRenamer
            implements StateSetRenamer<StateT,TransitionT> {

        public String rename(AnnotatedStateNFA<StateT,TransitionT> nfa,
                             Set<State> stateSet) {
            return stateSet.toString();
        }

        public String rename(AnnotatedStateNFA<StateT,TransitionT> nfa,
                             State state1, State state2) {
            return null;
        }
        
    }

    public class DefaultAnnotationMerger
            implements AnnotationMerger<StateT,TransitionT> {

        public void merge(AnnotatedStateNFA<StateT,TransitionT> oldNFA,
                          AnnotatedStateNFA<StateT,TransitionT> newNFA,
                          Map<State,Set<State>> stateMap) {
//            assert false : "DefaultAnnotationMerger methods should never be called";
            return;
        }

        public StateT mergeStateAnnotations(StateT stateAnn1,
                                            StateT stateAnn2) {
//            assert false : "DefaultAnnotationMerger methods should never be called";
            return null;
        }

        public TransitionT mergeTransitionAnnotations(TransitionT transAnn1,
                                                      TransitionT transAnn2) {
//            assert false : "DefaultAnnotationMerger methods should never be called";
            return null;
        }

        public StateT mergeStateAnnotation(State state1, StateT stateAnn1,
                                           State state2, StateT stateAnn2) {
//            assert false : "DefaultAnnotationMerger methods should never be called";
            return null;
        }

        public TransitionT mergeTransitionAnnotations(Transition trans1,
                                                      TransitionT transAnn1,
                                                      Transition trans2,
                                                      TransitionT transAnn2) {
//            assert false : "DefaultAnnotationMerger methods should never be called";
            return null;
        }
        
    }

    protected class TransitionInfo {

        protected String symbolValue;
        protected String fromStateValue;
        protected String toStateValue;
        protected String trans1;
        protected String trans2;
        protected TransitionT ann1;
        protected TransitionT ann2;

        protected TransitionInfo(AnnotatedSparseNFA<StateT,TransitionT> nfa,
                                 Transition transition,
                                 Map<State,State> reverseMap) {
            symbolValue = transition.getSymbol().toString();
            State fromState = transition.getFromState();
            if (reverseMap.containsKey(fromState))
                fromStateValue = nfa.getStateValue(reverseMap.get(fromState));
            else
                fromStateValue = nfa.getStateValue(fromState);
            State toState = transition.getToState();
            if (reverseMap.containsKey(toState))
                toStateValue = nfa.getStateValue(reverseMap.get(toState));
            else
                toStateValue = nfa.getStateValue(toState);
            assert symbolValue != null && !symbolValue.isEmpty() : "symbol is null";
            assert fromStateValue != null && !fromStateValue.isEmpty() : "fromState is null";
            assert toStateValue != null && !toStateValue.isEmpty() : "toState is null";
        }

        protected String getSignature() {
            return computeSignatureString(new String[] {symbolValue,
                                                        fromStateValue,
                                                        toStateValue});
        }

        protected void addAnnotation(Transition transition, TransitionT ann) {
            if (trans1 == null) {
                trans1 = transition.toString();
                ann1 = ann;
            } else if (!trans1.equals(transition.toString()) && trans2 == null) {
                trans2 = transition.toString();
                ann2 = ann;
            }
        }

        protected TransitionT getAnnotation() {
            return getAnnotationMerger().mergeTransitionAnnotations(ann1, ann2);
        }

        public String toString() {
            return fromStateValue + " [" + symbolValue + "] -> " + toStateValue;
        }
    
    }

}
