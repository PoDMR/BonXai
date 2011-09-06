package eu.fox7.bonxai.xsd.automaton.ParticleAutomatons;

import eu.fox7.bonxai.xsd.automaton.Automaton;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.ParticleState;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.ProductParticleState;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.SubsetParticleState;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.exceptions.*;

import java.util.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>ProductParticleState</tt> class, checks that every
 * method of this class performs properly.
 * @author Dominik Wolff
 */
public class ProductParticleStateTest extends junit.framework.TestCase {

    private LinkedList<ParticleState> particleStates;
    private ParticleState particleStateA;
    private SubsetParticleState particleStateB;

    public ProductParticleStateTest() {
    }

    @Before
    public void setUp() throws EmptySubsetParticleStateFieldException {

        // Set up particle states
        LinkedHashSet<ParticleState> particleStatesA = new LinkedHashSet<ParticleState>();
        particleStatesA.add(new ParticleState(11));
        particleStatesA.add(new ParticleState(12));
        particleStates = new LinkedList<ParticleState>();
        particleStateA = new ParticleState(11);
        particleStates.add(particleStateA);
        particleStateB = new SubsetParticleState(particleStatesA, 2);
        particleStates.add(particleStateB);
        particleStates.add(null);
    }

    /**
     * Test of getParticleStates method, of class ProductParticleState.
     */
    @Test
    public void testGetParticleStates() throws EmptyProductParticleStateFieldException {
        ProductParticleState instance = new ProductParticleState(particleStates, 0);

        LinkedList<ParticleState> expResult = new LinkedList<ParticleState>();
        expResult.add(particleStateA);
        expResult.add(particleStateB);
        expResult.add(null);
        LinkedList<ParticleState> result = instance.getParticleStates();

        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class ProductParticleState with no complex
     * representation.
     */
    @Test
    public void testToStringNoComplex() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException {
        ProductParticleState instance = new ProductParticleState(particleStates, 0);
        Automaton.COMPLEX_REPRESENTATION = false;

        String expResult = "(0)";
        String result = instance.toString();

        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class ProductParticleState with complex
     * representation.
     */
    @Test
    public void testToStringComplex() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException {
        ProductParticleState instance = new ProductParticleState(particleStates, 0);
        Automaton.COMPLEX_REPRESENTATION = true;

        String expResult = "((11),((11),(12)),sink)";
        String result = instance.toString();

        assertEquals(expResult, result);
    }
}