/*
 * Created on Sep 5, 2008
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
public class OptionalConcatFinder implements OpportunityFinder {

    public int[] getFirst(Automaton automaton) {
        for (int i = 0; i < automaton.getNumberOfStates() - 1; i++)
            for (int j = 0; j < automaton.getNumberOfStates() - 1; j++)
                if (i != j  && preconditionHolds(automaton, i, j))
                    return new int[] {i, j};
        return null;
    }

    protected boolean preconditionHolds(Automaton automaton, int i, int j) {
        /* succ(i) = {j} */
        if (automaton.get(i, j) == 0)
            return false;
        for (int k = 0; k < j; k++)
            if (automaton.get(i, k) != 0)
                return false;
        for (int k = j + 1; k < automaton.getNumberOfStates(); k++)
            if (automaton.get(i, k) != 0)
                return false;
        /* pred(i)\{i,j} = pred(j)\{i,j} */
        for (int k = 0; k < automaton.getNumberOfStates(); k++)
            if (k != i && k != j)
                if (automaton.get(k, i) != automaton.get(k, j))
                    return false;
        /* j in pred(i) => j in epred(j) */
        if (automaton.get(j, i) != 0 && hasSelfloop(automaton, j) == 0)
            return false;
        /* j in pred(j) => j in pred(i) */
        if (automaton.get(j, j) != 0 && automaton.get(j, i) == 0)
            return false;
        return true;
    }

    protected int hasSelfloop(Automaton automaton, int i) {
        return automaton.get(i,i) != 0 ||
            automaton.getLabel(i).startsWith(Regex.LEFT_BRACKET + Regex.ONE_OR_MORE_OPERATOR) ? 1 : 0;
    }

}
