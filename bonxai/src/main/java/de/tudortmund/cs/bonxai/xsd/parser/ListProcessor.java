package de.tudortmund.cs.bonxai.xsd.parser;

import de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute.*;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.content.*;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.type.*;
import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.xsd.*;
import org.w3c.dom.*;

/*******************************************************************************
 * Processor for the handling of an list-tag in XSD
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
public class ListProcessor extends Processor {

    // Referenced type of this list
    private SymbolTableRef<Type> typeRef;
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
    protected SimpleContentList processNode(Node node) throws Exception {
        // Call the visitChildren method to handle children and find necessary details
        visitChildren(node);

        // Case: There is no SimpleType-child defined in this list
        // -> It is necessary that the itemType-attribute is defined!
        if (this.typeRef == null && node.getAttributes() != null && node.getAttributes().getNamedItem("itemType") != null) {

            // Check for Attribute "itemType" and get the typeRef from the Type Symboltable
            String itemTypeNodeName = node.getAttributes().getNamedItem("itemType").getNodeValue();
            if (!isQName(itemTypeNodeName)) {
                throw new InvalidQNameException(itemTypeNodeName, "list");
            }
            String fullQualifiedItemName = getName(itemTypeNodeName);

            // It is important to find the correct namespace of the given Simpletype referenced by "itemType"
            if (!this.schema.getTypeSymbolTable().hasReference(fullQualifiedItemName)) {
                SimpleType simpleType = new SimpleType(fullQualifiedItemName, null);
                simpleType.setDummy(true);
                this.typeRef = this.schema.getTypeSymbolTable().updateOrCreateReference(fullQualifiedItemName, simpleType);
            } else {
                this.typeRef = this.schema.getTypeSymbolTable().getReference(fullQualifiedItemName);
            }

            simpleContentList = new SimpleContentList(typeRef);

        } else if (this.typeRef != null) {
            simpleContentList = new SimpleContentList(typeRef);
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
    protected void processChild(Node childNode) throws Exception {
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
                    this.typeRef = simpleTypeProcessor.processNode(childNode);
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
