package eu.fox7.flt.regex.simplification;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import eu.fox7.flt.automata.NoSuchStateException;
import eu.fox7.flt.automata.NoSuchTransitionException;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.automata.impl.sparse.Transition;
import eu.fox7.flt.regex.Glushkov;

/**
 * @author Jeroen Appermont 0726039
 */
public class Weight {

    /**
     * calculates regular expression of StateNFA using the state removal algorithm of Delgado and Morais.
     * this function accepts Glushkov automatons (starting with Glushkov.INITIAL_STATE) only!
     * this function uses regular expression from transition labels, not state values.
     * @param A                 the input NFA
     * @param acceptEmpty       does the input NFA accept the empty word?
     * @return                  the calculated regular expression
     * @throws NoSuchStateException
     * @throws NoSuchTransitionException
     */
    public static String stateWeight(StateNFA A, boolean acceptEmpty) throws NoSuchStateException, NoSuchTransitionException {
        SparseNFA nfa = setSingleFinalState(A, "q_F");
        nfa = AutomatonTools.getSimplifiedNFA(nfa);

        String removeStateValue = nextStateToBeRemoved(nfa);
        while (removeStateValue != null) { // continue until no more removable stateValues can be found
            nfa = removeState(nfa, removeStateValue);
            removeStateValue = nextStateToBeRemoved(nfa);
        }

        // now, there should only be two states left in the NFA A, with only one transition
        Set<Transition> transitionSet = nfa.getTransitionMap().getTransitions();
        Iterator<Transition> it = transitionSet.iterator();
        String r = it.next().getSymbol().toString(); // the regular expression

        r = regexCheckEmptyWord(r, acceptEmpty);
        return r;
    }

    /**
     * returns a new regular expression by adding the empty word to the original regular expression if acceptEmpty == true
     * @param regex         the input regular expression
     * @param acceptEmpty   the input boolean
     * @return              the new regular expression
     */
    private static String regexCheckEmptyWord(String regex, boolean acceptEmpty) {
        String regex2 = regex;
        if (acceptEmpty) {
            regex2 = "(? " + regex2 + ")";
        }
        return regex2;
    }

    /**
     * creates new NFA by setting a new final state and adding transitions from old final state(s) to new final state
     * @param nfa
     * @param finalStateValue   the stateValue of the new final state
     * @return                  a newly created NFA with a single final state
     */
    private static SparseNFA setSingleFinalState(StateNFA nfa, String finalStateValue) {
        SparseNFA nfa2 = new SparseNFA(nfa);
        Set<State> finalStateSet = new HashSet<State>(nfa2.getFinalStates());
        nfa2.addState(finalStateValue);
        for (State finalState : finalStateSet) { // make transactions from all final states to finalStateValue
            nfa2.addTransition(Symbol.getEpsilon(), finalState, nfa2.getState(finalStateValue));
        }
        nfa2.setFinalState(finalStateValue); // finalStateValue becomes only final state
        return nfa2;
    }

    /**
     * returns the stateValue of state to be removed according to state removal algorithm of Delgado and Morais
     * @param nfa       the input nfa
     * @return          the stateValue, has value null when no suitable stateValue found
     */
    private static String nextStateToBeRemoved(StateNFA nfa) {
        String I = nfa.getInitialStateValue();
        Set<String> F = nfa.getFinalStateValues();
        String stateValueMin = null;
        int weightMin = Integer.MAX_VALUE;
        for (String stateValue : nfa.getStateValues()) {
            if (stateValue.equals(Glushkov.INITIAL_STATE) || I.equals(stateValue) || F.contains(stateValue)) { // don't remove initial or final states
                continue;
            }
            int weight = getStateWeight(nfa, stateValue);
            if (weight < weightMin) {
                stateValueMin = stateValue;
                weightMin = weight;
            }
        }
        return stateValueMin;
    }

    /**
     * returns weight of an StateNFA's stateValue according to Delgado and Morais' removal algorithm
     * @param nfa           the input nfa
     * @param stateValue    the input stateValue
     * @return              the calculated weight
     */
    private static int getStateWeight(StateNFA nfa, String stateValue) {
        State state = nfa.getState(stateValue);
        Set<Transition> incoming = new HashSet<Transition>(nfa.getIncomingTransitions(state));
        Set<Transition> outgoing = new HashSet<Transition>(nfa.getOutgoingTransitions(state));
        int numIncoming = incoming.size();
        int numOutgoing = outgoing.size();

        // look for self-loop
        Transition selfLoop = null;
        for (Transition transition : incoming) {
            if (state.equals(transition.getFromState())) {
                selfLoop = transition;
                break; // max 1 self-loop available
            }
        }
        if (selfLoop != null) {
            incoming.remove(selfLoop);
            outgoing.remove(selfLoop);
            numIncoming--;
            numOutgoing--;
        }

        // calculate size of label NFA
        int stateWeight = 0;
        for (Transition transition : incoming) {
            String label = transition.getSymbol().toString();
            label = label.replaceAll("[() 0-9]", ""); // character exclusion, one char for each operation and state
            label = label.replaceAll("epsilon", "q"); // character exclusion, one char for each operation and state
            stateWeight += label.length() * (numOutgoing - 1);
        }
        for (Transition transition : outgoing) {
            String label = transition.getSymbol().toString();
            label = label.replaceAll("[() 0-9]", ""); // character exclusion, one char for each operation and state
            label = label.replaceAll("epsilon", "q"); // character exclusion, one char for each operation and state
            stateWeight += label.length() * (numIncoming - 1);
        }
        if (selfLoop != null) {
            String label = selfLoop.getSymbol().toString();
            label = label.replaceAll("[() 0-9]", ""); // character exclusion, one char for each operation and state
            label = label.replaceAll("epsilon", "q"); // character exclusion, one char for each operation and state
            stateWeight += label.length() * (numIncoming * numOutgoing - 1);
        }

        return stateWeight;
    }

