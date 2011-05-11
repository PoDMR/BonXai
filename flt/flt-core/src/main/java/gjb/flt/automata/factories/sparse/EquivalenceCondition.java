/*
 * Created on Jun 1, 2007
 * Modified on $Date: 2009-10-27 14:14:00 $
 */
package gjb.flt.automata.factories.sparse;

import gjb.flt.automata.NotDFAException;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.StateDFA;
import gjb.flt.automata.impl.sparse.StateNFA;
import gjb.flt.automata.impl.sparse.Symbol;

import java.util.HashSet;
import java.util.Set;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class EquivalenceCondition implements StateCondition {

    protected StateDFA nfa1, nfa2;
    protected Set<Symbol> alphabet;

    public void setNFAs(StateNFA nfa1, StateNFA nfa2) {
        this.nfa1 = (StateDFA) nfa1;
        this.nfa2 = (StateDFA) nfa2;
        this.alphabet = new HashSet<Symbol>();
        this.alphabet.addAll(nfa1.getSymbols());
        this.alphabet.addAll(nfa2.getSymbols());
    }

    public boolean satisfy(State fromState1, State fromState2) {
        if ((nfa1.isFinalState(fromState1) && !nfa2.isFinalState(fromState2)) ||
                (!nfa1.isFinalState(fromState1) && nfa2.isFinalState(fromState2)))
            return false;
        for (Symbol symbol : alphabet) {
            try {
                State toState1 = nfa1.getNextState(symbol, fromState1);
                State toState2 = nfa2.getNextState(symbol, fromState2);
                if ((toState1 == null && toState2 != null) ||
                        (toState1 != null && toState2 == null))
                    return false;
            } catch (NotDFAException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

}
