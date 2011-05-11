/**
 * Created on Jul 12, 2009
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.automata.factories.dense;

import gjb.flt.automata.impl.dense.GlushkovMatrixRepresentation;
import gjb.math.Matrix;
import gjb.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public abstract class AbstractGlushkovMatrixProductFactory<T> {

	protected Map<String,Set<Integer>> symbolMap = new HashMap<String,Set<Integer>>();
	protected Map<Integer,String> indexMap = new HashMap<Integer,String>();
	protected Map<Pair<Integer,Integer>,Integer> stateMap = new HashMap<Pair<Integer,Integer>,Integer>();
	protected Matrix<T> x0;
	protected Matrix<T> p;
	protected Matrix<T> exclusive1Xf, exclusive2Xf, unionXf, intersectionXf;
	protected boolean exclusive1AE, exclusive2AE, unionAE, intersectionAE;

	public AbstractGlushkovMatrixProductFactory(GlushkovMatrixRepresentation<T> m1,
	                                            GlushkovMatrixRepresentation<T> m2) {
		initMaps(m1, m2);
		computeInitialStateMatrices(m1, m2);
		computeTransitionMatrix(m1, m2);
		computeFinalStateMatrices(m1, m2);
		computeAcceptsEmpties(m1, m2);
	}

	public GlushkovMatrixRepresentation<T> getExclusiveFirst() {
		GlushkovMatrixRepresentation<T> m = new GlushkovMatrixRepresentation<T>();
		m.setX0(x0);
		m.setP(p);
		m.setXf(exclusive1Xf);
		m.setAcceptsEmptyString(exclusive1AE);
		m.setSymbolMap(symbolMap);
		m.setIndexMap(indexMap);
		return m;
	}

	public GlushkovMatrixRepresentation<T> getExclusiveSecond() {
		GlushkovMatrixRepresentation<T> m = new GlushkovMatrixRepresentation<T>();
		m.setX0(x0);
		m.setP(p);
		m.setXf(exclusive2Xf);
		m.setAcceptsEmptyString(exclusive2AE);
		m.setSymbolMap(symbolMap);
		m.setIndexMap(indexMap);
		return m;
	}
	
	public GlushkovMatrixRepresentation<T> getUnion() {
		GlushkovMatrixRepresentation<T> m = new GlushkovMatrixRepresentation<T>();
		m.setX0(x0);
		m.setP(p);
		m.setXf(unionXf);
		m.setAcceptsEmptyString(unionAE);
		m.setSymbolMap(symbolMap);
		m.setIndexMap(indexMap);
		return m;
	}
	
	public GlushkovMatrixRepresentation<T> getIntersection() {
		GlushkovMatrixRepresentation<T> m = new GlushkovMatrixRepresentation<T>();
		m.setX0(x0);
		m.setP(p);
		m.setXf(intersectionXf);
		m.setAcceptsEmptyString(intersectionAE);
		m.setSymbolMap(symbolMap);
		m.setIndexMap(indexMap);
		return m;
	}
	
	protected void initMaps(GlushkovMatrixRepresentation<T> m1,
			                GlushkovMatrixRepresentation<T> m2) {
		Set<String> alphabet = new HashSet<String>();
		alphabet.addAll(m1.getSymbolValues());
		alphabet.addAll(m2.getSymbolValues());
		int index = 0;
		for (String symbol : alphabet) {
			symbolMap.put(symbol, new HashSet<Integer>());
			Set<Integer> indexSet1 = new HashSet<Integer>();
			if (m1.hasSymbol(symbol))
				indexSet1.addAll(m1.getIndices(symbol));
			indexSet1.add(-1);
			for (Integer i : indexSet1) {
				Set<Integer> indexSet2 = new HashSet<Integer>();
				if (m2.hasSymbol(symbol))
					indexSet2.addAll(m2.getIndices(symbol));
				indexSet2.add(-1);
				for (Integer j : indexSet2) {
					if (i != -1 || j != -1) {
						stateMap.put(new Pair<Integer,Integer>(i, j), index);
						indexMap.put(index, symbol);
						symbolMap.get(symbol).add(index);
						index++;
					}
				}
			}
		}
	}

	protected abstract void computeInitialStateMatrices(GlushkovMatrixRepresentation<T> m1,
	                                                    GlushkovMatrixRepresentation<T> m2);

	protected abstract void computeTransitionMatrix(GlushkovMatrixRepresentation<T> m1,
	                                                GlushkovMatrixRepresentation<T> m2);

	protected abstract void computeFinalStateMatrices(GlushkovMatrixRepresentation<T> m1,
	                                                  GlushkovMatrixRepresentation<T> m2);

	protected void computeAcceptsEmpties(GlushkovMatrixRepresentation<T> m1, 
			                             GlushkovMatrixRepresentation<T> m2) {
		exclusive1AE = m1.acceptsEmptyString() && !m2.acceptsEmptyString();
		exclusive2AE = !m1.acceptsEmptyString() && m2.acceptsEmptyString();
		unionAE = m1.acceptsEmptyString() || m2.acceptsEmptyString();
		intersectionAE = m1.acceptsEmptyString() && m2.acceptsEmptyString();
	}

	protected Set<Integer> getToStates(GlushkovMatrixRepresentation<T> m,
			                           int j, String symbol) {
		Set<Integer> iSet = new HashSet<Integer>();
		if (m.hasSymbol(symbol) && j >= 0) {
			Set<Integer> states = m.getIndices(symbol);
			for (Integer i : states)
				if (m.hasTransition(i, j))
					iSet.add(i);
		}
		if (iSet.isEmpty())
			iSet.add(-1);
		return iSet;
	}

	protected Set<Integer> getToStates(GlushkovMatrixRepresentation<T> m,
			                           String symbol) {
		Set<Integer> iSet = new HashSet<Integer>();
		if (m.hasSymbol(symbol)) {
			Set<Integer> states = m.getIndices(symbol);
			for (Integer i : states)
				if (m.isInitial(i))
					iSet.add(i);
		}
		if (iSet.isEmpty())
			iSet.add(-1);
		return iSet;
	}
	
	protected int getNrStates() {
		return indexMap.size();
	}

}
