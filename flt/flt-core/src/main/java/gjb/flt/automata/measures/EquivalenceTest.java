/**
 * Created on Oct 10, 2009
 * Modified on $Date: 2009-11-12 09:10:15 $
 */
package gjb.flt.automata.measures;

import gjb.flt.automata.NotDFAException;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.StateDFA;
import gjb.flt.automata.impl.sparse.StateNFA;
import gjb.flt.automata.impl.sparse.Symbol;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
 */
public class EquivalenceTest {

	public static boolean areEquivalent(StateNFA...dfas) throws NotDFAException {
        List<String> initialStates = new ArrayList<String>();
        List<Set<State>> terminationSets = new ArrayList<Set<State>>();
        for (int i = 0; i < dfas.length; i++) {
            initialStates.add(dfas[i].getStateValue(dfas[i].getInitialState()));
            terminationSets.add(dfas[i].getTerminatingStates());
        }
        Set<Symbol> symbols = new HashSet<Symbol>();
        for (int i = 0; i < dfas.length; i++) {
            symbols.addAll(dfas[i].getSymbols());
        }
        Stack<List<String>> toDo = new Stack<List<String>>();
        toDo.push(initialStates);
        Set<String> done = new HashSet<String>();
        while (!toDo.isEmpty()) {
            List<String> fromStates = toDo.pop();
            String fromStateValues = fromStates.toString();
            done.add(fromStateValues);
            for (Symbol symbol : symbols) {
                List<String> toStateValues = new ArrayList<String>();
                String fromStateValue = fromStates.get(0);
                State toState = ((StateDFA) dfas[0]).getNextState(symbol,
                                                                  dfas[0].getState(fromStateValue));
                toStateValues.add(dfas[0].getStateValue(toState));
                boolean areAllFinal = dfas[0].isFinalState(toState);
                boolean areAllRedundant = toState == null || !terminationSets.get(0).contains(toState);
                for (int i = 1; i < dfas.length; i++) {
                    fromStateValue = fromStates.get(i);
                    toState = ((StateDFA) dfas[i]).getNextState(symbol,
                                                                dfas[i].getState(fromStateValue));
                    if (areAllRedundant != (toState == null || !terminationSets.get(i).contains(toState)))
                        return false;
                    if (areAllFinal != dfas[i].isFinalState(toState))
                        return false;
                    toStateValues.add(dfas[i].getStateValue(toState));
                }
                if (!done.contains(toStateValues.toString()))
                    toDo.push(toStateValues);
            }
        }
        return true;
    }

}
