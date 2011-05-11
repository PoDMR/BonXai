/**
 * 
 */
package gjb.util.automata.disambiguate;

import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.NotDFAException;

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
