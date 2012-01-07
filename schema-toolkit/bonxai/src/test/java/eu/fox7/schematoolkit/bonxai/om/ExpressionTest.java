
package eu.fox7.schematoolkit.bonxai.om;

import java.util.Vector;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class ExpressionTest extends junit.framework.TestCase {

    public ExpressionTest() {
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
     * Test of getAncestorPattern method, of class Expression.
     */
    @Test
    public void testGetAncestorPattern() {
        DoubleSlashPrefixElement doubleSlashPrefixElement = new DoubleSlashPrefixElement("namespace", "name");
        AncestorPattern expResult = new AncestorPattern(doubleSlashPrefixElement);
        Expression instance = new Expression();
        instance.setAncestorPattern(expResult);
        AncestorPattern result = instance.getAncestorPattern();
        assertEquals(expResult, result);
    }

    /**
     * Test of setAncestorPattern method, of class Expression.
     */
    @Test
    public void testSetAncestorPattern() {
        DoubleSlashPrefixElement doubleSlashPrefixElement = new DoubleSlashPrefixElement("namespace", "name");
        AncestorPattern expResult = new AncestorPattern(doubleSlashPrefixElement);
        Expression instance = new Expression();
        instance.setAncestorPattern(expResult);
        AncestorPattern result = instance.getAncestorPattern();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAnnotations method, of class Expression.
     */
    @Test
    public void testGetAnnotations() {
        Expression instance = new Expression();
        Annotation annotation = new Annotation("key", "value");
        instance.addAnnotation(annotation);
        Vector<Annotation> expResult = new Vector<Annotation>();
        expResult.add(annotation);
        Vector<Annotation> result = instance.getAnnotations();
        assertEquals(expResult, result);
    }

    /**
     * Test of addAnnotation method, of class Expression.
     */
    @Test
    public void testAddAnnotation() {
        Expression instance = new Expression();
        Annotation annotation = new Annotation("key", "value");
        instance.addAnnotation(annotation);
        Vector<Annotation> expResult = new Vector<Annotation>();
        expResult.add(annotation);
        Vector<Annotation> result = instance.getAnnotations();
        assertEquals(expResult, result);
    }

    /**
     * Test of getChildPattern method, of class Expression.
     */
    @Test
    public void testGetChildPattern() {
        ChildPattern expResult = new ChildPattern(new AttributePattern(), new ElementPattern(new BonxaiType("", "type")));
        Expression instance = new Expression();
        instance.setChildPattern(expResult);
        ChildPattern result = instance.getChildPattern();
        assertEquals(expResult, result);
    }

    /**
     * Test of setChildPattern method, of class Expression.
     */
    @Test
    public void testSetChildPattern() {
        ChildPattern expResult = new ChildPattern(new AttributePattern(), new ElementPattern(new BonxaiType("", "type")));
        Expression instance = new Expression();
        instance.setChildPattern(expResult);
        ChildPattern result = instance.getChildPattern();
        assertEquals(expResult, result);
    }
}
