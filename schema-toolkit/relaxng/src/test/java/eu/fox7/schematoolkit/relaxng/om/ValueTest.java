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

import eu.fox7.schematoolkit.relaxng.om.Value;

/**
 * Test of class Value
 * @author Lars Schmidt
 */
public class ValueTest extends junit.framework.TestCase {

    /**
     * Test of getType method, of class Value.
     */
    @Test
    public void testGetType() {
        Value instance = new Value("content");
        instance.setType("myType");
        String expResult = "myType";
        String result = instance.getType();
        assertEquals(expResult, result);
    }

    /**
     * Test of setType method, of class Value.
     */
    @Test
    public void testSetType() {
        Value instance = new Value("content");
        instance.setType("myType");
        String expResult = "myType";
        String result = instance.getType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getContent method, of class Value.
     */
    @Test
    public void testGetContent() {
        Value instance = new Value("content");
        String expResult = "content";
        String result = instance.getContent();
        assertEquals(expResult, result);
    }

    /**
     * Test of setContent method, of class Value.
     */
    @Test
    public void testSetContent() {
        Value instance = new Value("content");
        instance.setContent("myNewContent");
        String expResult = "myNewContent";
        String result = instance.getContent();
        assertEquals(expResult, result);
    }

}