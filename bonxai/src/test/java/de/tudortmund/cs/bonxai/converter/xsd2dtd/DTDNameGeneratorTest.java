package de.tudortmund.cs.bonxai.converter.xsd2dtd;

import de.tudortmund.cs.bonxai.common.IdentifiedNamespace;
import de.tudortmund.cs.bonxai.dtd.DocumentTypeDefinition;
import de.tudortmund.cs.bonxai.xsd.Attribute;
import de.tudortmund.cs.bonxai.xsd.Element;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.XSDSchema.Qualification;
import org.junit.Before;
import org.junit.Test;

/**
 * Test of class DTDNameGenerator
 * @author Lars Schmidt
 */
public class DTDNameGeneratorTest extends junit.framework.TestCase {

    // Schema for this testcase
    XSDSchema schema;
    DocumentTypeDefinition dtd;
    XSD2DTDConverter xsd2dtdConverter;

    @Before
    @Override
    public void setUp() throws Exception {
        schema = new XSDSchema(XSD2DTDConverter.XMLSCHEMA_NAMESPACE);
        schema.getNamespaceList().addIdentifiedNamespace(new IdentifiedNamespace("test", "http://www.test.de/"));
        schema.getNamespaceList().addIdentifiedNamespace(new IdentifiedNamespace("xsd", XSD2DTDConverter.XMLSCHEMA_NAMESPACE));
        this.dtd = new DocumentTypeDefinition();
        xsd2dtdConverter = new XSD2DTDConverter(schema, false);
    }

    /**
     * Test of getDTDElementName method, of class DTDNameGenerator.
     */
    @Test
    public void testGetDTDElementName() {
        XSD2DTDConverter.NAMESPACE_PREFIX_FEATURE = false;
        XSD2DTDConverter.NAMESPACE_PREFIX_CONSIDER_QUALIFICATION_ATTRIBUTE = false;
        XSD2DTDConverter.NAMESPACE_PREFIX_FORCE_USAGE = false;

        Qualification instanceQualification = null;
        DTDNameGenerator instance = new DTDNameGenerator(schema);
        assertEquals("elementName", instance.getDTDElementName("{}elementName", instanceQualification));
        assertEquals("elementName", instance.getDTDElementName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}elementName", instanceQualification));

        XSD2DTDConverter.NAMESPACE_PREFIX_FEATURE = true;
        XSD2DTDConverter.NAMESPACE_PREFIX_CONSIDER_QUALIFICATION_ATTRIBUTE = false;
        XSD2DTDConverter.NAMESPACE_PREFIX_FORCE_USAGE = false;

        assertEquals("elementName", instance.getDTDElementName("{}elementName", instanceQualification));
        assertEquals("elementName", instance.getDTDElementName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}elementName", instanceQualification));

        XSD2DTDConverter.NAMESPACE_PREFIX_FEATURE = true;
        XSD2DTDConverter.NAMESPACE_PREFIX_CONSIDER_QUALIFICATION_ATTRIBUTE = true;
        XSD2DTDConverter.NAMESPACE_PREFIX_FORCE_USAGE = false;

        instanceQualification = Qualification.unqualified;
        assertEquals("elementName", instance.getDTDElementName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}elementName", instanceQualification));

        instanceQualification = Qualification.qualified;
        assertEquals("xsd:elementName", instance.getDTDElementName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}elementName", instanceQualification));

        instanceQualification = Qualification.unqualified;
        assertEquals("elementName", instance.getDTDElementName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}elementName", instanceQualification));

        XSD2DTDConverter.NAMESPACE_PREFIX_FEATURE = true;
        XSD2DTDConverter.NAMESPACE_PREFIX_CONSIDER_QUALIFICATION_ATTRIBUTE = true;
        XSD2DTDConverter.NAMESPACE_PREFIX_FORCE_USAGE = true;

        instanceQualification = Qualification.unqualified;
        assertEquals("xsd:elementName", instance.getDTDElementName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}elementName", instanceQualification));

        instanceQualification = Qualification.qualified;
        assertEquals("xsd:elementName", instance.getDTDElementName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}elementName", instanceQualification));

        instanceQualification = Qualification.unqualified;
        assertEquals("xsd:elementName", instance.getDTDElementName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}elementName", instanceQualification));

    }

