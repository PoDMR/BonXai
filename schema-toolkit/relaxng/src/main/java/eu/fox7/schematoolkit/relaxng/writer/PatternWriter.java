package eu.fox7.schematoolkit.relaxng.writer;

import eu.fox7.schematoolkit.common.NamespaceList;
import eu.fox7.schematoolkit.relaxng.*;
import eu.fox7.schematoolkit.relaxng.om.Attribute;
import eu.fox7.schematoolkit.relaxng.om.Choice;
import eu.fox7.schematoolkit.relaxng.om.Data;
import eu.fox7.schematoolkit.relaxng.om.Define;
import eu.fox7.schematoolkit.relaxng.om.Element;
import eu.fox7.schematoolkit.relaxng.om.Empty;
import eu.fox7.schematoolkit.relaxng.om.ExternalRef;
import eu.fox7.schematoolkit.relaxng.om.Grammar;
import eu.fox7.schematoolkit.relaxng.om.Group;
import eu.fox7.schematoolkit.relaxng.om.IncludeContent;
import eu.fox7.schematoolkit.relaxng.om.Interleave;
import eu.fox7.schematoolkit.relaxng.om.List;
import eu.fox7.schematoolkit.relaxng.om.Mixed;
import eu.fox7.schematoolkit.relaxng.om.NotAllowed;
import eu.fox7.schematoolkit.relaxng.om.OneOrMore;
import eu.fox7.schematoolkit.relaxng.om.Optional;
import eu.fox7.schematoolkit.relaxng.om.Param;
import eu.fox7.schematoolkit.relaxng.om.ParentRef;
import eu.fox7.schematoolkit.relaxng.om.Pattern;
import eu.fox7.schematoolkit.relaxng.om.Ref;
import eu.fox7.schematoolkit.relaxng.om.Text;
import eu.fox7.schematoolkit.relaxng.om.Value;
import eu.fox7.schematoolkit.relaxng.om.ZeroOrMore;
import eu.fox7.schematoolkit.relaxng.writer.exceptions.EmptyPatternException;
import eu.fox7.schematoolkit.relaxng.writer.exceptions.InvalidAnyUriException;
import eu.fox7.schematoolkit.relaxng.writer.exceptions.InvalidHrefException;
import eu.fox7.schematoolkit.relaxng.writer.exceptions.InvalidNCNameException;
import eu.fox7.schematoolkit.relaxng.writer.exceptions.InvalidQNameException;
import eu.fox7.schematoolkit.relaxng.writer.exceptions.MissingHrefException;
import eu.fox7.schematoolkit.relaxng.writer.exceptions.MissingNameException;
import eu.fox7.schematoolkit.relaxng.writer.exceptions.MissingPatternException;
import eu.fox7.schematoolkit.relaxng.writer.exceptions.MultipleNameException;
import eu.fox7.schematoolkit.relaxng.writer.exceptions.PatternReferencedButNotDefinedException;
import eu.fox7.schematoolkit.relaxng.writer.exceptions.UnknownPatternException;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Writer for all pattern structures of a Relax NG schema.
 * Writing means returning the correct DOM elements for the given data structure
 * object.
 * In this case: Pattern
 *
 * @author Lars Schmidt
 */
public class PatternWriter extends RNGWriterBase {

    /**
     * Constructor of class PatternWriter
     * This class handles the building of a DOM Element for the given Relax NG
     * pattern and manages the calls for all its children.
     * @param rngDocument
     * @param namespaces
     */
    public PatternWriter(org.w3c.dom.Document rngDocument, NamespaceList namespaces) {
        super(rngDocument, namespaces);
    }

    /**
     * Public method for the creation of an org.w3c.dom.Element for the given
     * Relax NG pattern
     * @param pattern
     * @return org.w3c.dom.Element
     * @throws Exception
     */
    public org.w3c.dom.Element createNodeForPattern(Pattern pattern) throws Exception {
        return this.switchNodeCreationForPattern(pattern);
    }

