package eu.fox7.util;
import junit.framework.*;

import java.util.HashSet;
import java.util.Set;

public class CollectionsTest extends TestCase {
  
    public static Test suite() {
        return new TestSuite(CollectionsTest.class);
    }

    public void test_takeOne() throws Exception {
        Set<String> set = new HashSet<String>();
        set.add("b");
        set.add("c");
        set.add("a");
        for (int i = set.size(); i > 0; i--) {
            assertEquals("size", i, set.size());
            String s = eu.fox7.util.Collections.takeOne(set);
            assertTrue(s.equals("a") || s.equals("b") || s.equals("c"));
        }
        assertTrue("empty", set.isEmpty());
        assertEquals("null value", null, eu.fox7.util.Collections.takeOne(set));
        assertEquals(0, set.size());
    }
    
    public void test_intersection() throws Exception {
        Set<String> set1 = new HashSet<String>();
        set1.add("a");
        set1.add("b");
        set1.add("c");
        Set<String> set2 = new HashSet<String>();
        set2.add("a");
        set2.add("c");
        set2.add("d");
        Set<String> intersection1 = eu.fox7.util.Collections.intersect(set1, set2);
        assertEquals("size", 2, intersection1.size());
        assertTrue("element a", intersection1.contains("a"));
        assertTrue("element c", intersection1.contains("c"));
        assertTrue("element b", !intersection1.contains("b"));
        assertTrue("element d", !intersection1.contains("d"));
        Set<String> intersection2 = eu.fox7.util.Collections.intersect(set2, set1);
        assertEquals("size", 2, intersection2.size());
        assertTrue("element a", intersection2.contains("a"));
        assertTrue("element c", intersection2.contains("c"));
        assertTrue("element b", !intersection2.contains("b"));
        assertTrue("element d", !intersection2.contains("d"));
        set2 = new HashSet<String>();
        set2.add("d");
        set2.add("e");
        Set<String> intersection3 = eu.fox7.util.Collections.intersect(set1, set2);
        assertTrue("size", intersection3.isEmpty());
        set2 = new HashSet<String>();
        Set<String> intersection4 = eu.fox7.util.Collections.intersect(set1, set2);
        assertTrue("size", intersection4.isEmpty());
        Set<String> intersection5 = eu.fox7.util.Collections.intersect(set2, set1);
        assertTrue("size", intersection5.isEmpty());
    }

}
