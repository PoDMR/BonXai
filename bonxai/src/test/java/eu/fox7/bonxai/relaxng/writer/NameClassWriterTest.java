package eu.fox7.bonxai.relaxng.writer;

import com.sun.org.apache.xerces.internal.dom.DocumentImpl;

import eu.fox7.bonxai.common.DefaultNamespace;
import eu.fox7.bonxai.common.NamespaceList;
import eu.fox7.bonxai.relaxng.*;
import eu.fox7.bonxai.relaxng.writer.NameClassWriter;
import eu.fox7.bonxai.relaxng.writer.exceptions.InvalidQNameException;
import eu.fox7.bonxai.relaxng.writer.exceptions.UnknownNameClassException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;

/**
 * Test of class NameClassWriter
 * @author Lars Schmidt
 */
public class NameClassWriterTest extends junit.framework.TestCase {

    // DOM Document for this testcase
    private org.w3c.dom.Document rngDocument;
    private NamespaceList rootElementNamespaceList;

    @Before
    @Override
    public void setUp() {
        rngDocument = new DocumentImpl();
        rootElementNamespaceList = new NamespaceList(new DefaultNamespace(RelaxNGSchema.RELAXNG_NAMESPACE));

    }

    /**
     * Test of valid case of createNodeForNameClass method with "Name", of class NameClassWriter.
     * @throws Exception
     */
    @Test
    public void testValidCaseOfCreateNodeForNameClassWithName() throws Exception {
        Name nameClass = new Name("myName", "myNamespace");
        NameClassWriter instance = new NameClassWriter(rngDocument, rootElementNamespaceList);
        Element result = instance.createNodeForNameClass(nameClass);
        assertEquals("name", result.getLocalName());
        assertEquals("myName", result.getTextContent());
        assertEquals("myNamespace", result.getAttribute("ns"));
        assertEquals(1, result.getAttributes().getLength());
    }

    /**
     * Test of valid case of createNodeForNameClass method with "NsName", of class NameClassWriter.
     * @throws Exception
     */
    @Test
    public void testValidCaseOfCreateNodeForNameClassWithNsName() throws Exception {
        NsName nameClass = new NsName();
        nameClass.setAttributeNamespace("myNamespace");
        nameClass.addExceptName(new Name("exceptName"));
        NameClassWriter instance = new NameClassWriter(rngDocument, rootElementNamespaceList);

        Element result = instance.createNodeForNameClass(nameClass);
        assertEquals("nsName", result.getLocalName());
        assertEquals("myNamespace", result.getAttribute("ns"));
        assertEquals(1, result.getAttributes().getLength());
        assertEquals(1, result.getChildNodes().getLength());

        Element exceptChild = (Element) result.getChildNodes().item(0);
        assertEquals("except", exceptChild.getLocalName());
        assertEquals(0, exceptChild.getAttributes().getLength());
        assertEquals(1, exceptChild.getChildNodes().getLength());

        Element nameChild = (Element) exceptChild.getChildNodes().item(0);
        assertEquals("exceptName", exceptChild.getTextContent());
        assertEquals(0, nameChild.getAttributes().getLength());
        // TextContent is defined as a childNode ([#text: exceptName])
        assertEquals(1, nameChild.getChildNodes().getLength());
    }