    /**
     * Method to switch over all possible Patterns
     * @param pattern       Pattern to create a DOM node for
     * @return org.w3c.dom.Element      resulting DOM element
     * @throws Exception    UnknownPatternException
     */
    private org.w3c.dom.Element switchNodeCreationForPattern(Pattern pattern) throws Exception {
        org.w3c.dom.Element returnElement;
        if (pattern instanceof Ref) {
            returnElement = createNodeForRef((Ref) pattern);
        } else if (pattern instanceof Attribute) {
            returnElement = createNodeForAttribute((Attribute) pattern);
        } else if (pattern instanceof Choice) {
            returnElement = createNodeForChoice((Choice) pattern);
        } else if (pattern instanceof Data) {
            returnElement = createNodeForData((Data) pattern);
        } else if (pattern instanceof Group) {
            returnElement = createNodeForGroup((Group) pattern);
        } else if (pattern instanceof List) {
            returnElement = createNodeForList((List) pattern);
        } else if (pattern instanceof Interleave) {
            returnElement = createNodeForInterleave((Interleave) pattern);
        } else if (pattern instanceof NotAllowed) {
            returnElement = createNodeForNotAllowed((NotAllowed) pattern);
        } else if (pattern instanceof Grammar) {
            returnElement = createNodeForGrammar((Grammar) pattern);
        } else if (pattern instanceof Text) {
            returnElement = createNodeForText((Text) pattern);
        } else if (pattern instanceof Value) {
            returnElement = createNodeForValue((Value) pattern);
        } else if (pattern instanceof Empty) {
            returnElement = createNodeForEmpty((Empty) pattern);
        } else if (pattern instanceof Element) {
            returnElement = createNodeForElement((Element) pattern);
        } else if (pattern instanceof OneOrMore) {
            returnElement = createNodeForOneOrMore((OneOrMore) pattern);
        } else if (pattern instanceof Optional) {
            returnElement = createNodeForOptional((Optional) pattern);
        } else if (pattern instanceof ZeroOrMore) {
            returnElement = createNodeForZeroOrMore((ZeroOrMore) pattern);
        } else if (pattern instanceof Mixed) {
            returnElement = createNodeForMixed((Mixed) pattern);
        } else if (pattern instanceof ParentRef) {
            returnElement = createNodeForParentRef((ParentRef) pattern);
        } else if (pattern instanceof ExternalRef) {
            returnElement = createNodeForExternalRef((ExternalRef) pattern);
        } else {
            throw new UnknownPatternException(pattern.getClass().getName());
        }
        return returnElement;
    }

