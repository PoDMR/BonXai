package eu.fox7.bonxai.relaxng.writer;

import com.sun.org.apache.xerces.internal.dom.DocumentImpl;

import eu.fox7.bonxai.common.DefaultNamespace;
import eu.fox7.bonxai.common.IdentifiedNamespace;
import eu.fox7.bonxai.common.NamespaceList;
import eu.fox7.bonxai.relaxng.RelaxNGSchema;
import eu.fox7.bonxai.relaxng.Text;
import eu.fox7.bonxai.relaxng.writer.RNGWriterBase;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Element;

/**
 * Test of class RNGWriterBase
 * @author Lars Schmidt
 */
public class RNGWriterBaseTest extends junit.framework.TestCase {

    // DOM Document for this testcase
    protected org.w3c.dom.Document rngDocument;

    @Before
    @Override
    public void setUp() {
        rngDocument = new DocumentImpl();
    }

    /**
     * Test of getPrefixForRNGNamespace method, of class RNGWriterBase.
     */
    @Test
    public void testGetPrefixForRNGNamespace() {
        NamespaceList rootElementNamespaceList = new NamespaceList(new DefaultNamespace(RelaxNGSchema.RELAXNG_NAMESPACE));
        RNGWriterBase instance = new RNGWriterBase(rngDocument, rootElementNamespaceList) {
        };
        String expResult = "";
        String result = instance.getPrefixForRNGNamespace();
        assertEquals(expResult, result);

        rootElementNamespaceList.addIdentifiedNamespace(new IdentifiedNamespace("rng", RelaxNGSchema.RELAXNG_NAMESPACE));
        String expResult2 = "rng:";
        String result2 = instance.getPrefixForRNGNamespace();
        assertEquals(expResult2, result2);
    }

    /**
     * Test of createElementNode method, of class RNGWriterBase.
     */
    @Test
    public void testCreateElementNode() {
        NamespaceList rootElementNamespaceList = new NamespaceList(new DefaultNamespace(RelaxNGSchema.RELAXNG_NAMESPACE));
        RNGWriterBase instance = new RNGWriterBase(rngDocument, rootElementNamespaceList) {
        };
        String name = "testNodeName";
        Element resultDOMElement = instance.createElementNode(name);
        assertEquals(name, resultDOMElement.getLocalName());
        assertEquals(0, resultDOMElement.getAttributes().getLength());
    }

    /**
     * Test of setPatternAttributes method, of class RNGWriterBase.
     */
    @Test
    public void testSetPatternAttributes() {
        NamespaceList rootElementNamespaceList = new NamespaceList(new DefaultNamespace(RelaxNGSchema.RELAXNG_NAMESPACE));
        RNGWriterBase instance = new RNGWriterBase(rngDocument, rootElementNamespaceList) {
        };
        String name = "testNodeName";
        Element resultDOMElement = instance.createElementNode(name);
        assertEquals(name, resultDOMElement.getLocalName());
        assertEquals(0, resultDOMElement.getAttributes().getLength());
        Text textPattern = new Text();

        instance.setPatternAttributes(resultDOMElement, textPattern);
        assertEquals(0, resultDOMElement.getAttributes().getLength());

        textPattern.setAttributeDatatypeLibrary("dataTypeLib");
        textPattern.setAttributeNamespace("namespace");
        textPattern.setDefaultNamespace("xmlnsNamespace");

        instance.setPatternAttributes(resultDOMElement, textPattern);
        assertEquals(3, resultDOMElement.getAttributes().getLength());
        assertEquals("dataTypeLib", resultDOMElement.getAttribute("datatypeLibrary"));
        assertEquals("namespace", resultDOMElement.getAttribute("ns"));
        assertEquals("xmlnsNamespace", resultDOMElement.getAttribute("xmlns"));
    }

    /**
     * Test of isName method, of class RNGWriterBase.
     */
    @Test
    public void testIsName() {
        NamespaceList rootElementNamespaceList = new NamespaceList(new DefaultNamespace(RelaxNGSchema.RELAXNG_NAMESPACE));
        RNGWriterBase instance = new RNGWriterBase(rngDocument, rootElementNamespaceList) {
        };
        /*
         * NameChar ::= Letter | Digit  | '.' | '-' | '_' | ':' | CombiningChar | Extender
         * Name     ::= (Letter | '_' | ':') ( NameChar)*
         */

        // RegExp: "[a-zA-Z\\_\\:][0-9a-zA-Z\\. \\-\\:\\_]*"
        assertFalse(instance.isName(""));
        assertFalse(instance.isName(" "));
        assertFalse(instance.isName("8"));
        assertTrue(instance.isName("MyName"));
        assertTrue(instance.isName("My Name"));
        assertTrue(instance.isName("My_Name"));
        assertTrue(instance.isName("My-Name"));
        assertFalse(instance.isName("1MyName"));
        assertTrue(instance.isName("::::"));
        assertFalse(instance.isName("@MyName"));
        assertTrue(instance.isName("My:Name"));
        assertFalse(instance.isName("http://www.myDomain.com"));
        assertFalse(instance.isName("http://www.myDomain.com/"));
        assertFalse(instance.isName("http://www.myDomain.com////"));
        assertFalse(instance.isName("http://///w\\\\ww.myDomain.com////"));
    }

