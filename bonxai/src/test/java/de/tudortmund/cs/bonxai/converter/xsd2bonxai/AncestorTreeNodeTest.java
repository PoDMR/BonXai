package de.tudortmund.cs.bonxai.converter.xsd2bonxai;

import de.tudortmund.cs.bonxai.xsd.*;
import java.util.LinkedList;
import org.junit.*;
import static org.junit.Assert.*;

public class AncestorTreeNodeTest extends junit.framework.TestCase {

    TypeAutomatonState state;
    AncestorTreeNode instance;
    AncestorTreeNode secondInstance;

    @Before
    @Override
    public void setUp() {
        SimpleContentType content = new SimpleContentType();
        ComplexType type = new ComplexType("{http://example.com/mynamespace}complexType", content);
        state = new TypeAutomatonState(type);
        instance = new AncestorTreeNode(state);
        secondInstance = new AncestorTreeNode(state);
    }

    /**
     * Test of getState method, of class AncestorTreeNode.
     */
    @Test
    public void testGetState() {
        TypeAutomatonState expResult = state;
        TypeAutomatonState result = instance.getState();
        assertEquals(expResult, result);
    }

    /**
     * Test of setIn method and getIn method, of class AncestorTreeNode.
     */
    @Test
    public void testSetInAndGetIn() {
        AncestorTreeTransition in = new AncestorTreeTransition(secondInstance, instance, "{http://example.com/mynamespace}elementName");
        instance.setIn(in);
        AncestorTreeTransition expResult = in;
        AncestorTreeTransition result = instance.getIn();
        assertEquals(expResult, result);
    }

    /**
     * Test of addOut method and getOut method, of class AncestorTreeNode.
     */
    @Test
    public void testAddOutAndGetOut() {
        AncestorTreeTransition outTransition = new AncestorTreeTransition(instance, secondInstance, "{http://example.com/mynamespace}elementName");
        instance.addOut(outTransition);
        LinkedList<AncestorTreeTransition> expResult = new LinkedList<AncestorTreeTransition>();
        expResult.add(outTransition);
        LinkedList<AncestorTreeTransition> result = instance.getOut();
        assertEquals(expResult, result);
    }

    /**
     * Test of addRecursionIn method and getRecursionIn method, of class AncestorTreeNode.
     */
    @Test
    public void testAddRecursionInAndGetRecursionIn() {
        AncestorTreeTransition recTransition = new AncestorTreeTransition(instance, secondInstance, "{http://example.com/mynamespace}elementName");
        instance.addRecursionIn(recTransition);
        LinkedList<AncestorTreeTransition> expResult = new LinkedList<AncestorTreeTransition>();
        expResult.add(recTransition);
        LinkedList<AncestorTreeTransition> result = instance.getRecursionIn();
        assertEquals(expResult, result);
    }

    /**
     * Test of setIsAtRoot method and getIsAtRoot method, of class AncestorTreeNode.
     */
    @Test
    public void testSetIsAtRootAndGetIsAtRoot() {
        instance.setIsAtRoot(true);
        Boolean expResult = true;
        Boolean result = instance.getIsAtRoot();
        assertEquals(expResult, result);
    }
}