/*
 * Created on Jul 9, 2007
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package eu.fox7.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class PairwiseIterator<T1,T2> implements Iterator<Pair<T1,T2>> {

    protected Iterator<T1> iterator1;
    protected Iterator<T2> iterator2;

    public PairwiseIterator(Collection<T1> coll1, Collection<T2> coll2) {
        this.iterator1 = coll1.iterator();
        this.iterator2 = coll2.iterator();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        return iterator1.hasNext() || iterator2.hasNext();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public Pair<T1,T2> next() {
        T1 first = iterator1.hasNext() ? iterator1.next() : null;
        T2 second = iterator2.hasNext() ? iterator2.next() : null;
        if (first != null || second != null)
            return new Pair<T1,T2>(first, second);
        else
            throw new NoSuchElementException();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    public void remove() {}

}
