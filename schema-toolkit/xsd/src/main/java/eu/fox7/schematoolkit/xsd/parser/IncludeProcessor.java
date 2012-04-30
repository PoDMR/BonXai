package eu.fox7.schematoolkit.xsd.parser;

import eu.fox7.schematoolkit.common.Annotation;
import eu.fox7.schematoolkit.xsd.om.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.content.*;

import org.w3c.dom.*;

/*******************************************************************************
 * This class processes an include element, which has the effect to bring in the
 * definitions and declarations contained in the specified schema, and make them
 * available as part of the schemas target namespace.
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
class IncludeProcessor extends Processor {

    // Location of the included schema
    private String schemaLocation;

    // Annotation of the include element
    private Annotation annotation;

    /**
     * Constructor of the IncludeProcessor, which receives only the schema.
     * @param schema    New schema created by the parser and its processors
     */
    public IncludeProcessor(XSDSchema schema) {
        super(schema);
    }

    /**
     * Constructs an IncludedSchema object from an include element of an XML XSDSchema
     * XSDSchema.
     * @param node      Node labeled with include in the dom tree
     * @return IncludedSchema object, which represents an include element of
     *          the schema
     * @throws java.lang.Exception
     */
    @Override
    protected IncludedSchema processNode(Node node) throws XSDParseException {

        // Makes sure that the schemaLocation is not empty or missing
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null && attributes.getNamedItem("schemaLocation") != null) {
            schemaLocation = ((Attr) attributes.getNamedItem("schemaLocation")).getValue();
            if (!isAnyUri(schemaLocation)) {
                throw new InvalidAnyUriException(schemaLocation, "schemaLocation attribute in an include element.");
            }
        } else {
            throw new MissingSchemaLocationException();
        }

        IncludedSchema includedSchema = new IncludedSchema(schemaLocation);
        if (node.getAttributes().getNamedItem("id") != null) {
            if (node.getAttributes().getNamedItem("id").getNodeValue().equals("")) {
                    throw new EmptyIdException("Inculde");
            }
            includedSchema.setId(node.getAttributes().getNamedItem("id").getNodeValue());
        }
        visitChildren(node);
        includedSchema.setAnnotation(annotation);
        return includedSchema;
    }

    /**
     * Visits a child of the include node and processes it according to its name
     * @param childNode     Node in the dom tree below the Node labeled with include
     * @throws java.lang.Exception
     */
    @Override
    protected void processChild(Node childNode) throws XSDParseException {

        // Tests if the node name is a local name and filters nodes with names #text, #comment and #document who are not in the enum
        String nodeName = childNode.getNodeName();
        if (nodeName.contains(":")) {
            nodeName = nodeName.split(":")[1];
        }
        if (childNode != null && !"#text".equals(nodeName) && !"#comment".equals(nodeName) && !"#document".equals(nodeName)) {
            switch (CASE.valueOf(nodeName.toUpperCase())) {
                case ANNOTATION:
                    if (annotation == null) {
                        AnnotationProcessor annotationProcessor = new AnnotationProcessor(schema);
                        annotation = annotationProcessor.processNode(childNode);
                    } else {
                        throw new MultipleAnnotationException("include");
                    }
                    break;
                default:
                    throw new UnsupportedContentException(nodeName, "include");
            }
        }
    }

}
