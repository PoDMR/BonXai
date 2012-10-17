package eu.fox7.flt.regex.simplification;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import eu.fox7.flt.automata.NoSuchStateException;
import eu.fox7.flt.automata.NoSuchTransitionException;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.automata.impl.sparse.Transition;
import eu.fox7.flt.regex.Glushkov;
import eu.fox7.flt.regex.infer.rwr.Rewriter;
import eu.fox7.flt.regex.infer.rwr.impl.Automaton;
import eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonConverter;
import eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory;

/**
 * @author Jeroen Appermont 0726039
 */
public class Nfa2ReClass {

    /**
     * returns a regular expression for the input NFA using Wouter Gelade's method.
     * this function accepts Glushkov automatons (starting with Glushkov.INITIAL_STATE) only!
     * this function uses regular expression from state values, not transition labels.
     * uses Bottom-Up() function before the actual Nfa2Re() function is used.
     * @param A                 the input NFA
     * @param acceptEmpty       does the input NFA accept the empty word?
     * @return                  the calculated regular expression
     * @throws NoSuchStateException
     * @throws NoSuchTransitionException
     */
    public static String Nfa2Re_withBottomUp(StateNFA A, boolean acceptEmpty) throws NoSuchStateException, NoSuchTransitionException {
        // convert StateNFA to Automaton
        GraphAutomatonFactory graphAutomatonFactory = new GraphAutomatonFactory();
        Automaton automaton = graphAutomatonFactory.create(A);

        // BottomUp()
        Rewriter rew = new Rewriter();
        automaton = rew.rewrite(automaton);

        // convert Automaton to SparseNFA
        GraphAutomatonConverter graphAutomatonConverter = new GraphAutomatonConverter();
        SparseNFA A2 = graphAutomatonConverter.convertToNFA(automaton);

        // fix initial state
        A2.addState(Glushkov.INITIAL_STATE);
        String init = A2.getInitialStateValue();
        for (String value : A2.getNextStateValues(init)) {
            A2.addTransition(value, Glushkov.INITIAL_STATE, value);
        }
        A2.removeState(init);
        A2.setInitialState(Glushkov.INITIAL_STATE);

        // Nfa2Re()
        String r = Nfa2Re(A2, acceptEmpty);
        return r;
    }

