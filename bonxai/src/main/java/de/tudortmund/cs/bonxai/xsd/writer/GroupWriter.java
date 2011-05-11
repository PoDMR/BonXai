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
package de.tudortmund.cs.bonxai.xsd.writer;

import org.w3c.dom.Node;
import de.tudortmund.cs.bonxai.xsd.Group;

public abstract class GroupWriter {

    protected static void writeGroup(Node root, Group group, FoundElements foundElements) {
        org.w3c.dom.Element groupNode = root.getOwnerDocument().createElementNS(
                "http://www.w3.org/2001/XMLSchema", "group");
        foundElements.setXSDPrefix(groupNode);
        groupNode.setAttribute("name", group.getLocalName());
        AnnotationWriter.writeAnnotation(groupNode, group, foundElements);
        if (group.getId() != null) {
            groupNode.setAttribute("id", group.getId());
        }
        root.appendChild(groupNode);
        ParticleWriter.writeParticleContainer(groupNode, group.getParticleContainer(), foundElements);
    }
}
