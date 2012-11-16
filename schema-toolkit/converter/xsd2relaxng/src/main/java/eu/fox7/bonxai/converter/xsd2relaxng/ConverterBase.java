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

package eu.fox7.bonxai.converter.xsd2relaxng;

import eu.fox7.schematoolkit.common.AnyAttribute;
import eu.fox7.schematoolkit.common.AnyPattern;
import eu.fox7.schematoolkit.common.AttributeParticle;
import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.relaxng.om.*;
import eu.fox7.schematoolkit.xsd.om.AttributeGroup;
import eu.fox7.schematoolkit.xsd.om.AttributeRef;
import eu.fox7.schematoolkit.xsd.om.ComplexContentExtension;
import eu.fox7.schematoolkit.xsd.om.ComplexContentRestriction;
import eu.fox7.schematoolkit.xsd.om.ComplexContentType;
import eu.fox7.schematoolkit.xsd.om.ComplexType;
import eu.fox7.schematoolkit.xsd.om.ForeignSchema;
import eu.fox7.schematoolkit.xsd.om.ImportedSchema;
import eu.fox7.schematoolkit.xsd.om.SimpleContentExtension;
import eu.fox7.schematoolkit.xsd.om.SimpleContentRestriction;
import eu.fox7.schematoolkit.xsd.om.SimpleContentType;
import eu.fox7.schematoolkit.xsd.om.SimpleType;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * Class ConverterBase
 *
 * This is the abstract base class for all converter-classes for each type of
 * XML XSDSchema component
 *
 * Lookup-maps for already converted components are defined here.
 *
 * @author Lars Schmidt
 */
public abstract class ConverterBase {

    /**
     * Source of the conversion
     */
    protected RelaxNGSchema relaxng;
    /**
     * Target for the conversion
     */
    protected XSDSchema xmlSchema;
    /**
     * Already converted XML XSDSchema components are referenced in the lookup-maps defined below.
     *
     * These are:
     * - elements                           in xsdElementDefineRefMap
     * - types (simpleType/complexType)     in xsdTypeDefineRefMap
     * - groups                             in xsdGroupDefineRefMap
     * - attributeGroups                    in xsdAttributeGroupDefineRefMap
     * - attributes                         in xsdAttributeDefineRefMap
     */
    protected HashMap<eu.fox7.schematoolkit.xsd.om.Element, String> xsdElementDefineRefMap;
    /**
     * types
     */
    protected HashMap<eu.fox7.schematoolkit.xsd.om.Type, String> xsdTypeDefineRefMap;
    /**
     * groups
     */
    protected HashMap<eu.fox7.schematoolkit.xsd.om.Group, String> xsdGroupDefineRefMap;
    /**
     * attributeGroups
     */
    protected HashMap<eu.fox7.schematoolkit.xsd.om.AttributeGroup, String> xsdAttributeGroupDefineRefMap;
    /**
     * attributes
     */
    protected HashMap<eu.fox7.schematoolkit.xsd.om.Attribute, String> xsdAttributeDefineRefMap;
    /**
     * Set of all attributes with ID-type, used in the schema and nested foreign schemas
     */
    protected LinkedHashSet<QualifiedName> attributeNamesWithIDType = new LinkedHashSet<QualifiedName>();
    /**
     * Grammar of the RelaxNG Schema
     */
    protected Grammar grammar;

    /**
     * Constructor of class ConverterBase
     * @param xmlSchema
     * @param relaxng
     */
    public ConverterBase() {}
    
    protected void init() {
        this.relaxng = new RelaxNGSchema();
        this.xsdAttributeDefineRefMap = new HashMap<eu.fox7.schematoolkit.xsd.om.Attribute, String>();
        this.xsdAttributeGroupDefineRefMap = new HashMap<AttributeGroup, String>();
        this.xsdElementDefineRefMap = new HashMap<eu.fox7.schematoolkit.xsd.om.Element, String>();
        this.xsdGroupDefineRefMap = new HashMap<eu.fox7.schematoolkit.xsd.om.Group, String>();
        this.xsdTypeDefineRefMap = new HashMap<Type, String>();
    }

