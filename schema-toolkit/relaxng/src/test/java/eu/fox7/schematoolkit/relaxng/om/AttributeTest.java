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

import eu.fox7.schematoolkit.relaxng.om.Attribute;
import eu.fox7.schematoolkit.relaxng.om.Empty;
import eu.fox7.schematoolkit.relaxng.om.Name;
import eu.fox7.schematoolkit.relaxng.om.NameClass;
import eu.fox7.schematoolkit.relaxng.om.Pattern;
import static org.junit.Assert.*;

/**
 * Test of class Attribute
 * @author Lars Schmidt
 */
public class AttributeTest extends junit.framework.TestCase {

    /**
     * Test of getPattern method, of class Attribute.
     */
    @Test
    public void testGetPattern() {
        Pattern pattern = new Empty();
        Attribute instance = new Attribute();
        instance.setPattern(pattern);
        assertEquals(pattern, instance.getPattern());
    }

    /**
     * Test of setPattern method, of class Attribute.
     */
    @Test
    public void testSetPattern() {
        Pattern pattern = new Empty();
        Attribute instance = new Attribute();
        instance.setPattern(pattern);
        assertEquals(pattern, instance.getPattern());
    }

    /**
     * Test of getNameClass method, of class Attribute.
     */
    @Test
    public void testGetNameClass() {
        Attribute instance = new Attribute();
        NameClass expResult = new Name("testName");
        instance.setNameClass(expResult);
        NameClass result = instance.getNameClass();
        assertEquals(expResult, result);
    }

    /**
     * Test of setNameClass method, of class Attribute.
     */
    @Test
    public void testSetNameClass() {
        Attribute instance = new Attribute();
        NameClass expResult = new Name("testName");
        instance.setNameClass(expResult);
        NameClass result = instance.getNameClass();
        assertEquals(expResult, result);
    }

    /**
     * Test of getNameAttribute method, of class Attribute.
     */
    @Test
    public void testGetNameAttribute() {
        Attribute instance = new Attribute();
        String expResult = "attributeName";
        instance.setNameAttribute(expResult);
        String result = instance.getNameAttribute();
        assertEquals(expResult, result);
    }

    /**
     * Test of setNameAttribute method, of class Attribute.
     */
    @Test
    public void testSetNameAttribute() {
        Attribute instance = new Attribute();
        String expResult = "attributeName";
        instance.setNameAttribute(expResult);
        String result = instance.getNameAttribute();
        assertEquals(expResult, result);
    }
}
