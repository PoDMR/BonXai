/**
 * Created on Sep 22, 2009
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.automata.factories.dense;

import eu.fox7.flt.automata.impl.dense.GlushkovMatrixRepresentation;
import eu.fox7.math.BigIntegerMatrix;
import eu.fox7.util.Pair;

import java.math.BigInteger;
import java.util.Set;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class GlushkovBigIntegerMatrixProductFactory extends
        AbstractGlushkovMatrixProductFactory<BigInteger> {

	public GlushkovBigIntegerMatrixProductFactory(GlushkovMatrixRepresentation<BigInteger> m1,
	                                              GlushkovMatrixRepresentation<BigInteger> m2) {
		super(m1, m2);
	}

	protected void computeInitialStateMatrices(GlushkovMatrixRepresentation<BigInteger> m1,
			                                   GlushkovMatrixRepresentation<BigInteger> m2) {
		x0 = new BigIntegerMatrix(getNrStates(), 1);
		for (String symbol : symbolMap.keySet()) {
			Set<Integer> set1 = getToStates(m1, symbol);
			Set<Integer> set2 = getToStates(m2, symbol);
			for (Integer i1 : set1)
				for (Integer i2 : set2) {
					if (i1 != -1 || i2 != -1) {
						Pair<Integer,Integer> indices = new Pair<Integer,Integer>(i1, i2);
						x0.set(stateMap.get(indices), 0, BigInteger.ONE);
					}
				}
		}
	}

	protected void computeTransitionMatrix(GlushkovMatrixRepresentation<BigInteger> m1,
			                               GlushkovMatrixRepresentation<BigInteger> m2) {
		p = new BigIntegerMatrix(getNrStates(), getNrStates());
		for (Pair<Integer,Integer> indices : stateMap.keySet()) {
			int j1 = indices.getFirst();
			int j2 = indices.getSecond();
			int j = stateMap.get(indices);
			for (String symbol : symbolMap.keySet()) {
				Set<Integer> i1Set = getToStates(m1, j1, symbol);
				Set<Integer> i2Set = getToStates(m2, j2, symbol);
				for (Integer i1 : i1Set)
					for (Integer i2 : i2Set) {
						if (i1 != -1 || i2 != -1) {
							Pair<Integer,Integer> toIndices = new Pair<Integer,Integer>(i1, i2);
							int i = stateMap.get(toIndices);
							p.set(i, j, BigInteger.ONE);
						}
					}
			}
		}
	}

	protected void computeFinalStateMatrices(GlushkovMatrixRepresentation<BigInteger> m1,
			                                 GlushkovMatrixRepresentation<BigInteger> m2) {
		exclusive1Xf = new BigIntegerMatrix(1, getNrStates());
		exclusive2Xf = new BigIntegerMatrix(1, getNrStates());
		unionXf = new BigIntegerMatrix(1, getNrStates());
		intersectionXf = new BigIntegerMatrix(1, getNrStates());
		for (Pair<Integer,Integer> indices : stateMap.keySet()) {
			int index = stateMap.get(indices);
			Integer j1 = indices.getFirst();
			boolean s1 = j1 >= 0 && m1.isFinal(j1);
			Integer j2 = indices.getSecond();
			boolean s2 = j2 >= 0 && !m2.isFinal(j2);
			if (s1 && !s2)
				exclusive1Xf.set(0, index, BigInteger.ONE);
			if (!s1 && s2)
				exclusive2Xf.set(0, index, BigInteger.ONE);
			if (s1 || s2)
				unionXf.set(0, index, BigInteger.ONE);
			if (s1 && s2)
				intersectionXf.set(0, index, BigInteger.ONE);
		}
	}

}
