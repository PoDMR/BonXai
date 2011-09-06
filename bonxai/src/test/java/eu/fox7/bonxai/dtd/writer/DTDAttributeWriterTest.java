package eu.fox7.bonxai.dtd.writer;

import eu.fox7.bonxai.dtd.Attribute;
import eu.fox7.bonxai.dtd.AttributeType;
import eu.fox7.bonxai.dtd.common.exceptions.AttributeEnumerationTypeIllegalDefaultValueException;
import eu.fox7.bonxai.dtd.writer.DTDAttributeWriter;
import eu.fox7.bonxai.dtd.writer.exceptions.DTDAttributeNameEmptyException;
import eu.fox7.bonxai.dtd.writer.exceptions.DTDAttributeNullException;
import eu.fox7.bonxai.dtd.writer.exceptions.DTDAttributeTypeEnumOrNotationWithEmptyTokensException;
import eu.fox7.bonxai.dtd.writer.exceptions.DTDAttributeTypeNullException;
import eu.fox7.bonxai.dtd.writer.exceptions.DTDAttributeValueEmptyException;

import java.util.LinkedHashSet;
import org.junit.Test;

/**
 * Test of class DTDAttributeWriter
 * @author Lars Schmidt
 */
public class DTDAttributeWriterTest extends junit.framework.TestCase {

    /**
     * Test of getAttributeDeclarationString method, of class DTDAttributeWriter.
     * @throws Exception 
     */
    @Test
    public void testWriteAttributeDeclaration() throws Exception {

        Attribute attribute = new Attribute("myAttribute", "one");
        attribute.setType(AttributeType.CDATA);

        DTDAttributeWriter attributeWriter = new DTDAttributeWriter(attribute);
        String expResult = "<!ATTLIST myElement myAttribute CDATA \"one\">\n";
        String result = attributeWriter.getAttributeDeclarationString("myElement");
        assertEquals(expResult, result);

        attribute = new Attribute("myAttribute", "one");
        attribute.setType(AttributeType.ENTITIES);
        attributeWriter = new DTDAttributeWriter(attribute);
        expResult = "<!ATTLIST myElement myAttribute ENTITIES \"one\">\n";
        result = attributeWriter.getAttributeDeclarationString("myElement");
        assertEquals(expResult, result);

        attribute = new Attribute("myAttribute", "one");
        attribute.setType(AttributeType.ENTITY);
        attributeWriter = new DTDAttributeWriter(attribute);
        expResult = "<!ATTLIST myElement myAttribute ENTITY \"one\">\n";
        result = attributeWriter.getAttributeDeclarationString("myElement");
        assertEquals(expResult, result);

        Attribute attribute2 = new Attribute("myAttribute2", "foo");
        attribute2.setType(AttributeType.ENUMERATION);
        LinkedHashSet<String> tokens = new LinkedHashSet<String>();
        tokens.add("test");
        tokens.add("foo");
        attribute2.setEnumerationOrNotationTokens(tokens);
        DTDAttributeWriter attributeWriter2 = new DTDAttributeWriter(attribute2);
        expResult = "<!ATTLIST myElement myAttribute2 (test|foo) \"foo\">\n";
        result = attributeWriter2.getAttributeDeclarationString("myElement");
        assertEquals(expResult, result);

        attribute = new Attribute("myAttribute", "one");
        attribute.setType(AttributeType.ID);
        attributeWriter = new DTDAttributeWriter(attribute);
        expResult = "<!ATTLIST myElement myAttribute ID \"one\">\n";
        result = attributeWriter.getAttributeDeclarationString("myElement");
        assertEquals(expResult, result);

        attribute = new Attribute("myAttribute", "one");
        attribute.setType(AttributeType.IDREF);
        attributeWriter = new DTDAttributeWriter(attribute);
        expResult = "<!ATTLIST myElement myAttribute IDREF \"one\">\n";
        result = attributeWriter.getAttributeDeclarationString("myElement");
        assertEquals(expResult, result);

        attribute = new Attribute("myAttribute", "one");
        attribute.setType(AttributeType.IDREFS);
        attributeWriter = new DTDAttributeWriter(attribute);
        expResult = "<!ATTLIST myElement myAttribute IDREFS \"one\">\n";
        result = attributeWriter.getAttributeDeclarationString("myElement");
        assertEquals(expResult, result);

        attribute = new Attribute("myAttribute", "one");
        attribute.setType(AttributeType.NMTOKEN);
        attributeWriter = new DTDAttributeWriter(attribute);
        expResult = "<!ATTLIST myElement myAttribute NMTOKEN \"one\">\n";
        result = attributeWriter.getAttributeDeclarationString("myElement");
        assertEquals(expResult, result);

        attribute = new Attribute("myAttribute", "one");
        attribute.setType(AttributeType.NMTOKENS);
        attributeWriter = new DTDAttributeWriter(attribute);
        expResult = "<!ATTLIST myElement myAttribute NMTOKENS \"one\">\n";
        result = attributeWriter.getAttributeDeclarationString("myElement");
        assertEquals(expResult, result);

        Attribute attribute3 = new Attribute("myAttribute3", "foo");
        attribute3.setType(AttributeType.NOTATION);
        LinkedHashSet<String> tokens2 = new LinkedHashSet<String>();
        tokens2.add("test");
        tokens2.add("foo");
        attribute3.setEnumerationOrNotationTokens(tokens);
        DTDAttributeWriter attributeWriter3 = new DTDAttributeWriter(attribute3);
        expResult = "<!ATTLIST myElement myAttribute3 NOTATION (test|foo) \"foo\">\n";
        result = attributeWriter3.getAttributeDeclarationString("myElement");
        assertEquals(expResult, result);
    }

