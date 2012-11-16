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

import eu.fox7.schematoolkit.relaxng.om.Element;
import eu.fox7.schematoolkit.relaxng.om.Empty;
import eu.fox7.schematoolkit.relaxng.om.ExternalRef;
import eu.fox7.schematoolkit.relaxng.om.Name;
import eu.fox7.schematoolkit.relaxng.om.NameClass;
import eu.fox7.schematoolkit.relaxng.om.Pattern;
import eu.fox7.schematoolkit.relaxng.om.Text;
import static org.junit.Assert.*;

/**
 * Test of class Element
 * @author Lars Schmidt
 */
public class ElementTest extends junit.framework.TestCase {

    /**
     * Test of getNameClass method, of class Element.
     */
    @Test
    public void testGetNameClass() {
        Element instance = new Element();
        NameClass expResult = new Name("testName");
        instance.setNameClass(expResult);
        NameClass result = instance.getNameClass();
        assertEquals(expResult, result);
    }

    /**
     * Test of setNameClass method, of class Element.
     */
    @Test
    public void testSetNameClass() {
        Element instance = new Element();
        NameClass expResult = new Name("testName");
        instance.setNameClass(expResult);
        NameClass result = instance.getNameClass();
        assertEquals(expResult, result);
    }

    /**
     * Test of getPatterns method, of class Element.
     */
    @Test
    public void testGetPatterns() {
        Pattern pattern = new Empty();
        Pattern pattern2 = new ExternalRef("http://www.example.org/rng.rng");
        Pattern pattern3 = new Text();
        Element instance = new Element();
        instance.addPattern(pattern);
        instance.addPattern(pattern2);
        instance.addPattern(pattern3);
        assertEquals(pattern, instance.getPatterns().getFirst());
        assertEquals(pattern2, instance.getPatterns().get(1));
        assertEquals(pattern3, instance.getPatterns().getLast());
    }

    /**
     * Test of addPattern method, of class Element.
     */
    @Test
    public void testAddPattern() {
        Pattern pattern = new Empty();
        Pattern pattern2 = new ExternalRef("http://www.example.org/rng.rng");
        Pattern pattern3 = new Text();
        Element instance = new Element();
        instance.addPattern(pattern2);
        instance.addPattern(pattern);
        instance.addPattern(pattern3);
        assertEquals(pattern2, instance.getPatterns().getFirst());
        assertEquals(pattern, instance.getPatterns().get(1));
        assertEquals(pattern3, instance.getPatterns().getLast());
    }

    /**
     * Test of getNameAttribute method, of class Element.
     */
    @Test
    public void testGetNameAttribute() {
        Element instance = new Element();
        String expResult = "attributeElementName";
        instance.setNameAttribute(expResult);
        String result = instance.getNameAttribute();
        assertEquals(expResult, result);
    }

    /**
     * Test of setNameAttribute method, of class Element.
     */
    @Test
    public void testSetNameAttribute() {
        Element instance = new Element();
        String expResult = "attributeElementName";
        instance.setNameAttribute(expResult);
        String result = instance.getNameAttribute();
        assertEquals(expResult, result);
    }
}
