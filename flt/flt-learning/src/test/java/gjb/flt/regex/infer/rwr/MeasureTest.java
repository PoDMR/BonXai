/*
 * Created on Oct 10, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package gjb.flt.regex.infer.rwr;

import gjb.flt.automata.FeatureNotSupportedException;
import gjb.flt.regex.UnknownOperatorException;
import gjb.flt.regex.infer.rwr.impl.Automaton;
import gjb.flt.regex.infer.rwr.impl.GraphAutomatonFactory;
import gjb.flt.regex.infer.rwr.measures.LanguageSizeMeasure;
import gjb.util.tree.SExpressionParseException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class MeasureTest extends TestCase {

    public static Test suite() {
        return new TestSuite(MeasureTest.class);
    }

    public void testLanguageSize01() {
        String regexStr = "(. (| (a) (b)) (+ (. (c) (d))) (? (e)))";
        try {
            LanguageSizeMeasure measure = new LanguageSizeMeasure();
            GraphAutomatonFactory factory = new GraphAutomatonFactory();
            Automaton automaton1 = factory.create(regexStr);
            double size1 = measure.compute(automaton1);
            RewriteEngine rewriter = new Rewriter();
            Automaton automaton2 = rewriter.rewrite(automaton1);
            assertTrue("reduced", automaton2.isReduced());
            double size2 = measure.compute(factory.expand(automaton2));
            assertEquals("size equal", size1, size2);
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (UnknownOperatorException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (FeatureNotSupportedException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }

}
