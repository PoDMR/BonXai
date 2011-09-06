/*
 * Created on Sep 4, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package eu.fox7.flt.regex.infer.rwr.rewriters;

import eu.fox7.flt.regex.infer.rwr.impl.Automaton;


/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class ConcatFinder implements OpportunityFinder {

    public int[] getFirst(Automaton automaton) {
        for (int i = 0; i < automaton.getNumberOfStates() - 1; i++)
            for (int j = 0; j < automaton.getNumberOfStates() - 1; j++)
                if (i != j  && preconditionHolds(automaton, i, j))
                    return new int[] {i, j};
        return null;
    }

    protected boolean preconditionHolds(Automaton automaton, int i, int j) {
        for (int k = 0; k < j; k++)
            if (automaton.get(i, k) != 0)
                return false;
        if (automaton.get(i, j) == 0)
            return false;
        for (int k = j + 1; k < automaton.getNumberOfStates(); k++)
            if (automaton.get(i, k) != 0)
                return false;
        for (int k = 0; k < i; k++)
            if (automaton.get(k, j) != 0)
                return false;
        if (automaton.get(i, j) == 0)
            return false;
        for (int k = i + 1; k < automaton.getNumberOfStates(); k++)
            if (automaton.get(k, j) != 0)
                return false;
        return true;
    }

}
