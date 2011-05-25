package de.tudortmund.cs.bonxai.xsd.setOperations.intersection;

import de.tudortmund.cs.bonxai.common.AnyAttribute;
import de.tudortmund.cs.bonxai.common.AnyPattern;
import de.tudortmund.cs.bonxai.common.ChoicePattern;
import de.tudortmund.cs.bonxai.common.SequencePattern;
import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.xsd.parser.XSDParser;
import java.util.*;
import org.junit.Test;

/**
 * Test case of the <tt>SchemaIntersectionGenerator</tt> class, checks that every
 * method of this class performs properly.
 * @author Dominik Wolff
 */
public class SchemaIntersectionGeneratorTest extends junit.framework.TestCase {

    public SchemaIntersectionGeneratorTest() {
    }

    /**
     * Test of generateIntersection method, of class SchemaIntersectionGenerator.
     */
    @Test
    public void testGenerateIntersection() throws Exception {
        XSDParser xmlSchemaParser = new XSDParser(false, false);
        XSDSchema schemaA = xmlSchemaParser.parse(this.getClass().getResource("/tests/de/tudortmund/cs/bonxai/xsd/setOperations/intersection/xsds/testGenerateIntersectionA.xsd").getFile());
        XSDSchema schemaB = xmlSchemaParser.parse(this.getClass().getResource("/tests/de/tudortmund/cs/bonxai/xsd/setOperations/intersection/xsds/testGenerateIntersectionB.xsd").getFile());
        XSDSchema schemaC = xmlSchemaParser.parse(this.getClass().getResource("/tests/de/tudortmund/cs/bonxai/xsd/setOperations/intersection/xsds/testGenerateIntersectionC.xsd").getFile());
        LinkedHashSet<XSDSchema> minuendSchemata = new LinkedHashSet<XSDSchema>();
        minuendSchemata.add(schemaA);
        minuendSchemata.add(schemaB);
        minuendSchemata.add(schemaC);
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(schemaA.getTypeSymbolTable().getReference("{A}complexTypeA").getReference(), schemaA);
        typeOldSchemaMap.put(schemaA.getTypeSymbolTable().getReference("{A}complexTypeB").getReference(), schemaA);
        typeOldSchemaMap.put(schemaA.getTypeSymbolTable().getReference("{A}complexTypeC").getReference(), schemaA);
        typeOldSchemaMap.put(schemaA.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string").getReference(), schemaA);
        typeOldSchemaMap.put(schemaB.getTypeSymbolTable().getReference("{A}complexTypeA").getReference(), schemaB);
        typeOldSchemaMap.put(schemaB.getTypeSymbolTable().getReference("{A}complexTypeB").getReference(), schemaB);
        typeOldSchemaMap.put(schemaB.getTypeSymbolTable().getReference("{A}complexTypeC").getReference(), schemaB);
        typeOldSchemaMap.put(schemaB.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string").getReference(), schemaB);
        typeOldSchemaMap.put(schemaB.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}integer").getReference(), schemaB);
        typeOldSchemaMap.put(schemaC.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}anyType").getReference(), schemaC);
        typeOldSchemaMap.put(schemaC.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string").getReference(), schemaC);
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put((AnyPattern) ((ChoicePattern) ((ComplexContentType) ((ComplexType) schemaA.getTypeSymbolTable().getReference("{A}complexTypeC").getReference()).getContent()).getParticle()).getParticles().getFirst(), schemaA);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(schemaA.getElementSymbolTable().getReference("{A}elementA").getReference(), schemaA);
        elementOldSchemaMap.put(schemaA.getElementSymbolTable().getReference("{A}elementC").getReference(), schemaA);
        elementOldSchemaMap.put((Element) ((ChoicePattern) ((ComplexContentType) ((ComplexType) schemaA.getTypeSymbolTable().getReference("{A}complexTypeA").getReference()).getContent()).getParticle()).getParticles().getFirst(), schemaA);
        elementOldSchemaMap.put((Element) ((SequencePattern) ((ComplexContentType) ((ComplexType) schemaA.getTypeSymbolTable().getReference("{A}complexTypeB").getReference()).getContent()).getParticle()).getParticles().getFirst(), schemaA);
        elementOldSchemaMap.put((Element) ((SequencePattern) ((ComplexContentType) ((ComplexType) schemaA.getTypeSymbolTable().getReference("{A}complexTypeB").getReference()).getContent()).getParticle()).getParticles().getLast(), schemaA);
        elementOldSchemaMap.put(schemaB.getElementSymbolTable().getReference("{A}elementA").getReference(), schemaB);
        elementOldSchemaMap.put((Element) ((ChoicePattern) ((ComplexContentType) ((ComplexType) schemaB.getTypeSymbolTable().getReference("{A}complexTypeA").getReference()).getContent()).getParticle()).getParticles().getFirst(), schemaB);
        elementOldSchemaMap.put((Element) ((ChoicePattern) ((ComplexContentType) ((ComplexType) schemaB.getTypeSymbolTable().getReference("{A}complexTypeA").getReference()).getContent()).getParticle()).getParticles().getLast(), schemaB);
        elementOldSchemaMap.put((Element) ((SequencePattern) ((ComplexContentType) ((ComplexType) schemaB.getTypeSymbolTable().getReference("{A}complexTypeB").getReference()).getContent()).getParticle()).getParticles().getFirst(), schemaB);
        elementOldSchemaMap.put((Element) ((SequencePattern) ((ComplexContentType) ((ComplexType) schemaB.getTypeSymbolTable().getReference("{A}complexTypeB").getReference()).getContent()).getParticle()).getParticles().getLast(), schemaB);
        elementOldSchemaMap.put((Element) ((ChoicePattern) ((ComplexContentType) ((ComplexType) schemaB.getTypeSymbolTable().getReference("{A}complexTypeC").getReference()).getContent()).getParticle()).getParticles().getFirst(), schemaB);
        elementOldSchemaMap.put(schemaC.getElementSymbolTable().getReference("{A}elementA").getReference(), schemaC);
        LinkedHashSet<XSDSchema> otherSchemata = new LinkedHashSet<XSDSchema>();
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        String workingDirectory = "c:/";
        SchemaIntersectionGenerator instance = new SchemaIntersectionGenerator(minuendSchemata, namespaceOutputSchemaMap, anyAttributeOldSchemaMap, typeOldSchemaMap, anyPatternOldSchemaMap, elementOldSchemaMap, otherSchemata, namespaceAbbreviationMap, workingDirectory);

        XSDSchema outputSchema = new XSDSchema("A");

        instance.generateIntersection(outputSchema);

        assertEquals("A", outputSchema.getTargetNamespace());
        assertEquals(XSDSchema.Qualification.unqualified, outputSchema.getAttributeFormDefault());
        assertTrue(outputSchema.getAttributeGroupSymbolTable().getReferences().isEmpty());
        assertTrue(outputSchema.getAttributeGroups().isEmpty());
        assertEquals(2, outputSchema.getAttributeSymbolTable().getReferences().size());
        assertEquals("{A}attributeB", outputSchema.getAttributeSymbolTable().getReferences().getFirst().getReference().getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", outputSchema.getAttributeSymbolTable().getReferences().getFirst().getReference().getSimpleType().getName());
        assertEquals("{A}attributeA", outputSchema.getAttributeSymbolTable().getReferences().getLast().getReference().getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", outputSchema.getAttributeSymbolTable().getReferences().getLast().getReference().getSimpleType().getName());
        LinkedList<Attribute> attributes = new LinkedList<Attribute>();
        attributes.add(outputSchema.getAttributeSymbolTable().getReferences().getLast().getReference());
        attributes.add(outputSchema.getAttributeSymbolTable().getReferences().getFirst().getReference());
        assertEquals(attributes, outputSchema.getAttributes());
        HashSet<XSDSchema.BlockDefault> blockDefaults = new HashSet<XSDSchema.BlockDefault>();
        blockDefaults.add(XSDSchema.BlockDefault.extension);
        blockDefaults.add(XSDSchema.BlockDefault.substitution);
        blockDefaults.add(XSDSchema.BlockDefault.restriction);
        assertEquals(blockDefaults, outputSchema.getBlockDefaults());
        assertEquals(new HashSet(), outputSchema.getConstraintNames());
        assertEquals(XSDSchema.Qualification.unqualified, outputSchema.getElementFormDefault());
        assertEquals(1, outputSchema.getElementSymbolTable().getReferences().size());
        assertEquals("{A}elementA", outputSchema.getElementSymbolTable().getReferences().get(0).getReference().getName());
        assertEquals("{A}intersection-type.complexTypeA.complexTypeA.anyType", outputSchema.getElementSymbolTable().getReferences().get(0).getReference().getType().getName());
        LinkedList<Element> elements = new LinkedList<Element>();
        elements.add(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference());
        assertEquals(elements, outputSchema.getElements());
        HashSet<XSDSchema.FinalDefault> finalDefaults = new HashSet<XSDSchema.FinalDefault>();
        finalDefaults.add(XSDSchema.FinalDefault.extension);
        finalDefaults.add(XSDSchema.FinalDefault.restriction);
        assertEquals(finalDefaults, outputSchema.getFinalDefaults());
        assertEquals(new LinkedList(), outputSchema.getForeignSchemas());
        assertTrue(outputSchema.getGroupSymbolTable().getReferences().isEmpty());
        assertTrue(outputSchema.getGroups().isEmpty());
        assertEquals(null, outputSchema.getId());
        assertTrue(outputSchema.getKeyAndUniqueSymbolTable().getReferences().isEmpty());
        assertTrue(outputSchema.getNamespaceList().getIdentifiedNamespaces().isEmpty());
        assertEquals(null, outputSchema.getSchemaLocation());
        assertEquals(new HashMap(), outputSchema.getSubstitutionElements());
        assertEquals(6, outputSchema.getTypeSymbolTable().getReferences().size());
        assertTrue(outputSchema.getTypeSymbolTable().getReferences().get(0).getReference() instanceof ComplexType);

        ComplexType complexType = (ComplexType) outputSchema.getTypeSymbolTable().getReferences().get(0).getReference();
        assertEquals("{A}complexTypeB", complexType.getName());
        assertEquals(false, (boolean) complexType.getMixed());
        assertTrue(complexType.getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) complexType.getContent()).getParticle() instanceof SequencePattern);
        assertEquals(2, ((SequencePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().size());
        assertTrue(((SequencePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst() instanceof Element);
        assertEquals("{A}elementD", ((Element) ((SequencePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Element) ((SequencePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getType().getName());
        assertTrue(((SequencePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getLast() instanceof Element);
        assertEquals("{A}elementE", ((Element) ((SequencePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getLast()).getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Element) ((SequencePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getLast()).getType().getName());
        assertTrue(outputSchema.getTypeSymbolTable().getReferences().get(1).getReference() instanceof ComplexType);

        complexType = (ComplexType) outputSchema.getTypeSymbolTable().getReferences().get(1).getReference();
        assertEquals("{A}complexTypeA", complexType.getName());
        assertEquals(false, (boolean) complexType.getMixed());
        assertTrue(complexType.getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) complexType.getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(2, ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().size());
        assertTrue(((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst() instanceof Element);
        assertEquals("{A}elementB", ((Element) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getName());
        assertEquals("{A}complexTypeB", ((Element) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getType().getName());
        assertTrue(((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getLast() instanceof Element);
        assertEquals("{A}elementC", ((Element) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getLast()).getName());
        assertEquals("{A}complexTypeC", ((Element) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getLast()).getType().getName());
        assertTrue(outputSchema.getTypeSymbolTable().getReferences().get(2).getReference() instanceof ComplexType);

        complexType = (ComplexType) outputSchema.getTypeSymbolTable().getReferences().get(2).getReference();
        assertEquals("{A}intersection-type.complexTypeC.complexTypeC", complexType.getName());
        assertEquals(false, (boolean) complexType.getMixed());
        assertTrue(complexType.getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) complexType.getContent()).getParticle() instanceof SequencePattern);
        assertEquals(1, ((SequencePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().size());
        assertTrue(((SequencePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst() instanceof Element);
        assertEquals("{A}elementC", ((Element) ((SequencePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getName());
        assertEquals("{A}intersection-type.complexTypeC.complexTypeC", ((Element) ((SequencePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getType().getName());
        assertTrue(outputSchema.getTypeSymbolTable().getReferences().get(3).getReference() instanceof ComplexType);

        complexType = (ComplexType) outputSchema.getTypeSymbolTable().getReferences().get(3).getReference();
        assertEquals("{A}complexTypeC", complexType.getName());
        assertEquals(false, (boolean) complexType.getMixed());
        assertTrue(complexType.getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) complexType.getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(2, ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().size());
        assertTrue(((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst() instanceof Element);
        assertEquals("{A}elementA", ((Element) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getName());
        assertEquals("{A}complexTypeA", ((Element) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getType().getName());
        assertTrue(((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getLast() instanceof Element);
        assertEquals("{A}elementC", ((Element) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getLast()).getName());
        assertEquals("{A}complexTypeC", ((Element) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getLast()).getType().getName());
        assertTrue(outputSchema.getTypeSymbolTable().getReferences().get(4).getReference() instanceof SimpleType);

        SimpleType simpleType = (SimpleType) outputSchema.getTypeSymbolTable().getReferences().get(4).getReference();
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", simpleType.getName());
        assertEquals(null, simpleType.getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().getReferences().get(5).getReference() instanceof ComplexType);

        complexType = (ComplexType) outputSchema.getTypeSymbolTable().getReferences().get(5).getReference();
        assertEquals("{A}intersection-type.complexTypeA.complexTypeA.anyType", complexType.getName());
        assertEquals(false, (boolean) complexType.getMixed());
        assertTrue(complexType.getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) complexType.getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(1, ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().size());
        assertTrue(((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst() instanceof Element);
        assertEquals("{A}elementC", ((Element) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getName());
        assertEquals("{A}intersection-type.complexTypeC.complexTypeC", ((Element) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getType().getName());
        LinkedList<Type> types = new LinkedList<Type>();
        types.add(outputSchema.getTypeSymbolTable().getReference("{A}intersection-type.complexTypeA.complexTypeA.anyType").getReference());
        types.add(outputSchema.getTypeSymbolTable().getReference("{A}complexTypeC").getReference());
        types.add(outputSchema.getTypeSymbolTable().getReference("{A}complexTypeA").getReference());
        types.add(outputSchema.getTypeSymbolTable().getReference("{A}complexTypeB").getReference());
        types.add(outputSchema.getTypeSymbolTable().getReference("{A}intersection-type.complexTypeC.complexTypeC").getReference());
        assertEquals(types, outputSchema.getTypes());
    }
}