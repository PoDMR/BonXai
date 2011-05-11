package de.tudortmund.cs.bonxai.relaxng.parser;

import de.tudortmund.cs.bonxai.relaxng.Optional;
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
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.EmptyPatternException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.GrammarIsNullException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.InvalidAnyUriException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.InvalidNCNameException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.MissingHrefException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.MultiplePatternException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.ParentGrammarIsNullException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.UnsupportedContentException;
import de.tudortmund.cs.bonxai.xsd.tools.NameChecker;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This processor handles the optional elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class OptionalProcessor extends RNGProcessorBase {

    private Optional optional;

    public OptionalProcessor(RelaxNGSchema rngSchema, Grammar grammar) {
        super(rngSchema, grammar);
    }

    @Override
    protected Optional processNode(Node node) throws Exception {

        /**
         * Attributes:
         * -----------
         */
        this.optional = new Optional();
        this.setPatternAttributes(optional, node);
        visitChildren(node);

        if (this.optional.getPatterns().size() < 1) {
            throw new EmptyPatternException("\"optional\" has not enough pattern");
        }

        return this.optional;
    }

    @Override
    protected void processChild(Node childNode) throws Exception {

        /**
         * Children:
         * ---------
         *
         * Full XML Syntax:
         * ----------------
         * (
         *      element|
         *      attribute|
         *      group|
         *      interleave|
         *      choice|
         *      optional|
         *      zeroOrMore|
         *      oneOrMore|
         *      list|
         *      mixed|
         *      ref|
         *      parentRef|
         *      empty|
         *      text|
         *      value|
         *      data|
         *      notAllowed|
         *      externalRef|
         *      grammar
         * )+
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
                            System.out.println("optional: empty");
                        }
                        for (Pattern currentPattern : optional.getPatterns()) {
                            if (currentPattern instanceof Empty) {
                                throw new MultiplePatternException("optional with more than one <empty/>");
                            }
                        }
                        Empty empty = new Empty();
                        this.setPatternAttributes(empty, childNode);
                        this.optional.addPattern(empty);
                        break;
                    case text:
                        if (getDebug()) {
                            System.out.println("optional: text");
                        }
                        Text text = new Text();
                        this.setPatternAttributes(text, childNode);
                        this.optional.addPattern(text);
                        break;
                    case data:
                        if (getDebug()) {
                            System.out.println("optional: data");
                        }
                        DataProcessor dataProcessor = new DataProcessor(rngSchema, this.grammar);
                        this.optional.addPattern(dataProcessor.processNode(childNode));
                        break;
                    case value:
                        if (getDebug()) {
                            System.out.println("optional: value");
                        }
                        ValueProcessor valueProcessor = new ValueProcessor(rngSchema, this.grammar);
                        this.optional.addPattern(valueProcessor.processNode(childNode));
                        break;
                    case list:
                        if (getDebug()) {
                            System.out.println("optional: list");
                        }
                        ListProcessor listProcessor = new ListProcessor(rngSchema, grammar);
                        this.optional.addPattern(listProcessor.processNode(childNode));
                        break;
                    case attribute:
                        if (getDebug()) {
                            System.out.println("optional: attribute");
                        }
                        AttributeProcessor attributeProcessor = new AttributeProcessor(rngSchema, this.grammar);
                        this.optional.addPattern(attributeProcessor.processNode(childNode));
                        break;
                    case element:
                        if (getDebug()) {
                            System.out.println("optional: element");
                        }
                        ElementProcessor elementProcessor = new ElementProcessor(rngSchema, this.grammar);
                        this.optional.addPattern(elementProcessor.processNode(childNode));
                        break;
                    case optional:
                        if (getDebug()) {
                            System.out.println("optional: optional");
                        }
                        OptionalProcessor optionalProcessor = new OptionalProcessor(rngSchema, this.grammar);
                        this.optional.addPattern(optionalProcessor.processNode(childNode));
                        break;
                    case mixed:
                        if (getDebug()) {
                            System.out.println("optional: mixed");
                        }
                        MixedProcessor mixedProcessor = new MixedProcessor(rngSchema, this.grammar);
                        this.optional.addPattern(mixedProcessor.processNode(childNode));
                        break;
                    case zeroOrMore:
                        if (getDebug()) {
                            System.out.println("optional: zeroOrMore");
                        }
                        ZeroOrMoreProcessor zeroOrMoreProcessor = new ZeroOrMoreProcessor(rngSchema, grammar);
                        this.optional.addPattern(zeroOrMoreProcessor.processNode(childNode));
                        break;
                    case externalRef:
                        if (getDebug()) {
                            System.out.println("optional: externalRef");
                        }
                        NamedNodeMap externalRefAttributes = childNode.getAttributes();
                        String externalRefName = null;
                        if (externalRefAttributes != null && externalRefAttributes.getNamedItem("href") != null) {
                            externalRefName = ((Attr) externalRefAttributes.getNamedItem("href")).getValue();
                            if (!NameChecker.isAnyUri(externalRefName)) {
                                throw new InvalidAnyUriException(externalRefName, "externalRef under optional");
                            }
                        }
                        if (externalRefName == null) {
                            throw new MissingHrefException(nodeName);
                        }
                        ExternalRef externalRef = new ExternalRef(externalRefName);
                        this.setPatternAttributes(externalRef, childNode);
                        this.optional.addPattern(externalRef);
                        break;
                    case notAllowed:
                        if (getDebug()) {
                            System.out.println("optional: notAllowed");
                        }
                        NotAllowed notAllowed = new NotAllowed();
                        this.setPatternAttributes(notAllowed, childNode);
                        this.optional.addPattern(notAllowed);
                        break;
                    case grammar:
                        if (getDebug()) {
                            System.out.println("optional: grammar");
                        }
                        GrammarProcessor grammarProcessor = new GrammarProcessor(rngSchema, grammar);
                        this.optional.addPattern(grammarProcessor.processNode(childNode));
                        break;
                    case ref:
                        if (getDebug()) {
                            System.out.print("optional: ref");
                        }
                        NamedNodeMap attributes = childNode.getAttributes();
                        if (attributes != null && attributes.getNamedItem("name") != null) {
                            String refName = ((Attr) attributes.getNamedItem("name")).getValue();

                            if (!NameChecker.isNCName(refName)) {
                                throw new InvalidNCNameException(refName, "ref under optional");
                            }

                            if (getDebug()) {
                                System.out.print(", name: " + refName + "\n");
                            }

                            if (this.grammar != null) {
                                this.grammar.registerDefinePatternInLookUpTable(new Define(refName));
                                Ref patternRef = new Ref(this.grammar.getDefineLookUpTable().getReference(refName), this.grammar);
                                patternRef.setRefName(refName);
                                this.setPatternAttributes(patternRef, childNode);
                                this.optional.addPattern(patternRef);
                            } else {
                                throw new GrammarIsNullException(refName);
                            }
                        }
                        break;
                    case parentRef:
                        if (getDebug()) {
                            System.out.print("optional: parentRef");
                        }
                        NamedNodeMap attributesParentRef = childNode.getAttributes();
                        if (attributesParentRef != null && attributesParentRef.getNamedItem("name") != null) {
                            String parentRefName = ((Attr) attributesParentRef.getNamedItem("name")).getValue();

                            if (!NameChecker.isNCName(parentRefName)) {
                                throw new InvalidNCNameException(parentRefName, "parentRef under optional");
                            }

                            if (getDebug()) {
                                System.out.print(", name: " + parentRefName + "\n");
                            }

                            if (this.grammar.getParentGrammar() != null) {
                                this.grammar.getParentGrammar().registerDefinePatternInLookUpTable(new Define(parentRefName));
                            } else {
                                throw new ParentGrammarIsNullException("optional: parentRef (\"" + parentRefName + "\")");
                            }

                            ParentRef parentRefPattern = new ParentRef(this.grammar.getParentGrammar().getDefineLookUpTable().getReference(parentRefName), this.grammar, this.grammar.getParentGrammar());
                            parentRefPattern.setRefName(parentRefName);
                            this.setPatternAttributes(parentRefPattern, childNode);
                            this.optional.addPattern(parentRefPattern);
                        }
                        break;
                    case oneOrMore:
                        if (getDebug()) {
                            System.out.println("optional: oneOrMore");
                        }
                        OneOrMoreProcessor oneOrMoreProcessor = new OneOrMoreProcessor(rngSchema, grammar);
                        this.optional.addPattern(oneOrMoreProcessor.processNode(childNode));
                        break;
                    case choice:
                        if (getDebug()) {
                            System.out.println("optional: choice");
                        }
                        // choice as pattern
                        ChoiceProcessor choiceProcessor = new ChoiceProcessor(rngSchema, this.grammar);
                        this.optional.addPattern(choiceProcessor.processNode(childNode));
                        break;
                    case group:
                        if (getDebug()) {
                            System.out.println("optional: group");
                        }
                        GroupProcessor groupProcessor = new GroupProcessor(rngSchema, grammar);
                        this.optional.addPattern(groupProcessor.processNode(childNode));
                        break;
                    case interleave:
                        if (getDebug()) {
                            System.out.println("optional: interleave");
                        }
                        InterleaveProcessor interleaveProcessor = new InterleaveProcessor(rngSchema, grammar);
                        this.optional.addPattern(interleaveProcessor.processNode(childNode));
                        break;
                    default:
                    throw new UnsupportedContentException(nodeName, "optional");
                }
            }
        }
    }
}
