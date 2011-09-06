/*
 * Created on Aug 29, 2008
 * Modified on $Date: 2009-10-26 18:37:40 $
 */
package eu.fox7.math;

import eu.fox7.math.Stats;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class StatsTest extends TestCase {

    public static Test suite() {
        return new TestSuite(StatsTest.class);
    }

    public void test_stats01() {
        double[] values = {3.0, 5.0, 7.0};
        Stats stats = new Stats();
        stats.compute(values);
        assertEquals("mean", 5.0, stats.getMean());
        assertEquals("stddev", 2.0, stats.getStddev());
        assertEquals("min", 3.0, stats.getMin());
        assertEquals("max", 7.0, stats.getMax());
        assertEquals("nrValues", 3, stats.getNumberOfValues());
    }
   
    public void test_stats02() {
        double[] values = {-3.0, -5.0, -7.0};
        Stats stats = new Stats();
        stats.compute(values);
        assertEquals("mean", -5.0, stats.getMean());
        assertEquals("stddev", 2.0, stats.getStddev());
        assertEquals("min", -7.0, stats.getMin());
        assertEquals("max", -3.0, stats.getMax());
        assertEquals("nrValues", 3, stats.getNumberOfValues());
    }
    
    public void test_stats03() {
        double[] values = {-3.0, -3.0, -3.0};
        Stats stats = new Stats();
        stats.compute(values);
        assertEquals("mean", -3.0, stats.getMean());
        assertEquals("stddev", 0.0, stats.getStddev());
        assertEquals("min", -3.0, stats.getMin());
        assertEquals("max", -3.0, stats.getMax());
        assertEquals("nrValues", 3, stats.getNumberOfValues());
    }
    
}
