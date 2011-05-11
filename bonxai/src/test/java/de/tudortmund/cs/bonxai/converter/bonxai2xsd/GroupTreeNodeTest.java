package de.tudortmund.cs.bonxai.converter.bonxai2xsd;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.bonxai.*;
import java.util.LinkedHashSet;
import java.util.Vector;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * GroupTreeNodeTest
 */
public class GroupTreeNodeTest extends junit.framework.TestCase {

    public GroupTreeNodeTest() {
    }

    /**
     * Test of generateId method, of class SchemaTreeNode.
     */
    @Test
    public void testGenerateId() {
        GroupTreeNode group = new GroupTreeNode("group", new Expression(), null);
        assertEquals("[group]()", group.generateId());
    }

    /**
     * Test of getParticle method, of class GroupTreeNode.
     */
    @Test
    public void testGetParticle() {
        SequencePattern regexp = new SequencePattern();
        ElementPattern elementPattern = new ElementPattern(regexp);
        ChildPattern childPattern = new ChildPattern(null, elementPattern);
        Expression expression = new Expression();
        expression.setChildPattern(childPattern);

        GroupTreeNode instance = new GroupTreeNode("group", expression, null);

        assertEquals(regexp, instance.getParticle());
    }

    /**
     * Test of getElement method, of class GroupTreeNode.
     */
    @Test
    public void testGetExpression() {

        Expression expression = new Expression();
        GroupTreeNode instance = new GroupTreeNode("group", expression, null);

        assertEquals(expression, instance.getExpression());
    }

    /**
     * Test of addChild method, of class GroupTreeNode.
     */
    @Test
    public void testAddChild() {
        SequencePattern regexp = new SequencePattern();
        ElementPattern elementPattern = new ElementPattern(regexp);
        ChildPattern childPattern = new ChildPattern(null, elementPattern);
        Expression expression = new Expression();
        expression.setChildPattern(childPattern);
        GroupTreeNode instance = new GroupTreeNode("group", expression, null);

        SequencePattern regexp2 = new SequencePattern();
        ElementPattern elementPattern2 = new ElementPattern(regexp2);
        ChildPattern childPattern2 = new ChildPattern(null, elementPattern2);
        Expression expression2 = new Expression();
        expression.setChildPattern(childPattern2);
        GroupTreeNode instance_child = new GroupTreeNode("group", expression2, null);

        instance.addChild(instance_child);

        LinkedHashSet<GroupTreeNode> expHashSet = new LinkedHashSet<GroupTreeNode>();
        expHashSet.add(instance_child);
        assertEquals(expHashSet, instance.getChilds());
    }

    /**
     * Test of getAncestorPath method, of class GroupTreeNode.
     */
    @Test
    public void testGetAncestorPath() {
        // Setup
        de.tudortmund.cs.bonxai.bonxai.Expression expression = new de.tudortmund.cs.bonxai.bonxai.Expression();
        de.tudortmund.cs.bonxai.bonxai.Expression expression1 = new de.tudortmund.cs.bonxai.bonxai.Expression();

        SchemaTreeNode schemaTreeNode = new SchemaTreeNode();

        ElementTreeNode instance1 = new ElementTreeNode("http://example.com/ns", "test", expression);

        SequencePattern regexp = new SequencePattern();
        ElementPattern elementPattern = new ElementPattern(regexp);
        ChildPattern childPattern = new ChildPattern(null, elementPattern);
        Expression expression_group = new Expression();
        expression_group.setChildPattern(childPattern);
        GroupTreeNode groupTreeNode = new GroupTreeNode("group", expression_group, null);

        ElementTreeNode instance2 = new ElementTreeNode("http://example.com/ns", "test2", expression1);

        schemaTreeNode.addChild(instance1);
        instance1.addChild(groupTreeNode);
        groupTreeNode.addChild(instance2);

        // Assertions
        Vector<TreeNode> expResult = new Vector<TreeNode>();
        expResult.add(schemaTreeNode);
        expResult.add(instance1);
        expResult.add(instance2);

        /* All the TreeNodes defined above have to be in the AncestorPath of
         * instance2
         */
        //System.out.println(instance2.getAncestorPath());
        assertTrue(instance2.getAncestorPath().containsAll(expResult));

        /* The AncestorPath Vector have to be the same like the Vector expResult
         */
        assertTrue(instance2.getAncestorPath().equals(expResult));

        /* The Elements in the AncestorPath Vector have to be sorted in the
         * same way like in the given order by adding them as parents to
         * the objects
         */
        assertEquals(instance2.getAncestorPath().indexOf(schemaTreeNode), 0);
        assertEquals(instance2.getAncestorPath().indexOf(instance1), 1);
    }
}
