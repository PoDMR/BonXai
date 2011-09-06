package eu.fox7.bonxai.xsd.setOperations.intersection;

import eu.fox7.bonxai.common.Annotation;
import eu.fox7.bonxai.common.AnyAttribute;
import eu.fox7.bonxai.common.ProcessContentsInstruction;
import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.setOperations.intersection.AttributeParticleIntersectionGenerator;
import eu.fox7.bonxai.xsd.setOperations.intersection.TypeIntersectionGenerator;

import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>AttributeParticleIntersectionGenerator</tt> class,
 * checks that every method of this class performs properly.
 * @author Dominik Wolff
 */
public class AttributeParticleIntersectionGeneratorTest extends junit.framework.TestCase {

    public AttributeParticleIntersectionGeneratorTest() {
    }

    /**
     * Test of setTypeIntersectionGenerator method, of class AttributeParticleIntersectionGenerator.
     */
    @Test
    public void testSetTypeIntersectionGenerator() {
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();

        LinkedList<Attribute> attributesA = new LinkedList<Attribute>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributesA.add(attributeA);
        topLevelAttributeListSet.add(attributesA);

        LinkedList<Attribute> attributesB = new LinkedList<Attribute>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributesB.add(attributeB);
        topLevelAttributeListSet.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedHashMap<String, LinkedHashSet<Attribute>> expResult = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        LinkedHashSet<Attribute> attributes = new LinkedHashSet<Attribute>();
        attributes.add(attributeA);
        attributes.add(attributeB);
        expResult.put("{A}attributeA", attributes);
        LinkedHashMap<String, LinkedHashSet<Attribute>> result = instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference("{A}attributeA"));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference()));
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getAnnotation());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getDefault());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getFixed());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getForm());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getId());
        assertEquals(attributeA.getName(), outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getName());
        assertEquals(false, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getSimpleType().isAnonymous());
        assertEquals(simpleTypeA.getName(), outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getSimpleType().getName());
        assertEquals(false, (boolean) outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getTypeAttr());
        assertEquals(AttributeUse.Optional, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributes() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributesB.add(attributeB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of multiple attributes.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributesMultipleAttributes() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        Attribute attributeC = new Attribute("{A}attributeB", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributesA.add(attributeA);
        attributesA.add(attributeC);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        Attribute attributeD = new Attribute("{A}attributeB", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributesB.add(attributeD);
        attributesB.add(attributeB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 2);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{}attributeA", ((Attribute) result.getFirst()).getName());
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
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result.getLast()).getForm());
        assertEquals(null, ((Attribute) result.getLast()).getId());
        assertEquals("{}attributeB", ((Attribute) result.getLast()).getName());
        assertEquals(false, ((Attribute) result.getLast()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getLast()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getLast()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getLast()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes with
     * annotation.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributesAnnotations() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
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
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        Annotation annotationB = new Annotation();
        AppInfo appInfoB = new AppInfo();
        appInfoB.setSource("blaB");
        Documentation documentationB = new Documentation();
        documentationB.setSource("blaaB");
        annotationB.addAppInfos(appInfoB);
        annotationB.addDocumentations(documentationB);
        attributeB.setAnnotation(annotationB);
        attributesB.add(attributeB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals("blaA", ((Attribute) result.getFirst()).getAnnotation().getAppInfos().get(0).getSource());
        assertEquals("blaB", ((Attribute) result.getFirst()).getAnnotation().getAppInfos().get(1).getSource());
        assertTrue(((Attribute) result.getFirst()).getAnnotation().getDocumentations().size() == 2);
        assertEquals("blaaA", ((Attribute) result.getFirst()).getAnnotation().getDocumentations().get(0).getSource());
        assertEquals("blaaB", ((Attribute) result.getFirst()).getAnnotation().getDocumentations().get(1).getSource());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes without
     * type attribute.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributesWithoutTypeAttr() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", new SimpleContentList(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null))));
        simpleTypeA.setIsAnonymous(true);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setTypeAttr(false);
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeA", new SimpleContentList(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null))));
        simpleTypeB.setIsAnonymous(true);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setTypeAttr(false);
        attributesB.add(attributeB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(true, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}intersection-type.simpleTypeA.simpleTypeA", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes with
     * empty type intersection.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributesEmptyTypeIntersection() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributesB.add(attributeB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 0);
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes with
     * different fixed values.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributesDifferentFixed() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setFixed("bla");
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setFixed("bla2");
        attributesB.add(attributeB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 0);
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes with
     * same fixed values.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributesSameFixed() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setFixed("bla");
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setFixed("bla");
        attributesB.add(attributeB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals("bla", ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes with
     * single fixed values.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributesSingleFixed() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setFixed("bla");
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributesB.add(attributeB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals("bla", ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes with
     * different default values.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributesDifferentDefault() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setDefault("bla");
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setDefault("bla2");
        attributesB.add(attributeB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes with
     * same default values.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributesSameDefault() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setDefault("bla");
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setDefault("bla");
        attributesB.add(attributeB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals("bla", ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes with
     * same default values but the resulting attribute is not oprtional.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributesSameDefaultNotOptional() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setUse(AttributeUse.Required);
        attributeA.setDefault("bla");
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setDefault("bla");
        attributesB.add(attributeB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Required, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes with
     * single default values.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributesSingleDefault() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setDefault("bla");
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributesB.add(attributeB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributesOptionalRequired() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setUse(AttributeUse.Required);
        attributesB.add(attributeB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Required, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributesOptionalProhibited() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setUse(AttributeUse.Prohibited);
        attributesB.add(attributeB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(null, ((Attribute) result.getFirst()).getSimpleType());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Prohibited, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributesRequiredRequired() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setUse(AttributeUse.Required);
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setUse(AttributeUse.Required);
        attributesB.add(attributeB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Required, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributesRequiredProhibited() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setUse(AttributeUse.Required);
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setUse(AttributeUse.Prohibited);
        attributesB.add(attributeB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributesProhibitedProhibited() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setUse(AttributeUse.Prohibited);
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setUse(AttributeUse.Prohibited);
        attributesB.add(attributeB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(null, ((Attribute) result.getFirst()).getSimpleType());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Prohibited, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributesQualifiedQualified() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setForm(XSDSchema.Qualification.qualified);
        attributesB.add(attributeB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributesQualifiedUnqualified() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributesB.add(attributeB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 0);
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributesForeignQualifiedQualified() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{B}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{B}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setForm(XSDSchema.Qualification.qualified);
        attributesB.add(attributeB);
        attributeParticleLists.add(attributesB);

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
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, anyAttributeOldSchemaMap, otherSchemata, namespaceAbbreviationMap, "C:/");
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) newAttributeParticle).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) newAttributeParticle).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) newAttributeParticle).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributesForeignQualifiedUnqualified() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setForm(XSDSchema.Qualification.unqualified);
        attributesB.add(attributeB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeRefs() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributesA.add(attributeRefA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of multiple attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeRefsMultipleAttributeRefs() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        Attribute attributeC = new Attribute("{A}attributeB", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefC = new AttributeRef(new SymbolTableRef<Attribute>(attributeC.getName(), attributeC));
        attributesA.add(attributeRefA);
        attributesA.add(attributeRefC);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        Attribute attributeD = new Attribute("{A}attributeB", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefD = new AttributeRef(new SymbolTableRef<Attribute>(attributeD.getName(), attributeD));
        attributesB.add(attributeRefB);
        attributesB.add(attributeRefD);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 2);
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
        assertTrue(result.getLast() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getLast()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getLast()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getLast()).getAnnotation());
        assertEquals(null, ((Attribute) result.getLast()).getDefault());
        assertEquals(null, ((Attribute) result.getLast()).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) result.getLast()).getForm());
        assertEquals(null, ((Attribute) result.getLast()).getId());
        assertEquals("{A}attributeB", ((Attribute) result.getLast()).getName());
        assertEquals(false, ((Attribute) result.getLast()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getLast()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getLast()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getLast()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attribute references
     * with annotations.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeRefsAnnotations() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
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
        attributeA.setAnnotation(annotationA);
        attributesA.add(attributeRefA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        Annotation annotationB = new Annotation();
        AppInfo appInfoB = new AppInfo();
        appInfoB.setSource("blaB");
        Documentation documentationB = new Documentation();
        documentationB.setSource("blaaB");
        annotationB.addAppInfos(appInfoB);
        annotationB.addDocumentations(documentationB);
        attributeRefB.setAnnotation(annotationB);
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertTrue(((Attribute) result.getFirst()).getAnnotation().getAppInfos().size() == 1);
        assertEquals("blaB", ((Attribute) result.getFirst()).getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((Attribute) result.getFirst()).getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaaB", ((Attribute) result.getFirst()).getAnnotation().getDocumentations().get(0).getSource());
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
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attribute references
     * with different fixed values.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeRefsDifferentFixed() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeA.setFixed("bla");
        attributesA.add(attributeRefA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setFixed("bla2");
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 0);
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attribute references
     * with same fixed values.
     */
    @Test
    public void testGenerateAttributeParticleIntersectioAttributeRefsSameFixed() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeA.setFixed("bla");
        attributesA.add(attributeRefA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setFixed("bla");
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attribute references
     * with single fixed values.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeRefsSingleFixed() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributesA.add(attributeRefA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setFixed("bla");
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attribute references
     * with different default values.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeRefsDifferentDefault() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeA.setDefault("bla");
        attributesA.add(attributeRefA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setDefault("bla2");
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attribute references
     * with same default values.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeRefsSameDefault() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeA.setDefault("bla");
        attributesA.add(attributeRefA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setDefault("bla");
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attribute references
     * with same default values but the resulting attribute is not oprtional.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeRefsSameDefaultNotOptional() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Required);
        attributeA.setDefault("bla");
        attributesA.add(attributeRefA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setDefault("bla");
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attribute references
     * with single default values.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeRefsSingleDefault() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributesA.add(attributeRefA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setDefault("bla");
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeRefsOptionalRequired() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributesA.add(attributeRefA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Required);
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeRefsOptionalProhibited() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributesA.add(attributeRefA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Prohibited);
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
        assertEquals(null, ((Attribute) result.getFirst()).getSimpleType());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Prohibited, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeRefsRequiredRequired() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Required);
        attributesA.add(attributeRefA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Required);
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeRefsRequiredProhibited() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Required);
        attributesA.add(attributeRefA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Prohibited);
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeRefsProhibitedProhibited() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Prohibited);
        attributesA.add(attributeRefA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Prohibited);
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
        assertEquals(null, ((Attribute) result.getFirst()).getSimpleType());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Prohibited, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeRefsAttributeRef() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributesA.add(attributeRefA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        LinkedList<Attribute> attributesA2 = new LinkedList<Attribute>();
        attributesA2.add(attributeA);
        topLevelAttributeListSet.add(attributesA2);
        LinkedList<Attribute> attributesB2 = new LinkedList<Attribute>();
        attributesB2.add(attributeB);
        topLevelAttributeListSet.add(attributesB2);
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AttributeRef);
        assertEquals(null, ((AttributeRef) result.getFirst()).getAnnotation());
        assertEquals(null, ((AttributeRef) result.getFirst()).getDefault());
        assertEquals(null, ((AttributeRef) result.getFirst()).getFixed());
        assertEquals(null, ((AttributeRef) result.getFirst()).getId());
        assertEquals(AttributeUse.Optional, ((AttributeRef) result.getFirst()).getUse());
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
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of multiple attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeRefsMultipleAttributeRefsAttributeRef() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        Attribute attributeC = new Attribute("{A}attributeB", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefC = new AttributeRef(new SymbolTableRef<Attribute>(attributeC.getName(), attributeC));
        attributesA.add(attributeRefA);
        attributesA.add(attributeRefC);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        Attribute attributeD = new Attribute("{A}attributeB", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefD = new AttributeRef(new SymbolTableRef<Attribute>(attributeD.getName(), attributeD));
        attributesB.add(attributeRefB);
        attributesB.add(attributeRefD);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        LinkedList<Attribute> attributesA2 = new LinkedList<Attribute>();
        attributesA2.add(attributeA);
        attributesA2.add(attributeC);
        topLevelAttributeListSet.add(attributesA2);
        LinkedList<Attribute> attributesB2 = new LinkedList<Attribute>();
        attributesB2.add(attributeB);
        attributesA2.add(attributeD);
        topLevelAttributeListSet.add(attributesB2);
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 2);
        assertTrue(result.getFirst() instanceof AttributeRef);
        assertEquals(null, ((AttributeRef) result.getFirst()).getAnnotation());
        assertEquals(null, ((AttributeRef) result.getFirst()).getDefault());
        assertEquals(null, ((AttributeRef) result.getFirst()).getFixed());
        assertEquals(null, ((AttributeRef) result.getFirst()).getId());
        assertEquals(AttributeUse.Optional, ((AttributeRef) result.getFirst()).getUse());
        Attribute newAttributeA = ((AttributeRef) result.getFirst()).getAttribute();
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(newAttributeA.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(newAttributeA.getName()).getReference()));
        assertEquals(null, newAttributeA.getAnnotation());
        assertEquals(null, newAttributeA.getDefault());
        assertEquals(null, newAttributeA.getFixed());
        assertEquals(null, newAttributeA.getForm());
        assertEquals(null, newAttributeA.getId());
        assertEquals("{A}attributeA", newAttributeA.getName());
        assertEquals(false, newAttributeA.getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", newAttributeA.getSimpleType().getName());
        assertEquals(false, (boolean) newAttributeA.getTypeAttr());
        assertEquals(AttributeUse.Optional, newAttributeA.getUse());
        Attribute newAttributeB = ((AttributeRef) result.getLast()).getAttribute();
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(newAttributeB.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(newAttributeB.getName()).getReference()));
        assertEquals(null, newAttributeB.getAnnotation());
        assertEquals(null, newAttributeB.getDefault());
        assertEquals(null, newAttributeB.getFixed());
        assertEquals(null, newAttributeB.getForm());
        assertEquals(null, newAttributeB.getId());
        assertEquals("{A}attributeB", newAttributeB.getName());
        assertEquals(false, newAttributeB.getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", newAttributeB.getSimpleType().getName());
        assertEquals(false, (boolean) newAttributeB.getTypeAttr());
        assertEquals(AttributeUse.Optional, newAttributeB.getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attribute references
     * with annotations.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeRefsAnnotationsAttributeRef() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
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
        attributeA.setAnnotation(annotationA);
        attributesA.add(attributeRefA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        Annotation annotationB = new Annotation();
        AppInfo appInfoB = new AppInfo();
        appInfoB.setSource("blaB");
        Documentation documentationB = new Documentation();
        documentationB.setSource("blaaB");
        annotationB.addAppInfos(appInfoB);
        annotationB.addDocumentations(documentationB);
        attributeRefB.setAnnotation(annotationB);
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        LinkedList<Attribute> attributesA2 = new LinkedList<Attribute>();
        attributesA2.add(attributeA);
        topLevelAttributeListSet.add(attributesA2);
        LinkedList<Attribute> attributesB2 = new LinkedList<Attribute>();
        attributesB2.add(attributeB);
        topLevelAttributeListSet.add(attributesB2);
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AttributeRef);
        assertTrue(((AttributeRef) result.getFirst()).getAnnotation().getAppInfos().size() == 1);
        assertEquals("blaB", ((AttributeRef) result.getFirst()).getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((AttributeRef) result.getFirst()).getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaaB", ((AttributeRef) result.getFirst()).getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(null, ((AttributeRef) result.getFirst()).getDefault());
        assertEquals(null, ((AttributeRef) result.getFirst()).getFixed());
        assertEquals(null, ((AttributeRef) result.getFirst()).getId());
        assertEquals(AttributeUse.Optional, ((AttributeRef) result.getFirst()).getUse());
        Attribute newAttribute = ((AttributeRef) result.getFirst()).getAttribute();
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(newAttribute.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(newAttribute.getName()).getReference()));
        assertTrue(newAttribute.getAnnotation().getAppInfos().size() == 1);
        assertEquals("blaA", newAttribute.getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(newAttribute.getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaaA", newAttribute.getAnnotation().getDocumentations().get(0).getSource());
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
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attribute references
     * with different fixed values.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeRefsDifferentFixedAttributeRef() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeA.setFixed("bla");
        attributesA.add(attributeRefA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setFixed("bla2");
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        LinkedList<Attribute> attributesA2 = new LinkedList<Attribute>();
        attributesA2.add(attributeA);
        topLevelAttributeListSet.add(attributesA2);
        LinkedList<Attribute> attributesB2 = new LinkedList<Attribute>();
        attributesB2.add(attributeB);
        topLevelAttributeListSet.add(attributesB2);
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 0);
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attribute references
     * with same fixed values.
     */
    @Test
    public void testGenerateAttributeParticleIntersectioAttributeRefsSameFixedAttributeRef() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeA.setFixed("bla");
        attributesA.add(attributeRefA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setFixed("bla");
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        LinkedList<Attribute> attributesA2 = new LinkedList<Attribute>();
        attributesA2.add(attributeA);
        topLevelAttributeListSet.add(attributesA2);
        LinkedList<Attribute> attributesB2 = new LinkedList<Attribute>();
        attributesB2.add(attributeB);
        topLevelAttributeListSet.add(attributesB2);
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AttributeRef);
        assertEquals(null, ((AttributeRef) result.getFirst()).getAnnotation());
        assertEquals(null, ((AttributeRef) result.getFirst()).getDefault());
        assertEquals("bla", ((AttributeRef) result.getFirst()).getFixed());
        assertEquals(null, ((AttributeRef) result.getFirst()).getId());
        assertEquals(AttributeUse.Optional, ((AttributeRef) result.getFirst()).getUse());
        Attribute newAttribute = ((AttributeRef) result.getFirst()).getAttribute();
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(newAttribute.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(newAttribute.getName()).getReference()));
        assertEquals(null, newAttribute.getAnnotation());
        assertEquals(null, newAttribute.getDefault());
        assertEquals("bla", newAttribute.getFixed());
        assertEquals(null, newAttribute.getForm());
        assertEquals(null, newAttribute.getId());
        assertEquals("{A}attributeA", newAttribute.getName());
        assertEquals(false, newAttribute.getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", newAttribute.getSimpleType().getName());
        assertEquals(false, (boolean) newAttribute.getTypeAttr());
        assertEquals(AttributeUse.Optional, newAttribute.getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attribute references
     * with single fixed values.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeRefsSingleFixedAttributeRef() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributesA.add(attributeRefA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setFixed("bla");
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        LinkedList<Attribute> attributesA2 = new LinkedList<Attribute>();
        attributesA2.add(attributeA);
        topLevelAttributeListSet.add(attributesA2);
        LinkedList<Attribute> attributesB2 = new LinkedList<Attribute>();
        attributesB2.add(attributeB);
        topLevelAttributeListSet.add(attributesB2);
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AttributeRef);
        assertEquals(null, ((AttributeRef) result.getFirst()).getAnnotation());
        assertEquals(null, ((AttributeRef) result.getFirst()).getDefault());
        assertEquals("bla", ((AttributeRef) result.getFirst()).getFixed());
        assertEquals(null, ((AttributeRef) result.getFirst()).getId());
        assertEquals(AttributeUse.Optional, ((AttributeRef) result.getFirst()).getUse());
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
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attribute references
     * with different default values.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeRefsDifferentDefaultAttributeRef() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeA.setDefault("bla");
        attributesA.add(attributeRefA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setDefault("bla2");
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        LinkedList<Attribute> attributesA2 = new LinkedList<Attribute>();
        attributesA2.add(attributeA);
        topLevelAttributeListSet.add(attributesA2);
        LinkedList<Attribute> attributesB2 = new LinkedList<Attribute>();
        attributesB2.add(attributeB);
        topLevelAttributeListSet.add(attributesB2);
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AttributeRef);
        assertEquals(null, ((AttributeRef) result.getFirst()).getAnnotation());
        assertEquals(null, ((AttributeRef) result.getFirst()).getDefault());
        assertEquals(null, ((AttributeRef) result.getFirst()).getFixed());
        assertEquals(null, ((AttributeRef) result.getFirst()).getId());
        assertEquals(AttributeUse.Optional, ((AttributeRef) result.getFirst()).getUse());
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
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attribute references
     * with same default values.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeRefsSameDefaultAttributeRef() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeA.setDefault("bla");
        attributesA.add(attributeRefA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setDefault("bla");
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        LinkedList<Attribute> attributesA2 = new LinkedList<Attribute>();
        attributesA2.add(attributeA);
        topLevelAttributeListSet.add(attributesA2);
        LinkedList<Attribute> attributesB2 = new LinkedList<Attribute>();
        attributesB2.add(attributeB);
        topLevelAttributeListSet.add(attributesB2);
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AttributeRef);
        assertEquals(null, ((AttributeRef) result.getFirst()).getAnnotation());
        assertEquals("bla", ((AttributeRef) result.getFirst()).getDefault());
        assertEquals(null, ((AttributeRef) result.getFirst()).getFixed());
        assertEquals(null, ((AttributeRef) result.getFirst()).getId());
        assertEquals(AttributeUse.Optional, ((AttributeRef) result.getFirst()).getUse());
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
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attribute references
     * with same default values but the resulting attribute is not oprtional.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeRefsSameDefaultNotOptionalAttributeRef() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Required);
        attributeA.setDefault("bla");
        attributesA.add(attributeRefA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setDefault("bla");
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        LinkedList<Attribute> attributesA2 = new LinkedList<Attribute>();
        attributesA2.add(attributeA);
        topLevelAttributeListSet.add(attributesA2);
        LinkedList<Attribute> attributesB2 = new LinkedList<Attribute>();
        attributesB2.add(attributeB);
        topLevelAttributeListSet.add(attributesB2);
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attribute references
     * with single default values.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeRefsSingleDefaultAttributeRef() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributesA.add(attributeRefA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setDefault("bla");
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        LinkedList<Attribute> attributesA2 = new LinkedList<Attribute>();
        attributesA2.add(attributeA);
        topLevelAttributeListSet.add(attributesA2);
        LinkedList<Attribute> attributesB2 = new LinkedList<Attribute>();
        attributesB2.add(attributeB);
        topLevelAttributeListSet.add(attributesB2);
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AttributeRef);
        assertEquals(null, ((AttributeRef) result.getFirst()).getAnnotation());
        assertEquals(null, ((AttributeRef) result.getFirst()).getDefault());
        assertEquals(null, ((AttributeRef) result.getFirst()).getFixed());
        assertEquals(null, ((AttributeRef) result.getFirst()).getId());
        assertEquals(AttributeUse.Optional, ((AttributeRef) result.getFirst()).getUse());
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
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeRefsOptionalRequiredAttributeRef() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributesA.add(attributeRefA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Required);
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        LinkedList<Attribute> attributesA2 = new LinkedList<Attribute>();
        attributesA2.add(attributeA);
        topLevelAttributeListSet.add(attributesA2);
        LinkedList<Attribute> attributesB2 = new LinkedList<Attribute>();
        attributesB2.add(attributeB);
        topLevelAttributeListSet.add(attributesB2);
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeRefsOptionalProhibitedAttributeRef() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributesA.add(attributeRefA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Prohibited);
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        LinkedList<Attribute> attributesA2 = new LinkedList<Attribute>();
        attributesA2.add(attributeA);
        topLevelAttributeListSet.add(attributesA2);
        LinkedList<Attribute> attributesB2 = new LinkedList<Attribute>();
        attributesB2.add(attributeB);
        topLevelAttributeListSet.add(attributesB2);
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(null, ((Attribute) result.getFirst()).getSimpleType());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Prohibited, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeRefsRequiredRequiredAttributeRef() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Required);
        attributesA.add(attributeRefA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Required);
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        LinkedList<Attribute> attributesA2 = new LinkedList<Attribute>();
        attributesA2.add(attributeA);
        topLevelAttributeListSet.add(attributesA2);
        LinkedList<Attribute> attributesB2 = new LinkedList<Attribute>();
        attributesB2.add(attributeB);
        topLevelAttributeListSet.add(attributesB2);
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeRefsRequiredProhibitedAttributeRef() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Required);
        attributesA.add(attributeRefA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Prohibited);
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        LinkedList<Attribute> attributesA2 = new LinkedList<Attribute>();
        attributesA2.add(attributeA);
        topLevelAttributeListSet.add(attributesA2);
        LinkedList<Attribute> attributesB2 = new LinkedList<Attribute>();
        attributesB2.add(attributeB);
        topLevelAttributeListSet.add(attributesB2);
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeRefsProhibitedProhibitedAttributeRef() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setUse(AttributeUse.Prohibited);
        attributesA.add(attributeRefA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Prohibited);
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        LinkedList<Attribute> attributesA2 = new LinkedList<Attribute>();
        attributesA2.add(attributeA);
        topLevelAttributeListSet.add(attributesA2);
        LinkedList<Attribute> attributesB2 = new LinkedList<Attribute>();
        attributesB2.add(attributeB);
        topLevelAttributeListSet.add(attributesB2);
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(null, ((Attribute) result.getFirst()).getSimpleType());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Prohibited, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeAttributeRef() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of multiple attributes
     * and attribute references.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributesMultipleAttributeAttributeRef() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        Attribute attributeC = new Attribute("{A}attributeB", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributeC.setForm(XSDSchema.Qualification.qualified);
        attributesA.add(attributeA);
        attributesA.add(attributeC);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        Attribute attributeD = new Attribute("{A}attributeB", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefD = new AttributeRef(new SymbolTableRef<Attribute>(attributeD.getName(), attributeD));
        attributesB.add(attributeRefB);
        attributesB.add(attributeRefD);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 2);
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
        assertTrue(result.getLast() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getLast()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getLast()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getLast()).getAnnotation());
        assertEquals(null, ((Attribute) result.getLast()).getDefault());
        assertEquals(null, ((Attribute) result.getLast()).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) result.getLast()).getForm());
        assertEquals(null, ((Attribute) result.getLast()).getId());
        assertEquals("{A}attributeB", ((Attribute) result.getLast()).getName());
        assertEquals(false, ((Attribute) result.getLast()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getLast()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getLast()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getLast()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes and 
     * attribute references with annotations.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeAttributeRefAnnotations() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
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
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        Annotation annotationB = new Annotation();
        AppInfo appInfoB = new AppInfo();
        appInfoB.setSource("blaB");
        Documentation documentationB = new Documentation();
        documentationB.setSource("blaaB");
        annotationB.addAppInfos(appInfoB);
        annotationB.addDocumentations(documentationB);
        attributeRefB.setAnnotation(annotationB);
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals("blaA", ((Attribute) result.getFirst()).getAnnotation().getAppInfos().get(0).getSource());
        assertEquals("blaB", ((Attribute) result.getFirst()).getAnnotation().getAppInfos().get(1).getSource());
        assertTrue(((Attribute) result.getFirst()).getAnnotation().getDocumentations().size() == 2);
        assertEquals("blaaA", ((Attribute) result.getFirst()).getAnnotation().getDocumentations().get(0).getSource());
        assertEquals("blaaB", ((Attribute) result.getFirst()).getAnnotation().getDocumentations().get(1).getSource());
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
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes and
     * attribute references without type attribute.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeAttributeRefWithoutTypeAttr() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", new SimpleContentList(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null))));
        simpleTypeA.setIsAnonymous(true);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributeA.setTypeAttr(false);
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeA", new SimpleContentList(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null))));
        simpleTypeB.setIsAnonymous(true);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeB.setTypeAttr(false);
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
        assertEquals(true, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}intersection-type.simpleTypeA.simpleTypeA", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes and
     * attribute references with empty type intersection.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeAttributeRefEmptyTypeIntersection() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 0);
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes and
     * attribute references with different fixed values.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeAttributeRefDifferentFixed() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributeA.setFixed("bla");
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setFixed("bla2");
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 0);
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes and
     * attribute references with same fixed values.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeAttributeRefSameFixed() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributeA.setFixed("bla");
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setFixed("bla");
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes and
     * attribute references with single fixed values.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeAttributeRefSingleFixed() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setFixed("bla");
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes and
     * attribute references with different default values.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeAttributeRefDifferentDefault() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributeA.setDefault("bla");
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setDefault("bla2");
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes and
     * attribute references with same default values.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeAttributeRefSameDefault() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributeA.setDefault("bla");
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setDefault("bla");
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes and 
     * attribute references with same default values but the resulting attribute
     * is not oprtional.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeAttributeRefSameDefaultNotOptional() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributeA.setUse(AttributeUse.Required);
        attributeA.setDefault("bla");
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setDefault("bla");
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes and
     * attribute references with single default values.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeAttributeRefSingleDefault() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setDefault("bla");
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeAttributeRefOptionalRequired() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Required);
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeAttributeRefOptionalProhibited() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Prohibited);
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
        assertEquals(null, ((Attribute) result.getFirst()).getSimpleType());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Prohibited, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeAttributeRefRequiredRequired() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributeA.setUse(AttributeUse.Required);
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Required);
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeAttributeRefRequiredProhibited() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributeA.setUse(AttributeUse.Required);
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Prohibited);
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeAttributeRefProhibitedProhibited() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributeA.setUse(AttributeUse.Prohibited);
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeRefB.setUse(AttributeUse.Prohibited);
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
        assertEquals(null, ((Attribute) result.getFirst()).getSimpleType());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Prohibited, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeAttributeRefQualifiedQualified() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeB.setForm(XSDSchema.Qualification.qualified);
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeAttributeRefQualifiedUnqualified() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeB.setForm(XSDSchema.Qualification.qualified);
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 0);
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeAttributeRefForeignQualifiedQualified() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{B}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{B}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        AttributeRef attributeRefB = new AttributeRef(new SymbolTableRef<Attribute>(attributeB.getName(), attributeB));
        attributeB.setForm(XSDSchema.Qualification.qualified);
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

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
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, anyAttributeOldSchemaMap, otherSchemata, namespaceAbbreviationMap, "C:/");
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) newAttributeParticle).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) newAttributeParticle).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) newAttributeParticle).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeAttributeRefForeignQualifiedUnqualified() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeA.setForm(XSDSchema.Qualification.qualified);
        attributesA.add(attributeRefA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setForm(XSDSchema.Qualification.unqualified);
        attributesB.add(attributeB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for any attributes and attribute.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAnyAttributeAttribute() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        LinkedList<AttributeParticle> attributeParticlesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeParticlesA.add(attributeA);
        LinkedList<AttributeParticle> attributeParticlesB = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Skip, "##any");
        attributeParticlesB.add(anyAttributeB);
        attributeParticleLists.add(attributeParticlesA);
        attributeParticleLists.add(attributeParticlesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, anyAttributeOldSchemaMap, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        LinkedList<Attribute> attributesA2 = new LinkedList<Attribute>();
        attributesA2.add(attributeA);
        topLevelAttributeListSet.add(attributesA2);
        LinkedList<Attribute> attributesB2 = new LinkedList<Attribute>();
        topLevelAttributeListSet.add(attributesB2);
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for any attributes and attribute.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAnyAttributeAttributeNull() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        LinkedList<AttributeParticle> attributeParticlesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeParticlesA.add(attributeA);
        LinkedList<AttributeParticle> attributeParticlesB = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Skip, "B");
        attributeParticlesB.add(anyAttributeB);
        attributeParticleLists.add(attributeParticlesA);
        attributeParticleLists.add(attributeParticlesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, anyAttributeOldSchemaMap, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        LinkedList<Attribute> attributesA2 = new LinkedList<Attribute>();
        attributesA2.add(attributeA);
        topLevelAttributeListSet.add(attributesA2);
        LinkedList<Attribute> attributesB2 = new LinkedList<Attribute>();
        topLevelAttributeListSet.add(attributesB2);
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertEquals(new LinkedList<AttributeParticle>(), result);
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for any attributes and attribute
     * reference.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAnyAttributeAttributeReference() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        LinkedList<AttributeParticle> attributeParticlesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeParticlesA.add(attributeRefA);
        LinkedList<AttributeParticle> attributeParticlesB = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Skip, "##any");
        attributeParticlesB.add(anyAttributeB);
        attributeParticleLists.add(attributeParticlesA);
        attributeParticleLists.add(attributeParticlesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, anyAttributeOldSchemaMap, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        LinkedList<Attribute> attributesA2 = new LinkedList<Attribute>();
        attributesA2.add(attributeA);
        topLevelAttributeListSet.add(attributesA2);
        LinkedList<Attribute> attributesB2 = new LinkedList<Attribute>();
        topLevelAttributeListSet.add(attributesB2);
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

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
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for any  attribute and attribute
     * reference.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAnyAttributeAttributeReferenceNull() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        LinkedList<AttributeParticle> attributeParticlesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeParticlesA.add(attributeRefA);
        LinkedList<AttributeParticle> attributeParticlesB = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Skip, "B");
        attributeParticlesB.add(anyAttributeB);
        attributeParticleLists.add(attributeParticlesA);
        attributeParticleLists.add(attributeParticlesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, anyAttributeOldSchemaMap, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        LinkedList<Attribute> attributesA2 = new LinkedList<Attribute>();
        attributesA2.add(attributeA);
        topLevelAttributeListSet.add(attributesA2);
        LinkedList<Attribute> attributesB2 = new LinkedList<Attribute>();
        topLevelAttributeListSet.add(attributesB2);
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertEquals(new LinkedList<AttributeParticle>(), result);
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of multiple attributes
     * with a prohibited attribute in one list.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributesMultipleAttributesEmptyProhibited() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        Attribute attributeC = new Attribute("{A}attributeB", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeC.setUse(AttributeUse.Prohibited);
        attributesA.add(attributeA);
        attributesA.add(attributeC);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributesB.add(attributeB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 2);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{}attributeA", ((Attribute) result.getFirst()).getName());
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
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result.getLast()).getForm());
        assertEquals(null, ((Attribute) result.getLast()).getId());
        assertEquals("{}attributeB", ((Attribute) result.getLast()).getName());
        assertEquals(null, ((Attribute) result.getLast()).getSimpleType());
        assertEquals(false, (boolean) ((Attribute) result.getLast()).getTypeAttr());
        assertEquals(AttributeUse.Prohibited, ((Attribute) result.getLast()).getUse());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of multiple attributes
     * with a required attribute in one list.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributesMultipleAttributesEmptyRequired() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        Attribute attributeC = new Attribute("{A}attributeB", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeC.setUse(AttributeUse.Required);
        attributesA.add(attributeA);
        attributesA.add(attributeC);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributesB.add(attributeB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of multiple attributes
     * with a required attribute in one list.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributesMultipleAttributesEmptyResultRequired() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        Attribute attributeC = new Attribute("{A}attributeB", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeC.setUse(AttributeUse.Required);
        attributesA.add(attributeA);
        attributesA.add(attributeC);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleType simpleTypeD = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        Attribute attributeD = new Attribute("{A}attributeB", new SymbolTableRef<Type>(simpleTypeD.getName(), simpleTypeD));
        attributesB.add(attributeD);
        attributesB.add(attributeB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertEquals(null, result);
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for any attributes.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAnyAttribute() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        LinkedList<AttributeParticle> attributeParticlesA = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Lax, "##local");
        attributeParticlesA.add(anyAttributeA);
        LinkedList<AttributeParticle> attributeParticlesB = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Lax, "##local");
        attributeParticlesB.add(anyAttributeB);
        attributeParticleLists.add(attributeParticlesA);
        attributeParticleLists.add(attributeParticlesB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchema);
        anyAttributeOldSchemaMap.put(anyAttributeB, oldSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, anyAttributeOldSchemaMap, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##local", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for any attributes with
     * annotations.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAnyAttributeAnnotation() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        LinkedList<AttributeParticle> attributeParticlesA = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Lax, "##local");
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        anyAttributeA.setAnnotation(annotationA);
        attributeParticlesA.add(anyAttributeA);
        LinkedList<AttributeParticle> attributeParticlesB = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Lax, "##local");
        Annotation annotationB = new Annotation();
        AppInfo appInfoB = new AppInfo();
        appInfoB.setSource("blaB");
        Documentation documentationB = new Documentation();
        documentationB.setSource("blaaB");
        annotationB.addAppInfos(appInfoB);
        annotationB.addDocumentations(documentationB);
        anyAttributeB.setAnnotation(annotationB);
        attributeParticlesB.add(anyAttributeB);
        attributeParticleLists.add(attributeParticlesA);
        attributeParticleLists.add(attributeParticlesB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchema);
        anyAttributeOldSchemaMap.put(anyAttributeB, oldSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, anyAttributeOldSchemaMap, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertTrue(((AnyAttribute) result.getFirst()).getAnnotation().getAppInfos().size() == 2);
        assertEquals("blaA", ((AnyAttribute) result.getFirst()).getAnnotation().getAppInfos().get(0).getSource());
        assertEquals("blaB", ((AnyAttribute) result.getFirst()).getAnnotation().getAppInfos().get(1).getSource());
        assertTrue(((AnyAttribute) result.getFirst()).getAnnotation().getDocumentations().size() == 2);
        assertEquals("blaaA", ((AnyAttribute) result.getFirst()).getAnnotation().getDocumentations().get(0).getSource());
        assertEquals("blaaB", ((AnyAttribute) result.getFirst()).getAnnotation().getDocumentations().get(1).getSource());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##local", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for any attributes with
     * namespace="##any".
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAnyAttributeSingleAny() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        LinkedList<AttributeParticle> attributeParticlesA = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Lax, "##any");
        attributeParticlesA.add(anyAttributeA);
        LinkedList<AttributeParticle> attributeParticlesB = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Lax, "##any");
        attributeParticlesB.add(anyAttributeB);
        attributeParticleLists.add(attributeParticlesA);
        attributeParticleLists.add(attributeParticlesB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchema);
        anyAttributeOldSchemaMap.put(anyAttributeB, oldSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, anyAttributeOldSchemaMap, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##any", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for any attributes with
     * namespace="##other".
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAnyAttributeSingleOther() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        LinkedList<AttributeParticle> attributeParticlesA = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Lax, "##other");
        attributeParticlesA.add(anyAttributeA);
        LinkedList<AttributeParticle> attributeParticlesB = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Lax, "##other");
        attributeParticlesB.add(anyAttributeB);
        attributeParticleLists.add(attributeParticlesA);
        attributeParticleLists.add(attributeParticlesB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchema);
        anyAttributeOldSchemaMap.put(anyAttributeB, oldSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, anyAttributeOldSchemaMap, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##other", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for any attributes with
     * namespace="##local".
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAnyAttributeSingleLocal() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        LinkedList<AttributeParticle> attributeParticlesA = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Lax, "##local");
        attributeParticlesA.add(anyAttributeA);
        LinkedList<AttributeParticle> attributeParticlesB = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Lax, "##local");
        attributeParticlesB.add(anyAttributeB);
        attributeParticleLists.add(attributeParticlesA);
        attributeParticleLists.add(attributeParticlesB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchema);
        anyAttributeOldSchemaMap.put(anyAttributeB, oldSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, anyAttributeOldSchemaMap, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##local", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for any attributes with
     * namespace="##targetNamespace".
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAnyAttributeSingleTargetNamespace() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        LinkedList<AttributeParticle> attributeParticlesA = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Lax, "##targetNamespace");
        attributeParticlesA.add(anyAttributeA);
        LinkedList<AttributeParticle> attributeParticlesB = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Lax, "##targetNamespace");
        attributeParticlesB.add(anyAttributeB);
        attributeParticleLists.add(attributeParticlesA);
        attributeParticleLists.add(attributeParticlesB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchema);
        anyAttributeOldSchemaMap.put(anyAttributeB, oldSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, anyAttributeOldSchemaMap, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##targetNamespace", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for any attributes with
     * namespace="A".
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAnyAttributeSingleUriA() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        LinkedList<AttributeParticle> attributeParticlesA = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Lax, "A");
        attributeParticlesA.add(anyAttributeA);
        LinkedList<AttributeParticle> attributeParticlesB = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Lax, "A");
        attributeParticlesB.add(anyAttributeB);
        attributeParticleLists.add(attributeParticlesA);
        attributeParticleLists.add(attributeParticlesB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchema);
        anyAttributeOldSchemaMap.put(anyAttributeB, oldSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, anyAttributeOldSchemaMap, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##targetNamespace", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for any attributes with
     * namespace="B".
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAnyAttributeSingleUriB() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        LinkedList<AttributeParticle> attributeParticlesA = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Lax, "B");
        attributeParticlesA.add(anyAttributeA);
        LinkedList<AttributeParticle> attributeParticlesB = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Lax, "B");
        attributeParticlesB.add(anyAttributeB);
        attributeParticleLists.add(attributeParticlesA);
        attributeParticleLists.add(attributeParticlesB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchema);
        anyAttributeOldSchemaMap.put(anyAttributeB, oldSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, anyAttributeOldSchemaMap, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("B", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for any attributes with
     * namespace="##other".
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAnyAttributeForeignOther() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        LinkedList<AttributeParticle> attributeParticlesA = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Lax, "##other");
        attributeParticlesA.add(anyAttributeA);
        LinkedList<AttributeParticle> attributeParticlesB = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Lax, "##other");
        attributeParticlesB.add(anyAttributeB);
        attributeParticleLists.add(attributeParticlesA);
        attributeParticleLists.add(attributeParticlesB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("B");
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchema);
        anyAttributeOldSchemaMap.put(anyAttributeB, oldSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, anyAttributeOldSchemaMap, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##any", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for any attributes with
     * namespace="##other" and "##local".
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAnyAttributeOtherLocal() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        LinkedList<AttributeParticle> attributeParticlesA = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Lax, "##other");
        attributeParticlesA.add(anyAttributeA);
        LinkedList<AttributeParticle> attributeParticlesB = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Lax, "##local");
        attributeParticlesB.add(anyAttributeB);
        attributeParticleLists.add(attributeParticlesA);
        attributeParticleLists.add(attributeParticlesB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchema);
        anyAttributeOldSchemaMap.put(anyAttributeB, oldSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, anyAttributeOldSchemaMap, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 0);
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for any attributes with
     * namespace="##other" and "##targetNamespace" .
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAnyAttributeOtherTargetNamespace() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        LinkedList<AttributeParticle> attributeParticlesA = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Lax, "##other");
        attributeParticlesA.add(anyAttributeA);
        LinkedList<AttributeParticle> attributeParticlesB = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Lax, "##targetNamespace");
        attributeParticlesB.add(anyAttributeB);
        attributeParticleLists.add(attributeParticlesA);
        attributeParticleLists.add(attributeParticlesB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchema);
        anyAttributeOldSchemaMap.put(anyAttributeB, oldSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, anyAttributeOldSchemaMap, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 0);
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for any attributes with
     * namespace="##other" and "##targetNamespace" .
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAnyAttributeForeignOtherTargetNamespace() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        LinkedList<AttributeParticle> attributeParticlesA = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Lax, "##other");
        attributeParticlesA.add(anyAttributeA);
        LinkedList<AttributeParticle> attributeParticlesB = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Lax, "##targetNamespace");
        attributeParticlesB.add(anyAttributeB);
        attributeParticleLists.add(attributeParticlesA);
        attributeParticleLists.add(attributeParticlesB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchemaA = new XSDSchema("A");
        XSDSchema oldSchemaB = new XSDSchema("B");
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchemaA);
        anyAttributeOldSchemaMap.put(anyAttributeB, oldSchemaB);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, anyAttributeOldSchemaMap, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("B", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for any attributes with
     * namespace="A" and "A B" .
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAnyAttributeUriAUriAB() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        LinkedList<AttributeParticle> attributeParticlesA = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Lax, "A");
        attributeParticlesA.add(anyAttributeA);
        LinkedList<AttributeParticle> attributeParticlesB = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Lax, "A B");
        attributeParticlesB.add(anyAttributeB);
        attributeParticleLists.add(attributeParticlesA);
        attributeParticleLists.add(attributeParticlesB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchema);
        anyAttributeOldSchemaMap.put(anyAttributeB, oldSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, anyAttributeOldSchemaMap, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##targetNamespace", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for any attributes with
     * namespace="##any" and "A B" .
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAnyAttributeAnyUriAB() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        LinkedList<AttributeParticle> attributeParticlesA = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Lax, "##any");
        attributeParticlesA.add(anyAttributeA);
        LinkedList<AttributeParticle> attributeParticlesB = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Lax, "A B");
        attributeParticlesB.add(anyAttributeB);
        attributeParticleLists.add(attributeParticlesA);
        attributeParticleLists.add(attributeParticlesB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchema);
        anyAttributeOldSchemaMap.put(anyAttributeB, oldSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, anyAttributeOldSchemaMap, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##targetNamespace B", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for any attributes with
     * namespace="##targetNamespace" and "A B" .
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAnyAttributeTargetNamespaceUriAB() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        LinkedList<AttributeParticle> attributeParticlesA = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Lax, "##targetNamespace");
        attributeParticlesA.add(anyAttributeA);
        LinkedList<AttributeParticle> attributeParticlesB = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Lax, "A B");
        attributeParticlesB.add(anyAttributeB);
        attributeParticleLists.add(attributeParticlesA);
        attributeParticleLists.add(attributeParticlesB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchema);
        anyAttributeOldSchemaMap.put(anyAttributeB, oldSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, anyAttributeOldSchemaMap, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##targetNamespace", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for any attributes while one list
     * contains no any attribute.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAnyAttributeListContainsNull() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        LinkedList<AttributeParticle> attributeParticlesA = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Lax, "##local");
        attributeParticlesA.add(anyAttributeA);
        LinkedList<AttributeParticle> attributeParticlesB = new LinkedList<AttributeParticle>();
        attributeParticleLists.add(attributeParticlesA);
        attributeParticleLists.add(attributeParticlesB);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, anyAttributeOldSchemaMap, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 0);
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for any attributes for lax 
     * validated any pattern.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAnyAttributeLax() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        LinkedList<AttributeParticle> attributeParticlesA = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Lax, "##any");
        attributeParticlesA.add(anyAttributeA);
        LinkedList<AttributeParticle> attributeParticlesB = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Lax, "##any");
        attributeParticlesB.add(anyAttributeB);
        attributeParticleLists.add(attributeParticlesA);
        attributeParticleLists.add(attributeParticlesB);

        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchema);
        anyAttributeOldSchemaMap.put(anyAttributeB, oldSchema);
        oldSchema.getAttributeSymbolTable().updateOrCreateReference("{A}attributeA", attributeA);
        oldSchema.addAttribute(oldSchema.getAttributeSymbolTable().getReference("{A}attributeA"));
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, anyAttributeOldSchemaMap, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 2);
        assertTrue(result.getFirst() instanceof Attribute);
        assertEquals(attributeA.getName(), ((Attribute) result.getFirst()).getName());
        assertTrue(result.getLast() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getLast()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getLast()).getId());
        assertEquals("##any", ((AnyAttribute) result.getLast()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getLast()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for any attributes for lax
     * validated any pattern with Uris.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAnyAttributeLaxUris() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        LinkedList<AttributeParticle> attributeParticlesA = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Lax, "B");
        attributeParticlesA.add(anyAttributeA);
        LinkedList<AttributeParticle> attributeParticlesB = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Lax, "B");
        attributeParticlesB.add(anyAttributeB);
        attributeParticleLists.add(attributeParticlesA);
        attributeParticleLists.add(attributeParticlesB);

        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));

        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        Attribute attributeB = new Attribute("{B}attributeB", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));

        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.setSchemaLocation("C:/A.xsd");
        XSDSchema oldSchemaA = new XSDSchema("A");
        XSDSchema oldSchemaB = new XSDSchema("B");
        ImportedSchema importedSchema = new ImportedSchema("B", "C:/b.xsd");
        importedSchema.setSchema(oldSchemaB);
        oldSchemaA.addForeignSchema(importedSchema);
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchemaA);
        anyAttributeOldSchemaMap.put(anyAttributeB, oldSchemaA);
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
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, anyAttributeOldSchemaMap, otherSchemata, namespaceAbbreviationMap, "C:/");
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 2);
        assertTrue(result.getFirst() instanceof AttributeGroupRef);
        assertEquals(attributeB.getName(), ((Attribute) ((AttributeGroupRef) result.getFirst()).getAttributeGroup().getAttributeParticles().getFirst()).getName());
        assertTrue(result.getLast() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getLast()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getLast()).getId());
        assertEquals("B", ((AnyAttribute) result.getLast()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getLast()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for any attributes for lax
     * validated any pattern with target namespace.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAnyAttributeLaxTargetNamespace() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        LinkedList<AttributeParticle> attributeParticlesA = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Lax, "##targetNamespace");
        attributeParticlesA.add(anyAttributeA);
        LinkedList<AttributeParticle> attributeParticlesB = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Lax, "##targetNamespace");
        attributeParticlesB.add(anyAttributeB);
        attributeParticleLists.add(attributeParticlesA);
        attributeParticleLists.add(attributeParticlesB);

        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));

        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        Attribute attributeB = new Attribute("{B}attributeB", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));

        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.setSchemaLocation("C:/A.xsd");
        XSDSchema oldSchemaA = new XSDSchema("A");
        XSDSchema oldSchemaB = new XSDSchema("B");
        ImportedSchema importedSchema = new ImportedSchema("B", "C:/b.xsd");
        importedSchema.setSchema(oldSchemaB);
        oldSchemaA.addForeignSchema(importedSchema);
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchemaA);
        anyAttributeOldSchemaMap.put(anyAttributeB, oldSchemaA);
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
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, anyAttributeOldSchemaMap, otherSchemata, namespaceAbbreviationMap, "C:/");
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 2);
        assertTrue(result.getFirst() instanceof Attribute);
        assertEquals(attributeA.getName(), ((Attribute) result.getFirst()).getName());
        assertTrue(result.getLast() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getLast()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getLast()).getId());
        assertEquals("##targetNamespace", ((AnyAttribute) result.getLast()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getLast()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for any attributes for lax
     * validated any pattern with ##local.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAnyAttributeLaxLocal() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        LinkedList<AttributeParticle> attributeParticlesA = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Lax, "##local");
        attributeParticlesA.add(anyAttributeA);
        LinkedList<AttributeParticle> attributeParticlesB = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Lax, "##local");
        attributeParticlesB.add(anyAttributeB);
        attributeParticleLists.add(attributeParticlesA);
        attributeParticleLists.add(attributeParticlesB);

        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));

        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        Attribute attributeB = new Attribute("{}attributeB", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));

        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.setSchemaLocation("C:/A.xsd");
        XSDSchema oldSchemaA = new XSDSchema("A");
        XSDSchema oldSchemaB = new XSDSchema("");
        ImportedSchema importedSchema = new ImportedSchema(null, "C:/b.xsd");
        importedSchema.setSchema(oldSchemaB);
        oldSchemaA.addForeignSchema(importedSchema);
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchemaA);
        anyAttributeOldSchemaMap.put(anyAttributeB, oldSchemaA);
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
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, anyAttributeOldSchemaMap, otherSchemata, namespaceAbbreviationMap, "C:/");
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 2);
        assertTrue(result.getFirst() instanceof Attribute);
        assertEquals(attributeB.getName(), ((Attribute) result.getFirst()).getName());
        assertTrue(result.getLast() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getLast()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getLast()).getId());
        assertEquals("##local", ((AnyAttribute) result.getLast()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getLast()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for any attributes for lax
     * validated any pattern with ##other.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAnyAttributeLaxOther() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        LinkedList<AttributeParticle> attributeParticlesA = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Lax, "##other");
        attributeParticlesA.add(anyAttributeA);
        LinkedList<AttributeParticle> attributeParticlesB = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Lax, "##other");
        attributeParticlesB.add(anyAttributeB);
        attributeParticleLists.add(attributeParticlesA);
        attributeParticleLists.add(attributeParticlesB);

        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));

        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        Attribute attributeB = new Attribute("{B}attributeB", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));

        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.setSchemaLocation("C:/A.xsd");
        XSDSchema oldSchemaA = new XSDSchema("A");
        XSDSchema oldSchemaB = new XSDSchema("B");
        ImportedSchema importedSchema = new ImportedSchema("B", "C:/b.xsd");
        importedSchema.setSchema(oldSchemaB);
        oldSchemaA.addForeignSchema(importedSchema);
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchemaA);
        anyAttributeOldSchemaMap.put(anyAttributeB, oldSchemaA);
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
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, anyAttributeOldSchemaMap, otherSchemata, namespaceAbbreviationMap, "C:/");
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 2);
        assertTrue(result.getFirst() instanceof AttributeGroupRef);
        assertEquals(attributeB.getName(), ((Attribute) ((AttributeGroupRef) result.getFirst()).getAttributeGroup().getAttributeParticles().getFirst()).getName());
        assertTrue(result.getLast() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getLast()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getLast()).getId());
        assertEquals("##other", ((AnyAttribute) result.getLast()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getLast()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for any attributes for strict
     * validated any pattern.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAnyAttributeStrict() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        LinkedList<AttributeParticle> attributeParticlesA = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Strict, "##any");
        attributeParticlesA.add(anyAttributeA);
        LinkedList<AttributeParticle> attributeParticlesB = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Strict, "##any");
        attributeParticlesB.add(anyAttributeB);
        attributeParticleLists.add(attributeParticlesA);
        attributeParticleLists.add(attributeParticlesB);

        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchema);
        anyAttributeOldSchemaMap.put(anyAttributeB, oldSchema);
        oldSchema.getAttributeSymbolTable().updateOrCreateReference("{A}attributeA", attributeA);
        oldSchema.addAttribute(oldSchema.getAttributeSymbolTable().getReference("{A}attributeA"));
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, anyAttributeOldSchemaMap, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertEquals(attributeA.getName(), ((Attribute) result.getFirst()).getName());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for any attributes for strict
     * validated any pattern with Uris.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAnyAttributeStrictUris() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        LinkedList<AttributeParticle> attributeParticlesA = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Strict, "B");
        attributeParticlesA.add(anyAttributeA);
        LinkedList<AttributeParticle> attributeParticlesB = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Strict, "B");
        attributeParticlesB.add(anyAttributeB);
        attributeParticleLists.add(attributeParticlesA);
        attributeParticleLists.add(attributeParticlesB);

        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));

        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        Attribute attributeB = new Attribute("{B}attributeB", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));

        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.setSchemaLocation("C:/A.xsd");
        XSDSchema oldSchemaA = new XSDSchema("A");
        XSDSchema oldSchemaB = new XSDSchema("B");
        ImportedSchema importedSchema = new ImportedSchema("B", "C:/b.xsd");
        importedSchema.setSchema(oldSchemaB);
        oldSchemaA.addForeignSchema(importedSchema);
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchemaA);
        anyAttributeOldSchemaMap.put(anyAttributeB, oldSchemaA);
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
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, anyAttributeOldSchemaMap, otherSchemata, namespaceAbbreviationMap, "C:/");
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AttributeGroupRef);
        assertEquals(attributeB.getName(), ((Attribute) ((AttributeGroupRef) result.getFirst()).getAttributeGroup().getAttributeParticles().getFirst()).getName());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for any attributes for strict
     * validated any pattern with target namespace.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAnyAttributeStrictTargetNamespace() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        LinkedList<AttributeParticle> attributeParticlesA = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Strict, "##targetNamespace");
        attributeParticlesA.add(anyAttributeA);
        LinkedList<AttributeParticle> attributeParticlesB = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Strict, "##targetNamespace");
        attributeParticlesB.add(anyAttributeB);
        attributeParticleLists.add(attributeParticlesA);
        attributeParticleLists.add(attributeParticlesB);

        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));

        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        Attribute attributeB = new Attribute("{B}attributeB", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));

        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.setSchemaLocation("C:/A.xsd");
        XSDSchema oldSchemaA = new XSDSchema("A");
        XSDSchema oldSchemaB = new XSDSchema("B");
        ImportedSchema importedSchema = new ImportedSchema("B", "C:/b.xsd");
        importedSchema.setSchema(oldSchemaB);
        oldSchemaA.addForeignSchema(importedSchema);
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchemaA);
        anyAttributeOldSchemaMap.put(anyAttributeB, oldSchemaA);
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
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, anyAttributeOldSchemaMap, otherSchemata, namespaceAbbreviationMap, "C:/");
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertEquals(attributeA.getName(), ((Attribute) result.getFirst()).getName());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for any attributes for strict
     * validated any pattern with ##local.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAnyAttributeStrictLocal() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        LinkedList<AttributeParticle> attributeParticlesA = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Strict, "##local");
        attributeParticlesA.add(anyAttributeA);
        LinkedList<AttributeParticle> attributeParticlesB = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Strict, "##local");
        attributeParticlesB.add(anyAttributeB);
        attributeParticleLists.add(attributeParticlesA);
        attributeParticleLists.add(attributeParticlesB);

        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));

        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        Attribute attributeB = new Attribute("{}attributeB", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));

        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.setSchemaLocation("C:/A.xsd");
        XSDSchema oldSchemaA = new XSDSchema("A");
        XSDSchema oldSchemaB = new XSDSchema("");
        ImportedSchema importedSchema = new ImportedSchema(null, "C:/b.xsd");
        importedSchema.setSchema(oldSchemaB);
        oldSchemaA.addForeignSchema(importedSchema);
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchemaA);
        anyAttributeOldSchemaMap.put(anyAttributeB, oldSchemaA);
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
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, anyAttributeOldSchemaMap, otherSchemata, namespaceAbbreviationMap, "C:/");
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertEquals(attributeB.getName(), ((Attribute) result.getFirst()).getName());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for any attributes for strict
     * validated any pattern with ##other.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAnyAttributeStrictOther() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();
        LinkedList<AttributeParticle> attributeParticlesA = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeA = new AnyAttribute(ProcessContentsInstruction.Strict, "##other");
        attributeParticlesA.add(anyAttributeA);
        LinkedList<AttributeParticle> attributeParticlesB = new LinkedList<AttributeParticle>();
        AnyAttribute anyAttributeB = new AnyAttribute(ProcessContentsInstruction.Strict, "##other");
        attributeParticlesB.add(anyAttributeB);
        attributeParticleLists.add(attributeParticlesA);
        attributeParticleLists.add(attributeParticlesB);

        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));

        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        Attribute attributeB = new Attribute("{B}attributeB", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));

        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.setSchemaLocation("C:/A.xsd");
        XSDSchema oldSchemaA = new XSDSchema("A");
        XSDSchema oldSchemaB = new XSDSchema("B");
        ImportedSchema importedSchema = new ImportedSchema("B", "C:/b.xsd");
        importedSchema.setSchema(oldSchemaB);
        oldSchemaA.addForeignSchema(importedSchema);
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeA, oldSchemaA);
        anyAttributeOldSchemaMap.put(anyAttributeB, oldSchemaA);
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
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, anyAttributeOldSchemaMap, otherSchemata, namespaceAbbreviationMap, "C:/");
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AttributeGroupRef);
        assertEquals(attributeB.getName(), ((Attribute) ((AttributeGroupRef) result.getFirst()).getAttributeGroup().getAttributeParticles().getFirst()).getName());
    }

    /**
     * Test of generateAttributeParticleIntersection method, of class
     * AttributeParticleIntersectionGenerator for a list of attributeGroups.
     */
    @Test
    public void testGenerateAttributeParticleIntersectionAttributeGroupResolving() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

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

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        attributesA.add(attributeGroupRefA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        attributesB.add(attributeGroupRefA);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<AnyAttribute, XSDSchema> anyAttributeOldSchemaMap = new LinkedHashMap<AnyAttribute, XSDSchema>();
        anyAttributeOldSchemaMap.put(anyAttributeC, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, namespaceOutputSchemaMap, anyAttributeOldSchemaMap, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();
        LinkedList<Attribute> attributesA2 = new LinkedList<Attribute>();
        topLevelAttributeListSet.add(attributesA2);
        LinkedList<Attribute> attributesB2 = new LinkedList<Attribute>();
        attributesB2.add(attributeB);
        topLevelAttributeListSet.add(attributesB2);
        instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleIntersection(attributeParticleLists);

        assertTrue(result.size() == 2);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
        assertTrue(result.get(1) instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.get(1)).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.get(1)).getName()).getReference()));
        assertEquals(null, ((Attribute) result.get(1)).getAnnotation());
        assertEquals(null, ((Attribute) result.get(1)).getDefault());
        assertEquals(null, ((Attribute) result.get(1)).getFixed());
        assertEquals(XSDSchema.Qualification.qualified, ((Attribute) result.get(1)).getForm());
        assertEquals(null, ((Attribute) result.get(1)).getId());
        assertEquals("{A}attributeB", ((Attribute) result.get(1)).getName());
        assertEquals(false, ((Attribute) result.get(1)).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.get(1)).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.get(1)).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.get(1)).getUse());
    }

    /**
     * Test of generateNewTopLevelAttributes method, of class
     * AttributeParticleIntersectionGenerator.
     */
    @Test
    public void testGenerateNewTopLevelAttributes() {
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();

        LinkedList<Attribute> attributesA = new LinkedList<Attribute>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributesA.add(attributeA);
        topLevelAttributeListSet.add(attributesA);

        LinkedList<Attribute> attributesB = new LinkedList<Attribute>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributesB.add(attributeB);
        topLevelAttributeListSet.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedHashMap<String, LinkedHashSet<Attribute>> expResult = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        LinkedHashSet<Attribute> attributes = new LinkedHashSet<Attribute>();
        attributes.add(attributeA);
        attributes.add(attributeB);
        expResult.put("{A}attributeA", attributes);
        LinkedHashMap<String, LinkedHashSet<Attribute>> result = instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference("{A}attributeA"));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference()));
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getAnnotation());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getDefault());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getFixed());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getForm());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getId());
        assertEquals(attributeA.getName(), outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getName());
        assertEquals(false, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getSimpleType().isAnonymous());
        assertEquals(simpleTypeA.getName(), outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getSimpleType().getName());
        assertEquals(false, (boolean) outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getTypeAttr());
        assertEquals(AttributeUse.Optional, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getUse());
    }

    /**
     * Test of generateNewTopLevelAttributes method, of class
     * AttributeParticleIntersectionGenerator for multiple top-level attributes.
     */
    @Test
    public void testGenerateNewTopLevelAttributesMultipleAttributes() {
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();

        LinkedList<Attribute> attributesA = new LinkedList<Attribute>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        Attribute attributeC = new Attribute("{A}attributeB", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributesA.add(attributeA);
        attributesA.add(attributeC);
        topLevelAttributeListSet.add(attributesA);

        LinkedList<Attribute> attributesB = new LinkedList<Attribute>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        Attribute attributeD = new Attribute("{A}attributeB", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributesB.add(attributeB);
        attributesB.add(attributeD);
        topLevelAttributeListSet.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedHashMap<String, LinkedHashSet<Attribute>> expResult = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        LinkedHashSet<Attribute> attributesA2 = new LinkedHashSet<Attribute>();
        attributesA2.add(attributeA);
        attributesA2.add(attributeB);
        expResult.put("{A}attributeA", attributesA2);
        LinkedHashSet<Attribute> attributesB2 = new LinkedHashSet<Attribute>();
        attributesB2.add(attributeC);
        attributesB2.add(attributeD);
        expResult.put("{A}attributeB", attributesB2);
        LinkedHashMap<String, LinkedHashSet<Attribute>> result = instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference("{A}attributeA"));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference()));
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getAnnotation());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getDefault());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getFixed());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getForm());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getId());
        assertEquals(attributeA.getName(), outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getName());
        assertEquals(false, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getSimpleType().isAnonymous());
        assertEquals(simpleTypeA.getName(), outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getSimpleType().getName());
        assertEquals(false, (boolean) outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getTypeAttr());
        assertEquals(AttributeUse.Optional, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getUse());
    }

    /**
     * Test of generateNewTopLevelAttributes method, of class
     * AttributeParticleIntersectionGenerator with annotations.
     */
    @Test
    public void testGenerateNewTopLevelAttributesAnnotations() {
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();

        LinkedList<Attribute> attributesA = new LinkedList<Attribute>();
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
        attributesA.add(attributeA);
        topLevelAttributeListSet.add(attributesA);

        LinkedList<Attribute> attributesB = new LinkedList<Attribute>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        Annotation annotationB = new Annotation();
        AppInfo appInfoB = new AppInfo();
        appInfoB.setSource("blaB");
        Documentation documentationB = new Documentation();
        documentationB.setSource("blaaB");
        annotationB.addAppInfos(appInfoB);
        annotationB.addDocumentations(documentationB);
        attributeB.setAnnotation(annotationB);
        attributesB.add(attributeB);
        topLevelAttributeListSet.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedHashMap<String, LinkedHashSet<Attribute>> expResult = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        LinkedHashSet<Attribute> attributes = new LinkedHashSet<Attribute>();
        attributes.add(attributeA);
        attributes.add(attributeB);
        expResult.put("{A}attributeA", attributes);
        LinkedHashMap<String, LinkedHashSet<Attribute>> result = instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference("{A}attributeA"));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference()));
        assertEquals("blaA", outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getAnnotation().getAppInfos().get(0).getSource());
        assertEquals("blaB", outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getAnnotation().getAppInfos().get(1).getSource());
        assertTrue(outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getAnnotation().getDocumentations().size() == 2);
        assertEquals("blaaA", outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getAnnotation().getDocumentations().get(0).getSource());
        assertEquals("blaaB", outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getAnnotation().getDocumentations().get(1).getSource());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getDefault());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getFixed());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getForm());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getId());
        assertEquals(attributeA.getName(), outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getName());
        assertEquals(false, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getSimpleType().isAnonymous());
        assertEquals(simpleTypeA.getName(), outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getSimpleType().getName());
        assertEquals(false, (boolean) outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getTypeAttr());
        assertEquals(AttributeUse.Optional, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getUse());
    }

    /**
     * Test of generateNewTopLevelAttributes method, of class
     * AttributeParticleIntersectionGenerator without type attribute.
     */
    @Test
    public void testGenerateNewTopLevelAttributesWithoutTypeAttr() {
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();

        LinkedList<Attribute> attributesA = new LinkedList<Attribute>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", new SimpleContentList(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null))));
        simpleTypeA.setIsAnonymous(true);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setTypeAttr(false);
        attributesA.add(attributeA);
        topLevelAttributeListSet.add(attributesA);

        LinkedList<Attribute> attributesB = new LinkedList<Attribute>();
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeA", new SimpleContentList(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null))));
        simpleTypeB.setIsAnonymous(true);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setTypeAttr(false);
        attributesB.add(attributeB);
        topLevelAttributeListSet.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedHashMap<String, LinkedHashSet<Attribute>> expResult = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        LinkedHashSet<Attribute> attributes = new LinkedHashSet<Attribute>();
        attributes.add(attributeA);
        attributes.add(attributeB);
        expResult.put("{A}attributeA", attributes);
        LinkedHashMap<String, LinkedHashSet<Attribute>> result = instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference("{A}attributeA"));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference()));
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getAnnotation());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getDefault());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getFixed());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getForm());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getId());
        assertEquals(attributeA.getName(), outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getName());
        assertEquals(true, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getSimpleType().isAnonymous());
        assertEquals("{A}intersection-type.simpleTypeA.simpleTypeA", outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getSimpleType().getName());
        assertEquals(true, (boolean) outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getTypeAttr());
        assertEquals(AttributeUse.Optional, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getUse());
    }

    /**
     * Test of generateNewTopLevelAttributes method, of class
     * AttributeParticleIntersectionGenerator for attributes with different
     * fixed values.
     */
    @Test
    public void testGenerateNewTopLevelAttributesDifferentFixed() {
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();

        LinkedList<Attribute> attributesA = new LinkedList<Attribute>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setFixed("bla");
        attributesA.add(attributeA);
        topLevelAttributeListSet.add(attributesA);

        LinkedList<Attribute> attributesB = new LinkedList<Attribute>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setFixed("bla2");
        attributesB.add(attributeB);
        topLevelAttributeListSet.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedHashMap<String, LinkedHashSet<Attribute>> expResult = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        LinkedHashSet<Attribute> attributes = new LinkedHashSet<Attribute>();
        attributes.add(attributeA);
        attributes.add(attributeB);
        expResult.put("{A}attributeA", attributes);
        LinkedHashMap<String, LinkedHashSet<Attribute>> result = instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        assertEquals(expResult, result);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference("{A}attributeA"));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference()));
    }

    /**
     * Test of generateNewTopLevelAttributes method, of class
     * AttributeParticleIntersectionGenerator for attributes with same
     * fixed values.
     */
    @Test
    public void testGenerateNewTopLevelAttributesSameFixed() {
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();

        LinkedList<Attribute> attributesA = new LinkedList<Attribute>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setFixed("bla");
        attributesA.add(attributeA);
        topLevelAttributeListSet.add(attributesA);

        LinkedList<Attribute> attributesB = new LinkedList<Attribute>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setFixed("bla");
        attributesB.add(attributeB);
        topLevelAttributeListSet.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedHashMap<String, LinkedHashSet<Attribute>> expResult = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        LinkedHashSet<Attribute> attributes = new LinkedHashSet<Attribute>();
        attributes.add(attributeA);
        attributes.add(attributeB);
        expResult.put("{A}attributeA", attributes);
        LinkedHashMap<String, LinkedHashSet<Attribute>> result = instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference("{A}attributeA"));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference()));
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getAnnotation());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getDefault());
        assertEquals("bla", outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getFixed());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getForm());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getId());
        assertEquals(attributeA.getName(), outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getName());
        assertEquals(false, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getSimpleType().isAnonymous());
        assertEquals(simpleTypeA.getName(), outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getSimpleType().getName());
        assertEquals(false, (boolean) outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getTypeAttr());
        assertEquals(AttributeUse.Optional, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getUse());
    }

    /**
     * Test of generateNewTopLevelAttributes method, of class
     * AttributeParticleIntersectionGenerator for attributes with a single
     * fixed values.
     */
    @Test
    public void testGenerateNewTopLevelAttributesSingleFixed() {
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();

        LinkedList<Attribute> attributesA = new LinkedList<Attribute>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setFixed("bla");
        attributesA.add(attributeA);
        topLevelAttributeListSet.add(attributesA);

        LinkedList<Attribute> attributesB = new LinkedList<Attribute>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributesB.add(attributeB);
        topLevelAttributeListSet.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedHashMap<String, LinkedHashSet<Attribute>> expResult = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        LinkedHashSet<Attribute> attributes = new LinkedHashSet<Attribute>();
        attributes.add(attributeA);
        attributes.add(attributeB);
        expResult.put("{A}attributeA", attributes);
        LinkedHashMap<String, LinkedHashSet<Attribute>> result = instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference("{A}attributeA"));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference()));
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getAnnotation());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getDefault());
        assertEquals("bla", outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getFixed());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getForm());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getId());
        assertEquals(attributeA.getName(), outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getName());
        assertEquals(false, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getSimpleType().isAnonymous());
        assertEquals(simpleTypeA.getName(), outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getSimpleType().getName());
        assertEquals(false, (boolean) outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getTypeAttr());
        assertEquals(AttributeUse.Optional, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getUse());
    }

    /**
     * Test of generateNewTopLevelAttributes method, of class
     * AttributeParticleIntersectionGenerator for attributes with different
     * default values.
     */
    @Test
    public void testGenerateNewTopLevelAttributesDifferentDefault() {
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();

        LinkedList<Attribute> attributesA = new LinkedList<Attribute>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setDefault("bla");
        attributesA.add(attributeA);
        topLevelAttributeListSet.add(attributesA);

        LinkedList<Attribute> attributesB = new LinkedList<Attribute>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setDefault("bla2");
        attributesB.add(attributeB);
        topLevelAttributeListSet.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedHashMap<String, LinkedHashSet<Attribute>> expResult = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        LinkedHashSet<Attribute> attributes = new LinkedHashSet<Attribute>();
        attributes.add(attributeA);
        attributes.add(attributeB);
        expResult.put("{A}attributeA", attributes);
        LinkedHashMap<String, LinkedHashSet<Attribute>> result = instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference("{A}attributeA"));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference()));
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getAnnotation());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getDefault());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getFixed());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getForm());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getId());
        assertEquals(attributeA.getName(), outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getName());
        assertEquals(false, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getSimpleType().isAnonymous());
        assertEquals(simpleTypeA.getName(), outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getSimpleType().getName());
        assertEquals(false, (boolean) outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getTypeAttr());
        assertEquals(AttributeUse.Optional, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getUse());
    }

    /**
     * Test of generateNewTopLevelAttributes method, of class
     * AttributeParticleIntersectionGenerator for attributes with same
     * default values.
     */
    @Test
    public void testGenerateNewTopLevelAttributesSameDefault() {
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();

        LinkedList<Attribute> attributesA = new LinkedList<Attribute>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setDefault("bla");
        attributesA.add(attributeA);
        topLevelAttributeListSet.add(attributesA);

        LinkedList<Attribute> attributesB = new LinkedList<Attribute>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setDefault("bla");
        attributesB.add(attributeB);
        topLevelAttributeListSet.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedHashMap<String, LinkedHashSet<Attribute>> expResult = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        LinkedHashSet<Attribute> attributes = new LinkedHashSet<Attribute>();
        attributes.add(attributeA);
        attributes.add(attributeB);
        expResult.put("{A}attributeA", attributes);
        LinkedHashMap<String, LinkedHashSet<Attribute>> result = instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference("{A}attributeA"));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference()));
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getAnnotation());
        assertEquals("bla", outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getDefault());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getFixed());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getForm());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getId());
        assertEquals(attributeA.getName(), outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getName());
        assertEquals(false, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getSimpleType().isAnonymous());
        assertEquals(simpleTypeA.getName(), outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getSimpleType().getName());
        assertEquals(false, (boolean) outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getTypeAttr());
        assertEquals(AttributeUse.Optional, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getUse());
    }

    /**
     * Test of generateNewTopLevelAttributes method, of class
     * AttributeParticleIntersectionGenerator for attributes with a single
     * default values.
     */
    @Test
    public void testGenerateNewTopLevelAttributesSingleDefault() {
        LinkedHashSet<LinkedList<Attribute>> topLevelAttributeListSet = new LinkedHashSet<LinkedList<Attribute>>();

        LinkedList<Attribute> attributesA = new LinkedList<Attribute>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setDefault("bla");
        attributesA.add(attributeA);
        topLevelAttributeListSet.add(attributesA);

        LinkedList<Attribute> attributesB = new LinkedList<Attribute>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributesB.add(attributeB);
        topLevelAttributeListSet.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        TypeIntersectionGenerator typeIntersectionGenerator = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        instance.setTypeIntersectionGenerator(typeIntersectionGenerator);

        LinkedHashMap<String, LinkedHashSet<Attribute>> expResult = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        LinkedHashSet<Attribute> attributes = new LinkedHashSet<Attribute>();
        attributes.add(attributeA);
        attributes.add(attributeB);
        expResult.put("{A}attributeA", attributes);
        LinkedHashMap<String, LinkedHashSet<Attribute>> result = instance.generateNewTopLevelAttributes(topLevelAttributeListSet);

        assertEquals(expResult, result);
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference("{A}attributeA"));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference()));
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getAnnotation());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getDefault());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getFixed());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getForm());
        assertEquals(null, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getId());
        assertEquals(attributeA.getName(), outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getName());
        assertEquals(false, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getSimpleType().isAnonymous());
        assertEquals(simpleTypeA.getName(), outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getSimpleType().getName());
        assertEquals(false, (boolean) outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getTypeAttr());
        assertEquals(AttributeUse.Optional, outputSchema.getAttributeSymbolTable().getReference("{A}attributeA").getReference().getUse());
    }

    /**
     * Test of isOptional method, of class
     * AttributeParticleIntersectionGenerator.
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
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);

        boolean expResult = true;
        boolean result = instance.isOptional(attributeParticles);

        assertEquals(expResult, result);
    }

    /**
     * Test of isOptional method, of class
     * AttributeParticleIntersectionGenerator for a required attríbute.
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
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);

        boolean expResult = false;
        boolean result = instance.isOptional(attributeParticles);

        assertEquals(expResult, result);
    }

    /**
     * Test of isOptional method, of class
     * AttributeParticleIntersectionGenerator for a required attríbute reference.
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
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);

        boolean expResult = false;
        boolean result = instance.isOptional(attributeParticles);

        assertEquals(expResult, result);
    }

    /**
     * Test of isOptional method, of class
     * AttributeParticleIntersectionGenerator for a required attríbute in an
     * attribute group.
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
        AttributeParticleIntersectionGenerator instance = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);

        boolean expResult = false;
        boolean result = instance.isOptional(attributeParticles);

        assertEquals(expResult, result);
    }
}