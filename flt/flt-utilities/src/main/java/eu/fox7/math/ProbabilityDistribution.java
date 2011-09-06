/*
 * Created on Jun 21, 2005
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package eu.fox7.math;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public interface ProbabilityDistribution {

    public int getNext();
    public int getNextConstrained();

}
