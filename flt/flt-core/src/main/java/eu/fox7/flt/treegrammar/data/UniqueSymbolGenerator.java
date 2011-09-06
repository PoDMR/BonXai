/*
 * Created on Feb 21, 2007
 * Modified on $Date: 2009-11-04 14:50:30 $
 */
package eu.fox7.flt.treegrammar.data;

import java.util.Properties;

import eu.fox7.util.SymbolIterator;


/**
 * @author eu.fox7
 * @version $Revision: 1.2 $
 * 
 */
public class UniqueSymbolGenerator implements DataGenerator {

    protected SymbolIterator symbolIt;

    public UniqueSymbolGenerator() {
        super();
        symbolIt = new SymbolIterator();
    }

    public UniqueSymbolGenerator(Properties properties) {
        super();
        symbolIt = new SymbolIterator(properties);
    }

    /* (non-Javadoc)
     * @see eu.fox7.xml.model.data.DataGenerator#getData()
     */
    public String getData() {
        return symbolIt.next();
    }

}
