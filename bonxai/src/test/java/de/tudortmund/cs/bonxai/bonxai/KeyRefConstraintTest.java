
package de.tudortmund.cs.bonxai.bonxai;

import java.util.Vector;
import java.util.HashSet;
import org.junit.Test;

import de.tudortmund.cs.bonxai.bonxai.*;
import static org.junit.Assert.*;

/**
 *
 */
public class KeyRefConstraintTest extends junit.framework.TestCase {

    public KeyRefConstraintTest() {
    }

    /**
     * Test of getAncestorPattern method, of class KeyRefConstraint.
     */
    @Test
    public void testGetAncestorPattern() {
        DoubleSlashPrefixElement particle = new DoubleSlashPrefixElement("namespace", "name");
        AncestorPattern ancestor          = new AncestorPattern(particle);
        KeyRefConstraint constraint       = new KeyRefConstraint("name", ancestor, "/", new HashSet<String>());
        assertSame(ancestor, constraint.getAncestorPattern());
    }

    /**
     * Test of getAncestorPattern method, of class KeyRefConstraint.
     */
    @Test
    public void testGetKeyRefConstraintSelector() {
        DoubleSlashPrefixElement particle = new DoubleSlashPrefixElement("namespace", "name");
        AncestorPattern ancestor          = new AncestorPattern(particle);
        KeyRefConstraint constraint       = new KeyRefConstraint("name", ancestor, "/", new HashSet<String>());
        assertEquals("/", constraint.getConstraintSelector());
    }

    /**
     * Test of getAncestorPattern method, of class KeyRefConstraint.
     */
    @Test
    public void testGetKeyRefConstraintFields() {
        DoubleSlashPrefixElement particle = new DoubleSlashPrefixElement("namespace", "name");
        HashSet<String> fields            = new HashSet<String>();
        AncestorPattern ancestor          = new AncestorPattern(particle);
        KeyRefConstraint constraint       = new KeyRefConstraint("name", ancestor, "/", new HashSet<String>());
        assertEquals(fields, constraint.getConstraintFields());
    }

    /**
     * Test of getAncestorPattern method, of class KeyRefConstraint.
     */
    @Test
    public void testGetReference() {
        DoubleSlashPrefixElement particle = new DoubleSlashPrefixElement("namespace", "name");
        HashSet<String> fields            = new HashSet<String>();
        AncestorPattern ancestor          = new AncestorPattern(particle);
        KeyRefConstraint constraint       = new KeyRefConstraint("name", ancestor, "/", new HashSet<String>());
        assertEquals("name", constraint.getReference());
    }
}
