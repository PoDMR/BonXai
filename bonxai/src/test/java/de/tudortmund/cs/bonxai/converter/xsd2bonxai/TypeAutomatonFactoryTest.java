package de.tudortmund.cs.bonxai.converter.xsd2bonxai;

import de.tudortmund.cs.bonxai.common.ChoicePattern;
import de.tudortmund.cs.bonxai.common.ElementRef;
import de.tudortmund.cs.bonxai.common.GroupRef;
import de.tudortmund.cs.bonxai.common.SequencePattern;
import de.tudortmund.cs.bonxai.common.SymbolAlreadyRegisteredException;
import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.xsd.Group;
import de.tudortmund.cs.bonxai.xsd.Element;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import org.junit.*;
import static org.junit.Assert.*;

public class TypeAutomatonFactoryTest extends junit.framework.TestCase {

    XSDSchema testSchema;
    ComplexType alpha;
    ComplexType beta;
    ComplexType gamma;
    ComplexType delta;
    Element elementA;
    Element elementSecondA;
    Element elementB;
    Element elementSecondB;
    Element elementThirdB;
    Element elementC;
    Element elementD;
    Element elementE;
    Element elementY;
    ElementRef elementYRef;
    ChoicePattern betaChoice;
    GroupRef groupRef;
    TypeAutomatonFactory instance;

