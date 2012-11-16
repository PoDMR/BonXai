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

package eu.fox7.schematoolkit.dtd.om;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import eu.fox7.schematoolkit.Schema;
import eu.fox7.schematoolkit.SchemaHandler;
import eu.fox7.schematoolkit.SchemaLanguage;
import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.dtd.DTDSchemaHandler;

/**
 * Main class of a datastructure for representing Document Type Definitions of XML
 * @author Lars Schmidt
 */
public class DocumentTypeDefinition implements Schema {
    /**
     * This SymbolTable holds references for ALL elements of the DTD
     */
    private Map<QualifiedName,Element> elements = new HashMap<QualifiedName, Element>();
    /**
     * This SymbolTable holds references for ALL internal entities of the DTD
     */
    private Map<String,InternalEntity> internalEntities;
    /**
     * This SymbolTable holds references for ALL external entities of the DTD
     */
    private Map<String,ExternalEntity> externalEntities;
    /**
     * This SymbolTable holds references for ALL notation of the DTD
     */
    private Map<String,Notation> notations = new HashMap<String,Notation>();
    /**
     * These two variables hold the public and system ID of the current DTD schema
     */
    private String publicID, systemID;

    /**
     * Constructor of the class DocumentTypeDefinition
     */
    public DocumentTypeDefinition() {
    }

    /**
     * Getter for the PublicID of the DTD
     * @return String
     */
    public String getPublicID() {
        return this.publicID;
    }

    /**
     * Setter for the PublicID of the DTD
     * @param publicID
     */
    public void setPublicID(String publicID) {
        this.publicID = publicID;
    }

    /**
     * Getter for the SystemID of the DTD
     * @return String
     */
    public String getSystemID() {
        return this.systemID;
    }

    /**
     * Setter for the SystemID of the DTD
     * @param systemID
     */
    public void setSystemID(String systemID) {
        this.systemID = systemID;
    }

    /**
     * Method for adding an internal entity to the symbolTable for ALL internal
     * entities defined in this DTD
     * @param internalEntity
     */
    public void addInternalEntity(InternalEntity internalEntity) {
        this.internalEntities.put(internalEntity.getName(), internalEntity);
    }

    /**
     * Method for adding an external entity to the symbolTable for ALL external
     * entities defined in this DTD
     * @param externalEntity
     */
    public void addExternalEntity(ExternalEntity externalEntity) {
        this.externalEntities.put(externalEntity.getName(), externalEntity);
    }


    /**
     * Method for adding a notation to the symbolTable for ALL notations
     * defined in this DTD
     * @param notation
     */
    public void addNotation(Notation notation) {
        this.notations.put(notation.getName(), notation);
    }

	public void addElement(Element element) {
		this.elements.put(element.getName(), element);
	}

	@Deprecated
	public Element getElement(String elementName) {
		return this.elements.get(new QualifiedName(Namespace.EMPTY_NAMESPACE, elementName));
	}

	public Element getElement(QualifiedName elementName) {
		return this.elements.get(elementName);
	}

	public Collection<Element> getElements() {
		return this.elements.values();
	}

	public Collection<Element> getRootElements() {
		return this.elements.values();
	}
	
	public Collection<QualifiedName> getRootElementNames() {
		//TODO root elements
		return null;
	}
	
	public Collection<InternalEntity> getInternalEntitys() {
		return this.internalEntities.values();
	}

	public Collection<Notation> getNotations() {
		return this.notations.values();
	}

	@Override
	public SchemaHandler getSchemaHandler() {
		return new DTDSchemaHandler(this);
	}

	@Override
	public SchemaLanguage getSchemaLanguage() {
		return SchemaLanguage.DTD;
	}
}