    /**
     * Constructor of class ConverterBase
     *
     * @param xmlSchema     The source XML XSDSchema object for the conversion
     *                      process
     * @param relaxng       The target RELAX NG object for the conversion
     *                      process
     *
     * @param xsdAttributeDefineRefMap          Look-up table for holding the mapping between an XML XSDSchema attribute and the corresponding conversion result RELAX NG pattern
     * @param xsdAttributeGroupDefineRefMap     Look-up table for holding the mapping between an XML XSDSchema attributeGroup and the corresponding conversion result RELAX NG pattern
     * @param xsdElementDefineRefMap            Look-up table for holding the mapping between an XML XSDSchema element and the corresponding conversion result RELAX NG pattern
     * @param xsdGroupDefineRefMap              Look-up table for holding the mapping between an XML XSDSchema group and the corresponding conversion result RELAX NG pattern
     * @param xsdTypeDefineRefMap               Look-up table for holding the mapping between an XML XSDSchema type and the corresponding conversion result RELAX NG pattern
     */
    public ConverterBase(XSDSchema xmlSchema,
            RelaxNGSchema relaxng,
            HashMap<eu.fox7.schematoolkit.xsd.om.Attribute, String> xsdAttributeDefineRefMap,
            HashMap<AttributeGroup, String> xsdAttributeGroupDefineRefMap,
            HashMap<eu.fox7.schematoolkit.xsd.om.Element, String> xsdElementDefineRefMap,
            HashMap<eu.fox7.schematoolkit.xsd.om.Group, String> xsdGroupDefineRefMap,
            HashMap<Type, String> xsdTypeDefineRefMap) {
        this.xmlSchema = xmlSchema;
        this.relaxng = relaxng;
        this.xsdAttributeDefineRefMap = xsdAttributeDefineRefMap;
        this.xsdAttributeGroupDefineRefMap = xsdAttributeGroupDefineRefMap;
        this.xsdElementDefineRefMap = xsdElementDefineRefMap;
        this.xsdGroupDefineRefMap = xsdGroupDefineRefMap;
        this.xsdTypeDefineRefMap = xsdTypeDefineRefMap;
        this.grammar = (Grammar) this.relaxng.getRootPattern();
    }

    /**
     * Getter for the RelaxNGSchema
     * @return RelaxNGSchema        Current version of the RelaxNGSchema
     */
    public RelaxNGSchema getRelaxNGSchema() {
        return relaxng;
    }

    /**
     * Getter for the XML XSDSchema
     * @return XSDSchema        Returns the result of the conversion progress.
     */
    public XSDSchema getXmlSchema() {
        return xmlSchema;
    }

    /**
     * Getter for the global set of all usedDefineNames
     * @return HashSet<String>      used define names
     */
    public HashSet<String> getUsedDefineNames() {
        // Currently only a grammar pattern is a valid root-pattern for the
        // conversion progress
        Grammar grammar = (Grammar) this.relaxng.getRootPattern();
        return grammar.getDefinedPatternNames();
    }

    /**
     * Getter for the root pattern default namespace
     * @return Namespace   the root pattern default namespace
     */
    public Namespace getRootPatternDefaultNamespace() {
        // Currently only a grammar pattern is a valid root-pattern for the
        // conversion progress
        Grammar grammar = (Grammar) this.relaxng.getRootPattern();
        return grammar.getAttributeNamespace();
    }


    /**
     * Helper method for setting a given pattern to a define pattern and return
     * a generated ref pattern as reference to this define. Defines can occur
     * under a grammar pattern in RELAX NG. For the conversion from XML XSDSchema
     * to RELAX NG there is always a grammar-pattern as the root-pattern for the
     * generated RELAX NG schema.
     * @param name      name for the define-pattern. This has to be unique under
     *                  a given grammar. To avoid duplicate names the method
     *                  "generateUniqueDefineName" from this class can be used.
     * @param pattern   pattern, which will be set into the define pattern.
     * @return Ref      reference to the generated define pattern.
     */
    public Ref setPatternToDefine(String name, Pattern pattern) {
        LinkedList<Pattern> tempList = new LinkedList<Pattern>();
        tempList.add(pattern);
        Ref ref = this.setPatternToDefine(name, tempList);
        return ref;
    }

