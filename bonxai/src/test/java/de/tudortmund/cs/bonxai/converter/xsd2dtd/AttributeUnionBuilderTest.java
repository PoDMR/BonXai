package de.tudortmund.cs.bonxai.converter.xsd2dtd;

import de.tudortmund.cs.bonxai.dtd.Attribute;
import de.tudortmund.cs.bonxai.dtd.AttributeType;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import java.util.LinkedHashSet;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class AttributeUnionBuilder
 * @author Lars Schmidt
 */
public class AttributeUnionBuilderTest extends junit.framework.TestCase {

    // Schema for this testcase
    XSDSchema schema;

    @Before
    @Override
    public void setUp() throws Exception {
        schema = new XSDSchema(XSD2DTDConverter.XMLSCHEMA_NAMESPACE);
    }

    /**
     * Test of unifyAttributes method, of class AttributeUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyAttributes_null_type() throws Exception {

        //--------------------------------------------------------------------//

        String attr1DefaultPresenceMode = null;
        String attr2DefaultPresenceMode = null;
        String attr1Value = null;
        String attr2Value = null;
        LinkedHashSet<String> attr1EnumerationTokens = null;
        LinkedHashSet<String> attr2EnumerationTokens = null;
        AttributeType attr1Type = null;
        AttributeType attr2Type = null;

        //--------------------------------------------------------------------//

        String name = "name";
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute1 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr1DefaultPresenceMode, attr1Value);
        dtdAttribute1.setEnumerationOrNotationTokens(attr1EnumerationTokens);
        dtdAttribute1.setType(attr1Type);
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute2 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr2DefaultPresenceMode, attr2Value);
        dtdAttribute2.setEnumerationOrNotationTokens(attr2EnumerationTokens);
        dtdAttribute2.setType(attr2Type);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDAttribute(dtdAttribute1);
        elementWrapper.addDTDAttribute(dtdAttribute2);
        AttributeUnionBuilder instance = new AttributeUnionBuilder(elementWrapper);

        instance.unifyAttributes();

        Attribute dtdAttribute = elementWrapper.getDTDAttributeMap().get("name").iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("name", dtdAttribute.getName());
        assertEquals(AttributeType.CDATA, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

    /**
     * Test of unifyAttributes method, of class AttributeUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyAttributes_AttributeDefaultPresence_Implied_Implied() throws Exception {

        //--------------------------------------------------------------------//

        String attr1DefaultPresenceMode = "#" + Attribute.AttributeDefaultPresence.IMPLIED.name();
        String attr2DefaultPresenceMode = "#" + Attribute.AttributeDefaultPresence.IMPLIED.name();
        String attr1Value = null;
        String attr2Value = null;
        LinkedHashSet<String> attr1EnumerationTokens = null;
        LinkedHashSet<String> attr2EnumerationTokens = null;
        AttributeType attr1Type = null;
        AttributeType attr2Type = null;

        //--------------------------------------------------------------------//

        String name = "name";
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute1 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr1DefaultPresenceMode, attr1Value);
        dtdAttribute1.setEnumerationOrNotationTokens(attr1EnumerationTokens);
        dtdAttribute1.setType(attr1Type);
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute2 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr2DefaultPresenceMode, attr2Value);
        dtdAttribute2.setEnumerationOrNotationTokens(attr2EnumerationTokens);
        dtdAttribute2.setType(attr2Type);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDAttribute(dtdAttribute1);
        elementWrapper.addDTDAttribute(dtdAttribute2);
        AttributeUnionBuilder instance = new AttributeUnionBuilder(elementWrapper);

        instance.unifyAttributes();

        Attribute dtdAttribute = elementWrapper.getDTDAttributeMap().get("name").iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("name", dtdAttribute.getName());
        assertEquals(AttributeType.CDATA, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

    /**
     * Test of unifyAttributes method, of class AttributeUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyAttributes_AttributeDefaultPresence_Implied_Required() throws Exception {

        //--------------------------------------------------------------------//

        String attr1DefaultPresenceMode = "#" + Attribute.AttributeDefaultPresence.IMPLIED.name();
        String attr2DefaultPresenceMode = "#" + Attribute.AttributeDefaultPresence.REQUIRED.name();
        String attr1Value = null;
        String attr2Value = null;
        LinkedHashSet<String> attr1EnumerationTokens = null;
        LinkedHashSet<String> attr2EnumerationTokens = null;
        AttributeType attr1Type = null;
        AttributeType attr2Type = null;

        //--------------------------------------------------------------------//

        String name = "name";
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute1 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr1DefaultPresenceMode, attr1Value);
        dtdAttribute1.setEnumerationOrNotationTokens(attr1EnumerationTokens);
        dtdAttribute1.setType(attr1Type);
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute2 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr2DefaultPresenceMode, attr2Value);
        dtdAttribute2.setEnumerationOrNotationTokens(attr2EnumerationTokens);
        dtdAttribute2.setType(attr2Type);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDAttribute(dtdAttribute1);
        elementWrapper.addDTDAttribute(dtdAttribute2);
        AttributeUnionBuilder instance = new AttributeUnionBuilder(elementWrapper);

        instance.unifyAttributes();

        Attribute dtdAttribute = elementWrapper.getDTDAttributeMap().get("name").iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("name", dtdAttribute.getName());
        assertEquals(AttributeType.CDATA, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

    /**
     * Test of unifyAttributes method, of class AttributeUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyAttributes_AttributeDefaultPresence_Required_Required() throws Exception {

        //--------------------------------------------------------------------//

        String attr1DefaultPresenceMode = "#" + Attribute.AttributeDefaultPresence.REQUIRED.name();
        String attr2DefaultPresenceMode = "#" + Attribute.AttributeDefaultPresence.REQUIRED.name();
        String attr1Value = null;
        String attr2Value = null;
        LinkedHashSet<String> attr1EnumerationTokens = null;
        LinkedHashSet<String> attr2EnumerationTokens = null;
        AttributeType attr1Type = null;
        AttributeType attr2Type = null;

        //--------------------------------------------------------------------//

        String name = "name";
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute1 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr1DefaultPresenceMode, attr1Value);
        dtdAttribute1.setEnumerationOrNotationTokens(attr1EnumerationTokens);
        dtdAttribute1.setType(attr1Type);
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute2 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr2DefaultPresenceMode, attr2Value);
        dtdAttribute2.setEnumerationOrNotationTokens(attr2EnumerationTokens);
        dtdAttribute2.setType(attr2Type);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDAttribute(dtdAttribute1);
        elementWrapper.addDTDAttribute(dtdAttribute2);
        AttributeUnionBuilder instance = new AttributeUnionBuilder(elementWrapper);

        instance.unifyAttributes();

        Attribute dtdAttribute = elementWrapper.getDTDAttributeMap().get("name").iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        assertEquals(Attribute.AttributeDefaultPresence.REQUIRED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("name", dtdAttribute.getName());
        assertEquals(AttributeType.CDATA, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

    /**
     * Test of unifyAttributes method, of class AttributeUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyAttributes_AttributeDefaultPresence_Implied_Fixed() throws Exception {

        //--------------------------------------------------------------------//

        String attr1DefaultPresenceMode = "#" + Attribute.AttributeDefaultPresence.IMPLIED.name();
        String attr2DefaultPresenceMode = "#" + Attribute.AttributeDefaultPresence.FIXED.name();
        String attr1Value = null;
        String attr2Value = null;
        LinkedHashSet<String> attr1EnumerationTokens = null;
        LinkedHashSet<String> attr2EnumerationTokens = null;
        AttributeType attr1Type = null;
        AttributeType attr2Type = null;

        //--------------------------------------------------------------------//

        String name = "name";
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute1 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr1DefaultPresenceMode, attr1Value);
        dtdAttribute1.setEnumerationOrNotationTokens(attr1EnumerationTokens);
        dtdAttribute1.setType(attr1Type);
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute2 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr2DefaultPresenceMode, attr2Value);
        dtdAttribute2.setEnumerationOrNotationTokens(attr2EnumerationTokens);
        dtdAttribute2.setType(attr2Type);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDAttribute(dtdAttribute1);
        elementWrapper.addDTDAttribute(dtdAttribute2);
        AttributeUnionBuilder instance = new AttributeUnionBuilder(elementWrapper);

        instance.unifyAttributes();

        Attribute dtdAttribute = elementWrapper.getDTDAttributeMap().get("name").iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("name", dtdAttribute.getName());
        assertEquals(AttributeType.CDATA, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

    /**
     * Test of unifyAttributes method, of class AttributeUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyAttributes_AttributeDefaultPresence_Required_Fixed() throws Exception {

        //--------------------------------------------------------------------//

        String attr1DefaultPresenceMode = "#" + Attribute.AttributeDefaultPresence.REQUIRED.name();
        String attr2DefaultPresenceMode = "#" + Attribute.AttributeDefaultPresence.FIXED.name();
        String attr1Value = null;
        String attr2Value = null;
        LinkedHashSet<String> attr1EnumerationTokens = null;
        LinkedHashSet<String> attr2EnumerationTokens = null;
        AttributeType attr1Type = null;
        AttributeType attr2Type = null;

        //--------------------------------------------------------------------//

        String name = "name";
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute1 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr1DefaultPresenceMode, attr1Value);
        dtdAttribute1.setEnumerationOrNotationTokens(attr1EnumerationTokens);
        dtdAttribute1.setType(attr1Type);
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute2 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr2DefaultPresenceMode, attr2Value);
        dtdAttribute2.setEnumerationOrNotationTokens(attr2EnumerationTokens);
        dtdAttribute2.setType(attr2Type);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDAttribute(dtdAttribute1);
        elementWrapper.addDTDAttribute(dtdAttribute2);
        AttributeUnionBuilder instance = new AttributeUnionBuilder(elementWrapper);

        instance.unifyAttributes();

        Attribute dtdAttribute = elementWrapper.getDTDAttributeMap().get("name").iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        assertEquals(Attribute.AttributeDefaultPresence.REQUIRED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("name", dtdAttribute.getName());
        assertEquals(AttributeType.CDATA, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

    /**
     * Test of unifyAttributes method, of class AttributeUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyAttributes_one_value() throws Exception {

        //--------------------------------------------------------------------//

        String attr1DefaultPresenceMode = null;
        String attr2DefaultPresenceMode = null;
        String attr1Value = "one";
        String attr2Value = null;
        LinkedHashSet<String> attr1EnumerationTokens = null;
        LinkedHashSet<String> attr2EnumerationTokens = null;
        AttributeType attr1Type = AttributeType.CDATA;
        AttributeType attr2Type = AttributeType.CDATA;

        //--------------------------------------------------------------------//

        String name = "name";
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute1 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr1DefaultPresenceMode, attr1Value);
        dtdAttribute1.setEnumerationOrNotationTokens(attr1EnumerationTokens);
        dtdAttribute1.setType(attr1Type);
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute2 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr2DefaultPresenceMode, attr2Value);
        dtdAttribute2.setEnumerationOrNotationTokens(attr2EnumerationTokens);
        dtdAttribute2.setType(attr2Type);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDAttribute(dtdAttribute1);
        elementWrapper.addDTDAttribute(dtdAttribute2);
        AttributeUnionBuilder instance = new AttributeUnionBuilder(elementWrapper);

        instance.unifyAttributes();

        Attribute dtdAttribute = elementWrapper.getDTDAttributeMap().get("name").iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("name", dtdAttribute.getName());
        assertEquals(AttributeType.CDATA, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

    /**
     * Test of unifyAttributes method, of class AttributeUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyAttributes_unequal_values() throws Exception {

        //--------------------------------------------------------------------//

        String attr1DefaultPresenceMode = null;
        String attr2DefaultPresenceMode = null;
        String attr1Value = "one";
        String attr2Value = "two";
        LinkedHashSet<String> attr1EnumerationTokens = null;
        LinkedHashSet<String> attr2EnumerationTokens = null;
        AttributeType attr1Type = AttributeType.CDATA;
        AttributeType attr2Type = AttributeType.CDATA;

        //--------------------------------------------------------------------//

        String name = "name";
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute1 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr1DefaultPresenceMode, attr1Value);
        dtdAttribute1.setEnumerationOrNotationTokens(attr1EnumerationTokens);
        dtdAttribute1.setType(attr1Type);
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute2 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr2DefaultPresenceMode, attr2Value);
        dtdAttribute2.setEnumerationOrNotationTokens(attr2EnumerationTokens);
        dtdAttribute2.setType(attr2Type);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDAttribute(dtdAttribute1);
        elementWrapper.addDTDAttribute(dtdAttribute2);
        AttributeUnionBuilder instance = new AttributeUnionBuilder(elementWrapper);

        instance.unifyAttributes();

        Attribute dtdAttribute = elementWrapper.getDTDAttributeMap().get("name").iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("name", dtdAttribute.getName());
        assertEquals(AttributeType.CDATA, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

    /**
     * Test of unifyAttributes method, of class AttributeUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyAttributes_equal_values() throws Exception {

        //--------------------------------------------------------------------//

        String attr1DefaultPresenceMode = null;
        String attr2DefaultPresenceMode = null;
        String attr1Value = "one";
        String attr2Value = "one";
        LinkedHashSet<String> attr1EnumerationTokens = null;
        LinkedHashSet<String> attr2EnumerationTokens = null;
        AttributeType attr1Type = AttributeType.CDATA;
        AttributeType attr2Type = AttributeType.CDATA;

        //--------------------------------------------------------------------//

        String name = "name";
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute1 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr1DefaultPresenceMode, attr1Value);
        dtdAttribute1.setEnumerationOrNotationTokens(attr1EnumerationTokens);
        dtdAttribute1.setType(attr1Type);
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute2 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr2DefaultPresenceMode, attr2Value);
        dtdAttribute2.setEnumerationOrNotationTokens(attr2EnumerationTokens);
        dtdAttribute2.setType(attr2Type);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDAttribute(dtdAttribute1);
        elementWrapper.addDTDAttribute(dtdAttribute2);
        AttributeUnionBuilder instance = new AttributeUnionBuilder(elementWrapper);

        instance.unifyAttributes();

        Attribute dtdAttribute = elementWrapper.getDTDAttributeMap().get("name").iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        assertEquals(null, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("name", dtdAttribute.getName());
        assertEquals(AttributeType.CDATA, dtdAttribute.getType());
        assertEquals("one", dtdAttribute.getValue());
    }

    /**
     * Test of unifyAttributes method, of class AttributeUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyAttributes_FIXED_without_value() throws Exception {

        //--------------------------------------------------------------------//

        String attr1DefaultPresenceMode = "#FIXED";
        String attr2DefaultPresenceMode = "#FIXED";
        String attr1Value = null;
        String attr2Value = null;
        LinkedHashSet<String> attr1EnumerationTokens = null;
        LinkedHashSet<String> attr2EnumerationTokens = null;
        AttributeType attr1Type = AttributeType.CDATA;
        AttributeType attr2Type = AttributeType.CDATA;

        //--------------------------------------------------------------------//

        String name = "name";
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute1 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr1DefaultPresenceMode, attr1Value);
        dtdAttribute1.setEnumerationOrNotationTokens(attr1EnumerationTokens);
        dtdAttribute1.setType(attr1Type);
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute2 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr2DefaultPresenceMode, attr2Value);
        dtdAttribute2.setEnumerationOrNotationTokens(attr2EnumerationTokens);
        dtdAttribute2.setType(attr2Type);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDAttribute(dtdAttribute1);
        elementWrapper.addDTDAttribute(dtdAttribute2);
        AttributeUnionBuilder instance = new AttributeUnionBuilder(elementWrapper);

        instance.unifyAttributes();

        Attribute dtdAttribute = elementWrapper.getDTDAttributeMap().get("name").iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("name", dtdAttribute.getName());
        assertEquals(AttributeType.CDATA, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

    /**
     * Test of unifyAttributes method, of class AttributeUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyAttributes_FIXED_with_equal_values() throws Exception {

        //--------------------------------------------------------------------//

        String attr1DefaultPresenceMode = "#FIXED";
        String attr2DefaultPresenceMode = "#FIXED";
        String attr1Value = "one";
        String attr2Value = "one";
        LinkedHashSet<String> attr1EnumerationTokens = null;
        LinkedHashSet<String> attr2EnumerationTokens = null;
        AttributeType attr1Type = AttributeType.CDATA;
        AttributeType attr2Type = AttributeType.CDATA;

        //--------------------------------------------------------------------//

        String name = "name";
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute1 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr1DefaultPresenceMode, attr1Value);
        dtdAttribute1.setEnumerationOrNotationTokens(attr1EnumerationTokens);
        dtdAttribute1.setType(attr1Type);
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute2 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr2DefaultPresenceMode, attr2Value);
        dtdAttribute2.setEnumerationOrNotationTokens(attr2EnumerationTokens);
        dtdAttribute2.setType(attr2Type);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDAttribute(dtdAttribute1);
        elementWrapper.addDTDAttribute(dtdAttribute2);
        AttributeUnionBuilder instance = new AttributeUnionBuilder(elementWrapper);

        instance.unifyAttributes();

        Attribute dtdAttribute = elementWrapper.getDTDAttributeMap().get("name").iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        assertEquals(Attribute.AttributeDefaultPresence.FIXED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("name", dtdAttribute.getName());
        assertEquals(AttributeType.CDATA, dtdAttribute.getType());
        assertEquals("one", dtdAttribute.getValue());
    }

    /**
     * Test of unifyAttributes method, of class AttributeUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyAttributes_FIXED_with_unequal_values() throws Exception {

        //--------------------------------------------------------------------//

        String attr1DefaultPresenceMode = "#FIXED";
        String attr2DefaultPresenceMode = "#FIXED";
        String attr1Value = "one";
        String attr2Value = "two";
        LinkedHashSet<String> attr1EnumerationTokens = null;
        LinkedHashSet<String> attr2EnumerationTokens = null;
        AttributeType attr1Type = AttributeType.CDATA;
        AttributeType attr2Type = AttributeType.CDATA;

        //--------------------------------------------------------------------//

        String name = "name";
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute1 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr1DefaultPresenceMode, attr1Value);
        dtdAttribute1.setEnumerationOrNotationTokens(attr1EnumerationTokens);
        dtdAttribute1.setType(attr1Type);
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute2 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr2DefaultPresenceMode, attr2Value);
        dtdAttribute2.setEnumerationOrNotationTokens(attr2EnumerationTokens);
        dtdAttribute2.setType(attr2Type);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDAttribute(dtdAttribute1);
        elementWrapper.addDTDAttribute(dtdAttribute2);
        AttributeUnionBuilder instance = new AttributeUnionBuilder(elementWrapper);

        instance.unifyAttributes();

        Attribute dtdAttribute = elementWrapper.getDTDAttributeMap().get("name").iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("name", dtdAttribute.getName());
        assertEquals(AttributeType.CDATA, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

    /**
     * Test of unifyAttributes method, of class AttributeUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyAttributes_types_CDATA_ID() throws Exception {

        //--------------------------------------------------------------------//

        String attr1DefaultPresenceMode = "#IMPLIED";
        String attr2DefaultPresenceMode = "#IMPLIED";
        String attr1Value = null;
        String attr2Value = null;
        LinkedHashSet<String> attr1EnumerationTokens = null;
        LinkedHashSet<String> attr2EnumerationTokens = null;
        AttributeType attr1Type = AttributeType.CDATA;
        AttributeType attr2Type = AttributeType.ID;

        //--------------------------------------------------------------------//

        String name = "name";
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute1 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr1DefaultPresenceMode, attr1Value);
        dtdAttribute1.setEnumerationOrNotationTokens(attr1EnumerationTokens);
        dtdAttribute1.setType(attr1Type);
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute2 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr2DefaultPresenceMode, attr2Value);
        dtdAttribute2.setEnumerationOrNotationTokens(attr2EnumerationTokens);
        dtdAttribute2.setType(attr2Type);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDAttribute(dtdAttribute1);
        elementWrapper.addDTDAttribute(dtdAttribute2);
        AttributeUnionBuilder instance = new AttributeUnionBuilder(elementWrapper);

        instance.unifyAttributes();

        Attribute dtdAttribute = elementWrapper.getDTDAttributeMap().get("name").iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("name", dtdAttribute.getName());
        assertEquals(AttributeType.CDATA, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

    /**
     * Test of unifyAttributes method, of class AttributeUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyAttributes_types_NMTOKEN_NMTOKENS() throws Exception {

        //--------------------------------------------------------------------//

        String attr1DefaultPresenceMode = "#IMPLIED";
        String attr2DefaultPresenceMode = "#IMPLIED";
        String attr1Value = null;
        String attr2Value = null;
        LinkedHashSet<String> attr1EnumerationTokens = null;
        LinkedHashSet<String> attr2EnumerationTokens = null;
        AttributeType attr1Type = AttributeType.NMTOKEN;
        AttributeType attr2Type = AttributeType.NMTOKENS;

        //--------------------------------------------------------------------//

        String name = "name";
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute1 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr1DefaultPresenceMode, attr1Value);
        dtdAttribute1.setEnumerationOrNotationTokens(attr1EnumerationTokens);
        dtdAttribute1.setType(attr1Type);
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute2 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr2DefaultPresenceMode, attr2Value);
        dtdAttribute2.setEnumerationOrNotationTokens(attr2EnumerationTokens);
        dtdAttribute2.setType(attr2Type);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDAttribute(dtdAttribute1);
        elementWrapper.addDTDAttribute(dtdAttribute2);
        AttributeUnionBuilder instance = new AttributeUnionBuilder(elementWrapper);

        instance.unifyAttributes();

        Attribute dtdAttribute = elementWrapper.getDTDAttributeMap().get("name").iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("name", dtdAttribute.getName());
        assertEquals(AttributeType.NMTOKENS, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

    /**
     * Test of unifyAttributes method, of class AttributeUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyAttributes_types_IDREF_NMTOKENS() throws Exception {

        //--------------------------------------------------------------------//

        String attr1DefaultPresenceMode = "#IMPLIED";
        String attr2DefaultPresenceMode = "#IMPLIED";
        String attr1Value = null;
        String attr2Value = null;
        LinkedHashSet<String> attr1EnumerationTokens = null;
        LinkedHashSet<String> attr2EnumerationTokens = null;
        AttributeType attr1Type = AttributeType.NMTOKENS;
        AttributeType attr2Type = AttributeType.IDREF;

        //--------------------------------------------------------------------//

        String name = "name";
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute1 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr1DefaultPresenceMode, attr1Value);
        dtdAttribute1.setEnumerationOrNotationTokens(attr1EnumerationTokens);
        dtdAttribute1.setType(attr1Type);
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute2 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr2DefaultPresenceMode, attr2Value);
        dtdAttribute2.setEnumerationOrNotationTokens(attr2EnumerationTokens);
        dtdAttribute2.setType(attr2Type);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDAttribute(dtdAttribute1);
        elementWrapper.addDTDAttribute(dtdAttribute2);
        AttributeUnionBuilder instance = new AttributeUnionBuilder(elementWrapper);

        instance.unifyAttributes();

        Attribute dtdAttribute = elementWrapper.getDTDAttributeMap().get("name").iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("name", dtdAttribute.getName());
        assertEquals(AttributeType.NMTOKENS, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

    /**
     * Test of unifyAttributes method, of class AttributeUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyAttributes_types_ENTITY_NMTOKEN() throws Exception {

        //--------------------------------------------------------------------//

        String attr1DefaultPresenceMode = "#IMPLIED";
        String attr2DefaultPresenceMode = "#IMPLIED";
        String attr1Value = null;
        String attr2Value = null;
        LinkedHashSet<String> attr1EnumerationTokens = null;
        LinkedHashSet<String> attr2EnumerationTokens = null;
        AttributeType attr1Type = AttributeType.NMTOKEN;
        AttributeType attr2Type = AttributeType.ENTITY;

        //--------------------------------------------------------------------//

        String name = "name";
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute1 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr1DefaultPresenceMode, attr1Value);
        dtdAttribute1.setEnumerationOrNotationTokens(attr1EnumerationTokens);
        dtdAttribute1.setType(attr1Type);
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute2 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr2DefaultPresenceMode, attr2Value);
        dtdAttribute2.setEnumerationOrNotationTokens(attr2EnumerationTokens);
        dtdAttribute2.setType(attr2Type);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDAttribute(dtdAttribute1);
        elementWrapper.addDTDAttribute(dtdAttribute2);
        AttributeUnionBuilder instance = new AttributeUnionBuilder(elementWrapper);

        instance.unifyAttributes();

        Attribute dtdAttribute = elementWrapper.getDTDAttributeMap().get("name").iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("name", dtdAttribute.getName());
        assertEquals(AttributeType.NMTOKEN, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

    /**
     * Test of unifyAttributes method, of class AttributeUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyAttributes_Enumeration_with_both_null() throws Exception {

        //--------------------------------------------------------------------//

        String attr1DefaultPresenceMode = "#IMPLIED";
        String attr2DefaultPresenceMode = "#IMPLIED";
        String attr1Value = null;
        String attr2Value = null;
        LinkedHashSet<String> attr1EnumerationTokens = null;
        LinkedHashSet<String> attr2EnumerationTokens = null;
        AttributeType attr1Type = AttributeType.ENUMERATION;
        AttributeType attr2Type = AttributeType.ENUMERATION;

        //--------------------------------------------------------------------//

        String name = "name";
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute1 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr1DefaultPresenceMode, attr1Value);
        dtdAttribute1.setEnumerationOrNotationTokens(attr1EnumerationTokens);
        dtdAttribute1.setType(attr1Type);
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute2 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr2DefaultPresenceMode, attr2Value);
        dtdAttribute2.setEnumerationOrNotationTokens(attr2EnumerationTokens);
        dtdAttribute2.setType(attr2Type);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDAttribute(dtdAttribute1);
        elementWrapper.addDTDAttribute(dtdAttribute2);
        AttributeUnionBuilder instance = new AttributeUnionBuilder(elementWrapper);

        instance.unifyAttributes();

        Attribute dtdAttribute = elementWrapper.getDTDAttributeMap().get("name").iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(0, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("name", dtdAttribute.getName());
        assertEquals(AttributeType.ENUMERATION, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

    /**
     * Test of unifyAttributes method, of class AttributeUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyAttributes_Enumeration_with_one_null() throws Exception {

        //--------------------------------------------------------------------//

        String attr1DefaultPresenceMode = "#IMPLIED";
        String attr2DefaultPresenceMode = "#IMPLIED";
        String attr1Value = null;
        String attr2Value = null;
        LinkedHashSet<String> attr1EnumerationTokens = new LinkedHashSet<String>();
        attr1EnumerationTokens.add("test1");
        attr1EnumerationTokens.add("test2");
        LinkedHashSet<String> attr2EnumerationTokens = null;
        AttributeType attr1Type = AttributeType.ENUMERATION;
        AttributeType attr2Type = AttributeType.ENUMERATION;

        //--------------------------------------------------------------------//

        String name = "name";
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute1 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr1DefaultPresenceMode, attr1Value);
        dtdAttribute1.setEnumerationOrNotationTokens(attr1EnumerationTokens);
        dtdAttribute1.setType(attr1Type);
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute2 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr2DefaultPresenceMode, attr2Value);
        dtdAttribute2.setEnumerationOrNotationTokens(attr2EnumerationTokens);
        dtdAttribute2.setType(attr2Type);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDAttribute(dtdAttribute1);
        elementWrapper.addDTDAttribute(dtdAttribute2);
        AttributeUnionBuilder instance = new AttributeUnionBuilder(elementWrapper);

        instance.unifyAttributes();

        Attribute dtdAttribute = elementWrapper.getDTDAttributeMap().get("name").iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(2, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("test1", dtdAttribute.getEnumerationOrNotationTokens().toArray()[0]);
        assertEquals("test2", dtdAttribute.getEnumerationOrNotationTokens().toArray()[1]);
        assertEquals("name", dtdAttribute.getName());
        assertEquals(AttributeType.ENUMERATION, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

    /**
     * Test of unifyAttributes method, of class AttributeUnionBuilder.
     * @throws Exception
     */
    @Test
    public void testUnifyAttributes_Enumeration() throws Exception {

        //--------------------------------------------------------------------//

        String attr1DefaultPresenceMode = "#IMPLIED";
        String attr2DefaultPresenceMode = "#IMPLIED";
        String attr1Value = null;
        String attr2Value = null;
        LinkedHashSet<String> attr1EnumerationTokens = new LinkedHashSet<String>();
        attr1EnumerationTokens.add("test3");
        attr1EnumerationTokens.add("test4");
        LinkedHashSet<String> attr2EnumerationTokens = new LinkedHashSet<String>();
        attr2EnumerationTokens.add("test1");
        attr2EnumerationTokens.add("test2");
        AttributeType attr1Type = AttributeType.ENUMERATION;
        AttributeType attr2Type = AttributeType.ENUMERATION;

        //--------------------------------------------------------------------//

        String name = "name";
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute1 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr1DefaultPresenceMode, attr1Value);
        dtdAttribute1.setEnumerationOrNotationTokens(attr1EnumerationTokens);
        dtdAttribute1.setType(attr1Type);
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute2 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr2DefaultPresenceMode, attr2Value);
        dtdAttribute2.setEnumerationOrNotationTokens(attr2EnumerationTokens);
        dtdAttribute2.setType(attr2Type);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDAttribute(dtdAttribute1);
        elementWrapper.addDTDAttribute(dtdAttribute2);
        AttributeUnionBuilder instance = new AttributeUnionBuilder(elementWrapper);

        instance.unifyAttributes();

        Attribute dtdAttribute = elementWrapper.getDTDAttributeMap().get("name").iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(4, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("test1", dtdAttribute.getEnumerationOrNotationTokens().toArray()[0]);
        assertEquals("test2", dtdAttribute.getEnumerationOrNotationTokens().toArray()[1]);
        assertEquals("test3", dtdAttribute.getEnumerationOrNotationTokens().toArray()[2]);
        assertEquals("test4", dtdAttribute.getEnumerationOrNotationTokens().toArray()[3]);
        assertEquals("name", dtdAttribute.getName());
        assertEquals(AttributeType.ENUMERATION, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }

        /**
     * Test of unifyAttributes method, of class AttributeUnionBuilder.
         * @throws Exception 
         */
    @Test
    public void testUnifyAttributes_Enumeration_equal_values() throws Exception {

        //--------------------------------------------------------------------//

        String attr1DefaultPresenceMode = "#IMPLIED";
        String attr2DefaultPresenceMode = "#IMPLIED";
        String attr1Value = null;
        String attr2Value = null;
        LinkedHashSet<String> attr1EnumerationTokens = new LinkedHashSet<String>();
        attr1EnumerationTokens.add("test2");
        attr1EnumerationTokens.add("test4");
        LinkedHashSet<String> attr2EnumerationTokens = new LinkedHashSet<String>();
        attr2EnumerationTokens.add("test1");
        attr2EnumerationTokens.add("test2");
        AttributeType attr1Type = AttributeType.ENUMERATION;
        AttributeType attr2Type = AttributeType.ENUMERATION;

        //--------------------------------------------------------------------//

        String name = "name";
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute1 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr1DefaultPresenceMode, attr1Value);
        dtdAttribute1.setEnumerationOrNotationTokens(attr1EnumerationTokens);
        dtdAttribute1.setType(attr1Type);
        de.tudortmund.cs.bonxai.dtd.Attribute dtdAttribute2 = new de.tudortmund.cs.bonxai.dtd.Attribute(name, attr2DefaultPresenceMode, attr2Value);
        dtdAttribute2.setEnumerationOrNotationTokens(attr2EnumerationTokens);
        dtdAttribute2.setType(attr2Type);
        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDAttribute(dtdAttribute1);
        elementWrapper.addDTDAttribute(dtdAttribute2);
        AttributeUnionBuilder instance = new AttributeUnionBuilder(elementWrapper);

        instance.unifyAttributes();

        Attribute dtdAttribute = elementWrapper.getDTDAttributeMap().get("name").iterator().next();

        //--------------------------------------------------------------------//
        // Result:
        //--------------------------------------------------------------------//

        assertEquals(Attribute.AttributeDefaultPresence.IMPLIED, dtdAttribute.getAttributeDefaultPresence());
        assertEquals(3, dtdAttribute.getEnumerationOrNotationTokens().size());
        assertEquals("test1", dtdAttribute.getEnumerationOrNotationTokens().toArray()[0]);
        assertEquals("test2", dtdAttribute.getEnumerationOrNotationTokens().toArray()[1]);
        assertEquals("test4", dtdAttribute.getEnumerationOrNotationTokens().toArray()[2]);
        assertEquals("name", dtdAttribute.getName());
        assertEquals(AttributeType.ENUMERATION, dtdAttribute.getType());
        assertEquals(null, dtdAttribute.getValue());
    }
}
