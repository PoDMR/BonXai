package de.tudortmund.cs.bonxai.xsd.setOperations.difference;

import de.tudortmund.cs.bonxai.common.AnyAttribute;
import de.tudortmund.cs.bonxai.common.AnyPattern;
import de.tudortmund.cs.bonxai.common.ChoicePattern;
import de.tudortmund.cs.bonxai.common.SequencePattern;
import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.xsd.parser.XSDParser;
import java.util.*;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>SchemaDifferenceGenerator</tt> class, checks that every
 * method of this class performs properly.
 * @author Dominik Wolff
 */
public class SchemaDifferenceGeneratorTest extends junit.framework.TestCase {

    public SchemaDifferenceGeneratorTest() {
    }

    /**
     * Test of generateDifference method, of class SchemaDifferenceGenerator.
     */
    @Test
    public void testGenerateDifference() throws Exception {
        XSDParser xmlSchemaParser = new XSDParser(false, false);
        XSDSchema schemaA = xmlSchemaParser.parse(this.getClass().getResource("/tests/de/tudortmund/cs/bonxai/xsd/setOperations/difference/xsds/testGenerateDifferenceA.xsd").getFile());
        XSDSchema schemaB = xmlSchemaParser.parse(this.getClass().getResource("/tests/de/tudortmund/cs/bonxai/xsd/setOperations/difference/xsds/testGenerateDifferenceB.xsd").getFile());
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(schemaA.getTypeSymbolTable().getReference("{A}complexTypeA").getReference(), schemaA);
        typeOldSchemaMap.put(schemaA.getTypeSymbolTable().getReference("{A}complexTypeB").getReference(), schemaA);
        typeOldSchemaMap.put(schemaA.getTypeSymbolTable().getReference("{A}complexTypeC").getReference(), schemaA);
        typeOldSchemaMap.put(schemaA.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string").getReference(), schemaA);
        typeOldSchemaMap.put(schemaB.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}integer").getReference(), schemaA);
        typeOldSchemaMap.put(schemaB.getTypeSymbolTable().getReference("{A}complexTypeA").getReference(), schemaB);
        typeOldSchemaMap.put(schemaB.getTypeSymbolTable().getReference("{A}complexTypeB").getReference(), schemaB);
        typeOldSchemaMap.put(schemaB.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string").getReference(), schemaB);
        typeOldSchemaMap.put(schemaB.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}integer").getReference(), schemaB);
        typeOldSchemaMap.put(schemaB.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}anyType").getReference(), schemaB);
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put((AnyPattern) ((ChoicePattern) ((ComplexContentType) ((ComplexType) schemaA.getTypeSymbolTable().getReference("{A}complexTypeC").getReference()).getContent()).getParticle()).getParticles().getFirst(), schemaA);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(schemaA.getElementSymbolTable().getReference("{A}elementA").getReference(), schemaA);
        elementOldSchemaMap.put(schemaA.getElementSymbolTable().getReference("{A}elementC").getReference(), schemaA);
        elementOldSchemaMap.put((Element) ((ChoicePattern) ((ComplexContentType) ((ComplexType) schemaA.getTypeSymbolTable().getReference("{A}complexTypeA").getReference()).getContent()).getParticle()).getParticles().getFirst(), schemaA);
        elementOldSchemaMap.put((Element) ((SequencePattern) ((ComplexContentType) ((ComplexType) schemaA.getTypeSymbolTable().getReference("{A}complexTypeB").getReference()).getContent()).getParticle()).getParticles().getFirst(), schemaA);
        elementOldSchemaMap.put((Element) ((SequencePattern) ((ComplexContentType) ((ComplexType) schemaA.getTypeSymbolTable().getReference("{A}complexTypeB").getReference()).getContent()).getParticle()).getParticles().getLast(), schemaA);
        elementOldSchemaMap.put(schemaB.getElementSymbolTable().getReference("{A}elementA").getReference(), schemaB);
        elementOldSchemaMap.put(schemaB.getElementSymbolTable().getReference("{A}elementC").getReference(), schemaB);
        elementOldSchemaMap.put((Element) ((ChoicePattern) ((ComplexContentType) ((ComplexType) schemaB.getTypeSymbolTable().getReference("{A}complexTypeA").getReference()).getContent()).getParticle()).getParticles().getFirst(), schemaB);
        elementOldSchemaMap.put((Element) ((SequencePattern) ((ComplexContentType) ((ComplexType) schemaB.getTypeSymbolTable().getReference("{A}complexTypeB").getReference()).getContent()).getParticle()).getParticles().getFirst(), schemaB);
        elementOldSchemaMap.put((Element) ((SequencePattern) ((ComplexContentType) ((ComplexType) schemaB.getTypeSymbolTable().getReference("{A}complexTypeB").getReference()).getContent()).getParticle()).getParticles().getLast(), schemaB);
        LinkedHashMap<String, LinkedHashSet<Element>> namespaceConflictingElementsMap = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashMap<Element, Group> elementGroupMap = new LinkedHashMap<Element, Group>();
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        LinkedHashSet<XSDSchema> otherSchemata = new LinkedHashSet<XSDSchema>();
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        String workingDirectory = "c:/";
        SchemaDifferenceGenerator instance = new SchemaDifferenceGenerator(schemaA, schemaB, namespaceOutputSchemaMap, namespaceOutputSchemaMap, namespaceConflictingElementsMap, elementGroupMap, anyAttributeOldSchemaMap, attributeOldSchemaMap, typeOldSchemaMap, anyPatternOldSchemaMap, elementOldSchemaMap, usedIDs, otherSchemata, namespaceAbbreviationMap, workingDirectory);

        XSDSchema outputSchema = new XSDSchema("A");

        instance.generateDifference(outputSchema);

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
        assertEquals(blockDefaults, outputSchema.getBlockDefaults());
        assertEquals(new HashSet(), outputSchema.getConstraintNames());
        assertEquals(XSDSchema.Qualification.unqualified, outputSchema.getElementFormDefault());
        LinkedList<Element> elements = new LinkedList<Element>();
        elements.add(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference());
        assertEquals(elements, outputSchema.getElements());
        HashSet<XSDSchema.FinalDefault> finalDefaults = new HashSet<XSDSchema.FinalDefault>();
        finalDefaults.add(XSDSchema.FinalDefault.extension);
        assertEquals(finalDefaults, outputSchema.getFinalDefaults());
        assertEquals(new LinkedList(), outputSchema.getForeignSchemas());
        assertTrue(outputSchema.getGroupSymbolTable().getReferences().isEmpty());
        assertTrue(outputSchema.getGroups().isEmpty());
        assertEquals(null, outputSchema.getId());
        assertTrue(outputSchema.getKeyAndUniqueSymbolTable().getReferences().isEmpty());
        assertTrue(outputSchema.getNamespaceList().getIdentifiedNamespaces().isEmpty());
        assertEquals(null, outputSchema.getSchemaLocation());
        assertEquals(new HashMap(), outputSchema.getSubstitutionElements());
        assertEquals(9, outputSchema.getTypeSymbolTable().getReferences().size());
        assertTrue(outputSchema.getTypeSymbolTable().getReferences().get(0).getReference() instanceof SimpleType);

        SimpleType simpleType = (SimpleType) outputSchema.getTypeSymbolTable().getReferences().get(0).getReference();
        assertEquals("{http://www.w3.org/2001/XMLSchema}integer", simpleType.getName());
        assertEquals(null, simpleType.getInheritance());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReferences().get(1).getReference());
        assertTrue(outputSchema.getTypeSymbolTable().getReferences().get(2).getReference() instanceof ComplexType);

        ComplexType complexType = (ComplexType) outputSchema.getTypeSymbolTable().getReferences().get(2).getReference();
        assertEquals("{A}difference-type.complexTypeB-null.1", complexType.getName());
        assertEquals(false, (boolean) complexType.getMixed());
        assertTrue(complexType.getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) complexType.getContent()).getParticle() instanceof SequencePattern);
        assertEquals(2, ((SequencePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().size());
        assertTrue(((SequencePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst() instanceof Element);
        assertEquals("{A}elementD", ((Element) ((SequencePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}integer", ((Element) ((SequencePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getType().getName());
        assertTrue(((SequencePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getLast() instanceof Element);
        assertEquals("{A}elementE", ((Element) ((SequencePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getLast()).getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Element) ((SequencePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getLast()).getType().getName());
        assertTrue(outputSchema.getTypeSymbolTable().getReferences().get(3).getReference() instanceof SimpleType);

        simpleType = (SimpleType) outputSchema.getTypeSymbolTable().getReferences().get(3).getReference();
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", simpleType.getName());
        assertEquals(null, simpleType.getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().getReferences().get(4).getReference() instanceof ComplexType);

        complexType = (ComplexType) outputSchema.getTypeSymbolTable().getReferences().get(4).getReference();
        assertEquals("{A}difference-type.complexTypeA-complexTypeA", complexType.getName());
        assertEquals(false, (boolean) complexType.getMixed());
        assertTrue(complexType.getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) complexType.getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(2, ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().size());
        assertTrue(((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst() instanceof Element);
        assertEquals("{A}elementC", ((Element) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getName());
        assertEquals("{A}difference-type.complexTypeC-null.1", ((Element) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getType().getName());
        assertTrue(((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getLast() instanceof Element);
        assertEquals("{A}elementB", ((Element) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getLast()).getName());
        assertEquals("{A}difference-type.complexTypeB-complexTypeB", ((Element) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getLast()).getType().getName());
        assertTrue(outputSchema.getTypeSymbolTable().getReferences().get(2).getReference() instanceof ComplexType);
        assertTrue(outputSchema.getTypeSymbolTable().getReferences().get(5).getReference() instanceof ComplexType);

        complexType = (ComplexType) outputSchema.getTypeSymbolTable().getReferences().get(5).getReference();
        assertEquals("{A}difference-type.complexTypeC-null.1", complexType.getName());
        assertEquals(false, (boolean) complexType.getMixed());
        assertTrue(complexType.getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) complexType.getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(1, ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().size());
        assertTrue(((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(0) instanceof ChoicePattern);
        assertEquals(2, ((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(0)).getParticles().size());
        assertTrue(((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(0)).getParticles().getFirst() instanceof Element);
        assertEquals("{A}elementA", ((Element) ((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(0)).getParticles().getFirst()).getName());
        assertEquals("{A}difference-type.complexTypeA-null.1", ((Element) ((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(0)).getParticles().getFirst()).getType().getName());
        assertTrue(((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(0)).getParticles().getLast() instanceof Element);
        assertEquals("{A}elementC", ((Element) ((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(0)).getParticles().getLast()).getName());
        assertEquals("{A}difference-type.complexTypeC-null.1", ((Element) ((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(0)).getParticles().getLast()).getType().getName());
        assertTrue(outputSchema.getTypeSymbolTable().getReferences().get(6).getReference() instanceof ComplexType);

        complexType = (ComplexType) outputSchema.getTypeSymbolTable().getReferences().get(6).getReference();
        assertEquals("{A}difference-type.complexTypeA-null.1", complexType.getName());
        assertEquals(false, (boolean) complexType.getMixed());
        assertTrue(complexType.getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) complexType.getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(1, ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().size());
        assertTrue(((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst() instanceof Element);
        assertEquals("{A}elementB", ((Element) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getName());
        assertEquals("{A}difference-type.complexTypeB-null", ((Element) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getType().getName());
        assertTrue(outputSchema.getTypeSymbolTable().getReferences().get(7).getReference() instanceof ComplexType);

        complexType = (ComplexType) outputSchema.getTypeSymbolTable().getReferences().get(7).getReference();
        assertEquals("{A}difference-type.complexTypeB-null", complexType.getName());
        assertEquals(false, (boolean) complexType.getMixed());
        assertTrue(complexType.getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) complexType.getContent()).getParticle() instanceof SequencePattern);
        assertEquals(2, ((SequencePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().size());
        assertTrue(((SequencePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst() instanceof Element);
        assertEquals("{A}elementD", ((Element) ((SequencePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}integer", ((Element) ((SequencePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getFirst()).getType().getName());
        assertTrue(((SequencePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getLast() instanceof Element);
        assertEquals("{A}elementE", ((Element) ((SequencePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getLast()).getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Element) ((SequencePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().getLast()).getType().getName());
        assertTrue(outputSchema.getTypeSymbolTable().getReferences().get(8).getReference() instanceof ComplexType);

        complexType = (ComplexType) outputSchema.getTypeSymbolTable().getReferences().get(8).getReference();
        assertEquals("{A}difference-type.complexTypeB-complexTypeB", complexType.getName());
        assertEquals(false, (boolean) complexType.getMixed());
        assertTrue(complexType.getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) complexType.getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(1, ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().size());
        assertTrue(((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(0) instanceof ChoicePattern);
        assertEquals(1, ((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(0)).getParticles().size());
        assertTrue(((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(0)).getParticles().getFirst() instanceof SequencePattern);
        assertEquals(2, ((SequencePattern) ((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(0)).getParticles().getFirst()).getParticles().size());
        assertTrue(((SequencePattern) ((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(0)).getParticles().getFirst()).getParticles().getFirst() instanceof Element);
        assertEquals("{A}elementD", ((Element) ((SequencePattern) ((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(0)).getParticles().getFirst()).getParticles().getFirst()).getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}integer", ((Element) ((SequencePattern) ((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(0)).getParticles().getFirst()).getParticles().getFirst()).getType().getName());
        assertTrue(((SequencePattern) ((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(0)).getParticles().getFirst()).getParticles().getLast() instanceof Element);
        assertEquals("{A}elementE", ((Element) ((SequencePattern) ((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(0)).getParticles().getFirst()).getParticles().getLast()).getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Element) ((SequencePattern) ((ChoicePattern) ((ChoicePattern) ((ComplexContentType) complexType.getContent()).getParticle()).getParticles().get(0)).getParticles().getFirst()).getParticles().getLast()).getType().getName());
    }
}