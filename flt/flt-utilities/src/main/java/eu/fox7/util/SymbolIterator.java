/*
 * Created on Feb 21, 2007
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package eu.fox7.util;

import java.util.Iterator;
import java.util.Properties;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class SymbolIterator implements Iterator<String> {

    public static final String BASE_SYMBOL = "baseSymbol";
    public static final String DEFAULT_BASE_SYMBOL = "S";
    public static final String SEPARATOR = "separator";
    public static final String DEFAULT_SEPARATOR = "";
    public static final String NR_DIGITS = "nrDigits";
    public static final String DEFAULT_NR_DIGITS = "3";
    public static final String INIT_NUMBER = "initNumber";
    public static final String DEFAULT_INIT_NUMBER = "0";
    public static final String FORMAT = "format";
    public static final String DEFAULT_FORMAT = "%s%s%0" + DEFAULT_NR_DIGITS + "d";
    protected String baseSymbol = DEFAULT_BASE_SYMBOL;
    protected String separator = DEFAULT_SEPARATOR;
    protected String format = DEFAULT_FORMAT;
    protected Properties properties;
    protected int currentNumber;

    public SymbolIterator() {
        properties = new Properties();
        properties.setProperty(BASE_SYMBOL, DEFAULT_BASE_SYMBOL);
        properties.setProperty(SEPARATOR, DEFAULT_SEPARATOR);
        properties.setProperty(NR_DIGITS, DEFAULT_NR_DIGITS);
        properties.setProperty(INIT_NUMBER, DEFAULT_INIT_NUMBER);
        properties.setProperty(FORMAT, DEFAULT_FORMAT);
        currentNumber = getInitNumber();
    }

    public SymbolIterator(Properties newProperties) {
        this();
        if (newProperties != null) {
            for (Iterator<?> it = newProperties.keySet().iterator(); it.hasNext(); ) {
                String key = (String) it.next();
                properties.setProperty(key, newProperties.getProperty(key));
            }
        }
        currentNumber = getInitNumber();
    }

    public String getBaseSymbol() {
        return properties.getProperty(BASE_SYMBOL);
    }

    public String getSeparator() {
        return properties.getProperty(SEPARATOR);
    }

    public int getNrDigits() {
        return Integer.parseInt(properties.getProperty(NR_DIGITS));
    }

    public String getFormat() {
        return properties.getProperty(FORMAT);
    }

    public int getInitNumber() {
        return Integer.parseInt(properties.getProperty(INIT_NUMBER));
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        return currentNumber < Math.pow(10, getNrDigits()) &&
                   currentNumber < Integer.MAX_VALUE;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public String next() {
        return String.format(getFormat(),
                             getBaseSymbol(), getSeparator(), currentNumber++);
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    public void remove() {}

}
