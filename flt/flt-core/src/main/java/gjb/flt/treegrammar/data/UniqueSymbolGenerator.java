/*
 * Created on Feb 21, 2007
 * Modified on $Date: 2009-11-04 14:50:30 $
 */
package gjb.flt.treegrammar.data;

import java.util.Properties;

import gjb.util.SymbolIterator;

/**
 * @author gjb
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
     * @see gjb.xml.model.data.DataGenerator#getData()
     */
    public String getData() {
        return symbolIt.next();
    }

}