    /**
     * Test of getDTDElementName method, of class DTDNameGenerator.
     */
    @Test
    public void testGetDTDElementNameToplevel() {
        XSD2DTDConverter.NAMESPACE_PREFIX_FEATURE = false;
        XSD2DTDConverter.NAMESPACE_PREFIX_CONSIDER_QUALIFICATION_ATTRIBUTE = false;
        XSD2DTDConverter.NAMESPACE_PREFIX_FORCE_USAGE = false;

        Qualification instanceQualification = null;
        DTDNameGenerator instance = new DTDNameGenerator(schema);
        assertEquals("elementName", instance.getDTDElementName("{}elementName", instanceQualification));
        assertEquals("elementName", instance.getDTDElementName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}elementName", instanceQualification));

        XSD2DTDConverter.NAMESPACE_PREFIX_FEATURE = true;
        XSD2DTDConverter.NAMESPACE_PREFIX_CONSIDER_QUALIFICATION_ATTRIBUTE = false;
        XSD2DTDConverter.NAMESPACE_PREFIX_FORCE_USAGE = false;

        this.schema.addElement(schema.getElementSymbolTable().updateOrCreateReference("{}elementName", new Element("{}elementName")));
        this.schema.addElement(schema.getElementSymbolTable().updateOrCreateReference("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}elementName", new Element("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}elementName")));

        assertEquals("elementName", instance.getDTDElementName("{}elementName", instanceQualification));
        assertEquals("xsd:elementName", instance.getDTDElementName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}elementName", instanceQualification));

        XSD2DTDConverter.NAMESPACE_PREFIX_FEATURE = true;
        XSD2DTDConverter.NAMESPACE_PREFIX_CONSIDER_QUALIFICATION_ATTRIBUTE = true;
        XSD2DTDConverter.NAMESPACE_PREFIX_FORCE_USAGE = false;

        instanceQualification = Qualification.unqualified;
        assertEquals("elementName", instance.getDTDElementName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}elementName", instanceQualification));

        instanceQualification = Qualification.qualified;
        assertEquals("xsd:elementName", instance.getDTDElementName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}elementName", instanceQualification));

        instanceQualification = Qualification.unqualified;
        assertEquals("elementName", instance.getDTDElementName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}elementName", instanceQualification));

        XSD2DTDConverter.NAMESPACE_PREFIX_FEATURE = true;
        XSD2DTDConverter.NAMESPACE_PREFIX_CONSIDER_QUALIFICATION_ATTRIBUTE = true;
        XSD2DTDConverter.NAMESPACE_PREFIX_FORCE_USAGE = true;

        instanceQualification = Qualification.unqualified;
        assertEquals("xsd:elementName", instance.getDTDElementName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}elementName", instanceQualification));

        instanceQualification = Qualification.qualified;
        assertEquals("xsd:elementName", instance.getDTDElementName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}elementName", instanceQualification));

        instanceQualification = Qualification.unqualified;
        assertEquals("xsd:elementName", instance.getDTDElementName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}elementName", instanceQualification));
    }

