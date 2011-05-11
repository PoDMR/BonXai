package de.tudortmund.cs.bonxai.xsd.automaton.ParticleAutomatons;

import de.tudortmund.cs.bonxai.common.AnyPattern;
import de.tudortmund.cs.bonxai.common.ChoicePattern;
import de.tudortmund.cs.bonxai.common.CountingPattern;
import de.tudortmund.cs.bonxai.common.Particle;
import de.tudortmund.cs.bonxai.common.ProcessContentsInstruction;
import de.tudortmund.cs.bonxai.common.SequencePattern;
import de.tudortmund.cs.bonxai.xsd.Element;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import java.util.LinkedHashMap;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>UnionConstructor</tt> class, checks that every
 * method of this class performs properly.
 * @author Dominik Wolff
 */
public class ParticleBuilderTest extends junit.framework.TestCase {

    public ParticleBuilderTest() {
    }

    /**
     * Test of buildParticle method, of class ParticleBuilder.
     */
    @Test
    public void testBuildParticle1st() throws Exception {
        Element elementA = new Element("{A}a");
        Element elementB = new Element("{A}b");
        Element elementC = new Element("{A}c");

        CountingPattern countingPatternB = new CountingPattern(1, null);
        CountingPattern countingPatternC = new CountingPattern(1, null);

        countingPatternB.addParticle(elementB);
        countingPatternC.addParticle(elementC);

        SequencePattern sequencePattern = new SequencePattern();
        sequencePattern.addParticle(elementA);
        sequencePattern.addParticle(countingPatternB);
        sequencePattern.addParticle(countingPatternC);

        ParticleAutomatonFactory particleAutomatonFactory = new ParticleAutomatonFactory();
        ParticleAutomaton particleAutomaton = particleAutomatonFactory.buildSubsetParticleAutomaton(particleAutomatonFactory.buildParticleAutomaton(sequencePattern, new LinkedHashMap<AnyPattern, XSDSchema>()));

        ParticleBuilder instance = new ParticleBuilder();

        Particle result = instance.buildParticle(particleAutomaton);

        assertTrue(result instanceof SequencePattern);
        assertTrue(((SequencePattern) result).getParticles().size() == 4);
        assertTrue(((SequencePattern) result).getParticles().get(0) instanceof ChoicePattern);
        assertTrue(((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().size() == 2);
        assertTrue(((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().get(0) instanceof SequencePattern);
        assertTrue(((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().get(1) instanceof SequencePattern);
        assertTrue(((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().get(0)).getParticles().size() == 4);
        assertTrue(((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().get(0)).getParticles().get(2) instanceof CountingPattern);
        assertEquals(0, (int) ((CountingPattern) ((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().get(0)).getParticles().get(2)).getMin());
        assertEquals(null, ((CountingPattern) ((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().get(0)).getParticles().get(2)).getMax());
        assertEquals(elementB, ((CountingPattern) ((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().get(0)).getParticles().get(2)).getParticles().getFirst());
        assertEquals(elementB, ((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().get(0)).getParticles().get(3));
        assertEquals(elementA, ((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().get(0)).getParticles().get(0));
        assertEquals(elementB, ((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().get(0)).getParticles().get(1));
        assertEquals(elementB, ((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().get(0)).getParticles().get(3));
        assertTrue(((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().get(1)).getParticles().size() == 2);
        assertEquals(elementA, ((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().get(1)).getParticles().get(0));
        assertEquals(elementB, ((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().get(1)).getParticles().get(1));
        assertTrue(((SequencePattern) result).getParticles().get(1) instanceof CountingPattern);
        assertEquals(0, (int) ((CountingPattern) ((SequencePattern) result).getParticles().get(1)).getMin());
        assertEquals(null, ((CountingPattern) ((SequencePattern) result).getParticles().get(1)).getMax());
        assertEquals(elementB, ((CountingPattern) ((SequencePattern) result).getParticles().get(1)).getParticles().getFirst());
        assertEquals(elementC, ((SequencePattern) result).getParticles().get(2));
        assertTrue(((SequencePattern) result).getParticles().get(3) instanceof CountingPattern);
        assertEquals(0, (int) ((CountingPattern) ((SequencePattern) result).getParticles().get(3)).getMin());
        assertEquals(null, ((CountingPattern) ((SequencePattern) result).getParticles().get(3)).getMax());
        assertEquals(elementC, ((CountingPattern) ((SequencePattern) result).getParticles().get(3)).getParticles().getFirst());
    }

    /**
     * Test of buildParticle method, of class ParticleBuilder.
     */
    @Test
    public void testBuildParticle2nd() throws Exception {
        Element elementA = new Element("{A}a");
        Element elementB = new Element("{A}b");
        Element elementC = new Element("{A}c");
        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Lax, "A");

        SequencePattern sequencePattern = new SequencePattern();
        SequencePattern sequencePatternA = new SequencePattern();
        SequencePattern sequencePatternB = new SequencePattern();

        sequencePatternA.addParticle(elementA);
        sequencePatternA.addParticle(anyPattern);

        sequencePatternB.addParticle(anyPattern);
        sequencePatternB.addParticle(elementB);

        ChoicePattern choicePatternA = new ChoicePattern();
        choicePatternA.addParticle(sequencePatternA);
        choicePatternA.addParticle(sequencePatternB);

        sequencePattern.addParticle(choicePatternA);
        sequencePattern.addParticle(anyPattern);
        sequencePattern.addParticle(elementC);

        ParticleAutomatonFactory particleAutomatonFactory = new ParticleAutomatonFactory();
        ParticleAutomaton particleAutomaton = particleAutomatonFactory.buildSubsetParticleAutomaton(particleAutomatonFactory.buildParticleAutomaton(sequencePattern, new LinkedHashMap<AnyPattern, XSDSchema>()));

        ParticleBuilder instance = new ParticleBuilder();

        Particle result = instance.buildParticle(particleAutomaton);

        assertTrue(result instanceof SequencePattern);
        assertTrue(((SequencePattern) result).getParticles().size() == 3);
        assertTrue(((SequencePattern) result).getParticles().get(0) instanceof ChoicePattern);
        assertTrue(((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().size() == 2);
        assertTrue(((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().get(0) instanceof SequencePattern);
        assertTrue(((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().get(0)).getParticles().size() == 2);
        assertEquals(anyPattern, ((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().get(0)).getParticles().getFirst());
        assertEquals(elementB, ((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().get(0)).getParticles().getLast());
        assertTrue(((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().get(1) instanceof SequencePattern);
        assertTrue(((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().get(1)).getParticles().size() == 2);
        assertEquals(elementA, ((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().get(1)).getParticles().getFirst());
        assertEquals(anyPattern, ((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().get(1)).getParticles().getLast());
        assertEquals(anyPattern, ((SequencePattern) result).getParticles().get(1));
        assertEquals(elementC, ((SequencePattern) result).getParticles().get(2));
    }

    /**
     * Test of buildParticle method, of class ParticleBuilder.
     */
    @Test
    public void testBuildParticle3rd() throws Exception {
        Element elementA = new Element("{A}a");
        Element elementB = new Element("{A}b");

        CountingPattern countingPatternA = new CountingPattern(0, null);
        CountingPattern countingPatternB = new CountingPattern(1, 2);

        countingPatternA.addParticle(elementA);
        countingPatternB.addParticle(elementB);

        SequencePattern sequencePattern = new SequencePattern();
        sequencePattern.addParticle(countingPatternA);
        sequencePattern.addParticle(countingPatternB);

        ParticleAutomatonFactory particleAutomatonFactory = new ParticleAutomatonFactory();
        ParticleAutomaton particleAutomaton = particleAutomatonFactory.buildSubsetParticleAutomaton(particleAutomatonFactory.buildParticleAutomaton(sequencePattern, new LinkedHashMap<AnyPattern, XSDSchema>()));

        ParticleBuilder instance = new ParticleBuilder();

        Particle result = instance.buildParticle(particleAutomaton);

        assertTrue(result instanceof SequencePattern);
        assertTrue(((SequencePattern) result).getParticles().size() == 2);
        assertTrue(((SequencePattern) result).getParticles().get(0) instanceof ChoicePattern);
        assertTrue(((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().size() == 2);
        assertTrue(((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().getFirst() instanceof SequencePattern);
        assertEquals(elementB, ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().getLast());
        assertTrue(((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().getFirst()).getParticles().size() == 3);
        assertTrue(((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().getFirst()).getParticles().get(0) instanceof ChoicePattern);
        assertTrue(((ChoicePattern) ((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().getFirst()).getParticles().get(0)).getParticles().size() == 2);
        assertTrue(((ChoicePattern) ((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().getFirst()).getParticles().get(0)).getParticles().getFirst() instanceof SequencePattern);
        assertTrue(((SequencePattern) ((ChoicePattern) ((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().getFirst()).getParticles().get(0)).getParticles().getFirst()).getParticles().size() == 3);
        assertEquals(elementA, ((SequencePattern) ((ChoicePattern) ((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().getFirst()).getParticles().get(0)).getParticles().getFirst()).getParticles().get(0));
        assertTrue(((SequencePattern) ((ChoicePattern) ((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().getFirst()).getParticles().get(0)).getParticles().getFirst()).getParticles().get(1) instanceof CountingPattern);
        assertEquals(0, (int) ((CountingPattern) ((SequencePattern) ((ChoicePattern) ((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().getFirst()).getParticles().get(0)).getParticles().getFirst()).getParticles().get(1)).getMin());
        assertEquals(null, ((CountingPattern) ((SequencePattern) ((ChoicePattern) ((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().getFirst()).getParticles().get(0)).getParticles().getFirst()).getParticles().get(1)).getMax());
        assertEquals(elementA, ((CountingPattern) ((SequencePattern) ((ChoicePattern) ((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().getFirst()).getParticles().get(0)).getParticles().getFirst()).getParticles().get(1)).getParticles().getFirst());
        assertEquals(elementA, ((SequencePattern) ((ChoicePattern) ((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().getFirst()).getParticles().get(0)).getParticles().getFirst()).getParticles().get(2));
        assertEquals(elementA, ((ChoicePattern) ((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().getFirst()).getParticles().get(0)).getParticles().getLast());
        ((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().getFirst()).getParticles().get(1);
        assertTrue(((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().getFirst()).getParticles().get(1) instanceof CountingPattern);
        assertEquals(0, (int) ((CountingPattern) ((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().getFirst()).getParticles().get(1)).getMin());
        assertEquals(null, ((CountingPattern) ((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().getFirst()).getParticles().get(1)).getMax());
        assertEquals(elementA, ((CountingPattern) ((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().getFirst()).getParticles().get(1)).getParticles().getFirst());
        assertEquals(elementB, ((SequencePattern) ((ChoicePattern) ((SequencePattern) result).getParticles().get(0)).getParticles().getFirst()).getParticles().get(2));
        assertTrue(((SequencePattern) result).getParticles().get(1) instanceof CountingPattern);
        assertEquals(0, (int) ((CountingPattern) ((SequencePattern) result).getParticles().get(1)).getMin());
        assertEquals(1, (int) ((CountingPattern) ((SequencePattern) result).getParticles().get(1)).getMax());
        assertEquals(elementB, ((CountingPattern) ((SequencePattern) result).getParticles().get(1)).getParticles().getFirst());
    }
}