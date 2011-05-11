/*
 * Created on Sep 8, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package gjb.flt.regex.infer.rwr;

import gjb.flt.automata.FeatureNotSupportedException;
import gjb.flt.automata.NotDFAException;
import gjb.flt.automata.factories.sparse.GlushkovFactory;
import gjb.flt.automata.impl.sparse.ModifiableStateNFA;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.measures.EquivalenceTest;
import gjb.flt.regex.UnknownOperatorException;
import gjb.flt.regex.infer.rwr.impl.Automaton;
import gjb.flt.regex.infer.rwr.impl.GraphAutomatonFactory;
import gjb.util.tree.SExpressionParseException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class RewriterTest extends TestCase {

    public static Test suite() {
        return new TestSuite(RewriterTest.class);
    }

    public void testExpression1() {
        final String targetRegexStr = "(. (| (a) (b)) (? (c)) (+ (d)))";
        checkExpression(targetRegexStr);
    }
    
    public void testExpression2() {
        final String targetRegexStr = "(? (| (. (a) (b)) (c) (d) (+ (e))))";
        checkExpression(targetRegexStr);
    }
    
    public void testExpression3() {
        final String targetRegexStr = "(. (b) (a))";
        checkExpression(targetRegexStr);
    }

    public void testExpression4() {
        final String targetRegexStr = "(+ (. (a) (b)))";
        checkExpression(targetRegexStr);
    }

    public void testExpression5() {
        final String targetRegexStr = "(. (| (a) (b)) (+ (. (c) (d))) (| (e) (f)))";
        checkExpression(targetRegexStr);
    }
    
    protected void checkExpression(final String targetRegexStr) {
        try {
            GlushkovFactory glushkov = new GlushkovFactory();
            SparseNFA targetNfa = glushkov.create(targetRegexStr);
            GraphAutomatonFactory factory = new GraphAutomatonFactory();
            Automaton automaton = factory.create(targetRegexStr);
            RewriteEngine rewriter = new Rewriter();
            String regexStr = rewriter.rewriteToRegex(automaton);
            ModifiableStateNFA nfa = glushkov.create(regexStr);
            assertTrue("equiv", EquivalenceTest.areEquivalent(targetNfa, nfa));
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (NoOpportunityFoundException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (UnknownOperatorException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (FeatureNotSupportedException e) {
            e.printStackTrace();
            fail("unexpected exception");
        } catch (NotDFAException e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
    }

}
