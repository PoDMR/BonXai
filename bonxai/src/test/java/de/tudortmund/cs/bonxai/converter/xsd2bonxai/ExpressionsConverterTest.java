package de.tudortmund.cs.bonxai.converter.xsd2bonxai;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.bonxai.*;
import de.tudortmund.cs.bonxai.xsd.Attribute;
import de.tudortmund.cs.bonxai.xsd.ComplexContentType;
import de.tudortmund.cs.bonxai.xsd.ComplexType;
import de.tudortmund.cs.bonxai.xsd.Element;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.SimpleContentRestriction;
import de.tudortmund.cs.bonxai.xsd.SimpleContentType;
import de.tudortmund.cs.bonxai.xsd.SimpleType;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

public class ExpressionsConverterTest extends ConverterTest {

    XSDSchema testSchema;
    ComplexType alpha;
    ComplexType beta;
    ComplexType gamma;
    ComplexType delta;
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
        delta = new ComplexType("{http://example.com/mynamespace}delta", null);
        testSchema.getTypeSymbolTable().createReference("{http://example.com/mynamespace}delta", delta);
        testSchema.addType(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}delta"));
        testSchema.getTypeSymbolTable().createReference("{http://example.com/mynamespace}string", new SimpleType("{http://example.com/mynamespace}string", null));

        // Build elments
        Element elementA = new Element("{http://example.com/mynamespace}a");
        Element elementSecondA = new Element("{http://example.com/mynamespace}a");
        Element elementB = new Element("{http://example.com/mynamespace}b");
        Element elementSecondB = new Element("{http://example.com/mynamespace}b");
        Element elementC = new Element("{http://example.com/mynamespace}c");
        Element elementD = new Element("{http://example.com/mynamespace}d");
        Element elementE = new Element("{http://example.com/mynamespace}e");
        Element elementX = new Element("{http://example.com/mynamespace}x");
        Element elementY = new Element("{http://example.com/mynamespace}y");
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

        // Build ComplexType alpha content
        // <sequence>
        //      <element name="b" type="beta"/>
        //      <element name="x" type="string"/>
        //      <element ref="y"/>
        // </sequence>
        // <attribute name="id" type="string"/>
        // <attribute name="name" type="string"/>
        ComplexContentType alphaContent = new ComplexContentType();
        SequencePattern alphaSequence = new SequencePattern();
        alphaSequence.addParticle(elementB);
        alphaSequence.addParticle(elementX);
        ElementRef elementYRef = new ElementRef(testSchema.getElementSymbolTable().getReference("{http://example.com/mynamespace}y"));
        alphaSequence.addParticle(elementYRef);
        alphaContent.setParticle(alphaSequence);
        alpha.setContent(alphaContent);
        Attribute attributeId = new Attribute("{http://example.com/mynamespace}id", testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}string"));
        alpha.addAttribute(attributeId);
        Attribute attributeName = new Attribute("{http://example.com/mynamespace}name", testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}string"));
        alpha.addAttribute(attributeName);

        // Build ComplexType beta content
        // <choice>
        //      <element name="a" type="gamma"/>
        //      <element name="c" type="alpha"/>
        //      <element name="e" type="gamma"/>
        // </choice>
        ComplexContentType betaContent = new ComplexContentType();
        ChoicePattern betaChoice = new ChoicePattern();
        betaChoice.addParticle(elementSecondA);
        betaChoice.addParticle(elementC);
        betaChoice.addParticle(elementE);
        betaContent.setParticle(betaChoice);
        beta.setContent(betaContent);

        // Build Complex gamma content:
        // <sequence>
        //      <element name="z" type="string"/>
        //      <element name="b" type="alpha"/>
        //      <element name="d" type="delta"/>
        // </sequence>
        ComplexContentType gammaContent = new ComplexContentType();
        SequencePattern gammaSequence = new SequencePattern();
        gammaSequence.addParticle(elementZ);
        gammaSequence.addParticle(elementSecondB);
        gammaSequence.addParticle(elementD);
        gammaContent.setParticle(gammaSequence);
        gamma.setContent(gammaContent);

