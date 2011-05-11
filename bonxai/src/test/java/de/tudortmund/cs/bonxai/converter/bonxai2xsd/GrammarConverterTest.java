package de.tudortmund.cs.bonxai.converter.bonxai2xsd;

import org.junit.Test;
import java.util.Vector;
import java.util.HashMap;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.bonxai.*;
import de.tudortmund.cs.bonxai.xsd.*;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 */
public class GrammarConverterTest extends junit.framework.TestCase {

    @Test
    public void testReduceElementsInTree() {
        HashMap<String, XSDSchema> schemas = new HashMap<String, XSDSchema>();
        GrammarConverter converter = new GrammarConverter(schemas);

        SchemaTreeNode root = new SchemaTreeNode();
        ElementTreeNode element1 = new ElementTreeNode("http://example.com/ns", "foo", new de.tudortmund.cs.bonxai.bonxai.Expression());
        ElementTreeNode element2 = new ElementTreeNode("http://example.com/ns", "bar", new de.tudortmund.cs.bonxai.bonxai.Expression());
        ElementTreeNode elementWithSameIdAsElement2 = new ElementTreeNode("http://example.com/ns", "bar", new de.tudortmund.cs.bonxai.bonxai.Expression());

        element1.addChild(element2);
        root.addChild(element1);
        root.addChild(elementWithSameIdAsElement2);

        // Test for direct Childs of the root node before reducing the tree
        assertTrue(root.getChilds().contains(element1));
        assertTrue(root.getChilds().contains(elementWithSameIdAsElement2));

        converter.reduceElementsInTree(root);

        // It is difficult to write assertions for this case:
        // Test for direct Childs of the root node after reducing the tree
        assertTrue(root.getChilds().contains(element1));

        // elementWithSameIdAsElement2 has to be replaced with
        // the original element2
        assertTrue(root.getChilds().contains(element2));

    }

    @Test
    public void testReduceElementsInTreeMoreComplex() {
        HashMap<String, XSDSchema> schemas = new HashMap<String, XSDSchema>();
        GrammarConverter converter = new GrammarConverter(schemas);

        SchemaTreeNode root = new SchemaTreeNode();
        ElementTreeNode element1 = new ElementTreeNode("http://example.com/ns", "foo", new de.tudortmund.cs.bonxai.bonxai.Expression());
        ElementTreeNode element2 = new ElementTreeNode("http://example.com/ns", "bar", new de.tudortmund.cs.bonxai.bonxai.Expression());
        ElementTreeNode elementWithSameIdAsElement2 = new ElementTreeNode("http://example.com/ns", "bar", new de.tudortmund.cs.bonxai.bonxai.Expression());

        GroupTreeNode group1 = new GroupTreeNode("group1", new de.tudortmund.cs.bonxai.bonxai.Expression(), null);
        GroupTreeNode group2 = new GroupTreeNode("group2", new de.tudortmund.cs.bonxai.bonxai.Expression(), null);

        element1.addChild(element2);
        root.addChild(element1);
        root.addChild(elementWithSameIdAsElement2);

        group1.addChild(element1);
        group1.addChild(element2);

        group2.addChild(element1);
        group2.addChild(element2);

        root.addChild(group1);
        root.addChild(group2);

        // Test for direct Childs of the root node before reducing the tree
        assertTrue(root.getChilds().contains(element1));
        assertTrue(root.getChilds().contains(elementWithSameIdAsElement2));

        converter.reduceElementsInTree(root);

        // It is difficult to write assertions for this case:
        // Test for direct Childs of the root node after reducing the tree
        assertTrue(root.getChilds().contains(element1));

        // elementWithSameIdAsElement2 has to be replaced with
        // the original element2
        assertTrue(root.getChilds().contains(element2));
    }

    private Bonxai getBasicSchema() {
        Bonxai schema = new Bonxai();
        schema.setDeclaration(new Declaration(
                new ImportList(),
                new DataTypeList(),
                new NamespaceList(new DefaultNamespace("http://example.com/ns"))));
        return schema;
    }

