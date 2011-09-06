/*
 * Created on Feb 20, 2007
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package eu.fox7.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;

/**
 * Class that iterates over pairs of objects.  The objects must be comparable
 * using a specified Comparator.  It is optional whether a pair can have the same
 * first and second member, but in any case pair.getFirst() <= pair.getSecond() for
 * all pairs.
 * 
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * @param <T>
 * 
 */
public class PairIterator<T> implements Iterator<Pair<T,T>> {

    protected NavigableSet<T> set;
    protected Iterator<T> primaryIt, secondaryIt;
    protected Pair<T,T> nextPair = null;
    protected boolean inclusive = false;

    /**
     * Constructor for a pair iterator based on a collection of objects that can
     * be compared using a given comparator.
     * @param coll Collection containing the objects to compute the pairs for
     * @param cmp Comparator to compare the objects
     * @param inclusive boolean specifying whether comparison is strict
     */
    public PairIterator(Collection<T> coll, Comparator<T> cmp, boolean inclusive) {
        this.inclusive = inclusive;
        this.set = cmp == null ? new TreeSet<T>() : new TreeSet<T>(cmp);
        this.set.addAll(coll);
        this.primaryIt = set.iterator();
        if (primaryIt.hasNext()) {
            T m1 = primaryIt.next();
            this.secondaryIt = set.tailSet(m1, isInclusive()).iterator();
            if (secondaryIt.hasNext()) {
                T m2 = secondaryIt.next();
                nextPair = new Pair<T,T>(m1, m2);
            }
        }
    }

    /**
     * Constructor for a pair iterator based on a collection of objects that can
     * be compared using the natural comparator.
     * @param coll Collection containing the objects to compute the pairs for
     * @param inclusive boolean specifying whether comparison is strict
     */
    public PairIterator(Collection<T> coll, boolean inclusive) {
        this(coll, null, inclusive);
    }
    
    /**
     * Constructor for a pair iterator based on a collection of objects that can
     * be compared using a given comparator.  Comparison is strict.
     * @param coll Collection containing the objects to compute the pairs for
     * @param cmp Comparator to compare the objects
     */
    public PairIterator(Collection<T> coll, Comparator<T> cmp) {
        this(coll, cmp, false);
    }

    /**
     * Constructor for a pair iterator based on a collection of objects that can
     * be compared using the natural comparator.  Comparison is strict.
     * @param coll Collection containing the objects to compute the pairs for
     */
    public PairIterator(Collection<T> coll) {
        this(coll, null, false);
    }
    
    /**
     * method that checks whether more pairs are available
     * @return boolean true if a next Pair exists, false otherwise
     */
    public boolean hasNext() {
        return nextPair != null;
    }

    /**
     * method that returns the next Pair if there is one, null otherwise
     * @return Pair<T,T> next pair
     */
    public Pair<T,T> next() {
        Pair<T,T> pair = nextPair;
        nextPair = null;
        if (secondaryIt.hasNext()) {
            nextPair = new Pair<T,T>(pair.getFirst(), secondaryIt.next());
        } else if (primaryIt.hasNext()) {
            T m1 = primaryIt.next();
            this.secondaryIt = set.tailSet(m1, isInclusive()).iterator();
            if (secondaryIt.hasNext()) {
                T m2 = secondaryIt.next();
                nextPair = new Pair<T,T>(m1, m2);
            }
        }
        return pair;
    }

    /**
     * not implemented
     */
    public void remove() {}

    /**
     * method to verify whether the iteration nicludes Pairs with the first member
     * equal to the second, according to the comparator specified.
     * @return boolean
     */
    public boolean isInclusive() {
        return inclusive;
    }

    public int size() {
        if (isInclusive())
            return set.size()*(set.size() + 1)/2;
        else
            return set.size()*(set.size() - 1)/2;
    }

}
