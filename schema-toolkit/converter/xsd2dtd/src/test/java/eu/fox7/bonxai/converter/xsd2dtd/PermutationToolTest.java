/**
 * Copyright 2009-2012 TU Dortmund
 *
 * This file is part of FoXLib.
 *
 * FoXLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoXLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */

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
