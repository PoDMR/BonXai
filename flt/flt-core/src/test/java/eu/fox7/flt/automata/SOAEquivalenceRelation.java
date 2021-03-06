/*
 * Created on Jan 2, 2006
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package eu.fox7.flt.automata;

import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.regex.Glushkov;
import eu.fox7.util.AbstractEquivalenceRelation;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class SOAEquivalenceRelation extends AbstractEquivalenceRelation<State> {

    protected SparseNFA nfa;

    protected SOAEquivalenceRelation() {
        super();
    }

    public SOAEquivalenceRelation(SparseNFA nfa) {
        this.nfa = nfa;
    }

    public boolean areEquivalent(State state1, State state2) {
        String stateValue1 = nfa.getStateValue(state1);
        String stateValue2 = nfa.getStateValue(state2);
        return Glushkov.unmark(stateValue1).equals(Glushkov.unmark(stateValue2));
    }

}