    /**
     * Test of getDTDAttributeName method, of class DTDNameGenerator.
     */
    @Test
    public void testGetDTDAttributeName() {
        XSD2DTDConverter.NAMESPACE_PREFIX_FEATURE = false;
        XSD2DTDConverter.NAMESPACE_PREFIX_CONSIDER_QUALIFICATION_ATTRIBUTE = false;
        XSD2DTDConverter.NAMESPACE_PREFIX_FORCE_USAGE = false;

        Qualification instanceQualification = null;
        DTDNameGenerator instance = new DTDNameGenerator(schema);
        assertEquals("attributeName", instance.getDTDAttributeName("{}attributeName", instanceQualification));
        assertEquals("attributeName", instance.getDTDAttributeName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}attributeName", instanceQualification));

        XSD2DTDConverter.NAMESPACE_PREFIX_FEATURE = true;
        XSD2DTDConverter.NAMESPACE_PREFIX_CONSIDER_QUALIFICATION_ATTRIBUTE = false;
        XSD2DTDConverter.NAMESPACE_PREFIX_FORCE_USAGE = false;

        assertEquals("attributeName", instance.getDTDAttributeName("{}attributeName", instanceQualification));
        assertEquals("attributeName", instance.getDTDAttributeName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}attributeName", instanceQualification));

        XSD2DTDConverter.NAMESPACE_PREFIX_FEATURE = true;
        XSD2DTDConverter.NAMESPACE_PREFIX_CONSIDER_QUALIFICATION_ATTRIBUTE = true;
        XSD2DTDConverter.NAMESPACE_PREFIX_FORCE_USAGE = false;

        instanceQualification = Qualification.unqualified;
        assertEquals("attributeName", instance.getDTDAttributeName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}attributeName", instanceQualification));

        instanceQualification = Qualification.qualified;
        assertEquals("xsd:attributeName", instance.getDTDAttributeName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}attributeName", instanceQualification));

        instanceQualification = Qualification.unqualified;
        assertEquals("attributeName", instance.getDTDAttributeName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}attributeName", instanceQualification));

        XSD2DTDConverter.NAMESPACE_PREFIX_FEATURE = true;
        XSD2DTDConverter.NAMESPACE_PREFIX_CONSIDER_QUALIFICATION_ATTRIBUTE = true;
        XSD2DTDConverter.NAMESPACE_PREFIX_FORCE_USAGE = true;

        instanceQualification = Qualification.unqualified;
        assertEquals("xsd:attributeName", instance.getDTDAttributeName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}attributeName", instanceQualification));

        instanceQualification = Qualification.qualified;
        assertEquals("xsd:attributeName", instance.getDTDAttributeName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}attributeName", instanceQualification));

        instanceQualification = Qualification.unqualified;
        assertEquals("xsd:attributeName", instance.getDTDAttributeName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}attributeName", instanceQualification));

    }

    /**
     * Test of getDTDAttributeName method, of class DTDNameGenerator.
     */
    @Test
    public void testGetDTDAttributeNameTopLevel() {
        XSD2DTDConverter.NAMESPACE_PREFIX_FEATURE = false;
        XSD2DTDConverter.NAMESPACE_PREFIX_CONSIDER_QUALIFICATION_ATTRIBUTE = false;
        XSD2DTDConverter.NAMESPACE_PREFIX_FORCE_USAGE = false;

        Qualification instanceQualification = null;
        DTDNameGenerator instance = new DTDNameGenerator(schema);

        this.schema.addAttribute(this.schema.getAttributeSymbolTable().updateOrCreateReference("{}attributeName", new Attribute("{}attributeName")));
        this.schema.addAttribute(this.schema.getAttributeSymbolTable().updateOrCreateReference("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}attributeName", new Attribute("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}attributeName")));

        assertEquals("attributeName", instance.getDTDAttributeName("{}attributeName", instanceQualification));
        assertEquals("attributeName", instance.getDTDAttributeName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}attributeName", instanceQualification));

        XSD2DTDConverter.NAMESPACE_PREFIX_FEATURE = true;
        XSD2DTDConverter.NAMESPACE_PREFIX_CONSIDER_QUALIFICATION_ATTRIBUTE = false;
        XSD2DTDConverter.NAMESPACE_PREFIX_FORCE_USAGE = false;

        assertEquals("attributeName", instance.getDTDAttributeName("{}attributeName", instanceQualification));
        assertEquals("xsd:attributeName", instance.getDTDAttributeName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}attributeName", instanceQualification));

        XSD2DTDConverter.NAMESPACE_PREFIX_FEATURE = true;
        XSD2DTDConverter.NAMESPACE_PREFIX_CONSIDER_QUALIFICATION_ATTRIBUTE = true;
        XSD2DTDConverter.NAMESPACE_PREFIX_FORCE_USAGE = false;

        instanceQualification = Qualification.unqualified;
        assertEquals("attributeName", instance.getDTDAttributeName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}attributeName", instanceQualification));

        instanceQualification = Qualification.qualified;
        assertEquals("xsd:attributeName", instance.getDTDAttributeName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}attributeName", instanceQualification));

        instanceQualification = Qualification.unqualified;
        assertEquals("attributeName", instance.getDTDAttributeName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}attributeName", instanceQualification));

        XSD2DTDConverter.NAMESPACE_PREFIX_FEATURE = true;
        XSD2DTDConverter.NAMESPACE_PREFIX_CONSIDER_QUALIFICATION_ATTRIBUTE = true;
        XSD2DTDConverter.NAMESPACE_PREFIX_FORCE_USAGE = true;

        instanceQualification = Qualification.unqualified;
        assertEquals("xsd:attributeName", instance.getDTDAttributeName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}attributeName", instanceQualification));

        instanceQualification = Qualification.qualified;
        assertEquals("xsd:attributeName", instance.getDTDAttributeName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}attributeName", instanceQualification));

        instanceQualification = Qualification.unqualified;
        assertEquals("xsd:attributeName", instance.getDTDAttributeName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}attributeName", instanceQualification));

    }

