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

import org.junit.Test;

import eu.fox7.schematoolkit.relaxng.om.Name;

/**
 * Test of class Name
 * @author Lars Schmidt
 */
public class NameTest extends junit.framework.TestCase {


    /**
     * Test of getAttributeNamespace method, of class Name.
     */
    @Test
    public void testGetAttributeNamespace() {
        Name instance = new Name("myName");
        instance.setAttributeNamespace("http://www.example.org/");
        String expResult = "http://www.example.org/";
        String result = instance.getAttributeNamespace().getUri();
        assertEquals(expResult, result);
    }

    /**
     * Test of setAttributeNamespace method, of class Name.
     */
    @Test
    public void testSetAttributeNamespace() {
        Name instance = new Name("myName");
        instance.setAttributeNamespace("http://www.example.org/");
        String expResult = "http://www.example.org/";
        String result = instance.getAttributeNamespace().getUri();
        assertEquals(expResult, result);
    }

    /**
     * Test of getContent method, of class Name.
     */
    @Test
    public void testGetContent() {
        Name instance = new Name("myName");
        assertEquals("myName", instance.getContent());
        instance.setContent("example");
        String expResult = "example";
        String result = instance.getContent();
        assertEquals(expResult, result);
    }

    /**
     * Test of setContent method, of class Name.
     */
    @Test
    public void testSetContent() {
        Name instance = new Name("myName");
        assertEquals("myName", instance.getContent());
        instance.setContent("example");
        String expResult = "example";
        String result = instance.getContent();
        assertEquals(expResult, result);
    }

}