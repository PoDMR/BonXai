package de.tudortmund.cs.bonxai.xsd.setOperations.union;

import de.tudortmund.cs.bonxai.common.AnyAttribute;
import de.tudortmund.cs.bonxai.common.AnyPattern;
import de.tudortmund.cs.bonxai.common.ChoicePattern;
import de.tudortmund.cs.bonxai.common.GroupRef;
import de.tudortmund.cs.bonxai.common.ProcessContentsInstruction;
import de.tudortmund.cs.bonxai.common.SequencePattern;
import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import de.tudortmund.cs.bonxai.xsd.Element.Block;
import de.tudortmund.cs.bonxai.xsd.Element.Final;
import de.tudortmund.cs.bonxai.xsd.XSDSchema.BlockDefault;
import de.tudortmund.cs.bonxai.xsd.XSDSchema.FinalDefault;
import de.tudortmund.cs.bonxai.xsd.XSDSchema.Qualification;
import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons.*;
import de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons.exceptions.*;
import de.tudortmund.cs.bonxai.xsd.parser.XSDParser;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>SchemaUnionGenerator</tt> class, checks that every
 * method of this class performs properly.
 * @author Dominik Wolff
 */
public class SchemaUnionGeneratorTest extends junit.framework.TestCase {

    public SchemaUnionGeneratorTest() {
    }

