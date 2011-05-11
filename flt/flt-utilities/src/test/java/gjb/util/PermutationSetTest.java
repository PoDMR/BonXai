package gjb.util;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import gjb.util.PermutationSet;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/*
 * Created on Aug 16, 2004
 *
 */

/**
 * @author gjb
 * @version 1.0
 */
public class PermutationSetTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(PermutationSetTest.class);
    }

	public static Test suite() {
		return new TestSuite(PermutationSetTest.class);
	}

    public void test_permutationIterator() {
        List<String> list = new LinkedList<String>();
        list.add("a");
        list.add("b");
        list.add("c");
        PermutationSet<String> pSet = new PermutationSet<String>(list);
        Set<List> allSeqs = new HashSet<List>();
        for (Iterator it = pSet.iterator(); it.hasNext(); ) {
            Set<String> set = new HashSet<String>();
            set.addAll(list);
            List seq = (List) it.next();
            assertEquals("sequence size", 3, seq.size());
            set.removeAll(seq);
            assertEquals("all elements", 0, set.size());
            assertTrue("no new sequence: " + set, !allSeqs.contains(seq));
            allSeqs.add(seq);
        }
        assertEquals("number of permutatins", 6, allSeqs.size());
    }

    public void test_emptyPermutationIterator() {
        List<String> list = new LinkedList<String>();
        PermutationSet<String> pSet = new PermutationSet<String>(list);
        Iterator it = pSet.iterator();
        assertTrue(it.hasNext());
        List seq = (List) it.next();
        assertEquals(0, seq.size());
        assertTrue(!it.hasNext());
    }
    
    public void test_singletonPermutationIteration() {
        List<String> list = new LinkedList<String>();
        list.add("a");
        PermutationSet<String> pSet = new PermutationSet<String>(list);
        Iterator it = pSet.iterator();
        assertTrue(it.hasNext());
        List seq = (List) it.next();
        assertEquals(1, seq.size());
        assertEquals("a", (String) seq.get(0));
        assertTrue(!it.hasNext());
    }

}
