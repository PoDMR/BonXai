/*
 * Created on Feb 22, 2007
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package eu.fox7.util;

import eu.fox7.math.IllDefinedDistributionException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.math.RandomUtils;

import static java.lang.Math.abs;

/**
 * Helper class that selects a random subcollection from a collection of objects
 * 
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class RandomSelector<T> {

    protected Map<T,Double> distribution;
    public final double epsilon = 1e-5;

    public RandomSelector() {
        super();
    }

    public RandomSelector(Map<T,Double> distribution)
            throws IllDefinedDistributionException {
        this();
        checkDistribution(distribution);
        this.distribution = distribution;
    }

    protected void checkDistribution(Map<T, Double> distribution)
            throws IllDefinedDistributionException {
        double sum = 0.0;
        for (T element : distribution.keySet())
            sum += distribution.get(element);
        if (abs(sum - 1.0) > epsilon)
            throw new IllDefinedDistributionException("probabilities sum up to " + sum);
    }

    public T selectOne() {
        double p = RandomUtils.nextDouble();
        double sum = 0.0;
        for (T element : distribution.keySet()) {
            sum += distribution.get(element);
            if (p < sum)
                return element;
        }
        return null;
    }

    public Set<T> selectSubsetFrom(List<T> list, int size) {
        Set<T> set = new HashSet<T>();
        selectSubcollectionFrom(list, size, set);
        return set;
    }

    public List<T> selectSublistFrom(List<T> list, int size) {
        List<T> sublist = new ArrayList<T>();
        selectSubcollectionFrom(list, size, sublist);
        return sublist;
    }
    
    protected void selectSubcollectionFrom(List<T> list, int size,
                                           Collection<T> subColl) {
        if (size < list.size()/2)
            while (subColl.size() < size) {
                T candidate = list.get(RandomUtils.nextInt(list.size()));
                if (!subColl.contains(candidate))
                    subColl.add(candidate);
            }
        else {
            subColl.addAll(list);
            while (subColl.size() > size) {
                T candidate = list.get(RandomUtils.nextInt(list.size()));
                if (subColl.contains(candidate))
                    subColl.remove(candidate);
            }
        }
    }

    protected void selectSubcollectionFrom(Set<T> set, int size,
                                           Collection<T> subColl) {
        selectSubcollectionFrom(new ArrayList<T>(set), size, subColl);
    }

    /**
     * selects a random subset of the specified size out of the given set; note
     * that the selected elements are <em>not</em> removed from the set
     * @param set to select from
     * @param size of the subset to select
     * @return Set selected subset
     */
    public Set<T> selectSubsetFrom(Set<T> set, int size) {
        return selectSubsetFrom(new ArrayList<T>(set), size);
    }

    public List<T> selectSublistFrom(Set<T> set, int size) {
        List<T> sublist = new ArrayList<T>();
        selectSubcollectionFrom(set, size, sublist);
        return sublist;
    }
    
   /**
     * selects a random element out of the given set; note that the selected 
     * element is <em>not</em> removed from the set
     * @param set to select from
     * @return selected element
     */
    public T selectOneFrom(Set<T> set) {
        return selectSubsetFrom(set, 1).iterator().next();
    }

}
