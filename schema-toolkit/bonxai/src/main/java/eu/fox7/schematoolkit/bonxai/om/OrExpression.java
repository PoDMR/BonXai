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
package eu.fox7.schematoolkit.bonxai.om;

import java.util.List;

/**
 * Class for representing or expressions in Bonxai
 */
public class OrExpression extends ContainerParticle {

    /**
     * Constructor for the class OrExpression
     * @param children
     */
    public OrExpression(List<AncestorPattern> children) {
        super(children);
    }

    public OrExpression() {
	}

	@Override
    public String toString() {
        String or = "";
        for (AncestorPattern aPattern: getChildren()) {
            or += "(" + aPattern.toString() + ")|" ;
        }
        return or.substring(0, or.length()-1);
    }

}

