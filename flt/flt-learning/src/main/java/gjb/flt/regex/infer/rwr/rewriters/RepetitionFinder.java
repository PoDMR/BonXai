/*
 * Created on Sep 4, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package gjb.flt.regex.infer.rwr.rewriters;

import gjb.flt.regex.infer.rwr.impl.Automaton;


/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class RepetitionFinder implements OpportunityFinder {

    public int[] getFirst(Automaton automaton) {
        for (int i = 0; i < automaton.getNumberOfStates() - 1; i++)
            if (automaton.get(i, i) != 0)
                return new int[] {i};
        return null;
    }

}
