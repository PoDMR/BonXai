/*
 * Created on Sep 11, 2008
 * Modified on $Date: 2009-10-27 09:30:13 $
 */
package eu.fox7.flt.regex.infer.rwr.impl;


import eu.fox7.flt.regex.infer.rwr.AutomatonRewriter;
import eu.fox7.flt.regex.infer.rwr.repairers.ConcatOptionalRepairer;
import eu.fox7.flt.regex.infer.rwr.repairers.DisjunctionRepairer;
import eu.fox7.flt.regex.infer.rwr.repairers.OptionalConcatOptionalRepairer;
import eu.fox7.flt.regex.infer.rwr.repairers.OptionalConcatRepairer;

import java.util.Comparator;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class OpportunityComparator implements Comparator<Opportunity> {

    public int compare(Opportunity o1, Opportunity o2) {
        double m1 = o1.getMeasure();
        double m2 = o2.getMeasure();
        if (m1 < m2)
            return -1;
        if (m1 > m2)
            return 1;
        else {
            int r1 = rankRepairer(o1.getRepairer());
            int r2 = rankRepairer(o2.getRepairer());
            if (r1 < r2)
                return -1;
            else if (r2 > r1)
                return 1;
            else {
                int i1 = o1.getIndices()[0];
                int i2 = o2.getIndices()[0];
                if (i1 < i2)
                    return -1;
                else if (i1 > i2)
                    return 1;
                else {
                    i1 = o1.getIndices()[1];
                    i2 = o2.getIndices()[1];
                    if (i1 < i2)
                        return -1;
                    else if (i1 > i2)
                        return 1;
                    else
                        return 0;
                }
            }
        }
    }

    protected int rankRepairer(AutomatonRewriter repairer) {
        if (repairer instanceof ConcatOptionalRepairer)
            return 1;
        else if (repairer instanceof OptionalConcatRepairer)
            return 2;
        else if (repairer instanceof OptionalConcatOptionalRepairer)
            return 3;
        else if (repairer instanceof DisjunctionRepairer)
            return 4;
        else
            throw new RuntimeException("unknown AutomatonRewriter type");
    }

}
