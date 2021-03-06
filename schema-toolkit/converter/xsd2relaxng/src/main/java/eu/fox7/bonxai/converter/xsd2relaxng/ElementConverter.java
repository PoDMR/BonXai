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

import eu.fox7.schematoolkit.relaxng.om.*;
import eu.fox7.schematoolkit.xsd.om.AttributeGroup;
import eu.fox7.schematoolkit.xsd.om.ComplexType;
import eu.fox7.schematoolkit.xsd.om.SimpleType;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * Class ElementConverter
 *
 * This class handles the conversion progress of one XML XSDSchema element.
 *
 * @author Lars Schmidt
 */
public class ElementConverter extends ConverterBase {

    private ComplexTypeConverter complexTypeConverter;
    private SimpleTypeConverter simpleTypeConverter;
    private LinkedHashMap<eu.fox7.schematoolkit.xsd.om.Element, LinkedHashSet<eu.fox7.schematoolkit.xsd.om.Element>> elementInheritanceInformation;

    /**
     * Constructor of class ElementConverter
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
     * @param elementInheritanceInformation     Inheritance mapping of possible element substitutions for an element
     * @param typeInheritanceInformation        Inheritance mapping of possible element substitutions for an element
     */
    public ElementConverter(XSDSchema xmlSchema, RelaxNGSchema relaxng, HashMap<eu.fox7.schematoolkit.xsd.om.Attribute, String> xsdAttributeDefineRefMap, HashMap<AttributeGroup, String> xsdAttributeGroupDefineRefMap, HashMap<eu.fox7.schematoolkit.xsd.om.Element, String> xsdElementDefineRefMap, HashMap<eu.fox7.schematoolkit.xsd.om.Group, String> xsdGroupDefineRefMap, HashMap<Type, String> xsdTypeDefineRefMap, LinkedHashMap<eu.fox7.schematoolkit.xsd.om.Element, LinkedHashSet<eu.fox7.schematoolkit.xsd.om.Element>> elementInheritanceInformation, LinkedHashMap<Type, LinkedHashSet<Type>> typeInheritanceInformation) {
        super(xmlSchema, relaxng, xsdAttributeDefineRefMap, xsdAttributeGroupDefineRefMap, xsdElementDefineRefMap, xsdGroupDefineRefMap, xsdTypeDefineRefMap);
        this.elementInheritanceInformation = elementInheritanceInformation;

        this.simpleTypeConverter = new SimpleTypeConverter(this.xmlSchema, this.relaxng, xsdAttributeDefineRefMap, xsdAttributeGroupDefineRefMap, xsdElementDefineRefMap, xsdGroupDefineRefMap, xsdTypeDefineRefMap, typeInheritanceInformation);
        this.complexTypeConverter = new ComplexTypeConverter(this.xmlSchema, this.relaxng, xsdAttributeDefineRefMap, xsdAttributeGroupDefineRefMap, xsdElementDefineRefMap, xsdGroupDefineRefMap, xsdTypeDefineRefMap, this, simpleTypeConverter, this.elementInheritanceInformation, typeInheritanceInformation);

        this.simpleTypeConverter.setComplexTypeConverter(complexTypeConverter);

        /**
         * The following method call is necessary for the correct handling of
         *
         *                      -- anyAttributes --
         *
         * In RELAX NG it is an error, if there is an Attribute with an anyName
         * in the same namespace like an attribute with an ID-type Type like
         * (ID, IDREF or IDREFS). These can be defined at completely different
         * positions in the schema.
         * Such an anyName is used in case of generating a Pattern representing
         * the anyType from XML XSDSchema. There is no corresponding feature for
         * this in RELAX NG, so it has to be generated manually
         * (see class "ConverterBase", method "generateAnyTypePattern).
         *
         * To avoid overlapping sets of names in RELAX NG one has to use the
         * concept of except-names. With this it is possible to remove specified
         * names with a given namespace from the set of names provided by the
         * nsName or anyName.
         *
         * The handling of this takes place in class "ConverterBase" in method
         * "generateAnyAttributePattern"
         */
//        collectAttributeNamesWithIDTypeRecursively(xmlSchema, this.attributeNamesWithIDType, new LinkedHashSet<XSDSchema>());
    }

