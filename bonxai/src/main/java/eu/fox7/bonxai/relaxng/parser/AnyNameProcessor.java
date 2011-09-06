package eu.fox7.bonxai.relaxng.parser;

import eu.fox7.bonxai.relaxng.AnyName;
import eu.fox7.bonxai.relaxng.NameClass;
import eu.fox7.bonxai.relaxng.RelaxNGSchema;
import eu.fox7.bonxai.relaxng.parser.exceptions.MultipleExceptException;
import eu.fox7.bonxai.relaxng.parser.exceptions.UnsupportedContentException;

import java.util.Iterator;
import java.util.LinkedList;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This processor handles the anyName elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class AnyNameProcessor extends RNGProcessorBase {

    private AnyName anyName;
    private boolean exceptAlreadyFound = false;

    public AnyNameProcessor(RelaxNGSchema rngSchema) {
        super(rngSchema);
    }

    @Override
    protected AnyName processNode(Node node) throws Exception {

        /**
         * Attributes:
         * -----------
         */
        this.anyName = new AnyName();

        visitChildren(node);

        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null && attributes.getNamedItem("ns") != null) {
            String nsName = ((Attr) attributes.getNamedItem("ns")).getValue();
            anyName.setAttributeNamespace(nsName);
        }

        return this.anyName;
    }

    @Override
    protected void processChild(Node childNode) throws Exception {

        /**
         * Children:
         * ---------
         * (except)?
         * 
         */
        String nodeName = childNode.getNodeName();
        if (nodeName.contains(":")) {
            nodeName = nodeName.split(":")[1];
        }
        if (childNode != null && !"#text".equals(nodeName) && !"#comment".equals(nodeName) && !"#document".equals(nodeName) && !"#cdata-section".equals(nodeName)) {
            if (valueIsValid(nodeName)) {
                switch (CASE.valueOf(nodeName)) {
                    case except:
                        if (getDebug()) {
                            System.out.println("data: except");
                        }
                        if (exceptAlreadyFound) {
                            throw new MultipleExceptException("anyName");
                        }
                        ExceptNameProcessor exceptNameProcessor = new ExceptNameProcessor(rngSchema, this.grammar);

                        LinkedList<NameClass> nameClasses = exceptNameProcessor.processNode(childNode);
                        for (Iterator<NameClass> it = nameClasses.iterator(); it.hasNext();) {
                            NameClass nameClass = it.next();
                            this.anyName.addExceptName(nameClass);
                        }
                        exceptAlreadyFound = true;
                        break;
                    default:
                    throw new UnsupportedContentException(nodeName, "grammar");
                }
            }
        }
    }
}
