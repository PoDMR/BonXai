package eu.fox7.bonxai.xsd.automaton.ParticleAutomatons;

import eu.fox7.bonxai.xsd.automaton.Automaton;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.ParticleState;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.SubsetParticleState;
import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.exceptions.EmptySubsetParticleStateFieldException;

import java.util.LinkedHashSet;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>SubsetParticleState</tt> class, checks that every method
 * of this class performs properly.
 * @author Dominik Wolff
 */
public class SubsetParticleStateTest extends junit.framework.TestCase {

    private LinkedHashSet<ParticleState> particleStates;
    private ParticleState particleStateA;
    private SubsetParticleState particleStateB;

    public SubsetParticleStateTest() {
    }

    @Before
    public void setUp() throws EmptySubsetParticleStateFieldException {

        // Set up particle states
        LinkedHashSet<ParticleState> particleStatesA = new LinkedHashSet<ParticleState>();
        particleStatesA.add(new ParticleState(11));
        particleStatesA.add(new ParticleState(12));
        particleStates = new LinkedHashSet<ParticleState>();
        particleStateA = new ParticleState(11);
        particleStates.add(particleStateA);
        particleStateB = new SubsetParticleState(particleStatesA, 2);
        particleStates.add(particleStateB);
    }

    /**
     * Test of getParticleStates method, of class SubsetParticleState.
     */
    @Test
    public void testGetParticleStates() throws EmptySubsetParticleStateFieldException {
        SubsetParticleState instance = new SubsetParticleState(particleStates, 0);

        LinkedHashSet<ParticleState> expResult = new LinkedHashSet<ParticleState>();
        expResult.add(particleStateA);
        expResult.add(particleStateB);
        LinkedHashSet<ParticleState> result = instance.getParticleStates();

        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class SubsetParticleState with no complex
     * representation.
     */
    @Test
    public void testToStringNoComplex() throws EmptySubsetParticleStateFieldException {
        SubsetParticleState instance = new SubsetParticleState(particleStates, 0);

        String expResult = "(0)";
        String result = instance.toString();

        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class SubsetParticleState with complex
     * representation.
     */
    @Test
    public void testToStringComplex() throws EmptySubsetParticleStateFieldException {
        SubsetParticleState instance = new SubsetParticleState(particleStates, 0);
        Automaton.COMPLEX_REPRESENTATION = true;

        String expResult = "((11),((11),(12)))";
        String result = instance.toString();

        assertEquals(expResult, result);
    }
}