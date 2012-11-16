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

package eu.fox7.schematoolkit.converter.relaxng2xsd;

import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.converter.relaxng2xsd.exceptions.*;
import eu.fox7.schematoolkit.relaxng.om.*;
import eu.fox7.schematoolkit.xsd.om.SimpleContentFixableRestrictionProperty;
import eu.fox7.schematoolkit.xsd.om.SimpleContentList;
import eu.fox7.schematoolkit.xsd.om.SimpleContentRestriction;
import eu.fox7.schematoolkit.xsd.om.SimpleContentUnion;
import eu.fox7.schematoolkit.xsd.om.SimpleType;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Class PatternConverter
 * @author Lars Schmidt
 */
public class PatternSimpleTypeConverter extends ConverterBase {

    /**
     * Constructor of the class PatternSimpleTypeConverter
     * @param xmlSchema             target of the conversion
     * @param relaxng               source of the conversion
     * @param patternInformation    global map of pattern information
     * @param usedLocalNames        globale set of all used local names
     * @param usedLocalTypeNames    global set of already used local type names
     */
    public PatternSimpleTypeConverter(XSDSchema xmlSchema, RelaxNGSchema relaxng, HashMap<Pattern, HashSet<String>> patternInformation, HashSet<String> usedLocalNames, HashSet<String> usedLocalTypeNames) {
        super(xmlSchema, relaxng, patternInformation, usedLocalNames, usedLocalTypeNames);
    }

