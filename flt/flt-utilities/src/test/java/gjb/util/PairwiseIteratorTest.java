/*
 * Created on Jul 9, 2007
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package gjb.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class PairwiseIteratorTest extends TestCase {

    public static Test suite() {
        return new TestSuite(PairwiseIteratorTest.class);
    }

    public void test_twoLists() {
        List<Integer> list1 = Arrays.asList(new Integer[] {1, 2, 3, 4});
        List<Integer> list2 = Arrays.asList(new Integer[] {4, 3, 2, 1});
        PairwiseIterator<Integer,Integer> it = new PairwiseIterator<Integer,Integer>(list1, list2);
        for ( ; it.hasNext(); ) {
            Pair<Integer,Integer> numberPair = it.next();
            assertEquals("equal", 5, numberPair.getFirst() + numberPair.getSecond());
        }
    }

    public void test_twoListsInhomogeneous() {
        List<String> list1 = Arrays.asList(new String[] {"abcd", "abc", "ab", "a"});
        List<Integer> list2 = Arrays.asList(new Integer[] {4, 3, 2, 1});
        PairwiseIterator<String,Integer> it = new PairwiseIterator<String,Integer>(list1, list2);
        for ( ; it.hasNext(); ) {
            Pair<String,Integer> pair = it.next();
            assertEquals("equal",
                         pair.getFirst().length(),
                         (int) pair.getSecond());
        }
    }
    
    public void test_twoListsInhomogeneousUnequalLengthLeft() {
        List<String> list1 = Arrays.asList(new String[] {"abcd", "abc"});
        List<Integer> list2 = Arrays.asList(new Integer[] {4, 3, -1, -1});
        PairwiseIterator<String,Integer> it = new PairwiseIterator<String,Integer>(list1, list2);
        for ( ; it.hasNext(); ) {
            Pair<String,Integer> pair = it.next();
            String str = pair.getFirst();
            assertEquals("equal",
                         str != null ? str.length() : -1,
                                 (int) pair.getSecond());
        }
    }
    
    public void test_twoListsInhomogeneousUnequalLengthRight() {
        List<String> list1 = Arrays.asList(new String[] {"abcd", "abc", "ab", "cd"});
        List<Integer> list2 = Arrays.asList(new Integer[] {4, 3});
        PairwiseIterator<String,Integer> it = new PairwiseIterator<String,Integer>(list1, list2);
        for ( ; it.hasNext(); ) {
            Pair<String,Integer> pair = it.next();
            Integer i = pair.getSecond();
            assertEquals("equal", pair.getFirst().length(),
                         i != null ? (int) i : 2);
        }
    }

    public void test_emptyLists() {
        List<Integer> list1 = new LinkedList<Integer>();
        List<String> list2 = new LinkedList<String>();
        PairwiseIterator<Integer,String> it = new PairwiseIterator<Integer,String>(list1, list2);
        assertTrue("empty", !it.hasNext());
        try {
            it.next();
            fail("exception expected");
        } catch (NoSuchElementException e) {}
    }

}
