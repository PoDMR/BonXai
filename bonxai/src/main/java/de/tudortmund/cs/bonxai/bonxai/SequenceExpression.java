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
package de.tudortmund.cs.bonxai.bonxai;

import java.util.Vector;

/**
 * Class for representing sequences expressions in Bonxai
 */
public class SequenceExpression extends ContainerParticle {

    /**
     * Constructor for the class SequenceExpression
     * @param children
     */
    public SequenceExpression(Vector<AncestorPatternParticle> children) {
        super(children);
    }

    @Override
    public String toString() {
        String sequence = "";
        for (int i = 0; i < getChildren().size(); i++) {
            sequence += getChildren().get(i).toString();
        }
        return sequence;
    }
}