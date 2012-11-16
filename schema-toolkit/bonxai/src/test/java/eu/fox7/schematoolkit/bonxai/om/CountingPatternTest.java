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

package eu.fox7.schematoolkit.bonxai.om;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.fox7.schematoolkit.common.CountingPattern;
import static org.junit.Assert.*;

/**
 *
 */
public class CountingPatternTest extends junit.framework.TestCase {

    public CountingPatternTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getMax method, of class CountingPattern.
     */
    @Test
    public void testGetMax() {
        Integer expResult = 10;
        CountingPattern instance = new CountingPattern(null, 5, expResult);
        Integer result = instance.getMax();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMin method, of class CountingPattern.
     */
    @Test
    public void testGetMin() {
        Integer expResult = 10;
        CountingPattern instance = new CountingPattern(null, expResult, 5);
        Integer result = instance.getMin();
        assertEquals(expResult, result);
    }

}
