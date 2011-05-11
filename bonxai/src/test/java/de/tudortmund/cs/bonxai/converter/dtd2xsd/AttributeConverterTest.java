package de.tudortmund.cs.bonxai.converter.dtd2xsd;

import de.tudortmund.cs.bonxai.converter.dtd2xsd.exceptions.EnumerationOrNotationTokensEmtpyException;
import de.tudortmund.cs.bonxai.dtd.Attribute;
import de.tudortmund.cs.bonxai.dtd.DocumentTypeDefinition;
import de.tudortmund.cs.bonxai.dtd.Element;
import de.tudortmund.cs.bonxai.dtd.parser.DTDSAXParser;
import de.tudortmund.cs.bonxai.xsd.AttributeUse;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.SimpleContentRestriction;
import java.util.LinkedHashSet;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class AttributeConverter
 * @author lightsabre
 */
public class AttributeConverterTest extends junit.framework.TestCase {
    // Schema for this testcase
    XSDSchema schema;
    Element dtdElement;
    AttributeConverter instance;
    DocumentTypeDefinition dtd;

    /**
     * Before every test the schema and schemaProcessor are refreshed
     * @throws Exception 
     */
    @Before
    @Override
    public void setUp() throws Exception {
        schema = new XSDSchema();
        String filePath = new String("tests/de/tudortmund/cs/bonxai/converter/dtd2xsd/dtds/attributeConverterTests/attributes.xml");
        DTDSAXParser dtdParser = new DTDSAXParser(false);
        this.dtd = dtdParser.parseXML(filePath);
        this.dtdElement = dtd.getRootElement();
        this.instance = new AttributeConverter(schema, null, false);
    }

    /**
     * Test of convert method, of class AttributeConverter with myAttr01.
     * @throws Exception
     */
    @Test
    public void testConvert_myAttr01() throws Exception {

        // myAttr01
        Attribute dtdAttribute = dtdElement.getAttributes().get(0);
        de.tudortmund.cs.bonxai.xsd.Attribute xsdAttribute = instance.convert(dtd, dtdElement, dtdAttribute);
        assertEquals("{}myAttr01", xsdAttribute.getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}ID", xsdAttribute.getSimpleType().getName());
        assertFalse(xsdAttribute.getSimpleType().isAnonymous());
        assertTrue(xsdAttribute.getTypeAttr());
        assertEquals(null, xsdAttribute.getDefault());
        assertEquals(null, xsdAttribute.getFixed());
        assertEquals(null, xsdAttribute.getForm());
        assertEquals(AttributeUse.Optional, xsdAttribute.getUse());
        assertEquals(false, xsdAttribute.isDummy());
    }

    /**
     * Test of convert method, of class AttributeConverter with myAttr02.
     * @throws Exception
     */
    @Test
    public void testConvert_myAttr02() throws Exception {
        // myAttr02
        Attribute dtdAttribute = dtdElement.getAttributes().get(1);
        de.tudortmund.cs.bonxai.xsd.Attribute xsdAttribute = instance.convert(dtd, dtdElement, dtdAttribute);
        assertEquals("{}myAttr02", xsdAttribute.getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}IDREF", xsdAttribute.getSimpleType().getName());
        assertFalse(xsdAttribute.getSimpleType().isAnonymous());
        assertTrue(xsdAttribute.getTypeAttr());
        assertEquals("two", xsdAttribute.getDefault());
        assertEquals(null, xsdAttribute.getFixed());
        assertEquals(null, xsdAttribute.getForm());
        assertEquals(null, xsdAttribute.getUse());
        assertEquals(false, xsdAttribute.isDummy());
    }

    /**
     * Test of convert method, of class AttributeConverter with myAttr03.
     * @throws Exception
     */
    @Test
    public void testConvert_myAttr03() throws Exception {
        // myAttr03
        Attribute dtdAttribute = dtdElement.getAttributes().get(2);
        de.tudortmund.cs.bonxai.xsd.Attribute xsdAttribute = instance.convert(dtd, dtdElement, dtdAttribute);
        assertEquals("{}myAttr03", xsdAttribute.getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}IDREFS", xsdAttribute.getSimpleType().getName());
        assertFalse(xsdAttribute.getSimpleType().isAnonymous());
        assertTrue(xsdAttribute.getTypeAttr());
        assertEquals("three", xsdAttribute.getDefault());
        assertEquals(null, xsdAttribute.getFixed());
        assertEquals(null, xsdAttribute.getForm());
        assertEquals(null, xsdAttribute.getUse());
        assertEquals(false, xsdAttribute.isDummy());
    }

