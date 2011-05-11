package de.tudortmund.cs.bonxai.converter.relaxng2xsd;

import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import de.tudortmund.cs.bonxai.converter.relaxng2xsd.exceptions.*;
import de.tudortmund.cs.bonxai.relaxng.*;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.SimpleContentFixableRestrictionProperty;
import de.tudortmund.cs.bonxai.xsd.SimpleContentList;
import de.tudortmund.cs.bonxai.xsd.SimpleContentRestriction;
import de.tudortmund.cs.bonxai.xsd.SimpleContentUnion;
import de.tudortmund.cs.bonxai.xsd.SimpleType;
import de.tudortmund.cs.bonxai.xsd.Type;
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

        if (pattern instanceof de.tudortmund.cs.bonxai.relaxng.Data) {

            // Case: Data

            de.tudortmund.cs.bonxai.relaxng.Data data = (de.tudortmund.cs.bonxai.relaxng.Data) pattern;
            String typeNamespace = null;
            if (data.getAttributeDatatypeLibrary() == null || data.getAttributeDatatypeLibrary().equals("http://www.w3.org/2001/XMLSchema-datatypes")) {
                typeNamespace = RelaxNG2XSDConverter.XMLSCHEMA_NAMESPACE;
            } else {
                typeNamespace = data.getAttributeNamespace();
                if (typeNamespace == null) {
                    typeNamespace = "";
                }
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
                typeNamespace = RelaxNG2XSDConverter.XMLSCHEMA_NAMESPACE;
            }

            SymbolTableRef<Type> strType = this.checkOrCreateBuiltInType(typeNamespace, data.getType());

            // Case: There are inner param-patterns defined
            if (data.getParams() != null && !data.getParams().isEmpty()) {

                String localTypeName = "simpleType";
                if (this.usedLocalTypeNames.contains(localTypeName)) {
                    localTypeName = localTypeName + "_" + this.usedLocalTypeNames.size();
                }
                this.usedLocalTypeNames.add(localTypeName);
                String outerTypeNamespace = data.getAttributeNamespace();
                if (outerTypeNamespace == null) {
                    outerTypeNamespace = "";
                }
                String fqTypeName = "{" + outerTypeNamespace + "}" + localTypeName;
                SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(this.xmlSchema.getTypeSymbolTable().getReference(strType.getReference().getName()));

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

                    for (Iterator<Param> it = patternParamList.iterator(); it.hasNext();) {
                        Param param = it.next();

                        String localTypeNameInner = "simpleType";
                        if (this.usedLocalTypeNames.contains(localTypeNameInner)) {
                            localTypeNameInner = localTypeNameInner + "_" + this.usedLocalTypeNames.size();
                        }
                        this.usedLocalTypeNames.add(localTypeNameInner);

                        String fqTypeNameInner = "{" + outerTypeNamespace + "}" + localTypeNameInner;
                        SimpleContentRestriction simpleContentRestrictionInner = new SimpleContentRestriction(this.xmlSchema.getTypeSymbolTable().updateOrCreateReference(newSimpleType.getName(), newSimpleType));
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
                resultingSimpleType = (SimpleType) strType.getReference();
            }

        } else if (pattern instanceof de.tudortmund.cs.bonxai.relaxng.List) {

            // Case: List

            de.tudortmund.cs.bonxai.relaxng.List list = (de.tudortmund.cs.bonxai.relaxng.List) pattern;
            String typeNamespace = list.getAttributeNamespace();
            if (typeNamespace == null) {
                typeNamespace = "";
            }
            String localTypeName = "simpleType";
            if (this.usedLocalTypeNames.contains(localTypeName)) {
                localTypeName = localTypeName + "_" + this.usedLocalTypeNames.size();
            }
            this.usedLocalTypeNames.add(localTypeName);

            String fqTypeName = "{" + typeNamespace + "}" + localTypeName;
            LinkedList<SymbolTableRef<Type>> memberTypes = new LinkedList<SymbolTableRef<Type>>();
            for (Iterator<Pattern> it = list.getPatterns().iterator(); it.hasNext();) {
                Pattern listPattern = it.next();
                SimpleType listInnerSimpleType = this.generateSimpleTypeForPattern(listPattern, new LinkedList<Pattern>());

                if (listInnerSimpleType != null) {
                    memberTypes.add(this.xmlSchema.getTypeSymbolTable().updateOrCreateReference(listInnerSimpleType.getName(), listInnerSimpleType));
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
            String innerInnerFqTypeName = null;

            if (memberTypes.size() != 1) {

                SimpleContentUnion simpleContentUnion = new SimpleContentUnion(memberTypes);

                String innerInnerLocalTypeName = "simpleType";
                if (this.usedLocalTypeNames.contains(innerInnerLocalTypeName)) {
                    innerInnerLocalTypeName = innerInnerLocalTypeName + "_" + this.usedLocalTypeNames.size();
                }
                this.usedLocalTypeNames.add(innerInnerLocalTypeName);
                innerInnerFqTypeName = "{" + typeNamespace + "}" + innerInnerLocalTypeName;
                innerInnerSimpleType = new SimpleType(innerInnerFqTypeName, simpleContentUnion, true);
            } else {
                innerInnerFqTypeName = memberTypes.getFirst().getReference().getName();
                innerInnerSimpleType = (SimpleType) memberTypes.getFirst().getReference();
            }
            SimpleContentList simpleContentList = new SimpleContentList(this.xmlSchema.getTypeSymbolTable().updateOrCreateReference(innerInnerFqTypeName, innerInnerSimpleType));
            String innerLocalTypeName = "simpleType";
            if (this.usedLocalTypeNames.contains(innerLocalTypeName)) {
                innerLocalTypeName = innerLocalTypeName + "_" + this.usedLocalTypeNames.size();
            }
            this.usedLocalTypeNames.add(innerLocalTypeName);
            String innerFqTypeName = "{" + typeNamespace + "}" + innerLocalTypeName;
            SimpleType innerSimpleType = new SimpleType(innerFqTypeName, simpleContentList, true);

            SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(this.xmlSchema.getTypeSymbolTable().updateOrCreateReference(innerFqTypeName, innerSimpleType));
            simpleContentRestriction.setLength(new SimpleContentFixableRestrictionProperty<Integer>(list.getPatterns().size(), false));
            SimpleType simpleType = new SimpleType(fqTypeName, simpleContentRestriction, true);
            resultingSimpleType = simpleType;

        } else if (pattern instanceof de.tudortmund.cs.bonxai.relaxng.Choice) {

            // Case: Choice

            de.tudortmund.cs.bonxai.relaxng.Choice choice = (de.tudortmund.cs.bonxai.relaxng.Choice) pattern;
            String typeNamespace = choice.getAttributeNamespace();
            if (typeNamespace == null) {
                typeNamespace = "";
            }
            String localTypeName = "simpleType";
            if (this.usedLocalTypeNames.contains(localTypeName)) {
                localTypeName = localTypeName + "_" + this.usedLocalTypeNames.size();
            }
            this.usedLocalTypeNames.add(localTypeName);

            String fqTypeName = "{" + typeNamespace + "}" + localTypeName;
            LinkedList<SymbolTableRef<Type>> memberTypes = new LinkedList<SymbolTableRef<Type>>();
            for (Iterator<Pattern> it = choice.getPatterns().iterator(); it.hasNext();) {
                Pattern listPattern = it.next();
                SimpleType listInnerSimpleType = this.generateSimpleTypeForPattern(listPattern, parentList);

                if (listInnerSimpleType != null) {
                    memberTypes.add(this.xmlSchema.getTypeSymbolTable().updateOrCreateReference(listInnerSimpleType.getName(), listInnerSimpleType));
                }
            }

            /*
             * <xs:simpleType>
             *      <xs:union memberTypes="xs:integer xs:string"/>
             * </xs:simpleType>
             */

            memberTypes = optimizeSimpleTypeUnion(memberTypes);

            if (memberTypes.size() != 1) {
                SimpleContentUnion simpleContentUnion = new SimpleContentUnion(memberTypes);
                SimpleType simpleType = new SimpleType(fqTypeName, simpleContentUnion, true);
                resultingSimpleType = simpleType;
            } else {
                resultingSimpleType = (SimpleType) memberTypes.getFirst().getReference();
            }

        } else if (pattern instanceof de.tudortmund.cs.bonxai.relaxng.Optional) {

            // Case: Optional

            de.tudortmund.cs.bonxai.relaxng.Optional optional = (de.tudortmund.cs.bonxai.relaxng.Optional) pattern;
            String typeNamespace = optional.getAttributeNamespace();
            if (typeNamespace == null) {
                typeNamespace = "";
            }
            String localTypeName = "simpleType";
            if (this.usedLocalTypeNames.contains(localTypeName)) {
                localTypeName = localTypeName + "_" + this.usedLocalTypeNames.size();
            }
            this.usedLocalTypeNames.add(localTypeName);

            String fqTypeName = "{" + typeNamespace + "}" + localTypeName;
            LinkedList<SymbolTableRef<Type>> memberTypes = new LinkedList<SymbolTableRef<Type>>();
            for (Iterator<Pattern> it = optional.getPatterns().iterator(); it.hasNext();) {
                Pattern optionalPattern = it.next();
                SimpleType optionalInnerSimpleType = this.generateSimpleTypeForPattern(optionalPattern, parentList);
                if (optionalInnerSimpleType != null) {
                    memberTypes.add(this.xmlSchema.getTypeSymbolTable().updateOrCreateReference(optionalInnerSimpleType.getName(), optionalInnerSimpleType));
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

            SymbolTableRef<Type> strBaseType = checkOrCreateBuiltInType(RelaxNG2XSDConverter.XMLSCHEMA_NAMESPACE, "token");

            SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(strBaseType);
            simpleContentRestriction.setLength(new SimpleContentFixableRestrictionProperty<Integer>(0, false));

            String optionalLocalTypeName = "simpleType";
            if (this.usedLocalTypeNames.contains(optionalLocalTypeName)) {
                optionalLocalTypeName = optionalLocalTypeName + "_" + this.usedLocalTypeNames.size();
            }
            this.usedLocalTypeNames.add(optionalLocalTypeName);

            String optionalFqTypeName = "{" + typeNamespace + "}" + optionalLocalTypeName;

            SimpleType optionalSimpleType = new SimpleType(optionalFqTypeName, simpleContentRestriction, true);
            memberTypes.add(this.xmlSchema.getTypeSymbolTable().updateOrCreateReference(optionalSimpleType.getName(), optionalSimpleType));

            memberTypes = optimizeSimpleTypeUnion(memberTypes);

            if (memberTypes.size() != 1) {
                SimpleContentUnion simpleContentUnion = new SimpleContentUnion(memberTypes);
                SimpleType simpleType = new SimpleType(fqTypeName, simpleContentUnion, true);
                resultingSimpleType = simpleType;
            } else {
                resultingSimpleType = (SimpleType) memberTypes.getFirst().getReference();
            }
        } else if (pattern instanceof de.tudortmund.cs.bonxai.relaxng.Value) {

            // Case: Value

            de.tudortmund.cs.bonxai.relaxng.Value value = (de.tudortmund.cs.bonxai.relaxng.Value) pattern;
            String typeNamespace = value.getAttributeNamespace();
            if (typeNamespace == null) {
                typeNamespace = "";
            }
            String localTypeName = "simpleType";
            if (this.usedLocalTypeNames.contains(localTypeName)) {
                localTypeName = localTypeName + "_" + this.usedLocalTypeNames.size();
            }
            this.usedLocalTypeNames.add(localTypeName);

            String fqTypeName = "{" + typeNamespace + "}" + localTypeName;

            LinkedList<String> enumerationValues = new LinkedList<String>();
            enumerationValues.add(value.getContent());

            /*
             * <xs:simpleType>
             *      <xs:restriction base="xs:token">
             *          <xs:enumeration value="4"/>
             *      </xs:restriction>
             * </xs:simpleType>
             */

            String baseTypeNamespace = null;
            if (value.getAttributeDatatypeLibrary() == null || value.getAttributeDatatypeLibrary().equals("http://www.w3.org/2001/XMLSchema-datatypes")) {
                baseTypeNamespace = RelaxNG2XSDConverter.XMLSCHEMA_NAMESPACE;
            } else {
                baseTypeNamespace = value.getAttributeNamespace();
                if (baseTypeNamespace == null) {
                    baseTypeNamespace = "";
                }
            }

            String innerLocalTypeName = null;
            if (value.getType() == null) {
                innerLocalTypeName = "token";
            } else {
                innerLocalTypeName = value.getType();
            }

            SymbolTableRef<Type> strBaseType = checkOrCreateBuiltInType(baseTypeNamespace, innerLocalTypeName);

            SimpleContentRestriction simpleContentRestriction = new SimpleContentRestriction(strBaseType);
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

            SymbolTableRef<Type> strString = this.checkOrCreateBuiltInType(RelaxNG2XSDConverter.XMLSCHEMA_NAMESPACE, "string");
            resultingSimpleType = (SimpleType) strString.getReference();
        }

//        if (resultingSimpleType == null) {
//            // Exception: There is an unexpected null simpleType under the given element
//            throw new SimpleTypeIsNullException(pattern, "Pattern: " + ((pattern != null) ? pattern.getClass().getName() : "is null!"));
//        }
        parentList.removeLast();

        return resultingSimpleType;
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
    private SymbolTableRef<Type> checkOrCreateBuiltInType(String namespace, String typeName) {
        SymbolTableRef<Type> builtInTypeRef = null;

        if (namespace.equals(RelaxNG2XSDConverter.XMLSCHEMA_NAMESPACE)) {
            if (checkXMLSchemaBuiltInTypeName(typeName)) {
                builtInTypeRef = this.xmlSchema.getTypeSymbolTable().updateOrCreateReference("{" + RelaxNG2XSDConverter.XMLSCHEMA_NAMESPACE + "}" + typeName, new SimpleType("{" + RelaxNG2XSDConverter.XMLSCHEMA_NAMESPACE + "}" + typeName, null));
                builtInTypeRef.getReference().setDummy(true);
            } else {
                builtInTypeRef = this.xmlSchema.getTypeSymbolTable().updateOrCreateReference("{" + namespace + "}" + typeName, new SimpleType("{" + namespace + "}" + typeName, null, false));
            }
        } else {
            builtInTypeRef = this.xmlSchema.getTypeSymbolTable().updateOrCreateReference("{" + namespace + "}" + typeName, new SimpleType("{" + namespace + "}" + typeName, null));
        }
        return builtInTypeRef;
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
    public LinkedList<SymbolTableRef<Type>> optimizeSimpleTypeUnion(LinkedList<SymbolTableRef<Type>> memberTypes) {
        LinkedList<SymbolTableRef<Type>> newMemberTypes = new LinkedList<SymbolTableRef<Type>>();
        LinkedList<SymbolTableRef<Type>> otherMemberTypes = new LinkedList<SymbolTableRef<Type>>();
        LinkedHashMap<String, LinkedList<SymbolTableRef<Type>>> simpleTypeWithRestrictionEnumOrderedByBase = new LinkedHashMap<String, LinkedList<SymbolTableRef<Type>>>();

        for (Iterator<SymbolTableRef<Type>> it = memberTypes.iterator(); it.hasNext();) {
            SymbolTableRef<Type> strType = it.next();
            SimpleType currentSimpleType = (SimpleType) strType.getReference();

            if (currentSimpleType.getInheritance() != null && currentSimpleType.getInheritance() instanceof SimpleContentRestriction) {
                SimpleContentRestriction currentSimpleContentRestriction = (SimpleContentRestriction) currentSimpleType.getInheritance();
                if (currentSimpleContentRestriction.getEnumeration() != null && !currentSimpleContentRestriction.getEnumeration().isEmpty()) {
                    if (simpleTypeWithRestrictionEnumOrderedByBase.get(currentSimpleContentRestriction.getBase().getName()) != null) {
                        simpleTypeWithRestrictionEnumOrderedByBase.get(currentSimpleContentRestriction.getBase().getName()).add(strType);
                    } else {
                        LinkedList<SymbolTableRef<Type>> newEnumTypeList = new LinkedList<SymbolTableRef<Type>>();
                        newEnumTypeList.add(strType);
                        simpleTypeWithRestrictionEnumOrderedByBase.put(currentSimpleContentRestriction.getBase().getName(), newEnumTypeList);
                    }
                } else {
                    otherMemberTypes.add(strType);
                }
            } else {
                otherMemberTypes.add(strType);
            }
        }
        for (Iterator<String> it = simpleTypeWithRestrictionEnumOrderedByBase.keySet().iterator(); it.hasNext();) {
            String currentBaseTypeName = it.next();

            if (simpleTypeWithRestrictionEnumOrderedByBase.get(currentBaseTypeName) != null && simpleTypeWithRestrictionEnumOrderedByBase.get(currentBaseTypeName).size() > 1) {

                SymbolTableRef<Type> firstRestrictionTypeRef = simpleTypeWithRestrictionEnumOrderedByBase.get(currentBaseTypeName).removeFirst();
                SimpleType firstRestrictionType = (SimpleType) firstRestrictionTypeRef.getReference();
                SimpleContentRestriction firstSimpleContentRestriction = (SimpleContentRestriction) firstRestrictionType.getInheritance();

                while (!simpleTypeWithRestrictionEnumOrderedByBase.get(currentBaseTypeName).isEmpty()) {
                    SymbolTableRef<Type> nextRestrictionTypeRef = simpleTypeWithRestrictionEnumOrderedByBase.get(currentBaseTypeName).removeFirst();
                    SimpleType nextRestrictionType = (SimpleType) nextRestrictionTypeRef.getReference();
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
}