    @Before
    @Override
    public void setUp() throws SymbolAlreadyRegisteredException {
        testSchema = new XSDSchema();

        // Build types
        alpha = new ComplexType("{http://example.com/mynamespace}alpha", null);
        testSchema.getTypeSymbolTable().createReference("{http://example.com/mynamespace}alpha", alpha);
        testSchema.addType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}alpha"));
        beta = new ComplexType("{http://example.com/mynamespace}beta", null);
        testSchema.getTypeSymbolTable().createReference("{http://example.com/mynamespace}beta", beta);
        testSchema.addType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}beta"));
        gamma = new ComplexType("{http://example.com/mynamespace}gamma", null);
        testSchema.getTypeSymbolTable().createReference("{http://example.com/mynamespace}gamma", gamma);
        testSchema.addType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}gamma"));
        delta = new ComplexType("{http://example.com/mynamespace}delta", null);
        testSchema.getTypeSymbolTable().createReference("{http://example.com/mynamespace}delta", delta);
        testSchema.addType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}delta"));
        testSchema.getTypeSymbolTable().createReference("{http://example.com/mynamespace}string", new SimpleType("{http://example.com/mynamespace}string", null));

        // Build elments
        elementA = new Element("{http://example.com/mynamespace}a");
        elementSecondA = new Element("{http://example.com/mynamespace}a");
        elementB = new Element("{http://example.com/mynamespace}b");
        elementSecondB = new Element("{http://example.com/mynamespace}b");
        elementC = new Element("{http://example.com/mynamespace}c");
        elementD = new Element("{http://example.com/mynamespace}d");
        elementE = new Element("{http://example.com/mynamespace}e");
        Element elementX = new Element("{http://example.com/mynamespace}x");
        elementY = new Element("{http://example.com/mynamespace}y");
        Element elementZ = new Element("{http://example.com/mynamespace}z");

        // Register types to elements
        elementA.setType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}alpha"));
        elementSecondA.setType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}gamma"));
        elementB.setType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}beta"));
        elementSecondB.setType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}alpha"));
        elementC.setType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}alpha"));
        elementD.setType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}delta"));
        elementE.setType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}gamma"));
        elementX.setType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}string"));
        elementY.setType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}string"));
        elementZ.setType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}string"));

        // Build global elements a and y
        testSchema.getElementSymbolTable().createReference("{http://example.com/mynamespace}a", elementA);
        testSchema.getElementSymbolTable().createReference("{http://example.com/mynamespace}y", elementY);
        testSchema.addElement(testSchema.getElementSymbolTable().getReference("{http://example.com/mynamespace}a"));
        testSchema.addElement(testSchema.getElementSymbolTable().getReference("{http://example.com/mynamespace}y"));

        // Third b not contained in previous Example
        elementThirdB = new Element("{http://example.com/mynamespace}b");
        elementThirdB.setType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}alpha"));
        testSchema.getElementSymbolTable().createReference("{http://example.com/mynamespace}b", elementThirdB);
        testSchema.addElement(testSchema.getElementSymbolTable().getReference("{http://example.com/mynamespace}b"));

        // Build group
        // <group name="group">
        //  <sequence>
        //      <element name="b" type="alpha"/>
        //      <element name="d" type="delta"/>
        //  </sequence>
        // </group>
        SequencePattern groupSequence = new SequencePattern();
        groupSequence.addParticle(elementSecondB);
        groupSequence.addParticle(elementD);
        Group group = new de.tudortmund.cs.bonxai.xsd.Group("{http://example.com/mynamespace}group", groupSequence);
        testSchema.getGroupSymbolTable().createReference("{http://example.com/mynamespace}group", group);
        testSchema.addGroup(testSchema.getGroupSymbolTable().getReference("{http://example.com/mynamespace}group"));

        // Build ComplexType alpha content
        // <sequence>
        //      <element name="b" type="beta"/>
        //      <element name="x" type="string"/>
        //      <element ref="y"/>
        // </sequence>
        // <attribute name="attritbute" type="string"/>
        ComplexContentType alphaContent = new ComplexContentType();
        SequencePattern alphaSequence = new SequencePattern();
        alphaSequence.addParticle(elementB);
        alphaSequence.addParticle(elementX);
        elementYRef = new ElementRef(testSchema.getElementSymbolTable().getReference("{http://example.com/mynamespace}y"));
        alphaSequence.addParticle(elementYRef);
        alphaContent.setParticle(alphaSequence);
        alpha.setContent(alphaContent);
        Attribute attribute = new Attribute("{http://example.com/mynamespace}attritbute", testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}string"));
        alpha.addAttribute(attribute);

        //System.out.println(testSchema.getElements()); // For Hash Problem

        // Build ComplexType beta content
        // <choice>
        //      <element name="a" type="gamma"/>
        //      <element name="c" type="alpha"/>
        //      <element name="e" type="gamma"/>
        // </choice>
        ComplexContentType betaContent = new ComplexContentType();
        betaChoice = new ChoicePattern();
        betaChoice.addParticle(elementSecondA);
        betaChoice.addParticle(elementC);
        betaChoice.addParticle(elementE);
        betaContent.setParticle(betaChoice);
        beta.setContent(betaContent);

        //System.out.println(testSchema.getElements());

        // Build Complex gamma content:
        // <sequence>
        //      <element name="z" type="string"/>
        //      <group ref="group"/>
        // </sequence>
        ComplexContentType gammaContent = new ComplexContentType();
        SequencePattern gammaSequence = new SequencePattern();
        gammaSequence.addParticle(elementZ);
        groupRef = new GroupRef(testSchema.getGroupSymbolTable().getReference("{http://example.com/mynamespace}group"));
        gammaSequence.addParticle(groupRef);
        gammaContent.setParticle(gammaSequence);
        gamma.setContent(gammaContent);

        // Build ComplexType delta content:
        // <choice>
        //      <element name="x" type="string"/>
        //      <element ref="y"/>
        // </choice>
        ComplexContentType deltaContent = new ComplexContentType();
        ChoicePattern deltaChoice = new ChoicePattern();
        deltaChoice.addParticle(elementX);
        deltaChoice.addParticle(elementYRef);
        deltaContent.setParticle(deltaChoice);
        delta.setContent(deltaContent);
    }

    /**
     * Test of createTypeAutomaton method, of class TypeAutomatonFactory.
     */
    @Test
    public void testCreateTypeAutomaton() {

        // Build Automaton and root of the automaton, which contains no ComplexType
        // as it is the root of the XSD Schema
        TypeAutomatonState rootState = new TypeAutomatonState(null);
        TypeAutomaton testTypeAutomaton = new TypeAutomaton(rootState);

        // Build next State in the Automaton containing a ComplexType
        TypeAutomatonState alphaState = new TypeAutomatonState(alpha);

        // Build Transition from root to alpha annotated with Element a
        TypeAutomatonTransition rootToAlpha = new TypeAutomatonTransition(rootState, alphaState);
        rootToAlpha.addElement(elementA);
        rootToAlpha.addElement(elementThirdB);
        testTypeAutomaton.registerTransition(elementA.getName(), rootToAlpha);
        testTypeAutomaton.registerTransition(elementThirdB.getName(), rootToAlpha);
        rootState.addOutTransition(rootToAlpha);
        alphaState.addInTransition(rootToAlpha);

        // Build another State in the Automaton containing a ComplexType
        TypeAutomatonState betaState = new TypeAutomatonState(beta);

        // Build Transition from alpha to beta annotated with Element b
        TypeAutomatonTransition alphaToBeta = new TypeAutomatonTransition(alphaState, betaState);
        alphaToBeta.addElement(elementB);
        testTypeAutomaton.registerTransition(elementB.getName(), alphaToBeta);
        alphaState.addOutTransition(alphaToBeta);
        betaState.addInTransition(alphaToBeta);

        // Build Transition from beta to alpha annotated with Element c
        TypeAutomatonTransition betaToAlpha = new TypeAutomatonTransition(betaState, alphaState);
        betaToAlpha.addElement(elementC);
        testTypeAutomaton.registerTransition(elementC.getName(), betaToAlpha);
        betaState.addOutTransition(betaToAlpha);
        alphaState.addInTransition(betaToAlpha);

        // Build another State in the Automaton containing a ComplexType
        TypeAutomatonState gammaState = new TypeAutomatonState(gamma);

        // Build Transition from beta to gamma annotated with Element a and e
        TypeAutomatonTransition betaToGamma = new TypeAutomatonTransition(betaState, gammaState);
        betaToGamma.addElement(elementSecondA);
        betaToGamma.addElement(elementE);
        testTypeAutomaton.registerTransition(elementSecondA.getName(), betaToGamma);
        testTypeAutomaton.registerTransition(elementE.getName(), betaToGamma);
        betaState.addOutTransition(betaToGamma);
        gammaState.addInTransition(betaToGamma);

        // Build Transition from gamma to alpha annotated with Element b
        TypeAutomatonTransition gammaToAlpha = new TypeAutomatonTransition(gammaState, alphaState);
        gammaToAlpha.addElement(elementSecondB);
        testTypeAutomaton.registerTransition(elementSecondB.getName(), gammaToAlpha);
        gammaState.addOutTransition(gammaToAlpha);
        alphaState.addInTransition(gammaToAlpha);

        // Build another State in the Automaton containing a ComplexType
        TypeAutomatonState deltaState = new TypeAutomatonState(delta);

        // Build Transition from gamma to delta annotated with Element d
        TypeAutomatonTransition gammaToDelta = new TypeAutomatonTransition(gammaState, deltaState);
        gammaToDelta.addElement(elementD);
        testTypeAutomaton.registerTransition(elementD.getName(), gammaToDelta);
        gammaState.addOutTransition(gammaToDelta);
        deltaState.addInTransition(gammaToDelta);

        //Build test instance
        instance = new TypeAutomatonFactory();
        TypeAutomaton expResult = testTypeAutomaton;
        TypeAutomaton result = instance.createTypeAutomaton(testSchema);
        assertNotNull(result);
        Boolean exists = false;

        // Result and expResult contain the same element names and are not null
        assertNotNull(expResult.getElementNames());
        assertNotNull(result.getElementNames());
        assertEquals(expResult.getElementNames().size(), result.getElementNames().size());
        assertTrue(expResult.getElementNames().containsAll(result.getElementNames()));

        // For each element name
        for (Iterator<String> it = expResult.getElementNames().iterator(); it.hasNext();) {
            String elementName = it.next();
            // System.out.println("For" + elementName + " is:");

            // Expected Transitions for this element and actual Transitions for this element
            LinkedList<TypeAutomatonTransition> expTransitions = expResult.lookupTransitions(elementName);
            LinkedList<TypeAutomatonTransition> resultTransitions = result.lookupTransitions(elementName);

            /* System.out.println("Expected Transitions: ");
            for (Iterator<TypeAutomatonTransition> secondIt = expTransitions.iterator(); secondIt.hasNext();) {
            TypeAutomatonTransition typeAutomatonTransition = secondIt.next();
            System.out.println("    " + typeAutomatonTransition);
            if (typeAutomatonTransition.getSource() == rootState) {
            System.out.println("        Source State     : rootState");
            } else {
            System.out.println("        Source State     : " + typeAutomatonTransition.getSource().getType().getName());
            }
            System.out.println("        Destination State: " + typeAutomatonTransition.getDestination().getType().getName());
            }

            System.out.println("Actual Transitions: ");
            for (Iterator<TypeAutomatonTransition> secondIt = resultTransitions.iterator(); secondIt.hasNext();) {
            TypeAutomatonTransition typeAutomatonTransition = secondIt.next();
            System.out.println("    " + typeAutomatonTransition);
            if (typeAutomatonTransition.getSource().getType() == null) {
            System.out.println("        Source State     : rootState");
            } else {
            System.out.println("        Source State     : " + typeAutomatonTransition.getSource().getType().getName());
            }
            System.out.println("        Destination State: " + typeAutomatonTransition.getDestination().getType().getName());
            }*/

            // Transitions with specified elment name occur with same number in result as in expResult
            assertEquals(expTransitions.size(), resultTransitions.size());

            // For each transition of this element name
            for (int expTransitionsPointer = 0; expTransitionsPointer < expTransitions.size(); expTransitionsPointer++) {
                for (int resultTransitionsPointer = 0; resultTransitionsPointer < resultTransitions.size(); resultTransitionsPointer++) {

                    // Source of expected transition equals the Source of the actual transition
                    if (expTransitions.get(expTransitionsPointer).getSource().getType() == resultTransitions.get(resultTransitionsPointer).getSource().getType()) {

                        // Destination of expected transition equals the destination of the actual transition
                        if (expTransitions.get(expTransitionsPointer).getDestination().getType() == resultTransitions.get(resultTransitionsPointer).getDestination().getType()) {

                            // Elements occur with same number for expected transition and actual transition
                            LinkedList<Element> expElements = expTransitions.get(expTransitionsPointer).getElements();
                            LinkedList<Element> resultElements = resultTransitions.get(resultTransitionsPointer).getElements();
                            assertEquals(expElements.size(), resultElements.size());

                            // Elements on transistions are the same
                            for (int expElemtPointer = 0; expElemtPointer < expElements.size(); expElemtPointer++) {
                                for (int resultElemtPointer = 0; resultElemtPointer < resultElements.size(); resultElemtPointer++) {
                                    if (expElements.get(expElemtPointer) == resultElements.get(resultElemtPointer)) {
                                        exists = true;
                                    }
                                }
                                assertTrue(exists);
                                exists = false;
                            }

                            // Expected transition is contained in it source state out transitions
                            assertTrue(expTransitions.get(expTransitionsPointer).getSource().getOutTransitions().contains(expTransitions.get(expTransitionsPointer)));

                            // Actual transition is contained in it source state out transitions
                            assertTrue(resultTransitions.get(resultTransitionsPointer).getSource().getOutTransitions().contains(resultTransitions.get(resultTransitionsPointer)));

                            // Expected transition is contained in it destination state in transitions
                            assertTrue(expTransitions.get(expTransitionsPointer).getDestination().getInTransitions().contains(expTransitions.get(expTransitionsPointer)));

                            //Actual transition is contained in it destination state in transitions
                            assertTrue(resultTransitions.get(resultTransitionsPointer).getDestination().getInTransitions().contains(resultTransitions.get(resultTransitionsPointer)));

                            // Out transitions of expected and actual transitions source states
                            LinkedList<TypeAutomatonTransition> expOutTransitions = expTransitions.get(expTransitionsPointer).getSource().getOutTransitions();
                            LinkedList<TypeAutomatonTransition> resultOutTransitions = resultTransitions.get(resultTransitionsPointer).getSource().getOutTransitions();

                            // Out transistions occur with same number for both
                            assertEquals(expOutTransitions.size(), resultOutTransitions.size());

                            // In transitions of expected and actual transitions source states
                            LinkedList<TypeAutomatonTransition> expInTransitions = expTransitions.get(expTransitionsPointer).getDestination().getInTransitions();
                            LinkedList<TypeAutomatonTransition> resultInTransitions = resultTransitions.get(resultTransitionsPointer).getDestination().getInTransitions();

                            // In transistions occur with same number for both
                            assertEquals(expInTransitions.size(), resultInTransitions.size());

                            exists = true;
                        }
                    }
                }
                assertTrue(exists);
                exists = false;
            }
        }
    }

    /**
     * Test of extractElements method, run with null-pointer as parameter, of class TypeAutomatonFactory.
     */
    @Test
    public void testExtractElementsWithNullParameter() {
        instance = new TypeAutomatonFactory();
        HashMap<Group, Boolean> groupSeen = new HashMap<Group, Boolean>();
        LinkedList<Element> result = instance.extractElements(null, groupSeen);
        LinkedList<Element> expResult = new LinkedList<Element>();
        assertEquals(expResult, result);
    }

    /**
     * Test of extractElements method, run with Element as parameter, of class TypeAutomatonFactory.
     */
    @Test
    public void testExtractElementsWithElementParameter() {
        instance = new TypeAutomatonFactory();
        HashMap<Group, Boolean> groupSeen = new HashMap<Group, Boolean>();
        LinkedList<Element> result = instance.extractElements(elementB, groupSeen);
        LinkedList<Element> expResult = new LinkedList<Element>();
        expResult.add(elementB);
        assertEquals(expResult, result);
    }

    /**
     * Test of extractElements method, run with ElementRef as parameter, of class TypeAutomatonFactory.
     */
    @Test
    public void testExtractElementsWithElementRefParameter() {
        instance = new TypeAutomatonFactory();
        HashMap<Group, Boolean> groupSeen = new HashMap<Group, Boolean>();
        LinkedList<Element> result = instance.extractElements(elementYRef, groupSeen);
        LinkedList<Element> expResult = new LinkedList<Element>();
        expResult.add(elementY);
        assertEquals(expResult, result);
    }

    /**
     * Test of extractElements method, run with ParticleContainer as parameter, of class TypeAutomatonFactory.
     */
    @Test
    public void testExtractElementsWithParticleContainerParameter() {
        instance = new TypeAutomatonFactory();
        HashMap<Group, Boolean> groupSeen = new HashMap<Group, Boolean>();
        LinkedList<Element> result = instance.extractElements(betaChoice, groupSeen);
        LinkedList<Element> expResult = new LinkedList<Element>();
        expResult.add(elementSecondA);
        expResult.add(elementC);
        expResult.add(elementE);
        assertEquals(expResult, result);
    }

    /**
     * Test of extractElements method, run with GroupRef as parameter, of class TypeAutomatonFactory.
     */
    @Test
    public void testExtractElementsWithGroupRefParameter() throws SymbolAlreadyRegisteredException {
        instance = new TypeAutomatonFactory();
        HashMap<Group, Boolean> groupSeen = new HashMap<Group, Boolean>();
        SequencePattern groupSequence = new SequencePattern();
        Group newGroup = new de.tudortmund.cs.bonxai.xsd.Group("{http://example.com/mynamespace}newGroup", groupSequence);
        testSchema.getGroupSymbolTable().createReference("{http://example.com/mynamespace}newGroup", newGroup);
        testSchema.addGroup(testSchema.getGroupSymbolTable().getReference("newGroup"));
        GroupRef newGroupRef = new GroupRef(testSchema.getGroupSymbolTable().getReference("{http://example.com/mynamespace}newGroup"));
        groupSequence.addParticle(elementSecondB);
        groupSequence.addParticle(elementD);
        groupSequence.addParticle(newGroupRef);
        LinkedList<Element> result = instance.extractElements(newGroupRef, groupSeen);
        LinkedList<Element> expResult = new LinkedList<Element>();
        expResult.add(elementSecondB);
        expResult.add(elementD);
        assertEquals(expResult, result);
    }
}