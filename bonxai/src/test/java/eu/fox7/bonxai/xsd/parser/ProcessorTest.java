package eu.fox7.bonxai.xsd.parser;

import eu.fox7.bonxai.xsd.XSDSchema;
import eu.fox7.bonxai.xsd.parser.Processor;
import eu.fox7.bonxai.xsd.parser.SchemaProcessor;

import java.util.HashSet;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
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
        
        assertEquals("{http://www.w3.org/2001/XMLSchema}complexTypeOne", schemaProcessor.getName(newNode));
    }


    /**
     * Test of getLocalName method, of class Processor.
     */
    @Test
    public void testGetLocalName() {
        Node node = null;
        Node newNode = null;
        try {
            node = Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/processorTests/processor_getname_node.xsd");
            schema = schemaProcessor.processNode(node);
            newNode = node.getChildNodes().item(1);
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }

        assertEquals("complexTypeOne", schemaProcessor.getLocalName(newNode));
    }

    /**
     * Test of getNamespace method, of class Processor.
     */
    @Test
    public void testGetNamespace() {
        Node node = null;
        Node newNode = null;
        try {
            node = Utilities.getSchemaNode("tests/eu/fox7/bonxai/xsd/parser/xsds/processorTests/processor_getname_node.xsd");
            schema = schemaProcessor.processNode(node);
            newNode = node.getChildNodes().item(1);
        } catch (Exception error) {
            fail("Exception was thrown: " + error);
        }

        assertEquals("http://www.w3.org/2001/XMLSchema", schemaProcessor.getNamespace(newNode));
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
        Node node = null;
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
        assertFalse(schemaProcessor.isName(""));
        assertFalse(schemaProcessor.isName(" "));
        assertFalse(schemaProcessor.isName("8"));
        assertTrue(schemaProcessor.isName("MyName"));
        assertTrue(schemaProcessor.isName("My Name"));
        assertTrue(schemaProcessor.isName("My_Name"));
        assertTrue(schemaProcessor.isName("My-Name"));
        assertFalse(schemaProcessor.isName("1MyName"));
        assertTrue(schemaProcessor.isName("::::"));
        assertFalse(schemaProcessor.isName("@MyName"));
        assertTrue(schemaProcessor.isName("My:Name"));
        assertFalse(schemaProcessor.isName("http://www.myDomain.com"));
        assertFalse(schemaProcessor.isName("http://www.myDomain.com/"));
        assertFalse(schemaProcessor.isName("http://www.myDomain.com////"));
        assertFalse(schemaProcessor.isName("http://///w\\\\ww.myDomain.com////"));

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
        assertFalse(schemaProcessor.isNCName(""));
        assertFalse(schemaProcessor.isNCName(" "));
        assertFalse(schemaProcessor.isNCName("8"));
        assertTrue(schemaProcessor.isNCName("MyName"));
        assertTrue(schemaProcessor.isNCName("My Name"));
        assertTrue(schemaProcessor.isNCName("My_Name"));
        assertTrue(schemaProcessor.isNCName("My-Name"));
        assertFalse(schemaProcessor.isNCName("1MyName"));
        assertFalse(schemaProcessor.isNCName("::::"));
        assertFalse(schemaProcessor.isNCName("@MyName"));
        assertFalse(schemaProcessor.isNCName("My:Name"));
        assertFalse(schemaProcessor.isNCName("http://www.myDomain.com"));
        assertFalse(schemaProcessor.isNCName("http://www.myDomain.com/"));
        assertFalse(schemaProcessor.isNCName("http://www.myDomain.com////"));
        assertFalse(schemaProcessor.isNCName("http://///w\\\\ww.myDomain.com////"));

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

        assertFalse(schemaProcessor.isQName(""));
        assertFalse(schemaProcessor.isQName(" "));
        assertFalse(schemaProcessor.isQName("8"));
        assertTrue(schemaProcessor.isQName("MyName"));
        assertTrue(schemaProcessor.isQName("My Name"));
        assertTrue(schemaProcessor.isQName("My_Name"));
        assertTrue(schemaProcessor.isQName("My-Name"));
        assertFalse(schemaProcessor.isQName("1MyName"));
        assertFalse(schemaProcessor.isQName("::::"));
        assertFalse(schemaProcessor.isQName("@MyName"));
        assertTrue(schemaProcessor.isQName("My:Name"));
        assertFalse(schemaProcessor.isQName("http://www.myDomain.com"));
        assertFalse(schemaProcessor.isQName("http://www.myDomain.com/"));
        assertFalse(schemaProcessor.isQName("http://www.myDomain.com////"));
        assertFalse(schemaProcessor.isQName("http://///w\\\\ww.myDomain.com////"));

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

        assertTrue(schemaProcessor.isAnyUri(""));
        assertFalse(schemaProcessor.isAnyUri(" "));
        assertTrue(schemaProcessor.isAnyUri("8"));
        assertTrue(schemaProcessor.isAnyUri("MyName"));
        assertFalse(schemaProcessor.isAnyUri("My Name"));
        assertTrue(schemaProcessor.isAnyUri("My_Name"));
        assertTrue(schemaProcessor.isAnyUri("My-Name"));
        assertTrue(schemaProcessor.isAnyUri("1MyName"));
        assertTrue(schemaProcessor.isAnyUri("::::"));
        assertTrue(schemaProcessor.isAnyUri("@MyName"));
        assertTrue(schemaProcessor.isAnyUri("My:Name"));
        assertTrue(schemaProcessor.isAnyUri("http://www.myDomain.com"));
        assertTrue(schemaProcessor.isAnyUri("http://www.myDomain.com/"));
        assertTrue(schemaProcessor.isAnyUri("http://www.myDomain.com////"));
        assertFalse(schemaProcessor.isAnyUri("http://///w\\\\ww.myDomain.com////"));
    }
}
