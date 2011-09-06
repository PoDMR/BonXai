/*
 * Created on Feb 26, 2007
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package eu.fox7.flt.automata.random;

import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.random.RandomGlushkovNFAFactory;

import java.io.StringReader;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class RandomGlushkovNFAFactoryTest extends TestCase {

    public static Test suite() {
        return new TestSuite(RandomGlushkovNFAFactoryTest.class);
    }

    public void test_valid() throws Exception {
        final String propertiesStr =
            "alphabetSize=5\n" +
            "typeDistribution=1,1,1,1,1\n" +
            "numberOfFinalStates=2\n" +
            "numberOfOutTransitionsInitState=1\n";
        Properties properties = new Properties();
        properties.load(new StringReader(propertiesStr));
        RandomGlushkovNFAFactory factory = new RandomGlushkovNFAFactory(properties);
        for (int i = 0; i < 10; i++) {
            SparseNFA nfa = factory.create();
            assertEquals("alphabet", 5, nfa.getNumberOfSymbols());
            assertEquals("states", 6, nfa.getNumberOfStates());
            assertEquals("final states", 2, nfa.getFinalStates().size());
            assertEquals("outgoing transitions from initial state",
                         1,
                         nfa.getOutgoingTransitions(nfa.getInitialState()).size());
            assertEquals("all reachable", 6, nfa.reachableStates().size());
            assertEquals("all terminating", 6, nfa.getTerminatingStates().size());
        }
    }

}