    /**
     * Helper method for setting a given patternList to a define pattern and
     * return a generated ref pattern as reference to this define. Defines can
     * occur under a grammar pattern in RELAX NG. For the conversion from
     * XML XSDSchema to RELAX NG there is always a grammar-pattern as the
     * root-pattern for the generated RELAX NG schema.
     * @param name          name for the define-pattern. This has to be unique under
     *                      a given grammar. To avoid duplicate names the method
     *                      "generateUniqueDefineName" from this class can be used.
     * @param patternList   patternList, which will be set into the define pattern.
     * @return Ref          reference to the generated define pattern.
     */
    public Ref setPatternToDefine(String name, LinkedList<Pattern> patternList) {
        // Currently only a grammar pattern is a valid root-pattern for the
        // conversion progress. This is a valid way for generating a RELAX NG
        // schema with equal expressiveness.
        Grammar grammar = (Grammar) this.relaxng.getRootPattern();

        // Generate a new define pattern with the given name
        Define define = new Define(name);
        // Add each part of the given patternList to this define pattern
        for (Pattern pattern: patternList) {
            define.addPattern(pattern);
        }

        // Register the new define pattern in the root grammar pattern
        grammar.addDefinePattern(define);

        // Generate a new Ref object and return it
        Ref ref = new Ref(name, grammar);
        return ref;
    }

    /**
     * Generate a unique String, that can be used as a define name.
     * @param name      suggested name, if this name is not already used, it is
     *                  a valid unique name and will be returned untouched. If
     *                  it if already used, a new unique name will be
     *                  constructed on the base of the given name.
     * @return name     a unique string, that can be used as a new define name.
     */
    public String generateUniqueDefineName(String name) {
        String returnString = "";
        // Check if the suggested name is already used in this schema
        if (grammar.getDefinedPatternNames().contains(name)) {
            returnString = name + "_" + grammar.getDefinedPatternNames().size();
        } else {
            returnString = name;
        }
        return returnString;
    }
    
    public String generateUniqueDefineName(QualifiedName name) {
    	return generateUniqueDefineName(name.getName());
    }

    /**
     * Simplify a given pattern in the case of a group, a choice or an interleave
     * which only contains one child pattern. In this case the child pattern
     * will be returned directly, because the framing pattern has no semantical
     * meaning.
     *
     * @param pattern       source for the simplification
     * @return pattern      result of the simplification
     */
    public Pattern simplifyPatternStructure(Pattern pattern) {
        if (pattern instanceof Group && ((Group) pattern).getPatterns().size() == 1) {
            return ((Group) pattern).getPatterns().getFirst();
        } else if (pattern instanceof Choice && ((Choice) pattern).getPatterns().size() == 1) {
            return ((Choice) pattern).getPatterns().getFirst();
        } else if (pattern instanceof Interleave && ((Interleave) pattern).getPatterns().size() == 1) {
            return ((Interleave) pattern).getPatterns().getFirst();
        } else {
            return pattern;
        }
    }

    /**
     * Generate a RELAX NG pattern structure for representing a semantically
     * equal structure to the XML schema anyType. In RELAX NG there is no
     * corresponding component, that could be used in this case directly :-(
     * @param anyType   XML XSDSchema anyType object
     * @return pattern  RELAX NG pattern that represents the XML XSDSchema anyType
     */
    public Pattern generateAnyTypePattern(Type anyType) {
        /**
         * <define name="anyType">
         *      <zeroOrMore>
         *          <choice>
         *
         *              <element>
         *                  <anyName/>
         *                  <ref name="anyType"/>
         *              </element>
         *
         *              <text/>
         *
         *              <attribute>
         *                  <anyName/>
         *              </attribute>
         *
         *          </choice>
         *      </zeroOrMore>
         * </define>
         */
        // Check if this has been already generated, return the pattern
        if (this.xsdTypeDefineRefMap.containsKey(anyType)) {
            return new Ref(this.xsdTypeDefineRefMap.get(anyType), grammar);
        } else {
            // Generate a new structure for representing the XML schema anyType
            Element element = new Element();
            AnyName anyName = new AnyName();
            Text text = new Text();
            // Generate a pattern for representing a XML XSDSchema anyAttribute
            Attribute attribute = (Attribute) ((ZeroOrMore) this.generateAnyAttributePattern(new AnyAttribute())).getPatterns().getFirst();
            ZeroOrMore zeroOrMore = new ZeroOrMore();

            String refName = this.generateUniqueDefineName(anyType.getName());
            Ref ref = this.setPatternToDefine(refName, zeroOrMore);

            this.xsdTypeDefineRefMap.put(anyType, ref.getRefName());

            element.setNameClass(anyName);
            element.addPattern(ref);
            Choice choice = new Choice();
            choice.addPattern(element);
            choice.addPattern(text);
            choice.addPattern(attribute);
            zeroOrMore.addPattern(choice);

            return ref;
        }
    }

