/*
 * Created on Feb 9, 2006
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package eu.fox7.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p> AbstractEquivalenceRelation is a base class for equivalence classes.
 * Derived classes should implement the <code>areEquivalent()</code> method,
 * providing context via a constructor (or any other means) if necessary.
 * Context has been left to the implementation to guarantee as much flexibility
 * and genericity as possible.  The requirements for the data structure to
 * compute the equivalence classes on are also minimal: one should simply be
 * able to iterate over the desired type. </p>
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public abstract class AbstractEquivalenceRelation<ElementType>
        implements EquivalenceRelation<ElementType> {

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
    public abstract boolean areEquivalent(ElementType element1,
                                          ElementType element2);

    /**
     * method that computes the set of equivalence classes from a given data
     * structure
     * @param set
     *     Object that is iterable over the relevant type
     * @return set of equivalence classes for the relation, i.e.,  Set of Sets
     * of the relevant type
     */
    public Set<Set<ElementType>> getClasses(Iterable<ElementType> set) {
        Map<ElementType,Set<ElementType>> classMap = new HashMap<ElementType,Set<ElementType>>();
        set: for (ElementType element : set) {
            for (ElementType representant : classMap.keySet()) {
                if (areEquivalent(element, representant)) {
                    classMap.get(representant).add(element);
                    continue set;
                }
            }
            Set<ElementType> newClass = new HashSet<ElementType>();
            newClass.add(element);
            classMap.put(element, newClass);
        }
        return new HashSet<Set<ElementType>>(classMap.values());
    }

}
