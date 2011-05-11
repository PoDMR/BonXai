/**
 * Created on Oct 6, 2009
 * Modified on $Date: 2009-10-27 14:14:00 $
 */
package gjb.flt.automata.factories.sparse;

import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.StateNFA;
import gjb.flt.automata.impl.sparse.Symbol;
import gjb.util.CarthesianProduct;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class ProductNFAFactory {

    public static SparseNFA product(StateNFA...dfas) {
    	SparseNFA nfa = new SparseNFA();
    	product(nfa, dfas);
    	return nfa;
    }
	
	@SuppressWarnings("unchecked")	
	public static void product(SparseNFA target, StateNFA...dfas) {
        SparseNFA nfa = target;
        List<String> initialStates = new ArrayList<String>();
        for (int i = 0; i < dfas.length; i++) {
            initialStates.add(dfas[i].getStateValue(dfas[i].getInitialState()));
        }
        Set<Symbol> symbols = new HashSet<Symbol>();
        symbols.addAll(dfas[0].getSymbols());
        for (int i = 1; i < dfas.length; i++) {
            symbols.retainAll(dfas[i].getSymbols());
        }
        nfa.setInitialState(initialStates.toString());
        Stack<List<String>> toDo = new Stack<List<String>>();
        toDo.push(initialStates);
        Set<String> done = new HashSet<String>();
        while (!toDo.isEmpty()) {
            List<String> fromStates = toDo.pop();
            String fromStateValues = fromStates.toString();
            done.add(fromStateValues);
            for (Symbol symbol : symbols) {
                List<Set<State>> toStateList = new ArrayList<Set<State>>();
                for (int i = 0; i < dfas.length; i++) {
                    String fromStateValue = fromStates.get(i);
                    Set<State> toStateSet = dfas[i].getNextStates(symbol,
                                                                  dfas[i].getState(fromStateValue));
                    toStateList.add((toStateSet));
                }
                CarthesianProduct prod = new CarthesianProduct(toStateList);
                for (Iterator prodIt = prod.iterator(); prodIt.hasNext();) {
                    List toStates = (List) prodIt.next();
                    List<String> toStateValues = new ArrayList<String>();
                    for (int i = 0; i < dfas.length; i++) {
                        State state = (State) toStates.get(i);
                        toStateValues.add(dfas[i].getStateValue(state));
                    }
                    String toStateValue = toStateValues.toString();
                    nfa.addTransition(symbol.toString(), fromStateValues,
                                      toStateValue);
                    if (!done.contains(toStateValue)) {
                        toDo.push(toStateValues);
                    }
                }
            }
        }
    }

	@SuppressWarnings("unchecked")
    public static SparseNFA intersection(StateNFA...nfas) {
        SparseNFA[] dfas = new SparseNFA[nfas.length];
        List<Set<State>> finalStateSets = new LinkedList<Set<State>>();
        for (int i = 0; i < nfas.length; i++) {
            dfas[i] = Determinizer.dfa(nfas[i]);
            finalStateSets.add(dfas[i].getFinalStates());
        }
        SparseNFA nfa = null;
        nfa = product(dfas);
        CarthesianProduct finalTuples = new CarthesianProduct(finalStateSets);
        for (Iterator it = finalTuples.iterator(); it.hasNext(); ) {
            List tuple = (List) it.next();
            String tupleString = ProductNFAFactory.encodeTuple(tuple, dfas);
            if (nfa.hasState(tupleString)) {
                nfa.addFinalState(tupleString);
            }
        }
        return nfa;
    }

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