    /**
     * Method for creating a corresponding DOM node for a "grammar"-pattern
     * @param grammar   Source of the creation
     * @return org.w3c.dom.Element  resulting DOM element
     * @throws Exception
     */
    private org.w3c.dom.Element createNodeForGrammar(Grammar grammar) throws Exception {
        org.w3c.dom.Element grammarNode = createElementNode("grammar");

        // Set the standard Pattern-attributes (xmlns, ns, datatypeLibrary) for the grammar result node
        setPatternAttributes(grammarNode, grammar);

        if (grammar.getIncludeContents().isEmpty() && grammar.getDefinedPatternNames().isEmpty() && grammar.getStartPatterns().isEmpty()) {
            throw new EmptyPatternException("grammar without start or include or define");
        }

        // Handle include elements of this grammar object
        if (grammar.getIncludeContents() != null && !grammar.getIncludeContents().isEmpty()) {
            LinkedList<IncludeContent> includeContentList = grammar.getIncludeContents();

            // There can be more than one include child --> loop over all children
            for (Iterator<IncludeContent> it = includeContentList.iterator(); it.hasNext();) {
                IncludeContent includeContent = it.next();
                org.w3c.dom.Element includeContentNode = createElementNode("include");

                // Every include object must have an "href"-entry!
                if (includeContent.getHref() == null || !isAnyUri(includeContent.getHref())) {
                    throw new InvalidHrefException("include --> attribute: \"href\"");
                }

                includeContentNode.setAttribute("href", includeContent.getHref());

                // If there are start-patterns defined in an include,
                // they have to be combined with the start-patterns of the parent grammar object.
                if (includeContent.getStartPatterns() != null && !includeContent.getStartPatterns().isEmpty()) {
                    for (Iterator<Pattern> itInludeStart = includeContent.getStartPatterns().iterator(); itInludeStart.hasNext();) {
                        Pattern startInlcudePattern = itInludeStart.next();
                        org.w3c.dom.Element startInludeNode = this.createNodeForStart(startInlcudePattern);

                        if (grammar.getStartCombineMethod() != null) {
                            startInludeNode.setAttribute("combine", grammar.getStartCombineMethod().name());
                        }
                        // Append the generated start DOM elements to the include element.
                        includeContentNode.appendChild(startInludeNode);
                    }
                }

                // Handle defined-patterns in the correct order. The order is given by the LinkedList of DefinedPatternNames in the include
                if (includeContent.getDefinedPatternNames() != null && !includeContent.getDefinedPatternNames().isEmpty()) {
                    for (Iterator<String> itIncludeDefine = includeContent.getDefinedPatternNames().iterator(); itIncludeDefine.hasNext();) {
                        String defineIncludeName = itIncludeDefine.next();

                        // "define"-tags can also be combined according to their names and the combine method given by the combine-attribute.
                        LinkedList<Define> defineIncludeList = includeContent.getDefinedPatternsFromLookUpTable(defineIncludeName);
                        for (Iterator<Define> defineIncludeIt = defineIncludeList.iterator(); defineIncludeIt.hasNext();) {
                            Define currentDefineInclude = defineIncludeIt.next();
                            org.w3c.dom.Element defineIncludeNode = createElementNode("define");
                            defineIncludeNode.setAttribute("name", defineIncludeName);
                            if (currentDefineInclude.getCombineMethod() != null) {
                                defineIncludeNode.setAttribute("combine", currentDefineInclude.getCombineMethod().name());
                            }
                            if (currentDefineInclude.getPatterns() != null) {
                                for (Iterator<Pattern> it3 = currentDefineInclude.getPatterns().iterator(); it3.hasNext();) {
                                    Pattern definePattern = it3.next();
                                    if (definePattern != null) {
                                        defineIncludeNode.appendChild(createNodeForPattern(definePattern));
                                    }
                                }
                            }
                            // Append the generated define DOM elements to the include element.
                            includeContentNode.appendChild(defineIncludeNode);
                        }
                    }
                }
                // Append the generated include DOM elements to the grammar element.
                grammarNode.appendChild(includeContentNode);
            }
        }

        // Handle the start-patterns defined in a grammar.
        if (grammar.getStartPatterns() != null && !grammar.getStartPatterns().isEmpty()) {
            for (Iterator<Pattern> itGrammarStart = grammar.getStartPatterns().iterator(); itGrammarStart.hasNext();) {
                Pattern startPattern = itGrammarStart.next();

                org.w3c.dom.Element startNode = this.createNodeForStart(startPattern);

                if (grammar.getStartCombineMethod() != null) {
                    startNode.setAttribute("combine", grammar.getStartCombineMethod().name());
                }
                // Append the generated start DOM elements to the grammar element.
                grammarNode.appendChild(startNode);
            }
        }
        // Handle defined-patterns in the correct order. The order is given by the LinkedList of DefinedPatternNames in the grammar
        if (grammar.getDefinedPatternNames() != null && !grammar.getDefinedPatternNames().isEmpty()) {
            for (Iterator<String> itGrammarDefine = grammar.getDefinedPatternNames().iterator(); itGrammarDefine.hasNext();) {
                String defineName = itGrammarDefine.next();

                // "define"-tags can be combined according to their names and the combine method given by the combine-attribute.
                java.util.List<Define> defineList = grammar.getDefinedPattern(defineName);
                for (Iterator<Define> itDefGra = defineList.iterator(); itDefGra.hasNext();) {
                    Define define = itDefGra.next();

                    org.w3c.dom.Element defineNode = createElementNode("define");
                    defineNode.setAttribute("name", defineName);
                    if (define.getCombineMethod() != null) {
                        defineNode.setAttribute("combine", define.getCombineMethod().name());
                    }
                    if (define.getPatterns() != null) {
                        for (Iterator<Pattern> it2 = define.getPatterns().iterator(); it2.hasNext();) {
                            Pattern definePattern = it2.next();
                            if (definePattern != null) {
                                defineNode.appendChild(createNodeForPattern(definePattern));
                            }
                        }
                    }
                    // Append the generated define DOM elements to the grammar element.
                    grammarNode.appendChild(defineNode);
                }
            }
        }
        return grammarNode;
    }

