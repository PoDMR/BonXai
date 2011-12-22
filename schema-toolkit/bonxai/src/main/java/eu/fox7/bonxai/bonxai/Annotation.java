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
 * Class for representing Annotations in Bonxai
 */
public class Annotation {

    /**
     * The attribute key of Annotation is used to reference this annotation
     */
    private String key;
    /**
     * The attribute value of Annotation contains the information of this annotation
     */
    private String value;

    /**
     * Constructor for the class Annotation
     * @param key
     * @param value
     */
    public Annotation(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Returns the attribute key of this Annotation
     * @return key
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the attribute value of this Annotation
     * @return value
     */
    public String getValue() {
        return value;
    }
}

