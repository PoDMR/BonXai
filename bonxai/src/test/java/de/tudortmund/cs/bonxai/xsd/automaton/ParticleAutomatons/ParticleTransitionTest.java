package de.tudortmund.cs.bonxai.xsd.automaton.ParticleAutomatons;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.xsd.Element;
import de.tudortmund.cs.bonxai.xsd.Group;
import java.util.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>ParticleTransition</tt> class, checks that every
 * method of this class performs properly.
 * @author Dominik Wolff
 */
public class ParticleTransitionTest extends junit.framework.TestCase {

    private ParticleState sourceState;
    private ParticleState destinationState;
    private Element elementA;
    private Element elementB;
    private AnyPattern anyPatternA;
    private AnyPattern anyPatternB;

    public ParticleTransitionTest() {
    }

    @Before
    public void setUp() {

        // Set up particle states
        sourceState = new ParticleState(0);
        destinationState = new ParticleState(1);

        // Set up elements
        elementA = new Element("{A}foo");
        elementB = new Element("{B}bar");

        // Set up any patterns
        anyPatternA = new AnyPattern(ProcessContentsInstruction.Lax, "##any");
        anyPatternB = new AnyPattern(ProcessContentsInstruction.Strict, "##any");
    }

    /**
     * Test of addAllAnyPatterns method, of class ParticleTransition.
     */
    @Test
    public void testAddAllAnyPatterns() {
        ParticleTransition instance = new ParticleTransition(sourceState, destinationState, false);
        LinkedHashSet<AnyPattern> anyPatternSet = new LinkedHashSet<AnyPattern>();
        anyPatternSet.add(anyPatternA);
        anyPatternSet.add(anyPatternB);
        instance.addAllAnyPatterns(anyPatternSet);

        LinkedHashSet<AnyPattern> expResult = anyPatternSet;
        LinkedHashSet<AnyPattern> result = instance.getAnyPatterns();

        assertEquals(expResult, result);
    }

    /**
     * Test of testAddAnyPattern method, of class ParticleTransition for an any
     * pattern, which is not present in the list.
     */
    @Test
    public void testAddAnyPatternNotInList() {
        ParticleTransition instance = new ParticleTransition(sourceState, destinationState, false);

        boolean expResult = true;
        boolean result = instance.addAnyPattern(anyPatternA);

        assertEquals(expResult, result);
        assertTrue(instance.getAnyPatterns().contains(anyPatternA));
        assertEquals(instance.getResultingParticle(), anyPatternA);
    }

    /**
     * Test of addAnyPattern method, of class ParticleTransition for an any
     * pattern, which is not present in the list but another any pattern.
     */
    @Test
    public void testAddAnyPatternNotSameInList() {
        ParticleTransition instance = new ParticleTransition(sourceState, destinationState, false);
        instance.addAnyPattern(anyPatternB);

        boolean expResult = true;
        boolean result = instance.addAnyPattern(anyPatternA);

        assertEquals(expResult, result);
        assertTrue(instance.getAnyPatterns().contains(anyPatternA));
        assertTrue(instance.getAnyPatterns().contains(anyPatternB));

        LinkedList<Particle> particles = new LinkedList<Particle>();
        particles.add(anyPatternA);
        particles.add(anyPatternB);

        assertTrue(instance.getResultingParticle() instanceof ChoicePattern);
        assertEquals(((ChoicePattern) instance.getResultingParticle()).getParticles(), particles);
    }

    /**
     * Test of addAnyPattern method, of class ParticleTransition for an any
     * pattern, which is present in the list.
     */
    @Test
    public void testAddAnyPatternInList() {
        ParticleTransition instance = new ParticleTransition(sourceState, destinationState, false);
        instance.addAnyPattern(anyPatternA);

        boolean expResult = false;
        boolean result = instance.addAnyPattern(anyPatternA);

        assertEquals(expResult, result);
        assertTrue(instance.getAnyPatterns().contains(anyPatternA));
        assertEquals(instance.getResultingParticle(), anyPatternA);
    }

