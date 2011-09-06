/*
 * Created on Jun 24, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.automata.converters;

import eu.fox7.flt.automata.impl.sparse.ModifiableStateNFA;
import eu.fox7.flt.automata.misc.NerodeEquivalenceRelation;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class NFAMinimizer extends AbstractMinimizer {

    /* (non-Javadoc)
     * @see eu.fox7.util.automata.AbstractMinimizer#initRelation(eu.fox7.util.automata.NFA)
     */
    @Override
    protected void initRelation(ModifiableStateNFA nfa) {
        relation = new NerodeEquivalenceRelation(nfa);
    }

}
