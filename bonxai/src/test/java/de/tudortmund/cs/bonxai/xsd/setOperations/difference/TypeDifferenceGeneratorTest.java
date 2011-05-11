package de.tudortmund.cs.bonxai.xsd.setOperations.difference;

import de.tudortmund.cs.bonxai.common.Annotation;
import de.tudortmund.cs.bonxai.common.ChoicePattern;
import de.tudortmund.cs.bonxai.common.CountingPattern;
import de.tudortmund.cs.bonxai.common.Particle;
import de.tudortmund.cs.bonxai.common.SequencePattern;
import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.xsd.automaton.ParticleAutomatons.exceptions.*;
import de.tudortmund.cs.bonxai.xsd.automaton.exceptions.*;
import java.util.*;
import java.util.logging.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>TypeDifferenceGenerator</tt> class, checks that every
 * method of this class performs properly.
 * @author Dominik Wolff
 */
public class TypeDifferenceGeneratorTest extends junit.framework.TestCase {

    public TypeDifferenceGeneratorTest() {
    }

    /**
     * Test of isOptionalParticle method, of class TypeDifferenceGenerator.
     */
    @Test
    public void testIsOptionalParticleTrue() throws Exception {
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
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);

        boolean expResult = true;
        boolean result = instance.isOptionalParticle(particle);

