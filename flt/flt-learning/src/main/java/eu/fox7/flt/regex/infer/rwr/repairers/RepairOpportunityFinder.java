/*
 * Created on Sep 9, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package eu.fox7.flt.regex.infer.rwr.repairers;

import eu.fox7.flt.regex.infer.rwr.impl.Automaton;
import eu.fox7.flt.regex.infer.rwr.rewriters.OpportunityFinder;

import java.util.List;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public interface RepairOpportunityFinder extends OpportunityFinder {

    public List<int[]> getAll(Automaton automaton);

}
