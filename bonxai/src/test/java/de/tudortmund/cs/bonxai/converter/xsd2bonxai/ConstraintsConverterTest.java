package de.tudortmund.cs.bonxai.converter.xsd2bonxai;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.bonxai.AncestorPattern;
import de.tudortmund.cs.bonxai.bonxai.AncestorPatternParticle;
import de.tudortmund.cs.bonxai.bonxai.Constraint;
import de.tudortmund.cs.bonxai.bonxai.ConstraintList;
import de.tudortmund.cs.bonxai.bonxai.DoubleSlashPrefixElement;
import de.tudortmund.cs.bonxai.bonxai.KeyConstraint;
import de.tudortmund.cs.bonxai.bonxai.KeyRefConstraint;
import de.tudortmund.cs.bonxai.bonxai.OrExpression;
import de.tudortmund.cs.bonxai.bonxai.SequenceExpression;
import de.tudortmund.cs.bonxai.bonxai.SingleSlashPrefixElement;
import de.tudortmund.cs.bonxai.bonxai.Bonxai;
import de.tudortmund.cs.bonxai.bonxai.UniqueConstraint;
import de.tudortmund.cs.bonxai.xsd.ComplexContentType;
import de.tudortmund.cs.bonxai.xsd.ComplexType;
import de.tudortmund.cs.bonxai.xsd.Element;
import de.tudortmund.cs.bonxai.xsd.Key;
import de.tudortmund.cs.bonxai.xsd.KeyRef;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.SimpleType;
import de.tudortmund.cs.bonxai.xsd.Unique;
import java.util.*;
import org.junit.*;

public class ConstraintsConverterTest extends ConverterTest {

    ComplexType alpha;
    ComplexType beta;
    ComplexType gamma;
    XSDSchema testSchema;
    TypeAutomaton testSchemaAutomaton;

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
        testSchema.getTypeSymbolTable().createReference("{http://example.com/mynamespace}string", new SimpleType("{http://example.com/mynamespace}string", null));

        // Build elments
        Element elementA = new Element("{http://example.com/mynamespace}a");
        Element elementB = new Element("{http://example.com/mynamespace}b");
        Element elementSecondB = new Element("{http://example.com/mynamespace}b");
        Element elementC = new Element("{http://example.com/mynamespace}c");
        Element elementD = new Element("{http://example.com/mynamespace}d");

