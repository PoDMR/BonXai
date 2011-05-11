/*
 * Created on Sep 7, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package gjb.flt.regex.infer.rwr.rewriters;

import gjb.flt.regex.Regex;
import gjb.flt.regex.infer.rwr.AutomatonRewriter;
import gjb.flt.regex.infer.rwr.impl.Automaton;
import gjb.flt.regex.infer.rwr.impl.GraphAutomaton;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class ConcatRewriter implements AutomatonRewriter {

    public Automaton rewrite(Automaton automaton, int[] indices) {
        Automaton newAutomaton = new GraphAutomaton(automaton.getNumberOfStates() - 2);
        int i = indices[0], j = indices[1];
        int writePos = Math.min(i, j);
        int skipPos = Math.max(i, j);
        for (int k = 0; k < writePos; k++) {
            for (int l = 0; l < writePos; l++)
                newAutomaton.set(k, l, automaton.get(k, l));
            newAutomaton.set(k, writePos, automaton.get(k, i));
            for (int l = writePos + 1; l < skipPos; l++)
                newAutomaton.set(k, l, automaton.get(k, l));
            for (int l = skipPos + 1; l < automaton.getNumberOfStates(); l++)
                newAutomaton.set(k, l - 1, automaton.get(k, l));
        }
        for (int l = 0; l < writePos; l++)
            newAutomaton.set(writePos, l, automaton.get(j, l));
        newAutomaton.set(writePos, writePos, automaton.get(j, i));
        for (int l = writePos + 1; l < skipPos; l++)
            newAutomaton.set(writePos, l, automaton.get(j, l));
        for (int l = skipPos + 1; l < automaton.getNumberOfStates(); l++)
            newAutomaton.set(writePos, l - 1, automaton.get(j, l));
        for (int k = writePos + 1; k < skipPos; k++) {
            for (int l = 0; l < writePos; l++)
                newAutomaton.set(k, l, automaton.get(k, l));
            newAutomaton.set(k, writePos, automaton.get(k, i));
            for (int l = writePos + 1; l < skipPos; l++)
                newAutomaton.set(k, l, automaton.get(k, l));
            for (int l = skipPos + 1; l < automaton.getNumberOfStates(); l++)
                newAutomaton.set(k, l - 1, automaton.get(k, l));
        }
        for (int k = skipPos + 1; k < automaton.getNumberOfStates(); k++) {
            for (int l = 0; l < writePos; l++)
                newAutomaton.set(k - 1, l, automaton.get(k, l));
            newAutomaton.set(k - 1, writePos, automaton.get(k, i));
            for (int l = writePos + 1; l < skipPos; l++)
                newAutomaton.set(k - 1, l, automaton.get(k, l));
            for (int l = skipPos + 1; l < automaton.getNumberOfStates(); l++)
                newAutomaton.set(k - 1, l - 1, automaton.get(k, l));
        }
        if (automaton.get(j, i) != 0)
            newAutomaton.set(writePos, writePos, 1);
        for (int k = 0; k < writePos; k++)
            newAutomaton.setLabel(k, automaton.getLabel(k));
        newAutomaton.setLabel(writePos, makeLabel(automaton, i, j));
        for (int k = writePos + 1; k < skipPos; k++)
            newAutomaton.setLabel(k, automaton.getLabel(k));
        for (int k = skipPos + 1; k < automaton.getNumberOfStates(); k++)
            newAutomaton.setLabel(k - 1, automaton.getLabel(k));
        return newAutomaton;
    }

    protected String makeLabel(Automaton automaton, int i, int j) {
        return Regex.LEFT_BRACKET + Regex.CONCAT_OPERATOR + " " +
            automaton.getLabel(i) + " " + automaton.getLabel(j) +
            Regex.RIGHT_BRACKET;
    }

}
