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
public interface OpportunityMeasure {

    public Opportunity measure(Automaton oldAutomaton,
                               Automaton newAutomaton,
                               AutomatonRewriter repairer,
                               AutomatonRewriter rewriter,
                               int[] indices);

}