    /**
     * Method for creating a corresponding DOM node for a "start"-pattern
     * @param startPattern   Source of the creation
     * @return org.w3c.dom.Element  resulting DOM element
     * @throws Exception
     */
    private org.w3c.dom.Element createNodeForStart(Pattern startPattern) throws Exception {
        org.w3c.dom.Element startNode = createElementNode("start");

        if (startPattern != null) {
            startNode.appendChild(createNodeForPattern(startPattern));
        } else {
            throw new MissingPatternException("start content");
        }
//        // Set the standard Pattern-attributes (xmlns, ns, datatypeLibrary) to the result node
//        setPatternAttributes(startNode, startPattern);
        return startNode;
    }

    /**
     * Method for creating a corresponding DOM node for a "element"-pattern
     * @param startPattern   Source of the creation
     * @return org.w3c.dom.Element  resulting DOM element
     * @throws Exception
     */
    private org.w3c.dom.Element createNodeForElement(Element element) throws Exception {
        org.w3c.dom.Element elementNode = createElementNode("element");
        // Handle the attributes of the element:
        if (element.getNameAttribute() != null && element.getNameClass() == null) {
            if (!isQName(element.getNameAttribute())) {
                throw new InvalidQNameException(element.getNameAttribute(), "name-attribute of element");
            }
            elementNode.setAttribute("name", element.getNameAttribute());

        } else if (element.getNameAttribute() == null && element.getNameClass() != null) {
            NameClassWriter nameClassWriter = new NameClassWriter(this.rngDocument, this.rootElementNamespaceList);
            if (element.getNameClass() != null) {
                elementNode.appendChild(nameClassWriter.createNodeForNameClass(element.getNameClass()));
            }
        } else if (element.getNameAttribute() != null && element.getNameClass() != null) {
            throw new MultipleNameException("element: " + element.getNameAttribute());
        } else {
            throw new MissingNameException("element");
        }

        // Set the standard Pattern-attributes (xmlns, ns, datatypeLibrary) to the result node
        setPatternAttributes(elementNode, element);

        if (element.getPatterns().isEmpty()) {
            throw new MissingPatternException("element content");
        }

        // Handle the children of the element:
        for (Iterator<Pattern> it = element.getPatterns().iterator(); it.hasNext();) {
            Pattern currentPattern = it.next();
            if (currentPattern != null) {
                elementNode.appendChild(createNodeForPattern(currentPattern));
            }
        }
        return elementNode;
    }

    /**
     * Method for creating a corresponding DOM node for a "ref"-pattern
     * @param ref   Source of the creation
     * @return org.w3c.dom.Element  resulting DOM element
     * @throws Exception    InvalidNCNameException or PatternReferencedButNotDefinedException
     */
    private org.w3c.dom.Element createNodeForRef(Ref ref) throws InvalidNCNameException, PatternReferencedButNotDefinedException {
        org.w3c.dom.Element refNode = createElementNode("ref");
        // Set the standard Pattern-attributes (xmlns, ns, datatypeLibrary) to the result node
        setPatternAttributes(refNode, ref);


        // The following check works not correct, if there are included grammars and the
        // RelaxNGExternalSchemaLoader is not used! There can be references to defines in the including schema, that are not applicable at this moment

//            if (ref.getDefineList() == null || ref.getDefineList().isEmpty() || ref.getDefineList().getFirst() == null) {
//                throw new PatternReferencedButNotDefinedException("ref: " + ref.getRefName());
//            }

        if (ref.getDefineList() != null && !ref.getDefineList().isEmpty()) {
            if (!isNCName(ref.getDefineList().iterator().next().getName())) {
                throw new InvalidNCNameException(ref.getDefineList().iterator().next().getName(), "name of ref");
            }
            refNode.setAttribute("name", ref.getDefineList().iterator().next().getName());
        } else {
            // Define is not set!
            if (!isNCName(ref.getRefName())) {
                throw new InvalidNCNameException(ref.getRefName(), "name of ref");
            }
            refNode.setAttribute("name", ref.getRefName());
        }
        return refNode;
    }

