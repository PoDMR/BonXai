package de.tudortmund.cs.bonxai.xsd.parser;

import de.tudortmund.cs.bonxai.common.Annotation;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute.*;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.content.*;
import de.tudortmund.cs.bonxai.xsd.*;
import org.w3c.dom.*;

/*******************************************************************************
 * This class generates an ImportedSchema object from a dom tree node. Imported
 * schemas elements are allowed to contain namespace and schemaLocation attributes.
 * But since both are optional, a bare <import/> information item is allowed.
 * This simply allows unqualified reference to foreign components with no target
 * namespace without giving any hints as to where to find them.
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
class ImportProcessor extends Processor {

    // Location of the included schema and namespace prefix for this schema
    private String schemaLocation,  namespace;

    // Annotation of the import element
    private Annotation annotation;

    /**
     * Constructor of the ImportProcessor, which receives only the schema.
     * @param schema    New schema created by the parser and its processors
     */
    public ImportProcessor(XSDSchema schema) {
        super(schema);
    }

    /**
     * Constructs an ImportedSchema object from an import element of an XML XSDSchema
     * XSDSchema.
     * @param node      Node labeled with import in the dom tree
     * @return ImportedSchema object, which represents an import element of
     *         the schema
     * @throws java.lang.Exception
     */
    @Override
    protected ImportedSchema processNode(Node node) throws Exception {
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {

            // Sets the namespace prefix if present
            if (attributes.getNamedItem("namespace") != null) {
                namespace = ((Attr) attributes.getNamedItem("namespace")).getValue();
                if (!isAnyUri(namespace)) {
                    throw new InvalidAnyUriException(namespace, "namespace attribute in an import element.");
                }
            }
            // Sets the schemaLocation if present
            if (attributes.getNamedItem("schemaLocation") != null) {
                schemaLocation = ((Attr) attributes.getNamedItem("schemaLocation")).getValue();
                if (!isAnyUri(schemaLocation)) {
                    throw new InvalidAnyUriException(schemaLocation, "schemaLocation attribute in an import element.");
                }
            }
        }
        ImportedSchema importedSchema = new ImportedSchema(namespace, schemaLocation);
        if (node.getAttributes().getNamedItem("id") != null) {
            if (node.getAttributes().getNamedItem("id").getNodeValue().equals("")) {
                    throw new EmptyIdException("Import");
            }
            importedSchema.setId(node.getAttributes().getNamedItem("id").getNodeValue());
        }
        visitChildren(node);
        importedSchema.setAnnotation(annotation);
        return importedSchema;
    }

    /**
     * Visits a child of the import node and processes it according to its name
     * @param childNode     Node in the dom tree below the Node labeled with import
     * @throws java.lang.Exception
     */
    @Override
    protected void processChild(Node childNode) throws Exception {

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
                        throw new MultipleAnnotationException("import");
                    }
                    break;
                default:
                    throw new UnsupportedContentException(nodeName, "import");
            }
        }
    }
}