    /**
     * Generate a XML XSDSchema simpleType corresponding to the given RELAX NG pattern
     * This is a recursive method
     * @param pattern           source of the creation
     * @param parentList        information about the already seen parent patterns to avoid infinity loops
     * @return SimpleType       result of the creation
     * @throws Exception
     */
    public SimpleType generateSimpleTypeForPattern(Pattern pattern, LinkedList<Pattern> parentList) throws Exception {

        parentList.add(pattern);

        SimpleType resultingSimpleType = null;

        if (pattern instanceof eu.fox7.schematoolkit.relaxng.om.Data) {

            // Case: Data

            eu.fox7.schematoolkit.relaxng.om.Data data = (eu.fox7.schematoolkit.relaxng.om.Data) pattern;
            Namespace typeNamespace = null;
            if (data.getAttributeDatatypeLibrary() == null || data.getAttributeDatatypeLibrary().equals("http://www.w3.org/2001/XMLSchema-datatypes")) {
                typeNamespace = this.xmlSchema.getNamespaceByIdentifier(XSDSchema.XMLSCHEMA_NAMESPACE);
            } else {
                typeNamespace = data.getAttributeNamespace();
            }

            /**
             *
             * An EXCEPT of a data pattern cannot be mapped to XML XSDSchema.
             * Is it possible to build something with similar semantics in XML XSDSchema?
             * <element name="bob" xmlns="http://relaxng.org/ns/structure/1.0">
             *     <data type="integer" datatypeLibrary="http://www.w3.org/2001/XMLSchema-datatypes">
             *         <except>
             *             <data type="boolean"/>
             *         </except>
             *     </data>
             * </element>
             *
             * An except element with a data parent can contain only data, value, and choice elements.
             * data.addExceptPattern(pattern);
             *
             */
            if ((data.getAttributeDatatypeLibrary() == null || (data.getAttributeDatatypeLibrary() != null && data.getAttributeDatatypeLibrary().equals(""))) && checkXMLSchemaBuiltInTypeName(data.getType())) {
                typeNamespace = this.xmlSchema.getNamespaceByIdentifier(XSDSchema.XMLSCHEMA_NAMESPACE);
            }

            Type type = this.checkOrCreateBuiltInType(typeNamespace, data.getType());

            // Case: There are inner param-patterns defined
            if (data.getParams() != null && !data.getParams().isEmpty()) {
                QualifiedName fqTypeName = getNewLocalTypename(data.getAttributeNamespace());
                SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(type.getName());

                LinkedList<Param> patternParamList = new LinkedList<Param>();

                boolean isFirstPatternParam = true;
                for (Iterator<Param> it = data.getParams().iterator(); it.hasNext();) {
                    Param param = it.next();
                    if (!param.getName().equals("pattern")) {
                        setParamAsFacetToRestriction(param, simpleContentRestriction);
                    } else {
                        if (isFirstPatternParam) {
                            isFirstPatternParam = false;
                            setParamAsFacetToRestriction(param, simpleContentRestriction);
                        } else {
                            patternParamList.add(param);
                        }
                    }
                }

                SimpleType simpleType = new SimpleType(fqTypeName, simpleContentRestriction, true);

                if (!patternParamList.isEmpty()) {

                    SimpleType newSimpleType = simpleType;

                    for (Param param: patternParamList) {
                        QualifiedName fqTypeNameInner = getNewLocalTypename(data.getAttributeNamespace());
                        SimpleContentRestriction simpleContentRestrictionInner = new SimpleContentRestriction(newSimpleType.getName());
                        setParamAsFacetToRestriction(param, simpleContentRestrictionInner);
                        newSimpleType = new SimpleType(fqTypeNameInner, simpleContentRestrictionInner, true);
                    }
                    simpleType = newSimpleType;
                } else {
                    simpleType = new SimpleType(fqTypeName, simpleContentRestriction, true);
                }
                resultingSimpleType = simpleType;

            } else {

                // Case: no inner param-patterns
                resultingSimpleType = (SimpleType) type;
            }

        } else if (pattern instanceof eu.fox7.schematoolkit.relaxng.om.List) {

            // Case: List

            eu.fox7.schematoolkit.relaxng.om.List list = (eu.fox7.schematoolkit.relaxng.om.List) pattern;
            QualifiedName fqTypeName = getNewLocalTypename(list.getAttributeNamespace());
            LinkedList<Type> memberTypes = new LinkedList<Type>();
            for (Pattern listPattern: list.getPatterns()) {
                SimpleType listInnerSimpleType = this.generateSimpleTypeForPattern(listPattern, new LinkedList<Pattern>());

                if (listInnerSimpleType != null) {
                    memberTypes.add(listInnerSimpleType);
                }
            }

            /*
             * <xs:simpleType>
             *      <xs:restriction>
             *          <xs:simpleType>
             *              <xs:list>
             *                  <xs:simpleType>
             *                      <xs:union memberTypes="xs:integer xs:string"/>
             *                  </xs:simpleType>
             *              </xs:list>
             *          </xs:simpleType>
             *          <xs:length value="2"/>
             *      </xs:restriction>
             * </xs:simpleType>
             */
            // merge List of Values of the same type to one enumeration-list
            memberTypes = optimizeSimpleTypeUnion(memberTypes);

            SimpleType innerInnerSimpleType = null;
            QualifiedName innerInnerFqTypeName = null;

            if (memberTypes.size() == 1) {
                innerInnerFqTypeName = memberTypes.getFirst().getName();
                innerInnerSimpleType = (SimpleType) memberTypes.getFirst();
            } else {
                SimpleContentUnion simpleContentUnion = new SimpleContentUnion(getTypeNames(memberTypes));

                innerInnerFqTypeName = getNewLocalTypename(list.getAttributeNamespace());
                innerInnerSimpleType = new SimpleType(innerInnerFqTypeName, simpleContentUnion, true);
            }
            
            SimpleContentList simpleContentList = new SimpleContentList(innerInnerSimpleType.getName());
            String innerLocalTypeName = "simpleType";
            if (this.usedLocalTypeNames.contains(innerLocalTypeName)) {
                innerLocalTypeName = innerLocalTypeName + "_" + this.usedLocalTypeNames.size();
            }
            this.usedLocalTypeNames.add(innerLocalTypeName);
            QualifiedName innerFqTypeName = new QualifiedName(list.getAttributeNamespace(),innerLocalTypeName);
            SimpleType innerSimpleType = new SimpleType(innerFqTypeName, simpleContentList, true);

            SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(innerSimpleType.getName());
            simpleContentRestriction.setLength(new SimpleContentFixableRestrictionProperty<Integer>(list.getPatterns().size(), false));
            SimpleType simpleType = new SimpleType(fqTypeName, simpleContentRestriction, true);
            resultingSimpleType = simpleType;

        } else if (pattern instanceof eu.fox7.schematoolkit.relaxng.om.Choice) {

            // Case: Choice

            eu.fox7.schematoolkit.relaxng.om.Choice choice = (eu.fox7.schematoolkit.relaxng.om.Choice) pattern;
            Namespace typeNamespace = choice.getAttributeNamespace();

            QualifiedName fqTypeName = getNewLocalTypename(typeNamespace);
            LinkedList<Type> memberTypes = new LinkedList<Type>();
            for (Pattern listPattern: choice.getPatterns()) {
                SimpleType listInnerSimpleType = this.generateSimpleTypeForPattern(listPattern, parentList);

                if (listInnerSimpleType != null) {
                    memberTypes.add(listInnerSimpleType);
                }
            }

            /*
             * <xs:simpleType>
             *      <xs:union memberTypes="xs:integer xs:string"/>
             * </xs:simpleType>
             */

            memberTypes = optimizeSimpleTypeUnion(memberTypes);

            if (memberTypes.size() != 1) {
                SimpleContentUnion simpleContentUnion = new SimpleContentUnion(getTypeNames(memberTypes));
                SimpleType simpleType = new SimpleType(fqTypeName, simpleContentUnion, true);
                resultingSimpleType = simpleType;
            } else {
                resultingSimpleType = (SimpleType) memberTypes.getFirst();
            }

        } else if (pattern instanceof eu.fox7.schematoolkit.relaxng.om.Optional) {

            // Case: Optional

            eu.fox7.schematoolkit.relaxng.om.Optional optional = (eu.fox7.schematoolkit.relaxng.om.Optional) pattern;
            Namespace typeNamespace = optional.getAttributeNamespace();

            QualifiedName fqTypeName = getNewLocalTypename(typeNamespace);
            LinkedList<Type> memberTypes = new LinkedList<Type>();
            for (Iterator<Pattern> it = optional.getPatterns().iterator(); it.hasNext();) {
                Pattern optionalPattern = it.next();
                SimpleType optionalInnerSimpleType = this.generateSimpleTypeForPattern(optionalPattern, parentList);
                if (optionalInnerSimpleType != null) {
                    memberTypes.add(optionalInnerSimpleType);
                }
            }

            /*
             * <xs:simpleType>                                      // <-- simpleType
             *      <xs:union>
             *          <xs:simpleType>                             // <-- one of memberTypes
             *              <xs:restriction base="xs:token">
             *                  <xs:enumeration value="3"/>
             *              </xs:restriction>
             *          </xs:simpleType>
             *          <xs:simpleType>                             // <-- optionalSimpleType
             *              <xs:restriction base="xs:token">
             *                  <xs:length value="0"/>
             *              </xs:restriction>
             *          </xs:simpleType>
             *      </xs:union>
             * </xs:simpleType>
             */

            Type baseType = checkOrCreateBuiltInType(this.xmlSchema.getNamespaceByURI(XSDSchema.XMLSCHEMA_NAMESPACE), "token");

            SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(baseType.getName());
            simpleContentRestriction.setLength(new SimpleContentFixableRestrictionProperty<Integer>(0, false));

            QualifiedName optionalFqTypeName = getNewLocalTypename(typeNamespace);

            SimpleType optionalSimpleType = new SimpleType(optionalFqTypeName, simpleContentRestriction, true);
            memberTypes.add(optionalSimpleType);

            memberTypes = optimizeSimpleTypeUnion(memberTypes);

            if (memberTypes.size() != 1) {
                
            	SimpleContentUnion simpleContentUnion = new SimpleContentUnion(getTypeNames(memberTypes));
                SimpleType simpleType = new SimpleType(fqTypeName, simpleContentUnion, true);
                resultingSimpleType = simpleType;
            } else {
                resultingSimpleType = (SimpleType) memberTypes.getFirst();
            }
        } else if (pattern instanceof eu.fox7.schematoolkit.relaxng.om.Value) {

            // Case: Value

            eu.fox7.schematoolkit.relaxng.om.Value value = (eu.fox7.schematoolkit.relaxng.om.Value) pattern;
            Namespace typeNamespace = value.getAttributeNamespace();

            QualifiedName fqTypeName = getNewLocalTypename(typeNamespace);

            LinkedList<String> enumerationValues = new LinkedList<String>();
            enumerationValues.add(value.getContent());

            /*
             * <xs:simpleType>
             *      <xs:restriction base="xs:token">
             *          <xs:enumeration value="4"/>
             *      </xs:restriction>
             * </xs:simpleType>
             */

            Namespace baseTypeNamespace = null;
            if (value.getAttributeDatatypeLibrary() == null || value.getAttributeDatatypeLibrary().equals("http://www.w3.org/2001/XMLSchema-datatypes")) {
                baseTypeNamespace = this.xmlSchema.getNamespaceByURI(XSDSchema.XMLSCHEMA_NAMESPACE);
            } else {
                baseTypeNamespace = value.getAttributeNamespace();
            }

            String innerLocalTypeName = null;
            if (value.getType() == null) {
                innerLocalTypeName = "token";
            } else {
                innerLocalTypeName = value.getType();
            }

            Type baseType = checkOrCreateBuiltInType(baseTypeNamespace, innerLocalTypeName);

            SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(baseType.getName());
            simpleContentRestriction.addEnumeration(enumerationValues);
            SimpleType simpleType = new SimpleType(fqTypeName, simpleContentRestriction, true);
            resultingSimpleType = simpleType;
        } else if (pattern instanceof Ref) {

            // Case: Ref

            Ref ref = (Ref) pattern;

            LinkedList<Pattern> tempParentlist = new LinkedList<Pattern>(parentList);
            tempParentlist.removeLast();
            if (!tempParentlist.contains(ref)) {

                LinkedList<Pattern> defineSimpleTypePatterns = new LinkedList<Pattern>();

                for (Iterator<Define> it = ref.getDefineList().iterator(); it.hasNext();) {
                    Define define = it.next();
                    for (Iterator<Pattern> it1 = define.getPatterns().iterator(); it1.hasNext();) {
                        Pattern innerPattern = it1.next();
                        if (this.patternInformation.get(innerPattern) != null && (this.patternInformation.get(innerPattern).contains("data") || this.patternInformation.get(innerPattern).contains("value") || this.patternInformation.get(innerPattern).contains("text")) || innerPattern instanceof Data || innerPattern instanceof Value || innerPattern instanceof Text) {
                            defineSimpleTypePatterns.add(innerPattern);
                        }
                    }
                }
                if (defineSimpleTypePatterns.size() > 0) {
                    for (Iterator<Pattern> it = defineSimpleTypePatterns.iterator(); it.hasNext();) {
                        Pattern currentSimpleTypeContentPattern = it.next();
                        if (resultingSimpleType != null) {
                            // Exception: There are multiple simpleType patterns under the current reference
                            throw new MultipleSimpleTypelException(ref, "Ref: " + ref.getRefName());
                        } else {
                            resultingSimpleType = this.generateSimpleTypeForPattern(currentSimpleTypeContentPattern, parentList);
                        }
                    }
                }
            }
        } else if (pattern instanceof Grammar) {

            // Case: Grammar

            Grammar grammar = (Grammar) pattern;

            LinkedList<Pattern> grammarSimpleTypePatterns = new LinkedList<Pattern>();

            for (Iterator<Pattern> it1 = grammar.getStartPatterns().iterator(); it1.hasNext();) {
                Pattern innerPattern = it1.next();
                if (this.patternInformation.get(innerPattern) != null && (this.patternInformation.get(innerPattern).contains("data") || this.patternInformation.get(innerPattern).contains("value") || this.patternInformation.get(innerPattern).contains("text")) || innerPattern instanceof Data || innerPattern instanceof Value || innerPattern instanceof Text) {
                    grammarSimpleTypePatterns.add(innerPattern);
                }
            }
            if (grammarSimpleTypePatterns.size() > 0) {
                for (Iterator<Pattern> it = grammarSimpleTypePatterns.iterator(); it.hasNext();) {
                    Pattern currentSimpleTypeContentPattern = it.next();
                    if (resultingSimpleType != null) {
                        // Exception: There are multiple simpleType patterns under the current reference
                        throw new MultipleSimpleTypelException(grammar, "grammar: start patterns");
                    } else {
                        resultingSimpleType = this.generateSimpleTypeForPattern(currentSimpleTypeContentPattern, parentList);
                    }
                }
            }
        } else if (pattern instanceof ParentRef) {

            // Case: ParentRef

            ParentRef parentRef = (ParentRef) pattern;

            LinkedList<Pattern> tempParentlist = new LinkedList<Pattern>(parentList);
            tempParentlist.removeLast();
            if (!tempParentlist.contains(parentRef)) {

                LinkedList<Pattern> defineSimpleTypePatterns = new LinkedList<Pattern>();

                for (Iterator<Define> it = parentRef.getDefineList().iterator(); it.hasNext();) {
                    Define define = it.next();
                    for (Iterator<Pattern> it1 = define.getPatterns().iterator(); it1.hasNext();) {
                        Pattern innerPattern = it1.next();
                        if (this.patternInformation.get(innerPattern) != null && (this.patternInformation.get(innerPattern).contains("data") || this.patternInformation.get(innerPattern).contains("value") || this.patternInformation.get(innerPattern).contains("text")) || innerPattern instanceof Data || innerPattern instanceof Value || innerPattern instanceof Text) {
                            defineSimpleTypePatterns.add(innerPattern);
                        }
                    }
                }
                if (defineSimpleTypePatterns.size() > 0) {
                    for (Iterator<Pattern> it = defineSimpleTypePatterns.iterator(); it.hasNext();) {
                        Pattern currentSimpleTypeContentPattern = it.next();
                        if (resultingSimpleType != null) {
                            // Exception: There are multiple simpleType patterns under the current reference
                            throw new MultipleSimpleTypelException(parentRef, "Ref: " + parentRef.getRefName());
                        } else {
                            resultingSimpleType = this.generateSimpleTypeForPattern(currentSimpleTypeContentPattern, parentList);
                        }
                    }
                }
            }
        } else if (pattern instanceof Text) {

            // Case: Text

            Type string = this.checkOrCreateBuiltInType(this.xmlSchema.getNamespaceByURI(XSDSchema.XMLSCHEMA_NAMESPACE), "string");
            resultingSimpleType = (SimpleType) string;
        }

//        if (resultingSimpleType == null) {
//            // Exception: There is an unexpected null simpleType under the given element
//            throw new SimpleTypeIsNullException(pattern, "Pattern: " + ((pattern != null) ? pattern.getClass().getName() : "is null!"));
//        }
        parentList.removeLast();

        return resultingSimpleType;
    }

