package de.tudortmund.cs.bonxai.converter.bonxai2xsd;

import java.util.Vector;
import org.junit.*;

import de.tudortmund.cs.bonxai.converter.bonxai2xsd.*;
import de.tudortmund.cs.bonxai.bonxai.*;
import java.util.LinkedHashSet;

/**
 * Tests for type connector
 */
public class FullMatchingRootElementFinderTest extends junit.framework.TestCase {

    @Test
    public final void testFindSingleSlashPrefixElement() {
        GrammarList grammar   = new GrammarList();
        Expression expression = new Expression();
        grammar.addExpression(expression);
        SingleSlashPrefixElement element_1 = new SingleSlashPrefixElement("http://example.com", "root");
        expression.setAncestorPattern(new AncestorPattern(element_1));

        FullMatchingRootElementFinder finder = new FullMatchingRootElementFinder();
        LinkedHashSet<ElementTreeNode> elements = finder.find(grammar);

        assertEquals(1, elements.size());
        //assertTrue(elements.contains(element_1));
    }

    @Test
    public final void testFindDoubleSlashPrefixElement() {
        GrammarList grammar   = new GrammarList();
        Expression expression = new Expression();
        grammar.addExpression(expression);
        DoubleSlashPrefixElement element_1 = new DoubleSlashPrefixElement("http://example.com", "root");
        expression.setAncestorPattern(new AncestorPattern(element_1));

        FullMatchingRootElementFinder finder = new FullMatchingRootElementFinder();
        LinkedHashSet<ElementTreeNode> elements = finder.find(grammar);

        assertEquals(1, elements.size());
        //assertTrue(elements.contains(element_1));
    }

    @Test
    public final void testIgnoreSequence() {
        GrammarList grammar   = new GrammarList();
        Expression expression = new Expression();
        grammar.addExpression(expression);

        Vector<AncestorPatternParticle> sequence = new Vector<AncestorPatternParticle>();
        DoubleSlashPrefixElement element_1 = new DoubleSlashPrefixElement("http://example.com", "root");
        sequence.add(element_1);
        SingleSlashPrefixElement element_2 = new SingleSlashPrefixElement("http://example.com", "root");
        sequence.add(element_2);
        expression.setAncestorPattern(new AncestorPattern(new SequenceExpression(sequence)));

        FullMatchingRootElementFinder finder = new FullMatchingRootElementFinder();
        LinkedHashSet<ElementTreeNode> elements = finder.find(grammar);

        assertEquals(0, elements.size());
    }

    @Test
    public final void testFindMultipleInOr() {
        GrammarList grammar   = new GrammarList();
        Expression expression = new Expression();
        grammar.addExpression(expression);

        Vector<AncestorPatternParticle> or = new Vector<AncestorPatternParticle>();
        DoubleSlashPrefixElement element_1 = new DoubleSlashPrefixElement("http://example.com/ns1", "root");
        or.add(element_1);
        SingleSlashPrefixElement element_2 = new SingleSlashPrefixElement("http://example.com/ns2", "root");
        or.add(element_2);
        expression.setAncestorPattern(new AncestorPattern(new OrExpression(or)));

        FullMatchingRootElementFinder finder = new FullMatchingRootElementFinder();
        LinkedHashSet<ElementTreeNode> elements = finder.find(grammar);

        assertEquals(2, elements.size());
        //assertTrue(elements.contains(element_1));
        //assertTrue(elements.contains(element_2));
    }

    @Test
    public final void testFindInCardinality0() {
        GrammarList grammar   = new GrammarList();
        Expression expression = new Expression();
        grammar.addExpression(expression);
        SingleSlashPrefixElement element_1 = new SingleSlashPrefixElement("http://example.com", "root");
        expression.setAncestorPattern(new AncestorPattern(new CardinalityParticle(element_1, new Integer(0))));

        FullMatchingRootElementFinder finder = new FullMatchingRootElementFinder();
        LinkedHashSet<ElementTreeNode> elements = finder.find(grammar);

        assertEquals(1, elements.size());
        //assertTrue(elements.contains(element_1));
    }

    @Test
    public final void testFindInCardinality1() {
        GrammarList grammar   = new GrammarList();
        Expression expression = new Expression();
        grammar.addExpression(expression);
        SingleSlashPrefixElement element_1 = new SingleSlashPrefixElement("http://example.com", "root");
        expression.setAncestorPattern(new AncestorPattern(new CardinalityParticle(element_1, new Integer(1))));

        FullMatchingRootElementFinder finder = new FullMatchingRootElementFinder();
        LinkedHashSet<ElementTreeNode> elements = finder.find(grammar);

        assertEquals(1, elements.size());
        //assertTrue(elements.contains(element_1));
    }

    @Test
    public final void testNotFindInCardinality() {
        GrammarList grammar   = new GrammarList();
        Expression expression = new Expression();
        grammar.addExpression(expression);
        SingleSlashPrefixElement element_1 = new SingleSlashPrefixElement("http://example.com", "root");
        expression.setAncestorPattern(new AncestorPattern(new CardinalityParticle(element_1, new Integer(2))));

        FullMatchingRootElementFinder finder = new FullMatchingRootElementFinder();
        LinkedHashSet<ElementTreeNode> elements = finder.find(grammar);

        assertEquals(0, elements.size());
    }

