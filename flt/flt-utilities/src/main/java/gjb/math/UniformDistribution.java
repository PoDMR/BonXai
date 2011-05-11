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
public class UniformDistribution extends AbstractDistribution {

    public UniformDistribution(int min, int max) {
        super();
        this.min = min;
        this.max = max;
    }

    public UniformDistribution(Properties properties) {
        super(properties);
    }

    public int getNext() {
        return rand.nextInt(getMin(), getMax()); 
    }

}
