/*
 * Created on Mar 5, 2009
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package gjb.math;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 * This class is based on EquidistantBinner, but the bounds are computed using
 * a logarithmic scale.  It is hence better suited for data that spans several
 * orders of magnitude.
 */
public class LogEquidistantBinner extends EquidistantBinner {

    public LogEquidistantBinner(double min, double max, int nrBins)
            throws InvalidBinDefinitionException {
        super(min, max, nrBins);
        if (min <= 0.0)
        	throw new InvalidBinDefinitionException("minimum must be strictly positive");
    }

    @Override
    public double[] getBinBounds() {
        double[] bins = new double[getNrBins() - 1];
        bins[0] = getMin();
        if (getNrBins() > 2) {
            bins[bins.length - 1] = getMax();
            double delta = Math.pow(10, (Math.log10(max) - Math.log10(min))/(getNrBins() - 2));
            for (int i = 1; i < bins.length - 1; i++)
                bins[i] = min*Math.pow(delta, i);
        }
        return bins;
    }

}
