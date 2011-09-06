/*
 * Created on Sep 6, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package eu.fox7.flt.regex.infer.rwr.rewriters;

import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.infer.rwr.AutomatonRewriter;
import eu.fox7.flt.regex.infer.rwr.impl.Automaton;
import eu.fox7.flt.regex.infer.rwr.impl.GraphAutomaton;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class RepetitionRewriter implements AutomatonRewriter {

    public Automaton rewrite(Automaton automaton, int[] indices) {
        Automaton newAutomaton = new GraphAutomaton(automaton.getNumberOfStates() - 1);
        int i = indices[0];
        for (int k = 0; k < automaton.getNumberOfStates(); k++)
            for (int l = 0; l < automaton.getNumberOfStates(); l++)
                newAutomaton.set(k, l, automaton.get(k, l));
        newAutomaton.set(i, i, 0);
        for (int k = 0; k < automaton.getNumberOfStates(); k++)
            if (k != i)
                newAutomaton.setLabel(k, automaton.getLabel(k));
        newAutomaton.setLabel(i, makeLabel(automaton, i));
        return newAutomaton;
    }

    protected String makeLabel(Automaton automaton, int i) {
        return Regex.LEFT_BRACKET + Regex.ONE_OR_MORE_OPERATOR + " " +
            automaton.getLabel(i) + Regex.RIGHT_BRACKET;
    }

}
