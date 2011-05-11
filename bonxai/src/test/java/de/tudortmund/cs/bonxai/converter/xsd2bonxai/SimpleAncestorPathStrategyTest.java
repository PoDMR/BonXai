package de.tudortmund.cs.bonxai.converter.xsd2bonxai;

import de.tudortmund.cs.bonxai.common.GroupRef;
import de.tudortmund.cs.bonxai.common.SequencePattern;
import de.tudortmund.cs.bonxai.common.SymbolAlreadyRegisteredException;
import de.tudortmund.cs.bonxai.bonxai.*;
import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.xsd.Element;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

public class SimpleAncestorPathStrategyTest extends junit.framework.TestCase {

    /**
     * Test of calculate method, of class SimpleAncestorPathStrategy.
     */
    @Test
    public void testCalculate() {
        // Build Automaton and root of the automaton, which contains no ComplexType
        // as it is the root of the XSD Schema
        TypeAutomatonState rootState = new TypeAutomatonState(null);
        TypeAutomaton testTypeAutomaton = new TypeAutomaton(rootState);

        // Build next State in the Automaton containing a ComplexType
        SimpleContentType alphaContent = new SimpleContentType();
        ComplexType alphaComplexType = new ComplexType("{http://example.com/mynamespace}alphaComplexType", alphaContent);
        TypeAutomatonState alphaState = new TypeAutomatonState(alphaComplexType);

        // Build Transition from root to alpha annotated with Element a
        TypeAutomatonTransition rootToAlpha = new TypeAutomatonTransition(rootState, alphaState);
        de.tudortmund.cs.bonxai.xsd.Element elementA = new de.tudortmund.cs.bonxai.xsd.Element("{http://example.com/mynamespace}a");
        rootToAlpha.addElement(elementA);
        testTypeAutomaton.registerTransition(elementA.getName(), rootToAlpha);
        rootState.addOutTransition(rootToAlpha);
        alphaState.addInTransition(rootToAlpha);

        // Build another State in the Automaton containing a ComplexType
        SimpleContentType betaContent = new SimpleContentType();
        ComplexType betaComplexType = new ComplexType("{http://example.com/mynamespace}betaComplexType", betaContent);
        TypeAutomatonState betaState = new TypeAutomatonState(betaComplexType);

        // Build Transition from alpha to beta annotated with Element b
        TypeAutomatonTransition alphaToBeta = new TypeAutomatonTransition(alphaState, betaState);
        de.tudortmund.cs.bonxai.xsd.Element elementB = new de.tudortmund.cs.bonxai.xsd.Element("{http://example.com/mynamespace}b");
        alphaToBeta.addElement(elementB);
        testTypeAutomaton.registerTransition(elementB.getName(), alphaToBeta);
        alphaState.addOutTransition(alphaToBeta);
        betaState.addInTransition(alphaToBeta);

        // Build Transition from beta to alpha annotated with Element c
        TypeAutomatonTransition betaToAlpha = new TypeAutomatonTransition(betaState, alphaState);
        de.tudortmund.cs.bonxai.xsd.Element elementC = new de.tudortmund.cs.bonxai.xsd.Element("{http://example.com/mynamespace}c");
        betaToAlpha.addElement(elementC);
        testTypeAutomaton.registerTransition(elementC.getName(), betaToAlpha);
        betaState.addOutTransition(betaToAlpha);
        alphaState.addInTransition(betaToAlpha);

        // Build another State in the Automaton containing a ComplexType
        SimpleContentType gammaContent = new SimpleContentType();
        ComplexType gammaComplexType = new ComplexType("{http://example.com/mynamespace}gammaComplexType", gammaContent);
        TypeAutomatonState gammaState = new TypeAutomatonState(gammaComplexType);

        // Build Transition from beta to gamma annotated with Element a
        TypeAutomatonTransition betaToGamma = new TypeAutomatonTransition(betaState, gammaState);
        betaToGamma.addElement(elementA);
        de.tudortmund.cs.bonxai.xsd.Element elementE = new de.tudortmund.cs.bonxai.xsd.Element("{http://example.com/mynamespace}e");
        betaToGamma.addElement(elementE);
        testTypeAutomaton.registerTransition(elementA.getName(), betaToGamma);
        testTypeAutomaton.registerTransition(elementE.getName(), betaToGamma);
        betaState.addOutTransition(betaToGamma);
        gammaState.addInTransition(betaToGamma);

        // Build Transition from gamma to alpha annotated with Element b
        TypeAutomatonTransition gammaToAlpha = new TypeAutomatonTransition(gammaState, alphaState);
        gammaToAlpha.addElement(elementB);
        testTypeAutomaton.registerTransition(elementB.getName(), gammaToAlpha);
        gammaState.addOutTransition(gammaToAlpha);
        alphaState.addInTransition(gammaToAlpha);

        // Build another State in the Automaton containing a ComplexType
        SimpleContentType deltaContent = new SimpleContentType();
        ComplexType deltaComplexType = new ComplexType("{http://example.com/mynamespace}deltaComplexType", deltaContent);
        TypeAutomatonState deltaState = new TypeAutomatonState(deltaComplexType);

        // Build Transition from gamma to delta annotated with Element d
        TypeAutomatonTransition gammaToDelta = new TypeAutomatonTransition(gammaState, deltaState);
        de.tudortmund.cs.bonxai.xsd.Element elementD = new de.tudortmund.cs.bonxai.xsd.Element("{http://example.com/mynamespace}d");
        gammaToDelta.addElement(elementD);
        testTypeAutomaton.registerTransition(elementD.getName(), gammaToDelta);
        gammaState.addOutTransition(gammaToDelta);
        deltaState.addInTransition(gammaToDelta);
        //Build test instance
        String namespace = "http://example.com/mynamespace";
        SimpleAncestorPathStrategy instance = new SimpleAncestorPathStrategy();

        //Build result and begin construction of expected result
        HashMap<TypeAutomatonState, AncestorPattern> result = instance.calculate(testTypeAutomaton);
        HashMap<TypeAutomatonState, AncestorPattern> expResult = new HashMap<TypeAutomatonState, AncestorPattern>();

        //alpha ancestor-pattern part (//b/a/b) from gamma to alpha transition
        Vector<AncestorPatternParticle> gammaToAlphaSequenceVector = new Vector<AncestorPatternParticle>();
        DoubleSlashPrefixElement doubleSlashB = new DoubleSlashPrefixElement(namespace, "b");
        gammaToAlphaSequenceVector.add(doubleSlashB);
        SingleSlashPrefixElement singleSlashA = new SingleSlashPrefixElement(namespace, "a");
        gammaToAlphaSequenceVector.add(singleSlashA);
        SingleSlashPrefixElement singleSlashB = new SingleSlashPrefixElement(namespace, "b");
        gammaToAlphaSequenceVector.add(singleSlashB);
        SequenceExpression gammaToAlphaSequence = new SequenceExpression(gammaToAlphaSequenceVector);

        //alpha ancestor-pattern second part (//e/b) from gamma to alpha transition
        Vector<AncestorPatternParticle> secondGammaToAlphaSequenceVector = new Vector<AncestorPatternParticle>();
        DoubleSlashPrefixElement doubleSlashE = new DoubleSlashPrefixElement(namespace, "e");
        secondGammaToAlphaSequenceVector.add(doubleSlashE);
        secondGammaToAlphaSequenceVector.add(singleSlashB);
        SequenceExpression secondGammaToAlphaSequence = new SequenceExpression(secondGammaToAlphaSequenceVector);

        //alpha ancestor-pattern part (//c) from beta to alpha transition
        DoubleSlashPrefixElement doubleSlashC = new DoubleSlashPrefixElement(namespace, "c");

        //alpha ancestor-pattern part (/a) from root to alpha transition

        //alpha ancestor-pattern ((/a)|(//e/b)|(//b/a/b)|(//c))
        Vector<AncestorPatternParticle> alphaVector = new Vector<AncestorPatternParticle>();
        alphaVector.add(singleSlashA);
        alphaVector.add(secondGammaToAlphaSequence);
        alphaVector.add(gammaToAlphaSequence);
        alphaVector.add(doubleSlashC);
        OrExpression alpha = new OrExpression(alphaVector);
        AncestorPattern alphaAncestorPattern = new AncestorPattern(alpha);

        //beta ancestor-pattern part (/a/b) from alpha to beta transition
        Vector<AncestorPatternParticle> alphaToBetaSequenceVector = new Vector<AncestorPatternParticle>();
        alphaToBetaSequenceVector.add(singleSlashA);
        alphaToBetaSequenceVector.add(singleSlashB);
        SequenceExpression alphaToBetaSequence = new SequenceExpression(alphaToBetaSequenceVector);

        //beta ancestor-pattern part (//c/b) from alpha to beta transition
        Vector<AncestorPatternParticle> thirdAlphaToBetaSequenceVector = new Vector<AncestorPatternParticle>();
        thirdAlphaToBetaSequenceVector.add(doubleSlashC);
        thirdAlphaToBetaSequenceVector.add(singleSlashB);
        SequenceExpression thirdAlphaToBetaSequence = new SequenceExpression(thirdAlphaToBetaSequenceVector);

        //beta ancestor-pattern part (//b/b) from alpha to beta transition
        Vector<AncestorPatternParticle> secondAlphaToBetaSequenceVector = new Vector<AncestorPatternParticle>();
        secondAlphaToBetaSequenceVector.add(doubleSlashB);
        secondAlphaToBetaSequenceVector.add(singleSlashB);
        SequenceExpression secondAlphaToBetaSequence = new SequenceExpression(secondAlphaToBetaSequenceVector);

        //beta ancestor-pattern ((/a/b)|(//c/b)|(//b/b))
        Vector<AncestorPatternParticle> betaVector = new Vector<AncestorPatternParticle>();
        betaVector.add(alphaToBetaSequence);
        betaVector.add(thirdAlphaToBetaSequence);
        betaVector.add(secondAlphaToBetaSequence);
        OrExpression beta = new OrExpression(betaVector);
        AncestorPattern betaAncestorPattern = new AncestorPattern(beta);


        //gamma ancestor-pattern ((//b/a)|(//e))
        Vector<AncestorPatternParticle> gammaVector = new Vector<AncestorPatternParticle>();
        Vector<AncestorPatternParticle> gammaSequencePartVector = new Vector<AncestorPatternParticle>();
        gammaSequencePartVector.add(doubleSlashB);
        gammaSequencePartVector.add(singleSlashA);
        SequenceExpression gammaSequence = new SequenceExpression(gammaSequencePartVector);
        gammaVector.add(gammaSequence);
        gammaVector.add(doubleSlashE);
        OrExpression gamma = new OrExpression(gammaVector);
        AncestorPattern gammaAncestorPattern = new AncestorPattern(gamma);


        //delta ancestor-pattern (//d)
        DoubleSlashPrefixElement delta = new DoubleSlashPrefixElement(namespace, "d");
        AncestorPattern deltaAncestorPattern = new AncestorPattern(delta);


        // Build expected result
        expResult.put(alphaState, alphaAncestorPattern);
        expResult.put(betaState, betaAncestorPattern);
        expResult.put(gammaState, gammaAncestorPattern);
        expResult.put(deltaState, deltaAncestorPattern);

        /*
        System.out.println("Expected Results: ");
        System.out.println("alphaComplexType: " + alphaAncestorPattern);
        System.out.println("betaComplexType: " + betaAncestorPattern);
        System.out.println("gammaComplexType: " + gammaAncestorPattern);
        System.out.println("deltaComplexType: " + deltaAncestorPattern);
        System.out.println();

        System.out.println("Results: ");*/
        Set<TypeAutomatonState> states = result.keySet();
        for (Iterator<TypeAutomatonState> it = states.iterator(); it.hasNext();) {
            TypeAutomatonState typeAutomatonState = it.next();
            assertEquals(expResult.get(typeAutomatonState).toString(), result.get(typeAutomatonState).toString());
        //System.out.println(typeAutomatonState.getType().getLocalName() + ": " + result.get(typeAutomatonState));
        }
    }