    private Collection<QualifiedName> getTypeNames(LinkedList<Type> memberTypes) {
    	LinkedList<QualifiedName> memberTypeNames = new LinkedList<QualifiedName>();
    	for (Type memberType: memberTypes) {
    		memberTypeNames.add(memberType.getName());
    	}
    	return memberTypeNames;
	}

	/**
     * Check for or create a builtin type
     *
     * This method generated a simpleType for a given name (String) and namespace (string) regarding
     * to all known/specified XML XSDSchema simpleTypes.
     *
     * @param namespace     String namespace
     * @param typeName      String name
     * @return SymbolTableRef<Type>
     */
    private Type checkOrCreateBuiltInType(Namespace namespace, String typeName) {
        return new SimpleType(new QualifiedName(namespace,typeName), null);
    }

    /**
     * Method for checking a given typename against the XMLSchemaBuiltInDatatypes enumeration
     * @param typeName      String to check for
     * @return boolean      true, if this name is defined in the enum
     */
    private boolean checkXMLSchemaBuiltInTypeName(String typeName) {
        boolean checkValidXMLSchemaType = false;
        if (typeName != null) {
            String test = null;
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
     * Convert a given RELAX NG param into the corresponding
     * XML XSDSchema simpleType facet and set it to the given
     * simpleContentRestriction
     * @param param          source of the conversion
     * @param simpleContentRestriction      target of the conversion
     */
    private void setParamAsFacetToRestriction(Param param, SimpleContentRestriction simpleContentRestriction) {
        if (checkXMLSchemaBuiltInFacetName(param.getName())) {
            // Switch over possible facets
            switch (XMLSchemaBuiltInSimpleTypeFacetNames.valueOf(param.getName())) {
                case fractionDigits:
                    simpleContentRestriction.setFractionDigits(new SimpleContentFixableRestrictionProperty<Integer>(Integer.parseInt(param.getContent()), false));
                    break;
                case length:
                    simpleContentRestriction.setLength(new SimpleContentFixableRestrictionProperty<Integer>(Integer.parseInt(param.getContent()), false));
                    break;
                case maxExclusive:
                    simpleContentRestriction.setMaxExclusive(new SimpleContentFixableRestrictionProperty<String>(param.getContent(), false));
                    break;
                case maxInclusive:
                    simpleContentRestriction.setMaxInclusive(new SimpleContentFixableRestrictionProperty<String>(param.getContent(), false));
                    break;
                case maxLength:
                    simpleContentRestriction.setMaxLength(new SimpleContentFixableRestrictionProperty<Integer>(Integer.parseInt(param.getContent()), false));
                    break;
                case minLength:
                    simpleContentRestriction.setMinLength(new SimpleContentFixableRestrictionProperty<Integer>(Integer.parseInt(param.getContent()), false));
                    break;
                case minExclusive:
                    simpleContentRestriction.setMinExclusive(new SimpleContentFixableRestrictionProperty<String>(param.getContent(), false));
                    break;
                case minInclusive:
                    simpleContentRestriction.setMinInclusive(new SimpleContentFixableRestrictionProperty<String>(param.getContent(), false));
                    break;
                case pattern:
                    simpleContentRestriction.setPattern(new SimpleContentFixableRestrictionProperty<String>(param.getContent(), false));
                    break;
                case totalDigits:
                    simpleContentRestriction.setTotalDigits(new SimpleContentFixableRestrictionProperty<Integer>(Integer.parseInt(param.getContent()), false));
                    break;
            }
        }
    }

    /**
     * Method for checking a given facetname against the XMLSchemaBuiltInSimpleTypeFacetNames enumeration
     * @param facetName      String to check for
     * @return boolean      true, if this name is defined in the enum
     */
    private boolean checkXMLSchemaBuiltInFacetName(String facetName) {
        boolean checkValidXMLSchemaTypeFacet = false;
        String test = null;
        try {
            test = XMLSchemaBuiltInSimpleTypeFacetNames.valueOf(facetName).toString();
        } catch (IllegalArgumentException e) {
        }
        if (test != null) {
            checkValidXMLSchemaTypeFacet = true;
        }
        return checkValidXMLSchemaTypeFacet;
    }

    /**
     * Optimize the list of memberTypes of a SimpleTypeUnion
     * Enumerations of the same type are merged.
     * @param memberTypes               source of the conversion
     * @return LinkedList<SymbolTableRef<Type>>     result of the conversion
     */
    public LinkedList<Type> optimizeSimpleTypeUnion(LinkedList<Type> memberTypes) {
        LinkedList<Type> newMemberTypes = new LinkedList<Type>();
        LinkedList<Type> otherMemberTypes = new LinkedList<Type>();
        LinkedHashMap<QualifiedName, LinkedList<Type>> simpleTypeWithRestrictionEnumOrderedByBase = new LinkedHashMap<QualifiedName, LinkedList<Type>>();

        for (Type type: memberTypes) {
            SimpleType currentSimpleType = (SimpleType) type;

            if (currentSimpleType.getInheritance() != null && currentSimpleType.getInheritance() instanceof SimpleContentRestriction) {
                SimpleContentRestriction currentSimpleContentRestriction = (SimpleContentRestriction) currentSimpleType.getInheritance();
                if (currentSimpleContentRestriction.getEnumeration() != null && !currentSimpleContentRestriction.getEnumeration().isEmpty()) {
                    if (simpleTypeWithRestrictionEnumOrderedByBase.get(currentSimpleContentRestriction.getBaseType()) != null) {
                        simpleTypeWithRestrictionEnumOrderedByBase.get(currentSimpleContentRestriction.getBaseType()).add(type);
                    } else {
                        LinkedList<Type> newEnumTypeList = new LinkedList<Type>();
                        newEnumTypeList.add(type);
                        simpleTypeWithRestrictionEnumOrderedByBase.put(currentSimpleContentRestriction.getBaseType(), newEnumTypeList);
                    }
                } else {
                    otherMemberTypes.add(type);
                }
            } else {
                otherMemberTypes.add(type);
            }
        }
        for (QualifiedName currentBaseTypeName: simpleTypeWithRestrictionEnumOrderedByBase.keySet()) {
            if (simpleTypeWithRestrictionEnumOrderedByBase.get(currentBaseTypeName) != null && simpleTypeWithRestrictionEnumOrderedByBase.get(currentBaseTypeName).size() > 1) {

                Type firstRestrictionTypeRef = simpleTypeWithRestrictionEnumOrderedByBase.get(currentBaseTypeName).removeFirst();
                SimpleType firstRestrictionType = (SimpleType) firstRestrictionTypeRef;
                SimpleContentRestriction firstSimpleContentRestriction = (SimpleContentRestriction) firstRestrictionType.getInheritance();

                while (!simpleTypeWithRestrictionEnumOrderedByBase.get(currentBaseTypeName).isEmpty()) {
                    Type nextRestrictionTypeRef = simpleTypeWithRestrictionEnumOrderedByBase.get(currentBaseTypeName).removeFirst();
                    SimpleType nextRestrictionType = (SimpleType) nextRestrictionTypeRef;
                    SimpleContentRestriction nextSimpleContentRestriction = (SimpleContentRestriction) nextRestrictionType.getInheritance();
                    for (Iterator<String> it1 = nextSimpleContentRestriction.getEnumeration().iterator(); it1.hasNext();) {
                        String currentEnumString = it1.next();
                        if (!firstSimpleContentRestriction.getEnumeration().contains(currentEnumString)) {
                            firstSimpleContentRestriction.getEnumeration().add(currentEnumString);
                        }
                    }
                }
                newMemberTypes.add(firstRestrictionTypeRef);
            } else if (simpleTypeWithRestrictionEnumOrderedByBase.get(currentBaseTypeName) != null) {
                newMemberTypes.add(simpleTypeWithRestrictionEnumOrderedByBase.get(currentBaseTypeName).getFirst());
            }
        }
        newMemberTypes.addAll(otherMemberTypes);

        return newMemberTypes;
    }
    
    private QualifiedName getNewLocalTypename(Namespace namespace) {
        String localTypeName = "simpleType";
        if (this.usedLocalTypeNames.contains(localTypeName)) {
            localTypeName = localTypeName + "_" + this.usedLocalTypeNames.size();
        }
        this.usedLocalTypeNames.add(localTypeName);

        return new QualifiedName(namespace, localTypeName);
    	
    }
}


