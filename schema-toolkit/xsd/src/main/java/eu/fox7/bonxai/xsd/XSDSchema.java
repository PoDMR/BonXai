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

import java.util.LinkedList;

import eu.fox7.bonxai.common.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Class representing a full XML-XSDSchema.
 *
 * This class Represents an XML-XSDSchema. It contains all references to
 * referenced (foreign) schemas, contained types, attributeGroups,
 * groups, attributes, elements and constraints.
 *
 * Note that there are two different kinds of object storages in the XSDSchema:
 * {@link SymbolTable}s and {@link LinkedList}s. The {@link SymbolTable}s are
 * only used to maintain references to objects in deeper levels of the schema.
 * The {@link LinkedList}s in contrast represent the objects directly defined
 * below the <schema /> tag.
 */
public class XSDSchema {

    /**
     * In difference to the getSchemaLocation method of ForeignSchemas this method
     * returns an absolute path string.
     */
    public String getSchemaLocation() {
        return schemaLocation;
    }
    /**
     * XSD namespace.
     */
    public static final String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
    /**
     * List of referenced foreign schemas.
     *
     * These schema references can by {@link ImportedSchema}s, {@link;
     * IncludedSchema}s and {@link RedefinedSchema}s.;
     */
    protected List<ForeignSchema> foreignSchemas;
    /**
     * Types defined directly below the <schema /> tag.
     *
     * List of types which are defined in a level below this one are not
     * included in this list. Note that there is also the {@link
     * typeSymbolTable}, which contains *all* types defined the the schema.
     */
    protected LinkedHashMap<QualifiedName,Type> types;
    /**
     * Attribute groups.
     *
     * This is a list of all {@link AttributeGroup}s defined in the schema.
     * Note that {@link AttributeGroup}s can only be defined in the top level
     * of the <schema /> tag.
     */
    protected List<AttributeGroup> attributeGroups;
    /**
     * Element groups.
     *
     * This is a list of all {@link Group}s defined in the schema.  Note that
     * {@link Group}s can only be defined in the top level of the <schema />
     * tag.
     */
    protected List<Group> groups;
    /**
     * Globally defined Attributes.
     *
     * This is a list {@link Attribute}s defined right below the <schema />
     * tag. Note that attributes which are defined anywhere else in the schema
     * (i.e. in {@link ComplexType}s and {@link AttributeGroup}s) are *not*
     * contained in this list.
     *
     * Attention: {@link Attribute}s must be referenced by their full-qualified
     * name, which is the namespace the {@link Attribute} resides in
     * concatenated with its local name in the form "{<namespace>}<localName>".
     */
    protected List<Attribute> attributes;
    /**
     * Globally defined Elements.
     *
     * This is a list {@link Element}s defined right below the <schema /> tag.
     * Note that attributes which are defined anywhere else in the schema (i.e.
     * in {@link Particle}s) are *not* contained in this list.
     *
     * Attention: {@link Element}s must be referenced by their full-qualified
     * name, which is the namespace the {@link Element} resides in
     * concatenated with its local name in the form "{<namespace>}<localName>".
     */
    protected List<Element> elements;

    /**
     * List of namespaces used in the schema.
     */
    private NamespaceList namespaceList;
        
    /**
	 * @return the defaultNamespace
	 */
	public DefaultNamespace getDefaultNamespace() {
		return this.namespaceList.getDefaultNamespace();
	}

	/**
	 * @param defaultNamespace the defaultNamespace to set
	 */
	public void setDefaultNamespace(DefaultNamespace defaultNamespace) {
		this.namespaceList.setDefaultNamespace(defaultNamespace);
	}
	private String schemaLocation;

    public void setSchemaLocation(String documentURI) {
        this.schemaLocation = documentURI;

    }

    /**
     * Public enumeration for the Values of the global defined
     * elementFormdefault and attributeFormDefault
     **/
    public enum Qualification {

        qualified,
        unqualified;
    }

    /**
     * Public enumeration for the Values of the global defined
     * blockDefault
     **/
    public enum BlockDefault {

        extension, // for Elements, ComplexTypes, SimpleTypes
        restriction, // for Elements, ComplexTypes, SimpleTypes
        substitution;   // for Elements
    }

    /**
     * Public enumeration for the Values of the global defined
     * finalDefault
     **/
    public enum FinalDefault {