    /**
     * Test of getAttributeDeclarationString method with presence value, of class DTDAttributeWriter.
     * @throws Exception
     */
    @Test
    public void testWriteAttributeDeclarationPresence() throws Exception {

        Attribute attribute = new Attribute("myAttribute", "#FIXED", "one");
        attribute.setType(AttributeType.CDATA);

        DTDAttributeWriter attributeWriter = new DTDAttributeWriter(attribute);
        String expResult = "<!ATTLIST myElement myAttribute CDATA #FIXED \"one\">\n";
        String result = attributeWriter.getAttributeDeclarationString("myElement");
        assertEquals(expResult, result);

        attribute = new Attribute("myAttribute", "#IMPLIED", "one");
        attribute.setType(AttributeType.ENTITIES);
        attributeWriter = new DTDAttributeWriter(attribute);
        expResult = "<!ATTLIST myElement myAttribute ENTITIES #IMPLIED>\n";
        result = attributeWriter.getAttributeDeclarationString("myElement");
        assertEquals(expResult, result);

        attribute = new Attribute("myAttribute", "#REQUIRED", "one");
        attribute.setType(AttributeType.ID);
        attributeWriter = new DTDAttributeWriter(attribute);
        expResult = "<!ATTLIST myElement myAttribute ID #REQUIRED>\n";
        result = attributeWriter.getAttributeDeclarationString("myElement");
        assertEquals(expResult, result);
    }

    /**
     * Test of getAttributeDeclarationString method with DTDAttributeNameEmptyException, of class DTDAttributeWriter.
     * @throws Exception
     */
    @Test
    public void testDTDAttributeNameEmptyException() throws Exception {
        try {
            Attribute attribute = new Attribute("", "one");
            attribute.setType(AttributeType.CDATA);

            DTDAttributeWriter attributeWriter = new DTDAttributeWriter(attribute);
            attributeWriter.getAttributeDeclarationString("myElement");

        } catch (DTDAttributeNameEmptyException ex) {
            return;
        }
        fail("The name of the attribute is empty, but this wasn't detected");
    }

    /**
     * Test of getAttributeDeclarationString method with DTDAttributeNullException, of class DTDAttributeWriter.
     * @throws Exception
     */
    @Test
    public void testDTDAttributeNullException() throws Exception {
        try {
            Attribute attribute = null;
            DTDAttributeWriter attributeWriter = new DTDAttributeWriter(attribute);
            attributeWriter.getAttributeDeclarationString("myElement");

        } catch (DTDAttributeNullException ex) {
            return;
        }
        fail("The attribute is null, but this wasn't detected");
    }

