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
package eu.fox7.bonxai.xsd;

import org.w3c.dom.NodeList;

/**
 * Class representing a XSD-documentation-tag.
 * This is a direct child of an Annotation.
 * @author Dominik Wolff, Lars Schmidt
 */
public class AppInfo {

    String source;

    // A NodeList for the content of this documentation-tag.
    // TextContent is handled by #text-nodes.
    NodeList nodeList;

    /**
     * Getter for the source-String of this documentation-object.
     * The source represents an anyURI of XSD.
     * An anyURI value can be absolute or relative, and may have an optional
     * fragment identifier (i.e., it may be a URI Reference).
     * @return String
     */
    public String getSource() {
        return source;
    }

    /**
     * Setter for the source-String of this documentation-object.
     * The source represents an anyURI of XSD.
     * @param source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Setter of the attribute nodeList.
     * @param nodeList
     */
    public void setNodeList(NodeList nodeList) {
        this.nodeList = nodeList;
    }

    /**
     * Getter for the attribute nodeList holding a List of DOM-nodes for later
     * management or the writer
     * @return NodeList
     */
    public NodeList getNodeList() {
        return this.nodeList;
    }
}
