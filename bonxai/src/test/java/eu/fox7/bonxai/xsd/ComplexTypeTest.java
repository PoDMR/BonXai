package eu.fox7.bonxai.xsd;

import java.util.HashSet;
import java.util.LinkedList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.fox7.bonxai.xsd.Attribute;
import eu.fox7.bonxai.xsd.AttributeParticle;
import eu.fox7.bonxai.xsd.ComplexType;
import eu.fox7.bonxai.xsd.ComplexTypeInheritanceModifier;
import eu.fox7.bonxai.xsd.SimpleContentType;

/**
 */
public class ComplexTypeTest extends junit.framework.TestCase {

    public ComplexTypeTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getAttributes method, of class ComplexType.
     */
    @Test
    public void testGetAttributes() {
        SimpleContentType inputSimpleContentType = new SimpleContentType();
        ComplexType instance = new ComplexType("{}someType", inputSimpleContentType);

        LinkedList<AttributeParticle> inputAttributeParticleList = new LinkedList<AttributeParticle>();
        inputAttributeParticleList.add(new Attribute( "{}attribute" ));

        instance.setAttributes(inputAttributeParticleList);

        // Check if it is not the same, but an equal object
        assertNotSame(inputAttributeParticleList, instance.getAttributes());
        assertEquals(inputAttributeParticleList, instance.getAttributes());
    }

    /**
     * Test of addAttribute method, of class ComplexType.
     */
    @Test
    public void testAddAttribute() {
        SimpleContentType inputSimpleContentType = new SimpleContentType();
        ComplexType instance = new ComplexType("{}someType", inputSimpleContentType);

        Attribute testAttribute = new Attribute( "{}attribute" );
        instance.addAttribute(testAttribute);

        // Check if testAttribute is part of the LinkedList
        assertTrue(instance.getAttributes().contains(testAttribute));
    }

    /**
     * Test of getBlockModifiers method, of class ComplexType.
     */
    @Test
    public void testGetBlockModifiers() {
        SimpleContentType inputSimpleContentType = new SimpleContentType();
        ComplexType instance = new ComplexType("{}someType", inputSimpleContentType);

        HashSet<ComplexTypeInheritanceModifier> inputComplexTypeInheritanceModifierSet = new HashSet<ComplexTypeInheritanceModifier>();
        inputComplexTypeInheritanceModifierSet.add(ComplexTypeInheritanceModifier.Extension);

        instance.setBlockModifiers(inputComplexTypeInheritanceModifierSet);

        // Check if it is not the same, but an equal object
        assertNotSame(inputComplexTypeInheritanceModifierSet, instance.getBlockModifiers());
        assertEquals(inputComplexTypeInheritanceModifierSet, instance.getBlockModifiers());
    }

    /**
     * Test of addBlockModifiers method, of class ComplexType.
     */
    @Test
    public void testAddBlockModifier() {
        SimpleContentType inputSimpleContentType = new SimpleContentType();
        ComplexType instance = new ComplexType("{}someType", inputSimpleContentType);

        instance.addBlockModifier(ComplexTypeInheritanceModifier.Extension);

        // Check if Extension is part of the HashSet
        assertTrue(instance.getBlockModifiers().contains(ComplexTypeInheritanceModifier.Extension));
    }

    /**
     * Test of getContent method, of class ComplexType.
     */
    @Test
    public void testGetContent() {
        SimpleContentType inputSimpleContentType = new SimpleContentType();
        ComplexType instance = new ComplexType("{}someType", inputSimpleContentType);
        assertEquals(inputSimpleContentType, instance.getContent());
    }

    /**
     * Test of getFinalModifiers method, of class ComplexType.
     */
    @Test
    public void testGetFinalModifiers() {
        SimpleContentType inputSimpleContentType = new SimpleContentType();
        ComplexType instance = new ComplexType("{}someType", inputSimpleContentType);

        HashSet<ComplexTypeInheritanceModifier> inputComplexTypeInheritanceModifierSet = new HashSet<ComplexTypeInheritanceModifier>();
        inputComplexTypeInheritanceModifierSet.add(ComplexTypeInheritanceModifier.Extension);

        instance.setFinalModifiers(inputComplexTypeInheritanceModifierSet);

        // Check if it is not the same, but an equal object
        assertNotSame(inputComplexTypeInheritanceModifierSet, instance.getFinalModifiers());
        assertEquals(inputComplexTypeInheritanceModifierSet, instance.getFinalModifiers());
    }

