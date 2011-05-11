/**
 * Created on Oct 12, 2009
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.automata.measures;

import gjb.flt.automata.factories.sparse.ProductNFAFactory;
import gjb.flt.automata.factories.sparse.ThompsonBuilder;
import gjb.flt.automata.impl.sparse.StateNFA;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class NonTrivialEmptynessTest implements LanguageTest {

	protected EmptynessTest emptyTest = new EmptynessTest();

    /**
     * <code>test</code> method checks whether the language
     * defined by the NFA is the empty set or the singleton with the empty
     * string.
     * 
     * @return a <code>boolean</code> value <code>true</code> if the
     *         corresponding language is either the empty set or the singleton
     *         containing the empty string, <code>false</code> otherwise
     */
	public boolean test(StateNFA nfa) {
        StateNFA[] sigmaPlusNFAs = {
        		ThompsonBuilder.anyCharNFA(nfa.getSymbols()),
        		ThompsonBuilder.sigmaStarNFA(nfa.getSymbols())
        };
        StateNFA[] nfas = { nfa, ThompsonBuilder.concatenate(sigmaPlusNFAs) };
        return emptyTest.test(ProductNFAFactory.intersection(nfas));
	}

}
