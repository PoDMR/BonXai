package de.tudortmund.cs.bonxai.converter.bonxai2xsd;

import static org.junit.Assert.*;

import org.junit.*;
import org.hamcrest.*;

import java.util.Vector;

import de.tudortmund.cs.bonxai.converter.bonxai2xsd.*;
import de.tudortmund.cs.bonxai.bonxai.*;

/**
 * Tests for type connector
 */
public class RegularExpressionAncestorPatternMatcherTest extends junit.framework.TestCase {

    protected TreeNode craftTreeNodeSameNamespace()
    {
        SchemaTreeNode schema = new SchemaTreeNode();

        ElementTreeNode root = new ElementTreeNode("http://example.com/ns", "root", new Expression());
        schema.addChild(root);

        return root;
    }

    protected TreeNode craftComplexTreeNode()
    {
        SchemaTreeNode schema = new SchemaTreeNode();

        ElementTreeNode root = new ElementTreeNode("http://example.com/ns", "root", new Expression());
        schema.addChild(root);

        ElementTreeNode path1 = new ElementTreeNode("http://example.com/ns1", "path", new Expression());
        root.addChild(path1);

        ElementTreeNode path2 = new ElementTreeNode("http://example.com/ns2", "path", new Expression());
        path1.addChild(path2);

        return path2;
    }

    @Test
    public final void testEmptyExpressionMatchesEmptyPath() {
        RegularExpressionAncestorPatternMatcher matcher = new RegularExpressionAncestorPatternMatcher();
        boolean matches         = true;
        AncestorPattern pattern = new AncestorPattern(
            new SequenceExpression(
                new Vector<AncestorPatternParticle>()
            )
        );

        assertEquals(
            matches,
            matcher.matches(new SchemaTreeNode(), pattern)
        );
    }

    @Test
    public final void testEmptyExpressionNotMatchesSimplePath() {
        RegularExpressionAncestorPatternMatcher matcher = new RegularExpressionAncestorPatternMatcher();
        boolean matches         = false;
        AncestorPattern pattern = new AncestorPattern(
            new SequenceExpression(
                new Vector<AncestorPatternParticle>()
            )
        );

        assertEquals(
            matches,
            matcher.matches(this.craftTreeNodeSameNamespace(), pattern)
        );
    }

    @Test
    public final void testSingleSlashElementMatchesSimplePath() {
        RegularExpressionAncestorPatternMatcher matcher = new RegularExpressionAncestorPatternMatcher();
        boolean matches         = true;
        AncestorPattern pattern = new AncestorPattern(new SingleSlashPrefixElement("http://example.com/ns", "root"));

        assertEquals(
            matches,
            matcher.matches(this.craftTreeNodeSameNamespace(), pattern)
        );
    }

    @Test
    public final void testSingleSlashElementMatchesSimplePathWrongName() {
        RegularExpressionAncestorPatternMatcher matcher = new RegularExpressionAncestorPatternMatcher();
        boolean matches         = false;
        AncestorPattern pattern = new AncestorPattern(new SingleSlashPrefixElement("http://example.com/ns", "unknown"));

        assertEquals(
            matches,
            matcher.matches(this.craftTreeNodeSameNamespace(), pattern)
        );
    }

    @Test
    public final void testSingleSlashElementMatchesSimplePathWrongNamespace() {
        RegularExpressionAncestorPatternMatcher matcher = new RegularExpressionAncestorPatternMatcher();
        boolean matches         = false;
        AncestorPattern pattern = new AncestorPattern(new SingleSlashPrefixElement("http://example.com/unknown", "root"));

        assertEquals(
            matches,
            matcher.matches(this.craftTreeNodeSameNamespace(), pattern)
        );
    }

    @Test
    public final void testDoubleSlashElementMatchesSimplePath() {
        RegularExpressionAncestorPatternMatcher matcher = new RegularExpressionAncestorPatternMatcher();
        boolean matches         = true;
        AncestorPattern pattern = new AncestorPattern(new DoubleSlashPrefixElement("http://example.com/ns", "root"));

        assertEquals(
            matches,
            matcher.matches(this.craftTreeNodeSameNamespace(), pattern)
        );
    }

    @Test
    public final void testDoubleSlashElementMatchesSimplePathWrongName() {
        RegularExpressionAncestorPatternMatcher matcher = new RegularExpressionAncestorPatternMatcher();
        boolean matches         = false;
        AncestorPattern pattern = new AncestorPattern(new DoubleSlashPrefixElement("http://example.com/ns", "unknown"));

        assertEquals(
            matches,
            matcher.matches(this.craftTreeNodeSameNamespace(), pattern)
        );
    }

