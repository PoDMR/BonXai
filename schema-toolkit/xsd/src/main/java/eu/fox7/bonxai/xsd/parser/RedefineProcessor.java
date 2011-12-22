package eu.fox7.bonxai.xsd.parser;

import eu.fox7.bonxai.common.AttributeParticle;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.parser.exceptions.attribute.*;
import eu.fox7.bonxai.xsd.parser.exceptions.content.*;

import org.w3c.dom.*;

/*******************************************************************************
 * This class processes a dom node into a RedefinedSchema object.
 * The redefine element acts very much like the include element as it includes
 * all the declarations and definitions from the specified file. Inside the
 * redefine element complexTypes, simpleTypes, groups and attributeGroups can be
 * redefined.
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
class RedefineProcessor extends Processor {

    // Location of the schema, which contains the redefined groups and types
    private String schemaLocation;

    // Created redefinedSchema object
    private RedefinedSchema redefinedSchema;

    /**
     * Constructor of the RedefineProcessor, which receives only the schema.
     * @param schema    New schema created by the parser and its processors
     */
    public RedefineProcessor(XSDSchema schema) {
        super(schema);
    }

    /**
     * Constructs an ImportedSchema object from an import element of an XML XSDSchema
     * XSDSchema.
     * @param node      Node labeled with import in the dom tree
     * @return RedefinedSchema object, which represents an redefined element of
     *          the schema
     * @throws java.lang.Exception
     */
    @Override
    protected RedefinedSchema processNode(Node node) throws Exception {

        // Makes sure that the schemaLocation is not empty or missing
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null && attributes.getNamedItem("schemaLocation") != null) {
            schemaLocation = ((Attr) attributes.getNamedItem("schemaLocation")).getValue();
            if (!isAnyUri(schemaLocation)) {
                throw new InvalidAnyUriException(schemaLocation, "schemaLocation attribute in a redefine element.");
            }
        } else {
            throw new MissingSchemaLocationException();
        }
        redefinedSchema = new RedefinedSchema(schemaLocation);
        
        visitChildren(node);

        if (node.getAttributes().getNamedItem("id") != null) {
            if (node.getAttributes().getNamedItem("id").getNodeValue().equals("")) {
                throw new EmptyIdException("Redefine");
            }
            this.redefinedSchema.setId(node.getAttributes().getNamedItem("id").getNodeValue());
        }
        return redefinedSchema;
    }

    /**
     * Visits a child of the redefine node and processes it according to its name
     * @param childNode     Node in the dom tree below the Node labeled with redefine
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
                case COMPLEXTYPE:
                    if (getDebug()) {
                        System.out.println("complexType");
                    }
                    ComplexTypeProcessor complexTypeProcessor = new ComplexTypeProcessor(schema);
                    SymbolTableRef<Type> strComplexType = complexTypeProcessor.processNode(childNode);
                    strComplexType.getReference().setIsAnonymous(false);
                    redefinedSchema.addType(strComplexType);
                    break;
                case SIMPLETYPE:
                    if (getDebug()) {
                        System.out.println("simpleType");
                    }
                    SimpleTypeProcessor simpleTypeProcessor = new SimpleTypeProcessor(schema);
                    SymbolTableRef<Type> strSimpleType = simpleTypeProcessor.processNode(childNode);
                    strSimpleType.getReference().setIsAnonymous(false);
                    redefinedSchema.addType(strSimpleType);
                    break;
                case GROUP:
                    if (getDebug()) {
                        System.out.println("group");
                    }
                    // The addGroup methode is called for groups, which were not registered inside the GroupsProcessor processNode methode
                    GroupProcessor groupProcessor = new GroupProcessor(schema);
                    Object object = groupProcessor.processNode(childNode);
                    if (object instanceof Group) {
                        redefinedSchema.addGroup(schema.getGroupSymbolTable().updateOrCreateReference(((Group) object).getName(), (Group) object));
                    } else {
                        throw new IllegalObjectReturnedException(object.getClass().getName(), "redefine");
                    }
                    break;
                case ATTRIBUTEGROUP:
                    if (getDebug()) {
                        System.out.println("attributeGroup");
                    }
                    // The addAttributeGroup methode is called for AttributeGroupRefs, which were not registered inside the AttributeGroupProcessor processNode methode
                    AttributeGroupProcessor attributeGroupProcessor = new AttributeGroupProcessor(schema);
                    AttributeParticle attributeParticle = attributeGroupProcessor.processNode(childNode);
                    if (attributeParticle instanceof AttributeGroup) {
                        redefinedSchema.addAttributeGroup(schema.getAttributeGroupSymbolTable().getReference(((AttributeGroup) attributeParticle).getName()));
                    } else {
                        throw new IllegalObjectReturnedException(attributeParticle.getClass().getName(), "redefine");
                    }
                    break;
                case ANNOTATION:
                    if (redefinedSchema.getAnnotation() == null) {
                        AnnotationProcessor annotationProcessor = new AnnotationProcessor(schema);
                        redefinedSchema.setAnnotation(annotationProcessor.processNode(childNode));
                    } else {
                        throw new MultipleAnnotationException("redefine");
                    }
                    break;
                default:
                    throw new UnsupportedContentException(nodeName, "redefine");
            }
        }
    }
}

