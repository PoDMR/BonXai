/*
 * Created on Sep 10, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package eu.fox7.flt.regex.infer.rwr.repairers;

import eu.fox7.flt.regex.infer.rwr.impl.Automaton;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class OptionalConcatOptionalRepairFinder extends
        ConcatOptionalRepairFinder {

    @Override
    protected boolean preconditionHolds(Automaton automaton, int i, int j) {
        return automaton.get(i, j) != 0 && automaton.get(j, i) == 0;
    }

}
