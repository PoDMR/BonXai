/**
 * 
 */
package eu.fox7.util.automata.disambiguate;

import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;

import java.util.Set;

/**
 * @author woutergelade
 *
 */
public interface Isomorphism {
	public Set<SparseNFA> removeIsomporhicDFAs(Set<SparseNFA> DFAs) throws NotDFAException;
	public boolean isContainedIn(SparseNFA dfa, Set<SparseNFA> DFAs) throws NotDFAException;
	public boolean isContainedIn(Set<SparseNFA> subset, Set<SparseNFA> superset) throws NotDFAException;
}
