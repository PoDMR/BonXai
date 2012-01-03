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

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.fox7.schematoolkit.common.IdentifiedNamespace;
import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

public abstract class DOMHelper
{
    /**
     * Searches the given element recursively for an Element with the given name.
     *
     * returns null if no such Element is found.
     *
     * @param root
     * @param name
     * @return
     */
    protected static Node findChildNode(Node root, String name)
    {
        Node ret = null, tmpNode;
        NodeList list;

        list = root.getChildNodes();
        for(int i = 0; i < list.getLength(); i++)
        {
            tmpNode = list.item(i);
            if(tmpNode.getNodeName().equals(name))
            {
                ret = tmpNode;
                break;
            } else
            {
                ret = findChildNode((Element)tmpNode, name);
                if(ret != null)
                    break;
            }
        }
        return ret;
    }

    /**
     * Searches the childNodes for a Node which has an Attribute with the given Name
     * and the given value. If no node is found, null is returned.
     *
     * @param root
     * @param attrName
     * @param attrVal
     * @return
     */
    protected static Node findByAttribute(Node root, String attrName, String attrVal)
    {

        Node ret = null, tmpNode;
        NodeList list;

        list = root.getChildNodes();
        for(int i = 0; i < list.getLength(); i++)
        {
            tmpNode = list.item(i);
            if(tmpNode.hasAttributes()
                    && tmpNode.getAttributes().getNamedItem(attrName) != null
                    && tmpNode.getAttributes().getNamedItem(attrName).getNodeValue().equals(attrVal))
            {
                ret = tmpNode;
                break;
            } else
            {
                ret = findByAttribute(tmpNode, attrName, attrVal);
                if(ret != null)
                    break;
            }
        }
        return ret;
    }
    
    protected static void setXSDPrefix(Node node, XSDSchema schema) {
    	Namespace namespace = schema.getNamespaceByURI("http://www.w3.org/2001/XMLSchema");
    	if (namespace != null) {
    		node.setPrefix(namespace.getIdentifier());
    	}
    }
}
