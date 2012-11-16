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

package eu.fox7.schematoolkit.relaxng.om;

/**
 * Class representing the Value element of RelaxNG
 * @author Lars Schmidt
 */
public class Value extends Pattern {

    private String content;                  // string
    private String type;                     // NCName

    /**
     * Constructor of class Data
     * @param content
     */
    public Value(String content) {
        this.content = content;
    }

    /**
     * Getter for the name of this Value
     * @return String   name of this Value
     */
    public String getType() {
        return type;
    }

    /**
     * Setter for the name of this Value
     * @param type 
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Getter for the content of this Value
     * @return String   content of this Value
     */
    public String getContent() {
        return content;
    }

    /**
     * Setter for the content of this Value
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }
}
