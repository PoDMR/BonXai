package de.tudortmund.cs.bonxai.xsd.parser;

import de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute.*;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.content.*;
import de.tudortmund.cs.bonxai.xsd.parser.exceptions.type.*;
import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.xsd.*;
import java.util.*;
import org.w3c.dom.*;

/*******************************************************************************
 * Processor for the handling of an union-tag in XSD
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
public class UnionProcessor extends Processor {

    // SimpleTypes which are used in this union
    private LinkedList<de.tudortmund.cs.bonxai.common.SymbolTableRef<Type>> simpleTypes = new LinkedList<de.tudortmund.cs.bonxai.common.SymbolTableRef<Type>>();

    // New simpleContentUnion created by this processor
    private SimpleContentUnion simpleContentUnion;
    
    // Annotation of ths union
    private Annotation annotation;

    /**
     * This is the constructor of the class ListProcessor.
     * @param schema
     */
    public UnionProcessor(XSDSchema schema) {
        super(schema);
    }

    /**
     * The method processNode handles all attributes and properties of
     * the current union-tag. The given properties are checked against the
     * allowed values defined in the XSD primer.
     *
     * <union
     * id = ID
     * memberTypes = List of QName
     * {any attributes with non-schema namespace . . .}>
     * Content: (annotation?, simpleType*)
     * </union>
     *
     * The content is managed by the processChild-method.
     *
     * @param node
     * @return SimpleContentList
     * @throws Exception
     */
    @Override
    protected SimpleContentUnion processNode(Node node) throws Exception {
        // Call the visitChildren method to handle children and find necessary details
        visitChildren(node);

        this.simpleContentUnion = new SimpleContentUnion(simpleTypes);
        simpleContentUnion.setAnnotation(annotation);

        if (node.getAttributes() != null && node.getAttributes().getNamedItem("memberTypes") != null) {
            // Check for Attribute "memberTypes" and get the typeRefs from the included simpleTypes
            String memberTypesValue = node.getAttributes().getNamedItem("memberTypes").getNodeValue();
            String[] typeRefNameArray = memberTypesValue.split(" ");
            for (int i = 0; i < typeRefNameArray.length; i++) {
                if (!isQName(typeRefNameArray[i])) {
                    throw new InvalidQNameException(typeRefNameArray[i], "union");
                }
                String fullQualifiedItemName = getName(typeRefNameArray[i]);
                SymbolTableRef<Type> typeRef = null;
                
                // It is important to find the correct namespace of the given Simpletype referenced by "itemType"
                if (!this.schema.getTypeSymbolTable().hasReference(fullQualifiedItemName)) {
                    SimpleType simpleType = new SimpleType(fullQualifiedItemName, null);
                    simpleType.setDummy(true);
                    typeRef = this.schema.getTypeSymbolTable().updateOrCreateReference(fullQualifiedItemName, simpleType);
                } else {
                    typeRef = this.schema.getTypeSymbolTable().getReference(fullQualifiedItemName);
                }
                
                simpleContentUnion.addMemberType(typeRef);
            }
        }
        // If there is an ID, we can add it to the corresponding object.
        if (node.getAttributes().getNamedItem("id") != null) {
            if (node.getAttributes().getNamedItem("id").getNodeValue().equals("")) {
                throw new EmptyIdException("simpleContentUnion");
            }
            this.simpleContentUnion.setId(node.getAttributes().getNamedItem("id").getNodeValue());
        }

        // The union can combine simpleType-Children and a list of MemberTypes.
        // It is a problem, if there is no MemberType and no simpleType associated with the union.
        if (this.simpleContentUnion.getMemberTypes().isEmpty() && this.simpleContentUnion.getAnonymousSimpleTypes().isEmpty()) {
            throw new EmptySimpleContentUnionMemberTypesException("simpleContentUnion");
        }

        return this.simpleContentUnion;
    }

    /**
     * This method manages the possible child-content af the current union
     *
     * Content: (annotation?, simpleType*)
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
                    // The union can combine simpleType-Children and a list of MemberTypes.
                    // This case handles the simpleType-children.
                    SimpleTypeProcessor simpleTypeProcessor = new SimpleTypeProcessor(schema);
                    this.simpleTypes.add(simpleTypeProcessor.processNode(childNode));
                    break;
                case ANNOTATION:
                    if (getDebug()) {
                        System.out.println("annotation");
                    }
                    if (annotation == null) {
                        AnnotationProcessor annotationProcessor = new AnnotationProcessor(schema);
                        annotation = annotationProcessor.processNode(childNode);
                    } else {
                        throw new MultipleAnnotationException("union");
                    }
                    break;
                default:
                    throw new UnsupportedContentException(nodeName, "union");
            }
        }
    }
}
