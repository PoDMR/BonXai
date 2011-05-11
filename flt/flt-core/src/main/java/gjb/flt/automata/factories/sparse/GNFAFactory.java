/**
 * Created on Nov 12, 2009
 * Modified on $Date: 2009-11-12 22:57:18 $
 */
package gjb.flt.automata.factories.sparse;

import static gjb.util.Collections.extractSingleton;
import gjb.flt.automata.impl.sparse.GNFA;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.StateDFA;
import gjb.flt.automata.impl.sparse.StatePair;
import gjb.flt.automata.impl.sparse.Symbol;
import gjb.flt.automata.impl.sparse.Transition;
import gjb.flt.regex.Regex;
import gjb.flt.regex.factories.RegexFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
 * This factory converts an DFA to an GNFA.  Note that a sparse implementation
 * should be considered for any computationally intensive application.
 */
public class GNFAFactory {

	protected RegexFactory factory;

	public GNFAFactory() {
		this(new Regex());
	}

	public GNFAFactory(Regex regex) {
		this.factory = new RegexFactory(regex);
	}
	
	public GNFA create(StateDFA dfa, boolean isGlushkov) {
		GNFA gnfa = new GNFA();
		gnfa.setRegex(gnfa.getInitialState(), gnfa.getFinalState(), factory.createEmpty());
		Map<State,State> stateMap = new HashMap<State,State>();
		stateMap.put(dfa.getInitialState(), gnfa.getInitialState());
		if (!isGlushkov)
			gnfa.setRegex(gnfa.getInitialState(),
			              stateMap.get(dfa.getInitialState()),
			              factory.createEpsilon());
		for (State finalState : dfa.getFinalStates()) {
			registerState(stateMap, finalState);
			gnfa.setRegex(stateMap.get(finalState), gnfa.getFinalState(),
			              factory.createEpsilon());
		}
		Map<StatePair,Set<Symbol>> symbolMap = new HashMap<StatePair,Set<Symbol>>();
		for (Transition transition : dfa.getTransitionMap().getTransitions()) {
			State fromState = transition.getFromState();
			registerState(stateMap, fromState);
			State toState = transition.getToState();
			registerState(stateMap, toState);
			StatePair statePair = new StatePair(fromState, toState);
			if (!symbolMap.containsKey(statePair))
				symbolMap.put(statePair, new HashSet<Symbol>());
			symbolMap.get(statePair).add(transition.getSymbol());
		}
		for (StatePair statePair : symbolMap.keySet())
			gnfa.setRegex(stateMap.get(statePair.getFirst()),
			              stateMap.get(statePair.getSecond()),
			              createUnion(symbolMap.get(statePair)));
		return gnfa;
	}

	protected void registerState(Map<State, State> stateMap, State fromState) {
	    if (!stateMap.containsKey(fromState))
	    	stateMap.put(fromState, new State());
    }

	protected Regex createUnion(Set<Symbol> symbols) {
		if (symbols.size() == 1) {
			String symbolValue = extractSingleton(symbols).toString();
			return factory.createSymbol(symbolValue);
		} else {
			Regex[] regexs = new Regex[symbols.size()];
			int i = 0;
			for (Symbol symbol : symbols)
            		regexs[i++] = factory.createSymbol(symbol.toString());
			return factory.createUnion(regexs);
		}
    }

}
