/*
 * Created on Mar 5, 2009
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package gjb.math;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 * This class computes bin bounds based on a minimal and maximal value.  When
 * the requested number of bins is n, the interval [min, max] will be devided
 * into n - 2 bins, while values smaller than min and larger than max will
 * be classified into the two remaining bins respectively.  Bounds are taken
 * equidistant, i.e., all bins except the first and the last have exactly the
 * same size.
 * E.g., min = 2.0, max = 4.0 and 6 bins will result in the intervals
 * ]-infty, 2.0[, [2.0, 2.5[, [2.5, 3.0[, [3.0, 3.5[, [3.5, 4.0[, [4.0, +infty[
 * Note that data values equal to max will be classified in the last bin.
 */
public class EquidistantBinner extends AbstractBinner {

    protected double min, max;

    /**
     * constructs a Binner that will have nrBins bins with bounds
     * ]-infty, min[,[min, b[1][,..., [b[nrBins-3], max[, [max, +infy[,
     * where b[0] = min, b[nrBins-2] = max, and
     * b[i] = min + i*(max-min)/(nrBins-2)
     * @param min first bin bound
     * @param max last bin bound
     * @param nrBins number of bins to construct
     * @throws InvalidBinDefinitionException when min > max, or nrBins < 2
     */
    public EquidistantBinner(double min, double max, int nrBins)
            throws InvalidBinDefinitionException {
        if (min > max)
            throw new InvalidBinDefinitionException("minimum should be less than maximum");
        if (nrBins < 2)
            throw new InvalidBinDefinitionException("minimal 2 bins");
        this.min = min;
        this.max = max;
        this.nrBins = nrBins;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double[] getBinBounds() {
        double[] bins = new double[getNrBins() - 1];
        bins[0] = getMin();
        if (getNrBins() > 2) {
            bins[bins.length - 1] = getMax();
            double delta = (max - min)/(getNrBins() - 2);
            for (int i = 1; i < bins.length - 1; i++)
                bins[i] = min + i*delta;
        }
        return bins;
    }

}
