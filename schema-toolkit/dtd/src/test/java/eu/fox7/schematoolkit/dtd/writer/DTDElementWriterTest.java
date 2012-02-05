package eu.fox7.schematoolkit.dtd.writer;

import eu.fox7.schematoolkit.common.AnyPattern;
import eu.fox7.schematoolkit.common.ProcessContentsInstruction;
import eu.fox7.schematoolkit.dtd.om.Attribute;
import eu.fox7.schematoolkit.dtd.om.AttributeType;
import eu.fox7.schematoolkit.dtd.om.Element;
import eu.fox7.schematoolkit.dtd.writer.DTDElementWriter;
import eu.fox7.schematoolkit.dtd.writer.exceptions.DTDElementNameEmptyException;

import org.junit.Test;

/**
 * Test of class DTDElementWriter
 * @author Lars Schmidt
 */
public class DTDElementWriterTest extends junit.framework.TestCase {

    /**
     * Test of getElementDeclarationString method, of class DTDElementWriter.
     * @throws Exception
     */
    @Test
    public void testGetElementDeclarationString() throws Exception {

        /**
         * The handling and generating of more complex regular expressions for
         * content model takes place in class ElementContentModelProcessor and
         * is tested by the corresponding test class.
         */
        Element element = new Element("myElement");
        DTDElementWriter elementWriter = new DTDElementWriter(element);
        String expResult = "<!ELEMENT myElement EMPTY>\n";
        String result = elementWriter.getElementDeclarationString();
        assertEquals(expResult, result);

        Element element2 = new Element("myElement");
        element2.setParticle(new AnyPattern(ProcessContentsInstruction.Strict, null));
        DTDElementWriter elementWriter2 = new DTDElementWriter(element2);
        String expResult2 = "<!ELEMENT myElement ANY>\n";
        String result2 = elementWriter2.getElementDeclarationString();
        assertEquals(expResult2, result2);

        Element element3 = new Element("myElement");
        element3.setMixedStar(true);
        DTDElementWriter elementWriter3 = new DTDElementWriter(element3);
        String expResult3 = "<!ELEMENT myElement (#PCDATA)*>\n";
        String result3 = elementWriter3.getElementDeclarationString();
        assertEquals(expResult3, result3);

    }

    /**
     * Test of getElementAttachedAttributesString method, of class DTDElementWriter.
     * @throws Exception
     */
    @Test
    public void testGetElementAttachedAttributesString() throws Exception {
        Element element = new Element("myElement");
        Attribute attribute1 = new Attribute("test1", "value1");
        attribute1.setType(AttributeType.ENTITY);
        element.addAttribute(attribute1);
        Attribute attribute2 = new Attribute("test2", "value2");
        attribute2.setType(AttributeType.NMTOKEN);
        element.addAttribute(attribute2);
        DTDElementWriter elementWriter = new DTDElementWriter(element);
        String expResult = "<!ATTLIST myElement test1 ENTITY \"value1\">\n<!ATTLIST myElement test2 NMTOKEN \"value2\">\n";
        String result = elementWriter.getElementAttachedAttributesString();
        assertEquals(expResult, result);
    }

    /**
     * Test of testGetElementDeclarationString method with DTDAttributeValueEmptyException, of class DTDElementWriter.
     * @throws Exception
     */
    @Test
    public void testDTDElementNameEmptyExceptionEmpty() throws Exception {
        try {
            Element element = new Element("");
            DTDElementWriter elementWriter = new DTDElementWriter(element);
            elementWriter.getElementDeclarationString();
            
        } catch (DTDElementNameEmptyException ex) {
            return;
        }
        fail("The name of the element is empty, but this wasn't detected");
    }

    /**
     * Test of testGetElementDeclarationString method with DTDAttributeValueEmptyException, of class DTDElementWriter.
     * @throws Exception
     */
    @Test
    public void testDTDElementNameEmptyExceptionNull() throws Exception {
        try {
            Element element = new Element(null);
            DTDElementWriter elementWriter = new DTDElementWriter(element);
            elementWriter.getElementDeclarationString();

        } catch (DTDElementNameEmptyException ex) {
            return;
        }
        fail("The name of the element is null, but this wasn't detected");
    }
}
