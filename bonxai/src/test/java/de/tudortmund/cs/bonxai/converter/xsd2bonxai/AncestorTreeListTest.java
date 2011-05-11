package de.tudortmund.cs.bonxai.converter.xsd2bonxai;

import de.tudortmund.cs.bonxai.xsd.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

public class AncestorTreeListTest extends junit.framework.TestCase {

    TypeAutomatonState state;
    AncestorTreeNode root;
    AncestorTreeNode alpha;
    AncestorTreeTransition startTransition;
    AncestorTreeTransition alphaTransition;
    AncestorTreeList instance;
    AncestorTree tree;

    @Before
    @Override
    public void setUp() {
        SimpleContentType content = new SimpleContentType();
        ComplexType type = new ComplexType("{http://example.com/mynamespace}complexType", content);
        state = new TypeAutomatonState(type);
        root = new AncestorTreeNode(state);
        alpha = new AncestorTreeNode(state);
        startTransition = new AncestorTreeTransition(null, root, "{http://example.com/mynamespace}start");
        alphaTransition = new AncestorTreeTransition(root, alpha, "{http://example.com/mynamespace}alpha");
        root.addOut(alphaTransition);
        alpha.setIn(alphaTransition);
        instance = new AncestorTreeList();
        tree = new AncestorTree(root, startTransition, instance);
        instance.getTreeList().add(tree);
    }

    /**
     * Test of getTreeList method, of class AncestorTreeList.
     */
    @Test
    public void testGetTreeList() {
        LinkedList<AncestorTree> expResult = new LinkedList<AncestorTree>();
        expResult.add(tree);
        LinkedList<AncestorTree> result = instance.getTreeList();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCurrentMarks method and setCurrentMark method, of class AncestorTreeList.
     */
    @Test
    public void testGetCurrentMarksAndSetCurrentMark() {
        instance.setCurrentMark("{http://example.com/mynamespace}alpha");
        HashMap<String, Boolean> expResult = new HashMap<String, Boolean>();
        expResult.put("{http://example.com/mynamespace}alpha", false);
        expResult.put("{http://example.com/mynamespace}start", false);
        HashMap<String, Boolean> result = instance.getCurrentMarks();
        assertEquals(expResult, result);
        instance.setCurrentMark("{http://example.com/mynamespace}alpha");
        expResult.put("{http://example.com/mynamespace}alpha", true);
        result = instance.getCurrentMarks();
        assertEquals(expResult, result);
    }

    /**
     * Test of getNextMarks method and setNextMark method, of class AncestorTreeList.
     */
    @Test
    public void testGetNextMarksAndSetNextMark() {
        instance.setNextMark("{http://example.com/mynamespace}alpha");
        HashMap<String, Boolean> expResult = new HashMap<String, Boolean>();
        expResult.put("{http://example.com/mynamespace}alpha", false);
        HashMap<String, Boolean> result = instance.getNextMarks();
        assertEquals(expResult, result);
        instance.setNextMark("{http://example.com/mynamespace}alpha");
        expResult.put("{http://example.com/mynamespace}alpha", true);
        result = instance.getNextMarks();
        assertEquals(expResult, result);
    }

    /**
     * Test of swapMarks method, of class AncestorTreeList.
     */
    @Test
    public void testSwapMarks() {
        assertNotNull(instance.getCurrentMarks());
        assertNotNull(instance.getNextMarks());
        instance.setCurrentMark("{http://example.com/mynamespace}start");
        instance.setNextMark("{http://example.com/mynamespace}alpha");
        instance.swapMarks();
        HashMap<String, Boolean> expResult = new HashMap<String, Boolean>();
        expResult.put("{http://example.com/mynamespace}alpha", false);
        HashMap<String, Boolean> result = instance.getCurrentMarks();
        assertEquals(expResult, result);
        assertTrue(instance.getNextMarks().isEmpty());
    }

    /**
     * Test of setAllNextMarkToFalse method, of class AncestorTreeList.
     */
    @Test
    public void testSetAllNextMarkToFalse() {
        instance.setNextMark("{http://example.com/mynamespace}alpha");
        instance.setNextMark("{http://example.com/mynamespace}alpha");
        HashMap<String, Boolean> expResult = new HashMap<String, Boolean>();
        expResult.put("{http://example.com/mynamespace}alpha", false);
        instance.setAllNextMarkToFalse();
        HashMap<String, Boolean> result = instance.getNextMarks();
        assertEquals(expResult, result);
    }
}