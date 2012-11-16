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

/**
 * An enumeration of possible values for the Relax NG combine method of start or
 * define elements.
 *
 * It is important, that all those elements with the same name share the same
 * combine method.
 *
 * @author Lars Schmidt
 */
public enum CombineMethod {

    /**
     * All start or define elements with the same name are combined in the
     * form of a choice.
     */
    choice,
    /**
     * All start or define elements with the same name are combined with the
     * interleaving operator.
     */
    interleave,
}
