package eu.fox7.bonxai.converter.dtd2xsd;

import eu.fox7.bonxai.converter.dtd2xsd.exceptions.*;
import eu.fox7.schematoolkit.dtd.om.*;
import eu.fox7.schematoolkit.exceptions.ConversionFailedException;
import eu.fox7.schematoolkit.common.AnyPattern;
import eu.fox7.schematoolkit.common.AttributeParticle;
import eu.fox7.schematoolkit.common.AttributeRef;
import eu.fox7.schematoolkit.common.ChoicePattern;
import eu.fox7.schematoolkit.common.CountingPattern;
import eu.fox7.schematoolkit.common.DefaultNamespace;
import eu.fox7.schematoolkit.common.ElementRef;
import eu.fox7.schematoolkit.common.IdentifiedNamespace;
import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.common.ProcessContentsInstruction;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.common.SequencePattern;
import eu.fox7.schematoolkit.xsd.XSDSimpleTypes;
import eu.fox7.schematoolkit.xsd.om.ComplexContentType;
import eu.fox7.schematoolkit.xsd.om.ComplexType;
import eu.fox7.schematoolkit.xsd.om.ImportedSchema;
import eu.fox7.schematoolkit.xsd.om.SimpleType;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

import java.util.Iterator;

/**
 * Class for the conversion of a DTD Element to XML XSDSchema.
 *
 * Depending on the content model and the attributes of a DTD element it can be
 * different, what type of XSD structure the result of this process will be.
 *
 * The conversion of DTD attributes is handled by this class, too. The
 * AttributeConverter-class is used for this. Attributes are directly attached
 * to the DTD elements.
 *
 * @author Lars Schmidt
 */
public class ElementConverter extends ConverterBase {

    /**
     * Constructor of class ElementConverter
     * This class handles the conversion process from a DTD element to the
     * corresponding XML XSDSchema element.
     * @param xmlSchema     The resulting XML XSDSchema object structure schema
     * @param targetNamespace       The targetNamespace of the resulting schema
     * @param namespaceAware        Handle the correct namespaces according to leading abbreviation of dtd names (i.e. "abc:testname")
     */
    public ElementConverter(XSDSchema xmlSchema, Namespace targetNamespace) {
        super(xmlSchema, targetNamespace);
    }

    /**
     * Method "convert"
     *
     * This method is the central method of this class.
     * By calling this method the conversion process will be started.
     *
     * @param dtd       The source DTD object structure for the conversion
     * @throws Exception        Some different exceptions can be thrown in sub-methods of this method.
     */
    public void convert(DocumentTypeDefinition dtd) throws ConversionFailedException {
        String targetUri = ((this.targetNamespace == null) ? "" : this.targetNamespace.getUri());
        // Walk over the elements and convert each element
        for (Element currentElement: dtd.getElements()) {
        	// Convert one element
        	eu.fox7.schematoolkit.xsd.om.Element xsdElement = this.convertElement(currentElement);

        	// Handle attributes
        	// Check if there are attributes linked to the current element
        	if (currentElement.getAttributes() != null && !currentElement.getAttributes().isEmpty()) {
        		AttributeConverter attributeConverter = new AttributeConverter(this.xmlSchema, this.targetNamespace);
        		for (Attribute currentAttribute: currentElement.getAttributes()) {
    				Type type = xmlSchema.getType(xsdElement.getTypeName());
        			if (type != null && (type instanceof ComplexType)) {
        				ComplexType currentComplexType = (ComplexType) type;

        				eu.fox7.schematoolkit.xsd.om.Attribute currentXSDattribute = attributeConverter.convert(dtd, currentElement, currentAttribute);
    					currentComplexType.addAttribute(currentXSDattribute);
        			}
        		}
        	}

        	// Register Element in the symbolTable
        	this.xmlSchema.addElement(xsdElement);
        }
    }

    /**
     * Method "convertElement"
     * This method converts one particular DTD element to an according XSD element
     * @param dtdElement    The current DTD element as source of the conversion
     * @return eu.fox7.schematoolkit.xsd.om.Element        The resulting XSD element object
     * @throws Exception        Some different exceptions can be thrown in sub-methods of this method.
     */
    private eu.fox7.schematoolkit.xsd.om.Element convertElement(Element dtdElement) throws ConversionFailedException {
        // Declaration and initialization of necessary variables for the conversion

        // Generate a full qualified name for the resulting XSD attribute
        QualifiedName elementName = this.generateXSDFQName(dtdElement.getName());

        QualifiedName typeName = new QualifiedName(elementName.getNamespaceURI(),elementName.getName() + "-Type");

        // Initialize the result object for an element
        eu.fox7.schematoolkit.xsd.om.Element xsdElement = new eu.fox7.schematoolkit.xsd.om.Element(elementName);

        // If the source DTD element is not empty and it has some attributes attached to it
        // the result has to be a XSD complexType
        if (!dtdElement.isEmpty() || !dtdElement.getAttributes().isEmpty()) {
            ComplexType complexType = new ComplexType(typeName, null, true);

            // Mixed
            if (dtdElement.getMixed()) {
                complexType.setMixed(true);
            }

            // Content Model
            Particle particle = this.convertParticle(dtdElement.getParticle(), complexType);

            // ComplexContent of the XSD element
            ComplexContentType complexContentType = new ComplexContentType(particle);
            complexType.setContent(complexContentType);
            complexType.setIsAnonymous(true);

            // Register the generated type in the type symbolTable
            this.xmlSchema.addType(complexType);

        } else if (!(!dtdElement.getMixed() && !dtdElement.getMixedStar() && dtdElement.isEmpty())) {
            // Full qualified name of the type
            typeName = XSDSimpleTypes.STRING;
        }

        // Handling of an EMPTY - Content Model
        if (dtdElement.isEmpty()) {
            ComplexType complexType = new ComplexType(typeName, null, true);

            // Register the generated type in the type symbolTable
            this.xmlSchema.addType(complexType);

        }
        

        // Set the generated type into the element object
        xsdElement.setTypeName(typeName);

        return xsdElement;
    }

    /**
     * Conversion of the DTD element content model
     *
     * The content model of a DTD element will be translated into an according
     * Particle structure in XSD
     *
     * @param dtdParticle       Source of the conversion progress
     * @param complexType       In case of an AnyPattern in DTD the given complexType has to be set to "mixed" content.
     * @return Particle         Target/Result of the conversion progress
     * @throws Exception
     */
    private Particle convertParticle(Particle dtdParticle, ComplexType complexType) throws ConversionFailedException {
        // Declaration of the object holding the result
        Particle resultParticle = null;
        Particle particle = null;

        // Convert the given particle
        if (dtdParticle != null) {
            particle = dtdParticle;
        }

        /**
         * Special case:
         * The result is an ElementRef or a CountingPattern:
         * An ElementRef or a CountingPattern has to be surrounded by a Sequence
         * in order to form a valid XSD ComplexType content.
         */
        if ((particle instanceof eu.fox7.schematoolkit.common.ElementRef) || (particle instanceof CountingPattern && 
        		(((CountingPattern) particle).getParticle() instanceof eu.fox7.schematoolkit.common.ElementRef))) {
            SequencePattern sequencePattern = new SequencePattern();
            sequencePattern.addParticle(particle);
            resultParticle = sequencePattern;
        } else {
            // Standard case:
            resultParticle = particle;
        }

        return resultParticle;
    }
}
