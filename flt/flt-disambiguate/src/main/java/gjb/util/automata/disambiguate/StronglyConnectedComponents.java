package gjb.util.automata.disambiguate;

import gjb.flt.automata.NFA;

import java.util.HashSet;
import java.util.Set;


public interface StronglyConnectedComponents {
	public Set<HashSet<String>> computeStronglyConnectedComponents(NFA nfa, String getInitialState);
	public Set<HashSet<String>> computeAllStronglyConnectedComponents(NFA nfa);
}
