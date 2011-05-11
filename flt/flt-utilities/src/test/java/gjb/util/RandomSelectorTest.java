/*
 * Created on Feb 22, 2007
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package gjb.util;

import gjb.math.IllDefinedDistributionException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import static java.lang.Math.abs;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class RandomSelectorTest extends TestCase {

    public static Test suite() {
        return new TestSuite(RandomSelectorTest.class);
    }

    public void test_selection() {
        Set<String> set = new HashSet<String>(Arrays.asList(new String[] {"a", "b", "c", "d"}));
        testSet(set);
        testList(set);
    }

    public void test_selectionFromEmptySet() {
        Set<String> set = new HashSet<String>();
        testSet(set);
        testList(set);
    }

    public void test_selectionFromSingleton() {
        Set<String> set = new HashSet<String>(Arrays.asList(new String[] {"a"}));
        testSet(set);
        testList(set);
    }

    public void test_distributionSelection() {
        Map<String,Double> distr = new HashMap<String,Double>();
        distr.put("a", 0.1);
        distr.put("b", 0.6);
        distr.put("c", 0.3);
        try {
            Map<String,Double> realizedDistr = new HashMap<String,Double>();
            RandomSelector<String> selector = new RandomSelector<String>(distr);
            int n = 10000;
            for (int i = 0; i < n; i++) {
                String element = selector.selectOne();
                if (!realizedDistr.containsKey(element))
                    realizedDistr.put(element, 0.0);
                realizedDistr.put(element, realizedDistr.get(element) + 1.0/n);
            }
            for (String element : distr.keySet())
                assertTrue("element " + element,
                           abs(distr.get(element) - realizedDistr.get(element)) < 0.01);
        } catch (IllDefinedDistributionException e) {
            e.printStackTrace();
            fail("no exception should be thrown");
        }
    }

    public void test_distributionSelectionSingleton() {
        Map<String,Double> distr = new HashMap<String,Double>();
        distr.put("a", 1.0);
        try {
            Map<String,Double> realizedDistr = new HashMap<String,Double>();
            RandomSelector<String> selector = new RandomSelector<String>(distr);
            int n = 10000;
            for (int i = 0; i < n; i++) {
                String element = selector.selectOne();
                if (!realizedDistr.containsKey(element))
                    realizedDistr.put(element, 0.0);
                realizedDistr.put(element, realizedDistr.get(element) + 1.0/n);
            }
            for (String element : distr.keySet())
                assertTrue("element " + element,
                           abs(distr.get(element) - realizedDistr.get(element)) < 1e-8);
        } catch (IllDefinedDistributionException e) {
            e.printStackTrace();
            fail("no exception should be thrown");
        }
    }
    
    public void test_invalidDistribution() {
        Map<String,Double> distr = new HashMap<String,Double>();
        distr.put("a", 0.05);
        distr.put("b", 0.6);
        distr.put("c", 0.3);
        try {
            new RandomSelector<String>(distr);
            fail("exception must be thrown");
        } catch (IllDefinedDistributionException e) {}
        distr = new HashMap<String,Double>();
        distr.put("a", 0.05);
        distr.put("b", 0.8);
        distr.put("c", 0.3);
        try {
            new RandomSelector<String>(distr);
            fail("exception must be thrown");
        } catch (IllDefinedDistributionException e) {}
    }

    protected void testSet(Set<String> set) {
        RandomSelector<String> selector = new RandomSelector<String>();
        for (int i = 0; i < set.size(); i++) {
            Set<String> subset = selector.selectSubsetFrom(set, i);
            assertEquals("size", i, subset.size());
            assertTrue("member", set.containsAll(subset));
        }
        Set<String> subset = selector.selectSubsetFrom(set, set.size() + 5);
        assertEquals("size", set.size(), subset.size());
        assertTrue("member", set.containsAll(subset) && subset.containsAll(set));
    }

    protected void testList(Set<String> set) {
        RandomSelector<String> selector = new RandomSelector<String>();
        for (int i = 0; i < set.size(); i++) {
            List<String> sublist = selector.selectSublistFrom(set, i);
            assertEquals("size", i, sublist.size());
            assertTrue("member", set.containsAll(sublist));
            assertTrue("unique", isUnique(sublist));
        }
        List<String> sublist = selector.selectSublistFrom(set, set.size() + 5);
        assertEquals("size", set.size(), sublist.size());
        assertTrue("member", set.containsAll(sublist) && sublist.containsAll(set));
    }

    protected boolean isUnique(List<String> list) {
        Set<String> set = new HashSet<String>();
        set.addAll(list);
        return set.size() == list.size();
    }

}
