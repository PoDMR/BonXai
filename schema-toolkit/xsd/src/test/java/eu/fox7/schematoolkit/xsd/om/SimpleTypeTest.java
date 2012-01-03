package eu.fox7.schematoolkit.xsd.om;

import eu.fox7.schematoolkit.common.SymbolTableRef;
import eu.fox7.schematoolkit.xsd.om.SimpleContentList;
import eu.fox7.schematoolkit.xsd.om.SimpleType;
import eu.fox7.schematoolkit.xsd.om.SimpleTypeInheritance;
import eu.fox7.schematoolkit.xsd.om.SimpleTypeInheritanceModifier;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.Types;

import java.util.HashSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 */
public class SimpleTypeTest extends junit.framework.TestCase {

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link Types.SimpleType#SimpleType(String, SimpleTypeInheritance)}.
     */
    @Test
    public final void testSimpleType() {
        String inputName = "{}testSimpleTypeName";
        SymbolTableRef<Type> test = new SymbolTableRef<Type>("NameRef");
        SimpleContentList inputInheritanceTest = new SimpleContentList(test);
        SimpleType instance = new SimpleType(inputName, inputInheritanceTest);
        assertEquals(inputName, instance.getName());
        assertEquals(inputInheritanceTest, instance.getInheritance());
    }

    /**
     * Test method for {@link Types.SimpleType#getFinalModifiers()}.
     */
    @Test
    public final void testGetFinalModifiers() {
        SymbolTableRef<Type> test = new SymbolTableRef<Type>("NameRef");
        SimpleContentList inputInheritanceTest = new SimpleContentList(test);
        SimpleType instance = new SimpleType("{}someType", inputInheritanceTest);
        HashSet<SimpleTypeInheritanceModifier> inputSimpleTypeInheritanceModifierSet = new HashSet<SimpleTypeInheritanceModifier>();
        inputSimpleTypeInheritanceModifierSet.add(SimpleTypeInheritanceModifier.Union);
        instance.setFinalModifiers(inputSimpleTypeInheritanceModifierSet);
        // Check if it is not the same, but an equal object
        assertNotSame(inputSimpleTypeInheritanceModifierSet, instance.getFinalModifiers());
        assertEquals(inputSimpleTypeInheritanceModifierSet, instance.getFinalModifiers());

    }

    /**
     * Test method for {@link Types.SimpleType#setFinalModifiers(Set)}.
     */
    @Test
    public final void testSetFinalModifiers() {
        SymbolTableRef<Type> test = new SymbolTableRef<Type>("NameRef");
        SimpleContentList inputInheritanceTest = new SimpleContentList(test);
        SimpleType instance = new SimpleType("{}someType", inputInheritanceTest);
        HashSet<SimpleTypeInheritanceModifier> inputSimpleTypeInheritanceModifierSet = new HashSet<SimpleTypeInheritanceModifier>();
        inputSimpleTypeInheritanceModifierSet.add(SimpleTypeInheritanceModifier.Union);
        instance.setFinalModifiers(inputSimpleTypeInheritanceModifierSet);
        // Check if it is not the same, but an equal object
        assertNotSame(inputSimpleTypeInheritanceModifierSet, instance.getFinalModifiers());
        assertEquals(inputSimpleTypeInheritanceModifierSet, instance.getFinalModifiers());
    }

    /**
     * Test method for {@link Types.SimpleType#getInheritance()}.
     */
    @Test
    public final void testGetInheritance() {
        SymbolTableRef<Type> test = new SymbolTableRef<Type>("NameRef");
        SimpleContentList inputInheritanceTest = new SimpleContentList(test);
        SimpleType instance = new SimpleType("{}someType", inputInheritanceTest);
        assertEquals(inputInheritanceTest, instance.getInheritance());
    }

    /**
     * Test method for {@link Types.SimpleType#getName()}.
     */
    @Test
    public final void testGetName() {
        String expResult = "{}testSimpleTypeName";
        SimpleType instance = new SimpleType(expResult, null);
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetNamespace() {
        SimpleType type = new SimpleType("{http://example.com}foo", null);

        assertEquals("http://example.com", type.getNamespace());
    }

    @Test
    public void testGetLocalName() {
        SimpleType type = new SimpleType("{http://example.com}foo", null);

        assertEquals("foo", type.getLocalName());
    }
}
