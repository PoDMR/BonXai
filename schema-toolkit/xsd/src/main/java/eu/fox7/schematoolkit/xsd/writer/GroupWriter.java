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
package eu.fox7.schematoolkit.xsd.writer;

import org.w3c.dom.Node;

import eu.fox7.schematoolkit.xsd.om.Group;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

public abstract class GroupWriter {

    protected static void writeGroup(Node root, Group group, XSDSchema schema) {
        org.w3c.dom.Element groupNode = root.getOwnerDocument().createElementNS(
                "http://www.w3.org/2001/XMLSchema", "group");
        DOMHelper.setXSDPrefix(groupNode, schema);
        groupNode.setAttribute("name", group.getName().getName());
        AnnotationWriter.writeAnnotation(groupNode, group, schema);
        if (group.getId() != null) {
            groupNode.setAttribute("id", group.getId());
        }
        root.appendChild(groupNode);
        ParticleWriter.writeParticle(groupNode, group.getParticle(), schema);
    }
}