    /**
     * Test of calculate method with a second complex automaton, of class SimpleAncestorPathStrategy.
     */
    @Test
    public void testCalculateWithSecondAutomaton() throws SymbolAlreadyRegisteredException {

        //Build test instance
        String namespace = "http://example.com/mynamespace";
        SimpleAncestorPathStrategy instance = new SimpleAncestorPathStrategy();
        XSDSchema testSchema = new XSDSchema();

        // Build types
        ComplexType alpha = new ComplexType("{http://example.com/mynamespace}alpha", null);
        testSchema.getTypeSymbolTable().createReference("{http://example.com/mynamespace}alpha", alpha);
        testSchema.addType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}alpha"));
        ComplexType beta = new ComplexType("{http://example.com/mynamespace}beta", null);
        testSchema.getTypeSymbolTable().createReference("{http://example.com/mynamespace}beta", beta);
        testSchema.addType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}beta"));
        ComplexType gamma = new ComplexType("{http://example.com/mynamespace}gamma", null);
        testSchema.getTypeSymbolTable().createReference("{http://example.com/mynamespace}gamma", gamma);
        testSchema.addType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}gamma"));
        ComplexType delta = new ComplexType("{http://example.com/mynamespace}delta", null);
        testSchema.getTypeSymbolTable().createReference("{http://example.com/mynamespace}delta", delta);
        testSchema.addType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}delta"));

        // Build elments
        Element elementA = new Element("{http://example.com/mynamespace}a");
        Element elementB = new Element("{http://example.com/mynamespace}b");
        Element elementSecondB = new Element("{http://example.com/mynamespace}b");
        Element elementC = new Element("{http://example.com/mynamespace}c");
        Element elementD = new Element("{http://example.com/mynamespace}d");

        // Register types to elements
        elementA.setType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}alpha"));
        elementB.setType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}beta"));
        elementSecondB.setType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}delta"));
        elementC.setType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}alpha"));
        elementD.setType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}gamma"));

        // Build global element A
        testSchema.getElementSymbolTable().createReference("{http://example.com/mynamespace}a", elementA);
        testSchema.addElement(testSchema.getElementSymbolTable().getReference("{http://example.com/mynamespace}a"));

        // Build group
        // <group name="group">
        //  <sequence>
        //      <element name="c" type="alpha"/>
        //  </sequence>
        // </group>
        SequencePattern groupSequence = new SequencePattern();
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
        //      <element name="b" type="beta"/>
        // </sequence>
        ComplexContentType betaContent = new ComplexContentType();
        SequencePattern betaSequence = new SequencePattern();
        GroupRef groupRef = new GroupRef(testSchema.getGroupSymbolTable().getReference("{http://example.com/mynamespace}group"));
        betaSequence.addParticle(groupRef);
        betaSequence.addParticle(elementSecondB);
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

        // Generate TypeAutomatons for testSchema
        TypeAutomatonFactory typeAutomatonFactory = new TypeAutomatonFactory();
        TypeAutomaton testTypeAutomaton = typeAutomatonFactory.createTypeAutomaton(testSchema);

        //Build result and begin construction of expected result
        HashMap<TypeAutomatonState, AncestorPattern> result = instance.calculate(testTypeAutomaton);
        HashMap<TypeAutomatonState, AncestorPattern> expResult = new HashMap<TypeAutomatonState, AncestorPattern>();

        // Build all PrefixElements
        SingleSlashPrefixElement singleSlashA = new SingleSlashPrefixElement(namespace, "a");
        SingleSlashPrefixElement singleSlashB = new SingleSlashPrefixElement(namespace, "b");
        DoubleSlashPrefixElement doubleSlashA = new DoubleSlashPrefixElement(namespace, "a");
        DoubleSlashPrefixElement doubleSlashC = new DoubleSlashPrefixElement(namespace, "c");
        DoubleSlashPrefixElement doubleSlashB = new DoubleSlashPrefixElement(namespace, "b");
        DoubleSlashPrefixElement doubleSlashD = new DoubleSlashPrefixElement(namespace, "d");

        // Build ancestor-pattern ((/a)|(//c)) for alpha
        Vector<AncestorPatternParticle> alphaOrVector = new Vector<AncestorPatternParticle>();
        alphaOrVector.add(singleSlashA);
        alphaOrVector.add(doubleSlashC);
        AncestorPattern alphaAncestorPattern = new AncestorPattern(new OrExpression(alphaOrVector));

        // Build ancestor-pattern ((//a/b)|(//c/b)) for beta
        Vector<AncestorPatternParticle> betaSequenceVector = new Vector<AncestorPatternParticle>();
        betaSequenceVector.add(doubleSlashA);
        betaSequenceVector.add(singleSlashB);
        SequenceExpression firstBetaSequence = new SequenceExpression(betaSequenceVector);
        Vector<AncestorPatternParticle> secondBetaSequenceVector = new Vector<AncestorPatternParticle>();
        secondBetaSequenceVector.add(doubleSlashC);
        secondBetaSequenceVector.add(singleSlashB);
        SequenceExpression secondBetaSequence = new SequenceExpression(secondBetaSequenceVector);
        Vector<AncestorPatternParticle> BetaVector = new Vector<AncestorPatternParticle>();
        BetaVector.add(firstBetaSequence);
        BetaVector.add(secondBetaSequence);
        AncestorPattern betaAncestorPattern = new AncestorPattern(new OrExpression(BetaVector));

        // Build ancestor-pattern (//d) for gamma
        AncestorPattern gammaAncestorPattern = new AncestorPattern(doubleSlashD);

        // Build ancestor-pattern (//b/b) for delta;
        Vector<AncestorPatternParticle> deltaSequenceVector = new Vector<AncestorPatternParticle>();
        deltaSequenceVector.add(doubleSlashB);
        deltaSequenceVector.add(singleSlashB);
        AncestorPattern deltaAncestorPattern = new AncestorPattern(new SequenceExpression(deltaSequenceVector));

        // Build expected result
        LinkedList<TypeAutomatonState> expStates = new LinkedList<TypeAutomatonState>();
        expStates.add(new TypeAutomatonState(alpha));
        expStates.add(new TypeAutomatonState(beta));
        expStates.add(new TypeAutomatonState(gamma));
        expStates.add(new TypeAutomatonState(delta));
        expResult.put(expStates.get(0), alphaAncestorPattern);
        expResult.put(expStates.get(1), betaAncestorPattern);
        expResult.put(expStates.get(3), gammaAncestorPattern);
        expResult.put(expStates.get(2), deltaAncestorPattern);

        /*
        System.out.println("Expected Results: ");
        System.out.println("alpha: " + alphaAncestorPattern);
        System.out.println("beta: " + betaAncestorPattern);
        System.out.println("gamma: " + gammaAncestorPattern);
        System.out.println("delta: " + deltaAncestorPattern);
        System.out.println();

        System.out.println("Results: ");*/
        Vector<TypeAutomatonState> states = new Vector<TypeAutomatonState>(result.keySet());
        Collections.sort(states, new Comparator<TypeAutomatonState>() {

            public int compare(TypeAutomatonState typeAutomatonState, TypeAutomatonState secondtypeAutomatonState) {
                return typeAutomatonState.getType().getName().toString().compareTo(secondtypeAutomatonState.getType().getName().toString());
            }
        });
        for (int i = 0; i < states.size(); i++) {
            assertEquals(expResult.get(expStates.get(i)).toString(), result.get(states.get(i)).toString());
        //System.out.println(states.get(i).getType().getLocalName() + ": " + result.get(states.get(i)).toString());
        }
    }

    /**
     * Test of calculate method with a third simple automaton, of class SimpleAncestorPathStrategy.
     */
    @Test
    public void testCalculateWithThirdAutomaton() throws SymbolAlreadyRegisteredException {

        //Build test instance
        String namespace = "http://example.com/mynamespace";
        SimpleAncestorPathStrategy instance = new SimpleAncestorPathStrategy();
        XSDSchema testSchema = new XSDSchema();

        // Build types
        ComplexType alpha = new ComplexType("{http://example.com/mynamespace}alpha", null);
        testSchema.getTypeSymbolTable().createReference("{http://example.com/mynamespace}alpha", alpha);
        testSchema.addType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}alpha"));
        ComplexType beta = new ComplexType("{http://example.com/mynamespace}beta", null);
        testSchema.getTypeSymbolTable().createReference("{http://example.com/mynamespace}beta", beta);
        testSchema.addType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}beta"));


        // Build elments
        Element elementA = new Element("{http://example.com/mynamespace}a");
        Element elementSecondA = new Element("{http://example.com/mynamespace}a");
        Element elementThirdA = new Element("{http://example.com/mynamespace}a");


        // Register types to elements
        elementA.setType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}alpha"));
        elementSecondA.setType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}beta"));
        elementThirdA.setType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}alpha"));


        // Build global element A
        testSchema.getElementSymbolTable().createReference("{http://example.com/mynamespace}a", elementA);
        testSchema.addElement(testSchema.getElementSymbolTable().getReference("{http://example.com/mynamespace}a"));

        // Build the ComplexType alpha content:
        // <complexType>
        //  <sequence>
        //      <element name="a" type="beta"/>
        //  </sequence>
        // </complexType>
        ComplexContentType alphaContent = new ComplexContentType();
        SequencePattern alphaSequence = new SequencePattern();
        alphaSequence.addParticle(elementSecondA);
        alphaContent.setParticle(alphaSequence);
        alpha.setContent(alphaContent);

        // Build ComplexType beta content:
        // <sequence>
        //      <element name="a" type="beta"/>
        // </sequence>
        ComplexContentType betaContent = new ComplexContentType();
        SequencePattern betaSequence = new SequencePattern();
        betaSequence.addParticle(elementThirdA);
        betaContent.setParticle(betaSequence);
        beta.setContent(betaContent);

        // Generate TypeAutomatons for testSchema
        TypeAutomatonFactory typeAutomatonFactory = new TypeAutomatonFactory();
        TypeAutomaton testTypeAutomaton = typeAutomatonFactory.createTypeAutomaton(testSchema);

        //Build result and begin construction of expected result
        HashMap<TypeAutomatonState, AncestorPattern> result = instance.calculate(testTypeAutomaton);
        HashMap<TypeAutomatonState, AncestorPattern> expResult = new HashMap<TypeAutomatonState, AncestorPattern>();

        // Build all PrefixElements
        SingleSlashPrefixElement singleSlashA = new SingleSlashPrefixElement(namespace, "a");

        // Build ancestor-pattern ((/a/a)*/a) for alpha
        Vector<AncestorPatternParticle> alphaSequenceVector = new Vector<AncestorPatternParticle>();
        Vector<AncestorPatternParticle> secondAlphaSequenceVector = new Vector<AncestorPatternParticle>();
        secondAlphaSequenceVector.add(singleSlashA);
        secondAlphaSequenceVector.add(singleSlashA);
        CardinalityParticle alphaCardinalityParticle = new CardinalityParticle(new SequenceExpression(secondAlphaSequenceVector), 0, 0);
        alphaSequenceVector.add(alphaCardinalityParticle);
        alphaSequenceVector.add(singleSlashA);
        AncestorPattern alphaAncestorPattern = new AncestorPattern(new SequenceExpression(alphaSequenceVector));

        // Build ancestor-pattern (/a(/a/a)*/a) for beta
        Vector<AncestorPatternParticle> betaSequenceVector = new Vector<AncestorPatternParticle>();
        betaSequenceVector.add(singleSlashA);
        Vector<AncestorPatternParticle> secondBetaSequenceVector = new Vector<AncestorPatternParticle>();
        secondBetaSequenceVector.add(singleSlashA);
        secondBetaSequenceVector.add(singleSlashA);
        CardinalityParticle betaCardinalityParticle = new CardinalityParticle(new SequenceExpression(secondBetaSequenceVector), 0, 0);
        betaSequenceVector.add(betaCardinalityParticle);
        betaSequenceVector.add(singleSlashA);
        AncestorPattern betaAncestorPattern = new AncestorPattern(new SequenceExpression(betaSequenceVector));

        // Build expected result
        LinkedList<TypeAutomatonState> expStates = new LinkedList<TypeAutomatonState>();
        expStates.add(new TypeAutomatonState(alpha));
        expStates.add(new TypeAutomatonState(beta));
        expResult.put(expStates.get(0), alphaAncestorPattern);
        expResult.put(expStates.get(1), betaAncestorPattern);

        /*
        System.out.println("Expected Results: ");
        System.out.println("alpha: " + alphaAncestorPattern);
        System.out.println("beta: " + betaAncestorPattern);
        System.out.println();

        System.out.println("Results: ");*/
        Vector<TypeAutomatonState> states = new Vector<TypeAutomatonState>(result.keySet());
        Collections.sort(states, new Comparator<TypeAutomatonState>() {

            public int compare(TypeAutomatonState typeAutomatonState, TypeAutomatonState secondtypeAutomatonState) {
                return typeAutomatonState.getType().getName().toString().compareTo(secondtypeAutomatonState.getType().getName().toString());
            }
        });
        for (int i = 0; i < states.size(); i++) {
            assertEquals(expResult.get(expStates.get(i)).toString(), result.get(states.get(i)).toString());
        // System.out.println(states.get(i).getType().getLocalName() + ": " + result.get(states.get(i)).toString());
        }
    }
}
