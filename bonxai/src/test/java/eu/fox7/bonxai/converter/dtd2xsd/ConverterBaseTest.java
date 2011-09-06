package eu.fox7.bonxai.converter.dtd2xsd;

import eu.fox7.bonxai.common.IdentifiedNamespace;
import eu.fox7.bonxai.converter.dtd2xsd.ConverterBase;
import eu.fox7.bonxai.converter.dtd2xsd.DTD2XSDConverter;
import eu.fox7.bonxai.converter.dtd2xsd.exceptions.DTDNameIsEmptyException;
import eu.fox7.bonxai.converter.dtd2xsd.exceptions.DTDNameStartsWithUnsupportedSymbolException;
import eu.fox7.bonxai.converter.dtd2xsd.exceptions.IdentifiedNamespaceNotFoundException;
import eu.fox7.bonxai.xsd.ImportedSchema;
import eu.fox7.bonxai.xsd.XSDSchema;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class ConverterBase
 * @author lightsabre
 */
public class ConverterBaseTest extends junit.framework.TestCase {

    // Schema for this testcase
    XSDSchema schema;

    /**
     * Before every test the schema and schemaProcessor are refreshed
     */
    @Before
    @Override
    public void setUp() {
        schema = new XSDSchema();
    }

    /**
     * Test of generateXSDFQName method, of class ConverterBase.
     */
    @Test
    public void testGenerateXSDFQNameWithoutNamespaces() {
        ConverterBase instance = new ConverterBase(schema, new IdentifiedNamespace("xs", DTD2XSDConverter.XMLSCHEMA_NAMESPACE), false) {
        };
        try {
            assertEquals("{http://www.w3.org/2001/XMLSchema}snake", instance.generateXSDFQName("snake"));
            assertEquals("{http://www.w3.org/2001/XMLSchema}safari-lion", instance.generateXSDFQName("safari:lion"));
            assertEquals("{http://www.w3.org/2001/XMLSchema}a---", instance.generateXSDFQName("a*?="));

            instance = new ConverterBase(schema, new IdentifiedNamespace("", ""), false) {
            };

            assertEquals("{}snake", instance.generateXSDFQName("snake"));
            assertEquals("{}safari-lion", instance.generateXSDFQName("safari:lion"));
            assertEquals("{}a---", instance.generateXSDFQName("a*?="));

            instance = new ConverterBase(schema, null, false) {
            };

            assertEquals("{}snake", instance.generateXSDFQName("snake"));
            assertEquals("{}safari-lion", instance.generateXSDFQName("safari:lion"));
            assertEquals("{}a---", instance.generateXSDFQName("a*?="));

        } catch (Exception ex) {
            fail("Exception was thrown: " + ex.getMessage());
        }
    }

    /**
     * Test of generateXSDFQName method, of class ConverterBase.
     */
    @Test
    public void testGenerateXSDFQNameWithNamespaces() {
        ConverterBase instance = new ConverterBase(schema, new IdentifiedNamespace("xs", DTD2XSDConverter.XMLSCHEMA_NAMESPACE), true) {
        };
        try {
            assertEquals("{http://www.w3.org/2001/XMLSchema}snake", instance.generateXSDFQName("snake"));
            assertEquals("{http://www.example.org/safari}lion", instance.generateXSDFQName("safari:lion"));
            assertEquals("{http://www.w3.org/2001/XMLSchema}a---", instance.generateXSDFQName("a*?="));

            instance = new ConverterBase(schema, new IdentifiedNamespace("", ""), true) {
            };

            assertEquals("{}snake", instance.generateXSDFQName("snake"));
            assertEquals("{http://www.example.org/safari}lion", instance.generateXSDFQName("safari:lion"));
            assertEquals("{}a---", instance.generateXSDFQName("a*?="));

            instance = new ConverterBase(schema, null, true) {
            };

            assertEquals("{}snake", instance.generateXSDFQName("snake"));
            assertEquals("{http://www.example.org/safari}lion", instance.generateXSDFQName("safari:lion"));
            assertEquals("{}a---", instance.generateXSDFQName("a*?="));

        } catch (Exception ex) {
            fail("Exception was thrown: " + ex.getMessage());
        }
    }

    /**
     * Test of generateXSDFQName method with DTDNameIsEmptyException, of class ConverterBase.
     * @throws Exception 
     */
    @Test
    public void testGenerateXSDFQNameWithoutNamespacesWithDTDNameIsEmptyException() throws Exception {
        ConverterBase instance = new ConverterBase(schema, new IdentifiedNamespace("xs", DTD2XSDConverter.XMLSCHEMA_NAMESPACE), false) {
        };
        try {
            assertEquals("", instance.generateXSDFQName(""));
        } catch (DTDNameIsEmptyException ex) {
            return;
        }
        fail("There is an empty name, but this was not detected");
    }

    /**
     * Test of generateXSDFQName method with DTDNameIsEmptyException, of class ConverterBase.
     * @throws Exception
     */
    @Test
    public void testGenerateXSDFQNameWithoutNamespacesWithDTDNameStartsWithUnsupportedSymbolException() throws Exception {
        ConverterBase instance = new ConverterBase(schema, new IdentifiedNamespace("xs", DTD2XSDConverter.XMLSCHEMA_NAMESPACE), false) {
        };
        try {
            assertEquals("", instance.generateXSDFQName("#"));
        } catch (DTDNameStartsWithUnsupportedSymbolException ex) {
            return;
        }
        fail("There is a DTD name that starts with an unsupported symbol, but this was not detected");
    }

    /**
     * Test of updateOrCreateImportedSchema method, of class ConverterBase.
     * @throws Exception
     */
    @Test
    public void testUpdateOrCreateImportedSchema() throws Exception{
        try {
            this.schema.getNamespaceList().addIdentifiedNamespace(new IdentifiedNamespace("safari", "http://www.example.org/safari"));
            this.schema.getNamespaceList().addIdentifiedNamespace(new IdentifiedNamespace("lion", "http://www.example2.org/lion"));
            ConverterBase instance = new ConverterBase(schema, null, true) {
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
     * Test of updateOrCreateImportedSchema method with IdentifiedNamespaceNotFoundException, of class ConverterBase.
     * @throws Exception
     */
    @Test
    public void testUpdateOrCreateImportedSchemaWithIdentifiedNamespaceNotFoundException() throws Exception {
        try {
            ConverterBase instance = new ConverterBase(schema, null, true) {
            };
            assertEquals(0, this.schema.getForeignSchemas().size());
            instance.updateOrCreateImportedSchema("http://www.example.org/safari");
        } catch (IdentifiedNamespaceNotFoundException ex) {
            return;
        }
        fail("An expected namespace was not found in the namespacelist of the schema, but this was not detected.");
    }
}
