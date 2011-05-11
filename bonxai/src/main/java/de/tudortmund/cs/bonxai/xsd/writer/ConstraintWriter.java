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

import java.util.Iterator;
import org.w3c.dom.Node;
import de.tudortmund.cs.bonxai.xsd.*;

public abstract class ConstraintWriter {

    /**
     * Writes the (Simple)Constraint node (unique, key or keyref).
     */
    protected static void writeConstraint(Node root, Constraint constraint, FoundElements foundElements) {
        if (constraint instanceof SimpleConstraint) {
            SimpleConstraint simpleConstraint = (SimpleConstraint) constraint;
            String nodeName = "";
            if (simpleConstraint instanceof Unique) {
                nodeName = "unique";
            } else if (simpleConstraint instanceof Key) {
                nodeName = "key";
            } else if (simpleConstraint instanceof KeyRef) {
                nodeName = "keyref";
            }
            org.w3c.dom.Element simpleConstraintNode = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", nodeName);
            foundElements.setXSDPrefix(simpleConstraintNode);
            if (nodeName.equals("key")) {
                Key key = (Key) simpleConstraint;
                simpleConstraintNode.setAttribute("name", key.getLocalName());
            } else {
                simpleConstraintNode.setAttribute("name", simpleConstraint.getLocalName());
            }
            org.w3c.dom.Element selectorNode = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "selector");
            foundElements.setXSDPrefix(selectorNode);
            selectorNode.setAttribute("xpath", simpleConstraint.getSelector());
            simpleConstraintNode.appendChild(selectorNode);
            Iterator<String> iterator = simpleConstraint.getFields().iterator();
            while (iterator.hasNext()) {
                org.w3c.dom.Element fieldNode = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "field");
                foundElements.setXSDPrefix(fieldNode);
                fieldNode.setAttribute("xpath", iterator.next());
                simpleConstraintNode.appendChild(fieldNode);
            }
            if (nodeName.equals("keyref")) {
                KeyRef keyRef = (KeyRef) simpleConstraint;
                simpleConstraintNode.setAttribute("refer", foundElements.getPrefix(keyRef.getKeyOrUnique().getNamespace()) + keyRef.getKeyOrUnique().getLocalName());
            }
            AnnotationWriter.writeAnnotation(simpleConstraintNode, simpleConstraint, foundElements);
            if (constraint.getId() != null) {
                simpleConstraintNode.setAttribute("id", constraint.getId());
            }
            root.appendChild(simpleConstraintNode);
        }
    }

    
}
