package eu.fox7.schematoolkit.dtd.common;

import java.util.StringTokenizer;

/**
 * ElementContentModelStringTokenizer 
 * @author Lars Schmidt
 */
public class ElementContentModelStringTokenizer extends StringTokenizer {

    private String pushbackValue = null;

    public ElementContentModelStringTokenizer(String token, String delimiter, boolean incrementValue) {
        super(token, delimiter, incrementValue);
    }

    /**
     * Returns the next token from this string tokenizer.
     *
     * @return     the next token from this string tokenizer.
     */
    @Override
    public String nextToken() {
        String next;
        if (pushbackValue != null) {
            next = pushbackValue;
            pushbackValue = null;
        } else {
            next = super.nextToken();
        }
        return next;
    }

    /**
     * Tests if there are more tokens available from this tokenizer's string.
     * If this method returns <tt>true</tt>, then a subsequent call to
     * <tt>nextToken</tt> with no argument will successfully return a token.
     *
     * @return  <code>true</code> if and only if there is at least one token
     *          in the string after the current position; <code>false</code>
     *          otherwise.
     */
    @Override
    public boolean hasMoreTokens() {
        if (pushbackValue != null) {
            return true;
        } else {
            return super.hasMoreTokens();
        }
    }

    public void setPushback(String pushback) {
        if (this.pushbackValue != null) {
            throw new IllegalStateException();
        }
        this.pushbackValue = pushback;
    }
}