    /**
     * Test of convert method, of class AttributeConverter with myAttr04.
     * @throws Exception
     */
    @Test
    public void testConvert_myAttr04() throws Exception {
        // myAttr04
        Attribute dtdAttribute = dtdElement.getAttributes().get(3);
        de.tudortmund.cs.bonxai.xsd.Attribute xsdAttribute = instance.convert(dtd, dtdElement, dtdAttribute);
        assertEquals("{}myAttr04", xsdAttribute.getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}ENTITY", xsdAttribute.getSimpleType().getName());
        assertFalse(xsdAttribute.getSimpleType().isAnonymous());
        assertTrue(xsdAttribute.getTypeAttr());
        assertEquals("four", xsdAttribute.getDefault());
        assertEquals(null, xsdAttribute.getFixed());
        assertEquals(null, xsdAttribute.getForm());
        assertEquals(null, xsdAttribute.getUse());
        assertEquals(false, xsdAttribute.isDummy());
    }

    /**
     * Test of convert method, of class AttributeConverter with myAttr05.
     * @throws Exception
     */
    @Test
    public void testConvert_myAttr05() throws Exception {
        // myAttr05
        Attribute dtdAttribute = dtdElement.getAttributes().get(4);
        de.tudortmund.cs.bonxai.xsd.Attribute xsdAttribute = instance.convert(dtd, dtdElement, dtdAttribute);
        assertEquals("{}myAttr05", xsdAttribute.getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}ENTITIES", xsdAttribute.getSimpleType().getName());
        assertFalse(xsdAttribute.getSimpleType().isAnonymous());
        assertTrue(xsdAttribute.getTypeAttr());
        assertEquals("five", xsdAttribute.getDefault());
        assertEquals(null, xsdAttribute.getFixed());
        assertEquals(null, xsdAttribute.getForm());
        assertEquals(null, xsdAttribute.getUse());
        assertEquals(false, xsdAttribute.isDummy());
    }

    /**
     * Test of convert method, of class AttributeConverter with myAttr06.
     * @throws Exception
     */
    @Test
    public void testConvert_myAttr06() throws Exception {
        // myAttr06
        Attribute dtdAttribute = dtdElement.getAttributes().get(5);
        de.tudortmund.cs.bonxai.xsd.Attribute xsdAttribute = instance.convert(dtd, dtdElement, dtdAttribute);
        assertEquals("{}myAttr06", xsdAttribute.getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}ENTITIES", xsdAttribute.getSimpleType().getName());
        assertFalse(xsdAttribute.getSimpleType().isAnonymous());
        assertTrue(xsdAttribute.getTypeAttr());
        assertEquals(null, xsdAttribute.getDefault());
        assertEquals(null, xsdAttribute.getFixed());
        assertEquals(null, xsdAttribute.getForm());
        assertEquals(AttributeUse.Optional, xsdAttribute.getUse());
        assertEquals(false, xsdAttribute.isDummy());
    }

    /**
     * Test of convert method, of class AttributeConverter with myAttr07.
     * @throws Exception
     */
    @Test
    public void testConvert_myAttr07() throws Exception {
        // myAttr07
        Attribute dtdAttribute = dtdElement.getAttributes().get(6);
        de.tudortmund.cs.bonxai.xsd.Attribute xsdAttribute = instance.convert(dtd, dtdElement, dtdAttribute);
        assertEquals("{}myAttr07", xsdAttribute.getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}NMTOKEN", xsdAttribute.getSimpleType().getName());
        assertFalse(xsdAttribute.getSimpleType().isAnonymous());
        assertTrue(xsdAttribute.getTypeAttr());
        assertEquals("seven", xsdAttribute.getDefault());
        assertEquals(null, xsdAttribute.getFixed());
        assertEquals(null, xsdAttribute.getForm());
        assertEquals(null, xsdAttribute.getUse());
        assertEquals(false, xsdAttribute.isDummy());
    }

