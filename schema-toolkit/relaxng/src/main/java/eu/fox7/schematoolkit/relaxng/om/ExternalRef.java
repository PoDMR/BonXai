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
 * Class representing the Choice element of RelaxNG.
 * @author Lars Schmidt
 */
public class ExternalRef extends Pattern {

    private String href;                        // anyURI

    /**
     * Object holding the parsed external RELAX NG XSDSchema, if not null.
     */
    private RelaxNGSchema rngSchema;

    /**
     * Constructor of class Choice
     * @param href
     */
    public ExternalRef(String href) {
        this.href = href;
    }

    /**
     * Getter of the XML attribute href.
     * @return String   A string containing the value of the datatypeLibrary
     */
    public String getHref() {
        return this.href;
    }

    /**
     * Setter of the href attribute of this include element.
     * @param href 
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * Getter for the parsed Relax NG XSDSchema, which builds the target of this externalRef.
     * @return RelaxNGSchema        the parsed Relax NG XSDSchema
     */
    public RelaxNGSchema getRngSchema() {
        return rngSchema;
    }

    /**
     * Setter for the parsed Relax NG XSDSchema, which builds the target of this externalRef.
     * @param rngSchema     the parsed Relax NG XSDSchema
     */
    public void setRngSchema(RelaxNGSchema rngSchema) {
        this.rngSchema = rngSchema;
    }
}
