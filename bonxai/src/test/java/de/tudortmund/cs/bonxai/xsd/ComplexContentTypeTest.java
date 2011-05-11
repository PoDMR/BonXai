package de.tudortmund.cs.bonxai.xsd;

import de.tudortmund.cs.bonxai.common.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 *
 */
public class ComplexContentTypeTest extends junit.framework.TestCase {

    public ComplexContentTypeTest() {
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
     * Test of getInheritance method, of class ComplexContentType.
     */
    @Test
    public void testGetInheritance() {
        SymbolTableRef<Type> test = new SymbolTableRef<Type>("NameRef");
        ComplexContentExtension inputInheritanceTest = new ComplexContentExtension( test );
        ComplexContentType instance = new ComplexContentType();
        instance.setInheritance(inputInheritanceTest);
        assertEquals(inputInheritanceTest, instance.getInheritance());
    }

    /**
     * Test of setInheritance method, of class ComplexContentType.
     */
    @Test
    public void testSetInheritance() {
        SymbolTableRef<Type> test = new SymbolTableRef<Type>("NameRef");
        ComplexContentExtension inputInheritanceTest = new ComplexContentExtension( test );
        ComplexContentType instance = new ComplexContentType();
        instance.setInheritance(inputInheritanceTest);
        assertEquals(inputInheritanceTest, instance.getInheritance());
    }

    /**
     * Test of getMixed method, of class ComplexContentType.
     */
    @Test
    public void testGetMixed() {
        ComplexContentType instance = new ComplexContentType();
        instance.setMixed(true);

        // Check if it is not the same, but an equal object
        assertTrue(instance.getMixed());
    }

    /**
     * Test of setMixed method, of class ComplexContentType.
     */
    @Test
    public void testSetMixed() {
        ComplexContentType instance = new ComplexContentType();
        instance.setMixed(true);

        // Check if it is not the same, but an equal object
        assertTrue(instance.getMixed());
    }

    /**
     * Test of getParticle method, of class ComplexContentType.
     */
    @Test
    public void testGetParticle() {
        ChoicePattern testParticle = new ChoicePattern();
        ComplexContentType instance = new ComplexContentType();
        instance.setParticle(testParticle);
        assertEquals(testParticle, instance.getParticle());
    }

    /**
     * Test of setParticle method, of class ComplexContentType.
     */
    @Test
    public void testSetParticle() {
        ChoicePattern testParticle = new ChoicePattern();
        ComplexContentType instance = new ComplexContentType();
        instance.setParticle(testParticle);
        assertEquals(testParticle, instance.getParticle());
    }
}