        extension, // for Elements, ComplexTypes, SimpleTypes
        restriction, // for Elements, ComplexTypes, SimpleTypes
        list, // for SimpleTypes
        union;          // for SimpleTypes
    }
    private Qualification attributeFormDefault = Qualification.unqualified;
    private Qualification elementFormDefault = Qualification.unqualified;
    private HashSet<BlockDefault> blockDefault = new HashSet<BlockDefault>(); // There is no default-Value for this property
    private HashSet<FinalDefault> finalDefault = new HashSet<FinalDefault>(); // There is no default-Value for this property
    /**
     * HashMap for all occurrences of SubstitutionGroup in the schema.
     */
// TODO:    private HashMap<SymbolTableRef<Element>, HashSet<SymbolTableRef<Element>>> substitutionElements;

    /**
     * Creates a new schema with empty containers.
     *
     * This method creates the {@link SymbolTable}s and {@link LinkedList}s to
     * represent the schema structure. The accessor methods can be used to add
     * the schema elements, to retreive them and to manipulate them.
     */
    public XSDSchema() {

        // Initialize item lists
        foreignSchemas = new LinkedList<ForeignSchema>();
        types = new LinkedHashMap<QualifiedName,Type>();
        attributeGroups = new LinkedList<AttributeGroup>();
        groups = new LinkedList<Group>();
        attributes = new LinkedList<Attribute>();
        elements = new LinkedList<Element>();

        namespaceList = new NamespaceList();

        blockDefault = new HashSet<BlockDefault>();
        finalDefault = new HashSet<FinalDefault>();
    }
    // Attribute "id" represents the ID attribute type from [XML 1.0 (Second Edition)].
    private String id;

    /**
     * Getter for the attribute id
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Setter for the attribute id
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter for attributeFormDefault
     * @return attributeFormDefault
     */
    public Qualification getAttributeFormDefault() {
        return attributeFormDefault;
    }

    /**
     * Setter for attributeFormDefault
     * This describes if there has to be the namespace abbreviation infront of
     * atributes.
     * @param attributeFormDefault
     */
    public void setAttributeFormDefault(Qualification attributeFormDefault) {
        this.attributeFormDefault = attributeFormDefault;
    }

    /**
     * Getter for elementFormDefault
     * @return elementFormDefault
     */
    public Qualification getElementFormDefault() {
        return elementFormDefault;
    }

    /**
     * Setter for elementFormDefault
     * This describes if there has to be the namespace abbreviation infront of
     * elements.
     * @param elementFormDefault
     */
    public void setElementFormDefault(Qualification elementFormDefault) {
        this.elementFormDefault = elementFormDefault;
    }

    /**
     * Getter for blockDefault
     * @return blockDefault
     */
    public HashSet<BlockDefault> getBlockDefaults() {
        return this.blockDefault;
    }

    /**
     * Setter for blockDefault
     * This defines the default-value to block features of the inheritance handling.
     * @param blockDefault
     */
    public void addBlockDefault(BlockDefault blockDefault) {
        this.blockDefault.add(blockDefault);
    }

    /**
     * Getter for finalDefault
     * @return finalDefault
     */
    public HashSet<FinalDefault> getFinalDefaults() {
        return new HashSet<FinalDefault>(finalDefault);
    }

    /**
     * Setter for finalDefault
     * This defines the default-value for the final-handling.
     * @param finalDefault 
     */
    public void addFinalDefault(FinalDefault finalDefault) {
        this.finalDefault.add(finalDefault);
    }

    /**
     * Sets the finalDefault HashSet
     * @param finalDefaultHashSet 
     */
    public void setFinalDefaults(HashSet<FinalDefault> finalDefaultHashSet) {
        this.finalDefault = finalDefaultHashSet;
    }

    /**
     * Sets the blockDefault HashSet
     * @param blockDefaultHashSet
     */
    public void setBlockDefaults(HashSet<BlockDefault> blockDefaultHashSet) {
        this.blockDefault = blockDefaultHashSet;
    }

    /**
     * Returns a list of all {@link AttributeGroup}s defined in the schema.
     *
     * Returns a list of all {@link AttributeGroup}s defined in the schema.
     * Note that {@link AttributeGroup}s can only be defined in the top level
     * of the <schema /> tag. Note: The returned list is a *copy* of the;
     * internal referenced schema list. You can not use this list to manipulate;
     * the schemas referenced! To do this, use the {@link addAttributeGroup()};
     * method!
     */
    public Collection<AttributeGroup> getAttributeGroups() {

        LinkedList<AttributeGroup> list = new LinkedList<AttributeGroup>();

        if (attributeGroups != null) {
            for (AttributeGroup st : attributeGroups) {
                list.add(st);
            }
        }
        return list;
    }

