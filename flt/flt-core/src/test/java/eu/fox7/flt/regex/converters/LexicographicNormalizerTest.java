/**
 * Created on Mar 23, 2009
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package eu.fox7.flt.regex.converters;

import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.regex.converters.Converter;
import eu.fox7.flt.regex.converters.LexicographicNormalizer;
import eu.fox7.util.tree.SExpressionParseException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class LexicographicNormalizerTest extends TestCase {

    public static Test suite() {
        return new TestSuite(LexicographicNormalizerTest.class);
    }

	public void testExpressions() {
		final String[] regexStr = {
			"(EMPTY)",
			"(| (b) (a) (d) (c))",
			"(| (. (a) (b)) (a))",
			"(. (b) (c) (a))",
			"(| (. (a) (d)) (. (a) (c)))",
			"(| ({2,4} (. (b) (a))) ({1,3} (. (b) (a))) ({2,4} (. (a) (b))) ({1,7} (. (b) (a))))"
		};
		final String[] targetRegexStr = {
			"(EMPTY)",
			"(| (a) (b) (c) (d))",
			"(| (. (a) (b)) (a))",
			"(. (b) (c) (a))",
			"(| (. (a) (c)) (. (a) (d)))",
			"(| ({1,7} (. (b) (a))) ({1,3} (. (b) (a))) ({2,4} (. (a) (b))) ({2,4} (. (b) (a))))"
		};
		Converter converter = new LexicographicNormalizer();
		try {
			for (int i = 0; i < regexStr.length; i++) {
				String resultStr = converter.convert(new Regex(regexStr[i]));
				assertEquals("expr " + i, targetRegexStr[i], resultStr);
			}
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (UnknownOperatorException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}

}
