/*
 * Created on Sep 9, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package gjb.flt.regex.infer.rwr.repairers;

import gjb.flt.regex.infer.rwr.impl.Automaton;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class OptionalConcatRepairFinder extends ConcatOptionalRepairFinder {

    @Override
    protected boolean preconditionHolds(Automaton automaton, int i, int j) {
        if (automaton.get(i, j) == 0)
            return false;
        int count = 0;
        for (int k = 0; k < automaton.getNumberOfStates(); k++)
            if (automaton.get(i, k) != 0)
                count++;
        return count == 1;
    }

}
