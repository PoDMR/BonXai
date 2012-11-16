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
import org.junit.Test;

import eu.fox7.schematoolkit.relaxng.om.Name;
import eu.fox7.schematoolkit.relaxng.om.NameClass;
import eu.fox7.schematoolkit.relaxng.om.NameClassChoice;
import static org.junit.Assert.*;

/**
 * Test of class NameClassChoice
 * @author Lars Schmidt
 */
public class NameClassChoiceTest extends junit.framework.TestCase {

    /**
     * Test of getChoiceNames method, of class NameClassChoice.
     */
    @Test
    public void testGetChoiceNames() {
        NameClassChoice instance = new NameClassChoice();
        Name name = new Name("myName");
        instance.addChoiceName(name);
        LinkedHashSet<NameClass> result = instance.getChoiceNames();
        assertTrue(result.contains(name));
    }

    /**
     * Test of addChoiceName method, of class NameClassChoice.
     */
    @Test
    public void testAddChoiceName() {
        NameClassChoice instance = new NameClassChoice();
        Name name = new Name("myName");
        instance.addChoiceName(name);
        LinkedHashSet<NameClass> result = instance.getChoiceNames();
        assertTrue(result.contains(name));
    }

}