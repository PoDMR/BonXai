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
import eu.fox7.schematoolkit.relaxng.om.Element;
import eu.fox7.schematoolkit.relaxng.om.Pattern;
import eu.fox7.schematoolkit.relaxng.om.RelaxNGSchema;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class RelaxNGSchema
 * @author Lars Schmidt
 */
public class RelaxNGSchemaTest extends junit.framework.TestCase {

    /**
     * Test of getRootPattern method, of class RelaxNGSchema.
     */
    @Test
    public void testGetRootPattern() {
        RelaxNGSchema instance = new RelaxNGSchema();
        Element expResult = new Element();
        instance.setRootPattern(expResult);
        Pattern result = instance.getRootPattern();
        assertEquals(expResult, result);
    }

    /**
     * Test of setRootPattern method, of class RelaxNGSchema.
     */
    @Test
    public void testSetRootPattern() {
        RelaxNGSchema instance = new RelaxNGSchema();
        Element expResult = new Element();
        instance.setRootPattern(expResult);
        Pattern result = instance.getRootPattern();
        assertEquals(expResult, result);
    }

    /**
     * Test of getNamespaceList method, of class RelaxNGSchema.
     */
    @Test
    public void testGetNamespaceList() {
        RelaxNGSchema instance = new RelaxNGSchema();
        NamespaceList result = instance.getNamespaceList();
        assertNotNull(result);
        assertEquals(RelaxNGSchema.RELAXNG_NAMESPACE, result.getDefaultNamespace().getUri());
        assertEquals(0, result.getNamespaces().size());
    }

    /**
     * Test of getDefaultNamespace method, of class RelaxNGSchema.
     */
    @Test
    public void testGetDefaultNamespace() {
        RelaxNGSchema instance = new RelaxNGSchema();
        assertEquals(RelaxNGSchema.RELAXNG_NAMESPACE, instance.getDefaultNamespace());
        instance.setDefaultNamespace("test");
        String expResult = "test";
        String result = instance.getDefaultNamespace();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDefaultNamespace method, of class RelaxNGSchema.
     */
    @Test
    public void testSetDefaultNamespace() {
        RelaxNGSchema instance = new RelaxNGSchema();
        assertEquals(RelaxNGSchema.RELAXNG_NAMESPACE, instance.getDefaultNamespace());
        instance.setDefaultNamespace("test");
        String expResult = "test";
        String result = instance.getDefaultNamespace();
        assertEquals(expResult, result);
    }

    /**
     * Test of getRootNamespacePrefix method, of class RelaxNGSchema.
     */
    @Test
    public void testGetRootNamespacePrefix() {
        String rootNamespacePrefix = "temp";
        RelaxNGSchema instance = new RelaxNGSchema();
        instance.setRootNamespacePrefix(rootNamespacePrefix);
        assertEquals(rootNamespacePrefix, instance.getRootNamespacePrefix());
    }

    /**
     * Test of setRootNamespacePrefix method, of class RelaxNGSchema.
     */
    @Test
    public void testSetRootNamespacePrefix() {
        String rootNamespacePrefix = "temp";
        RelaxNGSchema instance = new RelaxNGSchema();
        instance.setRootNamespacePrefix(rootNamespacePrefix);
        assertEquals(rootNamespacePrefix, instance.getRootNamespacePrefix());
    }
}
