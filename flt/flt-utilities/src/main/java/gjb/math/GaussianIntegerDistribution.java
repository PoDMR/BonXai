/*
 * Created on Jun 22, 2005
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package gjb.math;

import java.util.Properties;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class GaussianIntegerDistribution extends AbstractDistribution {

    protected double mean = 0.0;
    protected double variance = 1.0;

    public GaussianIntegerDistribution() {
        super();
    }

    public GaussianIntegerDistribution(double mean, double variance) {
        this();
        this.mean = mean;
        this.variance = variance;
    }

    public GaussianIntegerDistribution(Properties properties) {
        super(properties);
        String meanStr = properties.getProperty("mean", "0.0");
        String varianceStr = properties.getProperty("variance", "1.0");
        this.mean = Double.valueOf(meanStr).doubleValue();
        this.variance= Double.valueOf(varianceStr).doubleValue();
   }

    /**
     * @return Returns the mean.
     */
    public double getMean() {
        return mean;
    }

    public double getVariance() {
        return variance;
    }

    public int getNext() {
        return (int) Math.ceil(rand.nextGaussian(getMean(), getVariance()));
    }

}