    /**
     * returns a regular expression for the input NFA using Wouter Gelade's method.
     * this function accepts Glushkov automatons (starting with Glushkov.INITIAL_STATE) only!
     * this function uses regular expression from state values, not transition labels.
     * does not use Bottom-Up() function.
     * @param A                 the input NFA
     * @param acceptEmpty       does the input NFA accept the empty word?
     * @return                  the calculated regular expression
     * @throws NoSuchStateException
     * @throws NoSuchTransitionException
     */
    public static String Nfa2Re(StateNFA A, boolean acceptEmpty) throws NoSuchStateException, NoSuchTransitionException {
        String r = ""; // the regular expression
        A = AutomatonTools.getSimplifiedNFA(A); // remove unnecessary states and edges

        // if NFA is trivial -> return regular expression
        if (isTrivial(A)) {
            Set<String> I = A.getNextStateValues(Glushkov.INITIAL_STATE); // get set of 'real' initial states
            Iterator<String> it = I.iterator();
            r = it.next(); // if trivial, only one state possible

            r = regexCheckBrackets(r);
            r = regexCheckEmptyWord(r, acceptEmpty);
            return r;
        }

        // NFA is not trivial -> search for weakly connected components
        Set<Set<String>> WCCSet = getWeaklyConnectedComponents(A);
        int k = WCCSet.size();

        // if more than one weakly connected component found -> split up
        if (k > 1) {
            for (Set<String> WCC : WCCSet) { // calculate regular expression of every weakly connected component
                SparseNFA WCC_NFA = AutomatonTools.getSubNFA(A, WCC);
                String rAid = Nfa2Re(WCC_NFA, false); // calculate regular expression
                if (!r.isEmpty()) {
                    r = r + " " + rAid;
                } else {
                    r = rAid;
                }
            }
            r = "(| " + r + ")";

            r = regexCheckEmptyWord(r, acceptEmpty);
            return r;
        }

        // if only one weakly connected component found -> search for strongly connected components
        Set<Set<String>> SCCSet = getStronglyConnectedComponents(A);
        k = SCCSet.size();

        // if only one strongly connected component found -> remove transitions coming from certain final states
        if (k == 1) {
            SparseNFA SCC_NFA = new SparseNFA(A);

            Set<String> I = new HashSet<String>(SCC_NFA.getNextStateValues(Glushkov.INITIAL_STATE));
            Set<String> F = new HashSet<String>(SCC_NFA.getFinalStateValues());

            Set<String> FPrime = new HashSet<String>(); // F'
            Set<String> IPrime = new HashSet<String>(); // I'
            Set<Transition> FAcIAcTransitionSet = new HashSet<Transition>(); // F' x I' transitions
            getFPrimeXIPrime(SCC_NFA, FPrime, IPrime, FAcIAcTransitionSet); // get F', I' and F' x I' transitions

            for (Transition transition : FAcIAcTransitionSet) { // remove F' x I' transitions from SCC_NFA
                SCC_NFA.removeTransition(transition.getSymbol(), transition.getFromState(), transition.getToState());
            }

            // impossible scenario
            if (IPrime.isEmpty() || FPrime.isEmpty()) { // this should never happen
                System.out.println("IMPOSSIBLE ERROR: Nfa2Re(): 4 scenarios and I' and F' are empty");
                System.exit(1);
            }

            // Four possible scenarios
            if (IPrime.equals(I) && FPrime.equals(F)) {
                String r1 = Nfa2Re(SCC_NFA, false);
                r = "(+ " + r1 + ")";
            } else if (!IPrime.equals(I) && FPrime.equals(F)) {
                String r1 = Nfa2Re(SCC_NFA, false); // I, F
                SCC_NFA.removeState(Glushkov.INITIAL_STATE); // turn initial states into ordinary states
                SCC_NFA.setInitialState(Glushkov.INITIAL_STATE);
                for (String newInitStateValue : IPrime) { // turn I' into initial states
                    SCC_NFA.addTransition(Symbol.getEpsilon(), SCC_NFA.getState(Glushkov.INITIAL_STATE), SCC_NFA.getState(newInitStateValue));
                }
                String r2 = Nfa2Re(SCC_NFA, false); // I', F
                r = "(. " + r1 + " " + "(* " + r2 + ")" + ")";
            } else if (IPrime.equals(I) && !FPrime.equals(F)) {
                /*
                String r1 = Nfa2Re(SCC_NFA, false); // I, F
                String r3 = Nfa2Re(SCC_NFA, true); // I, F
                SCC_NFA.setFinalStateValues(FPrime); // turn F' states into sole final states
                String r2 = Nfa2Re(SCC_NFA, false); // I, F'
                r = "(| " + r1 + " " + "(. " + "(* " + r2 + ")" + " " + r3 + ")" + ")";
                 */
                // using own method
                String r1 = Nfa2Re(SCC_NFA, false); // I, F
                SCC_NFA.setFinalStateValues(FPrime); // turn F' states into sole final states
                String r2 = Nfa2Re(SCC_NFA, false); // I, F'
                r = "(. " + "(* " + r2 + ")" + " " + r1 + ")";
            } else if (!IPrime.equals(I) && !FPrime.equals(F)) {
                String r1 = Nfa2Re(SCC_NFA, false); // I, F
                SCC_NFA.setFinalStateValues(FPrime); // turn F' states into sole final states
                String r2 = Nfa2Re(SCC_NFA, false); // I, F'
                SCC_NFA.removeState(Glushkov.INITIAL_STATE); // turn initial states into ordinary states
                SCC_NFA.setInitialState(Glushkov.INITIAL_STATE);
                for (String newInitStateValue : IPrime) { // turn I' into initial states
                    SCC_NFA.addTransition(Symbol.getEpsilon(), SCC_NFA.getState(Glushkov.INITIAL_STATE), SCC_NFA.getState(newInitStateValue));
                }
                String r3 = Nfa2Re(SCC_NFA, false); // I', F'
                SCC_NFA.setFinalStateValues(F); // turn F states into sole final states
                String r4 = Nfa2Re(SCC_NFA, true); // I', F
                r = "(| " + r1 + " " + "(. " + r2 + " " + "(* " + r3 + ")" + " " + r4 + ")" + ")";
            }

            r = regexCheckEmptyWord(r, acceptEmpty);
            return r;
        }

        // if multiple strongly connected components found -> do concat-check
        List<SparseNFA> concatList = new LinkedList<SparseNFA>();
        List<Boolean> currBoolList = new LinkedList<Boolean>();
        List<Boolean> nextBoolList = new LinkedList<Boolean>();
        concatCheck(A, acceptEmpty, concatList, currBoolList, nextBoolList);

        // if concatCheck successful -> concatenation of strongly connected components -> split up
        if (!concatList.isEmpty()) {
            // create regular expression by starting with last SCC and ending with first SCC
            Collections.reverse(concatList);
            Collections.reverse(currBoolList);
            Collections.reverse(nextBoolList);

            // calculate regular expression of every strongly connected component
            Iterator<SparseNFA> concatIt = concatList.iterator();
            Iterator<Boolean> currBoolIt = currBoolList.iterator(); // concatList and currBoolList have same size
            Iterator<Boolean> nextBoolIt = nextBoolList.iterator(); // concatList and nextBoolList have same size
            for (int i = 0; concatIt.hasNext(); i++) {
                if (nextBoolIt.next()) { // this part of the regular expression is optional
                    int pos = r.indexOf("(? ");
                    if (!(pos == 0 && i == 1)) { // if (? ... ) already placed by currBoolIt and r is not a concatenation, don't place (? ... ) otherwise (? (? ... ))
                        r = "(? (. " + r + "))";
                    }
                }
                String rAid = Nfa2Re(concatIt.next(), currBoolIt.next());
                if (!r.isEmpty()) {
                    r = rAid + " " + r; // don't set the concatenation token '.' yet for smaller regular expression
                } else {
                    r = rAid;
                }
            }
            r = "(. " + r + ")";

            r = regexCheckEmptyWord(r, acceptEmpty);
            return r;
        }

        // if concatCheck failed -> search for non-trivial strongly connected components
        boolean nonTrivial = false;
        for (Set<String> SCC : SCCSet) {
            SparseNFA SCC_NFA = AutomatonTools.getSubNFA(A, SCC);
            if (!isTrivial(SCC_NFA)) {
                nonTrivial = true;
                break;
            }
        }

        // if at least one non-trivial strongly connected component -> do trivialize
        if (nonTrivial) {
            SparseNFA APrime = new SparseNFA();
            trivialize(A, SCCSet, APrime);
            r = Nfa2Re(APrime, acceptEmpty);

            r = regexCheckEmptyWord(r, acceptEmpty); // acceptEmpty always remains the same with trivialize()
            return r;
        }

        // if all SCC's are trivial -> split automaton into two sub-automata
        Set<String> I = A.getNextStateValues(Glushkov.INITIAL_STATE);

        // impossible scenario
        if (I.size() < 2) { // this should never happen
            System.out.println("IMPOSSIBLE ERROR: Nfa2Re(): last step, all SCC's trivial and no 2 initial states found");
            System.exit(1);
        }

        // split states I over I1 and I2
        Set<String> I1 = new HashSet<String>();
        Set<String> I2 = new HashSet<String>();
        int choice = 1;
        for (String initStateValue : I) {
            if (choice == 1) {
                I1.add(initStateValue);
            } else { // choice == 2
                I2.add(initStateValue);
            }
            choice = choice % 2 + 1;
        }

        // impossible scenario
        if (I1.isEmpty() || I2.isEmpty()) { // this should never happen
            System.out.println("IMPOSSIBLE ERROR: Nfa2Re(): last step, automaton split in two sub-automata and one of them has no initial states");
            System.exit(1);
        }

        // create subautomaton A1 and A2 using I1 and I2
        SparseNFA A1 = new SparseNFA(A);
        SparseNFA A2 = new SparseNFA(A);
        A1.removeState(Glushkov.INITIAL_STATE); // turn initial states into ordinary states
        A1.setInitialState(Glushkov.INITIAL_STATE);
        for (String newInitStateValue : I1) { // turn I1 into initial states
            A1.addTransition(Symbol.getEpsilon(), A1.getState(Glushkov.INITIAL_STATE), A1.getState(newInitStateValue));
        }
        A2.removeState(Glushkov.INITIAL_STATE); // turn initial states into ordinary states
        A2.setInitialState(Glushkov.INITIAL_STATE);
        for (String newInitStateValue : I2) { // turn I2 into initial states
            A2.addTransition(Symbol.getEpsilon(), A2.getState(Glushkov.INITIAL_STATE), A2.getState(newInitStateValue));
        }

        String r1 = Nfa2Re(A1, false);
        String r2 = Nfa2Re(A2, false);
        r = "(| " + r1 + " " + r2 + ")";

        r = regexCheckEmptyWord(r, acceptEmpty);
        return r;
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
     * checks if a NFA is trivial
     * @param nfa   the input NFA
     * @return      the output boolean
     * @throws NoSuchStateException
     */
    private static boolean isTrivial(StateNFA nfa) throws NoSuchStateException {
        SparseNFA nfa2 = new SparseNFA(nfa);

        // Glushkov.INITIAL_STATE no 'real' part of NFA -> remove
        nfa2.removeState(Glushkov.INITIAL_STATE);

        // trivial if 1 state and no transitions
        boolean trivial;
        if (nfa2.getNumberOfStates() == 1 && nfa2.getNumberOfTransitions() == 0) {
            trivial = true;
        } else {
            trivial = false;
        }

        return trivial;
    }

    /**
     * Searches NFA to find the weakly connected components
     * @param nfa           the input NFA to be searched
     * @return              the set of found weakly connected components
     */
    private static Set<Set<String>> getWeaklyConnectedComponents(StateNFA nfa) {
        Set<Set<String>> WCCSet = new HashSet<Set<String>>();

        Set<String> stateValueSet = nfa.getNextStateValues(Glushkov.INITIAL_STATE); // get set of 'real' initial states
        for (String initStateValue : stateValueSet) { // find all weakly connected components using 'real' initial states
            List<String> BFSList = breadthFirstSearch(nfa, initStateValue);
            Set<String> BFSSet = new HashSet<String>(BFSList);

            // check if BFSSet is a new WCC
            boolean added = false;
            Set<Set<String>> WCCSetNew = new HashSet<Set<String>>();
            for (Set<String> WCC : WCCSet) {
                Set<String> intersection = new HashSet<String>(WCC);
                intersection.retainAll(BFSSet);
                if (intersection.isEmpty()) {
                    WCCSetNew.add(WCC); // WCC remains the same
                } else { // if sets share at least one state -> belong to same WCC -> update WCC
                    if (!added) { // BFSSet not yet added to another WCC
                        WCC.addAll(BFSSet); // union
                        WCCSetNew.add(WCC); // WCC updated
                        BFSSet = WCC;
                        added = true;
                    } else { // BFSSet already added to another WCC -> update the other WCC with this WCC and don't copy this WCC to WCCSetNew
                        BFSSet.addAll(WCC); // BFSSet part of WCCSetNew -> WCCSetNew automatically updated
                    }
                }
            }

            // if BFSSet couldn't be added to existing WCC -> new WCC
            if (!added) {
                WCCSetNew.add(BFSSet);
            }

            // update WCCSet
            WCCSet = WCCSetNew;
        }

        return WCCSet;
    }

    /**
     * Searches nfa to find the strongly connected components
     * @param nfa           the input NFA to be searched
     * @return              the set of found strongly connected components
     * @throws NoSuchStateException
     * @throws NoSuchTransitionException
     */
    private static Set<Set<String>> getStronglyConnectedComponents(StateNFA nfa) throws NoSuchStateException, NoSuchTransitionException {
        Set<Set<String>> SCCSet = new HashSet<Set<String>>();

        // step 1: find states in reversed DFS post-order
        List<String> orderedStateValues = depthFirstSearch(nfa, Glushkov.INITIAL_STATE);
        orderedStateValues.remove(Glushkov.INITIAL_STATE); // Glushkov.INITIAL_STATE no 'real' part of NFA -> remove
        Collections.reverse(orderedStateValues);

        // step 2: find transposed nfa
        SparseNFA nfaTransposed = getTransposedNFA(nfa);
        nfaTransposed.removeState(Glushkov.INITIAL_STATE); // Glushkov.INITIAL_STATE no 'real' part of NFA -> remove

        // step 3: find reachable states for each state in output step 1 -> SCC
        Set<String> stateSCCFound = new HashSet<String>(); // keeps track of all states whose SCC has been found

        // find all SCC's
        for (String stateValue : orderedStateValues) {
            if (stateSCCFound.contains(stateValue)) { // if SCC already found
                continue;
            }
            // List of states in nfaState's SCC
            List<String> stateValueList = depthFirstSearch(nfaTransposed, stateValue);
            Set<String> SCC = new HashSet<String>(stateValueList);
            // remove all found states from nfaState
            for (String state : SCC) {
                stateSCCFound.add(state);
                nfaTransposed.removeState(state);
            }
            SCCSet.add(SCC);
        }

        return SCCSet;
    }

    /**
     * Searches best possible F', I' and F' x I' transitions so F' x I' contained by E (see Nfa2Re algorithm paper line 12)
     * @param nfa                   the input NFA to be searched
     * @param FPrime                an output set of stateValues, empty if no possible F' found
     * @param IPrime                an output set of stateValues, empty if no possible I' found
     * @param FAcIAcTransitionSet   an output set of transitions, empty if no possible F' or I' found
     */
    private static void getFPrimeXIPrime(StateNFA nfa, Set<String> FPrime, Set<String> IPrime, Set<Transition> FAcIAcTransitionSet) {
        Set<String> S = new HashSet<String>(nfa.getStateValues());
        S.remove(Glushkov.INITIAL_STATE); // not a 'real' state
        Set<String> I = new HashSet<String>(nfa.getNextStateValues(Glushkov.INITIAL_STATE)); // Glushkov.INITIAL_STATE state points to the 'real' initial states
        Set<String> F = new HashSet<String>(nfa.getFinalStateValues());

        FPrime.clear(); // F'
        IPrime.clear(); // I'

        // check if F x I subset of E
        boolean failed = false;
        for (String finalStateValue : F) {
            failed = !nfa.getNextStateValues(finalStateValue).containsAll(I);
            if (failed) {
                break;
            }
        }
        if (!failed) {
            FPrime.addAll(F);
            IPrime.addAll(I);
            getFPrimeXIPrime2(nfa, FPrime, IPrime, FAcIAcTransitionSet); // calculate FAcIAcTransitionSet
            return;
        }

        // check if F x I' (with I' subset of S) subset of E
        failed = false;
        IPrime.addAll(S);
        for (String finalStateValue : F) {
            IPrime.retainAll(nfa.getNextStateValues(finalStateValue));
            if (IPrime.isEmpty()) {
                failed = true;
                break;
            }
        }
        if (!failed) {
            FPrime.addAll(F);
            getFPrimeXIPrime2(nfa, FPrime, IPrime, FAcIAcTransitionSet); // calculate FAcIAcTransitionSet
            return;
        } else {
            IPrime.clear();
        }

        // check if F' x I (with F' subset of F) subset of E
        failed = false;
        FPrime.addAll(F);
        for (String initStateValue : I) {
            FPrime.retainAll(nfa.getPreviousStateValues(initStateValue));
            if (FPrime.isEmpty()) {
                failed = true;
                break;
            }
        }
        if (!failed) {
            IPrime.addAll(I);
            getFPrimeXIPrime2(nfa, FPrime, IPrime, FAcIAcTransitionSet); // calculate FAcIAcTransitionSet
            return;
        } else {
            FPrime.clear();
        }

        // check if F' x I' (with F' subset of F and I' subset of S) subset of E
        //TODO can probably be improved for bigger F' and I'
        String maxFinalStateValue = "";
        int maxOutgoingTransitions = 0;
        Map<String, Integer> transitionsFromFinalStates = new HashMap<String, Integer>();
        for (String finalStateValue : F) {
            // look for finalStateValue with the most outgoing transitions
            Set<String> stateValueSet = nfa.getNextStateValues(finalStateValue);
            if (stateValueSet.size() > maxOutgoingTransitions) {
                maxOutgoingTransitions = stateValueSet.size();
                maxFinalStateValue = finalStateValue;
            }

            // for each state, count how many of these outgoing transitions point to it
            for (String stateValue : stateValueSet) {
                if (!transitionsFromFinalStates.containsKey(stateValue)) { // if stateValue not found in Map
                    transitionsFromFinalStates.put(stateValue, 0);
                }
                transitionsFromFinalStates.put(stateValue, transitionsFromFinalStates.get(stateValue) + 1);
            }
        }

        // look for stateValue with the most incoming transitions from Map
        String maxStateValue = "";
        int maxIncomingTransitions = 0;
        for (String stateValue : transitionsFromFinalStates.keySet()) {
            if (transitionsFromFinalStates.get(stateValue) > maxIncomingTransitions) {
                maxIncomingTransitions = transitionsFromFinalStates.get(stateValue);
                maxStateValue = stateValue;
            }
        }

        // fill in F' and I'
        if (maxOutgoingTransitions >= maxIncomingTransitions) { // if maxFinalStateValue (of F') best (= most transitions) state to start with
            FPrime.add(maxFinalStateValue);
            IPrime.addAll(nfa.getNextStateValues(maxFinalStateValue)); // all transitions point to states of I'
            for (String finalStateValue : F) { // I' can't get bigger, so try expanding F'
                if (nfa.getNextStateValues(finalStateValue).containsAll(IPrime)) { // F' x I' subset of E must remain true
                    FPrime.add(finalStateValue);
                }
            }
        } else { // if maxStateValue (of I') best (= most transitions) state to start with
            IPrime.add(maxStateValue);
            Set<String> previousStateValues = nfa.getPreviousStateValues(maxStateValue); // not all transitions come from states of F'
            previousStateValues.retainAll(F); // use only the states which are part of F
            FPrime.addAll(previousStateValues);
            for (String stateValue : S) { // F' can't get bigger, so try expanding I'
                if (nfa.getPreviousStateValues(stateValue).containsAll(FPrime)) { // F' x I' subset of E must remain true
                    IPrime.add(stateValue);
                }
            }
        }

        // possible F' and I' found
        if (!FPrime.isEmpty() && !IPrime.isEmpty()) {
            getFPrimeXIPrime2(nfa, FPrime, IPrime, FAcIAcTransitionSet); // calculate FAcIAcTransitionSet
        } else { // no possible F' or I' found
            FPrime.clear();
            IPrime.clear();
        }

        return;
    }

    private static void getFPrimeXIPrime2(StateNFA nfa, Set<String> FPrime, Set<String> IPrime, Set<Transition> FAcIAcTransitionSet) {
        FAcIAcTransitionSet.clear(); // F' x I' transitions

        FAcIAcTransitionSet.addAll(nfa.getTransitionMap().getTransitions());
        Set<Transition> transitionSetLoop = new HashSet<Transition>(FAcIAcTransitionSet);
        for (Transition transition : transitionSetLoop) { // remove unwanted transitions
            if (!FPrime.contains(nfa.getStateValue(transition.getFromState())) || !IPrime.contains(nfa.getStateValue(transition.getToState()))) {
                FAcIAcTransitionSet.remove(transition);
            }
        }
    }

    /**
     * checks if a NFA consists of a concatenation of strongly connected components, returns list of SCC's as NFA's if true
     * @param A                 the input NFA to be searched
     * @param acceptEmpty       the input boolean, accepts NFA A the empty string?
     * @param concatList        the output list of concatenated SCC's, empty if concatCheck failed
     * @param currBoolList      the output list of acceptEmpty values of the SCC's, empty if concatCheck failed
     * @param nextBoolList      the output list of acceptEmpty values of the following sub-automaton, empty if concatCheck failed
     * @throws NoSuchStateException
     */
    private static void concatCheck(StateNFA A, boolean acceptEmpty, List<SparseNFA> concatList, List<Boolean> currBoolList, List<Boolean> nextBoolList) throws NoSuchStateException {
        SparseNFA A2 = new SparseNFA(A);
        concatList.clear();
        currBoolList.clear();
        nextBoolList.clear();
        concatCheck2(A2, acceptEmpty, concatList, currBoolList, nextBoolList);
    }

    private static void concatCheck2(SparseNFA A, boolean acceptEmpty, List<SparseNFA> concatList, List<Boolean> currBoolList, List<Boolean> nextBoolList) throws NoSuchStateException {
        List<String> S = breadthFirstSearch(A, Glushkov.INITIAL_STATE);
        S.remove(Glushkov.INITIAL_STATE); // not a 'real' state
        Set<String> I = new HashSet<String>(A.getNextStateValues(Glushkov.INITIAL_STATE)); // Glushkov.INITIAL_STATE state points to the 'real' initial states
        Set<String> F = new HashSet<String>(A.getFinalStateValues());
        SparseNFA closure = getClosure(A);

        // for each state: calculate it's SCC if not yet found
        int c = 1; // number of found SCC's
        for (String s1 : S) {
            if (!A.hasState(s1)) { // if state s1 no longer part of A (because it's SCC has been found) -> skip
                continue;
            }

            // calculate I0, assuming s1 is part of F0
            Set<String> I0 = new HashSet<String>();
            for (String s : A.getNextStateValues(s1)) { // for all existing transitions from s1 to s
                if (!closure.getNextStateValues(s).contains(s1)) { // if no path from s to s1 -> part of I0
                    I0.add(s);
                }
            }

            // if I0 empty -> skip
            if (I0.isEmpty()) {
                continue;
            }

            // calculate all reachable states from I-I0 without passing I0 -> S1
            SparseNFA aid = new SparseNFA(A);
            for (String initStateValue : I0) {
                aid.removeState(initStateValue); // all states past I0 become unreachable
            }
            Set<String> S1 = new HashSet<String>(breadthFirstSearch(aid, Glushkov.INITIAL_STATE));
            S1.remove(Glushkov.INITIAL_STATE);

            // S2 complement of S1
            Set<String> S2 = new HashSet<String>(A.getStateValues());
            S2.remove(Glushkov.INITIAL_STATE);
            S2.removeAll(S1);

            // using own method instead of Concat-Check() algorithm lines 7-8
            // calculate F0, using I0
            Set<String> F0 = new HashSet<String>(S1); // F0 must be subset of S1
            for (String initStateValue : I0) { // remove all unnecessary states from F0
                F0.retainAll(A.getPreviousStateValues(initStateValue)); // all states of F0 must have outgoing transitions to all states of I0
            }

            // remove states of F0 that allow path from S2 to S1
            //TODO useful? Or already covered in bridgeTest()?
            Set<String> stateValueSet = new HashSet<String>(F0);
            for (String s : stateValueSet) { // for all states s of F0 (which all have transitions to all states of I0)
                Set<String> reachingStateValueSet = new HashSet<String>(closure.getPreviousStateValues(s)); // states that can reach state s with a path
                reachingStateValueSet.retainAll(S2);
                if (!reachingStateValueSet.isEmpty()) { // if any reaching state part of S2 -> path from S2 to S1 -> remove state s from F0
                    F0.remove(s);
                }
            }

            // if F0 empty -> skip
            if (F0.isEmpty()) {
                continue;
            }

            // if bridge between S1 and S2 -> SCC found, remove SCC from A
            if (bridgeTest(A, acceptEmpty, S1, S2, F0, I0)) {
                SparseNFA Ac = new SparseNFA(A); // the found SCC

                // update states and transitions
                for (String stateValue : S2) { // Ac restricted to S1 -> remove states of S2
                    Ac.removeState(stateValue);
                }
                for (String stateValue : S1) { // A now restricted to S2 -> remove states of S1
                    A.removeState(stateValue);
                }

                // update initial states
                Set<String> IMinusI0 = new HashSet<String>(I);
                IMinusI0.removeAll(I0);
                Ac.removeState(Glushkov.INITIAL_STATE); // removes state and it's transitions
                Ac.setInitialState(Glushkov.INITIAL_STATE);
                for (String newInitStateValue : IMinusI0) { // set new 'real' initial states
                    Ac.addTransition(Symbol.getEpsilon(), Ac.getState(Glushkov.INITIAL_STATE), Ac.getState(newInitStateValue));
                }
                A.removeState(Glushkov.INITIAL_STATE); // removes state and it's transitions
                A.setInitialState(Glushkov.INITIAL_STATE);
                for (String newInitStateValue : I0) { // set new 'real' initial states
                    A.addTransition(Symbol.getEpsilon(), A.getState(Glushkov.INITIAL_STATE), A.getState(newInitStateValue));
                }

                // update final states
                Ac.setFinalStateValues(F0);
                Set<String> FMinusF0 = new HashSet<String>(F);
                FMinusF0.removeAll(F0);
                A.setFinalStateValues(FMinusF0);

                // update acceptEmpty
                boolean acceptEmpty_c;
                if (I.containsAll(I0)) {
                    acceptEmpty_c = true;
                } else {
                    acceptEmpty_c = false;
                }
                if (F.containsAll(F0)) {
                    acceptEmpty = true;
                } else {
                    acceptEmpty = false;
                }

                // update sets of states
                I = new HashSet<String>(A.getNextStateValues(Glushkov.INITIAL_STATE)); // Glushkov.INITIAL_STATE state points to the 'real' initial states
                F = new HashSet<String>(A.getFinalStateValues());
                closure = getClosure(A);

                // add SCC Ac to list
                concatList.add(Ac);
                currBoolList.add(acceptEmpty_c);
                nextBoolList.add(acceptEmpty);
                c++;
            }
        }

        // add remaining SCC A to list
        concatList.add(A);
        currBoolList.add(acceptEmpty);
        nextBoolList.add(false); // last value of list is not relevant

        // if no concatenation found
        if (!(c > 1)) {
            concatList.clear();
            currBoolList.clear();
            nextBoolList.clear();
        }
    }

    /**
     * Attempts to convert every strongly connected component of the input nfa to one or more trivial SCC's
     * @param A                 the input NFA to be searched
     * @param acceptEmpty       the input boolean, accepts NFA A the empty string?
     * @param SCCSet            the input set of SCC's
     * @param APrime            the output NFA, empty if concatCheck failed
     * @throws NoSuchStateException
     * @throws NoSuchTransitionException
     */
    private static void trivialize(StateNFA A, Set<Set<String>> SCCSet, SparseNFA APrime) throws NoSuchStateException, NoSuchTransitionException {
        List<Set<String>> SCCList = new LinkedList<Set<String>>(SCCSet);

        // remove all states from APrime -> empty SparseNFA
        Set<String> stateValueSet = new HashSet<String>(APrime.getStateValues());
        for (String stateValue : stateValueSet) {
            APrime.removeState(stateValue);
        }

        trivialize2(A, SCCList, APrime);
    }

    private static void trivialize2(StateNFA A, List<Set<String>> SCCList, SparseNFA APrime) throws NoSuchStateException, NoSuchTransitionException {
        Set<String> I = A.getNextStateValues(Glushkov.INITIAL_STATE); // Glushkov.INITIAL_STATE state points to the 'real' initial states
        Set<String> F = A.getFinalStateValues();

        // these two lists store #SCC's number of (sub)lists
        // for both list, every SCC has it's own (sub)list
        // the first element of the (sub)list contains the (set of) I/F states contained in it's SCC, but only if any I/F states found
        // the other elements of the (sub)list contain the (set of) Ii/Fi states between it's SCC (i) and another SCC (j), so Fj x Ii or Fi x Ij subset of E
        List<List<Set<String>>> II = new LinkedList<List<Set<String>>>();
        List<List<Set<String>>> FF = new LinkedList<List<Set<String>>>();

        // these sets keep track of transitions
        Set<Transition> origTransitionSet = new HashSet<Transition>(); // transitions between original states
        Map<String, Set<String>> newToOrig = new HashMap<String, Set<String>>(); // key is a new state, value is a set of original states so new state has transitions to original states
        Map<String, Set<String>> origIncOfNew = new HashMap<String, Set<String>>(); // key is a new state, value is a set of original states so original states are incoming states of new state

        // for every SCC: search all initial and final states which are part of this SCC
        for (Set<String> Si : SCCList) {
            List<Set<String>> IIi = new LinkedList<Set<String>>();
            List<Set<String>> FFi = new LinkedList<Set<String>>();
            Set<String> intersection = new HashSet<String>(Si);
            intersection.retainAll(I);
            if (!intersection.isEmpty()) { // intersection may not be empty
                IIi.add(intersection);
            }
            II.add(IIi); // IIi either contains one element or no elements

            intersection = new HashSet<String>(Si);
            intersection.retainAll(F);
            if (!intersection.isEmpty()) { // intersection may not be empty
                FFi.add(intersection);
            }
            FF.add(FFi); // FFi either contains one element or no elements
        }

        // for every SCC: search for all states of this SCC (i) sending or receiving transitions to or from another SCC (j)
        Iterator<List<Set<String>>> FFit = FF.iterator();
        for (Set<String> Si : SCCList) {
            List<Set<String>> FFi = FFit.next();

            Iterator<List<Set<String>>> IIit = II.iterator();
            for (Set<String> Sj : SCCList) {
                List<Set<String>> IIj = IIit.next();
                if (Si.equals(Sj)) { // only compare two different SCC's
                    continue;
                }

                List<Set<String>> FiList = new LinkedList<Set<String>>();
                List<Set<String>> IjList = new LinkedList<Set<String>>();
                Set<Transition> FiIjTransitionSet = new HashSet<Transition>();
                getFiXIj(A, Si, Sj, FiList, IjList, FiIjTransitionSet);
                if (FiList.isEmpty() || IjList.isEmpty()) { // if no transitions from SCCi to SCCj
                    continue;
                }

                for (Set<String> Fi : FiList) {
                    if (!FFi.contains(Fi)) {
                        FFi.add(Fi);
                    }
                }
                for (Set<String> Ij : IjList) {
                    if (!IIj.contains(Ij)) {
                        IIj.add(Ij);
                    }
                }
                origTransitionSet.addAll(FiIjTransitionSet);
            }
        }

        // calculate APrime
        APrime.setInitialState(Glushkov.INITIAL_STATE);

        // add states
        FFit = FF.iterator();
        Iterator<List<Set<String>>> IIit = II.iterator();
        for (Set<String> Si : SCCList) { // for every SCC: search all useful regular expressions, then use the expression as a stateValue of a new state
            List<Set<String>> FFi = FFit.next();
            List<Set<String>> IIi = IIit.next();

            // create automaton
            SparseNFA Si_NFA = AutomatonTools.getSubNFA(A, Si);

            // search all regular expressions of SCCi, using every possible Fi and Ii combination
            for (Set<String> Fi : FFi) {
                for (Set<String> Ii : IIi) {
                    // set initial states for Si_NFA
                    Si_NFA.removeState(Glushkov.INITIAL_STATE); // also removes all transitions to 'real' initial states
                    Si_NFA.setInitialState(Glushkov.INITIAL_STATE);
                    for (String newInitStateValue : Ii) {
                        Si_NFA.addTransition(Symbol.getEpsilon(), Si_NFA.getState(Glushkov.INITIAL_STATE), Si_NFA.getState(newInitStateValue));
                    }

                    // set final states for Si_NFA
                    Si_NFA.setFinalStateValues(Fi);

                    // get regular expression and create new state
                    String regex = Nfa2Re(Si_NFA, false);
                    APrime.addState(regex);

                    // check if new state is an initial state
                    Set<String> intersection = new HashSet<String>(Si);
                    intersection.retainAll(I);
                    if (Ii.equals(intersection)) { // if all initial states of Si_NFA are initial states of the automaton -> initial state
                        APrime.addTransition(Symbol.getEpsilon(), APrime.getState(Glushkov.INITIAL_STATE), APrime.getState(regex));
                    }

                    // check if new state is a final state
                    intersection.clear();
                    intersection.addAll(Si);
                    intersection.retainAll(F);
                    if (Fi.equals(intersection)) { // if all final states of Si_NFA are final states of the automaton -> final state
                        APrime.addFinalState(regex);
                    }

                    // update newToOrig (for all original states s: (regex,s) part of newToOrig if regex' Fi X s part of E)
                    Map<String, Integer> numberOfNewToOrigTransitions = new HashMap<String, Integer>(); // number of transitions from the current new state to an original state
                    for (Transition transition : origTransitionSet) {
                        String fromStateValue = A.getStateValue(transition.getFromState());
                        String toStateValue = A.getStateValue(transition.getToState());
                        if (Fi.contains(fromStateValue)) {
                            if (!numberOfNewToOrigTransitions.containsKey(toStateValue)) { // if toStateValue is not yet in Map
                                numberOfNewToOrigTransitions.put(toStateValue, 0);
                            }
                            numberOfNewToOrigTransitions.put(toStateValue, numberOfNewToOrigTransitions.get(toStateValue) + 1);
                        }
                    }
                    newToOrig.put(regex, new HashSet<String>());
                    for (String stateValue : numberOfNewToOrigTransitions.keySet()) {
                        if (numberOfNewToOrigTransitions.get(stateValue) == Fi.size()) { // if all of Fi's states have an outgoing transition to State state
                            newToOrig.get(regex).add(stateValue);
                        }
                    }

                    // update origIncOfNew (for all original states s: (regex,s) part of origIncOfNew if s part of regex' Ii)
                    origIncOfNew.put(regex, new HashSet<String>());
                    for (String stateValue : Ii) {
                        origIncOfNew.get(regex).add(stateValue);
                    }
                }
            }
        }

        // add transitions, using newToOrig and origIncOfNew (for all new states s1 and s2: s1 transition s2 if newToOrig(s1) contains origIncOfNew(s2))
        Set<String> stateValueSetLoop = new HashSet<String>(APrime.getStateValues());
        stateValueSetLoop.remove(Glushkov.INITIAL_STATE);
        for (String stateValueFrom : stateValueSetLoop) {
            for (String stateValueTo : stateValueSetLoop) {
                if (newToOrig.get(stateValueFrom).containsAll(origIncOfNew.get(stateValueTo))) {
                    APrime.addTransition(Symbol.getEpsilon(), APrime.getState(stateValueFrom), APrime.getState(stateValueTo));
                }
            }
        }
    }

    //////////////////////////////////////////////////
    //////////////// Aiding Functions ////////////////
    //////////////////////////////////////////////////
    /**
     * Breadth-First Search, starting at given state
     * @param nfa               the input NFA to be searched
     * @param initStateValue    the value of the given state
     * @return                  the ordered list of visited states
     */
    private static List<String> breadthFirstSearch(StateNFA nfa, String initStateValue) {
        // List keeps track of States visited by BFS
        List<String> statesList = new LinkedList<String>();

        // Map keeps track of States: undiscovered, discovered or examined by BFS
        Map<Integer, String> statesVisit = new HashMap<Integer, String>();
        for (State state : nfa.getStates()) {
            statesVisit.put(state.getId(), "undiscovered");
        }

        // Queue keeps track of visiting order
        Queue<State> queue = new LinkedList<State>();

        State initState = nfa.getState(initStateValue);
        statesVisit.put(initState.getId(), "discovered");
        queue.add(initState);
        while (queue.size() > 0) {
            State currentState = queue.peek(); // currentState is always discovered
            for (State state : nfa.getNextStates(currentState)) { // append undiscovered next states to queue
                if (statesVisit.get(state.getId()).equals("undiscovered")) {
                    statesVisit.put(state.getId(), "discovered");
                    queue.add(state);
                }
            }
            queue.remove(); // remove first item (= currentState) in queue
            statesVisit.put(currentState.getId(), "examined");
            statesList.add(nfa.getStateValue(currentState)); // add examined states to statesList
        }

        return statesList;
    }

    /**
     * Depth-First Search, starting at given state
     * @param nfa               the input NFA to be searched
     * @param initStateValue    the value of the given state
     * @return                  the ordered list of visited states (in post-order)
     */
    private static List<String> depthFirstSearch(StateNFA nfa, String initStateValue) {
        // List keeps track of States visited by DFS (in post-order)
        List<String> statesList = new LinkedList<String>();

        // Map keeps track of States: undiscovered, discovered or examined by DFS
        Map<Integer, String> statesVisit = new HashMap<Integer, String>();
        for (State state : nfa.getStates()) {
            statesVisit.put(state.getId(), "undiscovered");
        }

        State initState = nfa.getState(initStateValue);
        depthFirstSearch2(nfa, initState, statesList, statesVisit);

        return statesList;
    }

    private static void depthFirstSearch2(StateNFA nfa, State initState, List<String> statesList, Map<Integer, String> statesVisit) {
        Stack<State> stack = new Stack<State>();
        stack.push(initState);
        while (!stack.empty()) {
            State currentState = stack.peek();
            if (statesVisit.get(currentState.getId()).equals("undiscovered")) { // if state undiscovered
                statesVisit.put(currentState.getId(), "discovered");
                for (State state : nfa.getNextStates(currentState)) { // push undiscovered next states on stack
                    if (statesVisit.get(state.getId()).equals("undiscovered")) {
                        stack.push(state);
                    }
                }
            } else if (statesVisit.get(currentState.getId()).equals("discovered")) { // if state discovered
                statesVisit.put(currentState.getId(), "examined");
                statesList.add(nfa.getStateValue(currentState));
                stack.pop();
            } else { // if state examined
                stack.pop();
            }
        }
    }

    /**
     * transposes a NFA, changes direction of transitions, doesn't change symbols, initial states or final states
     * @param nfa       the input NFA
     * @return          the transposed NFA
     * @throws NoSuchTransitionException
     */
    private static SparseNFA getTransposedNFA(StateNFA nfa) throws NoSuchTransitionException {
        SparseNFA nfa2 = new SparseNFA(nfa);

        Set<Transition> transitionSet = new HashSet<Transition>(nfa2.getTransitionMap().getTransitions());
        for (Transition transition : transitionSet) { // change all transitions' direction
            Symbol symbol = transition.getSymbol();
            State fromState = transition.getFromState();
            State toState = transition.getToState();

            int numBefore = nfa2.getNumberOfTransitions();
            nfa2.addTransition(symbol, toState, fromState);
            int numAfter = nfa2.getNumberOfTransitions();
            if (numBefore != numAfter) { // if newly added transition didn't exist yet -> remove old transition
                nfa2.removeTransition(symbol, fromState, toState);
            }
        }

        return nfa2;
    }

    /**
     * calculates the transitive closure of a NFA
     * @param nfa       the input NFA
     * @return          the transitive closure
     */
    private static SparseNFA getClosure(StateNFA nfa) {
        SparseNFA closure = new SparseNFA(nfa);
        Set<String> stateValueSet = new HashSet<String>(nfa.getStateValues());
        stateValueSet.remove(Glushkov.INITIAL_STATE); // Glushkov.INITIAL_STATE state points to the 'real' initial states

        // search all reachable states for every state
        for (String fromStateValue : stateValueSet) {
            // use state "TEMP" with breadthFirstSearch() instead of fromStateValue to check if fromStateValue can reach itself
            SparseNFA nfaAid = new SparseNFA(nfa);
            if (nfaAid.hasState("TEMP")) { // assume no state with stateValue "TEMP"
                return null;
            }
            nfaAid.addState("TEMP"); // add state "TEMP" with same outgoing transitions as fromStateValue
            for (String toStateValue : nfa.getNextStateValues(fromStateValue)) { // add outgoing transitions to state "TEMP" (including self-loop)
                nfaAid.addTransition(Symbol.getEpsilon(), nfaAid.getState("TEMP"), nfaAid.getState(toStateValue));
            }
            List<String> stateValueList = breadthFirstSearch(nfaAid, "TEMP"); // list of reachable states
            stateValueList.remove(0); // remove "TEMP"

            // for all states: add direct transitions to all reachable states
            State fromState = closure.getState(fromStateValue);
            for (String toStateValue : stateValueList) {
                State toState = closure.getState(toStateValue);
                closure.addTransition(Symbol.getEpsilon(), fromState, toState);
            }
        }

        return closure;
    }

    /**
     * Checks if bridge exists in NFA A between automaton S1 with final states F0 and automaton S2 with initial states I0
     * @param nfa           the input NFA
     * @param acceptEmpty   the input boolean, accepts NFA A the empty string?
     * @param S1            the input set of states S1
     * @param S2            the input set of states S2
     * @param F0            the input set of states F0
     * @param I0            the input set of states I0
     * @return              the output boolean
     */
    private static boolean bridgeTest(StateNFA nfa, boolean acceptEmpty, Set<String> S1, Set<String> S2, Set<String> F0, Set<String> I0) {
        Set<String> S = new HashSet<String>(nfa.getStateValues());
        S.remove(Glushkov.INITIAL_STATE); // not a 'real' state
        Set<String> I = nfa.getNextStateValues(Glushkov.INITIAL_STATE); // Glushkov.INITIAL_STATE points to the 'real' initial states
        Set<String> F = nfa.getFinalStateValues();

        // if bridge -> I0 intersect F0 equals empty
        Set<String> intersection = new HashSet<String>(I0);
        intersection.retainAll(F0);
        if (!intersection.isEmpty()) {
            return false;
        }

        // if bridge -> F0 x I0 subset of E (= edges of A)
        for (String fromStateValue : F0) {
            for (String toStateValue : I0) {
                if (!nfa.getNextStateValues(fromStateValue).contains(toStateValue)) {
                    return false;
                }
            }
        }

        // if bridge -> S1 disjoint union S2 equals S (= states of A)
        Set<String> disUnion = new HashSet<String>(S1);
        disUnion.addAll(S2);
        if (!disUnion.equals(S)) {
            return false;
        }
        if (S1.size() + S2.size() != S.size()) {
            return false;
        }

        // if bridge -> no edges from S2 to S1
        for (String fromStateValue : S2) {
            for (String toStateValue : S1) {
                if (nfa.getNextStateValues(fromStateValue).contains(toStateValue)) {
                    return false;
                }
            }
        }

        // if bridge -> no edges from S1 to S2 apart from edges F0 x I0
        for (String fromStateValue : S1) {
            for (String toStateValue : S2) {
                if (nfa.getNextStateValues(fromStateValue).contains(toStateValue) && (!F0.contains(fromStateValue) || !I0.contains(toStateValue))) {
                    return false;
                }
            }
        }

        // if bridge -> F0 intersect F equals empty OR F0 subset of F
        intersection.clear();
        intersection.addAll(F0);
        intersection.retainAll(F);
        if (!intersection.isEmpty() && !F.containsAll(F0)) {
            return false;
        }

        // if bridge -> I0 intersect I equals empty OR I0 subset of I
        intersection.clear();
        intersection.addAll(I0);
        intersection.retainAll(I);
        if (!intersection.isEmpty() && !I.containsAll(I0)) {
            return false;
        }

        // if F0 subset of F AND I0 subset of I THEN acceptEmpty must be true
        if (F.containsAll(F0) && I.containsAll(I0) && acceptEmpty == false) {
            return false;
        }

        // if bridge -> S1 intersect F subset of F0 (ie. no final states before the bridge)
        intersection.clear();
        intersection.addAll(S1);
        intersection.retainAll(F);
        if (!F0.containsAll(intersection)) {
            return false;
        }

        return true;
    }

    /**
     * Searches all, best possible Fi, Ij and Fi x Ij transitions so Fi subset of SCCi, Ij subset of SCCj and Fi x Ij contained by E (see Trivialize algorithm paper line 10)
     * @param nfa                   the input NFA to be searched
     * @param SCCi                  the input set of states SCCi
     * @param SCCj                  the input set of states SCCj
     * @param FiList                an output list of sets of stateValues (= list of Fi's), empty if no possible Fi found
     * @param IjList                an output list of sets of stateValues (= list of Ij's), empty if no possible Ij found
     * @param FiIjTransitionSet     an output set of transitions, empty if no possible Fi or Ij found
     */
    private static void getFiXIj(StateNFA nfa, Set<String> SCCi, Set<String> SCCj, List<Set<String>> FiList, List<Set<String>> IjList, Set<Transition> FiIjTransitionSet) {
        FiList.clear();
        IjList.clear();

        // check if Fi x Ij (with Fi subset of SCCi and Ij subset of SCCj) subset of E
        Set<String> validFinalStateValueSet = new HashSet<String>(); // contains all states of SCCi which have outgoing transitions to SCCj
        for (String finalStateValue : SCCi) {
            Set<String> SCCjAid = new HashSet<String>(SCCj);
            SCCjAid.retainAll(nfa.getNextStateValues(finalStateValue));
            if (!SCCjAid.isEmpty()) {
                validFinalStateValueSet.add(finalStateValue);
            }
        }

        // find all best possible Fi's and Ij's
        //TODO can probably be improved for bigger Fi and Ij
        while (!validFinalStateValueSet.isEmpty()) {
            Set<String> Fi = new HashSet<String>();
            Set<String> Ij = new HashSet<String>(SCCj);
            Set<String> validFinalStateValueSetLoop = new HashSet<String>(validFinalStateValueSet);
            for (String finalStateValue : validFinalStateValueSetLoop) { // find best possible Fi and Ij that are not yet found
                if (Fi.isEmpty()) { // first run through loop
                    Ij.retainAll(nfa.getNextStateValues(finalStateValue));
                    Fi.add(finalStateValue);
                    validFinalStateValueSet.remove(finalStateValue);
                } else if (Ij.equals(nfa.getNextStateValues(finalStateValue))) { // expand Fi with finalStateValue
                    Fi.add(finalStateValue);
                    validFinalStateValueSet.remove(finalStateValue);
                }
            }
            if (!Fi.isEmpty() && !Ij.isEmpty()) { // possible Fi and Ij found
                FiList.add(Fi);
                IjList.add(Ij);
                Set<Transition> newFiIjTransitionSet = new HashSet<Transition>();
                getFiXIj2(nfa, Fi, Ij, newFiIjTransitionSet);
                FiIjTransitionSet.addAll(newFiIjTransitionSet);
            }
        }

        return;
    }

    private static void getFiXIj2(StateNFA nfa, Set<String> Fi, Set<String> Ij, Set<Transition> FiIjTransitionSet) {
        FiIjTransitionSet.clear(); // Fi x Ij transitions

        FiIjTransitionSet.addAll(nfa.getTransitionMap().getTransitions());
        Set<Transition> transitionSetLoop = new HashSet<Transition>(FiIjTransitionSet);
        for (Transition transition : transitionSetLoop) { // remove unwanted transitions
            if (!Fi.contains(nfa.getStateValue(transition.getFromState())) || !Ij.contains(nfa.getStateValue(transition.getToState()))) {
                FiIjTransitionSet.remove(transition);
            }
        }
    }

   
}
