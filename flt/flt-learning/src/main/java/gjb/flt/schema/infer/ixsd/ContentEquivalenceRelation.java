/*
 * Created on Jan 17, 2007
 * Modified on $Date: 2009-11-05 14:45:15 $
 */
package gjb.flt.schema.infer.ixsd;

import gjb.flt.treeautomata.impl.ContentAutomaton;
import gjb.util.EquivalenceRelation;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public interface ContentEquivalenceRelation extends EquivalenceRelation<ContentAutomaton> {

    public boolean areEquivalent(ContentAutomaton model1, ContentAutomaton model2);

}