    @Test
    public final void testDoubleSlashElementMatchesSimplePathWrongNamespace() {
        RegularExpressionAncestorPatternMatcher matcher = new RegularExpressionAncestorPatternMatcher();
        boolean matches         = false;
        AncestorPattern pattern = new AncestorPattern(new DoubleSlashPrefixElement("http://example.com/unknown", "root"));

        assertEquals(
            matches,
            matcher.matches(this.craftTreeNodeSameNamespace(), pattern)
        );
    }

    @Test
    public final void testSequenceExpressionMatchesSimplePath() {
        RegularExpressionAncestorPatternMatcher matcher = new RegularExpressionAncestorPatternMatcher();
        boolean matches         = true;
        Vector<AncestorPatternParticle> sequence = new Vector<AncestorPatternParticle>();
        sequence.add(new SingleSlashPrefixElement( "http://example.com/ns", "root" ));
        AncestorPattern pattern = new AncestorPattern(new SequenceExpression(sequence));

        assertEquals(
            matches,
            matcher.matches(this.craftTreeNodeSameNamespace(), pattern)
        );
    }

    @Test
    public final void testSequenceExpressionNotMatchesSimplePath() {
        RegularExpressionAncestorPatternMatcher matcher = new RegularExpressionAncestorPatternMatcher();
        boolean matches         = false;
        Vector<AncestorPatternParticle> sequence = new Vector<AncestorPatternParticle>();
        sequence.add(new SingleSlashPrefixElement( "http://example.com/ns", "root" ));
        sequence.add(new SingleSlashPrefixElement( "http://example.com/ns", "root" ));
        AncestorPattern pattern = new AncestorPattern(new SequenceExpression(sequence));

        assertEquals(
            matches,
            matcher.matches(this.craftTreeNodeSameNamespace(), pattern)
        );
    }

    @Test
    public final void testOrExpressionMatchesSimplePath() {
        RegularExpressionAncestorPatternMatcher matcher = new RegularExpressionAncestorPatternMatcher();
        boolean matches         = true;
        Vector<AncestorPatternParticle> or = new Vector<AncestorPatternParticle>();
        or.add(new SingleSlashPrefixElement( "http://example.com/ns", "root" ));
        AncestorPattern pattern = new AncestorPattern(new OrExpression(or));

        assertEquals(
            matches,
            matcher.matches(this.craftTreeNodeSameNamespace(), pattern)
        );
    }

    @Test
    public final void testOrExpressionMatchesSimplePathMultipleOptions() {
        RegularExpressionAncestorPatternMatcher matcher = new RegularExpressionAncestorPatternMatcher();
        boolean matches         = true;
        Vector<AncestorPatternParticle> or = new Vector<AncestorPatternParticle>();
        or.add(new SingleSlashPrefixElement( "http://example.com/ns", "root" ));
        or.add(new SingleSlashPrefixElement( "http://example.com/ns", "root" ));
        AncestorPattern pattern = new AncestorPattern(new OrExpression(or));

        assertEquals(
            matches,
            matcher.matches(this.craftTreeNodeSameNamespace(), pattern)
        );
    }

    @Test
    public final void testOrExpressionMatchesSimplePathWithNonMatches() {
        RegularExpressionAncestorPatternMatcher matcher = new RegularExpressionAncestorPatternMatcher();
        boolean matches         = true;
        Vector<AncestorPatternParticle> or = new Vector<AncestorPatternParticle>();
        or.add(new SingleSlashPrefixElement( "http://example.com/ns", "unknown" ));
        or.add(new SingleSlashPrefixElement( "http://example.com/ns", "root" ));
        AncestorPattern pattern = new AncestorPattern(new OrExpression(or));

        assertEquals(
            matches,
            matcher.matches(this.craftTreeNodeSameNamespace(), pattern)
        );
    }

    @Test
    public final void testOrExpressionNotMatchesSimplePathWithNonMatch() {
        RegularExpressionAncestorPatternMatcher matcher = new RegularExpressionAncestorPatternMatcher();
        boolean matches         = false;
        Vector<AncestorPatternParticle> or = new Vector<AncestorPatternParticle>();
        or.add(new SingleSlashPrefixElement( "http://example.com/ns", "unknown" ));
        AncestorPattern pattern = new AncestorPattern(new OrExpression(or));

        assertEquals(
            matches,
            matcher.matches(this.craftTreeNodeSameNamespace(), pattern)
        );
    }