    /**
     * Test of isNCName method, of class RNGWriterBase.
     */
    @Test
    public void testIsNCName() {
        NamespaceList rootElementNamespaceList = new NamespaceList(new DefaultNamespace(RelaxNGSchema.RELAXNG_NAMESPACE));
        RNGWriterBase instance = new RNGWriterBase(rngDocument, rootElementNamespaceList) {
        };
        /*
         * NCName       ::= (Letter | '_') (NCNameChar)*
         * NCNameChar 	::= Letter | Digit | '.' | '-' | '_' | CombiningChar | Extender
         */

        // An XML Name, minus the ":"
        assertFalse(instance.isNCName(""));
        assertFalse(instance.isNCName(" "));
        assertFalse(instance.isNCName("8"));
        assertTrue(instance.isNCName("MyName"));
        assertTrue(instance.isNCName("My Name"));
        assertTrue(instance.isNCName("My_Name"));
        assertTrue(instance.isNCName("My-Name"));
        assertFalse(instance.isNCName("1MyName"));
        assertFalse(instance.isNCName("::::"));
        assertFalse(instance.isNCName("@MyName"));
        assertFalse(instance.isNCName("My:Name"));
        assertFalse(instance.isNCName("http://www.myDomain.com"));
        assertFalse(instance.isNCName("http://www.myDomain.com/"));
        assertFalse(instance.isNCName("http://www.myDomain.com////"));
        assertFalse(instance.isNCName("http://///w\\\\ww.myDomain.com////"));
    }

    /**
     * Test of isQName method, of class RNGWriterBase.
     */
    @Test
    public void testIsQName() {
        NamespaceList rootElementNamespaceList = new NamespaceList(new DefaultNamespace(RelaxNGSchema.RELAXNG_NAMESPACE));
        RNGWriterBase instance = new RNGWriterBase(rngDocument, rootElementNamespaceList) {
        };
        /*
         * QName represents XML qualified names. The value space of QName is
         * the set of tuples {namespace name, local part}, where namespace name
         * is an anyURI and local part is an NCName.
         */
        assertFalse(instance.isQName(""));
        assertFalse(instance.isQName(" "));
        assertFalse(instance.isQName("8"));
        assertTrue(instance.isQName("MyName"));
        assertTrue(instance.isQName("My Name"));
        assertTrue(instance.isQName("My_Name"));
        assertTrue(instance.isQName("My-Name"));
        assertFalse(instance.isQName("1MyName"));
        assertFalse(instance.isQName("::::"));
        assertFalse(instance.isQName("@MyName"));
        assertTrue(instance.isQName("My:Name"));
        assertFalse(instance.isQName("http://www.myDomain.com"));
        assertFalse(instance.isQName("http://www.myDomain.com/"));
        assertFalse(instance.isQName("http://www.myDomain.com////"));
        assertFalse(instance.isQName("http://///w\\\\ww.myDomain.com////"));
    }

    /**
     * Test of isAnyUri method, of class RNGWriterBase.
     */
    @Test
    public void testIsAnyUri() {
        NamespaceList rootElementNamespaceList = new NamespaceList(new DefaultNamespace(RelaxNGSchema.RELAXNG_NAMESPACE));
        RNGWriterBase instance = new RNGWriterBase(rngDocument, rootElementNamespaceList) {
        };
        /*
         * anyURI represents a Uniform Resource Identifier Reference (URI).
         * An anyURI value can be absolute or relative, and may have an optional
         * fragment identifier (i.e., it may be a URI Reference).
         * This type should be used to specify the intention that the value
         * fulfills the role of a URI as defined by [RFC 2396], as amended by
         * [RFC 2732].
         */

        // RegExp: "(([a-zA-Z][0-9a-zA-Z+\\-\\.]*:)?/{0,2}[0-9a-zA-Z;/?:@&=+$\\.\\-_!~*'()%]+)?(#[0-9a-zA-Z;/?:@&=+$\\.\\-_!~*'()%]+)?"

        assertTrue(instance.isAnyUri(""));
        assertFalse(instance.isAnyUri(" "));
        assertTrue(instance.isAnyUri("8"));
        assertTrue(instance.isAnyUri("MyName"));
        assertFalse(instance.isAnyUri("My Name"));
        assertTrue(instance.isAnyUri("My_Name"));
        assertTrue(instance.isAnyUri("My-Name"));
        assertTrue(instance.isAnyUri("1MyName"));
        assertTrue(instance.isAnyUri("::::"));
        assertTrue(instance.isAnyUri("@MyName"));
        assertTrue(instance.isAnyUri("My:Name"));
        assertTrue(instance.isAnyUri("http://www.myDomain.com"));
        assertTrue(instance.isAnyUri("http://www.myDomain.com/"));
        assertTrue(instance.isAnyUri("http://www.myDomain.com////"));
        assertFalse(instance.isAnyUri("http://///w\\\\ww.myDomain.com////"));
    }
}
