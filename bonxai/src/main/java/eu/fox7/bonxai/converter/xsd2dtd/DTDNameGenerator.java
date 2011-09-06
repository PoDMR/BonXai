package eu.fox7.bonxai.converter.xsd2dtd;

import eu.fox7.bonxai.xsd.XSDSchema;

import java.util.Iterator;

/**
 * Class DTDNameGenerator
 * @author Lars Schmidt
 */
public class DTDNameGenerator {

    private final XSDSchema xmlSchema;
    private boolean topLevel;

    /**
     * Constructor of class DTDNameGenerator
     * @param xmlSchema     The XML XSDSchema source of the conversion
     */
    public DTDNameGenerator(XSDSchema xmlSchema) {
        this.xmlSchema = xmlSchema;
        this.topLevel = false;
    }

    /**
     * Generate a DTD element name with respect to the global settings for
     * namespace abbreviations and topLevel elements and its qualification
     * property
     *
     * @param fullQualifiedXSDName      the full qualified name of the xsd element
     * @param instanceQualification     the current qualification property of the xsd element
     * @return String   the generated DTD element name
     */
    public String getDTDElementName(String fullQualifiedXSDName, XSDSchema.Qualification instanceQualification) {
        if (this.isTopLevelElement(fullQualifiedXSDName)) {
            this.topLevel = true;
        }

        if (instanceQualification != null) {
            return this.getDTDName(fullQualifiedXSDName, instanceQualification);
        } else if (this.xmlSchema.getElementFormDefault() != null) {
            return this.getDTDName(fullQualifiedXSDName, this.xmlSchema.getElementFormDefault());
        } else {
            return this.getDTDName(fullQualifiedXSDName, null);
        }
    }

    /**
     * Generate a DTD attribute name with respect to the global settings for
     * namespace abbreviations
     *
     * @param fullQualifiedXSDName            the full qualified name of the xsd attribute
     * @param instanceQualification     the current qualification property of the xsd attribute
     * @return String   the generated DTD attribute name
     */
    public String getDTDAttributeName(String fullQualifiedXSDName, XSDSchema.Qualification instanceQualification) {
        if (this.isTopLevelAttribute(fullQualifiedXSDName)) {
            this.topLevel = true;
        }

        if (instanceQualification != null) {
            return this.getDTDName(fullQualifiedXSDName, instanceQualification);
        } else if (this.xmlSchema.getAttributeFormDefault() != null) {
            return this.getDTDName(fullQualifiedXSDName, this.xmlSchema.getAttributeFormDefault());
        } else {
            return this.getDTDName(fullQualifiedXSDName, null);
        }
    }

    /**
     * Generate a DTD element or attribute name with respect to the global settings for
     * namespace abbreviations and topLevel elements and its qualification
     * property
     *
     * @param fullQualifiedXSDName      the full qualified name of the xsd element or attribute
     * @param qualification     the current qualification property of the xsd element or attribute
     * @return String      the generated DTD element or attribute name
     */
    public String getDTDName(String fullQualifiedXSDName, XSDSchema.Qualification qualification) {
        boolean usePrefix = false;

        if (qualification != null) {
            if (qualification.equals(XSDSchema.Qualification.qualified)) {
                usePrefix = true;
            }
        }

        String dtdName = "";
        if (XSD2DTDConverter.NAMESPACE_PREFIX_FEATURE || XSD2DTDConverter.NAMESPACE_PREFIX_FORCE_USAGE) {
            if (!this.getNamespace(fullQualifiedXSDName).equals("") && (this.topLevel || usePrefix) || XSD2DTDConverter.NAMESPACE_PREFIX_FORCE_USAGE) {
                if (XSD2DTDConverter.NAMESPACE_PREFIX_CONSIDER_QUALIFICATION_ATTRIBUTE && usePrefix || !XSD2DTDConverter.NAMESPACE_PREFIX_CONSIDER_QUALIFICATION_ATTRIBUTE || XSD2DTDConverter.NAMESPACE_PREFIX_FORCE_USAGE) {
                    if (this.xmlSchema.getNamespaceList().getNamespaceByUri(this.getNamespace(fullQualifiedXSDName)).getIdentifier() != null) {
                        String namespaceIdentifier = this.xmlSchema.getNamespaceList().getNamespaceByUri(this.getNamespace(fullQualifiedXSDName)).getIdentifier();
                        dtdName = namespaceIdentifier + ":";
                    }
                }
            }
        }
        return dtdName + this.getLocalName(fullQualifiedXSDName);
    }

    /**
     * Get namespace URI from full qualified name.
     *
     * @param fullQualifiedXSDName
     * @return string
     */
    public String getNamespace(String fullQualifiedXSDName) {
        if (fullQualifiedXSDName.lastIndexOf("}") != -1) {
            return fullQualifiedXSDName.substring(1, fullQualifiedXSDName.lastIndexOf("}"));
        } else {
            return "";
        }
    }

    /**
     * Get local name from full qualified name.
     *
     * @param fullQualifiedXSDName
     * @return string
     */
    public String getLocalName(String fullQualifiedXSDName) {
        return fullQualifiedXSDName.substring(fullQualifiedXSDName.lastIndexOf("}") + 1);
    }

    private boolean isTopLevelElement(String fullQualifiedXSDName) {
        for (Iterator<eu.fox7.bonxai.xsd.Element> it = xmlSchema.getElements().iterator(); it.hasNext();) {
            eu.fox7.bonxai.xsd.Element element = it.next();
            if (element.getName().equals(fullQualifiedXSDName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTopLevelAttribute(String fullQualifiedXSDName) {
        for (Iterator<eu.fox7.bonxai.xsd.Attribute> it = xmlSchema.getAttributes().iterator(); it.hasNext();) {
            eu.fox7.bonxai.xsd.Attribute attribute = it.next();
            if (attribute.getName().equals(fullQualifiedXSDName)) {
                return true;
            }
        }
        return false;
    }
}
