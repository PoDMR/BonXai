package eu.fox7.flt.automata.factories.sparse;

import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.impl.sparse.ModifiableStateDFA;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateDFA;
import eu.fox7.flt.automata.impl.sparse.Symbol;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class ProductDFAFactory {

	public static SparseNFA union(StateDFA...dfas) throws NotDFAException {
		SparseNFA dfa = new SparseNFA();
		product(dfa, true, dfas);
		return dfa;
	}

	public static SparseNFA product(StateDFA...dfas) throws NotDFAException {
		SparseNFA dfa = new SparseNFA();
		product(dfa, false, dfas);
		return dfa;
	}

	public static void product(ModifiableStateDFA target, StateDFA...dfas) throws NotDFAException {
		product(target, false, dfas);
	}

	public static void product(ModifiableStateDFA target, boolean computeUnion, StateDFA...dfas) throws NotDFAException {
		ModifiableStateDFA productDFA = target;
		List<String> initialStates = new ArrayList<String>();
		for (StateDFA dfa: dfas) {
			initialStates.add(dfa.getStateValue(dfa.getInitialState()));
		}
		Set<Symbol> symbols = new HashSet<Symbol>();
		for (StateDFA dfa: dfas) {
			symbols.addAll(dfa.getSymbols());
		}
		productDFA.setInitialState(initialStates.toString());
		Stack<List<String>> toDo = new Stack<List<String>>();
		toDo.push(initialStates);
		Set<String> done = new HashSet<String>();
		while (!toDo.isEmpty()) {
			List<String> fromStates = toDo.pop();
			String fromStateValues = fromStates.toString();
//			System.err.println("Product: doing state "+fromStateValues);
			done.add(fromStateValues);
			if (computeUnion) {
				boolean accepting = false;
				for (int i = 0; i < dfas.length; i++) {
					String stateValue = fromStates.get(i);
					if (dfas[i].isFinalState(stateValue))
						accepting = true;
				}
				
				if (accepting)
					productDFA.addFinalState(fromStateValues);
			}
			
			for (Symbol symbol : symbols) {
				List<State> toStateList = new ArrayList<State>();
				List<String> toStateValues = new ArrayList<String>();
				boolean sink = true;
				for (int i = 0; i < dfas.length; i++) {
					String fromStateValue = fromStates.get(i);
					State toState = dfas[i].getNextState(symbol,
							dfas[i].getState(fromStateValue));
					if (toState != null)
						sink = false;
					toStateList.add(toState);
					toStateValues.add(dfas[i].getStateValue(toState));
				}

				if (!sink) {
					productDFA.addTransition(symbol.toString(), fromStateValues,
							toStateValues.toString());
					if (!done.contains(toStateValues.toString())) {
						toDo.push(toStateValues);
					}
				}
			}
		}
	}

}

