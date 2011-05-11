/**
 * Created on Jun 9, 2009
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.automata.impl.dense;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class GlushkovMatrixRepresentation<T> extends MatrixRepresentation<T> {

	protected Map<Integer,String> indexMap = new HashMap<Integer,String>();
	protected Map<String,Set<Integer>> symbolMap = new HashMap<String,Set<Integer>>();

	public GlushkovMatrixRepresentation() {
		super();
	}

	public Set<String> getSymbolValues() {
		return Collections.unmodifiableSet(symbolMap.keySet());
	}

	public boolean hasSymbol(String symbol) {
		return symbolMap.containsKey(symbol);
	}

	public Set<Integer> getIndeces(String symbolValue) {
		return symbolMap.get(symbolValue);
	}

	public void setSymbolMap(Map<String,Set<Integer>> symbolMap) {
    	this.symbolMap = symbolMap;	    
    }

	public void setIndexMap(Map<Integer, String> indexMap) {
		this.indexMap = indexMap;
    }

	public String getSymbol(Integer i) {
		return indexMap.get(i);
	}

	public Set<Integer> getIndices() {
        return Collections.unmodifiableSet(indexMap.keySet());
    }

	public Set<Integer> getIndices(String symbol) {
		return Collections.unmodifiableSet(symbolMap.get(symbol));
	}

	public void addSymbol(String symbol, Integer stateNumber) {
    	if (!hasSymbol(symbol))
    		symbolMap.put(symbol, new HashSet<Integer>());
    	symbolMap.get(symbol).add(stateNumber);
    }

	public void addIndex(Integer stateNumber, String symbol) {
    	indexMap.put(stateNumber, symbol);
    }

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < indexMap.size(); i++)
			str.append(indexMap.get(i)).append(":").append(i).append(i < indexMap.size() - 1 ? " " : "\n");
		str.append("x0 = \n");
		for (int i = 0; i < x0.getRowDimension(); i++)
			str.append(String.format("%6.0f\n", x0.get(i, 0)));
		str.append("p = \n");
		for (int i = 0;i < p.getRowDimension(); i++)
			for (int j = 0; j < p.getColumnDimension(); j++)
				str.append(String.format("%6.0f%s",
						                 p.get(i, j),
						                 j < p.getColumnDimension() - 1 ? " " : "\n"));
		str.append("xf = \n");
		for (int j = 0; j < xf.getColumnDimension(); j++)
			str.append(String.format("%6.0f%s",
					                 xf.get(0, j),
					                 j < xf.getColumnDimension() - 1 ? " " : "\n"));
		str.append("accepts epsilon = ").append(acceptsEmptyString() ? 1 : 0).append("\n");
		return str.toString();
	}

}
