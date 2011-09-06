
package eu.fox7.bonxai.bonxai;

import java.util.Vector;
import java.util.HashSet;
import org.junit.Test;

import eu.fox7.bonxai.bonxai.*;
import static org.junit.Assert.*;

/**
 *
 */
public class ConstraintListTest extends junit.framework.TestCase {

    public ConstraintListTest() {
    }

    /**
     * Test of getConstraints method, of class ConstraintList.
     */
    @Test
    public void testGetConstraints() {
        ConstraintList instance = new ConstraintList();
        Constraint constraint = new UniqueConstraint(new AncestorPattern(new SingleSlashPrefixElement("ns", "name")), "/", new HashSet<String>());
        instance.addConstraint(constraint);
        Vector<Constraint> expResult = new Vector<Constraint>();
        expResult.add(constraint);
        Vector<Constraint> result = instance.getConstraints();
        assertEquals(expResult, result);
    }

    /**
     * Test of addConstraint method, of class ConstraintList.
     */
    @Test
    public void testAddConstraint() {
        ConstraintList instance = new ConstraintList();
        Constraint constraint = new UniqueConstraint(new AncestorPattern(new SingleSlashPrefixElement("ns", "name")), "/", new HashSet<String>());
        instance.addConstraint(constraint);
        Vector<Constraint> expResult = new Vector<Constraint>();
        expResult.add(constraint);
        Vector<Constraint> result = instance.getConstraints();
        assertEquals(expResult, result);
    }
}