    public Bonxai createSchemaLikeInArchitectureDocument() {
        // Bonxai Schema
        Bonxai bonxai = this.getBasicSchema();

        // GrammarList in Bonxai Schema
        GrammarList grammar = new GrammarList();
        bonxai.setGrammarList(grammar);

        // Grouplist
        de.tudortmund.cs.bonxai.common.SequencePattern sequenceForGroup_a = new SequencePattern();
        de.tudortmund.cs.bonxai.bonxai.Element a_a = new de.tudortmund.cs.bonxai.bonxai.Element("http://example.com", "a_a");
        de.tudortmund.cs.bonxai.bonxai.Element a_b = new de.tudortmund.cs.bonxai.bonxai.Element("http://example.com", "a_b");
        sequenceForGroup_a.addParticle(a_a);
        sequenceForGroup_a.addParticle(a_b);

        de.tudortmund.cs.bonxai.common.SequencePattern sequenceForGroup_b = new SequencePattern();
        de.tudortmund.cs.bonxai.bonxai.Element b_a = new de.tudortmund.cs.bonxai.bonxai.Element("http://example.com", "b_a");
        de.tudortmund.cs.bonxai.bonxai.Element b_b = new de.tudortmund.cs.bonxai.bonxai.Element("http://example.com", "b_b");
        sequenceForGroup_b.addParticle(b_a);
        sequenceForGroup_b.addParticle(b_b);

        // GroupElements
        ElementGroupElement group_a = new ElementGroupElement("a", sequenceForGroup_a);
        ElementGroupElement group_b = new ElementGroupElement("b", sequenceForGroup_b);

        // GroupList in Bonxai
        GroupList grouplist = new GroupList();
        bonxai.setGroupList(grouplist);

        // GroupElements for GroupList
        grouplist.addGroupElement(group_a);
        bonxai.getGroupSymbolTable().updateOrCreateReference("a", group_a);
        grouplist.addGroupElement(group_b);
        bonxai.getGroupSymbolTable().updateOrCreateReference("b", group_b);

        // Grammar

        // Expression1
        Expression expression1 = new Expression();
        grammar.addExpression(expression1);

        // AncestorPattern for expression1
        DoubleSlashPrefixElement root = new DoubleSlashPrefixElement("http://example.com/ns1", "root");
        expression1.setAncestorPattern(new AncestorPattern(root));

        // ChildPattern for expression1
        de.tudortmund.cs.bonxai.common.GroupRef groupRefTo_a1 = new GroupRef(bonxai.getGroupSymbolTable().getReference("a"));
        de.tudortmund.cs.bonxai.common.GroupRef groupRefTo_b1 = new GroupRef(bonxai.getGroupSymbolTable().getReference("b"));

        de.tudortmund.cs.bonxai.common.SequencePattern groupRootSequence1 = new SequencePattern();
        groupRootSequence1.addParticle(groupRefTo_a1);
        groupRootSequence1.addParticle(groupRefTo_b1);

        ChildPattern rootChildPattern1 = new ChildPattern(null, new ElementPattern(groupRootSequence1));
        expression1.setChildPattern(rootChildPattern1);


        // Expression2
        Expression expression2 = new Expression();
        grammar.addExpression(expression2);

        // AncestorPattern for expression2
        DoubleSlashPrefixElement root2 = new DoubleSlashPrefixElement("http://example.com/ns2", "root2");
        expression2.setAncestorPattern(new AncestorPattern(root2));

        // ChildPattern for expression2
        de.tudortmund.cs.bonxai.common.GroupRef groupRefTo_a2 = new GroupRef(bonxai.getGroupSymbolTable().getReference("a"));
        de.tudortmund.cs.bonxai.common.GroupRef groupRefTo_b2 = new GroupRef(bonxai.getGroupSymbolTable().getReference("b"));

        de.tudortmund.cs.bonxai.common.SequencePattern groupRootSequence2 = new SequencePattern();
        groupRootSequence2.addParticle(groupRefTo_a2);
        groupRootSequence2.addParticle(groupRefTo_b2);

        ChildPattern rootChildPattern2 = new ChildPattern(null, new ElementPattern(groupRootSequence2));
        expression2.setChildPattern(rootChildPattern2);

        // ChildPattern for expression2
        expression2.setChildPattern(rootChildPattern2);


        // Expression3
        Expression expression3 = new Expression();
        grammar.addExpression(expression3);

        Vector<AncestorPatternParticle> childrenOrExpression1 = new Vector<AncestorPatternParticle>();
        childrenOrExpression1.add(new SingleSlashPrefixElement("http://example.com/ns1", "root"));
        childrenOrExpression1.add(new SingleSlashPrefixElement("http://example.com/ns2", "root2"));
        OrExpression orExpression1 = new OrExpression(childrenOrExpression1);

        Vector<AncestorPatternParticle> childrenOrExpression2 = new Vector<AncestorPatternParticle>();
        childrenOrExpression2.add(new SingleSlashPrefixElement("http://example.com", "a_a"));
        childrenOrExpression2.add(new SingleSlashPrefixElement("http://example.com", "a_b"));
        childrenOrExpression2.add(new SingleSlashPrefixElement("http://example.com", "b_a"));
        childrenOrExpression2.add(new SingleSlashPrefixElement("http://example.com", "b_b"));
        OrExpression orExpression2 = new OrExpression(childrenOrExpression2);

        Vector<AncestorPatternParticle> childrenSequenceComplex = new Vector<AncestorPatternParticle>();
        childrenSequenceComplex.add(orExpression1);
        childrenSequenceComplex.add(orExpression2);

        SequenceExpression complexOne = new SequenceExpression(childrenSequenceComplex);
        expression3.setAncestorPattern(new AncestorPattern(complexOne));

        ChildPattern expression3ChildPattern = new ChildPattern(null, new ElementPattern(new de.tudortmund.cs.bonxai.bonxai.Element("http://example.com", "c")));
        expression3.setChildPattern(expression3ChildPattern);


        // Expression4
        Expression expression4 = new Expression();
        grammar.addExpression(expression4);

        Vector<AncestorPatternParticle> childrenSequenceComplexTwo = new Vector<AncestorPatternParticle>();
        childrenSequenceComplexTwo.add(new DoubleSlashPrefixElement("http://example.com/ns1", "root"));
        childrenSequenceComplexTwo.add(new SingleSlashPrefixElement("http://example.com", "a_a"));

        SequenceExpression complexTwo = new SequenceExpression(childrenSequenceComplexTwo);
        expression4.setAncestorPattern(new AncestorPattern(complexTwo));

        // ChildPattern for expression4
        ChildPattern expression4ChildPattern = new ChildPattern(null, new ElementPattern(new de.tudortmund.cs.bonxai.bonxai.Element("http://example.com", "d")));
        expression4.setChildPattern(expression4ChildPattern);

        return bonxai;
    }

