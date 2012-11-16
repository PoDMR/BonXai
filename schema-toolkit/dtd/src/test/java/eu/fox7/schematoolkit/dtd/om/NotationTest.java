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

package eu.fox7.schematoolkit.dtd.om;

import org.junit.Before;
import org.junit.Test;

import eu.fox7.schematoolkit.dtd.om.DocumentTypeDefinition;
import eu.fox7.schematoolkit.dtd.om.PublicNotation;
import eu.fox7.schematoolkit.dtd.om.SystemNotation;

/**
 * Test of classes PublicNotation and SystemNotation
 * @author Lars Schmidt
 */
public class NotationTest extends junit.framework.TestCase {

    private DocumentTypeDefinition dtd;

    @Before
    @Override
    public void setUp() {
        this.dtd = new DocumentTypeDefinition();
    }

    /**
     * Test of getName method, of class Notation.
     */
    @Test
    public void testGetName() {
        SystemNotation instance = new SystemNotation("name", "systemID");
        String expResult = "name";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getIdentifier method, of class PublicNotation.
     */
    @Test
    public void testGetIdentifierPublic() {
        PublicNotation instance = new PublicNotation("name", "publicID");
        String expResult = "publicID";
        String result = instance.getIdentifier();
        assertEquals(expResult, result);
    }

    /**
     * Test of getIdentifier method, of class SystemNotation.
     */
    @Test
    public void testGetIdentifierSystem() {
        SystemNotation instance = new SystemNotation("name", "systemID");
        String expResult = "systemID";
        String result = instance.getIdentifier();
        assertEquals(expResult, result);
    }
}