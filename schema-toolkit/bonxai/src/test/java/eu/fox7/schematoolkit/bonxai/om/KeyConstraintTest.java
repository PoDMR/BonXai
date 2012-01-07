
package eu.fox7.schematoolkit.bonxai.om;

import java.util.Vector;
import java.util.HashSet;
import org.junit.Test;

import eu.fox7.bonxai.bonxai.*;
import static org.junit.Assert.*;

/**
 *
 */
public class KeyConstraintTest extends junit.framework.TestCase {

    public KeyConstraintTest() {
    }

    /**
     * Test of getAncestorPattern method, of class KeyConstraint.
     */
    @Test
    public void testGetAncestorPattern() {
        DoubleSlashPrefixElement particle = new DoubleSlashPrefixElement("namespace", "name");
        AncestorPattern ancestor          = new AncestorPattern(particle);
        KeyConstraint constraint          = new KeyConstraint("name", ancestor, "/", new HashSet<String>());
        assertSame(ancestor, constraint.getAncestorPattern());
    }

    /**
     * Test of getAncestorPattern method, of class KeyConstraint.
     */
    @Test
    public void testGetKeyConstraintSelector() {
        DoubleSlashPrefixElement particle = new DoubleSlashPrefixElement("namespace", "name");
        AncestorPattern ancestor          = new AncestorPattern(particle);
        KeyConstraint constraint          = new KeyConstraint("name", ancestor, "/", new HashSet<String>());
        assertEquals("/", constraint.getConstraintSelector());
    }

    /**
     * Test of getAncestorPattern method, of class KeyConstraint.
     */
    @Test
    public void testGetKeyConstraintFields() {
        DoubleSlashPrefixElement particle = new DoubleSlashPrefixElement("namespace", "name");
        HashSet<String> fields            = new HashSet<String>();
        AncestorPattern ancestor          = new AncestorPattern(particle);
        KeyConstraint constraint          = new KeyConstraint("name", ancestor, "/", new HashSet<String>());
        assertEquals(fields, constraint.getConstraintFields());
    }

    /**
     * Test of getAncestorPattern method, of class KeyConstraint.
     */
    @Test
    public void testGetName() {
        DoubleSlashPrefixElement particle = new DoubleSlashPrefixElement("namespace", "name");
        HashSet<String> fields            = new HashSet<String>();
        AncestorPattern ancestor          = new AncestorPattern(particle);
        KeyConstraint constraint          = new KeyConstraint("name", ancestor, "/", new HashSet<String>());
        assertEquals("name", constraint.getName());
    }
}
