package eu.fox7.bonxai.xsd.setOperations.intersection;

import eu.fox7.bonxai.common.Annotation;
import eu.fox7.bonxai.common.ChoicePattern;
import eu.fox7.bonxai.common.CountingPattern;
import eu.fox7.bonxai.common.SequencePattern;
import eu.fox7.bonxai.common.SymbolTableRef;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.setOperations.intersection.AttributeParticleIntersectionGenerator;
import eu.fox7.bonxai.xsd.setOperations.intersection.ParticleIntersectionGenerator;
import eu.fox7.bonxai.xsd.setOperations.intersection.TypeIntersectionGenerator;

import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case of the <tt>TypeIntersectionGenerator</tt> class, checks that every
 * method of this class performs properly.
 * @author Dominik Wolff
 */
public class TypeIntersectionGeneratorTest extends junit.framework.TestCase {

    public TypeIntersectionGeneratorTest() {
    }

    /**
     * Test of generateNewSimpleType method, of class TypeIntersectionGenerator.
     */
    @Test
    public void testGenerateNewSimpleType1st() {
        LinkedHashSet<SimpleType> simpleTypes = new LinkedHashSet<SimpleType>();

        SimpleType integerA = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        SimpleType stringA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        LinkedList<SymbolTableRef<Type>> memberTypesA = new LinkedList<SymbolTableRef<Type>>();
        memberTypesA.add(new SymbolTableRef<Type>(stringA.getName(), stringA));
        memberTypesA.add(new SymbolTableRef<Type>(integerA.getName(), integerA));
        SimpleContentUnion simpleContentUnionA = new SimpleContentUnion(memberTypesA);
        SimpleType unionA = new SimpleType("{A}unionA", simpleContentUnionA);
        SimpleContentList simpleContentListA = new SimpleContentList(new SymbolTableRef<Type>(unionA.getName(), unionA));
        SimpleType listA = new SimpleType("{A}listA", simpleContentListA);

        SimpleType integerB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        SimpleType stringB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        SimpleContentList simpleContentListB = new SimpleContentList(new SymbolTableRef<Type>(integerB.getName(), integerB));
        SimpleType listB = new SimpleType("{A}listB", simpleContentListB);
        LinkedList<SymbolTableRef<Type>> memberTypesB = new LinkedList<SymbolTableRef<Type>>();
        memberTypesB.add(new SymbolTableRef<Type>(stringB.getName(), stringB));
        memberTypesB.add(new SymbolTableRef<Type>(listB.getName(), listB));
        SimpleContentUnion simpleContentUnionB = new SimpleContentUnion(memberTypesB);
        SimpleType unionB = new SimpleType("{A}unionB", simpleContentUnionB);

        simpleTypes.add(listA);
        simpleTypes.add(unionB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(listA, new XSDSchema("A"));
        typeOldSchemaMap.put(listB, new XSDSchema("A"));
        typeOldSchemaMap.put(unionA, new XSDSchema("A"));
        typeOldSchemaMap.put(unionB, new XSDSchema("A"));
        typeOldSchemaMap.put(integerA, new XSDSchema("A"));
        typeOldSchemaMap.put(integerB, new XSDSchema("A"));
        typeOldSchemaMap.put(stringA, new XSDSchema("A"));
        typeOldSchemaMap.put(stringB, new XSDSchema("A"));
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        SimpleType result = instance.generateNewSimpleType(simpleTypes);

        assertTrue(result instanceof SimpleType);
        assertEquals("{A}intersection-type.listA.unionB", ((SimpleType) result).getName());
        assertTrue(result.getInheritance() instanceof SimpleContentUnion);
        assertTrue(((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().size() == 2);
        assertTrue(((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertTrue(((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference() instanceof SimpleType);

        if (((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference().getName().equals("{http://www.w3.org/2001/XMLSchema}string")) {
            assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference().getName());
            assertEquals("{A}intersection-type.list", ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference().getName());
            assertEquals(null, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance());
            assertTrue(((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance() instanceof SimpleContentList);
            assertTrue(((SimpleContentList) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance()).getBase() instanceof SimpleType);
            assertEquals("{http://www.w3.org/2001/XMLSchema}integer", ((SimpleContentList) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance()).getBase().getName());
        } else {
            assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference().getName());
            assertEquals("{A}intersection-type.list", ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference().getName());
            assertEquals(null, ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance());
            assertTrue(((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance() instanceof SimpleContentList);
            assertTrue(((SimpleContentList) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase() instanceof SimpleType);
            assertEquals("{http://www.w3.org/2001/XMLSchema}integer", ((SimpleContentList) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName());
        }
    }

    /**
     * Test of generateNewSimpleType method, of class TypeIntersectionGenerator.
     */
    @Test
    public void testGenerateNewSimpleType2nd() {
        LinkedHashSet<SimpleType> simpleTypes = new LinkedHashSet<SimpleType>();

        SimpleType integerA = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        SimpleType integerB = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        SimpleType integerC = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);

        SimpleContentRestriction simpleContentRestrictionA1 = new SimpleContentRestriction(new SymbolTableRef<Type>(integerA.getName(), integerA));
        simpleContentRestrictionA1.setMaxExclusive(new SimpleContentFixableRestrictionProperty<String>("10", false));
        SimpleContentRestriction simpleContentRestrictionA2 = new SimpleContentRestriction(new SymbolTableRef<Type>(integerA.getName(), integerA));
        simpleContentRestrictionA2.setMinExclusive(new SimpleContentFixableRestrictionProperty<String>("20", false));
        SimpleContentRestriction simpleContentRestrictionB1 = new SimpleContentRestriction(new SymbolTableRef<Type>(integerB.getName(), integerB));
        simpleContentRestrictionB1.setMaxInclusive(new SimpleContentFixableRestrictionProperty<String>("5", false));
        simpleContentRestrictionB1.setMinInclusive(new SimpleContentFixableRestrictionProperty<String>("5", false));
        SimpleContentRestriction simpleContentRestrictionB2 = new SimpleContentRestriction(new SymbolTableRef<Type>(integerB.getName(), integerB));
        simpleContentRestrictionB2.setMinExclusive(new SimpleContentFixableRestrictionProperty<String>("15", false));
        SimpleContentRestriction simpleContentRestrictionC1 = new SimpleContentRestriction(new SymbolTableRef<Type>(integerC.getName(), integerC));
        simpleContentRestrictionC1.setMaxInclusive(new SimpleContentFixableRestrictionProperty<String>("40", false));
        simpleContentRestrictionC1.setMinInclusive(new SimpleContentFixableRestrictionProperty<String>("5", false));

        SimpleType restrictionA1 = new SimpleType("{A}restrictionA1", simpleContentRestrictionA1);
        SimpleType restrictionA2 = new SimpleType("{A}restrictionA2", simpleContentRestrictionA2);
        SimpleType restrictionB1 = new SimpleType("{A}restrictionB1", simpleContentRestrictionB1);
        SimpleType restrictionB2 = new SimpleType("{A}restrictionB2", simpleContentRestrictionB2);
        SimpleType restrictionC1 = new SimpleType("{A}restrictionC1", simpleContentRestrictionC1);

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

        simpleTypes.add(restrictionC1);
        simpleTypes.add(unionA);
        simpleTypes.add(unionB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(integerA, new XSDSchema("A"));
        typeOldSchemaMap.put(integerB, new XSDSchema("A"));
        typeOldSchemaMap.put(integerC, new XSDSchema("A"));
        typeOldSchemaMap.put(restrictionA1, new XSDSchema("A"));
        typeOldSchemaMap.put(restrictionA2, new XSDSchema("A"));
        typeOldSchemaMap.put(restrictionB1, new XSDSchema("A"));
        typeOldSchemaMap.put(restrictionB2, new XSDSchema("A"));
        typeOldSchemaMap.put(restrictionC1, new XSDSchema("A"));
        typeOldSchemaMap.put(unionA, new XSDSchema("A"));
        typeOldSchemaMap.put(unionB, new XSDSchema("A"));
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        SimpleType result = instance.generateNewSimpleType(simpleTypes);

        assertTrue(result instanceof SimpleType);
        assertEquals("{A}intersection-type.restrictionC1.unionA.unionB", ((SimpleType) result).getName());
        assertTrue(result.getInheritance() instanceof SimpleContentUnion);
        assertTrue(((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().size() == 2);
        assertTrue(((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertTrue(((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference() instanceof SimpleType);

        if (((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinExclusive() != null) {
            assertTrue(((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance() instanceof SimpleContentRestriction);
            assertTrue(((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance() instanceof SimpleContentRestriction);
            assertEquals("20", ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinExclusive().getValue());
            assertEquals("40", ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxInclusive().getValue());
            assertEquals("5", ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance()).getMinInclusive().getValue());
            assertEquals("5", ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance()).getMaxInclusive().getValue());
            assertTrue(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase() instanceof SimpleType);
            assertTrue(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance()).getBase() instanceof SimpleType);
            assertEquals("{http://www.w3.org/2001/XMLSchema}integer", ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName());
            assertEquals("{http://www.w3.org/2001/XMLSchema}integer", ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance()).getBase().getName());
        } else {
            assertTrue(((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance() instanceof SimpleContentRestriction);
            assertTrue(((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance() instanceof SimpleContentRestriction);
            assertEquals("20", ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance()).getMinExclusive().getValue());
            assertEquals("40", ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance()).getMaxInclusive().getValue());
            assertEquals("5", ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinInclusive().getValue());
            assertEquals("5", ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxInclusive().getValue());
            assertTrue(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance()).getBase() instanceof SimpleType);
            assertTrue(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase() instanceof SimpleType);
            assertEquals("{http://www.w3.org/2001/XMLSchema}integer", ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance()).getBase().getName());
            assertEquals("{http://www.w3.org/2001/XMLSchema}integer", ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName());
        }
    }

    /**
     * Test of generateNewSimpleType method, of class TypeIntersectionGenerator.
     */
    @Test
    public void testGenerateNewSimpleType3rd() {
        LinkedHashSet<SimpleType> simpleTypes = new LinkedHashSet<SimpleType>();

        SimpleType stringA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleType stringB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);

        SimpleContentRestriction simpleContentRestrictionA1 = new SimpleContentRestriction(new SymbolTableRef<Type>(stringA.getName(), stringA));
        simpleContentRestrictionA1.setMinLength(new SimpleContentFixableRestrictionProperty<Integer>(10, false));
        simpleContentRestrictionA1.setMaxLength(new SimpleContentFixableRestrictionProperty<Integer>(15, false));
        SimpleContentRestriction simpleContentRestrictionA2 = new SimpleContentRestriction(new SymbolTableRef<Type>(stringA.getName(), stringA));
        simpleContentRestrictionA2.setMinLength(new SimpleContentFixableRestrictionProperty<Integer>(20, false));
        simpleContentRestrictionA2.setMaxLength(new SimpleContentFixableRestrictionProperty<Integer>(25, false));
        SimpleContentRestriction simpleContentRestrictionB1 = new SimpleContentRestriction(new SymbolTableRef<Type>(stringB.getName(), stringB));
        simpleContentRestrictionB1.setMinLength(new SimpleContentFixableRestrictionProperty<Integer>(14, false));
        simpleContentRestrictionB1.setMaxLength(new SimpleContentFixableRestrictionProperty<Integer>(20, false));
        SimpleContentRestriction simpleContentRestrictionB2 = new SimpleContentRestriction(new SymbolTableRef<Type>(stringB.getName(), stringB));
        simpleContentRestrictionB2.setMinLength(new SimpleContentFixableRestrictionProperty<Integer>(23, false));
        simpleContentRestrictionB2.setMaxLength(new SimpleContentFixableRestrictionProperty<Integer>(30, false));

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
        simpleContentRestrictionListA.setMinLength(new SimpleContentFixableRestrictionProperty<Integer>(2, false));
        simpleContentRestrictionListA.setMaxLength(new SimpleContentFixableRestrictionProperty<Integer>(5, false));


        SimpleContentRestriction simpleContentRestrictionListB = new SimpleContentRestriction(new SymbolTableRef<Type>(listB.getName(), listB));
        simpleContentRestrictionListB.setLength(new SimpleContentFixableRestrictionProperty<Integer>(3, false));

        SimpleType restrictionListA = new SimpleType("{A}restrictionListA", simpleContentRestrictionListA);
        SimpleType restrictionListB = new SimpleType("{A}restrictionListB", simpleContentRestrictionListB);

        simpleTypes.add(restrictionListA);
        simpleTypes.add(restrictionListB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(listA, new XSDSchema("A"));
        typeOldSchemaMap.put(listB, new XSDSchema("A"));
        typeOldSchemaMap.put(restrictionA1, new XSDSchema("A"));
        typeOldSchemaMap.put(restrictionA2, new XSDSchema("A"));
        typeOldSchemaMap.put(restrictionB1, new XSDSchema("A"));
        typeOldSchemaMap.put(restrictionB2, new XSDSchema("A"));
        typeOldSchemaMap.put(restrictionListA, new XSDSchema("A"));
        typeOldSchemaMap.put(restrictionListB, new XSDSchema("A"));
        typeOldSchemaMap.put(stringA, new XSDSchema("A"));
        typeOldSchemaMap.put(stringB, new XSDSchema("A"));
        typeOldSchemaMap.put(unionA, new XSDSchema("A"));
        typeOldSchemaMap.put(unionA, new XSDSchema("A"));
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        SimpleType result = instance.generateNewSimpleType(simpleTypes);

        assertTrue(result instanceof SimpleType);
        assertEquals("{A}intersection-type.restrictionListA.restrictionListB", ((SimpleType) result).getName());
        assertTrue(result.getInheritance() instanceof SimpleContentUnion);
        assertTrue(((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().size() == 1);
        assertTrue(((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertTrue(((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance() instanceof SimpleContentRestriction);
        assertEquals(3, (int) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getLength().getValue());
        assertTrue(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{A}intersection-type.list", ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName());
        assertTrue(((SimpleType) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase()).getInheritance() instanceof SimpleContentList);

        SimpleContentList resultingList = (SimpleContentList) ((SimpleType) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) result.getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase()).getInheritance();
        assertTrue(resultingList.getBase() instanceof SimpleType);
        assertEquals("{A}intersection-type.union", resultingList.getBase().getName());
        assertTrue(((SimpleType) resultingList.getBase()).getInheritance() instanceof SimpleContentUnion);
        assertTrue(((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().size() == 3);
        assertTrue(((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertTrue(((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().get(1).getReference() instanceof SimpleType);
        assertTrue(((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().getLast().getReference() instanceof SimpleType);
        assertTrue(((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance() instanceof SimpleContentRestriction);
        assertTrue(((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().get(1).getReference()).getInheritance() instanceof SimpleContentRestriction);
        assertTrue(((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance() instanceof SimpleContentRestriction);


        if (((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinLength().getValue() == 14) {
            assertEquals(14, (int) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinLength().getValue());
            assertEquals(15, (int) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxLength().getValue());
        }
        if (((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinLength().getValue() == 20) {
            assertEquals(20, (int) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinLength().getValue());
            assertEquals(20, (int) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxLength().getValue());
        }
        if (((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinLength().getValue() == 23) {
            assertEquals(23, (int) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinLength().getValue());
            assertEquals(25, (int) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxLength().getValue());
        }

        if (((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().get(1).getReference()).getInheritance()).getMinLength().getValue() == 14) {
            assertEquals(14, (int) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().get(1).getReference()).getInheritance()).getMinLength().getValue());
            assertEquals(15, (int) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().get(1).getReference()).getInheritance()).getMaxLength().getValue());
        }
        if (((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().get(1).getReference()).getInheritance()).getMinLength().getValue() == 20) {
            assertEquals(20, (int) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().get(1).getReference()).getInheritance()).getMinLength().getValue());
            assertEquals(20, (int) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().get(1).getReference()).getInheritance()).getMaxLength().getValue());
        }
        if (((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().get(1).getReference()).getInheritance()).getMinLength().getValue() == 23) {
            assertEquals(23, (int) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().get(1).getReference()).getInheritance()).getMinLength().getValue());
            assertEquals(25, (int) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().get(1).getReference()).getInheritance()).getMaxLength().getValue());
        }

        if (((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance()).getMinLength().getValue() == 14) {
            assertEquals(14, (int) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance()).getMinLength().getValue());
            assertEquals(15, (int) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance()).getMaxLength().getValue());
        }
        if (((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance()).getMinLength().getValue() == 20) {
            assertEquals(20, (int) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance()).getMinLength().getValue());
            assertEquals(20, (int) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance()).getMaxLength().getValue());
        }
        if (((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance()).getMinLength().getValue() == 23) {
            assertEquals(23, (int) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance()).getMinLength().getValue());
            assertEquals(25, (int) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance()).getMaxLength().getValue());
        }
        assertTrue(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase() instanceof SimpleType);
        assertTrue(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().get(1).getReference()).getInheritance()).getBase() instanceof SimpleType);
        assertTrue(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().get(1).getReference()).getInheritance()).getBase().getName());
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) resultingList.getBase()).getInheritance()).getAllMemberTypes().getLast().getReference()).getInheritance()).getBase().getName());

    }

    /**
     * Test of generateNewTopLevelType method, of class
     * TypeIntersectionGenerator for a build-in datatype.
     */
    @Test
    public void testGenerateNewTopLevelTypeBuildIn() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        types.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        String newTypeName = "{http://www.w3.org/2001/XMLSchema}string";
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, null, null, null);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertEquals(null, ((SimpleType) result).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class
     * TypeIntersectionGenerator for a set containing an any type.
     */
    @Test
    public void testGenerateNewTopLevelTypeAnyType() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}anyType", null);
        types.add(simpleTypeA);
        types.add(simpleTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        String newTypeName = "{http://www.w3.org/2001/XMLSchema}string";
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, null, null, null);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertEquals(null, ((SimpleType) result).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class
     * TypeIntersectionGenerator for an empty set.
     */
    @Test
    public void testGenerateNewTopLevelTypeEmptySet() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();

        XSDSchema outputSchema = new XSDSchema("A");
        String newTypeName = "{http://www.w3.org/2001/XMLSchema}string";
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, null, null, null);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertEquals(null, ((SimpleType) result).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for
     * a simpleType set containing an any simple type.
     */
    @Test
    public void testgenerateNewTopLevelTypeSimpleTypesAnySimpleType() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        types.add(simpleTypeA);
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}anySimpleType", null);
        types.add(simpleTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        String newTypeName = "{http://www.w3.org/2001/XMLSchema}string";
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertEquals(null, ((SimpleType) result).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for
     * simpleTypes with ID.
     */
    @Test
    public void testgenerateNewTopLevelTypeSimpleTypesDifferentIDs() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.setId("idOne");
        types.add(simpleTypeA);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeA", null);
        simpleTypeB.setId("idTwo");
        types.add(simpleTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}intersection-type.simpleTypeA.simpleTypeB";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for
     * simpleTypes with ID.
     */
    @Test
    public void testgenerateNewTopLevelTypeSimpleTypesSameIDs() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.setId("idOne");
        types.add(simpleTypeA);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeA", null);
        simpleTypeB.setId("idOne");
        types.add(simpleTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}intersection-type.simpleTypeA.simpleTypeB";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals("idOne", ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for
     * simpleTypes with ID.
     */
    @Test
    public void testgenerateNewTopLevelTypeSimpleTypesSingleID() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.setId("idOne");
        types.add(simpleTypeA);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeA", null);
        types.add(simpleTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}intersection-type.simpleTypeA.simpleTypeB";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for
     * simpleTypes with annotation.
     */
    @Test
    public void testgenerateNewTopLevelTypeSimpleTypesAnnotation() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        simpleTypeA.setAnnotation(annotationA);
        types.add(simpleTypeA);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeA", null);
        Annotation annotationB = new Annotation();
        AppInfo appInfoB = new AppInfo();
        appInfoB.setSource("blaB");
        Documentation documentationB = new Documentation();
        documentationB.setSource("blaaB");
        annotationB.addAppInfos(appInfoB);
        annotationB.addDocumentations(documentationB);
        simpleTypeB.setAnnotation(annotationB);
        types.add(simpleTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}intersection-type.simpleTypeA.simpleTypeB";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for
     * simpleType with ID.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleSimpleTypeID() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null))));
        simpleTypeA.setId("idOne");
        types.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}simpleTypeA";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals("idOne", ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for
     * simpleType with annotation.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleSimpleTypeAnnotation() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null))));
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        simpleTypeA.setAnnotation(annotationA);
        types.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}simpleTypeA";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for
     * a single anonymous simpleType.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleSimpleTypeAnonymousType() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null))));
        simpleTypeA.setIsAnonymous(true);
        types.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}simpleTypeA";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * set of anonymous types.
     */
    @Test
    public void testgenerateNewTopLevelTypeSimpleTypesAnonymousTypes() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.setIsAnonymous(true);
        types.add(simpleTypeA);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeA", null);
        simpleTypeB.setIsAnonymous(true);
        types.add(simpleTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}intersection-type.simpleTypeA.simpleTypeB";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for
     * a single anonymous simpleType from another schema.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleSimpleTypeAnonymousTypeOtherSchema() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null))));
        simpleTypeA.setIsAnonymous(true);
        types.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema(""));
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}simpleTypeA";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * simpleType set which finalizes list.
     */
    @Test
    public void testgenerateNewTopLevelTypeSimpleTypesFinalList() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.List);
        types.add(simpleTypeA);

        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeA", null);
        simpleTypeB.addFinalModifier(SimpleTypeInheritanceModifier.List);
        types.add(simpleTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}intersection-type.simpleTypeA.simpleTypeB";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        HashSet<SimpleTypeInheritanceModifier> finalValue = new HashSet<SimpleTypeInheritanceModifier>();
        finalValue.add(SimpleTypeInheritanceModifier.List);
        assertEquals(finalValue, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * simpleType set which finalizes restriction.
     */
    @Test
    public void testgenerateNewTopLevelTypeSimpleTypesFinalRestriction() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.Restriction);
        types.add(simpleTypeA);

        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeA", null);
        simpleTypeB.addFinalModifier(SimpleTypeInheritanceModifier.Restriction);
        types.add(simpleTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}intersection-type.simpleTypeA.simpleTypeB";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        HashSet<SimpleTypeInheritanceModifier> finalValue = new HashSet<SimpleTypeInheritanceModifier>();
        finalValue.add(SimpleTypeInheritanceModifier.Restriction);
        assertEquals(finalValue, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * simpleType set which finalizes union.
     */
    @Test
    public void testgenerateNewTopLevelTypeSimpleTypesFinalUnion() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.Union);
        types.add(simpleTypeA);

        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeA", null);
        simpleTypeB.addFinalModifier(SimpleTypeInheritanceModifier.Union);
        types.add(simpleTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));

        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}intersection-type.simpleTypeA.simpleTypeB";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        HashSet<SimpleTypeInheritanceModifier> finalValue = new HashSet<SimpleTypeInheritanceModifier>();
        finalValue.add(SimpleTypeInheritanceModifier.Union);
        assertEquals(finalValue, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * simpleType set which finalizes list.
     */
    @Test
    public void testgenerateNewTopLevelTypeSimpleTypesFinalDefaultList() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.List);
        types.add(simpleTypeA);

        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeA", null);
        types.add(simpleTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        outputSchema.addFinalDefault(XSDSchema.FinalDefault.list);
        outputSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.list);
        typeOldSchemaMap.put(simpleTypeA, oldSchema);
        typeOldSchemaMap.put(simpleTypeB, oldSchema);
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}intersection-type.simpleTypeA.simpleTypeB";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        HashSet<SimpleTypeInheritanceModifier> finalValue = new HashSet<SimpleTypeInheritanceModifier>();
        finalValue.add(SimpleTypeInheritanceModifier.List);
        assertEquals(finalValue, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * simpleType set which finalizes restriction.
     */
    @Test
    public void testgenerateNewTopLevelTypeSimpleTypesFinalDefaultRestriction() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.Restriction);
        types.add(simpleTypeA);

        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeA", null);
        types.add(simpleTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        outputSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        outputSchema.addFinalDefault(XSDSchema.FinalDefault.union);
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        typeOldSchemaMap.put(simpleTypeA, oldSchema);
        typeOldSchemaMap.put(simpleTypeB, oldSchema);
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}intersection-type.simpleTypeA.simpleTypeB";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        HashSet<SimpleTypeInheritanceModifier> finalValue = new HashSet<SimpleTypeInheritanceModifier>();
        finalValue.add(SimpleTypeInheritanceModifier.Restriction);
        assertEquals(finalValue, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * simpleType set which finalizes union.
     */
    @Test
    public void testgenerateNewTopLevelTypeSimpleTypesFinalDefaultUnion() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.Union);
        types.add(simpleTypeA);

        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeA", null);
        types.add(simpleTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        outputSchema.addFinalDefault(XSDSchema.FinalDefault.union);
        outputSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.union);
        typeOldSchemaMap.put(simpleTypeA, oldSchema);
        typeOldSchemaMap.put(simpleTypeB, oldSchema);
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}intersection-type.simpleTypeA.simpleTypeB";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        HashSet<SimpleTypeInheritanceModifier> finalValue = new HashSet<SimpleTypeInheritanceModifier>();
        finalValue.add(SimpleTypeInheritanceModifier.Union);
        assertEquals(finalValue, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * simpleType set which finalizes union which is schema default.
     */
    @Test
    public void testgenerateNewTopLevelTypeSimpleTypesFinalDefault() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.Union);
        types.add(simpleTypeA);

        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeA", null);
        types.add(simpleTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        outputSchema.addFinalDefault(XSDSchema.FinalDefault.union);
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.union);
        typeOldSchemaMap.put(simpleTypeA, oldSchema);
        typeOldSchemaMap.put(simpleTypeB, oldSchema);
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}intersection-type.simpleTypeA.simpleTypeB";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * simpleType which finalizes list.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleSimpleTypeFinalList() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null))));
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.List);
        types.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}intersection-type.simpleTypeA.simpleTypeB";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        HashSet<SimpleTypeInheritanceModifier> finalValue = new HashSet<SimpleTypeInheritanceModifier>();
        finalValue.add(SimpleTypeInheritanceModifier.List);
        assertEquals(finalValue, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * simpleType which finalizes restriction.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleSimpleTypeFinalRestriction() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null))));
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.Restriction);
        types.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}intersection-type.simpleTypeA.simpleTypeB";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        HashSet<SimpleTypeInheritanceModifier> finalValue = new HashSet<SimpleTypeInheritanceModifier>();
        finalValue.add(SimpleTypeInheritanceModifier.Restriction);
        assertEquals(finalValue, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * simpleType which finalizes union.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleSimpleTypeFinalUnion() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null))));
        simpleTypeA.addFinalModifier(SimpleTypeInheritanceModifier.Union);
        types.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}intersection-type.simpleTypeA.simpleTypeB";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        HashSet<SimpleTypeInheritanceModifier> finalValue = new HashSet<SimpleTypeInheritanceModifier>();
        finalValue.add(SimpleTypeInheritanceModifier.Union);
        assertEquals(finalValue, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * simpleType which finalizes union which is schema default.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleSimpleTypeFinalDefault() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null))));
        types.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        outputSchema.addFinalDefault(XSDSchema.FinalDefault.union);
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.union);
        typeOldSchemaMap.put(simpleTypeA, oldSchema);
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}intersection-type.simpleTypeA.simpleTypeB";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * set of simpleTypes containing same types.
     */
    @Test
    public void testgenerateNewTopLevelTypeSimpleTypesRemoveTypes() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        types.add(simpleTypeA);
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        types.add(simpleTypeB);
        SimpleType simpleTypeC = new SimpleType("{http://www.w3.org/2001/XMLSchema}integer", null);
        types.add(simpleTypeC);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeC, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}intersection-type.string.integer";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertEquals(null, result);
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for
     * a single list type.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleSimpleTypeList() throws Exception {
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentList simpleContentList = new SimpleContentList(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", simpleContentList);
        types.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}simpleTypeA";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().size() == 1);
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertTrue(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance() instanceof SimpleContentList);
        assertTrue(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance() instanceof SimpleContentList);
        assertEquals(null, ((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getAnnotation());
        assertTrue(((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBaseSimpleType().getFinalModifiers());
        assertEquals(null, ((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBaseSimpleType().getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for
     * a single list type with ID.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleSimpleTypeListID() throws Exception {
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentList simpleContentList = new SimpleContentList(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        simpleContentList.setId("id");
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", simpleContentList);
        types.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}simpleTypeA";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().size() == 1);
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertTrue(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance() instanceof SimpleContentList);
        assertTrue(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance() instanceof SimpleContentList);
        assertEquals(null, ((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getAnnotation());
        assertTrue(((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBaseSimpleType().getFinalModifiers());
        assertEquals(null, ((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBaseSimpleType().getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for
     * a single list type with annotation.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleSimpleTypeListAnnotation() throws Exception {
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
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", simpleContentList);
        types.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}simpleTypeA";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().size() == 1);
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertTrue(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance() instanceof SimpleContentList);
        assertEquals(null, ((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getAnnotation());
        assertTrue(((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBaseSimpleType().getFinalModifiers());
        assertEquals(null, ((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBaseSimpleType().getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentList) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for
     * a single union type.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleSimpleTypeUnion() throws Exception {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        simpleTypeB.setIsAnonymous(true);
        LinkedList<SymbolTableRef<Type>> memberTypes = new LinkedList<SymbolTableRef<Type>>();
        memberTypes.add(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        memberTypes.add(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        SimpleContentUnion simpleContentUnion = new SimpleContentUnion(memberTypes);

        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeC = new SimpleType("{A}simpleTypeA", simpleContentUnion);
        types.add(simpleTypeC);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeC, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}simpleTypeA";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertEquals(null, ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getId());
        assertEquals(null, ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAnnotation());
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().size() == 2);
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertEquals(false, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).isAnonymous());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getId());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getName()).getReference()));
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference() instanceof SimpleType);
        assertEquals(false, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference()).isAnonymous());
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
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for
     * a single union type with ID.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleSimpleTypeUnionID() throws Exception {
        SimpleType simpleTypeA = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeB", null);
        simpleTypeB.setIsAnonymous(true);
        LinkedList<SymbolTableRef<Type>> memberTypes = new LinkedList<SymbolTableRef<Type>>();
        memberTypes.add(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        memberTypes.add(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        SimpleContentUnion simpleContentUnion = new SimpleContentUnion(memberTypes);
        simpleContentUnion.setId("id");

        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeC = new SimpleType("{A}simpleTypeA", simpleContentUnion);
        types.add(simpleTypeC);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeC, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}simpleTypeA";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertEquals(null, ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getId());
        assertEquals(null, ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAnnotation());
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().size() == 2);
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertEquals(false, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).isAnonymous());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getId());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getName()).getReference()));
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference() instanceof SimpleType);
        assertEquals(false, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference()).isAnonymous());
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
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for
     * a single union type with Annotation.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleSimpleTypeUnionAnnotation() throws Exception {
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

        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeC = new SimpleType("{A}simpleTypeA", simpleContentUnion);
        types.add(simpleTypeC);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeC, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}simpleTypeA";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertEquals(null, ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getId());
        assertEquals(null, ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAnnotation());
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().size() == 2);
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertEquals(false, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).isAnonymous());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getId());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getName()).getReference()));
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference() instanceof SimpleType);
        assertEquals(false, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getLast().getReference()).isAnonymous());
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
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for
     * a single restriction type.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleSimpleTypeRestriction() throws Exception {
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", simpleContentRestriction);
        types.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}simpleTypeA";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());


        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().size() == 1);
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertTrue(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance() instanceof SimpleContentRestriction);

        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getAnnotation());
        assertEquals(new LinkedList(), ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getEnumeration());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getFractionDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getPattern());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getTotalDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getWhitespace());
        assertTrue(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for
     * a single restriction type with facetts.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleSimpleTypeRestrictionFacetts1st() throws Exception {
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        LinkedList<String> enumeration = new LinkedList<String>();
        enumeration.add("foo");
        simpleContentRestriction.addEnumeration(enumeration);
        SimpleContentFixableRestrictionProperty<Integer> facetInt = new SimpleContentFixableRestrictionProperty<Integer>(2, false);
        simpleContentRestriction.setFractionDigits(facetInt);
        simpleContentRestriction.setWhitespace(new SimpleContentFixableRestrictionProperty<SimpleContentPropertyWhitespace>(SimpleContentPropertyWhitespace.Collapse, false));
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", simpleContentRestriction);
        types.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}simpleTypeA";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().size() == 1);
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertTrue(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance() instanceof SimpleContentRestriction);
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getAnnotation());
        assertEquals(enumeration, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getEnumeration());
        assertEquals(facetInt.getValue(), ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getFractionDigits().getValue());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getPattern());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getTotalDigits());
        assertEquals(SimpleContentPropertyWhitespace.Collapse, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getWhitespace().getValue());
        assertTrue(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for
     * a single restriction type with facetts.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleSimpleTypeRestrictionFacetts2nd() throws Exception {
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        SimpleContentFixableRestrictionProperty<Integer> facetInt = new SimpleContentFixableRestrictionProperty<Integer>(2, false);
        SimpleContentFixableRestrictionProperty<String> facetString = new SimpleContentFixableRestrictionProperty<String>("2", false);
        simpleContentRestriction.setLength(facetInt);
        simpleContentRestriction.setPattern(facetString);
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", simpleContentRestriction);
        types.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}simpleTypeA";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().size() == 1);
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertTrue(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance() instanceof SimpleContentRestriction);
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getAnnotation());
        assertEquals(new LinkedList(), ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getEnumeration());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getFractionDigits());
        assertEquals(facetInt.getValue(), ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getLength().getValue());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinLength());
        assertEquals(facetString.getValue(), ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getPattern().getValue());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getTotalDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getWhitespace());
        assertTrue(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for
     * a single restriction type with facetts.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleSimpleTypeRestrictionFacetts3rd() throws Exception {
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        SimpleContentFixableRestrictionProperty<Integer> facetInt = new SimpleContentFixableRestrictionProperty<Integer>(2, false);
        SimpleContentFixableRestrictionProperty<String> facetString = new SimpleContentFixableRestrictionProperty<String>("2", false);
        //simpleContentRestriction.setMaxExclusive(facetString);
        //simpleContentRestriction.setMaxInclusive(facetString);
        simpleContentRestriction.setMaxLength(facetInt);
        //simpleContentRestriction.setMinExclusive(facetString);
        //simpleContentRestriction.setMinInclusive(facetString);
        simpleContentRestriction.setMinLength(facetInt);
        //simpleContentRestriction.setTotalDigits(facetInt);
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", simpleContentRestriction);
        types.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}simpleTypeA";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().size() == 1);
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertTrue(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance() instanceof SimpleContentRestriction);
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getAnnotation());
        assertEquals(new LinkedList(), ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getEnumeration());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getFractionDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxInclusive());
        assertEquals(facetInt.getValue(), ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxLength().getValue());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinInclusive());
        assertEquals(facetInt.getValue(), ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinLength().getValue());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getPattern());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getTotalDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getWhitespace());
        assertTrue(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for
     * a single restriction type with facetts.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleSimpleTypeRestrictionFacetts4th() throws Exception {
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        SimpleContentFixableRestrictionProperty<Integer> facetInt = new SimpleContentFixableRestrictionProperty<Integer>(2, false);
        SimpleContentFixableRestrictionProperty<String> facetString = new SimpleContentFixableRestrictionProperty<String>("2", false);
        //simpleContentRestriction.setMaxExclusive(facetString);
        simpleContentRestriction.setMaxInclusive(facetString);
        //simpleContentRestriction.setMinExclusive(facetString);
        simpleContentRestriction.setMinInclusive(facetString);
        simpleContentRestriction.setTotalDigits(facetInt);
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", simpleContentRestriction);
        types.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}simpleTypeA";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().size() == 1);
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertTrue(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance() instanceof SimpleContentRestriction);
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getAnnotation());
        assertEquals(new LinkedList(), ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getEnumeration());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getFractionDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxExclusive());
        assertEquals(facetString.getValue(), ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxInclusive().getValue());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinExclusive());
        assertEquals(facetString.getValue(), ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinInclusive().getValue());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getPattern());
        assertEquals(facetInt.getValue(), ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getTotalDigits().getValue());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getWhitespace());
        assertTrue(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for
     * a single restriction type with facetts.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleSimpleTypeRestrictionFacetts5th() throws Exception {
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        SimpleContentFixableRestrictionProperty<String> facetString1 = new SimpleContentFixableRestrictionProperty<String>("5", false);
        SimpleContentFixableRestrictionProperty<String> facetString2 = new SimpleContentFixableRestrictionProperty<String>("2", false);
        simpleContentRestriction.setMaxExclusive(facetString1);
        simpleContentRestriction.setMinExclusive(facetString2);
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", simpleContentRestriction);
        types.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}simpleTypeA";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().size() == 1);
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertTrue(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance() instanceof SimpleContentRestriction);
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getAnnotation());
        assertEquals(new LinkedList(), ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getEnumeration());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getFractionDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getLength());
        assertEquals(facetString1.getValue(), ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxExclusive().getValue());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxLength());
        assertEquals(facetString2.getValue(), ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinExclusive().getValue());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getPattern());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getTotalDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getWhitespace());
        assertTrue(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for
     * a single restriction type with ID.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleSimpleTypeRestrictionID() throws Exception {
        SimpleType simpleTypeB = new SimpleType("{http://www.w3.org/2001/XMLSchema}string", null);
        SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(new SymbolTableRef<Type>("{http://www.w3.org/2001/XMLSchema}string", simpleTypeB));
        simpleContentRestriction.setId("id");
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", simpleContentRestriction);
        types.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);

        String newTypeName = "{A}simpleTypeA";
        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().size() == 1);
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertTrue(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance() instanceof SimpleContentRestriction);
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getAnnotation());
        assertEquals(new LinkedList(), ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getEnumeration());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getFractionDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getPattern());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getTotalDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getWhitespace());
        assertTrue(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for
     * a single restriction type with Annotation.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleSimpleTypeRestrictionAnnotation() throws Exception {
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
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", simpleContentRestriction);
        types.add(simpleTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, null, null);
        String newTypeName = "{A}complexTypeA";
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().size() == 1);
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertTrue(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance() instanceof SimpleContentRestriction);
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getAnnotation());
        assertEquals(new LinkedList(), ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getEnumeration());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getFractionDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMaxLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinExclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinInclusive());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getMinLength());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getPattern());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getTotalDigits());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getWhitespace());
        assertTrue(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase() instanceof SimpleType);
        assertEquals("{http://www.w3.org/2001/XMLSchema}string", ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName());
        assertEquals(false, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().isAnonymous());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getId());
        assertEquals(null, ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase().getName()));
        assertFalse(outputSchema.getTypes().contains(((SimpleContentRestriction) ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance()).getBase()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * complexType.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleComplexType() throws Exception {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.addAttribute(new Attribute("{A}attribute"));
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        types.add(oldType);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        String newTypeName = oldType.getName();
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());
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
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * complexType with ID.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleComplexTypeID() throws Exception {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.setId("id");
        oldType.addAttribute(new Attribute("{A}attribute"));
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        types.add(oldType);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        String newTypeName = oldType.getName();
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());
        assertEquals("id", result.getId());
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
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * complexType with annotation.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleComplexTypeAnnotation() throws Exception {
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
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        types.add(oldType);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        String newTypeName = oldType.getName();
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());
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
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for an
     * abstract complexType.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleComplexTypeAbstract() throws Exception {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.setAbstract(true);
        oldType.addAttribute(new Attribute("{A}attribute"));
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        types.add(oldType);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        String newTypeName = oldType.getName();
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertEquals(null, result);
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for an
     * anonymous complexType.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleComplexTypeAnonymous() throws Exception {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.setIsAnonymous(true);
        oldType.addAttribute(new Attribute("{A}attribute"));
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        types.add(oldType);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        String newTypeName = oldType.getName();
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

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
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getParticle());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * mixed complexType.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleComplexTypeMixed() throws Exception {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.setMixed(true);
        oldType.addAttribute(new Attribute("{A}attribute"));
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        types.add(oldType);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        String newTypeName = oldType.getName();
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());

        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(true, (boolean) ((ComplexType) result).getMixed());
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
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * complexType with attributes.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleComplexTypeAttributes() throws Exception {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.setMixed(true);
        Attribute attributeA = new Attribute("{A}attributeA");
        Attribute attributeB = new Attribute("{A}attributeB");
        oldType.addAttribute(attributeA);
        oldType.addAttribute(attributeB);
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        types.add(oldType);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        String newTypeName = "{A}complexTypeA";
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());

        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(true, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 2);
        assertTrue(((ComplexType) result).getAttributes().getFirst() instanceof Attribute);
        assertEquals("{}attributeA", ((Attribute) ((ComplexType) result).getAttributes().getFirst()).getName());
        assertEquals(null, ((Attribute) ((ComplexType) result).getAttributes().getFirst()).getFixed());
        assertEquals(null, ((Attribute) ((ComplexType) result).getAttributes().getFirst()).getDefault());
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) ((ComplexType) result).getAttributes().getFirst()).getForm());
        assertEquals(null, ((Attribute) ((ComplexType) result).getAttributes().getFirst()).getId());
        assertEquals(null, ((Attribute) ((ComplexType) result).getAttributes().getFirst()).getAnnotation());
        assertEquals(false, (boolean) ((Attribute) ((ComplexType) result).getAttributes().getFirst()).getTypeAttr());
        assertEquals("{http://www.w3.org/2001/XMLSchema}anySimpleType", ((Attribute) ((ComplexType) result).getAttributes().getFirst()).getSimpleType().getName());
        assertEquals(AttributeUse.Optional, ((Attribute) ((ComplexType) result).getAttributes().getFirst()).getUse());
        assertTrue(((ComplexType) result).getAttributes().getLast() instanceof Attribute);
        assertEquals("{}attributeB", ((Attribute) ((ComplexType) result).getAttributes().getLast()).getName());
        assertEquals(null, ((Attribute) ((ComplexType) result).getAttributes().getLast()).getFixed());
        assertEquals(null, ((Attribute) ((ComplexType) result).getAttributes().getLast()).getDefault());
        assertEquals(XSDSchema.Qualification.unqualified, ((Attribute) ((ComplexType) result).getAttributes().getLast()).getForm());
        assertEquals(null, ((Attribute) ((ComplexType) result).getAttributes().getLast()).getId());
        assertEquals(null, ((Attribute) ((ComplexType) result).getAttributes().getLast()).getAnnotation());
        assertEquals(false, (boolean) ((Attribute) ((ComplexType) result).getAttributes().getLast()).getTypeAttr());
        assertEquals("{http://www.w3.org/2001/XMLSchema}anySimpleType", ((Attribute) ((ComplexType) result).getAttributes().getLast()).getSimpleType().getName());
        assertEquals(AttributeUse.Optional, ((Attribute) ((ComplexType) result).getAttributes().getLast()).getUse());
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
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * complexType which blocks extension.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleComplexTypeBlockExtension() throws Exception {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        HashSet<ComplexTypeInheritanceModifier> complexTypeInheritanceModifierSet = new HashSet<ComplexTypeInheritanceModifier>();
        complexTypeInheritanceModifierSet.add(ComplexTypeInheritanceModifier.Extension);
        oldType.setBlockModifiers(complexTypeInheritanceModifierSet);
        oldType.addAttribute(new Attribute("{A}attribute"));
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        types.add(oldType);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        String newTypeName = oldType.getName();
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());

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
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * complexType which blocks restriction.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleComplexTypeBlockRestriction() throws Exception {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        HashSet<ComplexTypeInheritanceModifier> complexTypeInheritanceModifierSet = new HashSet<ComplexTypeInheritanceModifier>();
        complexTypeInheritanceModifierSet.add(ComplexTypeInheritanceModifier.Restriction);
        oldType.setBlockModifiers(complexTypeInheritanceModifierSet);
        oldType.addAttribute(new Attribute("{A}attribute"));
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        types.add(oldType);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        String newTypeName = oldType.getName();
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());

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
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * complexType which blocks extension per schema default.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleComplexTypeBlockDefaultExtension() throws Exception {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.addAttribute(new Attribute("{A}attribute"));
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        types.add(oldType);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.extension);
        typeOldSchemaMap.put(oldType, oldSchema);
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        String newTypeName = oldType.getName();
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());

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
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * complexType which blocks restriction per schema default.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleComplexTypeBlockDefaultRestriction() throws Exception {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.addAttribute(new Attribute("{A}attribute"));
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        types.add(oldType);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.restriction);
        typeOldSchemaMap.put(oldType, oldSchema);
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        String newTypeName = oldType.getName();
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());

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
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * complexType which finalizes extension per schema default.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleComplexTypeFinalDefaultExtension() throws Exception {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.addAttribute(new Attribute("{A}attribute"));
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        types.add(oldType);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.extension);
        typeOldSchemaMap.put(oldType, oldSchema);
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        String newTypeName = oldType.getName();
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());

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
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * complexType which finalizes restriction per schema default.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleComplexTypeFinalDefaultRestriction() throws Exception {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.addAttribute(new Attribute("{A}attribute"));
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        types.add(oldType);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        typeOldSchemaMap.put(oldType, oldSchema);
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        String newTypeName = oldType.getName();
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());

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
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * complexType with complex content ID.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleComplexTypeComplexContentID() throws Exception {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        complexContentType.setId("id");
        oldType.addAttribute(new Attribute("{A}attribute"));
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        types.add(oldType);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        String newTypeName = oldType.getName();
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());

        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getLast() instanceof Attribute);
        assertTrue(((ComplexType) result).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getParticle());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals("id", ((ComplexContentType) ((ComplexType) result).getContent()).getId());
        assertEquals(null, ((ComplexContentType) ((ComplexType) result).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) result).getContent()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * complexType with complex content annotation.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleComplexTypeComplexContentAnnotation() throws Exception {
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
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        types.add(oldType);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        String newTypeName = oldType.getName();
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());

        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getLast() instanceof Attribute);
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
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * mixed complexType with complex content.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleComplexTypeComplexContentMixed() throws Exception {
        ComplexContentType complexContentType = new ComplexContentType(new ChoicePattern());
        ComplexType oldType = new ComplexType("{A}complexTypeA", complexContentType);
        oldType.setMixed(true);
        oldType.addAttribute(new Attribute("{A}attribute"));
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        types.add(oldType);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        String newTypeName = oldType.getName();
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());

        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(true, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 1);
        assertTrue(((ComplexType) result).getAttributes().getLast() instanceof Attribute);
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
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * complexType with simple content.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleComplexTypeSimpleContent() throws Exception {
        SimpleContentType simpleContentType = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtension = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentType.setInheritance(simpleContentExtension);
        ComplexType oldType = new ComplexType("{A}complexTypeA", simpleContentType);
        simpleContentExtension.addAttribute(new Attribute("{A}attribute"));
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        types.add(oldType);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        String newTypeName = oldType.getName();
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());

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
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * complexType with simple content with content ID.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleComplexTypeSimpleContentContentID() throws Exception {
        SimpleContentType simpleContentType = new SimpleContentType();
        simpleContentType.setId("id");
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtension = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentType.setInheritance(simpleContentExtension);
        ComplexType oldType = new ComplexType("{A}complexTypeA", simpleContentType);
        simpleContentExtension.addAttribute(new Attribute("{A}attribute"));
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        types.add(oldType);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        String newTypeName = oldType.getName();
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());

        assertEquals(null, result.getId());
        assertEquals(null, result.getAnnotation());
        assertEquals(false, (boolean) ((ComplexType) result).getMixed());
        assertEquals(false, (boolean) ((ComplexType) result).isAbstract());
        assertEquals(null, ((ComplexType) result).getFinalModifiers());
        assertEquals(null, ((ComplexType) result).getBlockModifiers());
        assertTrue(((ComplexType) result).getAttributes().size() == 0);
        assertTrue(((ComplexType) result).getContent() instanceof SimpleContentType);
        assertEquals(null, ((SimpleContentType) ((ComplexType) result).getContent()).getAnnotation());
        assertEquals("id", ((SimpleContentType) ((ComplexType) result).getContent()).getId());
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
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * complexType with simple content with content annotation.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleComplexTypeSimpleContentContentAnnotation() throws Exception {
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
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        types.add(oldType);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        String newTypeName = oldType.getName();
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());

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
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * complexType with simple content with inheritance ID.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleComplexTypeSimpleContentInheritanceID() throws Exception {
        SimpleContentType simpleContentType = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtension = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentExtension.setId("id");
        simpleContentType.setInheritance(simpleContentExtension);
        ComplexType oldType = new ComplexType("{A}complexTypeA", simpleContentType);
        simpleContentExtension.addAttribute(new Attribute("{A}attribute"));
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        types.add(oldType);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        String newTypeName = oldType.getName();
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());

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
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getBase() instanceof SimpleType);
        assertEquals(simpleTypeA.getName(), ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getBase().getName());
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().size() == 1);
        assertTrue(((SimpleContentExtension) ((SimpleContentType) ((ComplexType) result).getContent()).getInheritance()).getAttributes().getLast() instanceof Attribute);
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(result.getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(result.getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a
     * complexType with simple content with inheritance annotation.
     */
    @Test
    public void testgenerateNewTopLevelTypeSingleComplexTypeSimpleContentInheritanceAnnotation() throws Exception {
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
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        types.add(oldType);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(oldType, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        String newTypeName = oldType.getName();
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(result instanceof ComplexType);
        assertEquals(oldType.getName(), result.getName());

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
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypes() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getMixed());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getParticle());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals("", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes and an anyType.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesAnyType() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        SimpleType simpleTypeC = new SimpleType("{http://www.w3.org/2001/XMLSchema}anyType", null);
        types.add(complexTypeA);
        types.add(complexTypeB);
        types.add(simpleTypeC);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getMixed());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getParticle());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals("", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with IDs.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesSingleID() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        complexTypeA.setId("idOne");
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getMixed());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getParticle());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals("", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with IDs.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesDifferentIDs() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        complexTypeA.setId("idOne");
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        complexTypeB.setId("idTwo");
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getMixed());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getParticle());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals("", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with IDs.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesSameID() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        complexTypeA.setId("idOne");
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        complexTypeB.setId("idOne");
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals("idOne", outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getMixed());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getParticle());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals("", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with annotations.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesAnnotations() throws Exception {
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
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        LinkedHashSet<String> usedIDs = new LinkedHashSet<String>();
        usedIDs.add("idOne.idTwo");
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getMixed());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getParticle());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals("", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of abstract complexTypes.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesAbstract() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        complexTypeA.setAbstract(true);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        complexTypeB.setAbstract(true);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertEquals(null, result);
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of anonymous complexTypes.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesAnonymous() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        complexTypeA.setIsAnonymous(true);
        types.add(complexTypeA);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(true, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().isAnonymous());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getMixed());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getParticle());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of mixed complexTypes.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesMixed() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        complexTypeA.setMixed(true);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        complexTypeB.setMixed(true);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getMixed());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getParticle());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals("", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(true, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes which blocks extension.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesBlockExtension() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        complexTypeA.addBlockModifier(ComplexTypeInheritanceModifier.Extension);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        complexTypeB.addBlockModifier(ComplexTypeInheritanceModifier.Extension);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        HashSet<ComplexTypeInheritanceModifier> block = new HashSet<ComplexTypeInheritanceModifier>();
        block.add(ComplexTypeInheritanceModifier.Extension);
        assertEquals(block, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getMixed());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getParticle());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals("", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes which blocks restriction.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesBlockRestriction() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        complexTypeA.addBlockModifier(ComplexTypeInheritanceModifier.Restriction);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        complexTypeB.addBlockModifier(ComplexTypeInheritanceModifier.Restriction);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        HashSet<ComplexTypeInheritanceModifier> block = new HashSet<ComplexTypeInheritanceModifier>();
        block.add(ComplexTypeInheritanceModifier.Restriction);
        assertEquals(block, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getMixed());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getParticle());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals("", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes which blocks extension per default.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesBlockDefaultExtension() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        complexTypeB.addBlockModifier(ComplexTypeInheritanceModifier.Extension);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.extension);
        typeOldSchemaMap.put(complexTypeA, oldSchema);
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        HashSet<ComplexTypeInheritanceModifier> block = new HashSet<ComplexTypeInheritanceModifier>();
        block.add(ComplexTypeInheritanceModifier.Extension);
        assertEquals(block, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getMixed());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getParticle());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals("", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes which blocks restriction per default.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesBlockDefaultRestriction() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        complexTypeB.addBlockModifier(ComplexTypeInheritanceModifier.Restriction);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.restriction);
        typeOldSchemaMap.put(complexTypeA, oldSchema);
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        HashSet<ComplexTypeInheritanceModifier> block = new HashSet<ComplexTypeInheritanceModifier>();
        block.add(ComplexTypeInheritanceModifier.Restriction);
        assertEquals(block, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getMixed());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getParticle());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals("", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes which blocks restriction like the output schema default.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesBlockSchemaDefault() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        complexTypeB.addBlockModifier(ComplexTypeInheritanceModifier.Restriction);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.addBlockDefault(XSDSchema.BlockDefault.restriction);
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addBlockDefault(XSDSchema.BlockDefault.restriction);
        typeOldSchemaMap.put(complexTypeA, oldSchema);
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getMixed());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getParticle());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals("", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes which finalizes restriction like the output schema default.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesFinalSchemaDefault() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        complexTypeB.addFinalModifier(ComplexTypeInheritanceModifier.Restriction);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        outputSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        XSDSchema oldSchema = new XSDSchema("A");
        oldSchema.addFinalDefault(XSDSchema.FinalDefault.restriction);
        typeOldSchemaMap.put(complexTypeA, oldSchema);
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertEquals(new LinkedList(), ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getMixed());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getParticle());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals("", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with attributes.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesAttributes() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", null);
        complexTypeA.addAttribute(new Attribute("{A}attributeA"));
        complexTypeA.addAttribute(new Attribute("{A}attributeB"));
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", null);
        complexTypeB.addAttribute(new Attribute("{A}attributeA"));
        complexTypeB.addAttribute(new Attribute("{A}attributeB"));
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes().size() == 2);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes().get(0) instanceof Attribute);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes().get(1) instanceof Attribute);
        assertEquals("{}attributeA", ((Attribute) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes().get(0)).getName());
        assertEquals("{}attributeB", ((Attribute) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes().get(1)).getName());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getMixed());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getParticle());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals("", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with complex content with optional attributes without intersection.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesComplexContentAttributesOptional() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexContentType complexContentTypeA = new ComplexContentType();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", complexContentTypeA);
        complexTypeA.addAttribute(new Attribute("{A}attributeA"));
        complexTypeA.addAttribute(new Attribute("{A}attributeB"));
        ComplexContentType complexContentTypeB = new ComplexContentType();
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", complexContentTypeB);
        complexTypeB.addAttribute(new Attribute("{A}attributeC"));
        complexTypeB.addAttribute(new Attribute("{A}attributeD"));
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes().size() == 0);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getMixed());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getParticle());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with complex content with required attributes without intersection.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesComplexContentAttributesRequired() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexContentType complexContentTypeA = new ComplexContentType();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", complexContentTypeA);
        Attribute attributeA = new Attribute("{A}attributeA");
        attributeA.setUse(AttributeUse.Required);
        complexTypeA.addAttribute(attributeA);
        complexTypeA.addAttribute(new Attribute("{A}attributeB"));
        ComplexContentType complexContentTypeB = new ComplexContentType();
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", complexContentTypeB);
        complexTypeB.addAttribute(new Attribute("{A}attributeC"));
        complexTypeB.addAttribute(new Attribute("{A}attributeD"));
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertEquals(null, result);
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with complex content with optional particles without intersection.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesComplexContentParticleOptional() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ChoicePattern choicePatternA = new ChoicePattern();
        Element elementA = new Element("{A}elementA");
        CountingPattern countingPatternA = new CountingPattern(0, 1);
        countingPatternA.addParticle(elementA);
        choicePatternA.addParticle(countingPatternA);
        ComplexContentType complexContentTypeA = new ComplexContentType(choicePatternA);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", complexContentTypeA);
        ChoicePattern choicePatternB = new ChoicePattern();
        Element elementB = new Element("{A}elementB");
        CountingPattern countingPatternB = new CountingPattern(0, 1);
        countingPatternB.addParticle(elementB);
        choicePatternB.addParticle(countingPatternB);
        ComplexContentType complexContentTypeB = new ComplexContentType(choicePatternB);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", complexContentTypeB);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes().size() == 0);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getMixed());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getParticle());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with complex content with required particles without intersection.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesComplexContentParticleRequired() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ChoicePattern choicePatternA = new ChoicePattern();
        Element elementA = new Element("{A}elementA");
        choicePatternA.addParticle(elementA);
        ComplexContentType complexContentTypeA = new ComplexContentType(choicePatternA);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", complexContentTypeA);
        ChoicePattern choicePatternB = new ChoicePattern();
        Element elementB = new Element("{A}elementB");
        CountingPattern countingPatternB = new CountingPattern(0, 1);
        countingPatternB.addParticle(elementB);
        choicePatternB.addParticle(countingPatternB);
        ComplexContentType complexContentTypeB = new ComplexContentType(choicePatternB);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", complexContentTypeB);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertEquals(null, result);
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with complex content with IDs.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesComplexContentDifferentIDs() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexContentType complexContentTypeA = new ComplexContentType();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", complexContentTypeA);
        complexContentTypeA.setId("idOne");
        ComplexContentType complexContentTypeB = new ComplexContentType();
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", complexContentTypeB);
        complexContentTypeA.setId("idTwo");
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes().size() == 0);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getMixed());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getParticle());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with complex content with IDs.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesComplexContentDifferentSameIDs() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexContentType complexContentTypeA = new ComplexContentType();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", complexContentTypeA);
        complexContentTypeA.setId("idOne");
        ComplexContentType complexContentTypeB = new ComplexContentType();
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", complexContentTypeB);
        complexContentTypeB.setId("idOne");
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes().size() == 0);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getMixed());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getParticle());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals("idOne", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with complex content with IDs.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesComplexContentSingleIDs() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexContentType complexContentTypeA = new ComplexContentType();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", complexContentTypeA);
        complexContentTypeA.setId("idOne");
        ComplexContentType complexContentTypeB = new ComplexContentType();
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", complexContentTypeB);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes().size() == 0);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getMixed());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getParticle());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with complex content with annotations.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesComplexContentAnnotations() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexContentType complexContentTypeA = new ComplexContentType();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", complexContentTypeA);
        Annotation annotationA = new Annotation();
        AppInfo appInfoA = new AppInfo();
        appInfoA.setSource("blaA");
        Documentation documentationA = new Documentation();
        documentationA.setSource("blaaA");
        annotationA.addAppInfos(appInfoA);
        annotationA.addDocumentations(documentationA);
        complexContentTypeA.setAnnotation(annotationA);
        ComplexContentType complexContentTypeB = new ComplexContentType();
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", complexContentTypeB);
        Annotation annotationB = new Annotation();
        AppInfo appInfoB = new AppInfo();
        appInfoB.setSource("blaB");
        Documentation documentationB = new Documentation();
        documentationB.setSource("blaaB");
        annotationB.addAppInfos(appInfoB);
        annotationB.addDocumentations(documentationB);
        complexContentTypeA.setAnnotation(annotationB);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes().size() == 0);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getMixed());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getParticle());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with mixed complex content.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesComplexContentMixed() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexContentType complexContentTypeA = new ComplexContentType();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", complexContentTypeA);
        complexContentTypeA.setMixed(true);
        ComplexContentType complexContentTypeB = new ComplexContentType();
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", complexContentTypeB);
        complexContentTypeB.setMixed(true);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes().size() == 0);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance());
        assertEquals(true, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getMixed());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getParticle());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with mixed complex content.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesComplexContentSingleMixed() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        ComplexContentType complexContentTypeA = new ComplexContentType();
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", complexContentTypeA);
        complexContentTypeA.setMixed(true);
        ComplexContentType complexContentTypeB = new ComplexContentType();
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", complexContentTypeB);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes().size() == 0);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof ComplexContentType);
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance());
        assertEquals(false, (boolean) ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getMixed());
        assertEquals(null, ((ComplexContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getParticle());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with simple content with IDs.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesSimpleContentDifferentIDs() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleContentType simpleContentTypeA = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtensionA = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentTypeA.setInheritance(simpleContentExtensionA);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", simpleContentTypeA);
        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        simpleContentTypeA.setId("idOne");
        simpleContentTypeB.setId("idTwo");
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes().size() == 0);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof SimpleContentType);
        assertTrue(((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with simple content with IDs.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesSimpleContentSameIDs() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleContentType simpleContentTypeA = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtensionA = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentTypeA.setInheritance(simpleContentExtensionA);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", simpleContentTypeA);
        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        simpleContentTypeA.setId("idOne");
        simpleContentTypeB.setId("idOne");
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes().size() == 0);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof SimpleContentType);
        assertTrue(((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals("idOne", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with simple content with IDs.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesSimpleContentSingleIDs() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleContentType simpleContentTypeA = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtensionA = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentTypeA.setInheritance(simpleContentExtensionA);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", simpleContentTypeA);
        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        simpleContentTypeA.setId("idOne");
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes().size() == 0);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof SimpleContentType);
        assertTrue(((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with simple content with annotations.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesSimpleContentAnnotations() throws Exception {
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
        simpleContentTypeA.setInheritance(simpleContentExtensionA);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", simpleContentTypeA);
        SimpleContentType simpleContentTypeB = new SimpleContentType();
        Annotation annotationB = new Annotation();
        AppInfo appInfoB = new AppInfo();
        appInfoB.setSource("blaB");
        Documentation documentationB = new Documentation();
        documentationB.setSource("blaaB");
        annotationB.addAppInfos(appInfoB);
        annotationB.addDocumentations(documentationB);
        simpleContentTypeB.setAnnotation(annotationB);
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes().size() == 0);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof SimpleContentType);
        assertTrue(((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with simple content extension with IDs.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesSimpleContentExtensionDifferentIDs() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleContentType simpleContentTypeA = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtensionA = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentTypeA.setInheritance(simpleContentExtensionA);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", simpleContentTypeA);
        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        simpleContentExtensionA.setId("idOne");
        simpleContentExtensionB.setId("idTwo");
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes().size() == 0);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof SimpleContentType);
        assertTrue(((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getAnnotation());
        assertEquals(new LinkedList(), ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getAttributes());
        assertEquals("{A}intersection-type.simpleTypeA.simpleTypeA", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getBase().getName());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with simple content extension with IDs.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesSimpleContentExtensionSameIDs() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleContentType simpleContentTypeA = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtensionA = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentTypeA.setInheritance(simpleContentExtensionA);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", simpleContentTypeA);
        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        simpleContentExtensionA.setId("idOne");
        simpleContentExtensionB.setId("idOne");
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes().size() == 0);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof SimpleContentType);
        assertTrue(((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertEquals("idOne", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getAnnotation());
        assertEquals(new LinkedList(), ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getAttributes());
        assertEquals("{A}intersection-type.simpleTypeA.simpleTypeA", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getBase().getName());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with simple content extension with IDs.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesSimpleContentExtensionSingleIDs() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleContentType simpleContentTypeA = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtensionA = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentTypeA.setInheritance(simpleContentExtensionA);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", simpleContentTypeA);
        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        simpleContentExtensionA.setId("idOne");
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes().size() == 0);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof SimpleContentType);
        assertTrue(((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getAnnotation());
        assertEquals(new LinkedList(), ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getAttributes());
        assertEquals("{A}intersection-type.simpleTypeA.simpleTypeA", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getBase().getName());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with simple content extension with annotations.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesSimpleContentExtensionAnnotations() throws Exception {
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
        simpleContentTypeA.setInheritance(simpleContentExtensionA);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", simpleContentTypeA);
        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        Annotation annotationB = new Annotation();
        AppInfo appInfoB = new AppInfo();
        appInfoB.setSource("blaB");
        Documentation documentationB = new Documentation();
        documentationB.setSource("blaaB");
        annotationB.addAppInfos(appInfoB);
        annotationB.addDocumentations(documentationB);
        simpleContentExtensionB.setAnnotation(annotationB);
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes().size() == 0);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof SimpleContentType);
        assertTrue(((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getAnnotation());
        assertEquals(new LinkedList(), ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getAttributes());
        assertEquals("{A}intersection-type.simpleTypeA.simpleTypeA", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getBase().getName());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with simple content extension with optional attributes without intersection.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesSimpleContentExtensionAttributesOptional() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleContentType simpleContentTypeA = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtensionA = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentTypeA.setInheritance(simpleContentExtensionA);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", simpleContentTypeA);
        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        types.add(complexTypeA);
        types.add(complexTypeB);
        simpleContentExtensionA.addAttribute(new Attribute("{A}attributeA"));
        simpleContentExtensionA.addAttribute(new Attribute("{A}attributeB"));
        simpleContentExtensionB.addAttribute(new Attribute("{A}attributeC"));
        simpleContentExtensionB.addAttribute(new Attribute("{A}attributeD"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes().size() == 0);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof SimpleContentType);
        assertTrue(((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getAnnotation());
        assertEquals(new LinkedList(), ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getAttributes());
        assertEquals("{A}intersection-type.simpleTypeA.simpleTypeA", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getBase().getName());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with simple content extension with required attributes without intersection.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesSimpleContentExtensionAttributesRequired() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleContentType simpleContentTypeA = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtensionA = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentTypeA.setInheritance(simpleContentExtensionA);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", simpleContentTypeA);
        SimpleContentType simpleContentTypeB = new SimpleContentType();
        SimpleType simpleTypeB = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtensionB = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeB.getName(), simpleTypeB));
        simpleContentTypeB.setInheritance(simpleContentExtensionB);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", simpleContentTypeB);
        types.add(complexTypeA);
        types.add(complexTypeB);
        Attribute attributeA = new Attribute("{A}attributeA");
        attributeA.setUse(AttributeUse.Required);
        simpleContentExtensionA.addAttribute(attributeA);
        simpleContentExtensionA.addAttribute(new Attribute("{A}attributeB"));
        simpleContentExtensionB.addAttribute(new Attribute("{A}attributeC"));
        simpleContentExtensionB.addAttribute(new Attribute("{A}attributeD"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeB, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertEquals(null, result);
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with simple/complex content extension with optional attributes without intersection.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesSimpleContentComplexContentAttributesOptional() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleContentType simpleContentTypeA = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtensionA = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentTypeA.setInheritance(simpleContentExtensionA);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", simpleContentTypeA);
        ComplexContentType complexContentTypeB = new ComplexContentType();
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", complexContentTypeB);
        complexTypeB.setMixed(true);
        types.add(complexTypeA);
        types.add(complexTypeB);
        simpleContentExtensionA.addAttribute(new Attribute("{A}attributeA"));
        simpleContentExtensionA.addAttribute(new Attribute("{A}attributeB"));
        complexTypeB.addAttribute(new Attribute("{A}attributeC"));
        complexTypeB.addAttribute(new Attribute("{A}attributeD"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes().size() == 0);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof SimpleContentType);
        assertTrue(((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getAnnotation());
        assertEquals(new LinkedList(), ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getAttributes());
        assertEquals("{A}simpleTypeA", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getBase().getName());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with simple/complex content extension with required attributes without intersection.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesSimpleContentComplexContentAttributesRequired() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleContentType simpleContentTypeA = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtensionA = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentTypeA.setInheritance(simpleContentExtensionA);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", simpleContentTypeA);
        ComplexContentType complexContentTypeB = new ComplexContentType();
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", complexContentTypeB);
        complexTypeB.setMixed(true);
        types.add(complexTypeA);
        types.add(complexTypeB);
        Attribute attributeA = new Attribute("{A}attributeA");
        attributeA.setUse(AttributeUse.Required);
        simpleContentExtensionA.addAttribute(attributeA);
        simpleContentExtensionA.addAttribute(new Attribute("{A}attributeB"));
        complexTypeB.addAttribute(new Attribute("{A}attributeC"));
        complexTypeB.addAttribute(new Attribute("{A}attributeD"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertEquals(null, result);
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with simple/complex content extension (mixed).
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesSimpleContentComplexContentMixed() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleContentType simpleContentTypeA = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtensionA = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentTypeA.setInheritance(simpleContentExtensionA);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", simpleContentTypeA);
        ComplexContentType complexContentTypeB = new ComplexContentType();
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", complexContentTypeB);
        complexTypeB.setMixed(true);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes().size() == 0);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof SimpleContentType);
        assertTrue(((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getAnnotation());
        assertEquals(new LinkedList(), ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getAttributes());
        assertEquals("{A}simpleTypeA", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getBase().getName());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with simple/complex content extension (not mixed).
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesSimpleContentComplexContentNotMixed() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleContentType simpleContentTypeA = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtensionA = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentTypeA.setInheritance(simpleContentExtensionA);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", simpleContentTypeA);
        ComplexContentType complexContentTypeB = new ComplexContentType();
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", complexContentTypeB);
        types.add(complexTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertEquals(null, result);
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with simple/complex content extension with IDs.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesSimpleContentComplexContentDifferentIDs() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleContentType simpleContentTypeA = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtensionA = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentTypeA.setInheritance(simpleContentExtensionA);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", simpleContentTypeA);
        ComplexContentType complexContentTypeB = new ComplexContentType();
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", complexContentTypeB);
        complexTypeB.setMixed(true);
        types.add(complexTypeA);
        types.add(complexTypeB);
        simpleContentTypeA.setId("idOne");
        complexContentTypeB.setId("idTwo");

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes().size() == 0);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof SimpleContentType);
        assertTrue(((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getAnnotation());
        assertEquals(new LinkedList(), ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getAttributes());
        assertEquals("{A}simpleTypeA", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getBase().getName());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with simple/complex content extension with IDs.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesSimpleContentComplexContentSameIDs() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleContentType simpleContentTypeA = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtensionA = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentTypeA.setInheritance(simpleContentExtensionA);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", simpleContentTypeA);
        ComplexContentType complexContentTypeB = new ComplexContentType();
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", complexContentTypeB);
        complexTypeB.setMixed(true);
        types.add(complexTypeA);
        types.add(complexTypeB);
        simpleContentTypeA.setId("idOne");
        complexContentTypeB.setId("idOne");

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes().size() == 0);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof SimpleContentType);
        assertTrue(((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getAnnotation());
        assertEquals(new LinkedList(), ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getAttributes());
        assertEquals("{A}simpleTypeA", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getBase().getName());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals("idOne", ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with simple/complex content extension with IDs.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesSimpleContentComplexContentSingleIDs() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleContentType simpleContentTypeA = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtensionA = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentTypeA.setInheritance(simpleContentExtensionA);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", simpleContentTypeA);
        ComplexContentType complexContentTypeB = new ComplexContentType();
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", complexContentTypeB);
        complexTypeB.setMixed(true);
        types.add(complexTypeA);
        types.add(complexTypeB);
        simpleContentTypeA.setId("idOne");

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes().size() == 0);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof SimpleContentType);
        assertTrue(((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getAnnotation());
        assertEquals(new LinkedList(), ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getAttributes());
        assertEquals("{A}simpleTypeA", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getBase().getName());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with simple/complex content extension with annotations.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesSimpleContentComplexContentAnnotations() throws Exception {
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
        simpleContentTypeA.setInheritance(simpleContentExtensionA);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", simpleContentTypeA);
        ComplexContentType complexContentTypeB = new ComplexContentType();
        Annotation annotationB = new Annotation();
        AppInfo appInfoB = new AppInfo();
        appInfoB.setSource("blaB");
        Documentation documentationB = new Documentation();
        documentationB.setSource("blaaB");
        annotationB.addAppInfos(appInfoB);
        annotationB.addDocumentations(documentationB);
        complexContentTypeB.setAnnotation(annotationB);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", complexContentTypeB);
        complexTypeB.setMixed(true);
        types.add(complexTypeA);
        types.add(complexTypeB);
        simpleContentTypeA.setId("idOne");

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes().size() == 0);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof SimpleContentType);
        assertTrue(((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getAnnotation());
        assertEquals(new LinkedList(), ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getAttributes());
        assertEquals("{A}simpleTypeA", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getBase().getName());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with simple/complex content extension with extension ID.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesSimpleContentComplexContentExtensionID() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleContentType simpleContentTypeA = new SimpleContentType();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        SimpleContentExtension simpleContentExtensionA = new SimpleContentExtension(new SymbolTableRef<Type>(simpleTypeA.getName(), simpleTypeA));
        simpleContentTypeA.setInheritance(simpleContentExtensionA);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", simpleContentTypeA);
        ComplexContentType complexContentTypeB = new ComplexContentType();
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", complexContentTypeB);
        complexTypeB.setMixed(true);
        types.add(complexTypeA);
        types.add(complexTypeB);
        simpleContentExtensionA.setId("idOne");

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes().size() == 0);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof SimpleContentType);
        assertTrue(((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertEquals("idOne", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getAnnotation());
        assertEquals(new LinkedList(), ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getAttributes());
        assertEquals("{A}simpleTypeA", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getBase().getName());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of complexTypes with simple/complex content extension with extension annotation.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesSimpleContentComplexContentExtensionAnnotation() throws Exception {
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
        simpleContentTypeA.setInheritance(simpleContentExtensionA);
        ComplexType complexTypeA = new ComplexType("{A}complexTypeA", simpleContentTypeA);
        ComplexContentType complexContentTypeB = new ComplexContentType();

        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", complexContentTypeB);
        complexTypeB.setMixed(true);
        types.add(complexTypeA);
        types.add(complexTypeB);
        simpleContentTypeA.setId("idOne");

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeA, new XSDSchema("A"));
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference() instanceof ComplexType);
        assertEquals(newTypeName, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getName());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getId());
        assertEquals(null, outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getBlockModifiers());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getFinalModifiers());
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getAttributes().size() == 0);
        assertTrue(((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent() instanceof SimpleContentType);
        assertTrue(((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance() instanceof SimpleContentExtension);
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getId());
        assertEquals(null, ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getAnnotation());
        assertEquals(new LinkedList(), ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getAttributes());
        assertEquals("{A}simpleTypeA", ((SimpleContentExtension) ((SimpleContentType) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent()).getInheritance()).getBase().getName());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getAnnotation());
        assertEquals(null, ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getContent().getId());
        assertEquals(false, (boolean) ((ComplexType) outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()).getMixed());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(newTypeName));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(newTypeName).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of simpleType and complexType with optional attributes without intersection.
     */
    @Test
    public void testgenerateNewTopLevelTypeSimpleTypeComplexTypeAttributesOptional() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        ComplexContentType complexContentTypeB = new ComplexContentType();
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", complexContentTypeB);
        complexTypeB.setMixed(true);
        types.add(simpleTypeA);
        types.add(complexTypeB);
        complexTypeB.addAttribute(new Attribute("{A}attributeC"));
        complexTypeB.addAttribute(new Attribute("{A}attributeD"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertEquals(null, ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getId());
        assertEquals(null, ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAnnotation());
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().size() == 1);
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertEquals("{A}simpleTypeA", ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getName());
        assertEquals(false, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).isAnonymous());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getId());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getName()).getReference()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of simpleType and complexType with required attributes without intersection.
     */
    @Test
    public void testgenerateNewTopLevelTypeSimpleTypeComplexTypeAttributesRequired() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        ComplexContentType complexContentTypeB = new ComplexContentType();
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", complexContentTypeB);
        complexTypeB.setMixed(true);
        types.add(simpleTypeA);
        types.add(complexTypeB);
        Attribute attributeC = new Attribute("{A}attributeC");
        attributeC.setUse(AttributeUse.Required);
        complexTypeB.addAttribute(attributeC);
        complexTypeB.addAttribute(new Attribute("{A}attributeD"));

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertEquals(null, result);
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of simpleType and complexType with non optional particle.
     */
    @Test
    public void testgenerateNewTopLevelTypeComplexTypesSimpleContentComplexContentNotOptionalParticle() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        ComplexContentType complexContentTypeB = new ComplexContentType();
        SequencePattern sequencePatternB = new SequencePattern();
        Element elementB = new Element("{A}elementB");
        sequencePatternB.addParticle(elementB);
        complexContentTypeB.setParticle(sequencePatternB);
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", complexContentTypeB);
        complexTypeB.setMixed(true);
        types.add(simpleTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertEquals(null, result);
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of simpleType and mixed complexType.
     */
    @Test
    public void testgenerateNewTopLevelTypeSimpleTypeComplexTypeMixed() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        ComplexContentType complexContentTypeB = new ComplexContentType();
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", complexContentTypeB);
        complexTypeB.setMixed(true);
        types.add(simpleTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertTrue(((SimpleType) result) instanceof SimpleType);
        assertEquals(newTypeName, ((SimpleType) result).getName());
        assertEquals(false, ((SimpleType) result).isAnonymous());
        assertEquals(null, ((SimpleType) result).getId());
        assertEquals(null, ((SimpleType) result).getAnnotation());
        assertEquals(null, ((SimpleType) result).getFinalModifiers());
        assertTrue(((SimpleType) result).getInheritance() instanceof SimpleContentUnion);
        assertEquals(null, ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getId());
        assertEquals(null, ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAnnotation());
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().size() == 1);
        assertTrue(((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference() instanceof SimpleType);
        assertEquals("{A}simpleTypeA", ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getName());
        assertEquals(false, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).isAnonymous());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getId());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getAnnotation());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getFinalModifiers());
        assertEquals(null, ((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getInheritance());
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getName()));
        assertFalse(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) ((SimpleContentUnion) ((SimpleType) result).getInheritance()).getAllMemberTypes().getFirst().getReference()).getName()).getReference()));
        assertTrue(outputSchema.getTypeSymbolTable().hasReference(((SimpleType) result).getName()));
        assertTrue(outputSchema.getTypes().contains(outputSchema.getTypeSymbolTable().getReference(((SimpleType) result).getName()).getReference()));
    }

    /**
     * Test of generateNewTopLevelType method, of class TypeIntersectionGenerator for a set
     * of simpleType and not mixed complexType.
     */
    @Test
    public void testgenerateNewTopLevelTypeSimpleTypeComplexTypeNotMixed() throws Exception {
        LinkedHashSet<Type> types = new LinkedHashSet<Type>();
        SimpleType simpleTypeA = new SimpleType("{A}simpleTypeA", null);
        ComplexContentType complexContentTypeB = new ComplexContentType();
        ComplexType complexTypeB = new ComplexType("{A}complexTypeB", complexContentTypeB);
        types.add(simpleTypeA);
        types.add(complexTypeB);

        XSDSchema outputSchema = new XSDSchema("A");
        LinkedHashMap<String, SymbolTableRef<Type>> localElementTypes = new LinkedHashMap<String, SymbolTableRef<Type>>();
        String newTypeName = "{A}intersection-type.complexTypeA.complexTypeB";
        LinkedHashMap<Type, XSDSchema> typeOldSchemaMap = new LinkedHashMap<Type, XSDSchema>();
        typeOldSchemaMap.put(complexTypeB, new XSDSchema("A"));
        typeOldSchemaMap.put(simpleTypeA, new XSDSchema("A"));
        AttributeParticleIntersectionGenerator attributeParticleIntersectionGenerator = new AttributeParticleIntersectionGenerator(outputSchema, null, null, null, null, null);
        ParticleIntersectionGenerator particleIntersectionGenerator = new ParticleIntersectionGenerator(outputSchema, null, null, null, null, null, null, null);
        TypeIntersectionGenerator instance = new TypeIntersectionGenerator(outputSchema, null, typeOldSchemaMap, attributeParticleIntersectionGenerator, particleIntersectionGenerator);
        attributeParticleIntersectionGenerator.setTypeIntersectionGenerator(instance);
        particleIntersectionGenerator.setTypeIntersectionGenerator(instance);

        Type result = instance.generateNewTopLevelType(types, newTypeName, localElementTypes);

        assertEquals(null, result);
    }
}