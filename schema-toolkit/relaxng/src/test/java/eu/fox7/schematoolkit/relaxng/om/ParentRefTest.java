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

import java.util.LinkedList;
import org.junit.Test;

import eu.fox7.schematoolkit.relaxng.om.Define;
import eu.fox7.schematoolkit.relaxng.om.Empty;
import eu.fox7.schematoolkit.relaxng.om.Grammar;
import eu.fox7.schematoolkit.relaxng.om.ParentRef;
import static org.junit.Assert.*;

/**
 * Test of class ParentRef
 * @author Lars Schmidt
 */
public class ParentRefTest extends junit.framework.TestCase {

    /**
     * Test of getDefineList method, of class ParentRef.
     */
    @Test
    public void testGetDefineList() {
        Grammar grammar = new Grammar();
        Grammar grammar2 = new Grammar();
        grammar2.setParentGrammar(grammar);
        Define define = new Define("test");
        define.addPattern(new Empty());
        grammar.addDefinePattern(define);
        ParentRef instance = new ParentRef("test", grammar2);
        java.util.List<Define> result = instance.getDefineList();
        assertTrue(result.contains(define));
    }

}