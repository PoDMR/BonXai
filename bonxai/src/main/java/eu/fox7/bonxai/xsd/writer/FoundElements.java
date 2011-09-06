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

import java.util.Hashtable;
import java.util.LinkedList;

import org.w3c.dom.Node;

import eu.fox7.bonxai.common.IdentifiedNamespace;
import eu.fox7.bonxai.common.NamespaceList;
import eu.fox7.bonxai.xsd.AttributeGroup;
import eu.fox7.bonxai.xsd.Constraint;
import eu.fox7.bonxai.xsd.Type;

class FoundElements {

    private LinkedList<Type> types;
    private LinkedList<eu.fox7.bonxai.xsd.Element> elements;
    private LinkedList<AttributeGroup> attributeGroups;
    private LinkedList<eu.fox7.bonxai.common.Group> groups;
    private LinkedList<Constraint> constraints;

    protected Hashtable<String, String> namespaces;

    private NamespaceList namespaceList;

    protected FoundElements()
    {
        types = new LinkedList<Type>();
        elements = new LinkedList<eu.fox7.bonxai.xsd.Element>();
        attributeGroups = new LinkedList<AttributeGroup>();
        groups = new LinkedList<eu.fox7.bonxai.common.Group>();
        constraints = new LinkedList<Constraint>();
        namespaces = new Hashtable<String, String>();
    }

    public void addType(Type t)
    {
        types.add(t);
    }

    public void addElement(eu.fox7.bonxai.xsd.Element e)
    {
        elements.add(e);
    }

    public void addAttributeGroup(AttributeGroup ag)
    {
        attributeGroups.add(ag);
    }

    public void addGroup(eu.fox7.bonxai.common.Group g)
    {
        groups.add(g);
    }

    public void addConstraint(Constraint c)
    {
        constraints.add(c);
    }



    /**
     * Checks if the Type has already been added. The check is done by comparing
     * the object.
     *
     * @param t
     * @return
     */
    public boolean containsType(Type t)
    {
        return types.contains(t);
    }

    /**
     * Checks if the Element has already been added. The check is done by comparing
     * the object.
     *
     * @param e
     * @return
     */
    public boolean containsElement(eu.fox7.bonxai.xsd.Element e)
    {
        return elements.contains(e);
    }


    /**
     * Checks if the AttributeGroup has already been added. The check is done by comparing
     * the object.
     * @param ag
     * @return
     */
    public boolean containsAttributeGroup(AttributeGroup ag)
    {
        return attributeGroups.contains(ag);
    }

    /**
     * Checks if the Group has already been added. The check is done by comparing
     * the object.
     *
     * @param g
     * @return
     */
    public boolean containsGroup(eu.fox7.bonxai.common.Group g)
    {
        return groups.contains(g);
    }


    /**
     * Checks if the Constraint has already been added. The check is done by comparing
     * the object.
     *
     * @param c
     * @return
     */
    public boolean containsConstraint(Constraint c)
    {
        return constraints.contains(c);
    }

    /**
     * Replaces the namespace list.
     * @param namespaceList
     */
    protected void setNamespaceList(NamespaceList namespaceList) {
        this.namespaceList = namespaceList;
    }

    /**
     * Returns the namespace list.
     * @param namespaceList
     */
    protected NamespaceList getNamespaceList() {
        return namespaceList;
    }

    /**
     * Returns a namespace prefix with a colon at the end or an empty
     * string if there is no identifier for the given uri in the namespace
     * list.
     * @param uri
     * @return
     */
    protected String getPrefix(String uri) {
        String prefix = "";
        if (namespaceList!=null && !uri.isEmpty()) {
            if(uri.equals(namespaceList.getDefaultNamespace().getUri()) == false && namespaceList.getNamespaceByUri(uri) != null)
            {
                IdentifiedNamespace identifiedNamespace = namespaceList.getNamespaceByUri(uri);
                if (identifiedNamespace!=null) {
                    prefix = identifiedNamespace.getIdentifier()+":";
                }
            }
        }
        return prefix;
    }

    /**
     * Sets the prefix of the passed node to the identifier of the namespace
     * "http://www.w3.org/2001/XMLSchema" in namespaceList (or does nothing
     * if there is no prefix for the XML XSDSchema namespace).
     * @param root
     */
    protected void setXSDPrefix (Node root) {
        if (namespaceList!=null) {
            if (namespaceList.getNamespaceByUri("http://www.w3.org/2001/XMLSchema")!=null) {
                root.setPrefix(namespaceList.getNamespaceByUri("http://www.w3.org/2001/XMLSchema").getIdentifier());
            }
        }
    }
}
