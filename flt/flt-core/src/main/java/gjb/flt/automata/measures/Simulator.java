/*
 * Created on May 11, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.automata.measures;

import gjb.flt.automata.NotDFAException;
import gjb.flt.automata.factories.sparse.StateCondition;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.StateDFA;
import gjb.flt.automata.impl.sparse.Symbol;
import gjb.util.Pair;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class Simulator {

    protected StateCondition condition;

    public Simulator(StateCondition condition) {
        super();
        this.condition = condition;
    }

    public boolean simulate(StateDFA nfa1, StateDFA nfa2) throws NotDFAException {
        return simulate(nfa1, nfa1.getInitialState(), nfa2, nfa2.getInitialState());
    }

    public boolean simulate(StateDFA nfa1, State initState1,
                            StateDFA nfa2, State initState2)
            throws NotDFAException {
        condition.setNFAs(nfa1, nfa2);
        Pair<State,State> initPair = new Pair<State,State>(initState1, initState2);
        List<Pair<State,State>> toDo = new LinkedList<Pair<State,State>>();
        toDo.add(initPair);
        Set<Pair<State,State>> checked = new HashSet<Pair<State,State>>();
        Set<Symbol> alphabet = new HashSet<Symbol>();
        alphabet.addAll(nfa1.getSymbols());
        alphabet.addAll(nfa2.getSymbols());
        while (!toDo.isEmpty()) {
            Pair<State,State> pair = toDo.remove(0);
            if (!condition.satisfy(pair.getFirst(), pair.getSecond()))
                return false;
            checked.add(pair);
            for (Symbol symbol : alphabet) {
                State state1 = nfa1.getNextState(symbol, pair.getFirst());
                State state2 = nfa2.getNextState(symbol, pair.getSecond());
                if (state1 != null && state2 != null) {
                    Pair<State, State> newPair = new Pair<State,State>(state1, state2);
                    if (!checked.contains(newPair))
                        toDo.add(newPair);
                }
            }
        }
        return true;
    }

}