    /**
     * Method for creating a corresponding DOM node for a "attribute"-pattern
     * @param attribute   Source of the creation
     * @return org.w3c.dom.Element  resulting DOM element
     * @throws Exception
     */
    private org.w3c.dom.Element createNodeForAttribute(Attribute attribute) throws Exception {
        org.w3c.dom.Element attributeNode = createElementNode("attribute");

        // Handle the attributes:
        if (attribute.getNameAttribute() != null && attribute.getNameClass() == null) {
            if (!isQName(attribute.getNameAttribute())) {
                throw new InvalidQNameException(attribute.getNameAttribute(), "name-attribute of attribute");
            }
            attributeNode.setAttribute("name", attribute.getNameAttribute());

        } else if (attribute.getNameAttribute() == null && attribute.getNameClass() != null) {
            NameClassWriter nameClassWriter = new NameClassWriter(this.rngDocument, this.rootElementNamespaceList);
            if (attribute.getNameClass() != null) {
                attributeNode.appendChild(nameClassWriter.createNodeForNameClass(attribute.getNameClass()));
            }
        } else if (attribute.getNameAttribute() != null && attribute.getNameClass() != null) {
            throw new MultipleNameException("attribute: " + attribute.getNameAttribute());
        } else {
            throw new MissingNameException("attribute");
        }

        // Set the standard Pattern-attributes (xmlns, ns, datatypeLibrary) to the result node
        setPatternAttributes(attributeNode, attribute);

        // Handle the children:
        if (attribute.getPattern() != null) {
            attributeNode.appendChild(createNodeForPattern(attribute.getPattern()));
        }
        return attributeNode;
    }

    /**
     * Method for creating a corresponding DOM node for a "oneOrMore"-pattern
     * @param oneOrMore   Source of the creation
     * @return org.w3c.dom.Element  resulting DOM element
     * @throws Exception
     */
    private org.w3c.dom.Element createNodeForOneOrMore(OneOrMore oneOrMore) throws Exception {
        org.w3c.dom.Element oneOrMoreNode = createElementNode("oneOrMore");

        // Set the standard Pattern-attributes (xmlns, ns, datatypeLibrary) to the result node
        setPatternAttributes(oneOrMoreNode, oneOrMore);

        // Handle the children:
        if (oneOrMore.getPatterns().isEmpty()) {
            throw new MissingPatternException("oneOrMore content");
        }

        // In the Full Syntax there are more than one Pattern allowed here.
        for (Iterator<Pattern> it = oneOrMore.getPatterns().iterator(); it.hasNext();) {
            Pattern currentPattern = it.next();
            if (currentPattern != null) {
                oneOrMoreNode.appendChild(createNodeForPattern(currentPattern));
            }
        }
        return oneOrMoreNode;
    }

    /**
     * Method for creating a corresponding DOM node for a "empty"-pattern
     * @param empty   Source of the creation
     * @return org.w3c.dom.Element  resulting DOM element
     * @throws Exception
     */
    private org.w3c.dom.Element createNodeForEmpty(Empty empty) {
        org.w3c.dom.Element emptyNode = createElementNode("empty");
        // Set the standard Pattern-attributes (xmlns, ns, datatypeLibrary) to the result node
        setPatternAttributes(emptyNode, empty);
        return emptyNode;
    }

    /**
     * Method for creating a corresponding DOM node for a "value"-pattern
     * @param value   Source of the creation
     * @return org.w3c.dom.Element  resulting DOM element
     * @throws Exception
     */
    private org.w3c.dom.Element createNodeForValue(Value value) throws InvalidNCNameException {
        org.w3c.dom.Element valueNode = createElementNode("value");
        // Set the standard Pattern-attributes (xmlns, ns, datatypeLibrary) to the result node
        setPatternAttributes(valueNode, value);
        valueNode.setTextContent(value.getContent());

        if (value.getType() != null) {
            if (!isNCName(value.getType())) {
                throw new InvalidNCNameException(value.getType(), "type of value");
            }
            valueNode.setAttribute("type", value.getType());
        }
        return valueNode;
    }

