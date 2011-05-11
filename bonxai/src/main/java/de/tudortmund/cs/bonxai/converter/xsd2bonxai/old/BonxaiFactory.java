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

import java.util.LinkedList;

import de.tudortmund.cs.bonxai.bonxai.Bonxai;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;

/**
 * Main class to create Bonxai from XSD.
 *
 * This is the main conversion class, that creates an {@link de.tudortmund.cs.bonxai.bonxai.Bonxai} from an
 * {@link de.tudortmund.cs.bonxai.xsd.XSDSchema}.
 *
 * The conversion is defined modulary using dependency injection, through the
 * {@link #addConverter} method. This method is called an arbitrary time with
 * instances of classes that implement the {@link Bonxai2XSDConverter} interface. Each of
 * these converters is applied to the newly created {@link de.tudortmund.cs.bonxai.bonxai.Bonxai}, given
 * the {@link de.tudortmund.cs.bonxai.xsd.XSDSchema} to convert and the {@link TypeAutomaton} of this
 * schema.
 *
 * @see Bonxai2XSDConverter
 * @see TypeAutomaton
 *
 */
class BonxaiFactory {

    /**
     * List of converters to be applied in {@link #convert}.
     */
    private LinkedList<Converter> converters = new LinkedList<Converter>();

    /**
     * Add a converter to be applied in {@link #convert}.
     *
     * Through this method, converters are added, which should be applied in
     * the {@link #convert} method.
     *
     */
    public void addConverter( Converter converter ) {
        this.converters.add(converter);
    }

    /**
     * Converts the given {@link de.tudortmund.cs.bonxai.xsd.XSDSchema} to an {@link de.tudortmund.cs.bonxai.bonxai.Bonxai}.
     *
     * This method receives a schema and its corresponding {@link
     * TypeAutomaton} and returns a new instance of {@link de.tudortmund.cs.bonxai.bonxai.Bonxai}.
     *
     * A new instance of {@link de.tudortmund.cs.bonxai.bonxai.Bonxai} is created inside the method and all
     * {@link Bonxai2XSDConverter}s registered with this object are applied. The {@link
     * de.tudortmund.cs.bonxai.bonxai.Bonxai} instance is returned afterwards.
     *
     * Note that the given {@link de.tudortmund.cs.bonxai.xsd.XSDSchema} must have already been run
     * through the {@link PreProcessor}. Otherwise, this method might fail to
     * convert all information.
     */
    public Bonxai convert( XSDSchema schema, TypeAutomaton typeAutomaton ) {
        Bonxai bonxai = new Bonxai();
        for (Converter converter : this.converters) {
            bonxai = converter.convert(schema, typeAutomaton, bonxai);
        }
        return bonxai;
    }
}
