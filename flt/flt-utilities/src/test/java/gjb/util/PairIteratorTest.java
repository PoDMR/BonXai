/*
 * Created on Feb 20, 2007
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package gjb.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class PairIteratorTest extends TestCase {

    public static Test suite() {
        return new TestSuite(PairIteratorTest.class);
    }

    public void test_listNonInclusive() {
        List<Integer> list = Arrays.asList(new Integer[] {-1, 7, 2, 3, 0, 10, 2});
        List<Pair<Integer,Integer>> pairs = new ArrayList<Pair<Integer,Integer>>();
        PairIterator<Integer> pairIt = new PairIterator<Integer>(list);
        assertEquals("size", 15, pairIt.size());
        for (; pairIt.hasNext(); )
            pairs.add(pairIt.next());
        assertEquals("number of pairs", 15, pairs.size());
        for (Pair<Integer, Integer> pair : pairs)
            assertTrue("pair " + pair.toString(),
                       pair.getFirst() < pair.getSecond());
        for (int i = 1; i < pairs.size(); i++)
            assertTrue("pair " + pairs.get(i).toString(),
                       pairs.get(i-1).getFirst() <= pairs.get(i).getFirst());
    }

    public void test_listInclusive() {
        List<Integer> list = Arrays.asList(new Integer[] {-1, 7, 2, 3, 0, 10, 2});
        List<Pair<Integer,Integer>> pairs = new ArrayList<Pair<Integer,Integer>>();
        PairIterator<Integer> pairIt = new PairIterator<Integer>(list, true);
        assertEquals("size", 21, pairIt.size());
        for (; pairIt.hasNext(); )
            pairs.add(pairIt.next());
        assertEquals("number of pairs", 21, pairs.size());
        for (Pair<Integer, Integer> pair : pairs)
            assertTrue("pair condition " + pair.toString(),
                       pair.getFirst() <= pair.getSecond());
        for (int i = 1; i < pairs.size(); i++)
            assertTrue("consecutive pairs " + pairs.get(i).toString(),
                       pairs.get(i-1).getFirst() <= pairs.get(i).getFirst());
    }
    
    public void test_listNonInclusiveSingleton() {
        List<Integer> list = Arrays.asList(new Integer[] {-1});
        List<Pair<Integer,Integer>> pairs = new ArrayList<Pair<Integer,Integer>>();
        PairIterator<Integer> pairIt = new PairIterator<Integer>(list);
        assertEquals("size", 0, pairIt.size());
        for (; pairIt.hasNext(); )
            pairs.add(pairIt.next());
        assertEquals("number of pairs", 0, pairs.size());
        for (Pair<Integer, Integer> pair : pairs)
            assertTrue("pair condition " + pair.toString(),
                       pair.getFirst() < pair.getSecond());
        for (int i = 1; i < pairs.size(); i++)
            assertTrue("consecutive pairs " + pairs.get(i).toString(),
                       pairs.get(i-1).getFirst() <= pairs.get(i).getFirst());
    }
    
    public void test_listInclusiveSingleton() {
        List<Integer> list = Arrays.asList(new Integer[] {-1});
        List<Pair<Integer,Integer>> pairs = new ArrayList<Pair<Integer,Integer>>();
        PairIterator<Integer> pairIt = new PairIterator<Integer>(list, true);
        assertEquals("size", 1, pairIt.size());
        for (; pairIt.hasNext(); )
            pairs.add(pairIt.next());
        assertEquals("number of pairs", 1, pairs.size());
        for (Pair<Integer, Integer> pair : pairs)
            assertTrue("pair condition " + pair.toString(),
                       pair.getFirst() <= pair.getSecond());
        for (int i = 1; i < pairs.size(); i++)
            assertTrue("consecutive pairs " + pairs.get(i).toString(),
                       pairs.get(i-1).getFirst() <= pairs.get(i).getFirst());
    }
    
    public void test_listNonInclusiveEmptySet() {
        List<Integer> list = Arrays.asList(new Integer[] {});
        List<Pair<Integer,Integer>> pairs = new ArrayList<Pair<Integer,Integer>>();
        PairIterator<Integer> pairIt = new PairIterator<Integer>(list);
        assertEquals("size", 0, pairIt.size());
        for (; pairIt.hasNext(); )
            pairs.add(pairIt.next());
        assertEquals("number of pairs", 0, pairs.size());
        for (Pair<Integer, Integer> pair : pairs)
            assertTrue("pair condition " + pair.toString(),
                       pair.getFirst() < pair.getSecond());
        for (int i = 1; i < pairs.size(); i++)
            assertTrue("consecutive pairs " + pairs.get(i).toString(),
                       pairs.get(i-1).getFirst() <= pairs.get(i).getFirst());
    }
    
    public void test_listInclusiveEmptySet() {
        List<Integer> list = Arrays.asList(new Integer[] {});
        List<Pair<Integer,Integer>> pairs = new ArrayList<Pair<Integer,Integer>>();
        PairIterator<Integer> pairIt = new PairIterator<Integer>(list, true);
        assertEquals("size", 0, pairIt.size());
        for (; pairIt.hasNext(); )
            pairs.add(pairIt.next());
        assertEquals("number of pairs", 0, pairs.size());
        for (Pair<Integer, Integer> pair : pairs)
            assertTrue("pair condition " + pair.toString(),
                       pair.getFirst() <= pair.getSecond());
        for (int i = 1; i < pairs.size(); i++)
            assertTrue("consecutive pairs " + pairs.get(i).toString(),
                       pairs.get(i-1).getFirst() <= pairs.get(i).getFirst());
    }

    public void test_stringListNonInclusive() {
        List<String> list = Arrays.asList(new String[] {"aa", "bb", "abc", "f"});
        List<Pair<String,String>> pairs = new ArrayList<Pair<String,String>>();
        PairIterator<String> pairIt = new PairIterator<String>(list);
        assertEquals("size", 6, pairIt.size());
        for (; pairIt.hasNext(); )
            pairs.add(pairIt.next());
        assertEquals("number of pairs", 6, pairs.size());
        for (Pair<String,String> pair : pairs)
            assertTrue("pair condition " + pair.toString(),
                       pair.getFirst().compareTo(pair.getSecond()) < 0);
        for (int i = 1; i < pairs.size(); i++)
            assertTrue("consecutive pairs " + pairs.get(i).toString(),
                       pairs.get(i-1).getFirst().compareTo(pairs.get(i).getFirst()) <= 0);
    }
    
    public void test_stringListInclusive() {
        List<String> list = Arrays.asList(new String[] {"aa", "bb", "abc", "f"});
        List<Pair<String,String>> pairs = new ArrayList<Pair<String,String>>();
        PairIterator<String> pairIt = new PairIterator<String>(list, true);
        assertEquals("size", 10, pairIt.size());
        for (; pairIt.hasNext(); )
            pairs.add(pairIt.next());
        assertEquals("number of pairs", 10, pairs.size());
        for (Pair<String,String> pair : pairs)
            assertTrue("pair condition " + pair.toString(),
                       pair.getFirst().compareTo(pair.getSecond()) <= 0);
        for (int i = 1; i < pairs.size(); i++)
            assertTrue("consecutive pairs " + pairs.get(i).toString(),
                       pairs.get(i-1).getFirst().compareTo(pairs.get(i).getFirst()) <= 0);
    }

    public void test_stringListNonInclusiveComparator() {
        List<String> list = Arrays.asList(new String[] {"aa", "bb", "abc", "f"});
        List<Pair<String,String>> pairs = new ArrayList<Pair<String,String>>();
        PairIterator<String> pairIt = new PairIterator<String>(list, new StringLengthComparator());
        assertEquals("size", 6, pairIt.size());
        for (; pairIt.hasNext(); )
            pairs.add(pairIt.next());
        assertEquals("number of pairs", 6, pairs.size());
        for (Pair<String,String> pair : pairs)
            assertTrue("pair condition " + pair.toString(),
                       pair.getFirst().length() < pair.getSecond().length() ||
                       pair.getFirst().compareTo(pair.getSecond()) < 0);
        for (int i = 1; i < pairs.size(); i++)
            assertTrue("consecutive pairs " + pairs.get(i).toString(),
                       pairs.get(i-1).getFirst().length() < pairs.get(i).getFirst().length() ||
                       pairs.get(i-1).getFirst().compareTo(pairs.get(i).getFirst()) <= 0);
    }
    
    public void test_stringListInclusiveComparator() {
        List<String> list = Arrays.asList(new String[] {"aa", "bb", "abc", "f"});
        List<Pair<String,String>> pairs = new ArrayList<Pair<String,String>>();
        PairIterator<String> pairIt = new PairIterator<String>(list, new StringLengthComparator(), true);
        assertEquals("size", 10, pairIt.size());
        for (; pairIt.hasNext(); )
            pairs.add(pairIt.next());
        assertEquals("number of pairs", 10, pairs.size());
        for (Pair<String,String> pair : pairs)
            assertTrue("pair condition " + pair.toString(),
                       pair.getFirst().length() < pair.getSecond().length() ||
                       pair.getFirst().compareTo(pair.getSecond()) <= 0);
        for (int i = 1; i < pairs.size(); i++)
            assertTrue("consecutive pairs " + pairs.get(i).toString(),
                       pairs.get(i-1).getFirst().length() < pairs.get(i).getFirst().length() ||
                       pairs.get(i-1).getFirst().compareTo(pairs.get(i).getFirst()) <= 0);
    }
    
    protected class StringLengthComparator implements Comparator<String> {

        public int compare(String s1, String s2) {
            if (s1.length() < s2.length())
                return -1;
            else if (s1.length() > s2.length())
                return 1;
            else
                return s1.compareTo(s2);
        }
        
    }

}