    /**
     * Test of valid case of createNodeForNameClass method with "AnyName", of class NameClassWriter.
     * @throws Exception
     */
    @Test
    public void testValidCaseOfCreateNodeForNameClassWithAnyName() throws Exception {
        AnyName nameClass = new AnyName();
        nameClass.addExceptName(new Name("exceptAnyName"));
        NameClassWriter instance = new NameClassWriter(rngDocument, rootElementNamespaceList);

        Element result = instance.createNodeForNameClass(nameClass);
        assertEquals("anyName", result.getLocalName());
        assertEquals(0, result.getAttributes().getLength());
        assertEquals(1, result.getChildNodes().getLength());

        Element exceptChild = (Element) result.getChildNodes().item(0);
        assertEquals("except", exceptChild.getLocalName());
        assertEquals(0, exceptChild.getAttributes().getLength());
        assertEquals(1, exceptChild.getChildNodes().getLength());

        Element nameChild = (Element) exceptChild.getChildNodes().item(0);
        assertEquals("exceptAnyName", exceptChild.getTextContent());
        assertEquals(0, nameChild.getAttributes().getLength());
        // TextContent is defined as a childNode ([#text: exceptAnyName])
        assertEquals(1, nameChild.getChildNodes().getLength());
    }

    /**
     * Test of valid case of createNodeForNameClass method with "Choice", of class NameClassWriter.
     * @throws Exception
     */
    @Test
    public void testValidCaseOfCreateNodeForNameClassWithChoice() throws Exception {
        NameClassChoice nameClass = new NameClassChoice();
        nameClass.addChoiceName(new Name("myName"));
        nameClass.addChoiceName(new NsName());
        nameClass.addChoiceName(new AnyName());
        nameClass.addChoiceName(new NameClassChoice());
        NameClassWriter instance = new NameClassWriter(rngDocument, rootElementNamespaceList);

        Element result = instance.createNodeForNameClass(nameClass);
        assertEquals(0, result.getAttributes().getLength());
        assertEquals(4, result.getChildNodes().getLength());

        Element choiceNameClass0 = (Element) result.getChildNodes().item(0);
        assertEquals("name", choiceNameClass0.getLocalName());
        assertEquals(0, choiceNameClass0.getAttributes().getLength());
        // TextContent is defined as a childNode ([#text: myName])
        assertEquals(1, choiceNameClass0.getChildNodes().getLength());

        Element choiceNameClass1 = (Element) result.getChildNodes().item(1);
        assertEquals("nsName", choiceNameClass1.getLocalName());
        assertEquals(0, choiceNameClass1.getAttributes().getLength());
        assertEquals(0, choiceNameClass1.getChildNodes().getLength());

        Element choiceNameClass2 = (Element) result.getChildNodes().item(2);
        assertEquals("anyName", choiceNameClass2.getLocalName());
        assertEquals(0, choiceNameClass2.getAttributes().getLength());
        assertEquals(0, choiceNameClass2.getChildNodes().getLength());

        Element choiceNameClass3 = (Element) result.getChildNodes().item(3);
        assertEquals("choice", choiceNameClass3.getLocalName());
        assertEquals(0, choiceNameClass3.getAttributes().getLength());
        assertEquals(0, choiceNameClass3.getChildNodes().getLength());
    }

    /**
     * Test of invalid case of createNodeForNameClass method with UnknownNameClassException, of class NameClassWriter.
     * @throws Exception
     */
    @Test
    public void testInvalidCaseOfCreateNodeForNameClassWithUnknownNameClassException() throws Exception {
        try {
            NameClass nameClass = new NameClass() {
            };
            NameClassWriter instance = new NameClassWriter(rngDocument, rootElementNamespaceList);
            instance.createNodeForNameClass(nameClass);
        } catch (UnknownNameClassException e) {
            return;
        }
        fail("The type of the NameClass is not supportet, but this was not detected.");
    }

    /**
     * Test of invalid case of createNodeForNameClass method with InvalidQNameException, of class NameClassWriter.
     * @throws Exception
     */
    @Test
    public void testInvalidCaseOfCreateNodeForNameClassWithInvalidQNameException() throws Exception {
        try {
            Name nameClass = new Name("my\\{/Name", "myNamespace");
            NameClassWriter instance = new NameClassWriter(rngDocument, rootElementNamespaceList);
            instance.createNodeForNameClass(nameClass);
        } catch (InvalidQNameException e) {
            return;
        }
        fail("The name of the Name is an invalid QName, but this was not detected.");
    }
}
