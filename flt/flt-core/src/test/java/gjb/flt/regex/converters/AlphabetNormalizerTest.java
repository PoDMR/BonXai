/**
 * Created on Mar 20, 2009
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package gjb.flt.regex.converters;

import java.util.Properties;

import gjb.flt.regex.Regex;
import gjb.flt.regex.UnknownOperatorException;
import gjb.flt.regex.converters.AlphabetNormalizer;
import gjb.flt.regex.converters.Converter;
import gjb.util.tree.SExpressionParseException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class AlphabetNormalizerTest extends TestCase {

    public static Test suite() {
        return new TestSuite(AlphabetNormalizerTest.class);
    }

	public void testRegex01() {
		final String regexStr = "(. (| (alpha) (beta)) (gamma) (? (alpha)))";
		final String targetStr = "(. (| (S000) (S001)) (S002) (? (S000)))";
		try {
			Regex regex = new Regex(regexStr);
			Converter converter = new AlphabetNormalizer();
			assertEquals(targetStr, converter.convert(regex));
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (UnknownOperatorException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}

	public void testRegex02() {
		final String regexStr = "(. (| (alpha) (EPSILON)) (gamma) (? (alpha)))";
		final String targetStr = "(. (| (S000) (EPSILON)) (S001) (? (S000)))";
		try {
			Regex regex = new Regex(regexStr);
			Converter converter = new AlphabetNormalizer();
			assertEquals(targetStr, converter.convert(regex));
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (UnknownOperatorException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}
	
	public void testRegex03() {
		final String regexStr = "(. (| (a) (a)) (a) (? (a)))";
		final String targetStr = "(. (| (S000) (S000)) (S000) (? (S000)))";
		try {
			Regex regex = new Regex(regexStr);
			Converter converter = new AlphabetNormalizer();
			assertEquals(targetStr, converter.convert(regex));
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (UnknownOperatorException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}
	
	public void testRegex04() {
		final String regexStr = "(EPSILON)";
		final String targetStr = "(EPSILON)";
		try {
			Regex regex = new Regex(regexStr);
			Converter converter = new AlphabetNormalizer();
			assertEquals(targetStr, converter.convert(regex));
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (UnknownOperatorException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}
	
	public void testRegex05() {
		final String regexStr = "(EMPTY)";
		final String targetStr = "(EMPTY)";
		try {
			Regex regex = new Regex(regexStr);
			Converter converter = new AlphabetNormalizer();
			assertEquals(targetStr, converter.convert(regex));
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (UnknownOperatorException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}
	
	public void testRegex06() {
		final String regexStr = "(a)";
		final String targetStr = "(S000)";
		try {
			Regex regex = new Regex(regexStr);
			Converter converter = new AlphabetNormalizer();
			assertEquals(targetStr, converter.convert(regex));
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (UnknownOperatorException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}
	
	public void testRegex07() {
		final String regexStr = "(% (id) (cis_ovz_katzdroju_F1_kod) (cis_ovz_katzdroju_F1_nazev) (cis_ovz_corinair_H1_kod) (cis_ovz_corinair_H1_nazev) (cis_ovz_druhtopeniste_I1_kod) (cis_ovz_druhtopeniste_I1_nazev) (kotel_vykon) (cis_ovz_mj_kapvykon_N1_kod) (cis_ovz_mj_kapvykon_N1_nazev) (kotel_prikon) (cis_ovz_mj_kapprikon_N2_kod) (cis_ovz_mj_kapprikon_N2_nazev) (kotel_ucinnost_procent) (prumerne_vyuziti_kapacity_procent) (vyroba_tepla_GJzarok) (PSE_LCP))";
		final String targetStr = "(% (S000) (S001) (S002) (S003) (S004) (S005) (S006) (S007) (S008) (S009) (S010) (S011) (S012) (S013) (S014) (S015) (S016))";
		Properties prop = new Properties();
		prop.setProperty("interleave", "%");
		try {
			Regex regex = new Regex(prop);
			Converter converter = new AlphabetNormalizer(regex);
			assertEquals(targetStr, converter.convert(regexStr));
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			fail("unexpected exception");
		} catch (UnknownOperatorException e) {
			e.printStackTrace();
			fail("unexpected exception");
		}
	}

}
