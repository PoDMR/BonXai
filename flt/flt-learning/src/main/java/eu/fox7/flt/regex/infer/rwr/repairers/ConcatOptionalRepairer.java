/*
 * Created on Sep 9, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package eu.fox7.flt.regex.infer.rwr.repairers;

import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.infer.rwr.AutomatonRewriter;
import eu.fox7.flt.regex.infer.rwr.impl.Automaton;
import eu.fox7.flt.regex.infer.rwr.impl.GraphAutomaton;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class ConcatOptionalRepairer implements AutomatonRewriter {

    public Automaton rewrite(Automaton automaton, int[] indices) {
        Automaton newAutomaton = new GraphAutomaton(automaton.getNumberOfStates() - 1);
        int i = indices[0];
        int j = indices[1];
        int firstIndex = Math.min(indices[0], indices[1]);
        int lastIndex = Math.max(indices[0], indices[1]);
        /* copy all upto first index (either i or j) */
        for (int k = 0; k < firstIndex; k++)
            for (int l = 0; l < automaton.getNumberOfStates(); l++)
                newAutomaton.set(k, l, automaton.get(k, l));
        /* set row i = succ(i) cup succ(j) */
        for (int l = 0; l < automaton.getNumberOfStates(); l++)
            if (automaton.get(j, l) != 0)
                newAutomaton.set(i, l, 1);
            else
                newAutomaton.set(i, l, automaton.get(i, l));
        /* copy all between indices i and j */
        for (int k = firstIndex + 1; k < lastIndex; k++)
            for (int l = 0; l < automaton.getNumberOfStates(); l++)
                newAutomaton.set(k, l, automaton.get(k, l));
        /* set row j = succ(i) cup succ(j) */
        for (int l = 0; l < automaton.getNumberOfStates(); l++)
            if (automaton.get(i, l) != 0 && l != j)
                newAutomaton.set(j, l, 1);
            else
                newAutomaton.set(j, l, automaton.get(j, l));
        /* copy all from last index onwards (either i or j) */
        for (int k = lastIndex + 1; k < automaton.getNumberOfStates(); k++)
            for (int l = 0; l < automaton.getNumberOfStates(); l++)
                newAutomaton.set(k, l, automaton.get(k, l));
        /* fix self loops */
        if (automaton.get(j, i) != 0)
            if (hasSelfloop(automaton, i) == 0)
                newAutomaton.set(i, i, 1);
        for (int k = 0; k < automaton.getNumberOfStates() - 1; k++)
            newAutomaton.setLabel(k, automaton.getLabel(k));
        return newAutomaton;
    }

    protected int hasSelfloop(Automaton automaton, int i) {
        return automaton.get(i,i) != 0 ||
            automaton.getLabel(i).startsWith(Regex.LEFT_BRACKET + Regex.ONE_OR_MORE_OPERATOR) ? 1 : 0;
    }

}
