package eu.fox7.schematoolkit.dtd.common;

import org.junit.Test;

import eu.fox7.schematoolkit.dtd.common.DTDNameChecker;

/**
 * Test of class DTDNameChecker
 * @author Lars Schmidt
 */
public class DTDNameCheckerTest extends junit.framework.TestCase {

    /**
     * Test of checkForXMLName method, of class DTDNameChecker.
     */
    @Test
    public void testCheckForXMLName() {
        DTDNameChecker instance = new DTDNameChecker();

        assertEquals(false, instance.checkForXMLName(""));
        assertEquals(false, instance.checkForXMLName(" "));
        assertEquals(false, instance.checkForXMLName("-"));
        assertEquals(true, instance.checkForXMLName("_"));
        assertEquals(false, instance.checkForXMLName("0"));
        assertEquals(true, instance.checkForXMLName("a"));
        assertEquals(true, instance.checkForXMLName("A"));
        assertEquals(false, instance.checkForXMLName("#"));
        assertEquals(true, instance.checkForXMLName("Asdfsdf"));
        assertEquals(false, instance.checkForXMLName("sdfsdf#"));
        assertEquals(false, instance.checkForXMLName("As df sdf"));
        assertEquals(false, instance.checkForXMLName("sd fsd f#"));
    }

    /**
     * Test of checkForXMLNames method, of class DTDNameChecker.
     */
    @Test
    public void testCheckForXMLNames() {
        DTDNameChecker instance = new DTDNameChecker();

        assertEquals(false, instance.checkForXMLNames(""));
        assertEquals(false, instance.checkForXMLNames(" "));
        assertEquals(false, instance.checkForXMLNames("-"));
        assertEquals(true, instance.checkForXMLNames("_"));
        assertEquals(false, instance.checkForXMLNames("0"));
        assertEquals(true, instance.checkForXMLNames("a"));
        assertEquals(true, instance.checkForXMLNames("A"));
        assertEquals(false, instance.checkForXMLNames("#"));
        assertEquals(true, instance.checkForXMLNames("Asdf:sdf"));
        assertEquals(false, instance.checkForXMLNames("sdfsdf#"));
        assertEquals(true, instance.checkForXMLNames("As df sdf"));
        assertEquals(false, instance.checkForXMLNames("sd fsd f#"));
    }

    /**
     * Test of checkForXMLNmtoken method, of class DTDNameChecker.
     */
    @Test
    public void testCheckForXMLNmtoken() {
        DTDNameChecker instance = new DTDNameChecker();

        assertEquals(false, instance.checkForXMLNmtoken(""));
        assertEquals(false, instance.checkForXMLNmtoken(" "));
        assertEquals(true, instance.checkForXMLNmtoken("-"));
        assertEquals(true, instance.checkForXMLNmtoken("_"));
        assertEquals(true, instance.checkForXMLNmtoken("0"));
        assertEquals(true, instance.checkForXMLNmtoken("a"));
        assertEquals(true, instance.checkForXMLNmtoken("A"));
        assertEquals(false, instance.checkForXMLNmtoken("#"));
        assertEquals(true, instance.checkForXMLNmtoken("Asdf:sdf"));
        assertEquals(false, instance.checkForXMLNmtoken("sdfsdf#"));
        assertEquals(false, instance.checkForXMLNmtoken("As df sdf"));
        assertEquals(false, instance.checkForXMLNmtoken("sd fsd f#"));

    }

    /**
     * Test of checkForXMLNmtokens method, of class DTDNameChecker.
     */
    @Test
    public void testCheckForXMLNmtokens() {
        DTDNameChecker instance = new DTDNameChecker();

        assertEquals(false, instance.checkForXMLNmtokens(""));
        assertEquals(false, instance.checkForXMLNmtokens(" "));
        assertEquals(true, instance.checkForXMLNmtokens("-"));
        assertEquals(true, instance.checkForXMLNmtokens("_"));
        assertEquals(true, instance.checkForXMLNmtokens("0"));
        assertEquals(true, instance.checkForXMLNmtokens("a"));
        assertEquals(true, instance.checkForXMLNmtokens("A"));
        assertEquals(false, instance.checkForXMLNmtokens("#"));
        assertEquals(true, instance.checkForXMLNmtokens("Asdfsdf"));
        assertEquals(false, instance.checkForXMLNmtokens("sdfsdf#"));
        assertEquals(true, instance.checkForXMLNmtokens("As df sdf"));
        assertEquals(false, instance.checkForXMLNmtokens("sd fsd f#"));
    }
}