    @Test
    public void testReduceGroupsInTree() {

        // Bonxai Schema
        Bonxai bonxai = this.getBasicSchema();

        ConstraintList constraintList = new ConstraintList();
        bonxai.setConstraintList(constraintList);

        // GrammarList in Bonxai Schema
        GrammarList grammar = new GrammarList();
        bonxai.setGrammarList(grammar);

        // Grouplist
        SequencePattern sequenceForGroup_a = new SequencePattern();
        de.tudortmund.cs.bonxai.bonxai.Element a_a = new de.tudortmund.cs.bonxai.bonxai.Element("http://example.com", "a_a");
        sequenceForGroup_a.addParticle(a_a);

        // GroupElements
        ElementGroupElement group_a = new ElementGroupElement("a", sequenceForGroup_a);

        // GroupList in Bonxai
        GroupList grouplist = new GroupList();
        bonxai.setGroupList(grouplist);

        // GroupElements for GroupList
        grouplist.addGroupElement(group_a);
        bonxai.getGroupSymbolTable().updateOrCreateReference("a", group_a);

        // Grammar

        // Expression1
        Expression expression1 = new Expression();
        grammar.addExpression(expression1);

        // AncestorPattern for expression1
        DoubleSlashPrefixElement root = new DoubleSlashPrefixElement("http://example.com/1", "root");
        expression1.setAncestorPattern(new AncestorPattern(root));

        // ChildPattern for expression1
        de.tudortmund.cs.bonxai.common.GroupRef groupRefTo_a1 = new GroupRef(bonxai.getGroupSymbolTable().getReference("a"));

        de.tudortmund.cs.bonxai.common.SequencePattern groupRootSequence1 = new SequencePattern();
        groupRootSequence1.addParticle(groupRefTo_a1);

        ChildPattern rootChildPattern1 = new ChildPattern(null, new ElementPattern(groupRootSequence1));
        expression1.setChildPattern(rootChildPattern1);

        // Expression2
        Expression expression2 = new Expression();
        grammar.addExpression(expression2);

        // AncestorPattern for expression2
        DoubleSlashPrefixElement root2 = new DoubleSlashPrefixElement("http://example.com/2", "root2");
        expression2.setAncestorPattern(new AncestorPattern(root2));

        // ChildPattern for expression2
        de.tudortmund.cs.bonxai.common.GroupRef groupRefTo_a2 = new GroupRef(bonxai.getGroupSymbolTable().getReference("a"));

        de.tudortmund.cs.bonxai.common.SequencePattern groupRootSequence2 = new SequencePattern();
        groupRootSequence2.addParticle(groupRefTo_a2);

        ChildPattern rootChildPattern2 = new ChildPattern(null, new ElementPattern(groupRootSequence2));
        expression2.setChildPattern(rootChildPattern2);

        // ChildPattern for expression2
        expression2.setChildPattern(rootChildPattern2);

        // Expression4
        Expression expression4 = new Expression();
        grammar.addExpression(expression4);

        Vector<AncestorPatternParticle> childrenSequenceComplexTwo = new Vector<AncestorPatternParticle>();
        childrenSequenceComplexTwo.add(new DoubleSlashPrefixElement("http://example.com/1", "root"));
        childrenSequenceComplexTwo.add(new SingleSlashPrefixElement("http://example.com", "a_a"));

        SequenceExpression complexTwo = new SequenceExpression(childrenSequenceComplexTwo);
        expression4.setAncestorPattern(new AncestorPattern(complexTwo));

        // ChildPattern for expression4
        ChildPattern expression4ChildPattern = new ChildPattern(null, new ElementPattern(new de.tudortmund.cs.bonxai.bonxai.Element("http://example.com", "d")));
        expression4.setChildPattern(expression4ChildPattern);

        HashMap<String, XSDSchema> schemas = new HashMap<String, XSDSchema>();
        GrammarConverter converter = new GrammarConverter(schemas);

        // Before the conversion and reduction of the document tree, there are 2
        // groups registered in the global grouplist
        assertEquals(1, bonxai.getGroupList().getGroupElements().size());

        converter.convert(bonxai);

        // After the conversion and reduction of the document tree, there have
        // to exist 3 groups and these have to be registered in the grouplist
        assertEquals(2, bonxai.getGroupList().getGroupElements().size());

    }

