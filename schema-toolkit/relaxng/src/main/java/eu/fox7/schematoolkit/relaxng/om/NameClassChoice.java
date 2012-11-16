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
 * Class representing the choice element for nameClass contents of RelaxNG
 * @author Lars Schmidt
 */
public class NameClassChoice extends NameClass {

    /**
     * In the Simple XML Syntax of RELAX NG there are only two elements allowed
     * in each choice.
     * The Full XML Syntax makes no restrictions on the quantity of names
     * defined in a choice.
     */
    private LinkedHashSet<NameClass> choiceNames;

    /**
     * Constructor of class NameClassChoice
     */
    public NameClassChoice() {
        super();
        this.choiceNames = new LinkedHashSet<NameClass>();
    }

    /**
     * Getter for the contained choiceNames in this choice
     * @return LinkedList<Pattern>
     */
    public LinkedHashSet<NameClass> getChoiceNames() {
        return choiceNames;
    }

    /**
     * Method for adding an choiceName to this choice
     * @param choiceName
     */
    public void addChoiceName(NameClass choiceName) {
        this.choiceNames.add(choiceName);
    }
}
