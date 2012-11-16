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
import eu.fox7.schematoolkit.relaxng.om.NsName;
import static org.junit.Assert.*;

/**
 * Test of class NsName
 * @author Lars Schmidt
 */
public class NsNameTest extends junit.framework.TestCase {

    /**
     * Test of getExceptNames method, of class NsName.
     */
    @Test
    public void testGetExceptNames() {
        NsName instance = new NsName();
        Name name = new Name("myName");
        instance.addExceptName(name);
        LinkedHashSet<NameClass> result = instance.getExceptNames();
        assertTrue(result.contains(name));
    }

    /**
     * Test of addExceptName method, of class NsName.
     */
    @Test
    public void testAddExceptName() {
        NsName instance = new NsName();
        Name name = new Name("myName");
        instance.addExceptName(name);
        LinkedHashSet<NameClass> result = instance.getExceptNames();
        assertTrue(result.contains(name));
    }

    /**
     * Test of getAttributeNamespace method, of class NsName.
     */
    @Test
    public void testGetAttributeNamespace() {
        NsName instance = new NsName();
        instance.setAttributeNamespace("http://www.example.org/");
        String expResult = "http://www.example.org/";
        String result = instance.getAttributeNamespace().getUri();
        assertEquals(expResult, result);
    }

    /**
     * Test of setAttributeNamespace method, of class NsName.
     */
    @Test
    public void testSetAttributeNamespace() {
        NsName instance = new NsName();
        instance.setAttributeNamespace("http://www.example.org/");
        String expResult = "http://www.example.org/";
        String result = instance.getAttributeNamespace().getUri();
        assertEquals(expResult, result);
    }

}