    @Test
    public void testReduceGroupsInArchitectureExampleTree() {

        /**
         * This test uses a schema described in the architecture.txt document as
         * base schema. This is a case where a group has to be redefined. A
         * expression with a higher priority changes the childpattern of a
         * already defined group. This has to be handled in the right way,
         * because it is not possible to convert this group to XSD in the
         * straight way.
         */
        HashMap<String, XSDSchema> schemas = new HashMap<String, XSDSchema>();
        GrammarConverter converter = new GrammarConverter(schemas);
        Bonxai bonxai = createSchemaLikeInArchitectureDocument();
        ConstraintList constraintList = new ConstraintList();
        bonxai.setConstraintList(constraintList);

        // Before the conversion and reduction of the document tree, there are 2
        // groups registered in the global grouplist
        assertEquals(2, bonxai.getGroupList().getGroupElements().size());

        converter.convert(bonxai);

        // After the conversion and reduction of the document tree, there have
        // to exist 3 groups and these have to be registered in the grouplist
        assertEquals(3, bonxai.getGroupList().getGroupElements().size());

        // The first group under the second element "//root2" has to be renamed.
        // So there must not be a group with the original name "a"
        Iterator<TreeNode> itrAfter1 = converter.getTreeRoot().getChilds().iterator();
        while (itrAfter1.hasNext()) {
            TreeNode currTreeNode = (TreeNode) itrAfter1.next();
            if (currTreeNode.getId().equals("{http://example.com}root([group]({http://example.com}a_a({http://example.com}d()), {http://example.com}a_b({http://example.com}c())), [group]({http://example.com}b_a({http://example.com}c()), {http://example.com}b_b({http://example.com}c())))")) {
                Iterator<TreeNode> itrAfter2 = currTreeNode.getChilds().iterator();
                while (itrAfter2.hasNext()) {
                    TreeNode currTreeNode2 = (TreeNode) itrAfter2.next();
                    if (currTreeNode2 instanceof GroupTreeNode) {
                        assertFalse(((GroupTreeNode) currTreeNode2).getName().equals("a"));
                    }

                    if (currTreeNode2 instanceof GroupTreeNode && ((GroupTreeNode) currTreeNode2).getName().equals("name-[group]({http://example.com}a_a({http://example.com}c()), {http://example.com}a_b({http://example.com}c()))-name")) {
                        assertTrue(((GroupRef) ((SequencePattern) ((ElementTreeNode) (((GroupTreeNode) currTreeNode2).getParents().iterator().next())).getExpression().getChildPattern().getElementPattern().getRegexp()).getParticles().getFirst()).getGroup().getName().equals("name-[group]({http://example.com}a_a({http://example.com}c()), {http://example.com}a_b({http://example.com}c()))-name"));
                    }
                }
            }
        }
    }