    /**
     * Generate a RELAX NG pattern structure for representing a semantically
     * equal structure to the XML schema anyPattern <any/>, which is a wildcard
     * for any kind of one element. In RELAX NG there is no corresponding
     * component, that could be used in this case directly :-(
     * One has to define an element pattern with an any-name.
     * @param anyPattern   XML XSDSchema anyPattern object
     * @return pattern  RELAX NG pattern that represents the XML XSDSchema <any/>
     */
    public Pattern generateAnyElementPattern(AnyPattern anyPattern) {

        // Prepare a element pattern from RELAX NG
        Element element = new Element();

        // Check the namespace attribute of the given anyPattern from XML schema
        if (!anyPattern.isAnyNamespace()) {
        	Collection<Namespace> namespaces = anyPattern.getNamespaces();

        	NameClass nameClass = getNameClassForAnyPatternNamespaces(namespaces);

        	element.setNameClass(nameClass);
        } else {
            // There is no namespace defined --> any is allowed
            AnyName anyName = new AnyName();
            element.setNameClass(anyName);
        }

        // The wildcard element needs a RELAX NG pattern structure representing
        // the anyType from XML XSDSchema
        QualifiedName anytypeName = new QualifiedName(xmlSchema.getNamespaceByURI(XSDSchema.XMLSCHEMA_NAMESPACE),"anyType");
        Type type = this.xmlSchema.getType(anytypeName);


        // Check if there is already a anyType in the symbolTable from XML schema
        if (type==null) {
            // There is no object representing the anyType, so create a new one.
            SimpleType simpleType = new SimpleType(anytypeName, null);
            simpleType.setIsAnonymous(false);
            // return the new type
            type = simpleType;
        }

        // Generate a pattern structure representing the anyType object from
        // XML XSDSchema in RELAX NG.
        element.addPattern(this.generateAnyTypePattern(type));

        return element;
    }

    private NameClass getNameClassForAnyPatternNamespaces(Collection<Namespace> namespaces) {
    	// Prepare a name choice as result container for names of the
    	// RELAX NG Element
    	NameClassChoice nameClassChoice = new NameClassChoice();
    	LinkedHashSet<Namespace> usedNamespaces = new LinkedHashSet<Namespace>();

    	// Fetch the targetNamespace of the xmlSchema object
    	Namespace targetNamespace = this.xmlSchema.getTargetNamespace();

    	// Walk over all namespace values from the XML XSDSchema attribute from
    	// the anyPattern
    	for (Namespace namespace: namespaces) {
    		if (namespace.equals(Namespace.OTHER_NAMESPACE)) {
    			// Case: ##other -  This represents all namespaces except
    			//                  the target namespace or the empty namespace.
    			AnyName anyName = new AnyName();
    			NsName nsName = new NsName();
    			nsName.setAttributeNamespace(targetNamespace);
    			anyName.addExceptName(nsName);
    			nameClassChoice.addChoiceName(anyName);
    		} else if (namespace.equals(Namespace.TARGET_NAMESPACE)) {
    			// Case: ##targetNamespace - This represents the
    			//                           targetNamespace of the given
    			//                           XML XSDSchema document without
    			//                           naming it directly
    			if (!usedNamespaces.contains(targetNamespace)) {
    				NsName nsName = new NsName();
    				nsName.setAttributeNamespace(targetNamespace);
    				usedNamespaces.add(targetNamespace);
    				nameClassChoice.addChoiceName(nsName);
    			}
    		} else if (namespace.equals(Namespace.LOCAL_NAMESPACE)) {
    			// Case: ##local -  This represents all local namespaces
    			//                  defined in the XML XSDSchema document or
    			//                  imported through external schemas
    			if (!usedNamespaces.contains(Namespace.EMPTY_NAMESPACE)) {
    				NsName nsName = new NsName();
    				nsName.setAttributeNamespace(Namespace.EMPTY_NAMESPACE);
    				usedNamespaces.add(Namespace.EMPTY_NAMESPACE);
    				nameClassChoice.addChoiceName(nsName);
    			}
    		} else {
    			// Case: name -  This represents a specified name of a
    			//               namespace
    			if (!usedNamespaces.contains(namespace)) {
    				NsName nsName = new NsName();
    				nsName.setAttributeNamespace(namespace);
    				usedNamespaces.add(namespace);
    				nameClassChoice.addChoiceName(nsName);
    			}
    		}
    	}
    	
    	if (nameClassChoice.getChoiceNames().size()==1)
    		return nameClassChoice.getChoiceNames().iterator().next();
    	else
    		return nameClassChoice;
    }

