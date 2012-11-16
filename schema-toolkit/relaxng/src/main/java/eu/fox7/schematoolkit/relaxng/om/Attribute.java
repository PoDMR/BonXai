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
 * Class representing the <attribute/> element of RelaxNG
 * @author Lars Schmidt
 */
public class Attribute extends Pattern {

    private String name; // QName, this is an alternative to the nameClass content element
    private NameClass nameClass;
    // In the Full XML Syntax of Relax NG the pattern is optional in an attribute
    private Pattern pattern;

    /**
     * Getter for the pattern of this attribute.
     * In the Full XML Syntax of RELAX NG this is an optional content of an
     * attribute.
     * @return Pattern
     */
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * Setter for the pattern of this attribute.
     * In the Full XML Syntax of RELAX NG this is an optional content of an
     * attribute.
     * @param pattern
     */
    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     * Getter of the nameclass attribute of this attribute
     * @return NameClass
     */
    public NameClass getNameClass() {
        return this.nameClass;
    }

    /**
     * Setter for the nameclass attribute of this attribute
     * @param nameClass
     */
    public void setNameClass(NameClass nameClass) {
        this.nameClass = nameClass;
    }

    /**
     * Getter for the name attribute of this attribute
     * @return NameClass
     */
    public String getNameAttribute() {
        return this.name;
    }

    /**
     * Setter for the name attribute of this attribute
     * @param name
     */
    public void setNameAttribute(String name) {
        this.name = name;
    }

}
