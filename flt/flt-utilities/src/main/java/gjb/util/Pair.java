/*
 * Created on Feb 20, 2007
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package gjb.util;

/**
 * Class that encodes a pair of two objects, not necessarily of the same type.
 * The members of the Pair are assigned upon construction and can be accessed
 * via getters.
 * Used in the PairIterator class.
 * 
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class Pair<T1,T2> {

    protected T1 firstMember;
    protected T2 secondMember;

    @SuppressWarnings("unused")
    private Pair() {
        super();
    }

    /**
     * Constructor, the two members are specified
     * @param firstMember the pair's first member
     * @param secondMember the pair's second member
     */
    public Pair(T1 firstMember, T2 secondMember) {
        this.firstMember = firstMember;
        this.secondMember = secondMember;
    }

    /**
     * Getter method for the pair's first member
     * @return T1 pair's first Member
     */
    public T1 getFirst() {
        return firstMember;
    }

    /**
     * Getter method for the pair's second member
     * @return T2 pair's second Member
     */
    public T2 getSecond() {
        return secondMember;
    }

    @Override
    public int hashCode() {
        return getFirst().hashCode() + 37*getSecond().hashCode();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object pair) {
        if (!(pair instanceof Pair))
            return false;
        if (pair == null)
            return false;
        return this.getFirst().equals(((Pair) pair).getFirst()) &&
            this.getSecond().equals(((Pair) pair).getSecond());
    }

    @Override
    /**
     * Generic method to compute a string representation of a Pair, the members
     * are supposed to implement a toString() method of their own.
     * @return String representation of a Pair
     */
    public String toString() {
        return "(" + firstMember.toString() + ", " + secondMember.toString() + ")";
    }

}
