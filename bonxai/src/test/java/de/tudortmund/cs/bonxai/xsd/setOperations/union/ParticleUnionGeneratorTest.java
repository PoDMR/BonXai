package de.tudortmund.cs.bonxai.xsd.setOperations.union;

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
import de.tudortmund.cs.bonxai.xsd.Element.Block;
import de.tudortmund.cs.bonxai.xsd.Element.Final;
import de.tudortmund.cs.bonxai.xsd.*;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>ParticleUnionGenerator</tt> class, checks that every
 * method of this class performs properly.
 * @author Dominik Wolff
 */
public class ParticleUnionGeneratorTest extends junit.framework.TestCase  {

    public ParticleUnionGeneratorTest() {
    }

    /**
     * Test of setTypeUnionGeneratornion method, of class ParticleUnionGenerator.
     */
    @Test
    public void testSetTypeUnionGeneratornion() {
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        Particle oldParticle = elementA;
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{A}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) result).getType().getName());
    }

    /**
     * Test of getContainedElements method, of class ParticleUnionGenerator for
     * a skip validated any pattern.
     */
    @Test
    public void testGetContainedElementsSkip() {
        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Skip, "##any");

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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, elementTypeMap, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        LinkedHashSet<Element> expResult = null;
        LinkedHashSet<Element> result = instance.getContainedElements(anyPattern);

        assertEquals(expResult, result);
    }

    /**
     * Test of getContainedElements method, of class ParticleUnionGenerator for
     * a strict validated any pattern.
     */
    @Test
    public void testGetContainedElementsStrict() {
        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Strict, "##any");

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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, elementTypeMap, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        LinkedHashSet<Element> expResult = new LinkedHashSet<Element>();
        expResult.add(elementA);
        LinkedHashSet<Element> result = instance.getContainedElements(anyPattern);

        assertEquals(expResult, result);
    }

    /**
     * Test of getContainedElements method, of class ParticleUnionGenerator for
     * a lax validated any pattern.
     */
    @Test
    public void testGetContainedElementsLax() {
        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Lax, "##any");

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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, elementTypeMap, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        LinkedHashSet<Element> expResult = null;
        LinkedHashSet<Element> result = instance.getContainedElements(anyPattern);

        assertEquals(expResult, result);
    }

    /**
     * Test of getContainedElements method, of class ParticleUnionGenerator for
     * a strict validated any pattern with uris.
     */
    @Test
    public void testGetContainedElementsUris() {
        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Strict, "A B");

        Element elementA = new Element("{A}elementA");
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        Element elementB = new Element("{B}elementB");
        SimpleType simpleTypeB = new SimpleType("{B}simpleTypeB", null);

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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, elementTypeMap, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        LinkedHashSet<Element> expResult = new LinkedHashSet<Element>();
        expResult.add(elementA);
        expResult.add(elementB);
        LinkedHashSet<Element> result = instance.getContainedElements(anyPattern);

        assertEquals(expResult, result);
    }

    /**
     * Test of getContainedElements method, of class ParticleUnionGenerator for
     * a strict validated any pattern with "##targetNamespace".
     */
    @Test
    public void testGetContainedElementsTarget() {
        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Strict, "##targetNamespace");

        Element elementA = new Element("{A}elementA");
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        Element elementB = new Element("{}elementB");
        SimpleType simpleTypeB = new SimpleType("{}simpleTypeB", null);

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
        oldSchemaB.getElementSymbolTable().updateOrCreateReference("{B}elementB", elementB);
        oldSchemaB.addElement(oldSchemaB.getElementSymbolTable().getReference("{B}elementB"));
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPattern, oldSchemaA);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, elementTypeMap, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        LinkedHashSet<Element> expResult = new LinkedHashSet<Element>();
        expResult.add(elementA);
        LinkedHashSet<Element> result = instance.getContainedElements(anyPattern);

        assertEquals(expResult, result);
    }

    /**
     * Test of getContainedElements method, of class ParticleUnionGenerator for
     * a strict validated any pattern with "##local".
     */
    @Test
    public void testGetContainedElementsLocal() {
        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Strict, "##local");

        Element elementA = new Element("{A}elementA");
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        Element elementB = new Element("{}elementB");
        SimpleType simpleTypeB = new SimpleType("{}simpleTypeB", null);

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
        oldSchemaB.getElementSymbolTable().updateOrCreateReference("{B}elementB", elementB);
        oldSchemaB.addElement(oldSchemaB.getElementSymbolTable().getReference("{B}elementB"));
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPattern, oldSchemaA);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, elementTypeMap, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        LinkedHashSet<Element> expResult = new LinkedHashSet<Element>();
        expResult.add(elementB);
        LinkedHashSet<Element> result = instance.getContainedElements(anyPattern);

        assertEquals(expResult, result);
    }

    /**
     * Test of getContainedElements method, of class ParticleUnionGenerator for
     * a strict validated any pattern with "##other".
     */
    @Test
    public void testGetContainedElementsOther() {
        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Strict, "##other");

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
        oldSchemaB.getElementSymbolTable().updateOrCreateReference("{B}elementB", elementB);
        oldSchemaB.addElement(oldSchemaB.getElementSymbolTable().getReference("{B}elementB"));
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPattern, oldSchemaA);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, elementTypeMap, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        LinkedHashSet<Element> expResult = new LinkedHashSet<Element>();
        expResult.add(elementB);
        LinkedHashSet<Element> result = instance.getContainedElements(anyPattern);

        assertEquals(expResult, result);
    }

    /**
     * Test of generateParticleUnion method, of class ParticleUnionGenerator for
     * a single particle.
     */
    @Test
    public void testGenerateParticleUnionSingleParticle() {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        particles.add(elementA);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));

        Particle result = instance.generateParticleUnion(particles, elementTypeMap);

        assertTrue(result instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(true, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElementSymbolTable().hasReference(((Element) result).getName()));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of generateParticleUnion method, of class ParticleUnionGenerator for
     * a set of particle.
     */
    @Test
    public void testGenerateParticleUnionParticles() {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();
        Element elementA = new Element("{A}elementA");
        Element elementB = new Element("{A}elementB");
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        particles.add(elementA);
        particles.add(elementB);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        elementTypeMap.put("{}elementB", new SymbolTableRef<Type>("{A}simpleTypeB", simpleTypeB));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));

        Particle result = instance.generateParticleUnion(particles, elementTypeMap);

        assertTrue(result instanceof ChoicePattern);
        assertTrue(((ChoicePattern) result).getParticles().size() == 2);
        assertTrue(((ChoicePattern) result).getParticles().getFirst() instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) ((ChoicePattern) result).getParticles().getFirst()).getType().getName());
        assertEquals(false, ((Element) ((ChoicePattern) result).getParticles().getFirst()).getType().isAnonymous());
        assertEquals(true, (boolean) ((Element) ((ChoicePattern) result).getParticles().getFirst()).getTypeAttr());
        assertEquals(null, ((Element) ((ChoicePattern) result).getParticles().getFirst()).getDefault());
        assertEquals(null, ((Element) ((ChoicePattern) result).getParticles().getFirst()).getFixed());
        assertEquals(null, ((Element) ((ChoicePattern) result).getParticles().getFirst()).getId());
        assertEquals(null, ((Element) ((ChoicePattern) result).getParticles().getFirst()).getAnnotation());
        assertEquals(false, (boolean) ((Element) ((ChoicePattern) result).getParticles().getFirst()).getAbstract());
        assertEquals(false, (boolean) ((Element) ((ChoicePattern) result).getParticles().getFirst()).getNillable());
        assertFalse(outputSchema.getElementSymbolTable().hasReference(((Element) ((ChoicePattern) result).getParticles().getFirst()).getName()));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) ((ChoicePattern) result).getParticles().getFirst()).getName()).getReference()));
        assertEquals(null, ((Element) ((ChoicePattern) result).getParticles().getFirst()).getForm());
        assertTrue(((ChoicePattern) result).getParticles().getLast() instanceof Element);
        assertEquals("{A}simpleTypeB", ((Element) ((ChoicePattern) result).getParticles().getLast()).getType().getName());
        assertEquals(false, ((Element) ((ChoicePattern) result).getParticles().getLast()).getType().isAnonymous());
        assertEquals(true, (boolean) ((Element) ((ChoicePattern) result).getParticles().getLast()).getTypeAttr());
        assertEquals(null, ((Element) ((ChoicePattern) result).getParticles().getLast()).getDefault());
        assertEquals(null, ((Element) ((ChoicePattern) result).getParticles().getLast()).getFixed());
        assertEquals(null, ((Element) ((ChoicePattern) result).getParticles().getLast()).getId());
        assertEquals(null, ((Element) ((ChoicePattern) result).getParticles().getLast()).getAnnotation());
        assertEquals(false, (boolean) ((Element) ((ChoicePattern) result).getParticles().getLast()).getAbstract());
        assertEquals(false, (boolean) ((Element) ((ChoicePattern) result).getParticles().getLast()).getNillable());
        assertFalse(outputSchema.getElementSymbolTable().hasReference(((Element) ((ChoicePattern) result).getParticles().getLast()).getName()));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) ((ChoicePattern) result).getParticles().getLast()).getName()).getReference()));
        assertEquals(null, ((Element) ((ChoicePattern) result).getParticles().getLast()).getForm());
    }

    /**
     * Test of generateParticleUnion method, of class ParticleUnionGenerator for
     * no particle.
     */
    @Test
    public void testGenerateParticleUnionNoParticle() {
        LinkedHashSet<Particle> particles = new LinkedHashSet<Particle>();

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));

        Particle result = instance.generateParticleUnion(particles, elementTypeMap);

        assertEquals(null, result);
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, elementTypeMap, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof ChoicePattern);
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(((ChoicePattern) result).getParticles().size() == 1);
        assertTrue(((ChoicePattern) result).getParticles().getFirst() instanceof Element);

        Particle resultingElement = (Element) ((ChoicePattern) result).getParticles().getFirst();
        assertEquals("{A}simpleTypeA", ((Element) resultingElement).getType().getName());
        assertEquals(false, ((Element) resultingElement).getType().isAnonymous());
        assertEquals(true, (boolean) ((Element) resultingElement).getTypeAttr());
        assertEquals(null, ((Element) resultingElement).getDefault());
        assertEquals(null, ((Element) resultingElement).getFixed());
        assertEquals(null, ((Element) resultingElement).getId());
        assertEquals(null, ((Element) resultingElement).getAnnotation());
        assertEquals(false, (boolean) ((Element) resultingElement).getAbstract());
        assertEquals(false, (boolean) ((Element) resultingElement).getNillable());
        assertFalse(outputSchema.getElementSymbolTable().hasReference(((Element) resultingElement).getName()));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) resultingElement).getName()).getReference()));
        assertEquals(null, ((Element) resultingElement).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, elementTypeMap, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertEquals(null, result);
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, elementTypeMap, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof AnyPattern);
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals("##local", ((AnyPattern) result).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyPattern) result).getProcessContentsInstruction());
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, elementTypeMap, usedIDs, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof AnyPattern);
        assertEquals("id.1", result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals("##local", ((AnyPattern) result).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyPattern) result).getProcessContentsInstruction());
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, elementTypeMap, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

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
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, elementTypeMap, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof AnyPattern);
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals("##local ##targetNamespace", ((AnyPattern) result).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyPattern) result).getProcessContentsInstruction());
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, elementTypeMap, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof AnyPattern);
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals("##targetNamespace", ((AnyPattern) result).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyPattern) result).getProcessContentsInstruction());
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, elementTypeMap, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof AnyPattern);
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals("B C D", ((AnyPattern) result).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyPattern) result).getProcessContentsInstruction());
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, elementTypeMap, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof AnyPattern);
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals("##any", ((AnyPattern) result).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyPattern) result).getProcessContentsInstruction());
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(true, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElementSymbolTable().hasReference(((Element) result).getName()));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

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
        assertFalse(outputSchema.getElementSymbolTable().hasReference(((Element) result).getName()));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

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
        assertFalse(outputSchema.getElementSymbolTable().hasReference(((Element) result).getName()));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(true, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(true, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElementSymbolTable().hasReference(((Element) result).getName()));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(true, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElementSymbolTable().hasReference(((Element) result).getName()));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
     * an foreign qualified element.
     */
    @Test
    public void testGenerateNewParticleElementForeignQualified() {
        Element elementA = new Element("{A}elementA");
        elementA.setForm(XSDSchema.Qualification.qualified);
        Particle oldParticle = elementA;
        SimpleType simpleTypeA = new SimpleType("{B}simpleTypeA", null);

        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{A}elementA", new SymbolTableRef<Type>("{B}simpleTypeA", simpleTypeA));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("B");
        outputSchema.setSchemaLocation("Main");
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("B", outputSchema);
        LinkedHashSet<XSDSchema> otherSchemata = new LinkedHashSet<XSDSchema>();
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, elementOldSchemaMap, null, null, null, null, otherSchemata, namespaceAbbreviationMap, "C:/");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(new SimpleType("{A}simpleTypeA", null), new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(otherSchemata.size() == 1);
        assertTrue(otherSchemata.iterator().next().getGroups().contains(((GroupRef) result).getGroup()));
        assertTrue(otherSchemata.iterator().next().getGroupSymbolTable().hasReference(((GroupRef) result).getGroup().getName()));
        assertEquals(null, ((Group) ((GroupRef) result).getGroup()).getId());
        assertEquals(null, ((Group) ((GroupRef) result).getGroup()).getAnnotation());
        assertTrue(((Group) ((GroupRef) result).getGroup()).getParticleContainer().getParticles().size() == 1);
        Particle resultingElement = ((Group) ((GroupRef) result).getGroup()).getParticleContainer().getParticles().getFirst();
        assertEquals("{B}simpleTypeA", ((Element) resultingElement).getType().getName());
        assertEquals(false, ((Element) resultingElement).getType().isAnonymous());
        assertEquals(true, (boolean) ((Element) resultingElement).getTypeAttr());
        assertEquals(elementA.getDefault(), ((Element) resultingElement).getDefault());
        assertEquals(elementA.getFixed(), ((Element) resultingElement).getFixed());
        assertEquals(null, ((Element) resultingElement).getId());
        assertEquals(null, ((Element) resultingElement).getAnnotation());
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Element) resultingElement).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getElementSymbolTable().getReference(((Element) resultingElement).getName()).getReference()));
        assertEquals(XSDSchema.Qualification.qualified, ((Element) resultingElement).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(new SimpleType("{A}simpleTypeA", null), new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof Element);
        assertEquals("{B}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(true, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElementSymbolTable().hasReference(((Element) result).getName()));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(XSDSchema.Qualification.unqualified, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(true, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElementSymbolTable().hasReference(((Element) result).getName()));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(true, (boolean) ((Element) result).getTypeAttr());
        assertEquals("bla", ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElementSymbolTable().hasReference(((Element) result).getName()));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(true, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals("bla", ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElementSymbolTable().hasReference(((Element) result).getName()));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(true, (boolean) ((Element) result).getTypeAttr());
        HashSet<Element.Block> blocks = new HashSet<Element.Block>();
        blocks.add(Element.Block.extension);
        assertEquals(blocks, ((Element) result).getBlockModifiers());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElementSymbolTable().hasReference(((Element) result).getName()));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(true, (boolean) ((Element) result).getTypeAttr());
        HashSet<Element.Block> blocks = new HashSet<Element.Block>();
        blocks.add(Element.Block.extension);
        assertEquals(blocks, ((Element) result).getBlockModifiers());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElementSymbolTable().hasReference(((Element) result).getName()));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, usedIDs, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(true, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals("id.1", ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElementSymbolTable().hasReference(((Element) result).getName()));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(true, (boolean) ((Element) result).getTypeAttr());
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
        assertFalse(outputSchema.getElementSymbolTable().hasReference(((Element) result).getName()));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(null, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, namespaceConflictingElementsMap, elementTypeMap, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof ElementRef);
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(((ElementRef) result).getElement() == null);
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, namespaceConflictingElementsMap, elementTypeMap, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof ElementRef);
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(((ElementRef) result).getElement() == null);
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, namespaceConflictingElementsMap, elementTypeMap, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertEquals(null, result);
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, namespaceConflictingElementsMap, elementTypeMap, usedIDs, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof ElementRef);
        assertEquals("id.1", result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(((ElementRef) result).getElement() == null);
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, namespaceConflictingElementsMap, elementTypeMap, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

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
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, namespaceConflictingElementsMap, elementTypeMap, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(true, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElementSymbolTable().hasReference(((Element) result).getName()));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, namespaceConflictingElementsMap, elementTypeMap, usedIDs, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(true, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals("idTwo.idOne.1", result.getId());
        assertEquals(null, ((Element) result).getAnnotation());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElementSymbolTable().hasReference(((Element) result).getName()));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, namespaceConflictingElementsMap, elementTypeMap, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) result).getType().getName());
        assertEquals(false, ((Element) result).getType().isAnonymous());
        assertEquals(true, (boolean) ((Element) result).getTypeAttr());
        assertEquals(null, ((Element) result).getDefault());
        assertEquals(null, ((Element) result).getFixed());
        assertEquals(null, ((Element) result).getId());
        assertTrue(result.getAnnotation().getAppInfos().size() == 2);
        assertEquals("blaB", result.getAnnotation().getAppInfos().get(0).getSource());
        assertEquals("blaA", result.getAnnotation().getAppInfos().get(1).getSource());
        assertTrue(result.getAnnotation().getDocumentations().size() == 2);
        assertEquals("blaaB", result.getAnnotation().getDocumentations().get(0).getSource());
        assertEquals("blaaA", result.getAnnotation().getDocumentations().get(1).getSource());
        assertEquals(false, (boolean) ((Element) result).getAbstract());
        assertEquals(false, (boolean) ((Element) result).getNillable());
        assertFalse(outputSchema.getElementSymbolTable().hasReference(((Element) result).getName()));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) result).getName()).getReference()));
        assertEquals(XSDSchema.Qualification.qualified, ((Element) result).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof GroupRef);
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(((GroupRef) result).getGroup() instanceof Group);
        assertEquals(null, ((Group) ((GroupRef) result).getGroup()).getId());
        assertEquals(null, ((Group) ((GroupRef) result).getGroup()).getAnnotation());
        assertTrue(((Group) ((GroupRef) result).getGroup()).getParticleContainer() instanceof ChoicePattern);
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, usedIDs, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof GroupRef);
        assertEquals("id.1", result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(((GroupRef) result).getGroup() instanceof Group);
        assertEquals(null, ((Group) ((GroupRef) result).getGroup()).getId());
        assertEquals(null, ((Group) ((GroupRef) result).getGroup()).getAnnotation());
        assertTrue(((Group) ((GroupRef) result).getGroup()).getParticleContainer() instanceof ChoicePattern);
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

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
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, usedIDs, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof GroupRef);
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(((GroupRef) result).getGroup() instanceof Group);
        assertEquals("id.1", ((Group) ((GroupRef) result).getGroup()).getId());
        assertEquals(null, ((Group) ((GroupRef) result).getGroup()).getAnnotation());
        assertTrue(((Group) ((GroupRef) result).getGroup()).getParticleContainer() instanceof ChoicePattern);
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

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
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof ChoicePattern);
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(((ChoicePattern) result).getParticles().getFirst() instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) ((ChoicePattern) result).getParticles().getFirst()).getType().getName());
        assertEquals(false, ((Element) ((ChoicePattern) result).getParticles().getFirst()).getType().isAnonymous());
        assertEquals(true, (boolean) ((Element) ((ChoicePattern) result).getParticles().getFirst()).getTypeAttr());
        assertEquals(null, ((Element) ((ChoicePattern) result).getParticles().getFirst()).getDefault());
        assertEquals(null, ((Element) ((ChoicePattern) result).getParticles().getFirst()).getFixed());
        assertEquals(null, ((Element) ((ChoicePattern) result).getParticles().getFirst()).getId());
        assertEquals(null, ((Element) ((ChoicePattern) result).getParticles().getFirst()).getAnnotation());
        assertEquals(false, (boolean) ((Element) ((ChoicePattern) result).getParticles().getFirst()).getAbstract());
        assertEquals(false, (boolean) ((Element) ((ChoicePattern) result).getParticles().getFirst()).getNillable());
        assertFalse(outputSchema.getElementSymbolTable().hasReference((((Element) ((ChoicePattern) result).getParticles().getFirst())).getName()));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) ((ChoicePattern) result).getParticles().getFirst()).getName()).getReference()));
        assertEquals(null, (((Element) ((ChoicePattern) result).getParticles().getFirst())).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof ChoicePattern);
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(((ChoicePattern) result).getParticles().getFirst() instanceof Element);
        assertEquals("{A}simpleTypeA", ((Element) ((ChoicePattern) result).getParticles().getFirst()).getType().getName());
        assertEquals(false, ((Element) ((ChoicePattern) result).getParticles().getFirst()).getType().isAnonymous());
        assertEquals(true, (boolean) ((Element) ((ChoicePattern) result).getParticles().getFirst()).getTypeAttr());
        assertEquals(null, ((Element) ((ChoicePattern) result).getParticles().getFirst()).getDefault());
        assertEquals(null, ((Element) ((ChoicePattern) result).getParticles().getFirst()).getFixed());
        assertEquals(null, ((Element) ((ChoicePattern) result).getParticles().getFirst()).getId());
        assertEquals(null, ((Element) ((ChoicePattern) result).getParticles().getFirst()).getAnnotation());
        assertEquals(false, (boolean) ((Element) ((ChoicePattern) result).getParticles().getFirst()).getAbstract());
        assertEquals(false, (boolean) ((Element) ((ChoicePattern) result).getParticles().getFirst()).getNillable());
        assertFalse(outputSchema.getElementSymbolTable().hasReference((((Element) ((ChoicePattern) result).getParticles().getFirst())).getName()));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) ((ChoicePattern) result).getParticles().getFirst()).getName()).getReference()));
        assertEquals(null, (((Element) ((ChoicePattern) result).getParticles().getFirst())).getForm());
        assertEquals("{A}simpleTypeB", ((Element) ((ChoicePattern) result).getParticles().get(1)).getType().getName());
        assertEquals(false, ((Element) ((ChoicePattern) result).getParticles().get(1)).getType().isAnonymous());
        assertEquals(true, (boolean) ((Element) ((ChoicePattern) result).getParticles().get(1)).getTypeAttr());
        assertEquals(null, ((Element) ((ChoicePattern) result).getParticles().get(1)).getDefault());
        assertEquals(null, ((Element) ((ChoicePattern) result).getParticles().get(1)).getFixed());
        assertEquals(null, ((Element) ((ChoicePattern) result).getParticles().get(1)).getId());
        assertEquals(null, ((Element) ((ChoicePattern) result).getParticles().get(1)).getAnnotation());
        assertEquals(false, (boolean) ((Element) ((ChoicePattern) result).getParticles().get(1)).getAbstract());
        assertEquals(false, (boolean) ((Element) ((ChoicePattern) result).getParticles().get(1)).getNillable());
        assertFalse(outputSchema.getElementSymbolTable().hasReference((((Element) ((ChoicePattern) result).getParticles().get(1))).getName()));
        assertFalse(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(((Element) ((ChoicePattern) result).getParticles().get(1)).getName()).getReference()));
        assertEquals(null, (((Element) ((ChoicePattern) result).getParticles().get(1))).getForm());
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof CountingPattern);
        assertEquals(0, (int) ((CountingPattern) result).getMin());
        assertEquals(4, (int) ((CountingPattern) result).getMax());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(((CountingPattern) result).getParticles().getFirst() instanceof AllPattern);
        assertTrue(((AllPattern) ((CountingPattern) result).getParticles().getFirst()).getParticles().size() == 3);
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, usedIDs, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

        assertTrue(result instanceof AllPattern);
        assertEquals("id.1", result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(((AllPattern) result).getParticles().size() == 3);
    }

    /**
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, false);

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
     * Test of generateNewParticle method, of class ParticleUnionGenerator for
     * a particle container with resolved all pattern.
     */
    @Test
    public void testGenerateNewParticleParticleContainerAllResolve() {
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
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, null, null, null, null));
        instance.generateParticleUnion(new LinkedHashSet<Particle>(), elementTypeMap);

        Particle result = instance.generateNewParticle(oldParticle, true);

        assertTrue(result instanceof ChoicePattern);
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(((ChoicePattern) result).getParticles().size() == 4);
        assertTrue(((ChoicePattern) result).getParticles().get(0) instanceof SequencePattern);
        assertTrue(((SequencePattern) ((ChoicePattern) result).getParticles().get(0)).getParticles().size() == 2);
        assertTrue(((SequencePattern) ((ChoicePattern) result).getParticles().get(0)).getParticles().get(0) instanceof Element);
        assertEquals("{A}elementA", ((Element) ((SequencePattern) ((ChoicePattern) result).getParticles().get(0)).getParticles().get(0)).getName());
        assertTrue(((SequencePattern) ((ChoicePattern) result).getParticles().get(0)).getParticles().get(1) instanceof Element);
        assertEquals("{A}elementB", ((Element) ((SequencePattern) ((ChoicePattern) result).getParticles().get(0)).getParticles().get(1)).getName());
        assertTrue(((ChoicePattern) result).getParticles().get(1) instanceof SequencePattern);
        assertTrue(((SequencePattern) ((ChoicePattern) result).getParticles().get(1)).getParticles().size() == 2);
        assertTrue(((SequencePattern) ((ChoicePattern) result).getParticles().get(1)).getParticles().get(0) instanceof Element);
        assertEquals("{A}elementB", ((Element) ((SequencePattern) ((ChoicePattern) result).getParticles().get(1)).getParticles().get(0)).getName());
        assertTrue(((SequencePattern) ((ChoicePattern) result).getParticles().get(1)).getParticles().get(1) instanceof Element);
        assertEquals("{A}elementA", ((Element) ((SequencePattern) ((ChoicePattern) result).getParticles().get(1)).getParticles().get(1)).getName());
        assertTrue(((ChoicePattern) result).getParticles().get(2) instanceof Element);
        assertEquals("{A}elementA", ((Element) ((ChoicePattern) result).getParticles().get(2)).getName());
        assertTrue(((ChoicePattern) result).getParticles().get(3) instanceof Element);
        assertEquals("{A}elementB", ((Element) ((ChoicePattern) result).getParticles().get(3)).getName());
    }

    /**
     * Test of generateNewTopLevelElement method, of class ParticleUnionGenerator
     * for an element set which finalizes extension.
     */
    @Test
    public void testGenerateNewTopLevelElementFinalExtension() {
        LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.addFinalModifier(Element.Final.extension);
        elementA.addFinalModifier(Element.Final.restriction);
        topLevelElements.add(elementA);

        Element elementB = new Element("{A}elementB");
        elementB.addFinalModifier(Element.Final.extension);
        topLevelElements.add(elementB);

        Element elementC = new Element("{A}elementC");
        elementC.addFinalModifier(Element.Final.extension);
        elementC.addFinalModifier(Element.Final.restriction);
        topLevelElements.add(elementC);

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        elementOldSchemaMap.put(elementC, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Element result = instance.generateNewTopLevelElement(topLevelElements, typeRef);

        assertEquals("{A}simpleTypeA", result.getType().getName());
        assertEquals(false, result.getType().isAnonymous());
        assertEquals(true, (boolean) result.getTypeAttr());
        HashSet<Final> finalValue = new HashSet<Final>();
        finalValue.add(Final.extension);
        assertEquals(finalValue, result.getFinalModifiers());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertTrue(outputSchema.getElementSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelElement method, of class ParticleUnionGenerator
     * for an element set which finalizes restriction.
     */
    @Test
    public void testGenerateNewTopLevelElementFinalRestriction() {
        LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.addFinalModifier(Element.Final.extension);
        elementA.addFinalModifier(Element.Final.restriction);
        topLevelElements.add(elementA);

        Element elementB = new Element("{A}elementB");
        elementB.addFinalModifier(Element.Final.extension);
        elementB.addFinalModifier(Element.Final.restriction);
        topLevelElements.add(elementB);

        Element elementC = new Element("{A}elementC");
        elementC.addFinalModifier(Element.Final.restriction);
        topLevelElements.add(elementC);

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        elementOldSchemaMap.put(elementC, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Element result = instance.generateNewTopLevelElement(topLevelElements, typeRef);

        assertEquals("{A}simpleTypeA", result.getType().getName());
        assertEquals(false, result.getType().isAnonymous());
        assertEquals(true, (boolean) result.getTypeAttr());
        HashSet<Final> finalValue = new HashSet<Final>();
        finalValue.add(Final.restriction);
        assertEquals(finalValue, result.getFinalModifiers());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertTrue(outputSchema.getElementSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelElement method, of class ParticleUnionGenerator
     * for an element set which finalizes extension.
     */
    @Test
    public void testGenerateNewTopLevelElementFinalDefaultExtension() {
        LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.addFinalModifier(Element.Final.extension);
        elementA.addFinalModifier(Element.Final.restriction);
        topLevelElements.add(elementA);

        Element elementB = new Element("{A}elementB");
        topLevelElements.add(elementB);

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.extension);
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Element result = instance.generateNewTopLevelElement(topLevelElements, typeRef);

        assertEquals("{A}simpleTypeA", result.getType().getName());
        assertEquals(false, result.getType().isAnonymous());
        assertEquals(true, (boolean) result.getTypeAttr());
        HashSet<Final> finalValue = new HashSet<Final>();
        finalValue.add(Final.extension);
        assertEquals(null, result.getFinalModifiers());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertTrue(outputSchema.getElementSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelElement method, of class ParticleUnionGenerator
     * for an element set which finalizes restriction.
     */
    @Test
    public void testGenerateNewTopLevelElementFinalDefaulRestriction() {
        LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.addFinalModifier(Element.Final.extension);
        elementA.addFinalModifier(Element.Final.restriction);
        topLevelElements.add(elementA);

        Element elementB = new Element("{A}elementB");
        topLevelElements.add(elementB);

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Element result = instance.generateNewTopLevelElement(topLevelElements, typeRef);

        assertEquals("{A}simpleTypeA", result.getType().getName());
        assertEquals(false, result.getType().isAnonymous());
        assertEquals(true, (boolean) result.getTypeAttr());
        HashSet<Final> finalValue = new HashSet<Final>();
        finalValue.add(Final.restriction);
        assertEquals(null, result.getFinalModifiers());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertTrue(outputSchema.getElementSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelElement method, of class ParticleUnionGenerator
     * for an element set which finalizes substitution which is schema default.
     */
    @Test
    public void testGenerateNewTopLevelElementFinalDefault() {
        LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.addFinalModifier(Element.Final.extension);
        elementA.addFinalModifier(Element.Final.restriction);
        topLevelElements.add(elementA);

        Element elementB = new Element("{A}elementB");
        topLevelElements.add(elementB);

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Element result = instance.generateNewTopLevelElement(topLevelElements, typeRef);

        assertEquals("{A}simpleTypeA", result.getType().getName());
        assertEquals(false, result.getType().isAnonymous());
        assertEquals(true, (boolean) result.getTypeAttr());
        assertEquals(null, result.getFinalModifiers());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertTrue(outputSchema.getElementSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelElement method, of class ParticleUnionGenerator
     * for an element set which blocks extension.
     */
    @Test
    public void testGenerateNewTopLevelElementBlockExtension() {
        LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.addBlockModifier(Element.Block.extension);
        elementA.addBlockModifier(Element.Block.restriction);
        elementA.addBlockModifier(Element.Block.substitution);
        topLevelElements.add(elementA);

        Element elementB = new Element("{A}elementB");
        elementB.addBlockModifier(Element.Block.extension);
        elementB.addBlockModifier(Element.Block.substitution);
        topLevelElements.add(elementB);

        Element elementC = new Element("{A}elementC");
        elementC.addBlockModifier(Element.Block.extension);
        elementC.addBlockModifier(Element.Block.restriction);
        topLevelElements.add(elementC);

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        elementOldSchemaMap.put(elementC, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Element result = instance.generateNewTopLevelElement(topLevelElements, typeRef);

        assertEquals("{A}simpleTypeA", result.getType().getName());
        assertEquals(false, result.getType().isAnonymous());
        assertEquals(true, (boolean) result.getTypeAttr());
        HashSet<Block> block = new HashSet<Block>();
        block.add(Block.extension);
        assertEquals(block, result.getBlockModifiers());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertTrue(outputSchema.getElementSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelElement method, of class ParticleUnionGenerator
     * for an element set which blocks restriction.
     */
    @Test
    public void testGenerateNewTopLevelElementBlockRestriction() {
        LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.addBlockModifier(Element.Block.extension);
        elementA.addBlockModifier(Element.Block.restriction);
        elementA.addBlockModifier(Element.Block.substitution);
        topLevelElements.add(elementA);

        Element elementB = new Element("{A}elementB");
        elementB.addBlockModifier(Element.Block.extension);
        elementB.addBlockModifier(Element.Block.restriction);
        topLevelElements.add(elementB);

        Element elementC = new Element("{A}elementC");
        elementC.addBlockModifier(Element.Block.substitution);
        elementC.addBlockModifier(Element.Block.restriction);
        topLevelElements.add(elementC);

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        elementOldSchemaMap.put(elementC, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Element result = instance.generateNewTopLevelElement(topLevelElements, typeRef);

        assertEquals("{A}simpleTypeA", result.getType().getName());
        assertEquals(false, result.getType().isAnonymous());
        assertEquals(true, (boolean) result.getTypeAttr());
        HashSet<Block> block = new HashSet<Block>();
        block.add(Block.restriction);
        assertEquals(block, result.getBlockModifiers());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertTrue(outputSchema.getElementSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelElement method, of class ParticleUnionGenerator
     * for an element set which blocks substituttion.
     */
    @Test
    public void testGenerateNewTopLevelElementBlockSubstitution() {
        LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.addBlockModifier(Element.Block.extension);
        elementA.addBlockModifier(Element.Block.restriction);
        elementA.addBlockModifier(Element.Block.substitution);
        topLevelElements.add(elementA);

        Element elementB = new Element("{A}elementB");
        elementB.addBlockModifier(Element.Block.restriction);
        elementB.addBlockModifier(Element.Block.substitution);
        topLevelElements.add(elementB);

        Element elementC = new Element("{A}elementC");
        elementC.addBlockModifier(Element.Block.extension);
        elementC.addBlockModifier(Element.Block.substitution);
        topLevelElements.add(elementC);

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        elementOldSchemaMap.put(elementC, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Element result = instance.generateNewTopLevelElement(topLevelElements, typeRef);

        assertEquals("{A}simpleTypeA", result.getType().getName());
        assertEquals(false, result.getType().isAnonymous());
        assertEquals(true, (boolean) result.getTypeAttr());
        HashSet<Block> block = new HashSet<Block>();
        block.add(Block.substitution);
        assertEquals(block, result.getBlockModifiers());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertTrue(outputSchema.getElementSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelElement method, of class ParticleUnionGenerator
     * for an element set which blocks extension.
     */
    @Test
    public void testGenerateNewTopLevelElementBlockDefaultExtension() {
        LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.addBlockModifier(Element.Block.extension);
        elementA.addBlockModifier(Element.Block.restriction);
        elementA.addBlockModifier(Element.Block.substitution);
        topLevelElements.add(elementA);

        Element elementB = new Element("{A}elementB");
        topLevelElements.add(elementB);

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.extension);
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Element result = instance.generateNewTopLevelElement(topLevelElements, typeRef);

        assertEquals("{A}simpleTypeA", result.getType().getName());
        assertEquals(false, result.getType().isAnonymous());
        assertEquals(true, (boolean) result.getTypeAttr());
        HashSet<Block> block = new HashSet<Block>();
        block.add(Block.extension);
        assertEquals(block, result.getBlockModifiers());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertTrue(outputSchema.getElementSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelElement method, of class ParticleUnionGenerator
     * for an element set which blocks restriction.
     */
    @Test
    public void testGenerateNewTopLevelElementBlockDefaulRestriction() {
        LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.addBlockModifier(Element.Block.extension);
        elementA.addBlockModifier(Element.Block.restriction);
        elementA.addBlockModifier(Element.Block.substitution);
        topLevelElements.add(elementA);

        Element elementB = new Element("{A}elementB");
        topLevelElements.add(elementB);

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.restriction);
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Element result = instance.generateNewTopLevelElement(topLevelElements, typeRef);

        assertEquals("{A}simpleTypeA", result.getType().getName());
        assertEquals(false, result.getType().isAnonymous());
        assertEquals(true, (boolean) result.getTypeAttr());
        HashSet<Block> block = new HashSet<Block>();
        block.add(Block.restriction);
        assertEquals(block, result.getBlockModifiers());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertTrue(outputSchema.getElementSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelElement method, of class ParticleUnionGenerator
     * for an element set which blocks substitution.
     */
    @Test
    public void testGenerateNewTopLevelElementBlockDefaultSubstitution() {
        LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.addBlockModifier(Element.Block.extension);
        elementA.addBlockModifier(Element.Block.restriction);
        elementA.addBlockModifier(Element.Block.substitution);
        topLevelElements.add(elementA);

        Element elementB = new Element("{A}elementB");
        topLevelElements.add(elementB);

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.substitution);
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Element result = instance.generateNewTopLevelElement(topLevelElements, typeRef);

        assertEquals("{A}simpleTypeA", result.getType().getName());
        assertEquals(false, result.getType().isAnonymous());
        assertEquals(true, (boolean) result.getTypeAttr());
        HashSet<Block> block = new HashSet<Block>();
        block.add(Block.substitution);
        assertEquals(block, result.getBlockModifiers());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertTrue(outputSchema.getElementSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelElement method, of class ParticleUnionGenerator
     * for an element set which blocks substitution which is schema default.
     */
    @Test
    public void testGenerateNewTopLevelElementBlockDefault() {
        LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.addBlockModifier(Element.Block.extension);
        elementA.addBlockModifier(Element.Block.restriction);
        elementA.addBlockModifier(Element.Block.substitution);
        topLevelElements.add(elementA);

        Element elementB = new Element("{A}elementB");
        topLevelElements.add(elementB);

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.substitution);
        elementOldSchemaMap.put(elementA, oldSchema);
        elementOldSchemaMap.put(elementB, oldSchema);
        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.addBlockDefault(XSDSchema.BlockDefault.substitution);
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Element result = instance.generateNewTopLevelElement(topLevelElements, typeRef);

        assertEquals("{A}simpleTypeA", result.getType().getName());
        assertEquals(false, result.getType().isAnonymous());
        assertEquals(true, (boolean) result.getTypeAttr());
        assertEquals(null, result.getBlockModifiers());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertTrue(outputSchema.getElementSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelElement method, of class ParticleUnionGenerator.
     */
    @Test
    public void testGenerateNewTopLevelElement() {
        LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        topLevelElements.add(elementA);
        Element elementB = new Element("{A}elementB");
        topLevelElements.add(elementB);
        Element elementC = new Element("{A}elementC");
        topLevelElements.add(elementC);

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        elementOldSchemaMap.put(elementC, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Element result = instance.generateNewTopLevelElement(topLevelElements, typeRef);

        assertEquals("{A}simpleTypeA", result.getType().getName());
        assertEquals(false, result.getType().isAnonymous());
        assertEquals(true, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertTrue(outputSchema.getElementSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelElement method, of class ParticleUnionGenerator
     * for an anonymous type.
     */
    @Test
    public void testGenerateNewTopLevelElementAnonymousType() {
        LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        topLevelElements.add(elementA);
        Element elementB = new Element("{A}elementB");
        topLevelElements.add(elementB);
        Element elementC = new Element("{A}elementC");
        topLevelElements.add(elementC);

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.setIsAnonymous(true);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        elementOldSchemaMap.put(elementC, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Element result = instance.generateNewTopLevelElement(topLevelElements, typeRef);

        assertEquals("{A}simpleTypeA", result.getType().getName());
        assertEquals(true, result.getType().isAnonymous());
        assertEquals(false, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertTrue(outputSchema.getElementSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelElement method, of class ParticleUnionGenerator
     * for a build-in type.
     */
    @Test
    public void testGenerateNewTopLevelElementBuildInType() {
        LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        topLevelElements.add(elementA);
        Element elementB = new Element("{A}elementB");
        topLevelElements.add(elementB);
        Element elementC = new Element("{A}elementC");
        topLevelElements.add(elementC);

        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        simpleTypeA.setIsAnonymous(true);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeA);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        elementOldSchemaMap.put(elementC, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Element result = instance.generateNewTopLevelElement(topLevelElements, typeRef);

        assertEquals("{http://www.w3.org/2001/XMLSchema}string", result.getType().getName());
        assertEquals(true, result.getType().isAnonymous());
        assertEquals(true, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertTrue(outputSchema.getElementSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelElement method, of class ParticleUnionGenerator
     * for one abstract element.
     */
    @Test
    public void testGenerateNewTopLevelElementOneElementAbstract() {
        LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.setAbstract(true);
        topLevelElements.add(elementA);
        Element elementB = new Element("{A}elementB");
        topLevelElements.add(elementB);
        Element elementC = new Element("{A}elementC");
        topLevelElements.add(elementC);

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        elementOldSchemaMap.put(elementC, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Element result = instance.generateNewTopLevelElement(topLevelElements, typeRef);

        assertEquals("{A}simpleTypeA", result.getType().getName());
        assertEquals(false, result.getType().isAnonymous());
        assertEquals(true, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertTrue(outputSchema.getElementSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelElement method, of class ParticleUnionGenerator
     * for all elements abstract.
     */
    @Test
    public void testGenerateNewTopLevelElementAllElementsAbstract() {
        LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.setAbstract(true);
        topLevelElements.add(elementA);
        Element elementB = new Element("{A}elementB");
        elementB.setAbstract(true);
        topLevelElements.add(elementB);
        Element elementC = new Element("{A}elementC");
        elementC.setAbstract(true);
        topLevelElements.add(elementC);

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        elementOldSchemaMap.put(elementC, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Element result = instance.generateNewTopLevelElement(topLevelElements, typeRef);

        assertEquals("{A}simpleTypeA", result.getType().getName());
        assertEquals(false, result.getType().isAnonymous());
        assertEquals(true, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(true, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertTrue(outputSchema.getElementSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelElement method, of class ParticleUnionGenerator
     * for one nillable element.
     */
    @Test
    public void testGenerateNewTopLevelElementOneElementNillable() {
        LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.setNillable();
        topLevelElements.add(elementA);
        Element elementB = new Element("{A}elementB");
        topLevelElements.add(elementB);
        Element elementC = new Element("{A}elementC");
        topLevelElements.add(elementC);

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        elementOldSchemaMap.put(elementC, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Element result = instance.generateNewTopLevelElement(topLevelElements, typeRef);

        assertEquals("{A}simpleTypeA", result.getType().getName());
        assertEquals(false, result.getType().isAnonymous());
        assertEquals(true, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(true, (boolean) result.getNillable());
        assertTrue(outputSchema.getElementSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelElement method, of class ParticleUnionGenerator
     * for elements with IDs.
     */
    @Test
    public void testGenerateNewTopLevelElementID() {
        LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.setId("idOne");
        topLevelElements.add(elementA);
        Element elementB = new Element("{A}elementB");
        elementB.setId("idTwo");
        topLevelElements.add(elementB);

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("idOne.idTwo");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, usedIDs, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Element result = instance.generateNewTopLevelElement(topLevelElements, typeRef);

        assertEquals("{A}simpleTypeA", result.getType().getName());
        assertEquals(false, result.getType().isAnonymous());
        assertEquals(true, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals("idOne.idTwo.1", result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertTrue(outputSchema.getElementSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelElement method, of class ParticleUnionGenerator
     * for elements with annotations.
     */
    @Test
    public void testGenerateNewTopLevelElementAnnotation() {
        LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");

        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        elementA.setAnnotation(annotationA);

        topLevelElements.add(elementA);
        Element elementB = new Element("{A}elementB");

        Annotation annotationB = new Annotation();
        AppInfo appInfoB = new AppInfo();
        appInfoB.setSource("blaB");
        Documentation documentationB = new Documentation();
        documentationB.setSource("blaaB");
        annotationB.addAppInfos(appInfoB);
        annotationB.addDocumentations(documentationB);
        elementB.setAnnotation(annotationB);

        topLevelElements.add(elementB);

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));

        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Element result = instance.generateNewTopLevelElement(topLevelElements, typeRef);

        assertEquals("{A}simpleTypeA", result.getType().getName());
        assertEquals(false, result.getType().isAnonymous());
        assertEquals(true, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertTrue(result.getAnnotation().getAppInfos().size() == 2);
        assertEquals("blaA", result.getAnnotation().getAppInfos().get(0).getSource());
        assertEquals("blaB", result.getAnnotation().getAppInfos().get(1).getSource());
        assertTrue(result.getAnnotation().getDocumentations().size() == 2);
        assertEquals("blaaA", result.getAnnotation().getDocumentations().get(0).getSource());
        assertEquals("blaaB", result.getAnnotation().getDocumentations().get(1).getSource());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertTrue(outputSchema.getElementSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelElement method, of class ParticleUnionGenerator
     * for different fixed type values.
     */
    @Test
    public void testGenerateNewTopLevelElementFixedTypes() {
        LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.setFixed("bla");
        topLevelElements.add(elementA);
        Element elementB = new Element("{A}elementB");
        elementB.setFixed("bla2");
        topLevelElements.add(elementB);

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Element result = instance.generateNewTopLevelElement(topLevelElements, typeRef);

        assertEquals(true, result.getType().isAnonymous());
        assertTrue(result.getType() instanceof SimpleType);
        assertTrue(((SimpleType) result.getType()).getInheritance() instanceof SimpleContentRestriction);
        assertTrue(((SimpleContentRestriction) ((SimpleType) result.getType()).getInheritance()).getEnumeration().contains("bla"));
        assertTrue(((SimpleContentRestriction) ((SimpleType) result.getType()).getInheritance()).getEnumeration().contains("bla2"));
        assertEquals(false, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertTrue(outputSchema.getElementSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelElement method, of class ParticleUnionGenerator
     * for a single fixed type value.
     */
    @Test
    public void testGenerateNewTopLevelElementFixedType() {
        LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.setFixed("bla");
        topLevelElements.add(elementA);
        Element elementB = new Element("{A}elementB");
        elementB.setFixed("bla");
        topLevelElements.add(elementB);

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Element result = instance.generateNewTopLevelElement(topLevelElements, typeRef);

        assertEquals("{A}simpleTypeA", result.getType().getName());
        assertEquals(false, result.getType().isAnonymous());
        assertTrue(result.getType() instanceof SimpleType);
        assertEquals(true, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals("bla", result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertTrue(outputSchema.getElementSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelElement method, of class ParticleUnionGenerator
     * for no resulting fixed type value.
     */
    @Test
    public void testGenerateNewTopLevelElementNoFixedType() {
        LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.setFixed("bla");
        topLevelElements.add(elementA);
        Element elementB = new Element("{A}elementB");
        topLevelElements.add(elementB);

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Element result = instance.generateNewTopLevelElement(topLevelElements, typeRef);

        assertEquals("{A}simpleTypeA", result.getType().getName());
        assertEquals(false, result.getType().isAnonymous());
        assertTrue(result.getType() instanceof SimpleType);
        assertEquals(true, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertTrue(outputSchema.getElementSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelElement method, of class ParticleUnionGenerator
     * for different default type values.
     */
    @Test
    public void testGenerateNewTopLevelElementDefaultTypes() {
        LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.setDefault("bla");
        topLevelElements.add(elementA);
        Element elementB = new Element("{A}elementB");
        elementB.setDefault("bla2");
        topLevelElements.add(elementB);

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Element result = instance.generateNewTopLevelElement(topLevelElements, typeRef);

        assertEquals("{A}simpleTypeA", result.getType().getName());
        assertEquals(false, result.getType().isAnonymous());
        assertEquals(true, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertTrue(outputSchema.getElementSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelElement method, of class ParticleUnionGenerator
     * for a single default type value.
     */
    @Test
    public void testGenerateNewTopLevelElementDefaultType() {
        LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.setDefault("bla");
        topLevelElements.add(elementA);
        Element elementB = new Element("{A}elementB");
        elementB.setDefault("bla");
        topLevelElements.add(elementB);

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Element result = instance.generateNewTopLevelElement(topLevelElements, typeRef);

        assertEquals("{A}simpleTypeA", result.getType().getName());
        assertEquals(false, result.getType().isAnonymous());
        assertEquals(true, (boolean) result.getTypeAttr());
        assertEquals("bla", result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertTrue(outputSchema.getElementSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelElement method, of class ParticleUnionGenerator
     * for a single element with fixed type value.
     */
    @Test
    public void testGenerateNewTopLevelElementSingleDefaultType() {
        LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.setDefault("bla");
        topLevelElements.add(elementA);
        Element elementB = new Element("{A}elementB");
        topLevelElements.add(elementB);

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Element result = instance.generateNewTopLevelElement(topLevelElements, typeRef);

        assertEquals("{A}simpleTypeA", result.getType().getName());
        assertEquals(false, result.getType().isAnonymous());
        assertEquals(true, (boolean) result.getTypeAttr());
        assertEquals("bla", result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertTrue(outputSchema.getElementSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelElement method, of class ParticleUnionGenerator
     * for no resulting default type value.
     */
    @Test
    public void testGenerateNewTopLevelElementNoDefaultType() {
        LinkedHashSet<Element> topLevelElements = new LinkedHashSet<Element>();
        Element elementA = new Element("{A}elementA");
        elementA.setDefault("blaa");
        elementA.setFixed("bla");
        topLevelElements.add(elementA);
        Element elementB = new Element("{A}elementB");
        elementB.setFixed("bla");
        topLevelElements.add(elementB);

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SymbolTableRef<Type> typeRef = new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA);
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        XSDSchema outputSchema = new XSDSchema("A");
        ParticleUnionGenerator instance = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGeneratornion(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Element result = instance.generateNewTopLevelElement(topLevelElements, typeRef);

        assertEquals("{A}simpleTypeA", result.getType().getName());
        assertEquals(false, result.getType().isAnonymous());
        assertTrue(result.getType() instanceof SimpleType);
        assertEquals(true, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals("bla", result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) result.getAbstract());
        assertEquals(false, (boolean) result.getNillable());
        assertTrue(outputSchema.getElementSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getElements().contains(outputSchema.getElementSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getForm());
    }
}