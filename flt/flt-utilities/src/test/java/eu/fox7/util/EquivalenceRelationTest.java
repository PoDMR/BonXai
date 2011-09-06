/*
 * Created on Feb 9, 2006
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package eu.fox7.util;

import eu.fox7.util.AbstractEquivalenceRelation;
import eu.fox7.util.EquivalenceRelation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class EquivalenceRelationTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(EquivalenceRelationTest.class);
    }

    public static Test suite() {
        return new TestSuite(EquivalenceRelationTest.class);
    }

    public void test_firstChars() {
        String[] strs = {"abc", "aaa", "cad", "bcc", "aaq", "bqc"};
        Map<String,Integer> sizeMap = new HashMap<String,Integer>();
        sizeMap.put("a", 3);
        sizeMap.put("b", 2);
        sizeMap.put("c", 1);
        List<String> strList = Arrays.asList(strs);
        EquivalenceRelation<String> relation = new FirstCharEquivRel();
        Set<Set<String>> classes = relation.getClasses(strList);
        assertEquals("number of classes", 3, classes.size());
        for (Set<String> set : classes) {
            assertTrue("class size > 0", set.size() > 0);
            String firstChar = set.iterator().next().substring(0, 1);
            assertTrue("class actually exists", sizeMap.containsKey(firstChar));
            assertEquals("size matches for" + firstChar,
                         sizeMap.get(firstChar).intValue(),
                         set.size());
        }
    }

    protected static class FirstCharEquivRel
            extends AbstractEquivalenceRelation<String> {

        @Override
        public boolean areEquivalent(String element1, String element2) {
            return element2.startsWith(element1.substring(0, 1));
        }
        
    }
}
