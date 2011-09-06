package eu.fox7.bonxai.xsd.setOperations.union;

import eu.fox7.bonxai.common.Annotation;
import eu.fox7.bonxai.common.AnyAttribute;
import eu.fox7.bonxai.common.ProcessContentsInstruction;
import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.setOperations.union.AttributeParticleUnionGenerator;
import eu.fox7.bonxai.xsd.setOperations.union.TypeUnionGenerator;

import java.util.*;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>AttributeParticleUnionGenerator</tt> class, checks that
 * every method of this class performs properly.
 * @author Dominik Wolff
 */
public class AttributeParticleUnionGeneratorTest extends junit.framework.TestCase {

    public AttributeParticleUnionGeneratorTest() {
    }

    /**
     * Test of setTypeUnionGenerator method, of class AttributeParticleUnionGenerator.
     */
    @Test
    public void testSetTypeUnionGenerator() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        AttributeParticle oldAttributeParticle = oldAttribute;

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        typeOldSchemaMap.put(simpleTypeA, oldSchema);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertNotNull(((Attribute) result).getSimpleType());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributes() {
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

        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of multiple attributes.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributesMultipleAttributes() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
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
        assertEquals("{A}union-type.string.string.1", ((Attribute) result.getLast()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getLast()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getLast()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes with
     * annotation.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributesAnnotations() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals(null, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes without
     * type attribute.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributesWithoutTypeAttr() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", new SimpleContentList(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null))));
        simpleTypeA.setIsAnonymous(true);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setTypeAttr(false);
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(true, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{A}simpleTypeA", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes with
     * different fixed values.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributesDifferentFixed() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals(true, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertTrue(((SimpleType) ((Attribute) result.getFirst()).getSimpleType()).getInheritance() instanceof SimpleContentRestriction);
        assertTrue(((SimpleContentRestriction) ((SimpleType) ((Attribute) result.getFirst()).getSimpleType()).getInheritance()).getEnumeration().contains("bla"));
        assertTrue(((SimpleContentRestriction) ((SimpleType) ((Attribute) result.getFirst()).getSimpleType()).getInheritance()).getEnumeration().contains("bla2"));
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes with
     * same fixed values.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributesSameFixed() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes with
     * single fixed values.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributesSingleFixed() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes with
     * different default values.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributesDifferentDefault() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes with
     * same default values.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributesSameDefault() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes with
     * same default values but the resulting attribute is not oprtional.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributesSameDefaultNotOptional() {
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
        attributeB.setUse(AttributeUse.Required);
        attributesB.add(attributeB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Required, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes with
     * single default values.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributesSingleDefault() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributesOptionalRequired() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributesOptionalProhibited() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributesRequiredRequired() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Required, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributesRequiredProhibited() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributesProhibitedProhibited() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Prohibited, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributesQualifiedQualified() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributesQualifiedUnqualified() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

        assertTrue(result.size() == 2);
        assertTrue(result.getFirst() instanceof Attribute);
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
        assertEquals(null, ((Attribute) result.getLast()).getAnnotation());
        assertEquals(null, ((Attribute) result.getLast()).getDefault());
        assertEquals(null, ((Attribute) result.getLast()).getFixed());
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result.getLast()).getForm());
        assertEquals(null, ((Attribute) result.getLast()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getLast()).getName());
        assertEquals(false, ((Attribute) result.getLast()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getLast()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getLast()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getLast()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributesForeignQualifiedQualified() {
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
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, null, anyAttributeOldSchemaMap, null, usedIDs, otherSchemata, namespaceAbbreviationMap, "C:/");
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) newAttributeParticle).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) newAttributeParticle).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) newAttributeParticle).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributesForeignQualifiedUnqualified() {
        LinkedList<LinkedList<AttributeParticle>> attributeParticleLists = new LinkedList<LinkedList<AttributeParticle>>();

        LinkedList<AttributeParticle> attributesA = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        attributeA.setForm(XSDSchema.Qualification.unqualified);
        attributesA.add(attributeA);
        attributeParticleLists.add(attributesA);

        LinkedList<AttributeParticle> attributesB = new LinkedList<AttributeParticle>();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeB = new Attribute("{}attributeA", new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        attributeB.setForm(XSDSchema.Qualification.qualified);
        attributesB.add(attributeB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeRefsAttributeRef() {
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
        LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        namespaceConflictingAttributeMap.put("A", new LinkedHashSet<Attribute>());
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, namespaceConflictingAttributeMap, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));
        LinkedList<AttributeParticle> attributes = new LinkedList<AttributeParticle>();
        attributes.add(attributeA);
        attributes.add(attributeB);
        instance.generateNewTopLevelAttribute(attributes);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", newAttribute.getSimpleType().getName());
        assertEquals(true, (boolean) newAttribute.getTypeAttr());
        assertEquals(null, newAttribute.getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of multiple attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeRefsMultipleAttributeRefsAttributeRef() {
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
        LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        namespaceConflictingAttributeMap.put("A", new LinkedHashSet<Attribute>());
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, namespaceConflictingAttributeMap, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));
        LinkedList<AttributeParticle> attributesA2 = new LinkedList<AttributeParticle>();
        attributesA2.add(attributeA);
        attributesA2.add(attributeB);
        instance.generateNewTopLevelAttribute(attributesA2);
        LinkedList<AttributeParticle> attributesB2 = new LinkedList<AttributeParticle>();
        attributesB2.add(attributeC);
        attributesB2.add(attributeD);
        instance.generateNewTopLevelAttribute(attributesB2);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", newAttributeA.getSimpleType().getName());
        assertEquals(true, (boolean) newAttributeA.getTypeAttr());
        assertEquals(null, newAttributeA.getUse());
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
        assertEquals("{A}union-type.string.string.1", newAttributeB.getSimpleType().getName());
        assertEquals(true, (boolean) newAttributeB.getTypeAttr());
        assertEquals(null, newAttributeB.getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attribute references
     * with annotations.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeRefsAnnotationsAttributeRef() {
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
        LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        namespaceConflictingAttributeMap.put("A", new LinkedHashSet<Attribute>());
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, namespaceConflictingAttributeMap, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));
        LinkedList<AttributeParticle> attributes = new LinkedList<AttributeParticle>();
        attributes.add(attributeA);
        attributes.add(attributeB);
        instance.generateNewTopLevelAttribute(attributes);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", newAttribute.getSimpleType().getName());
        assertEquals(true, (boolean) newAttribute.getTypeAttr());
        assertEquals(null, newAttribute.getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attribute references
     * with different fixed values.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeRefsDifferentFixedAttributeRef() {
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
        LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        namespaceConflictingAttributeMap.put("A", new LinkedHashSet<Attribute>());
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, namespaceConflictingAttributeMap, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));
        LinkedList<AttributeParticle> attributes = new LinkedList<AttributeParticle>();
        attributes.add(attributeA);
        attributes.add(attributeB);
        instance.generateNewTopLevelAttribute(attributes);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals(true, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertTrue(((SimpleType) ((Attribute) result.getFirst()).getSimpleType()).getInheritance() instanceof SimpleContentRestriction);
        assertTrue(((SimpleContentRestriction) ((SimpleType) ((Attribute) result.getFirst()).getSimpleType()).getInheritance()).getEnumeration().contains("bla"));
        assertTrue(((SimpleContentRestriction) ((SimpleType) ((Attribute) result.getFirst()).getSimpleType()).getInheritance()).getEnumeration().contains("bla2"));
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attribute references
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
        LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        namespaceConflictingAttributeMap.put("A", new LinkedHashSet<Attribute>());
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, namespaceConflictingAttributeMap, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));
        LinkedList<AttributeParticle> attributes = new LinkedList<AttributeParticle>();
        attributes.add(attributeA);
        attributes.add(attributeB);
        instance.generateNewTopLevelAttribute(attributes);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", newAttribute.getSimpleType().getName());
        assertEquals(true, (boolean) newAttribute.getTypeAttr());
        assertEquals(null, newAttribute.getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attribute references
     * with single fixed values.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeRefsSingleFixedAttributeRef() {
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
        LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        namespaceConflictingAttributeMap.put("A", new LinkedHashSet<Attribute>());
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, namespaceConflictingAttributeMap, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));
        LinkedList<AttributeParticle> attributes = new LinkedList<AttributeParticle>();
        attributes.add(attributeA);
        attributes.add(attributeB);
        instance.generateNewTopLevelAttribute(attributes);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", newAttribute.getSimpleType().getName());
        assertEquals(true, (boolean) newAttribute.getTypeAttr());
        assertEquals(null, newAttribute.getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attribute references
     * with different default values.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeRefsDifferentDefaultAttributeRef() {
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
        LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        namespaceConflictingAttributeMap.put("A", new LinkedHashSet<Attribute>());
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, namespaceConflictingAttributeMap, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));
        LinkedList<AttributeParticle> attributes = new LinkedList<AttributeParticle>();
        attributes.add(attributeA);
        attributes.add(attributeB);
        instance.generateNewTopLevelAttribute(attributes);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("bla", newAttribute.getDefault());
        assertEquals(null, newAttribute.getFixed());
        assertEquals(null, newAttribute.getForm());
        assertEquals(null, newAttribute.getId());
        assertEquals("{A}attributeA", newAttribute.getName());
        assertEquals(false, newAttribute.getSimpleType().isAnonymous());
        assertEquals("{A}union-type.string.string", newAttribute.getSimpleType().getName());
        assertEquals(true, (boolean) newAttribute.getTypeAttr());
        assertEquals(null, newAttribute.getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attribute references
     * with same default values.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeRefsSameDefaultAttributeRef() {
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
        LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        namespaceConflictingAttributeMap.put("A", new LinkedHashSet<Attribute>());
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, namespaceConflictingAttributeMap, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));
        LinkedList<AttributeParticle> attributes = new LinkedList<AttributeParticle>();
        attributes.add(attributeA);
        attributes.add(attributeB);
        instance.generateNewTopLevelAttribute(attributes);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("bla", newAttribute.getDefault());
        assertEquals(null, newAttribute.getFixed());
        assertEquals(null, newAttribute.getForm());
        assertEquals(null, newAttribute.getId());
        assertEquals("{A}attributeA", newAttribute.getName());
        assertEquals(false, newAttribute.getSimpleType().isAnonymous());
        assertEquals("{A}union-type.string.string", newAttribute.getSimpleType().getName());
        assertEquals(true, (boolean) newAttribute.getTypeAttr());
        assertEquals(null, newAttribute.getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attribute references
     * with same default values but the resulting attribute is not oprtional.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeRefsSameDefaultNotOptionalAttributeRef() {
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
        attributeRefB.setUse(AttributeUse.Required);
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, XSDSchema> namespaceOutputSchemaMap = new LinkedHashMap<String, XSDSchema>();
        namespaceOutputSchemaMap.put("A", outputSchema);
        LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        namespaceConflictingAttributeMap.put("A", new LinkedHashSet<Attribute>());
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, namespaceConflictingAttributeMap, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));
        LinkedList<AttributeParticle> attributes = new LinkedList<AttributeParticle>();
        attributes.add(attributeA);
        attributes.add(attributeB);
        instance.generateNewTopLevelAttribute(attributes);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("bla", newAttribute.getDefault());
        assertEquals(null, newAttribute.getFixed());
        assertEquals(null, newAttribute.getForm());
        assertEquals(null, newAttribute.getId());
        assertEquals("{A}attributeA", newAttribute.getName());
        assertEquals(false, newAttribute.getSimpleType().isAnonymous());
        assertEquals("{A}union-type.string.string", newAttribute.getSimpleType().getName());
        assertEquals(true, (boolean) newAttribute.getTypeAttr());
        assertEquals(null, newAttribute.getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attribute references
     * with single default values.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeRefsSingleDefaultAttributeRef() {
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
        LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        namespaceConflictingAttributeMap.put("A", new LinkedHashSet<Attribute>());
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, namespaceConflictingAttributeMap, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));
        LinkedList<AttributeParticle> attributes = new LinkedList<AttributeParticle>();
        attributes.add(attributeA);
        attributes.add(attributeB);
        instance.generateNewTopLevelAttribute(attributes);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", newAttribute.getSimpleType().getName());
        assertEquals(true, (boolean) newAttribute.getTypeAttr());
        assertEquals(null, newAttribute.getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeRefsOptionalRequiredAttributeRef() {
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
        LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        namespaceConflictingAttributeMap.put("A", new LinkedHashSet<Attribute>());
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, namespaceConflictingAttributeMap, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));
        LinkedList<AttributeParticle> attributes = new LinkedList<AttributeParticle>();
        attributes.add(attributeA);
        attributes.add(attributeB);
        instance.generateNewTopLevelAttribute(attributes);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", newAttribute.getSimpleType().getName());
        assertEquals(true, (boolean) newAttribute.getTypeAttr());
        assertEquals(null, newAttribute.getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeRefsOptionalProhibitedAttributeRef() {
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
        LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        namespaceConflictingAttributeMap.put("A", new LinkedHashSet<Attribute>());
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, namespaceConflictingAttributeMap, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));
        LinkedList<AttributeParticle> attributes = new LinkedList<AttributeParticle>();
        attributes.add(attributeA);
        attributes.add(attributeB);
        instance.generateNewTopLevelAttribute(attributes);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", newAttribute.getSimpleType().getName());
        assertEquals(true, (boolean) newAttribute.getTypeAttr());
        assertEquals(null, newAttribute.getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeRefsRequiredRequiredAttributeRef() {
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
        LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        namespaceConflictingAttributeMap.put("A", new LinkedHashSet<Attribute>());
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, namespaceConflictingAttributeMap, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));
        LinkedList<AttributeParticle> attributes = new LinkedList<AttributeParticle>();
        attributes.add(attributeA);
        attributes.add(attributeB);
        instance.generateNewTopLevelAttribute(attributes);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", newAttribute.getSimpleType().getName());
        assertEquals(true, (boolean) newAttribute.getTypeAttr());
        assertEquals(null, newAttribute.getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeRefsRequiredProhibitedAttributeRef() {
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
        LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        namespaceConflictingAttributeMap.put("A", new LinkedHashSet<Attribute>());
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, namespaceConflictingAttributeMap, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));
        LinkedList<AttributeParticle> attributes = new LinkedList<AttributeParticle>();
        attributes.add(attributeA);
        attributes.add(attributeB);
        instance.generateNewTopLevelAttribute(attributes);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", newAttribute.getSimpleType().getName());
        assertEquals(true, (boolean) newAttribute.getTypeAttr());
        assertEquals(null, newAttribute.getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attribute
     * references.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeRefsProhibitedProhibitedAttributeRef() {
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
        LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        namespaceConflictingAttributeMap.put("A", new LinkedHashSet<Attribute>());
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, null, null, namespaceConflictingAttributeMap, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));
        LinkedList<AttributeParticle> attributes = new LinkedList<AttributeParticle>();
        attributes.add(attributeA);
        attributes.add(attributeB);
        instance.generateNewTopLevelAttribute(attributes);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", newAttribute.getSimpleType().getName());
        assertEquals(true, (boolean) newAttribute.getTypeAttr());
        assertEquals(null, newAttribute.getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeAttributeRef() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of multiple attributes
     * and attribute references.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributesMultipleAttributeAttributeRef() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
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
        assertEquals("{A}union-type.string.string.1", ((Attribute) result.getLast()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getLast()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getLast()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes and
     * attribute references with annotations.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeAttributeRefAnnotations() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes and
     * attribute references without type attribute.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeAttributeRefWithoutTypeAttr() {
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
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}simpleTypeA", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes and
     * attribute references with different fixed values.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeAttributeRefDifferentFixed() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertTrue(((SimpleType) ((Attribute) result.getFirst()).getSimpleType()).getInheritance() instanceof SimpleContentRestriction);
        assertTrue(((SimpleContentRestriction) ((SimpleType) ((Attribute) result.getFirst()).getSimpleType()).getInheritance()).getEnumeration().contains("bla"));
        assertTrue(((SimpleContentRestriction) ((SimpleType) ((Attribute) result.getFirst()).getSimpleType()).getInheritance()).getEnumeration().contains("bla2"));
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes and
     * attribute references with same fixed values.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeAttributeRefSameFixed() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes and
     * attribute references with single fixed values.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeAttributeRefSingleFixed() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes and
     * attribute references with different default values.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeAttributeRefDifferentDefault() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes and
     * attribute references with same default values.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeAttributeRefSameDefault() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes and
     * attribute references with same default values but the resulting attribute
     * is not oprtional.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeAttributeRefSameDefaultNotOptional() {
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
        attributeRefB.setUse(AttributeUse.Required);
        attributesB.add(attributeRefB);
        attributeParticleLists.add(attributesB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Required, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes and
     * attribute references with single default values.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeAttributeRefSingleDefault() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeAttributeRefOptionalRequired() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeAttributeRefOptionalProhibited() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeAttributeRefRequiredRequired() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Required, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeAttributeRefRequiredProhibited() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeAttributeRefProhibitedProhibited() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Prohibited, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeAttributeRefQualifiedQualified() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeAttributeRefQualifiedUnqualified() {
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
        LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        namespaceConflictingAttributeMap.put("A", new LinkedHashSet<Attribute>());
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, namespaceConflictingAttributeMap, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

        assertTrue(result.size() == 2);
        assertTrue(result.getFirst() instanceof Attribute);
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
        assertTrue(result.getLast() instanceof AttributeRef);
        assertEquals(null, ((AttributeRef) result.getLast()).getAnnotation());
        assertEquals(null, ((AttributeRef) result.getLast()).getDefault());
        assertEquals(null, ((AttributeRef) result.getLast()).getFixed());
        assertEquals(null, ((AttributeRef) result.getLast()).getId());
        assertEquals(AttributeUse.Optional, ((AttributeRef) result.getLast()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeAttributeRefForeignQualifiedQualified() {
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
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();

        LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        namespaceConflictingAttributeMap.put("A", new LinkedHashSet<Attribute>());
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, null, anyAttributeOldSchemaMap, namespaceConflictingAttributeMap, usedIDs, otherSchemata, namespaceAbbreviationMap, "C:/");
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) newAttributeParticle).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) newAttributeParticle).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) newAttributeParticle).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributes and
     * attribute references.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeAttributeRefForeignQualifiedUnqualified() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for any attributes and attribute.
     */
    @Test
    public void testGenerateAttributeParticleUnionAnyAttributeAttribute() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, null, anyAttributeOldSchemaMap, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##any", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for any attributes and attribute.
     */
    @Test
    public void testGenerateAttributeParticleUnionAnyAttributeAttributeNull() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, null, anyAttributeOldSchemaMap, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

        assertTrue(result.size() == 2);
        assertTrue(result.getFirst() instanceof Attribute);
        assertFalse(outputSchema.getAttributeSymbolTable().hasReference(((Attribute) result.getFirst()).getName()));
        assertFalse(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((Attribute) result.getFirst()).getName()).getReference()));
        assertEquals(null, ((Attribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((Attribute) result.getFirst()).getDefault());
        assertEquals(null, ((Attribute) result.getFirst()).getFixed());
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result.getFirst()).getForm());
        assertEquals(null, ((Attribute) result.getFirst()).getId());
        assertEquals("{A}attributeA", ((Attribute) result.getFirst()).getName());
        assertEquals(false, ((Attribute) result.getFirst()).getSimpleType().isAnonymous());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
        assertTrue(result.getLast() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getLast()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getLast()).getId());
        assertEquals("B", ((AnyAttribute) result.getLast()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getLast()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for any attributes and attribute
     * reference.
     */
    @Test
    public void testGenerateAttributeParticleUnionAnyAttributeAttributeReference() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, null, anyAttributeOldSchemaMap, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));
        LinkedList<AttributeParticle> attributes = new LinkedList<AttributeParticle>();
        attributes.add(attributeA);
        instance.generateNewTopLevelAttribute(attributes);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##any", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for any  attribute and attribute
     * reference.
     */
    @Test
    public void testGenerateAttributeParticleUnionAnyAttributeAttributeReferenceNull() {
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
        LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        namespaceConflictingAttributeMap.put("A", new LinkedHashSet<Attribute>());
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, null, anyAttributeOldSchemaMap, namespaceConflictingAttributeMap, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));
        LinkedList<AttributeParticle> attributes = new LinkedList<AttributeParticle>();
        attributes.add(attributeA);
        instance.generateNewTopLevelAttribute(attributes);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

        assertTrue(result.size() == 2);
        assertTrue(result.getFirst() instanceof AttributeRef);
        assertEquals(null, ((AttributeRef) result.getFirst()).getAnnotation());
        assertEquals(null, ((AttributeRef) result.getFirst()).getDefault());
        assertEquals(null, ((AttributeRef) result.getFirst()).getFixed());
        assertEquals(null, ((AttributeRef) result.getFirst()).getId());
        assertEquals(AttributeUse.Optional, ((AttributeRef) result.getFirst()).getUse());
        assertTrue(result.getLast() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getLast()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getLast()).getId());
        assertEquals("B", ((AnyAttribute) result.getLast()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getLast()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of multiple attributes
     * with a prohibited attribute in one list.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributesMultipleAttributesEmptyProhibited() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
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
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((Attribute) result.getLast()).getSimpleType().getName());
        assertEquals(false, (boolean) ((Attribute) result.getLast()).getTypeAttr());
        assertEquals(AttributeUse.Prohibited, ((Attribute) result.getLast()).getUse());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for any attributes.
     */
    @Test
    public void testGenerateAttributeParticleUnionAnyAttribute() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, anyAttributeOldSchemaMap, null, null, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##local", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for any attributes with
     * annotations.
     */
    @Test
    public void testGenerateAttributeParticleUnionAnyAttributeAnnotation() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, anyAttributeOldSchemaMap, null, null, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for any attributes with
     * namespace="##any".
     */
    @Test
    public void testGenerateAttributeParticleUnionAnyAttributeSingleAny() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, anyAttributeOldSchemaMap, null, null, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##any", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for any attributes with
     * namespace="##other".
     */
    @Test
    public void testGenerateAttributeParticleUnionAnyAttributeSingleOther() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, anyAttributeOldSchemaMap, null, null, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##other", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for any attributes with
     * namespace="##local".
     */
    @Test
    public void testGenerateAttributeParticleUnionAnyAttributeSingleLocal() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, anyAttributeOldSchemaMap, null, null, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##local", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for any attributes with
     * namespace="##targetNamespace".
     */
    @Test
    public void testGenerateAttributeParticleUnionAnyAttributeSingleTargetNamespace() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, anyAttributeOldSchemaMap, null, null, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##targetNamespace", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for any attributes with
     * namespace="A".
     */
    @Test
    public void testGenerateAttributeParticleUnionAnyAttributeSingleUriA() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, anyAttributeOldSchemaMap, null, null, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##targetNamespace", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for any attributes with
     * namespace="B".
     */
    @Test
    public void testGenerateAttributeParticleUnionAnyAttributeSingleUriB() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, anyAttributeOldSchemaMap, null, null, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("B", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for any attributes with
     * namespace="##other".
     */
    @Test
    public void testGenerateAttributeParticleUnionAnyAttributeForeignOther() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, anyAttributeOldSchemaMap, null, null, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##any", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for any attributes with
     * namespace="##other" and "##local".
     */
    @Test
    public void testGenerateAttributeParticleUnionAnyAttributeOtherLocal() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, anyAttributeOldSchemaMap, null, null, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##any", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for any attributes with
     * namespace="##other" and "##targetNamespace" .
     */
    @Test
    public void testGenerateAttributeParticleUnionAnyAttributeOtherTargetNamespace() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, anyAttributeOldSchemaMap, null, null, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##any", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for any attributes with
     * namespace="##other" and "##targetNamespace" .
     */
    @Test
    public void testGenerateAttributeParticleUnionAnyAttributeForeignOtherTargetNamespace() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, anyAttributeOldSchemaMap, null, null, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##other", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for any attributes with
     * namespace="A" and "A B" .
     */
    @Test
    public void testGenerateAttributeParticleUnionAnyAttributeUriAUriAB() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, anyAttributeOldSchemaMap, null, null, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##targetNamespace B", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for any attributes with
     * namespace="##any" and "A B" .
     */
    @Test
    public void testGenerateAttributeParticleUnionAnyAttributeAnyUriAB() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, anyAttributeOldSchemaMap, null, null, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##any", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for any attributes with
     * namespace="##targetNamespace" and "A B" .
     */
    @Test
    public void testGenerateAttributeParticleUnionAnyAttributeTargetNamespaceUriAB() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, anyAttributeOldSchemaMap, null, null, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##targetNamespace B", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for any attributes while one list
     * contains no any attribute.
     */
    @Test
    public void testGenerateAttributeParticleUnionAnyAttributeListContainsNull() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, anyAttributeOldSchemaMap, null, null, null, null, null);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getFirst()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getFirst()).getId());
        assertEquals("##local", ((AnyAttribute) result.getFirst()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getFirst()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for any attributes for lax
     * validated any pattern.
     */
    @Test
    public void testGenerateAttributeParticleUnionAnyAttributeLax() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, anyAttributeOldSchemaMap, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for any attributes for lax
     * validated any pattern with Uris.
     */
    @Test
    public void testGenerateAttributeParticleUnionAnyAttributeLaxUris() {
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
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, null, anyAttributeOldSchemaMap, null, usedIDs, otherSchemata, namespaceAbbreviationMap, "C:/");
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for any attributes for lax
     * validated any pattern with target namespace.
     */
    @Test
    public void testGenerateAttributeParticleUnionAnyAttributeLaxTargetNamespace() {
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
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, null, anyAttributeOldSchemaMap, null, usedIDs, otherSchemata, namespaceAbbreviationMap, "C:/");
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for any attributes for lax
     * validated any pattern with ##local.
     */
    @Test
    public void testGenerateAttributeParticleUnionAnyAttributeLaxLocal() {
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
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, null, anyAttributeOldSchemaMap, null, usedIDs, otherSchemata, namespaceAbbreviationMap, "C:/");
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

        assertTrue(result.size() == 2);
        assertTrue(result.getFirst() instanceof Attribute);
        assertEquals("{A}attributeB", ((Attribute) result.getFirst()).getName());
        assertTrue(result.getLast() instanceof AnyAttribute);
        assertEquals(null, ((AnyAttribute) result.getLast()).getAnnotation());
        assertEquals(null, ((AnyAttribute) result.getLast()).getId());
        assertEquals("##local", ((AnyAttribute) result.getLast()).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result.getLast()).getProcessContentsInstruction());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for any attributes for lax
     * validated any pattern with ##other.
     */
    @Test
    public void testGenerateAttributeParticleUnionAnyAttributeLaxOther() {
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
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, null, anyAttributeOldSchemaMap, null, usedIDs, otherSchemata, namespaceAbbreviationMap, "C:/");
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for any attributes for strict
     * validated any pattern.
     */
    @Test
    public void testGenerateAttributeParticleUnionAnyAttributeStrict() {
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, anyAttributeOldSchemaMap, null, null, null, null, "C:/");
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertEquals(attributeA.getName(), ((Attribute) result.getFirst()).getName());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for any attributes for strict
     * validated any pattern with Uris.
     */
    @Test
    public void testGenerateAttributeParticleUnionAnyAttributeStrictUris() {
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
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, null, anyAttributeOldSchemaMap, null, usedIDs, otherSchemata, namespaceAbbreviationMap, "C:/");
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AttributeGroupRef);
        assertEquals(attributeB.getName(), ((Attribute) ((AttributeGroupRef) result.getFirst()).getAttributeGroup().getAttributeParticles().getFirst()).getName());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for any attributes for strict
     * validated any pattern with target namespace.
     */
    @Test
    public void testGenerateAttributeParticleUnionAnyAttributeStrictTargetNamespace() {
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
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, null, anyAttributeOldSchemaMap, null, usedIDs, otherSchemata, namespaceAbbreviationMap, "C:/");
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertEquals(attributeA.getName(), ((Attribute) result.getFirst()).getName());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for any attributes for strict
     * validated any pattern with ##local.
     */
    @Test
    public void testGenerateAttributeParticleUnionAnyAttributeStrictLocal() {
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
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, null, anyAttributeOldSchemaMap, null, usedIDs, otherSchemata, namespaceAbbreviationMap, "C:/");
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof Attribute);
        assertEquals("{A}attributeB", ((Attribute) result.getFirst()).getName());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for any attributes for strict
     * validated any pattern with ##other.
     */
    @Test
    public void testGenerateAttributeParticleUnionAnyAttributeStrictOther() {
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
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, null, anyAttributeOldSchemaMap, null, usedIDs, otherSchemata, namespaceAbbreviationMap, "C:/");
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

        assertTrue(result.size() == 1);
        assertTrue(result.getFirst() instanceof AttributeGroupRef);
        assertEquals(attributeB.getName(), ((Attribute) ((AttributeGroupRef) result.getFirst()).getAttributeGroup().getAttributeParticles().getFirst()).getName());
    }

    /**
     * Test of generateAttributeParticleUnion method, of class
     * AttributeParticleUnionGenerator for a list of attributeGroups.
     */
    @Test
    public void testGenerateAttributeParticleUnionAttributeGroupResolving() {
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
        LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        namespaceConflictingAttributeMap.put("A", new LinkedHashSet<Attribute>());
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, null, anyAttributeOldSchemaMap, namespaceConflictingAttributeMap, null, null, null, "C:/");
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));
        LinkedList<AttributeParticle> attributes = new LinkedList<AttributeParticle>();
        attributes.add(attributeB);
        instance.generateNewTopLevelAttribute(attributes);

        LinkedList<AttributeParticle> result = instance.generateAttributeParticleUnion(attributeParticleLists);

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
        assertEquals("{A}union-type.string.string", ((Attribute) result.getFirst()).getSimpleType().getName());
        assertEquals(true, (boolean) ((Attribute) result.getFirst()).getTypeAttr());
        assertEquals(AttributeUse.Optional, ((Attribute) result.getFirst()).getUse());
        assertTrue(result.getLast() instanceof AttributeRef);
        assertEquals(null, ((AttributeRef) result.getLast()).getAnnotation());
        assertEquals(null, ((AttributeRef) result.getLast()).getDefault());
        assertEquals(null, ((AttributeRef) result.getLast()).getFixed());
        assertEquals(null, ((AttributeRef) result.getLast()).getId());
        assertEquals(AttributeUse.Optional, ((AttributeRef) result.getLast()).getUse());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(((AttributeRef) result.getLast()).getAttribute().getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((AttributeRef) result.getLast()).getAttribute().getName()).getReference()));
        assertEquals(null, ((AttributeRef) result.getLast()).getAttribute().getAnnotation());
        assertEquals(null, ((AttributeRef) result.getLast()).getAttribute().getDefault());
        assertEquals(null, ((AttributeRef) result.getLast()).getAttribute().getFixed());
        assertEquals(null, ((AttributeRef) result.getLast()).getAttribute().getForm());
        assertEquals(null, ((AttributeRef) result.getLast()).getAttribute().getId());
        assertEquals("{A}attributeB", ((AttributeRef) result.getLast()).getAttribute().getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((AttributeRef) result.getLast()).getAttribute().getSimpleType().getName());
        assertEquals(true, (boolean) ((AttributeRef) result.getLast()).getAttribute().getTypeAttr());
        assertEquals(null, ((AttributeRef) result.getLast()).getAttribute().getUse());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleUnionGenerator for an any attribute.
     */
    @Test
    public void testGenerateNewAttributeParticleAnyAttribute() {
        AttributeParticle oldAttributeParticle = new AnyAttribute(ProcessContentsInstruction.Lax, "##local");

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, null, null, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getId());
        assertEquals("##local", ((AnyAttribute) result).getNamespace());
        assertEquals(ProcessContentsInstruction.Lax, ((AnyAttribute) result).getProcessContentsInstruction());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleUnionGenerator for an any attribute with id.
     */
    @Test
    public void testGenerateNewAttributeParticleAnyAttributeID() {
        AttributeParticle oldAttributeParticle = new AnyAttribute(ProcessContentsInstruction.Lax, "##local");
        oldAttributeParticle.setId("id");

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, usedIDs, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, null, null, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertEquals(null, result.getAnnotation());
        assertEquals("id.1", result.getId());
        assertEquals("##local", ((AnyAttribute) result).getNamespace());
        assertEquals(ProcessContentsInstruction.Lax, ((AnyAttribute) result).getProcessContentsInstruction());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleUnionGenerator for an any attribute with annotation.
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, null, null, null, null));

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
     * AttributeParticleUnionGenerator for an any attribute with namespace value.
     */
    @Test
    public void testGenerateNewAttributeParticleAnyAttributeNamespace() {
        AttributeParticle oldAttributeParticle = new AnyAttribute(ProcessContentsInstruction.Lax, "bla");

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, null, null, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getId());
        assertEquals("bla", ((AnyAttribute) result).getNamespace());
        assertEquals(ProcessContentsInstruction.Lax, ((AnyAttribute) result).getProcessContentsInstruction());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleUnionGenerator for an skip validated any attribute .
     */
    @Test
    public void testGenerateNewAttributeParticleAnyAttributeSkip() {
        AttributeParticle oldAttributeParticle = new AnyAttribute(ProcessContentsInstruction.Skip, "##local");

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, null, null, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getId());
        assertEquals("##local", ((AnyAttribute) result).getNamespace());
        assertEquals(ProcessContentsInstruction.Skip, ((AnyAttribute) result).getProcessContentsInstruction());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleUnionGenerator for an strict validated any attribute .
     */
    @Test
    public void testGenerateNewAttributeParticleAnyAttributeStrict() {
        AttributeParticle oldAttributeParticle = new AnyAttribute(ProcessContentsInstruction.Strict, "##local");

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, null, null, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getId());
        assertEquals("##local", ((AnyAttribute) result).getNamespace());
        assertEquals(ProcessContentsInstruction.Strict, ((AnyAttribute) result).getProcessContentsInstruction());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleUnionGenerator for an attribute.
     */
    @Test
    public void testGenerateNewAttributeParticleAttribute() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        AttributeParticle oldAttributeParticle = oldAttribute;

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

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
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleUnionGenerator for an attribute with use required.
     */
    @Test
    public void testGenerateNewAttributeParticleAttributeUseRequired() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        oldAttribute.setUse(AttributeUse.Required);
        AttributeParticle oldAttributeParticle = oldAttribute;

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

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
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleUnionGenerator for an attribute with use optional.
     */
    @Test
    public void testGenerateNewAttributeParticleAttributeUseOptional() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        oldAttribute.setUse(AttributeUse.Optional);
        AttributeParticle oldAttributeParticle = oldAttribute;

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

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
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleUnionGenerator for an attribute with use prohibited.
     */
    @Test
    public void testGenerateNewAttributeParticleAttributeUseProhibited() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        oldAttribute.setUse(AttributeUse.Prohibited);
        AttributeParticle oldAttributeParticle = oldAttribute;

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

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
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleUnionGenerator for an attribute with type attribute.
     */
    @Test
    public void testGenerateNewAttributeParticleAttributeTypeAttr() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        oldAttribute.setTypeAttr(true);
        AttributeParticle oldAttributeParticle = oldAttribute;

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

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
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleUnionGenerator for an attribute with default.
     */
    @Test
    public void testGenerateNewAttributeParticleAttributeDefault() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        oldAttribute.setDefault("bla");
        AttributeParticle oldAttributeParticle = oldAttribute;

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

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
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleUnionGenerator for an attribute with fixed.
     */
    @Test
    public void testGenerateNewAttributeParticleAttributeFixed() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        oldAttribute.setFixed("bla");
        AttributeParticle oldAttributeParticle = oldAttribute;

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

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
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleUnionGenerator for an unqualified attribute.
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, attributeOldSchemaMap, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

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
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleUnionGenerator for an foreign qualified attribute.
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, attributeOldSchemaMap, null, null, null, otherSchemata, namespaceAbbreviationMap, "C:/");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

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
     * AttributeParticleUnionGenerator for an foreign unqualified attribute.
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, attributeOldSchemaMap, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

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
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) result).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleUnionGenerator for a qualified attribute.
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, attributeOldSchemaMap, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

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
     * AttributeParticleUnionGenerator for an attribute reference.
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
        LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        namespaceConflictingAttributeMap.put("A", new LinkedHashSet<Attribute>());
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, namespaceConflictingAttributeMap, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertTrue(result instanceof AttributeRef);
        assertEquals(null, ((AttributeRef) result).getAnnotation());
        assertEquals(null, ((AttributeRef) result).getDefault());
        assertEquals(null, ((AttributeRef) result).getFixed());
        assertEquals(null, ((AttributeRef) result).getId());
        assertEquals(AttributeUse.Optional, ((AttributeRef) result).getUse());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(((AttributeRef) result).getAttribute().getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((AttributeRef) result).getAttribute().getName()).getReference()));
        assertEquals(null, ((AttributeRef) result).getAttribute().getAnnotation());
        assertEquals(null, ((AttributeRef) result).getAttribute().getDefault());
        assertEquals(null, ((AttributeRef) result).getAttribute().getFixed());
        assertEquals(null, ((AttributeRef) result).getAttribute().getForm());
        assertEquals(null, ((AttributeRef) result).getAttribute().getId());
        assertEquals("{A}attributeA", ((AttributeRef) result).getAttribute().getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((AttributeRef) result).getAttribute().getSimpleType().getName());
        assertEquals(false, (boolean) ((AttributeRef) result).getAttribute().getTypeAttr());
        assertEquals(AttributeUse.Optional, ((AttributeRef) result).getAttribute().getUse());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleUnionGenerator for an attribute reference with ID.
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
        LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        namespaceConflictingAttributeMap.put("A", new LinkedHashSet<Attribute>());
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, namespaceConflictingAttributeMap, usedIDs, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertTrue(result instanceof AttributeRef);
        assertEquals(null, ((AttributeRef) result).getAnnotation());
        assertEquals(null, ((AttributeRef) result).getDefault());
        assertEquals(null, ((AttributeRef) result).getFixed());
        assertEquals("id.1", ((AttributeRef) result).getId());
        assertEquals(AttributeUse.Optional, ((AttributeRef) result).getUse());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(((AttributeRef) result).getAttribute().getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((AttributeRef) result).getAttribute().getName()).getReference()));
        assertEquals(null, ((AttributeRef) result).getAttribute().getAnnotation());
        assertEquals(null, ((AttributeRef) result).getAttribute().getDefault());
        assertEquals(null, ((AttributeRef) result).getAttribute().getFixed());
        assertEquals(null, ((AttributeRef) result).getAttribute().getForm());
        assertEquals(null, ((AttributeRef) result).getAttribute().getId());
        assertEquals("{A}attributeA", ((AttributeRef) result).getAttribute().getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((AttributeRef) result).getAttribute().getSimpleType().getName());
        assertEquals(false, (boolean) ((AttributeRef) result).getAttribute().getTypeAttr());
        assertEquals(AttributeUse.Optional, ((AttributeRef) result).getAttribute().getUse());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleUnionGenerator for an attribute reference with
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
        LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        namespaceConflictingAttributeMap.put("A", new LinkedHashSet<Attribute>());
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, namespaceConflictingAttributeMap, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertTrue(result instanceof AttributeRef);
        assertNotSame(annotation, ((AttributeRef) result).getAnnotation());
        assertTrue(((AttributeRef) result).getAnnotation().getAppInfos().size() == 1);
        assertEquals("bla", ((AttributeRef) result).getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((AttributeRef) result).getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaa", ((AttributeRef) result).getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(null, ((AttributeRef) result).getDefault());
        assertEquals(null, ((AttributeRef) result).getFixed());
        assertEquals(null, ((AttributeRef) result).getId());
        assertEquals(AttributeUse.Optional, ((AttributeRef) result).getUse());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(((AttributeRef) result).getAttribute().getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((AttributeRef) result).getAttribute().getName()).getReference()));
        assertEquals(null, ((AttributeRef) result).getAttribute().getAnnotation());
        assertEquals(null, ((AttributeRef) result).getAttribute().getDefault());
        assertEquals(null, ((AttributeRef) result).getAttribute().getFixed());
        assertEquals(null, ((AttributeRef) result).getAttribute().getForm());
        assertEquals(null, ((AttributeRef) result).getAttribute().getId());
        assertEquals("{A}attributeA", ((AttributeRef) result).getAttribute().getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((AttributeRef) result).getAttribute().getSimpleType().getName());
        assertEquals(false, (boolean) ((AttributeRef) result).getAttribute().getTypeAttr());
        assertEquals(AttributeUse.Optional, ((AttributeRef) result).getAttribute().getUse());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleUnionGenerator for an attribute reference with
     * default value.
     */
    @Test
    public void testGenerateNewAttributeParticleAttributeRefDefault() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setDefault("bla");
        AttributeParticle oldAttributeParticle = attributeRefA;

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        namespaceConflictingAttributeMap.put("A", new LinkedHashSet<Attribute>());
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, namespaceConflictingAttributeMap, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertTrue(result instanceof AttributeRef);
        assertEquals(null, ((AttributeRef) result).getAnnotation());
        assertEquals("bla", ((AttributeRef) result).getDefault());
        assertEquals(null, ((AttributeRef) result).getFixed());
        assertEquals(null, ((AttributeRef) result).getId());
        assertEquals(AttributeUse.Optional, ((AttributeRef) result).getUse());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(((AttributeRef) result).getAttribute().getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((AttributeRef) result).getAttribute().getName()).getReference()));
        assertEquals(null, ((AttributeRef) result).getAttribute().getAnnotation());
        assertEquals(null, ((AttributeRef) result).getAttribute().getDefault());
        assertEquals(null, ((AttributeRef) result).getAttribute().getFixed());
        assertEquals(null, ((AttributeRef) result).getAttribute().getForm());
        assertEquals(null, ((AttributeRef) result).getAttribute().getId());
        assertEquals("{A}attributeA", ((AttributeRef) result).getAttribute().getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((AttributeRef) result).getAttribute().getSimpleType().getName());
        assertEquals(false, (boolean) ((AttributeRef) result).getAttribute().getTypeAttr());
        assertEquals(AttributeUse.Optional, ((AttributeRef) result).getAttribute().getUse());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleUnionGenerator for an attribute reference with
     * fixed value.
     */
    @Test
    public void testGenerateNewAttributeParticleAttributeRefFixed() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute attributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        AttributeRef attributeRefA = new AttributeRef(new SymbolTableRef<Attribute>(attributeA.getName(), attributeA));
        attributeRefA.setFixed("bla");
        AttributeParticle oldAttributeParticle = attributeRefA;

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        namespaceConflictingAttributeMap.put("A", new LinkedHashSet<Attribute>());
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, namespaceConflictingAttributeMap, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertTrue(result instanceof AttributeRef);
        assertEquals(null, ((AttributeRef) result).getAnnotation());
        assertEquals(null, ((AttributeRef) result).getDefault());
        assertEquals("bla", ((AttributeRef) result).getFixed());
        assertEquals(null, ((AttributeRef) result).getId());
        assertEquals(AttributeUse.Optional, ((AttributeRef) result).getUse());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(((AttributeRef) result).getAttribute().getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((AttributeRef) result).getAttribute().getName()).getReference()));
        assertEquals(null, ((AttributeRef) result).getAttribute().getAnnotation());
        assertEquals(null, ((AttributeRef) result).getAttribute().getDefault());
        assertEquals(null, ((AttributeRef) result).getAttribute().getFixed());
        assertEquals(null, ((AttributeRef) result).getAttribute().getForm());
        assertEquals(null, ((AttributeRef) result).getAttribute().getId());
        assertEquals("{A}attributeA", ((AttributeRef) result).getAttribute().getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((AttributeRef) result).getAttribute().getSimpleType().getName());
        assertEquals(false, (boolean) ((AttributeRef) result).getAttribute().getTypeAttr());
        assertEquals(AttributeUse.Optional, ((AttributeRef) result).getAttribute().getUse());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleUnionGenerator for an attribute reference with
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
        LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        namespaceConflictingAttributeMap.put("A", new LinkedHashSet<Attribute>());
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, namespaceConflictingAttributeMap, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertTrue(result instanceof AttributeRef);
        assertEquals(null, ((AttributeRef) result).getAnnotation());
        assertEquals(null, ((AttributeRef) result).getDefault());
        assertEquals(null, ((AttributeRef) result).getFixed());
        assertEquals(null, ((AttributeRef) result).getId());
        assertEquals(AttributeUse.Prohibited, ((AttributeRef) result).getUse());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(((AttributeRef) result).getAttribute().getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((AttributeRef) result).getAttribute().getName()).getReference()));
        assertEquals(null, ((AttributeRef) result).getAttribute().getAnnotation());
        assertEquals(null, ((AttributeRef) result).getAttribute().getDefault());
        assertEquals(null, ((AttributeRef) result).getAttribute().getFixed());
        assertEquals(null, ((AttributeRef) result).getAttribute().getForm());
        assertEquals(null, ((AttributeRef) result).getAttribute().getId());
        assertEquals("{A}attributeA", ((AttributeRef) result).getAttribute().getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((AttributeRef) result).getAttribute().getSimpleType().getName());
        assertEquals(false, (boolean) ((AttributeRef) result).getAttribute().getTypeAttr());
        assertEquals(AttributeUse.Optional, ((AttributeRef) result).getAttribute().getUse());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleUnionGenerator for an attribute reference with
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
        LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        namespaceConflictingAttributeMap.put("A", new LinkedHashSet<Attribute>());
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, namespaceConflictingAttributeMap, null, null, null, null);
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));
        instance.generateNewTopLevelAttribute(attributeA);

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertTrue(result instanceof AttributeRef);
        assertEquals(null, ((AttributeRef) result).getAnnotation());
        assertEquals(null, ((AttributeRef) result).getDefault());
        assertEquals(null, ((AttributeRef) result).getFixed());
        assertEquals(null, ((AttributeRef) result).getId());
        assertEquals(AttributeUse.Required, ((AttributeRef) result).getUse());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(((AttributeRef) result).getAttribute().getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(((AttributeRef) result).getAttribute().getName()).getReference()));
        assertEquals(null, ((AttributeRef) result).getAttribute().getAnnotation());
        assertEquals(null, ((AttributeRef) result).getAttribute().getDefault());
        assertEquals(null, ((AttributeRef) result).getAttribute().getFixed());
        assertEquals(null, ((AttributeRef) result).getAttribute().getForm());
        assertEquals(null, ((AttributeRef) result).getAttribute().getId());
        assertEquals("{A}attributeA", ((AttributeRef) result).getAttribute().getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((AttributeRef) result).getAttribute().getSimpleType().getName());
        assertEquals(false, (boolean) ((AttributeRef) result).getAttribute().getTypeAttr());
        assertEquals(AttributeUse.Optional, ((AttributeRef) result).getAttribute().getUse());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleUnionGenerator for an attribute reference referring
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
        namespaceOutputSchemaMap.put("A", new XSDSchema("A"));
        LinkedHashSet<XSDSchema> otherSchemata = new LinkedHashSet<XSDSchema>();
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        LinkedHashMap<String, String> namespaceAbbreviationMap = new LinkedHashMap<String, String>();
        LinkedHashMap<String, LinkedHashSet<Attribute>> namespaceConflictingAttributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        namespaceConflictingAttributeMap.put("A", new LinkedHashSet<Attribute>());
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, namespaceOutputSchemaMap, attributeOldSchemaMap, null, namespaceConflictingAttributeMap, usedIDs, otherSchemata, namespaceAbbreviationMap, "C:/");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        AttributeParticle result = instance.generateNewAttributeParticle(oldAttributeParticle);

        assertTrue(result instanceof AttributeRef);
        assertEquals(null, ((AttributeRef) result).getAnnotation());
        assertEquals(null, ((AttributeRef) result).getDefault());
        assertEquals(null, ((AttributeRef) result).getFixed());
        assertEquals(null, ((AttributeRef) result).getId());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleUnionGenerator for an attribute group reference.
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, attributeOldSchemaMap, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

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
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) resultingAttribute).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleUnionGenerator for an attribute group reference
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, attributeOldSchemaMap, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

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
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) resultingAttribute).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleUnionGenerator for an attribute group reference with ID.
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, attributeOldSchemaMap, null, null, usedIDs, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

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
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) resultingAttribute).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleUnionGenerator for an attribute group reference with
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, attributeOldSchemaMap, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

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
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) resultingAttribute).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleUnionGenerator for an attribute group reference with
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, attributeOldSchemaMap, null, null, usedIDs, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

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
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) resultingAttribute).getForm());
    }

    /**
     * Test of generateNewAttributeParticle method, of class
     * AttributeParticleUnionGenerator for an attribute group reference with
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
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, attributeOldSchemaMap, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

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
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) resultingAttribute).getForm());
    }

    /**
     * Test of generateNewTopLevelAttribute method, of class
     * AttributeParticleUnionGenerator with non anonymous type.
     */
    @Test
    public void testGenerateNewTopLevelAttribute_AttributeNonAnonymousType() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Attribute result = instance.generateNewTopLevelAttribute(oldAttribute);

        assertEquals("{A}simpleTypeA", result.getSimpleType().getName());
        assertEquals(false, result.getSimpleType().isAnonymous());
        assertEquals(oldAttribute.getTypeAttr(), result.getTypeAttr());
        assertEquals(oldAttribute.getDefault(), result.getDefault());
        assertEquals(oldAttribute.getFixed(), result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(AttributeUse.Optional, result.getUse());
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelAttribute method, of class
     * AttributeParticleUnionGenerator with anonymous type.
     */
    @Test
    public void testGenerateNewTopLevelAttribute_AttributeAnonymousType() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.setIsAnonymous(true);

        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Attribute result = instance.generateNewTopLevelAttribute(oldAttribute);

        assertEquals("{A}simpleTypeA", result.getSimpleType().getName());
        assertEquals(true, result.getSimpleType().isAnonymous());
        assertEquals(oldAttribute.getTypeAttr(), result.getTypeAttr());
        assertEquals(oldAttribute.getDefault(), result.getDefault());
        assertEquals(oldAttribute.getFixed(), result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(AttributeUse.Optional, result.getUse());
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelAttribute method, of class
     * AttributeParticleUnionGenerator with build-in type.
     */
    @Test
    public void testGenerateNewTopLevelAttribute_AttributeBuildInType() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Attribute result = instance.generateNewTopLevelAttribute(oldAttribute);

        assertEquals("{http://www.w3.org/2001/XMLSchema}string", result.getSimpleType().getName());
        assertEquals(false, result.getSimpleType().isAnonymous());
        assertEquals(oldAttribute.getTypeAttr(), result.getTypeAttr());
        assertEquals(oldAttribute.getDefault(), result.getDefault());
        assertEquals(oldAttribute.getFixed(), result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(AttributeUse.Optional, result.getUse());
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelAttribute method, of class
     * AttributeParticleUnionGenerator with type from foreign schema.
     */
    @Test
    public void testGenerateNewTopLevelAttribute_AttributeForeignType() {
        SimpleType simpleTypeA = new SimpleType("{B}simpleTypeA", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{B}simpleTypeA", simpleTypeA));

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Attribute result = instance.generateNewTopLevelAttribute(oldAttribute);

        assertEquals("{A}simpleTypeA", result.getSimpleType().getName());
        assertEquals(false, result.getSimpleType().isAnonymous());
        assertEquals(oldAttribute.getTypeAttr(), result.getTypeAttr());
        assertEquals(oldAttribute.getDefault(), result.getDefault());
        assertEquals(oldAttribute.getFixed(), result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(AttributeUse.Optional, result.getUse());
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelAttribute method, of class
     * AttributeParticleUnionGenerator with id.
     */
    @Test
    public void testGenerateNewTopLevelAttribute_AttributeID() {
        SimpleType simpleTypeA = new SimpleType("{B}simpleTypeA", null);
        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{B}simpleTypeA", simpleTypeA));
        oldAttribute.setId("id");

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, usedIDs, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Attribute result = instance.generateNewTopLevelAttribute(oldAttribute);

        assertEquals("{A}simpleTypeA", result.getSimpleType().getName());
        assertEquals(false, result.getSimpleType().isAnonymous());
        assertEquals(oldAttribute.getTypeAttr(), result.getTypeAttr());
        assertEquals(oldAttribute.getDefault(), result.getDefault());
        assertEquals(oldAttribute.getFixed(), result.getFixed());
        assertEquals("id.1", result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(AttributeUse.Optional, result.getUse());
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelAttribute method, of class
     * AttributeParticleUnionGenerator with annotation.
     */
    @Test
    public void testGenerateNewTopLevelAttribute_AttributeAnnotation() {
        SimpleType simpleTypeA = new SimpleType("{B}simpleTypeA", null);

        Attribute oldAttribute = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{B}simpleTypeA", simpleTypeA));
        Annotation annotation = new Annotation();
        AppInfo appInfo = new AppInfo();
        appInfo.setSource("bla");
        Documentation documentation = new Documentation();
        documentation.setSource("blaa");
        annotation.addAppInfos(appInfo);
        annotation.addDocumentations(documentation);
        oldAttribute.setAnnotation(annotation);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, usedIDs, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Attribute result = instance.generateNewTopLevelAttribute(oldAttribute);

        assertEquals("{A}simpleTypeA", result.getSimpleType().getName());
        assertEquals(false, result.getSimpleType().isAnonymous());
        assertEquals(oldAttribute.getTypeAttr(), result.getTypeAttr());
        assertEquals(oldAttribute.getDefault(), result.getDefault());
        assertEquals(oldAttribute.getFixed(), result.getFixed());
        assertEquals(null, result.getId());
        assertNotSame(annotation, result.getAnnotation());
        assertTrue(result.getAnnotation().getAppInfos().size() == 1);
        assertEquals("bla", result.getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(result.getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaa", result.getAnnotation().getDocumentations().get(0).getSource());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(AttributeUse.Optional, result.getUse());
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelAttribute method, of class
     * AttributeParticleUnionGenerator with non anonymous type.
     */
    @Test
    public void testGenerateNewTopLevelAttribute_LinkedListNonAnonymousType() {
        LinkedList<AttributeParticle> attributeParticles = new LinkedList<AttributeParticle>();

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.setIsAnonymous(true);
        Attribute oldAttributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        attributeParticles.add(oldAttributeA);

        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        Attribute oldAttributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeB", simpleTypeB));
        attributeParticles.add(oldAttributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Attribute result = instance.generateNewTopLevelAttribute(attributeParticles);

        assertEquals("{A}union-type.simpleTypeA.simpleTypeB", result.getSimpleType().getName());
        assertEquals(false, result.getSimpleType().isAnonymous());
        assertNotNull(result.getSimpleType().getInheritance());
        assertTrue(result.getSimpleType().getInheritance() instanceof SimpleContentUnion);
        assertEquals(true, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getUse());
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelAttribute method, of class
     * AttributeParticleUnionGenerator with anonymous type.
     */
    @Test
    public void testGenerateNewTopLevelAttribute_LinkedListAnonymousType() {
        LinkedList<AttributeParticle> attributeParticles = new LinkedList<AttributeParticle>();

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.setIsAnonymous(true);
        Attribute oldAttributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        attributeParticles.add(oldAttributeA);

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Attribute result = instance.generateNewTopLevelAttribute(attributeParticles);

        assertEquals("{A}simpleTypeA", result.getSimpleType().getName());
        assertEquals(true, result.getSimpleType().isAnonymous());
        assertNull(result.getSimpleType().getInheritance());
        assertEquals(false, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getUse());
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelAttribute method, of class
     * AttributeParticleUnionGenerator with build-in type.
     */
    @Test
    public void testGenerateNewTopLevelAttribute_LinkedListBuildInType() {
        LinkedList<AttributeParticle> attributeParticles = new LinkedList<AttributeParticle>();

        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        Attribute oldAttributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        attributeParticles.add(oldAttributeA);

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Attribute result = instance.generateNewTopLevelAttribute(attributeParticles);

        assertEquals("{http://www.w3.org/2001/XMLSchema}string", result.getSimpleType().getName());
        assertEquals(false, result.getSimpleType().isAnonymous());
        assertNull(result.getSimpleType().getInheritance());
        assertEquals(true, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getUse());
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelAttribute method, of class
     * AttributeParticleUnionGenerator with types from foreign schema.
     */
    @Test
    public void testGenerateNewTopLevelAttribute_LinkedListForeignTypes() {
        LinkedList<AttributeParticle> attributeParticles = new LinkedList<AttributeParticle>();

        SimpleType simpleTypeA = new SimpleType("{B}simpleTypeA", null);
        simpleTypeA.setIsAnonymous(true);
        Attribute oldAttributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        attributeParticles.add(oldAttributeA);

        SimpleType simpleTypeB = new SimpleType("{B}simpleTypeB", null);
        Attribute oldAttributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeB", simpleTypeB));
        attributeParticles.add(oldAttributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Attribute result = instance.generateNewTopLevelAttribute(attributeParticles);

        assertEquals("{A}union-type.simpleTypeA.simpleTypeB", result.getSimpleType().getName());
        assertEquals(false, result.getSimpleType().isAnonymous());
        assertNotNull(result.getSimpleType().getInheritance());
        assertTrue(result.getSimpleType().getInheritance() instanceof SimpleContentUnion);
        assertEquals(true, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getUse());
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelAttribute method, of class
     * AttributeParticleUnionGenerator with any type.
     */
    @Test
    public void testGenerateNewTopLevelAttribute_LinkedListAnyType() {
        LinkedList<AttributeParticle> attributeParticles = new LinkedList<AttributeParticle>();

        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}anyType", null);
        simpleTypeA.setIsAnonymous(true);
        Attribute oldAttributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}anyType", simpleTypeA));
        attributeParticles.add(oldAttributeA);

        SimpleType simpleTypeB = new SimpleType("{B}simpleTypeB", null);
        Attribute oldAttributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeB", simpleTypeB));
        attributeParticles.add(oldAttributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Attribute result = instance.generateNewTopLevelAttribute(attributeParticles);

        assertEquals("{http://www.w3.org/2001/XMLSchema}anySimpleType", result.getSimpleType().getName());
        assertEquals(false, result.getSimpleType().isAnonymous());
        assertNull(result.getSimpleType().getInheritance());
        assertEquals(true, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getUse());
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelAttribute method, of class
     * AttributeParticleUnionGenerator with any simple type.
     */
    @Test
    public void testGenerateNewTopLevelAttribute_LinkedListAnySimpleType() {
        LinkedList<AttributeParticle> attributeParticles = new LinkedList<AttributeParticle>();

        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        simpleTypeA.setIsAnonymous(true);
        Attribute oldAttributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}anyType", simpleTypeA));
        attributeParticles.add(oldAttributeA);

        SimpleType simpleTypeB = new SimpleType("{B}simpleTypeB", null);
        Attribute oldAttributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeB", simpleTypeB));
        attributeParticles.add(oldAttributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Attribute result = instance.generateNewTopLevelAttribute(attributeParticles);

        assertEquals("{http://www.w3.org/2001/XMLSchema}anySimpleType", result.getSimpleType().getName());
        assertEquals(false, result.getSimpleType().isAnonymous());
        assertNull(result.getSimpleType().getInheritance());
        assertEquals(true, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getUse());
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelAttribute method, of class
     * AttributeParticleUnionGenerator with ids.
     */
    @Test
    public void testGenerateNewTopLevelAttribute_LinkedListID() {
        LinkedList<AttributeParticle> attributeParticles = new LinkedList<AttributeParticle>();

        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        Attribute oldAttributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}anyType", simpleTypeA));
        oldAttributeA.setId("idOne");
        attributeParticles.add(oldAttributeA);

        SimpleType simpleTypeB = new SimpleType("{B}simpleTypeB", null);
        Attribute oldAttributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeB", simpleTypeB));
        oldAttributeB.setId("idTwo");
        attributeParticles.add(oldAttributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("idOne.idTwo");
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, usedIDs, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Attribute result = instance.generateNewTopLevelAttribute(attributeParticles);

        assertEquals("{http://www.w3.org/2001/XMLSchema}anySimpleType", result.getSimpleType().getName());
        assertEquals(false, result.getSimpleType().isAnonymous());
        assertNull(result.getSimpleType().getInheritance());
        assertEquals(true, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals("idOne.idTwo.1", result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getUse());
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelAttribute method, of class
     * AttributeParticleUnionGenerator with annotations.
     */
    @Test
    public void testGenerateNewTopLevelAttribute_LinkedListAnnotation() {
        LinkedList<AttributeParticle> attributeParticles = new LinkedList<AttributeParticle>();

        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        Attribute oldAttributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}anyType", simpleTypeA));
        attributeParticles.add(oldAttributeA);

        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        oldAttributeA.setAnnotation(annotationA);

        SimpleType simpleTypeB = new SimpleType("{B}simpleTypeB", null);
        Attribute oldAttributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeB", simpleTypeB));
        attributeParticles.add(oldAttributeB);

        Annotation annotationB = new Annotation();
        AppInfo appInfoB = new AppInfo();
        appInfoB.setSource("blaB");
        Documentation documentationB = new Documentation();
        documentationB.setSource("blaaB");
        annotationB.addAppInfos(appInfoB);
        annotationB.addDocumentations(documentationB);
        oldAttributeB.setAnnotation(annotationB);

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Attribute result = instance.generateNewTopLevelAttribute(attributeParticles);

        assertEquals("{http://www.w3.org/2001/XMLSchema}anySimpleType", result.getSimpleType().getName());
        assertEquals(false, result.getSimpleType().isAnonymous());
        assertNull(result.getSimpleType().getInheritance());
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
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getUse());
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelAttribute method, of class
     * AttributeParticleUnionGenerator with different fixed type values.
     */
    @Test
    public void testGenerateNewTopLevelAttribute_LinkedListFixedTypes() {
        LinkedList<AttributeParticle> attributeParticles = new LinkedList<AttributeParticle>();

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.setIsAnonymous(true);
        Attribute oldAttributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        oldAttributeA.setFixed("bla");
        attributeParticles.add(oldAttributeA);

        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        Attribute oldAttributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeB", simpleTypeB));
        oldAttributeB.setFixed("bla2");
        attributeParticles.add(oldAttributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Attribute result = instance.generateNewTopLevelAttribute(attributeParticles);

        assertEquals(true, result.getSimpleType().isAnonymous());
        assertNotNull(result.getSimpleType().getInheritance());
        assertTrue(result.getSimpleType().getInheritance() instanceof SimpleContentRestriction);
        assertTrue(((SimpleContentRestriction) result.getSimpleType().getInheritance()).getEnumeration().contains("bla"));
        assertTrue(((SimpleContentRestriction) result.getSimpleType().getInheritance()).getEnumeration().contains("bla2"));
        assertEquals(false, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getUse());
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelAttribute method, of class
     * AttributeParticleUnionGenerator with a single fixed type value.
     */
    @Test
    public void testGenerateNewTopLevelAttribute_LinkedListFixedType() {
        LinkedList<AttributeParticle> attributeParticles = new LinkedList<AttributeParticle>();

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.setIsAnonymous(true);
        Attribute oldAttributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        oldAttributeA.setFixed("bla");
        attributeParticles.add(oldAttributeA);

        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        Attribute oldAttributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeB", simpleTypeB));
        oldAttributeB.setFixed("bla");
        attributeParticles.add(oldAttributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Attribute result = instance.generateNewTopLevelAttribute(attributeParticles);

        assertEquals("{A}union-type.simpleTypeA.simpleTypeB", result.getSimpleType().getName());
        assertEquals(false, result.getSimpleType().isAnonymous());
        assertNotNull(result.getSimpleType().getInheritance());
        assertTrue(result.getSimpleType().getInheritance() instanceof SimpleContentUnion);
        assertEquals(true, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals("bla", result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getUse());
        assertEquals(null, result.getForm());
    }

    /**
     * Test of generateNewTopLevelAttribute method, of class
     * AttributeParticleUnionGenerator with no fixed type.
     */
    @Test
    public void testGenerateNewTopLevelAttribute_LinkedListNoFixedType() {
        LinkedList<AttributeParticle> attributeParticles = new LinkedList<AttributeParticle>();

        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.setIsAnonymous(true);
        Attribute oldAttributeA = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeA", simpleTypeA));
        oldAttributeA.setFixed("bob");
        attributeParticles.add(oldAttributeA);

        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        Attribute oldAttributeB = new Attribute("{A}attributeA", new SymbolTableRef<Type>("{A}simpleTypeB", simpleTypeB));
        attributeParticles.add(oldAttributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        AttributeParticleUnionGenerator instance = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        instance.setTypeUnionGenerator(new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null));

        Attribute result = instance.generateNewTopLevelAttribute(attributeParticles);

        assertEquals("{A}union-type.simpleTypeA.simpleTypeB", result.getSimpleType().getName());
        assertEquals(false, result.getSimpleType().isAnonymous());
        assertNotNull(result.getSimpleType().getInheritance());
        assertTrue(result.getSimpleType().getInheritance() instanceof SimpleContentUnion);
        assertEquals(true, (boolean) result.getTypeAttr());
        assertEquals(null, result.getDefault());
        assertEquals(null, result.getFixed());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertTrue(outputSchema.getAttributeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getAttributes().contains(outputSchema.getAttributeSymbolTable().getReference(result.getName()).getReference()));
        assertEquals(null, result.getUse());
        assertEquals(null, result.getForm());
    }
}