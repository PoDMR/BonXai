package eu.fox7.bonxai.relaxng.parser;

import eu.fox7.bonxai.relaxng.Grammar;
import eu.fox7.bonxai.relaxng.RelaxNGSchema;
import eu.fox7.bonxai.relaxng.parser.exceptions.UnsupportedContentException;

import org.w3c.dom.Node;

/**
 * This processor handles the grammar elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class GrammarProcessor extends RNGProcessorBase {

    private Grammar parentGrammar;

    public GrammarProcessor(RelaxNGSchema rngSchema, Grammar parentGrammar) {
        super(rngSchema);
        this.parentGrammar = parentGrammar;
    }

    @Override
    protected Grammar processNode(Node node) throws Exception {
        /**
         * Attributes:
         * -----------
         * xmlns CDATA #FIXED "http://relaxng.org/ns/structure/1.0"
         */
        /**
         * The grammar is initialized within the base class RNGProcessorBase
         */
        this.setPatternAttributes(grammar, node);

        if (this.parentGrammar != null) {
            this.grammar.setParentGrammar(this.parentGrammar);
        }

        visitChildren(node);

        // The following check works not correct in case of included schemas!
        // External Check-mechanism is necessary after the complete parseprocess.
        // See -> RelaxNGExternalSchemaLoader!

        // Check if all referenced Patterns are defined in this grammar. This is necessary in the Simple XML Syntax of RelaxNG.
//        if (this.grammar.getDefinedPatternNames().size() < this.grammar.getDefineLookUpTable().getAllReferencedObjects().size()) {
//            LinkedList<SymbolTableRef<LinkedList<Define>>> lookUpTableList = this.grammar.getDefineLookUpTable().getReferences();
//            for (Iterator<SymbolTableRef<LinkedList<Define>>> it = lookUpTableList.iterator(); it.hasNext();) {
//                SymbolTableRef<LinkedList<Define>> defineRef = it.next();
//                if (!this.grammar.getDefinedPatternNames().contains(defineRef.getKey())) {
//                    throw new PatternReferencedButNotDefinedException(defineRef.getKey());
//                }
//            }
//        }

//        if (this.grammar.getStartPatterns().isEmpty() && this.grammar.getIncludeContents().isEmpty() && grammar.getDefinedPatternNames().isEmpty()) {
//            throw new EmptyPatternException("grammar without start or include or define");
//        }

        return this.grammar;
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
                            System.out.println("grammar: start");
                        }
                        StartProcessor startProcessor = new StartProcessor(rngSchema, this.grammar);
                        this.grammar.addStartPattern(startProcessor.processNode(childNode));
                        break;
                    case define:
                        if (getDebug()) {
                            System.out.println("grammar: define");
                        }
                        DefineProcessor defineProcessor = new DefineProcessor(rngSchema, this.grammar);
                        defineProcessor.processNode(childNode);
                        break;
                    case div:
                        if (getDebug()) {
                            System.out.println("grammar: div");
                        }
                        DivProcessor divProcessor = new DivProcessor(rngSchema, this.grammar);
                        divProcessor.processNode(childNode);
                        break;
                    case include:
                        if (getDebug()) {
                            System.out.println("grammar: include");
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
