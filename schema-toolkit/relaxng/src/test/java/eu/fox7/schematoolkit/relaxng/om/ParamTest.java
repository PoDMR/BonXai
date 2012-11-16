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

import eu.fox7.schematoolkit.relaxng.om.Param;

/**
 * Test of class Param
 * @author Lars Schmidt
 */
public class ParamTest extends junit.framework.TestCase {

    /**
     * Test of getName method, of class Param.
     */
    @Test
    public void testGetName() {
        Param instance = new Param("myName");
        String result = instance.getName();
        assertEquals("myName", result);
        instance.setName("example");
        String expResult2 = "example";
        String result2 = instance.getName();
        assertEquals(expResult2, result2);
    }

    /**
     * Test of setName method, of class Param.
     */
    @Test
    public void testSetName() {
        Param instance = new Param("myName");
        String result = instance.getName();
        assertEquals("myName", result);
        instance.setName("example");
        String expResult2 = "example";
        String result2 = instance.getName();
        assertEquals(expResult2, result2);
    }

    /**
     * Test of getContent method, of class Param.
     */
    @Test
    public void testGetContent() {
        Param instance = new Param("myName");
        instance.setContent("example");
        String expResult = "example";
        String result = instance.getContent();
        assertEquals(expResult, result);
    }

    /**
     * Test of setContent method, of class Param.
     */
    @Test
    public void testSetContent() {
        Param instance = new Param("myName");
        instance.setContent("example");
        String expResult = "example";
        String result = instance.getContent();
        assertEquals(expResult, result);
    }

}