    /**
     * Test of getDTDName method, of class DTDNameGenerator.
     */
    @Test
    public void testGetDTDName() {
        XSD2DTDConverter.NAMESPACE_PREFIX_FEATURE = false;
        XSD2DTDConverter.NAMESPACE_PREFIX_CONSIDER_QUALIFICATION_ATTRIBUTE = false;
        XSD2DTDConverter.NAMESPACE_PREFIX_FORCE_USAGE = false;

        Qualification instanceQualification = null;
        DTDNameGenerator instance = new DTDNameGenerator(schema);
        assertEquals("elementName", instance.getDTDName("{}elementName", instanceQualification));
        assertEquals("elementName", instance.getDTDName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}elementName", instanceQualification));

        XSD2DTDConverter.NAMESPACE_PREFIX_FEATURE = true;
        XSD2DTDConverter.NAMESPACE_PREFIX_CONSIDER_QUALIFICATION_ATTRIBUTE = false;
        XSD2DTDConverter.NAMESPACE_PREFIX_FORCE_USAGE = false;

        assertEquals("elementName", instance.getDTDName("{}elementName", instanceQualification));
        assertEquals("elementName", instance.getDTDName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}elementName", instanceQualification));

        XSD2DTDConverter.NAMESPACE_PREFIX_FEATURE = true;
        XSD2DTDConverter.NAMESPACE_PREFIX_CONSIDER_QUALIFICATION_ATTRIBUTE = true;
        XSD2DTDConverter.NAMESPACE_PREFIX_FORCE_USAGE = false;

        instanceQualification = Qualification.unqualified;
        assertEquals("elementName", instance.getDTDName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}elementName", instanceQualification));

        instanceQualification = Qualification.qualified;
        assertEquals("xsd:elementName", instance.getDTDName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}elementName", instanceQualification));

        instanceQualification = Qualification.unqualified;
        assertEquals("elementName", instance.getDTDName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}elementName", instanceQualification));

        XSD2DTDConverter.NAMESPACE_PREFIX_FEATURE = true;
        XSD2DTDConverter.NAMESPACE_PREFIX_CONSIDER_QUALIFICATION_ATTRIBUTE = true;
        XSD2DTDConverter.NAMESPACE_PREFIX_FORCE_USAGE = true;

        instanceQualification = Qualification.unqualified;
        assertEquals("xsd:elementName", instance.getDTDName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}elementName", instanceQualification));

        instanceQualification = Qualification.qualified;
        assertEquals("xsd:elementName", instance.getDTDName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}elementName", instanceQualification));

        instanceQualification = Qualification.unqualified;
        assertEquals("xsd:elementName", instance.getDTDName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}elementName", instanceQualification));

    }

    /**
     * Test of getNamespace method, of class DTDNameGenerator.
     */
    @Test
    public void testGetNamespace() {
        DTDNameGenerator instance = new DTDNameGenerator(schema);

        String result = instance.getNamespace("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}elementName");
        assertEquals(XSD2DTDConverter.XMLSCHEMA_NAMESPACE, result);

        result = instance.getNamespace("{}elementName");
        assertEquals("", result);
        
        result = instance.getNamespace("elementName");
        assertEquals("", result);
    }

    /**
     * Test of getLocalName method, of class DTDNameGenerator.
     */
    @Test
    public void testGetLocalName() {
        DTDNameGenerator instance = new DTDNameGenerator(schema);

        String result = instance.getLocalName("{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}elementName");
        assertEquals("elementName", result);

        result = instance.getLocalName("{}elementName");
        assertEquals("elementName", result);

        result = instance.getLocalName("elementName");
        assertEquals("elementName", result);

        result = instance.getLocalName("{namespace}");
        assertEquals("", result);
    }
}
