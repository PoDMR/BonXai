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
public class DisjunctionRepairer implements AutomatonRewriter {

    public Automaton rewrite(Automaton automaton, int[] indices) {
        Automaton newAutomaton = new GraphAutomaton(automaton.getNumberOfStates() - 1);
        int i = Math.min(indices[0], indices[1]);
        int j = Math.max(indices[0], indices[1]);
        for (int k = 0; k < i; k++)
            copyRow(automaton, newAutomaton, i, j, k);
        for (int l = 0; l < automaton.getNumberOfStates(); l++)
            newAutomaton.set(i, l, compute(automaton, i, l, j, l));
        for (int k = i + 1; k < j; k++)
            copyRow(automaton, newAutomaton, i, j, k);
        for (int l = 0; l < automaton.getNumberOfStates(); l++)
            newAutomaton.set(j, l, compute(automaton, i, l, j, l));
        for (int k = j + 1; k < automaton.getNumberOfStates(); k++)
            copyRow(automaton, newAutomaton, i, j, k);
        for (int k = 0; k < automaton.getNumberOfStates() - 1; k++)
            newAutomaton.setLabel(k, automaton.getLabel(k));
        if (automaton.get(i, j) != 0 || automaton.get(j, i) != 0)
            fill(newAutomaton, i, j);
        return newAutomaton;
    }

    protected void fill(Automaton newAutomaton, int i, int j) {
        newAutomaton.set(i, j, 1);
        newAutomaton.set(j, i, 1);
        if (!newAutomaton.getLabel(i).startsWith(Regex.LEFT_BRACKET + Regex.ONE_OR_MORE_OPERATOR))
            newAutomaton.set(i, i, 1);
        if (!newAutomaton.getLabel(j).startsWith(Regex.LEFT_BRACKET + Regex.ONE_OR_MORE_OPERATOR))
            newAutomaton.set(j, j, 1);
    }

    protected void copyRow(Automaton automaton, Automaton newAutomaton, int i,
                          int j, int row) {
        for (int l = 0; l < i; l++)
            newAutomaton.set(row, l, automaton.get(row, l));
        newAutomaton.set(row, i, compute(automaton, row, i, row, j));
        for (int l = i + 1; l < j; l++)
            newAutomaton.set(row, l, automaton.get(row, l));
        newAutomaton.set(row, j, compute(automaton, row, i, row, j));
        for (int l = j + 1; l < automaton.getNumberOfStates(); l++)
            newAutomaton.set(row, l, automaton.get(row, l));
    }

    protected int compute(Automaton automaton, int i, int j, int k, int l) {
        if (automaton.get(i, j) != 0 || automaton.get(k, l) != 0)
            return 1;
        else
            return 0;
    }

}