	/**
     * Generate a RELAX NG pattern structure for representing a semantically
     * equal structure to the XML schema anyAttribute <anyAttribute/>, which is
     * a wildcard for a set of attributes. In RELAX NG there is no corresponding
     * component, that could be used in this case directly :-(
     * One has to define an attribute with an any-name.
     * @param anyAttribute   XML XSDSchema anyAttribute object
     * @return pattern  RELAX NG pattern that represents the XML XSDSchema <anyAttribute/>
     */
    public Pattern generateAnyAttributePattern(AnyAttribute anyAttribute) {

        // Create a new RELAX NG attribute pattern
        Attribute attribute = new Attribute();

        // Check the namespace attribute of the given anyAttribute from XML schema
        if (!anyAttribute.isAnyNamespace()) {
            NameClass nameClass = this.getNameClassForAnyPatternNamespaces(anyAttribute.getNamespaces());

            attribute.setNameClass(nameClass);
        } else {
            // There is no namespace defined --> any is allowed
            AnyName anyName = new AnyName();
            attribute.setNameClass(anyName);
        }

        // An "any"-attribute (attribute with an any-name) in RELAX NG has to
        // put into a zeroOrMore pattern in order to be valid.
        ZeroOrMore zeroOrMore = new ZeroOrMore();
        zeroOrMore.addPattern(attribute);

        return zeroOrMore;
    }

    /**
     * Collect all local namespaces. These are defined by the basis schema
     * and all imported external schemas.
     *
     * @param xmlSchema                 The source of the XML XSDSchema to RELAX NG conversion process
     * @param localNamespaces           The list of all found local namespaces. This will be filled within this method.
     * @param alreadySeenSchemas        A List of already seen schemas - this is used to avoid the duplicate handling of already seen schemas.
     */
    protected void getLocalNamespaces(XSDSchema xmlSchema, LinkedHashSet<Namespace> localNamespaces, LinkedHashSet<XSDSchema> alreadySeenSchemas) {
        // Get the targetNamespace of the current xmlSchema object.
        Namespace targetNamespace = xmlSchema.getTargetNamespace();

        // Add the targetNamespace to the list of local namespaces.
        localNamespaces.add(targetNamespace);
        // Add the current schema to the list of already seen schemas.
        alreadySeenSchemas.add(xmlSchema);

        // Walk over all foreignSchemas of the current schema and handle imports
        if (xmlSchema.getForeignSchemas() != null && !xmlSchema.getForeignSchemas().isEmpty()) {
            for (Iterator<ForeignSchema> it = xmlSchema.getForeignSchemas().iterator(); it.hasNext();) {
                ForeignSchema foreignSchema = it.next();
                // Check if the current foreignSchema is an instance of ImportedSchema.
                if (foreignSchema instanceof ImportedSchema) {
                    ImportedSchema importedSchema = (ImportedSchema) foreignSchema;
                    // If the namespace is not null, add the targetNamespace to the list of local namespaces
                    if (importedSchema.getNamespace() != null) {
                        localNamespaces.add(importedSchema.getNamespace());
                        if (!alreadySeenSchemas.contains(importedSchema.getSchema())) {
                            // Recurse into the imported schmea
                            this.getLocalNamespaces(importedSchema.getSchema(), localNamespaces, alreadySeenSchemas);
                        }
                    }
                }
            }
        }
    }

