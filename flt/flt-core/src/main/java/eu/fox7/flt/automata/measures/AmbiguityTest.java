/*
 * Created on Oct 17, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.automata.measures;

import java.math.BigInteger;

import eu.fox7.flt.automata.impl.dense.GlushkovMatrixRepresentation;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Symbol;


/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class AmbiguityTest<T> implements LanguageTest, GlushkovMatrixTest<T> {

    /* (non-Javadoc)
     * @see eu.fox7.util.automata.measures.LanguageTest#test(eu.fox7.util.automata.NFA)
     */
    public boolean test(StateNFA nfa) {
        for (State fromState : nfa.getStates())
            for (Symbol symbol : nfa.getSymbols())
                if (nfa.getNextStates(symbol, fromState).size() > 1)
                    return true;
        return false;
    }

	public boolean test(GlushkovMatrixRepresentation<T> m) {
		for (int i = 0; i < m.getP().getRowDimension(); i++) {
			for (int j = 0; j < m.getP().getRowDimension(); j++) {
				String symbol = m.getSymbol(j);
				boolean foundNonZeroP = false;
				boolean foundNonZeroX0 = false;
				for (Integer row : m.getIndices(symbol)) {
					if (!m.getP().get(row, i).equals(BigInteger.ZERO)) {
						if (foundNonZeroP)
							return true;
						else
							foundNonZeroP = true;
					}
					if (!m.getX0().get(row, 0).equals(BigInteger.ZERO)) {
						if (foundNonZeroX0)
							return true;
						else
							foundNonZeroX0 = true;
					}
				}
			}
		}
		return false;
	}

}
