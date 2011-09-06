package eu.fox7.bonxai.common;


import java.util.LinkedList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.fox7.bonxai.common.AllPattern;
import eu.fox7.bonxai.common.ChoicePattern;
import eu.fox7.bonxai.common.Element;
import eu.fox7.bonxai.common.Particle;
import eu.fox7.bonxai.common.ParticleContainer;
import eu.fox7.bonxai.common.SequencePattern;

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
        Particle expResult = new eu.fox7.bonxai.xsd.Element("{}someElement");
        container.addParticle(expResult);
        LinkedList<Particle> result = (LinkedList<Particle>) container.getParticles();
        assertTrue(result.contains(expResult));
    }

   @Test
   public void testGetParticlesChoicePattern() {
        ParticleContainer container = new ChoicePattern();
        Particle expResult = new eu.fox7.bonxai.xsd.Element("{}someElement");
        container.addParticle(expResult);
        LinkedList<Particle> result = (LinkedList<Particle>) container.getParticles();
        assertTrue(result.contains(expResult));
    }

   @Test
   public void testGetParticlesAllPattern() {
        ParticleContainer container = new AllPattern();
        Particle expResult = new eu.fox7.bonxai.xsd.Element("{}someElement");
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
        Particle expResult = new eu.fox7.bonxai.xsd.Element("{}someElement");
        ParticleContainer instance = new SequencePattern();
        instance.addParticle(expResult);
        LinkedList<Particle> result = instance.particles;
        assertTrue(result.contains(expResult));
    }
    @Test
    public void testAddParticleChoicePattern() {
        Particle expResult = new eu.fox7.bonxai.xsd.Element("{}someElement");
        ParticleContainer instance = new ChoicePattern();
        instance.addParticle(expResult);
        LinkedList<Particle> result = instance.particles;
        assertTrue(result.contains(expResult));
    }
@Test
    public void testAddParticleAllPattern() {
        Particle expResult = new eu.fox7.bonxai.xsd.Element("{}someElement");
        ParticleContainer instance = new AllPattern();
        instance.addParticle(expResult);
        LinkedList<Particle> result = instance.particles;
        assertTrue(result.contains(expResult));
    }
}
