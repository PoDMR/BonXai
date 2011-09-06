package eu.fox7.bonxai.converter.xsd2dtd;

import org.junit.Test;

import eu.fox7.bonxai.converter.xsd2dtd.PermutationTool;
import static org.junit.Assert.*;

/**
 * Test of class PermutationTool
 * @author Lars Schmidt
 */
public class PermutationToolTest extends junit.framework.TestCase {

    /**
     * Test of morePermutationsLeft method, of class PermutationTool.
     */
    @Test
    public void testMorePermutationsLeft() {
        PermutationTool instance = new PermutationTool(1);
        assertTrue(instance.morePermutationsLeft());
        instance.calculateNextPermutation();
        assertFalse(instance.morePermutationsLeft());
    }

    /**
     * Test of calculateNextPermutation method, of class PermutationTool.
     */
    @Test
    public void testCalculateNextPermutation() {
        PermutationTool instance = new PermutationTool(2);
        assertTrue(instance.morePermutationsLeft());
        int[] permutation = instance.calculateNextPermutation();
        assertEquals(0, permutation[0]);
        assertEquals(1, permutation[1]);

        permutation = instance.calculateNextPermutation();
        assertEquals(1, permutation[0]);
        assertEquals(0, permutation[1]);

        PermutationTool instance2 = new PermutationTool(3);
        assertTrue(instance2.morePermutationsLeft());
        permutation = instance2.calculateNextPermutation();
        assertEquals(0, permutation[0]);
        assertEquals(1, permutation[1]);
        assertEquals(2, permutation[2]);

        permutation = instance2.calculateNextPermutation();
        assertEquals(0, permutation[0]);
        assertEquals(2, permutation[1]);
        assertEquals(1, permutation[2]);

        permutation = instance2.calculateNextPermutation();
        assertEquals(1, permutation[0]);
        assertEquals(0, permutation[1]);
        assertEquals(2, permutation[2]);

        permutation = instance2.calculateNextPermutation();
        assertEquals(1, permutation[0]);
        assertEquals(2, permutation[1]);
        assertEquals(0, permutation[2]);

        permutation = instance2.calculateNextPermutation();
        assertEquals(2, permutation[0]);
        assertEquals(0, permutation[1]);
        assertEquals(1, permutation[2]);

        permutation = instance2.calculateNextPermutation();
        assertEquals(2, permutation[0]);
        assertEquals(1, permutation[1]);
        assertEquals(0, permutation[2]);
    }
}
