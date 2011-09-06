/**
 * Created on Jun 9, 2009
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.automata.factories.dense;

import eu.fox7.flt.automata.impl.dense.GlushkovMatrixRepresentation;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class GlushkovBigIntegerMatrixModifier {

	public GlushkovMatrixRepresentation<BigInteger> restrictAlphabet(GlushkovMatrixRepresentation<BigInteger> m,
	                                                                 Set<String> symbols) {
		GlushkovMatrixRepresentation<BigInteger> newM = new GlushkovMatrixRepresentation<BigInteger>();
		newM.setX0(m.getX0().clone());
		newM.setP(m.getP().clone());
		newM.setXf(m.getXf().clone());
		newM.setAcceptsEmptyString(m.acceptsEmptyString());
		newM.setZeroValue(BigInteger.ZERO);
		newM.setOneValue(BigInteger.ONE);
		for (Integer index : m.getIndices()) {
			String symbol = m.getSymbol(index);
			if (!symbols.contains(symbol)) {
				newM.getX0().set(index, 0, BigInteger.ZERO);
				for (int j = 0; j < newM.getP().getRowDimension(); j++)
					newM.getP().set(index, j, BigInteger.ZERO);
			} else {
				newM.addIndex(index, symbol);
				newM.addSymbol(symbol, index);
			}
		}
		return newM;
	}

	public GlushkovMatrixRepresentation<BigInteger> addWeights(GlushkovMatrixRepresentation<BigInteger> m,
	                                                           Map<String,BigInteger> weights) {
		GlushkovMatrixRepresentation<BigInteger> newM = new GlushkovMatrixRepresentation<BigInteger>();
		newM.setX0(m.getX0().clone());
		newM.setP(m.getP().clone());
		newM.setXf(m.getXf().clone());
		newM.setAcceptsEmptyString(m.acceptsEmptyString());
		newM.setZeroValue(BigInteger.ZERO);
		newM.setOneValue(BigInteger.ONE);
		for (Integer index : m.getIndices()) {
			String symbol = m.getSymbol(index);
			if (weights.containsKey(symbol)) {
				BigInteger weight = weights.get(symbol);
				if (newM.isInitial(index))
					newM.getX0().set(index, 0, weight);
				for (int j = 0; j < newM.getP().getRowDimension(); j++)
					if (newM.hasTransition(index, j))
						newM.getP().set(index, j, weight);
			} else {
				newM.addIndex(index, symbol);
				newM.addSymbol(symbol, index);
			}
		}
		return newM;
	}

}
