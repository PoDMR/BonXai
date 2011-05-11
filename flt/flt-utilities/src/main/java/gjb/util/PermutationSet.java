/*
 * Created on Aug 16, 2004
 *
 */
package gjb.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * <p>A PermutationSet exposes a lazy iterator over all permutations of a given list of objects.
 * Example:
 * <pre>
 *   List list = new LinkedList();
 *   list.add("a");
 *   list.add("b");
 *   list.add("c");
 *   PermutationSet pSet = new PermutationSet(list);
 *   for (Iterator it = pSet.iterator(); it.hasNext(); ) {
 *     List seq = (List) it.next();
 *     ...
 *   }
 * </pre>
 * The sequences generated are the six permutations of the list <code>["a", "b", "c"]</code>:
 * <pre>
 *   [a, b, c]
 *   [a, c, b]
 *   [c, a, b]
 *   [b, a, c]
 *   [b, c, a]
 *   [c, b, a]
 * </pre></p>
 * 
 * @author gjb
 * @version 1.0
 */
public class PermutationSet<T> {

    protected ArrayList<T> objects;
    
    public static void main(String[] args) {
        List<String> list = new LinkedList<String>();
        list.add("a");
        list.add("b");
        list.add("c");
        PermutationSet<String> pSet = new PermutationSet<String>(list);
        for (java.util.Iterator<?> it = pSet.iterator(); it.hasNext(); ) {
            System.out.println(it.next());
        }
    }
    
    /**
     * Constructor for a <code>PermutationList</code> that exposes a lazy
     * iterator over all permutations of the given <code>List</code>
     * @param objectList list of objects from which to compute all permutations
     */
    public PermutationSet(List<T> objectList) {
        objects = new ArrayList<T>(objectList);
    }

    public PermutationSet(T[] objectArray) {
        objects = new ArrayList<T>();
        for (int i = 0; i < objectArray.length; i++) {
            objects.add(objectArray[i]);
        }
    }
    
    public long size() {
        long size = 1;
        for (int i = 2; i <= objects.size(); i++) size *= i;
        return size;
    }
    
    /**
     * @return lazy iterator over all permutations of the given <code>List</code>
     */
    public Iterator iterator() {
        return new Iterator();
    }
    
    protected class Iterator implements java.util.Iterator<List<T>> {
        
        protected int[] positions;
        protected long max;
        protected long iteration;
        
        public Iterator() {
            positions = new int[objects.size()];
            reset();
            max = size();
        }

        public void reset() {
            for (int i = 0; i < positions.length; i++) {
                positions[i] = i;
            }
            iteration = 0;
        }
        
        public List<T> next() {
		    if (!hasNext()) {
		        throw new NoSuchElementException();
		    }
            List<T> list = new ArrayList<T>();
            for (int i = 0; i < objects.size(); i++) {
                list.add(positions[i], objects.get(i));
            }
            for (int i = positions.length - 1; i > 0; i--) {
                if (positions[i] > 0) {
                    positions[i]--;
                    break;
                } else {
                    positions[i] = i;
                }
            }
            iteration++;
            return list;
        }

        public boolean hasNext() {
            return iteration < max;
        }
     
        public void remove() {}
        
    }
    
}
