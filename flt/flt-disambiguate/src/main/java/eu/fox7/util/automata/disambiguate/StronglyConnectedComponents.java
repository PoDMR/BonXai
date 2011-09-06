package eu.fox7.util.automata.disambiguate;

import eu.fox7.flt.automata.NFA;

import java.util.HashSet;
import java.util.Set;


public interface StronglyConnectedComponents {
	public Set<HashSet<String>> computeStronglyConnectedComponents(NFA nfa, String getInitialState);
	public Set<HashSet<String>> computeAllStronglyConnectedComponents(NFA nfa);
}