        // Register types to elements
        elementA.setType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}alpha"));
        elementB.setType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}beta"));
        elementSecondB.setType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}string"));
        elementC.setType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}alpha"));
        elementD.setType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}gamma"));

        // Build global element A
        testSchema.getElementSymbolTable().createReference("{http://example.com/mynamespace}a", elementA);
        testSchema.addElement(testSchema.getElementSymbolTable().getReference("{http://example.com/mynamespace}a"));

        // Build group
        // <group name="group">
        //  <sequence>
        //      <element name="b" type="string"/>
        //      <element name="c" type="alpha"/>
        //  </sequence>
        // </group>
        SequencePattern groupSequence = new SequencePattern();
        groupSequence.addParticle(elementSecondB);
        groupSequence.addParticle(elementC);
        de.tudortmund.cs.bonxai.xsd.Group group = new de.tudortmund.cs.bonxai.xsd.Group("{http://example.com/mynamespace}group", groupSequence);
        testSchema.getGroupSymbolTable().createReference("{http://example.com/mynamespace}group", group);
        testSchema.addGroup(testSchema.getGroupSymbolTable().getReference("{http://example.com/mynamespace}group"));

        // Build the ComplexType alpha content:
        // <complexType>
        //  <sequence>
        //      <element name="b" type="beta"/>
        //      <element name="d" type="gamma"/>
        //  </sequence>
        // </complexType>
        ComplexContentType alphaContent = new ComplexContentType();
        SequencePattern alphaSequence = new SequencePattern();
        alphaSequence.addParticle(elementB);
        alphaSequence.addParticle(elementD);
        alphaContent.setParticle(alphaSequence);
        alpha.setContent(alphaContent);

        // Build ComplexType beta content:
        // <sequence>
        //      <group ref="group"/>
        // </sequence>
        ComplexContentType betaContent = new ComplexContentType();
        SequencePattern betaSequence = new SequencePattern();
        GroupRef groupRef = new GroupRef(testSchema.getGroupSymbolTable().getReference("{http://example.com/mynamespace}group"));
        betaSequence.addParticle(groupRef);
        betaContent.setParticle(betaSequence);
        beta.setContent(betaContent);

        // Build ComplexType gamma content:
        // <sequence>
        //      <group ref="group"/>
        // </sequence>
        ComplexContentType gammaContent = new ComplexContentType();
        SequencePattern gammaSequence = new SequencePattern();
        gammaSequence.addParticle(groupRef);
        gammaContent.setParticle(gammaSequence);
        gamma.setContent(gammaContent);

        // Register Key to element a
        // <key name="KeyForA">
        //      <selector xpath="bla"/>
        //      <field xpath="blub"/>
        // </key>
        Key keyForA = new Key("{http://example.com/mynamespace}KeyForA", "bla");
        keyForA.addField("blub");
        testSchema.getKeyAndUniqueSymbolTable().createReference("{http://example.com/mynamespace}KeyForA", keyForA);
        elementA.addConstraint(keyForA);

        // Register KeyRef to element a
        // <keyref name="KeyRefForA" refer="KeyForA">
        //      <selector xpath="bla"/>
        //      <field xpath="blub"/>
        //      <field xpath="blob"/>
        // </keyref>
        KeyRef keyRefForA = new KeyRef("{http://example.com/mynamespace}KeyRefForA", "bla", testSchema.getKeyAndUniqueSymbolTable().getReference("{http://example.com/mynamespace}KeyForA"));
        keyRefForA.addField("blub");
        keyRefForA.addField("blob");
        elementA.addConstraint(keyRefForA);

        // Register Unique to element a
        // <xs:unique name="UniqueForA">
        //      <xs:selector xpath="bla"/>
        //      <xs:field xpath="blub"/>
        // </xs:unique>
        Unique uniqueForA = new Unique("{http://example.com/mynamespace}UniqueForA", "bla");
        uniqueForA.addField("blub");
        elementA.addConstraint(uniqueForA);

        // Register Key to element c
        // <key name="KeyForC">
        //      <selector xpath="bla"/>
        //      <field xpath="blub"/>
        // </key>
        Key keyForC = new Key("{http://example.com/mynamespace}KeyForC", "bla");
        keyForC.addField("blub");
        testSchema.getKeyAndUniqueSymbolTable().createReference("{http://example.com/mynamespace}KeyForC", keyForC);
        elementC.addConstraint(keyForC);

        // Register KeyRef to element c
        // <keyref name="KeyRefForC" refer="KeyForC">
        //      <selector xpath="bla"/>
        //      <field xpath="blub"/>
        //      <field xpath="blob"/>
        // </keyref>
        KeyRef keyRefForC = new KeyRef("{http://example.com/mynamespace}KeyRefForC", "bla", testSchema.getKeyAndUniqueSymbolTable().getReference("{http://example.com/mynamespace}KeyForC"));
        keyRefForC.addField("blub");
        keyRefForC.addField("blob");
        elementC.addConstraint(keyRefForC);

        // Register Unique to element c
        // <unique name="UniqueForC">
        //      <selector xpath="bla"/>
        //      <field xpath="blub"/>
        // <unique>
        Unique uniqueForC = new Unique("{http://example.com/mynamespace}UniqueForC", "bla");
        uniqueForC.addField("blub");
        elementC.addConstraint(uniqueForC);

        // Register Key to element second b
        // <key name="KeyForSecondB">
        //      <selector xpath="bla"/>
        //      <field xpath="blub"/>
        // </key>
        Key keyForSecondB = new Key("{http://example.com/mynamespace}KeyForSecondB", "bla");
        keyForSecondB.addField("blub");
        testSchema.getKeyAndUniqueSymbolTable().createReference("{http://example.com/mynamespace}KeyForSecondB", keyForSecondB);
        elementSecondB.addConstraint(keyForSecondB);

        // Register KeyRef to element second b
        // <keyref name="KeyRefForSecondB" refer="KeyForSecondB">
        //      <selector xpath="bla"/>
        //      <field xpath="blub"/>
        //      <field xpath="blob"/>
        // </keyref>
        KeyRef keyRefForSecondB = new KeyRef("{http://example.com/mynamespace}KeyRefForSecondB", "bla", testSchema.getKeyAndUniqueSymbolTable().getReference("{http://example.com/mynamespace}KeyForSecondB"));
        keyRefForSecondB.addField("blub");
        keyRefForSecondB.addField("blob");
        elementSecondB.addConstraint(keyRefForSecondB);

        // Register Unique to element second b
        // <xs:unique name="UniqueForSecondB">
        //      <xs:selector xpath="bla"/>
        //      <xs:field xpath="blub"/>
        // </xs:unique>
        Unique uniqueForSecondB = new Unique("{http://example.com/mynamespace}UniqueForSecondB", "bla");
        uniqueForSecondB.addField("blub");
        elementSecondB.addConstraint(uniqueForSecondB);

        // Generate TypeAutomatons for testSchema
        TypeAutomatonFactory typeAutomatonFactory = new TypeAutomatonFactory();
        testSchemaAutomaton = typeAutomatonFactory.createTypeAutomaton(testSchema);
    }

    /**
     * Test of convert method, of class ConstraintsConverter.
     */
    @Test
    public void testConvertConstraints() {

        // Assemble all Bonxai Components
        String namespace = "http://example.com/mynamespace";
        ConstraintList constraintList = new ConstraintList();

        // Build Bonxai Schema
        Bonxai expResult = new Bonxai();
        expResult.setConstraintList(constraintList);

        // Build all PrefixElements
        SingleSlashPrefixElement singleSlashA = new SingleSlashPrefixElement(namespace, "a");
        SingleSlashPrefixElement singleSlashB = new SingleSlashPrefixElement(namespace, "b");
        DoubleSlashPrefixElement doubleSlashC = new DoubleSlashPrefixElement(namespace, "c");
        DoubleSlashPrefixElement doubleSlashB = new DoubleSlashPrefixElement(namespace, "b");
        DoubleSlashPrefixElement doubleSlashD = new DoubleSlashPrefixElement(namespace, "d");

        // Build ancestor-pattern (//a) for element a constraints
        AncestorPattern elementAAncestorPattern = new AncestorPattern(singleSlashA);

        // Build ancestor-pattern ((//b/b)|(//d/b)) for element b constraints
        // Element b ancestor-pattern part (//d/b) from gamma
        Vector<AncestorPatternParticle> elementBSequenceVector = new Vector<AncestorPatternParticle>();
        elementBSequenceVector.add(doubleSlashD);
        elementBSequenceVector.add(singleSlashB);
        SequenceExpression elementBSequence = new SequenceExpression(elementBSequenceVector);

        // Element b ancestor-pattern part (//b/b) from beta
        Vector<AncestorPatternParticle> secondElementBSequenceVector = new Vector<AncestorPatternParticle>();
        secondElementBSequenceVector.add(doubleSlashB);
        secondElementBSequenceVector.add(singleSlashB);
        SequenceExpression secondElementBSequence = new SequenceExpression(secondElementBSequenceVector);

        // Element b ancestor-pattern ((//b/b)|(//d/b))
        Vector<AncestorPatternParticle> elementBVector = new Vector<AncestorPatternParticle>();
        elementBVector.add(secondElementBSequence);
        elementBVector.add(elementBSequence);
        AncestorPattern elementBAncestorPattern = new AncestorPattern(new OrExpression(elementBVector));

        // Build ancestor-pattern (//c) for element c constraints;
        AncestorPattern elementCAncestorPattern = new AncestorPattern(doubleSlashC);

        // Build KeyConstraint for element a
        HashSet<String> keyFieldsA = new HashSet<String>();
        keyFieldsA.add("blub");
        KeyConstraint keyConstraintForA = new KeyConstraint("{http://example.com/mynamespace}KeyForA", elementAAncestorPattern, "bla", keyFieldsA);

        //Build KeyRefConstraint for element a
        HashSet<String> keyrefFieldsA = new HashSet<String>();
        keyrefFieldsA.add("blub");
        keyrefFieldsA.add("blob");
        KeyRefConstraint keyRefConstraintForA = new KeyRefConstraint("{http://example.com/mynamespace}KeyForA", elementAAncestorPattern, "bla", keyrefFieldsA);

        //Build UniqueConstraint for element a
        HashSet<String> uniqueFieldsA = new HashSet<String>();
        uniqueFieldsA.add("blub");
        UniqueConstraint uniqueConstraintForA = new UniqueConstraint(elementAAncestorPattern, "bla", uniqueFieldsA);

        // Build KeyConstraint for element b
        HashSet<String> keyFieldsB = new HashSet<String>();
        keyFieldsB.add("blub");
        KeyConstraint keyConstraintForB = new KeyConstraint("{http://example.com/mynamespace}KeyForSecondB", elementBAncestorPattern, "bla", keyFieldsB);

        //Build KeyRefConstraint for element b
        HashSet<String> keyrefFieldsB = new HashSet<String>();
        keyrefFieldsB.add("blub");
        keyrefFieldsB.add("blob");
        KeyRefConstraint keyRefConstraintForB = new KeyRefConstraint("{http://example.com/mynamespace}KeyForSecondB", elementBAncestorPattern, "bla", keyrefFieldsB);

        //Build UniqueConstraint for element b
        HashSet<String> uniqueFieldsB = new HashSet<String>();
        uniqueFieldsB.add("blub");
        UniqueConstraint uniqueConstraintForB = new UniqueConstraint(elementBAncestorPattern, "bla", uniqueFieldsB);

        // Build KeyConstraint for element c
        HashSet<String> keyFieldsC = new HashSet<String>();
        keyFieldsC.add("blub");
        KeyConstraint keyConstraintForC = new KeyConstraint("{http://example.com/mynamespace}KeyForC", elementCAncestorPattern, "bla", keyFieldsC);

        //Build KeyRefConstraint for element c
        HashSet<String> keyrefFieldsC = new HashSet<String>();
        keyrefFieldsC.add("blub");
        keyrefFieldsC.add("blob");
        KeyRefConstraint keyRefConstraintForC = new KeyRefConstraint("{http://example.com/mynamespace}KeyForC", elementCAncestorPattern, "bla", keyrefFieldsC);

        //Build UniqueConstraint for element c
        HashSet<String> uniqueFieldsC = new HashSet<String>();
        uniqueFieldsC.add("blub");
        UniqueConstraint uniqueConstraintForC = new UniqueConstraint(elementCAncestorPattern, "bla", uniqueFieldsC);

        //Build constraintList
        constraintList.addConstraint(keyConstraintForA);
        constraintList.addConstraint(keyRefConstraintForA);
        constraintList.addConstraint(uniqueConstraintForA);
        constraintList.addConstraint(keyConstraintForB);
        constraintList.addConstraint(keyRefConstraintForB);
        constraintList.addConstraint(uniqueConstraintForB);
        constraintList.addConstraint(keyConstraintForC);
        constraintList.addConstraint(keyRefConstraintForC);
        constraintList.addConstraint(uniqueConstraintForC);

        // Validate ConstraintsConverter
        ConstraintsConverter instance = new ConstraintsConverter();
        Bonxai result = instance.convert(testSchema, testSchemaAutomaton, new Bonxai());

        assertNotNull(result);
        assertEquals(expResult.getConstraintList().getConstraints().size(), result.getConstraintList().getConstraints().size());

        // Validate each constraint
        for (int i = 0; i < expResult.getConstraintList().getConstraints().size(); i++) {
            Constraint expConstraint = expResult.getConstraintList().getConstraints().get(i);
            Constraint resultConstraint = result.getConstraintList().getConstraints().get(i);

            // Assert that expConstraint equals resultConstraint
            assertTrue(expConstraint.getAncestorPattern().toString().equals(resultConstraint.getAncestorPattern().toString()));
            assertTrue(expConstraint.getConstraintSelector().equals(resultConstraint.getConstraintSelector()));
            assertEquals(expConstraint.getConstraintFields().size(), resultConstraint.getConstraintFields().size());
            assertTrue(expConstraint.getConstraintFields().containsAll(expConstraint.getConstraintFields()));


        /* System.out.println("Expected:");
        if (expConstraint instanceof KeyConstraint) {
        System.out.println("KeyConstraint: " + ((KeyConstraint) expConstraint).getName());
        }
        if (expConstraint instanceof KeyRefConstraint) {
        System.out.println("KeyRefConstraint");
        }
        if (expConstraint instanceof UniqueConstraint) {
        System.out.println("UniqueConstraint");
        }
        System.out.println("    Ancestor-path: " + expConstraint.getAncestorPattern().toString());
        System.out.println("    Selector: " + expConstraint.getConstraintSelector());
        for (Iterator<String> it3 = expConstraint.getConstraintFields().iterator(); it3.hasNext();) {
        System.out.println("    Field: " + it3.next());
        }
        System.out.println("Result:");
        if (resultConstraint instanceof KeyConstraint) {
        System.out.println("KeyConstraint: " + ((KeyConstraint) resultConstraint).getName());
        }
        if (resultConstraint instanceof KeyRefConstraint) {
        System.out.println("KeyRefConstraint");
        }
        if (resultConstraint instanceof UniqueConstraint) {
        System.out.println("UniqueConstraint");
        }
        System.out.println("    Ancestor-path: " + resultConstraint.getAncestorPattern().toString());
        System.out.println("    Selector: " + resultConstraint.getConstraintSelector());
        for (Iterator<String> it3 = resultConstraint.getConstraintFields().iterator(); it3.hasNext();) {
        System.out.println("    Field: " + it3.next());
        }*/
        }
    }

    @After
    @Override
    public void tearDown() {
        Boolean exists = false;
        TypeAutomatonFactory typeAutomatonFactory = new TypeAutomatonFactory();
        TypeAutomaton expResult = typeAutomatonFactory.createTypeAutomaton(testSchema);

        TypeAutomaton result = testSchemaAutomaton;

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

            /*System.out.println("Expected Transitions: ");
            for (Iterator<TypeAutomatonTransition> secondIt = expTransitions.iterator(); secondIt.hasNext();) {
            TypeAutomatonTransition typeAutomatonTransition = secondIt.next();
            System.out.println("    " + typeAutomatonTransition);
            if (typeAutomatonTransition.getSource() == expResult.getStart()) {
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
}