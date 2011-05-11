/*
 * Created on Jun 1, 2007
 * Modified on $Date: 2009-11-12 09:10:15 $
 */
package gjb.flt.automata.factories.sparse;

import gjb.flt.automata.NotDFAException;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.StateDFA;
import gjb.flt.automata.impl.sparse.StateNFA;
import gjb.flt.automata.impl.sparse.Symbol;

/**
 * This class implements an inclusion condition.  The <code>satisfy()</code>
 * method will check whether L(nfa1) is a subset of L(nfa2).
 * 
 * @author gjb
 * @version $Revision: 1.2 $
 * 
 */
public class InclusionCondition implements StateCondition {
    
    protected StateDFA nfa1, nfa2;
    
    public void setNFAs(StateNFA nfa1, StateNFA nfa2) {
        this.nfa1 = (StateDFA) nfa1;
        this.nfa2 = (StateDFA) nfa2;
    }

    public boolean satisfy(State fromState1, State fromState2) {
        if ((nfa1.isFinalState(fromState1) && !nfa2.isFinalState(fromState2)))
            return false;
        for (Symbol symbol : nfa1.getSymbols()) {
            try {
                State toState1 = nfa1.getNextState(symbol, fromState1);
                State toState2 = nfa2.getNextState(symbol, fromState2);
                if ((toState1 != null && toState2 == null))
                    return false;
            } catch (NotDFAException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }
    
}
