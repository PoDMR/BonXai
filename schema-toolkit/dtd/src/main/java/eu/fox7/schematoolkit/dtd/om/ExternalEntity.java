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

package eu.fox7.schematoolkit.dtd.om;

/**
 * Class for an external entity defined in a DTD
 *
 * Important:
 * This is not necessary and therefor not supported in the current
 * implementation. All external entities are fetched by the SAXParser itself
 * and their content will be placed within the generated dtd structure.
 *
 * @author Lars Schmidt
 */
public class ExternalEntity extends Entity {

    private String publicID, systemID;

    /**
     * Constructor of class ExternalEntity
     * @param name - String
     * @param publicID - String
     * @param systemID - String
     */
    public ExternalEntity(String name, String publicID, String systemID) {
        super(name);
        this.publicID = publicID;

        // Check "%" --> parsed entity

        // SystemID is currently the absolute path to the file.
        // This might be a problem.
        this.systemID = systemID;
    }

    /**
     * Getter for the publicID of the Notation
     * @return
     */
    public String getPublicID() {
        return this.publicID;
    }

    /**
     * Getter for the systemID of the Notation
     * @return
     */
    public String getSystemID() {
        return this.systemID;
    }


}
