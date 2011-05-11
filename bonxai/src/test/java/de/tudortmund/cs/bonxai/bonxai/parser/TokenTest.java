/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.tudortmund.cs.bonxai.bonxai.parser;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Gökhan
 */
public class TokenTest {

    public TokenTest() {
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
     * Test of getValue method, of class Token. For Objects of this class it
     * should return null.
     */
    @Test
    public void testGetValue() {
        Token instance = new Token();
        Object expResult = null;
        Object result = instance.getValue();
        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class Token. It should return an String,
     * when it is set. Elsewhere null.
     */
    @Test
    public void testToString() {
        Token instance1 = new Token(5,"image1");
        Token instance2 = new Token();
        String expResult1 = "image1";
        String expResult2 = null;
        String result1 = instance1.toString();
        String result2 = instance2.toString();
        assertEquals(expResult1, result1);
        assertEquals(expResult2, result2);
    }

    /**
     * Test of newToken method, of class Token. This methode returns a
     * new object with two parameter.
     */
    @Test
    public void testNewToken_int_String() {
        int ofKind = 0;
        String image = "";
        Token expResult = new Token(0,"");
        Token result = Token.newToken(ofKind, image);
        expResult.equals(result);
    }

    /**
     * Test of newToken method, of class Token. This methode returns a
     * new object with one parameter.
     */
    @Test
    public void testNewToken_int() {
        int ofKind = 0;
        Token expResult = new Token(0);
        Token result = Token.newToken(ofKind);
        expResult.equals(result);
    }

}