    /**
     * Test of addFinalModifier method, of class ComplexType.
     */
    @Test
    public void testAddFinalModifier() {
        SimpleContentType inputSimpleContentType = new SimpleContentType();
        ComplexType instance = new ComplexType("{}someType", inputSimpleContentType);

        instance.addFinalModifier(ComplexTypeInheritanceModifier.Extension);

        // Check if Extension is part of the HashSet
        assertTrue(instance.getFinalModifiers().contains(ComplexTypeInheritanceModifier.Extension));
    }

    /**
     * Test of setFinalModifiers method, of class ComplexType.
     */
    @Test
    public void testSetFinalModifiers() {
        SimpleContentType inputSimpleContentType = new SimpleContentType();
        ComplexType instance = new ComplexType("{}someType", inputSimpleContentType);

        HashSet<ComplexTypeInheritanceModifier> inputComplexTypeInheritanceModifierSet = new HashSet<ComplexTypeInheritanceModifier>();
        inputComplexTypeInheritanceModifierSet.add(ComplexTypeInheritanceModifier.Extension);

        instance.setFinalModifiers(inputComplexTypeInheritanceModifierSet);

        // Check if it is not the same, but an equal object
        assertNotSame(inputComplexTypeInheritanceModifierSet, instance.getFinalModifiers());
        assertEquals(inputComplexTypeInheritanceModifierSet, instance.getFinalModifiers());
    }

    /**
     * Test of setBlockModifiers method, of class ComplexType.
     */
    @Test
    public void testSetBlockModifiers() {
        SimpleContentType inputSimpleContentType = new SimpleContentType();
        ComplexType instance = new ComplexType("{}someType", inputSimpleContentType);

        HashSet<ComplexTypeInheritanceModifier> inputComplexTypeInheritanceModifierSet = new HashSet<ComplexTypeInheritanceModifier>();
        inputComplexTypeInheritanceModifierSet.add(ComplexTypeInheritanceModifier.Extension);

        instance.setBlockModifiers(inputComplexTypeInheritanceModifierSet);

        // Check if it is not the same, but an equal object
        assertNotSame(inputComplexTypeInheritanceModifierSet, instance.getBlockModifiers());
        assertEquals(inputComplexTypeInheritanceModifierSet, instance.getBlockModifiers());
    }

    /**
     * Test of setAttributes method, of class ComplexType.
     */
    @Test
    public void testSetAttributes() {
        SimpleContentType inputSimpleContentType = new SimpleContentType();
        ComplexType instance = new ComplexType("{}someType", inputSimpleContentType);

        LinkedList<AttributeParticle> inputAttributeParticleList = new LinkedList<AttributeParticle>();
        inputAttributeParticleList.add(new Attribute( "{}attribute" ));

        instance.setAttributes(inputAttributeParticleList);

        // Check if it is not the same, but an equal object
        assertNotSame(inputAttributeParticleList, instance.getAttributes());
        assertEquals(inputAttributeParticleList, instance.getAttributes());
    }

    /**
     * Test of setContent method, of class ComplexType.
     */
    @Test
    public void testSetContent() {
        SimpleContentType inputSimpleContentType = new SimpleContentType();
        ComplexType instance = new ComplexType("{}someType", inputSimpleContentType);
        instance.setContent(inputSimpleContentType);
        assertEquals(inputSimpleContentType, instance.getContent());
    }

    /**
     * Test of isAbstract method, of class ComplexType.
     */
    @Test
    public void testIsAbstract() {
        SimpleContentType inputSimpleContentType = new SimpleContentType();
        ComplexType instance = new ComplexType("{}someType", inputSimpleContentType);
        instance.setAbstract(true);

        // Check if it is not the same, but an equal object
        assertTrue(instance.isAbstract());
    }

    /**
     * Test of setAbstract method, of class ComplexType.
     */
    @Test
    public void testSetAbstract() {
        SimpleContentType inputSimpleContentType = new SimpleContentType();
        ComplexType instance = new ComplexType("{}someType", inputSimpleContentType);
        instance.setAbstract(true);

        // Check if it is not the same, but an equal object
        assertTrue(instance.isAbstract());
    }
}
