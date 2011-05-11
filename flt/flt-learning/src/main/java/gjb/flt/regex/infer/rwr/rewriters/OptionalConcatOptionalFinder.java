/*
 * Created on Sep 5, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package gjb.flt.regex.infer.rwr.rewriters;

import gjb.flt.regex.infer.rwr.impl.Automaton;


/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class OptionalConcatOptionalFinder implements OpportunityFinder {

    public int[] getFirst(Automaton automaton) {
        for (int i = 0; i < automaton.getNumberOfStates() - 1; i++)
            for (int j = 0; j < automaton.getNumberOfStates() - 1; j++)
                if (i != j  && preconditionHolds(automaton, i, j))
                    return new int[] {i, j};
        return null;
    }

    protected boolean preconditionHolds(Automaton automaton, int i, int j) {
        /* i not in succ(j) */
        if (automaton.get(j, i) != 0)
            return false;
        /* succ(i) = succ(j) cup {j} */
        for (int k = 0; k < j; k++)
            if (automaton.get(i, k) != automaton.get(j, k))
                return false;
        if (automaton.get(i, j) == 0)
            return false;
        for (int k = j + 1; k < automaton.getNumberOfStates(); k++)
            if (automaton.get(i, k) != automaton.get(j, k))
                return false;
        /* pred(j) = pred(i) cup {i} */
        for (int k = 0; k < i; k++)
            if (automaton.get(k, j) != automaton.get(k, i))
                return false;
        for (int k = i + 1; k < automaton.getNumberOfStates(); k++)
            if (automaton.get(k, j) != automaton.get(k, i))
                return false;
        return predecessorCheck(automaton, i, j);
    }

    protected boolean predecessorCheck(Automaton automaton, int i, int j) {
        /* pred(i) x succ(j) present */
        for (int k = 0; k < automaton.getNumberOfStates(); k++)
            if (automaton.get(k, i) != 0)
                for (int l = 0; l < automaton.getNumberOfStates(); l++)
                    if (automaton.get(j, l) != 0)
                        if (automaton.get(k, l) == 0)
                            return false;
        return true;
    }

}
