
package eu.fox7.schematoolkit.bonxai.om;

import java.util.Vector;
import java.util.HashSet;
import org.junit.Test;

import eu.fox7.bonxai.bonxai.*;
import static org.junit.Assert.*;

/**
 *
 */
public class UniqueConstraintTest extends junit.framework.TestCase {

    public UniqueConstraintTest() {
    }

    /**
     * Test of getAncestorPattern method, of class UniqueConstraint.
     */
    @Test
    public void testGetAncestorPattern() {
        DoubleSlashPrefixElement particle = new DoubleSlashPrefixElement("namespace", "name");
        AncestorPattern ancestor          = new AncestorPattern(particle);
        UniqueConstraint constraint       = new UniqueConstraint(ancestor, "/", new HashSet<String>());
        assertSame(ancestor, constraint.getAncestorPattern());
    }

    /**
     * Test of getAncestorPattern method, of class UniqueConstraint.
     */
    @Test
    public void testGetUniqueConstraintSelector() {
        DoubleSlashPrefixElement particle = new DoubleSlashPrefixElement("namespace", "name");
        AncestorPattern ancestor          = new AncestorPattern(particle);
        UniqueConstraint constraint       = new UniqueConstraint(ancestor, "/", new HashSet<String>());
        assertEquals("/", constraint.getConstraintSelector());
    }

    /**
     * Test of getAncestorPattern method, of class UniqueConstraint.
     */
    @Test
    public void testGetUniqueConstraintFields() {
        DoubleSlashPrefixElement particle = new DoubleSlashPrefixElement("namespace", "name");
        HashSet<String> fields            = new HashSet<String>();
        AncestorPattern ancestor          = new AncestorPattern(particle);
        UniqueConstraint constraint       = new UniqueConstraint(ancestor, "/", new HashSet<String>());
        assertEquals(fields, constraint.getConstraintFields());
    }
}
