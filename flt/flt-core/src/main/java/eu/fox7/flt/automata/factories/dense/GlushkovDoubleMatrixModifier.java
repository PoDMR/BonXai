/**
 * Created on Sep 23, 2009
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.automata.factories.dense;

import eu.fox7.flt.automata.impl.dense.GlushkovMatrixRepresentation;

import java.util.Map;
import java.util.Set;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class GlushkovDoubleMatrixModifier {

	public GlushkovMatrixRepresentation<Double> restrictAlphabet(GlushkovMatrixRepresentation<Double> m,
	                                                             Set<String> symbols) {
		GlushkovMatrixRepresentation<Double> newM = new GlushkovMatrixRepresentation<Double>();
		newM.setX0(m.getX0().clone());
		newM.setP(m.getP().clone());
		newM.setXf(m.getXf().clone());
		newM.setAcceptsEmptyString(m.acceptsEmptyString());
		newM.setZeroValue(Double.valueOf(0.0));
		newM.setOneValue(Double.valueOf(1.0));
		for (Integer index : m.getIndices()) {
			String symbol = m.getSymbol(index);
			if (!symbols.contains(symbol)) {
				newM.getX0().set(index, 0, Double.valueOf(0.0));
				for (int j = 0; j < newM.getP().getRowDimension(); j++)
					newM.getP().set(index, j, Double.valueOf(0.0));
			} else {
				newM.addIndex(index, symbol);
				newM.addSymbol(symbol, index);
			}
		}
		return newM;
	}

	public GlushkovMatrixRepresentation<Double> addWeights(GlushkovMatrixRepresentation<Double> m,
	                                                       Map<String,Double> weights) {
		GlushkovMatrixRepresentation<Double> newM = new GlushkovMatrixRepresentation<Double>();
		newM.setX0(m.getX0().clone());
		newM.setP(m.getP().clone());
		newM.setXf(m.getXf().clone());
		newM.setAcceptsEmptyString(m.acceptsEmptyString());
		newM.setZeroValue(Double.valueOf(0.0));
		newM.setOneValue(Double.valueOf(1.0));
		for (Integer index : m.getIndices()) {
			String symbol = m.getSymbol(index);
			if (weights.containsKey(symbol)) {
				Double weight = weights.get(symbol);
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