    /**
     * Test of getAttributeDeclarationString method with DTDAttributeTypeNullException, of class DTDAttributeWriter.
     * @throws Exception
     */
    @Test
    public void testDTDAttributeTypeNullException() throws Exception {
        try {
            Attribute attribute = new Attribute("test", "one");
            DTDAttributeWriter attributeWriter = new DTDAttributeWriter(attribute);
            attributeWriter.getAttributeDeclarationString("myElement");

        } catch (DTDAttributeTypeNullException ex) {
            return;
        }
        fail("The attributeType is null, but this wasn't detected");
    }

    /**
     * Test of getAttributeDeclarationString method with DTDAttributeTypeEnumOrNotationWithEmptyTokensException, of class DTDAttributeWriter.
     * @throws Exception
     */
    @Test
    public void testDTDAttributeTypeEnumOrNotationWithEmptyTokensException() throws Exception {
        try {
            Attribute attribute2 = new Attribute("myAttribute2", "foo");
            attribute2.setType(AttributeType.ENUMERATION);
            LinkedHashSet<String> tokens = new LinkedHashSet<String>();
            attribute2.setEnumerationOrNotationTokens(tokens);
            DTDAttributeWriter attributeWriter2 = new DTDAttributeWriter(attribute2);
            attributeWriter2.getAttributeDeclarationString("myElement");
        } catch (DTDAttributeTypeEnumOrNotationWithEmptyTokensException ex) {
            return;
        }
        fail("The enumeration or notation tokens are empty, but this wasn't detected");
    }

    /**
     * Test of getAttributeDeclarationString method with AttributeEnumerationTypeIllegalDefaultValueException, of class DTDAttributeWriter.
     * @throws Exception
     */
    @Test
    public void testAttributeEnumerationTypeIllegalDefaultValueException() throws Exception {
        try {
            Attribute attribute2 = new Attribute("myAttribute2", "hello");
            attribute2.setType(AttributeType.ENUMERATION);
            LinkedHashSet<String> tokens = new LinkedHashSet<String>();
            tokens.add("test");
            tokens.add("foo");
            attribute2.setEnumerationOrNotationTokens(tokens);
            DTDAttributeWriter attributeWriter2 = new DTDAttributeWriter(attribute2);
            attributeWriter2.getAttributeDeclarationString("myElement");

        } catch (AttributeEnumerationTypeIllegalDefaultValueException ex) {
            return;
        }
        fail("The default value is not part of the given enumeration, but this wasn't detected");
    }
    
    /**
     * DTDAttributeUnkownAttributeDefaultPresenceException
     */

    /**
     * Test of getAttributeDeclarationString method with presence value and DTDAttributeValueEmptyException, of class DTDAttributeWriter.
     * @throws Exception
     */
    @Test
    public void testDTDAttributeValueEmptyExceptionWithPresence() throws Exception {
        try {
        Attribute attribute = new Attribute("myAttribute", "#FIXED", "");
        attribute.setType(AttributeType.CDATA);

        DTDAttributeWriter attributeWriter = new DTDAttributeWriter(attribute);
        attributeWriter.getAttributeDeclarationString("myElement");


        } catch (DTDAttributeValueEmptyException ex) {
            return;
        }
        fail("The default value is null or empty while the presence is #FIXED, but this wasn't detected");
    }

    /**
     * Test of getAttributeDeclarationString method with DTDAttributeValueEmptyException, of class DTDAttributeWriter.
     * @throws Exception
     */
    @Test
    public void testDTDAttributeValueEmptyException() throws Exception {
        try {
        Attribute attribute = new Attribute("myAttribute", null);
        attribute.setType(AttributeType.CDATA);

        DTDAttributeWriter attributeWriter = new DTDAttributeWriter(attribute);
        attributeWriter.getAttributeDeclarationString("myElement");


        } catch (DTDAttributeValueEmptyException ex) {
            return;
        }
        fail("The default value is null, but this wasn't detected");
    }
}
