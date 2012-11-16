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

package eu.fox7.schematoolkit.relaxng.om;

import org.junit.Test;

import eu.fox7.schematoolkit.relaxng.om.ExternalRef;

/**
 * Test of class ExternalRef
 * @author Lars Schmidt
 */
public class ExternalRefTest extends junit.framework.TestCase {

    /**
     * Test of getHref method, of class ExternalRef.
     */
    @Test
    public void testGetHref() {
        ExternalRef instance = new ExternalRef("http://www.myDomain.org/rng.rng");
        String expResult = "http://www.myDomain.org/rng.rng";
        String result = instance.getHref();
        assertEquals(expResult, result);
    }

    /**
     * Test of setHref method, of class ExternalRef.
     */
    @Test
    public void testSetHref() {
        ExternalRef instance = new ExternalRef("http://www.myDomain.org/rng.rng");
        instance.setHref("http://www.someOtherDomain.com/rng.rng");
        String expResult = "http://www.myDomain.org/rng.rng";
        String result = instance.getHref();
        assertEquals("http://www.someOtherDomain.com/rng.rng", result);
    }

}