package de.tudortmund.cs.bonxai.xsd.automaton.ParticleAutomatons;

import de.tudortmund.cs.bonxai.xsd.Element;
import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.xsd.Group;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.SimpleType;
import de.tudortmund.cs.bonxai.xsd.Type;
import de.tudortmund.cs.bonxai.xsd.automaton.*;
import de.tudortmund.cs.bonxai.xsd.setOperations.difference.ParticleDifferenceGenerator;
import de.tudortmund.cs.bonxai.xsd.setOperations.intersection.ParticleIntersectionGenerator;
import java.util.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>ParticleAutomatonFactory</tt> class, checks that every
 * method of this class performs properly.
 * @author Dominik Wolff
 */
public class ParticleAutomatonFactoryTest extends junit.framework.TestCase {

    private SequencePattern particleA;
    private SequencePattern particleB;
    private LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap;
    private Element elementA;
    private Element elementB;
    private Element elementC;
    private Element elementA2;
    private Element elementB2;
    private Element elementC2;
    private Element elementD;
    private Element elementE;

    public ParticleAutomatonFactoryTest() {
    }

    @Before
    public void setUp() {

        // Set up particle A
        particleA = new SequencePattern();

        elementA = new Element("{A}a");
        CountingPattern countingPatternA = new CountingPattern(0, null);
        countingPatternA.addParticle(elementA);

        elementB = new Element("{A}b");
        elementC = new Element("{A}c");
        ChoicePattern choicePattern = new ChoicePattern();
        choicePattern.addParticle(elementB);
        choicePattern.addParticle(elementC);
        CountingPattern countingPatternChoice = new CountingPattern(0, 1);
        countingPatternChoice.addParticle(choicePattern);

        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Strict, "A");

        particleA.addParticle(countingPatternA);
        particleA.addParticle(countingPatternChoice);
        particleA.addParticle(anyPatternA);

        XSDSchema schema = new XSDSchema("A");
        schema.addElement(new SymbolTableRef<Element>("{A}a", elementA));
        schema.addElement(new SymbolTableRef<Element>("{A}b", elementB));

        anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, schema);

        // Set up particle B
        particleB = new SequencePattern();

        elementA2 = new Element("{A}a");
        CountingPattern countingPatternA2 = new CountingPattern(0, 0);
        countingPatternA2.addParticle(elementA2);

        elementB2 = new Element("{A}b");
        elementC2 = new Element("{A}c");
        ChoicePattern choicePattern2 = new ChoicePattern();
        choicePattern2.addParticle(elementB2);
        choicePattern2.addParticle(elementC2);
        CountingPattern countingPatternChoice2 = new CountingPattern(1, 2);
        countingPatternChoice2.addParticle(choicePattern2);
        Group group = new Group("{}group", countingPatternChoice2);
        GroupRef groupRef = new GroupRef(new SymbolTableRef<de.tudortmund.cs.bonxai.common.Group>("{}group", group));

        elementD = new Element("{A}d");
        ElementRef elementRefD = new ElementRef(new SymbolTableRef<Element>("{A}d", elementD));

        elementE = new Element("{A}e");
        elementE.setAbstract(true);
        ElementRef elementRefE = new ElementRef(new SymbolTableRef<Element>("{A}e", elementE));

        particleB.addParticle(countingPatternA2);
        particleB.addParticle(groupRef);
        particleB.addParticle(elementRefD);
        particleB.addParticle(elementRefE);
    }

    /**
     * Test of buildProductAutomatonDifference method, of class ParticleAutomatonFactory.
     */
    @Test
    public void testBuildProductAutomatonDifference() throws Exception {
        Element elementa1 = new Element("{A}a");
        Element elementb1 = new Element("{A}b");
        elementa1.setForm(XSDSchema.Qualification.qualified);
        elementb1.setForm(XSDSchema.Qualification.qualified);
        SequencePattern sequencePattern1 = new SequencePattern();
        CountingPattern countingPattern1 = new CountingPattern(1, null);
        CountingPattern countingPattern2 = new CountingPattern(0, 1);
        countingPattern1.addParticle(elementa1);
        countingPattern2.addParticle(elementb1);
        sequencePattern1.addParticle(countingPattern1);
        sequencePattern1.addParticle(countingPattern2);

        ParticleAutomatonFactory instance = new ParticleAutomatonFactory();
        ParticleAutomaton minuendAutomaton = instance.buildSubsetParticleAutomaton(instance.buildParticleAutomaton(particleA, anyPatternOldSchemaMap));
        ParticleAutomaton subtrahendAutomaton = instance.buildSubsetParticleAutomaton(instance.buildParticleAutomaton(sequencePattern1, anyPatternOldSchemaMap));
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        elementOldTypeMap.put("{A}a", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        elementOldTypeMap.put("{A}b", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        elementOldTypeMap.put("{}c", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementa1, new XSDSchema("A"));
        elementOldSchemaMap.put(elementb1, new XSDSchema("A"));
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        elementOldSchemaMap.put(elementC, new XSDSchema("A"));
        elementOldSchemaMap.put(elementD, new XSDSchema("A"));
        elementOldSchemaMap.put(elementE, new XSDSchema("A"));
        ParticleDifferenceGenerator particleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, elementOldSchemaMap, elementOldTypeMap, null, null, null, null);
        particleDifferenceGenerator.generateNewParticleDifference(null, null, null, elementOldTypeMap);

        ProductParticleAutomaton result = instance.buildProductAutomatonDifference(minuendAutomaton, subtrahendAutomaton, particleDifferenceGenerator);

        for (Iterator<ParticleState> it = result.getStates().iterator(); it.hasNext();) {
            ParticleState particleState = (ParticleState) it.next();

            if (particleState.getStateNumber() == 1) {
                assertFalse(result.getFinalStates().contains(particleState));
                assertTrue(particleState.getInTransitions().isEmpty());
                assertTrue(particleState.getOutTransitions().size() == 3);
                int counter = 0;

                for (Iterator<Transition> it2 = particleState.getOutTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}a", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 2) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}b", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 3) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}c", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
            }
            if (particleState.getStateNumber() == 2) {
                assertFalse(result.getFinalStates().contains(particleState));
                assertTrue(particleState.getInTransitions().size() == 2);
                int counter = 0;

                for (Iterator<Transition> it2 = particleState.getInTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}a", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 2) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}a", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
                assertTrue(particleState.getOutTransitions().size() == 3);
                counter = 0;

                for (Iterator<Transition> it2 = particleState.getOutTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 2) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}b", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 3) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}c", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                                        if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}a", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
            }
            if (particleState.getStateNumber() == 3) {
                assertTrue(result.getFinalStates().contains(particleState));
                assertTrue(particleState.getInTransitions().size() == 1);
                int counter = 0;

                for (Iterator<Transition> it2 = particleState.getInTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}b", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
                assertTrue(particleState.getOutTransitions().size() == 1);
                counter = 0;

                for (Iterator<Transition> it2 = particleState.getOutTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 2);
                        assertEquals("{A}a", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertEquals("{A}b", ((Element) particleTransition.getElements().toArray()[1]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[1]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
            }
            if (particleState.getStateNumber() == 4) {
                assertFalse(result.getFinalStates().contains(particleState));
                assertTrue(particleState.getInTransitions().size() == 2);
                int counter = 0;

                for (Iterator<Transition> it2 = particleState.getInTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}c", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 2) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}c", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
                assertTrue(particleState.getOutTransitions().size() == 1);
                counter = 0;

                for (Iterator<Transition> it2 = particleState.getOutTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 2);
                        assertEquals("{A}a", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertEquals("{A}b", ((Element) particleTransition.getElements().toArray()[1]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[1]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
            }
            if (particleState.getStateNumber() == 5) {
                assertTrue(result.getFinalStates().contains(particleState));
                assertTrue(particleState.getInTransitions().size() == 3);
                int counter = 0;

                for (Iterator<Transition> it2 = particleState.getInTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 2);
                        assertEquals("{A}a", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertEquals("{A}b", ((Element) particleTransition.getElements().toArray()[1]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[1]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 2) {
                        assertTrue(particleTransition.getElements().size() == 2);
                        assertEquals("{A}a", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertEquals("{A}b", ((Element) particleTransition.getElements().toArray()[1]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[1]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 3) {
                        assertTrue(particleTransition.getElements().size() == 2);
                        assertEquals("{A}a", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertEquals("{A}b", ((Element) particleTransition.getElements().toArray()[1]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[1]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
                assertTrue(particleState.getOutTransitions().isEmpty());
            }
            if (particleState.getStateNumber() == 6) {
                assertFalse(result.getFinalStates().contains(particleState));
                assertTrue(particleState.getInTransitions().size() == 1);
                int counter = 0;

                for (Iterator<Transition> it2 = particleState.getInTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}b", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
                assertTrue(particleState.getOutTransitions().size() == 1);
                counter = 0;

                for (Iterator<Transition> it2 = particleState.getOutTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 2);
                        assertEquals("{A}a", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertEquals("{A}b", ((Element) particleTransition.getElements().toArray()[1]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[1]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
            }
        }
    }

    /**
     * Test of buildProductAutomatonIntersection method, of class ParticleAutomatonFactory.
     */
    @Test
    public void testBuildProductAutomatonIntersection() throws Exception {
        Element elementa1 = new Element("{A}a");
        Element elementa2 = new Element("{A}a");
        Element elementa3 = new Element("{A}a");
        Element elementb1 = new Element("{A}b");
        Element elementb2 = new Element("{A}b");
        Element elementc1 = new Element("{A}c");
        Element elementc2 = new Element("{A}c");
        elementa1.setForm(XSDSchema.Qualification.qualified);
        elementa2.setForm(XSDSchema.Qualification.qualified);
        elementa3.setForm(XSDSchema.Qualification.qualified);
        elementb1.setForm(XSDSchema.Qualification.qualified);
        elementb2.setForm(XSDSchema.Qualification.qualified);
        SequencePattern sequencePattern = new SequencePattern();
        SequencePattern sequencePattern2 = new SequencePattern();
        SequencePattern sequencePattern3 = new SequencePattern();
        sequencePattern2.addParticle(elementb1);
        sequencePattern2.addParticle(elementa3);
        sequencePattern3.addParticle(elementc1);
        sequencePattern3.addParticle(elementb2);
        ChoicePattern choicePattern1 = new ChoicePattern();
        ChoicePattern choicePattern2 = new ChoicePattern();
        choicePattern2.addParticle(sequencePattern2);
        choicePattern2.addParticle(sequencePattern3);
        sequencePattern.addParticle(elementa1);
        sequencePattern.addParticle(elementa2);
        sequencePattern.addParticle(choicePattern2);
        choicePattern1.addParticle(elementc2);
        choicePattern1.addParticle(sequencePattern);

        ParticleAutomatonFactory instance = new ParticleAutomatonFactory();
        ParticleAutomaton particleAutomaton1 = instance.buildSubsetParticleAutomaton(instance.buildParticleAutomaton(particleA, anyPatternOldSchemaMap));
        ParticleAutomaton particleAutomaton2 = instance.buildSubsetParticleAutomaton(instance.buildParticleAutomaton(choicePattern1, anyPatternOldSchemaMap));
        LinkedList<ParticleAutomaton> particleAutomatons = new LinkedList<ParticleAutomaton>();
        particleAutomatons.add(particleAutomaton1);
        particleAutomatons.add(particleAutomaton2);
        LinkedList<Particle> particles = new LinkedList<Particle>();
        particles.add(particleA);
        particles.add(new SequencePattern());
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        elementTypeMap.put("{A}a", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        elementTypeMap.put("{A}b", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        elementTypeMap.put("{A}c", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        elementTypeMap.put("{}a", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        elementTypeMap.put("{}b", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        elementTypeMap.put("{}c", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementa1, new XSDSchema("A"));
        elementOldSchemaMap.put(elementa2, new XSDSchema("A"));
        elementOldSchemaMap.put(elementa3, new XSDSchema("A"));
        elementOldSchemaMap.put(elementb1, new XSDSchema("A"));
        elementOldSchemaMap.put(elementb2, new XSDSchema("A"));
        elementOldSchemaMap.put(elementc1, new XSDSchema("A"));
        elementOldSchemaMap.put(elementc2, new XSDSchema("A"));
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        elementOldSchemaMap.put(elementC, new XSDSchema("A"));
        elementOldSchemaMap.put(elementD, new XSDSchema("A"));
        elementOldSchemaMap.put(elementE, new XSDSchema("A"));
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, elementTypeMap, null, null, null);
        particleIntersectionGenerator.generateParticleIntersection(particles, elementTypeMap);

        ProductParticleAutomaton result = instance.buildProductAutomatonIntersection(particleAutomatons, particleIntersectionGenerator);

        for (Iterator<ParticleState> it = result.getStates().iterator(); it.hasNext();) {
            ParticleState particleState = (ParticleState) it.next();

            if (particleState.getStateNumber() == 1) {
                assertFalse(result.getFinalStates().contains(particleState));
                assertTrue(particleState.getInTransitions().isEmpty());
                assertTrue(particleState.getOutTransitions().size() == 2);
                int counter = 0;

                for (Iterator<Transition> it2 = particleState.getOutTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}a", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 2) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}c", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
            }
            if (particleState.getStateNumber() == 2) {
                assertFalse(result.getFinalStates().contains(particleState));
                assertTrue(particleState.getInTransitions().size() == 1);
                int counter = 0;

                for (Iterator<Transition> it2 = particleState.getInTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}a", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
                assertTrue(particleState.getOutTransitions().size() == 1);
                counter = 0;

                for (Iterator<Transition> it2 = particleState.getOutTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}a", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
            }
            if (particleState.getStateNumber() == 3) {
                assertFalse(result.getFinalStates().contains(particleState));
                assertTrue(particleState.getInTransitions().size() == 1);
                int counter = 0;

                for (Iterator<Transition> it2 = particleState.getInTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}c", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
                assertTrue(particleState.getOutTransitions().isEmpty());
            }
            if (particleState.getStateNumber() == 4) {
                assertFalse(result.getFinalStates().contains(particleState));
                assertTrue(particleState.getInTransitions().size() == 1);
                int counter = 0;

                for (Iterator<Transition> it2 = particleState.getInTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}a", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
                assertTrue(particleState.getOutTransitions().size() == 2);
                counter = 0;

                for (Iterator<Transition> it2 = particleState.getOutTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}b", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 2) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}c", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
            }
            if (particleState.getStateNumber() == 5) {
                assertFalse(result.getFinalStates().contains(particleState));
                assertTrue(particleState.getInTransitions().size() == 1);
                int counter = 0;

                for (Iterator<Transition> it2 = particleState.getInTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}b", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
                assertTrue(particleState.getOutTransitions().size() == 1);
                counter = 0;

                for (Iterator<Transition> it2 = particleState.getOutTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}a", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
            }
            if (particleState.getStateNumber() == 6) {
                assertFalse(result.getFinalStates().contains(particleState));
                assertTrue(particleState.getInTransitions().size() == 1);
                int counter = 0;

                for (Iterator<Transition> it2 = particleState.getInTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}c", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
                assertTrue(particleState.getOutTransitions().size() == 1);
                counter = 0;

                for (Iterator<Transition> it2 = particleState.getOutTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}b", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
            }

            if (particleState.getStateNumber() == 7) {
                assertTrue(result.getFinalStates().contains(particleState));
                assertTrue(particleState.getInTransitions().size() == 2);
                int counter = 0;

                for (Iterator<Transition> it2 = particleState.getInTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}b", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 2) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}a", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
                assertTrue(particleState.getOutTransitions().isEmpty());
            }
        }
    }

    /**
     * Test of buildParticleAutomaton method, of class ParticleAutomatonFactory
     * for particle A.
     */
    @Test
    public void testBuildParticleAutomatonParticleA() {
        ParticleAutomatonFactory instance = new ParticleAutomatonFactory();
        ParticleAutomaton result = instance.buildParticleAutomaton(particleA, anyPatternOldSchemaMap);

        for (Iterator<ParticleState> it = result.getStates().iterator(); it.hasNext();) {
            ParticleState particleState = (ParticleState) it.next();

            if (particleState.getStateNumber() == 1) {
                assertEquals(result.getStartState(), particleState);
                assertTrue(particleState.getInTransitions().isEmpty());
                assertTrue(particleState.getOutTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().isEmpty());
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).hasEpsilon());
            }
            if (particleState.getStateNumber() == 4) {
                assertTrue(particleState.getInTransitions().size() == 2);

                for (Iterator<Transition> it2 = particleState.getInTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();

                    assertTrue(particleTransition.getElements().isEmpty());
                    assertTrue(particleTransition.getAnyPatterns().isEmpty());
                    assertTrue(particleTransition.hasEpsilon());
                }
                assertTrue(particleState.getOutTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().size() == 1);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().contains(elementA));
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).hasEpsilon());
            }
            if (particleState.getStateNumber() == 5) {
                assertTrue(particleState.getInTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().size() == 1);
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().contains(elementA));
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).hasEpsilon());
                assertTrue(particleState.getOutTransitions().size() == 2);

                for (Iterator<Transition> it2 = particleState.getOutTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();

                    assertTrue(particleTransition.getElements().isEmpty());
                    assertTrue(particleTransition.getAnyPatterns().isEmpty());
                    assertTrue(particleTransition.hasEpsilon());
                }
            }
            if (particleState.getStateNumber() == 3) {
                assertTrue(particleState.getInTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().isEmpty());
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).hasEpsilon());
                assertTrue(particleState.getOutTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().isEmpty());
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).hasEpsilon());
            }
            if (particleState.getStateNumber() == 7) {
                assertTrue(particleState.getInTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().isEmpty());
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).hasEpsilon());
                assertTrue(particleState.getOutTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().size() == 2);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().contains(elementB));
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().contains(elementC));
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).hasEpsilon());
            }
            if (particleState.getStateNumber() == 8) {
                assertTrue(particleState.getInTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().size() == 2);
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().contains(elementB));
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().contains(elementC));
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).hasEpsilon());
                assertTrue(particleState.getOutTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().isEmpty());
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).hasEpsilon());
            }
            if (particleState.getStateNumber() == 6) {
                assertTrue(particleState.getInTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().isEmpty());
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).hasEpsilon());
                assertTrue(particleState.getOutTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().size() == 2);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().contains(elementA));
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().contains(elementB));
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertFalse(((ParticleTransition) particleState.getOutTransitions().iterator().next()).hasEpsilon());
            }
            if (particleState.getStateNumber() == 2) {
                assertTrue(result.getFinalStates().contains(particleState));
                assertTrue(particleState.getInTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().size() == 2);
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().contains(elementA));
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().contains(elementB));
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertFalse(((ParticleTransition) particleState.getInTransitions().iterator().next()).hasEpsilon());
                assertTrue(particleState.getOutTransitions().isEmpty());
            }
        }
    }

    /**
     * Test of buildParticleAutomaton method, of class ParticleAutomatonFactory
     * for particle B.
     */
    @Test
    public void testBuildParticleAutomatonParticleB() {
        ParticleAutomatonFactory instance = new ParticleAutomatonFactory();
        ParticleAutomaton result = instance.buildParticleAutomaton(particleB, anyPatternOldSchemaMap);

        for (Iterator<ParticleState> it = result.getStates().iterator(); it.hasNext();) {
            ParticleState particleState = (ParticleState) it.next();

            if (particleState.getStateNumber() == 1) {
                assertEquals(result.getStartState(), particleState);
                assertTrue(particleState.getInTransitions().isEmpty());
                assertTrue(particleState.getOutTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().isEmpty());
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).hasEpsilon());
            }
            if (particleState.getStateNumber() == 4) {
                assertTrue(particleState.getInTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().isEmpty());
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).hasEpsilon());
                assertTrue(particleState.getOutTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().isEmpty());
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).hasEpsilon());
            }
            if (particleState.getStateNumber() == 5) {
                assertTrue(particleState.getInTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().isEmpty());
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).hasEpsilon());
                assertTrue(particleState.getOutTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().isEmpty());
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).hasEpsilon());
            }
            if (particleState.getStateNumber() == 3) {
                assertTrue(particleState.getInTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().isEmpty());
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).hasEpsilon());
                assertTrue(particleState.getOutTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().isEmpty());
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).hasEpsilon());
            }
            if (particleState.getStateNumber() == 7) {
                assertTrue(particleState.getInTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().isEmpty());
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).hasEpsilon());
                assertTrue(particleState.getOutTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().size() == 2);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().contains(elementB2));
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().contains(elementC2));
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertFalse(((ParticleTransition) particleState.getOutTransitions().iterator().next()).hasEpsilon());
            }
            if (particleState.getStateNumber() == 9) {
                assertTrue(particleState.getInTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().size() == 2);
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().contains(elementB2));
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().contains(elementC2));
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertFalse(((ParticleTransition) particleState.getInTransitions().iterator().next()).hasEpsilon());
                assertTrue(particleState.getOutTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().size() == 2);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().contains(elementB2));
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().contains(elementC2));
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).hasEpsilon());
            }
            if (particleState.getStateNumber() == 8) {
                assertTrue(particleState.getInTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().size() == 2);
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().contains(elementB2));
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().contains(elementC2));
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).hasEpsilon());
                assertTrue(particleState.getOutTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().isEmpty());
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).hasEpsilon());
            }
            if (particleState.getStateNumber() == 6) {
                assertTrue(particleState.getInTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().isEmpty());
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).hasEpsilon());
                assertTrue(particleState.getOutTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().size() == 1);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().contains(elementD));
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertFalse(((ParticleTransition) particleState.getOutTransitions().iterator().next()).hasEpsilon());
            }
            if (particleState.getStateNumber() == 10) {
                assertTrue(particleState.getInTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().size() == 1);
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().contains(elementD));
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertFalse(((ParticleTransition) particleState.getInTransitions().iterator().next()).hasEpsilon());
                assertTrue(particleState.getOutTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().isEmpty());
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).hasEpsilon());
            }
            if (particleState.getStateNumber() == 2) {
                assertTrue(result.getFinalStates().contains(particleState));
                assertTrue(particleState.getInTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().isEmpty());
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).hasEpsilon());
                assertTrue(particleState.getOutTransitions().isEmpty());
            }
        }
    }

    /**
     * Test of buildSubsetParticleAutomaton method, of class 
     * ParticleAutomatonFactory for particle A.
     */
    @Test
    public void testBuildSubsetParticleAutomatonParticleA() throws Exception {
        ParticleAutomatonFactory instance = new ParticleAutomatonFactory();
        ParticleAutomaton particleAutomaton = instance.buildParticleAutomaton(particleA, anyPatternOldSchemaMap);
        SubsetParticleAutomaton result = instance.buildSubsetParticleAutomaton(particleAutomaton);

        for (Iterator<ParticleState> it = result.getStates().iterator(); it.hasNext();) {
            ParticleState particleState = (ParticleState) it.next();

            if (particleState.getStateNumber() == 1) {
                assertEquals(result.getStartState(), particleState);
                assertTrue(particleState.getInTransitions().isEmpty());
                assertTrue(particleState.getOutTransitions().size() == 3);
                int counter = 0;

                for (Iterator<Transition> it2 = particleState.getOutTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertTrue(particleTransition.getElements().contains(elementA));
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 2) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertTrue(particleTransition.getElements().contains(elementB));
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 3) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertTrue(particleTransition.getElements().contains(elementC));
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
            }
            if (particleState.getStateNumber() == 2) {
                assertTrue(result.getFinalStates().contains(particleState));
                assertTrue(particleState.getInTransitions().size() == 2);
                int counter = 0;

                for (Iterator<Transition> it2 = particleState.getInTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertTrue(particleTransition.getElements().contains(elementA));
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 2) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertTrue(particleTransition.getElements().contains(elementA));
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
                assertTrue(particleState.getOutTransitions().size() == 3);
                counter = 0;

                for (Iterator<Transition> it2 = particleState.getOutTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertTrue(particleTransition.getElements().contains(elementA));
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 2) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertTrue(particleTransition.getElements().contains(elementB));
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 3) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertTrue(particleTransition.getElements().contains(elementC));
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
            }
            if (particleState.getStateNumber() == 3) {
                assertTrue(result.getFinalStates().contains(particleState));
                assertTrue(particleState.getInTransitions().size() == 2);
                int counter = 0;

                for (Iterator<Transition> it2 = particleState.getInTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertTrue(particleTransition.getElements().contains(elementB));
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 2) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertTrue(particleTransition.getElements().contains(elementB));
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
                assertTrue(particleState.getOutTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().size() == 2);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().contains(elementA));
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().contains(elementB));
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertFalse(((ParticleTransition) particleState.getOutTransitions().iterator().next()).hasEpsilon());
            }
            if (particleState.getStateNumber() == 4) {
                assertFalse(result.getFinalStates().contains(particleState));
                assertTrue(particleState.getInTransitions().size() == 2);
                int counter = 0;

                for (Iterator<Transition> it2 = particleState.getInTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertTrue(particleTransition.getElements().contains(elementC));
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 2) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertTrue(particleTransition.getElements().contains(elementC));
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
                assertTrue(particleState.getOutTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().size() == 2);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().contains(elementA));
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().contains(elementB));
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertFalse(((ParticleTransition) particleState.getOutTransitions().iterator().next()).hasEpsilon());
            }
            if (particleState.getStateNumber() == 5) {
                assertTrue(result.getFinalStates().contains(particleState));
                assertTrue(particleState.getInTransitions().size() == 2);
                int counter = 0;

                for (Iterator<Transition> it2 = particleState.getInTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 2);
                        assertTrue(particleTransition.getElements().contains(elementA));
                        assertTrue(particleTransition.getElements().contains(elementB));
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 2) {
                        assertTrue(particleTransition.getElements().size() == 2);
                        assertTrue(particleTransition.getElements().contains(elementA));
                        assertTrue(particleTransition.getElements().contains(elementB));
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
                assertTrue(particleState.getOutTransitions().isEmpty());
            }
        }
    }

    /**
     * Test of buildSubsetParticleAutomaton method, of class
     * ParticleAutomatonFactory for particle B.
     */
    @Test
    public void testBuildSubsetParticleAutomatonParticleB() throws Exception {
        ParticleAutomatonFactory instance = new ParticleAutomatonFactory();
        ParticleAutomaton particleAutomaton = instance.buildParticleAutomaton(particleB, anyPatternOldSchemaMap);
        SubsetParticleAutomaton result = instance.buildSubsetParticleAutomaton(particleAutomaton);

        for (Iterator<ParticleState> it = result.getStates().iterator(); it.hasNext();) {
            ParticleState particleState = (ParticleState) it.next();

            if (particleState.getStateNumber() == 1) {
                assertFalse(result.getFinalStates().contains(particleState));
                assertTrue(particleState.getInTransitions().isEmpty());
                assertTrue(particleState.getOutTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().size() == 2);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().contains(elementB2));
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().contains(elementC2));
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertFalse(((ParticleTransition) particleState.getOutTransitions().iterator().next()).hasEpsilon());
            }
            if (particleState.getStateNumber() == 2) {
                assertFalse(result.getFinalStates().contains(particleState));
                assertTrue(particleState.getInTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().size() == 2);
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().contains(elementB2));
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().contains(elementC2));
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertFalse(((ParticleTransition) particleState.getInTransitions().iterator().next()).hasEpsilon());
                assertTrue(particleState.getOutTransitions().size() == 2);
                int counter = 0;

                for (Iterator<Transition> it2 = particleState.getOutTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 2);
                        assertTrue(particleTransition.getElements().contains(elementB2));
                        assertTrue(particleTransition.getElements().contains(elementC2));
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 2) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertTrue(particleTransition.getElements().contains(elementD));
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
            }
            if (particleState.getStateNumber() == 3) {
                assertFalse(result.getFinalStates().contains(particleState));
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().size() == 2);
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().contains(elementB2));
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().contains(elementC2));
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertFalse(((ParticleTransition) particleState.getInTransitions().iterator().next()).hasEpsilon());
                assertTrue(particleState.getOutTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().size() == 1);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().contains(elementD));
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertFalse(((ParticleTransition) particleState.getOutTransitions().iterator().next()).hasEpsilon());
            }
            if (particleState.getStateNumber() == 4) {
                assertTrue(result.getFinalStates().contains(particleState));
                assertTrue(particleState.getInTransitions().size() == 2);
                int counter = 0;

                for (Iterator<Transition> it2 = particleState.getInTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertTrue(particleTransition.getElements().contains(elementD));
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 2) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertTrue(particleTransition.getElements().contains(elementD));
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
                assertTrue(particleState.getOutTransitions().isEmpty());
            }
        }
    }

    /**
     * Test of buildSubstitutionParticleAutomaton method, of class ParticleAutomatonFactory.
     */
    @Test
    public void testBuildSubstitutionParticleAutomaton() throws Exception {
        SequencePattern sequencePattern = new SequencePattern();
        Element elementC3 = new Element("{A}c");
        Element elementD2 = new Element("{A}d");
        sequencePattern.addParticle(elementD2);
        sequencePattern.addParticle(elementC3);

        Particle minuendParticle = particleB;
        Particle subtrahendParticle = sequencePattern;

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        elementOldTypeMap.put("{}a", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        elementOldTypeMap.put("{}b", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        elementOldTypeMap.put("{}c", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        elementOldTypeMap.put("{}d", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        elementOldSchemaMap.put(elementA2, oldSchema);
        elementOldSchemaMap.put(elementB2, oldSchema);
        elementOldSchemaMap.put(elementC2, oldSchema);
        elementOldSchemaMap.put(elementD, oldSchema);
        ParticleDifferenceGenerator particleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, elementOldSchemaMap, elementOldTypeMap, null, null, null, null);
        particleDifferenceGenerator.generateNewParticleDifference(null, null, null, elementOldTypeMap);
        LinkedHashMap<String, SymbolTableRef<Type>> elementNameTypeSymbolTableRefMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        elementNameTypeSymbolTableRefMap.put("{}a", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        elementNameTypeSymbolTableRefMap.put("{}b", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        elementNameTypeSymbolTableRefMap.put("{}c", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        elementNameTypeSymbolTableRefMap.put("{}d", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        ParticleAutomatonFactory instance = new ParticleAutomatonFactory();

        ParticleAutomaton result = instance.buildSubstitutionParticleAutomaton(minuendParticle, subtrahendParticle, particleDifferenceGenerator, elementNameTypeSymbolTableRefMap);

        for (Iterator<ParticleState> it = result.getStates().iterator(); it.hasNext();) {
            ParticleState particleState = (ParticleState) it.next();

            if (particleState.getStateNumber() == 1) {
                assertFalse(result.getFinalStates().contains(particleState));
                assertTrue(particleState.getInTransitions().isEmpty());

                assertTrue(particleState.getOutTransitions().size() == 2);
                int counter = 0;

                for (Iterator<Transition> it2 = particleState.getOutTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 2);
                        assertEquals("{A}b", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeB", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertEquals("{A}c", ((Element) particleTransition.getElements().toArray()[1]).getName());
                        assertEquals("{A}simpleTypeB", ((Element) particleTransition.getElements().toArray()[1]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 2) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}c", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
            }
            if (particleState.getStateNumber() == 2) {
                assertFalse(result.getFinalStates().contains(particleState));
                assertTrue(particleState.getInTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().size() == 2);
                assertEquals("{A}b", ((Element) ((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().toArray()[0]).getName());
                assertEquals("{A}simpleTypeB", ((Element) ((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().toArray()[0]).getType().getName());
                assertEquals("{A}c", ((Element) ((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().toArray()[1]).getName());
                assertEquals("{A}simpleTypeB", ((Element) ((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().toArray()[1]).getType().getName());
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertFalse(((ParticleTransition) particleState.getInTransitions().iterator().next()).hasEpsilon());
                assertTrue(particleState.getOutTransitions().size() == 4);
                int counter = 0;

                for (Iterator<Transition> it2 = particleState.getOutTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 2);
                        assertEquals("{A}b", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeB", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertEquals("{A}c", ((Element) particleTransition.getElements().toArray()[1]).getName());
                        assertEquals("{A}simpleTypeB", ((Element) particleTransition.getElements().toArray()[1]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 2) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}d", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeB", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 3) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}c", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 4) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}d", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
            }
            if (particleState.getStateNumber() == 3) {
                assertFalse(result.getFinalStates().contains(particleState));
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().size() == 2);
                assertEquals("{A}b", ((Element) ((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().toArray()[0]).getName());
                assertEquals("{A}simpleTypeB", ((Element) ((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().toArray()[0]).getType().getName());
                assertEquals("{A}c", ((Element) ((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().toArray()[1]).getName());
                assertEquals("{A}simpleTypeB", ((Element) ((ParticleTransition) particleState.getInTransitions().iterator().next()).getElements().toArray()[1]).getType().getName());
                assertTrue(((ParticleTransition) particleState.getInTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertFalse(((ParticleTransition) particleState.getInTransitions().iterator().next()).hasEpsilon());
                assertTrue(particleState.getOutTransitions().size() == 2);
                int counter = 0;

                for (Iterator<Transition> it2 = particleState.getOutTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 2) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}d", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}d", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeB", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }

                }
            }
            if (particleState.getStateNumber() == 4) {
                assertFalse(result.getFinalStates().contains(particleState));
                assertTrue(particleState.getInTransitions().size() == 2);
                int counter = 0;

                for (Iterator<Transition> it2 = particleState.getInTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}d", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeB", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 2) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}d", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeB", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
                assertTrue(particleState.getOutTransitions().isEmpty());
            }
            if (particleState.getStateNumber() == 6) {
                assertFalse(result.getFinalStates().contains(particleState));
                assertTrue(particleState.getInTransitions().isEmpty());
                assertTrue(particleState.getOutTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().size() == 2);
                assertEquals("{A}b", ((Element) ((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().toArray()[0]).getName());
                assertEquals("{A}simpleTypeB", ((Element) ((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().toArray()[0]).getType().getName());
                assertEquals("{A}c", ((Element) ((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().toArray()[1]).getName());
                assertEquals("{A}simpleTypeB", ((Element) ((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().toArray()[1]).getType().getName());
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertFalse(((ParticleTransition) particleState.getOutTransitions().iterator().next()).hasEpsilon());
            }
            if (particleState.getStateNumber() == 7) {
                assertFalse(result.getFinalStates().contains(particleState));
                assertTrue(particleState.getInTransitions().size() == 2);
                int counter = 0;

                for (Iterator<Transition> it2 = particleState.getInTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 2);
                        assertTrue(particleTransition.getElements().size() == 2);
                        assertEquals("{A}b", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeB", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertEquals("{A}c", ((Element) particleTransition.getElements().toArray()[1]).getName());
                        assertEquals("{A}simpleTypeB", ((Element) particleTransition.getElements().toArray()[1]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 2) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}c", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
                assertTrue(particleState.getOutTransitions().size() == 2);
                counter = 0;

                for (Iterator<Transition> it2 = particleState.getOutTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 2);
                        assertTrue(particleTransition.getElements().size() == 2);
                        assertEquals("{A}b", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeB", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertEquals("{A}c", ((Element) particleTransition.getElements().toArray()[1]).getName());
                        assertEquals("{A}simpleTypeB", ((Element) particleTransition.getElements().toArray()[1]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 2) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}d", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeB", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
            }
            if (particleState.getStateNumber() == 8) {
                assertFalse(result.getFinalStates().contains(particleState));
                assertTrue(particleState.getInTransitions().size() == 2);
                int counter = 0;

                for (Iterator<Transition> it2 = particleState.getInTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 2);
                        assertEquals("{A}b", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeB", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertEquals("{A}c", ((Element) particleTransition.getElements().toArray()[1]).getName());
                        assertEquals("{A}simpleTypeB", ((Element) particleTransition.getElements().toArray()[1]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 2) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}c", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
                assertTrue(particleState.getOutTransitions().size() == 1);
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().size() == 1);
                assertEquals("{A}d", ((Element) ((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().toArray()[0]).getName());
                assertEquals("{A}simpleTypeB", ((Element) ((ParticleTransition) particleState.getOutTransitions().iterator().next()).getElements().toArray()[0]).getType().getName());
                assertTrue(((ParticleTransition) particleState.getOutTransitions().iterator().next()).getAnyPatterns().isEmpty());
                assertFalse(((ParticleTransition) particleState.getOutTransitions().iterator().next()).hasEpsilon());
            }
            if (particleState.getStateNumber() == 9) {
                assertTrue(result.getFinalStates().contains(particleState));
                assertTrue(particleState.getInTransitions().size() == 4);
                int counter = 0;

                for (Iterator<Transition> it2 = particleState.getInTransitions().iterator(); it2.hasNext();) {
                    ParticleTransition particleTransition = (ParticleTransition) it2.next();
                    counter++;

                    if (counter == 1) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}d", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeB", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 2) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}d", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeB", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 3) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}d", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                    if (counter == 4) {
                        assertTrue(particleTransition.getElements().size() == 1);
                        assertEquals("{A}d", ((Element) particleTransition.getElements().toArray()[0]).getName());
                        assertEquals("{A}simpleTypeA", ((Element) particleTransition.getElements().toArray()[0]).getType().getName());
                        assertTrue(particleTransition.getAnyPatterns().isEmpty());
                        assertFalse(particleTransition.hasEpsilon());
                    }
                }
                assertTrue(particleState.getOutTransitions().isEmpty());
            }
        }
    }
}