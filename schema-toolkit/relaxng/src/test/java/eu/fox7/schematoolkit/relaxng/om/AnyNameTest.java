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

import eu.fox7.schematoolkit.relaxng.om.AnyName;
import eu.fox7.schematoolkit.relaxng.om.Name;
import eu.fox7.schematoolkit.relaxng.om.NameClass;
import static org.junit.Assert.*;

/**
 * Test of class AnyName
 * @author Lars Schmidt
 */
public class AnyNameTest extends junit.framework.TestCase {

    /**
     * Test of getExceptNames method, of class AnyName.
     */
    @Test
    public void testGetExceptNames() {
        AnyName instance = new AnyName();

        instance.addExceptName(new Name("myName"));
        instance.addExceptName(new Name("mySecondName"));

        LinkedHashSet<NameClass> result = instance.getExceptNames();
        assertEquals(2, result.size());
        assertTrue(((Name)result.iterator().next()).getContent().equals("myName"));
    }

    /**
     * Test of addExceptName method, of class AnyName.
     */
    @Test
    public void testAddExceptName() {
        AnyName instance = new AnyName();

        instance.addExceptName(new Name("myName"));
        instance.addExceptName(new Name("mySecondName"));

        LinkedHashSet<NameClass> result = instance.getExceptNames();
        assertEquals(2, result.size());
        assertTrue(((Name)result.iterator().next()).getContent().equals("myName"));
    }

}