        assertEquals(expResult, result);
    }

    /**
     * Test of isOptionalParticle method, of class TypeDifferenceGenerator.
     */
    @Test
    public void testIsOptionalParticleFalse() throws Exception {
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
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);

        boolean expResult = false;
        boolean result = instance.isOptionalParticle(particle);

        assertEquals(expResult, result);
    }

    /**
     * Test of generateNewSimpleType method, of class TypeDifferenceGenerator.
     */
    @Test
    public void testGenerateNewSimpleType() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null);

        String typeName = "{A}simpleTypeA";
        SimpleType result = instance.generateNewSimpleType(simpleTypeA, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getFinalModifiers());
        assertEquals(null, result.getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeDifferenceGenerator for
     * a build-in type.
     */
    @Test
    public void testGenerateNewSimpleTypeBuildIn() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null);

        String typeName = "{http://www.w3.org/2001/XMLSchema}string";
        SimpleType result = instance.generateNewSimpleType(simpleTypeA, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getFinalModifiers());
        assertEquals(null, result.getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeDifferenceGenerator for
     * a single simpleType with same name as given name.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleTypeSameName() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null);

        String typeName = "{A}simpleTypeA";
        SimpleType result = instance.generateNewSimpleType(simpleTypeA, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getFinalModifiers());
        assertEquals(null, result.getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeDifferenceGenerator for
     * simpleType with ID.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleTypeID() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.setId("idOne");

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("idOne");
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, usedIDs, typeOldSchemaMap, null, null);

        String typeName = "{A}simpleTypeA";
        SimpleType result = instance.generateNewSimpleType(simpleTypeA, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals("idOne.1", result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getFinalModifiers());
        assertEquals(null, result.getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeDifferenceGenerator for
     * simpleType with annotation.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleTypeAnnotation() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        simpleTypeA.setAnnotation(annotationA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null);

        String typeName = "{A}simpleTypeA";
        SimpleType result = instance.generateNewSimpleType(simpleTypeA, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertTrue(result.getAnnotation().getAppInfos().size() == 1);
        assertEquals("blaA", result.getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(result.getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaaA", result.getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(null, result.getFinalModifiers());
        assertEquals(null, result.getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeDifferenceGenerator for
     * a single anonymous simpleType.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleAnonymousType() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.setIsAnonymous(true);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null);

        String typeName = "{A}simpleTypeA";
        SimpleType result = instance.generateNewSimpleType(simpleTypeA, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(null, result.getId());
        assertEquals(true, result.isAnonymous());
        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getFinalModifiers());
        assertEquals(null, result.getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeDifferenceGenerator for
     * a single anonymous simpleType from another schema.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleAnonymousTypeOtherSchema() {
        SimpleType simpleTypeA = new SimpleType("{}simpleTypeA", null);
        simpleTypeA.setIsAnonymous(true);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema(""));
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null);

        String typeName = "{A}simpleTypeA";
        SimpleType result = instance.generateNewSimpleType(simpleTypeA, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(null, result.getId());
        assertEquals(true, result.isAnonymous());
        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getFinalModifiers());
        assertEquals(null, result.getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeDifferenceGenerator for a
     * simpleType which finalizes list.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleTypeFinalList() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.List);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null);

        String typeName = "{A}union-type.simpleTypeA.simpleTypeB";
        SimpleType result = instance.generateNewSimpleType(simpleTypeA, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        HashSet<SimpleTypeInheritanceModifier> finalValue = new HashSet<SimpleTypeInheritanceModifier>();
        finalValue.add(SimpleTypeInheritanceModifier.List);
        assertEquals(finalValue, result.getFinalModifiers());
        assertEquals(null, result.getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeDifferenceGenerator for a
     * simpleType which finalizes restriction.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleTypeFinalRestriction() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.Restriction);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null);

        String typeName = "{A}union-type.simpleTypeA.simpleTypeB";
        SimpleType result = instance.generateNewSimpleType(simpleTypeA, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        HashSet<SimpleTypeInheritanceModifier> finalValue = new HashSet<SimpleTypeInheritanceModifier>();
        finalValue.add(SimpleTypeInheritanceModifier.Restriction);
        assertEquals(finalValue, result.getFinalModifiers());
        assertEquals(null, result.getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeDifferenceGenerator for a
     * simpleType which finalizes union.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleTypeFinalUnion() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.Union);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null);

        String typeName = "{A}union-type.simpleTypeA.simpleTypeB";
        SimpleType result = instance.generateNewSimpleType(simpleTypeA, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        HashSet<SimpleTypeInheritanceModifier> finalValue = new HashSet<SimpleTypeInheritanceModifier>();
        finalValue.add(SimpleTypeInheritanceModifier.Union);
        assertEquals(finalValue, result.getFinalModifiers());
        assertEquals(null, result.getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeDifferenceGenerator for a
     * simpleType which finalizes union which is schema default.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleTypeFinalDefault() {
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        outputSchema.addFinalDefault(XSDSchema.FinalDefault.union);
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.union);
        typeOldSchemaMap.put(simpleTypeA, oldSchema);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null);

        String typeName = "{A}union-type.simpleTypeA.simpleTypeB";
        SimpleType result = instance.generateNewSimpleType(simpleTypeA, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getFinalModifiers());
        assertEquals(null, result.getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeDifferenceGenerator for
     * a single list type.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleTypeList() {
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentList simpleContentList = new SimpleContentList(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", simpleContentList);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null);

        String typeName = "{A}simpleTypeA";
        SimpleType result = instance.generateNewSimpleType(simpleTypeA, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getFinalModifiers());
        assertTrue(result.getInheritance() instanceof SimpleContentList);
        assertEquals(null, ((SimpleContentList) result.getInheritance()).getId());
        assertEquals(null, ((SimpleContentList) result.getInheritance()).getAnnotation());
        assertTrue(((SimpleContentList) result.getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentList) result.getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentList) result.getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentList) result.getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentList) result.getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleContentList) result.getInheritance()).getBaseSimpleType().getFinalModifiers());
        assertEquals(null, ((SimpleContentList) result.getInheritance()).getBaseSimpleType().getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentList) result.getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentList) result.getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeDifferenceGenerator for
     * a single list type with ID.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleTypeListID() {
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentList simpleContentList = new SimpleContentList(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        simpleContentList.setId("id");
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", simpleContentList);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, usedIDs, typeOldSchemaMap, null, null);

        String typeName = "{A}simpleTypeA";
        SimpleType result = instance.generateNewSimpleType(simpleTypeA, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getFinalModifiers());
        assertTrue(result.getInheritance() instanceof SimpleContentList);
        assertEquals("id.1", ((SimpleContentList) result.getInheritance()).getId());
        assertEquals(null, ((SimpleContentList) result.getInheritance()).getAnnotation());
        assertTrue(((SimpleContentList) result.getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentList) result.getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentList) result.getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentList) result.getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentList) result.getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleContentList) result.getInheritance()).getBaseSimpleType().getFinalModifiers());
        assertEquals(null, ((SimpleContentList) result.getInheritance()).getBaseSimpleType().getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentList) result.getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentList) result.getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeDifferenceGenerator for
     * a single list type with annotation.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleTypeListAnnotation() {
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentList simpleContentList = new SimpleContentList(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        simpleContentList.setAnnotation(annotationA);
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", simpleContentList);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null);

        String typeName = "{A}simpleTypeA";
        SimpleType result = instance.generateNewSimpleType(simpleTypeA, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getFinalModifiers());
        assertTrue(result.getInheritance() instanceof SimpleContentList);
        assertEquals(null, ((SimpleContentList) result.getInheritance()).getId());
        assertTrue(((SimpleContentList) result.getInheritance()).getAnnotation().getAppInfos().size() == 1);
        assertEquals("blaA", ((SimpleContentList) result.getInheritance()).getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((SimpleContentList) result.getInheritance()).getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaaA", ((SimpleContentList) result.getInheritance()).getAnnotation().getDocumentations().get(0).getSource());
        assertTrue(((SimpleContentList) result.getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentList) result.getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentList) result.getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentList) result.getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentList) result.getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleContentList) result.getInheritance()).getBaseSimpleType().getFinalModifiers());
        assertEquals(null, ((SimpleContentList) result.getInheritance()).getBaseSimpleType().getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentList) result.getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentList) result.getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeDifferenceGenerator for
     * a single union type.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleTypeUnion() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        simpleTypeB.setIsAnonymous(true);
        LinkedList<SymbolTableRef<Type>> memberTypes = new LinkedList<SymbolTableRef<Type>>();
        memberTypes.add(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        memberTypes.add(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        SimpleContentUnion simpleContentUnion = new SimpleContentUnion(memberTypes);
        SimpleType simpleTypeC = new SimpleType("{A}simpleTypeA", simpleContentUnion);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeC, new XSDSchema("A"));
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null);

        String typeName = "{A}simpleTypeC";
        SimpleType result = instance.generateNewSimpleType(simpleTypeC, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getFinalModifiers());
        assertTrue(result.getInheritance() instanceof SimpleContentUnion);
        assertEquals(null, ((SimpleContentUnion) result.getInheritance()).getId());
        assertEquals(null, ((SimpleContentUnion) result.getInheritance()).getAnnotation());
        assertTrue(((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().size() == 2);
        assertTrue(((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getName());
        assertEquals(false, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).isAnonymous());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getId());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getName()).getReference()));
        assertTrue(((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference() instanceof SimpleType);
        assertEquals("{A}difference-type.simpleTypeB-null", ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getName());
        assertEquals(true, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).isAnonymous());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getId());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getName()).getReference()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeDifferenceGenerator for
     * a single union type with ID.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleTypeUnionID() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        simpleTypeB.setIsAnonymous(true);
        LinkedList<SymbolTableRef<Type>> memberTypes = new LinkedList<SymbolTableRef<Type>>();
        memberTypes.add(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        memberTypes.add(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        SimpleContentUnion simpleContentUnion = new SimpleContentUnion(memberTypes);
        simpleContentUnion.setId("id");
        SimpleType simpleTypeC = new SimpleType("{A}simpleTypeA", simpleContentUnion);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeC, new XSDSchema("A"));
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, usedIDs, typeOldSchemaMap, null, null);

        String typeName = "{A}simpleTypeC";
        SimpleType result = instance.generateNewSimpleType(simpleTypeC, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getFinalModifiers());
        assertTrue(result.getInheritance() instanceof SimpleContentUnion);
        assertEquals("id.1", ((SimpleContentUnion) result.getInheritance()).getId());
        assertEquals(null, ((SimpleContentUnion) result.getInheritance()).getAnnotation());
        assertTrue(((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().size() == 2);
        assertTrue(((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getName());
        assertEquals(false, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).isAnonymous());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getId());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getName()).getReference()));
        assertTrue(((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference() instanceof SimpleType);
        assertEquals("{A}difference-type.simpleTypeB-null", ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getName());
        assertEquals(true, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).isAnonymous());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getId());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getName()).getReference()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeDifferenceGenerator for
     * a single union type with Annotation.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleTypeUnionAnnotation() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        simpleTypeB.setIsAnonymous(true);
        LinkedList<SymbolTableRef<Type>> memberTypes = new LinkedList<SymbolTableRef<Type>>();
        memberTypes.add(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        memberTypes.add(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        SimpleContentUnion simpleContentUnion = new SimpleContentUnion(memberTypes);
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        simpleContentUnion.setAnnotation(annotationA);
        SimpleType simpleTypeC = new SimpleType("{A}simpleTypeA", simpleContentUnion);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeC, new XSDSchema("A"));
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null);

        String typeName = "{A}simpleTypeC";
        SimpleType result = instance.generateNewSimpleType(simpleTypeC, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getFinalModifiers());
        assertTrue(result.getInheritance() instanceof SimpleContentUnion);
        assertEquals(null, ((SimpleContentUnion) result.getInheritance()).getId());
        assertTrue(((SimpleContentUnion) result.getInheritance()).getAnnotation().getAppInfos().size() == 1);
        assertEquals("blaA", ((SimpleContentUnion) result.getInheritance()).getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((SimpleContentUnion) result.getInheritance()).getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaaA", ((SimpleContentUnion) result.getInheritance()).getAnnotation().getDocumentations().get(0).getSource());
        assertTrue(((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().size() == 2);
        assertTrue(((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getName());
        assertEquals(false, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).isAnonymous());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getId());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getName()).getReference()));
        assertTrue(((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference() instanceof SimpleType);
        assertEquals("{A}difference-type.simpleTypeB-null", ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getName());
        assertEquals(true, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).isAnonymous());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getId());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getName()).getReference()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeDifferenceGenerator for
     * a single restriction type.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleTypeRestriction() {
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", simpleContentRestriction);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null);

        String typeName = "{A}simpleTypeA";
        SimpleType result = instance.generateNewSimpleType(simpleTypeA, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getFinalModifiers());
        assertTrue(result.getInheritance() instanceof SimpleContentRestriction);
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getId());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getAnnotation());
        assertEquals(new LinkedList(), ((SimpleContentRestriction) result.getInheritance()).getEnumeration());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getFractionDigits());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getLength());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getMaxExclusive());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getMaxInclusive());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getMaxLength());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getMinExclusive());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getMinInclusive());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getMinLength());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getPattern());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getTotalDigits());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getWhitespace());
        assertTrue(((SimpleContentRestriction) result.getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) result.getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentRestriction) result.getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) result.getInheritance()).getBase()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) result.getInheritance()).getBase()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentRestriction) result.getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentRestriction) result.getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeDifferenceGenerator for
     * a single restriction type with facetts.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleTypeRestrictionFacetts() {
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        LinkedList<String> enumeration = new LinkedList<String>();
        enumeration.add("foo");
        simpleContentRestriction.addEnumeration(enumeration);
        SimpleContentFixableRestrictionProperty<Integer> facetInt = new SimpleContentFixableRestrictionProperty<Integer>(2, false);
        SimpleContentFixableRestrictionProperty<String> facetString = new SimpleContentFixableRestrictionProperty<String>("2", false);
        simpleContentRestriction.setFractionDigits(facetInt);
        simpleContentRestriction.setLength(facetInt);
        simpleContentRestriction.setMaxExclusive(facetString);
        simpleContentRestriction.setMaxInclusive(facetString);
        simpleContentRestriction.setMaxLength(facetInt);
        simpleContentRestriction.setMinExclusive(facetString);
        simpleContentRestriction.setMinInclusive(facetString);
        simpleContentRestriction.setMinLength(facetInt);
        simpleContentRestriction.setPattern(facetString);
        simpleContentRestriction.setTotalDigits(facetInt);
        simpleContentRestriction.setWhitespace(new SimpleContentFixableRestrictionProperty<SimpleContentPropertyWhitespace>(SimpleContentPropertyWhitespace.Collapse, false));
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", simpleContentRestriction);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null);

        String typeName = "{A}simpleTypeA";
        SimpleType result = instance.generateNewSimpleType(simpleTypeA, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getFinalModifiers());
        assertTrue(result.getInheritance() instanceof SimpleContentRestriction);
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getId());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getAnnotation());
        assertEquals(enumeration, ((SimpleContentRestriction) result.getInheritance()).getEnumeration());
        assertEquals(facetInt.getValue(), ((SimpleContentRestriction) result.getInheritance()).getFractionDigits().getValue());
        assertEquals(facetInt.getValue(), ((SimpleContentRestriction) result.getInheritance()).getLength().getValue());
        assertEquals(facetString.getValue(), ((SimpleContentRestriction) result.getInheritance()).getMaxExclusive().getValue());
        assertEquals(facetString.getValue(), ((SimpleContentRestriction) result.getInheritance()).getMaxInclusive().getValue());
        assertEquals(facetInt.getValue(), ((SimpleContentRestriction) result.getInheritance()).getMaxLength().getValue());
        assertEquals(facetString.getValue(), ((SimpleContentRestriction) result.getInheritance()).getMinExclusive().getValue());
        assertEquals(facetString.getValue(), ((SimpleContentRestriction) result.getInheritance()).getMinInclusive().getValue());
        assertEquals(facetInt.getValue(), ((SimpleContentRestriction) result.getInheritance()).getMinLength().getValue());
        assertEquals(facetString.getValue(), ((SimpleContentRestriction) result.getInheritance()).getPattern().getValue());
        assertEquals(facetInt.getValue(), ((SimpleContentRestriction) result.getInheritance()).getTotalDigits().getValue());
        assertEquals(SimpleContentPropertyWhitespace.Collapse, ((SimpleContentRestriction) result.getInheritance()).getWhitespace().getValue());
        assertTrue(((SimpleContentRestriction) result.getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) result.getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentRestriction) result.getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) result.getInheritance()).getBase()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) result.getInheritance()).getBase()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentRestriction) result.getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentRestriction) result.getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeDifferenceGenerator for
     * a single restriction type with ID.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleTypeRestrictionID() {
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        simpleContentRestriction.setId("id");
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", simpleContentRestriction);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, usedIDs, typeOldSchemaMap, null, null);

        String typeName = "{A}simpleTypeA";
        SimpleType result = instance.generateNewSimpleType(simpleTypeA, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getFinalModifiers());
        assertTrue(result.getInheritance() instanceof SimpleContentRestriction);
        assertEquals("id.1", ((SimpleContentRestriction) result.getInheritance()).getId());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getAnnotation());
        assertEquals(new LinkedList(), ((SimpleContentRestriction) result.getInheritance()).getEnumeration());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getFractionDigits());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getLength());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getMaxExclusive());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getMaxInclusive());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getMaxLength());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getMinExclusive());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getMinInclusive());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getMinLength());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getPattern());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getTotalDigits());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getWhitespace());
        assertTrue(((SimpleContentRestriction) result.getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) result.getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentRestriction) result.getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) result.getInheritance()).getBase()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) result.getInheritance()).getBase()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentRestriction) result.getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentRestriction) result.getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeDifferenceGenerator for
     * a single restriction type with Annotation.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleTypeRestrictionAnnotation() {
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        simpleContentRestriction.setAnnotation(annotationA);

        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", simpleContentRestriction);
        simpleTypes.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null);

        String typeName = "{A}simpleTypeA";
        SimpleType result = instance.generateNewSimpleType(simpleTypeA, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getFinalModifiers());
        assertTrue(result.getInheritance() instanceof SimpleContentRestriction);
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getId());
        assertTrue(((SimpleContentRestriction) result.getInheritance()).getAnnotation().getAppInfos().size() == 1);
        assertEquals("blaA", ((SimpleContentRestriction) result.getInheritance()).getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((SimpleContentRestriction) result.getInheritance()).getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaaA", ((SimpleContentRestriction) result.getInheritance()).getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(new LinkedList(), ((SimpleContentRestriction) result.getInheritance()).getEnumeration());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getFractionDigits());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getLength());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getMaxExclusive());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getMaxInclusive());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getMaxLength());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getMinExclusive());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getMinInclusive());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getMinLength());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getPattern());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getTotalDigits());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getWhitespace());
        assertTrue(((SimpleContentRestriction) result.getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) result.getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentRestriction) result.getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentRestriction) result.getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) result.getInheritance()).getBase()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) result.getInheritance()).getBase()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentRestriction) result.getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentRestriction) result.getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend simpleType and a subtrahend complexType.
     */
    @Test
    public void testGenerateNewTypeSimpleTypeMinusComplexTypeSimpleContentWithoutOptionalAttributeID() {
        SimpleType minuendType = new SimpleType("{A}simpleTypeA", null);
        minuendType.setId("id");

        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        Attribute attributeB = new Attribute("{A}AttributeB");
        attributeB.setUse(AttributeUse.Required);
        subtrahendType.addAttribute(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, usedIDs, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof SimpleType);
        assertEquals("{A}complexTypeA", ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals("id.1", ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertEquals(null, ((SimpleType) result).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend simpleType and a subtrahend complexType.
     */
    @Test
    public void testGenerateNewTypeSimpleTypeMinusComplexTypeSimpleContentWithoutOptionalAttributeAnnotation() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeA));
        SimpleType minuendType = new SimpleType("{A}simpleTypeA", simpleContentRestriction);
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        minuendType.setAnnotation(annotationA);

        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        Attribute attributeB = new Attribute("{A}AttributeB");
        attributeB.setUse(AttributeUse.Required);
        subtrahendType.addAttribute(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof SimpleType);
        assertEquals("{A}complexTypeA", ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertTrue(((SimpleType) result).getAnnotation().getAppInfos().size() == 1);
        assertEquals("blaA", ((SimpleType) result).getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((SimpleType) result).getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaaA", ((SimpleType) result).getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentRestriction);
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getAnnotation());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getFractionDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getPattern());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getTotalDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getWhitespace());
        assertTrue(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend simpleType and a subtrahend complexType.
     */
    @Test
    public void testGenerateNewTypeSimpleTypeMinusComplexTypeSimpleContentWithoutOptionalAttributeAnonymousType() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeA));
        SimpleType minuendType = new SimpleType("{A}simpleTypeA", simpleContentRestriction);
        minuendType.setIsAnonymous(true);

        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        Attribute attributeB = new Attribute("{A}AttributeB");
        attributeB.setUse(AttributeUse.Required);
        subtrahendType.addAttribute(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof SimpleType);
        assertEquals("{A}complexTypeA", ((SimpleType) result).getName());
        assertEquals(true, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentRestriction);
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getAnnotation());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getFractionDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getPattern());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getTotalDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getWhitespace());
        assertTrue(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend simpleType and a subtrahend complexType.
     */
    @Test
    public void testGenerateNewTypeSimpleTypeMinusComplexTypeSimpleContentWithoutOptionalAttributeAnonymousTypeOtherSchema() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeA));
        SimpleType minuendType = new SimpleType("{}simpleTypeA", simpleContentRestriction);
        minuendType.setIsAnonymous(true);

        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        Attribute attributeB = new Attribute("{A}AttributeB");
        attributeB.setUse(AttributeUse.Required);
        subtrahendType.addAttribute(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema(""));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof SimpleType);
        assertEquals("{A}complexTypeA", ((SimpleType) result).getName());
        assertEquals(true, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentRestriction);
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getAnnotation());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getFractionDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getPattern());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getTotalDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getWhitespace());
        assertTrue(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend simpleType and a subtrahend complexType.
     */
    @Test
    public void testGenerateNewTypeSimpleTypeMinusComplexTypeSimpleContentWithoutOptionalAttributeFinalList() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeA));
        SimpleType minuendType = new SimpleType("{A}simpleTypeA", simpleContentRestriction);
        minuendType.addFinalModifier(SimpleTypeInheritanceModifier.List);

        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        Attribute attributeB = new Attribute("{A}AttributeB");
        attributeB.setUse(AttributeUse.Required);
        subtrahendType.addAttribute(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof SimpleType);
        assertEquals("{A}complexTypeA", ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        HashSet<SimpleTypeInheritanceModifier> finals = new HashSet<SimpleTypeInheritanceModifier>();
        finals.add(SimpleTypeInheritanceModifier.List);
        assertEquals(finals, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentRestriction);
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getAnnotation());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getFractionDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getPattern());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getTotalDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getWhitespace());
        assertTrue(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend simpleType and a subtrahend complexType.
     */
    @Test
    public void testGenerateNewTypeSimpleTypeMinusComplexTypeSimpleContentWithoutOptionalAttributeFinalRestriction() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeA));
        SimpleType minuendType = new SimpleType("{A}simpleTypeA", simpleContentRestriction);
        minuendType.addFinalModifier(SimpleTypeInheritanceModifier.Restriction);

        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        Attribute attributeB = new Attribute("{A}AttributeB");
        attributeB.setUse(AttributeUse.Required);
        subtrahendType.addAttribute(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof SimpleType);
        assertEquals("{A}complexTypeA", ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        HashSet<SimpleTypeInheritanceModifier> finals = new HashSet<SimpleTypeInheritanceModifier>();
        finals.add(SimpleTypeInheritanceModifier.Restriction);
        assertEquals(finals, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentRestriction);
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getAnnotation());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getFractionDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getPattern());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getTotalDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getWhitespace());
        assertTrue(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend simpleType and a subtrahend complexType.
     */
    @Test
    public void testGenerateNewTypeSimpleTypeMinusComplexTypeSimpleContentWithoutOptionalAttributeFinalUnion() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeA));
        SimpleType minuendType = new SimpleType("{A}simpleTypeA", simpleContentRestriction);
        minuendType.addFinalModifier(SimpleTypeInheritanceModifier.Union);

        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        Attribute attributeB = new Attribute("{A}AttributeB");
        attributeB.setUse(AttributeUse.Required);
        subtrahendType.addAttribute(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof SimpleType);
        assertEquals("{A}complexTypeA", ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        HashSet<SimpleTypeInheritanceModifier> finals = new HashSet<SimpleTypeInheritanceModifier>();
        finals.add(SimpleTypeInheritanceModifier.Union);
        assertEquals(finals, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentRestriction);
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getAnnotation());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getFractionDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getPattern());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getTotalDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getWhitespace());
        assertTrue(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend simpleType and a subtrahend complexType.
     */
    @Test
    public void testGenerateNewTypeSimpleTypeMinusComplexTypeSimpleContentWithoutOptionalAttributeFinalDefault() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeA));
        SimpleType minuendType = new SimpleType("{A}simpleTypeA", simpleContentRestriction);

        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        Attribute attributeB = new Attribute("{A}AttributeB");
        attributeB.setUse(AttributeUse.Required);
        subtrahendType.addAttribute(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        outputSchema.addFinalDefault(XSDSchema.FinalDefault.union);
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.union);
        typeOldSchemaMap.put(minuendType, oldSchema);
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof SimpleType);
        assertEquals("{A}complexTypeA", ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentRestriction);
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getAnnotation());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getFractionDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getPattern());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getTotalDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getWhitespace());
        assertTrue(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend simpleType and a subtrahend complexType.
     */
    @Test
    public void testGenerateNewTypeSimpleTypeMinusComplexTypeSimpleContentWithoutOptionalAttributeList() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentList simpleContentList = new SimpleContentList(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeA));
        SimpleType minuendType = new SimpleType("{A}simpleTypeA", simpleContentList);

        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        Attribute attributeB = new Attribute("{A}AttributeB");
        attributeB.setUse(AttributeUse.Required);
        subtrahendType.addAttribute(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals("{A}complexTypeA", ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentList);
        assertEquals(null, ((SimpleContentList) ((SimpleType) result).getInheritance()).getId());
        assertEquals(null, ((SimpleContentList) ((SimpleType) result).getInheritance()).getAnnotation());
        assertTrue(((SimpleContentList) ((SimpleType) result).getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentList) ((SimpleType) result).getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentList) ((SimpleType) result).getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentList) ((SimpleType) result).getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentList) ((SimpleType) result).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleContentList) ((SimpleType) result).getInheritance()).getBaseSimpleType().getFinalModifiers());
        assertEquals(null, ((SimpleContentList) ((SimpleType) result).getInheritance()).getBaseSimpleType().getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentList) ((SimpleType) result).getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentList) ((SimpleType) result).getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend simpleType and a subtrahend complexType.
     */
    @Test
    public void testGenerateNewTypeSimpleTypeMinusComplexTypeSimpleContentWithoutOptionalAttributeListID() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentList simpleContentList = new SimpleContentList(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeA));
        SimpleType minuendType = new SimpleType("{A}simpleTypeA", simpleContentList);
        simpleContentList.setId("id");

        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        Attribute attributeB = new Attribute("{A}AttributeB");
        attributeB.setUse(AttributeUse.Required);
        subtrahendType.addAttribute(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, usedIDs, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals("{A}complexTypeA", ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentList);
        assertEquals("id.1", ((SimpleContentList) ((SimpleType) result).getInheritance()).getId());
        assertEquals(null, ((SimpleContentList) ((SimpleType) result).getInheritance()).getAnnotation());
        assertTrue(((SimpleContentList) ((SimpleType) result).getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentList) ((SimpleType) result).getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentList) ((SimpleType) result).getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentList) ((SimpleType) result).getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentList) ((SimpleType) result).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleContentList) ((SimpleType) result).getInheritance()).getBaseSimpleType().getFinalModifiers());
        assertEquals(null, ((SimpleContentList) ((SimpleType) result).getInheritance()).getBaseSimpleType().getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentList) ((SimpleType) result).getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentList) ((SimpleType) result).getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend simpleType and a subtrahend complexType.
     */
    @Test
    public void testGenerateNewTypeSimpleTypeMinusComplexTypeSimpleContentWithoutOptionalAttributeListAnnotation() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentList simpleContentList = new SimpleContentList(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeA));
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        simpleContentList.setAnnotation(annotationA);
        SimpleType minuendType = new SimpleType("{A}simpleTypeA", simpleContentList);

        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        Attribute attributeB = new Attribute("{A}AttributeB");
        attributeB.setUse(AttributeUse.Required);
        subtrahendType.addAttribute(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals("{A}complexTypeA", ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentList);
        assertEquals(null, ((SimpleContentList) ((SimpleType) result).getInheritance()).getId());
        assertTrue(((SimpleContentList) ((SimpleType) result).getInheritance()).getAnnotation().getAppInfos().size() == 1);
        assertEquals("blaA", ((SimpleContentList) ((SimpleType) result).getInheritance()).getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((SimpleContentList) ((SimpleType) result).getInheritance()).getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaaA", ((SimpleContentList) ((SimpleType) result).getInheritance()).getAnnotation().getDocumentations().get(0).getSource());
        assertTrue(((SimpleContentList) ((SimpleType) result).getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentList) ((SimpleType) result).getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentList) ((SimpleType) result).getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentList) ((SimpleType) result).getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentList) ((SimpleType) result).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleContentList) ((SimpleType) result).getInheritance()).getBaseSimpleType().getFinalModifiers());
        assertEquals(null, ((SimpleContentList) ((SimpleType) result).getInheritance()).getBaseSimpleType().getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentList) ((SimpleType) result).getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentList) ((SimpleType) result).getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend simpleType and a subtrahend complexType.
     */
    @Test
    public void testGenerateNewTypeSimpleTypeMinusComplexTypeSimpleContentWithoutOptionalAttributeUnion() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        simpleTypeB.setIsAnonymous(true);
        LinkedList<SymbolTableRef<Type>> memberTypes = new LinkedList<SymbolTableRef<Type>>();
        memberTypes.add(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        memberTypes.add(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        SimpleContentUnion simpleContentUnion = new SimpleContentUnion(memberTypes);
        SimpleType minuendType = new SimpleType("{A}simpleTypeA", simpleContentUnion);

        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeC = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeC));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        Attribute attributeB = new Attribute("{A}AttributeB");
        attributeB.setUse(AttributeUse.Required);
        subtrahendType.addAttribute(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeC, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals("{A}complexTypeA", ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertEquals(null, ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getId());
        assertEquals(null, ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAnnotation());
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().size() == 2);
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getName());
        assertEquals(false, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).isAnonymous());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getId());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getName()).getReference()));
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference() instanceof SimpleType);
        assertEquals("{A}difference-type.simpleTypeB-null", ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference()).getName());
        assertEquals(true, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference()).isAnonymous());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference()).getId());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference()).getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference()).getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference()).getName()).getReference()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend simpleType and a subtrahend complexType.
     */
    @Test
    public void testGenerateNewTypeSimpleTypeMinusComplexTypeSimpleContentWithoutOptionalAttributeUnionID() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        simpleTypeB.setIsAnonymous(true);
        LinkedList<SymbolTableRef<Type>> memberTypes = new LinkedList<SymbolTableRef<Type>>();
        memberTypes.add(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        memberTypes.add(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        SimpleContentUnion simpleContentUnion = new SimpleContentUnion(memberTypes);
        simpleContentUnion.setId("id");
        SimpleType minuendType = new SimpleType("{A}simpleTypeA", simpleContentUnion);

        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeC = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeC));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        Attribute attributeB = new Attribute("{A}AttributeB");
        attributeB.setUse(AttributeUse.Required);
        subtrahendType.addAttribute(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeC, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, usedIDs, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals("{A}complexTypeA", ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertEquals("id.1", ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getId());
        assertEquals(null, ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAnnotation());
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().size() == 2);
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getName());
        assertEquals(false, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).isAnonymous());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getId());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getName()).getReference()));
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference() instanceof SimpleType);
        assertEquals("{A}difference-type.simpleTypeB-null", ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference()).getName());
        assertEquals(true, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference()).isAnonymous());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference()).getId());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference()).getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference()).getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference()).getName()).getReference()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend simpleType and a subtrahend complexType.
     */
    @Test
    public void testGenerateNewTypeSimpleTypeMinusComplexTypeSimpleContentWithoutOptionalAttributeUnionAnnotation() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        simpleTypeB.setIsAnonymous(true);
        LinkedList<SymbolTableRef<Type>> memberTypes = new LinkedList<SymbolTableRef<Type>>();
        memberTypes.add(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        memberTypes.add(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        SimpleContentUnion simpleContentUnion = new SimpleContentUnion(memberTypes);
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        simpleContentUnion.setAnnotation(annotationA);
        SimpleType minuendType = new SimpleType("{A}simpleTypeA", simpleContentUnion);

        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeC = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeC));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        Attribute attributeB = new Attribute("{A}AttributeB");
        attributeB.setUse(AttributeUse.Required);
        subtrahendType.addAttribute(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeC, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals("{A}complexTypeA", ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertEquals(null, ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getId());
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAnnotation().getAppInfos().size() == 1);
        assertEquals("blaA", ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaaA", ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAnnotation().getDocumentations().get(0).getSource());
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().size() == 2);
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getName());
        assertEquals(false, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).isAnonymous());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getId());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getName()).getReference()));
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference() instanceof SimpleType);
        assertEquals("{A}difference-type.simpleTypeB-null", ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference()).getName());
        assertEquals(true, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference()).isAnonymous());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference()).getId());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference()).getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference()).getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference()).getName()).getReference()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend simpleType and a subtrahend complexType.
     */
    @Test
    public void testGenerateNewTypeSimpleTypeMinusComplexTypeSimpleContentWithoutOptionalAttributeRestriction() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeA));
        SimpleType minuendType = new SimpleType("{A}simpleTypeA", simpleContentRestriction);

        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        Attribute attributeB = new Attribute("{A}AttributeB");
        attributeB.setUse(AttributeUse.Required);
        subtrahendType.addAttribute(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals("{A}complexTypeA", ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentRestriction);
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getAnnotation());
        assertEquals(new LinkedList(), ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getEnumeration());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getFractionDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getPattern());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getTotalDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getWhitespace());
        assertTrue(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend simpleType and a subtrahend complexType.
     */
    @Test
    public void testGenerateNewTypeSimpleTypeMinusComplexTypeSimpleContentWithoutOptionalAttributeRestrictionFacetts() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeA));
        LinkedList<String> enumeration = new LinkedList<String>();
        enumeration.add("foo");
        simpleContentRestriction.addEnumeration(enumeration);
        SimpleContentFixableRestrictionProperty<Integer> facetInt = new SimpleContentFixableRestrictionProperty<Integer>(2, false);
        SimpleContentFixableRestrictionProperty<String> facetString = new SimpleContentFixableRestrictionProperty<String>("2", false);
        simpleContentRestriction.setFractionDigits(facetInt);
        simpleContentRestriction.setLength(facetInt);
        simpleContentRestriction.setMaxExclusive(facetString);
        simpleContentRestriction.setMaxInclusive(facetString);
        simpleContentRestriction.setMaxLength(facetInt);
        simpleContentRestriction.setMinExclusive(facetString);
        simpleContentRestriction.setMinInclusive(facetString);
        simpleContentRestriction.setMinLength(facetInt);
        simpleContentRestriction.setPattern(facetString);
        simpleContentRestriction.setTotalDigits(facetInt);
        simpleContentRestriction.setWhitespace(new SimpleContentFixableRestrictionProperty<SimpleContentPropertyWhitespace>(SimpleContentPropertyWhitespace.Collapse, false));
        SimpleType minuendType = new SimpleType("{A}simpleTypeA", simpleContentRestriction);

        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        Attribute attributeB = new Attribute("{A}AttributeB");
        attributeB.setUse(AttributeUse.Required);
        subtrahendType.addAttribute(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals("{A}complexTypeA", ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentRestriction);
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getAnnotation());
        assertEquals(enumeration, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getEnumeration());
        assertEquals(facetInt.getValue(), ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getFractionDigits().getValue());
        assertEquals(facetInt.getValue(), ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getLength().getValue());
        assertEquals(facetString.getValue(), ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxExclusive().getValue());
        assertEquals(facetString.getValue(), ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxInclusive().getValue());
        assertEquals(facetInt.getValue(), ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxLength().getValue());
        assertEquals(facetString.getValue(), ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinExclusive().getValue());
        assertEquals(facetString.getValue(), ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinInclusive().getValue());
        assertEquals(facetInt.getValue(), ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinLength().getValue());
        assertEquals(facetString.getValue(), ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getPattern().getValue());
        assertEquals(facetInt.getValue(), ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getTotalDigits().getValue());
        assertEquals(SimpleContentPropertyWhitespace.Collapse, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getWhitespace().getValue());
        assertTrue(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend simpleType and a subtrahend complexType.
     */
    @Test
    public void testGenerateNewTypeSimpleTypeMinusComplexTypeSimpleContentWithoutOptionalAttributeRestrictionID() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeA));
        simpleContentRestriction.setId("id");
        SimpleType minuendType = new SimpleType("{A}simpleTypeA", simpleContentRestriction);

        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        Attribute attributeB = new Attribute("{A}AttributeB");
        attributeB.setUse(AttributeUse.Required);
        subtrahendType.addAttribute(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, usedIDs, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals("{A}complexTypeA", ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentRestriction);
        assertEquals("id.1", ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getAnnotation());
        assertEquals(new LinkedList(), ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getEnumeration());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getFractionDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getPattern());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getTotalDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getWhitespace());
        assertTrue(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend simpleType and a subtrahend complexType.
     */
    @Test
    public void testGenerateNewTypeSimpleTypeMinusComplexTypeSimpleContentWithoutOptionalAttributeRestrictionAnnotation() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeA));
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        simpleContentRestriction.setAnnotation(annotationA);
        SimpleType minuendType = new SimpleType("{A}simpleTypeA", simpleContentRestriction);

        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        Attribute attributeB = new Attribute("{A}AttributeB");
        attributeB.setUse(AttributeUse.Required);
        subtrahendType.addAttribute(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals("{A}complexTypeA", ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentRestriction);
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getId());
        assertTrue(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getAnnotation().getAppInfos().size() == 1);
        assertEquals("blaA", ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaaA", ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(new LinkedList(), ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getEnumeration());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getFractionDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getPattern());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getTotalDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getWhitespace());
        assertTrue(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend simpleType and a subtrahend complexType.
     */
    @Test
    public void testGenerateNewTypeAnyTypeMinusComplexTypeSimpleContent() {
        SimpleType minuendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}anyType", null);

        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        Attribute attributeB = new Attribute("{A}AttributeB");
        attributeB.setUse(AttributeUse.Required);
        subtrahendType.addAttribute(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}anyType", ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertEquals(null, ((SimpleType) result).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend simpleType and a subtrahend complexType.
     */
    @Test
    public void testGenerateNewTypeSimpleTypeMinusComplexTypeSimpleContentOptionalAttribute() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeA));
        SimpleType minuendType = new SimpleType("{A}simpleTypeA", simpleContentRestriction);

        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        Attribute attributeB = new Attribute("{A}AttributeB");
        attributeB.setUse(AttributeUse.Optional);
        subtrahendType.addAttribute(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertEquals(null, result);
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend simpleType and a subtrahend complexType.
     */
    @Test
    public void testGenerateNewTypeSimpleTypeMinusComplexTypeSimpleContentWithoutOptionalAttribute() {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeA));
        SimpleType minuendType = new SimpleType("{A}simpleTypeA", simpleContentRestriction);

        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        Attribute attributeB = new Attribute("{A}AttributeB");
        attributeB.setUse(AttributeUse.Required);
        subtrahendType.addAttribute(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof SimpleType);
        assertEquals("{A}complexTypeA", ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentRestriction);
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getAnnotation());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getFractionDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getPattern());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getTotalDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getWhitespace());
        assertTrue(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend simpleType and a subtrahend mixed complexType.
     */
    @Test
    public void testGenerateNewTypeSimpleTypeMinusComplexTypeEmptyParticle() {
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        SimpleType minuendType = new SimpleType("{A}simpleTypeA", simpleContentRestriction);

        ComplexContentType complexContentTypeB = new ComplexContentType(null);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", complexContentTypeB);
        Attribute attributeB = new Attribute("{A}AttributeB");
        attributeB.setUse(AttributeUse.Optional);
        subtrahendType.addAttribute(attributeB);
        subtrahendType.setMixed(true);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertEquals(null, result);
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend simpleType and a subtrahend mixed complexType.
     */
    @Test
    public void testGenerateNewTypeSimpleTypeMinusComplexTypeEmptyChoice() {
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        SimpleType minuendType = new SimpleType("{A}simpleTypeA", simpleContentRestriction);

        ChoicePattern choicePatternB = new ChoicePattern();
        ComplexContentType complexContentTypeB = new ComplexContentType(choicePatternB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", complexContentTypeB);
        Attribute attributeB = new Attribute("{A}AttributeB");
        attributeB.setUse(AttributeUse.Optional);
        subtrahendType.addAttribute(attributeB);
        subtrahendType.setMixed(true);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertEquals(null, result);
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend simpleType and a subtrahend mixed complexType.
     */
    @Test
    public void testGenerateNewTypeSimpleTypeMinusComplexTypeWithoutOptionalAttributes() {
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        SimpleType minuendType = new SimpleType("{A}simpleTypeA", simpleContentRestriction);

        Element elementB = new Element("{A}elementB");
        CountingPattern countingPatternB = new CountingPattern(0, 1);
        countingPatternB.addParticle(elementB);
        ChoicePattern choicePatternB = new ChoicePattern();
        choicePatternB.addParticle(elementB);
        ComplexContentType complexContentTypeB = new ComplexContentType(choicePatternB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", complexContentTypeB);
        Attribute attributeB = new Attribute("{A}AttributeB");
        attributeB.setUse(AttributeUse.Required);
        subtrahendType.addAttribute(attributeB);
        subtrahendType.setMixed(true);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof SimpleType);
        assertEquals("{A}complexTypeA", ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentRestriction);
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getAnnotation());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getFractionDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getPattern());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getTotalDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getWhitespace());
        assertTrue(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend simpleType and a subtrahend non mixed complexType.
     */
    @Test
    public void testGenerateNewTypeSimpleTypeMinusComplexTypeNotMixed() {
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        SimpleType minuendType = new SimpleType("{A}simpleTypeA", simpleContentRestriction);

        Element elementB = new Element("{A}elementB");
        CountingPattern countingPatternB = new CountingPattern(0, 1);
        countingPatternB.addParticle(elementB);
        ChoicePattern choicePatternB = new ChoicePattern();
        choicePatternB.addParticle(elementB);
        ComplexContentType complexContentTypeB = new ComplexContentType(choicePatternB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", complexContentTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementB, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof SimpleType);
        assertEquals("{A}complexTypeA", ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentRestriction);
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getAnnotation());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getFractionDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMaxLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getMinLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getPattern());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getTotalDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getWhitespace());
        assertTrue(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentRestriction) ((SimpleType) result).getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend mixed complexType and a subtrahend non mixed complexType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMixedMinusComplexTypeNotMixed() {
        Element elementA = new Element("{A}elementA");
        CountingPattern countingPatternA = new CountingPattern(0, 1);
        countingPatternA.addParticle(elementA);
        ChoicePattern choicePatternA = new ChoicePattern();
        choicePatternA.addParticle(elementA);
        ComplexContentType complexContentTypeA = new ComplexContentType(choicePatternA);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentTypeA);
        minuendType.addAttribute(new Attribute("{A}attribute"));
        minuendType.setMixed(true);

        Element elementB = new Element("{A}elementB");
        CountingPattern countingPatternB = new CountingPattern(0, 1);
        countingPatternB.addParticle(elementB);
        ChoicePattern choicePatternB = new ChoicePattern();
        choicePatternB.addParticle(elementB);
        ComplexContentType complexContentTypeB = new ComplexContentType(choicePatternB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", complexContentTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(true, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend mixed complexType and a subtrahend non mixed complexType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMixedMinusComplexTypeNotMixedID() {
        Element elementA = new Element("{A}elementA");
        CountingPattern countingPatternA = new CountingPattern(0, 1);
        countingPatternA.addParticle(elementA);
        ChoicePattern choicePatternA = new ChoicePattern();
        choicePatternA.addParticle(elementA);
        ComplexContentType complexContentTypeA = new ComplexContentType(choicePatternA);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentTypeA);
        minuendType.addAttribute(new Attribute("{A}attribute"));
        minuendType.setId("id");
        minuendType.setMixed(true);

        Element elementB = new Element("{A}elementB");
        CountingPattern countingPatternB = new CountingPattern(0, 1);
        countingPatternB.addParticle(elementB);
        ChoicePattern choicePatternB = new ChoicePattern();
        choicePatternB.addParticle(elementB);
        ComplexContentType complexContentTypeB = new ComplexContentType(choicePatternB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", complexContentTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, usedIDs, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals("id.1", result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(true, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend mixed complexType and a subtrahend non mixed complexType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMixedMinusComplexTypeNotMixedAnnotation() {
        Element elementA = new Element("{A}elementA");
        CountingPattern countingPatternA = new CountingPattern(0, 1);
        countingPatternA.addParticle(elementA);
        ChoicePattern choicePatternA = new ChoicePattern();
        choicePatternA.addParticle(elementA);
        ComplexContentType complexContentTypeA = new ComplexContentType(choicePatternA);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentTypeA);
        minuendType.addAttribute(new Attribute("{A}attribute"));
        Annotation annotation = new Annotation();
        AppInfo appInfo = new AppInfo();
        appInfo.setSource("bla");
        Documentation documentation = new Documentation();
        documentation.setSource("blaa");
        annotation.addAppInfos(appInfo);
        annotation.addDocumentations(documentation);
        minuendType.setAnnotation(annotation);
        minuendType.setMixed(true);

        Element elementB = new Element("{A}elementB");
        CountingPattern countingPatternB = new CountingPattern(0, 1);
        countingPatternB.addParticle(elementB);
        ChoicePattern choicePatternB = new ChoicePattern();
        choicePatternB.addParticle(elementB);
        ComplexContentType complexContentTypeB = new ComplexContentType(choicePatternB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", complexContentTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertTrue(result.getAnnotation().getAppInfos().size() == 1);
        assertEquals("bla", result.getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(result.getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaa", result.getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(true, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend mixed complexType and a subtrahend non mixed complexType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMixedMinusComplexTypeNotMixedAbstract() {
        Element elementA = new Element("{A}elementA");
        CountingPattern countingPatternA = new CountingPattern(0, 1);
        countingPatternA.addParticle(elementA);
        ChoicePattern choicePatternA = new ChoicePattern();
        choicePatternA.addParticle(elementA);
        ComplexContentType complexContentTypeA = new ComplexContentType(choicePatternA);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentTypeA);
        minuendType.addAttribute(new Attribute("{A}attribute"));
        minuendType.setMixed(true);
        minuendType.setAbstract(true);

        Element elementB = new Element("{A}elementB");
        CountingPattern countingPatternB = new CountingPattern(0, 1);
        countingPatternB.addParticle(elementB);
        ChoicePattern choicePatternB = new ChoicePattern();
        choicePatternB.addParticle(elementB);
        ComplexContentType complexContentTypeB = new ComplexContentType(choicePatternB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", complexContentTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(true, (boolean) ((ComplexType) result).getMixed());
        assertEquals(true, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend mixed complexType and a subtrahend non mixed complexType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMixedMinusComplexTypeNotMixedAnonymous() {
        Element elementA = new Element("{A}elementA");
        CountingPattern countingPatternA = new CountingPattern(0, 1);
        countingPatternA.addParticle(elementA);
        ChoicePattern choicePatternA = new ChoicePattern();
        choicePatternA.addParticle(elementA);
        ComplexContentType complexContentTypeA = new ComplexContentType(choicePatternA);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentTypeA);
        minuendType.addAttribute(new Attribute("{A}attribute"));
        minuendType.setMixed(true);
        minuendType.setIsAnonymous(true);

        Element elementB = new Element("{A}elementB");
        CountingPattern countingPatternB = new CountingPattern(0, 1);
        countingPatternB.addParticle(elementB);
        ChoicePattern choicePatternB = new ChoicePattern();
        choicePatternB.addParticle(elementB);
        ComplexContentType complexContentTypeB = new ComplexContentType(choicePatternB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", complexContentTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(true, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(true, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend mixed complexType and a subtrahend non mixed complexType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMixedMinusComplexTypeNotMixedBlockExtension() {
        Element elementA = new Element("{A}elementA");
        CountingPattern countingPatternA = new CountingPattern(0, 1);
        countingPatternA.addParticle(elementA);
        ChoicePattern choicePatternA = new ChoicePattern();
        choicePatternA.addParticle(elementA);
        ComplexContentType complexContentTypeA = new ComplexContentType(choicePatternA);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentTypeA);
        minuendType.addAttribute(new Attribute("{A}attribute"));
        minuendType.setMixed(true);
        minuendType.addBlockModifier(ComplexTypeInheritanceModifier.Extension);

        Element elementB = new Element("{A}elementB");
        CountingPattern countingPatternB = new CountingPattern(0, 1);
        countingPatternB.addParticle(elementB);
        ChoicePattern choicePatternB = new ChoicePattern();
        choicePatternB.addParticle(elementB);
        ComplexContentType complexContentTypeB = new ComplexContentType(choicePatternB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", complexContentTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(true, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        HashSet<ComplexTypeInheritanceModifier> blocks = new HashSet<ComplexTypeInheritanceModifier>();
        blocks.add(ComplexTypeInheritanceModifier.Extension);
        assertEquals(blocks, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend mixed complexType and a subtrahend non mixed complexType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMixedMinusComplexTypeNotMixedBlockRestriction() {
        Element elementA = new Element("{A}elementA");
        CountingPattern countingPatternA = new CountingPattern(0, 1);
        countingPatternA.addParticle(elementA);
        ChoicePattern choicePatternA = new ChoicePattern();
        choicePatternA.addParticle(elementA);
        ComplexContentType complexContentTypeA = new ComplexContentType(choicePatternA);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentTypeA);
        minuendType.addAttribute(new Attribute("{A}attribute"));
        minuendType.setMixed(true);
        minuendType.addBlockModifier(ComplexTypeInheritanceModifier.Restriction);

        Element elementB = new Element("{A}elementB");
        CountingPattern countingPatternB = new CountingPattern(0, 1);
        countingPatternB.addParticle(elementB);
        ChoicePattern choicePatternB = new ChoicePattern();
        choicePatternB.addParticle(elementB);
        ComplexContentType complexContentTypeB = new ComplexContentType(choicePatternB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", complexContentTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(true, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        HashSet<ComplexTypeInheritanceModifier> blocks = new HashSet<ComplexTypeInheritanceModifier>();
        blocks.add(ComplexTypeInheritanceModifier.Restriction);
        assertEquals(blocks, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend mixed complexType and a subtrahend non mixed complexType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMixedMinusComplexTypeNotMixedBlockDefaultExtension() {
        Element elementA = new Element("{A}elementA");
        CountingPattern countingPatternA = new CountingPattern(0, 1);
        countingPatternA.addParticle(elementA);
        ChoicePattern choicePatternA = new ChoicePattern();
        choicePatternA.addParticle(elementA);
        ComplexContentType complexContentTypeA = new ComplexContentType(choicePatternA);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentTypeA);
        minuendType.addAttribute(new Attribute("{A}attribute"));
        minuendType.setMixed(true);

        Element elementB = new Element("{A}elementB");
        CountingPattern countingPatternB = new CountingPattern(0, 1);
        countingPatternB.addParticle(elementB);
        ChoicePattern choicePatternB = new ChoicePattern();
        choicePatternB.addParticle(elementB);
        ComplexContentType complexContentTypeB = new ComplexContentType(choicePatternB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", complexContentTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.extension);
        typeOldSchemaMap.put(minuendType, oldSchema);
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(true, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        HashSet<ComplexTypeInheritanceModifier> blocks = new HashSet<ComplexTypeInheritanceModifier>();
        blocks.add(ComplexTypeInheritanceModifier.Extension);
        assertEquals(blocks, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend mixed complexType and a subtrahend non mixed complexType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMixedMinusComplexTypeNotMixedBlockDefaultRestriction() {
        Element elementA = new Element("{A}elementA");
        CountingPattern countingPatternA = new CountingPattern(0, 1);
        countingPatternA.addParticle(elementA);
        ChoicePattern choicePatternA = new ChoicePattern();
        choicePatternA.addParticle(elementA);
        ComplexContentType complexContentTypeA = new ComplexContentType(choicePatternA);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentTypeA);
        minuendType.addAttribute(new Attribute("{A}attribute"));
        minuendType.setMixed(true);

        Element elementB = new Element("{A}elementB");
        CountingPattern countingPatternB = new CountingPattern(0, 1);
        countingPatternB.addParticle(elementB);
        ChoicePattern choicePatternB = new ChoicePattern();
        choicePatternB.addParticle(elementB);
        ComplexContentType complexContentTypeB = new ComplexContentType(choicePatternB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", complexContentTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.restriction);
        typeOldSchemaMap.put(minuendType, oldSchema);
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(true, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        HashSet<ComplexTypeInheritanceModifier> blocks = new HashSet<ComplexTypeInheritanceModifier>();
        blocks.add(ComplexTypeInheritanceModifier.Restriction);
        assertEquals(blocks, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend mixed complexType and a subtrahend non mixed complexType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMixedMinusComplexTypeNotMixedFinalDefaultExtension() {
        Element elementA = new Element("{A}elementA");
        CountingPattern countingPatternA = new CountingPattern(0, 1);
        countingPatternA.addParticle(elementA);
        ChoicePattern choicePatternA = new ChoicePattern();
        choicePatternA.addParticle(elementA);
        ComplexContentType complexContentTypeA = new ComplexContentType(choicePatternA);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentTypeA);
        minuendType.addAttribute(new Attribute("{A}attribute"));
        minuendType.setMixed(true);

        Element elementB = new Element("{A}elementB");
        CountingPattern countingPatternB = new CountingPattern(0, 1);
        countingPatternB.addParticle(elementB);
        ChoicePattern choicePatternB = new ChoicePattern();
        choicePatternB.addParticle(elementB);
        ComplexContentType complexContentTypeB = new ComplexContentType(choicePatternB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", complexContentTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.extension);
        typeOldSchemaMap.put(minuendType, oldSchema);
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(true, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        HashSet<ComplexTypeInheritanceModifier> finals = new HashSet<ComplexTypeInheritanceModifier>();
        finals.add(ComplexTypeInheritanceModifier.Extension);
        assertEquals(finals, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend mixed complexType and a subtrahend non mixed complexType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMixedMinusComplexTypeNotMixedFinalDefaultRestriction() {
        Element elementA = new Element("{A}elementA");
        CountingPattern countingPatternA = new CountingPattern(0, 1);
        countingPatternA.addParticle(elementA);
        ChoicePattern choicePatternA = new ChoicePattern();
        choicePatternA.addParticle(elementA);
        ComplexContentType complexContentTypeA = new ComplexContentType(choicePatternA);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentTypeA);
        minuendType.addAttribute(new Attribute("{A}attribute"));
        minuendType.setMixed(true);

        Element elementB = new Element("{A}elementB");
        CountingPattern countingPatternB = new CountingPattern(0, 1);
        countingPatternB.addParticle(elementB);
        ChoicePattern choicePatternB = new ChoicePattern();
        choicePatternB.addParticle(elementB);
        ComplexContentType complexContentTypeB = new ComplexContentType(choicePatternB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", complexContentTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        typeOldSchemaMap.put(minuendType, oldSchema);
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(true, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        HashSet<ComplexTypeInheritanceModifier> finals = new HashSet<ComplexTypeInheritanceModifier>();
        finals.add(ComplexTypeInheritanceModifier.Restriction);
        assertEquals(finals, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend mixed complexType and a subtrahend non mixed complexType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMixedMinusComplexTypeNotMixedComplexContentID() {
        Element elementA = new Element("{A}elementA");
        CountingPattern countingPatternA = new CountingPattern(0, 1);
        countingPatternA.addParticle(elementA);
        ChoicePattern choicePatternA = new ChoicePattern();
        choicePatternA.addParticle(elementA);
        ComplexContentType complexContentTypeA = new ComplexContentType(choicePatternA);
        complexContentTypeA.setId("id");
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentTypeA);
        minuendType.addAttribute(new Attribute("{A}attribute"));
        minuendType.setMixed(true);

        Element elementB = new Element("{A}elementB");
        CountingPattern countingPatternB = new CountingPattern(0, 1);
        countingPatternB.addParticle(elementB);
        ChoicePattern choicePatternB = new ChoicePattern();
        choicePatternB.addParticle(elementB);
        ComplexContentType complexContentTypeB = new ComplexContentType(choicePatternB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", complexContentTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, usedIDs, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(true, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals("id.1", ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend mixed complexType and a subtrahend non mixed complexType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMixedMinusComplexTypeNotMixedComplexContentAnnotation() {
        Element elementA = new Element("{A}elementA");
        CountingPattern countingPatternA = new CountingPattern(0, 1);
        countingPatternA.addParticle(elementA);
        ChoicePattern choicePatternA = new ChoicePattern();
        choicePatternA.addParticle(elementA);
        ComplexContentType complexContentTypeA = new ComplexContentType(choicePatternA);
        Annotation annotation = new Annotation();
        AppInfo appInfo = new AppInfo();
        appInfo.setSource("bla");
        Documentation documentation = new Documentation();
        documentation.setSource("blaa");
        annotation.addAppInfos(appInfo);
        annotation.addDocumentations(documentation);
        complexContentTypeA.setAnnotation(annotation);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentTypeA);
        minuendType.addAttribute(new Attribute("{A}attribute"));
        minuendType.setMixed(true);

        Element elementB = new Element("{A}elementB");
        CountingPattern countingPatternB = new CountingPattern(0, 1);
        countingPatternB.addParticle(elementB);
        ChoicePattern choicePatternB = new ChoicePattern();
        choicePatternB.addParticle(elementB);
        ComplexContentType complexContentTypeB = new ComplexContentType(choicePatternB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", complexContentTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(true, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertNotSame(annotation, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation().getAppInfos().size() == 1);
        assertEquals("bla", ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaa", ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend mixed complexType and a subtrahend non mixed complexType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMixedMinusComplexTypeNotMixedComplexContentMixed() {
        Element elementA = new Element("{A}elementA");
        CountingPattern countingPatternA = new CountingPattern(0, 1);
        countingPatternA.addParticle(elementA);
        ChoicePattern choicePatternA = new ChoicePattern();
        choicePatternA.addParticle(elementA);
        ComplexContentType complexContentTypeA = new ComplexContentType(choicePatternA);
        complexContentTypeA.setMixed(true);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentTypeA);
        minuendType.addAttribute(new Attribute("{A}attribute"));
        minuendType.setMixed(true);

        Element elementB = new Element("{A}elementB");
        CountingPattern countingPatternB = new CountingPattern(0, 1);
        countingPatternB.addParticle(elementB);
        ChoicePattern choicePatternB = new ChoicePattern();
        choicePatternB.addParticle(elementB);
        ComplexContentType complexContentTypeB = new ComplexContentType(choicePatternB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", complexContentTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(true, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(true, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend mixed complexType and a subtrahend non mixed complexType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMixedComplexContentEmptyParticleMinusComplexTypeSimpleContentAnySimpleType() {
        ComplexContentType complexContentTypeA = new ComplexContentType(null);
        complexContentTypeA.setMixed(true);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentTypeA);
        minuendType.addAttribute(new Attribute("{A}attribute"));

        SimpleContentType simpleContentType = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        SimpleContentExtension simpleContentExtension = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentType.setInheritance(simpleContentExtension);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeA", simpleContentType);
        simpleContentExtension.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);
        AttributeParticleDifferenceGenerator.setTypeDifferenceGenerator(instance);
        ParticleDifferenceGenerator.setTypeDifferenceGenerator(instance);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertEquals(null, result);
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend mixed complexType and a subtrahend non mixed complexType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMixedComplexContentEmptyChoiceMinusComplexTypeSimpleContentAnySimpleType() {
        ChoicePattern choicePatternA = new ChoicePattern();
        ComplexContentType complexContentTypeA = new ComplexContentType(choicePatternA);
        complexContentTypeA.setMixed(true);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentTypeA);
        Attribute attributeA = new Attribute("{A}attributeA");
        minuendType.addAttribute(attributeA);

        SimpleContentType simpleContentType = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        SimpleContentExtension simpleContentExtension = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentType.setInheritance(simpleContentExtension);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeA", simpleContentType);
        Attribute attributeB = new Attribute("{A}attributeA");
        simpleContentExtension.addAttribute(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, new XSDSchema("A"));
        attributeOldSchemaMap.put(attributeB, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);
        AttributeParticleDifferenceGenerator.setTypeDifferenceGenerator(instance);
        ParticleDifferenceGenerator.setTypeDifferenceGenerator(instance);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertEquals(null, result);
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend mixed complexType and a subtrahend non mixed complexType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeComplexContentEmptyParticleMinusComplexTypeSimpleContent() {
        ComplexContentType complexContentTypeA = new ComplexContentType(null);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentTypeA);
        minuendType.addAttribute(new Attribute("{A}attribute"));

        SimpleContentType simpleContentType = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentExtension simpleContentExtension = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentType.setInheritance(simpleContentExtension);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeA", simpleContentType);
        simpleContentExtension.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);
        AttributeParticleDifferenceGenerator.setTypeDifferenceGenerator(instance);
        ParticleDifferenceGenerator.setTypeDifferenceGenerator(instance);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertEquals(null, result);
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend mixed complexType and a subtrahend non mixed complexType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeComplexContentEmptyChoiceMinusComplexTypeSimpleContent() {
        ChoicePattern choicePatternA = new ChoicePattern();
        ComplexContentType complexContentTypeA = new ComplexContentType(choicePatternA);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentTypeA);
        Attribute attributeA = new Attribute("{A}attributeA");
        minuendType.addAttribute(attributeA);

        SimpleContentType simpleContentType = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentExtension simpleContentExtension = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentType.setInheritance(simpleContentExtension);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeA", simpleContentType);
        Attribute attributeB = new Attribute("{A}attributeA");
        simpleContentExtension.addAttribute(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, new XSDSchema("A"));
        attributeOldSchemaMap.put(attributeB, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);
        AttributeParticleDifferenceGenerator.setTypeDifferenceGenerator(instance);
        ParticleDifferenceGenerator.setTypeDifferenceGenerator(instance);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertEquals(null, result);
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend non mixed complexType and a subtrahend mixed complexType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeSimpleContentMinusMixedComplexTypeComplexContentEmptyParticle() {
        SimpleContentType simpleContentType = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        SimpleContentExtension simpleContentExtension = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentType.setInheritance(simpleContentExtension);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", simpleContentType);
        simpleContentExtension.addAttribute(new Attribute("{A}attribute"));

        ComplexContentType complexContentTypeA = new ComplexContentType(null);
        complexContentTypeA.setMixed(true);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeA", complexContentTypeA);
        subtrahendType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        LinkedHashMap<Type, LinkedList<AttributeParticle>> newTypeNewAttributesMap = new LinkedHashMap<Type, LinkedList<AttributeParticle>>();
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, newTypeNewAttributesMap, null);
        AttributeParticleDifferenceGenerator.setTypeDifferenceGenerator(instance);
        ParticleDifferenceGenerator.setTypeDifferenceGenerator(instance);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertEquals(null, result);
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend non mixed complexType and a subtrahend mixed complexType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeSimpleContentMinusMixedComplexTypeComplexContentEmptyChoice() {
        SimpleContentType simpleContentType = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        SimpleContentExtension simpleContentExtension = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentType.setInheritance(simpleContentExtension);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", simpleContentType);
        simpleContentExtension.addAttribute(new Attribute("{A}attribute"));

        ComplexContentType complexContentTypeA = new ComplexContentType(new ChoicePattern());
        complexContentTypeA.setMixed(true);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeA", complexContentTypeA);
        subtrahendType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        LinkedHashMap<Type, LinkedList<AttributeParticle>> newTypeNewAttributesMap = new LinkedHashMap<Type, LinkedList<AttributeParticle>>();
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, newTypeNewAttributesMap, null);
        AttributeParticleDifferenceGenerator.setTypeDifferenceGenerator(instance);
        ParticleDifferenceGenerator.setTypeDifferenceGenerator(instance);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertEquals(null, result);
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend mixed complexType and a subtrahend non mixed complexType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeComplexContentMinusComplexTypeNewAttributeOldParticle() {
        ChoicePattern choicePatternA = new ChoicePattern();
        ComplexContentType complexContentTypeA = new ComplexContentType(choicePatternA);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentTypeA);
        Attribute attributeA = new Attribute("{A}attributeA");
        minuendType.addAttribute(attributeA);

        ChoicePattern choicePatternB = new ChoicePattern();
        ComplexContentType complexContentTypeB = new ComplexContentType(choicePatternB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", complexContentTypeB);
        Attribute attributeB = new Attribute("{A}attributeA");
        attributeB.setUse(AttributeUse.Required);
        subtrahendType.addAttribute(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, new XSDSchema("A"));
        attributeOldSchemaMap.put(attributeB, new XSDSchema("A"));
        LinkedHashMap<Type, LinkedList<AttributeParticle>> newTypeNewAttributesMap = new LinkedHashMap<Type, LinkedList<AttributeParticle>>();
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, newTypeNewAttributesMap, null);
        AttributeParticleDifferenceGenerator.setTypeDifferenceGenerator(instance);
        ParticleDifferenceGenerator.setTypeDifferenceGenerator(instance);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals("{A}difference-type.complexTypeA-complexTypeB", result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertEquals(AttributeUse.Prohibited, ((Attribute) ((ComplexType) result).getAttributes().getFirst()).getUse());
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend mixed complexType and a subtrahend non mixed complexType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeComplexContentMinusComplexTypeOldAttributeNewParticle() {
        SequencePattern sequencePatternA = new SequencePattern();
        Element elementA = new Element("{A}elementA");
        sequencePatternA.addParticle(elementA);
        ComplexContentType complexContentTypeA = new ComplexContentType(sequencePatternA);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentTypeA);
        Attribute attributeA = new Attribute("{A}attributeA");
        minuendType.addAttribute(attributeA);

        ChoicePattern choicePatternB = new ChoicePattern();
        ComplexContentType complexContentTypeB = new ComplexContentType(choicePatternB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", complexContentTypeB);
        Attribute attributeB = new Attribute("{A}attributeA");
        subtrahendType.addAttribute(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, new XSDSchema("A"));
        attributeOldSchemaMap.put(attributeB, new XSDSchema("A"));
        LinkedHashMap<Type, LinkedList<AttributeParticle>> newTypeNewAttributesMap = new LinkedHashMap<Type, LinkedList<AttributeParticle>>();
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, newTypeNewAttributesMap, null);
        AttributeParticleDifferenceGenerator.setTypeDifferenceGenerator(instance);
        ParticleDifferenceGenerator.setTypeDifferenceGenerator(instance);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals("{A}difference-type.complexTypeA-complexTypeB", result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertEquals(AttributeUse.Optional, ((Attribute) ((ComplexType) result).getAttributes().getFirst()).getUse());
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend mixed complexType and a subtrahend non mixed complexType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeComplexContentMinusComplexTypeNewAttributeNewParticle() {
        SequencePattern sequencePatternA = new SequencePattern();
        Element elementA = new Element("{A}elementA");
        sequencePatternA.addParticle(elementA);
        ComplexContentType complexContentTypeA = new ComplexContentType(sequencePatternA);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentTypeA);
        Attribute attributeA = new Attribute("{A}attributeA");
        minuendType.addAttribute(attributeA);

        ChoicePattern choicePatternB = new ChoicePattern();
        ComplexContentType complexContentTypeB = new ComplexContentType(choicePatternB);
        ComplexType subtrahendType = new ComplexType("{A}complexTypeB", complexContentTypeB);
        Attribute attributeB = new Attribute("{A}attributeA");
        attributeB.setUse(AttributeUse.Required);
        subtrahendType.addAttribute(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        LinkedHashMap<Attribute, XSDSchema> attributeOldSchemaMap = new LinkedHashMap<Attribute, XSDSchema>();
        attributeOldSchemaMap.put(attributeA, new XSDSchema("A"));
        attributeOldSchemaMap.put(attributeB, new XSDSchema("A"));
        LinkedHashMap<Type, LinkedList<AttributeParticle>> newTypeNewAttributesMap = new LinkedHashMap<Type, LinkedList<AttributeParticle>>();
        LinkedHashSet<Type> possibleEmptyTypes = new LinkedHashSet<Type>();
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, attributeOldSchemaMap, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, newTypeNewAttributesMap, possibleEmptyTypes);
        AttributeParticleDifferenceGenerator.setTypeDifferenceGenerator(instance);
        ParticleDifferenceGenerator.setTypeDifferenceGenerator(instance);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (EmptySubsetParticleStateFieldException ex) {
            Logger.getLogger(TypeDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NonDeterministicFiniteAutomataException ex) {
            Logger.getLogger(TypeDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UniqueParticleAttributionViolationException ex) {
            Logger.getLogger(TypeDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EmptyProductParticleStateFieldException ex) {
            Logger.getLogger(TypeDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoUniqueStateNumbersException ex) {
            Logger.getLogger(TypeDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoDestinationStateFoundException ex) {
            Logger.getLogger(TypeDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotSupportedParticleAutomatonException ex) {
            Logger.getLogger(TypeDifferenceGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        assertTrue(possibleEmptyTypes.contains(result));
        assertTrue(result instanceof ComplexType);
        assertEquals("{A}difference-type.complexTypeA-complexTypeB", result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertEquals(AttributeUse.Prohibited, ((Attribute) ((ComplexType) result).getAttributes().getFirst()).getUse());
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * complexType.
     */
    @Test
    public void testGenerateNewTypeComplexType() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(oldType, null, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * complexType with ID.
     */
    @Test
    public void testGenerateNewTypeComplexTypeID() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.setId("id");
        oldType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, usedIDs, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(oldType, null, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals("id.1", result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * complexType with annotation.
     */
    @Test
    public void testGenerateNewTypeComplexTypeAnnotation() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        Annotation annotation = new Annotation();
        AppInfo appInfo = new AppInfo();
        appInfo.setSource("bla");
        Documentation documentation = new Documentation();
        documentation.setSource("blaa");
        annotation.addAppInfos(appInfo);
        annotation.addDocumentations(documentation);
        oldType.setAnnotation(annotation);
        oldType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(oldType, null, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertNotSame(annotation, result.getAnnotation());
        assertTrue(result.getAnnotation().getAppInfos().size() == 1);
        assertEquals("bla", result.getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(result.getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaa", result.getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for an
     * abstract complexType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeAbstract() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.setAbstract(true);
        oldType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(oldType, null, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(true, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for an
     * anonymous complexType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeAnonymous() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.setIsAnonymous(true);
        oldType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(oldType, null, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());
        assertEquals(true, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * mixed complexType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMixed() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.setMixed(true);
        oldType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(oldType, null, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(true, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * complexType with attributes.
     */
    @Test
    public void testGenerateNewTypeComplexTypeAttributes() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.setMixed(true);
        Attribute attributeA = new Attribute("{A}attributeA");
        Attribute attributeB = new Attribute("{A}attributeB");
        oldType.addAttribute(attributeA);
        oldType.addAttribute(attributeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(oldType, null, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(true, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 2);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertEquals(attributeA.getName(), ((Attribute) ((ComplexType) result).getAttributes().getFirst()).getName());
        assertEquals(null, ((Attribute) ((ComplexType) result).getAttributes().getFirst()).getFixed());
        assertEquals(null, ((Attribute) ((ComplexType) result).getAttributes().getFirst()).getDefault());
        assertEquals(null, ((Attribute) ((ComplexType) result).getAttributes().getFirst()).getForm());
        assertEquals(null, ((Attribute) ((ComplexType) result).getAttributes().getFirst()).getId());
        assertEquals(null, ((Attribute) ((ComplexType) result).getAttributes().getFirst()).getAnnotation());
        assertEquals(false, (boolean) ((Attribute) ((ComplexType) result).getAttributes().getFirst()).getTypeAttr());
        assertEquals(null, ((Attribute) ((ComplexType) result).getAttributes().getFirst()).getSimpleType());
        assertEquals(AttributeUse.Optional, ((Attribute) ((ComplexType) result).getAttributes().getFirst()).getUse());
        assertTrue(((ComplexType) result).getAttributes().getLast() instanceof Attribute);
        assertEquals(attributeB.getName(), ((Attribute) ((ComplexType) result).getAttributes().getLast()).getName());
        assertEquals(null, ((Attribute) ((ComplexType) result).getAttributes().getLast()).getFixed());
        assertEquals(null, ((Attribute) ((ComplexType) result).getAttributes().getLast()).getDefault());
        assertEquals(null, ((Attribute) ((ComplexType) result).getAttributes().getLast()).getForm());
        assertEquals(null, ((Attribute) ((ComplexType) result).getAttributes().getLast()).getId());
        assertEquals(null, ((Attribute) ((ComplexType) result).getAttributes().getLast()).getAnnotation());
        assertEquals(false, (boolean) ((Attribute) ((ComplexType) result).getAttributes().getLast()).getTypeAttr());
        assertEquals(null, ((Attribute) ((ComplexType) result).getAttributes().getLast()).getSimpleType());
        assertEquals(AttributeUse.Optional, ((Attribute) ((ComplexType) result).getAttributes().getLast()).getUse());
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * complexType which blocks extension.
     */
    @Test
    public void testGenerateNewTypeComplexTypeBlockExtension() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        HashSet<ComplexTypeInheritanceModifier> complexTypeInheritanceModifierSet = new HashSet<ComplexTypeInheritanceModifier>();
        complexTypeInheritanceModifierSet.add(ComplexTypeInheritanceModifier.Extension);
        oldType.setBlockModifiers(complexTypeInheritanceModifierSet);
        oldType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(oldType, null, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        HashSet<ComplexTypeInheritanceModifier> blocks = new HashSet<ComplexTypeInheritanceModifier>();
        blocks.add(ComplexTypeInheritanceModifier.Extension);
        assertEquals(blocks, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * complexType which blocks restriction.
     */
    @Test
    public void testGenerateNewTypeComplexTypeBlockRestriction() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        HashSet<ComplexTypeInheritanceModifier> complexTypeInheritanceModifierSet = new HashSet<ComplexTypeInheritanceModifier>();
        complexTypeInheritanceModifierSet.add(ComplexTypeInheritanceModifier.Restriction);
        oldType.setBlockModifiers(complexTypeInheritanceModifierSet);
        oldType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(oldType, null, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        HashSet<ComplexTypeInheritanceModifier> blocks = new HashSet<ComplexTypeInheritanceModifier>();
        blocks.add(ComplexTypeInheritanceModifier.Restriction);
        assertEquals(blocks, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * complexType which blocks extension per schema default.
     */
    @Test
    public void testGenerateNewTypeComplexTypeBlockDefaultExtension() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.extension);
        typeOldSchemaMap.put(oldType, oldSchema);
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(oldType, null, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        HashSet<ComplexTypeInheritanceModifier> blocks = new HashSet<ComplexTypeInheritanceModifier>();
        blocks.add(ComplexTypeInheritanceModifier.Extension);
        assertEquals(blocks, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * complexType which blocks restriction per schema default.
     */
    @Test
    public void testGenerateNewTypeComplexTypeBlockDefaultRestriction() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.restriction);
        typeOldSchemaMap.put(oldType, oldSchema);
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(oldType, null, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        HashSet<ComplexTypeInheritanceModifier> blocks = new HashSet<ComplexTypeInheritanceModifier>();
        blocks.add(ComplexTypeInheritanceModifier.Restriction);
        assertEquals(blocks, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * complexType which finalizes extension per schema default.
     */
    @Test
    public void testGenerateNewTypeComplexTypeFinalDefaultExtension() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.extension);
        typeOldSchemaMap.put(oldType, oldSchema);
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(oldType, null, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        HashSet<ComplexTypeInheritanceModifier> finals = new HashSet<ComplexTypeInheritanceModifier>();
        finals.add(ComplexTypeInheritanceModifier.Extension);
        assertEquals(finals, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * complexType which finalizes restriction per schema default.
     */
    @Test
    public void testGenerateNewTypeComplexTypeFinalDefaultRestriction() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        typeOldSchemaMap.put(oldType, oldSchema);
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(oldType, null, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        HashSet<ComplexTypeInheritanceModifier> finals = new HashSet<ComplexTypeInheritanceModifier>();
        finals.add(ComplexTypeInheritanceModifier.Restriction);
        assertEquals(finals, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * complexType with complex content ID.
     */
    @Test
    public void testGenerateNewTypeComplexTypeComplexContentID() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        complexContentType.setId("id");
        oldType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, usedIDs, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(oldType, null, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getLast() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals("id.1", ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * complexType with complex content annotation.
     */
    @Test
    public void testGenerateNewTypeComplexTypeComplexContentAnnotation() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        Annotation annotation = new Annotation();
        AppInfo appInfo = new AppInfo();
        appInfo.setSource("bla");
        Documentation documentation = new Documentation();
        documentation.setSource("blaa");
        annotation.addAppInfos(appInfo);
        annotation.addDocumentations(documentation);
        complexContentType.setAnnotation(annotation);
        oldType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(oldType, null, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getLast() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertNotSame(annotation, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation().getAppInfos().size() == 1);
        assertEquals("bla", ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaa", ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * mixed complexType with complex content.
     */
    @Test
    public void testGenerateNewTypeComplexTypeComplexContentMixed() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.setMixed(true);
        oldType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(oldType, null, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(true, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getLast() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * complexType with simple content.
     */
    @Test
    public void testGenerateNewTypeComplexTypeSimpleContent() {
        SimpleContentType simpleContentType = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtension = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentType.setInheritance(simpleContentExtension);
        ComplexType oldType = new ComplexType("{A}complexTypeA", simpleContentType);
        simpleContentExtension.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(oldType, null, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 0);
        assertTrue(((ComplexType) result).getContent() instanceof SimpleContentType);
        assertEquals(null, ((SimpleContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((SimpleContentType) ((ComplexType) result).getContent()).getId());
        assertTrue(((SimpleContentType) ((ComplexType) result).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAnnotation());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getId());
        assertTrue(outputSchema.getTypeSymbolTable().getReference("{A}difference-type.simpleTypeA-null").getReference() instanceof SimpleType);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().size() == 1);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().getLast() instanceof Attribute);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * complexType with simple content with content ID.
     */
    @Test
    public void testGenerateNewTypeComplexTypeSimpleContentContentID() {
        SimpleContentType simpleContentType = new SimpleContentType();
        simpleContentType.setId("id");
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtension = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentType.setInheritance(simpleContentExtension);
        ComplexType oldType = new ComplexType("{A}complexTypeA", simpleContentType);
        simpleContentExtension.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, usedIDs, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(oldType, null, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 0);
        assertTrue(((ComplexType) result).getContent() instanceof SimpleContentType);
        assertEquals(null, ((SimpleContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals("id.1", ((SimpleContentType) ((ComplexType) result).getContent()).getId());
        assertTrue(((SimpleContentType) ((ComplexType) result).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAnnotation());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getId());
        assertTrue(outputSchema.getTypeSymbolTable().getReference("{A}difference-type.simpleTypeA-null").getReference() instanceof SimpleType);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().size() == 1);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().getLast() instanceof Attribute);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * complexType with simple content with content annotation.
     */
    @Test
    public void testGenerateNewTypeComplexTypeSimpleContentContentAnnotation() {
        SimpleContentType simpleContentType = new SimpleContentType();
        Annotation annotation = new Annotation();
        AppInfo appInfo = new AppInfo();
        appInfo.setSource("bla");
        Documentation documentation = new Documentation();
        documentation.setSource("blaa");
        annotation.addAppInfos(appInfo);
        annotation.addDocumentations(documentation);
        simpleContentType.setAnnotation(annotation);
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtension = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentType.setInheritance(simpleContentExtension);
        ComplexType oldType = new ComplexType("{A}complexTypeA", simpleContentType);
        simpleContentExtension.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(oldType, null, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 0);
        assertTrue(((ComplexType) result).getContent() instanceof SimpleContentType);
        assertNotSame(annotation, ((SimpleContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertTrue(((SimpleContentType) ((ComplexType) result).getContent()).getAnnotation().getAppInfos().size() == 1);
        assertEquals("bla", ((SimpleContentType) ((ComplexType) result).getContent()).getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((SimpleContentType) ((ComplexType) result).getContent()).getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaa", ((SimpleContentType) ((ComplexType) result).getContent()).getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(null, ((SimpleContentType) ((ComplexType) result).getContent()).getId());
        assertTrue(((SimpleContentType) ((ComplexType) result).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAnnotation());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getId());
        assertTrue(outputSchema.getTypeSymbolTable().getReference("{A}difference-type.simpleTypeA-null").getReference() instanceof SimpleType);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().size() == 1);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().getLast() instanceof Attribute);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * complexType with simple content with inheritance ID.
     */
    @Test
    public void testGenerateNewTypeComplexTypeSimpleContentInheritanceID() {
        SimpleContentType simpleContentType = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtension = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentExtension.setId("id");
        simpleContentType.setInheritance(simpleContentExtension);
        ComplexType oldType = new ComplexType("{A}complexTypeA", simpleContentType);
        simpleContentExtension.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, usedIDs, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(oldType, null, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 0);
        assertTrue(((ComplexType) result).getContent() instanceof SimpleContentType);
        assertEquals(null, ((SimpleContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((SimpleContentType) ((ComplexType) result).getContent()).getId());
        assertTrue(((SimpleContentType) ((ComplexType) result).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAnnotation());
        assertEquals("id.1", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getId());
        assertTrue(outputSchema.getTypeSymbolTable().getReference("{A}difference-type.simpleTypeA-null").getReference() instanceof SimpleType);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().size() == 1);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().getLast() instanceof Attribute);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * complexType with simple content with inheritance annotation.
     */
    @Test
    public void testGenerateNewTypeComplexTypeSimpleContentInheritanceAnnotation() {
        SimpleContentType simpleContentType = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtension = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        Annotation annotation = new Annotation();
        AppInfo appInfo = new AppInfo();
        appInfo.setSource("bla");
        Documentation documentation = new Documentation();
        documentation.setSource("blaa");
        annotation.addAppInfos(appInfo);
        annotation.addDocumentations(documentation);
        simpleContentExtension.setAnnotation(annotation);
        simpleContentType.setInheritance(simpleContentExtension);
        ComplexType oldType = new ComplexType("{A}complexTypeA", simpleContentType);
        simpleContentExtension.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(oldType, null, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 0);
        assertTrue(((ComplexType) result).getContent() instanceof SimpleContentType);
        assertEquals(null, ((SimpleContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((SimpleContentType) ((ComplexType) result).getContent()).getId());
        assertTrue(((SimpleContentType) ((ComplexType) result).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertNotSame(annotation, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAnnotation());
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAnnotation().getAppInfos().size() == 1);
        assertEquals("bla", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaa", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getId());
        assertTrue(outputSchema.getTypeSymbolTable().getReference("{A}difference-type.simpleTypeA-null").getReference() instanceof SimpleType);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().size() == 1);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().getLast() instanceof Attribute);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType and a subtrahend simpleType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMinusSimpleType() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentType);
        minuendType.addAttribute(new Attribute("{A}attribute"));

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getParticle());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType and a subtrahend simpleType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMinusSimpleTypeID() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentType);
        minuendType.addAttribute(new Attribute("{A}attribute"));
        minuendType.setId("id");

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, usedIDs, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals("id.1", result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getParticle());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType and a subtrahend simpleType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMinusSimpleTypeAnnotation() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentType);
        minuendType.addAttribute(new Attribute("{A}attribute"));
        Annotation annotation = new Annotation();
        AppInfo appInfo = new AppInfo();
        appInfo.setSource("bla");
        Documentation documentation = new Documentation();
        documentation.setSource("blaa");
        annotation.addAppInfos(appInfo);
        annotation.addDocumentations(documentation);
        minuendType.setAnnotation(annotation);

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertNotSame(annotation, result.getAnnotation());
        assertTrue(result.getAnnotation().getAppInfos().size() == 1);
        assertEquals("bla", result.getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(result.getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaa", result.getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getParticle());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType and a subtrahend simpleType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMinusSimpleTypeAnonymous() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentType);
        minuendType.addAttribute(new Attribute("{A}attribute"));
        minuendType.setIsAnonymous(true);

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(true, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getParticle());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType and a subtrahend simpleType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMinusSimpleTypeMixed() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentType);
        minuendType.addAttribute(new Attribute("{A}attribute"));
        minuendType.setMixed(true);

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(true, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertTrue(((ComplexContentType) ((ComplexType) result).getContent()).getParticle() instanceof ChoicePattern);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType and a subtrahend simpleType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMinusSimpleTypeBlockExtension() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentType);
        minuendType.addAttribute(new Attribute("{A}attribute"));
        minuendType.addBlockModifier(ComplexTypeInheritanceModifier.Extension);

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        HashSet<ComplexTypeInheritanceModifier> blocks = new HashSet<ComplexTypeInheritanceModifier>();
        blocks.add(ComplexTypeInheritanceModifier.Extension);
        assertEquals(blocks, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getParticle());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType and a subtrahend simpleType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMinusSimpleTypeBlockRestriction() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentType);
        minuendType.addAttribute(new Attribute("{A}attribute"));
        minuendType.addBlockModifier(ComplexTypeInheritanceModifier.Restriction);

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        HashSet<ComplexTypeInheritanceModifier> blocks = new HashSet<ComplexTypeInheritanceModifier>();
        blocks.add(ComplexTypeInheritanceModifier.Restriction);
        assertEquals(blocks, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getParticle());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType and a subtrahend simpleType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMinusSimpleTypeBlockExtensionPerDefault() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentType);
        minuendType.addAttribute(new Attribute("{A}attribute"));

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.extension);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, oldSchema);
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        HashSet<ComplexTypeInheritanceModifier> blocks = new HashSet<ComplexTypeInheritanceModifier>();
        blocks.add(ComplexTypeInheritanceModifier.Extension);
        assertEquals(blocks, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getParticle());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType and a subtrahend simpleType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMinusSimpleTypeBlockRestrictionPerDefault() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentType);
        minuendType.addAttribute(new Attribute("{A}attribute"));

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.restriction);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, oldSchema);
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        HashSet<ComplexTypeInheritanceModifier> blocks = new HashSet<ComplexTypeInheritanceModifier>();
        blocks.add(ComplexTypeInheritanceModifier.Restriction);
        assertEquals(blocks, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getParticle());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType and a subtrahend simpleType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMinusSimpleTypeBlockSchemaDefault() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentType);
        minuendType.addAttribute(new Attribute("{A}attribute"));

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.addBlockDefault(XSDSchema.BlockDefault.restriction);
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.restriction);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, oldSchema);
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getParticle());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType and a subtrahend simpleType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMinusSimpleTypeFinalExtensionPerDefault() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentType);
        minuendType.addAttribute(new Attribute("{A}attribute"));

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.extension);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, oldSchema);
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        HashSet<ComplexTypeInheritanceModifier> finals = new HashSet<ComplexTypeInheritanceModifier>();
        finals.add(ComplexTypeInheritanceModifier.Extension);
        assertEquals(finals, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getParticle());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType and a subtrahend simpleType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMinusSimpleTypeFinalRestrictionPerDefault() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentType);
        minuendType.addAttribute(new Attribute("{A}attribute"));

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        XSDSchema outputSchema = new XSDSchema("A");
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, oldSchema);
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        HashSet<ComplexTypeInheritanceModifier> finals = new HashSet<ComplexTypeInheritanceModifier>();
        finals.add(ComplexTypeInheritanceModifier.Restriction);
        assertEquals(finals, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getParticle());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType and a subtrahend simpleType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMinusSimpleTypeFinalSchemaDefault() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentType);
        minuendType.addAttribute(new Attribute("{A}attribute"));

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, oldSchema);
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getParticle());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType and a subtrahend anyType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMinusAnyType() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentType);
        minuendType.addAttribute(new Attribute("{A}attribute"));

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}anyType", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertEquals(null, result);
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType and a subtrahend anySimpleType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMixedWithoutParticleMinusAnySimpleType() {
        ComplexContentType complexContentType = new ComplexContentType(null);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentType);
        minuendType.setMixed(true);

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertEquals(null, result);
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType and a subtrahend anySimpleType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMixedEmptyParticleMinusAnySimpleType() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentType);
        minuendType.setMixed(true);

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertEquals(null, result);
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType and a subtrahend anySimpleType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeMixedOptionalParticleMinusAnySimpleType() {
        Element elementA = new Element("{A}elementA");
        CountingPattern countingPatternA = new CountingPattern(0, 1);
        countingPatternA.addParticle(elementA);
        ChoicePattern choicePatternA = new ChoicePattern();
        choicePatternA.addParticle(elementA);
        ComplexContentType complexContentType = new ComplexContentType(choicePatternA);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentType);
        minuendType.setMixed(true);

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertEquals(null, result);
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType and a subtrahend simpleType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeWithoutParticleMinusPossibleEmptySimpleType() {
        ComplexContentType complexContentType = new ComplexContentType(null);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentType);

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertEquals(null, result);
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType and a subtrahend simpleType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeEmptyParticleMinusPossibleEmptySimpleType() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentType);

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertEquals(null, result);
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType and a subtrahend simpleType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeOptionalParticleMinusPossibleEmptySimpleType() {
        Element elementA = new Element("{A}elementA");
        CountingPattern countingPatternA = new CountingPattern(0, 1);
        countingPatternA.addParticle(elementA);
        ChoicePattern choicePatternA = new ChoicePattern();
        choicePatternA.addParticle(elementA);
        ComplexContentType complexContentType = new ComplexContentType(choicePatternA);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", complexContentType);

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(subtrahendType, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> elementOldTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementOldTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}elementA", minuendType));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, elementOldSchemaMap, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, elementOldTypeMap);
        } catch (Exception ex) {
            fail();
        }

        assertEquals(null, result);
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType with simple content and a subtrahend simpleType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeSimpleContentMinusSimpleType() {
        SimpleContentType simpleContentType = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtension = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentType.setInheritance(simpleContentExtension);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", simpleContentType);
        simpleContentExtension.addAttribute(new Attribute("{A}attribute"));

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 0);
        assertTrue(((ComplexType) result).getContent() instanceof SimpleContentType);
        assertEquals(null, ((SimpleContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((SimpleContentType) ((ComplexType) result).getContent()).getId());
        assertTrue(((SimpleContentType) ((ComplexType) result).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAnnotation());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getId());
        assertTrue(outputSchema.getTypeSymbolTable().getReference("{A}difference-type.simpleTypeA-null").getReference() instanceof SimpleType);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().size() == 1);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().getLast() instanceof Attribute);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType with simple content and a subtrahend simpleType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeSimpleContentMinusSimpleTypeContentID() {
        SimpleContentType simpleContentType = new SimpleContentType();
        simpleContentType.setId("id");
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtension = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentType.setInheritance(simpleContentExtension);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", simpleContentType);
        simpleContentExtension.addAttribute(new Attribute("{A}attribute"));

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, usedIDs, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 0);
        assertTrue(((ComplexType) result).getContent() instanceof SimpleContentType);
        assertEquals(null, ((SimpleContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals("id.1", ((SimpleContentType) ((ComplexType) result).getContent()).getId());
        assertTrue(((SimpleContentType) ((ComplexType) result).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAnnotation());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getId());
        assertTrue(outputSchema.getTypeSymbolTable().getReference("{A}difference-type.simpleTypeA-null").getReference() instanceof SimpleType);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().size() == 1);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().getLast() instanceof Attribute);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType with simple content and a subtrahend simpleType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeSimpleContentMinusSimpleTypeContentAnnotation() {
        SimpleContentType simpleContentType = new SimpleContentType();
        Annotation annotation = new Annotation();
        AppInfo appInfo = new AppInfo();
        appInfo.setSource("bla");
        Documentation documentation = new Documentation();
        documentation.setSource("blaa");
        annotation.addAppInfos(appInfo);
        annotation.addDocumentations(documentation);
        simpleContentType.setAnnotation(annotation);
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtension = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentType.setInheritance(simpleContentExtension);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", simpleContentType);
        simpleContentExtension.addAttribute(new Attribute("{A}attribute"));

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 0);
        assertTrue(((ComplexType) result).getContent() instanceof SimpleContentType);
        assertNotSame(annotation, ((SimpleContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertTrue(((SimpleContentType) ((ComplexType) result).getContent()).getAnnotation().getAppInfos().size() == 1);
        assertEquals("bla", ((SimpleContentType) ((ComplexType) result).getContent()).getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((SimpleContentType) ((ComplexType) result).getContent()).getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaa", ((SimpleContentType) ((ComplexType) result).getContent()).getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(null, ((SimpleContentType) ((ComplexType) result).getContent()).getId());
        assertTrue(((SimpleContentType) ((ComplexType) result).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAnnotation());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getId());
        assertTrue(outputSchema.getTypeSymbolTable().getReference("{A}difference-type.simpleTypeA-null").getReference() instanceof SimpleType);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().size() == 1);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().getLast() instanceof Attribute);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType with simple content and a subtrahend simpleType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeSimpleContentMinusSimpleTypeInheritanceID() {
        SimpleContentType simpleContentType = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtension = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentExtension.setId("id");
        simpleContentType.setInheritance(simpleContentExtension);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", simpleContentType);
        simpleContentExtension.addAttribute(new Attribute("{A}attribute"));

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, usedIDs, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 0);
        assertTrue(((ComplexType) result).getContent() instanceof SimpleContentType);
        assertEquals(null, ((SimpleContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((SimpleContentType) ((ComplexType) result).getContent()).getId());
        assertTrue(((SimpleContentType) ((ComplexType) result).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAnnotation());
        assertEquals("id", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getId());
        assertTrue(outputSchema.getTypeSymbolTable().getReference("{A}difference-type.simpleTypeA-null").getReference() instanceof SimpleType);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().size() == 1);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().getLast() instanceof Attribute);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType with simple content and a subtrahend simpleType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeSimpleContentMinusSimpleTypeInheritanceAnnotation() {
        SimpleContentType simpleContentType = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtension = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        Annotation annotation = new Annotation();
        AppInfo appInfo = new AppInfo();
        appInfo.setSource("bla");
        Documentation documentation = new Documentation();
        documentation.setSource("blaa");
        annotation.addAppInfos(appInfo);
        annotation.addDocumentations(documentation);
        simpleContentExtension.setAnnotation(annotation);
        simpleContentType.setInheritance(simpleContentExtension);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", simpleContentType);
        simpleContentExtension.addAttribute(new Attribute("{A}attribute"));

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 0);
        assertTrue(((ComplexType) result).getContent() instanceof SimpleContentType);
        assertEquals(null, ((SimpleContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((SimpleContentType) ((ComplexType) result).getContent()).getId());
        assertTrue(((SimpleContentType) ((ComplexType) result).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAnnotation().getAppInfos().size() == 1);
        assertEquals("bla", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaa", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getId());
        assertTrue(outputSchema.getTypeSymbolTable().getReference("{A}difference-type.simpleTypeA-null").getReference() instanceof SimpleType);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().size() == 1);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().getLast() instanceof Attribute);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType with simple content and a subtrahend simpleType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeSimpleContentWithoutAttributesMinusSimpleType() {
        SimpleContentType simpleContentType = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtension = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentType.setInheritance(simpleContentExtension);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", simpleContentType);

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 0);
        assertTrue(((ComplexType) result).getContent() instanceof SimpleContentType);
        assertEquals(null, ((SimpleContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((SimpleContentType) ((ComplexType) result).getContent()).getId());
        assertTrue(((SimpleContentType) ((ComplexType) result).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAnnotation());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getId());
        assertTrue(outputSchema.getTypeSymbolTable().getReference("{A}difference-type.simpleTypeA-string").getReference() instanceof SimpleType);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().size() == 0);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType with simple content and a subtrahend simpleType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeSimpleContentWithoutAttributesMinusSimpleTypeContentID() {
        SimpleContentType simpleContentType = new SimpleContentType();
        simpleContentType.setId("id");
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtension = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentType.setInheritance(simpleContentExtension);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", simpleContentType);

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, usedIDs, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 0);
        assertTrue(((ComplexType) result).getContent() instanceof SimpleContentType);
        assertEquals(null, ((SimpleContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals("id.1", ((SimpleContentType) ((ComplexType) result).getContent()).getId());
        assertTrue(((SimpleContentType) ((ComplexType) result).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAnnotation());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getId());
        assertTrue(outputSchema.getTypeSymbolTable().getReference("{A}difference-type.simpleTypeA-string").getReference() instanceof SimpleType);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().size() == 0);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType with simple content and a subtrahend simpleType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeSimpleContentWithoutAttributesMinusSimpleTypeContentAnnotation() {
        SimpleContentType simpleContentType = new SimpleContentType();
        Annotation annotation = new Annotation();
        AppInfo appInfo = new AppInfo();
        appInfo.setSource("bla");
        Documentation documentation = new Documentation();
        documentation.setSource("blaa");
        annotation.addAppInfos(appInfo);
        annotation.addDocumentations(documentation);
        simpleContentType.setAnnotation(annotation);
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtension = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentType.setInheritance(simpleContentExtension);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", simpleContentType);

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 0);
        assertTrue(((ComplexType) result).getContent() instanceof SimpleContentType);
        assertNotSame(annotation, ((SimpleContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertTrue(((SimpleContentType) ((ComplexType) result).getContent()).getAnnotation().getAppInfos().size() == 1);
        assertEquals("bla", ((SimpleContentType) ((ComplexType) result).getContent()).getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((SimpleContentType) ((ComplexType) result).getContent()).getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaa", ((SimpleContentType) ((ComplexType) result).getContent()).getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(null, ((SimpleContentType) ((ComplexType) result).getContent()).getId());
        assertTrue(((SimpleContentType) ((ComplexType) result).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAnnotation());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getId());
        assertTrue(outputSchema.getTypeSymbolTable().getReference("{A}difference-type.simpleTypeA-string").getReference() instanceof SimpleType);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().size() == 0);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType with simple content and a subtrahend simpleType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeSimpleContentWithoutAttributesMinusSimpleTypeInheritanceID() {
        SimpleContentType simpleContentType = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtension = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentExtension.setId("id");
        simpleContentType.setInheritance(simpleContentExtension);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", simpleContentType);

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, usedIDs, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 0);
        assertTrue(((ComplexType) result).getContent() instanceof SimpleContentType);
        assertEquals(null, ((SimpleContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((SimpleContentType) ((ComplexType) result).getContent()).getId());
        assertTrue(((SimpleContentType) ((ComplexType) result).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAnnotation());
        assertEquals("id", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getId());
        assertTrue(outputSchema.getTypeSymbolTable().getReference("{A}difference-type.simpleTypeA-string").getReference() instanceof SimpleType);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().size() == 0);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType with simple content and a subtrahend simpleType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeSimpleContentWithoutAttributesMinusSimpleTypeInheritanceAnnotation() {
        SimpleContentType simpleContentType = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtension = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        Annotation annotation = new Annotation();
        AppInfo appInfo = new AppInfo();
        appInfo.setSource("bla");
        Documentation documentation = new Documentation();
        documentation.setSource("blaa");
        annotation.addAppInfos(appInfo);
        annotation.addDocumentations(documentation);
        simpleContentExtension.setAnnotation(annotation);
        simpleContentType.setInheritance(simpleContentExtension);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", simpleContentType);

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertTrue(result instanceof ComplexType);
        assertEquals(minuendType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 0);
        assertTrue(((ComplexType) result).getContent() instanceof SimpleContentType);
        assertEquals(null, ((SimpleContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((SimpleContentType) ((ComplexType) result).getContent()).getId());
        assertTrue(((SimpleContentType) ((ComplexType) result).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAnnotation().getAppInfos().size() == 1);
        assertEquals("bla", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaa", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getId());
        assertTrue(outputSchema.getTypeSymbolTable().getReference("{A}difference-type.simpleTypeA-string").getReference() instanceof SimpleType);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().size() == 0);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeDifferenceGenerator for a
     * minuend complexType with simple content and a subtrahend simpleType.
     */
    @Test
    public void testGenerateNewTypeComplexTypeSimpleContentWithoutAttributesMinusSimpleTypeEmptyType() {
        SimpleContentType simpleContentType = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentExtension simpleContentExtension = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        Annotation annotation = new Annotation();
        AppInfo appInfo = new AppInfo();
        appInfo.setSource("bla");
        Documentation documentation = new Documentation();
        documentation.setSource("blaa");
        annotation.addAppInfos(appInfo);
        annotation.addDocumentations(documentation);
        simpleContentExtension.setAnnotation(annotation);
        simpleContentType.setInheritance(simpleContentExtension);
        ComplexType minuendType = new ComplexType("{A}complexTypeA", simpleContentType);

        SimpleType subtrahendType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(minuendType, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleDifferenceGenerator AttributeParticleDifferenceGenerator = new AttributeParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleDifferenceGenerator ParticleDifferenceGenerator = new ParticleDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, AttributeParticleDifferenceGenerator, ParticleDifferenceGenerator, null, typeOldSchemaMap, null, null);

        Type result = null;
        try {
            result = instance.generateNewType(minuendType, subtrahendType, "{A}complexTypeA", null, null);
        } catch (Exception ex) {
            fail();
        }

        assertEquals(null, result);
    }

    /**
     * Test of isBuiltInDatatype method, of class TypeDifferenceGenerator.
     */
    @Test
    public void testIsBuiltInDatatypeTrue() {
        String name = "{http://www.w3.org/2001/XMLSchema}string";
        XSDSchema outputSchema = new XSDSchema("A");
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);

        boolean expResult = true;
        boolean result = instance.isBuiltInDatatype(name);

        assertEquals(expResult, result);
    }

    /**
     * Test of isBuiltInDatatype method, of class TypeDifferenceGenerator.
     */
    @Test
    public void testIsBuiltInDatatypeFalse() {
        String name = "{http://www.w3.org/2001/XMLSchema}foo";
        XSDSchema outputSchema = new XSDSchema("A");
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, null, null, null);

        boolean expResult = false;
        boolean result = instance.isBuiltInDatatype(name);

        assertEquals(expResult, result);
    }

    //===============================================================================================================================================================================================================
    /**
     * Test of generateNewSimpleType method, of class TypeDifferenceGenerator.
     */
    @Test
    public void testGenerateNewSimpleType_3args1st() {
        SimpleType stringA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleType stringB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        SimpleContentRestriction simpleContentRestrictionA1 = new SimpleContentRestriction(new SymbolTableRef<Type>(stringA.getName(), stringA));
        simpleContentRestrictionA1.setMinLength(new SimpleContentFixableRestrictionProperty<Integer>(0, false));
        simpleContentRestrictionA1.setMaxLength(new SimpleContentFixableRestrictionProperty<Integer>(10, false));
        SimpleContentRestriction simpleContentRestrictionA2 = new SimpleContentRestriction(new SymbolTableRef<Type>(stringA.getName(), stringA));
        simpleContentRestrictionA2.setMinLength(new SimpleContentFixableRestrictionProperty<Integer>(20, false));
        simpleContentRestrictionA2.setMaxLength(new SimpleContentFixableRestrictionProperty<Integer>(25, false));
        SimpleContentRestriction simpleContentRestrictionB1 = new SimpleContentRestriction(new SymbolTableRef<Type>(stringB.getName(), stringB));
        simpleContentRestrictionB1.setMinLength(new SimpleContentFixableRestrictionProperty<Integer>(2, false));
        simpleContentRestrictionB1.setMaxLength(new SimpleContentFixableRestrictionProperty<Integer>(4, false));
        SimpleContentRestriction simpleContentRestrictionB2 = new SimpleContentRestriction(new SymbolTableRef<Type>(stringB.getName(), stringB));
        simpleContentRestrictionB2.setMinLength(new SimpleContentFixableRestrictionProperty<Integer>(10, false));
        SimpleContentRestriction simpleContentRestrictionB3 = new SimpleContentRestriction(new SymbolTableRef<Type>(stringB.getName(), stringB));
        simpleContentRestrictionB3.setLength(new SimpleContentFixableRestrictionProperty<Integer>(22, false));

        SimpleType restrictionA1 = new SimpleType("{A}restrictionA1", simpleContentRestrictionA1);
        SimpleType restrictionA2 = new SimpleType("{A}restrictionA2", simpleContentRestrictionA2);
        SimpleType restrictionB1 = new SimpleType("{A}restrictionB1", simpleContentRestrictionB1);
        SimpleType restrictionB2 = new SimpleType("{A}restrictionB2", simpleContentRestrictionB2);
        SimpleType restrictionB3 = new SimpleType("{A}restrictionB3", simpleContentRestrictionB3);

        LinkedList<SymbolTableRef<Type>> memberTypesA = new LinkedList<SymbolTableRef<Type>>();
        memberTypesA.add(new SymbolTableRef<Type>(restrictionA1.getName(), restrictionA1));
        memberTypesA.add(new SymbolTableRef<Type>(restrictionA2.getName(), restrictionA2));
        SimpleContentUnion simpleContentUnionA = new SimpleContentUnion(memberTypesA);

        LinkedList<SymbolTableRef<Type>> memberTypesB = new LinkedList<SymbolTableRef<Type>>();
        memberTypesB.add(new SymbolTableRef<Type>(restrictionB1.getName(), restrictionB1));
        memberTypesB.add(new SymbolTableRef<Type>(restrictionB2.getName(), restrictionB2));
        memberTypesB.add(new SymbolTableRef<Type>(restrictionB3.getName(), restrictionB3));
        SimpleContentUnion simpleContentUnionB = new SimpleContentUnion(memberTypesB);

        SimpleType unionA = new SimpleType("{A}unionA", simpleContentUnionA);
        SimpleType unionB = new SimpleType("{A}unionB", simpleContentUnionB);

        SimpleType minuendSimpleType = unionA;
        SimpleType subtrahendSimpleType = unionB;

        String newTypeName = "{A}difference-type.unionA.unionB";
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(restrictionA1, new XSDSchema("A"));
        typeOldSchemaMap.put(restrictionA2, new XSDSchema("A"));
        typeOldSchemaMap.put(restrictionB1, new XSDSchema("A"));
        typeOldSchemaMap.put(restrictionB2, new XSDSchema("A"));
        typeOldSchemaMap.put(restrictionB3, new XSDSchema("A"));
        typeOldSchemaMap.put(stringA, new XSDSchema("A"));
        typeOldSchemaMap.put(stringB, new XSDSchema("A"));
        typeOldSchemaMap.put(unionA, new XSDSchema("A"));
        typeOldSchemaMap.put(unionA, new XSDSchema("A"));
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null);

        SimpleType result = instance.generateNewSimpleType(minuendSimpleType, subtrahendSimpleType, newTypeName);

        assertTrue(result instanceof SimpleType);
        assertEquals("{A}difference-type.unionA.unionB", ((SimpleType) result).getName());
        assertTrue(result.getInheritance() instanceof SimpleContentUnion);
        assertTrue(((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().size() == 2);
        assertTrue(((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertTrue(((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference() instanceof SimpleType);

        if (((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxLength().getValue() == 1) {
            assertTrue(((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance() instanceof SimpleContentRestriction);
            assertTrue(((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance() instanceof SimpleContentRestriction);
            assertEquals(0, (int) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinLength().getValue());
            assertEquals(1, (int) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxLength().getValue());
            assertEquals(5, (int) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance()).getMinLength().getValue());
            assertEquals(9, (int) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance()).getMaxLength().getValue());
            assertTrue(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase() instanceof SimpleType);
            assertTrue(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance()).getBase() instanceof SimpleType);
            assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName());
            assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance()).getBase().getName());
        } else {
            assertTrue(((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance() instanceof SimpleContentRestriction);
            assertTrue(((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance() instanceof SimpleContentRestriction);
            assertEquals(5, (int) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance()).getMinLength().getValue());
            assertEquals(9, (int) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance()).getMaxLength().getValue());
            assertEquals(0, (int) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinLength().getValue());
            assertEquals(1, (int) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxLength().getValue());
            assertTrue(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance()).getBase() instanceof SimpleType);
            assertTrue(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase() instanceof SimpleType);
            assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance()).getBase().getName());
            assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName());
        }
    }

    /**
     * Test of generateNewSimpleType method, of class TypeDifferenceGenerator.
     */
    @Test
    public void testGenerateNewSimpleType_3args2nd() {
        SimpleType stringA = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        SimpleType stringB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);

        SimpleContentRestriction simpleContentRestrictionA1 = new SimpleContentRestriction(new SymbolTableRef<Type>(stringA.getName(), stringA));
        simpleContentRestrictionA1.setMaxInclusive(new SimpleContentFixableRestrictionProperty<String>("2", false));
        simpleContentRestrictionA1.setMinInclusive(new SimpleContentFixableRestrictionProperty<String>("2", false));
        SimpleContentRestriction simpleContentRestrictionA2 = new SimpleContentRestriction(new SymbolTableRef<Type>(stringA.getName(), stringA));
        simpleContentRestrictionA2.setMinInclusive(new SimpleContentFixableRestrictionProperty<String>("10", false));
        SimpleContentRestriction simpleContentRestrictionB1 = new SimpleContentRestriction(new SymbolTableRef<Type>(stringB.getName(), stringB));
        simpleContentRestrictionB1.setMaxInclusive(new SimpleContentFixableRestrictionProperty<String>("20", false));
        simpleContentRestrictionB1.setMinInclusive(new SimpleContentFixableRestrictionProperty<String>("0", false));
        SimpleContentRestriction simpleContentRestrictionB2 = new SimpleContentRestriction(new SymbolTableRef<Type>(stringB.getName(), stringB));
        simpleContentRestrictionB2.setMinInclusive(new SimpleContentFixableRestrictionProperty<String>("20", false));

        SimpleType restrictionA1 = new SimpleType("{A}restrictionA1", simpleContentRestrictionA1);
        SimpleType restrictionA2 = new SimpleType("{A}restrictionA2", simpleContentRestrictionA2);
        SimpleType restrictionB1 = new SimpleType("{A}restrictionB1", simpleContentRestrictionB1);
        SimpleType restrictionB2 = new SimpleType("{A}restrictionB2", simpleContentRestrictionB2);

        LinkedList<SymbolTableRef<Type>> memberTypesA = new LinkedList<SymbolTableRef<Type>>();
        memberTypesA.add(new SymbolTableRef<Type>(restrictionA1.getName(), restrictionA1));
        memberTypesA.add(new SymbolTableRef<Type>(restrictionA2.getName(), restrictionA2));
        SimpleContentUnion simpleContentUnionA = new SimpleContentUnion(memberTypesA);

        LinkedList<SymbolTableRef<Type>> memberTypesB = new LinkedList<SymbolTableRef<Type>>();
        memberTypesB.add(new SymbolTableRef<Type>(restrictionB1.getName(), restrictionB1));
        memberTypesB.add(new SymbolTableRef<Type>(restrictionB2.getName(), restrictionB2));
        SimpleContentUnion simpleContentUnionB = new SimpleContentUnion(memberTypesB);

        SimpleType unionA = new SimpleType("{A}unionA", simpleContentUnionA);
        SimpleType unionB = new SimpleType("{A}unionB", simpleContentUnionB);

        SimpleContentList simpleContentListA = new SimpleContentList(new SymbolTableRef<Type>(unionA.getName(), unionA));
        SimpleContentList simpleContentListB = new SimpleContentList(new SymbolTableRef<Type>(unionB.getName(), unionB));

        SimpleType listA = new SimpleType("{A}listA", simpleContentListA);
        SimpleType listB = new SimpleType("{A}listB", simpleContentListB);

        SimpleType minuendSimpleType = listA;
        SimpleType subtrahendSimpleType = listB;

        String newTypeName = "{A}difference-type.restrictionListA.restrictionListB";
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(restrictionA1, new XSDSchema("A"));
        typeOldSchemaMap.put(restrictionA2, new XSDSchema("A"));
        typeOldSchemaMap.put(restrictionB1, new XSDSchema("A"));
        typeOldSchemaMap.put(restrictionB2, new XSDSchema("A"));
        typeOldSchemaMap.put(stringA, new XSDSchema("A"));
        typeOldSchemaMap.put(stringB, new XSDSchema("A"));
        typeOldSchemaMap.put(unionA, new XSDSchema("A"));
        typeOldSchemaMap.put(unionA, new XSDSchema("A"));
        typeOldSchemaMap.put(listA, new XSDSchema("A"));
        typeOldSchemaMap.put(listB, new XSDSchema("A"));
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null);

        SimpleType result = instance.generateNewSimpleType(minuendSimpleType, subtrahendSimpleType, newTypeName);

        assertEquals(null, result);
    }

    /**
     * Test of generateNewSimpleType method, of class TypeDifferenceGenerator.
     */
    @Test
    public void testGenerateNewSimpleType_3args3rd() {
        SimpleType stringA = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        SimpleType stringB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);

        SimpleContentRestriction simpleContentRestrictionA1 = new SimpleContentRestriction(new SymbolTableRef<Type>(stringA.getName(), stringA));
        simpleContentRestrictionA1.setMaxInclusive(new SimpleContentFixableRestrictionProperty<String>("2", false));
        simpleContentRestrictionA1.setMinInclusive(new SimpleContentFixableRestrictionProperty<String>("2", false));
        SimpleContentRestriction simpleContentRestrictionA2 = new SimpleContentRestriction(new SymbolTableRef<Type>(stringA.getName(), stringA));
        simpleContentRestrictionA2.setMinInclusive(new SimpleContentFixableRestrictionProperty<String>("10", false));
        SimpleContentRestriction simpleContentRestrictionB1 = new SimpleContentRestriction(new SymbolTableRef<Type>(stringB.getName(), stringB));
        simpleContentRestrictionB1.setMaxInclusive(new SimpleContentFixableRestrictionProperty<String>("20", false));
        simpleContentRestrictionB1.setMinInclusive(new SimpleContentFixableRestrictionProperty<String>("0", false));
        SimpleContentRestriction simpleContentRestrictionB2 = new SimpleContentRestriction(new SymbolTableRef<Type>(stringB.getName(), stringB));
        simpleContentRestrictionB2.setMinInclusive(new SimpleContentFixableRestrictionProperty<String>("20", false));

        SimpleType restrictionA1 = new SimpleType("{A}restrictionA1", simpleContentRestrictionA1);
        SimpleType restrictionA2 = new SimpleType("{A}restrictionA2", simpleContentRestrictionA2);
        SimpleType restrictionB1 = new SimpleType("{A}restrictionB1", simpleContentRestrictionB1);
        SimpleType restrictionB2 = new SimpleType("{A}restrictionB2", simpleContentRestrictionB2);

        LinkedList<SymbolTableRef<Type>> memberTypesA = new LinkedList<SymbolTableRef<Type>>();
        memberTypesA.add(new SymbolTableRef<Type>(restrictionA1.getName(), restrictionA1));
        memberTypesA.add(new SymbolTableRef<Type>(restrictionA2.getName(), restrictionA2));
        SimpleContentUnion simpleContentUnionA = new SimpleContentUnion(memberTypesA);

        LinkedList<SymbolTableRef<Type>> memberTypesB = new LinkedList<SymbolTableRef<Type>>();
        memberTypesB.add(new SymbolTableRef<Type>(restrictionB1.getName(), restrictionB1));
        memberTypesB.add(new SymbolTableRef<Type>(restrictionB2.getName(), restrictionB2));
        SimpleContentUnion simpleContentUnionB = new SimpleContentUnion(memberTypesB);

        SimpleType unionA = new SimpleType("{A}unionA", simpleContentUnionA);
        SimpleType unionB = new SimpleType("{A}unionB", simpleContentUnionB);

        SimpleContentList simpleContentListA = new SimpleContentList(new SymbolTableRef<Type>(unionA.getName(), unionA));
        SimpleContentList simpleContentListB = new SimpleContentList(new SymbolTableRef<Type>(unionB.getName(), unionB));

        SimpleType listA = new SimpleType("{A}listA", simpleContentListA);
        SimpleType listB = new SimpleType("{A}listB", simpleContentListB);

        SimpleContentRestriction simpleContentRestrictionListA = new SimpleContentRestriction(new SymbolTableRef<Type>(listA.getName(), listA));
        simpleContentRestrictionListA.setMinLength(new SimpleContentFixableRestrictionProperty<Integer>(3, false));
        simpleContentRestrictionListA.setMaxLength(new SimpleContentFixableRestrictionProperty<Integer>(5, false));
        SimpleContentRestriction simpleContentRestrictionListB = new SimpleContentRestriction(new SymbolTableRef<Type>(listB.getName(), listB));
        simpleContentRestrictionListB.setLength(new SimpleContentFixableRestrictionProperty<Integer>(4, false));

        SimpleType restrictionListA = new SimpleType("{A}restrictionListA", simpleContentRestrictionListA);
        SimpleType restrictionListB = new SimpleType("{A}restrictionListB", simpleContentRestrictionListB);

        SimpleType minuendSimpleType = restrictionListA;
        SimpleType subtrahendSimpleType = restrictionListB;

        String newTypeName = "{A}difference-type.restrictionListA.restrictionListB";
        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(restrictionA1, new XSDSchema("A"));
        typeOldSchemaMap.put(restrictionA2, new XSDSchema("A"));
        typeOldSchemaMap.put(restrictionB1, new XSDSchema("A"));
        typeOldSchemaMap.put(restrictionB2, new XSDSchema("A"));
        typeOldSchemaMap.put(stringA, new XSDSchema("A"));
        typeOldSchemaMap.put(stringB, new XSDSchema("A"));
        typeOldSchemaMap.put(unionA, new XSDSchema("A"));
        typeOldSchemaMap.put(unionA, new XSDSchema("A"));
        typeOldSchemaMap.put(listA, new XSDSchema("A"));
        typeOldSchemaMap.put(listB, new XSDSchema("A"));
        typeOldSchemaMap.put(restrictionListA, new XSDSchema("A"));
        typeOldSchemaMap.put(restrictionListB, new XSDSchema("A"));
        TypeDifferenceGenerator instance = new TypeDifferenceGenerator(outputSchema, null, null, null, null, null, null, null, typeOldSchemaMap, null, null);

        SimpleType result = instance.generateNewSimpleType(minuendSimpleType, subtrahendSimpleType, newTypeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertTrue(result.getInheritance() instanceof SimpleContentUnion);
        assertTrue(((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().size() == 2);
        assertTrue(((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertTrue(((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference() instanceof SimpleType);
        assertTrue(((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance() instanceof SimpleContentRestriction);
        assertTrue(((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance() instanceof SimpleContentRestriction);

        SimpleContentRestriction resultingSimpleContentRestrictionListA = (SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance();
        SimpleContentRestriction resultingSimpleContentRestrictionListB = (SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance();

        if (resultingSimpleContentRestrictionListA.getMinLength().getValue() == 3) {
            assertEquals(3, (int) resultingSimpleContentRestrictionListA.getMinLength().getValue());
            assertEquals(3, (int) resultingSimpleContentRestrictionListA.getMaxLength().getValue());
            assertEquals(5, (int) resultingSimpleContentRestrictionListB.getMinLength().getValue());
            assertEquals(5, (int) resultingSimpleContentRestrictionListB.getMaxLength().getValue());
        } else {
            assertEquals(3, (int) resultingSimpleContentRestrictionListB.getMinLength().getValue());
            assertEquals(3, (int) resultingSimpleContentRestrictionListB.getMaxLength().getValue());
            assertEquals(5, (int) resultingSimpleContentRestrictionListA.getMinLength().getValue());
            assertEquals(5, (int) resultingSimpleContentRestrictionListA.getMaxLength().getValue());
        }
        assertTrue(resultingSimpleContentRestrictionListA.getBase() instanceof SimpleType);
        assertTrue(resultingSimpleContentRestrictionListB.getBase() instanceof SimpleType);
        assertTrue(((SimpleType) resultingSimpleContentRestrictionListA.getBase()).getInheritance() instanceof SimpleContentList);
        assertTrue(((SimpleType) resultingSimpleContentRestrictionListB.getBase()).getInheritance() instanceof SimpleContentList);

        SimpleContentList resultingSimpleContentListA = (SimpleContentList) ((SimpleType) resultingSimpleContentRestrictionListA.getBase()).getInheritance();
        SimpleContentList resultingSimpleContentListB = (SimpleContentList) ((SimpleType) resultingSimpleContentRestrictionListA.getBase()).getInheritance();

        assertTrue(resultingSimpleContentListA.getBase() instanceof SimpleType);
        assertTrue(resultingSimpleContentListB.getBase() instanceof SimpleType);
        assertTrue(((SimpleType) resultingSimpleContentListA.getBase()).getInheritance() instanceof SimpleContentUnion);
        assertTrue(((SimpleType) resultingSimpleContentListB.getBase()).getInheritance() instanceof SimpleContentUnion);

        SimpleContentUnion resultingSimpleContentUnionA = (SimpleContentUnion) ((SimpleType) resultingSimpleContentListA.getBase()).getInheritance();
        SimpleContentUnion resultingSimpleContentUnionB = (SimpleContentUnion) ((SimpleType) resultingSimpleContentListB.getBase()).getInheritance();

        assertTrue(resultingSimpleContentUnionA.getAllMemberTypes().size() == 2);
        assertTrue(resultingSimpleContentUnionA.getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertTrue(resultingSimpleContentUnionA.getAllMemberTypes().getLast().getReference() instanceof SimpleType);
        assertTrue(((SimpleType) resultingSimpleContentUnionA.getAllMemberTypes().getFirst().getReference()).getInheritance() instanceof SimpleContentRestriction);
        assertTrue(((SimpleType) resultingSimpleContentUnionA.getAllMemberTypes().getLast().getReference()).getInheritance() instanceof SimpleContentRestriction);

        if (((SimpleContentRestriction) ((SimpleType) resultingSimpleContentUnionA.getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxInclusive().getValue().equals("2")) {
            assertEquals("2", ((SimpleContentRestriction) ((SimpleType) resultingSimpleContentUnionA.getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxInclusive().getValue());
            assertEquals("2", ((SimpleContentRestriction) ((SimpleType) resultingSimpleContentUnionA.getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinInclusive().getValue());
            assertEquals("10", ((SimpleContentRestriction) ((SimpleType) resultingSimpleContentUnionA.getAllMemberTypes().getLast().getReference()).getInheritance()).getMinInclusive().getValue());
            assertTrue(((SimpleContentRestriction) ((SimpleType) resultingSimpleContentUnionA.getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase() instanceof SimpleType);
            assertTrue(((SimpleContentRestriction) ((SimpleType) resultingSimpleContentUnionA.getAllMemberTypes().getLast().getReference()).getInheritance()).getBase() instanceof SimpleType);
            assertEquals("{http://www.w3.org/2001/XMLSchema}integer", ((SimpleContentRestriction) ((SimpleType) resultingSimpleContentUnionA.getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName());
            assertEquals("{http://www.w3.org/2001/XMLSchema}integer", ((SimpleContentRestriction) ((SimpleType) resultingSimpleContentUnionA.getAllMemberTypes().getLast().getReference()).getInheritance()).getBase().getName());
        } else {
            assertEquals("2", ((SimpleContentRestriction) ((SimpleType) resultingSimpleContentUnionA.getAllMemberTypes().getLast().getReference()).getInheritance()).getMaxInclusive().getValue());
            assertEquals("2", ((SimpleContentRestriction) ((SimpleType) resultingSimpleContentUnionA.getAllMemberTypes().getLast().getReference()).getInheritance()).getMinInclusive().getValue());
            assertEquals("10", ((SimpleContentRestriction) ((SimpleType) resultingSimpleContentUnionA.getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinInclusive().getValue());
            assertTrue(((SimpleContentRestriction) ((SimpleType) resultingSimpleContentUnionA.getAllMemberTypes().getLast().getReference()).getInheritance()).getBase() instanceof SimpleType);
            assertTrue(((SimpleContentRestriction) ((SimpleType) resultingSimpleContentUnionA.getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase() instanceof SimpleType);
            assertEquals("{http://www.w3.org/2001/XMLSchema}integer", ((SimpleContentRestriction) ((SimpleType) resultingSimpleContentUnionA.getAllMemberTypes().getLast().getReference()).getInheritance()).getBase().getName());
            assertEquals("{http://www.w3.org/2001/XMLSchema}integer", ((SimpleContentRestriction) ((SimpleType) resultingSimpleContentUnionA.getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName());
        }
        assertTrue(resultingSimpleContentUnionB.getAllMemberTypes().size() == 2);
        assertTrue(resultingSimpleContentUnionB.getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertTrue(resultingSimpleContentUnionB.getAllMemberTypes().getLast().getReference() instanceof SimpleType);
        assertTrue(((SimpleType) resultingSimpleContentUnionB.getAllMemberTypes().getFirst().getReference()).getInheritance() instanceof SimpleContentRestriction);
        assertTrue(((SimpleType) resultingSimpleContentUnionB.getAllMemberTypes().getLast().getReference()).getInheritance() instanceof SimpleContentRestriction);

        if (((SimpleContentRestriction) ((SimpleType) resultingSimpleContentUnionB.getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxInclusive().getValue().equals("2")) {
            assertEquals("2", ((SimpleContentRestriction) ((SimpleType) resultingSimpleContentUnionB.getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxInclusive().getValue());
            assertEquals("2", ((SimpleContentRestriction) ((SimpleType) resultingSimpleContentUnionB.getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinInclusive().getValue());
            assertEquals("10", ((SimpleContentRestriction) ((SimpleType) resultingSimpleContentUnionB.getAllMemberTypes().getLast().getReference()).getInheritance()).getMinInclusive().getValue());
            assertTrue(((SimpleContentRestriction) ((SimpleType) resultingSimpleContentUnionB.getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase() instanceof SimpleType);
            assertTrue(((SimpleContentRestriction) ((SimpleType) resultingSimpleContentUnionB.getAllMemberTypes().getLast().getReference()).getInheritance()).getBase() instanceof SimpleType);
            assertEquals("{http://www.w3.org/2001/XMLSchema}integer", ((SimpleContentRestriction) ((SimpleType) resultingSimpleContentUnionB.getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName());
            assertEquals("{http://www.w3.org/2001/XMLSchema}integer", ((SimpleContentRestriction) ((SimpleType) resultingSimpleContentUnionB.getAllMemberTypes().getLast().getReference()).getInheritance()).getBase().getName());
        } else {
            assertEquals("2", ((SimpleContentRestriction) ((SimpleType) resultingSimpleContentUnionB.getAllMemberTypes().getLast().getReference()).getInheritance()).getMaxInclusive().getValue());
            assertEquals("2", ((SimpleContentRestriction) ((SimpleType) resultingSimpleContentUnionB.getAllMemberTypes().getLast().getReference()).getInheritance()).getMinInclusive().getValue());
            assertEquals("10", ((SimpleContentRestriction) ((SimpleType) resultingSimpleContentUnionB.getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinInclusive().getValue());
            assertTrue(((SimpleContentRestriction) ((SimpleType) resultingSimpleContentUnionB.getAllMemberTypes().getLast().getReference()).getInheritance()).getBase() instanceof SimpleType);
            assertTrue(((SimpleContentRestriction) ((SimpleType) resultingSimpleContentUnionB.getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase() instanceof SimpleType);
            assertEquals("{http://www.w3.org/2001/XMLSchema}integer", ((SimpleContentRestriction) ((SimpleType) resultingSimpleContentUnionB.getAllMemberTypes().getLast().getReference()).getInheritance()).getBase().getName());
            assertEquals("{http://www.w3.org/2001/XMLSchema}integer", ((SimpleContentRestriction) ((SimpleType) resultingSimpleContentUnionB.getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName());
        }
    }
}