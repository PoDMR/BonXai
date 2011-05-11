/*
 * Created on Jun 22, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.automata.converters;

import gjb.flt.automata.impl.sparse.ModifiableStateNFA;
import gjb.flt.automata.misc.GlushkovNerodeEquivalenceRelation;

/**
 * @author gjb
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