    /**
     * Convert a given XML XSDSchema element to the corresponding pattern structure
     * in RELAX NG
     * @param xsdElement        Source of the conversion
     * @param setRef            Setting to allow/disallow the creation of defines/refs for this element structure
     * @return Pattern          Result of the conversion
     */
    public eu.fox7.schematoolkit.relaxng.om.Pattern convertElement(eu.fox7.schematoolkit.xsd.om.Element xsdElement, boolean setRef) {

        Pattern returnPattern = null;

        if (this.xsdElementDefineRefMap.containsKey(xsdElement)) {
        	returnPattern = new Ref(this.xsdElementDefineRefMap.get(xsdElement), (Grammar) this.relaxng.getRootPattern());
        } else {

            // Handling for ONE element.
            String elementDefineName = null;

            if (setRef) {
                // Register a dummy-element as placeholder for the new RELAX NG element
                elementDefineName = this.registerDummyInDefineRefMap(xsdElement);
                // Update the global define/ref map with the generated RELAX NG element
                this.xsdElementDefineRefMap.put(xsdElement, ((Grammar) this.relaxng.getRootPattern()).getDefineLookUpTable().getReference(elementDefineName));
            }

            eu.fox7.schematoolkit.relaxng.om.Element relaxngElement = new Element();

            // Handling of abstract-attribute
            if (xsdElement.getAbstract()) {
                relaxngElement.addPattern(new NotAllowed());
            }

            // Set the localname
            relaxngElement.setNameAttribute(xsdElement.getName().getName());

            // Set the namespace
            if (!xsdElement.getName().getNamespace().equals(this.getRootPatternDefaultNamespace())) {
                relaxngElement.setAttributeNamespace(xsdElement.getName().getNamespace());
            }

            // There is no corresponding feature in RELAX NG for the form of an element
            // (xsdElement.getForm();)

            // The ID of a XML XSDSchema tag has no semantics in XML XSDSchema.
            // It is an attribute defined in the XML specification and is only present,
            // because a XML XSDSchema document is also a XML document
            // (xsdElement.getId();)

            // There is no corresponding feature in RELAX NG for default value of an element
            // (xsdElement.getDefault();)

            if (xsdElement.getFixed() != null) {

                // If the XML XSDSchema element has a fixed value, this can be
                // converted into a value pattern with the given fixed-value.
                Value value = new Value(xsdElement.getFixed());
                relaxngElement.addPattern(value);

            } else {

                // Type handling
                Pattern typePattern = this.simplifyPatternStructure(this.generatePatternForElementType(xsdElement));

                // Handling of nillable
                if (xsdElement.getNillable()) {
                    Choice choice = new Choice();
                    choice.addPattern(new Empty());
                    choice.addPattern(typePattern);
                    typePattern = choice;
                }

                // Unpack possible Group Pattern and add each pattern for itself to the element
                if (typePattern instanceof Group && !xsdElement.getNillable()) {
                    Group group = (Group) typePattern;

                    for (Iterator<Pattern> it = group.getPatterns().iterator(); it.hasNext();) {
                        Pattern pattern = it.next();
                        relaxngElement.addPattern(simplifyPatternStructure(pattern));
                    }
                } else {
                    relaxngElement.addPattern(typePattern);
                }
            }

            if (setRef) {
                // Set the generated pattern for the element to a define in the grammar object
                // An update of the symbolTable takes place within this method, too.
                Ref ref = this.setPatternToDefine(elementDefineName, simplifyPatternStructure(relaxngElement));
                returnPattern = ref;
            } else {
                returnPattern = simplifyPatternStructure(relaxngElement);
            }
        }
        return returnPattern;
    }

