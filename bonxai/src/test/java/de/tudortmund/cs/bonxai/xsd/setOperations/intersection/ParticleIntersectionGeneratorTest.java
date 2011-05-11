package de.tudortmund.cs.bonxai.xsd.setOperations.intersection;

import de.tudortmund.cs.bonxai.common.Annotation;
import de.tudortmund.cs.bonxai.common.AnyPattern;
import de.tudortmund.cs.bonxai.common.ChoicePattern;
import de.tudortmund.cs.bonxai.common.CountingPattern;
import de.tudortmund.cs.bonxai.common.ElementRef;
import de.tudortmund.cs.bonxai.common.GroupRef;
import de.tudortmund.cs.bonxai.common.Particle;
import de.tudortmund.cs.bonxai.common.ProcessContentsInstruction;
import de.tudortmund.cs.bonxai.common.SequencePattern;
import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.xsd.Element.Block;
import de.tudortmund.cs.bonxai.xsd.Element.*;
import de.tudortmund.cs.bonxai.xsd.automaton.ParticleAutomatons.exceptions.*;
import de.tudortmund.cs.bonxai.xsd.automaton.exceptions.*;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>ParticleIntersectionGenerator</tt> class, checks that
 * every method of this class performs properly.
 * @author Dominik Wolff
 */
public class ParticleIntersectionGeneratorTest extends junit.framework.TestCase {

    public ParticleIntersectionGeneratorTest() {
    }

