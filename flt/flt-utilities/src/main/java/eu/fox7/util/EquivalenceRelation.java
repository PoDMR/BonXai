/*
 * Created on Feb 9, 2006
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package eu.fox7.util;

import java.util.Set;

public interface EquivalenceRelation<ElementType> {

    /**
     * method to be implemented by derived classes; the relation should be
     * reflexive, symmetric and transitive, i.e.
     * <pre>
     *    areEquivalent(e, e) == true
     *    areEquivalent(e1, e2) == areEquivalent(e2, e1)
     *    !(areEquivalent(e1, e2) && areEquivalent(e2, e3)) ||
     *          areEquivalent(e1, e3)
     * </pre>
     * @param element1
     * @param element2
     * @return true if element1 is equivalent to element2, false otherwise
     */
    public boolean areEquivalent(ElementType element1, ElementType element2);

    /**
     * method that computes the set of equivalence classes from a given data
     * structure
     * @param set
     *     Object that is iterable over the relevant type
     * @return set of equivalence classes for the relation, i.e.,  Set of Sets
     * of the relevant type
     */
    public Set<Set<ElementType>> getClasses(Iterable<ElementType> set);

}