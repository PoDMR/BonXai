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
import eu.fox7.schematoolkit.dtd.om.InternalEntity;

/**
 * Test of class InternalEntity
 * @author Lars Schmidt
 */
public class InternalEntityTest extends junit.framework.TestCase {

    private DocumentTypeDefinition dtd;

    @Before
    @Override
    public void setUp() {
        this.dtd = new DocumentTypeDefinition();
    }


    /**
     * Test of getValue method, of class InternalEntity.
     */
    @Test
    public void testGetValue() {
        InternalEntity instance = new InternalEntity("name", "value");
        String expResult = "value";
        String result = instance.getValue();
        assertEquals(expResult, result);
    }

    /**
     * Test of setValue method, of class InternalEntity.
     */
    @Test
    public void testSetValue() {
        InternalEntity instance = new InternalEntity("name", "value");
        assertEquals("value", instance.getValue());
        instance.setValue("new value");
        assertEquals("new value", instance.getValue());
    }

}