    /**
     * Test of areIntersectableAnyPatterns method, of class 
     * ParticleIntersectionGenerator which returns true.
     */
    @Test
    public void testAreIntersectableAnyPatternsTrue() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##other");
        anyPatterns.add(anyPatternA);
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "##any");
        anyPatterns.add(anyPatternB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        anyPatternOldSchemaMap.put(anyPatternB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null);

        boolean expResult = true;
        boolean result = instance.areIntersectableAnyPatterns(anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of areIntersectableAnyPatterns method, of class
     * ParticleIntersectionGenerator which returns false.
     */
    @Test
    public void testAreIntersectableAnyPatternsFalse() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##other");
        anyPatterns.add(anyPatternA);
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "##local");
        anyPatterns.add(anyPatternB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        anyPatternOldSchemaMap.put(anyPatternB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null);

        boolean expResult = false;
        boolean result = instance.areIntersectableAnyPatterns(anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of areIntersectableElements method, of class ParticleIntersectionGenerator.
     */
    @Test
    public void testAreIntersectableElements() {
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        elements.add(elementA);
        Element elementB = new Element("{A}elementA");
        elements.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null);

        boolean expResult = true;
        boolean result = instance.areIntersectableElements(elements);

        assertEquals(expResult, result);
    }

    /**
     * Test of areIntersectableElements method, of class
     * ParticleIntersectionGenerator for qualified and ungualified elements.
     */
    @Test
    public void testAreIntersectableElementsUnqualifiedQualified() {
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        elements.add(elementA);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.unqualified);
        elements.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null);

        boolean expResult = false;
        boolean result = instance.areIntersectableElements(elements);

        assertEquals(expResult, result);
    }

    /**
     * Test of areIntersectableElements method, of class
     * ParticleIntersectionGenerator for ungualified elements.
     */
    @Test
    public void testAreIntersectableElementsUnqualifiedUnqualified() {
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.unqualified);
        elements.add(elementA);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.unqualified);
        elements.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null);

        boolean expResult = true;
        boolean result = instance.areIntersectableElements(elements);

        assertEquals(expResult, result);
    }

    /**
     * Test of areIntersectableElements method, of class
     * ParticleIntersectionGenerator for qualified and ungualified elements and
     * different namespaces.
     */
    @Test
    public void testAreIntersectableElementsUnqualifiedQualifiedDifferentNamespace() {
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        Element elementA = new Element("{}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        elements.add(elementA);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.unqualified);
        elements.add(elementB);

        XSDSchema oldSchemaA = new XSDSchema("A");
        XSDSchema oldSchemaB = new XSDSchema("B");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchemaA);
        elementOldSchemaMap.put(elementB, oldSchemaB);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null);

        boolean expResult = true;
        boolean result = instance.areIntersectableElements(elements);

        assertEquals(expResult, result);
    }

    /**
     * Test of areIntersectableElements method, of class 
     * ParticleIntersectionGenerator for a set containing same fixed values.
     */
    @Test
    public void testAreIntersectableElementsSameFixed() {
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.setFixed("bla");
        elements.add(elementA);
        Element elementB = new Element("{A}elementA");
        elementB.setFixed("bla");
        elements.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null);

        boolean expResult = true;
        boolean result = instance.areIntersectableElements(elements);

        assertEquals(expResult, result);
    }

    /**
     * Test of areIntersectableElements method, of class 
     * ParticleIntersectionGenerator for a set containing single fixed values.
     */
    @Test
    public void testAreIntersectableElementsSingleFixed() {
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.setFixed("bla");
        elements.add(elementA);
        Element elementB = new Element("{A}elementA");
        elements.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null);

        boolean expResult = true;
        boolean result = instance.areIntersectableElements(elements);

        assertEquals(expResult, result);
    }

    /**
     * Test of areIntersectableElements method, of class
     * ParticleIntersectionGenerator for a set containing different fixed
     * values.
     */
    @Test
    public void testAreIntersectableElementsDifferentFixed() {
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.setFixed("bla");
        elements.add(elementA);
        Element elementB = new Element("{A}elementA");
        elementB.setFixed("bla2");
        elements.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null);

        boolean expResult = false;
        boolean result = instance.areIntersectableElements(elements);

        assertEquals(expResult, result);
    }

    /**
     * Test of generateNewAnyPattern method, of class ParticleIntersectionGenerator.
     */
    @Test
    public void testGenerateNewAnyPattern() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##any");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "B");
        anyPatterns.add(anyPatternA);
        anyPatterns.add(anyPatternB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null);

        AnyPattern result = instance.generateNewAnyPattern(anyPatterns);

        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getId());
        assertEquals("B", result.getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, result.getProcessContentsInstruction());
    }

    /**
     * Test of generateNewAnyPattern method, of class 
     * ParticleIntersectionGenerator for an empty any pattern.
     */
    @Test
    public void testGenerateNewAnyPatternEmpty() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "A");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "B");
        anyPatterns.add(anyPatternA);
        anyPatterns.add(anyPatternB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null);

        AnyPattern result = instance.generateNewAnyPattern(anyPatterns);

        assertEquals(null, result);
    }

    /**
     * Test of generateNewAnyPattern method, of class
     * ParticleIntersectionGenerator for a set of any patterns with different
     * IDs.
     */
    @Test
    public void testGenerateNewAnyPatternDifferentIDs() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "B");
        anyPatternA.setId("idOne");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "B");
        anyPatternB.setId("idTwo");
        anyPatterns.add(anyPatternA);
        anyPatterns.add(anyPatternB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null);

        AnyPattern result = instance.generateNewAnyPattern(anyPatterns);

        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getId());
        assertEquals("B", result.getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, result.getProcessContentsInstruction());
    }

    /**
     * Test of generateNewAnyPattern method, of class
     * ParticleIntersectionGenerator for a set of any patterns with same IDs.
     */
    @Test
    public void testGenerateNewAnyPatternSameIDs() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "B");
        anyPatternA.setId("idOne");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "B");
        anyPatternB.setId("idOne");
        anyPatterns.add(anyPatternA);
        anyPatterns.add(anyPatternB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null);

        AnyPattern result = instance.generateNewAnyPattern(anyPatterns);

        assertEquals(null, result.getAnnotation());
        assertEquals("idOne", result.getId());
        assertEquals("B", result.getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, result.getProcessContentsInstruction());
    }

    /**
     * Test of generateNewAnyPattern method, of class
     * ParticleIntersectionGenerator for a set of any patterns with annotations.
     */
    @Test
    public void testGenerateNewAnyPatternAnnotation() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "B");
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        anyPatternA.setAnnotation(annotationA);

        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "B");
        Annotation annotationB = new Annotation();
        AppInfo appInfoB = new AppInfo();
        appInfoB.setSource("blaB");
        Documentation documentationB = new Documentation();
        documentationB.setSource("blaaB");
        annotationB.addAppInfos(appInfoB);
        annotationB.addDocumentations(documentationB);
        anyPatternB.setAnnotation(annotationB);

        anyPatterns.add(anyPatternA);
        anyPatterns.add(anyPatternB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null);

        AnyPattern result = instance.generateNewAnyPattern(anyPatterns);

        assertTrue(result.getAnnotation().getAppInfos().size() == 2);
        assertEquals("blaA", result.getAnnotation().getAppInfos().get(0).getSource());
        assertEquals("blaB", result.getAnnotation().getAppInfos().get(1).getSource());
        assertTrue(result.getAnnotation().getDocumentations().size() == 2);
        assertEquals("blaaA", result.getAnnotation().getDocumentations().get(0).getSource());
        assertEquals("blaaB", result.getAnnotation().getDocumentations().get(1).getSource());
        assertEquals(null, result.getId());
        assertEquals("B", result.getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, result.getProcessContentsInstruction());
    }

    /**
     * Test of getIncluded method, of class ParticleIntersectionGenerator for
     * a skip validated any pattern.
     */
    @Test
    public void testGetIncludedSkip() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Skip, "##any");
        anyPatterns.add(anyPattern);

        Element elementA = new Element("{A}elementA");
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        oldSchema.getElementSymbolTable().updateOrCreateReference("{A}elementA", elementA);
        oldSchema.addElement(oldSchema.getElementSymbolTable().getReference("{A}elementA"));
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPattern, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, elementTypeMap, null, null, null);
        instance.setTypeIntersectionGenerator(new TypeIntersectionGenerator(outputSchema, null, null, null, null));

        String elementName = "{A}elementA";

        Element expResult = null;
        Element result = instance.getIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of getIncluded method, of class ParticleIntersectionGenerator for
     * a strict validated any pattern.
     */
    @Test
    public void testGetIncludedStrict() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Strict, "##any");
        anyPatterns.add(anyPattern);

        Element elementA = new Element("{A}elementA");
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        oldSchema.getElementSymbolTable().updateOrCreateReference("{A}elementA", elementA);
        oldSchema.addElement(oldSchema.getElementSymbolTable().getReference("{A}elementA"));
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPattern, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, elementTypeMap, null, null, null);
        instance.setTypeIntersectionGenerator(new TypeIntersectionGenerator(outputSchema, null, null, null, null));

        String elementName = "{A}elementA";

        Element expResult = elementA;
        Element result = instance.getIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of getIncluded method, of class ParticleIntersectionGenerator for
     * a lax validated any pattern.
     */
    @Test
    public void testGetIncludedLax() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Lax, "##any");
        anyPatterns.add(anyPattern);

        Element elementA = new Element("{A}elementA");
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        oldSchema.getElementSymbolTable().updateOrCreateReference("{A}elementA", elementA);
        oldSchema.addElement(oldSchema.getElementSymbolTable().getReference("{A}elementA"));
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPattern, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, elementTypeMap, null, null, null);
        instance.setTypeIntersectionGenerator(new TypeIntersectionGenerator(outputSchema, null, null, null, null));

        String elementName = "{A}elementA";

        Element expResult = elementA;
        Element result = instance.getIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of getIncluded method, of class ParticleIntersectionGenerator for
     * a strict validated any pattern with uris.
     */
    @Test
    public void testGetIncludedUris() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Strict, "A B");
        anyPatterns.add(anyPattern);

        Element elementA = new Element("{A}elementA");
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        Element elementB = new Element("{B}elementB");

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        XSDSchema oldSchemaA = new XSDSchema("A");
        XSDSchema oldSchemaB = new XSDSchema("B");
        ImportedSchema importedSchema = new ImportedSchema("B", "C:/b.xsd");
        importedSchema.setSchema(oldSchemaB);
        oldSchemaA.addForeignSchema(importedSchema);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchemaA);
        elementOldSchemaMap.put(elementB, oldSchemaB);
        oldSchemaA.getElementSymbolTable().updateOrCreateReference("{A}elementA", elementA);
        oldSchemaA.addElement(oldSchemaA.getElementSymbolTable().getReference("{A}elementA"));
        oldSchemaB.getElementSymbolTable().updateOrCreateReference("{B}elementB", elementB);
        oldSchemaB.addElement(oldSchemaB.getElementSymbolTable().getReference("{B}elementB"));
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPattern, oldSchemaA);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, elementTypeMap, null, null, null);
        instance.setTypeIntersectionGenerator(new TypeIntersectionGenerator(outputSchema, null, null, null, null));

        String elementName = "{B}elementB";

        Element expResult = elementB;
        Element result = instance.getIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of getIncluded method, of class ParticleIntersectionGenerator for
     * a strict validated any pattern with "##targetNamespace".
     */
    @Test
    public void testGetIncludedTargetNamespace() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Strict, "##targetNamespace");
        anyPatterns.add(anyPattern);

        Element elementA = new Element("{A}elementA");
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        Element elementB = new Element("{B}elementB");

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        XSDSchema oldSchemaA = new XSDSchema("A");
        XSDSchema oldSchemaB = new XSDSchema("B");
        ImportedSchema importedSchema = new ImportedSchema("B", "C:/b.xsd");
        importedSchema.setSchema(oldSchemaB);
        oldSchemaA.addForeignSchema(importedSchema);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchemaA);
        elementOldSchemaMap.put(elementB, oldSchemaB);
        oldSchemaA.getElementSymbolTable().updateOrCreateReference("{A}elementA", elementA);
        oldSchemaA.addElement(oldSchemaA.getElementSymbolTable().getReference("{A}elementA"));
        oldSchemaB.getElementSymbolTable().updateOrCreateReference("{B}elementB", elementB);
        oldSchemaB.addElement(oldSchemaB.getElementSymbolTable().getReference("{B}elementB"));
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPattern, oldSchemaA);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, elementTypeMap, null, null, null);
        instance.setTypeIntersectionGenerator(new TypeIntersectionGenerator(outputSchema, null, null, null, null));

        String elementName = "{A}elementA";

        Element expResult = elementA;
        Element result = instance.getIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of getIncluded method, of class ParticleIntersectionGenerator for
     * a strict validated any pattern with "##local".
     */
    @Test
    public void testGetIncludedLocal() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Strict, "##local");
        anyPatterns.add(anyPattern);

        Element elementA = new Element("{A}elementA");
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        Element elementB = new Element("{}elementB");

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        XSDSchema oldSchemaA = new XSDSchema("A");
        XSDSchema oldSchemaB = new XSDSchema("");
        ImportedSchema importedSchema = new ImportedSchema(null, "C:/b.xsd");
        importedSchema.setSchema(oldSchemaB);
        oldSchemaA.addForeignSchema(importedSchema);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchemaA);
        elementOldSchemaMap.put(elementB, oldSchemaB);
        oldSchemaA.getElementSymbolTable().updateOrCreateReference("{A}elementA", elementA);
        oldSchemaA.addElement(oldSchemaA.getElementSymbolTable().getReference("{A}elementA"));
        oldSchemaB.getElementSymbolTable().updateOrCreateReference("{}elementB", elementB);
        oldSchemaB.addElement(oldSchemaB.getElementSymbolTable().getReference("{}elementB"));
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPattern, oldSchemaA);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, elementTypeMap, null, null, null);
        instance.setTypeIntersectionGenerator(new TypeIntersectionGenerator(outputSchema, null, null, null, null));

        String elementName = "{}elementB";

        Element expResult = elementB;
        Element result = instance.getIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of getIncluded method, of class ParticleIntersectionGenerator for
     * a strict validated any pattern with "##other".
     */
    @Test
    public void testGetIncludedOther() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Strict, "##other");
        anyPatterns.add(anyPattern);

        Element elementA = new Element("{A}elementA");
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        Element elementB = new Element("{}elementB");

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        XSDSchema oldSchemaA = new XSDSchema("A");
        XSDSchema oldSchemaB = new XSDSchema("");
        ImportedSchema importedSchema = new ImportedSchema(null, "C:/b.xsd");
        importedSchema.setSchema(oldSchemaB);
        oldSchemaA.addForeignSchema(importedSchema);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchemaA);
        elementOldSchemaMap.put(elementB, oldSchemaB);
        oldSchemaA.getElementSymbolTable().updateOrCreateReference("{A}elementA", elementA);
        oldSchemaA.addElement(oldSchemaA.getElementSymbolTable().getReference("{A}elementA"));
        oldSchemaB.getElementSymbolTable().updateOrCreateReference("{}elementB", elementB);
        oldSchemaB.addElement(oldSchemaB.getElementSymbolTable().getReference("{}elementB"));
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPattern, oldSchemaA);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, elementTypeMap, null, null, null);
        instance.setTypeIntersectionGenerator(new TypeIntersectionGenerator(outputSchema, null, null, null, null));

        String elementName = "{}elementB";

        Element expResult = elementB;
        Element result = instance.getIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of setTypeIntersectionGenerator method, of class ParticleIntersectionGenerator.
     */
    @Test
    public void testSetTypeIntersectionGenerator() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Strict, "##other");
        anyPatterns.add(anyPattern);

        Element elementA = new Element("{A}elementA");
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        Element elementB = new Element("{}elementB");

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        XSDSchema oldSchemaA = new XSDSchema("A");
        XSDSchema oldSchemaB = new XSDSchema("");
        ImportedSchema importedSchema = new ImportedSchema(null, "C:/b.xsd");
        importedSchema.setSchema(oldSchemaB);
        oldSchemaA.addForeignSchema(importedSchema);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchemaA);
        elementOldSchemaMap.put(elementB, oldSchemaB);
        oldSchemaA.getElementSymbolTable().updateOrCreateReference("{A}elementA", elementA);
        oldSchemaA.addElement(oldSchemaA.getElementSymbolTable().getReference("{A}elementA"));
        oldSchemaB.getElementSymbolTable().updateOrCreateReference("{}elementB", elementB);
        oldSchemaB.addElement(oldSchemaB.getElementSymbolTable().getReference("{}elementB"));
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPattern, oldSchemaA);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, elementTypeMap, null, null, null);
        instance.setTypeIntersectionGenerator(new TypeIntersectionGenerator(outputSchema, null, null, null, null));

        String elementName = "{}elementB";

        Element expResult = elementB;
        Element result = instance.getIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator.
     */
    @Test
    public void testGenerateNewLocalParticleElementsElement() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        particles.add(elementA);
        Element elementB = new Element("{A}elementA");
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator not all schemata contain same element.
     */
    @Test
    public void testGenerateNewLocalParticleElementsElementNotSameElement() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        particles.add(elementA);
        Element elementB = new Element("{A}elementB");
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertEquals(null, result);
        assertFalse(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator for a set of elements with different
     * IDs.
     */
    @Test
    public void testGenerateNewLocalParticleElementsElementDifferentIDs() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setId("idOne");
        particles.add(elementA);
        Element elementB = new Element("{A}elementA");
        elementB.setId("idTwo");
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator for a set of elements with same IDs.
     */
    @Test
    public void testGenerateNewLocalParticleElementsElementSameIDs() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setId("idOne");
        particles.add(elementA);
        Element elementB = new Element("{A}elementA");
        elementB.setId("idOne");
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals("idOne", ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator for a set of any patterns with annotations.
     */
    @Test
    public void testGenerateNewLocalParticleElementsElementAnnotation() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        elementA.setAnnotation(annotationA);
        particles.add(elementA);
        Element elementB = new Element("{A}elementA");
        Annotation annotationB = new Annotation();
        AppInfo appInfoB = new AppInfo();
        appInfoB.setSource("blaB");
        Documentation documentationB = new Documentation();
        documentationB.setSource("blaaB");
        annotationB.addAppInfos(appInfoB);
        annotationB.addDocumentations(documentationB);
        elementB.setAnnotation(annotationB);
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(null, ((Element) result).getForm());
        assertTrue(((Element) result).getAnnotation().getAppInfos().size() == 2);
        assertEquals("blaA", ((Element) result).getAnnotation().getAppInfos().get(0).getSource());
        assertEquals("blaB", ((Element) result).getAnnotation().getAppInfos().get(1).getSource());
        assertTrue(((Element) result).getAnnotation().getDocumentations().size() == 2);
        assertEquals("blaaA", ((Element) result).getAnnotation().getDocumentations().get(0).getSource());
        assertEquals("blaaB", ((Element) result).getAnnotation().getDocumentations().get(1).getSource());

    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator with nillable attribute.
     */
    @Test
    public void testGenerateNewLocalParticleElementsElementNillableTrue() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setNillable();
        particles.add(elementA);
        Element elementB = new Element("{A}elementA");
        elementB.setNillable();
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(true, (boolean) ((Element) result).getNillable());
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator without nillable attribute.
     */
    @Test
    public void testGenerateNewLocalParticleElementsElementNillableFalse() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setNillable();
        particles.add(elementA);
        Element elementB = new Element("{A}elementA");
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator without same default attributes.
     */
    @Test
    public void testGenerateNewLocalParticleElementsElementWithoutSameDefaults() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setDefault("bla");
        particles.add(elementA);
        Element elementB = new Element("{A}elementA");
        elementB.setDefault("bla2");
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator with same default attributes.
     */
    @Test
    public void testGenerateNewLocalParticleElementsElementSameDefaults() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setDefault("bla");
        particles.add(elementA);
        Element elementB = new Element("{A}elementA");
        elementB.setDefault("bla");
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals("bla", ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator with default attribute.
     */
    @Test
    public void testGenerateNewLocalParticleElementsElementDefault() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setDefault("bla");
        particles.add(elementA);
        Element elementB = new Element("{A}elementA");
        elementB.setDefault("bla");
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals("bla", ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator with true type attribute.
     */
    @Test
    public void testGenerateNewLocalParticleElementsElementTypeAttribute() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        particles.add(elementA);
        Element elementB = new Element("{A}elementA");
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.setIsAnonymous(true);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(true, ((Element) result).getType().isAnonymous());
        assertEquals(true, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator without same fixed attributes.
     */
    @Test
    public void testGenerateNewLocalParticleElementsElementWithoutSameFixed() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setFixed("bla");
        particles.add(elementA);
        Element elementB = new Element("{A}elementA");
        elementB.setFixed("bla2");
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertEquals(null, result);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator with same fixed attributes.
     */
    @Test
    public void testGenerateNewLocalParticleElementsElementSameFixed() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setFixed("bla");
        particles.add(elementA);
        Element elementB = new Element("{A}elementA");
        elementB.setFixed("bla");
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals("bla", ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator with fixed attribute.
     */
    @Test
    public void testGenerateNewLocalParticleElementsElementFixed() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setFixed("bla");
        particles.add(elementA);
        Element elementB = new Element("{A}elementA");
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals("bla", ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator for an element set which blocks extension.
     */
    @Test
    public void testGenerateNewLocalParticleElementsElementBlockExtension() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.addBlockModifier(Element.Block.extension);
        particles.add(elementA);
        Element elementB = new Element("{A}elementA");
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        HashSet<Block> block = new HashSet<Block>();
        block.add(Block.extension);
        assertEquals(block, ((Element) result).getBlockModifiers());
        assertEquals(null, ((Element) result).getFinalModifiers());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator for an element set which blocks restriction.
     */
    @Test
    public void testGenerateNewLocalParticleElementsElementBlockRestriction() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.addBlockModifier(Element.Block.restriction);
        particles.add(elementA);
        Element elementB = new Element("{A}elementA");
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        HashSet<Block> block = new HashSet<Block>();
        block.add(Block.restriction);
        assertEquals(block, ((Element) result).getBlockModifiers());
        assertEquals(null, ((Element) result).getFinalModifiers());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator for an element set which blocks
     * substitution.
     */
    @Test
    public void testGenerateNewLocalParticleElementsElementBlockSubstitution() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.addBlockModifier(Element.Block.substitution);
        particles.add(elementA);
        Element elementB = new Element("{A}elementA");
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        HashSet<Block> block = new HashSet<Block>();
        block.add(Block.substitution);
        assertEquals(block, ((Element) result).getBlockModifiers());
        assertEquals(null, ((Element) result).getFinalModifiers());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator for an element set which blocks
     * extension per default.
     */
    @Test
    public void testGenerateNewLocalParticleElementsElementBlockDefaultExtension() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.addBlockModifier(Element.Block.restriction);
        particles.add(elementA);
        Element elementB = new Element("{A}elementA");
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.extension);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        HashSet<Block> block = new HashSet<Block>();
        block.add(Block.restriction);
        block.add(Block.extension);
        assertEquals(block, ((Element) result).getBlockModifiers());
        assertEquals(null, ((Element) result).getFinalModifiers());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator for an element set which blocks
     * restriction per default.
     */
    @Test
    public void testGenerateNewLocalParticleElementsElementBlockDefaultRestriction() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.addBlockModifier(Element.Block.extension);
        particles.add(elementA);
        Element elementB = new Element("{A}elementA");
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.restriction);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        HashSet<Block> block = new HashSet<Block>();
        block.add(Block.restriction);
        block.add(Block.extension);
        assertEquals(block, ((Element) result).getBlockModifiers());
        assertEquals(null, ((Element) result).getFinalModifiers());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator for an element set which blocks
     * substitution per default.
     */
    @Test
    public void testGenerateNewLocalParticleElementsElementBlockDefaultSubstitution() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.addBlockModifier(Element.Block.extension);
        particles.add(elementA);
        Element elementB = new Element("{A}elementA");
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.substitution);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        HashSet<Block> block = new HashSet<Block>();
        block.add(Block.substitution);
        block.add(Block.extension);
        assertEquals(block, ((Element) result).getBlockModifiers());
        assertEquals(null, ((Element) result).getFinalModifiers());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator for an element set which blocks
     * substitution which is schema default.
     */
    @Test
    public void testGenerateNewLocalParticleElementsElementBlockDefault() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        particles.add(elementA);
        Element elementB = new Element("{A}elementA");
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.substitution);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.addBlockDefault(XSDSchema.BlockDefault.substitution);
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(null, ((Element) result).getBlockModifiers());
        assertEquals(null, ((Element) result).getFinalModifiers());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator for unqualified elements.
     */
    @Test
    public void testGenerateNewLocalParticleElementsElementQualifiedNull() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(null);
        particles.add(elementA);
        Element elementB = new Element("{A}elementA");
        elementA.setForm(null);
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertEquals("{A}elementA", ((Element) result).getName());
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator for foreign qualified elements.
     */
    @Test
    public void testGenerateNewLocalParticleElementsElementForeignQualified() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        particles.add(elementA);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.qualified);
        particles.add(elementB);
        Element elementC = new Element("{A}elementA");

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        elementOldSchemaMap.put(elementC, oldSchema);
        XSDSchema outputSchema = new XSDSchema("B");
        outputSchema.setSchemaLocation("Main");
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("B", outputSchema);
        LinkedHashSet<XSDSchema> otherSchemata = new LinkedHashSet<XSDSchema>();
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{A}elementA", typeRefA);
        elementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, elementOldSchemaMap, null, elementTypeMap, otherSchemata, namespaceAbbreviationMap, "C:/");
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementC);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertEquals("{A}elementA", ((Element) result).getName());
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{B}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{B}elementA").getReference()));
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator for foreign unqualified elements.
     */
    @Test
    public void testGenerateNewLocalParticleElementsElementForeignUnqualified() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.unqualified);
        particles.add(elementA);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.unqualified);
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("B");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertEquals("{B}elementA", ((Element) result).getName());
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator for qualified elements.
     */
    @Test
    public void testGenerateNewLocalParticleElementsElementQualified() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        particles.add(elementA);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.qualified);
        particles.add(elementB);
        Element elementC = new Element("{A}elementA");

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        elementOldSchemaMap.put(elementC, oldSchema);
        XSDSchema outputSchema = new XSDSchema("B");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{A}elementA", typeRefA);
        elementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementC);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertEquals("{A}elementA", ((Element) result).getName());
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator.
     */
    @Test
    public void testGenerateNewLocalParticleElementElementRefElement() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        ElementRef elementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        particles.add(elementRef);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.qualified);
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{A}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator not all schemata contain same element.
     */
    @Test
    public void testGenerateNewLocalParticleElementElementRefElementNotSameElement() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        ElementRef elementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        particles.add(elementRef);
        Element elementB = new Element("{A}elementB");
        elementB.setForm(XSDSchema.Qualification.qualified);
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{A}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertEquals(null, result);
        assertFalse(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator for a set of elements with different
     * IDs.
     */
    @Test
    public void testGenerateNewLocalParticleElementElementRefElementDifferentIDs() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        ElementRef elementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        elementRef.setId("idOne");
        particles.add(elementRef);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.qualified);
        elementB.setId("idTwo");
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{A}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator for a set of elements with same IDs.
     */
    @Test
    public void testGenerateNewLocalParticleElementElementRefElementSameIDs() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        ElementRef elementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        elementRef.setId("idOne");
        particles.add(elementRef);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.qualified);
        elementB.setId("idOne");
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{A}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals("idOne", ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator for a set of any patterns with annotations.
     */
    @Test
    public void testGenerateNewLocalParticleElementElementRefElementAnnotation() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        ElementRef elementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        elementRef.setAnnotation(annotationA);
        particles.add(elementRef);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.qualified);
        Annotation annotationB = new Annotation();
        AppInfo appInfoB = new AppInfo();
        appInfoB.setSource("blaB");
        Documentation documentationB = new Documentation();
        documentationB.setSource("blaaB");
        annotationB.addAppInfos(appInfoB);
        annotationB.addDocumentations(documentationB);
        elementB.setAnnotation(annotationB);
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{A}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
        assertTrue(((Element) result).getAnnotation().getAppInfos().size() == 2);
        assertEquals("blaA", ((Element) result).getAnnotation().getAppInfos().get(0).getSource());
        assertEquals("blaB", ((Element) result).getAnnotation().getAppInfos().get(1).getSource());
        assertTrue(((Element) result).getAnnotation().getDocumentations().size() == 2);
        assertEquals("blaaA", ((Element) result).getAnnotation().getDocumentations().get(0).getSource());
        assertEquals("blaaB", ((Element) result).getAnnotation().getDocumentations().get(1).getSource());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator with nillable attribute.
     */
    @Test
    public void testGenerateNewLocalParticleElementElementRefElementNillableTrue() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        elementA.setNillable();
        ElementRef elementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        particles.add(elementRef);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.qualified);
        elementB.setNillable();
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{A}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(true, (boolean) ((Element) result).getNillable());
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator without nillable attribute.
     */
    @Test
    public void testGenerateNewLocalParticleElementElementRefElementNillableFalse() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        elementA.setNillable();
        ElementRef elementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        particles.add(elementRef);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.qualified);
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{A}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator without same default attributes.
     */
    @Test
    public void testGenerateNewLocalParticleElementElementRefElementWithoutSameDefaults() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        elementA.setDefault("bla");
        ElementRef elementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        particles.add(elementRef);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.qualified);
        elementB.setDefault("bla2");
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{A}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator with same default attributes.
     */
    @Test
    public void testGenerateNewLocalParticleElementElementRefElementSameDefaults() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        elementA.setDefault("bla");
        ElementRef elementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        particles.add(elementRef);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.qualified);
        elementB.setDefault("bla");
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{A}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals("bla", ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator with default attribute.
     */
    @Test
    public void testGenerateNewLocalParticleElementElementRefElementDefault() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        elementA.setDefault("bla");
        ElementRef elementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        particles.add(elementRef);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.qualified);
        elementB.setDefault("bla");
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{A}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals("bla", ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator with true type attribute.
     */
    @Test
    public void testGenerateNewLocalParticleElementElementRefElementTypeAttribute() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        ElementRef elementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        particles.add(elementRef);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.qualified);
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.setIsAnonymous(true);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{A}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(true, ((Element) result).getType().isAnonymous());
        assertEquals(true, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator without same fixed attributes.
     */
    @Test
    public void testGenerateNewLocalParticleElementElementRefElementWithoutSameFixed() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        elementA.setFixed("bla");
        ElementRef elementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        particles.add(elementRef);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.qualified);
        elementB.setFixed("bla2");
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{A}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertEquals(null, result);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator with same fixed attributes.
     */
    @Test
    public void testGenerateNewLocalParticleElementElementRefElementSameFixed() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        elementA.setFixed("bla");
        ElementRef elementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        particles.add(elementRef);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.qualified);
        elementB.setFixed("bla");
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{A}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals("bla", ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator with fixed attribute.
     */
    @Test
    public void testGenerateNewLocalParticleElementElementRefElementFixed() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        elementA.setFixed("bla");
        ElementRef elementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        particles.add(elementRef);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.qualified);
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{A}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals("bla", ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator for an element set which blocks extension.
     */
    @Test
    public void testGenerateNewLocalParticleElementElementRefElementBlockExtension() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        elementA.addBlockModifier(Element.Block.extension);
        ElementRef elementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        particles.add(elementRef);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.qualified);
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{A}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        HashSet<Block> block = new HashSet<Block>();
        block.add(Block.extension);
        assertEquals(block, ((Element) result).getBlockModifiers());
        assertEquals(null, ((Element) result).getFinalModifiers());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator for an element set which blocks restriction.
     */
    @Test
    public void testGenerateNewLocalParticleElementElementRefElementBlockRestriction() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        elementA.addBlockModifier(Element.Block.restriction);
        ElementRef elementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        particles.add(elementRef);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.qualified);
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{A}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        HashSet<Block> block = new HashSet<Block>();
        block.add(Block.restriction);
        assertEquals(block, ((Element) result).getBlockModifiers());
        assertEquals(null, ((Element) result).getFinalModifiers());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator for an element set which blocks
     * substitution.
     */
    @Test
    public void testGenerateNewLocalParticleElementElementRefElementBlockSubstitution() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        elementA.addBlockModifier(Element.Block.substitution);
        ElementRef elementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        particles.add(elementRef);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.qualified);
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{A}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        HashSet<Block> block = new HashSet<Block>();
        block.add(Block.substitution);
        assertEquals(block, ((Element) result).getBlockModifiers());
        assertEquals(null, ((Element) result).getFinalModifiers());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator for an element set which blocks
     * extension per default.
     */
    @Test
    public void testGenerateNewLocalParticleElementElementRefElementBlockDefaultExtension() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        elementA.addBlockModifier(Element.Block.restriction);
        ElementRef elementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        particles.add(elementRef);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.qualified);
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.extension);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{A}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        HashSet<Block> block = new HashSet<Block>();
        block.add(Block.restriction);
        block.add(Block.extension);
        assertEquals(block, ((Element) result).getBlockModifiers());
        assertEquals(null, ((Element) result).getFinalModifiers());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator for an element set which blocks
     * restriction per default.
     */
    @Test
    public void testGenerateNewLocalParticleElementElementRefElementBlockDefaultRestriction() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        elementA.addBlockModifier(Element.Block.extension);
        ElementRef elementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        particles.add(elementRef);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.qualified);
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.restriction);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{A}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        HashSet<Block> block = new HashSet<Block>();
        block.add(Block.restriction);
        block.add(Block.extension);
        assertEquals(block, ((Element) result).getBlockModifiers());
        assertEquals(null, ((Element) result).getFinalModifiers());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator for an element set which blocks
     * substitution per default.
     */
    @Test
    public void testGenerateNewLocalParticleElementElementRefElementBlockDefaultSubstitution() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        elementA.addBlockModifier(Element.Block.extension);
        ElementRef elementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        particles.add(elementRef);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.qualified);
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.substitution);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{A}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        HashSet<Block> block = new HashSet<Block>();
        block.add(Block.substitution);
        block.add(Block.extension);
        assertEquals(block, ((Element) result).getBlockModifiers());
        assertEquals(null, ((Element) result).getFinalModifiers());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator for an element set which blocks
     * substitution which is schema default.
     */
    @Test
    public void testGenerateNewLocalParticleElementElementRefElementBlockDefault() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        ElementRef elementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        particles.add(elementRef);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.qualified);
        particles.add(elementB);

        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.substitution);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.addBlockDefault(XSDSchema.BlockDefault.substitution);
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{A}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(null, ((Element) result).getBlockModifiers());
        assertEquals(null, ((Element) result).getFinalModifiers());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator for foreign qualified elements.
     */
    @Test
    public void testGenerateNewLocalParticleElementElementRefElementForeignQualified() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");

        elementA.setForm(XSDSchema.Qualification.qualified);
        ElementRef elementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        particles.add(elementRef);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.qualified);
        particles.add(elementB);
        Element elementC = new Element("{A}elementA");

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        elementOldSchemaMap.put(elementC, oldSchema);
        XSDSchema outputSchema = new XSDSchema("B");
        outputSchema.setSchemaLocation("Main");
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("B", outputSchema);
        LinkedHashSet<XSDSchema> otherSchemata = new LinkedHashSet<XSDSchema>();
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{A}elementA", typeRefA);
        elementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, elementOldSchemaMap, null, elementTypeMap, otherSchemata, namespaceAbbreviationMap, "C:/");
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementC);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertEquals("{A}elementA", ((Element) result).getName());
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{B}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{B}elementA").getReference()));
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator for qualified elements.
     */
    @Test
    public void testGenerateNewLocalParticleElementElementRefElementQualified() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        ElementRef elementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        particles.add(elementRef);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.qualified);
        particles.add(elementB);
        Element elementC = new Element("{A}elementA");

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        elementOldSchemaMap.put(elementC, oldSchema);
        XSDSchema outputSchema = new XSDSchema("B");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{A}elementA", typeRefA);
        elementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementC);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof Element);
        assertEquals("{A}elementA", ((Element) result).getName());
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator.
     */
    @Test
    public void testGenerateNewLocalParticleElementRefElement() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        ElementRef elementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        particles.add(elementRef);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.qualified);
        ElementRef elementRefB = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementB));
        particles.add(elementRefB);

        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();
        LinkedList<Element> elementsA = new LinkedList<Element>();
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);
        LinkedList<Element> elementsB = new LinkedList<Element>();
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{A}elementA", typeRefA);
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        outputSchema.getElementSymbolTable().updateOrCreateReference("{A}elementA", elementA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateNewTopLevelElements(topLevelElementListSet);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof ElementRef);
        assertEquals(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference(), ((ElementRef) result).getElement());
        assertEquals(null, ((ElementRef) result).getId());
        assertEquals(null, ((ElementRef) result).getAnnotation());
        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator for a set of elements with same IDs.
     */
    @Test
    public void testGenerateNewLocalParticleElementRefElementSameIDs() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        ElementRef elementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        elementRef.setId("idOne");
        particles.add(elementRef);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.qualified);
        ElementRef elementRefB = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementB));
        elementRefB.setId("idOne");
        particles.add(elementRefB);

        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();
        LinkedList<Element> elementsA = new LinkedList<Element>();
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);
        LinkedList<Element> elementsB = new LinkedList<Element>();
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{A}elementA", typeRefA);
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        outputSchema.getElementSymbolTable().updateOrCreateReference("{A}elementA", elementA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateNewTopLevelElements(topLevelElementListSet);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof ElementRef);
        assertEquals(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference(), ((ElementRef) result).getElement());
        assertEquals("idOne", ((ElementRef) result).getId());
        assertEquals(null, ((ElementRef) result).getAnnotation());
        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator for a set of elements with different IDs.
     */
    @Test
    public void testGenerateNewLocalParticleElementRefElementDifferentIDs() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        ElementRef elementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        elementRef.setId("idOne");
        particles.add(elementRef);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.qualified);
        ElementRef elementRefB = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementB));
        elementRefB.setId("idTwo");
        particles.add(elementRefB);

        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();
        LinkedList<Element> elementsA = new LinkedList<Element>();
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);
        LinkedList<Element> elementsB = new LinkedList<Element>();
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{A}elementA", typeRefA);
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        outputSchema.getElementSymbolTable().updateOrCreateReference("{A}elementA", elementA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateNewTopLevelElements(topLevelElementListSet);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof ElementRef);
        assertEquals(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference(), ((ElementRef) result).getElement());
        assertEquals(null, ((ElementRef) result).getId());
        assertEquals(null, ((ElementRef) result).getAnnotation());
        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
    }

    /**
     * Test of testGenerateNewLocalParticle method, of class
     * ParticleIntersectionGenerator for a set of any patterns with annotations.
     */
    @Test
    public void testGenerateNewLocalParticleElementRefsElementAnnotation() throws EmptySubsetParticleStateFieldException, EmptyProductParticleStateFieldException, NonDeterministicFiniteAutomataException, NotSupportedParticleAutomatonException {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        ElementRef elementRefA = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        elementRefA.setAnnotation(annotationA);
        particles.add(elementRefA);
        Element elementB = new Element("{A}elementA");
        elementB.setForm(XSDSchema.Qualification.qualified);
        ElementRef elementRefB = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementB));
        Annotation annotationB = new Annotation();
        AppInfo appInfoB = new AppInfo();
        appInfoB.setSource("blaB");
        Documentation documentationB = new Documentation();
        documentationB.setSource("blaaB");
        annotationB.addAppInfos(appInfoB);
        annotationB.addDocumentations(documentationB);
        elementRefB.setAnnotation(annotationB);
        particles.add(elementRefB);

        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();
        LinkedList<Element> elementsA = new LinkedList<Element>();
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);
        LinkedList<Element> elementsB = new LinkedList<Element>();
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{A}elementA", typeRefA);
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        outputSchema.getElementSymbolTable().updateOrCreateReference("{A}elementA", elementA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, elementOldSchemaMap, null, elementTypeMap, null, null, null);
        LinkedList<Particle> particleList = new LinkedList<Particle>();
        particleList.add(elementA);
        instance.generateNewTopLevelElements(topLevelElementListSet);
        instance.generateParticleIntersection(particleList, elementTypeMap);

        Particle result = instance.generateNewLocalParticle(particles);

        assertTrue(result instanceof ElementRef);
        assertEquals(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference(), ((ElementRef) result).getElement());
        assertEquals(null, ((ElementRef) result).getId());
        assertTrue(((ElementRef) result).getAnnotation().getAppInfos().size() == 2);
        assertEquals("blaA", ((ElementRef) result).getAnnotation().getAppInfos().get(0).getSource());
        assertEquals("blaB", ((ElementRef) result).getAnnotation().getAppInfos().get(1).getSource());
        assertTrue(((ElementRef) result).getAnnotation().getDocumentations().size() == 2);
        assertEquals("blaaA", ((ElementRef) result).getAnnotation().getDocumentations().get(0).getSource());
        assertEquals("blaaB", ((ElementRef) result).getAnnotation().getDocumentations().get(1).getSource());
        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
    }

    /**
     * Test of generateNewTopLevelElements method, of class
     * ParticleIntersectionGenerator.
     */
    @Test
    public void testGenerateNewTopLevelElements() {
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();

        LinkedList<Element> elementsA = new LinkedList<Element>();
        Element elementA = new Element("{A}elementA");
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);

        LinkedList<Element> elementsB = new LinkedList<Element>();
        Element elementB = new Element("{A}elementA");
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        topLevelElementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, topLevelElementTypeMap, null, null, null);

        LinkedHashMap<String, LinkedHashSet<Element>> expResult = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        elements.add(elementA);
        elements.add(elementB);
        expResult.put("{}elementA", elements);
        LinkedHashMap<String, LinkedHashSet<Element>> result = instance.generateNewTopLevelElements(topLevelElementListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getType().isAnonymous());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getTypeAttr());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getDefault());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFixed());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getId());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAnnotation());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAbstract());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getNillable());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getForm());
    }

    /**
     * Test of generateNewTopLevelElements method, of class 
     * ParticleIntersectionGenerator containing abstract element in element list.
     */
    @Test
    public void testGenerateNewTopLevelElementsAbstract() {
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();

        LinkedList<Element> elementsA = new LinkedList<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.setAbstract(true);
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);

        LinkedList<Element> elementsB = new LinkedList<Element>();
        Element elementB = new Element("{A}elementA");
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        topLevelElementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, topLevelElementTypeMap, null, null, null);

        LinkedHashMap<String, LinkedHashSet<Element>> expResult = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashMap<String, LinkedHashSet<Element>> result = instance.generateNewTopLevelElements(topLevelElementListSet);

        assertEquals(expResult, result);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
    }

    /**
     * Test of generateNewTopLevelElements method, of class
     * ParticleIntersectionGenerator not all schemata contain same element.
     */
    @Test
    public void testGenerateNewTopLevelElementsNotSameElement() {
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();

        LinkedList<Element> elementsA = new LinkedList<Element>();
        Element elementA = new Element("{A}elementA");
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);

        LinkedList<Element> elementsB = new LinkedList<Element>();
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        topLevelElementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, topLevelElementTypeMap, null, null, null);

        LinkedHashMap<String, LinkedHashSet<Element>> expResult = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashMap<String, LinkedHashSet<Element>> result = instance.generateNewTopLevelElements(topLevelElementListSet);

        assertEquals(expResult, result);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
    }

    /**
     * Test of generateNewTopLevelElements method, of class
     * ParticleIntersectionGenerator for multiple top-level elements.
     */
    @Test
    public void testGenerateNewTopLevelElementsMultipleElements() {
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();

        LinkedList<Element> elementsA = new LinkedList<Element>();
        Element elementA = new Element("{A}elementA");
        Element elementC = new Element("{A}elementB");
        elementsA.add(elementA);
        elementsA.add(elementC);
        topLevelElementListSet.add(elementsA);

        LinkedList<Element> elementsB = new LinkedList<Element>();
        Element elementB = new Element("{A}elementA");
        Element elementD = new Element("{A}elementB");
        elementsB.add(elementB);
        elementsB.add(elementD);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        elementOldSchemaMap.put(elementC, oldSchema);
        elementOldSchemaMap.put(elementD, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        topLevelElementTypeMap.put("{}elementA", typeRefA);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefB = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        topLevelElementTypeMap.put("{}elementB", typeRefB);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, topLevelElementTypeMap, null, null, null);

        LinkedHashMap<String, LinkedHashSet<Element>> expResult = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elementsFirst = new LinkedHashSet<Element>();
        elementsFirst.add(elementA);
        elementsFirst.add(elementB);
        expResult.put("{}elementA", elementsFirst);
        LinkedHashSet<Element> elementsSecond = new LinkedHashSet<Element>();
        elementsSecond.add(elementC);
        elementsSecond.add(elementD);
        expResult.put("{}elementB", elementsSecond);
        LinkedHashMap<String, LinkedHashSet<Element>> result = instance.generateNewTopLevelElements(topLevelElementListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getType().isAnonymous());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getTypeAttr());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getDefault());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFixed());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getId());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAnnotation());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAbstract());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getNillable());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getForm());
        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementB"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementB").getReference()));
        assertEquals(false, outputSchema.getElementSymbolTable().getReference("{A}elementB").getReference().getType().isAnonymous());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementB").getReference().getTypeAttr());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementB").getReference().getDefault());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementB").getReference().getFixed());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementB").getReference().getId());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementB").getReference().getAnnotation());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementB").getReference().getAbstract());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementB").getReference().getNillable());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementB").getReference().getForm());
    }

    /**
     * Test of generateNewTopLevelElements method, of class
     * ParticleIntersectionGenerator with missing referenced type.
     */
    @Test
    public void testGenerateNewTopLevelElementsReferencedTypeMissing() {
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();

        LinkedList<Element> elementsA = new LinkedList<Element>();
        Element elementA = new Element("{A}elementA");
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);

        LinkedList<Element> elementsB = new LinkedList<Element>();
        Element elementB = new Element("{A}elementA");
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", null);
        topLevelElementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, topLevelElementTypeMap, null, null, null);

        LinkedHashMap<String, LinkedHashSet<Element>> expResult = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        elements.add(elementA);
        elements.add(elementB);
        expResult.put("{}elementA", elements);
        LinkedHashMap<String, LinkedHashSet<Element>> result = instance.generateNewTopLevelElements(topLevelElementListSet);

        assertEquals(expResult, result);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
    }

    /**
     * Test of generateNewTopLevelElements method, of class
     * ParticleIntersectionGenerator with dummy referenced type.
     */
    @Test
    public void testGenerateNewTopLevelElementsReferencedTypeDummy() {
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();

        LinkedList<Element> elementsA = new LinkedList<Element>();
        Element elementA = new Element("{A}elementA");
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);

        LinkedList<Element> elementsB = new LinkedList<Element>();
        Element elementB = new Element("{A}elementA");
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.setDummy(true);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        topLevelElementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, topLevelElementTypeMap, null, null, null);

        LinkedHashMap<String, LinkedHashSet<Element>> expResult = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        elements.add(elementA);
        elements.add(elementB);
        expResult.put("{}elementA", elements);
        LinkedHashMap<String, LinkedHashSet<Element>> result = instance.generateNewTopLevelElements(topLevelElementListSet);

        assertEquals(expResult, result);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
    }

    /**
     * Test of generateNewTopLevelElements method, of class
     * ParticleIntersectionGenerator for a set of elements with different
     * IDs.
     */
    @Test
    public void testGenerateNewTopLevelElementsDifferentIDs() {
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();

        LinkedList<Element> elementsA = new LinkedList<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.setId("idOne");
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);

        LinkedList<Element> elementsB = new LinkedList<Element>();
        Element elementB = new Element("{A}elementA");
        elementB.setId("idTwo");
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        topLevelElementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, topLevelElementTypeMap, null, null, null);

        LinkedHashMap<String, LinkedHashSet<Element>> expResult = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        elements.add(elementA);
        elements.add(elementB);
        expResult.put("{}elementA", elements);
        LinkedHashMap<String, LinkedHashSet<Element>> result = instance.generateNewTopLevelElements(topLevelElementListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getType().isAnonymous());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getTypeAttr());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getDefault());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFixed());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getId());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAnnotation());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAbstract());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getNillable());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getForm());
    }

    /**
     * Test of generateNewTopLevelElements method, of class
     * ParticleIntersectionGenerator for a set of elements with same IDs.
     */
    @Test
    public void testGenerateNewTopLevelElementsSameIDs() {
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();

        LinkedList<Element> elementsA = new LinkedList<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.setId("idOne");
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);

        LinkedList<Element> elementsB = new LinkedList<Element>();
        Element elementB = new Element("{A}elementA");
        elementB.setId("idOne");
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        topLevelElementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, topLevelElementTypeMap, null, null, null);

        LinkedHashMap<String, LinkedHashSet<Element>> expResult = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        elements.add(elementA);
        elements.add(elementB);
        expResult.put("{}elementA", elements);
        LinkedHashMap<String, LinkedHashSet<Element>> result = instance.generateNewTopLevelElements(topLevelElementListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getType().isAnonymous());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getTypeAttr());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getDefault());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFixed());
        assertEquals("idOne", outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getId());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAnnotation());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAbstract());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getNillable());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getForm());
    }

    /**
     * Test of generateNewTopLevelElements method, of class
     * ParticleIntersectionGenerator for a set of any patterns with annotations.
     */
    @Test
    public void testGenerateNewTopLevelElementsAnnotation() {
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();

        LinkedList<Element> elementsA = new LinkedList<Element>();
        Element elementA = new Element("{A}elementA");
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        elementA.setAnnotation(annotationA);
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);

        LinkedList<Element> elementsB = new LinkedList<Element>();
        Element elementB = new Element("{A}elementA");
        Annotation annotationB = new Annotation();
        AppInfo appInfoB = new AppInfo();
        appInfoB.setSource("blaB");
        Documentation documentationB = new Documentation();
        documentationB.setSource("blaaB");
        annotationB.addAppInfos(appInfoB);
        annotationB.addDocumentations(documentationB);
        elementB.setAnnotation(annotationB);
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        topLevelElementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, topLevelElementTypeMap, null, null, null);

        LinkedHashMap<String, LinkedHashSet<Element>> expResult = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        elements.add(elementA);
        elements.add(elementB);
        expResult.put("{}elementA", elements);
        LinkedHashMap<String, LinkedHashSet<Element>> result = instance.generateNewTopLevelElements(topLevelElementListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getType().isAnonymous());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getTypeAttr());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getDefault());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFixed());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getId());
        assertTrue(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAnnotation().getAppInfos().size() == 2);
        assertEquals("blaA", outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAnnotation().getAppInfos().get(0).getSource());
        assertEquals("blaB", outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAnnotation().getAppInfos().get(1).getSource());
        assertTrue(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAnnotation().getDocumentations().size() == 2);
        assertEquals("blaaA", outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAnnotation().getDocumentations().get(0).getSource());
        assertEquals("blaaB", outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAnnotation().getDocumentations().get(1).getSource());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAbstract());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getNillable());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getForm());
    }

    /**
     * Test of generateNewTopLevelElements method, of class
     * ParticleIntersectionGenerator with nillable attribute.
     */
    @Test
    public void testGenerateNewTopLevelElementsNillableTrue() {
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();

        LinkedList<Element> elementsA = new LinkedList<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.setNillable();
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);

        LinkedList<Element> elementsB = new LinkedList<Element>();
        Element elementB = new Element("{A}elementA");
        elementB.setNillable();
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        topLevelElementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, topLevelElementTypeMap, null, null, null);

        LinkedHashMap<String, LinkedHashSet<Element>> expResult = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        elements.add(elementA);
        elements.add(elementB);
        expResult.put("{}elementA", elements);
        LinkedHashMap<String, LinkedHashSet<Element>> result = instance.generateNewTopLevelElements(topLevelElementListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getType().isAnonymous());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getTypeAttr());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getDefault());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFixed());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getId());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAnnotation());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAbstract());
        assertEquals(true, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getNillable());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getForm());
    }

    /**
     * Test of generateNewTopLevelElements method, of class
     * ParticleIntersectionGenerator without nillable attribute.
     */
    @Test
    public void testGenerateNewTopLevelElementsNillableFalse() {
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();

        LinkedList<Element> elementsA = new LinkedList<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.setNillable();
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);

        LinkedList<Element> elementsB = new LinkedList<Element>();
        Element elementB = new Element("{A}elementA");
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        topLevelElementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, topLevelElementTypeMap, null, null, null);

        LinkedHashMap<String, LinkedHashSet<Element>> expResult = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        elements.add(elementA);
        elements.add(elementB);
        expResult.put("{}elementA", elements);
        LinkedHashMap<String, LinkedHashSet<Element>> result = instance.generateNewTopLevelElements(topLevelElementListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getType().isAnonymous());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getTypeAttr());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getDefault());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFixed());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getId());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAnnotation());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAbstract());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getNillable());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getForm());
    }

    /**
     * Test of generateNewTopLevelElements method, of class
     * ParticleIntersectionGenerator without same default attributes.
     */
    @Test
    public void testGenerateNewTopLevelElementsWithoutSameDefaults() {
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();

        LinkedList<Element> elementsA = new LinkedList<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.setDefault("bla");
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);

        LinkedList<Element> elementsB = new LinkedList<Element>();
        Element elementB = new Element("{A}elementA");
        elementB.setDefault("bla2");
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        topLevelElementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, topLevelElementTypeMap, null, null, null);

        LinkedHashMap<String, LinkedHashSet<Element>> expResult = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        elements.add(elementA);
        elements.add(elementB);
        expResult.put("{}elementA", elements);
        LinkedHashMap<String, LinkedHashSet<Element>> result = instance.generateNewTopLevelElements(topLevelElementListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getType().isAnonymous());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getTypeAttr());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getDefault());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFixed());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getId());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAnnotation());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAbstract());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getNillable());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getForm());
    }

    /**
     * Test of generateNewTopLevelElements method, of class
     * ParticleIntersectionGenerator with same default attributes.
     */
    @Test
    public void testGenerateNewTopLevelElementsSameDefaults() {
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();

        LinkedList<Element> elementsA = new LinkedList<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.setDefault("bla");
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);

        LinkedList<Element> elementsB = new LinkedList<Element>();
        Element elementB = new Element("{A}elementA");
        elementB.setDefault("bla");
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        topLevelElementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, topLevelElementTypeMap, null, null, null);

        LinkedHashMap<String, LinkedHashSet<Element>> expResult = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        elements.add(elementA);
        elements.add(elementB);
        expResult.put("{}elementA", elements);
        LinkedHashMap<String, LinkedHashSet<Element>> result = instance.generateNewTopLevelElements(topLevelElementListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getType().isAnonymous());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getTypeAttr());
        assertEquals("bla", outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getDefault());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFixed());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getId());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAnnotation());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAbstract());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getNillable());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getForm());
    }

    /**
     * Test of generateNewTopLevelElements method, of class
     * ParticleIntersectionGenerator with default attribute.
     */
    @Test
    public void testGenerateNewTopLevelElementsDefault() {
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();

        LinkedList<Element> elementsA = new LinkedList<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.setDefault("bla");
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);

        LinkedList<Element> elementsB = new LinkedList<Element>();
        Element elementB = new Element("{A}elementA");
        elementB.setDefault("bla");
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        topLevelElementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, topLevelElementTypeMap, null, null, null);

        LinkedHashMap<String, LinkedHashSet<Element>> expResult = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        elements.add(elementA);
        elements.add(elementB);
        expResult.put("{}elementA", elements);
        LinkedHashMap<String, LinkedHashSet<Element>> result = instance.generateNewTopLevelElements(topLevelElementListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getType().isAnonymous());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getTypeAttr());
        assertEquals("bla", outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getDefault());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFixed());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getId());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAnnotation());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAbstract());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getNillable());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getForm());
    }

    /**
     * Test of generateNewTopLevelElements method, of class
     * ParticleIntersectionGenerator with true type attribute.
     */
    @Test
    public void testGenerateNewTopLevelElementsTypeAttribute() {
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();

        LinkedList<Element> elementsA = new LinkedList<Element>();
        Element elementA = new Element("{A}elementA");
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);

        LinkedList<Element> elementsB = new LinkedList<Element>();
        Element elementB = new Element("{A}elementA");
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.setIsAnonymous(true);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        topLevelElementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, topLevelElementTypeMap, null, null, null);

        LinkedHashMap<String, LinkedHashSet<Element>> expResult = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        elements.add(elementA);
        elements.add(elementB);
        expResult.put("{}elementA", elements);
        LinkedHashMap<String, LinkedHashSet<Element>> result = instance.generateNewTopLevelElements(topLevelElementListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(true, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getType().isAnonymous());
        assertEquals(true, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getTypeAttr());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getDefault());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFixed());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getId());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAnnotation());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAbstract());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getNillable());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getForm());
    }

    /**
     * Test of generateNewTopLevelElements method, of class
     * ParticleIntersectionGenerator without same fixed attributes.
     */
    @Test
    public void testGenerateNewTopLevelElementsWithoutSameFixed() {
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();

        LinkedList<Element> elementsA = new LinkedList<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.setFixed("bla");
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);

        LinkedList<Element> elementsB = new LinkedList<Element>();
        Element elementB = new Element("{A}elementA");
        elementB.setFixed("bla2");
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        topLevelElementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, topLevelElementTypeMap, null, null, null);

        LinkedHashMap<String, LinkedHashSet<Element>> expResult = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        elements.add(elementA);
        elements.add(elementB);
        expResult.put("{}elementA", elements);
        LinkedHashMap<String, LinkedHashSet<Element>> result = instance.generateNewTopLevelElements(topLevelElementListSet);

        assertEquals(expResult, result);
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
    }

    /**
     * Test of generateNewTopLevelElements method, of class
     * ParticleIntersectionGenerator with same fixed attributes.
     */
    @Test
    public void testGenerateNewTopLevelElementsSameFixed() {
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();

        LinkedList<Element> elementsA = new LinkedList<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.setFixed("bla");
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);

        LinkedList<Element> elementsB = new LinkedList<Element>();
        Element elementB = new Element("{A}elementA");
        elementB.setFixed("bla");
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        topLevelElementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, topLevelElementTypeMap, null, null, null);

        LinkedHashMap<String, LinkedHashSet<Element>> expResult = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        elements.add(elementA);
        elements.add(elementB);
        expResult.put("{}elementA", elements);
        LinkedHashMap<String, LinkedHashSet<Element>> result = instance.generateNewTopLevelElements(topLevelElementListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getType().isAnonymous());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getTypeAttr());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getDefault());
        assertEquals("bla", outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFixed());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getId());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAnnotation());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAbstract());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getNillable());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getForm());
    }

    /**
     * Test of generateNewTopLevelElements method, of class
     * ParticleIntersectionGenerator with fixed attribute.
     */
    @Test
    public void testGenerateNewTopLevelElementsFixed() {
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();

        LinkedList<Element> elementsA = new LinkedList<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.setFixed("bla");
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);

        LinkedList<Element> elementsB = new LinkedList<Element>();
        Element elementB = new Element("{A}elementA");
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        topLevelElementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, topLevelElementTypeMap, null, null, null);

        LinkedHashMap<String, LinkedHashSet<Element>> expResult = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        elements.add(elementA);
        elements.add(elementB);
        expResult.put("{}elementA", elements);
        LinkedHashMap<String, LinkedHashSet<Element>> result = instance.generateNewTopLevelElements(topLevelElementListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getType().isAnonymous());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getTypeAttr());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getDefault());
        assertEquals("bla", outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFixed());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getId());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAnnotation());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAbstract());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getNillable());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getForm());
    }

    /**
     * Test of generateNewTopLevelElements method, of class
     * ParticleIntersectionGenerator for an element set which blocks extension.
     */
    @Test
    public void testGenerateNewTopLevelElementsBlockExtension() {
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();

        LinkedList<Element> elementsA = new LinkedList<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.addBlockModifier(Element.Block.extension);
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);

        LinkedList<Element> elementsB = new LinkedList<Element>();
        Element elementB = new Element("{A}elementA");
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        topLevelElementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, topLevelElementTypeMap, null, null, null);

        LinkedHashMap<String, LinkedHashSet<Element>> expResult = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        elements.add(elementA);
        elements.add(elementB);
        expResult.put("{}elementA", elements);
        LinkedHashMap<String, LinkedHashSet<Element>> result = instance.generateNewTopLevelElements(topLevelElementListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getType().isAnonymous());
        HashSet<Block> block = new HashSet<Block>();
        block.add(Block.extension);
        assertEquals(block, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getBlockModifiers());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFinalModifiers());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getTypeAttr());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getDefault());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFixed());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getId());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAnnotation());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAbstract());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getNillable());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getForm());
    }

    /**
     * Test of generateNewTopLevelElements method, of class
     * ParticleIntersectionGenerator for an element set which blocks restriction.
     */
    @Test
    public void testGenerateNewTopLevelElementsBlockRestriction() {
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();

        LinkedList<Element> elementsA = new LinkedList<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.addBlockModifier(Element.Block.restriction);
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);

        LinkedList<Element> elementsB = new LinkedList<Element>();
        Element elementB = new Element("{A}elementA");
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        topLevelElementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, topLevelElementTypeMap, null, null, null);

        LinkedHashMap<String, LinkedHashSet<Element>> expResult = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        elements.add(elementA);
        elements.add(elementB);
        expResult.put("{}elementA", elements);
        LinkedHashMap<String, LinkedHashSet<Element>> result = instance.generateNewTopLevelElements(topLevelElementListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getType().isAnonymous());
        HashSet<Block> block = new HashSet<Block>();
        block.add(Block.restriction);
        assertEquals(block, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getBlockModifiers());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFinalModifiers());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getTypeAttr());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getDefault());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFixed());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getId());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAnnotation());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAbstract());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getNillable());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getForm());
    }

    /**
     * Test of generateNewTopLevelElements method, of class
     * ParticleIntersectionGenerator for an element set which blocks
     * substitution.
     */
    @Test
    public void testGenerateNewTopLevelElementsBlockSubstitution() {
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();

        LinkedList<Element> elementsA = new LinkedList<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.addBlockModifier(Element.Block.substitution);
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);

        LinkedList<Element> elementsB = new LinkedList<Element>();
        Element elementB = new Element("{A}elementA");
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        topLevelElementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, topLevelElementTypeMap, null, null, null);

        LinkedHashMap<String, LinkedHashSet<Element>> expResult = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        elements.add(elementA);
        elements.add(elementB);
        expResult.put("{}elementA", elements);
        LinkedHashMap<String, LinkedHashSet<Element>> result = instance.generateNewTopLevelElements(topLevelElementListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getType().isAnonymous());
        HashSet<Block> block = new HashSet<Block>();
        block.add(Block.substitution);
        assertEquals(block, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getBlockModifiers());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFinalModifiers());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getTypeAttr());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getDefault());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFixed());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getId());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAnnotation());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAbstract());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getNillable());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getForm());
    }

    /**
     * Test of generateNewTopLevelElements method, of class
     * ParticleIntersectionGenerator for an element set which blocks
     * extension per default.
     */
    @Test
    public void testGenerateNewTopLevelElementsBlockDefaultExtension() {
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();

        LinkedList<Element> elementsA = new LinkedList<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.addBlockModifier(Element.Block.restriction);
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);

        LinkedList<Element> elementsB = new LinkedList<Element>();
        Element elementB = new Element("{A}elementA");
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.extension);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        topLevelElementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, topLevelElementTypeMap, null, null, null);

        LinkedHashMap<String, LinkedHashSet<Element>> expResult = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        elements.add(elementA);
        elements.add(elementB);
        expResult.put("{}elementA", elements);
        LinkedHashMap<String, LinkedHashSet<Element>> result = instance.generateNewTopLevelElements(topLevelElementListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getType().isAnonymous());
        HashSet<Block> block = new HashSet<Block>();
        block.add(Block.restriction);
        block.add(Block.extension);
        assertEquals(block, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getBlockModifiers());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFinalModifiers());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getTypeAttr());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getDefault());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFixed());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getId());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAnnotation());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAbstract());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getNillable());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getForm());
    }

    /**
     * Test of generateNewTopLevelElements method, of class
     * ParticleIntersectionGenerator for an element set which blocks
     * restriction per default.
     */
    @Test
    public void testGenerateNewTopLevelElementsBlockDefaultRestriction() {
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();

        LinkedList<Element> elementsA = new LinkedList<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.addBlockModifier(Element.Block.extension);
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);

        LinkedList<Element> elementsB = new LinkedList<Element>();
        Element elementB = new Element("{A}elementA");
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.restriction);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        topLevelElementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, topLevelElementTypeMap, null, null, null);

        LinkedHashMap<String, LinkedHashSet<Element>> expResult = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        elements.add(elementA);
        elements.add(elementB);
        expResult.put("{}elementA", elements);
        LinkedHashMap<String, LinkedHashSet<Element>> result = instance.generateNewTopLevelElements(topLevelElementListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getType().isAnonymous());
        HashSet<Block> block = new HashSet<Block>();
        block.add(Block.restriction);
        block.add(Block.extension);
        assertEquals(block, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getBlockModifiers());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFinalModifiers());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getTypeAttr());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getDefault());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFixed());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getId());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAnnotation());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAbstract());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getNillable());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getForm());
    }

    /**
     * Test of generateNewTopLevelElements method, of class
     * ParticleIntersectionGenerator for an element set which blocks
     * substitution per default.
     */
    @Test
    public void testGenerateNewTopLevelElementsBlockDefaultSubstitution() {
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();

        LinkedList<Element> elementsA = new LinkedList<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.addBlockModifier(Element.Block.extension);
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);

        LinkedList<Element> elementsB = new LinkedList<Element>();
        Element elementB = new Element("{A}elementA");
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.substitution);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        topLevelElementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, topLevelElementTypeMap, null, null, null);

        LinkedHashMap<String, LinkedHashSet<Element>> expResult = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        elements.add(elementA);
        elements.add(elementB);
        expResult.put("{}elementA", elements);
        LinkedHashMap<String, LinkedHashSet<Element>> result = instance.generateNewTopLevelElements(topLevelElementListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getType().isAnonymous());
        HashSet<Block> block = new HashSet<Block>();
        block.add(Block.substitution);
        block.add(Block.extension);
        assertEquals(block, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getBlockModifiers());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFinalModifiers());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getTypeAttr());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getDefault());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFixed());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getId());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAnnotation());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAbstract());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getNillable());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getForm());
    }

    /**
     * Test of generateNewTopLevelElements method, of class
     * ParticleIntersectionGenerator for an element set which blocks
     * substitution which is schema default.
     */
    @Test
    public void testGenerateNewTopLevelElementsBlockDefault() {
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();

        LinkedList<Element> elementsA = new LinkedList<Element>();
        Element elementA = new Element("{A}elementA");
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);

        LinkedList<Element> elementsB = new LinkedList<Element>();
        Element elementB = new Element("{A}elementA");
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.substitution);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.addBlockDefault(XSDSchema.BlockDefault.substitution);
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        topLevelElementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, topLevelElementTypeMap, null, null, null);

        LinkedHashMap<String, LinkedHashSet<Element>> expResult = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        elements.add(elementA);
        elements.add(elementB);
        expResult.put("{}elementA", elements);
        LinkedHashMap<String, LinkedHashSet<Element>> result = instance.generateNewTopLevelElements(topLevelElementListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getType().isAnonymous());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getBlockModifiers());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFinalModifiers());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getTypeAttr());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getDefault());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFixed());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getId());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAnnotation());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAbstract());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getNillable());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getForm());
    }

    /**
     * Test of generateNewTopLevelElements method, of class
     * ParticleIntersectionGenerator for an element set which finalizes
     * extension.
     */
    @Test
    public void testGenerateNewTopLevelElementsFinalExtension() {
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();

        LinkedList<Element> elementsA = new LinkedList<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.addFinalModifier(Element.Final.extension);
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);

        LinkedList<Element> elementsB = new LinkedList<Element>();
        Element elementB = new Element("{A}elementA");
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        topLevelElementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, topLevelElementTypeMap, null, null, null);

        LinkedHashMap<String, LinkedHashSet<Element>> expResult = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        elements.add(elementA);
        elements.add(elementB);
        expResult.put("{}elementA", elements);
        LinkedHashMap<String, LinkedHashSet<Element>> result = instance.generateNewTopLevelElements(topLevelElementListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getType().isAnonymous());
        HashSet<Final> finalValue = new HashSet<Final>();
        finalValue.add(Final.extension);
        assertEquals(finalValue, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFinalModifiers());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getBlockModifiers());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getTypeAttr());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getDefault());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFixed());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getId());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAnnotation());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAbstract());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getNillable());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getForm());
    }

    /**
     * Test of generateNewTopLevelElements method, of class
     * ParticleIntersectionGenerator for an element set which finalizes
     * restriction.
     */
    @Test
    public void testGenerateNewTopLevelElementsFinalRestriction() {
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();

        LinkedList<Element> elementsA = new LinkedList<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.addFinalModifier(Element.Final.restriction);
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);

        LinkedList<Element> elementsB = new LinkedList<Element>();
        Element elementB = new Element("{A}elementA");
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        topLevelElementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, topLevelElementTypeMap, null, null, null);

        LinkedHashMap<String, LinkedHashSet<Element>> expResult = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        elements.add(elementA);
        elements.add(elementB);
        expResult.put("{}elementA", elements);
        LinkedHashMap<String, LinkedHashSet<Element>> result = instance.generateNewTopLevelElements(topLevelElementListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getType().isAnonymous());
        HashSet<Final> finalValue = new HashSet<Final>();
        finalValue.add(Final.restriction);
        assertEquals(finalValue, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFinalModifiers());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getBlockModifiers());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getTypeAttr());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getDefault());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFixed());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getId());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAnnotation());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAbstract());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getNillable());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getForm());
    }

    /**
     * Test of generateNewTopLevelElements method, of class
     * ParticleIntersectionGenerator for an element set which finalizes
     * extension per default.
     */
    @Test
    public void testGenerateNewTopLevelElementsFinalDefaultExtension() {
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();

        LinkedList<Element> elementsA = new LinkedList<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.addFinalModifier(Element.Final.restriction);
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);

        LinkedList<Element> elementsB = new LinkedList<Element>();
        Element elementB = new Element("{A}elementA");
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.extension);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        topLevelElementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, topLevelElementTypeMap, null, null, null);

        LinkedHashMap<String, LinkedHashSet<Element>> expResult = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        elements.add(elementA);
        elements.add(elementB);
        expResult.put("{}elementA", elements);
        LinkedHashMap<String, LinkedHashSet<Element>> result = instance.generateNewTopLevelElements(topLevelElementListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getType().isAnonymous());
        HashSet<Final> finalValue = new HashSet<Final>();
        finalValue.add(Final.restriction);
        finalValue.add(Final.extension);
        assertEquals(finalValue, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFinalModifiers());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getBlockModifiers());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getTypeAttr());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getDefault());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFixed());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getId());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAnnotation());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAbstract());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getNillable());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getForm());
    }

    /**
     * Test of generateNewTopLevelElements method, of class
     * ParticleIntersectionGenerator for an element set which finalizes
     * restriction per default.
     */
    @Test
    public void testGenerateNewTopLevelElementsFinalDefaultRestriction() {
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();

        LinkedList<Element> elementsA = new LinkedList<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.addFinalModifier(Element.Final.extension);
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);

        LinkedList<Element> elementsB = new LinkedList<Element>();
        Element elementB = new Element("{A}elementA");
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        topLevelElementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, topLevelElementTypeMap, null, null, null);

        LinkedHashMap<String, LinkedHashSet<Element>> expResult = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        elements.add(elementA);
        elements.add(elementB);
        expResult.put("{}elementA", elements);
        LinkedHashMap<String, LinkedHashSet<Element>> result = instance.generateNewTopLevelElements(topLevelElementListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getType().isAnonymous());
        HashSet<Final> finalValue = new HashSet<Final>();
        finalValue.add(Final.restriction);
        finalValue.add(Final.extension);
        assertEquals(finalValue, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFinalModifiers());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getBlockModifiers());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getTypeAttr());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getDefault());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFixed());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getId());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAnnotation());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAbstract());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getNillable());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getForm());
    }

    /**
     * Test of generateNewTopLevelElements method, of class
     * ParticleIntersectionGenerator for an element set which finalizes
     * extension which is schema default.
     */
    @Test
    public void testGenerateNewTopLevelElementsFinalDefault() {
        LinkedHashSet<LinkedList<Element>> topLevelElementListSet = new LinkedHashSet<LinkedList<Element>>();

        LinkedList<Element> elementsA = new LinkedList<Element>();
        Element elementA = new Element("{A}elementA");
        elementsA.add(elementA);
        topLevelElementListSet.add(elementsA);

        LinkedList<Element> elementsB = new LinkedList<Element>();
        Element elementB = new Element("{A}elementA");
        elementsB.add(elementB);
        topLevelElementListSet.add(elementsB);

        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.extension);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.addFinalDefault(XSDSchema.FinalDefault.extension);
        LinkedHashMap<String, SymbolTableRef<Type>> topLevelElementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        topLevelElementTypeMap.put("{}elementA", typeRefA);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, topLevelElementTypeMap, null, null, null);

        LinkedHashMap<String, LinkedHashSet<Element>> expResult = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        elements.add(elementA);
        elements.add(elementB);
        expResult.put("{}elementA", elements);
        LinkedHashMap<String, LinkedHashSet<Element>> result = instance.generateNewTopLevelElements(topLevelElementListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getType().isAnonymous());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFinalModifiers());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getBlockModifiers());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getTypeAttr());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getDefault());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFixed());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getId());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAnnotation());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getAbstract());
        assertEquals(false, (boolean) outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getNillable());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getForm());
    }

    /**
     * Test of generateParticleIntersection method, of class
     * ParticleIntersectionGenerator.
     */
    @Test
    public void testGenerateParticleIntersection() throws Exception {
        Element elementA = new Element("{A}elementA");
        Element elementB = new Element("{A}elementB");
        Element elementC = new Element("{A}elementC");
        ChoicePattern choicePattern = new ChoicePattern();
        choicePattern.addParticle(elementA);
        choicePattern.addParticle(elementB);
        SequencePattern sequencePattern = new SequencePattern();
        sequencePattern.addParticle(choicePattern);
        sequencePattern.addParticle(elementC);
        LinkedList<Particle> particles = new LinkedList<Particle>();
        particles.add(sequencePattern);
        particles.add(sequencePattern);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{}elementA", typeRefA);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        SymbolTableRef<Type> typeRefB = new SymbolTableRef<Type>("{A}simpleTypeB", simpleTypeB);
        elementTypeMap.put("{}elementB", typeRefB);
        SimpleType simpleTypeC = new SimpleType("{A}simpleTypeC", null);
        SymbolTableRef<Type> typeRefC = new SymbolTableRef<Type>("{A}simpleTypeC", simpleTypeC);
        elementTypeMap.put("{}elementC", typeRefC);
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        elementOldSchemaMap.put(elementC, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null);

        Particle result = instance.generateParticleIntersection(particles, elementTypeMap);

        assertTrue(result instanceof SequencePattern);
        assertTrue(((SequencePattern) result).getParticles().size() == 2);
        assertTrue(((SequencePattern) result).getParticles().getFirst() instanceof ChoicePattern);
        ChoicePattern newChoicePattern = (ChoicePattern) ((SequencePattern) result).getParticles().getFirst();
        assertTrue(newChoicePattern.getParticles().size() == 2);
        assertTrue(newChoicePattern.getParticles().getFirst() instanceof Element);
        Element newElementB = (Element) newChoicePattern.getParticles().getFirst();
        assertEquals(elementB.getName(), newElementB.getName());
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementB"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementB").getReference()));
        assertEquals(false, newElementB.getType().isAnonymous());
        assertEquals(null, newElementB.getFinalModifiers());
        assertEquals(null, newElementB.getBlockModifiers());
        assertEquals(false, (boolean) newElementB.getTypeAttr());
        assertEquals(null, newElementB.getDefault());
        assertEquals(null, newElementB.getFixed());
        assertEquals(null, newElementB.getId());
        assertEquals(null, newElementB.getAnnotation());
        assertEquals(false, (boolean) newElementB.getAbstract());
        assertEquals(false, (boolean) newElementB.getNillable());
        assertEquals(null, newElementB.getForm());
        assertTrue(newChoicePattern.getParticles().getLast() instanceof Element);
        Element newElementA = (Element) newChoicePattern.getParticles().getLast();
        assertEquals(elementA.getName(), newElementA.getName());
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, newElementA.getType().isAnonymous());
        assertEquals(null, newElementA.getFinalModifiers());
        assertEquals(null, newElementA.getBlockModifiers());
        assertEquals(false, (boolean) newElementA.getTypeAttr());
        assertEquals(null, newElementA.getDefault());
        assertEquals(null, newElementA.getFixed());
        assertEquals(null, newElementA.getId());
        assertEquals(null, newElementA.getAnnotation());
        assertEquals(false, (boolean) newElementA.getAbstract());
        assertEquals(false, (boolean) newElementA.getNillable());
        assertEquals(null, newElementA.getForm());
        assertTrue(((SequencePattern) result).getParticles().getLast() instanceof Element);
        Element newElementC = (Element) ((SequencePattern) result).getParticles().getLast();
        assertEquals(elementC.getName(), newElementC.getName());
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementC"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementC").getReference()));
        assertEquals(false, newElementC.getType().isAnonymous());
        assertEquals(null, newElementC.getFinalModifiers());
        assertEquals(null, newElementC.getBlockModifiers());
        assertEquals(false, (boolean) newElementC.getTypeAttr());
        assertEquals(null, newElementC.getDefault());
        assertEquals(null, newElementC.getFixed());
        assertEquals(null, newElementC.getId());
        assertEquals(null, newElementC.getAnnotation());
        assertEquals(false, (boolean) newElementC.getAbstract());
        assertEquals(false, (boolean) newElementC.getNillable());
        assertEquals(null, newElementC.getForm());
    }

    /**
     * Test of generateParticleIntersection method, of class
     * ParticleIntersectionGenerator with element replacement
     */
    @Test
    public void testGenerateParticleIntersectionReplaceB() throws Exception {
        Element elementA = new Element("{A}elementA");
        Element elementB = new Element("{B}elementB");
        elementB.setForm(XSDSchema.Qualification.qualified);
        Element elementC = new Element("{A}elementC");
        ChoicePattern choicePattern = new ChoicePattern();
        choicePattern.addParticle(elementA);
        choicePattern.addParticle(elementB);
        SequencePattern sequencePattern = new SequencePattern();
        sequencePattern.addParticle(choicePattern);
        sequencePattern.addParticle(elementC);
        LinkedList<Particle> particles = new LinkedList<Particle>();
        particles.add(sequencePattern);
        particles.add(sequencePattern);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRefA = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{}elementA", typeRefA);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        SymbolTableRef<Type> typeRefB = new SymbolTableRef<Type>("{A}simpleTypeB", simpleTypeB);
        elementTypeMap.put("{B}elementB", typeRefB);
        SimpleType simpleTypeC = new SimpleType("{A}simpleTypeC", null);
        SymbolTableRef<Type> typeRefC = new SymbolTableRef<Type>("{A}simpleTypeC", simpleTypeC);
        elementTypeMap.put("{}elementC", typeRefC);
        XSDSchema oldSchema = new XSDSchema("A");
        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.setSchemaLocation("Main");
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        LinkedHashSet<XSDSchema> otherSchemata = new LinkedHashSet<XSDSchema>();
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, new XSDSchema("B"));
        elementOldSchemaMap.put(elementC, oldSchema);
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, elementOldSchemaMap, null, null, otherSchemata, namespaceAbbreviationMap, "C:/");

        Particle result = instance.generateParticleIntersection(particles, elementTypeMap);

        assertTrue(result instanceof SequencePattern);
        assertTrue(((SequencePattern) result).getParticles().size() == 2);
        assertTrue(((SequencePattern) result).getParticles().getFirst() instanceof ChoicePattern);
        ChoicePattern newChoicePattern = (ChoicePattern) ((SequencePattern) result).getParticles().getFirst();
        assertTrue(newChoicePattern.getParticles().size() == 2);
        assertTrue(newChoicePattern.getParticles().getFirst() instanceof GroupRef);
        GroupRef groupRef = (GroupRef) newChoicePattern.getParticles().getFirst();
        assertTrue(otherSchemata.size() == 1);       
        assertTrue(otherSchemata.iterator().next().getGroups().contains((groupRef).getGroup()));
        assertTrue(otherSchemata.iterator().next().getGroupSymbolTable().hasReference((groupRef).getGroup().getName()));
        assertEquals(null, ((Group) (groupRef).getGroup()).getId());
        assertEquals(null, ((Group) (groupRef).getGroup()).getAnnotation());
        assertTrue(((Group) groupRef.getGroup()).getParticleContainer().getParticles().size() == 1);
        Particle resultingElement = ((Group) groupRef.getGroup()).getParticleContainer().getParticles().getFirst();
        assertEquals("{A}simpleTypeB", ((Element) resultingElement).getType().getName());
        assertEquals(false, ((Element) resultingElement).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) resultingElement).getTypeAttr());
        assertEquals(elementA.getDefault(), ((Element) resultingElement).getDefault());
        assertEquals(elementA.getFixed(), ((Element) resultingElement).getFixed());
        assertEquals(null, ((Element) resultingElement).getId());
        assertEquals(null, ((Element) resultingElement).getAnnotation());
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Element) resultingElement).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getElementSymbolTable().getReference(((Element) resultingElement).getName()).getReference()));
        assertEquals(XSDSchema.Qualification.qualified, ((Element) resultingElement).getForm());
        assertTrue(newChoicePattern.getParticles().getLast() instanceof Element);
        Element newElementA = (Element) newChoicePattern.getParticles().getLast();
        assertEquals(elementA.getName(), newElementA.getName());
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, newElementA.getType().isAnonymous());
        assertEquals(null, newElementA.getFinalModifiers());
        assertEquals(null, newElementA.getBlockModifiers());
        assertEquals(false, (boolean) newElementA.getTypeAttr());
        assertEquals(null, newElementA.getDefault());
        assertEquals(null, newElementA.getFixed());
        assertEquals(null, newElementA.getId());
        assertEquals(null, newElementA.getAnnotation());
        assertEquals(false, (boolean) newElementA.getAbstract());
        assertEquals(false, (boolean) newElementA.getNillable());
        assertEquals(null, newElementA.getForm());
        assertTrue(((SequencePattern) result).getParticles().getLast() instanceof Element);
        Element newElementC = (Element) ((SequencePattern) result).getParticles().getLast();
        assertEquals(elementC.getName(), newElementC.getName());
        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementC"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementC").getReference()));
        assertEquals(false, newElementC.getType().isAnonymous());
        assertEquals(null, newElementC.getFinalModifiers());
        assertEquals(null, newElementC.getBlockModifiers());
        assertEquals(false, (boolean) newElementC.getTypeAttr());
        assertEquals(null, newElementC.getDefault());
        assertEquals(null, newElementC.getFixed());
        assertEquals(null, newElementC.getId());
        assertEquals(null, newElementC.getAnnotation());
        assertEquals(false, (boolean) newElementC.getAbstract());
        assertEquals(false, (boolean) newElementC.getNillable());
        assertEquals(null, newElementC.getForm());
    }

    /**
     * Test of getNamespace method, of class ParticleIntersectionGenerator for
     * a single any pattern with namespace="##any".
     */
    @Test
    public void testGetNamespaceSingleAny() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##any");
        anyPatterns.add(anyPatternA);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null);

        String expResult = "##any";
        String result = instance.getNamespace(anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of getNamespace method, of class ParticleIntersectionGenerator for
     * a single any pattern with namespace="##other".
     */
    @Test
    public void testGetNamespaceSingleOther() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##other");
        anyPatterns.add(anyPatternA);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null);

        String expResult = "##other";
        String result = instance.getNamespace(anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of getNamespace method, of class ParticleIntersectionGenerator for
     * a single any pattern with namespace="##local".
     */
    @Test
    public void testGetNamespaceSingleLocal() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##local");
        anyPatterns.add(anyPatternA);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null);

        String expResult = "##local";
        String result = instance.getNamespace(anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of getNamespace method, of class ParticleIntersectionGenerator for
     * a single any pattern with namespace="##targetNamespace".
     */
    @Test
    public void testGetNamespaceSingleTargetNamespace() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##targetNamespace");
        anyPatterns.add(anyPatternA);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null);

        String expResult = "##targetNamespace";
        String result = instance.getNamespace(anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of getNamespace method, of class ParticleIntersectionGenerator for
     * a single any pattern with namespace="A".
     */
    @Test
    public void testGetNamespaceSingleUriA() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "A");
        anyPatterns.add(anyPatternA);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null);

        String expResult = "##targetNamespace";
        String result = instance.getNamespace(anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of getNamespace method, of class ParticleIntersectionGenerator for
     * a single any pattern with namespace="B".
     */
    @Test
    public void testGetNamespaceSingleUriB() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "B");
        anyPatterns.add(anyPatternA);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null);

        String expResult = "B";
        String result = instance.getNamespace(anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of getNamespace method, of class ParticleIntersectionGenerator for
     * a single any pattern with namespace="##other".
     */
    @Test
    public void testGetNamespaceSingleForeignOther() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##other");
        anyPatterns.add(anyPatternA);

        XSDSchema oldSchema = new XSDSchema("B");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null);

        String expResult = "##any";
        String result = instance.getNamespace(anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of getNamespace method, of class ParticleIntersectionGenerator for
     * a any patterns with namespace="##other" and "##local".
     */
    @Test
    public void testGetNamespaceOtherLocal() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##other");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "##local");
        anyPatterns.add(anyPatternA);
        anyPatterns.add(anyPatternB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        anyPatternOldSchemaMap.put(anyPatternB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null);

        String expResult = "";
        String result = instance.getNamespace(anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of getNamespace method, of class ParticleIntersectionGenerator for
     * a any patterns with namespace="##other" and "##targetNamespace".
     */
    @Test
    public void testGetNamespaceOtherTargetNamespace() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##other");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "##targetNamespace");
        anyPatterns.add(anyPatternA);
        anyPatterns.add(anyPatternB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        anyPatternOldSchemaMap.put(anyPatternB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null);

        String expResult = "";
        String result = instance.getNamespace(anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of getNamespace method, of class ParticleIntersectionGenerator for
     * a any patterns with namespace="##other" and "##targetNamespace".
     */
    @Test
    public void testGetNamespaceForeignOtherTargetNamespace() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##other");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "##targetNamespace");
        anyPatterns.add(anyPatternA);
        anyPatterns.add(anyPatternB);

        XSDSchema oldSchemaA = new XSDSchema("A");
        XSDSchema oldSchemaB = new XSDSchema("B");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchemaB);
        anyPatternOldSchemaMap.put(anyPatternB, oldSchemaA);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null);

        String expResult = "##targetNamespace";
        String result = instance.getNamespace(anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of getNamespace method, of class ParticleIntersectionGenerator for
     * a any patterns with namespace="A" and "A B".
     */
    @Test
    public void testGetNamespaceUriAUriAB() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "A");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "A B");
        anyPatterns.add(anyPatternA);
        anyPatterns.add(anyPatternB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        anyPatternOldSchemaMap.put(anyPatternB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null);

        String expResult = "##targetNamespace";
        String result = instance.getNamespace(anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of getNamespace method, of class ParticleIntersectionGenerator for
     * a any patterns with namespace="##any" and "A B".
     */
    @Test
    public void testGetNamespaceAnyUriAB() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##any");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "A B");
        anyPatterns.add(anyPatternA);
        anyPatterns.add(anyPatternB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        anyPatternOldSchemaMap.put(anyPatternB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null);

        String expResult = "##targetNamespace B";
        String result = instance.getNamespace(anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of getNamespace method, of class ParticleIntersectionGenerator for
     * a any patterns with namespace="targetNamespace" and "A B".
     */
    @Test
    public void testGetNamespaceTargetNamespaceUriAB() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##targetNamespace");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "A B");
        anyPatterns.add(anyPatternA);
        anyPatterns.add(anyPatternB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        anyPatternOldSchemaMap.put(anyPatternB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null);

        String expResult = "##targetNamespace";
        String result = instance.getNamespace(anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of isIncluded method, of class ParticleIntersectionGenerator for
     * skip validated any pattern with namespace "##any".
     */
    @Test
    public void testIsIncludedAnyTrue() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##any");
        anyPatterns.add(anyPatternA);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null);

        String elementName = "{B}foo";

        boolean expResult = true;
        boolean result = instance.isIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of isIncluded method, of class ParticleIntersectionGenerator for
     * skip validated any pattern with namespace "##other".
     */
    @Test
    public void testIsIncludedOtherTrue() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##other");
        anyPatterns.add(anyPatternA);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null);

        String elementName = "{B}foo";

        boolean expResult = true;
        boolean result = instance.isIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of isIncluded method, of class ParticleIntersectionGenerator for
     * skip validated any pattern with namespace "##local".
     */
    @Test
    public void testIsIncludedLocalTrue() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##local");
        anyPatterns.add(anyPatternA);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null);

        String elementName = "{}foo";

        boolean expResult = true;
        boolean result = instance.isIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of isIncluded method, of class ParticleIntersectionGenerator for
     * skip validated any pattern with namespace "##targetNamespace".
     */
    @Test
    public void testIsIncludedTargetNamespaceTrue() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##targetNamespace");
        anyPatterns.add(anyPatternA);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null);

        String elementName = "{A}foo";

        boolean expResult = true;
        boolean result = instance.isIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of isIncluded method, of class ParticleIntersectionGenerator for
     * skip validated any pattern with namespace "A B".
     */
    @Test
    public void testIsIncludedUrisTrue() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "A B");
        anyPatterns.add(anyPatternA);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null);

        String elementName = "{A}foo";

        boolean expResult = true;
        boolean result = instance.isIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of isIncluded method, of class ParticleIntersectionGenerator for
     * skip validated any pattern with namespace "##other".
     */
    @Test
    public void testIsIncludedOtherFalse() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##other");
        anyPatterns.add(anyPatternA);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null);

        String elementName = "{A}foo";

        boolean expResult = false;
        boolean result = instance.isIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of isIncluded method, of class ParticleIntersectionGenerator for
     * skip validated any pattern with namespace "##local".
     */
    @Test
    public void testIsIncludedLocalFalse() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##local");
        anyPatterns.add(anyPatternA);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null);

        String elementName = "{A}foo";

        boolean expResult = false;
        boolean result = instance.isIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of isIncluded method, of class ParticleIntersectionGenerator for
     * skip validated any pattern with namespace "##targetNamespace".
     */
    @Test
    public void testIsIncludedTargetNamespaceFalse() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##targetNamespace");
        anyPatterns.add(anyPatternA);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null);

        String elementName = "{B}foo";

        boolean expResult = false;
        boolean result = instance.isIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of isIncluded method, of class ParticleIntersectionGenerator for
     * skip validated any pattern with namespace "A B".
     */
    @Test
    public void testIsIncludedUrisFalse() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "A B");
        anyPatterns.add(anyPatternA);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null);

        String elementName = "{C}foo";

        boolean expResult = false;
        boolean result = instance.isIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of isOptional method, of class ParticleIntersectionGenerator.
     */
    @Test
    public void testIsOptionalTrue() throws Exception {
        SequencePattern sequencePattern = new SequencePattern();
        ChoicePattern choicePattern = new ChoicePattern();
        Element elementA = new Element("{A}elementA");
        SequencePattern sequencePatternInChoice = new SequencePattern();
        choicePattern.addParticle(elementA);
        choicePattern.addParticle(sequencePatternInChoice);
        CountingPattern countingPattern = new CountingPattern(0, null);
        Element elementB = new Element("{A}elementA");
        countingPattern.addParticle(elementB);
        sequencePattern.addParticle(choicePattern);
        sequencePattern.addParticle(countingPattern);

        Particle particle = sequencePattern;
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);

        boolean expResult = true;
        boolean result = instance.isOptional(particle);

        assertEquals(expResult, result);
    }

    /**
     * Test of isOptional method, of class ParticleIntersectionGenerator.
     */
    @Test
    public void testIsOptionalFalse() throws Exception {
        SequencePattern sequencePattern = new SequencePattern();
        ChoicePattern choicePattern = new ChoicePattern();
        Element elementA = new Element("{A}elementA");
        SequencePattern sequencePatternInChoice = new SequencePattern();
        choicePattern.addParticle(elementA);
        choicePattern.addParticle(sequencePatternInChoice);
        CountingPattern countingPattern = new CountingPattern(1, null);
        Element elementB = new Element("{A}elementA");
        countingPattern.addParticle(elementB);
        sequencePattern.addParticle(choicePattern);
        sequencePattern.addParticle(countingPattern);

        Particle particle = sequencePattern;
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleIntersectionGenerator instance = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);

        boolean expResult = false;
        boolean result = instance.isOptional(particle);

        assertEquals(expResult, result);
    }
}