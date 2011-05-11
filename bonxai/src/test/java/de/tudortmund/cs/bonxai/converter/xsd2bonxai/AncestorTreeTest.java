package de.tudortmund.cs.bonxai.converter.xsd2bonxai;

import de.tudortmund.cs.bonxai.bonxai.*;
import de.tudortmund.cs.bonxai.xsd.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

public class AncestorTreeTest extends junit.framework.TestCase {

    TypeAutomatonState state;
    AncestorTreeNode root;
    AncestorTreeNode alpha;
    AncestorTreeTransition startTransition;
    AncestorTreeTransition alphaTransition;
    AncestorTreeList treeList;
    AncestorTree instance;
    String namespace = "http://example.com/mynamespace";

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
        treeList = new AncestorTreeList();
        treeList.getTreeList().add(instance);
        instance = new AncestorTree(root, startTransition, treeList);
    }

    /**
     * Test of getRoot method, of class AncestorTree.
     */
    @Test
    public void testGetRoot() {
        AncestorTreeNode expResult = root;
        AncestorTreeNode result = instance.getRoot();
        assertEquals(expResult, result);
    }

    /**
     * Test of addLeaf method and getLeafs method, of class AncestorTree.
     */
    @Test
    public void testAddLeafAndGetLeafs() {
        instance.addLeaf(root);
        LinkedList<AncestorTreeNode> expResult = new LinkedList<AncestorTreeNode>();
        expResult.add(root);
        LinkedList<AncestorTreeNode> result = instance.getLeafs();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCurrentLayer method, of class AncestorTree.
     */
    @Test
    public void testGetCurrentLayer() {
        LinkedList<AncestorTreeNode> expResult = new LinkedList<AncestorTreeNode>();
        expResult.add(root);
        LinkedList<AncestorTreeNode> result = instance.getCurrentLayer();
        assertEquals(expResult, result);
    }

    /**
     * Test of getNextLayer method, of class AncestorTree.
     */
    @Test
    public void testGetNextLayer() {
        assertNotNull(instance.getNextLayer());
        instance.getNextLayer().add(alpha);
        LinkedList<AncestorTreeNode> expResult = new LinkedList<AncestorTreeNode>();
        expResult.add(alpha);
        LinkedList<AncestorTreeNode> result = instance.getNextLayer();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCurrentTransitionLayer method, of class AncestorTree.
     */
    @Test
    public void testGetCurrentTransitionLayer() {
        LinkedList<AncestorTreeTransition> expResult = new LinkedList<AncestorTreeTransition>();
        expResult.add(startTransition);
        LinkedList<AncestorTreeTransition> result = instance.getCurrentTransitionLayer();
        assertEquals(expResult, result);
    }

    /**
     * Test of getNextTransitionLayer method, of class AncestorTree.
     */
    @Test
    public void testGetNextTransitionLayer() {
        assertNotNull(instance.getNextTransitionLayer());
        instance.getNextTransitionLayer().add(alphaTransition);
        LinkedList<AncestorTreeTransition> expResult = new LinkedList<AncestorTreeTransition>();
        expResult.add(alphaTransition);
        LinkedList<AncestorTreeTransition> result = instance.getNextTransitionLayer();
        assertEquals(expResult, result);
    }

    /**
     * Test of swapLayers method, of class AncestorTree.
     */
    @Test
    public void testSwapLayers() {
        instance.getNextTransitionLayer().add(alphaTransition);
        instance.swapLayers();
        LinkedList<AncestorTreeTransition> expResult = new LinkedList<AncestorTreeTransition>();
        expResult.add(alphaTransition);
        LinkedList<AncestorTreeTransition> result = instance.getCurrentTransitionLayer();
        assertEquals(expResult, result);
        assertTrue(instance.getNextTransitionLayer().isEmpty());
    }

    /**
     * Test of buildAncestorVector method, of class AncestorTree.
     */
    @Test
    public void testBuildAncestorVector() {
        Vector<AncestorPatternParticle> expResult = new Vector<AncestorPatternParticle>();
        Vector<AncestorPatternParticle> sequence = new Vector<AncestorPatternParticle>();
        DoubleSlashPrefixElement doubleSlashAlpha = new DoubleSlashPrefixElement(namespace, "alpha");
        SingleSlashPrefixElement singleSlashStart = new SingleSlashPrefixElement(namespace, "start");
        sequence.add(doubleSlashAlpha);
        sequence.add(singleSlashStart);
        expResult.add(new SequenceExpression(sequence));
        instance.addLeaf(alpha);
        Vector<AncestorPatternParticle> result = instance.buildAncestorVector();
        for (int i = 0; i < expResult.size(); i++) {
            assertEquals(expResult.get(i).toString(), result.get(i).toString());
        }
    }

    /**
     * Test of buildSequenceVector method, of class AncestorTree.
     */
    @Test
    public void testBuildSequenceVector() {
        Vector<AncestorPatternParticle> expResult = new Vector<AncestorPatternParticle>();
        DoubleSlashPrefixElement doubleSlashAlpha = new DoubleSlashPrefixElement(namespace, "alpha");
        SingleSlashPrefixElement singleSlashStart = new SingleSlashPrefixElement(namespace, "start");
        expResult.add(doubleSlashAlpha);
        expResult.add(singleSlashStart);
        Vector<AncestorPatternParticle> result = instance.buildSequenceVector(alpha, null);
        for (int i = 0; i < expResult.size(); i++) {
            assertEquals(expResult.get(i).toString(), result.get(i).toString());
        }
    }

    /**
     * Test of seenState method, of class AncestorTree.
     */
    @Test
    public void testSeenState() {
        AncestorTreeNode expResult = alpha;
        AncestorTreeNode result = instance.seenState(alpha, state);
        assertEquals(expResult, result);
    }
}