    /**
     * Adds an {@link AttributeGroup} to the schema.
     *
     * Make sure to submit a {@link SymbolTableRef} created by the {@link
     * SymbolTable} returned from {@link getAttributeGroupSymbolTable()}.
     */
    public void addAttributeGroup(AttributeGroup val) {
        this.attributeGroups.add(val);
    }

    /**
     * Returns a list of all {@link Attribute}s defined globally in the schema.
     *
     * Attention: This list does not contain {@link Attribute}s which are
     * defined anywhere deeper in the schema (i.e. in {@link ComplexType}s and
     * {@link AttributeGroup}s). Note: The returned list is a *copy* of the
     * internal referenced schema list. You can not use this list to manipulate
     * the schemas referenced! To do this, use the {@link addAttribute()}
     * method!
     */
    public Collection<Attribute> getAttributes() {
        return attributes;
    }

    /**
     * Adds a globally defined {@link Attribute} to the schema.
     *
     * Make sure to submit a {@link SymbolTableRef} created by the {@link
     * SymbolTable} returned from {@link getAttributeSymbolTable()}.
     *
     * Do *not* add {@link Attribute}s to this list which are not defined
     * globally. {@link Attribute}s defined in {@link AttributeGroup}s and
     * {@link ComplexType}s do *not* belong here!
     */
    public void addAttribute(Attribute val) {
        this.attributes.add(val);
    }

    /**
     * Returns a list of all {@link Element}s defined globally in the schema.
     *
     * Attention: This list does not contain {@link Element}s which are defined
     * anywhere deeper in the schema (i.e. in {@link Particle}s and {@link
     * Group}s). Note: The returned list is a *copy* of the internal referenced
     * schema list. You can not use this list to manipulate the schemas
     * referenced! To do this, use the {@link addElement()} method!
     */
    public Collection<Element> getElements() {
        return elements;
    }

    /**
     * Adds a globally defined {@link Element} to the schema.
     */
    public void addElement(Element val) {
        this.elements.add(val);
    }

	public void removeElement(Element elementRef) {
		this.elements.remove(elementRef);
	}
	
    /**
     * Returns all {@link ForeignSchema}s referenced in this schema.
     *
     * These schema references can by {@link ImportedSchema}s, {@link
     * IncludedSchema}s and {@link RedefinedSchema}s. Note: The returned list
     * is a *copy* of the internal referenced schema list. You can not use this
     * list to manipulate the schemas referenced! To do this, use the {@link
     * addForeignSchema()} method!
     */
    public LinkedList<ForeignSchema> getForeignSchemas() {
        LinkedList<ForeignSchema> list;

        list = new LinkedList<ForeignSchema>(foreignSchemas);
        return list;
    }

    /**
     * Adds a {@link ForeignSchema} to the schema.
     *
     * This method is used to add a new {@link ForeignSchema} reference to the
     * schema.
     */
    public void addForeignSchema(ForeignSchema val) {
        this.foreignSchemas.add(val);
    }

    /**
     * Clear the internal list of {@link ForeignSchema}s.
     *
     * After a call to this method the list of {@link ForeignSchema}s will be
     * empty. Do *not* use this method unless you are really sure what you're
     * doing! It is usually not necessary to use this method at all. Especially
     * not during the parsing of a schema!
     */
    public void clearForeignSchemas() {
        this.foreignSchemas.clear();
    }

    public void setForeignSchemas(LinkedList<ForeignSchema> foreignSchemas) {
        this.foreignSchemas = foreignSchemas;
    }

    /**
     * Returns a list of all {@link Group}s defined in the schema.
     *
     * Returns a list of all {@link Group}s defined in the schema.  Note that
     * {@link Group}s can only be defined in the top level of the <schema />
     * tag. Note: The returned list is a *copy* of the; internal referenced
     * schema list. You can not use this list to manipulate; the schemas
     * referenced! To do this, use the {@link addGroup()}; method!
     */
    public Collection<eu.fox7.bonxai.xsd.Group> getGroups() {
        return groups;
    }

    /**
     * Adds a {@link Group} to the schema.
     *
     * Make sure to submit a {@link SymbolTableRef} created by the {@link
     * SymbolTable} returned from {@link getGroupSymbolTable()}.
     */
    public void addGroup(eu.fox7.bonxai.xsd.Group val) {
        this.groups.add(val);
    }

