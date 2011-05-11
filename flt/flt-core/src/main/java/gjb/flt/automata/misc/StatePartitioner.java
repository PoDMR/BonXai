package gjb.flt.automata.misc;

import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.Symbol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StatePartitioner {

	protected static Set<Set<State>> partition(SparseNFA nfa, Set<State> set,
	                                           Map<State,Set<State>> map) {
		Set<Set<State>> parts = new HashSet<Set<State>>();
		for (Symbol symbol : nfa.getSymbols()) {
			Map<Set<State>,Set<State>> newSets = new HashMap<Set<State>,Set<State>>();
			for (State fromState : set) {
				Set<State> toStates = nfa.getNextStates(symbol, fromState);
				State toState = gjb.util.Collections.extractSingleton(toStates);
				Set<State> toSet = map.get(toState);
				if (!newSets.containsKey(toSet))
					newSets.put(toSet, new HashSet<State>());
				newSets.get(toSet).add(fromState);
			}
			if (newSets.size() > 1) {
				for (Set<State> partition : newSets.values())
					for (State state : partition)
						map.put(state, partition);
				parts.addAll(newSets.values());
				return parts;
			}
		}
		parts.add(set);
		return parts;
	}

}
