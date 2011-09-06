/*
 * Created on Jan 17, 2007
 * Modified on $Date: 2009-11-05 14:45:15 $
 */
package eu.fox7.flt.schema.infer.ixsd;

import eu.fox7.flt.treeautomata.impl.ContentAutomaton;
import eu.fox7.util.EquivalenceRelation;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public interface ContentEquivalenceRelation extends EquivalenceRelation<ContentAutomaton> {

    public boolean areEquivalent(ContentAutomaton model1, ContentAutomaton model2);

}
