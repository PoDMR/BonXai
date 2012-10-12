package eu.fox7.schematoolkit.xsd.parser;

import eu.fox7.schematoolkit.common.*;
import eu.fox7.schematoolkit.xsd.om.*;
import eu.fox7.schematoolkit.xsd.om.Element;
import eu.fox7.schematoolkit.xsd.om.Group;
import eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.content.*;

import org.w3c.dom.*;

/*******************************************************************************
 * The SchemaProcessor is responsible for building the schema object oft the
 * XSD object model.
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
public class SchemaProcessor extends Processor {

    // True when the foreignSchema part of the schema is over
    private boolean foreignSchemaPartOver = false;
    private String includeTargetNamespace;

    /**
     * Constructor of the SchemaProcessor as all processors it only receives the
     * schema, which is build by this processor.
     * @param schema    XSDSchema processed by this processor
     */
    public SchemaProcessor(XSDSchema schema) {
        super(schema);
    }

    public void setIncludeTargetNamespace(String targetNamespace) {
        this.includeTargetNamespace = targetNamespace;
    }

    /**
     * Creates an schema corresponding to the schema node in the dom tree.
     * @param node      Node labeled with schema in the dom tree
     * @return The complete schema to the calling class
     * @throws java.lang.Exception
     */
    @Override
    protected XSDSchema processNode(Node node) throws XSDParseException {
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            for (int i = 0; i < node.getAttributes().getLength(); i++) {
                if (((Node) node.getAttributes().item(i)).getNodeName().startsWith("xmlns")) {
                    Node currentNode = (Node) node.getAttributes().item(i);

                    if (!isAnyUri(currentNode.getNodeValue())) {
                        throw new InvalidAnyUriException(currentNode.getNodeValue(), "namespace of schema element");
                    }
                    if (currentNode.getNodeName().equals("xmlns")) {
                        schema.setDefaultNamespace(new DefaultNamespace(currentNode.getNodeValue()));
                    } else {
                        // The check if there is already an identified namespace with the same as the current identifier
                        // is already handled by the DOM-parser itself.
                        schema.addIdentifiedNamespace(new IdentifiedNamespace(currentNode.getLocalName(), currentNode.getNodeValue()));
                    }
                }
            }

            if (attributes.getNamedItem("targetNamespace") != null && !((Attr) attributes.getNamedItem("targetNamespace")).getNodeValue().equals("")) {
                String targetNamespace = ((Attr) attributes.getNamedItem("targetNamespace")).getNodeValue();
                if (!isAnyUri(targetNamespace)) {
                    throw new InvalidAnyUriException(targetNamespace, "the targetnamespace of schema element");
                }
                schema.setTargetNamespace(targetNamespace);
                //schema.addIdentifiedNamespace(new IdentifiedNamespace("xml", "http://www.w3.org/XML/1998/namespace"));
            } else {
                if (this.includeTargetNamespace != null) {
                    if (!this.includeTargetNamespace.equals("") && !isAnyUri(includeTargetNamespace)) {
                        throw new InvalidAnyUriException(includeTargetNamespace, "the includeTargetNamespace of schema element");
                    }
                    schema.setTargetNamespace(includeTargetNamespace);
                    //schema.addIdentifiedNamespace(new IdentifiedNamespace("xml", "http://www.w3.org/XML/1998/namespace"));
                }
            }
            if (attributes.getNamedItem("id") != null) {
                if (attributes.getNamedItem("id").getNodeValue().equals("")) {
                    throw new EmptyIdException("schema element");
                }
                schema.setId(((Attr) attributes.getNamedItem("id")).getValue());
            }
            // The default-value is unqualified, so we have nothing to do here, if it is set but not equal to qualified.
            if (attributes.getNamedItem("elementFormDefault") != null) {
                if (!attributes.getNamedItem("elementFormDefault").getNodeValue().equals("qualified") && !attributes.getNamedItem("elementFormDefault").getNodeValue().equals("unqualified")) {
                    throw new InvalidElementFormDefaultValueException();
                }
                if (attributes.getNamedItem("elementFormDefault").getNodeValue().equals("qualified")) {
                    schema.setElementFormDefault(XSDSchema.Qualification.qualified);
                }
            }
            if (attributes.getNamedItem("attributeFormDefault") != null) {
                if (!attributes.getNamedItem("attributeFormDefault").getNodeValue().equals("qualified") && !attributes.getNamedItem("attributeFormDefault").getNodeValue().equals("unqualified")) {
                    throw new InvalidAttributeFormDefaultValueException();
                }
                if (attributes.getNamedItem("attributeFormDefault").getNodeValue().equals("qualified")) {
                    schema.setAttributeFormDefault(XSDSchema.Qualification.qualified);
                }
            }
            if (node.getAttributes().getNamedItem("finalDefault") != null) {
                String finalModifierString = node.getAttributes().getNamedItem("finalDefault").getNodeValue();
                String[] finalModifierArray = finalModifierString.split(" ");
                for (String currentModifier : finalModifierArray) {
                    if (currentModifier.toUpperCase().equals(XSDSchema.FinalDefault.restriction.name().toUpperCase())) {
                        this.schema.addFinalDefault(XSDSchema.FinalDefault.restriction);
                    } else {
                        if (currentModifier.toUpperCase().equals(XSDSchema.FinalDefault.extension.name().toUpperCase())) {
                            this.schema.addFinalDefault(XSDSchema.FinalDefault.extension);
                        } else {
                            if (currentModifier.toUpperCase().equals(XSDSchema.FinalDefault.list.name().toUpperCase())) {
                                this.schema.addFinalDefault(XSDSchema.FinalDefault.list);
                            } else {
                                if (currentModifier.toUpperCase().equals(XSDSchema.FinalDefault.union.name().toUpperCase())) {
                                    this.schema.addFinalDefault(XSDSchema.FinalDefault.union);
                                } else {
                                    if (currentModifier.toUpperCase().equals("#all".toUpperCase())) {
                                        this.schema.addFinalDefault(XSDSchema.FinalDefault.restriction);
                                        this.schema.addFinalDefault(XSDSchema.FinalDefault.extension);
                                        this.schema.addFinalDefault(XSDSchema.FinalDefault.list);
                                        this.schema.addFinalDefault(XSDSchema.FinalDefault.union);
                                    } else {
                                        if (!finalModifierString.equals("")) {
                                            throw new InvalidFinalDefaultValueException();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (node.getAttributes().getNamedItem("blockDefault") != null) {
                String blockModifierString = node.getAttributes().getNamedItem("blockDefault").getNodeValue();
                String[] blockModifierArray = blockModifierString.split(" ");
                for (String currentModifier : blockModifierArray) {
                    if (currentModifier.toUpperCase().equals(XSDSchema.BlockDefault.restriction.name().toUpperCase())) {
                        this.schema.addBlockDefault(XSDSchema.BlockDefault.restriction);
                    } else {
                        if (currentModifier.toUpperCase().equals(XSDSchema.BlockDefault.extension.name().toUpperCase())) {
                            this.schema.addBlockDefault(XSDSchema.BlockDefault.extension);
                        } else {
                            if (currentModifier.toUpperCase().equals(XSDSchema.BlockDefault.substitution.name().toUpperCase())) {
                                this.schema.addBlockDefault(XSDSchema.BlockDefault.substitution);
                            } else {
                                if (currentModifier.toUpperCase().equals("#all".toUpperCase())) {
                                    this.schema.addBlockDefault(XSDSchema.BlockDefault.restriction);
                                    this.schema.addBlockDefault(XSDSchema.BlockDefault.extension);
                                    this.schema.addBlockDefault(XSDSchema.BlockDefault.substitution);
                                } else {
                                    if (!blockModifierString.equals("")) {
                                        throw new InvalidBlockDefaultValueException();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        visitChildren(node);
        return schema;
    }

    /**
     * Visits a child of the schema node and processes it according to its name
     * @param childNode     Node in the dom tree below the schema Node
     * @throws java.lang.Exception
     */
    @Override
    protected void processChild(Node childNode) throws XSDParseException {
        String nodeName = childNode.getNodeName();
        if (nodeName.contains(":")) {
            nodeName = nodeName.split(":")[1];
        }
        if (childNode != null && !"#text".equals(nodeName) && !"#comment".equals(nodeName)) {
            switch (CASE.valueOf(nodeName.toUpperCase())) {
                case INCLUDE:
                    if (getDebug()) {
                        System.out.println("include");
                    }
                    if (foreignSchemaPartOver) {
                        throw new InvalidForeignSchemaLocationException();
                    }
                    IncludeProcessor includeProcessor = new IncludeProcessor(schema);
                    IncludedSchema includedSchema = includeProcessor.processNode(childNode);
                    includedSchema.setParentSchema(schema);
                    schema.addForeignSchema(includedSchema);
                    break;
                case IMPORT:
                    if (getDebug()) {
                        System.out.println("import");
                    }
                    if (foreignSchemaPartOver) {
                        throw new InvalidForeignSchemaLocationException();
                    }
                    ImportProcessor importProcessor = new ImportProcessor(schema);
                    ImportedSchema importedSchema = importProcessor.processNode(childNode);
                    importedSchema.setParentSchema(schema);
                    schema.addForeignSchema(importedSchema);
                    break;
// TODO: Redefine
//                case REDEFINE:
//                    if (getDebug()) {
//                        System.out.println("redefine");
//                    }
//                    if (foreignSchemaPartOver) {
//                        throw new InvalidForeignSchemaLocationException();
//                    }
//                    RedefineProcessor redefineProcessor = new RedefineProcessor(schema);
//                    RedefinedSchema redefinedSchema = redefineProcessor.processNode(childNode);
//                    redefinedSchema.setParentSchema(schema);
//                    schema.addForeignSchema(redefinedSchema);
//                    break;
                case ATTRIBUTE:
                    if (getDebug()) {
                        System.out.println("attribute");
                    }
                    foreignSchemaPartOver = true;
 
                    AttributeProcessor attributeProcessor = new AttributeProcessor(schema);
                    Object object = attributeProcessor.processNode(childNode);
                    if (object instanceof Attribute) {
                        Attribute attribute = (Attribute) object;
                        schema.addAttribute(attribute);
                    } else {
                        throw new IllegalObjectReturnedException(object.getClass().getName(), "schema element");
                    }
                    break;
                case ELEMENT:
                    if (getDebug()) {
                        System.out.println("element");
                    }
                    if (!foreignSchemaPartOver) {
                        foreignSchemaPartOver = true;
                    }
                    ElementProcessor elementProcessor = new ElementProcessor(schema);
                    Particle particle = elementProcessor.processNode(childNode);
                    if (particle instanceof Element) {
                        Element element = (Element) particle;
                        schema.addElement(element);
                    } else {
                        throw new IllegalObjectReturnedException(particle.getClass().getName(), "schema element");
                    }
                    break;
                case COMPLEXTYPE:
                    if (getDebug()) {
                        System.out.println("complexType");
                    }
                    if (!foreignSchemaPartOver) {
                        foreignSchemaPartOver = true;
                    }
                    ComplexTypeProcessor complexTypeProcessor = new ComplexTypeProcessor(schema);
                    complexTypeProcessor.processNode(childNode);
                    break;
                case SIMPLETYPE:
                    if (getDebug()) {
                        System.out.println("simpleType");
                    }
                    if (!foreignSchemaPartOver) {
                        foreignSchemaPartOver = true;
                    }
                    SimpleTypeProcessor simpleTypeProcessor = new SimpleTypeProcessor(schema);
                    simpleTypeProcessor.processNode(childNode);
                    break;
                case GROUP:
                    if (getDebug()) {
                        System.out.println("group");
                    }
                    if (!foreignSchemaPartOver) {
                        foreignSchemaPartOver = true;
                    }
                    GroupProcessor groupProcessor = new GroupProcessor(schema);
                    object = groupProcessor.processNode(childNode);
                    if (object instanceof Group) {
                        Group group = (Group) object;
                        schema.addGroup(group);
                    } else {
                        throw new IllegalObjectReturnedException(object.getClass().getName(), "schema element");
                    }
                    break;
                case ATTRIBUTEGROUP:
                    if (getDebug()) {
                        System.out.println("attributeGroup");
                    }
                    if (!foreignSchemaPartOver) {
                        foreignSchemaPartOver = true;
                    }
                    AttributeGroupProcessor attributeGroupProcessor = new AttributeGroupProcessor(schema);
                    AttributeGroup attributeGroup = attributeGroupProcessor.processNode(childNode);
                    schema.addAttributeGroup(attributeGroup);
                    break;
                case ANNOTATION:
                    if (getDebug()) {
                        System.out.println("Node with name " + nodeName + " is not parsed");
                    }
                    break;
                case NOTATION:
                    if (getDebug()) {
                        System.out.println("Node with name " + nodeName + " is not parsed");
                    }
                    break;
                case REDEFINE:
                	//we ignore redefine, as it is not supported by the object model.
                	break;
                
                default:
                    throw new UnsupportedContentException(nodeName, "schema");
            }
        }
    }
}
