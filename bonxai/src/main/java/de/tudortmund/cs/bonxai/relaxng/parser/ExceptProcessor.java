package de.tudortmund.cs.bonxai.relaxng.parser;

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
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.ParentGrammarIsNullException;
import de.tudortmund.cs.bonxai.relaxng.parser.exceptions.UnsupportedContentException;
import de.tudortmund.cs.bonxai.xsd.tools.NameChecker;

import java.util.LinkedList;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This processor handles the except elements of a Relax NG XSDSchema
 *
 * @author Lars Schmidt
 */
class ExceptProcessor extends RNGProcessorBase {

    private LinkedList<Pattern> patterns = new LinkedList<Pattern>();

    public ExceptProcessor(RelaxNGSchema rngSchema, Grammar grammar) {
        super(rngSchema, grammar);
    }

    @Override
    protected LinkedList<Pattern> processNode(Node node) throws Exception {

        /**
         * Attributes:
         * -----------
         */
        visitChildren(node);

        if (!this.patterns.isEmpty()) {
            return this.patterns;
        } else {
            throw new EmptyPatternException("except");
        }

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
                            System.out.println("except: empty");
                        }
                        Empty empty = new Empty();
                        this.setPatternAttributes(empty, childNode);
                        this.patterns.add(empty);
                        break;
                    case text:
                        if (getDebug()) {
                            System.out.println("except: text");
                        }
                        Text text = new Text();
                        this.setPatternAttributes(text, childNode);
                        this.patterns.add(text);
                        break;
                    case data:
                        if (getDebug()) {
                            System.out.println("except: data");
                        }
                        DataProcessor dataProcessor = new DataProcessor(rngSchema, this.grammar);
                        this.patterns.add(dataProcessor.processNode(childNode));
                        break;
                    case value:
                        if (getDebug()) {
                            System.out.println("except: value");
                        }
                        ValueProcessor valueProcessor = new ValueProcessor(rngSchema, this.grammar);
                        this.patterns.add(valueProcessor.processNode(childNode));
                        break;
                    case list:
                        if (getDebug()) {
                            System.out.println("except: list");
                        }
                        ListProcessor listProcessor = new ListProcessor(rngSchema, grammar);
                        this.patterns.add(listProcessor.processNode(childNode));
                        break;
                    case attribute:
                        if (getDebug()) {
                            System.out.println("except: attribute");
                        }
                        AttributeProcessor attributeProcessor = new AttributeProcessor(rngSchema, this.grammar);
                        this.patterns.add(attributeProcessor.processNode(childNode));
                        break;
                    case element:
                        if (getDebug()) {
                            System.out.println("except: element");
                        }
                        ElementProcessor elementProcessor = new ElementProcessor(rngSchema, this.grammar);
                        this.patterns.add(elementProcessor.processNode(childNode));
                        break;
                    case optional:
                        if (getDebug()) {
                            System.out.println("except: optional");
                        }
                        OptionalProcessor optionalProcessor = new OptionalProcessor(rngSchema, this.grammar);
                        this.patterns.add(optionalProcessor.processNode(childNode));
                        break;
                    case mixed:
                        if (getDebug()) {
                            System.out.println("except: mixed");
                        }
                        MixedProcessor mixedProcessor = new MixedProcessor(rngSchema, this.grammar);
                        this.patterns.add(mixedProcessor.processNode(childNode));
                        break;
                    case zeroOrMore:
                        if (getDebug()) {
                            System.out.println("except: zeroOrMore");
                        }
                        ZeroOrMoreProcessor zeroOrMoreProcessor = new ZeroOrMoreProcessor(rngSchema, grammar);
                        this.patterns.add(zeroOrMoreProcessor.processNode(childNode));
                        break;
                    case externalRef:
                        if (getDebug()) {
                            System.out.println("except: externalRef");
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
                        this.patterns.add(externalRef);
                        break;
                    case notAllowed:
                        if (getDebug()) {
                            System.out.println("except: notAllowed");
                        }
                        NotAllowed notAllowed = new NotAllowed();
                        this.setPatternAttributes(notAllowed, childNode);
                        this.patterns.add(notAllowed);
                        break;
                    case grammar:
                        if (getDebug()) {
                            System.out.println("except: grammar");
                        }
                        GrammarProcessor grammarProcessor = new GrammarProcessor(rngSchema, grammar);
                        this.patterns.add(grammarProcessor.processNode(childNode));
                        break;
                    case ref:
                        if (getDebug()) {
                            System.out.print("except: ref");
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
                                this.patterns.add(patternRef);
                            } else {
                                throw new GrammarIsNullException(refName);
                            }
                        }
                        break;
                    case parentRef:
                        if (getDebug()) {
                            System.out.print("except: parentRef");
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
                                throw new ParentGrammarIsNullException("except: parentRef (\"" + parentRefName + "\")");
                            }

                            ParentRef parentRefPattern = new ParentRef(this.grammar.getParentGrammar().getDefineLookUpTable().getReference(parentRefName), this.grammar, this.grammar.getParentGrammar());
                            parentRefPattern.setRefName(parentRefName);
                            this.setPatternAttributes(parentRefPattern, childNode);
                            this.patterns.add(parentRefPattern);
                        }
                        break;
                    case oneOrMore:
                        if (getDebug()) {
                            System.out.println("except: oneOrMore");
                        }
                        OneOrMoreProcessor oneOrMoreProcessor = new OneOrMoreProcessor(rngSchema, grammar);
                        this.patterns.add(oneOrMoreProcessor.processNode(childNode));
                        break;
                    case choice:
                        if (getDebug()) {
                            System.out.println("except: choice");
                        }
                        // choice as pattern
                        ChoiceProcessor choiceProcessor = new ChoiceProcessor(rngSchema, this.grammar);
                        this.patterns.add(choiceProcessor.processNode(childNode));
                        break;
                    case group:
                        if (getDebug()) {
                            System.out.println("except: group");
                        }
                        GroupProcessor groupProcessor = new GroupProcessor(rngSchema, grammar);
                        this.patterns.add(groupProcessor.processNode(childNode));
                        break;
                    case interleave:
                        if (getDebug()) {
                            System.out.println("except: interleave");
                        }
                        InterleaveProcessor interleaveProcessor = new InterleaveProcessor(rngSchema, grammar);
                        this.patterns.add(interleaveProcessor.processNode(childNode));
                        break;
                    default:
                    throw new UnsupportedContentException(nodeName, "except");
                }
            }
        }
    }
}
