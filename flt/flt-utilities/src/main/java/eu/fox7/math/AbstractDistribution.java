/*
 * Created on Feb 28, 2006
 * Modified on $Date: 2009-10-30 08:26:19 $
 */
package eu.fox7.math;

import java.util.Properties;

import org.apache.commons.math.random.RandomDataImpl;

/**
 * @author eu.fox7
 * @version $Revision: 1.2 $
 * 
 */
public abstract class AbstractDistribution implements ProbabilityDistribution {

    public static final String DEFAULT_MIN = "0";
    public static final String DEFAULT_MAX = Integer.toString(Integer.MAX_VALUE);
    protected int min;
    protected int max;
    protected RandomDataImpl rand;

    public AbstractDistribution() {
        rand = new RandomDataImpl();
        reSeed(System.nanoTime() % System.currentTimeMillis());
    }

    public AbstractDistribution(Properties properties) {
        this();
        init(properties);
    }

    public void reSeed() {
    	rand.reSeed();
    }

    public void reSeed(long seed) {
    	rand.reSeed(seed);
    }

    protected void init(Properties properties) throws NumberFormatException {
        String minStr = properties.getProperty("min", DEFAULT_MIN);
        String maxStr = properties.getProperty("max", DEFAULT_MAX);
        setMin(Integer.valueOf(minStr).intValue());
        setMax(Integer.valueOf(maxStr).intValue());
    }

    /**
     * @return Returns the max.
     */
    public int getMax() {
        return max;
    }

    /**
     * @param max The max to set.
     */
    public void setMax(int max) {
        this.max = max;
    }

    /**
     * @return Returns the min.
     */
    public int getMin() {
        return min;
    }

    /**
     * @param min The min to set.
     */
    public void setMin(int min) {
        this.min = min;
    }

    public int getNextConstrained() {
        int n = getMin();
        do {
            n = getNext();
        } while (n < getMin() || n > getMax());
        return n;
    }

}
