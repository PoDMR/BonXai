/*
 * Created on Sep 10, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package gjb.flt.regex.infer.rwr.repairers;

import gjb.flt.regex.Regex;
import gjb.flt.regex.infer.rwr.AutomatonRewriter;
import gjb.flt.regex.infer.rwr.impl.Automaton;
import gjb.flt.regex.infer.rwr.impl.GraphAutomaton;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class OptionalConcatRepairer implements AutomatonRewriter {

    public Automaton rewrite(Automaton automaton, int[] indices) {
        Automaton newAutomaton = new GraphAutomaton(automaton.getNumberOfStates() - 1);
        int firstIndex = Math.min(indices[0], indices[1]);
        int lastIndex = Math.max(indices[0], indices[1]);
        int i = indices[0];
        int j = indices[1];
        for (int k = 0; k < automaton.getNumberOfStates(); k++) {
            /* copy all upto first index (i or j) */
            for (int l = 0; l < firstIndex; l++)
                newAutomaton.set(k, l, automaton.get(k, l));
            if (automaton.get(k, j) != 0 && k != i)
                newAutomaton.set(k, i, 1);
            else
                newAutomaton.set(k, i, automaton.get(k, i));
            /* copy all between first and last index */
            for (int l = firstIndex + 1; l < lastIndex; l++)
                newAutomaton.set(k, l, automaton.get(k, l));
            if (automaton.get(k, i) != 0)
                newAutomaton.set(k, j, 1);
            else
                newAutomaton.set(k, j, automaton.get(k, j));
            /* copy all from last index (i or j) */
            for (int l = lastIndex + 1; l < automaton.getNumberOfStates(); l++)
                newAutomaton.set(k, l, automaton.get(k, l));
        }
        if (automaton.get(j, i) != 0)
            if (hasSelfloop(automaton, j) == 0)
                newAutomaton.set(j, j, 1);
        for (int k = 0; k < automaton.getNumberOfStates() - 1; k++)
            newAutomaton.setLabel(k, automaton.getLabel(k));
        return newAutomaton;
    }

    protected int hasSelfloop(Automaton automaton, int i) {
        return automaton.get(i,i) != 0 ||
            automaton.getLabel(i).startsWith(Regex.LEFT_BRACKET + Regex.ONE_OR_MORE_OPERATOR) ? 1 : 0;
    }

}