    public void removeGroup(Group val) {
        this.groups.remove(val);
    }

    /**
     * Returns a list of all {@link Type}s defined *globally* in the schema.
     *
     * Returns a list of all {@link Type}s defined *globally* in the schema.
     * Note that {@link Type}s can only be defined in the top level of the
     * <schema /> tag.
     *
     * Attention: This list does not contain {@link Type}s which are defined
     * anywhere deeper in the schema (i.e. in {@link Element}s). Note: The
     * returned list is a *copy* of the; internal referenced schema list. You
     * can not use this list to manipulate; the schemas referenced! To do this,
     * use the {@link addType()}; method!
     */
    public Collection<Type> getTypes() {
        return types.values();
    }

    /**
     * Adds a globally defined {@link Type} to the schema.
     *
     * Make sure to submit a {@link SymbolTableRef} created by the {@link
     * SymbolTable} returned from {@link getTypeSymbolTable()}.
     *
     * Do *not* add {@link Type}s to this list which are not defined globally.
     * {@link Type}s defined in {@link Element}s!
     */
    public void addType(Type val) {
        this.types.put(val.getName(), val);
    }

    public void removeType(Type val) {
        this.types.remove(val.getName());
    }
    
    public void removeType(QualifiedName typename) {
    	this.types.remove(typename);
    }
    
    public Type getType(QualifiedName typename) {
    	return types.get(typename);
    }

    /**
     * Returns the list of namespaces.
     *
     * This list contains a mapping between namespace shortcuts and full
     * namespace URIs for all namespaces defined in the schema. Mostly, these
     * are short cuts for the targetNamespace (defaultNamespace in the {@link
     * NamespaceList}) and any imported/included/redefined schemas.
     *
     * The targetNamespace of the schema is defined in the NamespaceList as the
     * defaultNamespace.
     */
    public NamespaceList getNamespaceList() {
        return namespaceList;
    }

//    public void addSubstitutionElement(SymbolTableRef<Element> headElement, SymbolTableRef<Element> element) {
//        if (!this.substitutionElements.containsKey(headElement)) {
//            HashSet<SymbolTableRef<Element>> tempHashSet = new HashSet<SymbolTableRef<Element>>();
//            tempHashSet.add(element);
//            this.substitutionElements.put(headElement, tempHashSet);
//        } else {
//            HashSet<SymbolTableRef<Element>> tempHashSet = this.substitutionElements.get(headElement);
//            if (!tempHashSet.contains(element)) {
//                tempHashSet.add(element);
//            }
//        }
//    }
//
//    public HashSet<Element> getSubstitutionElements(SymbolTableRef<Element> headElement) {
//        HashSet<Element> returnHashSet = new HashSet<Element>();
//        if (substitutionElements != null && substitutionElements.get(headElement) != null) {
//            for (Iterator<SymbolTableRef<Element>> it = substitutionElements.get(headElement).iterator(); it.hasNext();) {
//                SymbolTableRef<Element> symbolTableRef = it.next();
//                returnHashSet.add(symbolTableRef.getReference());
//            }
//        }
//        return returnHashSet;
//    }
//
//    public HashMap<SymbolTableRef<eu.fox7.bonxai.xsd.Element>, HashSet<SymbolTableRef<eu.fox7.bonxai.xsd.Element>>> getSubstitutionElements() {
//        return substitutionElements;
//    }
//
//    public boolean isSubstitutionHead(SymbolTableRef<Element> headElement) {
//        return this.substitutionElements.containsKey(headElement);
//    }

	public Namespace getNamespaceByIdentifier(String identifier) {
		return this.namespaceList.getNamespaceByIdentifier(identifier);
	}

	public void addIdentifiedNamespace(IdentifiedNamespace identifiedNamespace) {
		this.namespaceList.addNamespace(identifiedNamespace);
	}

	public Key getKey(QualifiedName refer) {
		// TODO Auto-generated method stub
		return null;
	}

	public Namespace getNamespaceByURI(String uri) {
		return this.namespaceList.getNamespaceByUri(uri);
	}

	public Element getElement(QualifiedName elementName) {
		for (Element element: elements)
			if (element.getName().equals(elementName))
				return element;
		return null;		
	}

	public Attribute getAttribute(QualifiedName attributeName) {
		for (Attribute attribute: this.attributes)
			if (attribute.name.equals(attributeName))
				return attribute;
		return null;
	}
}

