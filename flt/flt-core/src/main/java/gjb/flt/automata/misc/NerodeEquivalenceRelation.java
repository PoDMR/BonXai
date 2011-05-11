/*
 * Created on Jun 24, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.automata.misc;

import gjb.flt.automata.NotDFAException;
import gjb.flt.automata.factories.sparse.EquivalenceCondition;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.StateDFA;
import gjb.flt.automata.impl.sparse.StateNFA;
import gjb.flt.automata.measures.Simulator;
import gjb.util.AbstractEquivalenceRelation;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class NerodeEquivalenceRelation extends AbstractEquivalenceRelation<State> {

    protected StateDFA nfa;
    protected Simulator simulator = new Simulator(new EquivalenceCondition());

    protected NerodeEquivalenceRelation() {}

    public NerodeEquivalenceRelation(StateNFA nfa) {
        this.nfa = (StateDFA) nfa;
    }

    /* (non-Javadoc)
     * @see gjb.util.AbstractEquivalenceRelation#areEquivalent(java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean areEquivalent(State state1, State state2) {
        if (state1 == state2) return true;
        try {
            return simulator.simulate(nfa, state1, nfa, state2);
        } catch (NotDFAException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


}
