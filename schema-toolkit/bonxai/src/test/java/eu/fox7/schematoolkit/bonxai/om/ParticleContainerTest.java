
package eu.fox7.schematoolkit.bonxai.om;

import java.util.LinkedList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.fox7.schematoolkit.common.AllPattern;
import eu.fox7.schematoolkit.common.ChoicePattern;
import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.common.ParticleContainer;
import eu.fox7.schematoolkit.common.SequencePattern;
import static org.junit.Assert.*;

/**
 *
 */
public class ParticleContainerTest extends junit.framework.TestCase {

    public ParticleContainerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getParticles method, of class ParticleContainer.
     */
    @Test
    public void testGetParticlesSequencePattern() {
        ParticleContainer container = new SequencePattern();
        Element expResult = new Element(null, "element");
        container.addParticle(expResult);
        LinkedList<Particle> result = (LinkedList<Particle>) container.getParticles();
        assertTrue(result.contains(expResult));
    }

   @Test
   public void testGetParticlesChoicePattern() {
        ParticleContainer container = new ChoicePattern();
        Element expResult = new Element(null, "element");
        container.addParticle(expResult);
        LinkedList<Particle> result = (LinkedList<Particle>) container.getParticles();
        assertTrue(result.contains(expResult));
    }

   @Test
   public void testGetParticlesAllPattern() {
        ParticleContainer container = new AllPattern();
        Element expResult = new Element(null, "element");
        container.addParticle(expResult);
        LinkedList<Particle> result = (LinkedList<Particle>) container.getParticles();
        assertTrue(result.contains(expResult));
    }

    /**
     * Test of addParticle method, of class ParticleContainer. As it is abstract,
     * the method must be tested in all subclasses.
     */
    @Test
    public void testAddParticleSequencePattern() {
        Element expResult = new Element(null, "element");
        ParticleContainer instance = new SequencePattern();
        instance.addParticle(expResult);
        LinkedList<Particle> result = instance.getParticles();
        assertTrue(result.contains(expResult));
    }
    @Test
    public void testAddParticleChoicePattern() {
        Element expResult = new Element(null, "element");
        ParticleContainer instance = new ChoicePattern();
        instance.addParticle(expResult);
        LinkedList<Particle> result = instance.getParticles();
        assertTrue(result.contains(expResult));
    }
@Test
    public void testAddParticleAllPattern() {
        Element expResult = new Element(null, "element");
        ParticleContainer instance = new AllPattern();
        instance.addParticle(expResult);
        LinkedList<Particle> result = instance.getParticles();
        assertTrue(result.contains(expResult));
    }
}
