/*
 * Created on Aug 29, 2008
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package eu.fox7.flt.regex.random;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.automata.factories.sparse.GlushkovFactory;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.matchers.NFAMatcher;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.regex.random.InvalidSampleGenerator;
import eu.fox7.flt.regex.random.MutatorCreationException;
import eu.fox7.flt.regex.random.NoMutationFoundException;
import eu.fox7.util.tree.SExpressionParseException;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class InvalidSampleGeneratorTest extends TestCase {

    public static Test suite() {
        return new TestSuite(InvalidSampleGeneratorTest.class);
    }

    public void test_generator01() {
        final String[] regexStrs = {
                "(. (a) (| (b) (c)) (? (d)) (+ (| (e) (f) (g))))",
                "(| (a) (b) (c))",
                "(EPSILON)"
        };
        final int sampleSize = 50;
        try {
            GlushkovFactory glushkov = new GlushkovFactory();
            for (String regexStr : regexStrs) {
                SparseNFA nfa = glushkov.create(regexStr);
                NFAMatcher matcher = new NFAMatcher(nfa);
                InvalidSampleGenerator generator = new InvalidSampleGenerator();
                List<String[]> sample = generator.getSample(regexStr, sampleSize);
                for (String[] example : sample) {
                    assertFalse("matches", matcher.matches(example));
                }
            }
        } catch (SExpressionParseException e) {
            e.printStackTrace();
        } catch (UnknownOperatorException e) {
            e.printStackTrace();
        } catch (FeatureNotSupportedException e) {
            e.printStackTrace();
        } catch (MutatorCreationException e) {
            e.printStackTrace();
        } catch (NoMutationFoundException e) {
            e.printStackTrace();
        }
    }

}
