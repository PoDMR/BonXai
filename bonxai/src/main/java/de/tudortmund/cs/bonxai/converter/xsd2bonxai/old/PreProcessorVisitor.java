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
package de.tudortmund.cs.bonxai.converter.xsd2bonxai.old;

import de.tudortmund.cs.bonxai.xsd.*;

/**
 * Interface which needs to be implemented by any Visitor used for
 * preprocessing.
 */
interface PreProcessorVisitor {
    /**
     * Called once for each visited schema
     */
    public void visitSchema( XSDSchema schema );

    /**
     * Called for each visited complex type in the processed schema.
     *
     * This method is called once for each complex type
     */
    public void visitComplexType( ComplexType type );
}