    /**
     * Test of addElement method, of class ParticleTransition for an element,
     * which is not present in the list.
     */
    @Test
    public void testAddElementNotInList() {
        ParticleTransition instance = new ParticleTransition(sourceState, destinationState, false);

        boolean expResult = true;
        boolean result = instance.addElement(elementA);

        assertEquals(expResult, result);
        assertTrue(instance.getNameElementMap().containsKey("{}foo"));
        assertTrue(instance.getNameElementMap().get("{}foo").contains(elementA));
        assertTrue(instance.getElements().contains(elementA));
        assertEquals(instance.getResultingParticle(), elementA);
    }

    /**
     * Test of addElement method, of class ParticleTransition for an element,
     * which is not present in the list but another element with same name.
     */
    @Test
    public void testAddElementNotSameInList() {
        ParticleTransition instance = new ParticleTransition(sourceState, destinationState, false);
        Element tempElement = new Element("{}foo");
        instance.addElement(tempElement);

        boolean expResult = true;
        boolean result = instance.addElement(elementA);

        assertEquals(expResult, result);
        assertTrue(instance.getNameElementMap().containsKey("{}foo"));
        assertTrue(instance.getNameElementMap().get("{}foo").contains(elementA));
        assertTrue(instance.getNameElementMap().get("{}foo").contains(tempElement));
        assertTrue(instance.getElements().contains(elementA));
        assertTrue(instance.getElements().contains(tempElement));

        LinkedList<Particle> particles = new LinkedList<Particle>();
        particles.add(elementA);
        particles.add(tempElement);

        assertTrue(instance.getResultingParticle() instanceof ChoicePattern);
        assertEquals(((ChoicePattern) instance.getResultingParticle()).getParticles(), particles);
    }

    /**
     * Test of addElement method, of class ParticleTransition for an element,
     * which is present in the list.
     */
    @Test
    public void testAddElementInList() {
        ParticleTransition instance = new ParticleTransition(sourceState, destinationState, false);
        instance.addElement(elementA);

        boolean expResult = false;
        boolean result = instance.addElement(elementA);

        assertEquals(expResult, result);
        assertTrue(instance.getNameElementMap().containsKey("{}foo"));
        assertTrue(instance.getNameElementMap().get("{}foo").contains(elementA));
        assertTrue(instance.getElements().contains(elementA));
        assertEquals(instance.getResultingParticle(), elementA);
    }

    /**
     * Test of getAnyPatterns method, of class ParticleTransition for no
     * existing any patterns.
     */
    @Test
    public void testGetAnyPatternsEmpty() {
        ParticleTransition instance = new ParticleTransition(sourceState, destinationState, false);

        LinkedHashSet<AnyPattern> expResult = new LinkedHashSet<AnyPattern>();
        LinkedHashSet<AnyPattern> result = instance.getAnyPatterns();

        assertEquals(expResult, result);
    }

    /**
     * Test of getAnyPatterns method, of class ParticleTransition for existing
     * any patterns.
     */
    @Test
    public void testGetAnyPatternsNotEmpty() {
        ParticleTransition instance = new ParticleTransition(sourceState, destinationState, false);
        instance.addAnyPattern(anyPatternA);
        instance.addAnyPattern(anyPatternB);

        LinkedHashSet<AnyPattern> expResult = new LinkedHashSet<AnyPattern>();
        expResult.add(anyPatternA);
        expResult.add(anyPatternB);
        LinkedHashSet<AnyPattern> result = instance.getAnyPatterns();

        assertEquals(expResult, result);
    }

    /**
     * Test of getResultingParticle method, of class ParticleTransition for an
     * empty resulting particle.
     */
    @Test
    public void testGetResultingParticleNull() {
        ParticleTransition instance = new ParticleTransition(sourceState, destinationState, false);

        Particle expResult = null;
        Particle result = instance.getResultingParticle();

        assertEquals(expResult, result);
    }

    /**
     * Test of getResultingParticle method, of class ParticleTransition for an
     * element.
     */
    @Test
    public void testGetResultingParticleElement() {
        ParticleTransition instance = new ParticleTransition(sourceState, destinationState, false);
        instance.addElement(elementA);

        Particle expResult = elementA;
        Particle result = instance.getResultingParticle();

        assertEquals(expResult, result);
    }