    /**
     * Method for creating a corresponding DOM node for a "text"-pattern
     * @param text   Source of the creation
     * @return org.w3c.dom.Element  resulting DOM element
     * @throws Exception
     */
    private org.w3c.dom.Element createNodeForText(Text text) {
        org.w3c.dom.Element textNode = createElementNode("text");
        // Set the standard Pattern-attributes (xmlns, ns, datatypeLibrary) to the result node
        setPatternAttributes(textNode, text);
        return textNode;
    }

    /**
     * Method for creating a corresponding DOM node for a "notAllowed"-pattern
     * @param notAllowed   Source of the creation
     * @return org.w3c.dom.Element  resulting DOM element
     * @throws Exception
     */
    private org.w3c.dom.Element createNodeForNotAllowed(NotAllowed notAllowed) {
        org.w3c.dom.Element notAllowedNode = createElementNode("notAllowed");
        // Set the standard Pattern-attributes (xmlns, ns, datatypeLibrary) to the result node
        setPatternAttributes(notAllowedNode, notAllowed);
        return notAllowedNode;
    }

    /**
     * Method for creating a corresponding DOM node for a "interleave"-pattern
     * @param interleave   Source of the creation
     * @return org.w3c.dom.Element  resulting DOM element
     * @throws Exception
     */
    private org.w3c.dom.Element createNodeForInterleave(Interleave interleave) throws Exception {
        org.w3c.dom.Element interleaveNode = createElementNode("interleave");
        // Set the standard Pattern-attributes (xmlns, ns, datatypeLibrary) to the result node
        setPatternAttributes(interleaveNode, interleave);

        // Handle the children:
        if (interleave.getPatterns() != null && !interleave.getPatterns().isEmpty()) {
            for (Iterator<Pattern> it = interleave.getPatterns().iterator(); it.hasNext();) {
                Pattern pattern = it.next();
                if (pattern != null) {
                    interleaveNode.appendChild(createNodeForPattern(pattern));
                }
            }
        } else {
            throw new MissingPatternException("interleave content");
        }
        return interleaveNode;
    }

    /**
     * Method for creating a corresponding DOM node for a "choice"-pattern
     * @param choice   Source of the creation
     * @return org.w3c.dom.Element  resulting DOM element
     * @throws Exception
     */
    private org.w3c.dom.Element createNodeForChoice(Choice choice) throws Exception {
        org.w3c.dom.Element choiceNode = createElementNode("choice");
        // Set the standard Pattern-attributes (xmlns, ns, datatypeLibrary) to the result node
        setPatternAttributes(choiceNode, choice);

        // Handle the children:
        if (choice.getPatterns() != null && !choice.getPatterns().isEmpty()) {
            for (Iterator<Pattern> it = choice.getPatterns().iterator(); it.hasNext();) {
                Pattern pattern = it.next();
                if (pattern != null) {
                    choiceNode.appendChild(createNodeForPattern(pattern));
                }
            }
        } else {
            throw new MissingPatternException("choice content");
        }
        return choiceNode;
    }

