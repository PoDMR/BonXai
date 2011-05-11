package de.tudortmund.cs.bonxai.xsd.parser;

import de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute.*;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.content.*;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.inheritance.*;
import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.xsd.*;
import org.w3c.dom.*;

/*******************************************************************************
 * Processor for the complexcontent-tag under a complextype in XSD
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
class ComplexContentProcessor extends Processor {

    // Content of the complexType
    private ComplexContentType complexContent;

    // Inheritance of the complexType if present
    private ComplexContentInheritance inheritance;

    // Content of the complexType or Inhertance
    private Particle particle;

    // Annotation of the Content
    private Annotation annotation;

    /**
     * This is the constructor of the class ComplexContentProcessor.
     * @param schema
     */
    public ComplexContentProcessor(XSDSchema schema) {
        super(schema);
    }

    /**
     * This method manages the possible child-content af the current complexcontent
     *
     * Content: (annotation?, (restriction | extension))
     *
     * @param childNode
     * @throws Exception
     */
    @Override
    protected void processChild(Node childNode) throws Exception {
        String nodeName = childNode.getNodeName();
        if (nodeName.contains(":")) {
            nodeName = nodeName.split(":")[1];
        }

        if (childNode != null && !"#text".equals(nodeName) && !"#comment".equals(nodeName) && !"#document".equals(nodeName)) {
            switch (CASE.valueOf(nodeName.toUpperCase())) {
                case EXTENSION:
                    if (getDebug()) {
                        System.out.println("extension");
                    }
                    // There must not be an inheritance before we add this one here.
                    if (this.inheritance == null) {
                        ExtensionProcessor extensionProcessor = new ExtensionProcessor(schema);
                        this.inheritance = (ComplexContentExtension) extensionProcessor.processNode(childNode);
                        this.particle = extensionProcessor.getParticle();
                    } else {
                        throw new ComplexContentMultipleInheritanceException("extension");
                    }
                    break;
                case RESTRICTION:
                    if (getDebug()) {
                        System.out.println("restriction");
                    }
                    // There must not be an inheritance before we add this one here.
                    if (this.inheritance == null) {
                        RestrictionProcessor restrictionProcessor = new RestrictionProcessor(schema);
                        this.inheritance = (ComplexContentRestriction) restrictionProcessor.processNode(childNode);
                        this.particle = restrictionProcessor.getParticle();
                    } else {
                        throw new ComplexContentMultipleInheritanceException("restriction");
                    }

                    break;
                case ANNOTATION:
                    if (annotation == null) {
                        AnnotationProcessor annotationProcessor = new AnnotationProcessor(schema);
                        annotation = annotationProcessor.processNode(childNode);
                    } else {
                        throw new MultipleAnnotationException("complexContent");
                    }
                    break;
                default:
                    throw new UnsupportedContentException(nodeName, "complexContent");
            }
        }
    }

    /**
     * The method processNode handles all attributes and properties of
     * the current complexcontent-tag. The given properties are checked against the
     * allowed values defined in the XSD primer.
     *
     * <complexContent
     * id = ID
     * mixed = boolean
     * {any attributes with non-schema namespace . . .}>
     * Content: (annotation?, (restriction | extension))
     * </complexContent>
     *
     * The content is managed by the processChild-method.
     *
     * @param node
     * @return ComplexContentType
     * @throws Exception
     */
    @Override
    protected ComplexContentType processNode(Node node) throws Exception {

        // Call the visitChildren method to handle children and find necessary details for the current complexType
        this.visitChildren(node);

        // Generate XSD simpleContent object
        this.complexContent = new ComplexContentType(this.particle, this.inheritance);
        complexContent.setAnnotation(annotation);

        // Handle the "mixed"-property
        if (node.getAttributes().getNamedItem("mixed") != null
                && node.getAttributes().getNamedItem("mixed").getNodeValue().equals("true")) {
            this.complexContent.setMixed(true);
        } else if (node.getAttributes().getNamedItem("mixed") != null
                && !node.getAttributes().getNamedItem("mixed").getNodeValue().equals("false")) {
            throw new WrongComplexContentMixedValueException("complexContent");
        }

        // If there is an ID, we can add it to the corresponding object.
        if (node.getAttributes().getNamedItem("id") != null) {
            if (node.getAttributes().getNamedItem("id").getNodeValue().equals("")) {
                throw new EmptyIdException("ComplexContent");
            }
            this.complexContent.setId(node.getAttributes().getNamedItem("id").getNodeValue());
        }

        // A complexContent must have an Inheritance.
        if (this.complexContent.getInheritance() == null) {
            throw new MissingComplexContentInheritanceException("ComplexContent");
        }

        return this.complexContent;
    }
}
