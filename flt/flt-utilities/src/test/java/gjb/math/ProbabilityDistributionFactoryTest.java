/*
 * Created on Feb 26, 2007
 * Modified on $Date: 2009-10-26 18:37:40 $
 */
package gjb.math;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class ProbabilityDistributionFactoryTest extends TestCase {

    public static Test suite() {
        return new TestSuite(ProbabilityDistributionFactoryTest.class);
    }

    public void test_uniformDistribution() {
        ProbabilityDistributionFactory factory = new ProbabilityDistributionFactory();
        try {
            ProbabilityDistribution distr = factory.create("gjb.math.UniformDistribution",
                                                           "min = 4, max = 5");
            assertTrue("correct type", distr instanceof gjb.math.UniformDistribution);
            assertEquals("min", 4, ((UniformDistribution) distr).getMin());
            assertEquals("max", 5, ((UniformDistribution) distr).getMax());
        } catch (IllDefinedDistributionException e) {
            e.printStackTrace();
            fail("no exception expected");
        }
    }

    public void test_nonexistingDistribution() {
        ProbabilityDistributionFactory factory = new ProbabilityDistributionFactory();
        try {
            factory.create("gjb.math.NoDistribution", "min = 4, max = 5");
            fail("exception expected");
        } catch (IllDefinedDistributionException e) {}
    }

}
