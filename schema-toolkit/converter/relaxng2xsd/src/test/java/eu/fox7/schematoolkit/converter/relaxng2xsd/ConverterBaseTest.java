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

package eu.fox7.schematoolkit.converter.relaxng2xsd;

import eu.fox7.schematoolkit.common.IdentifiedNamespace;
import eu.fox7.schematoolkit.converter.relaxng2xsd.ConverterBase;
import eu.fox7.schematoolkit.relaxng.om.Empty;
import eu.fox7.schematoolkit.relaxng.om.Pattern;
import eu.fox7.schematoolkit.relaxng.om.RelaxNGSchema;
import eu.fox7.schematoolkit.xsd.om.ImportedSchema;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class ConverterBase
 * @author Lars Schmidt
 */
public class ConverterBaseTest extends junit.framework.TestCase {
    // Schema for this testcase

    XSDSchema schema;
    RelaxNGSchema rng;

    /**
     * Befor every test the schema and schemaProcessor are refreshed
     */
    @Before
    @Override
    public void setUp() {
        schema = new XSDSchema();
        rng = new RelaxNGSchema();
    }

    /**
     * Test of getRelaxNGSchema method, of class ConverterBase.
     */
    @Test
    public void testGetRelaxNGSchema() {
        ConverterBase instance = new ConverterBase(schema, rng, null, null, null) {
        };
        RelaxNGSchema expResult = this.rng;
        RelaxNGSchema result = instance.getRelaxNGSchema();
        assertEquals(expResult, result);
    }

    /**
     * Test of getXmlSchema method, of class ConverterBase.
     */
    @Test
    public void testGetXmlSchema() {
        ConverterBase instance = new ConverterBase(schema, rng, null, null, null) {
        };
        XSDSchema expResult = this.schema;
        XSDSchema result = instance.getXmlSchema();
        assertEquals(expResult, result);
    }

    /**
     * Test of updateOrCreateImportedSchema method, of class ConverterBase.
     * @throws Exception
     */
    @Test
    public void testUpdateOrCreateImportedSchema() throws Exception {
        try {
            this.schema.getNamespaceList().addNamespace(new IdentifiedNamespace("safari", "http://www.example.org/safari"));
            this.schema.getNamespaceList().addNamespace(new IdentifiedNamespace("lion", "http://www.example2.org/lion"));
            ConverterBase instance = new ConverterBase(schema, null, null, null, null) {
            };
            assertEquals(0, this.schema.getForeignSchemas().size());
            ImportedSchema result = instance.updateOrCreateImportedSchema("http://www.example.org/safari");
            assertEquals(1, this.schema.getForeignSchemas().size());
            result = instance.updateOrCreateImportedSchema("http://www.example2.org/lion");
            assertEquals(2, this.schema.getForeignSchemas().size());
            result = instance.updateOrCreateImportedSchema("http://www.example.org/safari");
            assertEquals(2, this.schema.getForeignSchemas().size());
            assertEquals("importedSchema_safari.xsd", this.schema.getForeignSchemas().getFirst().getSchemaLocation());
            assertEquals("importedSchema_lion.xsd", this.schema.getForeignSchemas().getLast().getSchemaLocation());
            assertEquals("http://www.example.org/safari", this.schema.getForeignSchemas().getFirst().getSchema().getDefaultNamespace());
            assertEquals("http://www.example2.org/lion", this.schema.getForeignSchemas().getLast().getSchema().getDefaultNamespace());
        } catch (Exception ex) {
            Logger.getLogger(ConverterBaseTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of getUsedLocalNames method, of class ConverterBase.
     */
    @Test
    public void testGetUsedLocalNames() {
        HashSet<String> tempNameSet = new HashSet<String>();
        tempNameSet.add("lion");
        ConverterBase instance = new ConverterBase(schema, rng, null, tempNameSet, null) {
        };
        HashSet<String> result = instance.getUsedLocalNames();
        assertEquals(tempNameSet, result);
        assertTrue(result.contains("lion"));
    }

    /**
     * Test of getUsedLocalTypeNames method, of class ConverterBase.
     */
    @Test
    public void testGetUsedLocalTypeNames() {
        HashSet<String> tempNameSet = new HashSet<String>();
        tempNameSet.add("lion");
        ConverterBase instance = new ConverterBase(schema, rng, null, null, tempNameSet) {
        };
        HashSet<String> result = instance.getUsedLocalTypeNames();
        assertEquals(tempNameSet, result);
        assertTrue(result.contains("lion"));
    }

    /**
     * Test of getPatternInformation method, of class ConverterBase.
     */
    @Test
    public void testGetPatternInformation() {
        HashMap<Pattern, HashSet<String>> patternIntel = new HashMap<Pattern, HashSet<String>>();
        Empty empty = new Empty();
        HashSet<String> emptySet = new HashSet<String>();
        emptySet.add("empty");
        patternIntel.put(empty, emptySet);
        ConverterBase instance = new ConverterBase(schema, rng, patternIntel, null, null) {
        };
        HashMap<Pattern, HashSet<String>> result = instance.getPatternInformation();
        assertEquals(patternIntel, result);
        assertTrue(result.get(empty) != null);
        assertTrue(result.get(empty).contains("empty"));
    }
}
