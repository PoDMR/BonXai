package eu.fox7.bonxai.relaxng;

import eu.fox7.bonxai.common.NamespaceList;
import eu.fox7.bonxai.relaxng.Element;
import eu.fox7.bonxai.relaxng.Pattern;
import eu.fox7.bonxai.relaxng.RelaxNGSchema;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class RelaxNGSchema
 * @author Lars Schmidt
 */
public class RelaxNGSchemaTest extends junit.framework.TestCase {

    /**
     * Test of getRootPattern method, of class RelaxNGSchema.
     */
    @Test
    public void testGetRootPattern() {
        RelaxNGSchema instance = new RelaxNGSchema();
        Element expResult = new Element();
        instance.setRootPattern(expResult);
        Pattern result = instance.getRootPattern();
        assertEquals(expResult, result);
    }

    /**
     * Test of setRootPattern method, of class RelaxNGSchema.
     */
    @Test
    public void testSetRootPattern() {
        RelaxNGSchema instance = new RelaxNGSchema();
        Element expResult = new Element();
        instance.setRootPattern(expResult);
        Pattern result = instance.getRootPattern();
        assertEquals(expResult, result);
    }

    /**
     * Test of getNamespaceList method, of class RelaxNGSchema.
     */
    @Test
    public void testGetNamespaceList() {
        RelaxNGSchema instance = new RelaxNGSchema();
        NamespaceList result = instance.getNamespaceList();
        assertNotNull(result);
        assertEquals(RelaxNGSchema.RELAXNG_NAMESPACE, result.getDefaultNamespace().getUri());
        assertEquals(0, result.getIdentifiedNamespaces().size());
    }

    /**
     * Test of getDefaultNamespace method, of class RelaxNGSchema.
     */
    @Test
    public void testGetDefaultNamespace() {
        RelaxNGSchema instance = new RelaxNGSchema();
        assertEquals(RelaxNGSchema.RELAXNG_NAMESPACE, instance.getDefaultNamespace());
        instance.setDefaultNamespace("test");
        String expResult = "test";
        String result = instance.getDefaultNamespace();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDefaultNamespace method, of class RelaxNGSchema.
     */
    @Test
    public void testSetDefaultNamespace() {
        RelaxNGSchema instance = new RelaxNGSchema();
        assertEquals(RelaxNGSchema.RELAXNG_NAMESPACE, instance.getDefaultNamespace());
        instance.setDefaultNamespace("test");
        String expResult = "test";
        String result = instance.getDefaultNamespace();
        assertEquals(expResult, result);
    }

    /**
     * Test of getRootNamespacePrefix method, of class RelaxNGSchema.
     */
    @Test
    public void testGetRootNamespacePrefix() {
        String rootNamespacePrefix = "temp";
        RelaxNGSchema instance = new RelaxNGSchema();
        instance.setRootNamespacePrefix(rootNamespacePrefix);
        assertEquals(rootNamespacePrefix, instance.getRootNamespacePrefix());
    }

    /**
     * Test of setRootNamespacePrefix method, of class RelaxNGSchema.
     */
    @Test
    public void testSetRootNamespacePrefix() {
        String rootNamespacePrefix = "temp";
        RelaxNGSchema instance = new RelaxNGSchema();
        instance.setRootNamespacePrefix(rootNamespacePrefix);
        assertEquals(rootNamespacePrefix, instance.getRootNamespacePrefix());
    }
}
