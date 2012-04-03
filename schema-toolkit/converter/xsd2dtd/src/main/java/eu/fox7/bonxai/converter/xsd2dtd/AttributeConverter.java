package eu.fox7.bonxai.converter.xsd2dtd;

import eu.fox7.schematoolkit.common.AnyAttribute;
import eu.fox7.schematoolkit.common.AttributeParticle;
import eu.fox7.schematoolkit.dtd.om.Attribute;
import eu.fox7.schematoolkit.dtd.om.AttributeType;
import eu.fox7.schematoolkit.xsd.om.AttributeRef;
import eu.fox7.schematoolkit.xsd.om.AttributeUse;
import eu.fox7.schematoolkit.xsd.om.ComplexType;
import eu.fox7.schematoolkit.xsd.om.SimpleContentExtension;
import eu.fox7.schematoolkit.xsd.om.SimpleContentRestriction;
import eu.fox7.schematoolkit.xsd.om.SimpleContentType;
import eu.fox7.schematoolkit.xsd.om.SimpleType;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * Class AttributeConverter
 * 
 * @author Lars Schmidt
 */
public class AttributeConverter {

    private XSDSchema xmlSchema;

    /**
     * Constructor of class AttributeConverter
     *
     * @param xmlSchema     Source of the conversion
     */
    public AttributeConverter(XSDSchema xmlSchema) {
        this.xmlSchema = xmlSchema;
    }

    /**
     * Method for converting all attributes of an element (XML XSDSchema) to their
     * DTD counterparts
     *
     * @param element   element holding the source xsd attributes for the conversion
     * @return LinkedList<Attribute>    resulting list of DTD attributes
     */
    public LinkedList<Attribute> convertAttributes(eu.fox7.schematoolkit.xsd.om.Element element) {
        LinkedList<Attribute> resultdtdAttributeList = new LinkedList<Attribute>();
        if (element.getType() != null) {
            // only types can hold attributeParticles in XML XSDSchema
            if (element.getType() instanceof ComplexType) {

                // only CompleyTypes and SimpleContentExtension can hold attributeParticles in this place, because all other appearances are resolved by the inheritance resolver
                ComplexType complexType = (ComplexType) element.getType();

                if (complexType.getContent() != null && complexType.getContent() instanceof SimpleContentType) {
                    SimpleContentType simpleContentType = (SimpleContentType) complexType.getContent();
                    if (simpleContentType.getInheritance() != null && simpleContentType.getInheritance() instanceof SimpleContentExtension) {
                        SimpleContentExtension simpleContentExtension = (SimpleContentExtension) simpleContentType.getInheritance();
                        for (Iterator<AttributeParticle> it = simpleContentExtension.getAttributes().iterator(); it.hasNext();) {
                            AttributeParticle attributeParticle = it.next();
                            // Convert each XML XSDSchema attribute to DTD
                            Attribute resultAttribute = convertAttribute(attributeParticle);
                            if (resultAttribute != null) {
                                resultdtdAttributeList.add(resultAttribute);
                            }
                        }
                    }
                } else {
                    for (Iterator<AttributeParticle> it = complexType.getAttributes().iterator(); it.hasNext();) {
                        AttributeParticle attributeParticle = it.next();
                        // Convert each XML XSDSchema attribute to DTD
                        Attribute resultAttribute = convertAttribute(attributeParticle);
                        if (resultAttribute != null) {
                            resultdtdAttributeList.add(resultAttribute);
                        }
                    }
                }
            }
        }
        return resultdtdAttributeList;
    }

    /**
     * Method for converting one given XSD AttributeParticle to its DTD
     * attribute counterpart
     *
     * @param xsdAttributeParticle
     * @return
     */
    public Attribute convertAttribute(AttributeParticle xsdAttributeParticle) {
        // Prepare the result
        Attribute resultAttribute = null;
        // Initialize the NameGenerator
        DTDNameGenerator dtdNameGenerator = new DTDNameGenerator(this.xmlSchema);

        // Switch over the different AttributeParticle variants
        if (xsdAttributeParticle instanceof eu.fox7.schematoolkit.xsd.om.AttributeRef) {
            // Case "AttributeRef":
            eu.fox7.schematoolkit.xsd.om.AttributeRef attributeRef = (AttributeRef) xsdAttributeParticle;
            resultAttribute = convertAttribute(attributeRef.getAttribute());
        } else if (xsdAttributeParticle instanceof AnyAttribute) {
            // Case "AnyAttribute":
            return null;
        } else if (xsdAttributeParticle instanceof eu.fox7.schematoolkit.xsd.om.Attribute) {
            // Case "Attribute":
            eu.fox7.schematoolkit.xsd.om.Attribute xsdAttribute = (eu.fox7.schematoolkit.xsd.om.Attribute) xsdAttributeParticle;

            String dtdAttributeName = dtdNameGenerator.getDTDAttributeName(xsdAttribute.getName(), xsdAttribute.getForm());

            // Handle the default of fixed properties
            String defaultPresenceMode = null;
            if (xsdAttribute.getUse() != null) {
                if (xsdAttribute.getUse().equals(AttributeUse.Optional)) {
                    defaultPresenceMode = "#IMPLIED";
                } else if (xsdAttribute.getUse().equals(AttributeUse.Required)) {
                    defaultPresenceMode = "#REQUIRED";
                } else if (xsdAttribute.getUse().equals(AttributeUse.Prohibited)) {
                    return null;
                }
            }

            String dtdAttributeValue = null;
            if (xsdAttribute.getFixed() != null) {
                defaultPresenceMode = "#FIXED";
                dtdAttributeValue = xsdAttribute.getFixed();
            }

            if (dtdAttributeValue == null && xsdAttribute.getDefault() != null) {
                dtdAttributeValue = xsdAttribute.getDefault();
            }

            if (defaultPresenceMode == null && dtdAttributeValue == null) {
                defaultPresenceMode = "#IMPLIED";
            }

            resultAttribute = new Attribute(dtdAttributeName, defaultPresenceMode, dtdAttributeValue);

            // Handle the type of the given xsd attribute
            if (xsdAttribute.getSimpleType() != null) {
                this.convertAttributeType(resultAttribute, xsdAttribute.getSimpleType());
            } else {
                resultAttribute.setType(AttributeType.CDATA);
            }
        }
        // return the generated DTD attribute
        return resultAttribute;
    }

