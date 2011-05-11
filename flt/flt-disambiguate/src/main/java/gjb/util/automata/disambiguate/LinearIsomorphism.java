/**
 * 
 */
package gjb.util.automata.disambiguate;

import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.NotDFAException;

import java.util.HashSet;
import java.util.Set;

/**
 * @author woutergelade
 *
 */
public class LinearIsomorphism implements Isomorphism {

	/* (non-Javadoc)
	 * @see gjb.util.automata.disambiguate.Isomorphism#removeIsomporhicDFAs(java.util.Set)
	 */
	public Set<SparseNFA> removeIsomporhicDFAs(Set<SparseNFA> DFAs) throws NotDFAException {
		Set<SparseNFA> result = new HashSet<SparseNFA>();
		IsomorphismTree tree = new IsomorphismTree();
		
		for(SparseNFA dfa : DFAs){
			if(tree.addNFA(dfa))
				result.add(dfa);
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see gjb.util.automata.disambiguate.Isomorphism#isContainedIn(gjb.flt.automata.impl.sparse.SparseNFA, java.util.Set)
	 */
	public boolean isContainedIn(SparseNFA dfa, Set<SparseNFA> DFAs) throws NotDFAException {
		System.out.println("isContainedIn: Not implemented");
		return false;
	}

	/* (non-Javadoc)
	 * @see gjb.util.automata.disambiguate.Isomorphism#isContainedIn(java.util.Set, java.util.Set)
	 */
	public boolean isContainedIn(Set<SparseNFA> subset, Set<SparseNFA> superset)
			throws NotDFAException {
		System.out.println("isContainedIn: Not implemented");
		return false;
	}

}
