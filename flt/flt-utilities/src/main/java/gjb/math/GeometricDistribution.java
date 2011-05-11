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
public class GeometricDistribution extends AbstractDistribution {

    protected double p;

    public GeometricDistribution(double p) {
        super();
        this.p = p;
    }

    public double getP() {
        return p;
    }

    public int getNext() {
        int r = 0;
        do {
            r++;
        } while (Math.random() > p);
        return r;
    }

}
