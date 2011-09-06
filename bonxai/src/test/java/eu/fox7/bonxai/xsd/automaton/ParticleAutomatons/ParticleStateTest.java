package eu.fox7.bonxai.xsd.automaton.ParticleAutomatons;

import org.junit.Test;

import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.ParticleState;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>ParticleState</tt> class, checks that every method of
 * this class performs properly.
 * @author Dominik Wolff
 */
public class ParticleStateTest extends junit.framework.TestCase {

    public ParticleStateTest() {
    }

    /**
     * Test of getStateNumber method, of class ParticleState.
     */
    @Test
    public void testGetStateNumber() {
        ParticleState instance = new ParticleState(0);

        int expResult = 0;
        int result = instance.getStateNumber();

        assertEquals(expResult, result);
    }

    /**
     * Test of setStateNumber method, of class ParticleState.
     */
    @Test
    public void testSetStateNumber() {
        ParticleState instance = new ParticleState(0);
        instance.setStateNumber(0);

        int expResult = 0;
        int result = instance.getStateNumber();

        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class ParticleState.
     */
    @Test
    public void testToString() {
        ParticleState instance = new ParticleState(0);

        String expResult = "(0)";
        String result = instance.toString();

        assertEquals(expResult, result);
    }
}