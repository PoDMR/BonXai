package eu.fox7.util.automata.disambiguate;

import eu.fox7.flt.automata.NFA;

import java.util.Set;

public interface DFAGenerator {
	/**
	 * Takes as input a minimal, trimmed, "glushkovized" (i.e. non-returning state-labeled)
	 * DFA, and returns all DFAs of given size equivalent to the DFA. 
	 * The returned set may contain isomorphic DFAs
	 * @param minimalDFA
	 * @param size
	 * @return
	 */
	public Set<NFA> generateDFAs(NFA minimalDFA, int size);
	
	/**
	 * Takes as input a minimal, trimmed, "glushkovized" (i.e. non-returning state-labeled)
	 * DFA, and returns all DFAs of given size equivalent to the DFA. 
	 * The returned set does not contain isomorphic DFAs
	 * @param minimalDFA
	 * @param size
	 * @return
	 */
	public Set<NFA> generateDisjointDFAs(NFA minimalDFA, int size);
}
