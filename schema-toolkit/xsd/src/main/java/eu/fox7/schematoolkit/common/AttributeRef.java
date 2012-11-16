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

package eu.fox7.schematoolkit.common;


/**
 * The AttributeRef class stores a common.SymbolTableRef object to represent a
 * reference to an attribute.
 */
public class AttributeRef extends AbstractAttribute {
    public AttributeRef(QualifiedName attributeName, String defaultValue, String fixedValue, AttributeUse use, Annotation annotation) {
        super(attributeName, defaultValue, fixedValue, use);
        this.setAnnotation(annotation);
    }

    /**
     * Creates an AttributeRef object with the passed symbol table reference.
     */
    public AttributeRef (QualifiedName attributeName) {
        super(attributeName);
    }

	public AttributeRef(QualifiedName attributeName, String fixedValue,
			String defaultValue, boolean required) {
		this(attributeName, fixedValue, defaultValue, required?AttributeUse.required:AttributeUse.optional, null);
	}
}
