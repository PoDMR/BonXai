package de.tudortmund.cs.bonxai.xsd.setOperations.union;

import de.tudortmund.cs.bonxai.common.Annotation;
import de.tudortmund.cs.bonxai.common.AnyPattern;
import de.tudortmund.cs.bonxai.common.ChoicePattern;
import de.tudortmund.cs.bonxai.common.ProcessContentsInstruction;
import de.tudortmund.cs.bonxai.common.SequencePattern;
import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import de.tudortmund.cs.bonxai.xsd.*;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>TypeUnionGenerator</tt> class, checks that every
 * method of this class performs properly.
 * @author Dominik Wolff
 */
public class TypeUnionGeneratorTest extends junit.framework.TestCase {

    public TypeUnionGeneratorTest() {
    }

    /**
     * Test of generateNewSimpleType method, of class TypeUnionGenerator.
     */
    @Test
    public void testGenerateNewSimpleType() {
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypes.add(simpleTypeA);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        simpleTypes.add(simpleTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        String typeName = "{A}union-type.simpleTypeA.simpleTypeB";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getFinalModifiers());
        assertTrue(result.getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for
     * a build-in type.
     */
    @Test
    public void testGenerateNewSimpleTypeBuildIn() {
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        simpleTypes.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        String typeName = "{http://www.w3.org/2001/XMLSchema}string";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

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
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for
     * a single simpleType with same name as given name.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleTypeSameName() {
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypes.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        String typeName = "{A}simpleTypeA";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

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
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for
     * a single simpleType with a different name as given name.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleTypeDifferentName() {
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypes.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        String typeName = "{A}simpleTypeB";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getFinalModifiers());
        assertTrue(result.getInheritance() instanceof SimpleContentUnion);
        assertTrue(((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().size() == 1);
        assertTrue(((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertEquals("{A}simpleTypeA", ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference().getName());
        assertEquals(null, ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference().getId());
        assertEquals(null, ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference().getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getFinalModifiers());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference().getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference().getName()).getReference()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for
     * a simpleType set containing an any simple type.
     */
    @Test
    public void testGenerateNewSimpleTypeAnySimpleType() {
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypes.add(simpleTypeA);
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        simpleTypes.add(simpleTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        String typeName = "{A}union-type.simpleTypeA.anySimpleType";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}anySimpleType", result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getFinalModifiers());
        assertEquals(null, result.getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for
     * simpleTypes with ID.
     */
    @Test
    public void testGenerateNewSimpleTypeID() {
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.setId("idOne");
        simpleTypes.add(simpleTypeA);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        simpleTypeB.setId("idTwo");
        simpleTypes.add(simpleTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("idOne.idTwo");
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, usedIDs, null, null);

        String typeName = "{A}union-type.simpleTypeA.simpleTypeB";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals("idOne.idTwo.1", result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getFinalModifiers());
        assertTrue(result.getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for
     * simpleTypes with annotation.
     */
    @Test
    public void testGenerateNewSimpleTypeAnnotation() {
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        simpleTypeA.setAnnotation(annotationA);
        simpleTypes.add(simpleTypeA);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        Annotation annotationB = new Annotation();
        AppInfo appInfoB = new AppInfo();
        appInfoB.setSource("blaB");
        Documentation documentationB = new Documentation();
        documentationB.setSource("blaaB");
        annotationB.addAppInfos(appInfoB);
        annotationB.addDocumentations(documentationB);
        simpleTypeB.setAnnotation(annotationB);
        simpleTypes.add(simpleTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        String typeName = "{A}union-type.simpleTypeA.simpleTypeB";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertTrue(result.getAnnotation().getAppInfos().size() == 2);
        assertEquals("blaA", result.getAnnotation().getAppInfos().get(0).getSource());
        assertEquals("blaB", result.getAnnotation().getAppInfos().get(1).getSource());
        assertTrue(result.getAnnotation().getDocumentations().size() == 2);
        assertEquals("blaaA", result.getAnnotation().getDocumentations().get(0).getSource());
        assertEquals("blaaB", result.getAnnotation().getDocumentations().get(1).getSource());
        assertEquals(null, result.getFinalModifiers());
        assertTrue(result.getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for
     * simpleType with ID.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleTypeID() {
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.setId("idOne");
        simpleTypes.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("idOne");
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, usedIDs, null, null);

        String typeName = "{A}simpleTypeA";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

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
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for
     * simpleType with annotation.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleTypeAnnotation() {
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        simpleTypeA.setAnnotation(annotationA);
        simpleTypes.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        String typeName = "{A}simpleTypeA";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

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
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for
     * a single anonymous simpleType.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleAnonymousType() {
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.setIsAnonymous(true);
        simpleTypes.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        String typeName = "{A}simpleTypeA";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

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
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for a
     * set of anonymous types.
     */
    @Test
    public void testGenerateNewSimpleTypeAnonymousTypes() {
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.setIsAnonymous(true);
        simpleTypes.add(simpleTypeA);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        simpleTypeB.setIsAnonymous(true);
        simpleTypes.add(simpleTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        String typeName = "{A}union-type.simpleTypeA.simpleTypeB";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getFinalModifiers());
        assertTrue(result.getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for
     * a single anonymous simpleType from another schema.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleAnonymousTypeOtherSchema() {
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{}simpleTypeA", null);
        simpleTypeA.setIsAnonymous(true);
        simpleTypes.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema(""));
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        String typeName = "{A}simpleTypeA";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

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
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for a
     * simpleType set which finalizes list.
     */
    @Test
    public void testGenerateNewSimpleTypeFinalList() {
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.List);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.Restriction);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.Union);
        simpleTypes.add(simpleTypeA);

        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        simpleTypeB.addFinalModifier(SimpleTypeInheritanceModifier.List);
        simpleTypes.add(simpleTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        String typeName = "{A}union-type.simpleTypeA.simpleTypeB";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        HashSet<SimpleTypeInheritanceModifier> finalValue = new HashSet<SimpleTypeInheritanceModifier>();
        finalValue.add(SimpleTypeInheritanceModifier.List);
        assertEquals(finalValue, result.getFinalModifiers());
        assertTrue(result.getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for a
     * simpleType set which finalizes restriction.
     */
    @Test
    public void testGenerateNewSimpleTypeFinalRestriction() {
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.List);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.Restriction);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.Union);
        simpleTypes.add(simpleTypeA);

        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        simpleTypeB.addFinalModifier(SimpleTypeInheritanceModifier.Restriction);
        simpleTypes.add(simpleTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        String typeName = "{A}union-type.simpleTypeA.simpleTypeB";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        HashSet<SimpleTypeInheritanceModifier> finalValue = new HashSet<SimpleTypeInheritanceModifier>();
        finalValue.add(SimpleTypeInheritanceModifier.Restriction);
        assertEquals(finalValue, result.getFinalModifiers());
        assertTrue(result.getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for a
     * simpleType set which finalizes union.
     */
    @Test
    public void testGenerateNewSimpleTypeFinalUnion() {
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.List);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.Restriction);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.Union);
        simpleTypes.add(simpleTypeA);

        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        simpleTypeB.addFinalModifier(SimpleTypeInheritanceModifier.Union);
        simpleTypes.add(simpleTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        String typeName = "{A}union-type.simpleTypeA.simpleTypeB";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        HashSet<SimpleTypeInheritanceModifier> finalValue = new HashSet<SimpleTypeInheritanceModifier>();
        finalValue.add(SimpleTypeInheritanceModifier.Union);
        assertEquals(finalValue, result.getFinalModifiers());
        assertTrue(result.getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for a
     * simpleType set which finalizes list.
     */
    @Test
    public void testGenerateNewSimpleTypeFinalDefaultList() {
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.List);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.Union);
        simpleTypes.add(simpleTypeA);

        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        simpleTypes.add(simpleTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        outputSchema.addFinalDefault(XSDSchema.FinalDefault.list);
        outputSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.list);
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        typeOldSchemaMap.put(simpleTypeA, oldSchema);
        typeOldSchemaMap.put(simpleTypeB, oldSchema);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        String typeName = "{A}union-type.simpleTypeA.simpleTypeB";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        HashSet<SimpleTypeInheritanceModifier> finalValue = new HashSet<SimpleTypeInheritanceModifier>();
        finalValue.add(SimpleTypeInheritanceModifier.List);
        assertEquals(finalValue, result.getFinalModifiers());
        assertTrue(result.getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for a
     * simpleType set which finalizes restriction.
     */
    @Test
    public void testGenerateNewSimpleTypeFinalDefaultRestriction() {
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.List);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.Restriction);
        simpleTypes.add(simpleTypeA);

        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        simpleTypes.add(simpleTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        outputSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        outputSchema.addFinalDefault(XSDSchema.FinalDefault.union);
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.union);
        typeOldSchemaMap.put(simpleTypeA, oldSchema);
        typeOldSchemaMap.put(simpleTypeB, oldSchema);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        String typeName = "{A}union-type.simpleTypeA.simpleTypeB";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        HashSet<SimpleTypeInheritanceModifier> finalValue = new HashSet<SimpleTypeInheritanceModifier>();
        finalValue.add(SimpleTypeInheritanceModifier.Restriction);
        assertEquals(finalValue, result.getFinalModifiers());
        assertTrue(result.getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for a
     * simpleType set which finalizes union.
     */
    @Test
    public void testGenerateNewSimpleTypeFinalDefaultUnion() {
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.List);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.Union);
        simpleTypes.add(simpleTypeA);

        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        simpleTypes.add(simpleTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        outputSchema.addFinalDefault(XSDSchema.FinalDefault.union);
        outputSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.union);
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        typeOldSchemaMap.put(simpleTypeA, oldSchema);
        typeOldSchemaMap.put(simpleTypeB, oldSchema);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        String typeName = "{A}union-type.simpleTypeA.simpleTypeB";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        HashSet<SimpleTypeInheritanceModifier> finalValue = new HashSet<SimpleTypeInheritanceModifier>();
        finalValue.add(SimpleTypeInheritanceModifier.Union);
        assertEquals(finalValue, result.getFinalModifiers());
        assertTrue(result.getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for a
     * simpleType set which finalizes union which is schema default.
     */
    @Test
    public void testGenerateNewSimpleTypeFinalDefault() {
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.List);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.Union);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.Restriction);
        simpleTypes.add(simpleTypeA);

        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        simpleTypes.add(simpleTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        outputSchema.addFinalDefault(XSDSchema.FinalDefault.union);
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.union);
        typeOldSchemaMap.put(simpleTypeA, oldSchema);
        typeOldSchemaMap.put(simpleTypeB, oldSchema);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        String typeName = "{A}union-type.simpleTypeA.simpleTypeB";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getFinalModifiers());
        assertTrue(result.getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for a
     * simpleType which finalizes list.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleTypeFinalList() {
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.List);
        simpleTypes.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        String typeName = "{A}union-type.simpleTypeA.simpleTypeB";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        HashSet<SimpleTypeInheritanceModifier> finalValue = new HashSet<SimpleTypeInheritanceModifier>();
        finalValue.add(SimpleTypeInheritanceModifier.List);
        assertEquals(finalValue, result.getFinalModifiers());
        assertTrue(result.getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for a
     * simpleType which finalizes restriction.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleTypeFinalRestriction() {
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.Restriction);
        simpleTypes.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        String typeName = "{A}union-type.simpleTypeA.simpleTypeB";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        HashSet<SimpleTypeInheritanceModifier> finalValue = new HashSet<SimpleTypeInheritanceModifier>();
        finalValue.add(SimpleTypeInheritanceModifier.Restriction);
        assertEquals(finalValue, result.getFinalModifiers());
        assertTrue(result.getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for a
     * simpleType which finalizes union.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleTypeFinalUnion() {
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.Union);
        simpleTypes.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        String typeName = "{A}union-type.simpleTypeA.simpleTypeB";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        HashSet<SimpleTypeInheritanceModifier> finalValue = new HashSet<SimpleTypeInheritanceModifier>();
        finalValue.add(SimpleTypeInheritanceModifier.Union);
        assertEquals(finalValue, result.getFinalModifiers());
        assertTrue(result.getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for a
     * simpleType which finalizes union which is schema default.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleTypeFinalDefault() {
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypes.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        outputSchema.addFinalDefault(XSDSchema.FinalDefault.union);
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.union);
        typeOldSchemaMap.put(simpleTypeA, oldSchema);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        String typeName = "{A}union-type.simpleTypeA.simpleTypeB";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getFinalModifiers());
        assertTrue(result.getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for
     * a set of simpleTypes which finalize union.
     */
    @Test
    public void testGenerateNewSimpleTypeRemoveFinalUnion() {
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypes.add(simpleTypeA);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.List);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.Union);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        simpleTypeB.addFinalModifier(SimpleTypeInheritanceModifier.Union);
        simpleTypes.add(simpleTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        String typeName = "{A}union-type.simpleTypeA.simpleTypeB";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        HashSet<SimpleTypeInheritanceModifier> finalValue = new HashSet<SimpleTypeInheritanceModifier>();
        finalValue.add(SimpleTypeInheritanceModifier.Union);
        assertEquals(finalValue, result.getFinalModifiers());
        assertTrue(result.getInheritance() instanceof SimpleContentUnion);
        assertTrue(((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        HashSet<SimpleTypeInheritanceModifier> finalValueA = new HashSet<SimpleTypeInheritanceModifier>();
        finalValueA.add(SimpleTypeInheritanceModifier.List);
        assertEquals(finalValueA, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getFinalModifiers());
        assertTrue(((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference() instanceof SimpleType);
        HashSet<SimpleTypeInheritanceModifier> finalValueB = new HashSet<SimpleTypeInheritanceModifier>();
        assertEquals(finalValueB, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getFinalModifiers());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for a
     * set of simpleTypes containing same types.
     */
    @Test
    public void testGenerateNewSimpleTypeRemoveTypes() {
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        simpleTypes.add(simpleTypeA);
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        simpleTypes.add(simpleTypeB);
        SimpleType simpleTypeC = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        simpleTypes.add(simpleTypeC);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeC, new XSDSchema("A"));
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        String typeName = "{A}union-type.string.integer";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

        assertTrue(result instanceof SimpleType);
        assertEquals(typeName, result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(null, result.getFinalModifiers());
        assertTrue(result.getInheritance() instanceof SimpleContentUnion);
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
        assertEquals("{http://www.w3.org/2001/XMLSchema}integer", ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getName());
        assertEquals(false, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).isAnonymous());
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
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for
     * a single list type.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleTypeList() {
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentList simpleContentList = new SimpleContentList(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", simpleContentList);
        simpleTypes.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        String typeName = "{A}simpleTypeA";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

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
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for
     * a single list type with ID.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleTypeListID() {
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentList simpleContentList = new SimpleContentList(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        simpleContentList.setId("id");
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", simpleContentList);
        simpleTypes.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, usedIDs, null, null);

        String typeName = "{A}simpleTypeA";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

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
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for
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
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", simpleContentList);
        simpleTypes.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        String typeName = "{A}simpleTypeA";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

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
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for
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

        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeC = new SimpleType("{A}simpleTypeA", simpleContentUnion);
        simpleTypes.add(simpleTypeC);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeC, new XSDSchema("A"));
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        String typeName = "{A}simpleTypeA";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

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
        assertEquals("{A}simpleTypeB", ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getName());
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
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for
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

        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeC = new SimpleType("{A}simpleTypeA", simpleContentUnion);
        simpleTypes.add(simpleTypeC);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeC, new XSDSchema("A"));
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, usedIDs, null, null);

        String typeName = "{A}simpleTypeA";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

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
        assertEquals("{A}simpleTypeB", ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getName());
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
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for
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

        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeC = new SimpleType("{A}simpleTypeA", simpleContentUnion);
        simpleTypes.add(simpleTypeC);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeC, new XSDSchema("A"));
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        String typeName = "{A}simpleTypeA";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

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
        assertEquals("{A}simpleTypeB", ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getName());
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
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for
     * a single restriction type.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleTypeRestriction() {
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", simpleContentRestriction);
        simpleTypes.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        String typeName = "{A}simpleTypeA";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

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
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for
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
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", simpleContentRestriction);
        simpleTypes.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        String typeName = "{A}simpleTypeA";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

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
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for
     * a single restriction type with ID.
     */
    @Test
    public void testGenerateNewSimpleTypeSingleTypeRestrictionID() {
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        simpleContentRestriction.setId("id");
        LinkedList<SimpleType> simpleTypes = new LinkedList<SimpleType>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", simpleContentRestriction);
        simpleTypes.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, usedIDs, null, null);

        String typeName = "{A}simpleTypeA";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

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
     * Test of generateNewSimpleType method, of class TypeUnionGenerator for
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
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        String typeName = "{A}simpleTypeA";
        SimpleType result = instance.generateNewSimpleType(simpleTypes, typeName);

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
     * Test of generateNewTopLevelType method, of class TypeUnionGenerator.
     */
    @Test
    public void testGenerateNewTopLevelType() {
        Type topLevelType = new SimpleType("{A}simpleTypeA", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(topLevelType, new XSDSchema("A"));
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        instance.generateNewTopLevelType(topLevelType);

        assertTrue(outputSchema.getTypes().size() == 1);
        assertTrue(outputSchema.getTypes().getFirst() instanceof SimpleType);
        assertEquals("{A}simpleTypeA", ((SimpleType) outputSchema.getTypes().getFirst()).getName());
        assertEquals(null, ((SimpleType) outputSchema.getTypes().getFirst()).getId());
        assertEquals(null, ((SimpleType) outputSchema.getTypes().getFirst()).getAnnotation());
        assertEquals(null, ((SimpleType) outputSchema.getTypes().getFirst()).getFinalModifiers());
        assertEquals(null, ((SimpleType) outputSchema.getTypes().getFirst()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) outputSchema.getTypes().getFirst()).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) outputSchema.getTypes().getFirst()).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeUnionGenerator for
     * a build-in type.
     */
    @Test
    public void testGenerateNewTopLevelTypeBuildIn() {
        Type topLevelType = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(topLevelType, new XSDSchema("A"));
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        instance.generateNewTopLevelType(topLevelType);

        assertTrue(outputSchema.getTypes().size() == 0);
        assertTrue(outputSchema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string").getReference() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", (outputSchema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string").getReference()).getName());
        assertEquals(null, (outputSchema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string").getReference()).getId());
        assertEquals(null, (outputSchema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string").getReference()).getAnnotation());
        assertEquals(null, ((SimpleType) outputSchema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string").getReference()).getFinalModifiers());
        assertEquals(null, ((SimpleType) outputSchema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string").getReference()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference("{http://www.w3.org/2001/XMLSchema}string"));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference("{http://www.w3.org/2001/XMLSchema}string").getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeUnionGenerator for
     * an already existing top-level type.
     */
    @Test
    public void testGenerateNewTopLevelTypeAlreadyExisting() {
        Type topLevelTypeA = new SimpleType("{A}simpleTypeA", null);
        Type topLevelTypeB = new SimpleType("{A}simpleTypeA", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(topLevelTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(topLevelTypeB, new XSDSchema("A"));
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        instance.generateNewTopLevelType(topLevelTypeA);
        instance.generateNewTopLevelType(topLevelTypeB);

        assertTrue(outputSchema.getTypes().size() == 1);
        assertTrue(outputSchema.getTypes().getFirst() instanceof SimpleType);
        assertEquals("{A}simpleTypeA", ((SimpleType) outputSchema.getTypes().getFirst()).getName());
        assertEquals(null, ((SimpleType) outputSchema.getTypes().getFirst()).getId());
        assertEquals(null, ((SimpleType) outputSchema.getTypes().getFirst()).getAnnotation());
        assertEquals(null, ((SimpleType) outputSchema.getTypes().getFirst()).getFinalModifiers());
        assertEquals(null, ((SimpleType) outputSchema.getTypes().getFirst()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) outputSchema.getTypes().getFirst()).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) outputSchema.getTypes().getFirst()).getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a
     * simpleType.
     */
    @Test
    public void testGenerateNewType_TypeSimpleType() {
        SimpleType oldType = new SimpleType("{A}simpleTypeA", null);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        Type result = instance.generateNewType(oldType);

        assertTrue(result instanceof SimpleType);
        assertEquals(oldType.getName(), result.getName());
        assertEquals(false, result.isAnonymous());
        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertEquals(null, ((SimpleType) result).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a
     * complexType.
     */
    @Test
    public void testGenerateNewType_TypeComplexType() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        Type result = instance.generateNewType(oldType);

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
     * Test of generateNewType method, of class TypeUnionGenerator for a
     * complexType with ID.
     */
    @Test
    public void testGenerateNewType_TypeComplexTypeID() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.setId("id");
        oldType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, usedIDs, attributeParticleUnionGenerator, particleUnionGenerator);

        Type result = instance.generateNewType(oldType);

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
     * Test of generateNewType method, of class TypeUnionGenerator for a
     * complexType with annotation.
     */
    @Test
    public void testGenerateNewType_TypeComplexTypeAnnotation() {
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
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        Type result = instance.generateNewType(oldType);

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
     * Test of generateNewType method, of class TypeUnionGenerator for an
     * abstract complexType.
     */
    @Test
    public void testGenerateNewType_TypeComplexTypeAbstract() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.setAbstract(true);
        oldType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        Type result = instance.generateNewType(oldType);

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
     * Test of generateNewType method, of class TypeUnionGenerator for an
     * anonymous complexType.
     */
    @Test
    public void testGenerateNewType_TypeComplexTypeAnonymous() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.setIsAnonymous(true);
        oldType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        Type result = instance.generateNewType(oldType);

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
        assertFalse(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a
     * mixed complexType.
     */
    @Test
    public void testGenerateNewType_TypeComplexTypeMixed() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.setMixed(true);
        oldType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        Type result = instance.generateNewType(oldType);

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
     * Test of generateNewType method, of class TypeUnionGenerator for a
     * complexType with attributes.
     */
    @Test
    public void testGenerateNewType_TypeComplexTypeAttributes() {
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
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        Type result = instance.generateNewType(oldType);

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
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) ((ComplexType) result).getAttributes().getFirst()).getForm());
        assertEquals(null, ((Attribute) ((ComplexType) result).getAttributes().getFirst()).getId());
        assertEquals(null, ((Attribute) ((ComplexType) result).getAttributes().getFirst()).getAnnotation());
        assertEquals(false, (boolean) ((Attribute) ((ComplexType) result).getAttributes().getFirst()).getTypeAttr());
        assertEquals(null, ((Attribute) ((ComplexType) result).getAttributes().getFirst()).getSimpleType());
        assertEquals(AttributeUse.Optional, ((Attribute) ((ComplexType) result).getAttributes().getFirst()).getUse());
        assertTrue(((ComplexType) result).getAttributes().getLast() instanceof Attribute);
        assertEquals(attributeB.getName(), ((Attribute) ((ComplexType) result).getAttributes().getLast()).getName());
        assertEquals(null, ((Attribute) ((ComplexType) result).getAttributes().getLast()).getFixed());
        assertEquals(null, ((Attribute) ((ComplexType) result).getAttributes().getLast()).getDefault());
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) ((ComplexType) result).getAttributes().getLast()).getForm());
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
     * Test of generateNewType method, of class TypeUnionGenerator for a
     * complexType which blocks extension.
     */
    @Test
    public void testGenerateNewType_TypeComplexTypeBlockExtension() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        HashSet<ComplexTypeInheritanceModifier> complexTypeInheritanceModifierSet = new HashSet<ComplexTypeInheritanceModifier>();
        complexTypeInheritanceModifierSet.add(ComplexTypeInheritanceModifier.Extension);
        oldType.setBlockModifiers(complexTypeInheritanceModifierSet);
        oldType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        Type result = instance.generateNewType(oldType);

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
     * Test of generateNewType method, of class TypeUnionGenerator for a
     * complexType which blocks restriction.
     */
    @Test
    public void testGenerateNewType_TypeComplexTypeBlockRestriction() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        HashSet<ComplexTypeInheritanceModifier> complexTypeInheritanceModifierSet = new HashSet<ComplexTypeInheritanceModifier>();
        complexTypeInheritanceModifierSet.add(ComplexTypeInheritanceModifier.Restriction);
        oldType.setBlockModifiers(complexTypeInheritanceModifierSet);
        oldType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        Type result = instance.generateNewType(oldType);

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
     * Test of generateNewType method, of class TypeUnionGenerator for a
     * complexType which blocks extension per schema default.
     */
    @Test
    public void testGenerateNewType_TypeComplexTypeBlockDefaultExtension() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.extension);
        typeOldSchemaMap.put(oldType, oldSchema);
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        Type result = instance.generateNewType(oldType);

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
     * Test of generateNewType method, of class TypeUnionGenerator for a
     * complexType which blocks restriction per schema default.
     */
    @Test
    public void testGenerateNewType_TypeComplexTypeBlockDefaultRestriction() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.restriction);
        typeOldSchemaMap.put(oldType, oldSchema);
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        Type result = instance.generateNewType(oldType);

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
     * Test of generateNewType method, of class TypeUnionGenerator for a
     * complexType which finalizes extension.
     */
    @Test
    public void testGenerateNewType_TypeComplexTypeFinalExtension() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        HashSet<ComplexTypeInheritanceModifier> complexTypeInheritanceModifierSet = new HashSet<ComplexTypeInheritanceModifier>();
        complexTypeInheritanceModifierSet.add(ComplexTypeInheritanceModifier.Extension);
        oldType.setFinalModifiers(complexTypeInheritanceModifierSet);
        oldType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        Type result = instance.generateNewType(oldType);

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
     * Test of generateNewType method, of class TypeUnionGenerator for a
     * complexType which finalizes restriction.
     */
    @Test
    public void testGenerateNewType_TypeComplexTypeFinalRestriction() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        HashSet<ComplexTypeInheritanceModifier> complexTypeInheritanceModifierSet = new HashSet<ComplexTypeInheritanceModifier>();
        complexTypeInheritanceModifierSet.add(ComplexTypeInheritanceModifier.Restriction);
        oldType.setFinalModifiers(complexTypeInheritanceModifierSet);
        oldType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        Type result = instance.generateNewType(oldType);

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
     * Test of generateNewType method, of class TypeUnionGenerator for a
     * complexType which finalizes extension per schema default.
     */
    @Test
    public void testGenerateNewType_TypeComplexTypeFinalDefaultExtension() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.extension);
        typeOldSchemaMap.put(oldType, oldSchema);
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        Type result = instance.generateNewType(oldType);

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
     * Test of generateNewType method, of class TypeUnionGenerator for a
     * complexType which finalizes restriction per schema default.
     */
    @Test
    public void testGenerateNewType_TypeComplexTypeFinalDefaultRestriction() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        typeOldSchemaMap.put(oldType, oldSchema);
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        Type result = instance.generateNewType(oldType);

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
     * Test of generateNewType method, of class TypeUnionGenerator for a
     * complexType with complex content ID.
     */
    @Test
    public void testGenerateNewType_TypeComplexTypeComplexContentID() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        complexContentType.setId("id");
        oldType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("id");
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, usedIDs, attributeParticleUnionGenerator, particleUnionGenerator);

        Type result = instance.generateNewType(oldType);

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
     * Test of generateNewType method, of class TypeUnionGenerator for a
     * complexType with complex content annotation.
     */
    @Test
    public void testGenerateNewType_TypeComplexTypeComplexContentAnnotation() {
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
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        Type result = instance.generateNewType(oldType);

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
     * Test of generateNewType method, of class TypeUnionGenerator for a
     * mixed complexType with complex content.
     */
    @Test
    public void testGenerateNewType_TypeComplexTypeComplexContentMixed() {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.setMixed(true);
        oldType.addAttribute(new Attribute("{A}attribute"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        Type result = instance.generateNewType(oldType);

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
     * Test of generateNewType method, of class TypeUnionGenerator for a
     * complexType with simple content.
     */
    @Test
    public void testGenerateNewType_TypeComplexTypeSimpleContent() {
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
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        Type result = instance.generateNewType(oldType);

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
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getBase() instanceof SimpleType);
        assertEquals(simpleTypeA.getName(), ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getBase().getName());
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().size() == 1);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().getLast() instanceof Attribute);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a
     * complexType with simple content with content ID.
     */
    @Test
    public void testGenerateNewType_TypeComplexTypeSimpleContentContentID() {
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
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, usedIDs, attributeParticleUnionGenerator, particleUnionGenerator);

        Type result = instance.generateNewType(oldType);

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
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getBase() instanceof SimpleType);
        assertEquals(simpleTypeA.getName(), ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getBase().getName());
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().size() == 1);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().getLast() instanceof Attribute);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a
     * complexType with simple content with content annotation.
     */
    @Test
    public void testGenerateNewType_TypeComplexTypeSimpleContentContentAnnotation() {
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
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        Type result = instance.generateNewType(oldType);

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
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getBase() instanceof SimpleType);
        assertEquals(simpleTypeA.getName(), ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getBase().getName());
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().size() == 1);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().getLast() instanceof Attribute);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a
     * complexType with simple content with inheritance ID.
     */
    @Test
    public void testGenerateNewType_TypeComplexTypeSimpleContentInheritanceID() {
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
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, usedIDs, attributeParticleUnionGenerator, particleUnionGenerator);

        Type result = instance.generateNewType(oldType);

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
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getBase() instanceof SimpleType);
        assertEquals(simpleTypeA.getName(), ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getBase().getName());
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().size() == 1);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().getLast() instanceof Attribute);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a
     * complexType with simple content with inheritance annotation.
     */
    @Test
    public void testGenerateNewType_TypeComplexTypeSimpleContentInheritanceAnnotation() {
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
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        Type result = instance.generateNewType(oldType);

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
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getBase() instanceof SimpleType);
        assertEquals(simpleTypeA.getName(), ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getBase().getName());
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().size() == 1);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().getLast() instanceof Attribute);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of simpleTypes.
     */
    @Test
    public void testGenerateNewType_3argsSimpleTypes() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        types.add(simpleTypeA);
        types.add(simpleTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String typeName = "{A}union-type.simpleTypeA.simpleTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof SimpleType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((SimpleType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertTrue(((SimpleType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of simpleTypes and an anyType.
     */
    @Test
    public void testGenerateNewType_3argsSimpleTypesAnyType() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        SimpleType simpleTypeC = new SimpleType("{http://www.w3.org/2001/XMLSchema}anyType", null);
        types.add(simpleTypeA);
        types.add(simpleTypeB);
        types.add(simpleTypeC);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String typeName = "{http://www.w3.org/2001/XMLSchema}anyType";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, null, null);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof SimpleType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((SimpleType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(null, ((SimpleType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complexTypes.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypes() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complexTypes and an anyType.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypesAnyType() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        SimpleType simpleTypeC = new SimpleType("{http://www.w3.org/2001/XMLSchema}anyType", null);
        types.add(complexTypeA);
        types.add(complexTypeB);
        types.add(simpleTypeC);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String typeName = "{http://www.w3.org/2001/XMLSchema}anyType";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof SimpleType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((SimpleType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(null, ((SimpleType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complexTypes with IDs.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypesID() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        complexTypeA.setId("idOne");
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        complexTypeB.setId("idTwo");
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("idOne.idTwo");
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, usedIDs, attributeParticleUnionGenerator, particleUnionGenerator);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals("idOne.idTwo.1", outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complexTypes with annotations.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypesAnnotations() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        complexTypeA.setAnnotation(annotationA);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        Annotation annotationB = new Annotation();
        AppInfo appInfoB = new AppInfo();
        appInfoB.setSource("blaB");
        Documentation documentationB = new Documentation();
        documentationB.setSource("blaaB");
        annotationB.addAppInfos(appInfoB);
        annotationB.addDocumentations(documentationB);
        complexTypeB.setAnnotation(annotationB);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("idOne.idTwo");
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, usedIDs, attributeParticleUnionGenerator, particleUnionGenerator);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals("blaA", outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation().getAppInfos().get(0).getSource());
        assertEquals("blaB", outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation().getAppInfos().get(1).getSource());
        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation().getDocumentations().size() == 2);
        assertEquals("blaaA", outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation().getDocumentations().get(0).getSource());
        assertEquals("blaaB", outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation().getDocumentations().get(1).getSource());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of abstract complexTypes.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypesAbstract() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        complexTypeA.setAbstract(true);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        complexTypeB.setAbstract(true);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(true, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).isAbstract());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of anonymous complexTypes.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypesAnonymous() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        complexTypeA.setIsAnonymous(true);
        types.add(complexTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(true, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of mixed complexTypes.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypesMixed() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        complexTypeA.setMixed(true);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        complexTypeB.setMixed(true);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(true, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complexTypes which blocks extension.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypesBlockExtension() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        complexTypeA.addBlockModifier(ComplexTypeInheritanceModifier.Extension);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        complexTypeB.addBlockModifier(ComplexTypeInheritanceModifier.Extension);
        complexTypeB.addBlockModifier(ComplexTypeInheritanceModifier.Restriction);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        HashSet<ComplexTypeInheritanceModifier> block = new HashSet<ComplexTypeInheritanceModifier>();
        block.add(ComplexTypeInheritanceModifier.Extension);
        assertEquals(block, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complexTypes which blocks restriction.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypesBlockRestriction() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        complexTypeA.addBlockModifier(ComplexTypeInheritanceModifier.Restriction);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        complexTypeB.addBlockModifier(ComplexTypeInheritanceModifier.Extension);
        complexTypeB.addBlockModifier(ComplexTypeInheritanceModifier.Restriction);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        HashSet<ComplexTypeInheritanceModifier> block = new HashSet<ComplexTypeInheritanceModifier>();
        block.add(ComplexTypeInheritanceModifier.Restriction);
        assertEquals(block, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complexTypes which blocks extension per default.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypesBlockDefaultExtension() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        complexTypeB.addBlockModifier(ComplexTypeInheritanceModifier.Extension);
        complexTypeB.addBlockModifier(ComplexTypeInheritanceModifier.Restriction);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.extension);
        typeOldSchemaMap.put(complexTypeA, oldSchema);
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        HashSet<ComplexTypeInheritanceModifier> block = new HashSet<ComplexTypeInheritanceModifier>();
        block.add(ComplexTypeInheritanceModifier.Extension);
        assertEquals(block, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complexTypes which blocks restriction per default.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypesBlockDefaultRestriction() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        complexTypeB.addBlockModifier(ComplexTypeInheritanceModifier.Extension);
        complexTypeB.addBlockModifier(ComplexTypeInheritanceModifier.Restriction);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.restriction);
        typeOldSchemaMap.put(complexTypeA, oldSchema);
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        HashSet<ComplexTypeInheritanceModifier> block = new HashSet<ComplexTypeInheritanceModifier>();
        block.add(ComplexTypeInheritanceModifier.Restriction);
        assertEquals(block, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complexTypes which blocks restriction like the output schema default.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypesBlockSchemaDefault() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        complexTypeB.addBlockModifier(ComplexTypeInheritanceModifier.Extension);
        complexTypeB.addBlockModifier(ComplexTypeInheritanceModifier.Restriction);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.addBlockDefault(XSDSchema.BlockDefault.restriction);
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.restriction);
        typeOldSchemaMap.put(complexTypeA, oldSchema);
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complexTypes which finalizes extension.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypesFinalExtension() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        complexTypeA.addFinalModifier(ComplexTypeInheritanceModifier.Extension);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        complexTypeB.addFinalModifier(ComplexTypeInheritanceModifier.Extension);
        complexTypeB.addFinalModifier(ComplexTypeInheritanceModifier.Restriction);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        HashSet<ComplexTypeInheritanceModifier> finals = new HashSet<ComplexTypeInheritanceModifier>();
        finals.add(ComplexTypeInheritanceModifier.Extension);
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(finals, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complexTypes which finalizes restriction.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypesFinalRestriction() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        complexTypeA.addFinalModifier(ComplexTypeInheritanceModifier.Restriction);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        complexTypeB.addFinalModifier(ComplexTypeInheritanceModifier.Extension);
        complexTypeB.addFinalModifier(ComplexTypeInheritanceModifier.Restriction);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        HashSet<ComplexTypeInheritanceModifier> finals = new HashSet<ComplexTypeInheritanceModifier>();
        finals.add(ComplexTypeInheritanceModifier.Restriction);
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(finals, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complexTypes which finalizes extension per default.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypesFinalDefaultExtension() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        complexTypeB.addFinalModifier(ComplexTypeInheritanceModifier.Extension);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        outputSchema.addFinalDefault(XSDSchema.FinalDefault.extension);
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.extension);
        typeOldSchemaMap.put(complexTypeA, oldSchema);
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        HashSet<ComplexTypeInheritanceModifier> finals = new HashSet<ComplexTypeInheritanceModifier>();
        finals.add(ComplexTypeInheritanceModifier.Extension);
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(finals, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complexTypes which finalizes restriction per default.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypesFinalDefaultRestriction() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        complexTypeB.addFinalModifier(ComplexTypeInheritanceModifier.Restriction);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        outputSchema.addFinalDefault(XSDSchema.FinalDefault.extension);
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.extension);
        typeOldSchemaMap.put(complexTypeA, oldSchema);
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        HashSet<ComplexTypeInheritanceModifier> finals = new HashSet<ComplexTypeInheritanceModifier>();
        finals.add(ComplexTypeInheritanceModifier.Restriction);
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(finals, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complexTypes which finalizes restriction like the output schema default.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypesFinalSchemaDefault() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        complexTypeB.addFinalModifier(ComplexTypeInheritanceModifier.Extension);
        complexTypeB.addFinalModifier(ComplexTypeInheritanceModifier.Restriction);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        typeOldSchemaMap.put(complexTypeA, oldSchema);
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complexTypes with attributes.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypesAttributes() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        complexTypeA.addAttribute(new Attribute("{A}attributeA"));
        complexTypeA.addAttribute(new Attribute("{A}attributeB"));
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        complexTypeB.addAttribute(new Attribute("{A}attributeC"));
        complexTypeB.addAttribute(new Attribute("{A}attributeD"));
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().size() == 4);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(0) instanceof Attribute);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(1) instanceof Attribute);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(2) instanceof Attribute);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(3) instanceof Attribute);
        assertEquals("{A}attributeA", ((Attribute) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(0)).getName());
        assertEquals("{A}attributeB", ((Attribute) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(1)).getName());
        assertEquals("{A}attributeC", ((Attribute) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(2)).getName());
        assertEquals("{A}attributeD", ((Attribute) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(3)).getName());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complexTypes with complex content.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypesComplexContent() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SequencePattern sequencePatternA = new SequencePattern();
        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Lax, "A");
        sequencePatternA.addParticle(anyPattern);
        ComplexContentType complexContentTypeA = new ComplexContentType(sequencePatternA);
        SequencePattern sequencePatternB = new SequencePattern();
        sequencePatternB.addParticle(anyPattern);
        ComplexContentType complexContentTypeB = new ComplexContentType(sequencePatternB);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", complexContentTypeA);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", complexContentTypeB);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPattern, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertNotNull(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getMixed());
        assertTrue(((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getParticle() instanceof ChoicePattern);
        assertTrue(((ChoicePattern) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getParticle()).getParticles().size() == 2);
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complexTypes with complex content IDs.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypesComplexContentID() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SequencePattern sequencePatternA = new SequencePattern();
        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Lax, "A");
        sequencePatternA.addParticle(anyPattern);
        ComplexContentType complexContentTypeA = new ComplexContentType(sequencePatternA);
        complexContentTypeA.setId("idOne");
        SequencePattern sequencePatternB = new SequencePattern();
        sequencePatternB.addParticle(anyPattern);
        ComplexContentType complexContentTypeB = new ComplexContentType(sequencePatternB);
        complexContentTypeB.setId("idTwo");
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", complexContentTypeA);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", complexContentTypeB);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPattern, new XSDSchema("A"));
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("idOne.idTwo");
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, usedIDs, attributeParticleUnionGenerator, particleUnionGenerator);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertNotNull(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation());
        assertEquals("idOne.idTwo.1", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getMixed());
        assertTrue(((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getParticle() instanceof ChoicePattern);
        assertTrue(((ChoicePattern) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getParticle()).getParticles().size() == 2);
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complexTypes with complex content annotations.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypesComplexContentAnnotation() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SequencePattern sequencePatternA = new SequencePattern();
        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Lax, "A");
        sequencePatternA.addParticle(anyPattern);
        ComplexContentType complexContentTypeA = new ComplexContentType(sequencePatternA);
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        complexContentTypeA.setAnnotation(annotationA);
        SequencePattern sequencePatternB = new SequencePattern();
        sequencePatternB.addParticle(anyPattern);
        ComplexContentType complexContentTypeB = new ComplexContentType(sequencePatternB);
        Annotation annotationB = new Annotation();
        AppInfo appInfoB = new AppInfo();
        appInfoB.setSource("blaB");
        Documentation documentationB = new Documentation();
        documentationB.setSource("blaaB");
        annotationB.addAppInfos(appInfoB);
        annotationB.addDocumentations(documentationB);
        complexContentTypeB.setAnnotation(annotationB);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", complexContentTypeA);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", complexContentTypeB);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPattern, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertNotNull(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation().getAppInfos().size() == 2);
        assertEquals("blaA", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation().getAppInfos().get(0).getSource());
        assertEquals("blaB", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation().getAppInfos().get(1).getSource());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation().getDocumentations().size() == 2);
        assertEquals("blaaA", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation().getDocumentations().get(0).getSource());
        assertEquals("blaaB", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation().getDocumentations().get(1).getSource());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getMixed());
        assertTrue(((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getParticle() instanceof ChoicePattern);
        assertTrue(((ChoicePattern) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getParticle()).getParticles().size() == 2);
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complexTypes with mixed complex content.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypesComplexContentMixed() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SequencePattern sequencePatternA = new SequencePattern();
        AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Lax, "A");
        sequencePatternA.addParticle(anyPattern);
        ComplexContentType complexContentTypeA = new ComplexContentType(sequencePatternA);
        complexContentTypeA.setMixed(true);
        SequencePattern sequencePatternB = new SequencePattern();
        sequencePatternB.addParticle(anyPattern);
        ComplexContentType complexContentTypeB = new ComplexContentType(sequencePatternB);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", complexContentTypeA);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", complexContentTypeB);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        anyPatternOldSchemaMap.put(anyPattern, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, anyPatternOldSchemaMap, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertNotNull(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance());
        assertEquals(true, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getMixed());
        assertTrue(((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getParticle() instanceof ChoicePattern);
        assertTrue(((ChoicePattern) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getParticle()).getParticles().size() == 2);
        assertEquals(true, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complexTypes with simple content.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypesSimpleContent() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleContentType simpleContentTypeA = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtensionA = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentExtensionA.addAttribute(new Attribute("{A}attributeA"));
        simpleContentTypeA.setInheritance(simpleContentExtensionA);
        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        simpleContentExtensionB.addAttribute(new Attribute("{A}attributeB"));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", simpleContentTypeA);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertNotNull(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getId());
        assertNotNull(((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAnnotation());
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAttributes().size() == 2);
        assertEquals("{A}union-type.simpleTypeA.simpleTypeB", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getName());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complexTypes with simple content IDs.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypesSimpleContentID() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleContentType simpleContentTypeA = new SimpleContentType();
        simpleContentTypeA.setId("idOne");
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtensionA = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentExtensionA.addAttribute(new Attribute("{A}attributeA"));
        simpleContentTypeA.setInheritance(simpleContentExtensionA);
        SimpleContentType simpleContentTypeB = new SimpleContentType();
        simpleContentTypeB.setId("idTwo");
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        simpleContentExtensionB.addAttribute(new Attribute("{A}attributeB"));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", simpleContentTypeA);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("idOne.idTwo");
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, usedIDs, attributeParticleUnionGenerator, particleUnionGenerator);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertNotNull(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation());
        assertEquals("idOne.idTwo.1", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getId());
        assertNotNull(((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAnnotation());
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAttributes().size() == 2);
        assertEquals("{A}union-type.simpleTypeA.simpleTypeB", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getName());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complexTypes with simple content annotation.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypesSimpleContentAnnotation() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleContentType simpleContentTypeA = new SimpleContentType();
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        simpleContentTypeA.setAnnotation(annotationA);
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtensionA = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentExtensionA.addAttribute(new Attribute("{A}attributeA"));
        simpleContentTypeA.setInheritance(simpleContentExtensionA);
        SimpleContentType simpleContentTypeB = new SimpleContentType();
        Annotation annotationB = new Annotation();
        AppInfo appInfoB = new AppInfo();
        appInfoB.setSource("blaB");
        Documentation documentationB = new Documentation();
        documentationB.setSource("blaaB");
        annotationB.addAppInfos(appInfoB);
        annotationB.addDocumentations(documentationB);
        simpleContentTypeB.setAnnotation(annotationB);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        simpleContentExtensionB.addAttribute(new Attribute("{A}attributeB"));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", simpleContentTypeA);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertNotNull(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation().getAppInfos().size() == 2);
        assertEquals("blaA", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation().getAppInfos().get(0).getSource());
        assertEquals("blaB", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation().getAppInfos().get(1).getSource());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation().getDocumentations().size() == 2);
        assertEquals("blaaA", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation().getDocumentations().get(0).getSource());
        assertEquals("blaaB", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation().getDocumentations().get(1).getSource());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getId());
        assertNotNull(((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAnnotation());
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAttributes().size() == 2);
        assertEquals("{A}union-type.simpleTypeA.simpleTypeB", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getName());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complexTypes with simple content extension annotation.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypesSimpleContentExtensionAnnotation() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleContentType simpleContentTypeA = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtensionA = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        simpleContentExtensionA.setAnnotation(annotationA);
        simpleContentExtensionA.addAttribute(new Attribute("{A}attributeA"));
        simpleContentTypeA.setInheritance(simpleContentExtensionA);
        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        Annotation annotationB = new Annotation();
        AppInfo appInfoB = new AppInfo();
        appInfoB.setSource("blaB");
        Documentation documentationB = new Documentation();
        documentationB.setSource("blaaB");
        annotationB.addAppInfos(appInfoB);
        annotationB.addDocumentations(documentationB);
        simpleContentExtensionB.setAnnotation(annotationB);
        simpleContentExtensionB.addAttribute(new Attribute("{A}attributeB"));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", simpleContentTypeA);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertNotNull(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getId());
        assertNotNull(((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getId());
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAnnotation().getAppInfos().size() == 2);
        assertEquals("blaA", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAnnotation().getAppInfos().get(0).getSource());
        assertEquals("blaB", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAnnotation().getAppInfos().get(1).getSource());
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAnnotation().getDocumentations().size() == 2);
        assertEquals("blaaA", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAnnotation().getDocumentations().get(0).getSource());
        assertEquals("blaaB", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAnnotation().getDocumentations().get(1).getSource());
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAttributes().size() == 2);
        assertEquals("{A}union-type.simpleTypeA.simpleTypeB", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getName());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complexTypes with complex content and simple content.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypesComplexContentSimpleContentWithElements() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SequencePattern sequencePatternA = new SequencePattern();
        Element elementA = new Element("{A}elementA");
        sequencePatternA.addParticle(elementA);
        ComplexContentType complexContentTypeA = new ComplexContentType(sequencePatternA);
        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        simpleContentExtensionB.addAttribute(new Attribute("{A}attributeB"));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", complexContentTypeA);
        complexTypeA.addAttribute(new Attribute("{A}attributeA"));
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeC", new SimpleType("{A}simpleTypeC", null)));
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);
        particleUnionGenerator.setTypeUnionGeneratornion(instance);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().size() == 2);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(0) instanceof Attribute);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(1) instanceof Attribute);
        assertEquals("{A}attributeA", ((Attribute) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(0)).getName());
        assertEquals("{A}attributeB", ((Attribute) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(1)).getName());
        assertNotNull(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance());
        assertEquals(true, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getMixed());
        assertTrue(((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getParticle() instanceof ChoicePattern);
        assertTrue(((ChoicePattern) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getParticle()).getParticles().size() == 2);
        assertTrue(((ChoicePattern) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getParticle()).getParticles().getLast() instanceof SequencePattern);
        assertEquals(true, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complexTypes with complex content and simple content with IDs.
     */
    @Test
    public void testGenerateNewType_3argsComplexContentSimpleContentWithElementsID() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SequencePattern sequencePatternA = new SequencePattern();
        Element elementA = new Element("{A}elementA");
        sequencePatternA.addParticle(elementA);
        ComplexContentType complexContentTypeA = new ComplexContentType(sequencePatternA);
        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        simpleContentExtensionB.addAttribute(new Attribute("{A}attributeB"));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", complexContentTypeA);
        complexTypeA.addAttribute(new Attribute("{A}attributeA"));
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        types.add(complexTypeA);
        types.add(complexTypeB);
        complexContentTypeA.setId("idOne");
        simpleContentTypeB.setId("idTwo");

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeC", new SimpleType("{A}simpleTypeC", null)));
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("idOne");
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, usedIDs, attributeParticleUnionGenerator, particleUnionGenerator);
        particleUnionGenerator.setTypeUnionGeneratornion(instance);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().size() == 2);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(0) instanceof Attribute);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(1) instanceof Attribute);
        assertEquals("{A}attributeA", ((Attribute) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(0)).getName());
        assertEquals("{A}attributeB", ((Attribute) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(1)).getName());
        assertNotNull(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation());
        assertEquals("idOne.1", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance());
        assertEquals(true, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getMixed());
        assertTrue(((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getParticle() instanceof ChoicePattern);
        assertTrue(((ChoicePattern) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getParticle()).getParticles().size() == 2);
        assertTrue(((ChoicePattern) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getParticle()).getParticles().getLast() instanceof SequencePattern);
        assertEquals(true, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));

    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complexTypes with annotations.
     */
    @Test
    public void testGenerateNewType_3argsComplexContentSimpleContentWithElementsAnnotations() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SequencePattern sequencePatternA = new SequencePattern();
        Element elementA = new Element("{A}elementA");
        sequencePatternA.addParticle(elementA);
        ComplexContentType complexContentTypeA = new ComplexContentType(sequencePatternA);
        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        simpleContentExtensionB.addAttribute(new Attribute("{A}attributeB"));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", complexContentTypeA);
        complexTypeA.addAttribute(new Attribute("{A}attributeA"));
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        types.add(complexTypeA);
        types.add(complexTypeB);
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        complexContentTypeA.setAnnotation(annotationA);
        Annotation annotationB = new Annotation();
        AppInfo appInfoB = new AppInfo();
        appInfoB.setSource("blaB");
        Documentation documentationB = new Documentation();
        documentationB.setSource("blaaB");
        annotationB.addAppInfos(appInfoB);
        annotationB.addDocumentations(documentationB);
        simpleContentTypeB.setAnnotation(annotationB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeC", new SimpleType("{A}simpleTypeC", null)));
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);
        particleUnionGenerator.setTypeUnionGeneratornion(instance);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().size() == 2);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(0) instanceof Attribute);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(1) instanceof Attribute);
        assertEquals("{A}attributeA", ((Attribute) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(0)).getName());
        assertEquals("{A}attributeB", ((Attribute) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(1)).getName());
        assertNotNull(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation().getAppInfos().size() == 1);
        assertEquals("blaA", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaaA", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance());
        assertEquals(true, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getMixed());
        assertTrue(((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getParticle() instanceof ChoicePattern);
        assertTrue(((ChoicePattern) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getParticle()).getParticles().size() == 2);
        assertTrue(((ChoicePattern) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getParticle()).getParticles().getLast() instanceof SequencePattern);
        assertEquals(true, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of mixed complexTypes with complex content and simple content.
     */
    @Test
    public void testGenerateNewType_3argsComplexContentSimpleContentWithElementsMixed() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SequencePattern sequencePatternA = new SequencePattern();
        Element elementA = new Element("{A}elementA");
        sequencePatternA.addParticle(elementA);
        ComplexContentType complexContentTypeA = new ComplexContentType(sequencePatternA);
        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        simpleContentExtensionB.addAttribute(new Attribute("{A}attributeB"));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", complexContentTypeA);
        complexTypeA.addAttribute(new Attribute("{A}attributeA"));
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        types.add(complexTypeA);
        types.add(complexTypeB);
        complexContentTypeA.setMixed(true);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeC", new SimpleType("{A}simpleTypeC", null)));
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);
        particleUnionGenerator.setTypeUnionGeneratornion(instance);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().size() == 2);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(0) instanceof Attribute);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(1) instanceof Attribute);
        assertEquals("{A}attributeA", ((Attribute) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(0)).getName());
        assertEquals("{A}attributeB", ((Attribute) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(1)).getName());
        assertNotNull(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance());
        assertEquals(true, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getMixed());
        assertTrue(((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getParticle() instanceof ChoicePattern);
        assertTrue(((ChoicePattern) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getParticle()).getParticles().size() == 2);
        assertTrue(((ChoicePattern) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getParticle()).getParticles().getLast() instanceof SequencePattern);
        assertEquals(true, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complexTypes with complex content and simple content.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypesComplexContentSimpleContentWithoutElements() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SequencePattern sequencePatternA = new SequencePattern();
        ComplexContentType complexContentTypeA = new ComplexContentType(sequencePatternA);
        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        simpleContentExtensionB.addAttribute(new Attribute("{A}attributeB"));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", complexContentTypeA);
        complexTypeA.addAttribute(new Attribute("{A}attributeA"));
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeC", new SimpleType("{A}simpleTypeC", null)));
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);
        particleUnionGenerator.setTypeUnionGeneratornion(instance);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertNotNull(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getId());
        assertNotNull(((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAnnotation());
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAttributes().size() == 2);
        assertEquals("{A}simpleTypeB", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getName());
        LinkedList<AttributeParticle> attributeParticles = ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAttributes();
        assertTrue(attributeParticles.size() == 2);
        assertTrue(attributeParticles.get(0) instanceof Attribute);
        assertTrue(attributeParticles.get(1) instanceof Attribute);
        assertEquals("{A}attributeA", ((Attribute) attributeParticles.get(0)).getName());
        assertEquals("{A}attributeB", ((Attribute) attributeParticles.get(1)).getName());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complexTypes with complex content and simple content with IDs.
     */
    @Test
    public void testGenerateNewType_3argsComplexContentSimpleContentWithoutElementsID() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SequencePattern sequencePatternA = new SequencePattern();
        ComplexContentType complexContentTypeA = new ComplexContentType(sequencePatternA);
        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        simpleContentExtensionB.addAttribute(new Attribute("{A}attributeB"));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", complexContentTypeA);
        complexTypeA.addAttribute(new Attribute("{A}attributeA"));
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        types.add(complexTypeA);
        types.add(complexTypeB);
        complexContentTypeA.setId("idOne");
        simpleContentTypeB.setId("idTwo");

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeC", new SimpleType("{A}simpleTypeC", null)));
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("idTwo");
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, usedIDs, attributeParticleUnionGenerator, particleUnionGenerator);
        particleUnionGenerator.setTypeUnionGeneratornion(instance);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertNotNull(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation());
        assertEquals("idTwo.1", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getId());
        assertNotNull(((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAnnotation());
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAttributes().size() == 2);
        assertEquals("{A}simpleTypeB", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getName());
        LinkedList<AttributeParticle> attributeParticles = ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAttributes();
        assertTrue(attributeParticles.size() == 2);
        assertTrue(attributeParticles.get(0) instanceof Attribute);
        assertTrue(attributeParticles.get(1) instanceof Attribute);
        assertEquals("{A}attributeA", ((Attribute) attributeParticles.get(0)).getName());
        assertEquals("{A}attributeB", ((Attribute) attributeParticles.get(1)).getName());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));

    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complexTypes with complex content and simple content with annotations.
     */
    @Test
    public void testGenerateNewType_3argsComplexContentSimpleContentWithoutElementsAnnotations() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SequencePattern sequencePatternA = new SequencePattern();
        ComplexContentType complexContentTypeA = new ComplexContentType(sequencePatternA);
        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        simpleContentExtensionB.addAttribute(new Attribute("{A}attributeB"));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", complexContentTypeA);
        complexTypeA.addAttribute(new Attribute("{A}attributeA"));
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        types.add(complexTypeA);
        types.add(complexTypeB);
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        complexContentTypeA.setAnnotation(annotationA);
        Annotation annotationB = new Annotation();
        AppInfo appInfoB = new AppInfo();
        appInfoB.setSource("blaB");
        Documentation documentationB = new Documentation();
        documentationB.setSource("blaaB");
        annotationB.addAppInfos(appInfoB);
        annotationB.addDocumentations(documentationB);
        simpleContentTypeB.setAnnotation(annotationB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeC", new SimpleType("{A}simpleTypeC", null)));
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);
        particleUnionGenerator.setTypeUnionGeneratornion(instance);

        instance.generateNewType(types, elementTypeMap, typeName);


        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertNotNull(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation().getAppInfos().size() == 1);
        assertEquals("blaB", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaaB", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getId());
        assertNotNull(((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAnnotation());
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAttributes().size() == 2);
        assertEquals("{A}simpleTypeB", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getName());
        LinkedList<AttributeParticle> attributeParticles = ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAttributes();
        assertTrue(attributeParticles.size() == 2);
        assertTrue(attributeParticles.get(0) instanceof Attribute);
        assertTrue(attributeParticles.get(1) instanceof Attribute);
        assertEquals("{A}attributeA", ((Attribute) attributeParticles.get(0)).getName());
        assertEquals("{A}attributeB", ((Attribute) attributeParticles.get(1)).getName());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of mixed complexTypes with complex content and simple content.
     */
    @Test
    public void testGenerateNewType_3argsComplexContentSimpleContentWithoutElementsMixed() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SequencePattern sequencePatternA = new SequencePattern();
        ComplexContentType complexContentTypeA = new ComplexContentType(sequencePatternA);
        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        simpleContentExtensionB.addAttribute(new Attribute("{A}attributeB"));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", complexContentTypeA);
        complexTypeA.addAttribute(new Attribute("{A}attributeA"));
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        types.add(complexTypeA);
        types.add(complexTypeB);
        complexContentTypeA.setMixed(true);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeC", new SimpleType("{A}simpleTypeC", null)));
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);
        particleUnionGenerator.setTypeUnionGeneratornion(instance);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().size() == 2);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(0) instanceof Attribute);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(1) instanceof Attribute);
        assertEquals("{A}attributeA", ((Attribute) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(0)).getName());
        assertEquals("{A}attributeB", ((Attribute) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(1)).getName());
        assertNotNull(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance());
        assertEquals(true, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getMixed());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getParticle());
        assertEquals(true, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complex and simple types.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypeWithElementsSimpleType() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SequencePattern sequencePatternA = new SequencePattern();
        Element elementA = new Element("{A}elementA");
        sequencePatternA.addParticle(elementA);
        ComplexContentType complexContentTypeA = new ComplexContentType(sequencePatternA);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", complexContentTypeA);
        SimpleType simpleTypeB = new SimpleType("{A}SimpleTypeB", null);
        types.add(complexTypeA);
        types.add(simpleTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeC", new SimpleType("{A}simpleTypeC", null)));
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        elementOldSchemaMap.put(elementA, new XSDSchema("A"));
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);
        particleUnionGenerator.setTypeUnionGeneratornion(instance);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertNotNull(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getMixed());
        assertTrue(((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getParticle() instanceof ChoicePattern);
        assertTrue(((ChoicePattern) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getParticle()).getParticles().size() == 2);
        assertTrue(((ChoicePattern) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getParticle()).getParticles().getLast() instanceof SequencePattern);
        assertEquals(true, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complex and simple types.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypeWithoutElementsSimpleTypeEmptyMixed() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SequencePattern sequencePatternA = new SequencePattern();
        ComplexContentType complexContentTypeA = new ComplexContentType(sequencePatternA);
        complexContentTypeA.setMixed(true);
        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        simpleContentExtensionB.addAttribute(new Attribute("{A}attributeB"));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", complexContentTypeA);
        complexTypeA.addAttribute(new Attribute("{A}attributeA"));
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        SimpleType simpleTypeC = new SimpleType("{A}SimpleTypeC", null);
        types.add(complexTypeA);
        types.add(complexTypeB);
        types.add(simpleTypeC);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeC", new SimpleType("{A}simpleTypeC", null)));
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeC, new XSDSchema("A"));
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);
        particleUnionGenerator.setTypeUnionGeneratornion(instance);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().size() == 2);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(0) instanceof Attribute);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(1) instanceof Attribute);
        assertEquals("{A}attributeA", ((Attribute) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(0)).getName());
        assertEquals("{A}attributeB", ((Attribute) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(1)).getName());
        assertNull(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(true, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complex and simple types.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypeWithoutElementsSimpleTypeEmptyMixedID() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SequencePattern sequencePatternA = new SequencePattern();
        ComplexContentType complexContentTypeA = new ComplexContentType(sequencePatternA);
        complexContentTypeA.setMixed(true);
        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        simpleContentExtensionB.addAttribute(new Attribute("{A}attributeB"));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", complexContentTypeA);
        complexTypeA.addAttribute(new Attribute("{A}attributeA"));
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        SimpleType simpleTypeC = new SimpleType("{A}SimpleTypeC", null);
        types.add(complexTypeA);
        complexTypeA.setId("idOne");
        types.add(complexTypeB);
        complexTypeB.setId("idTwo");
        types.add(simpleTypeC);
        simpleTypeC.setId("idThree");

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeC", new SimpleType("{A}simpleTypeC", null)));
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeC, new XSDSchema("A"));
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("idOne.idTwo");
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, usedIDs, attributeParticleUnionGenerator, particleUnionGenerator);
        particleUnionGenerator.setTypeUnionGeneratornion(instance);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals("idOne.idTwo.1", outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().size() == 2);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(0) instanceof Attribute);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(1) instanceof Attribute);
        assertEquals("{A}attributeA", ((Attribute) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(0)).getName());
        assertEquals("{A}attributeB", ((Attribute) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(1)).getName());
        assertNull(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(true, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complex and simple types.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypeWithoutElementsSimpleTypeEmptyMixedAnnotation() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SequencePattern sequencePatternA = new SequencePattern();
        ComplexContentType complexContentTypeA = new ComplexContentType(sequencePatternA);
        complexContentTypeA.setMixed(true);
        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        simpleContentExtensionB.addAttribute(new Attribute("{A}attributeB"));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", complexContentTypeA);
        complexTypeA.addAttribute(new Attribute("{A}attributeA"));
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        SimpleType simpleTypeC = new SimpleType("{A}SimpleTypeC", null);
        types.add(complexTypeA);
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        complexTypeA.setAnnotation(annotationA);
        types.add(complexTypeB);
        Annotation annotationB = new Annotation();
        AppInfo appInfoB = new AppInfo();
        appInfoB.setSource("blaB");
        Documentation documentationB = new Documentation();
        documentationB.setSource("blaaB");
        annotationB.addAppInfos(appInfoB);
        annotationB.addDocumentations(documentationB);
        complexTypeB.setAnnotation(annotationB);
        types.add(simpleTypeC);
        Annotation annotationC = new Annotation();
        AppInfo appInfoC = new AppInfo();
        appInfoC.setSource("blaC");
        Documentation documentationC = new Documentation();
        documentationC.setSource("blaaC");
        annotationC.addAppInfos(appInfoC);
        annotationC.addDocumentations(documentationC);
        simpleTypeC.setAnnotation(annotationC);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeC", new SimpleType("{A}simpleTypeC", null)));
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeC, new XSDSchema("A"));
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);
        particleUnionGenerator.setTypeUnionGeneratornion(instance);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation().getAppInfos().size() == 2);
        assertEquals("blaA", outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation().getAppInfos().get(0).getSource());
        assertEquals("blaB", outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation().getAppInfos().get(1).getSource());
        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation().getDocumentations().size() == 2);
        assertEquals("blaaA", outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation().getDocumentations().get(0).getSource());
        assertEquals("blaaB", outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation().getDocumentations().get(1).getSource());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().size() == 2);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(0) instanceof Attribute);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(1) instanceof Attribute);
        assertEquals("{A}attributeA", ((Attribute) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(0)).getName());
        assertEquals("{A}attributeB", ((Attribute) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes().get(1)).getName());
        assertNull(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(true, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complex and simple types.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypeWithoutElementsSimpleType() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SequencePattern sequencePatternA = new SequencePattern();
        ComplexContentType complexContentTypeA = new ComplexContentType(sequencePatternA);
        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        simpleContentExtensionB.addAttribute(new Attribute("{A}attributeB"));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", complexContentTypeA);
        complexTypeA.addAttribute(new Attribute("{A}attributeA"));
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        SimpleType simpleTypeC = new SimpleType("{A}simpleTypeC", null);
        types.add(complexTypeA);
        types.add(complexTypeB);
        types.add(simpleTypeC);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeC", new SimpleType("{A}simpleTypeC", null)));
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeC, new XSDSchema("A"));
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);
        particleUnionGenerator.setTypeUnionGeneratornion(instance);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertNotNull(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getId());
        assertNotNull(((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAnnotation());
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAttributes().size() == 2);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAttributes().get(0) instanceof Attribute);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAttributes().get(1) instanceof Attribute);
        assertEquals("{A}attributeA", ((Attribute) ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAttributes().get(0)).getName());
        assertEquals("{A}attributeB", ((Attribute) ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAttributes().get(1)).getName());
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAttributes().size() == 2);
        assertEquals("{A}union-type.simpleTypeC.simpleTypeB", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getName());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complex and simple types.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypeWithoutElementsSimpleTypeID() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SequencePattern sequencePatternA = new SequencePattern();
        ComplexContentType complexContentTypeA = new ComplexContentType(sequencePatternA);
        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        simpleContentExtensionB.addAttribute(new Attribute("{A}attributeB"));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", complexContentTypeA);
        complexTypeA.addAttribute(new Attribute("{A}attributeA"));
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        SimpleType simpleTypeC = new SimpleType("{A}simpleTypeC", null);
        types.add(complexTypeA);
        complexTypeA.setId("idOne");
        types.add(complexTypeB);
        complexTypeB.setId("idTwo");
        types.add(simpleTypeC);
        simpleTypeC.setId("idThree");

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeC", new SimpleType("{A}simpleTypeC", null)));
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeC, new XSDSchema("A"));
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("idOne.idTwo");
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, usedIDs, attributeParticleUnionGenerator, particleUnionGenerator);
        particleUnionGenerator.setTypeUnionGeneratornion(instance);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals("idOne.idTwo.1", outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertNotNull(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getId());
        assertNotNull(((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAnnotation());
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAttributes().size() == 2);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAttributes().get(0) instanceof Attribute);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAttributes().get(1) instanceof Attribute);
        assertEquals("{A}attributeA", ((Attribute) ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAttributes().get(0)).getName());
        assertEquals("{A}attributeB", ((Attribute) ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAttributes().get(1)).getName());
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAttributes().size() == 2);
        assertEquals("{A}union-type.simpleTypeC.simpleTypeB", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getName());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getAnnotation());
        assertEquals("idThree.", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of generateNewType method, of class TypeUnionGenerator for a set
     * of complex and simple types.
     */
    @Test
    public void testGenerateNewType_3argsComplexTypeWithoutElementsSimpleTypeAnnotation() {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SequencePattern sequencePatternA = new SequencePattern();
        ComplexContentType complexContentTypeA = new ComplexContentType(sequencePatternA);
        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        simpleContentExtensionB.addAttribute(new Attribute("{A}attributeB"));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", complexContentTypeA);
        complexTypeA.addAttribute(new Attribute("{A}attributeA"));
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        SimpleType simpleTypeC = new SimpleType("{A}simpleTypeC", null);
        types.add(complexTypeA);
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        complexTypeA.setAnnotation(annotationA);
        types.add(complexTypeB);
        Annotation annotationB = new Annotation();
        AppInfo appInfoB = new AppInfo();
        appInfoB.setSource("blaB");
        Documentation documentationB = new Documentation();
        documentationB.setSource("blaaB");
        annotationB.addAppInfos(appInfoB);
        annotationB.addDocumentations(documentationB);
        complexTypeB.setAnnotation(annotationB);
        types.add(simpleTypeC);
        Annotation annotationC = new Annotation();
        AppInfo appInfoC = new AppInfo();
        appInfoC.setSource("blaC");
        Documentation documentationC = new Documentation();
        documentationC.setSource("blaaC");
        annotationC.addAppInfos(appInfoC);
        annotationC.addDocumentations(documentationC);
        simpleTypeC.setAnnotation(annotationC);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> elementTypeMap = new LinkedHashMap<String, SymbolTableRef<Type>>();
        elementTypeMap.put("{}elementA", new SymbolTableRef<Type>("{A}simpleTypeC", new SimpleType("{A}simpleTypeC", null)));
        String typeName = "{A}union-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeC, new XSDSchema("A"));
        LinkedHashMap<AnyPattern, XSDSchema> anyPatternOldSchemaMap = new LinkedHashMap<AnyPattern, XSDSchema>();
        LinkedHashMap<Element, XSDSchema> elementOldSchemaMap = new LinkedHashMap<Element, XSDSchema>();
        AttributeParticleUnionGenerator attributeParticleUnionGenerator = new AttributeParticleUnionGenerator(outputSchema, null, null, null, null, null, null, null, null);
        ParticleUnionGenerator particleUnionGenerator = new ParticleUnionGenerator(outputSchema, null, elementOldSchemaMap, anyPatternOldSchemaMap, null, null, null, null, null, null);
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, typeOldSchemaMap, null, attributeParticleUnionGenerator, particleUnionGenerator);
        particleUnionGenerator.setTypeUnionGeneratornion(instance);

        instance.generateNewType(types, elementTypeMap, typeName);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference() instanceof ComplexType);
        assertEquals(typeName, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getName());
        assertEquals(false, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getId());
        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation().getAppInfos().size() == 2);
        assertEquals("blaA", outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation().getAppInfos().get(0).getSource());
        assertEquals("blaB", outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation().getAppInfos().get(1).getSource());
        assertTrue(outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation().getDocumentations().size() == 2);
        assertEquals("blaaA", outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation().getDocumentations().get(0).getSource());
        assertEquals("blaaB", outputSchema.getTypeSymbolTable().getReference(typeName).getReference().getAnnotation().getDocumentations().get(1).getSource());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getAttributes());
        assertNotNull(((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent().getId());
        assertNotNull(((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAnnotation());
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAttributes().size() == 2);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAttributes().get(0) instanceof Attribute);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAttributes().get(1) instanceof Attribute);
        assertEquals("{A}attributeA", ((Attribute) ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAttributes().get(0)).getName());
        assertEquals("{A}attributeB", ((Attribute) ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAttributes().get(1)).getName());
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getAttributes().size() == 2);
        assertEquals("{A}union-type.simpleTypeC.simpleTypeB", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getName());
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getAnnotation().getAppInfos().size() == 1);
        assertEquals("blaC", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getAnnotation().getAppInfos().get(0).getSource());
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getAnnotation().getDocumentations().size() == 1);
        assertEquals("blaaC", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getAnnotation().getDocumentations().get(0).getSource());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getContent()).getInheritance()).getBase().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(typeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(typeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(typeName).getReference()));
    }

    /**
     * Test of isBuiltInDatatype method, of class TypeUnionGenerator.
     */
    @Test
    public void testIsBuiltInDatatype() {
        String name = "{http://www.w3.org/2001/XMLSchema}string";
        XSDSchema outputSchema = new XSDSchema("A");
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, null, null, null, null);

        boolean expResult = true;
        boolean result = instance.isBuiltInDatatype(name);

        assertEquals(expResult, result);
    }

    /**
     * Test of isBuiltInDatatype method, of class TypeUnionGenerator.
     */
    @Test
    public void testIsBuiltInDatatypeFalse() {
        String name = "{http://www.w3.org/2001/XMLSchema}foo";
        XSDSchema outputSchema = new XSDSchema("A");
        TypeUnionGenerator instance = new TypeUnionGenerator(outputSchema, null, null, null, null, null);

        boolean expResult = false;
        boolean result = instance.isBuiltInDatatype(name);

        assertEquals(expResult, result);
    }
}