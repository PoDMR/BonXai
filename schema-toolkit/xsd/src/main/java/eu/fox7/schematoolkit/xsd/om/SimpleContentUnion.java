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

package eu.fox7.schematoolkit.xsd.om;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import eu.fox7.schematoolkit.common.QualifiedName;


/**
 * Class representing a union of other simple types as a new simple type content.
 *
 * Used in {@link SimpleType}.
 */
public class SimpleContentUnion extends Inheritance implements SimpleTypeInheritance {
    /**
     * Member types contained in this union.
     */
    protected Collection<QualifiedName> memberTypes = new LinkedList<QualifiedName>();

    public SimpleContentUnion() {}
    
    /**
     * Creates an instance with the given simpleTypes.
     * @param memberTypes
     */
    public SimpleContentUnion (Collection<QualifiedName> memberTypes) {
        super(null);
        this.memberTypes = memberTypes;
    }

    /**
     * Adds a new SimpleType to the memberTypes.
     * @param val common.SymbolTableRef<Type>
     */
    public void addMemberType (QualifiedName val) {
        if (memberTypes.contains(val)) {
            throw new RuntimeException("Type " + val + " already contained in membertypelist of union.");
        } else {
            memberTypes.add(val);
        }
    }

    public Collection<QualifiedName> getMemberTypes() {
        return memberTypes;
    }
}
