/**
 * This file is part of BonXai.
 *
 * BonXai is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BonXai is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.fox7.bonxai.xsd;

import eu.fox7.bonxai.common.SymbolTableRef;

import java.util.Iterator;
import java.util.LinkedList;


/**
 * Class representing a union of other simple types as a new simple type content.
 *
 * Used in {@link SimpleType}.
 */
public class SimpleContentUnion extends Inheritance implements SimpleTypeInheritance {

    
    /**
     * Member types contained in this union.
     */
    protected LinkedList<eu.fox7.bonxai.common.SymbolTableRef<Type>> memberTypes = new LinkedList<SymbolTableRef<Type>>();

    /**
     * Creates an instance with the given simpleTypes.
     * @param memberTypes
     */
    public SimpleContentUnion (LinkedList<eu.fox7.bonxai.common.SymbolTableRef<Type>> memberTypes) {
        super(null);
        this.memberTypes = memberTypes;
    }

    /**
     * Return all references to anonymous SimpleTypes.
     * @return HashSet<SimpleType>
     */
    public LinkedList<eu.fox7.bonxai.common.SymbolTableRef<Type>> getAnonymousSimpleTypes () {
        LinkedList<eu.fox7.bonxai.common.SymbolTableRef<Type>> resultingTypes = new LinkedList<SymbolTableRef<Type>>();
        for (Iterator<SymbolTableRef<Type>> it = memberTypes.iterator(); it.hasNext();) {
            SymbolTableRef<Type> symbolTableRef = it.next();
            if (symbolTableRef.getReference() instanceof SimpleType) {
                if (((SimpleType) symbolTableRef.getReference()).isAnonymous()) {
                    resultingTypes.add(symbolTableRef);
                }
            }
        }
        return resultingTypes;
    }

    /**
     * Return all references to SimpleTypes.
     * @return HashSet<SimpleType>
     */
    public LinkedList<eu.fox7.bonxai.common.SymbolTableRef<Type>> getMemberTypes () {
        LinkedList<eu.fox7.bonxai.common.SymbolTableRef<Type>> resultingTypes = new LinkedList<SymbolTableRef<Type>>();
        for (Iterator<SymbolTableRef<Type>> it = memberTypes.iterator(); it.hasNext();) {
            SymbolTableRef<Type> symbolTableRef = it.next();
            if (symbolTableRef.getReference() instanceof SimpleType) {
                if (!((SimpleType) symbolTableRef.getReference()).isAnonymous()) {
                    resultingTypes.add(symbolTableRef);
                }
            }
        }
        return resultingTypes;
    }
    

    /**
     * Adds a new SimpleType to the memberTypes.
     * @param val common.SymbolTableRef<Type>
     */
    public void addMemberType (eu.fox7.bonxai.common.SymbolTableRef<Type> val) {
        if (memberTypes.contains(val)) {
            throw new RuntimeException("Type " + val + " already contained in membertypelist of union.");
        } else {
            memberTypes.add(val);
        }
    }

    public LinkedList<SymbolTableRef<Type>> getAllMemberTypes() {
        return memberTypes;
    }



}
