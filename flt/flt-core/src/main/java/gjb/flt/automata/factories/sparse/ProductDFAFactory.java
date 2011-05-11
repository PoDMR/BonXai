package gjb.flt.automata.factories.sparse;

import gjb.flt.automata.DFA;
import gjb.flt.automata.NotDFAException;
import gjb.flt.automata.impl.sparse.ModifiableStateDFA;
import gjb.flt.automata.impl.sparse.SparseDFA;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.StateDFA;
import gjb.flt.automata.impl.sparse.Symbol;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class ProductDFAFactory {

	public static SparseDFA product(StateDFA...dfas) throws NotDFAException {
		SparseDFA dfa = new SparseDFA();
		product(dfa, dfas);
		return dfa;
	}

	@SuppressWarnings("unchecked")	
	public static void product(ModifiableStateDFA target, StateDFA...dfas) throws NotDFAException {
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
			for (Symbol symbol : symbols) {
				List<State> toStateList = new ArrayList<State>();
				List<String> toStateValues = new ArrayList<String>();
				for (int i = 0; i < dfas.length; i++) {
					String fromStateValue = fromStates.get(i);
					State toState = dfas[i].getNextState(symbol,
							dfas[i].getState(fromStateValue));
					toStateList.add(toState);
					toStateValues.add(dfas[i].getStateValue(toState));
				}
//				CarthesianProduct prod = new CarthesianProduct(toStateList);
//				for (Iterator prodIt = prod.iterator(); prodIt.hasNext();) {
//					List toStates = (List) prodIt.next();
//					System.err.println("toStateValues: "+toStateValues);
					productDFA.addTransition(symbol.toString(), fromStateValues,
							toStateValues.toString());
					if (!done.contains(toStateValues.toString())) {
						toDo.push(toStateValues);
//					}
				}
			}
		}
	}

//	@SuppressWarnings("unchecked")
//	public static SparseNFA intersection(StateNFA...nfas) {
//		SparseNFA[] dfas = new SparseNFA[nfas.length];
//		List<Set<State>> finalStateSets = new LinkedList<Set<State>>();
//		for (int i = 0; i < nfas.length; i++) {
//			dfas[i] = Determinizer.dfa(nfas[i]);
//			finalStateSets.add(dfas[i].getFinalStates());
//		}
//		SparseNFA nfa = null;
//		nfa = product(dfas);
//		CarthesianProduct finalTuples = new CarthesianProduct(finalStateSets);
//		for (Iterator it = finalTuples.iterator(); it.hasNext(); ) {
//			List tuple = (List) it.next();
//			String tupleString = ProductNFAFactory.encodeTuple(tuple, dfas);
//			if (nfa.hasState(tupleString)) {
//				nfa.addFinalState(tupleString);
//			}
//		}
//		return nfa;
//	}

	/**
	 * Helper function that encodes tuples of states for the computation of a product of DFAs
	 * into a string representation that reflects the original state names
	 * 
	 * @param fromTuple tuple to encode using the user specified state names
	 * @param dfas original DFAs the states in the list originate from
	 */
	protected static String encodeTuple(List<State> fromTuple, SparseNFA...dfas) {
		List<String> valueTuple = new LinkedList<String>();
		for (int i = 0; i < dfas.length; i++) {
			valueTuple.add(dfas[i].getStateValue(fromTuple.get(i)));
		}
		return valueTuple.toString();
	}

}

