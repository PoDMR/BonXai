/*
 * Created on Feb 26, 2007
 * Modified on $Date: 2009-10-26 18:37:40 $
 */
package eu.fox7.math;

import eu.fox7.math.IllDefinedDistributionException;
import eu.fox7.math.ProbabilityDistribution;
import eu.fox7.math.ProbabilityDistributionFactory;
import eu.fox7.math.UniformDistribution;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author eu.fox7
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
            ProbabilityDistribution distr = factory.create("eu.fox7.math.UniformDistribution",
                                                           "min = 4, max = 5");
            assertTrue("correct type", distr instanceof eu.fox7.math.UniformDistribution);
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
            factory.create("eu.fox7.math.NoDistribution", "min = 4, max = 5");
            fail("exception expected");
        } catch (IllDefinedDistributionException e) {}
    }

}
