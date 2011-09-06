package eu.fox7.bonxai.relaxng.parser;

import eu.fox7.bonxai.relaxng.Grammar;
import eu.fox7.bonxai.relaxng.NameClass;
import eu.fox7.bonxai.relaxng.RelaxNGSchema;
import eu.fox7.bonxai.relaxng.parser.exceptions.UnsupportedContentException;

import java.util.LinkedList;
import org.w3c.dom.Node;

/**
 * This processor handles the except elements for nameClass rule
 * of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class ExceptNameProcessor extends RNGProcessorBase {

    private LinkedList<NameClass> nameClasses = new LinkedList<NameClass>();

    public ExceptNameProcessor(RelaxNGSchema rngSchema, Grammar grammar) {
        super(rngSchema, grammar);
    }

    @Override
    protected LinkedList<NameClass> processNode(Node node) throws Exception {

        /**
         * Attributes:
         * -----------
         */
        visitChildren(node);

        return this.nameClasses;
    }

    @Override
    protected void processChild(Node childNode) throws Exception {

        /**
         * Children:
         * ---------
         * 
         *      (
         *         name|
         *         anyName|
         *         nsName|
         *         choice
         *      )*
         * 
         */
        String nodeName = childNode.getNodeName();
        if (nodeName.contains(":")) {
            nodeName = nodeName.split(":")[1];
        }
        if (childNode != null && !"#text".equals(nodeName) && !"#comment".equals(nodeName) && !"#document".equals(nodeName) && !"#cdata-section".equals(nodeName)) {
            if (valueIsValid(nodeName)) {
                switch (CASE.valueOf(nodeName)) {
                    case name:
                        if (getDebug()) {
                            System.out.println("except (nameClass): name");
                        }
                        NameProcessor nameProcessor = new NameProcessor(rngSchema);
                        this.nameClasses.add(nameProcessor.processNode(childNode));
                        break;
                    case anyName:
                        if (getDebug()) {
                            System.out.println("except (nameClass): anyName");
                        }
                        AnyNameProcessor anyNameProcessor = new AnyNameProcessor(rngSchema);
                        this.nameClasses.add(anyNameProcessor.processNode(childNode));
                        break;
                    case choice:
                        if (getDebug()) {
                            System.out.println("except (nameClass): choice");
                        }
                        ChoiceNameProcessor choiceNameProcessor = new ChoiceNameProcessor(rngSchema);
                        this.nameClasses.add(choiceNameProcessor.processNode(childNode));
                        break;
                    case nsName:
                        if (getDebug()) {
                            System.out.println("except (nameClass): nsName");
                        }
                        NsNameProcessor nsNameProcessor = new NsNameProcessor(rngSchema);
                        this.nameClasses.add(nsNameProcessor.processNode(childNode));
                        break;
                    default:
                    throw new UnsupportedContentException(nodeName, "except (nameClass)");
                }
            }
        }
    }
}