    /**
     * Test of convert method, of class AttributeConverter with myAttr08.
     * @throws Exception
     */
    @Test
    public void testConvert_myAttr08() throws Exception {
        // myAttr08
        Attribute dtdAttribute = dtdElement.getAttributes().get(7);
        de.tudortmund.cs.bonxai.xsd.Attribute xsdAttribute = instance.convert(dtd, dtdElement, dtdAttribute);
        assertEquals("{}myAttr08", xsdAttribute.getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}NMTOKENS", xsdAttribute.getSimpleType().getName());
        assertFalse(xsdAttribute.getSimpleType().isAnonymous());
        assertTrue(xsdAttribute.getTypeAttr());
        assertEquals("eight", xsdAttribute.getDefault());
        assertEquals(null, xsdAttribute.getFixed());
        assertEquals(null, xsdAttribute.getForm());
        assertEquals(null, xsdAttribute.getUse());
        assertEquals(false, xsdAttribute.isDummy());
    }

    /**
     * Test of convert method, of class AttributeConverter with myAttr09.
     * @throws Exception
     */
    @Test
    public void testConvert_myAttr09() throws Exception {
        // myAttr09
        Attribute dtdAttribute = dtdElement.getAttributes().get(8);
        de.tudortmund.cs.bonxai.xsd.Attribute xsdAttribute = instance.convert(dtd, dtdElement, dtdAttribute);
        assertEquals("{}myAttr09", xsdAttribute.getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", xsdAttribute.getSimpleType().getName());
        assertFalse(xsdAttribute.getSimpleType().isAnonymous());
        assertTrue(xsdAttribute.getTypeAttr());
        assertEquals("nine", xsdAttribute.getDefault());
        assertEquals(null, xsdAttribute.getFixed());
        assertEquals(null, xsdAttribute.getForm());
        assertEquals(null, xsdAttribute.getUse());
        assertEquals(false, xsdAttribute.isDummy());
    }

    /**
     * Test of convert method, of class AttributeConverter with myAttr10.
     * @throws Exception
     */
    @Test
    public void testConvert_myAttr10() throws Exception {
        // myAttr10
        Attribute dtdAttribute = dtdElement.getAttributes().get(9);
        de.tudortmund.cs.bonxai.xsd.Attribute xsdAttribute = instance.convert(dtd, dtdElement, dtdAttribute);
        assertEquals("{}myAttr10", xsdAttribute.getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", xsdAttribute.getSimpleType().getName());
        assertFalse(xsdAttribute.getSimpleType().isAnonymous());
        assertTrue(xsdAttribute.getTypeAttr());
        assertEquals(null, xsdAttribute.getDefault());
        assertEquals("ten", xsdAttribute.getFixed());
        assertEquals(null, xsdAttribute.getForm());
        assertEquals(null, xsdAttribute.getUse());
        assertEquals(false, xsdAttribute.isDummy());
    }

    /**
     * Test of convert method, of class AttributeConverter with myAttr11.
     * @throws Exception
     */
    @Test
    public void testConvert_myAttr11() throws Exception {
        // myAttr11
        Attribute dtdAttribute = dtdElement.getAttributes().get(10);
        de.tudortmund.cs.bonxai.xsd.Attribute xsdAttribute = instance.convert(dtd, dtdElement, dtdAttribute);
        assertEquals("{}myAttr11", xsdAttribute.getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", xsdAttribute.getSimpleType().getName());
        assertFalse(xsdAttribute.getSimpleType().isAnonymous());
        assertTrue(xsdAttribute.getTypeAttr());
        assertEquals(null, xsdAttribute.getDefault());
        assertEquals(null, xsdAttribute.getFixed());
        assertEquals(null, xsdAttribute.getForm());
        assertEquals(AttributeUse.Optional, xsdAttribute.getUse());
        assertEquals(false, xsdAttribute.isDummy());
    }

    /**
     * Test of convert method, of class AttributeConverter with myAttr12.
     * @throws Exception
     */
    @Test
    public void testConvert_myAttr12() throws Exception {
        // myAttr12
        Attribute dtdAttribute = dtdElement.getAttributes().get(11);
        de.tudortmund.cs.bonxai.xsd.Attribute xsdAttribute = instance.convert(dtd, dtdElement, dtdAttribute);
        assertEquals("{}myAttr12", xsdAttribute.getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", xsdAttribute.getSimpleType().getName());
        assertFalse(xsdAttribute.getSimpleType().isAnonymous());
        assertTrue(xsdAttribute.getTypeAttr());
        assertEquals(null, xsdAttribute.getDefault());
        assertEquals(null, xsdAttribute.getFixed());
        assertEquals(null, xsdAttribute.getForm());
        assertEquals(AttributeUse.Required, xsdAttribute.getUse());
        assertEquals(false, xsdAttribute.isDummy());
    }

    /**
     * Test of convert method, of class AttributeConverter with myAttr13.
     * @throws Exception
     */
    @Test
    public void testConvert_myAttr13() throws Exception {
        // myAttr13
        Attribute dtdAttribute = dtdElement.getAttributes().get(12);
        de.tudortmund.cs.bonxai.xsd.Attribute xsdAttribute = instance.convert(dtd, dtdElement, dtdAttribute);
        assertEquals("{}myAttr13", xsdAttribute.getName());
        assertEquals("{}textContentAttr-myAttr13", xsdAttribute.getSimpleType().getName());
        assertTrue(xsdAttribute.getSimpleType().isAnonymous());
        assertFalse(xsdAttribute.getTypeAttr());
        assertEquals("lion", xsdAttribute.getDefault());
        assertEquals(null, xsdAttribute.getFixed());
        assertEquals(null, xsdAttribute.getForm());
        assertEquals(null, xsdAttribute.getUse());
        assertEquals(false, xsdAttribute.isDummy());

        assertTrue(xsdAttribute.getSimpleType().getInheritance() instanceof SimpleContentRestriction);
        SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) xsdAttribute.getSimpleType().getInheritance();
        assertTrue(simpleContentRestriction.getAttributes().isEmpty());
        assertEquals("{http://www.w3.org/2001/XMLSchema}token", simpleContentRestriction.getBase().getName());
        assertEquals(2, simpleContentRestriction.getEnumeration().size());
        assertEquals("tiger", simpleContentRestriction.getEnumeration().get(0));
        assertEquals("lion", simpleContentRestriction.getEnumeration().get(1));
    }

    /**
     * Test of convert method, of class AttributeConverter with myAttr14.
     * @throws Exception
     */
    @Test
    public void testConvert_myAttr14() throws Exception {
        // myAttr14
        Attribute dtdAttribute = dtdElement.getAttributes().get(13);
        de.tudortmund.cs.bonxai.xsd.Attribute xsdAttribute = instance.convert(dtd, dtdElement, dtdAttribute);
        assertEquals("{}myAttr14", xsdAttribute.getName());
        assertEquals("{}textContentAttr-myAttr14", xsdAttribute.getSimpleType().getName());
        assertTrue(xsdAttribute.getSimpleType().isAnonymous());
        assertFalse(xsdAttribute.getTypeAttr());
        assertEquals("snake", xsdAttribute.getDefault());
        assertEquals(null, xsdAttribute.getFixed());
        assertEquals(null, xsdAttribute.getForm());
        assertEquals(null, xsdAttribute.getUse());
        assertEquals(false, xsdAttribute.isDummy());

        assertTrue(xsdAttribute.getSimpleType().getInheritance() instanceof SimpleContentRestriction);
        SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) xsdAttribute.getSimpleType().getInheritance();
        assertTrue(simpleContentRestriction.getAttributes().isEmpty());
        assertEquals("{http://www.w3.org/2001/XMLSchema}token", simpleContentRestriction.getBase().getName());
        assertEquals(2, simpleContentRestriction.getEnumeration().size());
        assertEquals("elephant", simpleContentRestriction.getEnumeration().get(0));
        assertEquals("snake", simpleContentRestriction.getEnumeration().get(1));
    }

    /**
     * Test of convert method, of class AttributeConverter with myAttr14 and EnumerationOrNotationTokensEmtpyException.
     * @throws Exception
     */
    @Test
    public void testConvert_myAttr14_WithEnumerationOrNotationTokensEmtpyException() throws Exception {
        // myAttr14
        try {
        Attribute dtdAttribute = dtdElement.getAttributes().get(13);
        dtdAttribute.setEnumerationOrNotationTokens(new LinkedHashSet<String>());
        de.tudortmund.cs.bonxai.xsd.Attribute xsdAttribute = instance.convert(dtd, dtdElement, dtdAttribute);

        dtdAttribute.setEnumerationOrNotationTokens(null);
        xsdAttribute = instance.convert(dtd, dtdElement, dtdAttribute);

        } catch (EnumerationOrNotationTokensEmtpyException e) {
            return;
        }
        fail("There are no values in an enumeration type, but this was not detected");
    }
}
