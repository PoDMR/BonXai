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
package de.tudortmund.cs.bonxai.xsd;

import java.util.LinkedList;

import de.tudortmund.cs.bonxai.common.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

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
    protected LinkedList<ForeignSchema> foreignSchemas;
    /**
     * Types defined directly below the <schema /> tag.
     *
     * List of types which are defined in a level below this one are not
     * included in this list. Note that there is also the {@link
     * typeSymbolTable}, which contains *all* types defined the the schema.
     */
    protected LinkedList<SymbolTableRef<Type>> types;
    /**
     * Attribute groups.
     *
     * This is a list of all {@link AttributeGroup}s defined in the schema.
     * Note that {@link AttributeGroup}s can only be defined in the top level
     * of the <schema /> tag.
     */
    protected LinkedList<SymbolTableRef<AttributeGroup>> attributeGroups;
    /**
     * Element groups.
     *
     * This is a list of all {@link Group}s defined in the schema.  Note that
     * {@link Group}s can only be defined in the top level of the <schema />
     * tag.
     */
    protected LinkedList<SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Group>> groups;
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
    protected LinkedList<SymbolTableRef<Attribute>> attributes;
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
    protected LinkedList<SymbolTableRef<Element>> elements;
    /**
     * SymbolTable used to store *all* Types defined in a XSDSchema.
     *
     * SymbolTable for *all* types defined anywhere in the XSDSchema. Note that
     * this SymbolTable really contains *all* types, not only those defined
     * directly in below the <schema /> tag. These types are stored in addition
     * in the {@link types} attribute.
     *
     * Attention: {@link Type}s must be referenced by their full-qualified
     * name, which is the namespace the {@link Type} resides in concatenated
     * with its local name in the form "{<namespace>}<localName>".
     *
     * This {@link SymbolTable} also contains simple types which are defined by
     * XSD itself, so-called primitive datatypes. Primitive {@link SimpleType}s
     * reside in the namespace http://www.w3.org/2001/XMLSchema.
     */
    private SymbolTableFoundation<Type> typeSymbolTable;
    /**
     * SymbolTable used to store all AttributeGroups defined in a XSDSchema.
     *
     * SymbolTable for *all* {@link AttributeGroup}s defined in the schema.
     * Since {@link AttributeGroup}s can only be defined directly below the
     * <schema /> tag, this SymbolTable contains *all* {@link AttributeGroup}s
     *
     * Attention: {@link AttributeGroup}s must be referenced by their
     * full-qualified name, which is the namespace the {@link AttributeGroup}
     * resides in concatenated with its local name in the form
     * "{<namespace>}<localName>".
     */
    private SymbolTableFoundation<AttributeGroup> attributeGroupSymbolTable;
    /**
     * SymbolTable used to store all Groups defined in a XSDSchema.
     *
     * SymbolTable for *all* {@link Group}s defined in the schema. Since {@link
     * Group}s can only be defined directly below the <schema /> tag, this
     * SymbolTable contains *all* {@link Group}s
     *
     * Attention: {@link Group}s must be referenced by their full-qualified
     * name, which is the namespace the {@link Group} resides in concatenated
     * with its local name in the form "{<namespace>}<localName>".
     */
    private SymbolTableFoundation<de.tudortmund.cs.bonxai.xsd.Group> groupSymbolTable;
    /**
     * SymbolTable used to store all globally defined Attributes in a XSDSchema.
     *
     * SymbolTable for {@link Attribute}s defined globally in the schema.
     * {@link Attribute}s which are defined anywhere else in the schema (i.e.
     * in {@link ComplexType}s and {@link AttributeGroup}s) are *not*
     * referenced in this {@link SymbolTable}.
     *
     * Attention: {@link Attribute}s must be referenced by their full-qualified
     * name, which is the namespace the {@link Attribute} resides in concatenated
     * with its local name in the form "{<namespace>}<localName>".
     */
    private SymbolTableFoundation<Attribute> attributeSymbolTable;
    /**
     * SymbolTable used to store all globally defined Elements in a XSDSchema.
     *
     * SymbolTable for {@link Element}s defined globally in the schema. {@link
     * Element}s which are defined anywhere else in the schema (i.e. in {@link
     * Particle}s and {@link Group}s) are *not* referenced in this {@link
     * SymbolTable}.
     *
     * Attention: {@link Element}s must be referenced by their full-qualified
     * name, which is the namespace the {@link Element} resides in concatenated
     * with its local name in the form "{<namespace>}<localName>".
     */
    private SymbolTableFoundation<Element> elementSymbolTable;
    /**
     * SymbolTable used to store all {@link Key}s in a XSDSchema.
     *
     * This {@link SymbolTable} contains references to *all* {@link Key}s
     * defined anywhere in the XSDSchema. The references stored here are necessary
     * to create {@link KeyRef}s correctly. Note that {@link Key}s and {@link
     * KeyRef}s can only be defined below <element /> tags and  are not allowed
     * globally in the <schema />.
     */
    private SymbolTableFoundation<SimpleConstraint> keyAndUniqueSymbolTable;
    /**
     * List of namespaces used in the schema.
     *
     * This attribute contains a mapping between namespace shortcuts and full
     * namespace URIs for all namespaces defined in the schema. Mostly, these
     * are short cuts for the targetNamespace (defaultNamespace in the {@link
     * NamespaceList}) and any imported/included/redefined schemas.
     *
     * The targetNamespace of the schema is defined in the NamespaceList as the
     * defaultNamespace.
     */
    private NamespaceList namespaceList;
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
    private HashMap<SymbolTableRef<Element>, HashSet<SymbolTableRef<Element>>> substitutionElements;
    /**
     * HashSet for all names of (key, unique, keyref) constraints in the schema.
     */
    private HashSet<String> constraintNames;

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
        types = new LinkedList<SymbolTableRef<Type>>();
        attributeGroups = new LinkedList<SymbolTableRef<AttributeGroup>>();
        groups = new LinkedList<SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Group>>();
        attributes = new LinkedList<SymbolTableRef<Attribute>>();
        elements = new LinkedList<SymbolTableRef<Element>>();

        // Initialize all needed SymbolTables
        typeSymbolTable = new SymbolTable<Type>();
        attributeGroupSymbolTable = new SymbolTable<AttributeGroup>();
        groupSymbolTable = new SymbolTable<de.tudortmund.cs.bonxai.xsd.Group>();
        attributeSymbolTable = new SymbolTable<Attribute>();
        elementSymbolTable = new SymbolTable<Element>();
        keyAndUniqueSymbolTable = new SymbolTable<SimpleConstraint>();

        // Initialize NamespaceList, empty for now, default can be set via
        // setTargetNamespace()
        namespaceList = new NamespaceList(new DefaultNamespace(""));

        blockDefault = new HashSet<BlockDefault>();
        finalDefault = new HashSet<FinalDefault>();
        substitutionElements = new HashMap<SymbolTableRef<Element>, HashSet<SymbolTableRef<Element>>>();
        constraintNames = new HashSet<String>();
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
     * Creates a new XSDSchema for the given target namespace.
     *
     * Every XSDSchema should usually have a target namespace, so this should be
     * the preferred constructor.
     */
    public XSDSchema(String targetNamespace) {
        this();
        this.setTargetNamespace(targetNamespace);
    }

    /**
     * Retrieve the SymbolTable used to store {@link Type}s.
     *
     * Returns the SymbolTable for *all* types defined anywhere in the XSDSchema.
     * Note that this SymbolTable really contains *all* types, not only those
     * defined directly in below the <schema /> tag. These types are stored in
     * addition in the {@link types} attribute.
     *
     * Attention: {@link Type}s must be referenced by their full-qualified
     * name, which is the namespace the {@link Type} resides in concatenated
     * with its local name in the form "{<namespace>}<localName>".
     *
     * This {@link SymbolTable} also contains simple types which are defined by
     * XSD itself, so-called primitive datatypes. Primitive {@link SimpleType}s
     * reside in the namespace http://www.w3.org/2001/XMLSchema.
     */
    public SymbolTableFoundation<Type> getTypeSymbolTable() {
        return this.typeSymbolTable;
    }

    /**
     * Retrieve the SymbolTable used to store {@link Type}s.
     *
     * Attention: Do not use this method if you don't know what you're doing!
     * Replacing the SymbolTable may invalidated the complete schema due to
     * inconsistencies with SymbolTable references!
     *
     * Instead of replacing a SymbolTable, use the getter to retrieve an
     * existing one and work with its methods. Replacing a SymbolTable is never
     * neccessary when parsing an XSD!
     */
    public void setTypeSymbolTable(SymbolTableFoundation<Type> table) {
        this.typeSymbolTable = table;
    }

    /**
     * Retrieve the SymbolTable used to store {@link AttributeGroup}s.
     *
     * Returns the SymbolTable for *all* {@link AttributeGroup}s defined in the
     * schema.  Since {@link AttributeGroup}s can only be defined directly
     * below the <schema /> tag, this SymbolTable contains *all* {@link
     * AttributeGroup}s
     *
     * Attention: {@link AttributeGroup}s must be referenced by their
     * full-qualified name, which is the namespace the {@link AttributeGroup}
     * resides in concatenated with its local name in the form
     * "{<namespace>}<localName>".
     */
    public SymbolTableFoundation<AttributeGroup> getAttributeGroupSymbolTable() {
        return this.attributeGroupSymbolTable;
    }

    /**
     * Replace the SymbolTable used to store {@link AttributeGroup}s.
     *
     * Attention: Do not use this method if you don't know what you're doing!
     * Replacing the SymbolTable may invalidated the complete schema due to
     * inconsistencies with SymbolTable references!
     *
     * Instead of replacing a SymbolTable, use the getter to retrieve an
     * existing one and work with its methods. Replacing a SymbolTable is never
     * neccessary when parsing an XSD!
     */
    public void setAttributeGroupSymbolTable(SymbolTableFoundation<AttributeGroup> table) {
        this.attributeGroupSymbolTable = table;
    }

    /**
     * Retrieve the SymbolTable used to store {@link Group}s.
     *
     * Returns the SymbolTable for *all* {@link Group}s defined in the schema.
     * Since {@link Group}s can only be defined directly below the <schema />
     * tag, this SymbolTable contains *all* {@link Group}s
     *
     * Attention: {@link Group}s must be referenced by their full-qualified
     * name, which is the namespace the {@link Group} resides in concatenated
     * with its local name in the form "{<namespace>}<localName>".
     */
    public SymbolTableFoundation<de.tudortmund.cs.bonxai.xsd.Group> getGroupSymbolTable() {
        return this.groupSymbolTable;
    }

    /**
     * Replace the SymbolTable used to store {@link Group}s.
     *
     * Attention: Do not use this method if you don't know what you're doing!
     * Replacing the SymbolTable may invalidated the complete schema due to
     * inconsistencies with SymbolTable references!
     *
     * Instead of replacing a SymbolTable, use the getter to retrieve an
     * existing one and work with its methods. Replacing a SymbolTable is never
     * neccessary when parsing an XSD!
     */
    public void setGroupSymbolTable(SymbolTableFoundation<de.tudortmund.cs.bonxai.xsd.Group> table) {
        this.groupSymbolTable = table;
    }

    /**
     * Retrieve the SymbolTable used to store all globally defined {@link Attribute}s.
     *
     * Return the SymbolTable for {@link Attribute}s defined globally in the
     * schema.  {@link Attribute}s which are defined anywhere else in the
     * schema (i.e.  in {@link ComplexType}s and {@link AttributeGroup}s) are
     * *not* referenced in this {@link SymbolTable}.
     *
     * Attention: {@link Attribute}s must be referenced by their full-qualified
     * name, which is the namespace the {@link Attribute} resides in concatenated
     * with its local name in the form "{<namespace>}<localName>".
     */
    public SymbolTableFoundation<Attribute> getAttributeSymbolTable() {
        return this.attributeSymbolTable;
    }

    /**
     * Replace the SymbolTable used to store all globally defined {@link Attribute}s.
     *
     * Attention: Do not use this method if you don't know what you're doing!
     * Replacing the SymbolTable may invalidated the complete schema due to
     * inconsistencies with SymbolTable references!
     *
     * Instead of replacing a SymbolTable, use the getter to retrieve an
     * existing one and work with its methods. Replacing a SymbolTable is never
     * neccessary when parsing an XSD!
     */
    public void setAttributeSymbolTable(SymbolTableFoundation<Attribute> table) {
        this.attributeSymbolTable = table;
    }

    /**
     * Retrieve the SymbolTable used to store all globally defined Elements
     *
     * Returns the SymbolTable for {@link Element}s defined globally in the
     * schema. {@link Element}s which are defined anywhere else in the schema
     * (i.e. in {@link Particle}s and {@link Group}s) are *not* referenced in
     * this {@link SymbolTable}.
     *
     * Attention: {@link Element}s must be referenced by their full-qualified
     * name, which is the namespace the {@link Element} resides in concatenated
     * with its local name in the form "{<namespace>}<localName>".
     */
    public SymbolTableFoundation<Element> getElementSymbolTable() {
        return this.elementSymbolTable;
    }

    /**
     * Replace the SymbolTable used to store all globally defined {@link Element}s.
     *
     * Attention: Do not use this method if you don't know what you're doing!
     * Replacing the SymbolTable may invalidated the complete schema due to
     * inconsistencies with SymbolTable references!
     *
     * Instead of replacing a SymbolTable, use the getter to retrieve an
     * existing one and work with its methods. Replacing a SymbolTable is never
     * neccessary when parsing an XSD!
     */
    public void setElementSymbolTable(SymbolTableFoundation<Element> table) {
        this.elementSymbolTable = table;
    }

    /**
     * Retrieve the SymbolTable used to store all {@link Key}s.
     *
     * Returns the {@link SymbolTable} which contains references to *all*
     * {@link Key}s defined anywhere in the XSDSchema. The references stored here
     * are necessary to create {@link KeyRef}s correctly. Note that {@link
     * Key}s and {@link KeyRef}s can only be defined below <element /> tags and
     * are not allowed globally in the <schema />.
     */
    public SymbolTableFoundation<SimpleConstraint> getKeyAndUniqueSymbolTable() {
        return keyAndUniqueSymbolTable;
    }

    /**
     * Replace the SymbolTable used to store all {@link Key}s.
     *
     * Attention: Do not use this method if you don't know what you're doing!
     * Replacing the SymbolTable may invalidated the complete schema due to
     * inconsistencies with SymbolTable references!
     *
     * Instead of replacing a SymbolTable, use the getter to retrieve an
     * existing one and work with its methods. Replacing a SymbolTable is never
     * neccessary when parsing an XSD!
     */
    public void setKeySymbolTable(SymbolTableFoundation<SimpleConstraint> table) {
        keyAndUniqueSymbolTable = table;
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
    public LinkedList<AttributeGroup> getAttributeGroups() {

        LinkedList<AttributeGroup> list = new LinkedList<AttributeGroup>();

        if (attributeGroups != null) {
            for (SymbolTableRef<AttributeGroup> st : attributeGroups) {
                list.add(st.getReference());
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
    public void addAttributeGroup(SymbolTableRef<AttributeGroup> val) {
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
    public LinkedList<Attribute> getAttributes() {

        LinkedList<Attribute> list = new LinkedList<Attribute>();

        if (attributes != null) {
            for (SymbolTableRef<Attribute> st : attributes) {
                list.add(st.getReference());
            }
        }
        return list;
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
    public void addAttribute(SymbolTableRef<Attribute> val) {
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
    public LinkedList<Element> getElements() {
        LinkedList<Element> list = new LinkedList<Element>();

        if (elements != null) {
            for (SymbolTableRef<Element> st : elements) {
                list.add(st.getReference());
            }
        }
        return list;
    }

    /**
     * Adds a globally defined {@link Element} to the schema.
     *
     * Make sure to submit a {@link SymbolTableRef} created by the {@link
     * SymbolTable} returned from {@link getElementSymbolTable()}.
     *
     * Do *not* add {@link Element}s to this list which are not defined
     * globally. {@link Element}s defined in {@link ElementGroup}s and
     * {@link ComplexType}s do *not* belong here!
     *
     * TODO: WTF?
     */
    public void addElement(SymbolTableRef<Element> val) {
        this.elements.add(val);
    }

	public void removeElement(SymbolTableRef<Element> elementRef) {
		this.elements.remove(elementRef);
		this.elementSymbolTable.removeReference(elementRef.getKey());
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
    public LinkedList<de.tudortmund.cs.bonxai.xsd.Group> getGroups() {
        LinkedList<de.tudortmund.cs.bonxai.xsd.Group> list = new LinkedList<de.tudortmund.cs.bonxai.xsd.Group>();

        if (groups != null) {
            for (SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Group> st : groups) {
                list.add(st.getReference());
            }
        }
        return list;
    }

    /**
     * Adds a {@link Group} to the schema.
     *
     * Make sure to submit a {@link SymbolTableRef} created by the {@link
     * SymbolTable} returned from {@link getGroupSymbolTable()}.
     */
    public void addGroup(SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Group> val) {
        this.groups.add(val);
    }

    public void removeGroup(SymbolTableRef<Group> val) {
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
    public LinkedList<Type> getTypes() {
        LinkedList<Type> list = new LinkedList<Type>();
        if (types != null) {
            for (SymbolTableRef<Type> st : types) {
                list.add(st.getReference());
            }
        }
        return list;
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
    public void addType(SymbolTableRef<Type> val) {
        this.types.add(val);
    }

    public void removeType(SymbolTableRef<Type> val) {
        this.types.remove(val);
    }

    /**
     * Replaces the list of namespaces.
     *
     * Attention: Do not use this method if you don't know what you're doing!
     * Replacing this list may invalidated the complete schema due to
     * inconsistencies with the namespaces used!
     *
     * Instead of replacing a NamespaceList, use the getter to retrieve the
     * existing one and work with its methods. Replacing a NamespaceList is
     * never neccessary when parsing an XSD!
     */
    public void setNamespaceList(NamespaceList namespaceList) {
        this.namespaceList = namespaceList;
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

    /**
     * Returns the targetNamespace of this XSDSchema.
     *
     * This method is a shortcut for
     * getNamespaceList().getDefaultNamespace().getUri().
     */
    public String getTargetNamespace() {
        return namespaceList.getDefaultNamespace().getUri();
    }

    /**
     * Set the targetNamespace of this XSDSchema.
     *
     * This method is a shortcut for
     * getNamespaceList().getDefaultNamespace().setUri().
     */
    public void setTargetNamespace(String targetNamespace) {
        namespaceList.getDefaultNamespace().setUri(targetNamespace);
    }

    public void addSubstitutionElement(SymbolTableRef<Element> headElement, SymbolTableRef<Element> element) {
        if (!this.substitutionElements.containsKey(headElement)) {
            HashSet<SymbolTableRef<Element>> tempHashSet = new HashSet<SymbolTableRef<Element>>();
            tempHashSet.add(element);
            this.substitutionElements.put(headElement, tempHashSet);
        } else {
            HashSet<SymbolTableRef<Element>> tempHashSet = this.substitutionElements.get(headElement);
            if (!tempHashSet.contains(element)) {
                tempHashSet.add(element);
            }
        }
    }

    public HashSet<Element> getSubstitutionElements(SymbolTableRef<Element> headElement) {
        HashSet<Element> returnHashSet = new HashSet<Element>();
        if (substitutionElements != null && substitutionElements.get(headElement) != null) {
            for (Iterator<SymbolTableRef<Element>> it = substitutionElements.get(headElement).iterator(); it.hasNext();) {
                SymbolTableRef<Element> symbolTableRef = it.next();
                returnHashSet.add(symbolTableRef.getReference());
            }
        }
        return returnHashSet;
    }

    public HashMap<SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Element>, HashSet<SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Element>>> getSubstitutionElements() {
        return substitutionElements;
    }

    public boolean isSubstitutionHead(SymbolTableRef<Element> headElement) {
        return this.substitutionElements.containsKey(headElement);
    }

    public boolean addConstraintName(String name) {
        return this.constraintNames.add(name);
    }

    public HashSet<String> getConstraintNames() {
        return constraintNames;
    }


}

