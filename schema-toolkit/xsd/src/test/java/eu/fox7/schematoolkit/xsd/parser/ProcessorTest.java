package eu.fox7.schematoolkit.xsd.parser;

import eu.fox7.schematoolkit.xsd.om.XSDSchema;
import eu.fox7.schematoolkit.xsd.parser.Processor;
import eu.fox7.schematoolkit.xsd.parser.SchemaProcessor;
import eu.fox7.schematoolkit.xsd.parser.exceptions.UnknownNamespaceException;
import eu.fox7.schematoolkit.xsd.tools.NameChecker;

import java.util.HashSet;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class ProcessorTest extends junit.framework.TestCase {

    public ProcessorTest() {
    }

    // Schema for this testcase
    XSDSchema schema;

    // SchemaProcessor used by the testcase
    SchemaProcessor schemaProcessor;

    /**
     * Befor every test the schema and schemaProcessor are refreshed
     */
    @Before
    @Override
    public void setUp() {
        schema = new XSDSchema();
        schemaProcessor = new SchemaProcessor(schema);
    }

    /**
     * Test of visitChildren method, of class Processor.
     */
    @Test
    public void testVisitChildren() throws Exception {
        Node node = null;
        try {
            node = Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/processorTests/processor_visit.xsd");
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        schemaProcessor.visitChildren(node);
        assertEquals(6, schema.getTypes().size());
    }

    /**
     * Test of setDebug method, of class Processor.
     */
    @Test
    public void testSetDebug() {
        assertEquals(false, Processor.getDebug());
        Processor.setDebug(true);
        assertEquals(true, Processor.getDebug());
        Processor.setDebug(false);
        assertEquals(false, Processor.getDebug());
    }

    /**
     * Test of getDebug method, of class Processor.
     */
    @Test
    public void testGetDebug() {
        assertEquals(false, Processor.getDebug());
        Processor.setDebug(true);
        assertEquals(true, Processor.getDebug());
        Processor.setDebug(false);
        assertEquals(false, Processor.getDebug());
    }

    /**
     * Test of getName method, of class Processor.
     */
    @Test
    public void testGetName_Node_targetNamespace() {
        Node node = null;
        Node newNode = null;
        try {
            node = Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/processorTests/processor_getname_node.xsd");
            schema = schemaProcessor.processNode(node);
            newNode = node.getChildNodes().item(1);
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        
        try {
			assertEquals("{http://www.w3.org/2001/XMLSchema}complexTypeOne", schemaProcessor.getName(newNode));
		} catch (UnknownNamespaceException e) {
            fail("Exception was thrown: " + e);
		} catch (DOMException e) {
            fail("Exception was thrown: " + e);
		}
    }


    /**
     * Test of generateUniqueName method, of class Processor.
     */
    @Test
    public void testGenerateUniqueName() {
        Node node = null;
        Node newNode = null;
        try {
            node = Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/processorTests/processor_getname_node.xsd");
            schema = schemaProcessor.processNode(node);
            newNode = node.getChildNodes().item(1);
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        assertFalse(schemaProcessor.generateUniqueName(newNode).equals(""));

        // The following HashSet must not have two or more equal names in it.
        // This is a set. If there is already the same String in it, it is a failure.
        // The size of this Set has to be of the same value as the iteration count of the loop.
        HashSet<String> myNames = new HashSet<String>();
        for (int i = 0; i < 100000; i++) {
            myNames.add(schemaProcessor.generateUniqueName(newNode));
        }
        assertEquals(100000, myNames.size());
    }

    /**
     * Test of getName method, of class Processor.
     */
    @Test
    public void testGetName_String() throws Exception {
        try {
            schema = schemaProcessor.processNode(Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/processorTests/processor_getname_node.xsd"));
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }
        String xmlNameReference = "myName";
        assertEquals("{http://www.w3.org/2001/XMLSchema}myName", schemaProcessor.getName(xmlNameReference));
    }

    /**
     * Test of isName method, of class Processor.
     */
    @Test
    public void testIsName() {

        /*
         * NameChar ::= Letter | Digit  | '.' | '-' | '_' | ':' | CombiningChar | Extender
         * Name     ::= (Letter | '_' | ':') ( NameChar)*
         */

        // RegExp: "[a-zA-Z\\_\\:][0-9a-zA-Z\\. \\-\\:\\_]*"
        assertFalse(NameChecker.isName(""));
        assertFalse(NameChecker.isName(" "));
        assertFalse(NameChecker.isName("8"));
        assertTrue(NameChecker.isName("MyName"));
        assertTrue(NameChecker.isName("My Name"));
        assertTrue(NameChecker.isName("My_Name"));
        assertTrue(NameChecker.isName("My-Name"));
        assertFalse(NameChecker.isName("1MyName"));
        assertTrue(NameChecker.isName("::::"));
        assertFalse(NameChecker.isName("@MyName"));
        assertTrue(NameChecker.isName("My:Name"));
        assertFalse(NameChecker.isName("http://www.myDomain.com"));
        assertFalse(NameChecker.isName("http://www.myDomain.com/"));
        assertFalse(NameChecker.isName("http://www.myDomain.com////"));
        assertFalse(NameChecker.isName("http://///w\\\\ww.myDomain.com////"));

    }

    /**
     * Test of isNCName method, of class Processor.
     */
    @Test
    public void testIsNCName() {

        /*
         * NCName       ::= (Letter | '_') (NCNameChar)*
         * NCNameChar 	::= Letter | Digit | '.' | '-' | '_' | CombiningChar | Extender
         */

        // An XML Name, minus the ":"
        assertFalse(NameChecker.isNCName(""));
        assertFalse(NameChecker.isNCName(" "));
        assertFalse(NameChecker.isNCName("8"));
        assertTrue(NameChecker.isNCName("MyName"));
        assertTrue(NameChecker.isNCName("My Name"));
        assertTrue(NameChecker.isNCName("My_Name"));
        assertTrue(NameChecker.isNCName("My-Name"));
        assertFalse(NameChecker.isNCName("1MyName"));
        assertFalse(NameChecker.isNCName("::::"));
        assertFalse(NameChecker.isNCName("@MyName"));
        assertFalse(NameChecker.isNCName("My:Name"));
        assertFalse(NameChecker.isNCName("http://www.myDomain.com"));
        assertFalse(NameChecker.isNCName("http://www.myDomain.com/"));
        assertFalse(NameChecker.isNCName("http://www.myDomain.com////"));
        assertFalse(NameChecker.isNCName("http://///w\\\\ww.myDomain.com////"));

    }

    /**
     * Test of isQName method, of class Processor.
     */
    @Test
    public void testIsQName() {

        /*
         * QName represents XML qualified names. The value space of QName is
         * the set of tuples {namespace name, local part}, where namespace name
         * is an anyURI and local part is an NCName.
         */

        assertFalse(NameChecker.isQName(""));
        assertFalse(NameChecker.isQName(" "));
        assertFalse(NameChecker.isQName("8"));
        assertTrue(NameChecker.isQName("MyName"));
        assertTrue(NameChecker.isQName("My Name"));
        assertTrue(NameChecker.isQName("My_Name"));
        assertTrue(NameChecker.isQName("My-Name"));
        assertFalse(NameChecker.isQName("1MyName"));
        assertFalse(NameChecker.isQName("::::"));
        assertFalse(NameChecker.isQName("@MyName"));
        assertTrue(NameChecker.isQName("My:Name"));
        assertFalse(NameChecker.isQName("http://www.myDomain.com"));
        assertFalse(NameChecker.isQName("http://www.myDomain.com/"));
        assertFalse(NameChecker.isQName("http://www.myDomain.com////"));
        assertFalse(NameChecker.isQName("http://///w\\\\ww.myDomain.com////"));

    }

    /**
     * Test of isAnyUri method, of class Processor.
     */
    @Test
    public void testIsAnyUri() {
        /*
         * anyURI represents a Uniform Resource Identifier Reference (URI).
         * An anyURI value can be absolute or relative, and may have an optional
         * fragment identifier (i.e., it may be a URI Reference).
         * This type should be used to specify the intention that the value
         * fulfills the role of a URI as defined by [RFC 2396], as amended by
         * [RFC 2732].
         */

        // RegExp: "(([a-zA-Z][0-9a-zA-Z+\\-\\.]*:)?/{0,2}[0-9a-zA-Z;/?:@&=+$\\.\\-_!~*'()%]+)?(#[0-9a-zA-Z;/?:@&=+$\\.\\-_!~*'()%]+)?"

        assertTrue(NameChecker.isAnyUri(""));
        assertFalse(NameChecker.isAnyUri(" "));
        assertTrue(NameChecker.isAnyUri("8"));
        assertTrue(NameChecker.isAnyUri("MyName"));
        assertFalse(NameChecker.isAnyUri("My Name"));
        assertTrue(NameChecker.isAnyUri("My_Name"));
        assertTrue(NameChecker.isAnyUri("My-Name"));
        assertTrue(NameChecker.isAnyUri("1MyName"));
        assertTrue(NameChecker.isAnyUri("::::"));
        assertTrue(NameChecker.isAnyUri("@MyName"));
        assertTrue(NameChecker.isAnyUri("My:Name"));
        assertTrue(NameChecker.isAnyUri("http://www.myDomain.com"));
        assertTrue(NameChecker.isAnyUri("http://www.myDomain.com/"));
        assertTrue(NameChecker.isAnyUri("http://www.myDomain.com////"));
        assertFalse(NameChecker.isAnyUri("http://///w\\\\ww.myDomain.com////"));
    }
}
