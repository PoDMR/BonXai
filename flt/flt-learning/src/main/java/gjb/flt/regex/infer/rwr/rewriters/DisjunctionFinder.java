/*
 * Created on Sep 4, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package gjb.flt.regex.infer.rwr.rewriters;

import gjb.flt.regex.Regex;
import gjb.flt.regex.infer.rwr.impl.Automaton;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class DisjunctionFinder implements OpportunityFinder {

    public int[] getFirst(Automaton automaton) {
        /*
         * iterate over all pairs of states, excluding final state, for
         * disjunction, the order is irrelevant
         */
        for (int i = 0; i < automaton.getNumberOfStates() - 1; i++) {
            for (int j = i + 1; j < automaton.getNumberOfStates() - 1; j++) {
                if (preconditionHolds(automaton, i, j))
                    return new int[] {i, j};
            }
        }
        return null;
    }

    /*
     * pred(i) = pred(j) && succ(i) = succ(j) or
     * epred(i) = epred(j) && esucc(i) = esucc(j)
     */
    protected boolean preconditionHolds(Automaton automaton, int i, int j) {
        for (int k = 0; k < automaton.getNumberOfStates(); k++) {
            if (k != i && k != j) {
                if (automaton.get(k, i) != automaton.get(k, j))
                    return false;
                if (automaton.get(i, k) != automaton.get(j, k))
                    return false;
            }
        }
        if (automaton.get(i, j) != 0 || automaton.get(j, i) != 0 ||
                automaton.get(i, i) != 0 || automaton.get(j, j) != 0)
            if (hasSelfloop(automaton, i) == 0 || hasSelfloop(automaton, j) == 0 ||
                    automaton.get(i, j) == 0 || automaton.get(j, i) == 0)
                return false;
        return true;
    }

    protected int hasSelfloop(Automaton automaton, int i) {
        return automaton.get(i,i) != 0 ||
            automaton.getLabel(i).startsWith(Regex.LEFT_BRACKET + Regex.ONE_OR_MORE_OPERATOR) ? 1 : 0;
    }

}
