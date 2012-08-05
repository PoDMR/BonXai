package eu.fox7.schematoolkit.dtd.writer;

import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.dtd.common.exceptions.AttributeEnumerationTypeIllegalDefaultValueException;
import eu.fox7.schematoolkit.dtd.om.Attribute;
import eu.fox7.schematoolkit.dtd.om.AttributeType;
import eu.fox7.schematoolkit.dtd.writer.DTDAttributeWriter;
import eu.fox7.schematoolkit.dtd.writer.exceptions.DTDAttributeNameEmptyException;
import eu.fox7.schematoolkit.dtd.writer.exceptions.DTDAttributeNullException;
import eu.fox7.schematoolkit.dtd.writer.exceptions.DTDAttributeTypeEnumOrNotationWithEmptyTokensException;
import eu.fox7.schematoolkit.dtd.writer.exceptions.DTDAttributeTypeNullException;
import eu.fox7.schematoolkit.dtd.writer.exceptions.DTDAttributeValueEmptyException;

import java.util.LinkedHashSet;
import org.junit.Test;

/**
 * Test of class DTDAttributeWriter
 * @author Lars Schmidt
 */
public class DTDAttributeWriterTest extends junit.framework.TestCase {
	private QualifiedName elementName = new QualifiedName(Namespace.EMPTY_NAMESPACE,"myElement");

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
            attributeWriter.getAttributeDeclarationString(elementName);

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
            attributeWriter.getAttributeDeclarationString(elementName);

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
            attributeWriter.getAttributeDeclarationString(elementName);

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
            attributeWriter2.getAttributeDeclarationString(elementName);
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
            attributeWriter2.getAttributeDeclarationString(elementName);

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
        attributeWriter.getAttributeDeclarationString(elementName);


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
        attributeWriter.getAttributeDeclarationString(elementName);


        } catch (DTDAttributeValueEmptyException ex) {
            return;
        }
        fail("The default value is null, but this wasn't detected");
    }
}
