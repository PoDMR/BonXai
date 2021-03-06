/*
 * Created on Jun 22, 2005
 * Modified on $Date: 2009-10-29 13:35:07 $
 */
package eu.fox7.flt.treegrammar.data;

/**
 *<p> Interface generators should implement. </p>
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public interface DataGenerator {

    /**
     * Method that returns an example of the data model implemented by the
     * DataGenerator
     * @return String example
     */
    public String getData();

}
