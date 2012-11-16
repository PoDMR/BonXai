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

import eu.fox7.schematoolkit.common.AnyPattern;
import eu.fox7.schematoolkit.common.ProcessContentsInstruction;

/**
 *
 */
public class AnyPatternTest extends junit.framework.TestCase {

    public AnyPatternTest() {
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
     * Test of getNamespace method, of class AnyPattern.
     */
    @Test
    public void testGetNamespace() {
        AnyPattern anypattern = new AnyPattern(null, "someNamespace");
        String expResult = "someNamespace";
        String result = anypattern.getNamespaces().iterator().next().getUri();
        assertEquals(expResult, result);
    }

    /**
     * Test of getProcessContentsInstruction method, of class AnyPattern.
     */
    @Test
    public void testGetProcessContentsInstruction() {
        AnyPattern anypattern = new AnyPattern(ProcessContentsInstruction.LAX, "someNamespace");
        ProcessContentsInstruction expResult = ProcessContentsInstruction.LAX;
        ProcessContentsInstruction result = anypattern.getProcessContentsInstruction();
        assertEquals(expResult, result);
    }

}
