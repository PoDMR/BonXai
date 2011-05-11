/*
 * Created on Jun 21, 2005
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package gjb.math;

import java.util.Properties;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class ExponentialIntegerDistribution extends AbstractDistribution {

    public static final String DEFAULT_MEAN = "0.0";
    protected double mean;

    public ExponentialIntegerDistribution() {
        super();
    }

    public ExponentialIntegerDistribution(double mean) {
        this();
        this.mean = mean;
    }

    public ExponentialIntegerDistribution(Properties properties) {
        super(properties);
        String meanStr = properties.getProperty("mean", DEFAULT_MEAN);
        this.mean = Double.valueOf(meanStr).doubleValue();
    }

    /**
     * @return Returns the mean.
     */
    public double getMean() {
        return mean;
    }

    public int getNext() {
        return (int) Math.floor(rand.nextExponential(mean));
    }

}
