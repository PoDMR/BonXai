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

import java.util.LinkedHashSet;

/**
 * Class representing the anyName element of RelaxNG
 * @author Lars Schmidt
 */
public class AnyName extends NameClass {

    // The exceptions are optional in an anyName of Relax NG
    private LinkedHashSet<NameClass> exceptNames;

    /**
     * Constructor of class AnyName
     */
    public AnyName() {
        super();
        this.exceptNames = new LinkedHashSet<NameClass>();
    }

    /**
     * Getter for the contained exceptNames in this AnyName
     * @return LinkedList<Pattern>
     */
    public LinkedHashSet<NameClass> getExceptNames() {
        return exceptNames;
    }

    /**
     * Method for adding an exceptName to this AnyName
     * @param exceptName
     */
    public void addExceptName(NameClass exceptName) {
        this.exceptNames.add(exceptName);
    }

}
