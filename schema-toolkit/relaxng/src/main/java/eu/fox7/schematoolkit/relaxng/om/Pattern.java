/**
 * Copyright 2009-2012 TU Dortmund
 *
 * This file is part of FoXLib.
 *
 * FoXLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoXLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.fox7.schematoolkit.relaxng.om;

import eu.fox7.schematoolkit.common.AnonymousNamespace;
import eu.fox7.schematoolkit.common.DefaultNamespace;
import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.NamespaceList;

/**
 * Abstract class representing a RELAX NG pattern with the standard XML attributes
 * @author Lars Schmidt
 */
public abstract class Pattern {

    /**
     * Variable holding the anyURI of the attributeDatatypeLibrary
     */
    protected String attributeDatatypeLibrary;
    /**
     * Variable holding the namespace attribute ("ns")
     */
    protected Namespace attributeNamespace;
    /**
     * Variable holding the list of identified namespaces
     */
    protected NamespaceList namespaceList;

    /**
     * Constructor of class Pattern
     * @param attributeDatatypeLibrary      anyURI of the attributeDatatypeLibrary
     * @param attributeNamespace            namespace attribute ("ns")
     * @param namespaceList                 list of identified namespaces
     */
    public Pattern(String attributeDatatypeLibrary, String attributeNamespace, NamespaceList namespaceList) {
        this.attributeDatatypeLibrary = attributeDatatypeLibrary;
        this.attributeNamespace = new AnonymousNamespace(attributeNamespace);
        this.namespaceList = namespaceList;
    }

    /**
     * Constructor of class Pattern
     */
    public Pattern() {
        this.namespaceList = new NamespaceList(null);
    }

    /**
     * Getter of the XML attribute datatypeLibrary.
     * @return String   A string containing the value of the datatypeLibrary
     */
    public String getAttributeDatatypeLibrary() {
        return this.attributeDatatypeLibrary;
    }

    /**
     * Setter of the XML attribute datatypeLibrary.
     * @param datatypeLibrary
     */
    public void setAttributeDatatypeLibrary(String datatypeLibrary) {
        this.attributeDatatypeLibrary = datatypeLibrary;
    }

    /**
     * Getter of the XML attribute namespace.
     * @return String   A string containing the value of the namespace
     */
    public Namespace getAttributeNamespace() {
        return this.attributeNamespace;
    }

    /**
     * Setter of the XML attribute namespace.
     * @param namespace
     */
    public void setAttributeNamespace(String namespace) {
        this.attributeNamespace = new AnonymousNamespace(namespace);
    }

    /**
     * Setter of the XML attribute namespace.
     * @param namespace
     */
    public void setAttributeNamespace(Namespace namespace) {
        this.attributeNamespace = namespace;
    }

    /**
     * Getter of the XML attribute xmlns default namespace.
     * @return String   A string containing the value of the xmlns default namespace.
     */
    public String getDefaultNamespace() {
        if (this.namespaceList != null && this.namespaceList.getDefaultNamespace() != null) {
          return this.namespaceList.getDefaultNamespace().getUri();
        } else {
          return null;
        }
    }

    /**
     * Setter of the XML attribute xmlns default namespace.
     * @param xmlns
     */
    public void setDefaultNamespace(String xmlns) {
        if (this.namespaceList == null) {
          this.namespaceList = new NamespaceList(new DefaultNamespace(xmlns));
        } else {
          this.namespaceList.setDefaultNamespace(new DefaultNamespace(xmlns));
        }
        
    }

    /**
     * Returns the list of namespaces.
     * This list contains a mapping between namespace shortcuts and full
     * namespace URIs for all namespaces defined in this grammar.
     * @return NamespaceList
     */
    public NamespaceList getNamespaceList() {
        if (this.namespaceList == null) {
          this.namespaceList = new NamespaceList(null);
        }
        return namespaceList;
    }

    /**
     * Setter of the namespacelist
     * This list contains a mapping between namespace shortcuts and full
     * namespace URIs for all namespaces defined in this grammar.
     * @param namespaceList
     */
    public void setNamespaceList(NamespaceList namespaceList) {
        this.namespaceList = namespaceList;
    }
}
