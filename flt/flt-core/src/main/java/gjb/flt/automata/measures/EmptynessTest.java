/**
 * Created on Oct 12, 2009
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.automata.measures;

import gjb.flt.automata.NotDFAException;
import gjb.flt.automata.factories.sparse.Determinizer;
import gjb.flt.automata.generators.ShortestStringGenerator;
import gjb.flt.automata.impl.sparse.StateNFA;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class EmptynessTest implements LanguageTest {

    /**
     * <code>test</code> method checks whether the language defined by the
     * NFA is the empty set.
     * 
     * @return a <code>boolean</code> value <code>true</code> if the
     *         corresponding language is the empty set, <code>false</code>
     *         otherwise
     */
	public boolean test(StateNFA nfa) {
        try {
        	StateNFA dfa = Determinizer.dfa(nfa);
        	ShortestStringGenerator g = new ShortestStringGenerator(dfa);
            return g.shortestStatePaths().size() == 0;
        } catch (NotDFAException e) {
            throw new RuntimeException("Unexpected NotDFAException thrown");
        }
	}

}
