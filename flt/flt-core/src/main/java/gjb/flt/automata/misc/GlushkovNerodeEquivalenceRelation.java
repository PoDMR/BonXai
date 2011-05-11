/*
 * Created on Jun 21, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.automata.misc;

import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.StateNFA;
import gjb.flt.regex.Glushkov;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class GlushkovNerodeEquivalenceRelation extends NerodeEquivalenceRelation {

    @SuppressWarnings("unused")
    private GlushkovNerodeEquivalenceRelation() {
        super();
    }

    public GlushkovNerodeEquivalenceRelation(StateNFA nfa) {
        super(nfa);
    }

    /* (non-Javadoc)
     * @see gjb.util.AbstractEquivalenceRelation#areEquivalent(java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean areEquivalent(State state1, State state2) {
        String stateValue1 = Glushkov.unmark(nfa.getStateValue(state1));
        String stateValue2 = Glushkov.unmark(nfa.getStateValue(state2));
        if (!stateValue1.equals(stateValue2)) return false;
        return super.areEquivalent(state1, state2);
    }

}
