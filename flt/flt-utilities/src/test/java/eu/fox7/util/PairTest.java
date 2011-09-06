/*
 * Created on Mar 15, 2007
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package eu.fox7.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import eu.fox7.util.Pair;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PairTest extends TestCase {

    public static Test suite() {
        return new TestSuite(PairTest.class);
    }

    public void test_equality() {
        Pair<String,Integer> p1 = new Pair<String,Integer>("alpha", 3);
        Pair<String,Integer> p2 = new Pair<String,Integer>("alpha", 3);
        assertTrue("equality", p1.equals(p2));
    }

    public void test_inequality1() {
        Pair<String,Integer> p1 = new Pair<String,Integer>("alpha", 3);
        Pair<String,Integer> p2 = new Pair<String,Integer>("alpha", 2);
        assertTrue("equality", !p1.equals(p2));
    }
    
    public void test_inequality2() {
        Pair<String,Integer> p1 = new Pair<String,Integer>("alpha", 3);
        Pair<String,Integer> p2 = new Pair<String,Integer>("beta", 3);
        assertTrue("equality", !p1.equals(p2));
    }

    public void test_set() {
        Pair<String,Integer> p1 = new Pair<String,Integer>("alpha", 3);
        Pair<String,Integer> p2 = new Pair<String,Integer>("beta", 3);
        Pair<String,Integer> p3 = new Pair<String,Integer>("beta", 3);
        Set<Pair<String,Integer>> set = new HashSet<Pair<String,Integer>>();
        set.add(p1);
        set.add(p2);
        set.add(p3);
        assertEquals("set size", 2, set.size());
    }

    public void test_map() {
        Pair<String,Integer> p1 = new Pair<String,Integer>("alpha", 3);
        Pair<String,Integer> p2 = new Pair<String,Integer>("beta", 3);
        Pair<String,Integer> p3 = new Pair<String,Integer>("beta", 3);
        Map<Pair<String,Integer>,Double> map = new HashMap<Pair<String,Integer>,Double>();
        map.put(p1, 0.1);
        map.put(p2, 0.2);
        map.put(p3, 0.3);
        assertEquals("set size", 2, map.size());
        assertEquals("value 1", 0.1, map.get(p1));
        assertEquals("value 2", 0.3, map.get(p2));
        assertEquals("value 3", 0.3, map.get(p3));
    }

}
