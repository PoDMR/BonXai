package eu.fox7.bonxai.converter.xsd2dtd;

import eu.fox7.bonxai.converter.xsd2dtd.ConstraintHandler;
import eu.fox7.bonxai.converter.xsd2dtd.ElementWrapper;
import eu.fox7.bonxai.converter.xsd2dtd.XSD2DTDConverter;
import eu.fox7.bonxai.dtd.AttributeType;
import eu.fox7.schematoolkit.xsd.om.Key;
import eu.fox7.schematoolkit.xsd.om.KeyRef;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class ConstraintHandler
 * @author Lars Schmidt
 */
public class ConstraintHandlerTest extends junit.framework.TestCase {

    // Schema for this testcase
    XSDSchema schema;

    @Before
    @Override
    public void setUp() throws Exception {
        schema = new XSDSchema(XSD2DTDConverter.XMLSCHEMA_NAMESPACE);
    }

    /**
     * Test of manageElementConstraints method, of class ConstraintHandler.
     * @throws Exception 
     */
    @Test
    public void testManageElementConstraints() throws Exception {

        Key key = new Key("{}idKeyName", ".//elementOne");
        key.addField("@id");

        KeyRef keyRef = new KeyRef("{}idrefKeyRefName", ".//elementOne", null);
        keyRef.addField("idref");

        eu.fox7.bonxai.dtd.Attribute dtdAttribute = new eu.fox7.bonxai.dtd.Attribute("id", null);
        dtdAttribute.setType(AttributeType.ENTITY);

        eu.fox7.bonxai.dtd.Attribute dtdAttribute2 = new eu.fox7.bonxai.dtd.Attribute("idref", null);
        dtdAttribute2.setType(AttributeType.NMTOKEN);

        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDAttribute(dtdAttribute);
        elementWrapper.addDTDAttribute(dtdAttribute2);
        elementWrapper.addXSDConstraint(key);
        elementWrapper.addXSDConstraint(keyRef);

        ConstraintHandler instance = new ConstraintHandler(elementWrapper);
        instance.manageElementConstraints();

        eu.fox7.bonxai.dtd.Attribute resultDTDAttribute = elementWrapper.getDTDAttributeMap().get("id").iterator().next();
        assertEquals("id", resultDTDAttribute.getName());
        assertEquals(AttributeType.ID, resultDTDAttribute.getType());

        eu.fox7.bonxai.dtd.Attribute resultDTDAttribute2 = elementWrapper.getDTDAttributeMap().get("idref").iterator().next();
        assertEquals("idref", resultDTDAttribute2.getName());
        assertEquals(AttributeType.IDREF, resultDTDAttribute2.getType());

    }

    /**
     * Test of manageConstraints method, of class ConstraintHandler.
     * @throws Exception 
     */
    @Test
    public void testManageConstraints() throws Exception {

        Key key = new Key("{}idKeyName", ".//elementOne");
        key.addField("@id");

        KeyRef keyRef = new KeyRef("{}idrefKeyRefName", ".//elementOne", null);
        keyRef.addField("idref");

        eu.fox7.bonxai.dtd.Attribute dtdAttribute = new eu.fox7.bonxai.dtd.Attribute("id", null);
        dtdAttribute.setType(AttributeType.ENTITY);

        eu.fox7.bonxai.dtd.Attribute dtdAttribute2 = new eu.fox7.bonxai.dtd.Attribute("idref", null);
        dtdAttribute2.setType(AttributeType.NMTOKEN);

        ElementWrapper elementWrapper = new ElementWrapper(schema, "elementOne");
        elementWrapper.addDTDAttribute(dtdAttribute);
        elementWrapper.addDTDAttribute(dtdAttribute2);
        elementWrapper.addXSDConstraint(key);
        elementWrapper.addXSDConstraint(keyRef);

        ConstraintHandler instance = new ConstraintHandler(elementWrapper);
        instance.manageConstraints(elementWrapper.getXsdContraints());

        eu.fox7.bonxai.dtd.Attribute resultDTDAttribute = elementWrapper.getDTDAttributeMap().get("id").iterator().next();
        assertEquals("id", resultDTDAttribute.getName());
        assertEquals(AttributeType.ID, resultDTDAttribute.getType());

        eu.fox7.bonxai.dtd.Attribute resultDTDAttribute2 = elementWrapper.getDTDAttributeMap().get("idref").iterator().next();
        assertEquals("idref", resultDTDAttribute2.getName());
        assertEquals(AttributeType.IDREF, resultDTDAttribute2.getType());
    }
}
