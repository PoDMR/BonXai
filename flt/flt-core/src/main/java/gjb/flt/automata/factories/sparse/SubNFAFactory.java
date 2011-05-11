/*
 * Created on Feb 1, 2006
 * Modified on $Date: 2009-10-27 14:14:00 $
 */
package gjb.flt.automata.factories.sparse;

import gjb.flt.automata.converters.Simplifier;
import gjb.flt.automata.impl.sparse.SparseNFA;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class SubNFAFactory {

    public static SparseNFA create(SparseNFA nfa, String stateValue) {
        SparseNFA subNFA = new SparseNFA(nfa);
        subNFA.setInitialState(stateValue);
        Simplifier.simplify(subNFA);
        return subNFA;
    }

}