    /**
     * Generate a RELAX NG pattern structure for a given XML XSDSchema type
     * @param element           Element as parent of the type that will be converted
     * @return Pattern          Result of the conversion of a XML XSDSchema element type
     */
    private Pattern generatePatternForElementType(eu.fox7.schematoolkit.xsd.om.Element element) {
        Pattern returnPattern = null;
        Type type = this.xmlSchema.getType(element.getTypeName());
        if (type != null) {
        	if (type instanceof ComplexType) {
                // Case "ComplexType":
                returnPattern = this.complexTypeConverter.convert((ComplexType) type, !type.isAnonymous(), true, "");
                if (!type.isAnonymous()) {
                    returnPattern = this.complexTypeConverter.handleSubstitutions(element, returnPattern, true);
                }
            } else if (type instanceof SimpleType) {

                // Case "SimpleType":
                if (type.getName().equals("{http://www.w3.org/2001/XMLSchema}anyType")) {
                    return this.generateAnyTypePattern(type);
                }
                returnPattern = this.simpleTypeConverter.convert((SimpleType) type, !type.isAnonymous(), true);
                if (!type.isAnonymous() && !this.isXMLSchemaBuiltInType(type.getName())) {
                    returnPattern = this.simpleTypeConverter.handleSubstitutions((SimpleType) type, returnPattern, false);
                }
            }
        } else {

            // Case "AnyType":
            return this.generateAnyTypePattern(new SimpleType("{http://www.w3.org/2001/XMLSchema}anyType", null));
        }
        return simplifyPatternStructure(returnPattern);
    }

    /**
     * Handling of element substitutions from XML XSDSchema to offer a choice of
     * all allowed substitutions in the RELAX NG schema, too.
     * @param xsdElement            Element to check for substitutions
     * @param pattern               Pattern structure containing a choice of all
     *                              converted elements defined in the
     *                              substitution
     * @return Pattern              The result of the substitution handling
     */
    public Pattern handleSubstitutions(eu.fox7.schematoolkit.xsd.om.Element xsdElement, Pattern pattern) {
        /**
         * SubstitutionGroup Handling in type/group conversion:
         *
         * Use case: element is used deeper within the document.
         * This is not for topLevel Elements.
         *
         * If the complexType is the head of a substitutionGroup, convert all
         * substitutions of this type and combine them in a choice and return
         * the generated choice.
         *
         * The choice must not be put into a define for an element.
         * It is better to generate the substitutions on-the-fly.
         *
         */
        if (XSD2RelaxNGConverter.HANDLE_SUBSTITUTIONS && this.elementInheritanceInformation.containsKey(xsdElement)) {
            LinkedHashSet<eu.fox7.schematoolkit.xsd.om.Element> elementSubstitutions = this.elementInheritanceInformation.get(xsdElement);

            if (elementSubstitutions != null && !elementSubstitutions.isEmpty()) {

                Choice choice = new Choice();
                choice.addPattern(pattern);
                for (Iterator<eu.fox7.schematoolkit.xsd.om.Element> it = elementSubstitutions.iterator(); it.hasNext();) {
                    eu.fox7.schematoolkit.xsd.om.Element xsdSubstitutionElement = it.next();
                    Pattern substitutionElementPattern = this.convertElement(xsdSubstitutionElement, true);
                    choice.addPattern(substitutionElementPattern);
                }

                pattern = simplifyPatternStructure(choice);
            }
        }
        return pattern;
    }

    /**
     * Getter for the complexTypeConverter
     * @return ComplexTypeConverter
     */
    public ComplexTypeConverter getComplexTypeConverter() {
        return complexTypeConverter;
    }

    /**
     * Getter for the simpleTypeConverter
     * @return SimpleTypeConverter
     */
    public SimpleTypeConverter getSimpleTypeConverter() {
        return simpleTypeConverter;
    }
}
