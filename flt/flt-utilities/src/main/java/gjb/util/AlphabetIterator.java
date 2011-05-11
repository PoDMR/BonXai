/*
 * Created on Oct 6, 2007
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package gjb.util;

import java.util.Iterator;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class AlphabetIterator implements Iterator<String> {

    protected String currentSymbol;

    public AlphabetIterator() {
        this(false);
    }

    public AlphabetIterator(boolean uppercase) {
        super();
        if (uppercase)
            currentSymbol = "A";
        else
            currentSymbol = "a";
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        return currentSymbol != null;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public String next() {
        String symbol = currentSymbol;
        char c = (char) (currentSymbol.charAt(0) + 1);
        currentSymbol = Character.isLetter(c) ? Character.toString(c) : null;
        return symbol;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    public void remove() {}

}