    @Test
    public void testConvertBonxaitoTreeWithConstraintMatching() {

        HashMap<String, XSDSchema> schemas = new HashMap<String, XSDSchema>();
        GrammarConverter converter = new GrammarConverter(schemas);
        Bonxai bonxai = createSchemaLikeInArchitectureDocument();
        ConstraintList constraintList = new ConstraintList();

        DoubleSlashPrefixElement root2 = new DoubleSlashPrefixElement("http://example.com/ns2", "root2");
        KeyConstraint constraint = new KeyConstraint("{}name", new AncestorPattern(root2), "/", new HashSet<String>());

        constraintList.addConstraint(constraint);

        bonxai.setConstraintList(constraintList);

        converter.convert(bonxai);


        Iterator<TreeNode> itrAfter1 = converter.getTreeRoot().getChilds().iterator();
        while (itrAfter1.hasNext()) {
            TreeNode currTreeNode = (TreeNode) itrAfter1.next();
            if (currTreeNode.getId().equals("{http://example.com/ns2}root2([group]({http://example.com}a_a({http://example.com}c()), {http://example.com}a_b({http://example.com}c())), [group]({http://example.com}b_a({http://example.com}c()), {http://example.com}b_b({http://example.com}c())))")) {
                assertEquals(1, ((ElementTreeNode)currTreeNode).getConstraints().size());
            }
        }

//        DebugUtil debug = new DebugUtil();
//        debug.printTreeNodeToSystemOut(converter.getTreeRoot());

    }

    @Test
    public void testGenerateNamespaceListsForGroupTreeNodes() {

        // Bonxai Schema
        Bonxai bonxai = this.getBasicSchema();

        ConstraintList constraintList = new ConstraintList();
        bonxai.setConstraintList(constraintList);

        // GrammarList in Bonxai Schema
        GrammarList grammar = new GrammarList();
        bonxai.setGrammarList(grammar);

        // Grouplist
        SequencePattern sequenceForGroup_a = new SequencePattern();
        de.tudortmund.cs.bonxai.bonxai.Element a_a = new de.tudortmund.cs.bonxai.bonxai.Element("", "elementInGroup");
        sequenceForGroup_a.addParticle(a_a);

        // GroupElements
        ElementGroupElement group_a = new ElementGroupElement("a", sequenceForGroup_a);

        // GroupList in Bonxai
        GroupList grouplist = new GroupList();
        bonxai.setGroupList(grouplist);

        // GroupElements for GroupList
        grouplist.addGroupElement(group_a);
        bonxai.getGroupSymbolTable().updateOrCreateReference("a", group_a);

        // Grammar

        // Expression1
        Expression expression1 = new Expression();
        grammar.addExpression(expression1);

        // AncestorPattern for expression1
        DoubleSlashPrefixElement root = new DoubleSlashPrefixElement("http://example.com/namespace_one", "root");
        expression1.setAncestorPattern(new AncestorPattern(root));

        // ChildPattern for expression1
        de.tudortmund.cs.bonxai.common.GroupRef groupRefTo_a1 = new GroupRef(bonxai.getGroupSymbolTable().getReference("a"));

        de.tudortmund.cs.bonxai.common.SequencePattern groupRootSequence1 = new SequencePattern();
        groupRootSequence1.addParticle(groupRefTo_a1);

        ChildPattern rootChildPattern1 = new ChildPattern(null, new ElementPattern(groupRootSequence1));
        expression1.setChildPattern(rootChildPattern1);

        // Expression2
        Expression expression2 = new Expression();
        grammar.addExpression(expression2);

        // AncestorPattern for expression2
        DoubleSlashPrefixElement root2 = new DoubleSlashPrefixElement("http://example.com/namespace_two", "root2");
        expression2.setAncestorPattern(new AncestorPattern(root2));

        // ChildPattern for expression2
        de.tudortmund.cs.bonxai.common.GroupRef groupRefTo_a2 = new GroupRef(bonxai.getGroupSymbolTable().getReference("a"));

        de.tudortmund.cs.bonxai.common.SequencePattern groupRootSequence2 = new SequencePattern();
        groupRootSequence2.addParticle(groupRefTo_a2);

        ChildPattern rootChildPattern2 = new ChildPattern(null, new ElementPattern(groupRootSequence2));
        expression2.setChildPattern(rootChildPattern2);

        // ChildPattern for expression2
        expression2.setChildPattern(rootChildPattern2);

        HashMap<String, XSDSchema> schemas = new HashMap<String, XSDSchema>();
        GrammarConverter converter = new GrammarConverter(schemas);

        converter.convert(bonxai);

        assertTrue(((GroupTreeNode)converter.getTreeRoot().getChilds().iterator().next().getChilds().iterator().next()).getNamespaces().contains("http://example.com/namespace_two"));
        assertTrue(((GroupTreeNode)converter.getTreeRoot().getChilds().iterator().next().getChilds().iterator().next()).getNamespaces().contains("http://example.com/namespace_one"));
    }
}