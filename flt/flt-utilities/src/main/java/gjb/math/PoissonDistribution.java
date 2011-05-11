/*
 * Created on Feb 28, 2006
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package gjb.math;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class PoissonDistribution extends AbstractDistribution {

    protected double mean;

    public PoissonDistribution(double mean) {
        this.mean = mean;
    }

    public double getMean() {
        return mean;
    }

    public int getNext() {
        return (int) rand.nextPoisson(getMean());
    }

}
