package eu.fox7.bonxai.converter.xsd2relaxng;

import eu.fox7.schematoolkit.common.AttributeParticle;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.relaxng.om.*;
import eu.fox7.schematoolkit.xsd.om.AttributeGroup;
import eu.fox7.schematoolkit.xsd.om.ComplexType;
import eu.fox7.schematoolkit.xsd.om.SimpleContentExtension;
import eu.fox7.schematoolkit.xsd.om.SimpleContentList;
import eu.fox7.schematoolkit.xsd.om.SimpleContentRestriction;
import eu.fox7.schematoolkit.xsd.om.SimpleContentUnion;
import eu.fox7.schematoolkit.xsd.om.SimpleType;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * Class SimpleTypeConverter
 *
 * This class provides the conversion of a XML XSDSchema simpleType to a
 * corresponding RELAX NG pattern structure
 *
 * @author Lars Schmidt
 */
public class SimpleTypeConverter extends ConverterBase {

    private ComplexTypeConverter complexTypeConverter;
    private AttributeParticleConverter attributeParticleConverter;
    private LinkedHashMap<Type, LinkedHashSet<Type>> typeInheritanceInformation;

    /**
     * Constructor of class SimpleTypeConverter
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
     *
     * @param typeInheritanceInformation        Inheritance mapping of possible element substitutions for an element
     */
    public SimpleTypeConverter(XSDSchema xmlSchema, RelaxNGSchema relaxng, HashMap<eu.fox7.schematoolkit.xsd.om.Attribute, String> xsdAttributeDefineRefMap, HashMap<AttributeGroup, String> xsdAttributeGroupDefineRefMap, HashMap<eu.fox7.schematoolkit.xsd.om.Element, String> xsdElementDefineRefMap, HashMap<eu.fox7.schematoolkit.xsd.om.Group, String> xsdGroupDefineRefMap, HashMap<Type, String> xsdTypeDefineRefMap, LinkedHashMap<Type, LinkedHashSet<Type>> typeInheritanceInformation) {
    	super(xmlSchema, relaxng, xsdAttributeDefineRefMap, xsdAttributeGroupDefineRefMap, xsdElementDefineRefMap, xsdGroupDefineRefMap, xsdTypeDefineRefMap);
        this.typeInheritanceInformation = typeInheritanceInformation;
    }

    /**
     * Convert a given simpleType to its corresponding RELAX NG pattern
     * structure
     * @param simpleType        The source of the conversion in XML XSDSchema
     * @param refAllowed        Setting for the convecreation of defines/refs
     * @return Pattern          The result of the conversion in RELAX NG
     */
    public Pattern convert(SimpleType simpleType, boolean refAllowed) {
        return this.convert(simpleType, refAllowed, true, null);
    }

    public Pattern convert(SimpleType simpleType, boolean refAllowed, boolean convertAttributes) {
        return this.convert(simpleType, refAllowed, convertAttributes, null);
    }

