/**
 * Created on Oct 6, 2009
 * Modified on $Date: 2009-11-10 14:01:29 $
 */
package eu.fox7.flt.automata.factories.sparse;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import eu.fox7.flt.automata.NFAStateAlreadyExistsException;
import eu.fox7.flt.automata.NoSuchStateException;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.automata.impl.sparse.TransitionMap;


/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
 */
public class ThompsonBuilder {

	/**
     * method that returns an NFA that accepts the empty language, i.e. no strings
     * are accepted
     * @return NFA accepting the empty language
     */
    public static SparseNFA emptyNFA() {
        SparseNFA nfa = new SparseNFA();
        State state = new State();
        nfa.addState(state);
        nfa.setInitialState(state);
        return nfa;
    }

	/**
     * method that returns an NFA that only accepts the empty string
     * @return NFA that accepts only the empty string
     */
    public static SparseNFA epsilonNFA() {
        SparseNFA nfa = new SparseNFA();
        State state = new State();
        nfa.addState(state);
        nfa.setInitialState(state);
        nfa.setFinalState(state);
        return nfa;
    }

	/**
     * method that returns an NFA that accepts all strings with respect to
     * the specified alphabet
     * @param alphabet
     *            Collection of symbols that represents the NFA's alphabet
     * @return NFA that accepts all strings that can be constructed from the given
     *         alphabet
     */
    public static SparseNFA sigmaStarNFA(Collection<Symbol> alphabet) {
        SparseNFA nfa = new SparseNFA();
        for (Symbol symbol : alphabet)
            nfa.addTransition(symbol.toString(), "q0", "q0");
        nfa.setInitialState("q0");
        nfa.setFinalState("q0");
        return nfa;
    }

	/**
     * method that returns an NFA that accepts all strings with respect to
     * the specified alphabet
     * @param characters
     *            array of Strings that represent the characters of the alphabet
     * @return NFA that accepts all strings that can be constructed from the given
     *         alphabet
     */
    public static SparseNFA sigmaStarNFA(String[] characters) {
        Set<Symbol> symbols = new HashSet<Symbol>();
        for (int i = 0; i < characters.length; i++) {
            symbols.add(Symbol.create(characters[i]));
        }
        return sigmaStarNFA(symbols);
    }

	/**
     * method that returns an NFA that accepts all strings with respect to
     * the specified alphabet
     * @param objs
     *            array of Objects that represent the characters of the alphabet; the
     *            toString() method is used for the representation
     * @return NFA that accepts all strings that can be constructed from the given
     *         alphabet
     */
    public static SparseNFA sigmaStarNFA(Object[] objs) {
        Set<Symbol> symbols = new HashSet<Symbol>();
        for (int i = 0; i < objs.length; i++) {
            symbols.add(Symbol.create(objs[i].toString()));
        }
        return sigmaStarNFA(symbols);
    }

	public static SparseNFA anyCharNFA(Collection<Symbol> alphabet) {
        SparseNFA nfa = new SparseNFA();
        for (Symbol symbol : alphabet)
            nfa.addTransition(symbol.toString(), "q0", "q1");
        nfa.setInitialState("q0");
        nfa.setFinalState("q1");
        return nfa;
    }

	public static SparseNFA anyCharNFA(String[] characters) {
        Set<Symbol> symbols = new HashSet<Symbol>();
        for (int i = 0; i < characters.length; i++) {
            symbols.add(Symbol.create(characters[i]));
        }
        return ThompsonBuilder.anyCharNFA(symbols);
    }

	public static SparseNFA symbolNFA(String value) {
        SparseNFA nfa = new SparseNFA();
        nfa.addTransition(value, "q0", "q1");
        nfa.setInitialState("q0");
        nfa.setFinalState("q1");
        return nfa;
    }

	public static SparseNFA concatenate(StateNFA...nfas) {
        SparseNFA nfa = ThompsonBuilder.merge(nfas);
        for (int i = 1; i < nfas.length; i++)
            for (State finalState : nfas[i-1].getFinalStates())
                nfa.addTransition(Symbol.getEpsilon(), finalState,
                                  nfas[i].getInitialState());
        nfa.setInitialState(nfas[0].getInitialState());
        nfa.setFinalStates(nfas[nfas.length - 1].getFinalStates());
        return nfa;
    }

	public static SparseNFA union(StateNFA...nfas) {
        SparseNFA nfa = ThompsonBuilder.merge(nfas);
        State newInitialState = new State();
        nfa.addState(newInitialState);
        State newFinalState = new State();
        nfa.addState(newFinalState);
        for (int i = 0; i < nfas.length; i++) {
            nfa.addTransition(Symbol.getEpsilon(), newInitialState,
                              nfas[i].getInitialState());
            for (State oldFinalState : nfas[i].getFinalStates())
                nfa.addTransition(Symbol.getEpsilon(), oldFinalState,
                                  newFinalState);
        }
        nfa.setInitialState(newInitialState);
        nfa.setFinalState(newFinalState);
        return nfa;
    }

