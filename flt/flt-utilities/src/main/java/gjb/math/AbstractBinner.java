/**
 * 
 */
package gjb.math;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lucg5005
 *
 */
public abstract class AbstractBinner implements Binner {

    protected int nrBins;

    public int getNrBins() {
        return nrBins;
    }

	public List<List<Double>> computeBins(double[] data) {
		double[] bounds = getBinBounds();
		List<List<Double>> bins = new ArrayList<List<Double>>();
		for (int i = 0; i < getNrBins(); i++)
			bins.add(new ArrayList<Double>());
		for (int i = 0; i < data.length; i++) {
			boolean isClassified = false;
			for (int j = 0; j < bounds.length; j++)
				if (data[i] < bounds[j]) {
					bins.get(j).add(data[i]);
					isClassified = true;
					break;
				}
			if (!isClassified)
				bins.get(getNrBins() - 1).add(data[i]);
		}
		return bins;
	}

	public long[] computeBinCounts(double[] data) {
		double[] bounds = getBinBounds();
		long[] counts = new long[getNrBins()];
		for (int i = 0; i < data.length; i++) {
			boolean isClassified = false;
			for (int j = 0; j < bounds.length; j++)
				if (data[i] < bounds[j]) {
					counts[j]++;
					isClassified = true;
					break;
				}
			if (!isClassified)
				counts[getNrBins() - 1]++;
		}
		return counts;
	}

}
