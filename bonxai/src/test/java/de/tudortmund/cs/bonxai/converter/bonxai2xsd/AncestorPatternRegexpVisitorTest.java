package de.tudortmund.cs.bonxai.converter.bonxai2xsd;

import org.junit.*;
import java.util.Vector;

import de.tudortmund.cs.bonxai.converter.bonxai2xsd.*;
import de.tudortmund.cs.bonxai.bonxai.*;

/**
 * Tests for type connector
 */
public class AncestorPatternRegexpVisitorTest extends junit.framework.TestCase {

    @Test
    public final void testVisitEmptyAncestorPattern() throws AncestorPatternUnknownParticleException {
        AncestorPatternRegexpVisitor visitor = new AncestorPatternRegexpVisitor(
            new AncestorPattern(
                new SequenceExpression(
                    new Vector<AncestorPatternParticle>()
                )
            )
        );

        assertEquals( "\\A\\Z", visitor.visit().pattern() );
    }

    @Test
    public final void testVisitEmptyOrExpression() throws AncestorPatternUnknownParticleException {
        AncestorPatternRegexpVisitor visitor = new AncestorPatternRegexpVisitor(
            new AncestorPattern(
                new OrExpression(
                    new Vector<AncestorPatternParticle>()
                )
            )
        );

        assertEquals( "\\A\\Z", visitor.visit().pattern() );
    }

    @Test
    public final void testSingleElement() throws AncestorPatternUnknownParticleException {
        AncestorPatternRegexpVisitor visitor = new AncestorPatternRegexpVisitor(
            new AncestorPattern(
                new SingleSlashPrefixElement( "http://example.com", "element" )
            )
        );

        assertEquals( "\\A/\\{\\Qhttp://example.com\\E\\}\\Qelement\\E\\Z", visitor.visit().pattern() );
    }

    @Test
    public final void testDoubleElement() throws AncestorPatternUnknownParticleException {
        AncestorPatternRegexpVisitor visitor = new AncestorPatternRegexpVisitor(
            new AncestorPattern(
                new DoubleSlashPrefixElement( "http://example.com", "element" )
            )
        );

        assertEquals( "\\A.*/\\{\\Qhttp://example.com\\E\\}\\Qelement\\E\\Z", visitor.visit().pattern() );
    }

    @Test
    public final void testNonEmptySequence() throws AncestorPatternUnknownParticleException {
        Vector<AncestorPatternParticle> sequence = new Vector<AncestorPatternParticle>();
        sequence.add(new DoubleSlashPrefixElement( "http://example.com", "element" ));
        sequence.add(new SingleSlashPrefixElement( "http://example.com", "element" ));

        AncestorPatternRegexpVisitor visitor = new AncestorPatternRegexpVisitor(
            new AncestorPattern(
                new SequenceExpression(sequence)
            )
        );

        assertEquals( "\\A(?:.*/\\{\\Qhttp://example.com\\E\\}\\Qelement\\E/\\{\\Qhttp://example.com\\E\\}\\Qelement\\E)\\Z", visitor.visit().pattern() );
    }

    @Test
    public final void testNonEmptyOrExpression() throws AncestorPatternUnknownParticleException {
        Vector<AncestorPatternParticle> or = new Vector<AncestorPatternParticle>();
        or.add(new DoubleSlashPrefixElement( "http://example.com", "element" ));
        or.add(new SingleSlashPrefixElement( "http://example.com", "element" ));

        AncestorPatternRegexpVisitor visitor = new AncestorPatternRegexpVisitor(
            new AncestorPattern(
                new OrExpression(or)
            )
        );

        assertEquals( "\\A(?:.*/\\{\\Qhttp://example.com\\E\\}\\Qelement\\E|/\\{\\Qhttp://example.com\\E\\}\\Qelement\\E)\\Z", visitor.visit().pattern() );
    }

    @Test
    public final void testUnlimitedCardinalityConstraint() throws AncestorPatternUnknownParticleException {
        AncestorPatternRegexpVisitor visitor = new AncestorPatternRegexpVisitor(
            new AncestorPattern(
                new CardinalityParticle(
                    new DoubleSlashPrefixElement( "http://example.com", "element" ),
                    new Integer(2)
                )
            )
        );

        assertEquals( "\\A(?:.*/\\{\\Qhttp://example.com\\E\\}\\Qelement\\E){2,}\\Z", visitor.visit().pattern() );
    }

    @Test
    public final void testLimitedCardinalityConstraint() throws AncestorPatternUnknownParticleException {
        AncestorPatternRegexpVisitor visitor = new AncestorPatternRegexpVisitor(
            new AncestorPattern(
                new CardinalityParticle(
                    new DoubleSlashPrefixElement( "http://example.com", "element" ),
                    new Integer(2),
                    new Integer(4)
                )
            )
        );

        assertEquals( "\\A(?:.*/\\{\\Qhttp://example.com\\E\\}\\Qelement\\E){2,4}\\Z", visitor.visit().pattern() );
    }

    @Test
    public final void testRecursivelyStackedExpression() throws AncestorPatternUnknownParticleException {
        Vector<AncestorPatternParticle> or = new Vector<AncestorPatternParticle>();
        or.add(new DoubleSlashPrefixElement( "http://example.com", "element" ));
        or.add(new SingleSlashPrefixElement( "http://example.com", "element" ));

        Vector<AncestorPatternParticle> sequence = new Vector<AncestorPatternParticle>();
        sequence.add(new DoubleSlashPrefixElement( "http://example.com", "element" ));
        sequence.add(new OrExpression(or));

        AncestorPatternRegexpVisitor visitor = new AncestorPatternRegexpVisitor(
            new AncestorPattern(
                new SequenceExpression(sequence)
            )
        );

        assertEquals( "\\A(?:.*/\\{\\Qhttp://example.com\\E\\}\\Qelement\\E(?:.*/\\{\\Qhttp://example.com\\E\\}\\Qelement\\E|/\\{\\Qhttp://example.com\\E\\}\\Qelement\\E))\\Z", visitor.visit().pattern() );
    }

    @Test
    public final void testEscapeSpecialChars() throws AncestorPatternUnknownParticleException {
        AncestorPatternRegexpVisitor visitor = new AncestorPatternRegexpVisitor(
            new AncestorPattern(
                new SingleSlashPrefixElement( "http://example.com", "element:.\"§$%&/()" )
            )
        );

        assertEquals( "\\A/\\{\\Qhttp://example.com\\E\\}\\Qelement:.\"§$%&/()\\E\\Z", visitor.visit().pattern() );
    }
}
