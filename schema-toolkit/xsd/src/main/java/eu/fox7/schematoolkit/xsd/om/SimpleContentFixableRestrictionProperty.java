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
/**
 * All of these information pieces are stored using the
 * {@code SimpleContentRestrictionProperty} and its
 * derivative {@code SimpleContentFixableRestrictionProperty}.
 * These classes are simple information containers for
 * arbitrary datatypes, with the option to define the value
 * to be fixed with the {@code SimpleContentFixableRestrictionProperty}.
 * This is needed because XSD allows certain of the stored
 * values to acompanied by an optional {@code fixed}
 * attribute.
 *
 * @param <T>
 */

public class SimpleContentFixableRestrictionProperty < T > extends
SimpleContentRestrictionProperty<T> {

    protected boolean fixed;

    /**
     * Creates an instance of Simple Content Restriction
     * Property with the passed value and boolean fixed.
     * @param value <T>
     * @param fixed Boolean
     */
    public SimpleContentFixableRestrictionProperty (T value, boolean fixed) {
        super(value);
        this.fixed = fixed;
    }

    /**
     * Returns whether the information is fixed or not.
     * @return Boolean
     */
    public Boolean getFixed () {
        return fixed;
    }

    /**
     * Set whether the information is fixed or not.
     * @param fixed Boolean
     */
    public void setFixed (boolean fixed) {
        this.fixed = fixed;
    }
}