    /**
     * Convert a given simpleType to its corresponding RELAX NG pattern
     * structure with some more settings
     * @param simpleType            The source of the conversion in XML XSDSchema
     * @param refAllowed            Setting for the convecreation of defines/refs
     * @param convertAttributes     Setting for the conversion of attributes
     * @return Pattern              The result of the conversion in RELAX NG
     */
    public Pattern convert(SimpleType simpleType, boolean refAllowed, boolean convertAttributes, QualifiedName parentTypeName) {

        if (simpleType.getName().equals(parentTypeName)) {
            refAllowed = false;
        }

        Pattern returnPattern = null;
        Pattern inheritancePattern = null;
        String typeDefineName = null;

        if (this.xsdTypeDefineRefMap.containsKey(simpleType) && refAllowed) {
            Ref ref = new Ref(this.xsdTypeDefineRefMap.get(simpleType), (Grammar) this.relaxng.getRootPattern());
            returnPattern = ref;
        } else {

            // Check XML XSDSchema datatype
            if (this.isXMLSchemaBuiltInType(simpleType.getName())) {
                Data data = new Data(simpleType.getName().getName());
                returnPattern = data;
            } else {

                if (!simpleType.isAnonymous() && refAllowed) {
                    // ComplexType is not anonymous --> prepare a define in the root grammar object
                    typeDefineName = this.registerDummyInDefineRefMap(simpleType);
                    this.xsdTypeDefineRefMap.put(simpleType, ((Grammar) this.relaxng.getRootPattern()).getDefineLookUpTable().getReference(typeDefineName));
                }

                if (simpleType.getInheritance() != null) {
                    inheritancePattern = this.simplifyPatternStructure(this.convertSimpleContentInheritance(simpleType.getInheritance(), refAllowed, convertAttributes, simpleType.getName()));
                }

                if (inheritancePattern != null) {
                    returnPattern = inheritancePattern;
                }

                // The final attribute can not be translated to RELAX NG, because there is no inheritance
                // and thus it can not be prohibited to use the corresponding pattern as "base" for an inherited structure.
                // (simpleType.getFinalModifiers();)

                // Handling of a not anonymous type
                if (!simpleType.isAnonymous() && refAllowed) {
                    // ComplexType is not anonymous --> register the generated pattern in the define
                    Ref ref = this.setPatternToDefine(typeDefineName, returnPattern);
                    returnPattern = ref;
                }
            }
        }
        return returnPattern;
    }

