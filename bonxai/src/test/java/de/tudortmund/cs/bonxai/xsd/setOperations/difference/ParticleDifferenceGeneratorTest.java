package de.tudortmund.cs.bonxai.xsd.setOperations.difference;

import de.tudortmund.cs.bonxai.common.AllPattern;
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
import de.tudortmund.cs.bonxai.xsd.Element.Final;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>ParticleDifferenceGenerator</tt> class, checks that
 * every method of this class performs properly.
 * @author Dominik Wolff
 */
public class ParticleDifferenceGeneratorTest extends junit.framework.TestCase {

    public ParticleDifferenceGeneratorTest() {
    }

    /**
     * Test of getAnyPatternOldSchemaMap method, of class ParticleDifferenceGenerator.
     */
    @Test
    public void testGetAnyPatternOldSchemaMap() {
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##any");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "##other");

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        anyPatternOldSchemaMap.put(anyPatternB, oldSchema);
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        LinkedHashMap<AnyPattern, XSDSchema> expResult = anyPatternOldSchemaMap;
        LinkedHashMap<AnyPattern, XSDSchema> result = instance.getAnyPatternOldSchemaMap();

        assertEquals(expResult, result);
    }

    /**
     * Test of setTypeDifferenceGenerator method, of class ParticleDifferenceGenerator.
     */
    @Test
    public void testSetTypeDifferenceGenerator() {
        Element elementA = new Element("{A}elementA");
        Particle oldParticle = elementA;
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of getElementOldTypeMap method, of class ParticleDifferenceGenerator.
     */
    @Test
    public void testGetElementOldTypeMap() {
        Element elementA = new Element("{A}elementA");
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        HashMap<String, SymbolTableRef<Type>> expResult = elementTypeMap;
        HashMap<String, SymbolTableRef<Type>> result = instance.getElementOldTypeMap();

        assertEquals(expResult, result);
    }

    /**
     * Test of getIncluded method, of class ParticleDifferenceGenerator for
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
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));

        String elementName = "{A}elementA";

        Element expResult = null;
        Element result = instance.getIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of getIncluded method, of class ParticleDifferenceGenerator for
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
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));

        String elementName = "{A}elementA";

        Element expResult = elementA;
        Element result = instance.getIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of getIncluded method, of class ParticleDifferenceGenerator for
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
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));

        String elementName = "{A}elementA";

        Element expResult = elementA;
        Element result = instance.getIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of getIncluded method, of class ParticleDifferenceGenerator for
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
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));

        String elementName = "{B}elementB";

        Element expResult = elementB;
        Element result = instance.getIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of getIncluded method, of class ParticleDifferenceGenerator for
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
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));

        String elementName = "{A}elementA";

        Element expResult = elementA;
        Element result = instance.getIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of getIncluded method, of class ParticleDifferenceGenerator for
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
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));

        String elementName = "{}elementB";

        Element expResult = elementB;
        Element result = instance.getIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of getIncluded method, of class ParticleDifferenceGenerator for
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
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));

        String elementName = "{}elementB";

        Element expResult = elementB;
        Element result = instance.getIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of generateNewParticleDifference method, of class
     * ParticleDifferenceGenerator.
     */
    @Test
    public void testGenerateNewParticleDifferenceEmptyDifferenceResult() throws Exception {
        Element elementA = new Element("{A}elementA");
        Element elementB = new Element("{A}elementB");
        Element elementC = new Element("{A}elementC");
        ChoicePattern choicePattern = new ChoicePattern();
        choicePattern.addParticle(elementA);
        choicePattern.addParticle(elementB);
        SequencePattern sequencePattern = new SequencePattern();
        sequencePattern.addParticle(choicePattern);
        sequencePattern.addParticle(elementC);

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
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, null);

        Particle result = instance.generateNewParticleDifference(sequencePattern, sequencePattern, elementTypeMap, elementTypeMap);

        assertTrue(result instanceof ChoicePattern);
        assertTrue(((ChoicePattern) result).getParticles().size() == 1);
        assertTrue(((ChoicePattern) result).getParticles().getFirst() instanceof ChoicePattern);
        assertTrue(((ChoicePattern) ((ChoicePattern) result).getParticles().getFirst()).getParticles().size() == 2);
        assertTrue(((ChoicePattern) ((ChoicePattern) result).getParticles().getFirst()).getParticles().getFirst() instanceof SequencePattern);
        assertTrue(((ChoicePattern) ((ChoicePattern) result).getParticles().getFirst()).getParticles().getLast() instanceof SequencePattern);
        SequencePattern sequencePattern1 = (SequencePattern) ((ChoicePattern) ((ChoicePattern) result).getParticles().getFirst()).getParticles().getFirst();
        SequencePattern sequencePattern2 = (SequencePattern) ((ChoicePattern) ((ChoicePattern) result).getParticles().getFirst()).getParticles().getLast();
        assertTrue(sequencePattern1.getParticles().size() == 2);
        assertTrue(sequencePattern1.getParticles().getFirst() instanceof ChoicePattern);
        ChoicePattern newChoicePattern = (ChoicePattern) sequencePattern1.getParticles().getFirst();
        assertTrue(newChoicePattern.getParticles().size() == 2);
        assertTrue(newChoicePattern.getParticles().getFirst() instanceof Element);
        Element newElementB = (Element) newChoicePattern.getParticles().getFirst();
        assertEquals(elementB.getName(), newElementB.getName());
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
        assertTrue(sequencePattern1.getParticles().getLast() instanceof Element);
        Element newElementC = (Element) sequencePattern1.getParticles().getLast();
        assertEquals(elementC.getName(), newElementC.getName());
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
        assertTrue(sequencePattern2.getParticles().size() == 2);
        assertTrue(sequencePattern2.getParticles().getFirst() instanceof ChoicePattern);
        ChoicePattern newChoicePattern2 = (ChoicePattern) sequencePattern2.getParticles().getFirst();
        assertTrue(newChoicePattern2.getParticles().size() == 2);
        assertTrue(newChoicePattern2.getParticles().getFirst() instanceof Element);
        Element newElementB2 = (Element) newChoicePattern2.getParticles().getLast();
        assertEquals(elementB.getName(), newElementB2.getName());
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementB").getReference()));
        assertEquals(false, newElementB2.getType().isAnonymous());
        assertEquals(null, newElementB2.getFinalModifiers());
        assertEquals(null, newElementB2.getBlockModifiers());
        assertEquals(false, (boolean) newElementB2.getTypeAttr());
        assertEquals(null, newElementB2.getDefault());
        assertEquals(null, newElementB2.getFixed());
        assertEquals(null, newElementB2.getId());
        assertEquals(null, newElementB2.getAnnotation());
        assertEquals(false, (boolean) newElementB2.getAbstract());
        assertEquals(false, (boolean) newElementB2.getNillable());
        assertEquals(null, newElementB2.getForm());
        assertTrue(newChoicePattern2.getParticles().getLast() instanceof Element);
        Element newElementA2 = (Element) newChoicePattern2.getParticles().getFirst();
        assertEquals(elementA.getName(), newElementA2.getName());
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, newElementA2.getType().isAnonymous());
        assertEquals(null, newElementA2.getFinalModifiers());
        assertEquals(null, newElementA2.getBlockModifiers());
        assertEquals(false, (boolean) newElementA2.getTypeAttr());
        assertEquals(null, newElementA2.getDefault());
        assertEquals(null, newElementA2.getFixed());
        assertEquals(null, newElementA2.getId());
        assertEquals(null, newElementA2.getAnnotation());
        assertEquals(false, (boolean) newElementA2.getAbstract());
        assertEquals(false, (boolean) newElementA2.getNillable());
        assertEquals(null, newElementA2.getForm());
        assertTrue(sequencePattern2.getParticles().getLast() instanceof Element);
        Element newElementC2 = (Element) sequencePattern2.getParticles().getLast();
        assertEquals(elementC.getName(), newElementC2.getName());
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementC").getReference()));
        assertEquals(false, newElementC2.getType().isAnonymous());
        assertEquals(null, newElementC2.getFinalModifiers());
        assertEquals(null, newElementC2.getBlockModifiers());
        assertEquals(false, (boolean) newElementC2.getTypeAttr());
        assertEquals(null, newElementC2.getDefault());
        assertEquals(null, newElementC2.getFixed());
        assertEquals(null, newElementC2.getId());
        assertEquals(null, newElementC2.getAnnotation());
        assertEquals(false, (boolean) newElementC2.getAbstract());
        assertEquals(false, (boolean) newElementC2.getNillable());
        assertEquals(null, newElementC2.getForm());
    }

    /**
     * Test of generateNewParticleDifference method, of class
     * ParticleDifferenceGenerator.
     */
    @Test
    public void testGenerateNewParticleDifferenceEmptySubrahend() throws Exception {
        Element elementA = new Element("{A}elementA");
        Element elementB = new Element("{A}elementB");
        Element elementC = new Element("{A}elementC");
        ChoicePattern choicePattern = new ChoicePattern();
        choicePattern.addParticle(elementA);
        choicePattern.addParticle(elementB);
        SequencePattern sequencePattern = new SequencePattern();
        sequencePattern.addParticle(choicePattern);
        sequencePattern.addParticle(elementC);

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
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, null);

        Particle result = instance.generateNewParticleDifference(sequencePattern, null, elementTypeMap, elementTypeMap);

        assertTrue(result instanceof SequencePattern);
        assertTrue(((SequencePattern) result).getParticles().size() == 2);
        assertTrue(((SequencePattern) result).getParticles().getFirst() instanceof ChoicePattern);
        ChoicePattern newChoicePattern = (ChoicePattern) ((SequencePattern) result).getParticles().getFirst();
        assertTrue(newChoicePattern.getParticles().size() == 2);
        assertTrue(newChoicePattern.getParticles().getFirst() instanceof Element);
        Element newElementA = (Element) newChoicePattern.getParticles().getFirst();
        assertEquals(elementA.getName(), newElementA.getName());
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
        assertTrue(newChoicePattern.getParticles().getLast() instanceof Element);
        Element newElementB = (Element) newChoicePattern.getParticles().getLast();
        assertEquals(elementB.getName(), newElementB.getName());
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
        assertTrue(((SequencePattern) result).getParticles().getLast() instanceof Element);
        Element newElementC = (Element) ((SequencePattern) result).getParticles().getLast();
        assertEquals(elementC.getName(), newElementC.getName());
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
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * an any pattern.
     */
    @Test
    public void testGenerateNewParticleAnyPattern() {
        AnyPattern oldAnyPattern = new AnyPattern(ProcessContentsInstruction.Strict, "##any");
        Particle oldParticle = oldAnyPattern;

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
        anyPatternOldSchemaMap.put(oldAnyPattern, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof ChoicePattern);
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(((ChoicePattern) result).getParticles().size() == 1);
        assertTrue(((ChoicePattern) result).getParticles().getFirst() instanceof Element);
        Particle resultingElement = (Element) ((ChoicePattern) result).getParticles().getFirst();
        assertEquals("{A}simpleTypeA", ((Element) resultingElement).getType().getName());
        assertEquals(false, ((Element) resultingElement).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) resultingElement).getTypeAttr());
        assertEquals(null, ((Element) resultingElement).getDefault());
        assertEquals(null, ((Element) resultingElement).getFixed());
        assertEquals(null, ((Element) resultingElement).getId());
        assertEquals(null, ((Element) resultingElement).getAnnotation());
        assertEquals(false, (boolean) ((Element) resultingElement).getAbstract());
        assertEquals(false, (boolean) ((Element) resultingElement).getNillable());
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) resultingElement).getName()).getReference()));
        assertEquals(null, ((Element) resultingElement).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * an empty any pattern.
     */
    @Test
    public void testGenerateNewParticleAnyPatternEmpty() {
        AnyPattern oldAnyPattern = new AnyPattern(ProcessContentsInstruction.Strict, "##any");
        Particle oldParticle = oldAnyPattern;

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(oldAnyPattern, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertEquals(null, result);
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * an any pattern which is validated lax.
     */
    @Test
    public void testGenerateNewParticleAnyPatternLax() {
        AnyPattern oldAnyPattern = new AnyPattern(ProcessContentsInstruction.Lax, "##local");
        Particle oldParticle = oldAnyPattern;

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(oldAnyPattern, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof AnyPattern);
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals("##local", ((AnyPattern) result).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyPattern) result).getProcessContentsInstruction());
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * an any pattern with ID.
     */
    @Test
    public void testGenerateNewParticleAnyPatternID() {
        AnyPattern oldAnyPattern = new AnyPattern(ProcessContentsInstruction.Lax, "##local");
        oldAnyPattern.setId("id");
        Particle oldParticle = oldAnyPattern;

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(oldAnyPattern, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, elementOldSchemaMap, elementTypeMap, null, null, null, usedIDs);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof AnyPattern);
        assertEquals("id.1", result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals("##local", ((AnyPattern) result).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyPattern) result).getProcessContentsInstruction());
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * an any pattern with annotation.
     */
    @Test
    public void testGenerateNewParticleAnyPatternAnnotation() {
        AnyPattern oldAnyPattern = new AnyPattern(ProcessContentsInstruction.Lax, "##local");
        Annotation annotation = new Annotation();
        AppInfo appInfo = new AppInfo();
        appInfo.setSource("bla");
        Documentation documentation = new Documentation();
        documentation.setSource("blaa");
        annotation.addAppInfos(appInfo);
        annotation.addDocumentations(documentation);
        oldAnyPattern.setAnnotation(annotation);
        Particle oldParticle = oldAnyPattern;

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(oldAnyPattern, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof AnyPattern);
        assertEquals(null, result.getId());
        assertNotSame(annotation, result.getAnnotation());
        assertTrue(result.getAnnotation().getAppInfos().size() == 1);
        assertEquals("bla", result.getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(result.getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaa", result.getAnnotation().getDocumentations().get(0).getSource());
        assertEquals("##local", ((AnyPattern) result).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyPattern) result).getProcessContentsInstruction());
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * an any pattern which is validated lax and has namespace "##local A".
     */
    @Test
    public void testGenerateNewParticleAnyPatternNamespaceLocal() {
        AnyPattern oldAnyPattern = new AnyPattern(ProcessContentsInstruction.Lax, "##local A");
        Particle oldParticle = oldAnyPattern;

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(oldAnyPattern, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof AnyPattern);
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals("##local ##targetNamespace", ((AnyPattern) result).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyPattern) result).getProcessContentsInstruction());
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * an any pattern which is validated lax and has namespace "##targetNamespace A".
     */
    @Test
    public void testGenerateNewParticleAnyPatternNamespaceTarget() {
        AnyPattern oldAnyPattern = new AnyPattern(ProcessContentsInstruction.Lax, "##targetNamespace A");
        Particle oldParticle = oldAnyPattern;

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(oldAnyPattern, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof AnyPattern);
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals("##targetNamespace", ((AnyPattern) result).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyPattern) result).getProcessContentsInstruction());
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * an any pattern which is validated lax and has namespace "B C D".
     */
    @Test
    public void testGenerateNewParticleAnyPatternNamespaceUri() {
        AnyPattern oldAnyPattern = new AnyPattern(ProcessContentsInstruction.Lax, "B C D");
        Particle oldParticle = oldAnyPattern;

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(oldAnyPattern, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof AnyPattern);
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals("B C D", ((AnyPattern) result).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyPattern) result).getProcessContentsInstruction());
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * an any pattern which is validated lax and has namespace "##other".
     */
    @Test
    public void testGenerateNewParticleAnyPatternNamespaceOther() {
        AnyPattern oldAnyPattern = new AnyPattern(ProcessContentsInstruction.Lax, "##other");
        Particle oldParticle = oldAnyPattern;

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        XSDSchema oldSchema = new XSDSchema("B");
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(oldAnyPattern, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof AnyPattern);
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals("##any", ((AnyPattern) result).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyPattern) result).getProcessContentsInstruction());
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * an element.
     */
    @Test
    public void testGenerateNewParticleElement() {
        Element elementA = new Element("{A}elementA");
        Particle oldParticle = elementA;
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * an element with an anonymous type.
     */
    @Test
    public void testGenerateNewParticleElementAnonymousType() {
        Element elementA = new Element("{A}elementA");
        elementA.setTypeAttr(false);
        Particle oldParticle = elementA;
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.setIsAnonymous(true);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(true, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * an element with a built-in type.
     */
    @Test
    public void testGenerateNewParticleElementBuildInType() {
        Element elementA = new Element("{A}elementA");
        elementA.setTypeAttr(false);
        Particle oldParticle = elementA;
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        simpleTypeA.setIsAnonymous(true);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeA));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof Element);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Element) result).getType().getName());
        assertEquals(true, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * an element which is nillable.
     */
    @Test
    public void testGenerateNewParticleElementNillable() {
        Element elementA = new Element("{A}elementA");
        elementA.setNillable();
        Particle oldParticle = elementA;
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * an unqualified element.
     */
    @Test
    public void testGenerateNewParticleElementQualifiedNull() {
        Element elementA = new Element("{A}elementA");
        elementA.setForm(null);
        Particle oldParticle = elementA;
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * an foreign unqualified element.
     */
    @Test
    public void testGenerateNewParticleElementForeignUnqualified() {
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.unqualified);
        Particle oldParticle = elementA;
        SimpleType simpleTypeA = new SimpleType("{B}simpleTypeA", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{B}simpleTypeA", simpleTypeA));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("B");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(new SimpleType("{A}simpleTypeA", null), new XSDSchema("A"));
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof Element);
        assertEquals("{B}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * a qualified element.
     */
    @Test
    public void testGenerateNewParticleElementQualified() {
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        Particle oldParticle = elementA;
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{A}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(new SimpleType("{A}simpleTypeA", null), new XSDSchema("A"));
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * an element which has default value.
     */
    @Test
    public void testGenerateNewParticleElementDefault() {
        Element elementA = new Element("{A}elementA");
        elementA.setDefault("bla");
        Particle oldParticle = elementA;
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals("bla", ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * an element which has fixed value.
     */
    @Test
    public void testGenerateNewParticleElementFixed() {
        Element elementA = new Element("{A}elementA");
        elementA.setFixed("bla");
        Particle oldParticle = elementA;
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals("bla", ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * an element with block.
     */
    @Test
    public void testGenerateNewParticleElementBlock() {
        Element elementA = new Element("{A}elementA");
        elementA.addBlockModifier(Element.Block.extension);
        Particle oldParticle = elementA;
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        HashSet<Element.Block> blocks = new HashSet<Element.Block>();
        blocks.add(Element.Block.extension);
        assertEquals(blocks, ((Element) result).getBlockModifiers());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * an element with blockDefault.
     */
    @Test
    public void testGenerateNewParticleElementBlockDefault() {
        Element elementA = new Element("{A}elementA");
        Particle oldParticle = elementA;
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.extension);
        elementOldSchemaMap.put(elementA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        HashSet<Element.Block> blocks = new HashSet<Element.Block>();
        blocks.add(Element.Block.extension);
        assertEquals(blocks, ((Element) result).getBlockModifiers());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * an element with ID.
     */
    @Test
    public void testGenerateNewParticleElementID() {
        Element elementA = new Element("{A}elementA");
        elementA.setId("id");
        Particle oldParticle = elementA;
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, usedIDs);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals("id.1", ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * an element with annotation.
     */
    @Test
    public void testGenerateNewParticleElementAnnotation() {
        Element elementA = new Element("{A}elementA");
        Annotation annotation = new Annotation();
        AppInfo appInfo = new AppInfo();
        appInfo.setSource("bla");
        Documentation documentation = new Documentation();
        documentation.setSource("blaa");
        annotation.addAppInfos(appInfo);
        annotation.addDocumentations(documentation);
        elementA.setAnnotation(annotation);
        Particle oldParticle = elementA;
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertNotSame(annotation, ((Element) result).getAnnotation());
        assertTrue(((Element) result).getAnnotation().getAppInfos().size() == 1);
        assertEquals("bla", ((Element) result).getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((Element) result).getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaa", ((Element) result).getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * an element reference.
     */
    @Test
    public void testGenerateNewParticleElementReference() {
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        ElementRef oldElementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        Particle oldParticle = oldElementRef;
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{A}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, LinkedHashSet<Element>> namespaceConflictingElementsMap = new LinkedHashMap<String, LinkedHashSet<Element>>();
        namespaceConflictingElementsMap.put("A", null);
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, namespaceConflictingElementsMap, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, elementTypeMap, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof ElementRef);
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(((ElementRef) result).getElement() == null);
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * an element reference with empty namespaceConflictingElementsMap.
     */
    @Test
    public void testGenerateNewParticleElementReferenceWithEmptyMap() {
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        ElementRef oldElementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        Particle oldParticle = oldElementRef;
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{A}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, LinkedHashSet<Element>> namespaceConflictingElementsMap = new LinkedHashMap<String, LinkedHashSet<Element>>();
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, namespaceConflictingElementsMap, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, elementTypeMap, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof ElementRef);
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(((ElementRef) result).getElement() == null);
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * an element reference referencing abstract element.
     */
    @Test
    public void testGenerateNewParticleElementReferenceAbstract() {
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        elementA.setAbstract(true);
        ElementRef oldElementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        Particle oldParticle = oldElementRef;
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{A}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, LinkedHashSet<Element>> namespaceConflictingElementsMap = new LinkedHashMap<String, LinkedHashSet<Element>>();
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, namespaceConflictingElementsMap, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, elementTypeMap, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertEquals(null, result);
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * an element reference with ID.
     */
    @Test
    public void testGenerateNewParticleElementReferenceID() {
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        ElementRef oldElementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        oldElementRef.setId("id");
        Particle oldParticle = oldElementRef;
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{A}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, LinkedHashSet<Element>> namespaceConflictingElementsMap = new LinkedHashMap<String, LinkedHashSet<Element>>();
        namespaceConflictingElementsMap.put("A", null);
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, namespaceConflictingElementsMap, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, usedIDs);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, elementTypeMap, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof ElementRef);
        assertEquals("id.1", result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(((ElementRef) result).getElement() == null);
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * an element reference with annotation.
     */
    @Test
    public void testGenerateNewParticleElementReferenceAnnotation() {
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        ElementRef oldElementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        Annotation annotation = new Annotation();
        AppInfo appInfo = new AppInfo();
        appInfo.setSource("bla");
        Documentation documentation = new Documentation();
        documentation.setSource("blaa");
        annotation.addAppInfos(appInfo);
        annotation.addDocumentations(documentation);
        oldElementRef.setAnnotation(annotation);
        Particle oldParticle = oldElementRef;
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{A}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, LinkedHashSet<Element>> namespaceConflictingElementsMap = new LinkedHashMap<String, LinkedHashSet<Element>>();
        namespaceConflictingElementsMap.put("A", null);
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, namespaceConflictingElementsMap, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, elementTypeMap, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof ElementRef);
        assertEquals(null, result.getId());
        assertNotSame(annotation, result.getAnnotation());
        assertTrue(result.getAnnotation().getAppInfos().size() == 1);
        assertEquals("bla", result.getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(result.getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaa", result.getAnnotation().getDocumentations().get(0).getSource());
        assertTrue(((ElementRef) result).getElement() == null);
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * an element reference which is in conflict.
     */
    @Test
    public void testGenerateNewParticleElementReferenceElement() {
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        ElementRef oldElementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        Particle oldParticle = oldElementRef;
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{A}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, LinkedHashSet<Element>> namespaceConflictingElementsMap = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        elements.add(elementA);
        namespaceConflictingElementsMap.put("A", elements);
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, namespaceConflictingElementsMap, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, elementTypeMap, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * an element reference which is in conflict and has an ID.
     */
    @Test
    public void testGenerateNewParticleElementReferenceElementID() {
        Element elementA = new Element("{A}elementA");
        elementA.setId("idOne");
        elementA.setForm(XSDSchema.Qualification.qualified);
        ElementRef oldElementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));
        oldElementRef.setId("idTwo");
        Particle oldParticle = oldElementRef;
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{A}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, LinkedHashSet<Element>> namespaceConflictingElementsMap = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        elements.add(elementA);
        namespaceConflictingElementsMap.put("A", elements);
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("idTwo.idOne");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, namespaceConflictingElementsMap, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, usedIDs);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, elementTypeMap, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals("idOne.1", result.getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * an element reference which is in conflictand has an annotation.
     */
    @Test
    public void testGenerateNewParticleElementReferenceElementAnnotation() {
        Element elementA = new Element("{A}elementA");

        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        elementA.setAnnotation(annotationA);

        elementA.setForm(XSDSchema.Qualification.qualified);
        ElementRef oldElementRef = new ElementRef(new SymbolTableRef<Element>("{A}elementA", elementA));

        Annotation annotationB = new Annotation();
        AppInfo appInfoB = new AppInfo();
        appInfoB.setSource("blaB");
        Documentation documentationB = new Documentation();
        documentationB.setSource("blaaB");
        annotationB.addAppInfos(appInfoB);
        annotationB.addDocumentations(documentationB);
        oldElementRef.setAnnotation(annotationB);

        Particle oldParticle = oldElementRef;
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{A}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, LinkedHashSet<Element>> namespaceConflictingElementsMap = new LinkedHashMap<String, LinkedHashSet<Element>>();
        LinkedHashSet<Element> elements = new LinkedHashSet<Element>();
        elements.add(elementA);
        namespaceConflictingElementsMap.put("A", elements);
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, namespaceConflictingElementsMap, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, elementTypeMap, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertTrue(result.getAnnotation().getAppInfos().size() == 1);
        assertEquals("blaA", result.getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(result.getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaaA", result.getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * a group reference.
     */
    @Test
    public void testGenerateNewParticleGroupReference() {
        SimpleType oldSimpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Element elementA = new Element("{A}elementA");
        elementA.setType(new SymbolTableRef<Type>("{A}simpleTypeA", oldSimpleTypeA));

        SimpleType oldSimpleTypeB = new SimpleType("{A}simpleTypeB", null);
        Element elementB = new Element("{A}elementB");
        elementB.setType(new SymbolTableRef<Type>("{A}simpleTypeB", oldSimpleTypeB));

        ChoicePattern oldChoicePattern = new ChoicePattern();
        oldChoicePattern.addParticle(elementA);
        oldChoicePattern.addParticle(elementB);
        Group oldGroup = new Group("{A}group", oldChoicePattern);
        GroupRef oldGroupRef = new GroupRef(new SymbolTableRef<Group>("{A}group", oldGroup));
        Particle oldParticle = oldGroupRef;
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.getTypeSymbolTable().updateOrCreateReference("{A}simpleTypeA", simpleTypeA);
        outputSchema.getTypeSymbolTable().updateOrCreateReference("{A}simpleTypeB", simpleTypeB);
        elementTypeMap.put("{}elementA", outputSchema.getTypeSymbolTable().getReference("{A}simpleTypeA"));
        elementTypeMap.put("{}elementB", outputSchema.getTypeSymbolTable().getReference("{A}simpleTypeB"));
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof GroupRef);
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(((GroupRef) result).getGroup() instanceof Group);
        assertEquals(null, ((Group) ((GroupRef) result).getGroup()).getId());
        assertEquals(null, ((Group) ((GroupRef) result).getGroup()).getAnnotation());
        assertTrue(((Group) ((GroupRef) result).getGroup()).getParticleContainer() instanceof ChoicePattern);
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * a group reference with ID.
     */
    @Test
    public void testGenerateNewParticleGroupReferenceID() {
        SimpleType oldSimpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Element elementA = new Element("{A}elementA");
        elementA.setType(new SymbolTableRef<Type>("{A}simpleTypeA", oldSimpleTypeA));

        SimpleType oldSimpleTypeB = new SimpleType("{A}simpleTypeB", null);
        Element elementB = new Element("{A}elementB");
        elementB.setType(new SymbolTableRef<Type>("{A}simpleTypeB", oldSimpleTypeB));

        ChoicePattern oldChoicePattern = new ChoicePattern();
        oldChoicePattern.addParticle(elementA);
        oldChoicePattern.addParticle(elementB);
        Group oldGroup = new Group("{A}group", oldChoicePattern);
        GroupRef oldGroupRef = new GroupRef(new SymbolTableRef<Group>("{A}group", oldGroup));
        oldGroupRef.setId("id");
        Particle oldParticle = oldGroupRef;
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.getTypeSymbolTable().updateOrCreateReference("{A}simpleTypeA", simpleTypeA);
        outputSchema.getTypeSymbolTable().updateOrCreateReference("{A}simpleTypeB", simpleTypeB);
        elementTypeMap.put("{}elementA", outputSchema.getTypeSymbolTable().getReference("{A}simpleTypeA"));
        elementTypeMap.put("{}elementB", outputSchema.getTypeSymbolTable().getReference("{A}simpleTypeB"));
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, usedIDs);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof GroupRef);
        assertEquals("id.1", result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(((GroupRef) result).getGroup() instanceof Group);
        assertEquals(null, ((Group) ((GroupRef) result).getGroup()).getId());
        assertEquals(null, ((Group) ((GroupRef) result).getGroup()).getAnnotation());
        assertTrue(((Group) ((GroupRef) result).getGroup()).getParticleContainer() instanceof ChoicePattern);
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * a group reference with annotation.
     */
    @Test
    public void testGenerateNewParticleGroupReferenceAnnotation() {
        SimpleType oldSimpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Element elementA = new Element("{A}elementA");
        elementA.setType(new SymbolTableRef<Type>("{A}simpleTypeA", oldSimpleTypeA));

        SimpleType oldSimpleTypeB = new SimpleType("{A}simpleTypeB", null);
        Element elementB = new Element("{A}elementB");
        elementB.setType(new SymbolTableRef<Type>("{A}simpleTypeB", oldSimpleTypeB));

        ChoicePattern oldChoicePattern = new ChoicePattern();
        oldChoicePattern.addParticle(elementA);
        oldChoicePattern.addParticle(elementB);
        Group oldGroup = new Group("{A}group", oldChoicePattern);
        GroupRef oldGroupRef = new GroupRef(new SymbolTableRef<Group>("{A}group", oldGroup));
        Particle oldParticle = oldGroupRef;
        Annotation annotation = new Annotation();
        AppInfo appInfo = new AppInfo();
        appInfo.setSource("bla");
        Documentation documentation = new Documentation();
        documentation.setSource("blaa");
        annotation.addAppInfos(appInfo);
        annotation.addDocumentations(documentation);
        oldParticle.setAnnotation(annotation);
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.getTypeSymbolTable().updateOrCreateReference("{A}simpleTypeA", simpleTypeA);
        outputSchema.getTypeSymbolTable().updateOrCreateReference("{A}simpleTypeB", simpleTypeB);
        elementTypeMap.put("{}elementA", outputSchema.getTypeSymbolTable().getReference("{A}simpleTypeA"));
        elementTypeMap.put("{}elementB", outputSchema.getTypeSymbolTable().getReference("{A}simpleTypeB"));
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof GroupRef);
        assertEquals(null, result.getId());
        assertNotSame(annotation, result.getAnnotation());
        assertTrue(result.getAnnotation().getAppInfos().size() == 1);
        assertEquals("bla", result.getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(result.getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaa", result.getAnnotation().getDocumentations().get(0).getSource());
        assertTrue(((GroupRef) result).getGroup() instanceof Group);
        assertEquals(null, ((Group) ((GroupRef) result).getGroup()).getId());
        assertEquals(null, ((Group) ((GroupRef) result).getGroup()).getAnnotation());
        assertTrue(((Group) ((GroupRef) result).getGroup()).getParticleContainer() instanceof ChoicePattern);
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * a group reference with ID for the group.
     */
    @Test
    public void testGenerateNewParticleGroupReferenceGroupID() {
        SimpleType oldSimpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Element elementA = new Element("{A}elementA");
        elementA.setType(new SymbolTableRef<Type>("{A}simpleTypeA", oldSimpleTypeA));

        SimpleType oldSimpleTypeB = new SimpleType("{A}simpleTypeB", null);
        Element elementB = new Element("{A}elementB");
        elementB.setType(new SymbolTableRef<Type>("{A}simpleTypeB", oldSimpleTypeB));

        ChoicePattern oldChoicePattern = new ChoicePattern();
        oldChoicePattern.addParticle(elementA);
        oldChoicePattern.addParticle(elementB);
        Group oldGroup = new Group("{A}group", oldChoicePattern);
        oldGroup.setId("id");
        GroupRef oldGroupRef = new GroupRef(new SymbolTableRef<Group>("{A}group", oldGroup));
        Particle oldParticle = oldGroupRef;
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.getTypeSymbolTable().updateOrCreateReference("{A}simpleTypeA", simpleTypeA);
        outputSchema.getTypeSymbolTable().updateOrCreateReference("{A}simpleTypeB", simpleTypeB);
        elementTypeMap.put("{}elementA", outputSchema.getTypeSymbolTable().getReference("{A}simpleTypeA"));
        elementTypeMap.put("{}elementB", outputSchema.getTypeSymbolTable().getReference("{A}simpleTypeB"));
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, usedIDs);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof GroupRef);
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(((GroupRef) result).getGroup() instanceof Group);
        assertEquals("id.1", ((Group) ((GroupRef) result).getGroup()).getId());
        assertEquals(null, ((Group) ((GroupRef) result).getGroup()).getAnnotation());
        assertTrue(((Group) ((GroupRef) result).getGroup()).getParticleContainer() instanceof ChoicePattern);
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * a group reference with annotation for the group.
     */
    @Test
    public void testGenerateNewParticleGroupReferenceGroupAnnotation() {
        SimpleType oldSimpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Element elementA = new Element("{A}elementA");
        elementA.setType(new SymbolTableRef<Type>("{A}simpleTypeA", oldSimpleTypeA));

        SimpleType oldSimpleTypeB = new SimpleType("{A}simpleTypeB", null);
        Element elementB = new Element("{A}elementB");
        elementB.setType(new SymbolTableRef<Type>("{A}simpleTypeB", oldSimpleTypeB));

        ChoicePattern oldChoicePattern = new ChoicePattern();
        oldChoicePattern.addParticle(elementA);
        oldChoicePattern.addParticle(elementB);
        Group oldGroup = new Group("{A}group", oldChoicePattern);
        Annotation annotation = new Annotation();
        AppInfo appInfo = new AppInfo();
        appInfo.setSource("bla");
        Documentation documentation = new Documentation();
        documentation.setSource("blaa");
        annotation.addAppInfos(appInfo);
        annotation.addDocumentations(documentation);
        oldGroup.setAnnotation(annotation);
        GroupRef oldGroupRef = new GroupRef(new SymbolTableRef<Group>("{A}group", oldGroup));
        Particle oldParticle = oldGroupRef;

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.getTypeSymbolTable().updateOrCreateReference("{A}simpleTypeA", simpleTypeA);
        outputSchema.getTypeSymbolTable().updateOrCreateReference("{A}simpleTypeB", simpleTypeB);
        elementTypeMap.put("{}elementA", outputSchema.getTypeSymbolTable().getReference("{A}simpleTypeA"));
        elementTypeMap.put("{}elementB", outputSchema.getTypeSymbolTable().getReference("{A}simpleTypeB"));
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof GroupRef);
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(((GroupRef) result).getGroup() instanceof Group);
        assertEquals(null, ((Group) ((GroupRef) result).getGroup()).getId());
        assertNotSame(annotation, ((Group) ((GroupRef) result).getGroup()).getAnnotation());
        assertTrue(((Group) ((GroupRef) result).getGroup()).getAnnotation().getAppInfos().size() == 1);
        assertEquals("bla", ((Group) ((GroupRef) result).getGroup()).getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((Group) ((GroupRef) result).getGroup()).getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaa", ((Group) ((GroupRef) result).getGroup()).getAnnotation().getDocumentations().get(0).getSource());
        assertTrue(((Group) ((GroupRef) result).getGroup()).getParticleContainer() instanceof ChoicePattern);
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * a group reference which is resolved.
     */
    @Test
    public void testGenerateNewParticleGroupReferenceResolveOneElement() {
        SimpleType oldSimpleType = new SimpleType("{A}simpleTypeA", null);
        Element elementA = new Element("{A}elementA");
        elementA.setType(new SymbolTableRef<Type>("{A}simpleTypeA", oldSimpleType));
        ChoicePattern oldChoicePattern = new ChoicePattern();
        oldChoicePattern.addParticle(elementA);
        Group oldGroup = new Group("{A}group", oldChoicePattern);
        GroupRef oldGroupRef = new GroupRef(new SymbolTableRef<Group>("{A}group", oldGroup));
        Particle oldParticle = oldGroupRef;
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.getTypeSymbolTable().updateOrCreateReference("{A}simpleTypeA", simpleTypeA);
        elementTypeMap.put("{}elementA", outputSchema.getTypeSymbolTable().getReference("{A}simpleTypeA"));
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof GroupRef);
                assertTrue(((GroupRef)result).getGroup().getParticleContainer() instanceof ChoicePattern);
        ChoicePattern newChoicePattern = (ChoicePattern) ((GroupRef)result).getGroup().getParticleContainer();
        assertEquals(null, newChoicePattern.getId());
        assertEquals(null, newChoicePattern.getAnnotation());
        assertTrue(((ChoicePattern) newChoicePattern).getParticles().getFirst() instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) ((ChoicePattern) newChoicePattern).getParticles().getFirst()).getType().getName());
        assertEquals(false, ((Element) ((ChoicePattern) newChoicePattern).getParticles().getFirst()).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) ((ChoicePattern) newChoicePattern).getParticles().getFirst()).getTypeAttr());
        assertEquals(null, ((Element) ((ChoicePattern) newChoicePattern).getParticles().getFirst()).getDefault());
        assertEquals(null, ((Element) ((ChoicePattern) newChoicePattern).getParticles().getFirst()).getFixed());
        assertEquals(null, ((Element) ((ChoicePattern) newChoicePattern).getParticles().getFirst()).getId());
        assertEquals(null, ((Element) ((ChoicePattern) newChoicePattern).getParticles().getFirst()).getAnnotation());
        assertEquals(false, (boolean) ((Element) ((ChoicePattern) newChoicePattern).getParticles().getFirst()).getAbstract());
        assertEquals(false, (boolean) ((Element) ((ChoicePattern) newChoicePattern).getParticles().getFirst()).getNillable());
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) ((ChoicePattern) newChoicePattern).getParticles().getFirst()).getName()).getReference()));
        assertEquals(null, (((Element) ((ChoicePattern) newChoicePattern).getParticles().getFirst())).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * a group reference which is resolved.
     */
    @Test
    public void testGenerateNewParticleGroupReferenceResolveTwoElements() {
        SimpleType oldSimpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Element elementA = new Element("{A}elementA");
        elementA.setType(new SymbolTableRef<Type>("{A}simpleTypeA", oldSimpleTypeA));

        SimpleType oldSimpleTypeB = new SimpleType("{A}simpleTypeB", null);
        Element elementB = new Element("{A}elementB");
        elementB.setType(new SymbolTableRef<Type>("{A}simpleTypeB", oldSimpleTypeB));

        ChoicePattern oldChoicePattern = new ChoicePattern();
        oldChoicePattern.addParticle(elementA);
        oldChoicePattern.addParticle(elementB);
        Group oldGroup = new Group("{A}group", oldChoicePattern);
        GroupRef oldGroupRef = new GroupRef(new SymbolTableRef<Group>("{A}group", oldGroup));
        Particle oldParticle = oldGroupRef;
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.getTypeSymbolTable().updateOrCreateReference("{A}simpleTypeA", simpleTypeA);
        outputSchema.getTypeSymbolTable().updateOrCreateReference("{A}simpleTypeB", simpleTypeB);
        elementTypeMap.put("{}elementA", outputSchema.getTypeSymbolTable().getReference("{A}simpleTypeA"));
        elementTypeMap.put("{}elementB", new SymbolTableRef<Type>("{A}simpleTypeB", simpleTypeB));
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

                assertTrue(result instanceof GroupRef);
                assertTrue(((GroupRef)result).getGroup().getParticleContainer() instanceof ChoicePattern);
        ChoicePattern newChoicePattern = (ChoicePattern) ((GroupRef)result).getGroup().getParticleContainer();
        assertEquals(null,newChoicePattern.getId());
        assertEquals(null,newChoicePattern.getAnnotation());
        assertTrue(newChoicePattern.getParticles().getFirst() instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) newChoicePattern.getParticles().getFirst()).getType().getName());
        assertEquals(false, ((Element) newChoicePattern.getParticles().getFirst()).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) newChoicePattern.getParticles().getFirst()).getTypeAttr());
        assertEquals(null, ((Element) newChoicePattern.getParticles().getFirst()).getDefault());
        assertEquals(null, ((Element) newChoicePattern.getParticles().getFirst()).getFixed());
        assertEquals(null, ((Element) newChoicePattern.getParticles().getFirst()).getId());
        assertEquals(null, ((Element) newChoicePattern.getParticles().getFirst()).getAnnotation());
        assertEquals(false, (boolean) ((Element) newChoicePattern.getParticles().getFirst()).getAbstract());
        assertEquals(false, (boolean) ((Element) newChoicePattern.getParticles().getFirst()).getNillable());
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) newChoicePattern.getParticles().getFirst()).getName()).getReference()));
        assertEquals(null, (((Element) newChoicePattern.getParticles().getFirst())).getForm());
        assertEquals("{A}simpleTypeB", ((Element) newChoicePattern.getParticles().get(1)).getType().getName());
        assertEquals(false, ((Element) newChoicePattern.getParticles().get(1)).getType().isAnonymous());
        assertEquals(false, (boolean) ((Element) newChoicePattern.getParticles().get(1)).getTypeAttr());
        assertEquals(null, ((Element) newChoicePattern.getParticles().get(1)).getDefault());
        assertEquals(null, ((Element) newChoicePattern.getParticles().get(1)).getFixed());
        assertEquals(null, ((Element) newChoicePattern.getParticles().get(1)).getId());
        assertEquals(null, ((Element) newChoicePattern.getParticles().get(1)).getAnnotation());
        assertEquals(false, (boolean) ((Element) newChoicePattern.getParticles().get(1)).getAbstract());
        assertEquals(false, (boolean) ((Element) newChoicePattern.getParticles().get(1)).getNillable());
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) newChoicePattern.getParticles().get(1)).getName()).getReference()));
        assertEquals(null, (((Element) newChoicePattern.getParticles().get(1))).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * a particle container with counting pattern.
     */
    @Test
    public void testGenerateNewParticleParticleContainerCountingPattern() {
        AllPattern oldAllPattern = new AllPattern();
        Element elementA = new Element("{A}elementA");
        oldAllPattern.addParticle(elementA);
        Element elementB = new Element("{A}elementB");
        oldAllPattern.addParticle(elementB);
        Element elementC = new Element("{A}elementC");
        oldAllPattern.addParticle(elementC);
        CountingPattern oldCountingPattern = new CountingPattern(0, 4);
        oldCountingPattern.addParticle(oldAllPattern);
        Particle oldParticle = oldCountingPattern;

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        SimpleType simpleTypeC = new SimpleType("{A}simpleTypeC", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        elementTypeMap.put("{}elementB", new SymbolTableRef<Type>("{A}simpleTypeB", simpleTypeB));
        elementTypeMap.put("{}elementC", new SymbolTableRef<Type>("{A}simpleTypeC", simpleTypeC));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        elementOldSchemaMap.put(elementC, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof CountingPattern);
        assertEquals(0, (int) ((CountingPattern) result).getMin());
        assertEquals(4, (int) ((CountingPattern) result).getMax());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(((CountingPattern) result).getParticles().getFirst() instanceof AllPattern);
        assertTrue(((AllPattern) ((CountingPattern) result).getParticles().getFirst()).getParticles().size() == 3);
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * a particle container with id.
     */
    @Test
    public void testGenerateNewParticleParticleContainerID() {
        AllPattern oldAllPattern = new AllPattern();
        oldAllPattern.setId("id");
        Element elementA = new Element("{A}elementA");
        oldAllPattern.addParticle(elementA);
        Element elementB = new Element("{A}elementB");
        oldAllPattern.addParticle(elementB);
        Element elementC = new Element("{A}elementC");
        oldAllPattern.addParticle(elementC);
        Particle oldParticle = oldAllPattern;

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        SimpleType simpleTypeC = new SimpleType("{A}simpleTypeC", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        elementTypeMap.put("{}elementB", new SymbolTableRef<Type>("{A}simpleTypeB", simpleTypeB));
        elementTypeMap.put("{}elementC", new SymbolTableRef<Type>("{A}simpleTypeC", simpleTypeC));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        elementOldSchemaMap.put(elementC, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, usedIDs);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof AllPattern);
        assertEquals("id.1", result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(((AllPattern) result).getParticles().size() == 3);
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * a particle container with annotation.
     */
    @Test
    public void testGenerateNewParticleParticleContainerAnnotation() {
        AllPattern oldAllPattern = new AllPattern();
        Annotation annotation = new Annotation();
        AppInfo appInfo = new AppInfo();
        appInfo.setSource("bla");
        Documentation documentation = new Documentation();
        documentation.setSource("blaa");
        annotation.addAppInfos(appInfo);
        annotation.addDocumentations(documentation);
        oldAllPattern.setAnnotation(annotation);
        Element elementA = new Element("{A}elementA");
        oldAllPattern.addParticle(elementA);
        Element elementB = new Element("{A}elementB");
        oldAllPattern.addParticle(elementB);
        Element elementC = new Element("{A}elementC");
        oldAllPattern.addParticle(elementC);
        Particle oldParticle = oldAllPattern;

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        SimpleType simpleTypeC = new SimpleType("{A}simpleTypeC", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        elementTypeMap.put("{}elementB", new SymbolTableRef<Type>("{A}simpleTypeB", simpleTypeB));
        elementTypeMap.put("{}elementC", new SymbolTableRef<Type>("{A}simpleTypeC", simpleTypeC));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        elementOldSchemaMap.put(elementC, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof AllPattern);
        assertEquals(null, result.getId());
        assertNotSame(annotation, result.getAnnotation());
        assertTrue(result.getAnnotation().getAppInfos().size() == 1);
        assertEquals("bla", result.getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(result.getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaa", result.getAnnotation().getDocumentations().get(0).getSource());
        assertTrue(((AllPattern) result).getParticles().size() == 3);
    }

    /**
     * Test of generateNewParticle method, of class ParticleDifferenceGenerator for
     * a particle container with all pattern.
     */
    @Test
    public void testGenerateNewParticleParticleContainerAll() {
        AllPattern oldAllPattern = new AllPattern();
        Element elementA = new Element("{A}elementA");
        oldAllPattern.addParticle(elementA);
        Element elementB = new Element("{A}elementB");
        oldAllPattern.addParticle(elementB);
        Particle oldParticle = oldAllPattern;

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);


        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        elementTypeMap.put("{}elementB", new SymbolTableRef<Type>("{A}simpleTypeB", simpleTypeB));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, elementTypeMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));
        try {
            instance.generateNewParticleDifference(null, null, null, elementTypeMap);
        } catch (Exception ex) {
            Logger.getLogger(ParticleDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Particle result = instance.generateNewParticle(oldParticle);

        assertTrue(result instanceof AllPattern);
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(((AllPattern) result).getParticles().size() == 2);
        assertTrue(((AllPattern) result).getParticles().get(0) instanceof Element);
        assertEquals("{A}elementA", ((Element) ((AllPattern) result).getParticles().get(0)).getName());
        assertTrue(((AllPattern) result).getParticles().get(1) instanceof Element);
        assertEquals("{A}elementB", ((Element) ((AllPattern) result).getParticles().get(1)).getName());
    }

    /**
     * Test of generateNewTopLevelElement method, of class
     * ParticleDifferenceGenerator.
     */
    @Test
    public void testGenerateNewTopLevelElement() {
        Element minuendElement = new Element("{A}elementA");
        Element subtrahendElement = new Element("{A}elementA");

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA);

        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(minuendElement, new XSDSchema("A"));
        elementOldSchemaMap.put(subtrahendElement, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);

        instance.generateNewTopLevelElement(minuendElement, subtrahendElement, typeRef);

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
     * Test of generateNewTopLevelElement method, of class
     * ParticleDifferenceGenerator for an abstract minuend element.
     */
    @Test
    public void testGenerateNewTopLevelElementAbstractMinuend() {
        Element minuendElement = new Element("{A}elementA");
        minuendElement.setAbstract(true);
        Element subtrahendElement = new Element("{A}elementA");

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA);

        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(minuendElement, new XSDSchema("A"));
        elementOldSchemaMap.put(subtrahendElement, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);

        instance.generateNewTopLevelElement(minuendElement, subtrahendElement, typeRef);

        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
    }

    /**
     * Test of generateNewTopLevelElement method, of class
     * ParticleDifferenceGenerator for a dummy type.
     */
    @Test
    public void testGenerateNewTopLevelElementDummyType() {
        Element minuendElement = new Element("{A}elementA");
        Element subtrahendElement = new Element("{A}elementA");

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.setDummy(true);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA);

        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(minuendElement, new XSDSchema("A"));
        elementOldSchemaMap.put(subtrahendElement, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);

        instance.generateNewTopLevelElement(minuendElement, subtrahendElement, typeRef);

        assertFalse(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
    }

    /**
     * Test of generateNewTopLevelElement method, of class
     * ParticleDifferenceGenerator for a minuend element which finalizes
     * extension.
     */
    @Test
    public void testGenerateNewTopLevelElementFinalExtension() {
        Element minuendElement = new Element("{A}elementA");
        minuendElement.addFinalModifier(Element.Final.extension);
        Element subtrahendElement = new Element("{A}elementA");

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA);

        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(minuendElement, new XSDSchema("A"));
        elementOldSchemaMap.put(subtrahendElement, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);

        instance.generateNewTopLevelElement(minuendElement, subtrahendElement, typeRef);

        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getType().isAnonymous());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getBlockModifiers());
        HashSet<Final> finalValue = new HashSet<Final>();
        finalValue.add(Final.extension);
        assertEquals(finalValue, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFinalModifiers());
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
     * Test of generateNewTopLevelElement method, of class
     * ParticleDifferenceGenerator for a minuend element which finalizes
     * restriction.
     */
    @Test
    public void testGenerateNewTopLevelElementFinalRestriction() {
        Element minuendElement = new Element("{A}elementA");
        minuendElement.addFinalModifier(Element.Final.restriction);
        Element subtrahendElement = new Element("{A}elementA");

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA);

        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(minuendElement, new XSDSchema("A"));
        elementOldSchemaMap.put(subtrahendElement, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);

        instance.generateNewTopLevelElement(minuendElement, subtrahendElement, typeRef);

        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getType().isAnonymous());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getBlockModifiers());
        HashSet<Final> finalValue = new HashSet<Final>();
        finalValue.add(Final.restriction);
        assertEquals(finalValue, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFinalModifiers());
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
     * Test of generateNewTopLevelElement method, of class
     * ParticleDifferenceGenerator for a minuend element which finalizes
     * extension per default.
     */
    @Test
    public void testGenerateNewTopLevelElementFinalDefaultExtension() {
        Element minuendElement = new Element("{A}elementA");
        Element subtrahendElement = new Element("{A}elementA");

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA);

        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.extension);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(minuendElement, oldSchema);
        elementOldSchemaMap.put(subtrahendElement, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);

        instance.generateNewTopLevelElement(minuendElement, subtrahendElement, typeRef);

        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getType().isAnonymous());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getBlockModifiers());
        HashSet<Final> finalValue = new HashSet<Final>();
        finalValue.add(Final.extension);
        assertEquals(finalValue, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFinalModifiers());
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
     * Test of generateNewTopLevelElement method, of class
     * ParticleDifferenceGenerator for a minuend element which finalizes
     * restriction per default.
     */
    @Test
    public void testGenerateNewTopLevelElementFinalDefaultRestriction() {
        Element minuendElement = new Element("{A}elementA");
        Element subtrahendElement = new Element("{A}elementA");

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA);

        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(minuendElement, oldSchema);
        elementOldSchemaMap.put(subtrahendElement, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);

        instance.generateNewTopLevelElement(minuendElement, subtrahendElement, typeRef);

        assertTrue(outputSchema.getElementSymbolTable().hasReference("{A}elementA"));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getType().isAnonymous());
        assertEquals(null, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getBlockModifiers());
        HashSet<Final> finalValue = new HashSet<Final>();
        finalValue.add(Final.restriction);
        assertEquals(finalValue, outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference().getFinalModifiers());
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
     * Test of generateNewTopLevelElement method, of class
     * ParticleDifferenceGenerator for a minuend element which finalizes
     * restriction per default same as the output schema.
     */
    @Test
    public void testGenerateNewTopLevelElementFinalDefault() {
        Element minuendElement = new Element("{A}elementA");
        Element subtrahendElement = new Element("{A}elementA");

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA);

        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(minuendElement, oldSchema);
        elementOldSchemaMap.put(subtrahendElement, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);

        instance.generateNewTopLevelElement(minuendElement, subtrahendElement, typeRef);

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
     * Test of generateNewElement method, of class ParticleDifferenceGenerator.
     */
    @Test
    public void testGenerateNewElement() {
        Element minuendElement = new Element("{A}elementA");
        Element subtrahendElement = new Element("{A}elementA");

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA);

        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(minuendElement, new XSDSchema("A"));
        elementOldSchemaMap.put(subtrahendElement, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);

        Element result = instance.generateNewElement(minuendElement, subtrahendElement, typeRef);

        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, result.getType().isAnonymous());
        assertEquals(null, result.getBlockModifiers());
        assertEquals(null, result.getFinalModifiers());
        assertEquals(false, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewElement method, of class ParticleDifferenceGenerator
     * with true type attribute.
     */
    @Test
    public void testGenerateNewElementTypeAttr() {
        Element minuendElement = new Element("{A}elementA");
        minuendElement.setTypeAttr(true);
        Element subtrahendElement = new Element("{A}elementA");

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA);

        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(minuendElement, new XSDSchema("A"));
        elementOldSchemaMap.put(subtrahendElement, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);

        Element result = instance.generateNewElement(minuendElement, subtrahendElement, typeRef);

        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, result.getType().isAnonymous());
        assertEquals(null, result.getBlockModifiers());
        assertEquals(null, result.getFinalModifiers());
        assertEquals(true, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertEquals(null, result.getForm());
    }


    /**
     * Test of generateNewElement method, of class ParticleDifferenceGenerator
     * for nillable minuend element.
     */
    @Test
    public void testGenerateNewElementMinuendNillable() {
        Element minuendElement = new Element("{A}elementA");
        minuendElement.setNillable();
        Element subtrahendElement = new Element("{A}elementA");

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA);

        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(minuendElement, new XSDSchema("A"));
        elementOldSchemaMap.put(subtrahendElement, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);

        Element result = instance.generateNewElement(minuendElement, subtrahendElement, typeRef);

        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, result.getType().isAnonymous());
        assertEquals(null, result.getBlockModifiers());
        assertEquals(null, result.getFinalModifiers());
        assertEquals(false, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(true, (boolean) result.getNillable());
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewElement method, of class ParticleDifferenceGenerator
     * for nillable minuend and subtrahend element.
     */
    @Test
    public void testGenerateNewElementBothNillable() {
        Element minuendElement = new Element("{A}elementA");
                minuendElement.setNillable();
        Element subtrahendElement = new Element("{A}elementA");
                subtrahendElement.setNillable();

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA);

        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(minuendElement, new XSDSchema("A"));
        elementOldSchemaMap.put(subtrahendElement, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);

        Element result = instance.generateNewElement(minuendElement, subtrahendElement, typeRef);

        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, result.getType().isAnonymous());
        assertEquals(null, result.getBlockModifiers());
        assertEquals(null, result.getFinalModifiers());
        assertEquals(false, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewElement method, of class ParticleDifferenceGenerator
     * for qualified minuend element.
     */
    @Test
    public void testGenerateNewElementMinuendQualified() {
        Element minuendElement = new Element("{A}elementA");
        minuendElement.setForm(XSDSchema.Qualification.qualified);
        Element subtrahendElement = new Element("{A}elementA");

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA);

        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(minuendElement, new XSDSchema("A"));
        elementOldSchemaMap.put(subtrahendElement, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);

        Element result = instance.generateNewElement(minuendElement, subtrahendElement, typeRef);

        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, result.getType().isAnonymous());
        assertEquals(null, result.getBlockModifiers());
        assertEquals(null, result.getFinalModifiers());
        assertEquals(false, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertEquals(XSDSchema.Qualification.qualified, result.getForm());
    }





    /**
     * Test of generateNewElement method, of class ParticleDifferenceGenerator
     * for elements with different fixed values.
     */
    @Test
    public void testGenerateNewElementDifferentFixed() {
        Element minuendElement = new Element("{A}elementA");
        minuendElement.setFixed("bla");
        Element subtrahendElement = new Element("{A}elementA");
                subtrahendElement.setFixed("bla2");

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA);

        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(minuendElement, new XSDSchema("A"));
        elementOldSchemaMap.put(subtrahendElement, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);

        Element result = instance.generateNewElement(minuendElement, subtrahendElement, typeRef);

                assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, result.getType().isAnonymous());
        assertEquals(null, result.getBlockModifiers());
        assertEquals(null, result.getFinalModifiers());
        assertEquals(false, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals("bla", result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewElement method, of class ParticleDifferenceGenerator
     * for elements with same fixed values.
     */
    @Test
    public void testGenerateNewElementSameFixed() {
        Element minuendElement = new Element("{A}elementA");
        minuendElement.setFixed("bla");
        Element subtrahendElement = new Element("{A}elementA");
                subtrahendElement.setFixed("bla");

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA);

        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(minuendElement, new XSDSchema("A"));
        elementOldSchemaMap.put(subtrahendElement, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);

        Element result = instance.generateNewElement(minuendElement, subtrahendElement, typeRef);

        assertEquals(null, result);
    }


    /**
     * Test of generateNewElement method, of class ParticleDifferenceGenerator
     * for minuend element with annotation.
     */
    @Test
    public void testGenerateNewElementMinuendAnnotation() {
        Element minuendElement = new Element("{A}elementA");
          Annotation annotation = new Annotation();
        AppInfo appInfo = new AppInfo();
        appInfo.setSource("bla");
        Documentation documentation = new Documentation();
        documentation.setSource("blaa");
        annotation.addAppInfos(appInfo);
        annotation.addDocumentations(documentation);
        minuendElement.setAnnotation(annotation);
        Element subtrahendElement = new Element("{A}elementA");

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA);

        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(minuendElement, new XSDSchema("A"));
        elementOldSchemaMap.put(subtrahendElement, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);

        Element result = instance.generateNewElement(minuendElement, subtrahendElement, typeRef);

        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, result.getType().isAnonymous());
        assertEquals(null, result.getBlockModifiers());
        assertEquals(null, result.getFinalModifiers());
        assertEquals(false, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertNotSame(annotation, result.getAnnotation());
        assertTrue(result.getAnnotation().getAppInfos().size() == 1);
        assertEquals("bla", result.getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(result.getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaa", result.getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewElement method, of class ParticleDifferenceGenerator
     * for minuend element with id.
     */
    @Test
    public void testGenerateNewElementMinuendID() {
        Element minuendElement = new Element("{A}elementA");
        minuendElement.setId("id");
        Element subtrahendElement = new Element("{A}elementA");

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA);

        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(minuendElement, new XSDSchema("A"));
        elementOldSchemaMap.put(subtrahendElement, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
                LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, usedIDs);

        Element result = instance.generateNewElement(minuendElement, subtrahendElement, typeRef);

        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, result.getType().isAnonymous());
        assertEquals(null, result.getBlockModifiers());
        assertEquals(null, result.getFinalModifiers());
        assertEquals(false, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals("id.1", result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewElement method, of class ParticleDifferenceGenerator
     * for minuend element with default value.
     */
    @Test
    public void testGenerateNewElementMinuendDefault() {
        Element minuendElement = new Element("{A}elementA");
        minuendElement.setDefault("bla");
        Element subtrahendElement = new Element("{A}elementA");

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA);

        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(minuendElement, new XSDSchema("A"));
        elementOldSchemaMap.put(subtrahendElement, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);

        Element result = instance.generateNewElement(minuendElement, subtrahendElement, typeRef);

        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference("{A}elementA").getReference()));
        assertEquals(false, result.getType().isAnonymous());
        assertEquals(null, result.getBlockModifiers());
        assertEquals(null, result.getFinalModifiers());
        assertEquals(false, (boolean) result.getTypeAttr());
        assertEquals("bla", result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertEquals(null, result.getForm());
    }

    /**
     * Test of isDifferenceEmpty method, of class ParticleDifferenceGenerator.
     */
    @Test
    public void testIsDifferenceEmpty() {
        Element minunedElement = new Element("{A}elementA");
        Element subtrahendElement = new Element("{A}elementA");

        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);

        boolean expResult = true;
        boolean result = instance.isDifferenceEmpty(minunedElement, subtrahendElement);

        assertEquals(expResult, result);
    }

    /**
     * Test of isDifferenceEmpty method, of class ParticleDifferenceGenerator
     * for empty subtrahend element.
     */
    @Test
    public void testIsDifferenceEmptySubtrahendNull() {
        Element minunedElement = new Element("{A}elementA");
        Element subtrahendElement = null;

        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);

        boolean expResult = false;
        boolean result = instance.isDifferenceEmpty(minunedElement, subtrahendElement);

        assertEquals(expResult, result);
    }

    /**
     * Test of isDifferenceEmpty method, of class ParticleDifferenceGenerator
     * for empty minuend element.
     */
    @Test
    public void testIsDifferenceEmptyMinuendNull() {
        Element minunedElement = null;
        Element subtrahendElement = new Element("{A}elementA");

        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);

        boolean expResult = true;
        boolean result = instance.isDifferenceEmpty(minunedElement, subtrahendElement);

        assertEquals(expResult, result);
    }

    /**
     * Test of isDifferenceEmpty method, of class ParticleDifferenceGenerator
     * for elements with different names.
     */
    @Test
    public void testIsDifferenceEmptyDifferentNames() {
        Element minunedElement = new Element("{A}elementA");
        Element subtrahendElement = new Element("{A}elementB");

        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);

        boolean expResult = false;
        boolean result = instance.isDifferenceEmpty(minunedElement, subtrahendElement);

        assertEquals(expResult, result);
    }

    /**
     * Test of isDifferenceEmpty method, of class ParticleDifferenceGenerator
     * for abstract subtrahend element.
     */
    @Test
    public void testIsDifferenceEmptySubtrahendAbstract() {
        Element minunedElement = new Element("{A}elementA");
        Element subtrahendElement = new Element("{A}elementA");
        subtrahendElement.setAbstract(true);

        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);

        boolean expResult = false;
        boolean result = instance.isDifferenceEmpty(minunedElement, subtrahendElement);

        assertEquals(expResult, result);
    }

    /**
     * Test of isDifferenceEmpty method, of class ParticleDifferenceGenerator
     * for abstract minuend element.
     */
    @Test
    public void testIsDifferenceEmptyMinuendAbstract() {
        Element minunedElement = new Element("{A}elementA");
        minunedElement.setAbstract(true);
        Element subtrahendElement = new Element("{A}elementA");

        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);

        boolean expResult = false;
        boolean result = instance.isDifferenceEmpty(minunedElement, subtrahendElement);

        assertEquals(expResult, result);
    }

    /**
     * Test of isDifferenceEmpty method, of class ParticleDifferenceGenerator
     * for qualified elments.
     */
    @Test
    public void testIsDifferenceEmptyQualified() {
        Element minunedElement = new Element("{A}elementA");
        minunedElement.setForm(XSDSchema.Qualification.qualified);
        Element subtrahendElement = new Element("{A}elementA");
        subtrahendElement.setForm(XSDSchema.Qualification.qualified);

        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);

        boolean expResult = true;
        boolean result = instance.isDifferenceEmpty(minunedElement, subtrahendElement);

        assertEquals(expResult, result);
    }

    /**
     * Test of isDifferenceEmpty method, of class ParticleDifferenceGenerator
     * for element with different form values.
     */
    @Test
    public void testIsDifferenceEmptyDifferentForm() {
        Element minunedElement = new Element("{A}elementA");
        minunedElement.setForm(XSDSchema.Qualification.qualified);
        Element subtrahendElement = new Element("{A}elementA");

        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);

        boolean expResult = false;
        boolean result = instance.isDifferenceEmpty(minunedElement, subtrahendElement);

        assertEquals(expResult, result);
    }

    /**
     * Test of isDifferenceEmpty method, of class ParticleDifferenceGenerator
     * for fixed subtrahend elment.
     */
    @Test
    public void testIsDifferenceEmptySubtrahendFixed() {
        Element minunedElement = new Element("{A}elementA");
        Element subtrahendElement = new Element("{A}elementA");
        subtrahendElement.setFixed("bla");

        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);

        boolean expResult = false;
        boolean result = instance.isDifferenceEmpty(minunedElement, subtrahendElement);

        assertEquals(expResult, result);
    }

    /**
     * Test of isDifferenceEmpty method, of class ParticleDifferenceGenerator
     * for element with different fixed values.
     */
    @Test
    public void testIsDifferenceEmptyBothDifferentFixed() {
        Element minunedElement = new Element("{A}elementA");
        minunedElement.setFixed("bla");
        Element subtrahendElement = new Element("{A}elementA");
        subtrahendElement.setFixed("bla2");

        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);

        boolean expResult = false;
        boolean result = instance.isDifferenceEmpty(minunedElement, subtrahendElement);

        assertEquals(expResult, result);
    }

    /**
     * Test of isDifferenceEmpty method, of class ParticleDifferenceGenerator
     * for element with same fixed values.
     */
    @Test
    public void testIsDifferenceEmptyBothSameFixed() {
        Element minunedElement = new Element("{A}elementA");
        minunedElement.setFixed("bla");
        Element subtrahendElement = new Element("{A}elementA");
        subtrahendElement.setFixed("bla");

        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);

        boolean expResult = true;
        boolean result = instance.isDifferenceEmpty(minunedElement, subtrahendElement);

        assertEquals(expResult, result);
    }

    /**
     * Test of generateNewDifferenceAnyPattern method, of class
     * ParticleDifferenceGenerator.
     */
    @Test
    public void testGenerateNewDifferenceAnyPattern() {
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##any");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "B");

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        anyPatternOldSchemaMap.put(anyPatternB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        AnyPattern result = instance.generateNewDifferenceAnyPattern(anyPatternA, anyPatternB);

        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getId());
        assertEquals("##any", result.getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, result.getProcessContentsInstruction());
    }

    /**
     * Test of generateNewDifferenceAnyPattern method, of class
     * ParticleDifferenceGenerator for an empty any pattern.
     */
    @Test
    public void testGenerateNewDifferenceAnyPatternEmpty() {
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "A");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "A");

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        AnyPattern result = instance.generateNewDifferenceAnyPattern(anyPatternA, anyPatternB);

        assertEquals(null, result);
    }

    /**
     * Test of generateNewDifferenceAnyPattern method, of class
     * ParticleDifferenceGenerator for a set of any patterns with different
     * IDs.
     */
    @Test
    public void testGenerateNewDifferenceAnyPatternDifferentIDs() {
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "B");
        anyPatternA.setId("idOne");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "A");
        anyPatternB.setId("idTwo");

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("idOne");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, usedIDs);

        AnyPattern result = instance.generateNewDifferenceAnyPattern(anyPatternA, anyPatternB);

        assertEquals(null, result.getAnnotation());
        assertEquals("idOne.1", result.getId());
        assertEquals("B", result.getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, result.getProcessContentsInstruction());
    }

    /**
     * Test of generateNewDifferenceAnyPattern method, of class
     * ParticleDifferenceGenerator for a set of any patterns with same IDs.
     */
    @Test
    public void testGenerateNewDifferenceAnyPatternSameIDs() {
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "B");
        anyPatternA.setId("idOne");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "A");
        anyPatternB.setId("idOne");

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("idOne");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, usedIDs);

        AnyPattern result = instance.generateNewDifferenceAnyPattern(anyPatternA, anyPatternB);

        assertEquals(null, result.getAnnotation());
        assertEquals("idOne.1", result.getId());
        assertEquals("B", result.getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, result.getProcessContentsInstruction());
    }

    /**
     * Test of generateNewDifferenceAnyPattern method, of class
     * ParticleDifferenceGenerator for a set of any patterns with annotations.
     */
    @Test
    public void testGenerateNewDifferenceAnyPatternAnnotation() {
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "B");
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        anyPatternA.setAnnotation(annotationA);

        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "A");
        Annotation annotationB = new Annotation();
        AppInfo appInfoB = new AppInfo();
        appInfoB.setSource("blaB");
        Documentation documentationB = new Documentation();
        documentationB.setSource("blaaB");
        annotationB.addAppInfos(appInfoB);
        annotationB.addDocumentations(documentationB);
        anyPatternB.setAnnotation(annotationB);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        AnyPattern result = instance.generateNewDifferenceAnyPattern(anyPatternA, anyPatternB);

        assertTrue(result.getAnnotation().getAppInfos().size() == 1);
        assertEquals("blaA", result.getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(result.getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaaA", result.getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(null, result.getId());
        assertEquals("B", result.getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, result.getProcessContentsInstruction());
    }

    /**
     * Test of generateNewDifferenceAnyPattern method, of class
     * ParticleDifferenceGenerator for a single any pattern with
     * namespace="##any".
     */
    @Test
    public void testGenerateNewDifferenceAnyPatternSingleAny() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##any");
        anyPatterns.add(anyPatternA);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String expResult = "##any";
        AnyPattern result = instance.generateNewDifferenceAnyPattern(anyPatternA, null);

        assertEquals(expResult, result.getNamespace());
    }

    /**
     * Test of generateNewDifferenceAnyPattern method, of class
     * ParticleDifferenceGenerator for a single any pattern with
     * namespace="##other".
     */
    @Test
    public void testGenerateNewDifferenceAnyPatternSingleOther() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##other");
        anyPatterns.add(anyPatternA);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String expResult = "##other";
        AnyPattern result = instance.generateNewDifferenceAnyPattern(anyPatternA, null);

        assertEquals(expResult, result.getNamespace());
    }

    /**
     * Test of generateNewDifferenceAnyPattern method, of class
     * ParticleDifferenceGenerator for a single any pattern with
     * namespace="##local".
     */
    @Test
    public void testGenerateNewDifferenceAnyPatternSingleLocal() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##local");
        anyPatterns.add(anyPatternA);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String expResult = "##local";
        AnyPattern result = instance.generateNewDifferenceAnyPattern(anyPatternA, null);

        assertEquals(expResult, result.getNamespace());
    }

    /**
     * Test of generateNewDifferenceAnyPattern method, of class
     * ParticleDifferenceGenerator for a single any pattern with
     * namespace="##targetNamespace".
     */
    @Test
    public void testGenerateNewDifferenceAnyPatternSingleTargetNamespace() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##targetNamespace");
        anyPatterns.add(anyPatternA);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String expResult = "##targetNamespace";
        AnyPattern result = instance.generateNewDifferenceAnyPattern(anyPatternA, null);

        assertEquals(expResult, result.getNamespace());
    }

    /**
     * Test of generateNewDifferenceAnyPattern method, of class
     * ParticleDifferenceGenerator for a single any pattern with namespace="A".
     */
    @Test
    public void testGenerateNewDifferenceAnyPatternSingleUriA() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "A");
        anyPatterns.add(anyPatternA);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String expResult = "##targetNamespace";
        AnyPattern result = instance.generateNewDifferenceAnyPattern(anyPatternA, null);

        assertEquals(expResult, result.getNamespace());
    }

    /**
     * Test of generateNewDifferenceAnyPattern method, of class
     * ParticleDifferenceGenerator for a single any pattern with
     * namespace="B".
     */
    @Test
    public void testGenerateNewDifferenceAnyPatternSingleUriB() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "B");
        anyPatterns.add(anyPatternA);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String expResult = "B";
        AnyPattern result = instance.generateNewDifferenceAnyPattern(anyPatternA, null);

        assertEquals(expResult, result.getNamespace());
    }

    /**
     * Test of generateNewDifferenceAnyPattern method, of class
     * ParticleDifferenceGenerator for a single any pattern with
     * namespace="##other".
     */
    @Test
    public void testGenerateNewDifferenceAnyPatternSingleForeignOther() {
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##other");

        XSDSchema oldSchema = new XSDSchema("B");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String expResult = "##any";
        AnyPattern result = instance.generateNewDifferenceAnyPattern(anyPatternA, null);

        assertEquals(expResult, result.getNamespace());
    }

    /**
     * Test of generateNewDifferenceAnyPattern method, of class ParticleDifferenceGenerator for
     * a any patterns with namespace="##any" and "##local ##targetNamespace".
     */
    @Test
    public void testGenerateNewDifferenceAnyPatternAnyLocalTargetNamespace() {
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##any");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "##local ##targetNamespace");

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        anyPatternOldSchemaMap.put(anyPatternB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String expResult = "##other";
        AnyPattern result = instance.generateNewDifferenceAnyPattern(anyPatternA, anyPatternB);

        assertEquals(expResult, result.getNamespace());
    }

    /**
     * Test of generateNewDifferenceAnyPattern method, of class
     * ParticleDifferenceGenerator for a any patterns with
     * namespace="##any" and "##other".
     */
    @Test
    public void testGenerateNewDifferenceAnyPatternAnyOther() {
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##any");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "##other");

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        anyPatternOldSchemaMap.put(anyPatternB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String expResult = "##local ##targetNamespace";
        AnyPattern result = instance.generateNewDifferenceAnyPattern(anyPatternA, anyPatternB);

        assertEquals(expResult, result.getNamespace());
    }

    /**
     * Test of generateNewDifferenceAnyPattern method, of class
     * ParticleDifferenceGenerator for a any patterns with
     * namespace="##other" and "##targetNamespace ## local".
     */
    @Test
    public void testGenerateNewDifferenceAnyPatternForeignOtherTargetNamespaceLocal() {
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##other");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "##targetNamespace ##local");

        XSDSchema oldSchemaA = new XSDSchema("A");
        XSDSchema oldSchemaB = new XSDSchema("B");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchemaB);
        anyPatternOldSchemaMap.put(anyPatternB, oldSchemaA);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String expResult = "##other";
        AnyPattern result = instance.generateNewDifferenceAnyPattern(anyPatternA, anyPatternB);

        assertEquals(expResult, result.getNamespace());
    }

    /**
     * Test of generateNewDifferenceAnyPattern method, of class
     * ParticleDifferenceGenerator for a any patterns with
     * namespace="A B" and "A".
     */
    @Test
    public void testGenerateNewDifferenceAnyPatternUriABUriA() {
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "A B");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "B");

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        anyPatternOldSchemaMap.put(anyPatternB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String expResult = "##targetNamespace";
        AnyPattern result = instance.generateNewDifferenceAnyPattern(anyPatternA, anyPatternB);

        assertEquals(expResult, result.getNamespace());
    }

    /**
     * Test of generateNewDifferenceAnyPattern method, of class
     * ParticleDifferenceGenerator for a any patterns with
     * namespace="##any" and "A B".
     */
    @Test
    public void testGenerateNewDifferenceAnyPatternAnyUriAB() {
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##any");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "A B");

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        anyPatternOldSchemaMap.put(anyPatternB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String expResult = "##any";
        AnyPattern result = instance.generateNewDifferenceAnyPattern(anyPatternA, anyPatternB);

        assertEquals(expResult, result.getNamespace());
    }

    /**
     * Test of generateNewDifferenceAnyPattern method, of class
     * ParticleDifferenceGenerator for a any patterns with
     * namespace="targetNamespace" and "A B".
     */
    @Test
    public void testGenerateNewDifferenceAnyPatternUriABTargetNamespace() {
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "A B");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "##targetNamespace");

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        anyPatternOldSchemaMap.put(anyPatternB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String expResult = "B";
        AnyPattern result = instance.generateNewDifferenceAnyPattern(anyPatternA, anyPatternB);

        assertEquals(expResult, result.getNamespace());
    }

    /**
     * Test of areIntersectableAnyPatterns method, of class
     * ParticleDifferenceGenerator which returns true.
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
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        boolean expResult = true;
        boolean result = instance.areIntersectableAnyPatterns(anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of areIntersectableAnyPatterns method, of class
     * ParticleDifferenceGenerator which returns false.
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
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        boolean expResult = false;
        boolean result = instance.areIntersectableAnyPatterns(anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of generateNewIntersectionAnyPattern method, of class
     * ParticleDifferenceGenerator.
     */
    @Test
    public void testGenerateNewIntersectionAnyPattern() {
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##any");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "B");

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        AnyPattern result = instance.generateNewIntersectionAnyPattern(anyPatternA, anyPatternB);

        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getId());
        assertEquals("B", result.getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, result.getProcessContentsInstruction());
    }

    /**
     * Test of generateNewIntersectionAnyPattern method, of class
     * ParticleDifferenceGenerator for an empty any pattern.
     */
    @Test
    public void testGenerateNewIntersectionAnyPatternEmpty() {
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "A");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "B");

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        AnyPattern result = instance.generateNewIntersectionAnyPattern(anyPatternA, anyPatternB);

        assertEquals(null, result);
    }

    /**
     * Test of generateNewIntersectionAnyPattern method, of class
     * ParticleDifferenceGenerator for a set of any patterns with different
     * IDs.
     */
    @Test
    public void testGenerateNewIntersectionAnyPatternDifferentIDs() {
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "B");
        anyPatternA.setId("idOne");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "B");
        anyPatternB.setId("idTwo");

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("idOne");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, usedIDs);

        AnyPattern result = instance.generateNewIntersectionAnyPattern(anyPatternA, anyPatternB);

        assertEquals(null, result.getAnnotation());
        assertEquals("idOne.1", result.getId());
        assertEquals("B", result.getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, result.getProcessContentsInstruction());
    }

    /**
     * Test of generateNewIntersectionAnyPattern method, of class
     * ParticleDifferenceGenerator for a set of any patterns with same IDs.
     */
    @Test
    public void testGenerateNewIntersectionAnyPatternSameIDs() {
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "B");
        anyPatternA.setId("idOne");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "B");
        anyPatternB.setId("idOne");

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("idOne");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, usedIDs);

        AnyPattern result = instance.generateNewIntersectionAnyPattern(anyPatternA, anyPatternB);

        assertEquals(null, result.getAnnotation());
        assertEquals("idOne.1", result.getId());
        assertEquals("B", result.getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, result.getProcessContentsInstruction());
    }

    /**
     * Test of generateNewIntersectionAnyPattern method, of class
     * ParticleDifferenceGenerator for a set of any patterns with annotations.
     */
    @Test
    public void testGenerateNewIntersectionAnyPatternAnnotation() {
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

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        AnyPattern result = instance.generateNewIntersectionAnyPattern(anyPatternA, anyPatternB);

        assertTrue(result.getAnnotation().getAppInfos().size() == 1);
        assertEquals("blaA", result.getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(result.getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaaA", result.getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(null, result.getId());
        assertEquals("B", result.getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, result.getProcessContentsInstruction());
    }

    /**
     * Test of generateNewIntersectionAnyPattern method, of class
     * ParticleDifferenceGenerator for a single any pattern with
     * namespace="##any".
     */
    @Test
    public void testGenerateNewIntersectionAnyPatternSingleAny() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##any");
        anyPatterns.add(anyPatternA);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String expResult = "##any";
        AnyPattern result = instance.generateNewIntersectionAnyPattern(anyPatternA, null);

        assertEquals(expResult, result.getNamespace());
    }

    /**
     * Test of generateNewIntersectionAnyPattern method, of class
     * ParticleDifferenceGenerator for a single any pattern with
     * namespace="##other".
     */
    @Test
    public void testGenerateNewIntersectionAnyPatternSingleOther() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##other");
        anyPatterns.add(anyPatternA);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String expResult = "##other";
        AnyPattern result = instance.generateNewIntersectionAnyPattern(anyPatternA, null);

        assertEquals(expResult, result.getNamespace());
    }

    /**
     * Test of generateNewIntersectionAnyPattern method, of class
     * ParticleDifferenceGenerator for a single any pattern with
     * namespace="##local".
     */
    @Test
    public void testGenerateNewIntersectionAnyPatternSingleLocal() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##local");
        anyPatterns.add(anyPatternA);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String expResult = "##local";
        AnyPattern result = instance.generateNewIntersectionAnyPattern(anyPatternA, null);

        assertEquals(expResult, result.getNamespace());
    }

    /**
     * Test of generateNewIntersectionAnyPattern method, of class
     * ParticleDifferenceGenerator for a single any pattern with
     * namespace="##targetNamespace".
     */
    @Test
    public void testGenerateNewIntersectionAnyPatternSingleTargetNamespace() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##targetNamespace");
        anyPatterns.add(anyPatternA);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String expResult = "##targetNamespace";
        AnyPattern result = instance.generateNewIntersectionAnyPattern(anyPatternA, null);

        assertEquals(expResult, result.getNamespace());
    }

    /**
     * Test of generateNewIntersectionAnyPattern method, of class
     * ParticleDifferenceGenerator for a single any pattern with namespace="A".
     */
    @Test
    public void testGenerateNewIntersectionAnyPatternSingleUriA() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "A");
        anyPatterns.add(anyPatternA);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String expResult = "##targetNamespace";
        AnyPattern result = instance.generateNewIntersectionAnyPattern(anyPatternA, null);

        assertEquals(expResult, result.getNamespace());
    }

    /**
     * Test of generateNewIntersectionAnyPattern method, of class
     * ParticleDifferenceGenerator for a single any pattern with
     * namespace="B".
     */
    @Test
    public void testGenerateNewIntersectionAnyPatternSingleUriB() {
        LinkedHashSet<AnyPattern> anyPatterns = new LinkedHashSet<AnyPattern>();
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "B");
        anyPatterns.add(anyPatternA);

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String expResult = "B";
        AnyPattern result = instance.generateNewIntersectionAnyPattern(anyPatternA, null);

        assertEquals(expResult, result.getNamespace());
    }

    /**
     * Test of generateNewIntersectionAnyPattern method, of class
     * ParticleDifferenceGenerator for a single any pattern with
     * namespace="##other".
     */
    @Test
    public void testGenerateNewIntersectionAnyPatternSingleForeignOther() {
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##other");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "##other");

        XSDSchema oldSchema = new XSDSchema("B");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        anyPatternOldSchemaMap.put(anyPatternB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String expResult = "##any";
        AnyPattern result = instance.generateNewIntersectionAnyPattern(anyPatternA, anyPatternB);

        assertEquals(expResult, result.getNamespace());
    }

    /**
     * Test of generateNewIntersectionAnyPattern method, of class ParticleDifferenceGenerator for
     * a any patterns with namespace="##other" and "##local".
     */
    @Test
    public void testGenerateNewIntersectionAnyPatternOtherLocal() {
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##other");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "##local");

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        anyPatternOldSchemaMap.put(anyPatternB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String expResult = "";
        AnyPattern result = instance.generateNewIntersectionAnyPattern(anyPatternA, anyPatternB);

        assertEquals(null, result);
    }

    /**
     * Test of generateNewIntersectionAnyPattern method, of class
     * ParticleDifferenceGenerator for a any patterns with
     * namespace="##other" and "##targetNamespace".
     */
    @Test
    public void testGenerateNewIntersectionAnyPatternOtherTargetNamespace() {
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##other");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "##targetNamespace");

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        anyPatternOldSchemaMap.put(anyPatternB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String expResult = "";
        AnyPattern result = instance.generateNewIntersectionAnyPattern(anyPatternA, anyPatternB);

        assertEquals(null, result);
    }

    /**
     * Test of generateNewIntersectionAnyPattern method, of class
     * ParticleDifferenceGenerator for a any patterns with
     * namespace="##other" and "##targetNamespace".
     */
    @Test
    public void testGenerateNewIntersectionAnyPatternForeignOtherTargetNamespace() {
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##other");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "##targetNamespace");

        XSDSchema oldSchemaA = new XSDSchema("A");
        XSDSchema oldSchemaB = new XSDSchema("B");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchemaB);
        anyPatternOldSchemaMap.put(anyPatternB, oldSchemaA);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String expResult = "##targetNamespace";
        AnyPattern result = instance.generateNewIntersectionAnyPattern(anyPatternA, anyPatternB);

        assertEquals(expResult, result.getNamespace());
    }

    /**
     * Test of generateNewIntersectionAnyPattern method, of class
     * ParticleDifferenceGenerator for a any patterns with
     * namespace="A" and "A B".
     */
    @Test
    public void testGenerateNewIntersectionAnyPatternUriAUriAB() {
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "A");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "A B");

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        anyPatternOldSchemaMap.put(anyPatternB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String expResult = "##targetNamespace";
        AnyPattern result = instance.generateNewIntersectionAnyPattern(anyPatternA, anyPatternB);

        assertEquals(expResult, result.getNamespace());
    }

    /**
     * Test of generateNewIntersectionAnyPattern method, of class
     * ParticleDifferenceGenerator for a any patterns with
     * namespace="##any" and "A B".
     */
    @Test
    public void testGenerateNewIntersectionAnyPatternAnyUriAB() {
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##any");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "A B");

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        anyPatternOldSchemaMap.put(anyPatternB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String expResult = "##targetNamespace B";
        AnyPattern result = instance.generateNewIntersectionAnyPattern(anyPatternA, anyPatternB);

        assertEquals(expResult, result.getNamespace());
    }

    /**
     * Test of generateNewIntersectionAnyPattern method, of class
     * ParticleDifferenceGenerator for a any patterns with
     * namespace="targetNamespace" and "A B".
     */
    @Test
    public void testGenerateNewIntersectionAnyPatternTargetNamespaceUriAB() {
        AnyPattern anyPatternA = new AnyPattern(ProcessContentsInstruction.Skip, "##targetNamespace");
        AnyPattern anyPatternB = new AnyPattern(ProcessContentsInstruction.Skip, "A B");

        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPatternA, oldSchema);
        anyPatternOldSchemaMap.put(anyPatternB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String expResult = "##targetNamespace";
        AnyPattern result = instance.generateNewIntersectionAnyPattern(anyPatternA, anyPatternB);

        assertEquals(expResult, result.getNamespace());
    }

    /**
     * Test of isIncluded method, of class ParticleDifferenceGenerator for
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
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String elementName = "{B}foo";

        boolean expResult = true;
        boolean result = instance.isIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of isIncluded method, of class ParticleDifferenceGenerator for
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
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String elementName = "{B}foo";

        boolean expResult = true;
        boolean result = instance.isIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of isIncluded method, of class ParticleDifferenceGenerator for
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
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String elementName = "{}foo";

        boolean expResult = true;
        boolean result = instance.isIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of isIncluded method, of class ParticleDifferenceGenerator for
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
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String elementName = "{A}foo";

        boolean expResult = true;
        boolean result = instance.isIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of isIncluded method, of class ParticleDifferenceGenerator for
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
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String elementName = "{A}foo";

        boolean expResult = true;
        boolean result = instance.isIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of isIncluded method, of class ParticleDifferenceGenerator for
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
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String elementName = "{A}foo";

        boolean expResult = false;
        boolean result = instance.isIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of isIncluded method, of class ParticleDifferenceGenerator for
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
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String elementName = "{A}foo";

        boolean expResult = false;
        boolean result = instance.isIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of isIncluded method, of class ParticleDifferenceGenerator for
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
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String elementName = "{B}foo";

        boolean expResult = false;
        boolean result = instance.isIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }

    /**
     * Test of isIncluded method, of class ParticleDifferenceGenerator for
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
        ParticleDifferenceGenerator instance = new ParticleDifferenceGenerator(outputSchema, null, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);

        String elementName = "{C}foo";

        boolean expResult = false;
        boolean result = instance.isIncluded(elementName, anyPatterns);

        assertEquals(expResult, result);
    }
}