    /**
     * Check if a given type (via namespace and type name) is a XML XSDSchema
     * built-in datatype.
     * @param namespace     Namespace of the type
     * @param typeName      Local name of the type
     * @return boolean      True, if the type is a built-in datatype from XML XSDSchema
     */
    protected boolean isXMLSchemaBuiltInType(QualifiedName name) {
        boolean builtInType = false;

        // There is an enumeration with all local names from built-in datatypes
        // from XML XSDSchema defined in "XMLSchemaBuiltInDatatypes".
        // A given name can be checked via the method "checkXMLSchemaBuiltInTypeName"
        if (name.getNamespace().equals(XSDSchema.XMLSCHEMA_NAMESPACE)) {
            if (checkXMLSchemaBuiltInTypeName(name.getName())) {
                builtInType = true;
            }
        }
        return builtInType;
    }

    /**
     * Method for checking a given typename against the
     * XMLSchemaBuiltInDatatypes enumeration
     * @param typeName      String to check for
     * @return boolean      true, if this name is defined in the enum
     */
    private boolean checkXMLSchemaBuiltInTypeName(String typeName) {
        boolean checkValidXMLSchemaType = false;
        // The typeName has to be not null
        if (typeName != null) {
            String test = null;
            // If a value is not set in an enumeration it could throw an exception
            // --> catch this exception here and handle it directly.
            try {
                test = XMLSchemaBuiltInDatatypes.valueOf(typeName.toUpperCase()).toString();
            } catch (IllegalArgumentException e) {
            }
            if (test != null) {
                checkValidXMLSchemaType = true;
            }
        }
        return checkValidXMLSchemaType;
    }