    /**
     * Convert a given simpleType inheritance object to a RELAX NG pattern
     * structure
     * @param inheritance       Object (SimpleContentInheritance, SimpleTypeInheritance) holding the inheritance structure
     * @param refAllowed        Setting for the creation of defines/refs
     * @param convertAttributes Setting for the conversion of attributes
     * @return Pattern          The result of the conversion in RELAX NG
     */
    public Pattern convertSimpleContentInheritance(Object inheritance, boolean refAllowed, boolean convertAttributes, QualifiedName parentTypeName) {

        Pattern returnPattern = null;

        // Check if the inheritance object is null
        if (inheritance != null) {
            if (inheritance instanceof SimpleContentExtension) {

                // Case: ComplexType -> SimpleContentType -> inheritance
                SimpleContentExtension simpleContentExtension = (SimpleContentExtension) inheritance;

                Pattern basePattern = null;

                // Switch over the type if the inheritance base
                Type baseType = this.xmlSchema.getType(simpleContentExtension.getBaseType());
                if (baseType instanceof SimpleType) {
                    // Case "SimpleType":
                    basePattern = this.convert((SimpleType) baseType, true, false, parentTypeName);
                } else if (baseType instanceof ComplexType) {
                    // Case "ComplexType":

                    boolean localRefAllowed = true;
                    if (parentTypeName.equals(((ComplexType) baseType).getName())) {
                        localRefAllowed = false;
                    }

                    basePattern = this.complexTypeConverter.convert((ComplexType) baseType, localRefAllowed, convertAttributes, parentTypeName);
                }

                // Group for the result
                Group group = new Group();
                group.addPattern(simplifyPatternStructure(basePattern));

                // Conversion of attached attributes
                if (convertAttributes) {
                    for (Iterator<AttributeParticle> it = simpleContentExtension.getAttributes().iterator(); it.hasNext();) {
                        AttributeParticle attributeParticle = it.next();
                        // Call convert-method from the attributeParticleConverter
                        Pattern attributePattern = this.attributeParticleConverter.convert(attributeParticle);
                        group.addPattern(simplifyPatternStructure(attributePattern));
                    }
                }
                returnPattern = simplifyPatternStructure(group);

            } else if (inheritance instanceof SimpleContentRestriction) {

                // Case: ComplexType -> SimpleContentType or SimpleType -> inheritance
                SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) inheritance;

                // Group for the base conversion result
                Group basePattern = new Group();

                // Conversion of attributes
                if (convertAttributes && simpleContentRestriction.getAttributes() != null && !simpleContentRestriction.getAttributes().isEmpty()) {
                    for (Iterator<AttributeParticle> it = simpleContentRestriction.getAttributes().iterator(); it.hasNext();) {
                        AttributeParticle attributeParticle = it.next();
                        // Call convert-method from the attributeParticleConverter
                        Pattern attributePattern = this.attributeParticleConverter.convert(attributeParticle);
                        basePattern.addPattern(simplifyPatternStructure(attributePattern));
                    }
                    convertAttributes = false;
                }

                // Switch over the type if the inheritance base
                Type baseType = this.xmlSchema.getType(simpleContentRestriction.getBaseType());
                if (baseType instanceof SimpleType) {
                    // Case "SimpleType":
                    basePattern.addPattern(this.convert((SimpleType) baseType, !this.hasFacetts(simpleContentRestriction), convertAttributes, parentTypeName));
                } else if (baseType instanceof ComplexType) {
                    // Case "ComplexType":
                    basePattern.addPattern(this.complexTypeConverter.convert((ComplexType) baseType, false, convertAttributes, parentTypeName));
                }

//                // If the current restriction has an anonymous type within its content
//                if (simpleContentRestriction.getAnonymousSimpleType() != null) {
//                    // Conversion of the content anonymous type
//                    boolean localRefAllowed = !this.hasFacetts(simpleContentRestriction) && refAllowed;
//                    if (parentTypeName.equals(((SimpleType) simpleContentRestriction.getAnonymousSimpleType()).getName())) {
//                        localRefAllowed = false;
//                    }
//                    basePattern.addPattern(this.convert((SimpleType) simpleContentRestriction.getAnonymousSimpleType(), localRefAllowed, convertAttributes, parentTypeName));
//                }

                // Simplify the conversion result of the base type
                if (basePattern != null) {
                    returnPattern = simplifyPatternStructure(basePattern);
                }

                // If result is of type "Data" - handle the used XML XSDSchema facets
                if (returnPattern instanceof Data) {
                    Data data = (Data) returnPattern;

                    // Case "enumeration":
                    if (simpleContentRestriction.getEnumeration() != null && !simpleContentRestriction.getEnumeration().isEmpty()) {
                        Choice choice = new Choice();
                        for (Iterator<String> it = simpleContentRestriction.getEnumeration().iterator(); it.hasNext();) {
                            String valueString = it.next();
                            Value value = new Value(valueString);
                            value.setType("string");
                            choice.addPattern(value);
                        }
                        returnPattern = choice;
                    }

                    // Case "fractionDigits":
                    if (simpleContentRestriction.getFractionDigits() != null) {
                        Param param = new Param("fractionDigits");
                        param.setContent(simpleContentRestriction.getFractionDigits().getValue().toString());
                        data.addParam(param);
                    }

                    // Case "length":
                    if (simpleContentRestriction.getLength() != null) {
                        Param param = new Param("length");
                        param.setContent(simpleContentRestriction.getLength().getValue().toString());
                        data.addParam(param);
                    }

                    // Case "maxExclusive":
                    if (simpleContentRestriction.getMaxExclusive() != null) {
                        Param param = new Param("maxExclusive");
                        param.setContent(simpleContentRestriction.getMaxExclusive().getValue().toString());
                        data.addParam(param);
                    }

                    // Case "maxInclusive":
                    if (simpleContentRestriction.getMaxInclusive() != null) {
                        Param param = new Param("maxInclusive");
                        param.setContent(simpleContentRestriction.getMaxInclusive().getValue().toString());
                        data.addParam(param);
                    }

                    // Case "maxLength":
                    if (simpleContentRestriction.getMaxLength() != null) {
                        Param param = new Param("maxLength");
                        param.setContent(simpleContentRestriction.getMaxLength().getValue().toString());
                        data.addParam(param);
                    }

                    // Case "maxInclusive":
                    if (simpleContentRestriction.getMinExclusive() != null) {
                        Param param = new Param("minExclusive");
                        param.setContent(simpleContentRestriction.getMinExclusive().getValue().toString());
                        data.addParam(param);
                    }

                    // Case "minInclusive":
                    if (simpleContentRestriction.getMinInclusive() != null) {
                        Param param = new Param("minInclusive");
                        param.setContent(simpleContentRestriction.getMinInclusive().getValue().toString());
                        data.addParam(param);
                    }

                    // Case "minLength":
                    if (simpleContentRestriction.getMinLength() != null) {
                        Param param = new Param("minLength");
                        param.setContent(simpleContentRestriction.getMinLength().getValue().toString());
                        data.addParam(param);
                    }

                    // Case "pattern":
                    if (simpleContentRestriction.getPattern() != null) {
                        Param param = new Param("pattern");
                        String patternString = simpleContentRestriction.getPattern().getValue().toString();
                        // optional: escaping of [-+]? minus character in pattern [...] braces.
                        param.setContent(patternString);
                        data.addParam(param);
                    }

                    // Case "totalDigits":
                    if (simpleContentRestriction.getTotalDigits() != null) {
                        Param param = new Param("totalDigits");
                        param.setContent(simpleContentRestriction.getTotalDigits().getValue().toString());
                        data.addParam(param);
                    }

                    // There is no corresponding feature in RELAX NG for the XML XSDSchema "whitespace" facet!
                    // (simpleContentRestriction.getWhitespace();)
                }

            } else if (inheritance instanceof SimpleContentUnion) {

                // Case: SimpleType -> inheritance
                SimpleContentUnion simpleContentUnion = (SimpleContentUnion) inheritance;

                LinkedList<Pattern> memberTypePatterns = new LinkedList<Pattern>();

                // Handle all membertypes of the union
                for (QualifiedName typename:  simpleContentUnion.getMemberTypes()) {
                    Type type = this.xmlSchema.getType(typename);
                	if (type instanceof SimpleType) {

                        boolean localRefAllowed = true;
                        if (parentTypeName.equals(type.getName())) {
                            localRefAllowed = false;
                        }

                        memberTypePatterns.add(this.convert((SimpleType) type, localRefAllowed, convertAttributes, parentTypeName));
                    }
                }

                if (!memberTypePatterns.isEmpty()) {

                    if (memberTypePatterns.size() > 1) {
                        Choice choice = new Choice();

                        for (Iterator<Pattern> it = memberTypePatterns.iterator(); it.hasNext();) {
                            Pattern pattern = it.next();
                            choice.addPattern(pattern);
                        }
                        returnPattern = simplifyPatternStructure(choice);
                    } else {
                        returnPattern = simplifyPatternStructure(memberTypePatterns.getFirst());
                    }
                }

            } else if (inheritance instanceof SimpleContentList) {

                // Case: SimpleType -> inheritance
                SimpleContentList simpleContentList = (SimpleContentList) inheritance;

                /**
                 * Prohibited paths in the resulting RELAX NG schema:
                 * list//list
                 * list//ref
                 * list//attribute
                 * list//text
                 * list//interleave
                 */
                List list = new List();

                ZeroOrMore zeroOrMore = new ZeroOrMore();

                // Handle the base type of the list
                boolean localRefAllowed = false;
                Type baseType = this.xmlSchema.getType(simpleContentList.getBaseType());
                zeroOrMore.addPattern(this.convert((SimpleType) baseType, localRefAllowed, false, parentTypeName));

                list.addPattern(zeroOrMore);

                returnPattern = simplifyPatternStructure(list);
            }
        } else {
            // Exception for a null inheritance
            Data data = new Data("string");
            returnPattern = data;
        }

