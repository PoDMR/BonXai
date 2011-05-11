/*
 * Created on Jun 8, 2007
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package gjb.flt.automata.measures;

import gjb.flt.automata.factories.sparse.GlushkovFactory;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.measures.Redundancy;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class RedundancyTest extends TestCase {

    public static Test suite() {
        return new TestSuite(RedundancyTest.class);
    }

    public void test_simpleExpression01() throws Exception {
        GlushkovFactory glushkov = new GlushkovFactory();
        SparseNFA nfa = glushkov.create("(* (. (a) (b)))");
        Redundancy redundancy = Redundancy.compute(nfa);
        assertEquals("states", 1.0/3.0, redundancy.getStateRedundancy());
        assertEquals("transitions", 1.0/3.0, redundancy.getTransitionRedundancy());
        assertEquals("final states", 1.0/2.0, redundancy.getFinalStateRedundancy());
    }

    public void test_simpleExpression02() throws Exception {
        GlushkovFactory glushkov = new GlushkovFactory();
        SparseNFA nfa = glushkov.create("(| (a) (a))");
        Redundancy redundancy = Redundancy.compute(nfa);
        assertEquals("states", 1.0/3.0, redundancy.getStateRedundancy());
        assertEquals("transitions", 1.0/2.0, redundancy.getTransitionRedundancy());
        assertEquals("final states", 1.0/2.0, redundancy.getFinalStateRedundancy());
    }
    
    public void test_simpleExpression03() throws Exception {
        GlushkovFactory glushkov = new GlushkovFactory();
        SparseNFA nfa = glushkov.create("(| (. (a) (c)) (. (b) (c)))");
        Redundancy redundancy = Redundancy.compute(nfa);
        assertEquals("states", 2.0/5.0, redundancy.getStateRedundancy());
        assertEquals("transitions", 1.0/4.0, redundancy.getTransitionRedundancy());
        assertEquals("final states", 1.0/2.0, redundancy.getFinalStateRedundancy());
    }
    
}
