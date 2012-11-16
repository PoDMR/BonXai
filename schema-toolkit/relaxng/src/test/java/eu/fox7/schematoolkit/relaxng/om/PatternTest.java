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

import eu.fox7.schematoolkit.common.NamespaceList;
import eu.fox7.schematoolkit.relaxng.om.Pattern;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class Pattern
 * @author Lars Schmidt
 */
public class PatternTest extends junit.framework.TestCase {

    /**
     * Test of getAttributeDatatypeLibrary method, of class Pattern.
     */
    @Test
    public void testGetAttributeDatatypeLibrary() {
        Pattern instance = new Pattern() {
        };
        instance.setAttributeDatatypeLibrary("http://www.example.org/");
        String expResult = "http://www.example.org/";
        String result = instance.getAttributeDatatypeLibrary();
        assertEquals(expResult, result);
    }

    /**
     * Test of setAttributeDatatypeLibrary method, of class Pattern.
     */
    @Test
    public void testSetAttributeDatatypeLibrary() {
        Pattern instance = new Pattern() {
        };
        instance.setAttributeDatatypeLibrary("http://www.example.org/");
        String expResult = "http://www.example.org/";
        String result = instance.getAttributeDatatypeLibrary();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAttributeNamespace method, of class Pattern.
     */
    @Test
    public void testGetAttributeNamespace() {
        Pattern instance = new Pattern() {
        };
        instance.setAttributeNamespace("http://www.example.org/");
        String expResult = "http://www.example.org/";
        String result = instance.getAttributeNamespace().getUri();
        assertEquals(expResult, result);
    }

    /**
     * Test of setAttributeNamespace method, of class Pattern.
     */
    @Test
    public void testSetAttributeNamespace() {
        Pattern instance = new Pattern() {
        };
        instance.setAttributeNamespace("http://www.example.org/");
        String expResult = "http://www.example.org/";
        String result = instance.getAttributeNamespace().getUri();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDefaultNamespace method, of class Pattern.
     */
    @Test
    public void testGetDefaultNamespace() {
        Pattern instance = new Pattern() {
        };
        instance.setDefaultNamespace("http://www.example.org/");
        String expResult = "http://www.example.org/";
        String result = instance.getDefaultNamespace();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDefaultNamespace method, of class Pattern.
     */
    @Test
    public void testSetDefaultNamespace() {
        Pattern instance = new Pattern() {
        };
        instance.setDefaultNamespace("http://www.example.org/");
        String expResult = "http://www.example.org/";
        String result = instance.getDefaultNamespace();
        assertEquals(expResult, result);
    }

    /**
     * Test of getNamespaceList method, of class Pattern.
     */
    @Test
    public void testGetNamespaceList() {
        Pattern instance = new Pattern() {
        };
        NamespaceList result = instance.getNamespaceList();
        assertNotNull(result);
        assertEquals(null, result.getDefaultNamespace());
        assertEquals(0, result.getNamespaces().size());
    }
}
