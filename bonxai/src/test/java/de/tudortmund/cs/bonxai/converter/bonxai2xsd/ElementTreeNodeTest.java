package de.tudortmund.cs.bonxai.converter.bonxai2xsd;

import de.tudortmund.cs.bonxai.common.Element;
import de.tudortmund.cs.bonxai.common.SequencePattern;
import de.tudortmund.cs.bonxai.bonxai.ChildPattern;
import de.tudortmund.cs.bonxai.bonxai.ElementPattern;
import de.tudortmund.cs.bonxai.bonxai.Expression;
import java.util.LinkedHashSet;
import java.util.Vector;
import org.junit.Test;

/**
 *
 */
public class ElementTreeNodeTest extends junit.framework.TestCase {

    public ElementTreeNodeTest() {
    }

    /**
     * Test of generateId method, of class ElementTreeNode.
     */
    @Test
    public void testGenerateId() {
        ElementTreeNode element = new ElementTreeNode("http://example.com/ns", "foo", new de.tudortmund.cs.bonxai.bonxai.Expression());
        assertEquals("{http://example.com/ns}foo()", element.generateId());
    }

    /**
     * Test of addChild method, of class ElementTreeNode.
     */
    @Test
    public void testAddChild() {
        de.tudortmund.cs.bonxai.bonxai.Expression expression1 = new de.tudortmund.cs.bonxai.bonxai.Expression();
        de.tudortmund.cs.bonxai.bonxai.Expression expression2 = new de.tudortmund.cs.bonxai.bonxai.Expression();
        ElementTreeNode root = new ElementTreeNode("http://example.com/ns", "root", expression1);
        ElementTreeNode instance = new ElementTreeNode("http://example.com/ns", "instance", expression2);

        // Instance with same Expression like its parent
        ElementTreeNode instance_same_expression = new ElementTreeNode("http://example.com/ns", "root", expression1);

        root.addChild(instance);
        LinkedHashSet<ElementTreeNode> expHashSet = new LinkedHashSet<ElementTreeNode>();
        expHashSet.add(instance);
        assertTrue(root.getChilds().containsAll(expHashSet));

        // Instance with same Expression like its parent is not allowed to be put into the child list!
        instance.addChild(instance_same_expression);
        assertFalse(instance.getChilds().contains(instance_same_expression));
    }

    /**
     * Test of addChild method, of class ElementTreeNode.
     */
    @Test
    public void testAddChildDirectSuccessor() {
        de.tudortmund.cs.bonxai.bonxai.Expression expression1 = new de.tudortmund.cs.bonxai.bonxai.Expression();
        de.tudortmund.cs.bonxai.bonxai.Expression expression2 = new de.tudortmund.cs.bonxai.bonxai.Expression();
        ElementTreeNode root = new ElementTreeNode("http://example.com/ns", "root", expression2);
        ElementTreeNode instance = new ElementTreeNode("http://example.com/ns", "instance", expression1);

        // Instance with same Expression like its parent
        ElementTreeNode instance_same_expression = new ElementTreeNode("http://example.com/ns", "instance", expression1);

        root.addChild(instance);
        LinkedHashSet<ElementTreeNode> expHashSet = new LinkedHashSet<ElementTreeNode>();
        expHashSet.add(instance);
        assertTrue(root.getChilds().containsAll(expHashSet));

        // Instance with same Expression like its parent is not allowed to be put into the child list!
        instance.addChild(instance_same_expression);
        assertFalse(instance.getChilds().contains(instance_same_expression));
    }

