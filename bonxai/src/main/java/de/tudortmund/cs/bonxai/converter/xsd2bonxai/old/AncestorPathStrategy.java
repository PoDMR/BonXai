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

import de.tudortmund.cs.bonxai.bonxai.AncestorPattern;
import java.util.HashMap;

/**
 * Interface for ancestor-path generation Strategys.
 *
 * This interface must be implemented by all ancestor-path generation Strategys
 * and will be used in the ExpressionsConverter {@link ExpressionsConverter}.
 * An instance of a class implementing this interface has to generate ancestor-
 * paths for each state of the type automaton, so that each XSD ComplexType is
 * associated with a correct ancestor-path in a Bonxai expression..
 * Ancestor-path length and legibility may vary between implementations but they
 * should always be correct.
 */
public interface AncestorPathStrategy {

   /**
    * The calculate method receives an automaton and generates a hashmap holding
    * ancestor-patterns as values for each state of the type automaton.
    *
    * Neither a key nor a value of the hashmap is null.
    * For every state of the automaton, there is a key in the hashmap.
    *
    * @param automaton          type automaton, which was generated to find the
    *                           ancestor-path for each type
    * @return hashmap with types as keys and ancestor-paths as values
    */
    public HashMap<TypeAutomatonState, AncestorPattern> calculate(TypeAutomaton automaton);
}
