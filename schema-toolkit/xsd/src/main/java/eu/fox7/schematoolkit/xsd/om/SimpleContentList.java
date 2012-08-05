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
package eu.fox7.schematoolkit.xsd.om;

import eu.fox7.schematoolkit.common.QualifiedName;

public class SimpleContentList extends Inheritance implements SimpleTypeInheritance {

    /**
     * The constructor with the paramater base creates
     * an instance with the passed base as source type.
     * @param base
     */
    public SimpleContentList(QualifiedName baseType) {
        super(baseType);
    }

    /**
     * The constructor without any paramater is used for a List in which a new
     * simpleType is defined locally as direct child
     */
    public SimpleContentList() {
        super();
    }


}