    /**
     * Test of generateUnion method, of class SchemaUnionGenerator.
     */
    @Test
    public void testGenerateUnion() throws Exception {
        XSDParser xmlSchemaParser = new XSDParser(false, false);
        XSDSchema schemaA = xmlSchemaParser.parse(this.getClass().getResource("/tests/de/tudortmund/cs/bonxai/xsd/setOperations/union/xsds/testGenerateUnionA.xsd").getFile());
        XSDSchema schemaB = xmlSchemaParser.parse(this.getClass().getResource("/tests/de/tudortmund/cs/bonxai/xsd/setOperations/union/xsds/testGenerateUnionB.xsd").getFile());
        XSDSchema schemaC = xmlSchemaParser.parse(this.getClass().getResource("/tests/de/tudortmund/cs/bonxai/xsd/setOperations/union/xsds/testGenerateUnionC.xsd").getFile());
        LinkedHashSet<XSDSchema> minuendSchemata = new LinkedHashSet<XSDSchema>();
        minuendSchemata.add(schemaA);
        minuendSchemata.add(schemaB);
        minuendSchemata.add(schemaC);
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        LinkedHashMap<String, LinkedHashSet<Element>> namespaceConflictingElementsMap = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(schemaA.getTypeSymbolTable().getReference("{A}complexTypeA").getReference(), schemaA);
        typeOldSchemaMap.put(schemaA.getTypeSymbolTable().getReference("{A}complexTypeD").getReference(), schemaA);
        typeOldSchemaMap.put(schemaB.getTypeSymbolTable().getReference("{A}complexTypeA").getReference(), schemaB);
        typeOldSchemaMap.put(schemaB.getTypeSymbolTable().getReference("{A}complexTypeB").getReference(), schemaB);
        typeOldSchemaMap.put(schemaB.getTypeSymbolTable().getReference("{A}complexTypeC").getReference(), schemaB);
        typeOldSchemaMap.put(schemaB.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string").getReference(), schemaB);
        typeOldSchemaMap.put(schemaC.getTypeSymbolTable().getReference("{A}complexTypeA").getReference(), schemaC);
        typeOldSchemaMap.put(schemaC.getTypeSymbolTable().getReference("{A}complexTypeE").getReference(), schemaC);
        typeOldSchemaMap.put(schemaC.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}integer").getReference(), schemaC);
        typeOldSchemaMap.put(schemaC.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string").getReference(), schemaC);
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put((AnyPattern) ((ChoicePattern) ((ComplexContentType) ((ComplexType) schemaA.getTypeSymbolTable().getReference("{A}complexTypeD").getReference()).getContent()).getParticle()).getParticles().getFirst(), schemaA);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(schemaA.getElementSymbolTable().getReference("{A}elementA").getReference(), schemaA);
        elementOldSchemaMap.put(schemaA.getElementSymbolTable().getReference("{A}elementC").getReference(), schemaA);
        elementOldSchemaMap.put(schemaB.getElementSymbolTable().getReference("{A}elementA").getReference(), schemaB);
        elementOldSchemaMap.put((Element) ((ChoicePattern) ((ComplexContentType) ((ComplexType) schemaB.getTypeSymbolTable().getReference("{A}complexTypeA").getReference()).getContent()).getParticle()).getParticles().getFirst(), schemaB);
        elementOldSchemaMap.put((Element) ((ChoicePattern) ((ComplexContentType) ((ComplexType) schemaB.getTypeSymbolTable().getReference("{A}complexTypeA").getReference()).getContent()).getParticle()).getParticles().getLast(), schemaB);
        elementOldSchemaMap.put((Element) ((SequencePattern) ((ComplexContentType) ((ComplexType) schemaB.getTypeSymbolTable().getReference("{A}complexTypeB").getReference()).getContent()).getParticle()).getParticles().getFirst(), schemaB);
        elementOldSchemaMap.put((Element) ((ChoicePattern) ((ComplexContentType) ((ComplexType) schemaB.getTypeSymbolTable().getReference("{A}complexTypeC").getReference()).getContent()).getParticle()).getParticles().getFirst(), schemaB);
        elementOldSchemaMap.put(schemaC.getElementSymbolTable().getReference("{A}elementA").getReference(), schemaC);
        elementOldSchemaMap.put(schemaC.getElementSymbolTable().getReference("{A}elementE").getReference(), schemaC);
        elementOldSchemaMap.put((Element) ((ChoicePattern) ((ComplexContentType) ((ComplexType) schemaC.getTypeSymbolTable().getReference("{A}complexTypeA").getReference()).getContent()).getParticle()).getParticles().getFirst(), schemaC);
        elementOldSchemaMap.put((Element) ((SequencePattern) ((ComplexContentType) ((ComplexType) schemaC.getTypeSymbolTable().getReference("{A}complexTypeE").getReference()).getContent()).getParticle()).getParticles().getFirst(), schemaC);
        LinkedHashSet usedIDs = new LinkedHashSet();
        LinkedHashSet<XSDSchema> otherSchemata = new LinkedHashSet<XSDSchema>();
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        String workingDirectory = "c:/";
        SchemaUnionGenerator instance = new SchemaUnionGenerator(minuendSchemata, namespaceOutputSchemaMap, namespaceConflictingElementsMap, namespaceConflictingAttributeMap, anyAttributeOldSchemaMap, attributeOldSchemaMap, typeOldSchemaMap, anyPatternOldSchemaMap, elementOldSchemaMap, usedIDs, otherSchemata, namespaceAbbreviationMap, workingDirectory);
        XSDSchema outputSchema = new XSDSchema("A");

        instance.generateUnion(outputSchema);

        assertEquals("A", outputSchema.getTargetNamespace());
        assertEquals(Qualification.unqualified, outputSchema.getAttributeFormDefault());
        assertTrue(outputSchema.getAttributeGroupSymbolTable().getReferences().isEmpty());
        assertTrue(outputSchema.getAttributeGroups().isEmpty());
        assertEquals(2, outputSchema.getAttributeSymbolTable().getReferences().size());
        assertEquals("{A}attributeC", outputSchema.getAttributeSymbolTable().getReferences().getFirst().getReference().getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", outputSchema.getAttributeSymbolTable().getReferences().getFirst().getReference().getSimpleType().getName());
        assertEquals("{A}attributeB", outputSchema.getAttributeSymbolTable().getReferences().getLast().getReference().getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", outputSchema.getAttributeSymbolTable().getReferences().getLast().getReference().getSimpleType().getName());

        LinkedList<Attribute> attributes = new LinkedList<Attribute>();
        for (SymbolTableRef<Attribute> attributeRef : outputSchema.getAttributeSymbolTable().getReferences()) {
            attributes.add(attributeRef.getReference());
        }
        assertEquals(attributes, outputSchema.getAttributes());
        assertEquals(new HashSet(), outputSchema.getBlockDefaults());
        assertEquals(new HashSet(), outputSchema.getConstraintNames());
        assertEquals(Qualification.unqualified, outputSchema.getElementFormDefault());
        assertEquals(3, outputSchema.getElementSymbolTable().getReferences().size());
        assertEquals("{A}elementC", outputSchema.getElementSymbolTable().getReferences().get(0).getReference().getName());
        assertEquals("{A}complexTypeD", outputSchema.getElementSymbolTable().getReferences().get(0).getReference().getType().getName());
        assertEquals("{A}elementA", outputSchema.getElementSymbolTable().getReferences().get(1).getReference().getName());
        assertEquals("{A}union-type.complexTypeA.complexTypeA.complexTypeA", outputSchema.getElementSymbolTable().getReferences().get(1).getReference().getType().getName());
        assertEquals("{A}elementE", outputSchema.getElementSymbolTable().getReferences().get(2).getReference().getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}integer", outputSchema.getElementSymbolTable().getReferences().get(2).getReference().getType().getName());
        LinkedList<Element> elements = new LinkedList<Element>();
        elements.add(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference());
        elements.add(outputSchema.getElementSymbolTable().getReference("{A}elementC").getReference());
        elements.add(outputSchema.getElementSymbolTable().getReference("{A}elementE").getReference());
        assertEquals(elements, outputSchema.getElements());
        assertEquals(new HashSet(), outputSchema.getFinalDefaults());
        assertTrue(outputSchema.getForeignSchemas().isEmpty());
        assertEquals(1, outputSchema.getGroupSymbolTable().getReferences().getFirst().getReference().getParticleContainer().getParticles().size());
        assertTrue(outputSchema.getGroupSymbolTable().getReferences().getFirst().getReference().getParticleContainer().getParticles().getFirst() instanceof ElementRef);
        assertEquals(outputSchema.getElementSymbolTable().getReference("{A}elementC").getReference(), ((ElementRef) outputSchema.getGroupSymbolTable().getReferences().getFirst().getReference().getParticleContainer().getParticles().getFirst()).getElement());

        LinkedList<Group> groups = new LinkedList<Group>();
        groups.add(outputSchema.getGroupSymbolTable().getReference("{A}groupA").getReference());
        assertEquals(groups, outputSchema.getGroups());
        assertEquals(null, outputSchema.getId());
        assertTrue(outputSchema.getKeyAndUniqueSymbolTable().getReferences().isEmpty());
        assertTrue(outputSchema.getNamespaceList().getIdentifiedNamespaces().isEmpty());
        assertEquals(null, outputSchema.getSchemaLocation());
        assertEquals(new HashMap(), outputSchema.getSubstitutionElements());
        assertEquals(8, outputSchema.getTypeSymbolTable().getReferences().size());
        assertTrue(outputSchema.getTypeSymbolTable().getReferences().get(0).getReference() instanceof ComplexType);

        ComplexType complexType = (ComplexType) outputSchema.getTypeSymbolTable().getReferences().get(0).getReference();
        assertEquals("{A}union-type.complexTypeC.complexTypeE", complexType.getName());
        assertEquals(false, (boolean) complexType.getMixed());
        assertTrue(complexType.getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) complexType.getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(2, ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().size());
        assertTrue(((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst() instanceof ChoicePattern);
        assertTrue(((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getParticles().getFirst() instanceof Element);
        assertEquals("{A}elementA", ((Element) ((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getParticles().getFirst()).getName());
        assertEquals("{A}union-type.complexTypeB.string", ((Element) ((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getParticles().getFirst()).getType().getName());
        assertTrue(((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getLast() instanceof SequencePattern);
        assertTrue(((SequencePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getLast()).getParticles().getFirst() instanceof Element);
        assertEquals("{A}elementA", ((Element) ((SequencePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getLast()).getParticles().getFirst()).getName());
        assertEquals("{A}union-type.complexTypeB.string", ((Element) ((SequencePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getLast()).getParticles().getFirst()).getType().getName());
        assertTrue(outputSchema.getTypeSymbolTable().getReferences().get(1).getReference() instanceof ComplexType);

        complexType = (ComplexType) outputSchema.getTypeSymbolTable().getReferences().get(1).getReference();
        assertEquals("{A}complexTypeB", complexType.getName());
        assertEquals(false, (boolean) complexType.getMixed());
        assertTrue(complexType.getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) complexType.getContent()).getParticle() instanceof SequencePattern);
        assertEquals(1, ((SequencePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().size());
        assertTrue(((SequencePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst() instanceof Element);
        assertEquals("{A}elementC", ((Element) ((SequencePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Element) ((SequencePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getType().getName());
        assertTrue(outputSchema.getTypeSymbolTable().getReferences().get(2).getReference() instanceof ComplexType);

        complexType = (ComplexType) outputSchema.getTypeSymbolTable().getReferences().get(2).getReference();
        assertEquals("{A}complexTypeA", complexType.getName());
        assertEquals(false, (boolean) complexType.getMixed());
        assertTrue(complexType.getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) complexType.getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(1, ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().size());
        assertTrue(((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst() instanceof GroupRef);
        assertTrue(outputSchema.getTypeSymbolTable().getReferences().get(3).getReference() instanceof ComplexType);

        complexType = (ComplexType) outputSchema.getTypeSymbolTable().getReferences().get(3).getReference();
        assertEquals("{A}union-type.complexTypeA.complexTypeA.complexTypeA", complexType.getName());
        assertEquals(false, (boolean) complexType.getMixed());
        assertTrue(complexType.getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) complexType.getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(3, ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().size());
        assertTrue(((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(0) instanceof ChoicePattern);
        assertEquals(1, ((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(0)).getParticles().size());
        assertTrue(((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(0)).getParticles().getFirst() instanceof GroupRef);
        assertTrue(((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(1) instanceof ChoicePattern);
        assertEquals(2, ((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(1)).getParticles().size());
        assertTrue(((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(1)).getParticles().getFirst() instanceof Element);
        assertEquals("{A}elementA", ((Element) ((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(1)).getParticles().getFirst()).getName());
        assertEquals("{A}complexTypeB", ((Element) ((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(1)).getParticles().getFirst()).getType().getName());
        assertTrue(((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(1)).getParticles().getLast() instanceof Element);
        assertEquals("{A}elementB", ((Element) ((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(1)).getParticles().getLast()).getName());
        assertEquals("{A}union-type.complexTypeC.complexTypeE", ((Element) ((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(1)).getParticles().getLast()).getType().getName());
        assertTrue(((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(2) instanceof ChoicePattern);
        assertEquals(1, ((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(2)).getParticles().size());
        assertTrue(((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(2)).getParticles().getFirst() instanceof Element);
        assertEquals("{A}elementB", ((Element) ((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(2)).getParticles().getFirst()).getName());
        assertEquals("{A}union-type.complexTypeC.complexTypeE", ((Element) ((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(2)).getParticles().getFirst()).getType().getName());
        assertTrue(outputSchema.getTypeSymbolTable().getReferences().get(4).getReference() instanceof SimpleType);
        SimpleType simpleType = (SimpleType) outputSchema.getTypeSymbolTable().getReferences().get(4).getReference();
        assertEquals("{http://www.w3.org/2001/XMLSchema}integer", simpleType.getName());
        assertEquals(null, simpleType.getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().getReferences().get(5).getReference() instanceof ComplexType);
        complexType = (ComplexType) outputSchema.getTypeSymbolTable().getReferences().get(5).getReference();
        assertEquals("{A}union-type.complexTypeB.string", complexType.getName());
        assertEquals(true, (boolean) complexType.getMixed());
        assertTrue(complexType.getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) complexType.getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(2, ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().size());
        assertTrue(((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst() instanceof SequencePattern);
        assertTrue(((SequencePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getParticles().getFirst() instanceof Element);
        assertEquals("{A}elementC", ((Element) ((SequencePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getParticles().getFirst()).getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Element) ((SequencePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getParticles().getFirst()).getType().getName());
        assertTrue(((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getLast() instanceof SequencePattern);
        assertTrue(((SequencePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getLast()).getParticles().isEmpty());
        assertTrue(outputSchema.getTypeSymbolTable().getReferences().get(6).getReference() instanceof ComplexType);

        complexType = (ComplexType) outputSchema.getTypeSymbolTable().getReferences().get(6).getReference();
        assertEquals("{A}complexTypeD", complexType.getName());
        assertEquals(false, (boolean) complexType.getMixed());
        assertTrue(complexType.getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) complexType.getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(1, ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().size());
        assertTrue(((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst() instanceof ChoicePattern);
        assertEquals(2, ((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getParticles().size());
        assertTrue(((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getParticles().getFirst() instanceof Element);
        assertEquals("{A}elementA", ((Element) ((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getParticles().getFirst()).getName());
        assertEquals("{A}complexTypeA", ((Element) ((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getParticles().getFirst()).getType().getName());
        assertTrue(((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getParticles().getLast() instanceof Element);
        assertEquals("{A}elementC", ((Element) ((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getParticles().getLast()).getName());
        assertEquals("{A}complexTypeD", ((Element) ((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getParticles().getLast()).getType().getName());
        assertTrue(outputSchema.getTypeSymbolTable().getReferences().get(7).getReference() instanceof SimpleType);

        simpleType = (SimpleType) outputSchema.getTypeSymbolTable().getReferences().get(7).getReference();
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", simpleType.getName());
        assertEquals(null, simpleType.getInheritance());
        LinkedList<Type> types = new LinkedList<Type>();
        types.add(outputSchema.getTypeSymbolTable().getReference("{A}union-type.complexTypeA.complexTypeA.complexTypeA").getReference());
        types.add(outputSchema.getTypeSymbolTable().getReference("{A}complexTypeD").getReference());
        types.add(outputSchema.getTypeSymbolTable().getReference("{A}complexTypeA").getReference());
        types.add(outputSchema.getTypeSymbolTable().getReference("{A}complexTypeB").getReference());
        types.add(outputSchema.getTypeSymbolTable().getReference("{A}union-type.complexTypeC.complexTypeE").getReference());
        types.add(outputSchema.getTypeSymbolTable().getReference("{A}union-type.complexTypeB.string").getReference());
        assertEquals(types, outputSchema.getTypes());
    }

    /**
     * Test of getNameTopLevelAttributesMap method, of class SchemaUnionGenerator.
     */
    @Test
    public void testGetNameTopLevelAttributesMap() {
        XSDSchema schemaA = new XSDSchema("A");
        Attribute attributeA = new Attribute("{A}attributeA");
        schemaA.getAttributeSymbolTable().updateOrCreateReference("{A}attributeA", attributeA);
        schemaA.addAttribute(schemaA.getAttributeSymbolTable().getReference("{A}attributeA"));

        XSDSchema schemaB = new XSDSchema("A");
        Attribute attributeB = new Attribute("{A}attributeB");
        schemaB.getAttributeSymbolTable().updateOrCreateReference("{A}attributeB", attributeB);
        schemaB.addAttribute(schemaB.getAttributeSymbolTable().getReference("{A}attributeB"));

        XSDSchema schemaC = new XSDSchema("A");
        Attribute attributeC = new Attribute("{A}attributeA");
        schemaC.getAttributeSymbolTable().updateOrCreateReference("{A}attributeA", attributeC);
        schemaC.addAttribute(schemaC.getAttributeSymbolTable().getReference("{A}attributeA"));

        LinkedHashSet<XSDSchema> minuendSchemata = new LinkedHashSet<XSDSchema>();
        minuendSchemata.add(schemaA);
        minuendSchemata.add(schemaB);
        minuendSchemata.add(schemaC);

        SchemaUnionGenerator instance = new SchemaUnionGenerator(minuendSchemata, null, null, null, null, null, null, null, null, null, null, null, null);

        HashMap<String, LinkedHashSet<Attribute>> expResult = new HashMap<String, LinkedHashSet<Attribute>>();

        LinkedHashSet<Attribute> attributesA = new LinkedHashSet<Attribute>();
        attributesA.add(attributeA);
        attributesA.add(attributeC);
        expResult.put("{A}attributeA", attributesA);

        LinkedHashSet<Attribute> attributesB = new LinkedHashSet<Attribute>();
        attributesB.add(attributeB);
        expResult.put("{A}attributeB", attributesB);

        HashMap<String, LinkedHashSet<Attribute>> result = instance.getNameTopLevelAttributesMap();

        assertEquals(expResult, result);
    }

    /**
     * Test of getTypeName method, of class SchemaUnionGenerator for one 
     * contained type.
     */
    @Test
    public void testGetTypeNameSizeOne() throws InvalidTypeStateContentException, EmptyProductTypeStateFieldException {
        SimpleType simpleTypeA = new SimpleType("{}simpleTypeA", null);
        TypeState stateA = new TypeState(simpleTypeA);

        LinkedList<TypeState> typeStates = new LinkedList<TypeState>();
        typeStates.add(stateA);

        TypeState typeState = new ProductTypeState(typeStates);

        XSDSchema outputSchema = new XSDSchema("A");
        SchemaUnionGenerator instance = new SchemaUnionGenerator(null, null, null, null, null, null, null, null, null, null, null, null, null);

        String expResult = "{A}simpleTypeA";
        String result = instance.getTypeName(typeState, outputSchema);

        assertEquals(expResult, result);
    }

    /**
     * Test of getTypeName method, of class SchemaUnionGenerator for three
     * contained type.
     */
    @Test
    public void testGetTypeNameSizeThree() throws InvalidTypeStateContentException, EmptyProductTypeStateFieldException {
        SimpleType simpleTypeA = new SimpleType("{}simpleTypeA", null);
        TypeState stateA = new TypeState(simpleTypeA);

        SimpleType simpleTypeB = new SimpleType("{}simpleTypeB", null);
        TypeState stateB = new TypeState(simpleTypeB);

        LinkedList<TypeState> typeStates = new LinkedList<TypeState>();
        typeStates.add(stateA);
        typeStates.add(null);
        typeStates.add(stateB);

        TypeState typeState = new ProductTypeState(typeStates);

        XSDSchema outputSchema = new XSDSchema("A");
        SchemaUnionGenerator instance = new SchemaUnionGenerator(null, null, null, null, null, null, null, null, null, null, null, null, null);

        String expResult = "{A}union-type.simpleTypeA.simpleTypeB";
        String result = instance.getTypeName(typeState, outputSchema);

        assertEquals(expResult, result);
    }

    /**
     * Test of getTypeName method, of class SchemaUnionGenerator for one
     * contained build-in type.
     */
    @Test
    public void testGetTypeNameSizeOneBuildIn() throws InvalidTypeStateContentException, EmptyProductTypeStateFieldException {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        TypeState stateA = new TypeState(simpleTypeA);

        LinkedList<TypeState> typeStates = new LinkedList<TypeState>();
        typeStates.add(stateA);

        TypeState typeState = new ProductTypeState(typeStates);

        XSDSchema outputSchema = new XSDSchema("A");
        SchemaUnionGenerator instance = new SchemaUnionGenerator(null, null, null, null, null, null, null, null, null, null, null, null, null);

        String expResult = "{http://www.w3.org/2001/XMLSchema}string";
        String result = instance.getTypeName(typeState, outputSchema);

        assertEquals(expResult, result);
    }

    /**
     * Test of getTypeName method, of class SchemaUnionGenerator for three
     * contained type with any type.
     */
    @Test
    public void testGetTypeNameSizeThreeAnyType() throws InvalidTypeStateContentException, EmptyProductTypeStateFieldException {
        SimpleType simpleTypeA = new SimpleType("{}simpleTypeA", null);
        TypeState stateA = new TypeState(simpleTypeA);

        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}anyType", null);
        TypeState stateB = new TypeState(simpleTypeB);

        LinkedList<TypeState> typeStates = new LinkedList<TypeState>();
        typeStates.add(stateA);
        typeStates.add(null);
        typeStates.add(stateB);

        TypeState typeState = new ProductTypeState(typeStates);

        XSDSchema outputSchema = new XSDSchema("A");
        SchemaUnionGenerator instance = new SchemaUnionGenerator(null, null, null, null, null, null, null, null, null, null, null, null, null);

        String expResult = "{http://www.w3.org/2001/XMLSchema}anyType";
        String result = instance.getTypeName(typeState, outputSchema);

        assertEquals(expResult, result);
    }

    /**
     * Test of getTypeName method, of class SchemaUnionGenerator for three
     * contained type with any simple type without complexType.
     */
    @Test
    public void testGetTypeNameSizeThreeAnySimpleTypeWithout() throws InvalidTypeStateContentException, EmptyProductTypeStateFieldException {
        SimpleType simpleTypeA = new SimpleType("{}simpleTypeA", null);
        TypeState stateA = new TypeState(simpleTypeA);

        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        TypeState stateB = new TypeState(simpleTypeB);

        LinkedList<TypeState> typeStates = new LinkedList<TypeState>();
        typeStates.add(stateA);
        typeStates.add(null);
        typeStates.add(stateB);

        TypeState typeState = new ProductTypeState(typeStates);

        XSDSchema outputSchema = new XSDSchema("A");
        SchemaUnionGenerator instance = new SchemaUnionGenerator(null, null, null, null, null, null, null, null, null, null, null, null, null);

        String expResult = "{http://www.w3.org/2001/XMLSchema}anySimpleType";
        String result = instance.getTypeName(typeState, outputSchema);

        assertEquals(expResult, result);
    }

    /**
     * Test of getTypeName method, of class SchemaUnionGenerator for three
     * contained type with any simple type with complexType.
     */
    @Test
    public void testGetTypeNameSizeThreeAnySimpleTypeWith() throws InvalidTypeStateContentException, EmptyProductTypeStateFieldException {
        ComplexType complexTypeA = new ComplexType("{}complexTypeA", null);
        TypeState stateA = new TypeState(complexTypeA);

        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        TypeState stateB = new TypeState(simpleTypeB);

        LinkedList<TypeState> typeStates = new LinkedList<TypeState>();
        typeStates.add(stateA);
        typeStates.add(null);
        typeStates.add(stateB);

        TypeState typeState = new ProductTypeState(typeStates);

        XSDSchema outputSchema = new XSDSchema("A");
        SchemaUnionGenerator instance = new SchemaUnionGenerator(null, null, null, null, null, null, null, null, null, null, null, null, null);

        String expResult = "{A}union-type.complexTypeA.anySimpleType";
        String result = instance.getTypeName(typeState, outputSchema);

        assertEquals(expResult, result);
    }

    /**
     * Test of getTypeName method, of class SchemaUnionGenerator for one
     * contained type and check number.
     */
    @Test
    public void testGetTypeNameSizeOneNumber() throws InvalidTypeStateContentException, EmptyProductTypeStateFieldException {
        SimpleType simpleTypeA = new SimpleType("{}simpleTypeA", null);
        TypeState stateA = new TypeState(simpleTypeA);

        LinkedList<TypeState> typeStates = new LinkedList<TypeState>();
        typeStates.add(stateA);

        TypeState typeState = new ProductTypeState(typeStates);

        XSDSchema outputSchema = new XSDSchema("A");
        SimpleType simpleTypeA2 = new SimpleType("{A}simpleTypeA", null);
        outputSchema.getTypeSymbolTable().updateOrCreateReference("{A}simpleTypeA", simpleTypeA2);
        outputSchema.addType(outputSchema.getTypeSymbolTable().getReference("{A}simpleTypeA"));

        SchemaUnionGenerator instance = new SchemaUnionGenerator(null, null, null, null, null, null, null, null, null, null, null, null, null);

        String expResult = "{A}simpleTypeA.1";
        String result = instance.getTypeName(typeState, outputSchema);

        assertEquals(expResult, result);
    }

    /**
     * Test of generateNewBlockDefaultAttribute method, of class
     * SchemaUnionGenerator for a schema which blocks extension.
     */
    @Test
    public void testGenerateNewBlockDefaultAttributeExtension() {
        XSDSchema schemaA = new XSDSchema("A");
        schemaA.addBlockDefault(BlockDefault.extension);
        schemaA.addBlockDefault(BlockDefault.restriction);
        schemaA.addBlockDefault(BlockDefault.substitution);

        XSDSchema schemaB = new XSDSchema("A");
        schemaB.addBlockDefault(BlockDefault.extension);
        schemaB.addBlockDefault(BlockDefault.substitution);

        Element elementB = new Element("{A}elementB");
        schemaB.addElement(new SymbolTableRef<Element>("{A}elementB", elementB));

        XSDSchema schemaC = new XSDSchema("A");
        schemaC.addBlockDefault(BlockDefault.extension);
        schemaC.addBlockDefault(BlockDefault.restriction);

        LinkedHashSet<XSDSchema> minuendSchemata = new LinkedHashSet<XSDSchema>();
        minuendSchemata.add(schemaA);
        minuendSchemata.add(schemaB);
        minuendSchemata.add(schemaC);

        SchemaUnionGenerator instance = new SchemaUnionGenerator(minuendSchemata, null, null, null, null, null, null, null, null, null, null, null, null);

        LinkedHashSet<BlockDefault> expResult = new LinkedHashSet<XSDSchema.BlockDefault>();
        expResult.add(XSDSchema.BlockDefault.extension);
        LinkedHashSet<BlockDefault> result = instance.generateNewBlockDefaultAttribute();

        assertEquals(expResult, result);

        HashSet<Block> block = new HashSet<Block>();
        block.add(Block.extension);
        block.add(Block.substitution);
        assertEquals(block, elementB.getBlockModifiers());
    }

    /**
     * Test of generateNewBlockDefaultAttribute method, of class
     * SchemaUnionGenerator for a schema which blocks restriction.
     */
    @Test
    public void testGenerateNewBlockDefaultAttributeRestriction() {
        XSDSchema schemaA = new XSDSchema("A");
        schemaA.addBlockDefault(BlockDefault.extension);
        schemaA.addBlockDefault(BlockDefault.restriction);
        schemaA.addBlockDefault(BlockDefault.substitution);

        XSDSchema schemaB = new XSDSchema("A");
        schemaB.addBlockDefault(BlockDefault.extension);
        schemaB.addBlockDefault(BlockDefault.restriction);

        Element elementB = new Element("{A}elementB");
        schemaB.addElement(new SymbolTableRef<Element>("{A}elementB", elementB));

        XSDSchema schemaC = new XSDSchema("A");
        schemaC.addBlockDefault(BlockDefault.substitution);
        schemaC.addBlockDefault(BlockDefault.restriction);

        LinkedHashSet<XSDSchema> minuendSchemata = new LinkedHashSet<XSDSchema>();
        minuendSchemata.add(schemaA);
        minuendSchemata.add(schemaB);
        minuendSchemata.add(schemaC);

        SchemaUnionGenerator instance = new SchemaUnionGenerator(minuendSchemata, null, null, null, null, null, null, null, null, null, null, null, null);

        LinkedHashSet<BlockDefault> expResult = new LinkedHashSet<XSDSchema.BlockDefault>();
        expResult.add(XSDSchema.BlockDefault.restriction);
        LinkedHashSet<BlockDefault> result = instance.generateNewBlockDefaultAttribute();

        assertEquals(expResult, result);

        HashSet<Block> block = new HashSet<Block>();
        block.add(Block.extension);
        block.add(Block.restriction);
        assertEquals(block, elementB.getBlockModifiers());
    }

    /**
     * Test of generateNewBlockDefaultAttribute method, of class
     * SchemaUnionGenerator for a schema which blocks substitution.
     */
    @Test
    public void testGenerateNewBlockDefaultAttributeSubstitution() {
        XSDSchema schemaA = new XSDSchema("A");
        schemaA.addBlockDefault(BlockDefault.extension);
        schemaA.addBlockDefault(BlockDefault.restriction);
        schemaA.addBlockDefault(BlockDefault.substitution);

        XSDSchema schemaB = new XSDSchema("A");
        schemaB.addBlockDefault(BlockDefault.restriction);
        schemaB.addBlockDefault(BlockDefault.substitution);

        Element elementB = new Element("{A}elementB");
        schemaB.addElement(new SymbolTableRef<Element>("{A}elementB", elementB));

        XSDSchema schemaC = new XSDSchema("A");
        schemaC.addBlockDefault(BlockDefault.substitution);
        schemaC.addBlockDefault(BlockDefault.extension);

        LinkedHashSet<XSDSchema> minuendSchemata = new LinkedHashSet<XSDSchema>();
        minuendSchemata.add(schemaA);
        minuendSchemata.add(schemaB);
        minuendSchemata.add(schemaC);

        SchemaUnionGenerator instance = new SchemaUnionGenerator(minuendSchemata, null, null, null, null, null, null, null, null, null, null, null, null);

        LinkedHashSet<BlockDefault> expResult = new LinkedHashSet<XSDSchema.BlockDefault>();
        expResult.add(XSDSchema.BlockDefault.substitution);
        LinkedHashSet<BlockDefault> result = instance.generateNewBlockDefaultAttribute();

        assertEquals(expResult, result);

        HashSet<Block> block = new HashSet<Block>();
        block.add(Block.substitution);
        block.add(Block.restriction);
        assertEquals(block, elementB.getBlockModifiers());
    }

    /**
     * Test of generateNewFinalDefaultAttribute method, of class
     * SchemaUnionGenerator for a schema which finalizes extension.
     */
    @Test
    public void testGenerateNewFinalDefaultAttributeExtension() {
        XSDSchema schemaA = new XSDSchema("A");
        schemaA.addFinalDefault(FinalDefault.extension);
        schemaA.addFinalDefault(FinalDefault.restriction);
        schemaA.addFinalDefault(FinalDefault.list);
        schemaA.addFinalDefault(FinalDefault.union);

        XSDSchema schemaB = new XSDSchema("A");
        schemaB.addFinalDefault(FinalDefault.extension);
        schemaB.addFinalDefault(FinalDefault.list);

        Element elementB = new Element("{A}elementB");
        schemaB.addElement(new SymbolTableRef<Element>("{A}elementB", elementB));

        XSDSchema schemaC = new XSDSchema("A");
        schemaC.addFinalDefault(FinalDefault.extension);
        schemaC.addFinalDefault(FinalDefault.union);

        LinkedHashSet<XSDSchema> minuendSchemata = new LinkedHashSet<XSDSchema>();
        minuendSchemata.add(schemaA);
        minuendSchemata.add(schemaB);
        minuendSchemata.add(schemaC);

        SchemaUnionGenerator instance = new SchemaUnionGenerator(minuendSchemata, null, null, null, null, null, null, null, null, null, null, null, null);

        LinkedHashSet<FinalDefault> expResult = new LinkedHashSet<XSDSchema.FinalDefault>();
        expResult.add(XSDSchema.FinalDefault.extension);
        LinkedHashSet<FinalDefault> result = instance.generateNewFinalDefaultAttribute();

        assertEquals(expResult, result);

        HashSet<Final> finalValue = new HashSet<Final>();
        finalValue.add(Final.extension);
        assertEquals(finalValue, elementB.getFinalModifiers());
    }

    /**
     * Test of generateNewFinalDefaultAttribute method, of class
     * SchemaUnionGenerator for a schema which finalizes restriction.
     */
    @Test
    public void testGenerateNewFinalDefaultAttributeRestriction() {
        XSDSchema schemaA = new XSDSchema("A");
        schemaA.addFinalDefault(FinalDefault.extension);
        schemaA.addFinalDefault(FinalDefault.restriction);
        schemaA.addFinalDefault(FinalDefault.list);
        schemaA.addFinalDefault(FinalDefault.union);

        XSDSchema schemaB = new XSDSchema("A");
        schemaB.addFinalDefault(FinalDefault.restriction);
        schemaB.addFinalDefault(FinalDefault.list);

        Element elementB = new Element("{A}elementB");
        schemaB.addElement(new SymbolTableRef<Element>("{A}elementB", elementB));

        XSDSchema schemaC = new XSDSchema("A");
        schemaC.addFinalDefault(FinalDefault.restriction);
        schemaC.addFinalDefault(FinalDefault.union);

        LinkedHashSet<XSDSchema> minuendSchemata = new LinkedHashSet<XSDSchema>();
        minuendSchemata.add(schemaA);
        minuendSchemata.add(schemaB);
        minuendSchemata.add(schemaC);

        SchemaUnionGenerator instance = new SchemaUnionGenerator(minuendSchemata, null, null, null, null, null, null, null, null, null, null, null, null);

        LinkedHashSet<FinalDefault> expResult = new LinkedHashSet<XSDSchema.FinalDefault>();
        expResult.add(XSDSchema.FinalDefault.restriction);
        LinkedHashSet<FinalDefault> result = instance.generateNewFinalDefaultAttribute();

        assertEquals(expResult, result);

        HashSet<Final> finalValue = new HashSet<Final>();
        finalValue.add(Final.restriction);
        assertEquals(finalValue, elementB.getFinalModifiers());
    }

    /**
     * Test of generateNewFinalDefaultAttribute method, of class
     * SchemaUnionGenerator for a schema which finalizes union.
     */
    @Test
    public void testGenerateNewFinalDefaultAttributeUnion() {
        XSDSchema schemaA = new XSDSchema("A");
        schemaA.addFinalDefault(FinalDefault.extension);
        schemaA.addFinalDefault(FinalDefault.restriction);
        schemaA.addFinalDefault(FinalDefault.list);
        schemaA.addFinalDefault(FinalDefault.union);

        XSDSchema schemaB = new XSDSchema("A");
        schemaB.addFinalDefault(FinalDefault.list);
        schemaB.addFinalDefault(FinalDefault.union);

        Element elementB = new Element("{A}elementB");
        schemaB.addElement(new SymbolTableRef<Element>("{A}elementB", elementB));

        XSDSchema schemaC = new XSDSchema("A");
        schemaC.addFinalDefault(FinalDefault.restriction);
        schemaC.addFinalDefault(FinalDefault.union);

        LinkedHashSet<XSDSchema> minuendSchemata = new LinkedHashSet<XSDSchema>();
        minuendSchemata.add(schemaA);
        minuendSchemata.add(schemaB);
        minuendSchemata.add(schemaC);

        SchemaUnionGenerator instance = new SchemaUnionGenerator(minuendSchemata, null, null, null, null, null, null, null, null, null, null, null, null);

        LinkedHashSet<FinalDefault> expResult = new LinkedHashSet<XSDSchema.FinalDefault>();
        expResult.add(XSDSchema.FinalDefault.union);
        LinkedHashSet<FinalDefault> result = instance.generateNewFinalDefaultAttribute();

        assertEquals(expResult, result);
        assertEquals(null, elementB.getFinalModifiers());
    }

    /**
     * Test of generateNewFinalDefaultAttribute method, of class
     * SchemaUnionGenerator for a schema which finalizes list.
     */
    @Test
    public void testGenerateNewFinalDefaultAttributeList() {
        XSDSchema schemaA = new XSDSchema("A");
        schemaA.addFinalDefault(FinalDefault.extension);
        schemaA.addFinalDefault(FinalDefault.restriction);
        schemaA.addFinalDefault(FinalDefault.list);
        schemaA.addFinalDefault(FinalDefault.union);

        XSDSchema schemaB = new XSDSchema("A");
        schemaB.addFinalDefault(FinalDefault.list);
        schemaB.addFinalDefault(FinalDefault.union);

        Element elementB = new Element("{A}elementB");
        schemaB.addElement(new SymbolTableRef<Element>("{A}elementB", elementB));

        XSDSchema schemaC = new XSDSchema("A");
        schemaC.addFinalDefault(FinalDefault.restriction);
        schemaC.addFinalDefault(FinalDefault.list);

        LinkedHashSet<XSDSchema> minuendSchemata = new LinkedHashSet<XSDSchema>();
        minuendSchemata.add(schemaA);
        minuendSchemata.add(schemaB);
        minuendSchemata.add(schemaC);

        SchemaUnionGenerator instance = new SchemaUnionGenerator(minuendSchemata, null, null, null, null, null, null, null, null, null, null, null, null);

        LinkedHashSet<FinalDefault> expResult = new LinkedHashSet<XSDSchema.FinalDefault>();
        expResult.add(XSDSchema.FinalDefault.list);
        LinkedHashSet<FinalDefault> result = instance.generateNewFinalDefaultAttribute();

        assertEquals(expResult, result);
        assertEquals(null, elementB.getFinalModifiers());
    }

    /**
     * Test of generateNewElementFormDefaultAttribute method, of class
     * SchemaUnionGenerator for a schema with default value qualified.
     */
    @Test
    public void testGenerateNewElementFormDefaultAttributeQualified() {
        XSDSchema schemaA = new XSDSchema("A");
        schemaA.setElementFormDefault(Qualification.qualified);

        Element elementA = new Element("{A}elementA");
        schemaA.addElement(new SymbolTableRef<Element>("{A}elementA", elementA));
        ChoicePattern choicePatternA = new ChoicePattern();
        choicePatternA.addParticle(elementA);
        Group groupA = new Group("{A}Group", choicePatternA);
        schemaA.getGroupSymbolTable().updateOrCreateReference("{A}Group", groupA);
        schemaA.addGroup(schemaA.getGroupSymbolTable().getReference("{A}Group"));

        XSDSchema schemaB = new XSDSchema("A");
        schemaB.setElementFormDefault(Qualification.unqualified);

        Element elementB = new Element("{A}elementB");
        schemaB.addElement(new SymbolTableRef<Element>("{A}elementB", elementB));
        ChoicePattern choicePatternB = new ChoicePattern();
        choicePatternB.addParticle(elementB);
        Group groupB = new Group("{A}Group", choicePatternB);
        schemaB.getGroupSymbolTable().updateOrCreateReference("{A}Group", groupB);
        schemaB.addGroup(schemaB.getGroupSymbolTable().getReference("{A}Group"));

        LinkedHashSet<XSDSchema> minuendSchemata = new LinkedHashSet<XSDSchema>();
        minuendSchemata.add(schemaA);
        minuendSchemata.add(schemaB);

        SchemaUnionGenerator instance = new SchemaUnionGenerator(minuendSchemata, null, null, null, null, null, null, null, null, null, null, null, null);

        Qualification expResult = Qualification.unqualified;
        Qualification result = instance.generateNewElementFormDefaultAttribute();

        assertEquals(expResult, result);
        assertEquals(Qualification.qualified, elementA.getForm());
        assertEquals(Qualification.unqualified, elementB.getForm());
    }

    /**
     * Test of generateNewElementFormDefaultAttribute method, of class
     * SchemaUnionGenerator for a schema with default value unqualified.
     */
    @Test
    public void testGenerateNewElementFormDefaultAttributeUnqualified() {
        XSDSchema schemaA = new XSDSchema("A");
        schemaA.setElementFormDefault(Qualification.unqualified);

        Element elementA = new Element("{A}elementA");
        schemaA.addElement(new SymbolTableRef<Element>("{A}elementA", elementA));
        ChoicePattern choicePatternA = new ChoicePattern();
        choicePatternA.addParticle(elementA);
        Group groupA = new Group("{A}Group", choicePatternA);
        schemaA.getGroupSymbolTable().updateOrCreateReference("{A}Group", groupA);
        schemaA.addGroup(schemaA.getGroupSymbolTable().getReference("{A}Group"));

        XSDSchema schemaB = new XSDSchema("A");
        schemaB.setElementFormDefault(Qualification.unqualified);

        Element elementB = new Element("{A}elementB");
        schemaB.addElement(new SymbolTableRef<Element>("{A}elementB", elementB));
        ChoicePattern choicePatternB = new ChoicePattern();
        choicePatternB.addParticle(elementB);
        Group groupB = new Group("{A}Group", choicePatternB);
        schemaB.getGroupSymbolTable().updateOrCreateReference("{A}Group", groupB);
        schemaB.addGroup(schemaB.getGroupSymbolTable().getReference("{A}Group"));

        LinkedHashSet<XSDSchema> minuendSchemata = new LinkedHashSet<XSDSchema>();
        minuendSchemata.add(schemaA);
        minuendSchemata.add(schemaB);

        SchemaUnionGenerator instance = new SchemaUnionGenerator(minuendSchemata, null, null, null, null, null, null, null, null, null, null, null, null);

        Qualification expResult = Qualification.unqualified;
        Qualification result = instance.generateNewElementFormDefaultAttribute();

        assertEquals(expResult, result);
        assertEquals(null, elementA.getForm());
        assertEquals(null, elementB.getForm());
    }

    /**
     * Test of testGenerateNewAttributeFormDefaultAttribute method, of class
     * SchemaUnionGenerator for a schema with default value qualified.
     */
    @Test
    public void testGenerateNewAttributeFormDefaultAttributeQualified() {
        XSDSchema schemaA = new XSDSchema("A");
        schemaA.setAttributeFormDefault(Qualification.qualified);

        Attribute attributeA = new Attribute("{A}elementA");
        attributeA.setForm(null);
        AttributeGroup attributeGroupA = new AttributeGroup("{A}attributeGroupA");
        attributeGroupA.addAttributeParticle(attributeA);
        schemaA.getAttributeGroupSymbolTable().updateOrCreateReference("{A}attributeGroupA", attributeGroupA);
        schemaA.addAttributeGroup(schemaA.getAttributeGroupSymbolTable().getReference("{A}attributeGroupA"));

        XSDSchema schemaB = new XSDSchema("A");
        schemaB.setElementFormDefault(Qualification.unqualified);

        Attribute attributeB = new Attribute("{A}elementB");
        attributeB.setForm(null);
        AttributeGroup attributeGroupB = new AttributeGroup("{A}attributeGroupB");
        attributeGroupB.addAttributeParticle(attributeB);
        schemaB.getAttributeGroupSymbolTable().updateOrCreateReference("{A}attributeGroupB", attributeGroupB);
        schemaB.addAttributeGroup(schemaB.getAttributeGroupSymbolTable().getReference("{A}attributeGroupB"));

        LinkedHashSet<XSDSchema> minuendSchemata = new LinkedHashSet<XSDSchema>();
        minuendSchemata.add(schemaA);
        minuendSchemata.add(schemaB);

        SchemaUnionGenerator instance = new SchemaUnionGenerator(minuendSchemata, null, null, null, null, null, null, null, null, null, null, null, null);

        Qualification expResult = Qualification.unqualified;
        Qualification result = instance.generateNewAttributeFormDefaultAttribute();

        assertEquals(expResult, result);
        assertEquals(Qualification.qualified, attributeA.getForm());
        assertEquals(Qualification.unqualified, attributeB.getForm());
    }

    /**
     * Test of testGenerateNewAttributeFormDefaultAttribute method, of class
     * SchemaUnionGenerator for a schema with default value unqualified.
     */
    @Test
    public void testGenerateNewAttributeFormDefaultAttributeUnqualified() {
        XSDSchema schemaA = new XSDSchema("A");
        schemaA.setElementFormDefault(Qualification.unqualified);

        Attribute attributeA = new Attribute("{A}elementA");
        attributeA.setForm(null);
        AttributeGroup attributeGroupA = new AttributeGroup("{A}attributeGroupA");
        attributeGroupA.addAttributeParticle(attributeA);
        schemaA.getAttributeGroupSymbolTable().updateOrCreateReference("{A}attributeGroupA", attributeGroupA);
        schemaA.addAttributeGroup(schemaA.getAttributeGroupSymbolTable().getReference("{A}attributeGroupA"));

        XSDSchema schemaB = new XSDSchema("A");
        schemaB.setElementFormDefault(Qualification.unqualified);

        Attribute attributeB = new Attribute("{A}elementB");
        attributeB.setForm(null);
        AttributeGroup attributeGroupB = new AttributeGroup("{A}attributeGroupB");
        attributeGroupB.addAttributeParticle(attributeB);
        schemaB.getAttributeGroupSymbolTable().updateOrCreateReference("{A}attributeGroupB", attributeGroupB);
        schemaB.addAttributeGroup(schemaB.getAttributeGroupSymbolTable().getReference("{A}attributeGroupB"));

        LinkedHashSet<XSDSchema> minuendSchemata = new LinkedHashSet<XSDSchema>();
        minuendSchemata.add(schemaA);
        minuendSchemata.add(schemaB);

        SchemaUnionGenerator instance = new SchemaUnionGenerator(minuendSchemata, null, null, null, null, null, null, null, null, null, null, null, null);

        Qualification expResult = Qualification.unqualified;
        Qualification result = instance.generateNewAttributeFormDefaultAttribute();

        assertEquals(expResult, result);
        assertEquals(null, attributeA.getForm());
        assertEquals(null, attributeB.getForm());
    }

    /**
     * Test of isAnonymous method, of class SchemaUnionGenerator for an
     * anonymous type.
     */
    @Test
    public void testIsAnonymousOneAnonymousType() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.setIsAnonymous(true);
        types.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        SchemaUnionGenerator instance = new SchemaUnionGenerator(null, null, null, null, null, null, null, null, null, null, null, null, null);

        boolean expResult = true;
        boolean result = instance.isAnonymous(types, outputSchema);

        assertEquals(expResult, result);
    }

    /**
     * Test of isAnonymous method, of class SchemaUnionGenerator for a list of
     * anonymous types.
     */
    @Test
    public void testIsAnonymousListAnonymousTypes() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.setIsAnonymous(true);
        types.add(simpleTypeA);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        simpleTypeB.setIsAnonymous(true);
        types.add(simpleTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        SchemaUnionGenerator instance = new SchemaUnionGenerator(null, null, null, null, null, null, null, null, null, null, null, null, null);

        boolean expResult = false;
        boolean result = instance.isAnonymous(types, outputSchema);

        assertEquals(expResult, result);
    }

    /**
     * Test of isAnonymous method, of class SchemaUnionGenerator for an
     * anonymous type with strict any pattern.
     */
    @Test
    public void testIsAnonymousOneAnonymousTypeWithStrictAny() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ChoicePattern choicePatternA = new ChoicePattern();
        choicePatternA.addParticle(new AnyPattern(ProcessContentsInstruction.Strict, "##any"));
        ComplexContentType complexContentType = new ComplexContentType(choicePatternA);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", complexContentType);
        complexTypeA.setIsAnonymous(true);
        types.add(complexTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        SchemaUnionGenerator instance = new SchemaUnionGenerator(null, null, null, null, null, null, null, null, null, null, null, null, null);

        boolean expResult = false;
        boolean result = instance.isAnonymous(types, outputSchema);

        assertEquals(expResult, result);
    }

    /**
     * Test of isAnonymous method, of class SchemaUnionGenerator for an
     * anonymous type from another schema.
     */
    @Test
    public void testIsAnonymousOneAnonymousTypeOtherSchema() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{}simpleTypeA", null);
        simpleTypeA.setIsAnonymous(true);
        types.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        SchemaUnionGenerator instance = new SchemaUnionGenerator(null, null, null, null, null, null, null, null, null, null, null, null, null);

        boolean expResult = false;
        boolean result = instance.isAnonymous(types, outputSchema);

        assertEquals(expResult, result);
    }
}