    @Test
    public final void testFindInComplexPattern() {
        GrammarList grammar   = new GrammarList();
        Expression expression = new Expression();
        grammar.addExpression(expression);

        Vector<AncestorPatternParticle> or1 = new Vector<AncestorPatternParticle>();
        DoubleSlashPrefixElement element_1 = new DoubleSlashPrefixElement("http://example.com/ns", "path");
        or1.add(element_1);
        SingleSlashPrefixElement element_2 = new SingleSlashPrefixElement("http://example.com/ns", "root");
        or1.add(element_2);

        Vector<AncestorPatternParticle> or2 = new Vector<AncestorPatternParticle>();
        DoubleSlashPrefixElement element_3 = new DoubleSlashPrefixElement("http://example.com/ns1", "path");
        or2.add(element_3);
        SingleSlashPrefixElement element_4 = new SingleSlashPrefixElement("http://example.com/ns2", "path");
        or2.add(element_4);

        Vector<AncestorPatternParticle> sequence = new Vector<AncestorPatternParticle>();
        sequence.add(new CardinalityParticle(new OrExpression(or1), new Integer(0)));
        sequence.add(new CardinalityParticle(new OrExpression(or2), new Integer(1), new Integer(3)));
        expression.setAncestorPattern(new AncestorPattern(new SequenceExpression(sequence)));

        FullMatchingRootElementFinder finder = new FullMatchingRootElementFinder();
        LinkedHashSet<ElementTreeNode> elements = finder.find(grammar);

        assertEquals(2, elements.size());
        //assertTrue(elements.contains(element_3));
        //assertTrue(elements.contains(element_4));
    }

    @Test
    public final void testFindMoreInComplexPattern() {
        GrammarList grammar   = new GrammarList();
        Expression expression = new Expression();
        grammar.addExpression(expression);

        Vector<AncestorPatternParticle> or1 = new Vector<AncestorPatternParticle>();
        DoubleSlashPrefixElement element_1 = new DoubleSlashPrefixElement("http://example.com/ns", "path");
        or1.add(element_1);
        SingleSlashPrefixElement element_2 = new SingleSlashPrefixElement("http://example.com/ns", "root");
        or1.add(element_2);

        Vector<AncestorPatternParticle> or2 = new Vector<AncestorPatternParticle>();
        DoubleSlashPrefixElement element_3 = new DoubleSlashPrefixElement("http://example.com/ns1", "path");
        or2.add(element_3);
        SingleSlashPrefixElement element_4 = new SingleSlashPrefixElement("http://example.com/ns2", "path");
        or2.add(element_4);

        Vector<AncestorPatternParticle> sequence = new Vector<AncestorPatternParticle>();
        sequence.add(new CardinalityParticle(new OrExpression(or1), new Integer(0)));
        sequence.add(new CardinalityParticle(new OrExpression(or2), new Integer(0), new Integer(3)));
        expression.setAncestorPattern(new AncestorPattern(new SequenceExpression(sequence)));

        FullMatchingRootElementFinder finder = new FullMatchingRootElementFinder();
        LinkedHashSet<ElementTreeNode> elements = finder.find(grammar);

        assertEquals(4, elements.size());
        //assertTrue(elements.contains(element_1));
        //assertTrue(elements.contains(element_2));
        //assertTrue(elements.contains(element_3));
        //assertTrue(elements.contains(element_4));
    }

    @Test
    public final void testFindInMultipleExpressions() {
        GrammarList grammar   = new GrammarList();

        Expression expression_1 = new Expression();
        grammar.addExpression(expression_1);
        SingleSlashPrefixElement element_1 = new SingleSlashPrefixElement("http://example.com/ns1", "root");
        expression_1.setAncestorPattern(new AncestorPattern(element_1));

        Expression expression_2 = new Expression();
        grammar.addExpression(expression_2);
        DoubleSlashPrefixElement element_2 = new DoubleSlashPrefixElement("http://example.com/ns2", "root");
        expression_2.setAncestorPattern(new AncestorPattern(element_2));

        FullMatchingRootElementFinder finder = new FullMatchingRootElementFinder();
        LinkedHashSet<ElementTreeNode> elements = finder.find(grammar);

        assertEquals(2, elements.size());
        //assertTrue(elements.contains(element_1));
        //assertTrue(elements.contains(element_2));
    }

    @Test
    public final void testFindInMultipleExpressions2() {
        GrammarList grammar = new GrammarList();

        Expression root = new Expression();
        grammar.addExpression(root);

        SingleSlashPrefixElement rootPattern = new SingleSlashPrefixElement("http://example.com", "root");
        root.setAncestorPattern(new AncestorPattern(rootPattern));

        Expression path = new Expression();
        grammar.addExpression(path);

        Vector<AncestorPatternParticle> sequence = new Vector<AncestorPatternParticle>();
        sequence.add(new SingleSlashPrefixElement( "http://example.com", "root" ));
        sequence.add(new SingleSlashPrefixElement( "http://example.com", "path" ));
        SequenceExpression pathPattern = new SequenceExpression( sequence );
        path.setAncestorPattern(new AncestorPattern(pathPattern));

        FullMatchingRootElementFinder finder = new FullMatchingRootElementFinder();
        LinkedHashSet<ElementTreeNode> elements = finder.find(grammar);

        assertEquals(1, elements.size());
        //assertTrue(elements.contains(element_1));
        //assertTrue(elements.contains(element_2));
    }
}