    /**
     * Method for creating a corresponding DOM node for a "data"-pattern
     * @param data   Source of the creation
     * @return org.w3c.dom.Element  resulting DOM element
     * @throws Exception
     */
    private org.w3c.dom.Element createNodeForData(Data data) throws Exception {
        org.w3c.dom.Element dataNode = createElementNode("data");
        setPatternAttributes(dataNode, data);

        if (data.getType() != null) {
            if (!isNCName(data.getType())) {
                throw new InvalidNCNameException(data.getType(), "type of data");
            }
            dataNode.setAttribute("type", data.getType());
        }
        // Handle the param-children:
        if (data.getParams() != null && !data.getParams().isEmpty()) {
            for (Iterator<Param> it = data.getParams().iterator(); it.hasNext();) {
                Param currentParam = it.next();
                org.w3c.dom.Element currentParamNode = createElementNode("param");
                if (currentParam.getName() != null) {
                    if (!isNCName(currentParam.getName())) {
                        throw new InvalidNCNameException(currentParam.getName(), "name of param under data");
                    }
                    currentParamNode.setAttribute("name", currentParam.getName());
                }
                if (currentParam.getContent() != null) {
                    currentParamNode.setTextContent(currentParam.getContent());
                }
                dataNode.appendChild(currentParamNode);
            }
        }
        // Handle the except-children:
        if (data.getExceptPatterns() != null && !data.getExceptPatterns().isEmpty()) {
            org.w3c.dom.Element exceptNode = createElementNode("except");
            for (Iterator<Pattern> it = data.getExceptPatterns().iterator(); it.hasNext();) {
                Pattern pattern = it.next();
                if (pattern != null) {
                    org.w3c.dom.Element currentPatternNode = createNodeForPattern(pattern);
                    exceptNode.appendChild(currentPatternNode);
                } else {
                    throw new MissingPatternException("data except content");
                }
            }
            dataNode.appendChild(exceptNode);
        }
        return dataNode;
    }

    /**
     * Method for creating a corresponding DOM node for a "group"-pattern
     * @param group   Source of the creation
     * @return org.w3c.dom.Element  resulting DOM element
     * @throws Exception
     */
    private org.w3c.dom.Element createNodeForGroup(Group group) throws Exception {
        org.w3c.dom.Element groupNode = createElementNode("group");
        // Set the standard Pattern-attributes (xmlns, ns, datatypeLibrary) to the result node
        setPatternAttributes(groupNode, group);

        // Handle the children:
        if (group.getPatterns() != null && !group.getPatterns().isEmpty()) {
            for (Iterator<Pattern> it = group.getPatterns().iterator(); it.hasNext();) {
                Pattern pattern = it.next();
                if (pattern != null) {
                    groupNode.appendChild(createNodeForPattern(pattern));
                }
            }
        } else {
            throw new MissingPatternException("group content");
        }
        return groupNode;
    }

    /**
     * Method for creating a corresponding DOM node for a "list"-pattern
     * @param list   Source of the creation
     * @return org.w3c.dom.Element  resulting DOM element
     * @throws Exception
     */
    private org.w3c.dom.Element createNodeForList(List list) throws Exception {
        org.w3c.dom.Element listNode = createElementNode("list");
        // Set the standard Pattern-attributes (xmlns, ns, datatypeLibrary) to the result node
        setPatternAttributes(listNode, list);

        // Handle the children:
        if (list.getPatterns() != null && !list.getPatterns().isEmpty()) {
            for (Iterator<Pattern> it = list.getPatterns().iterator(); it.hasNext();) {
                Pattern pattern = it.next();
                if (pattern != null) {
                    listNode.appendChild(createNodeForPattern(pattern));
                }
            }
        } else {
            throw new MissingPatternException("list content");
        }
        return listNode;
    }

    /**
     * Method for creating a corresponding DOM node for a "optional"-pattern
     * @param optional   Source of the creation
     * @return org.w3c.dom.Element  resulting DOM element
     * @throws Exception
     */
    private org.w3c.dom.Element createNodeForOptional(Optional optional) throws Exception {
        org.w3c.dom.Element optionalNode = createElementNode("optional");
        // Set the standard Pattern-attributes (xmlns, ns, datatypeLibrary) to the result node
        setPatternAttributes(optionalNode, optional);

        // Handle the children:
        if (optional.getPatterns() != null && !optional.getPatterns().isEmpty()) {
            for (Iterator<Pattern> it = optional.getPatterns().iterator(); it.hasNext();) {
                Pattern pattern = it.next();
                if (pattern != null) {
                    optionalNode.appendChild(createNodeForPattern(pattern));
                }
            }
        } else {
            throw new MissingPatternException("optional content");
        }
        return optionalNode;
    }

