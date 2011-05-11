/*
 * Created on Sep 6, 2008
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
public class DisjunctionRewriter implements AutomatonRewriter {

    public Automaton rewrite(Automaton automaton, int[] indices) {
        int i = Math.min(indices[0], indices[1]);
        int j = Math.max(indices[0], indices[1]);
        Automaton newAutomaton = new GraphAutomaton(automaton.getNumberOfStates() - 2);
        int iIndex = 0, jIndex = 0;
        for (int k = 0; k < automaton.getNumberOfStates(); k++) {
            if (k == j)
                continue;
            jIndex = 0;
            for (int l = 0; l < automaton.getNumberOfStates(); l++) {
                if (l == j)
                    continue;
                newAutomaton.set(iIndex, jIndex, automaton.get(k, l));
                jIndex++;
            }
            iIndex++;
        }
        if (automaton.get(i, j) != 0)
            newAutomaton.set(i, i, 1);
        iIndex = 0;
        for (int k = 0; k < automaton.getNumberOfStates() - 1; k++) {
            if (k == j)
                continue;
            if (k == i) {
                iIndex++;
                continue;
            }
            newAutomaton.setLabel(iIndex++, automaton.getLabel(k));
        }
        newAutomaton.setLabel(i, makeLabel(automaton, i, j));
        return newAutomaton;
    }

    protected String makeLabel(Automaton automaton, int i, int j) {
        return Regex.LEFT_BRACKET + Regex.UNION_OPERATOR + " " +
            automaton.getLabel(i) + " " + automaton.getLabel(j) +
            Regex.RIGHT_BRACKET;
    }

}
