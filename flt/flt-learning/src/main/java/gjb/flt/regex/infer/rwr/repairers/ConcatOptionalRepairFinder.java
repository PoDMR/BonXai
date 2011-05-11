/*
 * Created on Sep 9, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package gjb.flt.regex.infer.rwr.repairers;

import gjb.flt.regex.infer.rwr.impl.Automaton;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class ConcatOptionalRepairFinder implements RepairOpportunityFinder {

    public List<int[]> getAll(Automaton automaton) {
        List<int[]> indices = new ArrayList<int[]>();
        int i = 0, j = 1;
        for (;;) {
            int[] result = getFirst(automaton, i, j);
            if (result != null) {
                i = result[0];
                j = result[1] + 1;
                indices.add(result);
            } else {
                break;
            }
        }
        return indices;
    }

    public int[] getFirst(Automaton automaton) {
        return getFirst(automaton, 0, 1);
    }

    protected int[] getFirst(Automaton automaton, int iStart, int jStart) {
        for (int i = iStart; i < automaton.getNumberOfStates() - 1; i++)
            for (int j = (i == iStart ? jStart : 0); j < automaton.getNumberOfStates() - 1; j++)
                if (i != j && preconditionHolds(automaton, i, j))
                    return new int[] {i, j};
        return null;
    }

    protected boolean preconditionHolds(Automaton automaton, int i, int j) {
        if (automaton.get(i, j) == 0)
            return false;
        int count = 0;
        for (int k = 0; k < automaton.getNumberOfStates(); k++)
            if (automaton.get(k, j) != 0)
                count++;
        return count == 1;
    }

}