    @Test
    public final void testOrExpressionNotMatchesSimplePathWithMultipleNonMatches() {
        RegularExpressionAncestorPatternMatcher matcher = new RegularExpressionAncestorPatternMatcher();
        boolean matches         = false;
        Vector<AncestorPatternParticle> or = new Vector<AncestorPatternParticle>();
        or.add(new SingleSlashPrefixElement( "http://example.com/ns", "unknown_1" ));
        or.add(new SingleSlashPrefixElement( "http://example.com/ns", "unknown_2" ));
        AncestorPattern pattern = new AncestorPattern(new OrExpression(or));

        assertEquals(
            matches,
            matcher.matches(this.craftTreeNodeSameNamespace(), pattern)
        );
    }

    @Test
    public final void testCardinalityMatchesMin0MaxUnlimited() {
        RegularExpressionAncestorPatternMatcher matcher = new RegularExpressionAncestorPatternMatcher();
        boolean matches         = true;
        AncestorPattern pattern = new AncestorPattern(new CardinalityParticle(new SingleSlashPrefixElement("http://example.com/ns", "root"), new Integer(0)));

        assertEquals(
            matches,
            matcher.matches(this.craftTreeNodeSameNamespace(), pattern)
        );
    }

    @Test
    public final void testCardinalityMatchesMin1MaxUnlimited() {
        RegularExpressionAncestorPatternMatcher matcher = new RegularExpressionAncestorPatternMatcher();
        boolean matches         = true;
            AncestorPattern pattern = new AncestorPattern(new CardinalityParticle(new SingleSlashPrefixElement("http://example.com/ns", "root"), new Integer(1)));

        assertEquals(
            matches,
            matcher.matches(this.craftTreeNodeSameNamespace(), pattern)
        );
    }

    @Test
    public final void testCardinalityNotMatchesMin2MaxUnlimited() {
        RegularExpressionAncestorPatternMatcher matcher = new RegularExpressionAncestorPatternMatcher();
        boolean matches         = false;
        AncestorPattern pattern = new AncestorPattern(new CardinalityParticle(new SingleSlashPrefixElement("http://example.com/ns", "root"), new Integer(2)));

        assertEquals(
            matches,
            matcher.matches(this.craftTreeNodeSameNamespace(), pattern)
        );
    }

    @Test
    public final void testCardinalityNotMatchesMin0Max0() {
        RegularExpressionAncestorPatternMatcher matcher = new RegularExpressionAncestorPatternMatcher();
        boolean matches         = false;
        AncestorPattern pattern = new AncestorPattern(new CardinalityParticle(new SingleSlashPrefixElement("http://example.com/ns", "root"), new Integer(0), new Integer(0)));

        assertEquals(
            matches,
            matcher.matches(this.craftTreeNodeSameNamespace(), pattern)
        );
    }

    @Test
    public final void testCardinalityNotMatchesMin0Max1() {
        RegularExpressionAncestorPatternMatcher matcher = new RegularExpressionAncestorPatternMatcher();
        boolean matches         = true;
        AncestorPattern pattern = new AncestorPattern(new CardinalityParticle(new SingleSlashPrefixElement("http://example.com/ns", "root"), new Integer(0), new Integer(1)));

        assertEquals(
            matches,
            matcher.matches(this.craftTreeNodeSameNamespace(), pattern)
        );
    }

    @Test
    public final void testCardinalityNotMatchesMin0Max2() {
        RegularExpressionAncestorPatternMatcher matcher = new RegularExpressionAncestorPatternMatcher();
        boolean matches         = true;
        AncestorPattern pattern = new AncestorPattern(new CardinalityParticle(new SingleSlashPrefixElement("http://example.com/ns", "root"), new Integer(0), new Integer(2)));

        assertEquals(
            matches,
            matcher.matches(this.craftTreeNodeSameNamespace(), pattern)
        );
    }

    @Test
    public final void testDoubleSlashMatchesLastElement() {
        RegularExpressionAncestorPatternMatcher matcher = new RegularExpressionAncestorPatternMatcher();
        boolean matches         = true;
        AncestorPattern pattern = new AncestorPattern(new DoubleSlashPrefixElement( "http://example.com/ns2", "path" ));

        assertEquals(
            matches,
            matcher.matches(this.craftComplexTreeNode(), pattern)
        );
    }

