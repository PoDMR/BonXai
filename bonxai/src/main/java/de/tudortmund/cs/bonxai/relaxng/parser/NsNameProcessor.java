package de.tudortmund.cs.bonxai.relaxng.parser;

import de.tudortmund.cs.bonxai.relaxng.NameClass;
import de.tudortmund.cs.bonxai.relaxng.NsName;
import de.tudortmund.cs.bonxai.relaxng.RelaxNGSchema;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.UnsupportedContentException;
import java.util.Iterator;
import java.util.LinkedList;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This processor handles the nsName elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class NsNameProcessor extends RNGProcessorBase {

    private NsName nsName;

    public NsNameProcessor(RelaxNGSchema rngSchema) {
        super(rngSchema);
    }

    @Override
    protected NsName processNode(Node node) throws Exception {

        /**
         * Attributes:
         * -----------
         * ns CDATA #IMPLIED
         */
        this.nsName = new NsName();

        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null && attributes.getNamedItem("ns") != null) {
            String nsNameString = ((Attr) attributes.getNamedItem("ns")).getValue();
            this.nsName.setAttributeNamespace(nsNameString);
        }

        visitChildren(node);

        return this.nsName;
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
                            System.out.println("nsName: except");
                        }
                        ExceptNameProcessor exceptNameProcessor = new ExceptNameProcessor(rngSchema, this.grammar);

                        LinkedList<NameClass> nameClasses = exceptNameProcessor.processNode(childNode);
                        for (Iterator<NameClass> it = nameClasses.iterator(); it.hasNext();) {
                            NameClass nameClass = it.next();
                            this.nsName.addExceptName(nameClass);
                        }
                        break;
                    default:
                        throw new UnsupportedContentException(nodeName, "grammar");
                }
            }
        }

    }
}
