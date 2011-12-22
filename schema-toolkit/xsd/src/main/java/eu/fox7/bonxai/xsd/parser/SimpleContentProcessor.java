package eu.fox7.bonxai.xsd.parser;

import eu.fox7.bonxai.common.Annotation;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.parser.exceptions.attribute.*;
import eu.fox7.bonxai.xsd.parser.exceptions.content.*;
import eu.fox7.bonxai.xsd.parser.exceptions.inheritance.*;

import org.w3c.dom.*;

/*******************************************************************************
 * Processor for the handling of an simplecontent-tag in XSD
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
class SimpleContentProcessor extends Processor {

    // SimpleContent of a complexType or simpleType
    private SimpleContentType simpleContent;

    // Inheritance of this content
    private  SimpleContentInheritance inheritance;

    // Annotation of this content
    private Annotation annotation;

    /**
     * This is the constructor of the class SimpleContentProcessor.
     * @param schema
     */
    public SimpleContentProcessor(XSDSchema schema) {
        super(schema);
    }

    /**
     * The method processNode handles all attributes and properties of
     * the current simplecontent-tag. The given properties are checked against the
     * allowed values defined in the XSD primer.
     *
     * <simpleContent
     * id = ID
     * {any attributes with non-schema namespace . . .}>
     * Content: (annotation?, (restriction | extension))
     * </simpleContent>
     *
     * The content is managed by the processChild-method.
     *
     * @param node
     * @return SymbolTableRef<Type>
     * @throws Exception
     */
    @Override
    protected eu.fox7.bonxai.xsd.SimpleContentType processNode(Node node) throws Exception {

        // Call the visitChildren method to handle children and find necessary details for the current complexType
        this.visitChildren(node);

        // Generate XSD simpleContent object
        this.simpleContent = new SimpleContentType();

        // If there is an ID, we can add it to the corresponding object.
        if (node.getAttributes().getNamedItem("id") != null) {
            if (node.getAttributes().getNamedItem("id").getNodeValue().equals("")) {
                throw new EmptyIdException("SimpleContent");
            }
            this.simpleContent.setId(node.getAttributes().getNamedItem("id").getNodeValue());
        }
        
        // There has to be an inheritance for this SimpleContent, otherwise there is a failure.
        if (inheritance == null) {
            throw new MissingSimpleContentInheritanceException("SimpleContent");
        } else {
            this.simpleContent.setInheritance(this.inheritance);
        }

        // Add an annotation to this simpleContent
        simpleContent.setAnnotation(annotation);
        return this.simpleContent;
    }

    /**
     * This method manages the possible child-content af the current simplecontent
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
                        this.inheritance = (SimpleContentExtension) extensionProcessor.processNode(childNode);
                    } else {
                        throw new SimpleContentMultipleInheritanceException("extension");
                    }
                    break;
                case RESTRICTION:
                    if (getDebug()) {
                        System.out.println("restriction");
                    }
                    // There must not be an inheritance before we add this one here.
                    if (this.inheritance == null) {
                        RestrictionProcessor restrictionProcessor = new RestrictionProcessor(schema);
                        this.inheritance = (SimpleContentRestriction) restrictionProcessor.processNode(childNode);
                    } else {
                        throw new SimpleContentMultipleInheritanceException("restriction");
                    }
                    break;
                case ANNOTATION:
                    if (annotation == null) {
                        AnnotationProcessor annotationProcessor = new AnnotationProcessor(schema);
                        annotation = annotationProcessor.processNode(childNode);
                    } else {
                        throw new MultipleAnnotationException("simpleContent");
                    }
                    break;
                default:
                    throw new UnsupportedContentException(nodeName, "simpleContent");
            }
        }
    }
}
