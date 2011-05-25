package gjb.math;
import gjb.math.GaussianIntegerDistribution;
import gjb.math.GeometricDistribution;
import gjb.math.PoissonDistribution;
import gjb.math.ProbabilityDistribution;
import gjb.math.UniformDistribution;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math.MathException;
import org.apache.commons.math.stat.inference.TestUtils;
import org.apache.commons.math.util.MathUtils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/*
 * Created on Feb 28, 2006
 * Modified on $Date: 2009-10-26 18:37:40 $
 */

public class DistributionTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(DistributionTest.class);
    }

    public static Test suite() {
        return new TestSuite(DistributionTest.class);
    }

    public void test_uniformDistribution() {
        final int NUMBER = 100000;
        final int MIN = 5;
        final int MAX = 10;
        final int VALUES = (MAX - MIN + 1);
        final double expectedValue = ((double) NUMBER)/VALUES;
        Map<Integer,Integer> count = new HashMap<Integer,Integer>();
        ProbabilityDistribution distr = new UniformDistribution(MIN, MAX);
        for (int i = 0; i < NUMBER; i++) {
            int n = distr.getNext();
            if (!count.containsKey(n))
                count.put(n, new Integer(0));
            count.put(n, count.get(n) + 1);
        }
        double[] expected = new double[count.size()];
        long[] observed = new long[count.size()];
        int i = 0;
        for (int n : count.keySet()) {
            assertTrue("right range " + n, MIN <= n && n <= MAX);
            expected[i] = expectedValue;
            observed[i] = count.get(n);
            i++;
        }
        try {
            assertTrue("chi^2 uniform",
                       !TestUtils.chiSquareTest(expected, observed, 0.05));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            fail("illegal argument");
        } catch (MathException e) {
            e.printStackTrace();
            fail("math exception");
        }
    }

    public void test_gaussianDistribution() {
        final int NUMBER = 10000;
        final double MEAN = 7.0;
        final double VAR = 3.0;
        Map<Integer,Integer> count = new HashMap<Integer,Integer>();
        GaussianIntegerDistribution distr = new GaussianIntegerDistribution(MEAN, VAR);
        for (int i = 0; i < NUMBER; i++) {
            int n = distr.getNext();
            if (!count.containsKey(n))
                count.put(n, 0);
            count.put(n, count.get(n) + 1);
        }
        double[] expected = new double[count.size()];
        long[] observed = new long[count.size()];
        int i = 0;
        for (int n : count.keySet()) {
            double x = n - 0.5;
            expected[i] = NUMBER*Math.exp(-0.5*(x - MEAN)*(x - MEAN)/(VAR*VAR))/(VAR*Math.sqrt(2*Math.PI));
            observed[i] = count.get(n);
            i++;
        }
        try {
            assertTrue("chi^2 integer gaussian",
                       !TestUtils.chiSquareTest(expected, observed, 0.05));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            fail("illegal argument");
        } catch (MathException e) {
            e.printStackTrace();
            fail("math exception");
        }
    }
}
