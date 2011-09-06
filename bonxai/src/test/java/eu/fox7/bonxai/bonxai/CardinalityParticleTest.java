
package eu.fox7.bonxai.bonxai;

import java.util.Vector;
import org.junit.Test;

import eu.fox7.bonxai.bonxai.*;
import static org.junit.Assert.*;

/**
 *
 */
public class CardinalityParticleTest extends junit.framework.TestCase {

    /**
     * Test of getChildren method, of class ContainerParticle.
     */
    @Test
    public void testConstructFromMin() {
        DoubleSlashPrefixElement element = new DoubleSlashPrefixElement("namespace", "name");
        CardinalityParticle test = new CardinalityParticle(element, new Integer( 1 ));

        assertEquals(new Integer( 1 ), test.getMin());
        assertEquals(null, test.getMax());
        assertSame(element, test.getChild());
    }

    /**
     * Test of getChildren method, of class ContainerParticle.
     */
    @Test
    public void testConstructFromMinAndMax() {
        DoubleSlashPrefixElement element = new DoubleSlashPrefixElement("namespace", "name");
        CardinalityParticle test = new CardinalityParticle(element, new Integer( 1 ), new Integer( 2 ));

        assertEquals(new Integer( 1 ), test.getMin());
        assertEquals(new Integer( 2 ), test.getMax());
        assertSame(element, test.getChild());
    }
}