    /**
     * Collect all attribute names from attributes with ID-type recursively from
     * a given schema.
     *
     * @param xmlSchema                     The source of the current conversion process.
     * @param attributeNamesWithIDType      Set of all found attribute names of the ID-type kind.
     * @param alreadySeenSchemas            A List of already seen schemas - this is used to avoid the duplicate handling of already seen schemas.
     */
    protected void collectAttributeNamesWithIDTypeRecursively(XSDSchema xmlSchema, LinkedHashSet<String> attributeNamesWithIDType, LinkedHashSet<XSDSchema> alreadySeenSchemas) {

        alreadySeenSchemas.add(xmlSchema);

        // Check all top-level attributes
        for (Iterator<eu.fox7.schematoolkit.xsd.om.Attribute> it = xmlSchema.getAttributes().iterator(); it.hasNext();) {
            eu.fox7.schematoolkit.xsd.om.Attribute attribute = it.next();
            if (attribute.getSimpleType() != null) {
                // An attribute is of type "ID", if it has the datatype "ID", "IDREF" or "IDREFS" from the XML XSDSchema namespace
                if (this.isXMLSchemaBuiltInType(attribute.getSimpleType().getNamespace(), attribute.getSimpleType().getLocalName())
                        && (attribute.getSimpleType().getLocalName().toUpperCase().equals("ID")
                        || attribute.getSimpleType().getLocalName().toUpperCase().equals("IDREF")
                        || attribute.getSimpleType().getLocalName().toUpperCase().equals("IDREFS"))) {
                    attributeNamesWithIDType.add(attribute.getName());
                }
            }
        }

        // Check all attributes within types
        for (Iterator<eu.fox7.schematoolkit.xsd.om.Type> it = xmlSchema.getTypeSymbolTable().getAllReferencedObjects().iterator(); it.hasNext();) {
            eu.fox7.schematoolkit.xsd.om.Type type = it.next();

            if (type instanceof ComplexType) {
                ComplexType complexType = (ComplexType) type;

                // Check attributes from all complexTypes
                for (Iterator<eu.fox7.schematoolkit.common.AttributeParticle> it1 = complexType.getAttributes().iterator(); it1.hasNext();) {
                    eu.fox7.schematoolkit.common.AttributeParticle attributeParticle = it1.next();
                    attributeNamesWithIDType.addAll(this.collectAttributeNamesWithIDTypeFromAttributeParticle(attributeParticle));
                }

                // Walk into the content of a complexType and check its inheritance
                if (complexType.getContent() != null && complexType.getContent() instanceof ComplexContentType) {

                    // Case "complexContent":
                    ComplexContentType complexContentType = (ComplexContentType) complexType.getContent();
                    if (complexContentType.getInheritance() != null && complexContentType.getInheritance() instanceof ComplexContentExtension) {

                        // Case "extension":
                        ComplexContentExtension complexContentExtension = (ComplexContentExtension) complexContentType.getInheritance();
                        for (Iterator<eu.fox7.schematoolkit.common.AttributeParticle> it1 = complexContentExtension.getAttributes().iterator(); it1.hasNext();) {
                            eu.fox7.schematoolkit.common.AttributeParticle attributeParticle = it1.next();
                            attributeNamesWithIDType.addAll(this.collectAttributeNamesWithIDTypeFromAttributeParticle(attributeParticle));
                        }
                    } else if (complexContentType.getInheritance() != null && complexContentType.getInheritance() instanceof ComplexContentRestriction) {

                        // Case "restriction":
                        ComplexContentRestriction complexContentRestriction = (ComplexContentRestriction) complexContentType.getInheritance();
                        for (Iterator<eu.fox7.schematoolkit.common.AttributeParticle> it1 = complexContentRestriction.getAttributes().iterator(); it1.hasNext();) {
                            eu.fox7.schematoolkit.common.AttributeParticle attributeParticle = it1.next();
                            attributeNamesWithIDType.addAll(this.collectAttributeNamesWithIDTypeFromAttributeParticle(attributeParticle));
                        }
                    }
                } else if (complexType.getContent() != null && complexType.getContent() instanceof SimpleContentType) {

                    // Case "simpleContent":
                    SimpleContentType simpleContentType = (SimpleContentType) complexType.getContent();
                    if (simpleContentType.getInheritance() != null && simpleContentType.getInheritance() instanceof SimpleContentExtension) {

                        // Case "extension":
                        SimpleContentExtension simpleContentExtension = (SimpleContentExtension) simpleContentType.getInheritance();
                        for (Iterator<eu.fox7.schematoolkit.common.AttributeParticle> it1 = simpleContentExtension.getAttributes().iterator(); it1.hasNext();) {
                            eu.fox7.schematoolkit.common.AttributeParticle attributeParticle = it1.next();
                            attributeNamesWithIDType.addAll(this.collectAttributeNamesWithIDTypeFromAttributeParticle(attributeParticle));
                        }
                    } else if (simpleContentType.getInheritance() != null && simpleContentType.getInheritance() instanceof SimpleContentRestriction) {

                        // Case "restriction":
                        SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleContentType.getInheritance();
                        for (Iterator<eu.fox7.schematoolkit.common.AttributeParticle> it1 = simpleContentRestriction.getAttributes().iterator(); it1.hasNext();) {
                            eu.fox7.schematoolkit.common.AttributeParticle attributeParticle = it1.next();
                            attributeNamesWithIDType.addAll(this.collectAttributeNamesWithIDTypeFromAttributeParticle(attributeParticle));
                        }
                    }
                }
            }
        }

        // Check all attributes within attributeGroups
        for (Iterator<eu.fox7.schematoolkit.xsd.om.AttributeGroup> it = xmlSchema.getAttributeGroups().iterator(); it.hasNext();) {
            eu.fox7.schematoolkit.xsd.om.AttributeGroup attributeGroup = it.next();
            attributeNamesWithIDType.addAll(this.collectAttributeNamesWithIDTypeFromAttributeParticle(attributeGroup));
        }

        // Walk trough all foreignSchemas.
        if (xmlSchema.getForeignSchemas() != null && !xmlSchema.getForeignSchemas().isEmpty()) {
            for (Iterator<ForeignSchema> it = xmlSchema.getForeignSchemas().iterator(); it.hasNext();) {
                ForeignSchema foreignSchema = it.next();
                if (!alreadySeenSchemas.contains(foreignSchema.getSchema())) {
                    this.collectAttributeNamesWithIDTypeRecursively(foreignSchema.getSchema(), attributeNamesWithIDType, alreadySeenSchemas);
                }
            }
        }

    }

