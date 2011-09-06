/*
 * Created on Mar 5, 2009
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package eu.fox7.math;

import java.util.List;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public interface Binner {

	/**
	 * retrieve the number of bins that will be used by the Binner
	 * @return int representing the Binner's number of bins
	 */
	public int getNrBins();
	/**
	 * computes and returns an array of size n - 1 for an n bins Binner,
	 * resulting in bins: ]-infty, b[0][, [b[0], b[1][,..., [b[n-3], b[n-2][,
	 * [b[n-2], +infty[
	 * @return double[] bin boundaries 
	 */
    public double[] getBinBounds();
    /**
     * classification of data in the Binner's bins, empty bins are represented
     * by empty lists.  The number of lists is equal to the Binner's number of
     * bins.
     * @param data to be classified in bins
     * @return List<List<Double>> data values classified in bins, each
     *         List<Double> represents a bin
     */
    public List<List<Double>> computeBins(double[] data);
    /**
     * counts the number of data values that is classified in each bin, the size
     * of the array is the Binner's number of bins.
     * @param data to be classified
     * @return long[] count of data values in each of the bins
     */
    public long[] computeBinCounts(double[] data);

}
