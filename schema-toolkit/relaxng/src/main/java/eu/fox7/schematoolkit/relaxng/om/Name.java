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

import eu.fox7.schematoolkit.common.AnonymousNamespace;

/**
 * Class representing the Name element of RelaxNG
 * @author Lars Schmidt
 */
public class Name extends NameClass {

    private String content;             // NCName

    /**
     * Constructor of class Name
     * The content of a name is of type "QName".
     * @param content
     */
    public Name(String content) {
        super();
        this.content = content;
    }

    /**
     * Constructor of class Name with content and namespace
     * @param content
     * @param attributeNamespace 
     */
    public Name(String content, String attributeNamespace) {
        super();
        this.content = content;
        this.attributeNamespace = new AnonymousNamespace(attributeNamespace);
    }

    /**
     * Getter for the content of this name
     * @return String   A string containing the string content
     */
    public String getContent() {
        return content;
    }
    /**
     * Setter for the content of this name
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }
}