        // Build ComplexType delta content:
        // <simpleContent>
        //  <extension base="string">
        //      <attribute name="id" type="string" />
        //  </extension>
        // </simpleContent>
        SimpleContentType deltaContent = new SimpleContentType();
        SimpleContentRestriction contentRestriction = new SimpleContentRestriction(testSchema.getTypeSymbolTable().getReference("{http://example.com/mynamespace}string"));
        deltaContent.setInheritance(contentRestriction);
        delta.setContent(deltaContent);
        delta.addAttribute(attributeId);

        // Generate TypeAutomatons for testSchema and secondTestSchema
        TypeAutomatonFactory typeAutomatonFactory = new TypeAutomatonFactory();
        testSchemaAutomaton = typeAutomatonFactory.createTypeAutomaton(testSchema);
    }

    /**
     * Test of setAncestorPathStrategy method, of class ExpressionsConverter.
     */
    @Test
    public void testSetAncestorPathStrategy() {
        AncestorPathStrategy ancestorPathStrategy = new SimpleAncestorPathStrategy();
        ExpressionsConverter instance = new ExpressionsConverter();
        instance.setAncestorPathStrategy(ancestorPathStrategy);
    }

    /**
     * Test of convert method with testSchema, of class ExpressionsConverter.
     */
    @Test
    public void testConvertWithTestSchema() {

        // Assemble all Bonxai Components
        String namespace = "http://example.com/mynamespace";
        GrammarList grammarList = new GrammarList();
        ConstraintList constraintList = new ConstraintList();

        // Build Bonxai Schema
        Bonxai expResult = new Bonxai();
        expResult.setGrammarList(grammarList);
        expResult.setConstraintList(constraintList);

        // Instantiate Processors
        AttributeProcessor attributeProcessor = new AttributeProcessor((SymbolTable<AttributeGroupElement>) expResult.getAttributeGroupElementSymbolTable());
        ParticleProcessor particleProcessor = new ParticleProcessor((SymbolTable<ElementGroupElement>) expResult.getGroupSymbolTable());

        // Build all PrefixElements
        SingleSlashPrefixElement singleSlashA = new SingleSlashPrefixElement(namespace, "a");
        SingleSlashPrefixElement singleSlashB = new SingleSlashPrefixElement(namespace, "b");
        SingleSlashPrefixElement singleSlashY = new SingleSlashPrefixElement(namespace, "y");
        DoubleSlashPrefixElement doubleSlashB = new DoubleSlashPrefixElement(namespace, "b");
        DoubleSlashPrefixElement doubleSlashC = new DoubleSlashPrefixElement(namespace, "c");
        DoubleSlashPrefixElement doubleSlashD = new DoubleSlashPrefixElement(namespace, "d");
        DoubleSlashPrefixElement doubleSlashE = new DoubleSlashPrefixElement(namespace, "e");

        // Build Expression for root element y former global element in XSD
        Expression expressionForRoot = new Expression();

        // Root element ancestor-pattern (/y)
        AncestorPattern rootAncestorPattern = new AncestorPattern(singleSlashY);
        expressionForRoot.setAncestorPattern(rootAncestorPattern);

        // Build child-pattern for root element
        BonxaiType bonxaiType = new BonxaiType("http://example.com/mynamespace", "string");
        ElementPattern rootElementPattern = new ElementPattern(bonxaiType);
        AttributePattern rootAttributePattern = new AttributePattern();
        ChildPattern rootChildPattern = new ChildPattern(rootAttributePattern, rootElementPattern);
        expressionForRoot.setChildPattern(rootChildPattern);

        // Build Expression for Alpha
        Expression expressionForAlpha = new Expression();

        // Alpha ancestor-pattern part (//b/a/b) from gamma to alpha transition
        Vector<AncestorPatternParticle> gammaToAlphaSequenceVector = new Vector<AncestorPatternParticle>();
        gammaToAlphaSequenceVector.add(doubleSlashB);
        gammaToAlphaSequenceVector.add(singleSlashA);
        gammaToAlphaSequenceVector.add(singleSlashB);
        SequenceExpression gammaToAlphaSequence = new SequenceExpression(gammaToAlphaSequenceVector);

        // Alpha ancestor-pattern second part (//e/b) from gamma to alpha transition
        Vector<AncestorPatternParticle> secondGammaToAlphaSequenceVector = new Vector<AncestorPatternParticle>();
        secondGammaToAlphaSequenceVector.add(doubleSlashE);
        secondGammaToAlphaSequenceVector.add(singleSlashB);
        SequenceExpression secondGammaToAlphaSequence = new SequenceExpression(secondGammaToAlphaSequenceVector);

        // Alpha ancestor-pattern ((/a)|(//e/b)|(//b/a/b)|(//c))
        Vector<AncestorPatternParticle> alphaVector = new Vector<AncestorPatternParticle>();
        alphaVector.add(singleSlashA);
        alphaVector.add(secondGammaToAlphaSequence);
        alphaVector.add(gammaToAlphaSequence);
        alphaVector.add(doubleSlashC);
        AncestorPattern alphaAncestorPattern = new AncestorPattern(new OrExpression(alphaVector));
        expressionForAlpha.setAncestorPattern(alphaAncestorPattern);

        // Build child-pattern for Alpha
        ElementPattern alphaElementPattern = new ElementPattern(particleProcessor.convertParticle(((ComplexContentType) alpha.getContent()).getParticle()));
        AttributePattern alphaAttributePattern = attributeProcessor.convertAttributes(alpha.getAttributes());
        ChildPattern alphaChildPattern = new ChildPattern(alphaAttributePattern, alphaElementPattern);
        expressionForAlpha.setChildPattern(alphaChildPattern);

        // Build Expression for Beta
        Expression expressionForBeta = new Expression();

        // Beta ancestor-pattern part (/a/b) from alpha to beta transition
        Vector<AncestorPatternParticle> alphaToBetaSequenceVector = new Vector<AncestorPatternParticle>();
        alphaToBetaSequenceVector.add(singleSlashA);
        alphaToBetaSequenceVector.add(singleSlashB);
        SequenceExpression alphaToBetaSequence = new SequenceExpression(alphaToBetaSequenceVector);

        // Beta ancestor-pattern part (//c/b) from alpha to beta transition
        Vector<AncestorPatternParticle> thirdAlphaToBetaSequenceVector = new Vector<AncestorPatternParticle>();
        thirdAlphaToBetaSequenceVector.add(doubleSlashC);
        thirdAlphaToBetaSequenceVector.add(singleSlashB);
        SequenceExpression thirdAlphaToBetaSequence = new SequenceExpression(thirdAlphaToBetaSequenceVector);

        // Beta ancestor-pattern part (//b/b) from alpha to beta transition
        Vector<AncestorPatternParticle> secondAlphaToBetaSequenceVector = new Vector<AncestorPatternParticle>();
        secondAlphaToBetaSequenceVector.add(doubleSlashB);
        secondAlphaToBetaSequenceVector.add(singleSlashB);
        SequenceExpression secondAlphaToBetaSequence = new SequenceExpression(secondAlphaToBetaSequenceVector);

        // Beta ancestor-pattern ((/a/b)|(//b/b)|(//c/b))
        Vector<AncestorPatternParticle> betaVector = new Vector<AncestorPatternParticle>();
        betaVector.add(alphaToBetaSequence);
        betaVector.add(thirdAlphaToBetaSequence);
        betaVector.add(secondAlphaToBetaSequence);
        AncestorPattern betaAncestorPattern = new AncestorPattern(new OrExpression(betaVector));
        expressionForBeta.setAncestorPattern(betaAncestorPattern);

        // Build child-pattern for Beta
        ElementPattern betaElementPattern = new ElementPattern(particleProcessor.convertParticle(((ComplexContentType) beta.getContent()).getParticle()));
        AttributePattern betaAttributePattern = attributeProcessor.convertAttributes(beta.getAttributes());
        ChildPattern betaChildPattern = new ChildPattern(betaAttributePattern, betaElementPattern);
        expressionForBeta.setChildPattern(betaChildPattern);

        // Build Expression for Gamma
        Expression expressionForGamma = new Expression();

        // Beta ancestor-pattern part (//b/a)
        Vector<AncestorPatternParticle> gammaSequencePartVector = new Vector<AncestorPatternParticle>();
        gammaSequencePartVector.add(doubleSlashB);
        gammaSequencePartVector.add(singleSlashA);
        SequenceExpression gammaSequence = new SequenceExpression(gammaSequencePartVector);

        // Gamma ancestor-pattern ((//b/a)|(//e))
        Vector<AncestorPatternParticle> gammaVector = new Vector<AncestorPatternParticle>();
        gammaVector.add(gammaSequence);
        gammaVector.add(doubleSlashE);
        AncestorPattern gammaAncestorPattern = new AncestorPattern(new OrExpression(gammaVector));
        expressionForGamma.setAncestorPattern(gammaAncestorPattern);

        // Build child-pattern for Gamma
        ElementPattern gammaElementPattern = new ElementPattern(particleProcessor.convertParticle(((ComplexContentType) gamma.getContent()).getParticle()));
        AttributePattern gammaAttributePattern = attributeProcessor.convertAttributes(gamma.getAttributes());
        ChildPattern gammaChildPattern = new ChildPattern(gammaAttributePattern, gammaElementPattern);
        expressionForGamma.setChildPattern(gammaChildPattern);

        // Build Expression for Delta
        Expression expressionForDelta = new Expression();

        // Delta ancestor-pattern (//d)
        AncestorPattern deltaAncestorPattern = new AncestorPattern(doubleSlashD);
        expressionForDelta.setAncestorPattern(deltaAncestorPattern);

        // Build child-pattern for Delta
        BonxaiType deltaBonxaiType = new BonxaiType("http://example.com/mynamespace", "string");
        ElementPattern deltaElementPattern = new ElementPattern(deltaBonxaiType);
        AttributePattern deltaAttributePattern = attributeProcessor.convertAttributes(delta.getAttributes());
        ChildPattern deltaChildPattern = new ChildPattern(deltaAttributePattern, deltaElementPattern);
        expressionForDelta.setChildPattern(deltaChildPattern);

        // Build Grammarlist
        grammarList.addExpression(expressionForAlpha);
        grammarList.addExpression(expressionForBeta);
        grammarList.addExpression(expressionForGamma);
        grammarList.addExpression(expressionForDelta);

        // Sort the GrammarList to this point and add the Expression for the root Element to the end
        Collections.sort(grammarList.getExpressions(), new Comparator<Expression>() {

            public int compare(Expression expression, Expression secondExpression) {
                return expression.getAncestorPattern().toString().compareTo(secondExpression.getAncestorPattern().toString());
            }
        });
        grammarList.addExpression(expressionForRoot);

        // Validate ExpressionConverter
        ExpressionsConverter instance = new ExpressionsConverter();
        assertNotNull(testSchema);
        assertNotNull(testSchemaAutomaton);
        Bonxai result = instance.convert(testSchema, testSchemaAutomaton, new Bonxai());

        // Validate that both GrammarLists have the same size and the Expressions match each other (only same ancestor-paths)
        assertEquals(expResult.getGrammarList().getExpressions().size(), result.getGrammarList().getExpressions().size());
        for (int i = 0; i < expResult.getGrammarList().getExpressions().size(); i++) {
            String expAncestorPath = expResult.getGrammarList().getExpressions().get(i).getAncestorPattern().toString();
            String resultAncestorPath = result.getGrammarList().getExpressions().get(i).getAncestorPattern().toString();
            if(i == expResult.getGrammarList().getExpressions().size()-1){
                assertEquals(expResult.getGrammarList().getExpressions().get(i).getChildPattern().getElementPattern().getBonxaiType().getType(),
                        result.getGrammarList().getExpressions().get(i).getChildPattern().getElementPattern().getBonxaiType().getType());
            }
            //System.out.println("Expected ancestor-path: " + expAncestorPath);
            //System.out.println("Actual ancestor-path:   " + resultAncestorPath);
            assertTrue(expAncestorPath.equals(resultAncestorPath));
        }
    }
}