    @Test
    public final void testDoubleSlashNotMatchBeforeLastElement() {
        RegularExpressionAncestorPatternMatcher matcher = new RegularExpressionAncestorPatternMatcher();
        boolean matches         = false;
        AncestorPattern pattern = new AncestorPattern(new DoubleSlashPrefixElement( "http://example.com/ns1", "path" ));

        assertEquals(
            matches,
            matcher.matches(this.craftComplexTreeNode(), pattern)
        );
    }

    @Test
    public final void testMatchComplexPattern() {
        RegularExpressionAncestorPatternMatcher matcher = new RegularExpressionAncestorPatternMatcher();
        boolean matches         = true;
        Vector<AncestorPatternParticle> or1 = new Vector<AncestorPatternParticle>();
        or1.add(new SingleSlashPrefixElement( "http://example.com/ns", "path" ));
        or1.add(new SingleSlashPrefixElement( "http://example.com/ns", "root" ));

        Vector<AncestorPatternParticle> or2 = new Vector<AncestorPatternParticle>();
        or2.add(new SingleSlashPrefixElement( "http://example.com/ns1", "path" ));
        or2.add(new SingleSlashPrefixElement( "http://example.com/ns2", "path" ));

        Vector<AncestorPatternParticle> sequence = new Vector<AncestorPatternParticle>();
        sequence.add(new CardinalityParticle(new OrExpression(or1), new Integer(1)));
        sequence.add(new CardinalityParticle(new OrExpression(or2), new Integer(2), new Integer(3)));
        AncestorPattern pattern = new AncestorPattern(new SequenceExpression(sequence));

        assertEquals(
            matches,
            matcher.matches(this.craftComplexTreeNode(), pattern)
        );
    }

    @Test
    public final void testNotMatchComplexPattern1() {
        RegularExpressionAncestorPatternMatcher matcher = new RegularExpressionAncestorPatternMatcher();
        boolean matches         = false;
        Vector<AncestorPatternParticle> or1 = new Vector<AncestorPatternParticle>();
        or1.add(new SingleSlashPrefixElement( "http://example.com/ns", "path" ));
        or1.add(new SingleSlashPrefixElement( "http://example.com/ns", "root" ));

        Vector<AncestorPatternParticle> or2 = new Vector<AncestorPatternParticle>();
        or2.add(new SingleSlashPrefixElement( "http://example.com/ns1", "path" ));
        or2.add(new SingleSlashPrefixElement( "http://example.com/ns2", "path" ));

        Vector<AncestorPatternParticle> sequence = new Vector<AncestorPatternParticle>();
        sequence.add(new CardinalityParticle(new OrExpression(or1), new Integer(2)));
        sequence.add(new CardinalityParticle(new OrExpression(or2), new Integer(2), new Integer(3)));
        AncestorPattern pattern = new AncestorPattern(new SequenceExpression(sequence));

        assertEquals(
            matches,
            matcher.matches(this.craftComplexTreeNode(), pattern)
        );
    }

    @Test
    public final void testNotMatchComplexPattern2() {
        RegularExpressionAncestorPatternMatcher matcher = new RegularExpressionAncestorPatternMatcher();
        boolean matches         = false;
        Vector<AncestorPatternParticle> or1 = new Vector<AncestorPatternParticle>();
        or1.add(new SingleSlashPrefixElement( "http://example.com/ns1", "path" ));
        or1.add(new SingleSlashPrefixElement( "http://example.com/ns2", "path" ));

        Vector<AncestorPatternParticle> or2 = new Vector<AncestorPatternParticle>();
        or2.add(new SingleSlashPrefixElement( "http://example.com/ns1", "path" ));
        or2.add(new SingleSlashPrefixElement( "http://example.com/ns2", "path" ));

        Vector<AncestorPatternParticle> sequence = new Vector<AncestorPatternParticle>();
        sequence.add(new CardinalityParticle(new OrExpression(or1), new Integer(1)));
        sequence.add(new CardinalityParticle(new OrExpression(or2), new Integer(2), new Integer(3)));
        AncestorPattern pattern = new AncestorPattern(new SequenceExpression(sequence));

        assertEquals(
            matches,
            matcher.matches(this.craftComplexTreeNode(), pattern)
        );
    }
}
