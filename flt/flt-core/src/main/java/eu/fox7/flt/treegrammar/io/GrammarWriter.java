/*
 * Created on Jan 24, 2007
 * Modified on $Date: 2009-11-03 12:37:43 $
 */
package eu.fox7.flt.treegrammar.io;

import eu.fox7.flt.treegrammar.XMLGrammar;

import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

/**
 * @author eu.fox7
 * @version $Revision: 1.2 $
 * 
 */
public interface GrammarWriter {

    public void write(XMLGrammar doc, Writer writer) throws IOException;
    public void write(XMLGrammar doc, Properties properties, Writer writer)
            throws IOException;

}