    /**
     * Test of getAncestorPath method, of class ElementTreeNode.
     */
    @Test
    public void testGetAncestorPath() {
        // Setup
        de.tudortmund.cs.bonxai.bonxai.Expression expression1 = new de.tudortmund.cs.bonxai.bonxai.Expression();
        de.tudortmund.cs.bonxai.bonxai.Expression expression2 = new de.tudortmund.cs.bonxai.bonxai.Expression();
        de.tudortmund.cs.bonxai.bonxai.Expression expression3 = new de.tudortmund.cs.bonxai.bonxai.Expression();
        de.tudortmund.cs.bonxai.bonxai.Expression expression4 = new de.tudortmund.cs.bonxai.bonxai.Expression();
        SchemaTreeNode schemaTreeNode = new SchemaTreeNode();
        ElementTreeNode instance1 = new ElementTreeNode("http://example.com/ns", "test", expression1);
        ElementTreeNode instance2 = new ElementTreeNode("http://example.com/ns", "test2", expression2);
        ElementTreeNode instance3 = new ElementTreeNode("http://example.com/ns", "test2", expression3);
        ElementTreeNode instance4 = new ElementTreeNode("http://example.com/ns", "test2", expression4);

        SequencePattern regexp = new SequencePattern();
        ElementPattern elementPattern = new ElementPattern(regexp);
        ChildPattern childPattern = new ChildPattern(null, elementPattern);
        Expression expression_group = new Expression();
        expression_group.setChildPattern(childPattern);
        GroupTreeNode groupTreeNode = new GroupTreeNode("group", expression_group, null);

        // Vector of TreeNodes to check the ancestor path
        Vector<TreeNode> expResult = new Vector<TreeNode>();

        // Vector expansion
        expResult.add(schemaTreeNode);
        // Add Child to the rootnode
        schemaTreeNode.addChild(instance1);
        // Assertion
        assertTrue(instance1.getAncestorPath().containsAll(expResult));
        // Debug
        //System.out.println(instance1.getAncestorPath());

        // Vector expansion
        expResult.add(instance1);
        // Add Child
        instance1.addChild(instance2);
        // Assertion
        assertTrue(instance2.getAncestorPath().containsAll(expResult));
        // Debug
        // System.out.println(instance2.getAncestorPath());

        // Vector expansion
        expResult.add(instance2);
        // Add Child
        instance2.addChild(groupTreeNode);
        // Assertion
        assertTrue(groupTreeNode.getAncestorPath().containsAll(expResult));
        // Debug
        //System.out.println(groupTreeNode.getAncestorPath());

        // Vector expansion

        /* ###### IMPOTANT: THE GROUP IS NOT ALLOWED TO BE PUT IN THE
         * ANCESTOR PATH HERE ! ######
         *
         * So the remaining vector is the same as before.
         */

        // Add Child
        groupTreeNode.addChild(instance3);
        // Assertion
        assertTrue(instance3.getAncestorPath().containsAll(expResult));
        //System.out.println(instance3.getAncestorPath());

        // Vector expansion
        expResult.add(instance3);
        // Add Child
        instance3.addChild(instance4);
        // Assertion
        assertTrue(instance4.getAncestorPath().containsAll(expResult));
    // Debug
    //System.out.println(instance4.getAncestorPath());
    }

    /**
     * Test of getParticle method, of class ElementTreeNode.
     */
    @Test
    public void testGetParticle() {
        de.tudortmund.cs.bonxai.bonxai.Expression expression = new de.tudortmund.cs.bonxai.bonxai.Expression();
        Element regexp = new de.tudortmund.cs.bonxai.xsd.Element("{}test");
        ElementPattern elementPattern = new ElementPattern(regexp);
        ChildPattern childpattern = new ChildPattern(null, elementPattern);
        expression.setChildPattern(childpattern);
        ElementTreeNode instance = new ElementTreeNode("http://example.com/ns", "test", expression);

        assertEquals(regexp, instance.getParticle());
    }

    /**
     * Test of replaceChild method, of class ElementTreeNode.
     */
    @Test
    public void testReplaceChild() {
        de.tudortmund.cs.bonxai.bonxai.Expression expression1 = new de.tudortmund.cs.bonxai.bonxai.Expression();
        de.tudortmund.cs.bonxai.bonxai.Expression expression2 = new de.tudortmund.cs.bonxai.bonxai.Expression();

        ElementTreeNode root = new ElementTreeNode("http://example.com/ns", "root", expression1);
        ElementTreeNode instance = new ElementTreeNode("http://example.com/ns", "instance", expression2);
        ElementTreeNode instance2 = new ElementTreeNode("http://example.com/ns", "instance", expression2);

        ElementTreeNode substitutionNode = new ElementTreeNode("http://example.com/ns", "substitution", new de.tudortmund.cs.bonxai.bonxai.Expression());
        root.addChild(instance);
        root.addChild(instance2);

        root.replaceChild(instance, substitutionNode);

        assertFalse(root.getChilds().contains(instance));
        assertTrue(root.getChilds().contains(substitutionNode));
        assertEquals(root.getChildCount(), 2);
    }
}