    /**
     * Test of getResultingParticle method, of class ParticleTransition for a
     * specified sequence pattern.
     */
    @Test
    public void testGetResultingParticleSequencePattern() {
        ParticleTransition instance = new ParticleTransition(sourceState, destinationState, false);

        SequencePattern sequencePattern = new SequencePattern();
        sequencePattern.addParticle(elementA);
        ChoicePattern choicePattern = new ChoicePattern();
        choicePattern.addParticle(elementB);
        sequencePattern.addParticle(choicePattern);

        instance.setResultingParticle(sequencePattern);

        Particle expResult = sequencePattern;
        Particle result = instance.getResultingParticle();

        assertEquals(expResult, result);
    }

    /**
     * Test of hasEpsilon method, of class ParticleTransition.
     */
    @Test
    public void testHasEpsilon() {
        ParticleTransition instance = new ParticleTransition(sourceState, destinationState, false);

        boolean expResult = false;
        boolean result = instance.hasEpsilon();

        assertEquals(expResult, result);
    }

    /**
     * Test of setEpsilon method, of class ParticleTransition.
     */
    @Test
    public void testSetEpsilon() {
        ParticleTransition instance = new ParticleTransition(sourceState, destinationState, false);
        instance.setEpsilon(true);

        boolean expResult = true;
        boolean result = instance.hasEpsilon();

        assertEquals(expResult, result);
    }

    /**
     * Test of setResultingParticle method, of class ParticleTransition for a
     * specified particle A.
     */
    @Test
    public void testSetResultingParticleTestParticleA() {
        ParticleTransition instance = new ParticleTransition(sourceState, destinationState, false);

        SequencePattern sequencePattern = new SequencePattern();
        ChoicePattern choicePattern = new ChoicePattern();
        ElementRef elementRef = new ElementRef(new SymbolTableRef<Element>("{A}foo", elementA));
        choicePattern.addParticle(elementRef);
        choicePattern.addParticle(anyPatternA);
        Group group = new Group("{A}group", choicePattern);
        GroupRef groupRef = new GroupRef(new SymbolTableRef<de.tudortmund.cs.bonxai.common.Group>("{A}group", group));
        sequencePattern.addParticle(groupRef);
        sequencePattern.addParticle(elementB);
        sequencePattern.addParticle(anyPatternB);

        instance.setResultingParticle(sequencePattern);

        Particle expResult = sequencePattern;
        Particle result = instance.getResultingParticle();

        assertEquals(expResult, result);
        assertTrue(instance.getElements().contains(elementA));
        assertTrue(instance.getElements().contains(elementB));
        assertTrue(instance.getAnyPatterns().contains(anyPatternA));
        assertTrue(instance.getAnyPatterns().contains(anyPatternB));
    }

    /**
     * Test of setResultingParticle method, of class ParticleTransition for a
     * specified particle B.
     */
    @Test
    public void testSetResultingParticleTestParticleB() {
        ParticleTransition instance = new ParticleTransition(sourceState, destinationState, false);

        SequencePattern sequencePattern = new SequencePattern();
        ChoicePattern choicePattern = new ChoicePattern();
        choicePattern.addParticle(elementA);
        choicePattern.addParticle(anyPatternA);
        choicePattern.addParticle(elementB);
        choicePattern.addParticle(anyPatternB);
        choicePattern.addParticle(new SequencePattern());
        sequencePattern.addParticle(choicePattern);

        instance.setResultingParticle(sequencePattern);

        Particle expResult = sequencePattern;
        Particle result = instance.getResultingParticle();

        assertEquals(expResult, result);
        assertTrue(instance.getElements().contains(elementA));
        assertTrue(instance.getElements().contains(elementB));
        assertTrue(instance.getAnyPatterns().contains(anyPatternA));
        assertTrue(instance.getAnyPatterns().contains(anyPatternB));
    }

    /**
     * Test of toString method, of class ParticleTransition.
     */
    @Test
    public void testToString() {
        ParticleTransition instance = new ParticleTransition(sourceState, destinationState, true);
        instance.addElement(elementA);
        instance.addElement(elementB);
        instance.addAnyPattern(anyPatternA);
        instance.addAnyPattern(anyPatternB);

        String expResult = "\"(0)\"->\"(1)\"[label=\"({A}foo,{B}bar,de.tudortmund.cs.bonxai.common.AnyPattern,de.tudortmund.cs.bonxai.common.AnyPattern,epsilon)\"]\n";
        String result = instance.toString();

        assertEquals(expResult, result);
    }
}