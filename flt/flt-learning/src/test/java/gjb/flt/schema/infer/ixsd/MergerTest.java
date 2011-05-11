/*
 * Created on Nov 4, 2006
 * Modified on $Date: 2009-11-10 12:21:42 $
 */
package gjb.flt.schema.infer.ixsd;

import gjb.flt.automata.factories.sparse.GlushkovFactory;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.impl.sparse.StateNFA;
import gjb.flt.automata.io.SimpleReader;
import gjb.flt.automata.io.SparseNFAReader;
import gjb.flt.automata.measures.EquivalenceTest;
import gjb.flt.automata.measures.SOAEditDistance;
import gjb.flt.treeautomata.impl.ContextAutomaton;

import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class MergerTest extends TestCase {

	protected GlushkovFactory glushkov = new GlushkovFactory();

    public static Test suite() {
        return new TestSuite(MergerTest.class);
    }

    /*
     *    -> a
     * a  -> b c
     * b  -> d1
     * d1 -> e
     * c  -> d2
     * d2 -> f
     * e  -> epsilon
     * f  -> epsilon 
     */
    public void test_noChange() throws Exception {
        String target =
            "init-state: q_I\n" +
            "q_I, a -> a\n" +
            "a, b -> b\n" +
            "a, c -> c\n" +
            "b, d -> d1\n" +
            "c, d -> d2\n" +
            "d1, e -> e\n" +
            "d2, f -> f\n" +
            "final-state: e\n" +
            "final-state: f\n";
        SparseNFAReader reader = new SimpleReader(new StringReader(target));
        StateNFA targetNFA = reader.read();
        String[] sample = {
                "<a>" +
                "  <b>" +
                "    <d>" +
                "      <e/>" +
                "    </d>" +
                "  </b>" +
                "  <c>" +
                "    <d>" +
                "      <f/>" +
                "    </d>" +
                "  </c>" +
                "</a>"
        };
        XMLSampler sampler = new XMLSampler();
        for (int i = 0; i < sample.length; i++)
            sampler.parse(new StringReader(sample[i]));
        ContextAutomaton contextFA = sampler.getContextFA();
        Merger merger = new Merger(sampler.getContextFAFactory(), contextFA);
        merger.merge();
        assertTrue("process", EquivalenceTest.areEquivalent(contextFA, targetNFA));
    }

    /*
     *   -> a
     * a -> b c d
     * b -> e1 f
     * c -> e2 f
     * d -> e1 f
     * e1 -> g h?
     * e2 -> g h
     * f -> i
     * g -> j
     * h -> epsilon
     * i -> epsilon
     * j -> epsilon
     */
    public void test_merger3() throws Exception {
        String target =
            "init-state: q_I\n" +
            "q_I, a -> a\n" +
            "a, b -> b\n" +
            "a, c -> c\n" +
            "a, d -> d\n" +
            "b, e -> e1\n" +
            "b, f -> f\n" +
            "c, e -> e2\n" +
            "c, f -> f\n" +
            "d, e -> e1\n" +
            "d, f -> f\n" +
            "e1, g -> g\n" +
            "e1, h -> h\n" +
            "e2, g -> g\n" +
            "e2, h -> h\n" +
            "f, i -> i\n" +
            "g, j -> j\n" +
            "final-state: h i j\n";
        SparseNFAReader reader = new SimpleReader(new StringReader(target));
        SparseNFA targetNFA = reader.read();
        String[] sample = {
                "<a><b><e><g><j/></g><h/></e><f><i/></f></b><c><e><g><j/></g><h/></e><f><i/></f></c><d><e><g><j/></g><h/></e><f><i/></f></d></a>",
                "<a><b><e><g><j/></g></e><f><i/></f></b><c><e><g><j/></g><h/></e><f><i/></f></c><d><e><g><j/></g></e><f><i/></f></d></a>"
        };
        XMLSampler sampler = new XMLSampler();
        for (int i = 0; i < sample.length; i++)
            sampler.parse(new StringReader(sample[i]));
        ContextAutomaton contextFA = sampler.getContextFA();
        Merger merger = new Merger(sampler.getContextFAFactory(), contextFA);
        merger.merge();
        assertTrue("process", EquivalenceTest.areEquivalent(contextFA, targetNFA));
        StateNFA nfaE4 = glushkov.create("(. (g) (? (h)))");
        assertTrue("e4", EquivalenceTest.areEquivalent(nfaE4, contextFA.getAnnotation("e_4")));
        StateNFA nfaE2 = glushkov.create("(. (g) (h))");
        assertTrue("e4", EquivalenceTest.areEquivalent(nfaE2, contextFA.getAnnotation("e_2")));
    }

    /*
     *    -> a
     * a  -> b c
     * b  -> d?
     * c  -> d?
     * d  -> d?
     */
    public void test_process4() throws Exception {
        String target =
            "init-state: q_I\n" +
            "q_I, a -> a\n" +
            "a, b -> b\n" +
            "a, c -> c\n" +
            "b, d -> d_1\n" +
            "c, d -> d_1\n" +
            "d_1, d -> d_2\n" +
            "final-state: b c d_1 d_2\n";
        SparseNFAReader reader = new SimpleReader(new StringReader(target));
        StateNFA targetNFA = reader.read();
        String[] sample = {
                "<a><b/><c/></a>",
                "<a> <b><d/></b> <c><d/></c> </a>",
                "<a> <b><d><d/></d></b> <c><d><d/></d></c> </a>"
        };
        XMLSampler sampler = new XMLSampler();
        for (int i = 0; i < sample.length; i++)
            sampler.parse(new StringReader(sample[i]));
        ContextAutomaton contextFA = sampler.getContextFA();
        Merger merger = new Merger(sampler.getContextFAFactory(), contextFA);
        ContentEquivalenceRelation equiv = new DirectEquivalenceRelation(0.01, new SOAEditDistance());
        merger.setContentEquivalenceRelation(equiv);
        merger.merge();
        assertTrue("process", EquivalenceTest.areEquivalent(contextFA, targetNFA));
    }

}
