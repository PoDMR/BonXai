package eu.fox7.schematoolkit.xsd.om;

import eu.fox7.schematoolkit.common.SymbolTableRef;
import eu.fox7.schematoolkit.xsd.om.SimpleContentExtension;
import eu.fox7.schematoolkit.xsd.om.SimpleContentInheritance;
import eu.fox7.schematoolkit.xsd.om.SimpleContentType;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.Types;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 */
public class SimpleContentTypeTest extends junit.framework.TestCase {

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
     * Test method for {@link Types.SimpleContentType#SimpleContentType()}.
     */
    @Test
    public final void testSimpleContentType() {
        SimpleContentType instance = new SimpleContentType();
        assertNotNull(instance);
    }

    /**
     * Test method for {@link Types.SimpleContentType#getInheritance()}.
     */
    @Test
    public final void testGetInheritance() {
        SymbolTableRef<Type> test = new SymbolTableRef<Type>("NameRef");
        SimpleContentExtension inputInheritanceTest = new SimpleContentExtension(test);
        SimpleContentType instance = new SimpleContentType();
        instance.setInheritance(inputInheritanceTest);
        assertEquals(inputInheritanceTest, instance.getInheritance());
    }

    /**
     * Test method for {@link Types.SimpleContentType#setInheritance(SimpleContentInheritance)}.
     */
    @Test
    public final void testSetInheritance() {
        SymbolTableRef<Type> test = new SymbolTableRef<Type>("NameRef");
        SimpleContentExtension inputInheritanceTest = new SimpleContentExtension(test);
        SimpleContentType instance = new SimpleContentType();
        instance.setInheritance(inputInheritanceTest);
        assertEquals(inputInheritanceTest, instance.getInheritance());
    }
}
