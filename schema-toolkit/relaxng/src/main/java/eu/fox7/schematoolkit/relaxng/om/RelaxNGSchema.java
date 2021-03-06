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

import eu.fox7.schematoolkit.Schema;
import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.SchemaLanguage;
import eu.fox7.schematoolkit.common.DefaultNamespace;
import eu.fox7.schematoolkit.common.NamespaceList;
import eu.fox7.schematoolkit.relaxng.RelaxNGSchemaHandler;

/**
 * Class representing a RELAX NG-XSDSchema.
 *
 * Relax NG has three different types of syntax definitions.
 *
 * - Simple XML Syntax
 * - Full XML Syntax
 * - Compact Syntax
 *
 * All these are semantically equivalent. They can be converted to each other.
 *
 * This object model supports all features of the Full XML Syntax.
 *
 * This class is the main class of the RELAX NG XSDSchema holding all references to
 * contained definitions and elements.
 *
 * In RELAX NG there is only one toplevel start pattern for the schema (root
 * pattern). This can be either an element or a grammar pattern.
 *
 * In the Simple XML Syntax there can only be a grammar pattern used as root.
 */
public class RelaxNGSchema implements Schema {

    /**
     * The XML namespace.
     */
    public static final String XML_NAMESPACE = "http://www.w3.org/XML/1998/namespace";
    /**
     * The RELAX NG namespace.
     */
    public static final String RELAXNG_NAMESPACE = "http://relaxng.org/ns/structure/1.0";
    /**
     * The Root pattern of this RELAX NG XSDSchema.
     */
    protected Pattern rootPattern;
    /**
     * Namespacelist of root element
     */
    private NamespaceList namespaceList;
    /**
     * Prefix of root element
     */
    private String rootNamespacePrefix;
    /**
     * Absolute Uri of this RELAX NG schema.
     */
    private String absoluteUri;

    /**
     * Constructor of class RelaxNGSchema with parameter rootPattern
     * @param rootPattern   This is the root pattern of the RELAX NG XSDSchema.
     */
    public RelaxNGSchema(Pattern rootPattern) {
        this.rootPattern = rootPattern;
        this.namespaceList = new NamespaceList(new DefaultNamespace(RelaxNGSchema.RELAXNG_NAMESPACE));
    }

    /**
     * Constructor of class RelaxNGSchema with parameter rootPattern
     */
    public RelaxNGSchema() {
        this.namespaceList = new NamespaceList(new DefaultNamespace(RelaxNGSchema.RELAXNG_NAMESPACE));
    }

    /**
     * Getter for the root pattern of this schema
     * @return Pattern     root pattern
     */
    public Pattern getRootPattern() {
        return rootPattern;
    }

    /**
     * Setter for the root pattern of this schema
     * @param rootPattern
     */
    public void setRootPattern(Pattern rootPattern) {
        this.rootPattern = rootPattern;
    }

    /**
     * Returns the list of namespaces of the root element of this Relax NG XSDSchema.
     * This list contains a mapping between namespace shortcuts and full
     * namespace URIs for all namespaces defined in this grammar.
     * @return NamespaceList
     */
    public NamespaceList getNamespaceList() {
        return namespaceList;
    }

    /**
     * Getter of the XML attribute xmlns default namespace.
     * @return String   A string containing the value of the xmlns default namespace.
     */
    public String getDefaultNamespace() {
        if (this.namespaceList.getDefaultNamespace() != null) {
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
        namespaceList.setDefaultNamespace(new DefaultNamespace(xmlns));
    }

    /**
     * Getter for the root namespace prefix
     * @return String   The root namespace prefix
     */
    public String getRootNamespacePrefix() {
        return rootNamespacePrefix;
    }

    /**
     * Setter for the root namespace prefix
     * @param rootNamespacePrefix   The root namespace prefix
     */
    public void setRootNamespacePrefix(String rootNamespacePrefix) {
        this.rootNamespacePrefix = rootNamespacePrefix;
    }

    /**
     * Getter for the absolute Uri of the schema
     * @return String   The absolute URI of the schema
     */
    public String getAbsoluteUri() {
        return absoluteUri;
    }

    /**
     * Setter for the absolute Uri of the schema
     * @param absoluteUri   The absolute URI of the schema
     */
    public void setAbsoluteUri(String absoluteUri) {
        this.absoluteUri = absoluteUri;
    }

	@Override
	public SchemaHandler getSchemaHandler() {
		return new RelaxNGSchemaHandler(this);
	}

	@Override
	public SchemaLanguage getSchemaLanguage() {
		return SchemaLanguage.RELAXNG;
	}
}
