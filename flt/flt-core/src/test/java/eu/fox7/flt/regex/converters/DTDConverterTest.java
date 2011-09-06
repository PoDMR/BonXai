/**
 * Created on Jun 17, 2009
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package eu.fox7.flt.regex.converters;

import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.regex.converters.ConversionException;
import eu.fox7.flt.regex.converters.Converter;
import eu.fox7.flt.regex.converters.DTDConverter;
import eu.fox7.util.tree.SExpressionParseException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class DTDConverterTest extends TestCase {

    public static Test suite() {
        return new TestSuite(DTDConverterTest.class);
    }

	public void test_expr01() {
		final String regexStr = "(. (| (a) (. (+ (b)) (* (c)))) (d) (? (e)))";
		final String targetStr = "((a | (b+ , c*)) , d , e?)";
		Converter converter = new DTDConverter();
		try {
			assertEquals("dtd", targetStr, converter.convert(regexStr));
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (UnknownOperatorException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (ConversionException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}

	public void test_expr02() {
		final String regexStr = "(EPSILON)";
		final String targetStr = "(EMPTY)";
		Converter converter = new DTDConverter();
		try {
			assertEquals("dtd", targetStr, converter.convert(regexStr));
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (UnknownOperatorException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (ConversionException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}
	
}
