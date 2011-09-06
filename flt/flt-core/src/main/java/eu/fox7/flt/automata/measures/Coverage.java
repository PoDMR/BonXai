/*
 * Created on May 28, 2007
 * Modified on $Date: 2009-11-12 09:10:15 $
 */
package eu.fox7.flt.automata.measures;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateDFA;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.automata.impl.sparse.Transition;


/**
 * @author eu.fox7
 * @version $Revision: 1.3 $
 * 
 */
public class Coverage {

    public static double compute(StateNFA nfa, String[][] sample)
            throws NotDFAException {
        Map<Transition,Integer> covered = computeCoverage(nfa, sample);
        return computeCoverage(covered);
    }

    public static double compute(StateNFA nfa, List<String[]> sample)
    throws NotDFAException {
        Map<Transition,Integer> covered = computeCoverage(nfa, sample);
        return computeCoverage(covered);
    }
    
    public static boolean isCovered(StateNFA nfa, String[][] sample)
            throws NotDFAException {
        Map<Transition,Integer> covered = computeCoverage(nfa, sample);
        return isCovered(covered);
    }

    public static boolean isCovered(StateNFA nfa, List<String[]> sample)
    throws NotDFAException {
        Map<Transition,Integer> covered = computeCoverage(nfa, sample);
        return isCovered(covered);
    }
    
    protected static Map<Transition, Integer> computeCoverage(StateNFA nfa,
                                                              String[][] sample)
            throws NotDFAException {
        Map<Transition,Integer> covered = new HashMap<Transition,Integer>();
        for (Transition transition : nfa.getTransitionMap().getTransitions())
            covered.put(transition, 0);
        if (nfa.isFinalState(nfa.getInitialState()))
            covered.put(new Transition(Symbol.getEpsilon(), nfa.getInitialState(), nfa.getInitialState()), 0);
        for (int exampleNr = 0; exampleNr < sample.length; exampleNr++) {
            State fromState = nfa.getInitialState();
            if (sample[exampleNr].length == 0) {
                if (nfa.isFinalState(fromState)) {
                    Transition transition = new Transition(Symbol.getEpsilon(), fromState, fromState);
                    covered.put(transition, covered.get(transition) + 1);
                }
            } else {
                for (int i = 0; i < sample[exampleNr].length; i++) {
                    Symbol symbol = Symbol.create(sample[exampleNr][i]);
                    State toState = ((StateDFA) nfa).getNextState(symbol, fromState);
                    Transition transition = new Transition(symbol, fromState, toState);
                    covered.put(transition, covered.get(transition) + 1);
                    fromState = toState;
                }
            }
        }
        return covered;
    }

    protected static Map<Transition, Integer> computeCoverage(StateNFA nfa,
                                                              List<String[]> sample)
                                                              throws NotDFAException {
        Map<Transition,Integer> covered = new HashMap<Transition,Integer>();
        for (Transition transition : nfa.getTransitionMap().getTransitions())
            covered.put(transition, 0);
        for (int exampleNr = 0; exampleNr < sample.size(); exampleNr++) {
            State fromState = nfa.getInitialState();
            for (int i = 0; i < sample.get(exampleNr).length; i++) {
                Symbol symbol = Symbol.create(sample.get(exampleNr)[i]);
                State toState = ((StateDFA) nfa).getNextState(symbol, fromState);
                Transition transition = new Transition(symbol, fromState, toState);
                covered.put(transition, covered.get(transition) + 1);
                fromState = toState;
            }
        }
        return covered;
    }
    
    protected static boolean isCovered(Map<Transition,Integer> covered) {
        for (Transition transition : covered.keySet())
            if (covered.get(transition) == 0)
                return false;
        return true;
    }

    protected static double computeCoverage(Map<Transition,Integer> covered) {
        double coverage = 0.0;
        for (Transition transition : covered.keySet())
            if (covered.get(transition) > 0)
                coverage += 1.0;
        return coverage/covered.size();
    }

}
