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
 * Exception thrown if a different kind of XSD type was expected.
 *
 * This exception is thrown if an Attribute receives a ComplexType instead of a
 * SimpleType.
 */
class UnexpectedTypeException extends RuntimeException
{
    /**
     *
     */
    private static final long serialVersionUID = 4544530718077800352L;

    /**
     * Creates a new exception for the actual type, while another Type was expected.
     */
    public UnexpectedTypeException(Type actual, String expected)
    {
        super(
            "Unexpected type '" + actual.getClass() + "' expected '" + expected + "'"
        );
    }
}
