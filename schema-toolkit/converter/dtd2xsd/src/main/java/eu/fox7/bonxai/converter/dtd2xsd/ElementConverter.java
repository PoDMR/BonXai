package eu.fox7.bonxai.converter.dtd2xsd;

import eu.fox7.bonxai.converter.dtd2xsd.exceptions.*;
import eu.fox7.schematoolkit.dtd.om.*;
import eu.fox7.schematoolkit.common.AnyPattern;
import eu.fox7.schematoolkit.common.AttributeParticle;
import eu.fox7.schematoolkit.common.ChoicePattern;
import eu.fox7.schematoolkit.common.CountingPattern;
import eu.fox7.schematoolkit.common.IdentifiedNamespace;
import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.common.ProcessContentsInstruction;
import eu.fox7.schematoolkit.common.SequencePattern;
import eu.fox7.schematoolkit.xsd.om.AttributeRef;
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
    public ElementConverter(XSDSchema xmlSchema, IdentifiedNamespace targetNamespace, boolean namespaceAware) {
        super(xmlSchema, targetNamespace, namespaceAware);
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
    public void convert(DocumentTypeDefinition dtd) throws Exception {
        String targetUri = ((this.targetNamespace == null) ? "" : this.targetNamespace.getUri());
        if (dtd != null && dtd.getRootElement() != null) {
            // Walk over the element-symbol-table and convert each element
            for (Iterator<Element> it = dtd.getElementSymbolTable().getAllReferencedObjects().iterator(); it.hasNext();) {
                Element currentElement = it.next();

                // Convert one element
                eu.fox7.schematoolkit.xsd.om.Element xsdElement = this.convertElement(currentElement);

                // Handle attributes
                // Check if there are attributes linked to the current element
                if (currentElement.getAttributes() != null && !currentElement.getAttributes().isEmpty()) {
                    AttributeConverter attributeConverter = new AttributeConverter(this.xmlSchema, this.targetNamespace, this.namespaceAware);
                    for (Iterator<Attribute> it1 = currentElement.getAttributes().iterator(); it1.hasNext();) {
                        Attribute currentAttribute = it1.next();
                        if (xsdElement.getType() != null) {
                            if (xsdElement.getType() instanceof ComplexType) {
                                ComplexType currentComplexType = (ComplexType) xsdElement.getType();

                                eu.fox7.schematoolkit.xsd.om.Attribute currentXSDattribute = attributeConverter.convert(dtd, currentElement, currentAttribute);

                                if (currentXSDattribute.getNamespace().equals(targetUri)) { // || currentXSDattribute.getNamespace().equals(DTD2XSDConverter.XMLSCHEMA_NAMESPACE)) {
                                    // Add the attribute to the ComplexType of the current element
                                    // Check duplicate names for attributes
                                    for (Iterator<AttributeParticle> it2 = currentComplexType.getAttributes().iterator(); it2.hasNext();) {
                                        AttributeParticle attributeParticle = it2.next();
                                        if (attributeParticle instanceof eu.fox7.schematoolkit.xsd.om.Attribute) {
                                            eu.fox7.schematoolkit.xsd.om.Attribute attributeForCheck = (eu.fox7.schematoolkit.xsd.om.Attribute) attributeParticle;
                                            if (attributeForCheck.getLocalName().equals(currentXSDattribute.getLocalName())) {
                                                throw new DuplicateAttributeNameException(currentXSDattribute.getLocalName(), xsdElement.getName());
                                            }
                                        }
                                    }
                                    currentComplexType.addAttribute(currentXSDattribute);
                                } else {
                                    SymbolTableRef<eu.fox7.schematoolkit.xsd.om.Attribute> attributeSymbolTableRef = this.xmlSchema.getAttributeSymbolTable().updateOrCreateReference(currentXSDattribute.getName(), currentXSDattribute);
                                    eu.fox7.schematoolkit.xsd.om.AttributeRef currentXSDAttributeRef = new AttributeRef(attributeSymbolTableRef);
                                    currentComplexType.addAttribute(currentXSDAttributeRef);

                                    ImportedSchema importedSchema = updateOrCreateImportedSchema(currentXSDattribute.getNamespace());
                                    if (!importedSchema.getSchema().getAttributeSymbolTable().hasReference(currentXSDattribute.getName())) {
                                        importedSchema.getSchema().addAttribute(attributeSymbolTableRef);
                                        importedSchema.getSchema().getAttributeSymbolTable().updateOrCreateReference(currentXSDattribute.getName(), currentXSDattribute);
                                    }
                                }
                            }
                        }
                    }
                }

                // Register Element in the symbolTable
                SymbolTableRef<eu.fox7.schematoolkit.xsd.om.Element> elementSymbolTableRef = this.xmlSchema.getElementSymbolTable().updateOrCreateReference(xsdElement.getName(), xsdElement);

                if (xsdElement.getNamespace().equals(targetUri)) { // || xsdElement.getNamespace().equals(DTD2XSDConverter.XMLSCHEMA_NAMESPACE)) {
                    // Add the element to the list of global elements
                    // Check duplicate names for elements
                    for (Iterator<eu.fox7.schematoolkit.xsd.om.Element> it2 = this.xmlSchema.getElements().iterator(); it2.hasNext();) {
                        eu.fox7.schematoolkit.xsd.om.Element elementForCheck = it2.next();
                        if (elementForCheck.getLocalName().equals(xsdElement.getLocalName())) {
                            throw new DuplicateElementNameException(xsdElement.getName());
                        }
                    }

                    this.xmlSchema.addElement(elementSymbolTableRef);
                } else {
                    // Generate Imported XSDSchema and attach it to the basis schema
                    // Put the xsdElement to the right XSDSchema
                    ImportedSchema importedSchema = updateOrCreateImportedSchema(xsdElement.getNamespace());
                    importedSchema.getSchema().addElement(elementSymbolTableRef);
                    importedSchema.getSchema().getElementSymbolTable().setReference(xsdElement.getName(), elementSymbolTableRef);
                }
            }
        }
    }

    /**
     * Method "convertElement"
     * This method converts one particular DTD element to an according XSD element
     * @param dtdElement    The current DTD element as source of the conversion
     * @return eu.fox7.schematoolkit.xsd.om.Element        The resulting XSD element object
     * @throws Exception        Some different exceptions can be thrown in sub-methods of this method.
     */
    private eu.fox7.schematoolkit.xsd.om.Element convertElement(Element dtdElement) throws Exception {
        // Declaration and initialization of necessary variables for the conversion

        // Generate a full qualified name for the resulting XSD attribute
        String elementName = this.generateXSDFQName(dtdElement.getName());

        SymbolTableRef<Type> elementTypeRef = null;
        String typeName = elementName + "-Type";
        boolean attributeTypeAttribute = false;

        // Initialize the result object for an element
        eu.fox7.schematoolkit.xsd.om.Element xsdElement = new eu.fox7.schematoolkit.xsd.om.Element(elementName);

        // If the source DTD element is not empty and it has some attributes attached to it
        // the result has to be a XSD complexType
        if (!dtdElement.isEmpty() || !dtdElement.getAttributes().isEmpty()) {
            ComplexType complexType = new ComplexType(typeName, null, true);
            complexType.setDummy(false);

            // Mixed
            if (dtdElement.getMixed()) {
                complexType.setMixed(true);
            }

            // Content Model
            Particle particle = this.convertParticle(dtdElement.getParticle(), complexType);

            // ComplexContent of the XSD element
            ComplexContentType complexContentType = new ComplexContentType(particle);
            complexType.setContent(complexContentType);

            // Register the generated type in the type symbolTable
            elementTypeRef = this.xmlSchema.getTypeSymbolTable().updateOrCreateReference(typeName, complexType);

        } else if (!(!dtdElement.getMixed() && !dtdElement.getMixedStar() && dtdElement.isEmpty())) {
            // Full qualified name of the type
            typeName = "{" + DTD2XSDConverter.XMLSCHEMA_NAMESPACE + "}" + "string";

            // Generate a corresponding notationBaseSimpleType
            if (this.xmlSchema.getTypeSymbolTable().hasReference(typeName)) {
                // Case "type exists already":
                elementTypeRef = this.xmlSchema.getTypeSymbolTable().getReference(typeName);
            } else {
                // Case "new type":
                SimpleType simpleType = new SimpleType(typeName, null, false);
                simpleType.setDummy(false);

                // Register the generated type in the type symbolTable
                elementTypeRef = this.xmlSchema.getTypeSymbolTable().updateOrCreateReference(typeName, simpleType);
            }
            attributeTypeAttribute = true;
        }

        // Handling of an EMPTY - Content Model
        if (dtdElement.isEmpty() && (elementTypeRef == null)) {
            ComplexType complexType = new ComplexType(typeName, null, true);
            complexType.setDummy(false);

            // Register the generated type in the type symbolTable
            elementTypeRef = this.xmlSchema.getTypeSymbolTable().updateOrCreateReference(typeName, complexType);

        }
        

        // Set the generated type into the element object
        xsdElement.setType(elementTypeRef);
        xsdElement.setTypeAttr(attributeTypeAttribute);

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
    private Particle convertParticle(Particle dtdParticle, ComplexType complexType) throws Exception {
        // Declaration of the object holding the result
        Particle resultParticle = null;
        Particle particle = null;

        // Convert the given particle
        if (dtdParticle != null) {
            particle = this.convertDTDParticleRecursive(dtdParticle, complexType);
        }

        /**
         * Special case:
         * The result is an ElementRef or a CountingPattern:
         * An ElementRef or a CountingPattern has to be surrounded by a Sequence
         * in order to form a valid XSD ComplexType content.
         */
        if ((particle instanceof eu.fox7.schematoolkit.common.ElementRef) || (particle instanceof CountingPattern
                && (((CountingPattern) particle).getParticles().size() == 1)
                && (((CountingPattern) particle).getParticles().getFirst() instanceof eu.fox7.schematoolkit.common.ElementRef))) {
            SequencePattern sequencePattern = new SequencePattern();
            sequencePattern.addParticle(particle);
            resultParticle = sequencePattern;
        } else {
            // Standard case:
            resultParticle = particle;
        }

        return resultParticle;
    }

    /**
     * Recursive method for the translation of a DTD particle structure to a XSD
     * particle structure
     *
     * Used by method: "convertParticle"
     *
     * @param dtdParticle       Source of the conversion progress
     * @param complexType       In case of an AnyPattern in DTD the given complexType has to be set to "mixed" content.
     * @return Particle         Target/Result of the conversion progress
     * @throws Exception        UnsupportedDTDParticleException
     */
    private Particle convertDTDParticleRecursive(Particle dtdParticle, ComplexType complexType) throws Exception {
        // Declaration of the object holding the result
        Particle currentParticle = null;

        // Case distinction //
        if (dtdParticle instanceof ElementRef) {
            // Case: ElementRef

            // ElementRefs or AnyPattern are the leaves of the content model hierachy
            // --> no recursive call in this case
            Element dtdElement = ((ElementRef) dtdParticle).getElement();
            SymbolTableRef<eu.fox7.schematoolkit.xsd.om.Element> elementSymbolTableRef = null;
            String elementName = this.generateXSDFQName(dtdElement.getName());
            if (this.xmlSchema.getElementSymbolTable().hasReference(elementName)) {
                elementSymbolTableRef = this.xmlSchema.getElementSymbolTable().getReference(elementName);
            } else {
                eu.fox7.schematoolkit.xsd.om.Element dummyElement = new eu.fox7.schematoolkit.xsd.om.Element(elementName);
                dummyElement.setDummy(true);
                elementSymbolTableRef = this.xmlSchema.getElementSymbolTable().updateOrCreateReference(elementName, dummyElement);
            }
            currentParticle = new eu.fox7.schematoolkit.common.ElementRef(elementSymbolTableRef);
        } else if (dtdParticle instanceof CountingPattern) {
            // Case: CountingPattern
            CountingPattern dtdCountingPattern = (CountingPattern) dtdParticle;
            CountingPattern countingPattern = new CountingPattern(dtdCountingPattern.getMin(), dtdCountingPattern.getMax());
            for (Iterator<Particle> it = dtdCountingPattern.getParticles().iterator(); it.hasNext();) {
                Particle dtdInnerParticle = it.next();
                // Recurse into the child-particle
                if (dtdInnerParticle != null) {
                    countingPattern.addParticle(this.convertDTDParticleRecursive(dtdInnerParticle, complexType));
                }
            }
            currentParticle = countingPattern;
        } else if (dtdParticle instanceof ChoicePattern) {
            // Case: ChoicePattern
            ChoicePattern dtdChoicePattern = (ChoicePattern) dtdParticle;
            ChoicePattern choicePattern = new ChoicePattern();
            for (Iterator<Particle> it = dtdChoicePattern.getParticles().iterator(); it.hasNext();) {
                Particle dtdInnerParticle = it.next();
                // Recurse into the child-particle
                if (dtdInnerParticle != null) {
                    choicePattern.addParticle(this.convertDTDParticleRecursive(dtdInnerParticle, complexType));
                }
            }
            currentParticle = choicePattern;
        } else if (dtdParticle instanceof SequencePattern) {
            // Case: SequencePattern
            SequencePattern dtdSequencePattern = (SequencePattern) dtdParticle;
            SequencePattern sequencePattern = new SequencePattern();
            for (Iterator<Particle> it = dtdSequencePattern.getParticles().iterator(); it.hasNext();) {
                Particle dtdInnerParticle = it.next();
                // Recurse into the child-particle
                if (dtdInnerParticle != null) {
                    sequencePattern.addParticle(this.convertDTDParticleRecursive(dtdInnerParticle, complexType));
                }
            }
            currentParticle = sequencePattern;
        } else if (dtdParticle instanceof AnyPattern) {
            // Case: AnyPattern

            // ElementRefs or AnyPattern are the leaves of the content model hierachy
            // --> no recursive call in this case
            SequencePattern sequencePattern = new SequencePattern();
            CountingPattern countingPattern = new CountingPattern(0, null);
            AnyPattern anyPattern = new AnyPattern(ProcessContentsInstruction.Skip, null);
            countingPattern.addParticle(anyPattern);
            sequencePattern.addParticle(countingPattern);
            currentParticle = sequencePattern;
            complexType.setMixed(true);
        } else {
            // Unsupported Particle Exception
            String exceptionInfo = (dtdParticle != null) ? dtdParticle.getClass().getName() : "";
            throw new UnsupportedDTDParticleException(exceptionInfo);
        }
        // return the result
        return currentParticle;
    }
}
