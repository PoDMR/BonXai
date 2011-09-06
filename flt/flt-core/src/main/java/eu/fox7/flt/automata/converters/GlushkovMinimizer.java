/*
 * Created on Jun 22, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.automata.converters;

import eu.fox7.flt.automata.impl.sparse.ModifiableStateNFA;
import eu.fox7.flt.automata.misc.GlushkovNerodeEquivalenceRelation;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class GlushkovMinimizer extends AbstractMinimizer {

    public GlushkovMinimizer() {
        super();
    }

    @Override
    protected void initRelation(ModifiableStateNFA nfa) {
        relation = new GlushkovNerodeEquivalenceRelation(nfa);
    }

}
