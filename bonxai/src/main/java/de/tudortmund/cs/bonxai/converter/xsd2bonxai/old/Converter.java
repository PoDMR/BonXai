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

import de.tudortmund.cs.bonxai.bonxai.Bonxai;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;

/**
 * Basic converter interface.
 *
 * This interface must be implemented by all converters to be used by the
 * {@link BonxaiFactory}. An instance of a class implementing this interface
 * convertes certain elements of a given {@link de.tudortmund.cs.bonxai.xsd.XSDSchema} into the given
 * {@link Bonxai}. The converter might utilize the given {@link TypeAutomaton} in
 * addition. The given Bonxai instance is modified directly by the {@link
 * #convert} method and must not be cloned.
 *
 */
interface Converter {

    /*
     * Creates a new converter.
     *
     * Indicate that an empty constructor must be used for Converters. But
     * constructors may not be declared in interfaces in Java.
     *
     */
    // public Bonxai2XSDConverter();

    /**
     * Performs the conversion realized by the converter.
     *
     * Takes the incoming schme and automaton and converts all elements
     * affected by this converter to the given bonxai. Returns the given bonxai
     * instance, for convenience reasons.
     *
     */
    public Bonxai convert( XSDSchema schema, TypeAutomaton automaton, Bonxai bonxai );
}
