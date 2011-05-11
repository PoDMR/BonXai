package gjb.flt.automata.impl.sparse;

import gjb.flt.automata.ModifiableNFA;
import gjb.flt.automata.NFAStateAlreadyExistsException;
import gjb.flt.automata.NoSuchStateException;
import gjb.flt.automata.NoSuchSymbolException;
import gjb.flt.automata.NoSuchTransitionException;

import java.util.Collection;

public interface ModifiableStateNFA extends StateNFA, ModifiableNFA {

	public void add(StateNFA... nfas);

	public void add(StateNFA nfa);

	public void addSymbol(Symbol symbol);

	public void addSymbols(Collection<Symbol> symbols);

	public void removeSymbol(Symbol symbol) throws NoSuchSymbolException;

	public void addState(State state);

	public void addState(String stateValue, State state)
	        throws NFAStateAlreadyExistsException;

	public void addStates(Collection<State> states);

	public void removeState(State state) throws NoSuchStateException;

	public void setTransitionMap(TransitionMap transitions);

	public void addTransition(Symbol symbol, State fromState, State toState);

	public void removeTransition(Symbol symbol, State fromState, State toState)
	        throws NoSuchTransitionException;

	public void setInitialState(State state);

	public void setFinalState(State state);

	public void setFinalStates(Collection<State> states);

	public void addFinalState(State state);

	public void addFinalStates(Collection<State> states);

	public void clearFinalState(State state) throws NoSuchStateException;

}