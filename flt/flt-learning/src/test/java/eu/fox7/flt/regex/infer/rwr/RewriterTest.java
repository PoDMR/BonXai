/*
 * Created on Sep 8, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package eu.fox7.flt.regex.infer.rwr;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.factories.sparse.GlushkovFactory;
import eu.fox7.flt.automata.impl.sparse.ModifiableStateNFA;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.measures.EquivalenceTest;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.regex.infer.rwr.NoOpportunityFoundException;
import eu.fox7.flt.regex.infer.rwr.RewriteEngine;
import eu.fox7.flt.regex.infer.rwr.Rewriter;
import eu.fox7.flt.regex.infer.rwr.impl.Automaton;
import eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory;
import eu.fox7.util.tree.SExpressionParseException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author eu.fox7
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
