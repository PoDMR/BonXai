/*
 * Created on Jun 26, 2008
 * Modified on $Date: 2009-11-12 09:10:15 $
 */
package gjb.flt.automata.matchers;

import gjb.flt.automata.NotDFAException;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.StateDFA;
import gjb.flt.automata.impl.sparse.StateNFA;
import gjb.flt.automata.impl.sparse.Symbol;

import java.util.List;

/**
 * @author gjb
 * @version $Revision: 1.2 $
 * 
 */
public class DeterministicPartialMatcher {

    protected StateDFA nfa;
    protected boolean isGreedy;

    public DeterministicPartialMatcher(StateNFA nfa) throws NotDFAException {
        this(nfa, true);
    }

    public DeterministicPartialMatcher(StateNFA nfa, boolean isGreedy)
            throws NotDFAException {
        if (!nfa.isDFA())
            throw new NotDFAException();
        this.nfa = (StateDFA) nfa;
        this.isGreedy = isGreedy;
    }

    public boolean isGreedy() {
        return isGreedy;
    }

    public boolean match(List<String> symbols) {
        State state = nfa.getInitialState();
        if (!isGreedy() && nfa.isFinalState(state))
            return true;
        while (!symbols.isEmpty()) {
            Symbol symbol = Symbol.create(symbols.get(0));
            try {
                State newState = nfa.getNextState(symbol, state);
                if (newState == null) {
                    return nfa.isFinalState(state);
                } else {
                    symbols.remove(0);
                    if (!isGreedy() && nfa.isFinalState(newState))
                        return true;
                    state = newState;
                }
            } catch (NotDFAException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return nfa.isFinalState(state);
    }

}
