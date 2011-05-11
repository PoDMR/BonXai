package de.tudortmund.cs.bonxai.relaxng.parser;

import de.tudortmund.cs.bonxai.relaxng.Grammar;
import de.tudortmund.cs.bonxai.relaxng.IncludeContent;
import de.tudortmund.cs.bonxai.relaxng.RelaxNGSchema;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.UnsupportedContentException;
import org.w3c.dom.Node;

/**
 * This processor handles div elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class DivProcessor extends RNGProcessorBase {

    private IncludeContent includeContent;

    public DivProcessor(RelaxNGSchema rngSchema, Grammar grammar) {
        super(rngSchema, grammar);

    }

    DivProcessor(RelaxNGSchema rngSchema, Grammar grammar, IncludeContent includeContent) {
        super(rngSchema, grammar);
        this.includeContent = includeContent;
    }

    @Override
    protected Object processNode(Node node) throws Exception {

        visitChildren(node);

        return null;
    }

    @Override
    protected void processChild(Node childNode) throws Exception {

        /**
         * Children:
         * ---------
         *      (
         *          start|
         *          define|
         *          div|
         *          include
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
                    case start:
                        if (getDebug()) {
                            System.out.println("div: start");
                        }
                        StartProcessor startProcessor = new StartProcessor(rngSchema, this.grammar);
                        if (this.includeContent != null) {
                            this.includeContent.addStartPattern(startProcessor.processNode(childNode));
                        } else {
                            this.grammar.addStartPattern(startProcessor.processNode(childNode));
                        }

                        break;
                    case define:
                        if (getDebug()) {
                            System.out.println("div: define");
                        }
                        if (this.includeContent != null) {
                            DefineProcessor defineProcessor = new DefineProcessor(rngSchema, this.grammar, this.includeContent);
                            defineProcessor.processNode(childNode);
                        } else {
                            DefineProcessor defineProcessor = new DefineProcessor(rngSchema, this.grammar);
                            defineProcessor.processNode(childNode);
                        }
                        break;
                    case div:
                        if (getDebug()) {
                            System.out.println("div: div");
                        }
                        if (this.includeContent != null) {
                            DivProcessor divProcessor = new DivProcessor(rngSchema, this.grammar, this.includeContent);
                            divProcessor.processNode(childNode);
                        } else {
                            DivProcessor divProcessor = new DivProcessor(rngSchema, this.grammar);
                            divProcessor.processNode(childNode);
                        }
                        break;
                    case include:
                        if (getDebug()) {
                            System.out.println("div: include");
                        }
                        IncludeProcessor includeProcessor = new IncludeProcessor(rngSchema, this.grammar);
                        this.grammar.addIncludeContent(includeProcessor.processNode(childNode));
                        break;
                    default:
                        throw new UnsupportedContentException(nodeName, "grammar");
                }
            }
        }
    }
}
