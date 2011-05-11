package de.tudortmund.cs.bonxai.relaxng.parser;

import de.tudortmund.cs.bonxai.relaxng.Define;
import de.tudortmund.cs.bonxai.relaxng.Empty;
import de.tudortmund.cs.bonxai.relaxng.ExternalRef;
import de.tudortmund.cs.bonxai.relaxng.Grammar;
import de.tudortmund.cs.bonxai.relaxng.Group;
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
 * This processor handles the group elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class GroupProcessor extends RNGProcessorBase {

    Group group;

    GroupProcessor(RelaxNGSchema rngSchema, Grammar grammar) {
        super(rngSchema, grammar);
    }

    @Override
    protected Group processNode(Node node) throws Exception {

        /**
         * Attributes:
         * -----------
         */
        this.group = new Group();
        this.setPatternAttributes(group, node);
        visitChildren(node);

        if (this.group.getPatterns().size() < 1) {
            throw new EmptyPatternException("group has not enough pattern");
        }
        return this.group;
    }

    @Override
    protected void processChild(Node childNode) throws Exception {

        /**
         * Children:
         * ---------
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
                            System.out.println("group: empty");
                        }
                        for (Pattern currentPattern : this.group.getPatterns()) {
                            if (currentPattern instanceof Empty) {
                                throw new MultiplePatternException("group with more than one <empty/>");
                            }
                        }
                        Empty empty = new Empty();
                        this.setPatternAttributes(empty, childNode);
                        this.group.addPattern(empty);
                        break;
                    case text:
                        if (getDebug()) {
                            System.out.println("group: text");
                        }
                        Text text = new Text();
                        this.setPatternAttributes(text, childNode);
                        this.group.addPattern(text);
                        break;
                    case data:
                        if (getDebug()) {
                            System.out.println("group: data");
                        }
                        DataProcessor dataProcessor = new DataProcessor(rngSchema, this.grammar);
                        this.group.addPattern(dataProcessor.processNode(childNode));
                        break;
                    case value:
                        if (getDebug()) {
                            System.out.println("group: value");
                        }
                        ValueProcessor valueProcessor = new ValueProcessor(rngSchema, this.grammar);
                        this.group.addPattern(valueProcessor.processNode(childNode));
                        break;
                    case list:
                        if (getDebug()) {
                            System.out.println("group: list");
                        }
                        ListProcessor listProcessor = new ListProcessor(rngSchema, grammar);
                        this.group.addPattern(listProcessor.processNode(childNode));
                        break;
                    case attribute:
                        if (getDebug()) {
                            System.out.println("group: attribute");
                        }
                        AttributeProcessor attributeProcessor = new AttributeProcessor(rngSchema, this.grammar);
                        this.group.addPattern(attributeProcessor.processNode(childNode));
                        break;
                    case element:
                        if (getDebug()) {
                            System.out.println("group: element");
                        }
                        ElementProcessor elementProcessor = new ElementProcessor(rngSchema, this.grammar);
                        this.group.addPattern(elementProcessor.processNode(childNode));
                        break;
                    case mixed:
                        if (getDebug()) {
                            System.out.println("group: mixed");
                        }
                        MixedProcessor mixedProcessor = new MixedProcessor(rngSchema, this.grammar);
                        this.group.addPattern(mixedProcessor.processNode(childNode));
                        break;
                    case optional:
                        if (getDebug()) {
                            System.out.println("group: optional");
                        }
                        OptionalProcessor optionalProcessor = new OptionalProcessor(rngSchema, this.grammar);
                        this.group.addPattern(optionalProcessor.processNode(childNode));
                        break;
                    case zeroOrMore:
                        if (getDebug()) {
                            System.out.println("group: zeroOrMore");
                        }
                        ZeroOrMoreProcessor zeroOrMoreProcessor = new ZeroOrMoreProcessor(rngSchema, grammar);
                        this.group.addPattern(zeroOrMoreProcessor.processNode(childNode));
                        break;
                    case externalRef:
                        if (getDebug()) {
                            System.out.println("group: externalRef");
                        }
                        NamedNodeMap externalRefAttributes = childNode.getAttributes();
                        String externalRefName = null;
                        if (externalRefAttributes != null && externalRefAttributes.getNamedItem("href") != null) {
                            externalRefName = ((Attr) externalRefAttributes.getNamedItem("href")).getValue();
                            if (!NameChecker.isAnyUri(externalRefName)) {
                                throw new InvalidAnyUriException(externalRefName, "externalRef under group");
                            }
                        }
                        if (externalRefName == null) {
                            throw new MissingHrefException(nodeName);
                        }
                        ExternalRef externalRef = new ExternalRef(externalRefName);
                        this.setPatternAttributes(externalRef, childNode);
                        this.group.addPattern(externalRef);
                        break;
                    case notAllowed:
                        if (getDebug()) {
                            System.out.println("group: notAllowed");
                        }
                        NotAllowed notAllowed = new NotAllowed();
                        this.setPatternAttributes(notAllowed, childNode);
                        this.group.addPattern(notAllowed);
                        break;
                    case grammar:
                        if (getDebug()) {
                            System.out.println("group: grammar");
                        }
                        GrammarProcessor grammarProcessor = new GrammarProcessor(rngSchema, grammar);
                        this.group.addPattern(grammarProcessor.processNode(childNode));
                        break;
                    case ref:
                        if (getDebug()) {
                            System.out.print("group: ref");
                        }
                        NamedNodeMap attributes = childNode.getAttributes();
                        if (attributes != null && attributes.getNamedItem("name") != null) {
                            String refName = ((Attr) attributes.getNamedItem("name")).getValue();

                            if (!NameChecker.isNCName(refName)) {
                                throw new InvalidNCNameException(refName, "ref under group");
                            }

                            if (getDebug()) {
                                System.out.print(", name: " + refName + "\n");
                            }

                            if (this.grammar != null) {
                                this.grammar.registerDefinePatternInLookUpTable(new Define(refName));
                                Ref patternRef = new Ref(this.grammar.getDefineLookUpTable().getReference(refName), this.grammar);
                                patternRef.setRefName(refName);
                                this.setPatternAttributes(patternRef, childNode);
                                this.group.addPattern(patternRef);
                            } else {
                                throw new GrammarIsNullException(refName);
                            }
                        }
                        break;
                    case parentRef:
                        if (getDebug()) {
                            System.out.print("group: parentRef");
                        }
                        NamedNodeMap attributesParentRef = childNode.getAttributes();
                        if (attributesParentRef != null && attributesParentRef.getNamedItem("name") != null) {
                            String parentRefName = ((Attr) attributesParentRef.getNamedItem("name")).getValue();

                            if (!NameChecker.isNCName(parentRefName)) {
                                throw new InvalidNCNameException(parentRefName, "parentRef under group");
                            }

                            if (getDebug()) {
                                System.out.print(", name: " + parentRefName + "\n");
                            }

                            if (this.grammar.getParentGrammar() != null) {
                                this.grammar.getParentGrammar().registerDefinePatternInLookUpTable(new Define(parentRefName));
                            } else {
                                throw new ParentGrammarIsNullException("group: parentRef (\"" + parentRefName + "\")");
                            }

                            ParentRef parentRefPattern = new ParentRef(this.grammar.getParentGrammar().getDefineLookUpTable().getReference(parentRefName), this.grammar, this.grammar.getParentGrammar());
                            parentRefPattern.setRefName(parentRefName);
                            this.setPatternAttributes(parentRefPattern, childNode);
                            this.group.addPattern(parentRefPattern);
                        }
                        break;
                    case oneOrMore:
                        if (getDebug()) {
                            System.out.println("group: oneOrMore");
                        }
                        OneOrMoreProcessor oneOrMoreProcessor = new OneOrMoreProcessor(rngSchema, grammar);
                        this.group.addPattern(oneOrMoreProcessor.processNode(childNode));
                        break;
                    case choice:
                        if (getDebug()) {
                            System.out.println("group: choice");
                        }
                        // choice as pattern
                        ChoiceProcessor choiceProcessor = new ChoiceProcessor(rngSchema, this.grammar);
                        this.group.addPattern(choiceProcessor.processNode(childNode));
                        break;
                    case group:
                        if (getDebug()) {
                            System.out.println("group: group");
                        }
                        GroupProcessor groupProcessor = new GroupProcessor(rngSchema, grammar);
                        this.group.addPattern(groupProcessor.processNode(childNode));
                        break;
                    case interleave:
                        if (getDebug()) {
                            System.out.println("group: interleave");
                        }
                        InterleaveProcessor interleaveProcessor = new InterleaveProcessor(rngSchema, grammar);
                        this.group.addPattern(interleaveProcessor.processNode(childNode));
                        break;
                    default:
                    throw new UnsupportedContentException(nodeName, "group");
                }
            }
        }
    }
}
