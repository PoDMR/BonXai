package de.tudortmund.cs.bonxai.relaxng.parser;

import de.tudortmund.cs.bonxai.relaxng.CombineMethod;
import de.tudortmund.cs.bonxai.relaxng.Define;
import de.tudortmund.cs.bonxai.relaxng.Empty;
import de.tudortmund.cs.bonxai.relaxng.ExternalRef;
import de.tudortmund.cs.bonxai.relaxng.Grammar;
import de.tudortmund.cs.bonxai.relaxng.NotAllowed;
import de.tudortmund.cs.bonxai.relaxng.ParentRef;
import de.tudortmund.cs.bonxai.relaxng.Pattern;
import de.tudortmund.cs.bonxai.relaxng.Ref;
import de.tudortmund.cs.bonxai.relaxng.RelaxNGSchema;
import de.tudortmund.cs.bonxai.relaxng.Text;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.CombineMethodNotMatchingException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.GrammarIsNullException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.InvalidAnyUriException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.InvalidCombineMethodException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.InvalidNCNameException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.MissingHrefException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.ParentGrammarIsNullException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.PatternNotInitializedException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.UnsupportedContentException;
import de.tudortmund.cs.bonxai.xsd.tools.NameChecker;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This processor handles the start elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class StartProcessor extends RNGProcessorBase {

    Pattern pattern;

    StartProcessor(RelaxNGSchema rngSchema, Grammar grammar) {
        super(rngSchema, grammar);
    }

    @Override
    protected Pattern processNode(Node node) throws Exception {
        /**
         * Attributes:
         * -----------
         */
        if (this.grammar == null) {
            throw new GrammarIsNullException("start");
        }

        CombineMethod combineMethod = null;
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            if (attributes.getNamedItem("combine") != null) {
                if (((Attr) attributes.getNamedItem("combine")).getValue().equals("choice")) {
                    combineMethod = CombineMethod.choice;
                }
                if (((Attr) attributes.getNamedItem("combine")).getValue().equals("interleave")) {
                    combineMethod = CombineMethod.interleave;
                }
                if (combineMethod == null) {
                    throw new InvalidCombineMethodException("start");
                } else {
                    if (this.grammar.getStartCombineMethod() != null) {
                        if (!this.grammar.getStartCombineMethod().equals(combineMethod)) {
                            throw new CombineMethodNotMatchingException("start: " + this.grammar.getStartCombineMethod().name() + " <-> " + combineMethod.name());
                        }
                    }
                    this.grammar.setStartCombineMethod(combineMethod);
                }
            }
        }

        visitChildren(node);
        if (this.pattern != null) {
            return this.pattern;
        } else {
            throw new PatternNotInitializedException("pattern", "start");
        }
    }

    @Override
    protected void processChild(Node childNode) throws Exception {
        /**
         * Children:
         * ---------
         * (
         * element|
         * attribute|
         * group|
         * interleave|
         * choice|
         * optional|
         * zeroOrMore|
         * oneOrMore|
         * list|
         * mixed|
         * ref|
         * parentRef|
         * empty|
         * text|
         * value|
         * data|
         * notAllowed|
         * externalRef|
         * grammar
         * )
         *
         * There is a List of patterns for the case of using a combine method.
         *
         */
        String nodeName = childNode.getNodeName();
        if (nodeName.contains(":")) {
            nodeName = nodeName.split(":")[1];
        }
        if (childNode != null && !"#text".equals(nodeName) && !"#comment".equals(nodeName) && !"#document".equals(nodeName) && !"#cdata-section".equals(nodeName)) {
            if (valueIsValid(nodeName)) {
                switch (CASE.valueOf(nodeName)) {
                    case empty:
                        if (getDebug()) {
                            System.out.println("start: empty");
                        }
                        // This is only allowed in nested grammars! (For Example within an attribute.)
                        Empty empty = new Empty();
                        this.setPatternAttributes(empty, childNode);
                        this.pattern = (empty);
                        break;
                     case text:
                        if (getDebug()) {
                            System.out.println("start: text");
                        }
                        Text text = new Text();
                        this.setPatternAttributes(text, childNode);
                        this.pattern = text;
                        break;
                    case element:
                        if (getDebug()) {
                            System.out.println("start: element");
                        }
                        ElementProcessor elementProcessor = new ElementProcessor(rngSchema, this.grammar);
                        this.pattern = elementProcessor.processNode(childNode);
                        break;
                    case attribute:
                        if (getDebug()) {
                            System.out.println("start: attribute");
                        }
                        AttributeProcessor attributeProcessor = new AttributeProcessor(rngSchema, this.grammar);
                        this.pattern = attributeProcessor.processNode(childNode);
                        break;
                    case group:
                        if (getDebug()) {
                            System.out.println("start: group");
                        }
                        GroupProcessor groupProcessor = new GroupProcessor(rngSchema, grammar);
                        this.pattern = groupProcessor.processNode(childNode);
                        break;
                    case interleave:
                        if (getDebug()) {
                            System.out.println("start: interleave");
                        }
                        InterleaveProcessor interleaveProcessor = new InterleaveProcessor(rngSchema, grammar);
                        this.pattern = interleaveProcessor.processNode(childNode);
                        break;
                    case mixed:
                        if (getDebug()) {
                            System.out.println("start: mixed");
                        }
                        MixedProcessor mixedProcessor = new MixedProcessor(rngSchema, this.grammar);
                        this.pattern = mixedProcessor.processNode(childNode);
                        break;
                    case optional:
                        if (getDebug()) {
                            System.out.println("start: optional");
                        }
                        OptionalProcessor optionalProcessor = new OptionalProcessor(rngSchema, this.grammar);
                        this.pattern = optionalProcessor.processNode(childNode);
                        break;
                    case oneOrMore:
                        if (getDebug()) {
                            System.out.println("start: oneOrMore");
                        }
                        OneOrMoreProcessor oneOrMoreProcessor = new OneOrMoreProcessor(rngSchema, grammar);
                        this.pattern = oneOrMoreProcessor.processNode(childNode);
                        break;
                    case zeroOrMore:
                        if (getDebug()) {
                            System.out.println("start: zeroOrMore");
                        }
                        ZeroOrMoreProcessor zeroOrMoreProcessor = new ZeroOrMoreProcessor(rngSchema, grammar);
                        this.pattern = zeroOrMoreProcessor.processNode(childNode);
                        break;
                    case list:
                        if (getDebug()) {
                            System.out.println("start: list");
                        }
                        ListProcessor listProcessor = new ListProcessor(rngSchema, grammar);
                        this.pattern = listProcessor.processNode(childNode);
                        break;
                    case externalRef:
                        if (getDebug()) {
                            System.out.println("start: externalRef");
                        }
                        NamedNodeMap externalRefAttributes = childNode.getAttributes();
                        String externalRefName = null;
                        if (externalRefAttributes != null && externalRefAttributes.getNamedItem("href") != null) {
                            externalRefName = ((Attr) externalRefAttributes.getNamedItem("href")).getValue();
                            if (!NameChecker.isAnyUri(externalRefName)) {
                                throw new InvalidAnyUriException(externalRefName, "externalRef under start");
                            }
                        }
                        if (externalRefName == null) {
                            throw new MissingHrefException(nodeName);
                        }
                        ExternalRef externalRef = new ExternalRef(externalRefName);
                        this.setPatternAttributes(externalRef, childNode);
                        this.pattern = externalRef;
                        break;
                    case notAllowed:
                        if (getDebug()) {
                            System.out.println("start: notAllowed");
                        }
                        NotAllowed notAllowed = new NotAllowed();
                        this.setPatternAttributes(notAllowed, childNode);
                        this.pattern = notAllowed;
                        break;
                    case grammar:
                        if (getDebug()) {
                            System.out.println("start: grammar");
                        }
                        GrammarProcessor grammarProcessor = new GrammarProcessor(rngSchema, grammar);
                        this.pattern = grammarProcessor.processNode(childNode);
                        break;
                    case ref:
                        if (getDebug()) {
                            System.out.print("start: ref");
                        }
                        NamedNodeMap attributes = childNode.getAttributes();
                        if (attributes != null && attributes.getNamedItem("name") != null) {
                            String refName = ((Attr) attributes.getNamedItem("name")).getValue();

                            if (!NameChecker.isNCName(refName)) {
                                throw new InvalidNCNameException(refName, "ref under start");
                            }

                            if (getDebug()) {
                                System.out.print(", name: " + refName + "\n");
                            }

                            if (this.grammar != null) {
                                this.grammar.registerDefinePatternInLookUpTable(new Define(refName));
                                Ref patternRef = new Ref(this.grammar.getDefineLookUpTable().getReference(refName), this.grammar);
                                patternRef.setRefName(refName);
                                this.setPatternAttributes(patternRef, childNode);
                                this.pattern = patternRef;
                            } else {
                                throw new GrammarIsNullException(refName);
                            }
                        }
                        break;
                    case parentRef:
                        if (getDebug()) {
                            System.out.print("start: parentRef");
                        }
                        NamedNodeMap attributesParentRef = childNode.getAttributes();
                        if (attributesParentRef != null && attributesParentRef.getNamedItem("name") != null) {
                            String parentRefName = ((Attr) attributesParentRef.getNamedItem("name")).getValue();

                            if (!NameChecker.isNCName(parentRefName)) {
                                throw new InvalidNCNameException(parentRefName, "parentRef under start");
                            }

                            if (getDebug()) {
                                System.out.print(", name: " + parentRefName + "\n");
                            }

                            if (this.grammar.getParentGrammar() != null) {
                                this.grammar.getParentGrammar().registerDefinePatternInLookUpTable(new Define(parentRefName));
                            } else {
                                throw new ParentGrammarIsNullException("start: parentRef (\"" + parentRefName + "\")");
                            }

                            ParentRef parentRefPattern = new ParentRef(this.grammar.getParentGrammar().getDefineLookUpTable().getReference(parentRefName), this.grammar, this.grammar.getParentGrammar());
                            parentRefPattern.setRefName(parentRefName);
                            this.setPatternAttributes(parentRefPattern, childNode);
                            this.pattern = parentRefPattern;
                        }
                        break;
                    case data:
                        if (getDebug()) {
                            System.out.println("start: data");
                        }
                        DataProcessor dataProcessor = new DataProcessor(rngSchema, this.grammar);
                        this.pattern = dataProcessor.processNode(childNode);
                        break;
                    case value:
                        if (getDebug()) {
                            System.out.println("start: value");
                        }
                        ValueProcessor valueProcessor = new ValueProcessor(rngSchema, this.grammar);
                        this.pattern = valueProcessor.processNode(childNode);
                        break;
                    case choice:
                        if (getDebug()) {
                            System.out.println("start: choice");
                        }
                        // choice as pattern
                        ChoiceProcessor choiceProcessor = new ChoiceProcessor(rngSchema, this.grammar);
                        this.pattern = choiceProcessor.processNode(childNode);
                        break;
                    default:
                        throw new UnsupportedContentException(nodeName, "start");
                }
            }
        }
    }
}
