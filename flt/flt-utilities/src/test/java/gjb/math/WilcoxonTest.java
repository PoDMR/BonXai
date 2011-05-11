/*
 * Created on Feb 26, 2009
 * Modified on $Date: 2009-10-26 18:37:40 $
 */
package gjb.math;

import gjb.math.WilcoxonSignedRank.Significance;
import gjb.math.WilcoxonSignedRank.WilcoxonTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class WilcoxonTest extends TestCase {

    public static Test suite() {
        return new TestSuite(WilcoxonTest.class);
    }

    public void test_rComputation1() {
        WilcoxonSignedRank test = new WilcoxonSignedRank();
        double[] diffs = {53.50, 61.30, 158.7, 165.2, 200.5, 207.5, -215.3, -220.2, -234.7, -246.3, 256.7, -291.7, 316.0, -321.85, 336.75, 357.9, 399.55, 414.4, 575.0,654.2};
        List<Double> diff = new ArrayList<Double>();
        for (double d : diffs)
            diff.add(d);
        assertEquals("R", 60.0, test.computeR(diff));
    }

    public void test_tableLookup() {
        try {
            assertEquals("R critical",
                         52,
                         WilcoxonTable.getCriticalValue(20, Significance.ALPHA_0_05));
            assertEquals("R critical",
                         13,
                         WilcoxonTable.getCriticalValue(12, Significance.ALPHA_0_05));
        } catch (InappropriateTestException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }

    public void test_hypothesis1() {
        WilcoxonSignedRank wilcoxon = new WilcoxonSignedRank();
        double[] diffs = {53.50, 61.30, 158.7, 165.2, 200.5, 207.5, -215.3, -220.2, -234.7, -246.3, 256.7, -291.7, 316.0, -321.85, 336.75, 357.9, 399.55, 414.4, 575.0,654.2};
        double mu0 = 2000.0;
        List<Double> observations = new ArrayList<Double>();
        for (double diff : diffs)
            observations.add(mu0 + diff);
        Collections.shuffle(observations);
        try {
            assertTrue("H0 accepted",
                       wilcoxon.test(mu0, observations, Significance.ALPHA_0_05));
        } catch (InappropriateTestException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }

    public void test_rComputation2() {
        WilcoxonSignedRank test = new WilcoxonSignedRank();
        double[] diffs = {-0.2, 0.3, 0.4, 0.5, -0.6, 0.7, -0.7, 0.8, 0.9, -1.0, 1.1, 1.3};
        List<Double> diff = new ArrayList<Double>();
        for (double d : diffs)
            diff.add(d);
        assertEquals("R", 22.5, test.computeR(diff));
        
    }

    public void test_hypothesis2() {
        WilcoxonSignedRank wilcoxon = new WilcoxonSignedRank();
        double[] diffs = {-0.2, 0.3, 0.4, 0.5, -0.6, 0.7, -0.7, 0.8, 0.9, -1.0, 1.1, 1.3};
        List<Double> obs1 = new ArrayList<Double>();
        for (int i = 0; i < diffs.length; i++)
            obs1.add(2.0*Math.random());
        List<Double> obs2 = new ArrayList<Double>();
        for (int i = 0; i < diffs.length; i++)
            obs2.add(obs1.get(i) + diffs[i]);
        try {
            assertTrue("H0 accepted",
                       wilcoxon.test(obs1, obs2, Significance.ALPHA_0_05));
        } catch (InappropriateTestException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }
    
    public void test_rComputation3() {
        WilcoxonSignedRank test = new WilcoxonSignedRank();
        double[] diffs = {-0.2, 0.3, 0.4, 0.5, -0.6, 0.7, 0.7, 0.8, 0.9, 1.0, 1.1, 1.3};
        List<Double> diff = new ArrayList<Double>();
        for (double d : diffs)
            diff.add(d);
        assertEquals("R", 6.0, test.computeR(diff));
    }

    public void test_hypothesis3() {
        WilcoxonSignedRank wilcoxon = new WilcoxonSignedRank();
        double[] diffs = {-0.2, 0.3, 0.4, 0.5, -0.6, 0.7, 0.7, 0.8, 0.9, 1.0, 1.1, 1.3};
        List<Double> obs1 = new ArrayList<Double>();
        for (int i = 0; i < diffs.length; i++)
            obs1.add(2.0*Math.random());
        List<Double> obs2 = new ArrayList<Double>();
        for (int i = 0; i < diffs.length; i++)
            obs2.add(obs1.get(i) + diffs[i]);
        try {
            assertFalse("H0 accepted",
                        wilcoxon.test(obs1, obs2, Significance.ALPHA_0_05));
        } catch (InappropriateTestException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }
 
    public void test_notApplicable() {
        WilcoxonSignedRank wilcoxon = new WilcoxonSignedRank();
        assertFalse("applicable", wilcoxon.isApplicable(2, Significance.ALPHA_0_05));
        assertTrue("applicable", wilcoxon.isApplicable(10, Significance.ALPHA_0_05));
    }

}