        return simplifyPatternStructure(returnPattern);
    }

    /**
     * Check if a restriction has facets attached
     * @param simpleContentRestriction      Source for the check
     * @return boolean                      True, if there is any facet
     */
    private boolean hasFacetts(SimpleContentRestriction simpleContentRestriction) {
        boolean returnValue = false;
        if ((simpleContentRestriction.getEnumeration() != null && !simpleContentRestriction.getEnumeration().isEmpty()) || simpleContentRestriction.getFractionDigits() != null || simpleContentRestriction.getLength() != null || simpleContentRestriction.getMaxExclusive() != null || simpleContentRestriction.getMaxInclusive() != null || simpleContentRestriction.getMaxLength() != null || simpleContentRestriction.getMinExclusive() != null || simpleContentRestriction.getMinInclusive() != null || simpleContentRestriction.getMinLength() != null || simpleContentRestriction.getPattern() != null || simpleContentRestriction.getTotalDigits() != null) {
            returnValue = true;
        }
        return returnValue;
    }

    /**
     * Getter for the complexTypeConverter
     * @return ComplexTypeConverter
     */
    public ComplexTypeConverter getComplexTypeConverter() {
        return complexTypeConverter;
    }

    /**
     * Setter for the ComplexTypeConverter
     * @param complexTypeConverter
     */
    public void setComplexTypeConverter(ComplexTypeConverter complexTypeConverter) {
        this.complexTypeConverter = complexTypeConverter;
    }

    /**
     * Getter for the attributeParticleConverter
     * @return AttributeParticleConverter
     */
    public AttributeParticleConverter getAttributeParticleConverter() {
        return attributeParticleConverter;
    }

    /**
     * Setter for the attributeParticleConverter
     * @param attributeParticleConverter
     */
    public void setAttributeParticleConverter(AttributeParticleConverter attributeParticleConverter) {
        this.attributeParticleConverter = attributeParticleConverter;
    }

    /**
     * Handling of simpleType substitutions from XML XSDSchema to offer a choice of
     * all allowed substitutions in the RELAX NG schema, too.
     * @param simpleType            simpleType as parent of the type to check for substitutions
     * @param pattern               Pattern structure containing a choice of all
     *                              converted elements defined in the
     *                              substitution
     * @param convertAttributes     Setting to allow/disallow the conversion of
     *                              attached attributes
     * @return Pattern              The result of the substitution handling
     */
    public Pattern handleSubstitutions(eu.fox7.schematoolkit.xsd.om.SimpleType simpleType, Pattern pattern, boolean convertAttributes) {
        /**
         * Inheritance Handling in type conversion:
         *
         * Use case: type is used deeper within the document.
         * This is not for topLevel types.
         *
         * If the complexType is the head of an inheritance structure, convert all
         * substitutions of this type and combine them in a choice and return
         * the generated choice.
         *
         * The choice must not be put into a define for a type.
         * It is better to generate the substitutions on-the-fly.
         */
        if (XSD2RelaxNGConverter.HANDLE_SUBSTITUTIONS && this.typeInheritanceInformation.containsKey(simpleType)) {
            LinkedHashSet<eu.fox7.schematoolkit.xsd.om.Type> typeSubstitutions = this.typeInheritanceInformation.get(simpleType);

            if (typeSubstitutions != null && !typeSubstitutions.isEmpty()) {

                Choice choice = new Choice();
                choice.addPattern(pattern);
                for (Iterator<eu.fox7.schematoolkit.xsd.om.Type> it = typeSubstitutions.iterator(); it.hasNext();) {
                    eu.fox7.schematoolkit.xsd.om.Type xsdSubstitutionType = it.next();
                    Pattern substitutionTypePattern = this.convert((SimpleType) xsdSubstitutionType, true, convertAttributes, simpleType.getName());
                    choice.addPattern(substitutionTypePattern);
                }
                pattern = simplifyPatternStructure(choice);
            }
        }
        return pattern;
    }
}
