
import de.tudortmund.cs.bonxai.converter.dtd2xsd.DTD2XSDTestSuite;
import de.tudortmund.cs.bonxai.converter.relaxng2xsd.RELAXNG2XSDTestSuite;
import de.tudortmund.cs.bonxai.converter.xsd2dtd.XSD2DTDTestSuite;
import de.tudortmund.cs.bonxai.converter.xsd2relaxng.XSD2RELAXNGTestSuite;
import de.tudortmund.cs.bonxai.dtd.DTDFullTestSuite;
import de.tudortmund.cs.bonxai.relaxng.RELAXNGFullTestSuite;
import de.tudortmund.cs.bonxai.xsd.parser.New_parserSuite;
import de.tudortmund.cs.bonxai.xsd.tools.XSDToolsTestSuite;

public class LarsSchmidtTestSuiteForDiplomathesis {

    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();

        // DTD data structure
        suite.addTest(DTDFullTestSuite.suite());
        // Converter: XML Schema -> DTD
        suite.addTest(XSD2DTDTestSuite.suite());
        // Converter: DTD -> XML Schema
        suite.addTest(DTD2XSDTestSuite.suite());

        // RELAX NG data structure
        suite.addTest(RELAXNGFullTestSuite.suite());
        // Converter: RELAX NG -> XML Schema
        suite.addTest(RELAXNG2XSDTestSuite.suite());
        // Converter: XML Schema -> RELAX NG
        suite.addTest(XSD2RELAXNGTestSuite.suite());

        // Tools for XML Schema
        suite.addTest(XSDToolsTestSuite.suite());

        // New parser for XML Schema
        suite.addTest(New_parserSuite.suite());

        return suite;
    }
}
