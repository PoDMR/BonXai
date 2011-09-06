/*
 * Created on Sep 5, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package eu.fox7.flt.regex.infer.rwr.rewriters;

import eu.fox7.flt.regex.infer.rwr.impl.Automaton;


/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class OptionalFinder implements OpportunityFinder {

    public int[] getFirst(Automaton automaton) {
        if (automaton.getNumberOfStates() == 2) {
            if (automaton.acceptsEpsilon())
                return new int[] {0};
        }
        return null;
    }

}
