/*
 * Created on Feb 21, 2007
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package gjb.util;

import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class SymbolIteratorTest extends TestCase {

    public static Test suite() {
        return new TestSuite(SymbolIteratorTest.class);
    }

    public void test_defaultIteration() {
        SymbolIterator it = new SymbolIterator();
        for (int i = 0; i < 10; i++) {
            String symbol = it.next();
            assertEquals("symbol " + i,
                         String.format(it.getFormat(),
                                       it.getBaseSymbol(), it.getSeparator(), i),
                         symbol);
            assertEquals("symbol " + i + " length", 4, symbol.length());
        }
    }

    public void test_alternativeFormatIteration() {
        Properties properties = new Properties();
        properties.setProperty(SymbolIterator.FORMAT, "%s%s%08d");
        properties.setProperty(SymbolIterator.NR_DIGITS, "8");
        SymbolIterator it = new SymbolIterator(properties);
        String format = "%s%s%08d";
        for (int i = 0; i < 10; i++) {
            String symbol = it.next();
            assertEquals("symbol " + i,
                         String.format(format,
                                       it.getBaseSymbol(), it.getSeparator(), i),
                                       symbol);
            assertEquals("symbol " + i + " length", 9, symbol.length());
        }
    }
    
    public void test_alternativeBaseIteration() {
        final String base = "alpha";
        Properties properties = new Properties();
        properties.setProperty(SymbolIterator.BASE_SYMBOL, base);
        SymbolIterator it = new SymbolIterator(properties);
        for (int i = 0; i < 10; i++)
            assertEquals("symbol " + i,
                         String.format(it.getFormat(),
                                       base, it.getSeparator(), i),
                                       it.next());
    }

        public void test_separatorIteration() {
        final String sep = "-";
        Properties properties = new Properties();
        properties.setProperty(SymbolIterator.SEPARATOR, sep);
        SymbolIterator it = new SymbolIterator(properties);
        for (int i = 0; i < 10; i++)
            assertEquals("symbol " + i,
                         String.format(it.getFormat(),
                                       it.getBaseSymbol(), sep, i),
                                       it.next());
    }

    public void test_alternativeInitIteration() {
        final int init = 100;
        Properties properties = new Properties();
        properties.setProperty(SymbolIterator.INIT_NUMBER, "100");
        SymbolIterator it = new SymbolIterator(properties);
        for (int i = 0; i < 10; i++)
            assertEquals("symbol " + i,
                         String.format(it.getFormat(),
                                       it.getBaseSymbol(), it.getSeparator(), init + i),
                                       it.next());
    }

    public void test_noNextIteration() {
        Properties properties = new Properties();
        properties.setProperty(SymbolIterator.NR_DIGITS, "1");
        int counter = 0;
        for (SymbolIterator it = new SymbolIterator(properties); it.hasNext(); ) {
            it.next();
            counter++;
        }
        assertEquals("maximum", 10, counter);
    }

}
