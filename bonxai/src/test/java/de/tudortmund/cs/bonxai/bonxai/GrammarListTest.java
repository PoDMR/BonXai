
package de.tudortmund.cs.bonxai.bonxai;

import java.util.Vector;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tudortmund.cs.bonxai.bonxai.Expression;
import de.tudortmund.cs.bonxai.bonxai.GrammarList;
import static org.junit.Assert.*;

/**
 *
 */
public class GrammarListTest extends junit.framework.TestCase {

    public GrammarListTest() {
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
     * Test of getExpressions method, of class GrammarList.
     */
    @Test
    public void testGetExpressions() {
        GrammarList instance = new GrammarList();
        Expression expression = new Expression();
        instance.addExpression(expression);
        Vector<Expression> expResult = new Vector<Expression>();
        expResult.add(expression);
        Vector<Expression> result = instance.getExpressions();
        assertEquals(expResult, result);
    }

    /**
     * Test of addExpresison method, of class GrammarList.
     */
    @Test
    public void testAddExpresison() {
        GrammarList instance = new GrammarList();
        Expression expression = new Expression();
        instance.addExpression(expression);
        Vector<Expression> expResult = new Vector<Expression>();
        expResult.add(expression);
        Vector<Expression> result = instance.getExpressions();
        assertEquals(expResult, result);
    }
}