    /**
     * Collect all attribute names from attributes with ID-type recursively from
     * a given attributepParticle.
     * @param attributeParticle         The source for the check
     * @return LinkedHashSet<String>    Set of found names of attributes with ID-type
     */
    private LinkedHashSet<String> collectAttributeNamesWithIDTypeFromAttributeParticle(AttributeParticle attributeParticle) {
        LinkedHashSet<String> currentAttributeNamesWithIDType = new LinkedHashSet<String>();

        if (attributeParticle instanceof eu.fox7.schematoolkit.xsd.om.Attribute) {

            // Case "attribute":
            eu.fox7.schematoolkit.xsd.om.Attribute attribute = (eu.fox7.schematoolkit.xsd.om.Attribute) attributeParticle;
            if (attribute.getSimpleType() != null) {
                // An attribute is of type "ID", if it has the datatype "ID", "IDREF" or "IDREFS" from the XML XSDSchema namespace
                if (this.isXMLSchemaBuiltInType(attribute.getSimpleType().getNamespace(), attribute.getSimpleType().getLocalName())
                        && (attribute.getSimpleType().getLocalName().toUpperCase().equals("ID")
                        || attribute.getSimpleType().getLocalName().toUpperCase().equals("IDREF")
                        || attribute.getSimpleType().getLocalName().toUpperCase().equals("IDREFS"))) {
                    currentAttributeNamesWithIDType.add(attribute.getName());
                }
            }
        } else if (attributeParticle instanceof eu.fox7.schematoolkit.xsd.om.AttributeRef) {

            // Case "attributeRef":
            eu.fox7.schematoolkit.xsd.om.AttributeRef attributeRef = (AttributeRef) attributeParticle;
            currentAttributeNamesWithIDType.addAll(collectAttributeNamesWithIDTypeFromAttributeParticle(attributeRef.getAttribute()));
        } else if (attributeParticle instanceof eu.fox7.schematoolkit.xsd.om.AttributeGroup) {

            // Case "attributeGroup":
            eu.fox7.schematoolkit.xsd.om.AttributeGroup attributeGroup = (AttributeGroup) attributeParticle;

            for (Iterator<AttributeParticle> it = attributeGroup.getAttributeParticles().iterator(); it.hasNext();) {
                AttributeParticle innerAttributeParticle = it.next();
                currentAttributeNamesWithIDType.addAll(collectAttributeNamesWithIDTypeFromAttributeParticle(innerAttributeParticle));
            }
        } else if (attributeParticle instanceof eu.fox7.bonxai.xsd.AttributeGroupRef) {

            // Case "attributeGroupRef":
            eu.fox7.bonxai.xsd.AttributeGroupRef attributeGroupRef = (AttributeGroupRef) attributeParticle;
            currentAttributeNamesWithIDType.addAll(collectAttributeNamesWithIDTypeFromAttributeParticle(attributeGroupRef.getAttributeGroup()));
        }
        return currentAttributeNamesWithIDType;
    }

    /**
     * Getter for all attribute names from attributes with ID-type defined in a
     * given namespace.
     * @param namespace                 Namespace for the attributes
     * @return LinkedHashSet<String>    Set of all found attribute names in the given namespace with ID-type
     */
    private LinkedHashSet<String> getAttributeIDTypeNamesForNamespace(String namespace) {
        LinkedHashSet<String> names = new LinkedHashSet<String>();
        for (Iterator<String> it = this.attributeNamesWithIDType.iterator(); it.hasNext();) {
            String fqName = it.next();
            if (getNamespace(fqName).equals(namespace)) {
                names.add(fqName);
            }
        }
        return names;
    }

    /**
     * Getter for all attribute names from attributes with ID-type defined in
     * OTHER namespaces than the given namespace.
     * @param namespace                 Namespace to exclude
     * @return LinkedHashSet<String>    Set of all found attribute names with ID-type not in the given namespace
     */
    private LinkedHashSet<String> getAttributeIDTypeNamesForOTHERNamespaces(String namespace) {
        LinkedHashSet<String> names = new LinkedHashSet<String>();
        for (Iterator<String> it = this.attributeNamesWithIDType.iterator(); it.hasNext();) {
            String fqName = it.next();
            if (!getNamespace(fqName).equals(namespace)) {
                names.add(fqName);
            }
        }
        return names;
    }

}
