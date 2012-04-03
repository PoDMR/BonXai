package eu.fox7.bonxai.converter.xsd2dtd;

import eu.fox7.schematoolkit.dtd.om.Attribute;
import eu.fox7.schematoolkit.dtd.om.Element;
import eu.fox7.schematoolkit.xsd.om.SimpleConstraint;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * Class ElementWrapper
 * 
 * This is a helper class for collecting information about one resulting DTD
 * element.
 *
 * @author Lars Schmidt
 */
public class ElementWrapper {

    private final XSDSchema xmlSchema;
    /**
     * Set of all dtdElements with the same name
     */
    private LinkedHashSet<Element> dtdElements;
    /**
     * Set of all dtdAttributes with the same name
     */
    private LinkedHashMap<String, LinkedHashSet<Attribute>> dtdAttributeMap;
    /**
     * Set of all ElementRefs targeting this Element
     */
    private LinkedHashSet<ElementRef> dtdElementRefs;
    private String dtdElementName;
    /**
     * Set of XSD constraints, which were attached to the original XSD elements
     */
    private LinkedHashSet<SimpleConstraint> xsdContraints;

    /**
     * Constructor of the class ElementWrapper
     * @param xmlSchema
     * @param dtdElementName
     */
    public ElementWrapper(XSDSchema xmlSchema, String dtdElementName) {
        this.xmlSchema = xmlSchema;
        this.dtdElementName = dtdElementName;
        this.dtdElements = new LinkedHashSet<Element>();
        this.dtdElementRefs = new LinkedHashSet<ElementRef>();
        this.dtdAttributeMap = new LinkedHashMap<String, LinkedHashSet<Attribute>>();
        this.xsdContraints = new LinkedHashSet<SimpleConstraint>();
    }

    /**
     * Getter for the set of DTD elements contained in this elementWrapper
     * @return LinkedHashSet<Element>
     */
    public LinkedHashSet<Element> getDTDElements() {
        return dtdElements;
    }

    /**
     * Add a DTD element to this elementWrapper
     * @param element       Element
     */
    public void addDTDElement(Element element) {
        this.dtdElements.add(element);
    }

    /**
     * Add a XML XSDSchema constraint to this elementWrapper
     * @param constraint        SimpleConstraint
     */
    public void addXSDConstraint(SimpleConstraint constraint) {
        this.xsdContraints.add(constraint);
    }

    /**
     * Getter for the set of XML XSDSchema constraints contained in this elementWrapper
     * @return LinkedHashSet<SimpleConstraint>
     */
    public LinkedHashSet<SimpleConstraint> getXsdContraints() {
        return xsdContraints;
    }

    /**
     * Add a DTD element reference to this elementWrapper
     * @param elementRef        ElementRef
     */
    public void addDTDElementRef(ElementRef elementRef) {
        this.dtdElementRefs.add(elementRef);
    }

    /**
     * Getter for the set of DTD element references contained in this elementWrapper
     * @return LinkedHashSet<ElementRef>
     */
    public LinkedHashSet<ElementRef> getDtdElementRefs() {
        return dtdElementRefs;
    }

    /**
     * Getter for the map of DTD attributes contained in this elementWrapper
     * @return LinkedHashMap<String, LinkedHashSet<Attribute>>
     */
    public LinkedHashMap<String, LinkedHashSet<Attribute>> getDTDAttributeMap() {
        return dtdAttributeMap;
    }

    /**
     * Method addDTDAttribute
     *
     * Add a DTD attribute to this elementWrapper 
     * @param attribute     DTD attribute for addition
     */
    public void addDTDAttribute(Attribute attribute) {
        if (attribute != null) {
            if (this.dtdAttributeMap.get(attribute.getName()) == null) {
                // Case: No Entry in the HashMap
                LinkedHashSet<Attribute> newAttributeList = new LinkedHashSet<Attribute>();
                newAttributeList.add(attribute);
                this.dtdAttributeMap.put(attribute.getName(), newAttributeList);
            } else {
                // Case: There is already an entry in the HashMap
                LinkedHashSet<Attribute> oldAttributeList = this.dtdAttributeMap.get(attribute.getName());
                oldAttributeList.add(attribute);
            }
        }
    }

    /**
     * Getter for the DTD element name of this elementWrapper
     * @return String   DTD element name
     */
    public String getDTDElementName() {
        return dtdElementName;
    }

    /**
     * Setter for the set of DTD elements
     * @param dtdElements       LinkedHashSet<Element>
     */
    public void setDtdElements(LinkedHashSet<Element> dtdElements) {
        this.dtdElements = dtdElements;
    }

    /**
     * Getter for the source XML XSDSchema
     * @return XSDSchema       XML XSDSchema object
     */
    public XSDSchema getXmlSchema() {
        return xmlSchema;
    }

    /**
     * Setter for the set of DTD attribute map
     * @param dtdAttributeMap       LinkedHashMap<String, LinkedHashSet<Attribute>>
     */
    public void setDtdAttributeMap(LinkedHashMap<String, LinkedHashSet<Attribute>> dtdAttributeMap) {
        this.dtdAttributeMap = dtdAttributeMap;
    }
}
