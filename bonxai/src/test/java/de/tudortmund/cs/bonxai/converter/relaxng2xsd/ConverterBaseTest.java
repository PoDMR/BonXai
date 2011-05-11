package de.tudortmund.cs.bonxai.converter.relaxng2xsd;

import de.tudortmund.cs.bonxai.common.IdentifiedNamespace;
import de.tudortmund.cs.bonxai.relaxng.Empty;
import de.tudortmund.cs.bonxai.relaxng.Pattern;
import de.tudortmund.cs.bonxai.relaxng.RelaxNGSchema;
import de.tudortmund.cs.bonxai.xsd.ImportedSchema;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
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
            this.schema.getNamespaceList().addIdentifiedNamespace(new IdentifiedNamespace("safari", "http://www.example.org/safari"));
            this.schema.getNamespaceList().addIdentifiedNamespace(new IdentifiedNamespace("lion", "http://www.example2.org/lion"));
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
            assertEquals("http://www.example.org/safari", this.schema.getForeignSchemas().getFirst().getSchema().getTargetNamespace());
            assertEquals("http://www.example2.org/lion", this.schema.getForeignSchemas().getLast().getSchema().getTargetNamespace());
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
