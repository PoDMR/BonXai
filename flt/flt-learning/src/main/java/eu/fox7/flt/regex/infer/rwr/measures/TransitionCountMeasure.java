/*
 * Created on Sep 11, 2008
 * Modified on $Date: 2009-10-27 09:30:13 $
 */
package eu.fox7.flt.regex.infer.rwr.measures;

import eu.fox7.flt.regex.infer.rwr.AutomatonRewriter;
import eu.fox7.flt.regex.infer.rwr.impl.Automaton;
import eu.fox7.flt.regex.infer.rwr.impl.Opportunity;

/**
 * @author eu.fox7
 * @version $Revision: 1.2 $
 * 
 */
public class TransitionCountMeasure implements OpportunityMeasure {

    public Opportunity measure(Automaton oldAutomaton, Automaton newAutomaton,
                               AutomatonRewriter repairer,
                               AutomatonRewriter rewriter,
                               int[] indices) {
        return new Opportunity(indices, compute(oldAutomaton, newAutomaton),
                               repairer, rewriter);
    }

    protected int compute(Automaton oldAutomaton, Automaton newAutomaton) {
        int count = 0;
        for (int i = 0; i < oldAutomaton.getNumberOfStates(); i++)
            for (int j = 0; j < oldAutomaton.getNumberOfStates(); j++)
                if (newAutomaton.get(i, j) != 0 && oldAutomaton.get(i, j) == 0)
                    count++;
        return count;
    }

}