    /**
     * Method for creating a corresponding DOM node for a "zeroOrMore"-pattern
     * @param zeroOrMore   Source of the creation
     * @return org.w3c.dom.Element  resulting DOM element
     * @throws Exception
     */
    private org.w3c.dom.Element createNodeForZeroOrMore(ZeroOrMore zeroOrMore) throws Exception {
        org.w3c.dom.Element zeroOrMoreNode = createElementNode("zeroOrMore");
        // Set the standard Pattern-attributes (xmlns, ns, datatypeLibrary) to the result node
        setPatternAttributes(zeroOrMoreNode, zeroOrMore);

        // Handle the children:
        if (zeroOrMore.getPatterns() != null && !zeroOrMore.getPatterns().isEmpty()) {
            for (Iterator<Pattern> it = zeroOrMore.getPatterns().iterator(); it.hasNext();) {
                Pattern pattern = it.next();
                if (pattern != null) {
                    zeroOrMoreNode.appendChild(createNodeForPattern(pattern));
                }
            }
        } else {
            throw new MissingPatternException("zeroOrMore content");
        }
        return zeroOrMoreNode;
    }

    /**
     * Method for creating a corresponding DOM node for a "mixed"-pattern
     * @param mixed   Source of the creation
     * @return org.w3c.dom.Element  resulting DOM element
     * @throws Exception
     */
    private org.w3c.dom.Element createNodeForMixed(Mixed mixed) throws Exception {
        org.w3c.dom.Element mixedNode = createElementNode("mixed");
        // Set the standard Pattern-attributes (xmlns, ns, datatypeLibrary) to the result node
        setPatternAttributes(mixedNode, mixed);

        // Handle the children:
        if (mixed.getPatterns() != null && !mixed.getPatterns().isEmpty()) {
            for (Iterator<Pattern> it = mixed.getPatterns().iterator(); it.hasNext();) {
                Pattern pattern = it.next();
                if (pattern != null) {
                    mixedNode.appendChild(createNodeForPattern(pattern));
                }
            }
        } else {
            throw new MissingPatternException("zeroOrMore content");
        }
        return mixedNode;
    }

    /**
     * Method for creating a corresponding DOM node for a "parentRef"-pattern
     * @param parentRef   Source of the creation
     * @return org.w3c.dom.Element  resulting DOM element
     * @throws Exception
     */
    private org.w3c.dom.Element createNodeForParentRef(ParentRef parentRef) throws Exception {
        org.w3c.dom.Element parentRefNode = createElementNode("parentRef");
        // Set the standard Pattern-attributes (xmlns, ns, datatypeLibrary) to the result node
        setPatternAttributes(parentRefNode, parentRef);

        if (parentRef.getDefineList() != null && !parentRef.getDefineList().isEmpty()) {
            if (parentRef.getDefineList() == null || !isNCName(parentRef.getDefineList().iterator().next().getName())) {
                throw new InvalidNCNameException(parentRef.getDefineList().iterator().next().getName(), "name of parentRef");
            }
            parentRefNode.setAttribute("name", parentRef.getDefineList().iterator().next().getName());
        }  else {
            // Define is not set!
            if (!isNCName(parentRef.getRefName())) {
                throw new InvalidNCNameException(parentRef.getRefName(), "name of parentRef");
            }
            parentRefNode.setAttribute("name", parentRef.getRefName());
        }
//        else {
//            throw new PatternReferencedButNotDefinedException("in parentRef");
//        }
        return parentRefNode;
    }

    /**
     * Method for creating a corresponding DOM node for a "externalRef"-pattern
     * @param externalRef   Source of the creation
     * @return org.w3c.dom.Element  resulting DOM element
     * @throws Exception
     */
    private org.w3c.dom.Element createNodeForExternalRef(ExternalRef externalRef) throws Exception {
        org.w3c.dom.Element externalRefNode = createElementNode("externalRef");
        // Set the standard Pattern-attributes (xmlns, ns, datatypeLibrary) to the result node
        setPatternAttributes(externalRefNode, externalRef);

        if (externalRef.getHref() != null) {
            if (!isAnyUri(externalRef.getHref())) {
                throw new InvalidAnyUriException(externalRef.getHref(), "href of externalRef");
            }
            externalRefNode.setAttribute("href", externalRef.getHref());
        } else {
            throw new MissingHrefException("externalRef");
        }
        return externalRefNode;
    }
}
