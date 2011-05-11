package de.tudortmund.cs.bonxai.xsd.setOperations.difference;

import de.tudortmund.cs.bonxai.common.Annotation;
import de.tudortmund.cs.bonxai.common.AnyAttribute;
import de.tudortmund.cs.bonxai.common.ProcessContentsInstruction;
import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import de.tudortmund.cs.bonxai.xsd.*;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>AttributeParticleDifferenceGenerator</tt> class,
 * checks that every method of this class performs properly.
 * @author Dominik Wolff
 */
public class AttributeParticleDifferenceGeneratorTest extends junit.framework.TestCase {

    public AttributeParticleDifferenceGeneratorTest() {
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributes() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, oldSchema);
        typeOldSchemaMap.put(simpleTypeB, oldSchema);
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a minuend attribute.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttribute() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, oldSchema);
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of multiple attributes.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesMultipleAttributes() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setUse(AttributeUse.Optional);
        Attribute attributeC = new Attribute("{A}attributeC", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeC.setUse(AttributeUse.Required);
        minuendAttributeParticles.add(attributeA);
        minuendAttributeParticles.add(attributeC);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeB", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setUse(AttributeUse.Optional);
        Attribute attributeD = new Attribute("{A}attributeD", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeD.setUse(AttributeUse.Required);
        subtrahendAttributeParticles.add(attributeB);
        subtrahendAttributeParticles.add(attributeD);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        attributeOldSchemaMap.put(attributeD, oldSchema);
        attributeOldSchemaMap.put(attributeC, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 2);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
        assertTrue(result.getLast() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getLast()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getLast()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getLast()).getAnnotation());
        assertEquals(null, ((Attribute) result.getLast()).getDefault());
        assertEquals(null, ((Attribute) result.getLast()).getFixed());
        assertEquals(null, ((Attribute) result.getLast()).getForm());
        assertEquals(null, ((Attribute) result.getLast()).getId());
        assertEquals("{A}attributeC", ((Attribute) result.getLast()).getName());
        assertEquals(false, ((Attribute) result.getLast()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getLast()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getLast()).getTypeAttr());
        assertEquals(AttributeUse.Required, ((Attribute) result.getLast()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes with
     * annotation.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesAnnotation() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        attributeA.setAnnotation(annotationA);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertTrue(((Attribute) result.getFirst()).getAnnotation().getAppInfos().size() == 1);
        assertEquals("blaA", ((Attribute) result.getFirst()).getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((Attribute) result.getFirst()).getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaaA", ((Attribute) result.getFirst()).getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a minuend attribute with
     * annotation.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeAnnotation() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        attributeA.setAnnotation(annotationA);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertTrue(((Attribute) result.getFirst()).getAnnotation().getAppInfos().size() == 1);
        assertEquals("blaA", ((Attribute) result.getFirst()).getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((Attribute) result.getFirst()).getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaaA", ((Attribute) result.getFirst()).getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getLast()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes with
     * empty type intersection.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesEmptyTypeIntersection() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes with
     * different fixed values.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesDifferentFixed() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setFixed("bla");
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setFixed("bla2");
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals("bla", ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a minuend attribute with
     * fixed value.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeFixed() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setFixed("bla");
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals("bla", ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getLast()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes with
     * same fixed values.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesSameFixed() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setFixed("bla");
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setFixed("bla");
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes with
     * single fixed values.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesSingleFixed() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setFixed("bla");
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals("bla", ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes with
     * different default values.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesDifferentDefault() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setDefault("bla");
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setDefault("bla2");
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals("bla", ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a minuend attribute with
     * default value.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeDefault() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setDefault("bla");
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals("bla", ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getLast()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes with
     * same default values.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesSameDefault() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setDefault("bla");
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setDefault("bla");
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals("bla", ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes with
     * same default values but the resulting attribute is not oprtional.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesSameDefaultNotOptional() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setUse(AttributeUse.Required);
        attributeA.setDefault("bla");
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setDefault("bla");
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Required, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a minuend attribute with
     * default value but the resulting attribute is not oprtional.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeDefaultNotOptional() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setUse(AttributeUse.Required);
        attributeA.setDefault("bla");
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals("bla", ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getLast()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Required, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes with
     * single default values.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesSingleDefault() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setDefault("bla");
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals("bla", ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a required minuend attribute.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRequired() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setUse(AttributeUse.Required);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getLast()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Required, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a prohibited minuend attribute.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeProhibited() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setUse(AttributeUse.Prohibited);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getLast()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Prohibited, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes with same
     * types.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesOptionalOptionalSameTypes() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setUse(AttributeUse.Optional);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setUse(AttributeUse.Optional);
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes with same
     * types.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesOptionalRequiredSameTypes() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setUse(AttributeUse.Optional);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setUse(AttributeUse.Required);
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getLast()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Prohibited, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes with same
     * types.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesOptionalProhibitedSameTypes() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setUse(AttributeUse.Optional);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setUse(AttributeUse.Prohibited);
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getLast()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Required, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes with same
     * types.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesRequiredOptionalSameTypes() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setUse(AttributeUse.Required);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setUse(AttributeUse.Optional);
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes with same
     * types.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesRequiredRequiredSameTypes() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setUse(AttributeUse.Required);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setUse(AttributeUse.Required);
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes with same
     * types.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesRequiredProhibitedSameTypes() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setUse(AttributeUse.Required);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setUse(AttributeUse.Prohibited);
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getLast()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Required, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes with same
     * types.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesProhibitedOptionalSameTypes() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setUse(AttributeUse.Prohibited);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setUse(AttributeUse.Optional);
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes with same
     * types.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesProhibitedRequiredSameTypes() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setUse(AttributeUse.Prohibited);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setUse(AttributeUse.Required);
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getLast()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Prohibited, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes with same
     * types.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesProhibitedProhibitedSameTypes() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setUse(AttributeUse.Prohibited);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setUse(AttributeUse.Prohibited);
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes with
     * different types.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesOptionalOptionalDifferentTypes() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setUse(AttributeUse.Optional);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setUse(AttributeUse.Optional);
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes with
     * different types.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesOptionalRequiredDifferentTypes() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setUse(AttributeUse.Optional);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setUse(AttributeUse.Required);
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes with
     * different types.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesOptionalProhibitedDifferentTypes() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setUse(AttributeUse.Optional);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setUse(AttributeUse.Prohibited);
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getLast()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Required, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes with
     * different types.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesRequiredOptionalDifferentTypes() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setUse(AttributeUse.Required);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setUse(AttributeUse.Optional);
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Required, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes with
     * different types.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesRequiredRequiredDifferentTypes() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setUse(AttributeUse.Required);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setUse(AttributeUse.Required);
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Required, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes with
     * different types.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesRequiredProhibitedDifferentTypes() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setUse(AttributeUse.Required);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setUse(AttributeUse.Prohibited);
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getLast()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Required, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes with
     * different types.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesProhibitedOptionalDifferentTypes() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setUse(AttributeUse.Prohibited);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setUse(AttributeUse.Optional);
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(null, ((Attribute) result.getFirst()).getSimpleType());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Prohibited, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes with
     * different types.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesProhibitedRequiredDifferentTypes() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setUse(AttributeUse.Prohibited);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setUse(AttributeUse.Required);
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getLast()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Prohibited, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes with
     * different types.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesProhibitedProhibitedDifferentTypes() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setUse(AttributeUse.Prohibited);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setUse(AttributeUse.Prohibited);
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a qualified attribute.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeQualified() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a unqualified attribute.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeUnqualified() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.unqualified);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a foreign qualified attribute.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeForeignQualified() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{B}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();

        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.setSchemaLocation("Main");
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        XSDSchema oldSchema = new XSDSchema("B");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        LinkedHashSet<XSDSchema> otherSchemata = new LinkedHashSet<XSDSchema>();
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, namespaceOutputSchemaMap, null, null, attributeOldSchemaMap, null, namespaceAbbreviationMap, otherSchemata, "C:/");
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AttributeGroupRef);
        assertTrue(otherSchemata.size() == 1);
        assertTrue(otherSchemata.iterator().next().getAttributeGroups().contains(((AttributeGroupRef) result.getFirst()).getAttributeGroup()));
        assertTrue(otherSchemata.iterator().next().getAttributeGroupSymbolTable().hasReference(((AttributeGroupRef) result.getFirst()).getAttributeGroup().getName()));
        assertEquals(null, ((AttributeGroupRef) result.getFirst()).getAttributeGroup().getId());
        assertEquals(null, ((AttributeGroupRef) result.getFirst()).getAttributeGroup().getAnnotation());
        assertTrue(((AttributeGroupRef) result.getFirst()).getAttributeGroup().getAttributeParticles().size() == 1);
        AttributeParticle resultingAttribute = ((AttributeGroupRef) result.getFirst()).getAttributeGroup().getAttributeParticles().getFirst();
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) resultingAttribute).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) resultingAttribute).getName()).getReference()));
        assertEquals(null, ((Attribute) resultingAttribute).getAnnotation());
        assertEquals(null, ((Attribute) resultingAttribute).getDefault());
        assertEquals(null, ((Attribute) resultingAttribute).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) resultingAttribute).getForm());
        assertEquals(null, ((Attribute) resultingAttribute).getId());
        assertEquals("{B}attributeA", ((Attribute) resultingAttribute).getName());
        assertEquals(false, ((Attribute) resultingAttribute).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) resultingAttribute).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) resultingAttribute).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) resultingAttribute).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a foreign unqualified attribute.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeForeignUnqualified() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{B}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.unqualified);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("B");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesQualifiedQualified() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setForm(XSDSchema.Qualification.qualified);
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesQualifiedUnqualified() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesForeignQualifiedQualified() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{B}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{B}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setForm(XSDSchema.Qualification.qualified);
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        outputSchema.setSchemaLocation("C:/A.xsd");
        XSDSchema oldSchemaA = new XSDSchema("A");
        XSDSchema oldSchemaB = new XSDSchema("B");
        oldSchemaA.getAttributeSymbolTable().updateOrCreateReference("{A}attributeA", attributeA);
        oldSchemaA.addAttribute(oldSchemaA.getAttributeSymbolTable().getReference("{A}attributeA"));
        oldSchemaB.getAttributeSymbolTable().updateOrCreateReference("{B}attributeB", attributeB);
        oldSchemaB.addAttribute(oldSchemaB.getAttributeSymbolTable().getReference("{B}attributeB"));
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, oldSchemaA);
        typeOldSchemaMap.put(simpleTypeB, oldSchemaB);
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        LinkedHashSet<XSDSchema> otherSchemata = new LinkedHashSet<XSDSchema>();
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, namespaceOutputSchemaMap, null, null, attributeOldSchemaMap, null, namespaceAbbreviationMap, otherSchemata, "C:/");
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AttributeGroupRef);
        assertTrue(((AttributeGroupRef) result.getFirst()).getAttributeGroup().getAttributeParticles().size() == 1);
        AttributeParticle newAttributeParticle = ((AttributeGroupRef) result.getFirst()).getAttributeGroup().getAttributeParticles().getFirst();
        assertTrue(newAttributeParticle instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) newAttributeParticle).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) newAttributeParticle).getName()).getReference()));
        assertEquals(null, ((Attribute) newAttributeParticle).getAnnotation());
        assertEquals(null, ((Attribute) newAttributeParticle).getDefault());
        assertEquals(null, ((Attribute) newAttributeParticle).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) newAttributeParticle).getForm());
        assertEquals(null, ((Attribute) newAttributeParticle).getId());
        assertEquals("{B}attributeA", ((Attribute) newAttributeParticle).getName());
        assertEquals(false, ((Attribute) newAttributeParticle).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) newAttributeParticle).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) newAttributeParticle).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) newAttributeParticle).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesForeignQualifiedUnqualified() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.unqualified);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setForm(XSDSchema.Qualification.qualified);
        subtrahendAttributeParticles.add(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for an attribute reference.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRef() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of multiple attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefMultipleAttributeRefs() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        Attribute attributeC = new Attribute("{A}attributeB", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefC = new AttributeRef(new SymbolTableRef<Attribute>(attributeC.getName(), attributeC));
        minuendAttributeParticles.add(attributeRefA);
        minuendAttributeParticles.add(attributeRefC);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 2);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
        assertTrue(result.getLast() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getLast()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getLast()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getLast()).getAnnotation());
        assertEquals(null, ((Attribute) result.getLast()).getDefault());
        assertEquals(null, ((Attribute) result.getLast()).getFixed());
        assertEquals(null, ((Attribute) result.getLast()).getForm());
        assertEquals(null, ((Attribute) result.getLast()).getId());
        assertEquals("{A}attributeB", ((Attribute) result.getLast()).getName());
        assertEquals(false, ((Attribute) result.getLast()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getLast()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getLast()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getLast()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for an attribute reference with
     * annotation.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefAnnotation() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        attributeRefA.setAnnotation(annotationA);
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertTrue(((Attribute) result.getFirst()).getAnnotation().getAppInfos().size() == 1);
        assertEquals("blaA", ((Attribute) result.getFirst()).getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((Attribute) result.getFirst()).getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaaA", ((Attribute) result.getFirst()).getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for attribute references with
     * annotation.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefsAnnotation() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        attributeRefA.setAnnotation(annotationA);
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertTrue(((Attribute) result.getFirst()).getAnnotation().getAppInfos().size() == 1);
        assertEquals("blaA", ((Attribute) result.getFirst()).getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((Attribute) result.getFirst()).getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaaA", ((Attribute) result.getFirst()).getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for attribute references with
     * different fixed values.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefsDifferentFixed() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeA.setFixed("bla");
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setFixed("bla2");
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals("bla", ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for attribute references with same
     * fixed values.
     */
    @Test
    public void testGenerateAttributeParticleIntersectioAttributeRefsSameFixed() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setFixed("bla");
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setFixed("bla");
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for an attribute reference with
     * single fixed values.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefSingleFixed() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setFixed("bla");
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals("bla", ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for attribute references with
     * different default values.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefsDifferentDefault() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeA.setDefault("bla");
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setDefault("bla2");
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals("bla", ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for attribute references with same
     * default value.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefSameDefault() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeA.setDefault("bla");
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setDefault("bla");
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals("bla", ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attribute references
     * with same default values but the resulting attribute is not oprtional.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefSameDefaultNotOptional() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Required);
        attributeA.setDefault("bla");
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setDefault("bla");
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Required, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attribute references
     * with single default values.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefSingleDefault() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setDefault("bla");
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefOptionalOptional() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Optional);
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Optional);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefOptionalRequired() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Optional);
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Required);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AttributeRef);
        assertEquals(null, ((AttributeRef) result.getFirst()).getAnnotation());
        assertEquals(null, ((AttributeRef) result.getFirst()).getDefault());
        assertEquals(null, ((AttributeRef) result.getFirst()).getFixed());
        assertEquals(null, ((AttributeRef) result.getFirst()).getId());
        assertEquals(AttributeUse.Prohibited, ((AttributeRef) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefOptionalProhibited() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Optional);
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Prohibited);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AttributeRef);
        assertEquals(null, ((AttributeRef) result.getFirst()).getAnnotation());
        assertEquals(null, ((AttributeRef) result.getFirst()).getDefault());
        assertEquals(null, ((AttributeRef) result.getFirst()).getFixed());
        assertEquals(null, ((AttributeRef) result.getFirst()).getId());
        assertEquals(AttributeUse.Required, ((AttributeRef) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefRequiredOptional() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Required);
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Optional);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefRequiredRequired() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Required);
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Required);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefRequiredProhibited() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Required);
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Prohibited);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Required, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefProhibitedOptinal() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Prohibited);
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Optional);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefProhibitedRequired() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Prohibited);
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Required);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Prohibited, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefProhibitedProhibited() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Prohibited);
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Prohibited);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefsAttributeRef() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Optional);
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Prohibited);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AttributeRef);
        assertEquals(null, ((AttributeRef) result.getFirst()).getAnnotation());
        assertEquals(null, ((AttributeRef) result.getFirst()).getDefault());
        assertEquals(null, ((AttributeRef) result.getFirst()).getFixed());
        assertEquals(null, ((AttributeRef) result.getFirst()).getId());
        assertEquals(AttributeUse.Required, ((AttributeRef) result.getFirst()).getUse());
        Attribute newAttribute = ((AttributeRef) result.getFirst()).getAttribute();
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(newAttribute.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(newAttribute.getName()).getReference()));
        assertEquals(null, newAttribute.getAnnotation());
        assertEquals(null, newAttribute.getDefault());
        assertEquals(null, newAttribute.getFixed());
        assertEquals(null, newAttribute.getForm());
        assertEquals(null, newAttribute.getId());
        assertEquals("{A}attributeA", newAttribute.getName());
        assertEquals(false, newAttribute.getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", newAttribute.getSimpleType().getName());
        assertEquals(false, (boolean) newAttribute.getTypeAttr());
        assertEquals(AttributeUse.Optional, newAttribute.getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attribute references
     * with annotations.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefsAnnotationsAttributeRef() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Optional);
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        attributeRefA.setAnnotation(annotationA);
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Prohibited);
        subtrahendAttributeParticles.add(attributeRefB);

        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AttributeRef);
        assertTrue(((AttributeRef) result.getFirst()).getAnnotation().getAppInfos().size() == 1);
        assertEquals("blaA", ((AttributeRef) result.getFirst()).getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((AttributeRef) result.getFirst()).getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaaA", ((AttributeRef) result.getFirst()).getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(null, ((AttributeRef) result.getFirst()).getDefault());
        assertEquals(null, ((AttributeRef) result.getFirst()).getFixed());
        assertEquals(null, ((AttributeRef) result.getFirst()).getId());
        assertEquals(AttributeUse.Required, ((AttributeRef) result.getFirst()).getUse());
        Attribute newAttribute = ((AttributeRef) result.getFirst()).getAttribute();
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(newAttribute.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(newAttribute.getName()).getReference()));
        assertEquals(null, newAttribute.getAnnotation());
        assertEquals(null, newAttribute.getDefault());
        assertEquals(null, newAttribute.getFixed());
        assertEquals(null, newAttribute.getForm());
        assertEquals(null, newAttribute.getId());
        assertEquals("{A}attributeA", newAttribute.getName());
        assertEquals(false, newAttribute.getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", newAttribute.getSimpleType().getName());
        assertEquals(false, (boolean) newAttribute.getTypeAttr());
        assertEquals(AttributeUse.Optional, newAttribute.getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attribute references
     * with different fixed values.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefDifferentFixedAttributeRef() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Optional);
        attributeRefA.setFixed("bla");
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Prohibited);
        attributeRefB.setFixed("bla2");
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AttributeRef);
        assertEquals(null, ((AttributeRef) result.getFirst()).getAnnotation());
        assertEquals(null, ((AttributeRef) result.getFirst()).getDefault());
        assertEquals("bla", ((AttributeRef) result.getFirst()).getFixed());
        assertEquals(null, ((AttributeRef) result.getFirst()).getId());
        assertEquals(AttributeUse.Required, ((AttributeRef) result.getFirst()).getUse());
        Attribute newAttribute = ((AttributeRef) result.getFirst()).getAttribute();
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(newAttribute.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(newAttribute.getName()).getReference()));
        assertEquals(null, newAttribute.getAnnotation());
        assertEquals(null, newAttribute.getDefault());
        assertEquals(null, newAttribute.getFixed());
        assertEquals(null, newAttribute.getForm());
        assertEquals(null, newAttribute.getId());
        assertEquals("{A}attributeA", newAttribute.getName());
        assertEquals(false, newAttribute.getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", newAttribute.getSimpleType().getName());
        assertEquals(false, (boolean) newAttribute.getTypeAttr());
        assertEquals(AttributeUse.Optional, newAttribute.getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attribute references
     * with same fixed values.
     */
    @Test
    public void testGenerateAttributeParticleIntersectioAttributeRefsSameFixedAttributeRef() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Optional);
        attributeRefA.setFixed("bla");
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Prohibited);
        attributeRefB.setFixed("bla");
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AttributeRef);
        assertEquals(null, ((AttributeRef) result.getFirst()).getAnnotation());
        assertEquals(null, ((AttributeRef) result.getFirst()).getDefault());
        assertEquals("bla", ((AttributeRef) result.getFirst()).getFixed());
        assertEquals(null, ((AttributeRef) result.getFirst()).getId());
        assertEquals(AttributeUse.Required, ((AttributeRef) result.getFirst()).getUse());
        Attribute newAttribute = ((AttributeRef) result.getFirst()).getAttribute();
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(newAttribute.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(newAttribute.getName()).getReference()));
        assertEquals(null, newAttribute.getAnnotation());
        assertEquals(null, newAttribute.getDefault());
        assertEquals(null, newAttribute.getFixed());
        assertEquals(null, newAttribute.getForm());
        assertEquals(null, newAttribute.getId());
        assertEquals("{A}attributeA", newAttribute.getName());
        assertEquals(false, newAttribute.getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", newAttribute.getSimpleType().getName());
        assertEquals(false, (boolean) newAttribute.getTypeAttr());
        assertEquals(AttributeUse.Optional, newAttribute.getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attribute references
     * with single fixed values.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefSingleFixedAttributeRef() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Optional);
        attributeRefA.setFixed("bla");
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Prohibited);

        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AttributeRef);
        assertEquals(null, ((AttributeRef) result.getFirst()).getAnnotation());
        assertEquals(null, ((AttributeRef) result.getFirst()).getDefault());
        assertEquals("bla", ((AttributeRef) result.getFirst()).getFixed());
        assertEquals(null, ((AttributeRef) result.getFirst()).getId());
        assertEquals(AttributeUse.Required, ((AttributeRef) result.getFirst()).getUse());
        Attribute newAttribute = ((AttributeRef) result.getFirst()).getAttribute();
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(newAttribute.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(newAttribute.getName()).getReference()));
        assertEquals(null, newAttribute.getAnnotation());
        assertEquals(null, newAttribute.getDefault());
        assertEquals(null, newAttribute.getFixed());
        assertEquals(null, newAttribute.getForm());
        assertEquals(null, newAttribute.getId());
        assertEquals("{A}attributeA", newAttribute.getName());
        assertEquals(false, newAttribute.getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", newAttribute.getSimpleType().getName());
        assertEquals(false, (boolean) newAttribute.getTypeAttr());
        assertEquals(AttributeUse.Optional, newAttribute.getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attribute references
     * with different default values.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefDifferentDefaultAttributeRef() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setDefault("bla");
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Prohibited);
        attributeRefB.setDefault("bla2");
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AttributeRef);
        assertEquals(null, ((AttributeRef) result.getFirst()).getAnnotation());
        assertEquals(null, ((AttributeRef) result.getFirst()).getDefault());
        assertEquals(null, ((AttributeRef) result.getFirst()).getFixed());
        assertEquals(null, ((AttributeRef) result.getFirst()).getId());
        assertEquals(AttributeUse.Required, ((AttributeRef) result.getFirst()).getUse());
        Attribute newAttribute = ((AttributeRef) result.getFirst()).getAttribute();
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(newAttribute.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(newAttribute.getName()).getReference()));
        assertEquals(null, newAttribute.getAnnotation());
        assertEquals(null, newAttribute.getDefault());
        assertEquals(null, newAttribute.getFixed());
        assertEquals(null, newAttribute.getForm());
        assertEquals(null, newAttribute.getId());
        assertEquals("{A}attributeA", newAttribute.getName());
        assertEquals(false, newAttribute.getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", newAttribute.getSimpleType().getName());
        assertEquals(false, (boolean) newAttribute.getTypeAttr());
        assertEquals(AttributeUse.Optional, newAttribute.getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attribute references
     * with same default values.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefSameDefaultAttributeRef() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setDefault("bla");
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Prohibited);
        attributeRefB.setDefault("bla");
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AttributeRef);
        assertEquals(null, ((AttributeRef) result.getFirst()).getAnnotation());
        assertEquals(null, ((AttributeRef) result.getFirst()).getDefault());
        assertEquals(null, ((AttributeRef) result.getFirst()).getFixed());
        assertEquals(null, ((AttributeRef) result.getFirst()).getId());
        assertEquals(AttributeUse.Required, ((AttributeRef) result.getFirst()).getUse());
        Attribute newAttribute = ((AttributeRef) result.getFirst()).getAttribute();
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(newAttribute.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(newAttribute.getName()).getReference()));
        assertEquals(null, newAttribute.getAnnotation());
        assertEquals(null, newAttribute.getDefault());
        assertEquals(null, newAttribute.getFixed());
        assertEquals(null, newAttribute.getForm());
        assertEquals(null, newAttribute.getId());
        assertEquals("{A}attributeA", newAttribute.getName());
        assertEquals(false, newAttribute.getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", newAttribute.getSimpleType().getName());
        assertEquals(false, (boolean) newAttribute.getTypeAttr());
        assertEquals(AttributeUse.Optional, newAttribute.getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attribute references
     * with same default values but the resulting attribute is not oprtional.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefSameDefaultNotOptionalAttributeRef() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Optional);
        attributeRefA.setDefault("bla");
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Prohibited);
        attributeRefB.setDefault("bla");
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AttributeRef);
        assertEquals(null, ((AttributeRef) result.getFirst()).getAnnotation());
        assertEquals(null, ((AttributeRef) result.getFirst()).getDefault());
        assertEquals(null, ((AttributeRef) result.getFirst()).getFixed());
        assertEquals(null, ((AttributeRef) result.getFirst()).getId());
        assertEquals(AttributeUse.Required, ((AttributeRef) result.getFirst()).getUse());
        Attribute newAttribute = ((AttributeRef) result.getFirst()).getAttribute();
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(newAttribute.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(newAttribute.getName()).getReference()));
        assertEquals(null, newAttribute.getAnnotation());
        assertEquals(null, newAttribute.getDefault());
        assertEquals(null, newAttribute.getFixed());
        assertEquals(null, newAttribute.getForm());
        assertEquals(null, newAttribute.getId());
        assertEquals("{A}attributeA", newAttribute.getName());
        assertEquals(false, newAttribute.getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", newAttribute.getSimpleType().getName());
        assertEquals(false, (boolean) newAttribute.getTypeAttr());
        assertEquals(AttributeUse.Optional, newAttribute.getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attribute references
     * with single default values.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefSingleDefaultAttributeRef() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setDefault("bla");
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Prohibited);

        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AttributeRef);
        assertEquals(null, ((AttributeRef) result.getFirst()).getAnnotation());
        assertEquals(null, ((AttributeRef) result.getFirst()).getDefault());
        assertEquals(null, ((AttributeRef) result.getFirst()).getFixed());
        assertEquals(null, ((AttributeRef) result.getFirst()).getId());
        assertEquals(AttributeUse.Required, ((AttributeRef) result.getFirst()).getUse());
        Attribute newAttribute = ((AttributeRef) result.getFirst()).getAttribute();
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(newAttribute.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(newAttribute.getName()).getReference()));
        assertEquals(null, newAttribute.getAnnotation());
        assertEquals(null, newAttribute.getDefault());
        assertEquals(null, newAttribute.getFixed());
        assertEquals(null, newAttribute.getForm());
        assertEquals(null, newAttribute.getId());
        assertEquals("{A}attributeA", newAttribute.getName());
        assertEquals(false, newAttribute.getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", newAttribute.getSimpleType().getName());
        assertEquals(false, (boolean) newAttribute.getTypeAttr());
        assertEquals(AttributeUse.Optional, newAttribute.getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefOptionalOptionalAttributeRef() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Optional);
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Optional);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefOptionalRequiredAttributeRef() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Optional);
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Required);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AttributeRef);
        assertEquals(null, ((AttributeRef) result.getFirst()).getAnnotation());
        assertEquals(null, ((AttributeRef) result.getFirst()).getDefault());
        assertEquals(null, ((AttributeRef) result.getFirst()).getFixed());
        assertEquals(null, ((AttributeRef) result.getFirst()).getId());
        assertEquals(AttributeUse.Prohibited, ((AttributeRef) result.getFirst()).getUse());
        Attribute newAttribute = ((AttributeRef) result.getFirst()).getAttribute();
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(newAttribute.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(newAttribute.getName()).getReference()));
        assertEquals(null, newAttribute.getAnnotation());
        assertEquals(null, newAttribute.getDefault());
        assertEquals(null, newAttribute.getFixed());
        assertEquals(null, newAttribute.getForm());
        assertEquals(null, newAttribute.getId());
        assertEquals("{A}attributeA", newAttribute.getName());
        assertEquals(false, newAttribute.getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", newAttribute.getSimpleType().getName());
        assertEquals(false, (boolean) newAttribute.getTypeAttr());
        assertEquals(AttributeUse.Optional, newAttribute.getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefOptionalProhibitedAttributeRef() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Optional);
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Prohibited);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AttributeRef);
        assertEquals(null, ((AttributeRef) result.getFirst()).getAnnotation());
        assertEquals(null, ((AttributeRef) result.getFirst()).getDefault());
        assertEquals(null, ((AttributeRef) result.getFirst()).getFixed());
        assertEquals(null, ((AttributeRef) result.getFirst()).getId());
        assertEquals(AttributeUse.Required, ((AttributeRef) result.getFirst()).getUse());
        Attribute newAttribute = ((AttributeRef) result.getFirst()).getAttribute();
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(newAttribute.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(newAttribute.getName()).getReference()));
        assertEquals(null, newAttribute.getAnnotation());
        assertEquals(null, newAttribute.getDefault());
        assertEquals(null, newAttribute.getFixed());
        assertEquals(null, newAttribute.getForm());
        assertEquals(null, newAttribute.getId());
        assertEquals("{A}attributeA", newAttribute.getName());
        assertEquals(false, newAttribute.getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", newAttribute.getSimpleType().getName());
        assertEquals(false, (boolean) newAttribute.getTypeAttr());
        assertEquals(AttributeUse.Optional, newAttribute.getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefRequiredOptionalAttributeRef() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Required);
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Optional);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefRequiredRequiredAttributeRef() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Required);
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Required);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefRequiredProhibitedAttributeRef() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Required);
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Prohibited);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Required, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefProhibitedOptionalAttributeRef() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Prohibited);
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Optional);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefProhibitedRequiredAttributeRef() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Prohibited);
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Required);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Prohibited, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeRefProhibitedProhibitedAttributeRef() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Prohibited);
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Prohibited);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeAttributeRef() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes and
     * attribute references with annotations.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeAttributeRefAnnotations() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        attributeA.setAnnotation(annotationA);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertTrue(((Attribute) result.getFirst()).getAnnotation().getAppInfos().size() == 1);
        assertEquals("blaA", ((Attribute) result.getFirst()).getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((Attribute) result.getFirst()).getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaaA", ((Attribute) result.getFirst()).getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes and
     * attribute references without type attribute.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeAttributeRefWithoutTypeAttr() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", new SimpleContentList(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null))));
        simpleTypeA.setIsAnonymous(true);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributeA.setTypeAttr(false);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeA", new SimpleContentList(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}integer", new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null))));
        simpleTypeB.setIsAnonymous(true);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeB.setTypeAttr(false);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.simpleTypeA-simpleTypeA", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes and
     * attribute references with empty type intersection.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeAttributeRefEmptyTypeIntersection() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes and
     * attribute references with different fixed values.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeAttributeRefDifferentFixed() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributeA.setFixed("bla");
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setFixed("bla2");
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals("bla", ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes and
     * attribute references with same fixed values.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeAttributeRefSameFixed() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributeA.setFixed("bla");
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setFixed("bla");
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));
        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes and
     * attribute references with single fixed values.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeAttributeRefSingleFixed() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributeA.setFixed("bla");
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals("bla", ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes and
     * attribute references with different default values.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeAttributeRefDifferentDefault() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributeA.setDefault("bla");
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setDefault("bla2");
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals("bla", ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes and
     * attribute references with same default values.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeAttributeRefSameDefault() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributeA.setDefault("bla");
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setDefault("bla");
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals("bla", ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes and
     * attribute references with same default values but the resulting attribute
     * is not oprtional.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeAttributeRefSameDefaultNotOptional() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributeA.setUse(AttributeUse.Required);
        attributeA.setDefault("bla");
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setDefault("bla");
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Required, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes and
     * attribute references with single default values.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeAttributeRefSingleDefault() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setDefault("bla");
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeAttributeRefOptionalOptional() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributeA.setUse(AttributeUse.Optional);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Optional);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeAttributeRefOptionalRequired() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributeA.setUse(AttributeUse.Optional);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Required);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Prohibited, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeAttributeRefOptionalProhibited() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributeA.setUse(AttributeUse.Optional);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Prohibited);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Required, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeAttributeRefRequiredOptional() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributeA.setUse(AttributeUse.Required);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Optional);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeAttributeRefRequiredRequired() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributeA.setUse(AttributeUse.Required);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Required);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeAttributeRefRequiredProhibited() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributeA.setUse(AttributeUse.Required);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Prohibited);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, outputSchema);
        attributeOldSchemaMap.put(attributeB, outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Required, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeAttributeRefProhibitedOptional() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributeA.setUse(AttributeUse.Prohibited);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Optional);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeAttributeRefProhibitedRequired() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributeA.setUse(AttributeUse.Prohibited);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Required);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, outputSchema);
        attributeOldSchemaMap.put(attributeB, outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Prohibited, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeAttributeRefProhibitedProhibited() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributeA.setUse(AttributeUse.Prohibited);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Prohibited);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeAttributeRefQualifiedQualified() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeB.setForm(XSDSchema.Qualification.qualified);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeAttributeRefQualifiedUnqualified() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeB.setForm(XSDSchema.Qualification.qualified);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, outputSchema);
        attributeOldSchemaMap.put(attributeB, outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeAttributeRefForeignQualifiedQualified() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{B}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{B}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeB.setForm(XSDSchema.Qualification.qualified);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.setSchemaLocation("C:/A.xsd");
        XSDSchema oldSchemaA = new XSDSchema("A");
        XSDSchema oldSchemaB = new XSDSchema("B");
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        oldSchemaA.getAttributeSymbolTable().updateOrCreateReference("{A}attributeA", attributeA);
        oldSchemaA.addAttribute(oldSchemaA.getAttributeSymbolTable().getReference("{A}attributeA"));
        oldSchemaB.getAttributeSymbolTable().updateOrCreateReference("{B}attributeB", attributeB);
        oldSchemaB.addAttribute(oldSchemaB.getAttributeSymbolTable().getReference("{B}attributeB"));
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, oldSchemaA);
        typeOldSchemaMap.put(simpleTypeB, oldSchemaB);
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        LinkedHashSet<XSDSchema> otherSchemata = new LinkedHashSet<XSDSchema>();
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, outputSchema);
        attributeOldSchemaMap.put(attributeB, outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, namespaceOutputSchemaMap, namespaceOutputSchemaMap, anyAttributeOldSchemaMap, attributeOldSchemaMap, usedIDs, namespaceAbbreviationMap, otherSchemata, "C:/");
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AttributeGroupRef);
        assertTrue(((AttributeGroupRef) result.getFirst()).getAttributeGroup().getAttributeParticles().size() == 1);
        AttributeParticle newAttributeParticle = ((AttributeGroupRef) result.getFirst()).getAttributeGroup().getAttributeParticles().getFirst();
        assertTrue(newAttributeParticle instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) newAttributeParticle).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) newAttributeParticle).getName()).getReference()));
        assertEquals(null, ((Attribute) newAttributeParticle).getAnnotation());
        assertEquals(null, ((Attribute) newAttributeParticle).getDefault());
        assertEquals(null, ((Attribute) newAttributeParticle).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) newAttributeParticle).getForm());
        assertEquals(null, ((Attribute) newAttributeParticle).getId());
        assertEquals("{B}attributeA", ((Attribute) newAttributeParticle).getName());
        assertEquals(false, ((Attribute) newAttributeParticle).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) newAttributeParticle).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) newAttributeParticle).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) newAttributeParticle).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeAttributeRefForeignQualifiedUnqualified() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.unqualified);
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeB.setForm(XSDSchema.Qualification.qualified);
        subtrahendAttributeParticles.add(attributeRefB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for any attributes and attribute.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAnyAttributeAttributeNull() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Skip, "##any");
        subtrahendAttributeParticles.add(anyAttributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeB, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, namespaceOutputSchemaMap, namespaceOutputSchemaMap, anyAttributeOldSchemaMap, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for any  attribute and attribute
     * reference.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAnyAttributeAttributeReferenceNull() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        minuendAttributeParticles.add(attributeRefA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Skip, "##any");
        subtrahendAttributeParticles.add(anyAttributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeB, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, namespaceOutputSchemaMap, namespaceOutputSchemaMap, anyAttributeOldSchemaMap, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of multiple attributes
     * with a prohibited attribute in one list.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesMultipleAttributesEmptyProhibited() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        Attribute attributeC = new Attribute("{A}attributeB", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeC.setUse(AttributeUse.Prohibited);
        subtrahendAttributeParticles.add(attributeB);
        subtrahendAttributeParticles.add(attributeC);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, outputSchema);
        attributeOldSchemaMap.put(attributeB, outputSchema);
        attributeOldSchemaMap.put(attributeC, outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of multiple attributes
     * with a required attribute in one list.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesMultipleAttributesEmptyRequired() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        minuendAttributeParticles.add(attributeA);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        Attribute attributeC = new Attribute("{A}attributeB", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeC.setUse(AttributeUse.Required);
        subtrahendAttributeParticles.add(attributeB);
        subtrahendAttributeParticles.add(attributeC);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, outputSchema);
        attributeOldSchemaMap.put(attributeB, outputSchema);
        attributeOldSchemaMap.put(attributeC, outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list of multiple attributes
     * with a required attribute in one list.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributesMultipleAttributesEmptyResultRequired() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setUse(AttributeUse.Required);
        SimpleType simpleTypeD = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeD = new Attribute("{A}attributeB", new SymbolTableRef<Type>(simpleTypeD.getName(), simpleTypeD));
        attributeD.setUse(AttributeUse.Required);
        minuendAttributeParticles.add(attributeA);
        minuendAttributeParticles.add(attributeD);

        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        Attribute attributeC = new Attribute("{A}attributeB", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeB));
        subtrahendAttributeParticles.add(attributeB);
        subtrahendAttributeParticles.add(attributeC);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, outputSchema);
        attributeOldSchemaMap.put(attributeB, outputSchema);
        attributeOldSchemaMap.put(attributeC, outputSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 2);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}difference-type.string-integer", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Required, ((Attribute) result.getFirst()).getUse());
        assertTrue(result.getLast() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getLast()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getLast()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getLast()).getAnnotation());
        assertEquals(null, ((Attribute) result.getLast()).getDefault());
        assertEquals(null, ((Attribute) result.getLast()).getFixed());
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result.getLast()).getForm());
        assertEquals(null, ((Attribute) result.getLast()).getId());
        assertEquals("{A}attributeB", ((Attribute) result.getLast()).getName());
        assertEquals(false, ((Attribute) result.getLast()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}integer", ((Attribute) result.getLast()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getLast()).getTypeAttr());
        assertEquals(AttributeUse.Required, ((Attribute) result.getLast()).getUse());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for any attribute.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAnyAttribute() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Lax, "##any");
        minuendAttributeParticles.add(anyAttributeA);
        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, anyAttributeOldSchemaMap, null, null, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##any", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a strict any attribute.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAnyAttributeStrict() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Strict, "bla");
        minuendAttributeParticles.add(anyAttributeA);
        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, anyAttributeOldSchemaMap, null, null, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for any attribute with ID.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAnyAttributeID() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Skip, "##any");
        anyAttributeA.setId("id");
        minuendAttributeParticles.add(anyAttributeA);
        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchema);
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, anyAttributeOldSchemaMap, null, usedIDs, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals("id.4", ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##any", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for any attribute with annotation.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAnyAttributeAnnotation() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Skip, "##any");
        minuendAttributeParticles.add(anyAttributeA);
        Annotation annotation = new Annotation();
        AppInfo appInfo = new AppInfo();
        appInfo.setSource("bla");
        Documentation documentation = new Documentation();
        documentation.setSource("blaa");
        annotation.addAppInfos(appInfo);
        annotation.addDocumentations(documentation);
        anyAttributeA.setAnnotation(annotation);
        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, anyAttributeOldSchemaMap, null, null, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertNotSame(annotation, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertTrue(((AnyAttribute) result.getFirst()).getAnnotation().getAppInfos().size() == 1);
        assertEquals("bla", ((AnyAttribute) result.getFirst()).getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((AnyAttribute) result.getFirst()).getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaa", ((AnyAttribute) result.getFirst()).getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##any", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for any attributes.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAnyAttributes() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Lax, "##any");
        minuendAttributeParticles.add(anyAttributeA);
        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Lax, "##local");
        subtrahendAttributeParticles.add(anyAttributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchema);
        anyAttributeOldSchemaMap.put(anyAttributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, anyAttributeOldSchemaMap, null, null, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##any", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for any attributes with
     * annotations.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAnyAttributesAnnotation() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Lax, "##any");
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        anyAttributeA.setAnnotation(annotationA);
        minuendAttributeParticles.add(anyAttributeA);
        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Lax, "##local");
        Annotation annotationB = new Annotation();
        AppInfo appInfoB = new AppInfo();
        appInfoB.setSource("blaB");
        Documentation documentationB = new Documentation();
        documentationB.setSource("blaaB");
        annotationB.addAppInfos(appInfoB);
        annotationB.addDocumentations(documentationB);
        anyAttributeB.setAnnotation(annotationB);
        subtrahendAttributeParticles.add(anyAttributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchema);
        anyAttributeOldSchemaMap.put(anyAttributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, anyAttributeOldSchemaMap, null, null, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertTrue(((AnyAttribute) result.getFirst()).getAnnotation().getAppInfos().size() == 1);
        assertEquals("blaA", ((AnyAttribute) result.getFirst()).getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((AnyAttribute) result.getFirst()).getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaaA", ((AnyAttribute) result.getFirst()).getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##any", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for any attributes with IDs.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAnyAttributesID() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Lax, "##any");
        anyAttributeA.setId("idOne");
        minuendAttributeParticles.add(anyAttributeA);
        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Lax, "##local");
        anyAttributeB.setId("idTwo");
        subtrahendAttributeParticles.add(anyAttributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchema);
        anyAttributeOldSchemaMap.put(anyAttributeB, oldSchema);
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("idOne.idTwo");
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, anyAttributeOldSchemaMap, null, usedIDs, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals("idOne.3", ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##any", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for any attribute for strict
     * validated any pattern.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAnyAttributeStrictAny() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Strict, "##any");
        minuendAttributeParticles.add(anyAttributeA);
        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();

        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchema);
        oldSchema.getAttributeSymbolTable().updateOrCreateReference("{A}attributeA", attributeA);
        oldSchema.addAttribute(oldSchema.getAttributeSymbolTable().getReference("{A}attributeA"));
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, anyAttributeOldSchemaMap, attributeOldSchemaMap, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertEquals(attributeA.getName(), ((Attribute) result.getFirst()).getName());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for any attribute for strict
     * validated any pattern with Uris.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAnyAttributeStrictUris() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Strict, "B");
        minuendAttributeParticles.add(anyAttributeA);
        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();

        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));

        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        Attribute attributeB = new Attribute("{B}attributeB", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        outputSchema.setSchemaLocation("C:/A.xsd");
        XSDSchema oldSchemaA = new XSDSchema("A");
        XSDSchema oldSchemaB = new XSDSchema("B");
        ImportedSchema importedSchema = new ImportedSchema("B", "C:/b.xsd");
        importedSchema.setSchema(oldSchemaB);
        oldSchemaA.addForeignSchema(importedSchema);
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchemaA);
        oldSchemaA.getAttributeSymbolTable().updateOrCreateReference("{A}attributeA", attributeA);
        oldSchemaA.addAttribute(oldSchemaA.getAttributeSymbolTable().getReference("{A}attributeA"));
        oldSchemaB.getAttributeSymbolTable().updateOrCreateReference("{B}attributeB", attributeB);
        oldSchemaB.addAttribute(oldSchemaB.getAttributeSymbolTable().getReference("{B}attributeB"));
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, oldSchemaA);
        typeOldSchemaMap.put(simpleTypeB, oldSchemaB);
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        LinkedHashSet<XSDSchema> otherSchemata = new LinkedHashSet<XSDSchema>();
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, namespaceOutputSchemaMap, namespaceOutputSchemaMap, anyAttributeOldSchemaMap, attributeOldSchemaMap, usedIDs, namespaceAbbreviationMap, otherSchemata, "C:/");
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AttributeGroupRef);
        assertEquals(attributeB.getName(), ((Attribute) ((AttributeGroupRef) result.getFirst()).getAttributeGroup().getAttributeParticles().getFirst()).getName());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for any attribute for strict
     * validated any pattern with target namespace.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAnyAttributeStrictTargetNamespace() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Strict, "##targetNamespace");
        minuendAttributeParticles.add(anyAttributeA);
        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();

        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));

        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        Attribute attributeB = new Attribute("{B}attributeB", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        outputSchema.setSchemaLocation("C:/A.xsd");
        XSDSchema oldSchemaA = new XSDSchema("A");
        XSDSchema oldSchemaB = new XSDSchema("B");
        ImportedSchema importedSchema = new ImportedSchema("B", "C:/b.xsd");
        importedSchema.setSchema(oldSchemaB);
        oldSchemaA.addForeignSchema(importedSchema);
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchemaA);
        oldSchemaA.getAttributeSymbolTable().updateOrCreateReference("{A}attributeA", attributeA);
        oldSchemaA.addAttribute(oldSchemaA.getAttributeSymbolTable().getReference("{A}attributeA"));
        oldSchemaB.getAttributeSymbolTable().updateOrCreateReference("{B}attributeB", attributeB);
        oldSchemaB.addAttribute(oldSchemaB.getAttributeSymbolTable().getReference("{B}attributeB"));
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, oldSchemaA);
        typeOldSchemaMap.put(simpleTypeB, oldSchemaB);
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        LinkedHashSet<XSDSchema> otherSchemata = new LinkedHashSet<XSDSchema>();
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, namespaceOutputSchemaMap, namespaceOutputSchemaMap, anyAttributeOldSchemaMap, attributeOldSchemaMap, usedIDs, namespaceAbbreviationMap, otherSchemata, "C:/");
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertEquals(attributeA.getName(), ((Attribute) result.getFirst()).getName());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for any attribute for strict
     * validated any pattern with ##local.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAnyAttributeStrictLocal() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Strict, "##local");
        minuendAttributeParticles.add(anyAttributeA);
        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();

        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));

        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        Attribute attributeB = new Attribute("{}attributeB", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        outputSchema.setSchemaLocation("C:/A.xsd");
        XSDSchema oldSchemaA = new XSDSchema("A");
        XSDSchema oldSchemaB = new XSDSchema("");
        ImportedSchema importedSchema = new ImportedSchema(null, "C:/b.xsd");
        importedSchema.setSchema(oldSchemaB);
        oldSchemaA.addForeignSchema(importedSchema);
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchemaA);
        oldSchemaA.getAttributeSymbolTable().updateOrCreateReference("{A}attributeA", attributeA);
        oldSchemaA.addAttribute(oldSchemaA.getAttributeSymbolTable().getReference("{A}attributeA"));
        oldSchemaB.getAttributeSymbolTable().updateOrCreateReference("{}attributeB", attributeB);
        oldSchemaB.addAttribute(oldSchemaB.getAttributeSymbolTable().getReference("{}attributeB"));
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, oldSchemaA);
        typeOldSchemaMap.put(simpleTypeB, oldSchemaB);
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        LinkedHashSet<XSDSchema> otherSchemata = new LinkedHashSet<XSDSchema>();
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, namespaceOutputSchemaMap, namespaceOutputSchemaMap, anyAttributeOldSchemaMap, attributeOldSchemaMap, usedIDs, namespaceAbbreviationMap, otherSchemata, "C:/");
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertEquals("{A}attributeB", ((Attribute) result.getFirst()).getName());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for any attribute for strict
     * validated any pattern with ##other.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAnyAttributeStrictOther() {
        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Strict, "##other");
        minuendAttributeParticles.add(anyAttributeA);
        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();

        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));

        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        Attribute attributeB = new Attribute("{B}attributeB", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        outputSchema.setSchemaLocation("C:/A.xsd");
        XSDSchema oldSchemaA = new XSDSchema("A");
        XSDSchema oldSchemaB = new XSDSchema("B");
        ImportedSchema importedSchema = new ImportedSchema("B", "C:/b.xsd");
        importedSchema.setSchema(oldSchemaB);
        oldSchemaA.addForeignSchema(importedSchema);
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchemaA);
        oldSchemaA.getAttributeSymbolTable().updateOrCreateReference("{A}attributeA", attributeA);
        oldSchemaA.addAttribute(oldSchemaA.getAttributeSymbolTable().getReference("{A}attributeA"));
        oldSchemaB.getAttributeSymbolTable().updateOrCreateReference("{B}attributeB", attributeB);
        oldSchemaB.addAttribute(oldSchemaB.getAttributeSymbolTable().getReference("{B}attributeB"));
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, oldSchemaA);
        typeOldSchemaMap.put(simpleTypeB, oldSchemaB);
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        LinkedHashSet<XSDSchema> otherSchemata = new LinkedHashSet<XSDSchema>();
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, namespaceOutputSchemaMap, namespaceOutputSchemaMap, anyAttributeOldSchemaMap, attributeOldSchemaMap, usedIDs, namespaceAbbreviationMap, otherSchemata, "C:/");
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);
        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AttributeGroupRef);
        assertEquals(attributeB.getName(), ((Attribute) ((AttributeGroupRef) result.getFirst()).getAttributeGroup().getAttributeParticles().getFirst()).getName());
    }

    /**
     * Test of generateAttributeParticleDifference method, of class
     * AttributeParticleDifferenceGenerator for a list with an attributeGroup.
     */
    @Test
    public void testGenerateAttributeParticleDifferenceAttributeGroupResolving() {
        AttributeGroup attributeGroupA = new AttributeGroup("{A}attributeGroupA");
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeB", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        AnyAttribute anyAttributeC = new AnyAttribute();
        attributeGroupA.addAttributeParticle(attributeA);
        attributeGroupA.addAttributeParticle(attributeRefB);
        attributeGroupA.addAttributeParticle(anyAttributeC);
        AttributeGroupRef attributeGroupRefA = new AttributeGroupRef(new SymbolTableRef<AttributeGroup>(attributeGroupA.getName(), attributeGroupA));

        LinkedList<AttributeParticle> minuendAttributeParticles = new LinkedList<AttributeParticle>();
        minuendAttributeParticles.add(attributeGroupRefA);
        LinkedList<AttributeParticle> subtrahendAttributeParticles = new LinkedList<AttributeParticle>();

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeC, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, oldSchema);
        attributeOldSchemaMap.put(attributeB, oldSchema);
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, namespaceOutputSchemaMap, namespaceOutputSchemaMap, anyAttributeOldSchemaMap, attributeOldSchemaMap, null, null, null, "C:/");
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));
        instance.generateNewTopLevelAttribute(attributeB);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleDifference(minuendAttributeParticles, subtrahendAttributeParticles);

        assertTrue(result.size() == 2);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
        assertTrue(result.get(1) instanceof Attribute);
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.get(1)).getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.get(1)).getName()).getReference()));
        assertEquals(null, ((Attribute) result.get(1)).getAnnotation());
        assertEquals(null, ((Attribute) result.get(1)).getDefault());
        assertEquals(null, ((Attribute) result.get(1)).getFixed());
        assertEquals(null, ((Attribute) result.get(1)).getForm());
        assertEquals(null, ((Attribute) result.get(1)).getId());
        assertEquals("{A}attributeB", ((Attribute) result.get(1)).getName());
        assertEquals(false, ((Attribute) result.get(1)).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.get(1)).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.get(1)).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.get(1)).getUse());
    }

    /**
     * Test of generateNewTopLevelAttribute method, of class
     * AttributeParticleDifferenceGenerator.
     */
    @Test
    public void testGenerateNewTopLevelAttribute() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));

        Attribute result = instance.generateNewTopLevelAttribute(oldAttribute);

        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getForm());
        assertEquals(null, result.getId());
        assertEquals("{A}attributeA", result.getName());
        assertEquals(false, result.getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", result.getSimpleType().getName());
        assertEquals(false, (boolean) result.getTypeAttr());
        assertEquals(AttributeUse.Optional, result.getUse());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelAttribute method, of class
     * AttributeParticleDifferenceGenerator for attribute with type attribute.
     */
    @Test
    public void testGenerateNewTopLevelAttributeTypeAttribute() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        oldAttribute.setTypeAttr(true);

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));

        Attribute result = instance.generateNewTopLevelAttribute(oldAttribute);

        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getForm());
        assertEquals(null, result.getId());
        assertEquals("{A}attributeA", result.getName());
        assertEquals(false, result.getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", result.getSimpleType().getName());
        assertEquals(true, (boolean) result.getTypeAttr());
        assertEquals(AttributeUse.Optional, result.getUse());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelAttribute method, of class
     * AttributeParticleDifferenceGenerator for attribute with annotation.
     */
    @Test
    public void testGenerateNewTopLevelAttributeAnnotation() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        oldAttribute.setAnnotation(annotationA);

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));

        Attribute result = instance.generateNewTopLevelAttribute(oldAttribute);

        assertTrue(result.getAnnotation().getAppInfos().size() == 1);
        assertEquals("blaA", result.getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(result.getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaaA", result.getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getForm());
        assertEquals(null, result.getId());
        assertEquals("{A}attributeA", result.getName());
        assertEquals(false, result.getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", result.getSimpleType().getName());
        assertEquals(false, (boolean) result.getTypeAttr());
        assertEquals(AttributeUse.Optional, result.getUse());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelAttribute method, of class
     * AttributeParticleDifferenceGenerator for attribute with ID.
     */
    @Test
    public void testGenerateNewTopLevelAttributeID() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        oldAttribute.setId("id");

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, usedIDs, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));

        Attribute result = instance.generateNewTopLevelAttribute(oldAttribute);

        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getForm());
        assertEquals("id.1", result.getId());
        assertEquals("{A}attributeA", result.getName());
        assertEquals(false, result.getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", result.getSimpleType().getName());
        assertEquals(false, (boolean) result.getTypeAttr());
        assertEquals(AttributeUse.Optional, result.getUse());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelAttribute method, of class
     * AttributeParticleDifferenceGenerator for attribute with fixed value.
     */
    @Test
    public void testGenerateNewTopLevelAttributeFixed() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        oldAttribute.setFixed("bla");

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));

        Attribute result = instance.generateNewTopLevelAttribute(oldAttribute);

        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getDefault());
        assertEquals("bla", result.getFixed());
        assertEquals(null, result.getForm());
        assertEquals(null, result.getId());
        assertEquals("{A}attributeA", result.getName());
        assertEquals(false, result.getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", result.getSimpleType().getName());
        assertEquals(false, (boolean) result.getTypeAttr());
        assertEquals(AttributeUse.Optional, result.getUse());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelAttribute method, of class
     * AttributeParticleDifferenceGenerator for attribute with default value.
     */
    @Test
    public void testGenerateNewTopLevelAttributeDefault() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        oldAttribute.setDefault("bla");

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));

        Attribute result = instance.generateNewTopLevelAttribute(oldAttribute);

        assertEquals(null, result.getAnnotation());
        assertEquals("bla", result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getForm());
        assertEquals(null, result.getId());
        assertEquals("{A}attributeA", result.getName());
        assertEquals(false, result.getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", result.getSimpleType().getName());
        assertEquals(false, (boolean) result.getTypeAttr());
        assertEquals(AttributeUse.Optional, result.getUse());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelAttribute method, of class
     * AttributeParticleDifferenceGenerator without build-in type.
     */
    @Test
    public void testGenerateNewTopLevelAttributeWithoutBuildIn() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", new SimpleContentRestriction(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA)));

        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        Attribute result = instance.generateNewTopLevelAttribute(oldAttribute);

        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getForm());
        assertEquals(null, result.getId());
        assertEquals("{A}attributeA", result.getName());
        assertEquals(false, result.getSimpleType().isAnonymous());
        assertEquals("{A}simpleTypeB", result.getSimpleType().getName());
        assertEquals(false, (boolean) result.getTypeAttr());
        assertEquals(AttributeUse.Optional, result.getUse());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelAttribute method, of class
     * AttributeParticleDifferenceGenerator with type from foreign schema.
     */
    @Test
    public void testGenerateNewTopLevelAttributeForeignType() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleType simpleTypeB = new SimpleType("{B}simpleTypeB", new SimpleContentRestriction(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA)));

        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("B"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        Attribute result = instance.generateNewTopLevelAttribute(oldAttribute);

        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getForm());
        assertEquals(null, result.getId());
        assertEquals("{A}attributeA", result.getName());
        assertEquals(false, result.getSimpleType().isAnonymous());
        assertEquals("{A}simpleTypeB", result.getSimpleType().getName());
        assertEquals(false, (boolean) result.getTypeAttr());
        assertEquals(AttributeUse.Optional, result.getUse());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleDifferenceGenerator for an any attribute.
     */
    @Test
    public void testGenerateNewAttributeParticleAnyAttribute() {
        AttributeParticle oldAttributeParticle = new AnyAttribute(ProcessContentsInstruction.Lax, "##local");

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getId());
        assertEquals("##local", ((AnyAttribute) result).getNamespace());
        assertEquals(ProcessContentsInstruction.Lax, ((AnyAttribute) result).getProcessContentsInstruction());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleDifferenceGenerator for an any attribute with id.
     */
    @Test
    public void testGenerateNewAttributeParticleAnyAttributeID() {
        AttributeParticle oldAttributeParticle = new AnyAttribute(ProcessContentsInstruction.Lax, "##local");
        oldAttributeParticle.setId("id");

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, usedIDs, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertEquals(null, result.getAnnotation());
        assertEquals("id.1", result.getId());
        assertEquals("##local", ((AnyAttribute) result).getNamespace());
        assertEquals(ProcessContentsInstruction.Lax, ((AnyAttribute) result).getProcessContentsInstruction());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleDifferenceGenerator for an any attribute with annotation.
     */
    @Test
    public void testGenerateNewAttributeParticleAnyAttributeAnnotation() {
        AttributeParticle oldAttributeParticle = new AnyAttribute(ProcessContentsInstruction.Lax, "##local");
        Annotation annotation = new Annotation();
        AppInfo appInfo = new AppInfo();
        appInfo.setSource("bla");
        Documentation documentation = new Documentation();
        documentation.setSource("blaa");
        annotation.addAppInfos(appInfo);
        annotation.addDocumentations(documentation);
        oldAttributeParticle.setAnnotation(annotation);

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertNotSame(annotation, result.getAnnotation());
        assertTrue(result.getAnnotation().getAppInfos().size() == 1);
        assertEquals("bla", result.getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(result.getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaa", result.getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(null, result.getId());
        assertEquals("##local", ((AnyAttribute) result).getNamespace());
        assertEquals(ProcessContentsInstruction.Lax, ((AnyAttribute) result).getProcessContentsInstruction());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleDifferenceGenerator for an any attribute with namespace value.
     */
    @Test
    public void testGenerateNewAttributeParticleAnyAttributeNamespace() {
        AttributeParticle oldAttributeParticle = new AnyAttribute(ProcessContentsInstruction.Lax, "bla");

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getId());
        assertEquals("bla", ((AnyAttribute) result).getNamespace());
        assertEquals(ProcessContentsInstruction.Lax, ((AnyAttribute) result).getProcessContentsInstruction());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleDifferenceGenerator for an skip validated any attribute .
     */
    @Test
    public void testGenerateNewAttributeParticleAnyAttributeSkip() {
        AttributeParticle oldAttributeParticle = new AnyAttribute(ProcessContentsInstruction.Skip, "##local");

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getId());
        assertEquals("##local", ((AnyAttribute) result).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result).getProcessContentsInstruction());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleDifferenceGenerator for an strict validated any attribute .
     */
    @Test
    public void testGenerateNewAttributeParticleAnyAttributeStrict() {
        AttributeParticle oldAttributeParticle = new AnyAttribute(ProcessContentsInstruction.Strict, "##local");

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getId());
        assertEquals("##local", ((AnyAttribute) result).getNamespace());
        assertEquals(ProcessContentsInstruction.Strict, ((AnyAttribute) result).getProcessContentsInstruction());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleDifferenceGenerator for an attribute.
     */
    @Test
    public void testGenerateNewAttributeParticleAttribute() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        AttributeParticle oldAttributeParticle = oldAttribute;

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertEquals("{A}simpleTypeA", ((Attribute) result).getSimpleType().getName());
        assertEquals(false, ((Attribute) result).getSimpleType().isAnonymous());
        assertEquals(oldAttribute.getTypeAttr(), ((Attribute) result).getTypeAttr());
        assertEquals(oldAttribute.getDefault(), ((Attribute) result).getDefault());
        assertEquals(oldAttribute.getFixed(), ((Attribute) result).getFixed());
        assertEquals(null, ((Attribute) result).getId());
        assertEquals(null, ((Attribute) result).getAnnotation());
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result).getName()).getReference()));
        assertEquals(AttributeUse.Optional, ((Attribute) result).getUse());
        assertEquals(null, ((Attribute) result).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleDifferenceGenerator for an attribute with use required.
     */
    @Test
    public void testGenerateNewAttributeParticleAttributeUseRequired() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        oldAttribute.setUse(AttributeUse.Required);
        AttributeParticle oldAttributeParticle = oldAttribute;

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertEquals("{A}simpleTypeA", ((Attribute) result).getSimpleType().getName());
        assertEquals(false, ((Attribute) result).getSimpleType().isAnonymous());
        assertEquals(oldAttribute.getTypeAttr(), ((Attribute) result).getTypeAttr());
        assertEquals(oldAttribute.getDefault(), ((Attribute) result).getDefault());
        assertEquals(oldAttribute.getFixed(), ((Attribute) result).getFixed());
        assertEquals(null, ((Attribute) result).getId());
        assertEquals(null, ((Attribute) result).getAnnotation());
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result).getName()).getReference()));
        assertEquals(AttributeUse.Required, ((Attribute) result).getUse());
        assertEquals(null, ((Attribute) result).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleDifferenceGenerator for an attribute with use optional.
     */
    @Test
    public void testGenerateNewAttributeParticleAttributeUseOptional() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        oldAttribute.setUse(AttributeUse.Optional);
        AttributeParticle oldAttributeParticle = oldAttribute;

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertEquals("{A}simpleTypeA", ((Attribute) result).getSimpleType().getName());
        assertEquals(false, ((Attribute) result).getSimpleType().isAnonymous());
        assertEquals(oldAttribute.getTypeAttr(), ((Attribute) result).getTypeAttr());
        assertEquals(oldAttribute.getDefault(), ((Attribute) result).getDefault());
        assertEquals(oldAttribute.getFixed(), ((Attribute) result).getFixed());
        assertEquals(null, ((Attribute) result).getId());
        assertEquals(null, ((Attribute) result).getAnnotation());
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result).getName()).getReference()));
        assertEquals(AttributeUse.Optional, ((Attribute) result).getUse());
        assertEquals(null, ((Attribute) result).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleDifferenceGenerator for an attribute with use prohibited.
     */
    @Test
    public void testGenerateNewAttributeParticleAttributeUseProhibited() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        oldAttribute.setUse(AttributeUse.Prohibited);
        AttributeParticle oldAttributeParticle = oldAttribute;

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertEquals("{A}simpleTypeA", ((Attribute) result).getSimpleType().getName());
        assertEquals(false, ((Attribute) result).getSimpleType().isAnonymous());
        assertEquals(oldAttribute.getTypeAttr(), ((Attribute) result).getTypeAttr());
        assertEquals(oldAttribute.getDefault(), ((Attribute) result).getDefault());
        assertEquals(oldAttribute.getFixed(), ((Attribute) result).getFixed());
        assertEquals(null, ((Attribute) result).getId());
        assertEquals(null, ((Attribute) result).getAnnotation());
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result).getName()).getReference()));
        assertEquals(AttributeUse.Prohibited, ((Attribute) result).getUse());
        assertEquals(null, ((Attribute) result).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleDifferenceGenerator for an attribute with type attribute.
     */
    @Test
    public void testGenerateNewAttributeParticleAttributeTypeAttr() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        oldAttribute.setTypeAttr(true);
        AttributeParticle oldAttributeParticle = oldAttribute;

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertEquals("{A}simpleTypeA", ((Attribute) result).getSimpleType().getName());
        assertEquals(false, ((Attribute) result).getSimpleType().isAnonymous());
        assertEquals(oldAttribute.getTypeAttr(), ((Attribute) result).getTypeAttr());
        assertEquals(oldAttribute.getDefault(), ((Attribute) result).getDefault());
        assertEquals(oldAttribute.getFixed(), ((Attribute) result).getFixed());
        assertEquals(null, ((Attribute) result).getId());
        assertEquals(null, ((Attribute) result).getAnnotation());
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result).getName()).getReference()));
        assertEquals(AttributeUse.Optional, ((Attribute) result).getUse());
        assertEquals(null, ((Attribute) result).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleDifferenceGenerator for an attribute with default.
     */
    @Test
    public void testGenerateNewAttributeParticleAttributeDefault() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        oldAttribute.setDefault("bla");
        AttributeParticle oldAttributeParticle = oldAttribute;

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertEquals("{A}simpleTypeA", ((Attribute) result).getSimpleType().getName());
        assertEquals(false, ((Attribute) result).getSimpleType().isAnonymous());
        assertEquals(oldAttribute.getTypeAttr(), ((Attribute) result).getTypeAttr());
        assertEquals(oldAttribute.getDefault(), ((Attribute) result).getDefault());
        assertEquals(oldAttribute.getFixed(), ((Attribute) result).getFixed());
        assertEquals(null, ((Attribute) result).getId());
        assertEquals(null, ((Attribute) result).getAnnotation());
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result).getName()).getReference()));
        assertEquals(AttributeUse.Optional, ((Attribute) result).getUse());
        assertEquals(null, ((Attribute) result).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleDifferenceGenerator for an attribute with fixed.
     */
    @Test
    public void testGenerateNewAttributeParticleAttributeFixed() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        oldAttribute.setFixed("bla");
        AttributeParticle oldAttributeParticle = oldAttribute;

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertEquals("{A}simpleTypeA", ((Attribute) result).getSimpleType().getName());
        assertEquals(false, ((Attribute) result).getSimpleType().isAnonymous());
        assertEquals(oldAttribute.getTypeAttr(), ((Attribute) result).getTypeAttr());
        assertEquals(oldAttribute.getDefault(), ((Attribute) result).getDefault());
        assertEquals(oldAttribute.getFixed(), ((Attribute) result).getFixed());
        assertEquals(null, ((Attribute) result).getId());
        assertEquals(null, ((Attribute) result).getAnnotation());
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result).getName()).getReference()));
        assertEquals(AttributeUse.Optional, ((Attribute) result).getUse());
        assertEquals(null, ((Attribute) result).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleDifferenceGenerator for an unqualified attribute.
     */
    @Test
    public void testGenerateNewAttributeParticleAttributeQualifiedNull() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        oldAttribute.setForm(null);
        AttributeParticle oldAttributeParticle = oldAttribute;

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(oldAttribute, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertEquals("{A}simpleTypeA", ((Attribute) result).getSimpleType().getName());
        assertEquals(false, ((Attribute) result).getSimpleType().isAnonymous());
        assertEquals(oldAttribute.getTypeAttr(), ((Attribute) result).getTypeAttr());
        assertEquals(oldAttribute.getDefault(), ((Attribute) result).getDefault());
        assertEquals(oldAttribute.getFixed(), ((Attribute) result).getFixed());
        assertEquals(null, ((Attribute) result).getId());
        assertEquals(null, ((Attribute) result).getAnnotation());
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result).getName()).getReference()));
        assertEquals(AttributeUse.Optional, ((Attribute) result).getUse());
        assertEquals(null, ((Attribute) result).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleDifferenceGenerator for an foreign qualified attribute.
     */
    @Test
    public void testGenerateNewAttributeParticleAttributeForeignQualified() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        oldAttribute.setForm(XSDSchema.Qualification.qualified);
        AttributeParticle oldAttributeParticle = oldAttribute;

        XSDSchema outputSchema = new XSDSchema("B");
        outputSchema.setSchemaLocation("Main");
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(oldAttribute, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("B", outputSchema);
        LinkedHashSet<XSDSchema> otherSchemata = new LinkedHashSet<XSDSchema>();
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, namespaceOutputSchemaMap, null, null, attributeOldSchemaMap, null, namespaceAbbreviationMap, otherSchemata, "C:/");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertTrue(otherSchemata.size() == 1);
        assertTrue(otherSchemata.iterator().next().getAttributeGroups().contains(((AttributeGroupRef) result).getAttributeGroup()));
        assertTrue(otherSchemata.iterator().next().getAttributeGroupSymbolTable().hasReference(((AttributeGroupRef) result).getAttributeGroup().getName()));
        assertEquals(null, ((AttributeGroupRef) result).getAttributeGroup().getId());
        assertEquals(null, ((AttributeGroupRef) result).getAttributeGroup().getAnnotation());
        assertTrue(((AttributeGroupRef) result).getAttributeGroup().getAttributeParticles().size() == 1);
        AttributeParticle resultingAttribute = ((AttributeGroupRef) result).getAttributeGroup().getAttributeParticles().getFirst();
        assertEquals("{B}simpleTypeA", ((Attribute) resultingAttribute).getSimpleType().getName());
        assertEquals(false, ((Attribute) resultingAttribute).getSimpleType().isAnonymous());
        assertEquals(oldAttribute.getTypeAttr(), ((Attribute) resultingAttribute).getTypeAttr());
        assertEquals(oldAttribute.getDefault(), ((Attribute) resultingAttribute).getDefault());
        assertEquals(oldAttribute.getFixed(), ((Attribute) resultingAttribute).getFixed());
        assertEquals(null, ((Attribute) resultingAttribute).getId());
        assertEquals(null, ((Attribute) resultingAttribute).getAnnotation());
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) resultingAttribute).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) resultingAttribute).getName()).getReference()));
        assertEquals(AttributeUse.Optional, ((Attribute) resultingAttribute).getUse());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) resultingAttribute).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleDifferenceGenerator for an foreign unqualified attribute.
     */
    @Test
    public void testGenerateNewAttributeParticleAttributeForeignUnqualified() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        oldAttribute.setForm(XSDSchema.Qualification.unqualified);
        AttributeParticle oldAttributeParticle = oldAttribute;

        XSDSchema outputSchema = new XSDSchema("B");
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(oldAttribute, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertEquals("{B}simpleTypeA", ((Attribute) result).getSimpleType().getName());
        assertEquals(false, ((Attribute) result).getSimpleType().isAnonymous());
        assertEquals(oldAttribute.getTypeAttr(), ((Attribute) result).getTypeAttr());
        assertEquals(oldAttribute.getDefault(), ((Attribute) result).getDefault());
        assertEquals(oldAttribute.getFixed(), ((Attribute) result).getFixed());
        assertEquals(null, ((Attribute) result).getId());
        assertEquals(null, ((Attribute) result).getAnnotation());
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result).getName()).getReference()));
        assertEquals(AttributeUse.Optional, ((Attribute) result).getUse());
        assertEquals(null, ((Attribute) result).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleDifferenceGenerator for a qualified attribute.
     */
    @Test
    public void testGenerateNewAttributeParticleAttributeQualified() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        oldAttribute.setForm(XSDSchema.Qualification.qualified);
        AttributeParticle oldAttributeParticle = oldAttribute;

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(oldAttribute, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertEquals("{A}simpleTypeA", ((Attribute) result).getSimpleType().getName());
        assertEquals(false, ((Attribute) result).getSimpleType().isAnonymous());
        assertEquals(oldAttribute.getTypeAttr(), ((Attribute) result).getTypeAttr());
        assertEquals(oldAttribute.getDefault(), ((Attribute) result).getDefault());
        assertEquals(oldAttribute.getFixed(), ((Attribute) result).getFixed());
        assertEquals(null, ((Attribute) result).getId());
        assertEquals(null, ((Attribute) result).getAnnotation());
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result).getName()).getReference()));
        assertEquals(AttributeUse.Optional, ((Attribute) result).getUse());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) result).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleDifferenceGenerator for an attribute reference.
     */
    @Test
    public void testGenerateNewAttributeParticleAttributeRef() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        AttributeParticle oldAttributeParticle = attributeRefA;

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertTrue(result instanceof Attribute);
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result).getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result).getName()).getReference()));
        assertEquals(null, ((Attribute) result).getAnnotation());
        assertEquals(null, ((Attribute) result).getDefault());
        assertEquals(null, ((Attribute) result).getFixed());
        assertEquals(null, ((Attribute) result).getForm());
        assertEquals(null, ((Attribute) result).getId());
        assertEquals("{A}attributeA", ((Attribute) result).getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result).getUse());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleDifferenceGenerator for an attribute reference with ID.
     */
    @Test
    public void testGenerateNewAttributeParticleAttributeRefID() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setId("id");
        AttributeParticle oldAttributeParticle = attributeRefA;

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, usedIDs, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertTrue(result instanceof Attribute);
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result).getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result).getName()).getReference()));
        assertEquals(null, ((Attribute) result).getAnnotation());
        assertEquals(null, ((Attribute) result).getDefault());
        assertEquals(null, ((Attribute) result).getFixed());
        assertEquals(null, ((Attribute) result).getForm());
        assertEquals("id.1", ((Attribute) result).getId());
        assertEquals("{A}attributeA", ((Attribute) result).getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result).getUse());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleDifferenceGenerator for an attribute reference with
     * Annotation.
     */
    @Test
    public void testGenerateNewAttributeParticleAttributeRefAnnotation() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        Annotation annotation = new Annotation();
        AppInfo appInfo = new AppInfo();
        appInfo.setSource("bla");
        Documentation documentation = new Documentation();
        documentation.setSource("blaa");
        annotation.addAppInfos(appInfo);
        annotation.addDocumentations(documentation);
        attributeRefA.setAnnotation(annotation);
        AttributeParticle oldAttributeParticle = attributeRefA;

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertTrue(result instanceof Attribute);
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result).getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result).getName()).getReference()));
        assertNotSame(annotation, ((Attribute) result).getAnnotation());
        assertTrue(((Attribute) result).getAnnotation().getAppInfos().size() == 1);
        assertEquals("bla", ((Attribute) result).getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((Attribute) result).getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaa", ((Attribute) result).getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(null, ((Attribute) result).getDefault());
        assertEquals(null, ((Attribute) result).getFixed());
        assertEquals(null, ((Attribute) result).getForm());
        assertEquals(null, ((Attribute) result).getId());
        assertEquals("{A}attributeA", ((Attribute) result).getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result).getUse());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleDifferenceGenerator for an attribute reference with
     * use value prohibited.
     */
    @Test
    public void testGenerateNewAttributeParticleAttributeRefUseProhibited() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Prohibited);
        AttributeParticle oldAttributeParticle = attributeRefA;

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertTrue(result instanceof Attribute);
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result).getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result).getName()).getReference()));
        assertEquals(null, ((Attribute) result).getAnnotation());
        assertEquals(null, ((Attribute) result).getDefault());
        assertEquals(null, ((Attribute) result).getFixed());
        assertEquals(null, ((Attribute) result).getForm());
        assertEquals(null, ((Attribute) result).getId());
        assertEquals("{A}attributeA", ((Attribute) result).getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result).getTypeAttr());
        assertEquals(AttributeUse.Prohibited, ((Attribute) result).getUse());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleDifferenceGenerator for an attribute reference with
     * use value required.
     */
    @Test
    public void testGenerateNewAttributeParticleAttributeRefUseRequired() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Required);
        AttributeParticle oldAttributeParticle = attributeRefA;

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertTrue(result instanceof Attribute);
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result).getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result).getName()).getReference()));
        assertEquals(null, ((Attribute) result).getAnnotation());
        assertEquals(null, ((Attribute) result).getDefault());
        assertEquals(null, ((Attribute) result).getFixed());
        assertEquals(null, ((Attribute) result).getForm());
        assertEquals(null, ((Attribute) result).getId());
        assertEquals("{A}attributeA", ((Attribute) result).getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result).getTypeAttr());
        assertEquals(AttributeUse.Required, ((Attribute) result).getUse());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleDifferenceGenerator for an attribute reference referring
     * to a foreign attribute.
     */
    @Test
    public void testGenerateNewAttributeParticleAttributeRefForeignAttribute() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        oldAttribute.setForm(XSDSchema.Qualification.qualified);
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(oldAttribute.getName(), oldAttribute));
        AttributeParticle oldAttributeParticle = attributeRefA;

        XSDSchema outputSchema = new XSDSchema("B");
        outputSchema.setSchemaLocation("Main");
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(oldAttribute, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("B", outputSchema);
        LinkedHashSet<XSDSchema> otherSchemata = new LinkedHashSet<XSDSchema>();
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, namespaceOutputSchemaMap, null, null, attributeOldSchemaMap, null, namespaceAbbreviationMap, otherSchemata, "C:/");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertTrue(otherSchemata.size() == 1);
        assertTrue(otherSchemata.iterator().next().getAttributeGroups().contains(((AttributeGroupRef) result).getAttributeGroup()));
        assertTrue(otherSchemata.iterator().next().getAttributeGroupSymbolTable().hasReference(((AttributeGroupRef) result).getAttributeGroup().getName()));
        assertEquals(null, ((AttributeGroupRef) result).getAttributeGroup().getId());
        assertEquals(null, ((AttributeGroupRef) result).getAttributeGroup().getAnnotation());
        assertTrue(((AttributeGroupRef) result).getAttributeGroup().getAttributeParticles().size() == 1);
        AttributeParticle resultingAttribute = ((AttributeGroupRef) result).getAttributeGroup().getAttributeParticles().getFirst();
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) resultingAttribute).getSimpleType().getName());
        assertEquals(false, ((Attribute) resultingAttribute).getSimpleType().isAnonymous());
        assertEquals(oldAttribute.getTypeAttr(), ((Attribute) resultingAttribute).getTypeAttr());
        assertEquals(oldAttribute.getDefault(), ((Attribute) resultingAttribute).getDefault());
        assertEquals(oldAttribute.getFixed(), ((Attribute) resultingAttribute).getFixed());
        assertEquals(null, ((Attribute) resultingAttribute).getId());
        assertEquals(null, ((Attribute) resultingAttribute).getAnnotation());
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) resultingAttribute).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) resultingAttribute).getName()).getReference()));
        assertEquals(AttributeUse.Optional, ((Attribute) resultingAttribute).getUse());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) resultingAttribute).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleDifferenceGenerator for an attribute group reference.
     */
    @Test
    public void testGenerateNewAttributeParticleAttributeGroupReference() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));

        AttributeGroup oldAttributeGroup = new AttributeGroup("{A}attributeGroupA");
        oldAttributeGroup.addAttributeParticle(oldAttribute);

        AttributeParticle oldAttributeParticle = new AttributeGroupRef(new SymbolTableRef<AttributeGroup>("{A}attributeGroup", oldAttributeGroup));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(oldAttribute, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertTrue(result instanceof AttributeGroupRef);
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(((AttributeGroupRef) result).getAttributeGroup() instanceof AttributeGroup);
        assertEquals(null, ((AttributeGroupRef) result).getAttributeGroup().getId());
        assertEquals(null, ((AttributeGroupRef) result).getAttributeGroup().getAnnotation());
        assertTrue(outputSchema.getAttributeGroupSymbolTable().hasReference(((AttributeGroupRef) result).getAttributeGroup().getName()));
        assertTrue(outputSchema.getAttributeGroups().contains(((AttributeGroupRef) result).getAttributeGroup()));

        AttributeParticle resultingAttribute = ((AttributeGroupRef) result).getAttributeGroup().getAttributeParticles().getFirst();
        assertEquals("{A}simpleTypeA", ((Attribute) resultingAttribute).getSimpleType().getName());
        assertEquals(false, ((Attribute) resultingAttribute).getSimpleType().isAnonymous());
        assertEquals(oldAttribute.getTypeAttr(), ((Attribute) resultingAttribute).getTypeAttr());
        assertEquals(oldAttribute.getDefault(), ((Attribute) resultingAttribute).getDefault());
        assertEquals(oldAttribute.getFixed(), ((Attribute) resultingAttribute).getFixed());
        assertEquals(null, ((Attribute) resultingAttribute).getId());
        assertEquals(null, ((Attribute) resultingAttribute).getAnnotation());
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) resultingAttribute).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) resultingAttribute).getName()).getReference()));
        assertEquals(AttributeUse.Optional, ((Attribute) resultingAttribute).getUse());
        assertEquals(null, ((Attribute) resultingAttribute).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleDifferenceGenerator for an attribute group reference
     * referring to a foreign group.
     */
    @Test
    public void testGenerateNewAttributeParticleAttributeGroupReferenceForeignGroup() {
        SimpleType simpleTypeA = new SimpleType("{B}simpleTypeA", null);
        Attribute oldAttribute = new Attribute("{B}attributeA", new SymbolTableRef<Type>("{B}simpleTypeA", simpleTypeA));

        AttributeGroup oldAttributeGroup = new AttributeGroup("{B}attributeGroupA");
        oldAttributeGroup.addAttributeParticle(oldAttribute);

        AttributeParticle oldAttributeParticle = new AttributeGroupRef(new SymbolTableRef<AttributeGroup>("{B}attributeGroup", oldAttributeGroup));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(oldAttribute, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertTrue(result instanceof AttributeGroupRef);
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(((AttributeGroupRef) result).getAttributeGroup() instanceof AttributeGroup);
        assertEquals("{A}attributeGroupA", ((AttributeGroupRef) result).getAttributeGroup().getName());
        assertEquals(null, ((AttributeGroupRef) result).getAttributeGroup().getId());
        assertEquals(null, ((AttributeGroupRef) result).getAttributeGroup().getAnnotation());
        assertTrue(outputSchema.getAttributeGroupSymbolTable().hasReference(((AttributeGroupRef) result).getAttributeGroup().getName()));
        assertTrue(outputSchema.getAttributeGroups().contains(((AttributeGroupRef) result).getAttributeGroup()));
        AttributeParticle resultingAttribute = ((AttributeGroupRef) result).getAttributeGroup().getAttributeParticles().getFirst();
        assertEquals("{A}simpleTypeA", ((Attribute) resultingAttribute).getSimpleType().getName());
        assertEquals(false, ((Attribute) resultingAttribute).getSimpleType().isAnonymous());
        assertEquals(oldAttribute.getTypeAttr(), ((Attribute) resultingAttribute).getTypeAttr());
        assertEquals(oldAttribute.getDefault(), ((Attribute) resultingAttribute).getDefault());
        assertEquals(oldAttribute.getFixed(), ((Attribute) resultingAttribute).getFixed());
        assertEquals(null, ((Attribute) resultingAttribute).getId());
        assertEquals(null, ((Attribute) resultingAttribute).getAnnotation());
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) resultingAttribute).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) resultingAttribute).getName()).getReference()));
        assertEquals(AttributeUse.Optional, ((Attribute) resultingAttribute).getUse());
        assertEquals(null, ((Attribute) resultingAttribute).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleDifferenceGenerator for an attribute group reference with ID.
     */
    @Test
    public void testGenerateNewAttributeParticleAttributeGroupReferenceID() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));

        AttributeGroup oldAttributeGroup = new AttributeGroup("{A}attributeGroupA");
        oldAttributeGroup.addAttributeParticle(oldAttribute);

        AttributeParticle oldAttributeParticle = new AttributeGroupRef(new SymbolTableRef<AttributeGroup>("{A}attributeGroup", oldAttributeGroup));
        oldAttributeParticle.setId("id");

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        attributeOldSchemaMap.put(oldAttribute, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, usedIDs, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertTrue(result instanceof AttributeGroupRef);
        assertEquals("id.1", result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(((AttributeGroupRef) result).getAttributeGroup() instanceof AttributeGroup);
        assertEquals("{A}attributeGroupA", ((AttributeGroupRef) result).getAttributeGroup().getName());
        assertEquals(null, ((AttributeGroupRef) result).getAttributeGroup().getId());
        assertEquals(null, ((AttributeGroupRef) result).getAttributeGroup().getAnnotation());
        assertTrue(outputSchema.getAttributeGroupSymbolTable().hasReference(((AttributeGroupRef) result).getAttributeGroup().getName()));
        assertTrue(outputSchema.getAttributeGroups().contains(((AttributeGroupRef) result).getAttributeGroup()));
        AttributeParticle resultingAttribute = ((AttributeGroupRef) result).getAttributeGroup().getAttributeParticles().getFirst();
        assertEquals("{A}simpleTypeA", ((Attribute) resultingAttribute).getSimpleType().getName());
        assertEquals(false, ((Attribute) resultingAttribute).getSimpleType().isAnonymous());
        assertEquals(oldAttribute.getTypeAttr(), ((Attribute) resultingAttribute).getTypeAttr());
        assertEquals(oldAttribute.getDefault(), ((Attribute) resultingAttribute).getDefault());
        assertEquals(oldAttribute.getFixed(), ((Attribute) resultingAttribute).getFixed());
        assertEquals(null, ((Attribute) resultingAttribute).getId());
        assertEquals(null, ((Attribute) resultingAttribute).getAnnotation());
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) resultingAttribute).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) resultingAttribute).getName()).getReference()));
        assertEquals(AttributeUse.Optional, ((Attribute) resultingAttribute).getUse());
        assertEquals(null, ((Attribute) resultingAttribute).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleDifferenceGenerator for an attribute group reference with
     * annotation.
     */
    @Test
    public void testGenerateNewAttributeParticleAttributeGroupReferenceAnnotation() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));

        AttributeGroup oldAttributeGroup = new AttributeGroup("{A}attributeGroupA");
        oldAttributeGroup.addAttributeParticle(oldAttribute);

        AttributeParticle oldAttributeParticle = new AttributeGroupRef(new SymbolTableRef<AttributeGroup>("{A}attributeGroup", oldAttributeGroup));
        Annotation annotation = new Annotation();
        AppInfo appInfo = new AppInfo();
        appInfo.setSource("bla");
        Documentation documentation = new Documentation();
        documentation.setSource("blaa");
        annotation.addAppInfos(appInfo);
        annotation.addDocumentations(documentation);
        oldAttributeParticle.setAnnotation(annotation);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(oldAttribute, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertTrue(result instanceof AttributeGroupRef);
        assertEquals(null, result.getId());
        assertNotSame(annotation, result.getAnnotation());
        assertTrue(result.getAnnotation().getAppInfos().size() == 1);
        assertEquals("bla", result.getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(result.getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaa", result.getAnnotation().getDocumentations().get(0).getSource());
        assertTrue(((AttributeGroupRef) result).getAttributeGroup() instanceof AttributeGroup);
        assertEquals("{A}attributeGroupA", ((AttributeGroupRef) result).getAttributeGroup().getName());
        assertEquals(null, ((AttributeGroupRef) result).getAttributeGroup().getId());
        assertEquals(null, ((AttributeGroupRef) result).getAttributeGroup().getAnnotation());
        assertTrue(outputSchema.getAttributeGroupSymbolTable().hasReference(((AttributeGroupRef) result).getAttributeGroup().getName()));
        assertTrue(outputSchema.getAttributeGroups().contains(((AttributeGroupRef) result).getAttributeGroup()));
        AttributeParticle resultingAttribute = ((AttributeGroupRef) result).getAttributeGroup().getAttributeParticles().getFirst();
        assertEquals("{A}simpleTypeA", ((Attribute) resultingAttribute).getSimpleType().getName());
        assertEquals(false, ((Attribute) resultingAttribute).getSimpleType().isAnonymous());
        assertEquals(oldAttribute.getTypeAttr(), ((Attribute) resultingAttribute).getTypeAttr());
        assertEquals(oldAttribute.getDefault(), ((Attribute) resultingAttribute).getDefault());
        assertEquals(oldAttribute.getFixed(), ((Attribute) resultingAttribute).getFixed());
        assertEquals(null, ((Attribute) resultingAttribute).getId());
        assertEquals(null, ((Attribute) resultingAttribute).getAnnotation());
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) resultingAttribute).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) resultingAttribute).getName()).getReference()));
        assertEquals(AttributeUse.Optional, ((Attribute) resultingAttribute).getUse());
        assertEquals(null, ((Attribute) resultingAttribute).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleDifferenceGenerator for an attribute group reference with
     * attribute group ID.
     */
    @Test
    public void testGenerateNewAttributeParticleAttributeGroupReferenceGroupID() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));

        AttributeGroup oldAttributeGroup = new AttributeGroup("{A}attributeGroupA");
        oldAttributeGroup.addAttributeParticle(oldAttribute);
        oldAttributeGroup.setId("id");

        AttributeParticle oldAttributeParticle = new AttributeGroupRef(new SymbolTableRef<AttributeGroup>("{A}attributeGroup", oldAttributeGroup));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        attributeOldSchemaMap.put(oldAttribute, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, usedIDs, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertTrue(result instanceof AttributeGroupRef);
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(((AttributeGroupRef) result).getAttributeGroup() instanceof AttributeGroup);
        assertEquals("{A}attributeGroupA", ((AttributeGroupRef) result).getAttributeGroup().getName());
        assertEquals("id.1", ((AttributeGroupRef) result).getAttributeGroup().getId());
        assertEquals(null, ((AttributeGroupRef) result).getAttributeGroup().getAnnotation());
        assertTrue(outputSchema.getAttributeGroupSymbolTable().hasReference(((AttributeGroupRef) result).getAttributeGroup().getName()));
        assertTrue(outputSchema.getAttributeGroups().contains(((AttributeGroupRef) result).getAttributeGroup()));
        AttributeParticle resultingAttribute = ((AttributeGroupRef) result).getAttributeGroup().getAttributeParticles().getFirst();
        assertEquals("{A}simpleTypeA", ((Attribute) resultingAttribute).getSimpleType().getName());
        assertEquals(false, ((Attribute) resultingAttribute).getSimpleType().isAnonymous());
        assertEquals(oldAttribute.getTypeAttr(), ((Attribute) resultingAttribute).getTypeAttr());
        assertEquals(oldAttribute.getDefault(), ((Attribute) resultingAttribute).getDefault());
        assertEquals(oldAttribute.getFixed(), ((Attribute) resultingAttribute).getFixed());
        assertEquals(null, ((Attribute) resultingAttribute).getId());
        assertEquals(null, ((Attribute) resultingAttribute).getAnnotation());
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) resultingAttribute).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) resultingAttribute).getName()).getReference()));
        assertEquals(AttributeUse.Optional, ((Attribute) resultingAttribute).getUse());
        assertEquals(null, ((Attribute) resultingAttribute).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleDifferenceGenerator for an attribute group reference with
     * attribute group annotation.
     */
    @Test
    public void testGenerateNewAttributeParticleAttributeGroupReferenceGroupAnnotation() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));

        AttributeGroup oldAttributeGroup = new AttributeGroup("{A}attributeGroupA");
        oldAttributeGroup.addAttributeParticle(oldAttribute);
        Annotation annotation = new Annotation();
        AppInfo appInfo = new AppInfo();
        appInfo.setSource("bla");
        Documentation documentation = new Documentation();
        documentation.setSource("blaa");
        annotation.addAppInfos(appInfo);
        annotation.addDocumentations(documentation);
        oldAttributeGroup.setAnnotation(annotation);

        AttributeParticle oldAttributeParticle = new AttributeGroupRef(new SymbolTableRef<AttributeGroup>("{A}attributeGroup", oldAttributeGroup));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(oldAttribute, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertTrue(result instanceof AttributeGroupRef);
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(((AttributeGroupRef) result).getAttributeGroup() instanceof AttributeGroup);
        assertEquals("{A}attributeGroupA", ((AttributeGroupRef) result).getAttributeGroup().getName());
        assertEquals(null, ((AttributeGroupRef) result).getAttributeGroup().getId());
        assertNotSame(annotation, ((AttributeGroupRef) result).getAttributeGroup().getAnnotation());
        assertTrue(((AttributeGroupRef) result).getAttributeGroup().getAnnotation().getAppInfos().size() == 1);
        assertEquals("bla", ((AttributeGroupRef) result).getAttributeGroup().getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((AttributeGroupRef) result).getAttributeGroup().getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaa", ((AttributeGroupRef) result).getAttributeGroup().getAnnotation().getDocumentations().get(0).getSource());
        assertTrue(outputSchema.getAttributeGroupSymbolTable().hasReference(((AttributeGroupRef) result).getAttributeGroup().getName()));
        assertTrue(outputSchema.getAttributeGroups().contains(((AttributeGroupRef) result).getAttributeGroup()));
        AttributeParticle resultingAttribute = ((AttributeGroupRef) result).getAttributeGroup().getAttributeParticles().getFirst();
        assertEquals("{A}simpleTypeA", ((Attribute) resultingAttribute).getSimpleType().getName());
        assertEquals(false, ((Attribute) resultingAttribute).getSimpleType().isAnonymous());
        assertEquals(oldAttribute.getTypeAttr(), ((Attribute) resultingAttribute).getTypeAttr());
        assertEquals(oldAttribute.getDefault(), ((Attribute) resultingAttribute).getDefault());
        assertEquals(oldAttribute.getFixed(), ((Attribute) resultingAttribute).getFixed());
        assertEquals(null, ((Attribute) resultingAttribute).getId());
        assertEquals(null, ((Attribute) resultingAttribute).getAnnotation());
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) resultingAttribute).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) resultingAttribute).getName()).getReference()));
        assertEquals(AttributeUse.Optional, ((Attribute) resultingAttribute).getUse());
        assertEquals(null, ((Attribute) resultingAttribute).getForm());
    }

    /**
     * Test of setTypeDifferenceGenerator method, of class AttributeParticleDifferenceGenerator.
     */
    @Test
    public void testSetTypeDifferenceGenerator() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeDifferenceGenerator(new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null));

        Attribute result = instance.generateNewTopLevelAttribute(oldAttribute);

        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getForm());
        assertEquals(null, result.getId());
        assertEquals("{A}attributeA", result.getName());
        assertEquals(false, result.getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", result.getSimpleType().getName());
        assertEquals(false, (boolean) result.getTypeAttr());
        assertEquals(AttributeUse.Optional, result.getUse());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of isOptional method, of class AttributeParticleDifferenceGenerator.
     */
    @Test
    public void testIsOptional() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));

        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        Attribute attributeB = new Attribute("{A}attributeB", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));

        SimpleType simpleTypeC = new SimpleType("{A}simpleTypeC", null);
        Attribute attributeC = new Attribute("{A}attributeC", new SymbolTableRef<Type>(simpleTypeC.getName(), simpleTypeC));
        AttributeGroup attributeGroupC = new AttributeGroup("{A}attributeGroupC");
        attributeGroupC.addAttributeParticle(attributeC);
        AttributeGroupRef attributeGroupRefC = new AttributeGroupRef(new SymbolTableRef<AttributeGroup>(attributeGroupC.getName(), attributeGroupC));

        AnyAttribute anyAttributeD = new AnyAttribute();

        LinkedList<AttributeParticle> attributeParticles = new LinkedList<AttributeParticle>();
        attributeParticles.add(attributeA);
        attributeParticles.add(attributeRefB);
        attributeParticles.add(attributeGroupRefC);
        attributeParticles.add(anyAttributeD);

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);

        boolean expResult = true;
        boolean result = instance.isOptional(attributeParticles);

        assertEquals(expResult, result);
    }

    /**
     * Test of isOptional method, of class AttributeParticleDifferenceGenerator
     * for a required attríbute.
     */
    @Test
    public void testIsOptionalRequiredAttribute() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setUse(AttributeUse.Required);

        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        Attribute attributeB = new Attribute("{A}attributeB", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));

        SimpleType simpleTypeC = new SimpleType("{A}simpleTypeC", null);
        Attribute attributeC = new Attribute("{A}attributeC", new SymbolTableRef<Type>(simpleTypeC.getName(), simpleTypeC));
        AttributeGroup attributeGroupC = new AttributeGroup("{A}attributeGroupC");
        attributeGroupC.addAttributeParticle(attributeC);
        AttributeGroupRef attributeGroupRefC = new AttributeGroupRef(new SymbolTableRef<AttributeGroup>(attributeGroupC.getName(), attributeGroupC));

        AnyAttribute anyAttributeD = new AnyAttribute();

        LinkedList<AttributeParticle> attributeParticles = new LinkedList<AttributeParticle>();
        attributeParticles.add(attributeA);
        attributeParticles.add(attributeRefB);
        attributeParticles.add(attributeGroupRefC);
        attributeParticles.add(anyAttributeD);

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);

        boolean expResult = false;
        boolean result = instance.isOptional(attributeParticles);

        assertEquals(expResult, result);
    }

    /**
     * Test of isOptional method, of class AttributeParticleDifferenceGenerator
     * for a required attríbute reference.
     */
    @Test
    public void testIsOptionalRequiredAttributeReference() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));

        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        Attribute attributeB = new Attribute("{A}attributeB", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Required);

        SimpleType simpleTypeC = new SimpleType("{A}simpleTypeC", null);
        Attribute attributeC = new Attribute("{A}attributeC", new SymbolTableRef<Type>(simpleTypeC.getName(), simpleTypeC));
        AttributeGroup attributeGroupC = new AttributeGroup("{A}attributeGroupC");
        attributeGroupC.addAttributeParticle(attributeC);
        AttributeGroupRef attributeGroupRefC = new AttributeGroupRef(new SymbolTableRef<AttributeGroup>(attributeGroupC.getName(), attributeGroupC));

        AnyAttribute anyAttributeD = new AnyAttribute();

        LinkedList<AttributeParticle> attributeParticles = new LinkedList<AttributeParticle>();
        attributeParticles.add(attributeA);
        attributeParticles.add(attributeRefB);
        attributeParticles.add(attributeGroupRefC);
        attributeParticles.add(anyAttributeD);

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);

        boolean expResult = false;
        boolean result = instance.isOptional(attributeParticles);

        assertEquals(expResult, result);
    }

    /**
     * Test of isOptional method, of class AttributeParticleDifferenceGenerator
     * for a required attríbute in an attribute group.
     */
    @Test
    public void testIsOptionalRequiredAttributeInAttributeGroup() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));

        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        Attribute attributeB = new Attribute("{A}attributeB", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));

        SimpleType simpleTypeC = new SimpleType("{A}simpleTypeC", null);
        Attribute attributeC = new Attribute("{A}attributeC", new SymbolTableRef<Type>(simpleTypeC.getName(), simpleTypeC));
        attributeC.setUse(AttributeUse.Required);
        AttributeGroup attributeGroupC = new AttributeGroup("{A}attributeGroupC");
        attributeGroupC.addAttributeParticle(attributeC);
        AttributeGroupRef attributeGroupRefC = new AttributeGroupRef(new SymbolTableRef<AttributeGroup>(attributeGroupC.getName(), attributeGroupC));

        AnyAttribute anyAttributeD = new AnyAttribute();

        LinkedList<AttributeParticle> attributeParticles = new LinkedList<AttributeParticle>();
        attributeParticles.add(attributeA);
        attributeParticles.add(attributeRefB);
        attributeParticles.add(attributeGroupRefC);
        attributeParticles.add(anyAttributeD);

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleDifferenceGenerator instance = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);

        boolean expResult = false;
        boolean result = instance.isOptional(attributeParticles);

        assertEquals(expResult, result);
    }
}