/*
 * Created on Jul 7, 2008
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package gjb.flt.regex;

import gjb.flt.regex.measures.ContainsEpsilonTest;
import gjb.flt.regex.measures.LanguageTest;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class EpsilonCheckerTest extends TestCase {

    public static Test suite() {
        return new TestSuite(EpsilonCheckerTest.class);
    }

    public void test_expressions() {
        final String[] regexStrs = {
                "(+ (| (? (a)) (b)))",
                "(+ (| (a) (b)))",
                "(? (. (a) (b)))",
                "(. (? (a)) (b))",
                "(. (? (a)) (| (b) (? (c))))",
                "(EPSILON)"
        };
        final boolean[] targetContainsEpsilon = {
                true,
                false,
                true,
                false,
                true,
                true
        };
        try {
            LanguageTest checker= new ContainsEpsilonTest();
            for (int i = 0; i < regexStrs.length; i++) {
                assertEquals("expression " + regexStrs[i],
                             targetContainsEpsilon[i],
                             checker.test(regexStrs[i]));
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception");
        }
    }

}
