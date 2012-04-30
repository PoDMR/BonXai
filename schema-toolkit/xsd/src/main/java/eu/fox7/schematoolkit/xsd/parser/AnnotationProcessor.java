package eu.fox7.schematoolkit.xsd.parser;

import eu.fox7.schematoolkit.common.Annotation;
import eu.fox7.schematoolkit.xsd.om.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.content.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException;

import org.w3c.dom.*;

/*******************************************************************************
 * This processor processes nodes, which are representing annotations, and
 * generates for such a node a cooresponding annotation object in the XSD object
 * model.
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
public class AnnotationProcessor extends Processor {

    // New annotation object which is filled by this processor
    private Annotation annotation = new Annotation();

    /**
     * Constructor of the AnnotationProcessor, which receives only the schema.
     * @param schema    New schema created by the parser and its processors
     */
    public AnnotationProcessor(XSDSchema schema) {
        super(schema);
    }

    /**
     * Creates an annotation corresponding to the annotation node in the dom tree.
     * @param node      Node labeled with annotation in the dom tree
     * @return Annotation object representing an annotation in XML XSDSchema
     * @throws java.lang.Exception
     */
    @Override
    protected Annotation processNode(Node node) throws XSDParseException {
        visitChildren(node);
        if (node.getAttributes().getNamedItem("id") != null) {
            if (node.getAttributes().getNamedItem("id").getNodeValue().equals("")) {
                throw new EmptyIdException("annotation");
            }
            this.annotation.setId(node.getAttributes().getNamedItem("id").getNodeValue());
        }
        return annotation;
    }

    /**
     * Visits a child of the annotation node and processes it according to its name
     * @param childNode     Node in the dom tree below the annotation Node
     * @throws java.lang.Exception
     */
    @Override
    protected void processChild(Node childNode) throws XSDParseException {

        // Tests if the node name is a local name and filters nodes with names #text, #comment and #document who are not in the enum
        String nodeName = childNode.getNodeName();
        if (nodeName.contains(":")) {
            nodeName = nodeName.split(":")[1];
        }
        NamedNodeMap attributes;
        NodeList nodeList;
        if (childNode != null && !"#text".equals(nodeName) && !"#comment".equals(nodeName) && !"#document".equals(nodeName)) {
            switch (CASE.valueOf(nodeName.toUpperCase())) {
                case APPINFO:
                    if (getDebug()) {
                        System.out.println("appinfo");
                    }
                    AppInfo appInfo = new AppInfo();

                    //Checks the attributes of appinfo
                    attributes = childNode.getAttributes();
                    if (attributes != null && attributes.getNamedItem("source") != null) {
                        String sourceValue = ((Attr) attributes.getNamedItem("source")).getValue();
                        if (!isAnyUri(sourceValue)) {
                            throw new InvalidAnyUriException(sourceValue, "source attribute in an appinfo element.");
                        }
                        appInfo.setSource(sourceValue);
                    }
                    // Appends the childNodes of XSD appinfo to the appinfo object in the object model
                    if (childNode.getChildNodes() != null) {
                        nodeList = childNode.getChildNodes();
                        appInfo.setNodeList(nodeList);
                    }

                    annotation.addAppInfos(appInfo);
                    break;
                case DOCUMENTATION:
                    if (getDebug()) {
                        System.out.println("documentation");
                    }
                    Documentation documentation = new Documentation();

                    //Checks the attributes of documentation
                    attributes = childNode.getAttributes();
                    if (attributes != null) {
                        if (attributes.getNamedItem("source") != null) {
                            String sourceValue = ((Attr) attributes.getNamedItem("source")).getValue();
                            if (!isAnyUri(sourceValue)) {
                                throw new InvalidAnyUriException(sourceValue, "source attribute in a documentation element.");
                            }
                            documentation.setSource(sourceValue);
                        }
                        if (attributes.getNamedItem("xml:lang") != null) {
                            if (!((Attr) attributes.getNamedItem("xml:lang")).getValue().equals("")) {
                                documentation.setXmlLang(((Attr) attributes.getNamedItem("xml:lang")).getValue());
                            } else {
                                throw new EmptyLangException("documentation");
                            }
                        }
                    }
                    // Appends the childNodes of XSD documentation to the documentation object in the object model
                    if (childNode.getChildNodes() != null) {
                        nodeList = childNode.getChildNodes();
                        documentation.setNodeList(nodeList);
                    }

                    annotation.addDocumentations(documentation);
                    break;
                default:
                    throw new UnsupportedContentException(nodeName, "annotation");
            }
        }
    }

//    protected String convertChildToString(Node node) throws Exception {
//        String returnString = "";
//
//        if (!node.getNodeName().equals("#text")) {
//            returnString += "<" + node.getPrefix() + ":" + node.getLocalName() + " ";
//
//            if (node.hasAttributes()) {
//                for (int i = 0; i < node.getAttributes().getLength(); i++) {
//                    Attr attribute = (Attr) node.getAttributes().item(i);
//                    returnString += attribute.getName() + "=\"" + attribute.getValue() + "\" ";
//                }
//                returnString = returnString.substring(0, returnString.length()-1);
//            }
//
//            if(node.hasChildNodes()) {
//                returnString += ">";
//                for (int j = 0; j < node.getChildNodes().getLength(); j++) {
//                    returnString += convertChildToString(node.getChildNodes().item(j));
//                }
//                returnString += "</" + node.getPrefix() + ":" + node.getLocalName() + ">";
//            } else {
//                returnString += "/>";
//            }
//        } else {
//            returnString += node.getNodeValue();
//        }
//        return returnString;
//    }

}
