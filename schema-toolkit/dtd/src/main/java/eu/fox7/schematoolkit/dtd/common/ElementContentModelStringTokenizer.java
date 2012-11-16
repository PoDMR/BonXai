/**
 * Copyright 2009-2012 TU Dortmund
 *
 * This file is part of FoXLib.
 *
 * FoXLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoXLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */

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
