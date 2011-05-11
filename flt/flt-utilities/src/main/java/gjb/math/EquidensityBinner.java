/*
 * Created on Mar 5, 2009
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package gjb.math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math.random.RandomData;
import org.apache.commons.math.random.RandomDataImpl;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 * This class computes bin bounds based on actual data value.  When
 * the requested number of bins is n, the intervals will be computed such that
 * the number of values in each interval is approximately equal.  The bounds
 * will be based on the arithmetic average of the maximal and minimal values
 * in the left and right bin respectively.  If the number of bins does not
 * divide the number of data values, the content size of the bins will be
 * uniformly random increased for the remainder.
 * E.g., data = {1.1, 1.8, 2.1, 5.9, 6.1, 6.3, 6.5, 7.0} and 4 bins would
 * result in the following partition:
 * {{1.1, 1.8}, {2.1, 5.9}, {6.1, 6.3}, {6.5, 7.0}} and hence in the bins
 * ]-infty, 1.95[, [1.95, 6.0[, [6.0, 6.4[, [6.4, +infty[
 * Note that no bins can be empty for the data values used to construct the
 * Binner
 */
public class EquidensityBinner extends AbstractBinner {

    protected double[] data;
    protected RandomData rand = new RandomDataImpl();

    public EquidensityBinner(double[] data, int nrBins)
            throws InvalidBinDefinitionException, InvalidDataException {
    	super();
    	if (data.length < nrBins)
    		throw new InvalidBinDefinitionException("too few data for number of bins");
    	if (!isValid(data))
    		throw new InvalidDataException("negative numbers are not allowed");
    	this.data = data;
    	this.nrBins = nrBins;
    }

    protected boolean isValid(double[] data) {
		return true;
	}

    public double[] getData() {
        return this.data;
    }

    public double[] getBinBounds() {
        int nrPerBin = getData().length/getNrBins();
        int[] numbers = new int[getNrBins()];
        for (int i = 0; i < numbers.length; i++)
            numbers[i] = nrPerBin;
        for (int i = 0; i < getData().length % getNrBins(); i++)
            numbers[rand.nextInt(0, numbers.length - 1)]++;
        List<Double> dataList = new ArrayList<Double>();
        for (double x : data)
        	dataList.add(x);
        Collections.sort(dataList);
        double[] bins = new double[getNrBins() - 1];
        int currentPos = 0;
        for (int i = 0; i < bins.length; i++) {
        	currentPos += numbers[i];
            bins[i] = computeBin(dataList.get(currentPos - 1),
            		             dataList.get(currentPos));
        }
        return bins;
    }

	protected double computeBin(double data1, double data2) {
		return (data1 + data2)/2;
	}

}
