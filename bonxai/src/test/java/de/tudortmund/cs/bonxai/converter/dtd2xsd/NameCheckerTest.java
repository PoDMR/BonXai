package de.tudortmund.cs.bonxai.converter.dtd2xsd;

import org.junit.Test;

import de.tudortmund.cs.bonxai.xsd.tools.NameChecker;
import static org.junit.Assert.*;

/**
 * Test of abstract class NameChecker
 * @author Lars Schmidt
 */
public class NameCheckerTest extends junit.framework.TestCase {

    /**
     * Test of checkForXMLName method, of class NameChecker.
     */
    @Test
    public void testCheckForXMLName() {
        assertEquals(false, NameChecker.checkForXMLName(""));
        assertEquals(false, NameChecker.checkForXMLName(" "));
        assertEquals(false, NameChecker.checkForXMLName("-"));
        assertEquals(true, NameChecker.checkForXMLName("_"));
        assertEquals(false, NameChecker.checkForXMLName("0"));
        assertEquals(true, NameChecker.checkForXMLName("a"));
        assertEquals(true, NameChecker.checkForXMLName("A"));
        assertEquals(false, NameChecker.checkForXMLName("#"));
        assertEquals(true, NameChecker.checkForXMLName("Asdf:sdf"));
        assertEquals(false, NameChecker.checkForXMLName("sdfsdf#"));
        assertEquals(false, NameChecker.checkForXMLName("As df sdf"));
        assertEquals(false, NameChecker.checkForXMLName("sd fsd f#"));
    }

    /**
     * Test of checkForXMLNames method, of class NameChecker.
     */
    @Test
    public void testCheckForXMLNames() {
        assertEquals(false, NameChecker.checkForXMLNames(""));
        assertEquals(false, NameChecker.checkForXMLNames(" "));
        assertEquals(false, NameChecker.checkForXMLNames("-"));
        assertEquals(true, NameChecker.checkForXMLNames("_"));
        assertEquals(false, NameChecker.checkForXMLNames("0"));
        assertEquals(true, NameChecker.checkForXMLNames("a"));
        assertEquals(true, NameChecker.checkForXMLNames("A"));
        assertEquals(false, NameChecker.checkForXMLNames("#"));
        assertEquals(true, NameChecker.checkForXMLNames("Asdf:sdf"));
        assertEquals(false, NameChecker.checkForXMLNames("sdfsdf#"));
        assertEquals(true, NameChecker.checkForXMLNames("As df sdf"));
        assertEquals(false, NameChecker.checkForXMLNames("sd fsd f#"));
    }

    /**
     * Test of checkForXMLNmtoken method, of class NameChecker.
     */
    @Test
    public void testCheckForXMLNmtoken() {
        assertEquals(false, NameChecker.checkForXMLNmtoken(""));
        assertEquals(false, NameChecker.checkForXMLNmtoken(" "));
        assertEquals(true, NameChecker.checkForXMLNmtoken("-"));
        assertEquals(true, NameChecker.checkForXMLNmtoken("_"));
        assertEquals(true, NameChecker.checkForXMLNmtoken("0"));
        assertEquals(true, NameChecker.checkForXMLNmtoken("a"));
        assertEquals(true, NameChecker.checkForXMLNmtoken("A"));
        assertEquals(false, NameChecker.checkForXMLNmtoken("#"));
        assertEquals(true, NameChecker.checkForXMLNmtoken("Asdf:sdf"));
        assertEquals(false, NameChecker.checkForXMLNmtoken("sdfsdf#"));
        assertEquals(false, NameChecker.checkForXMLNmtoken("As df sdf"));
        assertEquals(false, NameChecker.checkForXMLNmtoken("sd fsd f#"));
    }

    /**
     * Test of checkForXMLNmtokens method, of class NameChecker.
     */
    @Test
    public void testCheckForXMLNmtokens() {
        assertEquals(false, NameChecker.checkForXMLNmtokens(""));
        assertEquals(false, NameChecker.checkForXMLNmtokens(" "));
        assertEquals(true, NameChecker.checkForXMLNmtokens("-"));
        assertEquals(true, NameChecker.checkForXMLNmtokens("_"));
        assertEquals(true, NameChecker.checkForXMLNmtokens("0"));
        assertEquals(true, NameChecker.checkForXMLNmtokens("a"));
        assertEquals(true, NameChecker.checkForXMLNmtokens("A"));
        assertEquals(false, NameChecker.checkForXMLNmtokens("#"));
        assertEquals(true, NameChecker.checkForXMLNmtokens("Asdfsdf"));
        assertEquals(false, NameChecker.checkForXMLNmtokens("sdfsdf#"));
        assertEquals(true, NameChecker.checkForXMLNmtokens("As df sdf"));
        assertEquals(false, NameChecker.checkForXMLNmtokens("sd fsd f#"));
    }

    /**
     * Test of isName method, of class NameChecker.
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
     * Test of isNCName method, of class NameChecker.
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
     * Test of isQName method, of class NameChecker.
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
     * Test of isAnyUri method, of class NameChecker.
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