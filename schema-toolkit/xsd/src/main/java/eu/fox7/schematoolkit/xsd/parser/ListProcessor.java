package eu.fox7.schematoolkit.xsd.parser;

import eu.fox7.schematoolkit.common.*;
import eu.fox7.schematoolkit.xsd.om.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.content.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.type.*;

import org.w3c.dom.*;

/*******************************************************************************
 * Processor for the handling of an list-tag in XSD
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
public class ListProcessor extends Processor {

    // Referenced type of this list
    private QualifiedName simpleTypeName;
    // List of simpleContents
    private SimpleContentList simpleContentList;
    // Annotation of this list
    private Annotation annotation;

    /**
     * This is the constructor of the class ListProcessor.
     * @param schema
     */
    public ListProcessor(XSDSchema schema) {
        super(schema);
    }

    /**
     * The method processNode handles all attributes and properties of
     * the current list-tag. The given properties are checked against the
     * allowed values defined in the XSD primer.
     *
     * <list
     * id = ID
     * itemType = QName
     * {any attributes with non-schema namespace . . .}>
     * Content: (annotation?, simpleType?)
     * </list>
     *
     * The content is managed by the processChild-method.
     *
     * @param node
     * @return SimpleContentList
     * @throws Exception
     */
    @Override
    protected SimpleContentList processNode(Node node) throws XSDParseException {
        // Call the visitChildren method to handle children and find necessary details
        visitChildren(node);

        // Case: There is no SimpleType-child defined in this list
        // -> It is necessary that the itemType-attribute is defined!
        if (this.simpleTypeName == null && node.getAttributes() != null && node.getAttributes().getNamedItem("itemType") != null) {
            QualifiedName fullQualifiedItemName = getName(node.getAttributes().getNamedItem("itemType"));

            simpleContentList = new SimpleContentList(fullQualifiedItemName);
        } else if (this.simpleTypeName != null) {
            simpleContentList = new SimpleContentList(simpleTypeName);
        } else {
            throw new EmptySimpleContentListTypeException("simpleContentList");
        }

        simpleContentList.setAnnotation(annotation);
        if (node.getAttributes().getNamedItem("id") != null) {
            if (node.getAttributes().getNamedItem("id").getNodeValue().equals("")) {
                throw new EmptyIdException("simpleContentList");
            }
            this.simpleContentList.setId(node.getAttributes().getNamedItem("id").getNodeValue());
        }

        return this.simpleContentList;
    }

    /**
     * This method manages the possible child-content af the current list
     *
     * Content: (annotation?, simpleType?)
     * 
     * @param childNode
     * @throws Exception
     */
    @Override
    protected void processChild(Node childNode) throws XSDParseException {
        String nodeName = childNode.getNodeName();
        if (nodeName.contains(":")) {
            nodeName = nodeName.split(":")[1];
        }
        if (childNode != null && !"#text".equals(nodeName) && !"#comment".equals(nodeName) && !"#document".equals(nodeName)) {
            switch (CASE.valueOf(nodeName.toUpperCase())) {
                case SIMPLETYPE:
                    if (getDebug()) {
                        System.out.println("SimpleType");
                    }
                    SimpleTypeProcessor simpleTypeProcessor = new SimpleTypeProcessor(schema);
                    this.simpleTypeName = simpleTypeProcessor.processNode(childNode);
                    break;
                case ANNOTATION:
                    if (getDebug()) {
                        System.out.println("annotation");
                    }
                    if (annotation == null) {
                        AnnotationProcessor annotationProcessor = new AnnotationProcessor(schema);
                        annotation = annotationProcessor.processNode(childNode);
                    } else {
                        throw new MultipleAnnotationException("list");
                    }
                    break;
                default:
                    throw new UnsupportedContentException(nodeName, "list");
            }
        }
    }
}
