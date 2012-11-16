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

import static org.junit.Assert.*;

/**
 *
 */
public class DataTypeTest extends junit.framework.TestCase {

    public DataTypeTest() {
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
     * Test of getUri method, of class DataType.
     */
    @Test
    public void testGetUri() {
        DataType instance = new DataType("identifier", "uri");
        String expResult = "uri";
        String result = instance.getUri();
        assertEquals(expResult, result);
    }

    /**
     * Test of getIdentifier method, of class DataType.
     */
    @Test
    public void testGetIdentifier() {
        DataType instance = new DataType("identifier", "uri");
        String expResult = "identifier";
        String result = instance.getIdentifier();
        assertEquals(expResult, result);
    }
}