    /**
     * removes state from NFA and updates transitions accordingly
     * @param nfa               the input NFA
     * @param removeStateValue  the value of the state that needs to be removed
     * @return                  the output NFA
     * @throws NoSuchStateException
     * @throws NoSuchTransitionException
     */
    private static SparseNFA removeState(StateNFA nfa, String removeStateValue) throws NoSuchStateException, NoSuchTransitionException {
        State state = nfa.getState(removeStateValue);
        Set<Transition> incomingTransitionSet = new HashSet<Transition>(nfa.getIncomingTransitions(state));
        Set<Transition> outgoingTransitionSet = new HashSet<Transition>(nfa.getOutgoingTransitions(state));

        // remove self-loop from incoming/outgoing transition set
        Transition selfLoop = null;
        for (Transition transition : incomingTransitionSet) { // look for self-loop
            if (state.equals(transition.getFromState())) { // if self-loop
                incomingTransitionSet.remove(transition);
                outgoingTransitionSet.remove(transition);
                selfLoop = transition;
                break; // max 1 self-loop available
            }
        }

        // check every combination of incoming and outgoing transitions to create the necessary new transitions
        SparseNFA nfa2 = new SparseNFA(nfa);
        for (Transition incomingTransition : incomingTransitionSet) {
            for (Transition outgoingTransition : outgoingTransitionSet) {
                String incoming = incomingTransition.getSymbol().toString();
                String outgoing = outgoingTransition.getSymbol().toString();
                incoming = regexCheckBrackets(incoming);
                outgoing = regexCheckBrackets(outgoing);

                // calculate regular expression for the new transition
                String regex;
                if (incomingTransition.getSymbol().equals(Symbol.getEpsilon())
                        && outgoingTransition.getSymbol().equals(Symbol.getEpsilon())) { // incoming & outgoing epsilon transition
                    if (selfLoop != null) { // has selfLoop
                        String self = selfLoop.getSymbol().toString();
                        self = regexCheckBrackets(self);
                        regex = "(* " + self + ")";
                    } else { // has no selfLoop
                        regex = Symbol.getEpsilon().toString();
                    }
                } else if (incomingTransition.getSymbol().equals(Symbol.getEpsilon())) { // incoming epsilon transition
                    if (selfLoop != null) { // has selfLoop
                        String self = selfLoop.getSymbol().toString();
                        self = regexCheckBrackets(self);
                        regex = "(. " + "(* " + self + ")" + " " + outgoing + ")";
                    } else { // has no selfLoop
                        regex = outgoing;
                    }
                } else if (outgoingTransition.getSymbol().equals(Symbol.getEpsilon())) { // outgoing epsilon transition
                    if (selfLoop != null) { // has selfLoop
                        String self = selfLoop.getSymbol().toString();
                        self = regexCheckBrackets(self);
                        regex = "(. " + incoming + " " + "(* " + self + ")" + ")";
                    } else { // has no selfLoop
                        regex = incoming;
                    }
                } else { // no incoming/outgoing epsilon transitions
                    if (selfLoop != null) { // has selfLoop
                        String self = selfLoop.getSymbol().toString();
                        self = regexCheckBrackets(self);
                        regex = "(. " + "(. " + incoming + " " + "(* " + self + ")" + ")" + " " + outgoing + ")";
                    } else { // has no selfLoop
                        regex = "(. " + incoming + " " + outgoing + ")";
                    }
                }

                // update transitions with new transition
                nfa2.addSymbol(regex);
                State newFromState = nfa2.getState(nfa.getStateValue(incomingTransition.getFromState()));
                State newToState = nfa2.getState(nfa.getStateValue(outgoingTransition.getToState()));
                if (!nfa2.getNextStates(newFromState).contains(newToState)) { // no transition yet
                    nfa2.addTransition(Symbol.create(regex), newFromState, newToState);
                } else { // transition already
                    Transition transitionOrig = null;
                    for (Transition transition : nfa2.getOutgoingTransitions(newFromState)) { // find existing transition
                        if (transition.getToState().equals(newToState)) { // found existing transition
                            transitionOrig = transition;
                            break;
                        }
                    }
                    String orig = transitionOrig.getSymbol().toString();
                    if (!transitionOrig.getSymbol().equals(Symbol.getEpsilon())) { // if no epsilon transition
                        orig = regexCheckBrackets(orig);
                        regex = "(| " + orig + " " + regex + ")";
                    } else { // if epsilon transition
                        regex = "(? " + regex + ")";
                    }
                    nfa2.removeTransition(transitionOrig.getSymbol(), transitionOrig.getFromState(), transitionOrig.getToState()); // remove previous transition
                    nfa2.addTransition(Symbol.create(regex), newFromState, newToState); // add new transition
                }
            }
        }

        // state can now be removed
        nfa2.removeState(removeStateValue);

        return nfa2;
    }

    /**
     * returns a new regular expression by adding brackets to the original regular expression if no brackets at beginning or end
     * @param regex         the input regular expression
     * @return              the new regular expression
     */
    private static String regexCheckBrackets(String regex) {
        String regex2 = regex;
        if (regex2.indexOf('(') != 0 || regex2.lastIndexOf(')') != regex2.length() - 1) { // if r has no brackets at beginning or end
            regex2 = "(" + regex2 + ")";
        }
        return regex2;
    }

}
