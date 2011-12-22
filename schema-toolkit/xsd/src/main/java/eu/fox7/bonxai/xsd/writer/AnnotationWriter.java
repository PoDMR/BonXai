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
package eu.fox7.bonxai.xsd.writer;

import org.w3c.dom.Node;

import eu.fox7.bonxai.common.Annotationable;
import eu.fox7.bonxai.xsd.*;

/**
 * @author Dominik Wolff, Lars Schmidt
 */
public abstract class AnnotationWriter {

    protected static void writeAnnotation(Node root, Annotationable annotationable, XSDSchema schema) {
        if (annotationable.getAnnotation() != null) {
            org.w3c.dom.Element annotNode = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "annotation");
            DOMHelper.setXSDPrefix(annotNode, schema);
            for (int i = 0; i < annotationable.getAnnotation().getDocumentations().size(); i++) {
                Documentation documentation = annotationable.getAnnotation().getDocumentations().get(i);
                org.w3c.dom.Element docNode = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "documentation");
                DOMHelper.setXSDPrefix(docNode, schema);
                if (documentation.getSource() != null) {
                    docNode.setAttribute("source", documentation.getSource());
                }
                if (documentation.getXmlLang() != null) {
                    docNode.setAttribute("xml:lang", documentation.getXmlLang());
                }

                // Import the content from the nodeList of the Documentation to the new DOM-tree structure
                if (documentation.getNodeList() != null) {
                    for (int j = 0; j < documentation.getNodeList().getLength(); j++) {
                      docNode.appendChild(root.getOwnerDocument().importNode(documentation.getNodeList().item(j), true));
                    }
                }

                annotNode.appendChild(docNode);
            }
            for (int i = 0; i < annotationable.getAnnotation().getAppInfos().size(); i++) {
                AppInfo appInfo = annotationable.getAnnotation().getAppInfos().get(i);
                org.w3c.dom.Element appNode = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "appinfo");
                DOMHelper.setXSDPrefix(appNode, schema);
                if (appInfo.getSource() != null) {
                    appNode.setAttribute("source", appInfo.getSource());
                }

                // Import the content from the nodeList of the AppInfo to the new DOM-tree structure
                if (appInfo.getNodeList() != null) {
                    for (int j = 0; j < appInfo.getNodeList().getLength(); j++) {
                      appNode.appendChild(root.getOwnerDocument().importNode(appInfo.getNodeList().item(j), true));
                    }
                }

                annotNode.appendChild(appNode);
            }
            if (annotationable.getAnnotation().getId() != null) {
                annotNode.setAttribute("id", annotationable.getAnnotation().getId());
            }
            root.appendChild(annotNode);
        }
    }
}
