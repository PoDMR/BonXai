/*
 * Created on Jun 24, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.automata.converters;

import gjb.flt.automata.impl.sparse.ModifiableStateNFA;
import gjb.flt.automata.misc.NerodeEquivalenceRelation;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class NFAMinimizer extends AbstractMinimizer {

    /* (non-Javadoc)
     * @see gjb.util.automata.AbstractMinimizer#initRelation(gjb.util.automata.NFA)
     */
    @Override
    protected void initRelation(ModifiableStateNFA nfa) {
        relation = new NerodeEquivalenceRelation(nfa);
    }

}
