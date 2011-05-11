/*
 * Created on Apr 13, 2008
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package gjb.flt.automata;


import gjb.flt.automata.factories.sparse.GlushkovFactory;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.iterators.BreadthFirstStateIterator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class BreadthFirstStateIteratorTest extends TestCase {

    public static Test suite() {
        return new TestSuite(BreadthFirstStateIteratorTest.class);
    }

    public void testSimpleExpression() throws Exception {
        String regexStr = "(+ (. (a) (+ (| (b) (c))) (? (c))))";
        GlushkovFactory glushkov = new GlushkovFactory();
        SparseNFA nfa = glushkov.create(regexStr);
        Set<State> states = new HashSet<State>();
        for (Iterator<State> it = new BreadthFirstStateIterator(nfa); it.hasNext(); ) {
            State state = it.next();
            assertTrue("unique state " + nfa.getStateValue(state), !states.contains(state));
            states.add(state);
        }
        assertEquals("all states found", nfa.getNumberOfStates(), states.size());
    }

}
