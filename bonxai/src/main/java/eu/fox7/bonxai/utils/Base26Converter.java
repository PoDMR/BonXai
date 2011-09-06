/**
 * This file is part of BonXai.
 *
 * BonXai is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BonXai is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.fox7.bonxai.utils;
/**
 * Convert an Integer to a base26 representation using only the character a-z
 * and vice versa.
 */
class Base26Converter {
    /**
     * Convert a base26 string representation to its integer value
     *
     * Base26 representation needs to be reflected by the lowercase letters
     * a-z.
     */
    public Integer convertFromBase26( String number ) {
        int result = 0;
        if ( !number.matches( "[a-z]+" ) ) {
            // Number in wrong format.
            return 0;
        }

        for( int i=0; i<number.length(); ++i ) {
            result += Math.pow( 26, i ) * ( number.charAt( number.length() - i - 1 ) - 97 );
        }
        return new Integer( result );
    }

    /**
     * Convert an arbitrary integer to a base26 string representation
     *
     * Base26 representation will reflect the integer by the lowercase letters
     * a-z.
     */
    public String convertToBase26( Integer number ) {
        StringBuilder builder = new StringBuilder();
        int conversion = number.intValue();

        if ( conversion == 0 ) {
            return "a";
        }

        while( conversion != 0 ) {
            builder.append( (char)( 97 + ( conversion % 26 ) ) );
            conversion = conversion / 26;
        }

        return builder.reverse().toString();
    }
}