    /**
     * Manage and choose the correct resulting DTD type for a given xsd attribute
     * @param dtdAttribute      The DTD attibute, where the resulting type has to be set
     * @param simpleType        Source of the type conversion
     */
    private void convertAttributeType(Attribute dtdAttribute, SimpleType simpleType) {
        // Prepare the result
        AttributeType dtdAttributeType = null;

        // NMTOKEN
        HashSet<String> typesForNMTOKEN = new HashSet<String>(
                Arrays.asList(
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}boolean",
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}float",
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}double",
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}decimal",
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}duration",
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}dateTime",
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}time",
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}date",
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}gYearMonth",
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}gYear",
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}gMonthDay",
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}gDay",
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}gMonth",
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}language",
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}NMTOKEN",
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}NCName",
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}integer",
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}nonPositiveInteger",
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}negativeInteger",
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}long",
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}int",
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}short",
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}byte",
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}nonNegativeInteger",
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}unsignedLong",
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}unsignedInt",
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}unsignedShort",
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}unsignedByte",
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}positiveInteger"));

        // IDREFS
        HashSet<String> typesForIDREFS = new HashSet<String>(
                Arrays.asList(
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}IDREFS"));

        // NMTOKENS
        HashSet<String> typesForNMTOKENS = new HashSet<String>(
                Arrays.asList(
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}NMTOKENS"));

        // ID
        HashSet<String> typesForID = new HashSet<String>(
                Arrays.asList(
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}ID"));

        // IDREF
        HashSet<String> typesForIDREF = new HashSet<String>(
                Arrays.asList(
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}IDREF"));
        // ENTITY
        HashSet<String> typesForENTITY = new HashSet<String>(
                Arrays.asList(
                "{" + XSD2DTDConverter.XMLSCHEMA_NAMESPACE + "}ENTITY"));

        if (typesForID.contains(simpleType.getName())) {
            dtdAttributeType = AttributeType.ID;
        } else if (typesForIDREF.contains(simpleType.getName())) {
            dtdAttributeType = AttributeType.IDREF;
        } else if (typesForIDREFS.contains(simpleType.getName())) {
            dtdAttributeType = AttributeType.IDREFS;
        } else if (typesForENTITY.contains(simpleType.getName())) {
            dtdAttributeType = AttributeType.ENTITY;
        } else if (typesForNMTOKEN.contains(simpleType.getName())) {
            dtdAttributeType = AttributeType.NMTOKEN;
        } else if (typesForNMTOKENS.contains(simpleType.getName())) {
            dtdAttributeType = AttributeType.NMTOKENS;
        } else {
            if (simpleType.getInheritance() == null) {
                // Standard attribute type
                dtdAttributeType = AttributeType.CDATA;
            } else {
                // Case "enumeration"?
                if (simpleType.getInheritance() instanceof SimpleContentRestriction) {
                    SimpleContentRestriction simpleContentRestriction = (SimpleContentRestriction) simpleType.getInheritance();
                    if (simpleContentRestriction.getEnumeration() != null && !simpleContentRestriction.getEnumeration().isEmpty()) {
                        // The current type is an enumeration.
                        LinkedList<String> tempList = simpleContentRestriction.getEnumeration();
                        LinkedHashSet<String> tempHashSet = new LinkedHashSet<String>();
                        for (Iterator<String> it = tempList.iterator(); it.hasNext();) {
                            String string = it.next();
                            tempHashSet.add(string);
                        }

                        if (!tempHashSet.isEmpty()) {
                            dtdAttributeType = AttributeType.ENUMERATION;
                            dtdAttribute.setType(dtdAttributeType);
                            dtdAttribute.setEnumerationOrNotationTokens(tempHashSet);
                        }
                    }
                }
            }
        }

        if (dtdAttributeType != null) {
            dtdAttribute.setType(dtdAttributeType);
        } else {
            // If nothing above matches the case, the resulting type is set to CDATA.
            dtdAttribute.setType(AttributeType.CDATA);
        }
    }
}
