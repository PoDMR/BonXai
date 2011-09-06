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
package eu.fox7.bonxai.utils.exceptions;
public class UPAViolationException extends InvalidContentModelException {

    /**
     *
     */
    private static final long serialVersionUID = -3676212491815116010L;

    public UPAViolationException(String name1, String name2) {
        super(name1 + " and " + name2 + " violate the Unique Particle Attribution constraint. During validation against this schema, ambiguity would be created for those two particles." );
    }

}
