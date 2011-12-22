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
package eu.fox7.bonxai.bonxai;

/**
 * Class representing a data-types
 */
public class DataType {

    /**
     * Identifier of the data-type
     */
    private String identifier;
    /**
     * Uri of the data-type
     */
    private String uri;

    /**
     * Constructor for the class DataTypes
     * @param identifier
     * @param uri
     */
    public DataType(String identifier, String uri) {
        this.identifier = identifier;
        this.uri = uri;
    }
    /**
     * Returns uri of data-type
     * @return uri
     */
    public String getUri() {
        return uri;
    }
    /**
     * Returns identifier of data-type
     * @return identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Returns a string representation of the datatype for string comparison.
     */
    @Override
    public String toString() {
        return identifier + "=" + uri;
    }
}