	public static SparseNFA zeroOrOne(StateNFA nfa) {
        StateNFA[] nfas = { nfa };
        SparseNFA newNFA = ThompsonBuilder.merge(nfas);
        State newInitialState = new State();
        newNFA.addState(newInitialState);
        newNFA.setInitialState(newInitialState);
        newNFA.addTransition(Symbol.getEpsilon(), newNFA.getInitialState(),
                             nfa.getInitialState());
        newNFA.setFinalStates(nfa.getFinalStates());
        newNFA.addFinalState(newInitialState);
        return newNFA;
    }

	public static SparseNFA zeroOrMore(StateNFA nfa) {
        StateNFA[] nfas = { nfa };
        SparseNFA newNFA = ThompsonBuilder.merge(nfas);
        State newInitialState = new State();
        newNFA.addState(newInitialState);
        newNFA.setInitialState(newInitialState);
        newNFA.addTransition(Symbol.getEpsilon(), newNFA.getInitialState(),
                             nfa.getInitialState());
        newNFA.setFinalStates(nfa.getFinalStates());
        for (State finalState : newNFA.getFinalStates())
            newNFA.addTransition(Symbol.getEpsilon(), finalState,
                                 nfa.getInitialState());
        newNFA.addFinalState(newInitialState);
        return newNFA;
    }

	public static SparseNFA oneOrMore(StateNFA nfa) {
        StateNFA[] nfas = { nfa };
        SparseNFA newNFA = ThompsonBuilder.merge(nfas);
        newNFA.setInitialState(nfa.getInitialState());
        newNFA.setFinalStates(nfa.getFinalStates());
        for (State finalState : newNFA.getFinalStates())
            newNFA.addTransition(Symbol.getEpsilon(), finalState,
                                 newNFA.getInitialState());
        return newNFA;
    }

	public static SparseNFA complement(StateNFA nfa) {
        return complement(nfa, new HashSet<String>());
    }

	public static SparseNFA complement(StateNFA nfa, String[] symbolValues) {
        SparseNFA dfa = Determinizer.dfa(ThompsonBuilder.complete(nfa, symbolValues));
        for (State state : dfa.getStates())
            if (dfa.isFinalState(state))
	            try {
	                dfa.clearFinalState(state);
                } catch (NoSuchStateException e) {
                	throw new RuntimeException("state '" + state + "' does not exist");
                }
            else
                dfa.addFinalState(state);
        return dfa;
    }

	public static SparseNFA complement(StateNFA nfa, Set<String> symbolValues) {
        return complement(nfa, symbolValues.toArray(new String[0]));
    }

	/**
     * @param nfa
     * @param symbolValues
     * @return the DFA that is complete with respect to the given alphabet 
     */
    public static SparseNFA complete(StateNFA nfa, String[] symbolValues) {
        SparseNFA dfa = ThompsonBuilder.complete(nfa);
        String toStateValue = "q_sink";
        for (int i = 0; i < symbolValues.length; i++) {
            if (!dfa.hasSymbol(symbolValues[i])) {
                for (String fromStateValue : dfa.getStateValues())
                    dfa.addTransition(symbolValues[i], fromStateValue, toStateValue);
                dfa.addTransition(symbolValues[i], toStateValue, toStateValue);
            }
        }
        return dfa;
    }

	public static SparseNFA complete(StateNFA nfa, Set<String> symbolValues) {
        return complete(nfa, symbolValues.toArray(new String[0]));
    }

	public static SparseNFA complete(StateNFA nfa) {
        SparseNFA dfa = new SparseNFA(nfa);
        String sinkValue = "q_sink";
        if (!dfa.hasState(sinkValue)) {
            try {
                dfa.addState(sinkValue, new State());
            } catch (NFAStateAlreadyExistsException e) {
                throw new Error("Unexpected exception: " + e.getMessage());
            }
        }
        for (String stateValue : dfa.getStateValues()) {
            State state = dfa.getState(stateValue);
            for (String symbolValue : dfa.getSymbolValues()) {
                Symbol symbol = Symbol.create(symbolValue);
                if (dfa.getNextStates(symbol, state).isEmpty())
                    dfa.addTransition(symbolValue, stateValue, sinkValue);
            }
        }
        return dfa;
    }

	public static SparseNFA merge(StateNFA...nfas) {
        SparseNFA nfa = new SparseNFA();
        TransitionMap[] maps = new TransitionMap[nfas.length];
        for (int i = 0; i < nfas.length; i++) {
            maps[i] = nfas[i].getTransitionMap();
            nfa.addSymbols(nfas[i].getSymbols());
            nfa.addStates(nfas[i].getStates());
        }
        nfa.setTransitionMap(TransitionMap.merge(maps));
        return nfa;
    }

}
