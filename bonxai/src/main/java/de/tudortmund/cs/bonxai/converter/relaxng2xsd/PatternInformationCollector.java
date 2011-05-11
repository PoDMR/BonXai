package de.tudortmund.cs.bonxai.converter.relaxng2xsd;

import de.tudortmund.cs.bonxai.relaxng.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

/**
 * Class PatternInformationCollector
 * @author Lars Schmidt
 */
public class PatternInformationCollector {

    private RelaxNGSchema rngSchema;
    private HashMap<Pattern, HashSet<String>> patternIntel;
    private HashSet<Pattern> alreadySeenPatterns;
    private Stack<Pattern> patternStack;
    private LinkedList<Pattern> parents;

    /**
     * Constructor of class PatternInformationCollector
     * @param rngSchema
     */
    public PatternInformationCollector(RelaxNGSchema rngSchema) {
        this.rngSchema = rngSchema;
        this.alreadySeenPatterns = new HashSet<Pattern>();
        this.patternIntel = new HashMap<Pattern, HashSet<String>>();
        this.patternStack = new Stack<Pattern>();
        this.parents = new LinkedList<Pattern>();
    }

    /**
     * Collect all information about each pattern in the given RELAX NG XSDSchema
     */
    public void collectData() {
        Pattern rootPattern = rngSchema.getRootPattern();
        collectDataForPattern(rootPattern, rootPattern);

        while (!this.patternStack.isEmpty()) {
            Pattern pattern = this.patternStack.pop();
            collectDataForPattern(pattern, pattern);
        }
    }

    /**
     * Getter for the collected information about a pattern
     * @param pattern
     * @return HashSet<String>
     */
    public HashSet<String> getDataForPattern(Pattern pattern) {
        if (this.patternIntel.get(pattern) != null && !this.patternIntel.get(pattern).isEmpty()) {
            return this.patternIntel.get(pattern);
        } else {
            collectDataForPattern(pattern, pattern);
            return this.patternIntel.get(pattern);
        }
    }

    /**
     * Method for collecting all information about a given RELAX NG pattern
     *
     * Handling of optional within a choice pattern.
     *
     * @param pattern
     * @param infoPattern
     */
    private void collectDataForPattern(Pattern pattern, Pattern infoPattern) {
        this.parents.add(pattern);
        if (!this.alreadySeenPatterns.contains(pattern)) {
            if (pattern != null) {

                if (pattern instanceof Element) {

                    // Case: Element

                    Element element = (Element) pattern;

                    for (Iterator<Pattern> it = element.getPatterns().iterator(); it.hasNext();) {
                        Pattern innerPattern = it.next();
                        checkInnerPattern(innerPattern, infoPattern);
                        addInfosFromChild(pattern, innerPattern);
                    }

                } else if (pattern instanceof Attribute) {

                    // Case: Attribute

                    Attribute attribute = (Attribute) pattern;

                    checkInnerPattern(attribute.getPattern(), infoPattern);
                    addInfosFromChild(pattern, attribute.getPattern());

                } else if (pattern instanceof Group) {

                    // Case: Group

                    Group group = (Group) pattern;

                    for (Iterator<Pattern> it = group.getPatterns().iterator(); it.hasNext();) {
                        Pattern innerPattern = it.next();
                        checkInnerPattern(innerPattern, infoPattern);
                        addInfosFromChild(group, innerPattern);
                    }

                } else if (pattern instanceof Interleave) {

                    // Case: Interleave

                    Interleave interleave = (Interleave) pattern;

                    for (Iterator<Pattern> it = interleave.getPatterns().iterator(); it.hasNext();) {
                        Pattern innerPattern = it.next();
                        checkInnerPattern(innerPattern, infoPattern);
                        addInfosFromChild(pattern, innerPattern);
                    }

                } else if (pattern instanceof Choice) {

                    // Case: Choice

                    Choice choice = (Choice) pattern;

                    boolean onlyElementContent = false;
                    boolean onlyAttributeContent = false;
                    boolean onlyDataValueContent = false;
                    int countAttributePatterns = 0;
                    for (Iterator<Pattern> it = choice.getPatterns().iterator(); it.hasNext();) {
                        Pattern innerPattern = it.next();

                        checkInnerPattern(innerPattern, infoPattern);
                        addInfosFromChild(pattern, innerPattern);
                        
                        HashSet<String> tempInfos = this.patternIntel.get(innerPattern);

                        if (tempInfos != null) {
                            if (tempInfos.contains("element") && !tempInfos.contains("attribute") && !(tempInfos.contains("data") || tempInfos.contains("value"))) {
                                onlyElementContent = true;
                            }
                            if (!tempInfos.contains("element") && tempInfos.contains("attribute") && !(tempInfos.contains("data") || tempInfos.contains("value"))) {
                                onlyAttributeContent = true;
                            }
                            if (!tempInfos.contains("element") && !tempInfos.contains("attribute") && (tempInfos.contains("data") || tempInfos.contains("value"))) {
                                onlyDataValueContent = true;
                            }
                            if (tempInfos.contains("attribute") || innerPattern instanceof Attribute) {
                                countAttributePatterns++;
                            }
                        }

                        if (innerPattern instanceof Element) {
                            onlyElementContent = true;
                        }
                        if (innerPattern instanceof Attribute) {
                            onlyAttributeContent = true;
                        }
                        if (innerPattern instanceof Data || innerPattern instanceof Value) {
                            onlyDataValueContent = true;
                        }
                    }

                    boolean optional = false;
                    if ((onlyElementContent && onlyAttributeContent) ||
                        (onlyElementContent && onlyDataValueContent) ||
                        (onlyAttributeContent && onlyDataValueContent) ||
                        (countAttributePatterns > 1)) {
                        optional = true;
                    }

                    for (Iterator<Pattern> it = choice.getPatterns().iterator(); it.hasNext();) {
                        Pattern innerPattern = it.next();
                        if (optional) {
                            this.addInfoToMap(innerPattern, "optional");
                        }
                        checkInnerPattern(innerPattern, infoPattern);
                        addInfosFromChild(pattern, innerPattern);
                    }

                } else if (pattern instanceof Optional) {

                    // Case: Optional

                    Optional optional = (Optional) pattern;

                    for (Iterator<Pattern> it = optional.getPatterns().iterator(); it.hasNext();) {
                        Pattern innerPattern = it.next();
                        checkInnerPattern(innerPattern, infoPattern);
                        addInfosFromChild(pattern, innerPattern);
                    }

                } else if (pattern instanceof ZeroOrMore) {

                    // Case: ZeroOrMore

                    ZeroOrMore zeroOrMore = (ZeroOrMore) pattern;

                    for (Iterator<Pattern> it = zeroOrMore.getPatterns().iterator(); it.hasNext();) {
                        Pattern innerPattern = it.next();
                        checkInnerPattern(innerPattern, infoPattern);
                        addInfosFromChild(pattern, innerPattern);
                    }

                } else if (pattern instanceof OneOrMore) {

                    // Case: OneOrMore

                    OneOrMore oneOrMore = (OneOrMore) pattern;

                    for (Iterator<Pattern> it = oneOrMore.getPatterns().iterator(); it.hasNext();) {
                        Pattern innerPattern = it.next();
                        checkInnerPattern(innerPattern, infoPattern);
                        addInfosFromChild(pattern, innerPattern);
                    }

                } else if (pattern instanceof List) {

                    // Case: List

                    List list = (List) pattern;

                    for (Iterator<Pattern> it = list.getPatterns().iterator(); it.hasNext();) {
                        Pattern innerPattern = it.next();
                        checkInnerPattern(innerPattern, infoPattern);
                        addInfosFromChild(pattern, innerPattern);
                    }

                } else if (pattern instanceof Mixed) {

                    // Case: Mixed

                    Mixed mixed = (Mixed) pattern;

                    for (Iterator<Pattern> it = mixed.getPatterns().iterator(); it.hasNext();) {
                        Pattern innerPattern = it.next();
                        checkInnerPattern(innerPattern, infoPattern);
                        addInfosFromChild(pattern, innerPattern);
                    }

                } else if (pattern instanceof Ref) {

                    // Case: Ref

                    Ref ref = (Ref) pattern;
                    LinkedList<Pattern> parentsWithoutItself = new LinkedList<Pattern>(this.parents);
                    parentsWithoutItself.removeLast();
                    if (!parentsWithoutItself.contains(ref)) {
                        for (Iterator<Define> it = ref.getDefineList().iterator(); it.hasNext();) {
                            Define innerDefine = it.next();
                            for (Iterator<Pattern> it2 = innerDefine.getPatterns().iterator(); it2.hasNext();) {
                                Pattern innerPattern = it2.next();
                                checkInnerPattern(innerPattern, infoPattern);
                                addInfosFromChild(pattern, innerPattern);
                            }
                        }
                    }

                } else if (pattern instanceof ParentRef) {

                    // Case: ParentRef

                    ParentRef parentRef = (ParentRef) pattern;
                    LinkedList<Pattern> parentsWithoutItself = new LinkedList<Pattern>(this.parents);
                    parentsWithoutItself.removeLast();
                    if (!parentsWithoutItself.contains(parentRef)) {
                        for (Iterator<Define> it = parentRef.getDefineList().iterator(); it.hasNext();) {
                            Define innerDefine = it.next();
                            for (Iterator<Pattern> it2 = innerDefine.getPatterns().iterator(); it2.hasNext();) {
                                Pattern innerPattern = it2.next();
                                checkInnerPattern(innerPattern, infoPattern);
                                addInfosFromChild(pattern, innerPattern);
                            }
                        }
                    }

                } else if (pattern instanceof Empty) {
//                    Empty empty = (Empty) pattern;
                } else if (pattern instanceof Text) {
//                    Text text = (Text) pattern;
                } else if (pattern instanceof Value) {
//                    Value value = (Value) pattern;
                } else if (pattern instanceof Data) {
//                    Data data = (Data) pattern;
                } else if (pattern instanceof NotAllowed) {
//                    NotAllowed notAllowed = (NotAllowed) pattern;
                } else if (pattern instanceof ExternalRef) {

                    // Case: ExternalRef

                    ExternalRef externalRef = (ExternalRef) pattern;

                    if (externalRef.getRngSchema() != null) {
                        checkInnerPattern(externalRef.getRngSchema().getRootPattern(), infoPattern);
                        addInfosFromChild(pattern, externalRef.getRngSchema().getRootPattern());
                    }

                } else if (pattern instanceof Grammar) {

                    // Case: Grammar

                    Grammar grammar = (Grammar) pattern;

                    for (Iterator<Pattern> it = grammar.getStartPatterns().iterator(); it.hasNext();) {
                        Pattern innerPattern = it.next();
                        checkInnerPattern(innerPattern, infoPattern);
                        addInfosFromChild(pattern, innerPattern);
                    }

                }
            }
        } else {
            if (this.patternIntel.get(pattern) != null) {
                for (Iterator<String> it = this.patternIntel.get(pattern).iterator(); it.hasNext();) {
                    String currentString = it.next();
                    this.addInfoToMap(pattern, currentString);
                    this.addInfoToMap(infoPattern, currentString);
                }
            }
        }
        parents.removeLast();
        this.alreadySeenPatterns.add(pattern);
    }

    /**
     * Check for necessary information and set it for the correct patterns
     * into the global map
     *
     * Information:
     * ------------
     * element,
     * attribute,
     * data,
     * value,
     * text,
     * empty,
     * notAllowed,
     * mixed,
     * optional
     *
     * @param innerPattern
     * @param infoPattern
     */
    private void checkInnerPattern(Pattern innerPattern, Pattern infoPattern) {

        Pattern directParent = null;
        if (!this.parents.isEmpty()) {
            directParent = this.parents.getLast();
            HashSet<String> parentInfos = this.patternIntel.get(directParent);

            // Properties depending on parents
            if (parentInfos != null && !parentInfos.isEmpty() && !(directParent instanceof Element) && !(directParent instanceof Attribute)) {
                if (parentInfos.contains("optional")) {
                    // This info is only used in the case of an attribute.
                    this.addInfoToMap(innerPattern, "optional");
                }
            }
        }

        // Properties depending on children
        if (innerPattern instanceof Element) {
            this.patternStack.push(innerPattern);
            this.addInfo(infoPattern, "element");

        } else if (innerPattern instanceof Attribute) {
            this.patternStack.push(innerPattern);
            this.addInfo(infoPattern, "attribute");

        } else if (innerPattern instanceof Data) {
            this.patternStack.push(innerPattern);
            this.addInfo(infoPattern, "data");

        } else if (innerPattern instanceof Value) {
            this.patternStack.push(innerPattern);
            this.addInfo(infoPattern, "value");

        } else if (innerPattern instanceof Text) {
            this.patternStack.push(innerPattern);
//            this.addInfoToMap(innerPattern, "text");
            this.addInfo(infoPattern, "text");

        } else if (innerPattern instanceof Empty) {
            this.patternStack.push(innerPattern);
//            this.addInfoToMap(innerPattern, "empty");
            this.addInfo(infoPattern, "empty");

        } else if (innerPattern instanceof NotAllowed) {
            this.patternStack.push(innerPattern);
//            this.addInfoToMap(innerPattern, "notAllowed");
            this.addInfo(infoPattern, "notAllowed");

        } else if (innerPattern instanceof Mixed) {
            if (directParent != null) {
                this.addInfoToMap(directParent, "mixed");
            }
            this.collectDataForPattern(innerPattern, infoPattern);

        } else if (innerPattern instanceof Optional) {
            // This info is only used in case of an attribute.
            this.addInfoToMap(innerPattern, "optional");
            this.collectDataForPattern(innerPattern, infoPattern);

        } else if (innerPattern instanceof ZeroOrMore) {
            // This info is only used in case of an attribute.
            this.addInfoToMap(innerPattern, "optional");
            this.collectDataForPattern(innerPattern, infoPattern);

        } else {
            this.collectDataForPattern(innerPattern, infoPattern);
        }
    }

    /**
     * Add the found information to the global map for a given pattern
     * @param infoPattern       target for the information
     * @param info              String - information
     */
    private void addInfoToMap(Pattern infoPattern, String info) {
        HashSet<String> information;
        if (this.patternIntel.get(infoPattern) == null || this.patternIntel.get(infoPattern).isEmpty()) {
            information = new HashSet<String>();
            if (info != null) {
                information.add(info);
            }
            this.patternIntel.put(infoPattern, information);
        } else {
            information = this.patternIntel.get(infoPattern);
            if (info != null) {
                information.add(info);
            }
        }
    }

    /**
     * Getter for the global map of information about all patterns
     * @return HashMap<Pattern, HashSet<String>>    patternIntel
     */
    public HashMap<Pattern, HashSet<String>> getPatternIntel() {
        return patternIntel;
    }

    /**
     * Add information from a given pattern to all of its predecessors of the traversion
     * @param infoPattern
     * @param patternName
     */
    private void addInfo(Pattern infoPattern, String patternName) {
        this.addInfoToMap(infoPattern, patternName);
        for (Iterator<Pattern> it = parents.iterator(); it.hasNext();) {
            Pattern pattern1 = it.next();
            this.addInfoToMap(pattern1, patternName);
        }
    }

    /**
     * Add information to a given pattern from all of its successors
     * @param infoPattern
     * @param childPattern
     */
    private void addInfosFromChild(Pattern infoPattern, Pattern childPattern) {
        if (this.patternIntel.get(childPattern) != null) {
            if (!(childPattern instanceof Element) && !(childPattern instanceof Attribute)) {
                for (Iterator<String> it = this.patternIntel.get(childPattern).iterator(); it.hasNext();) {
                    String currentString = it.next();
                    if (!currentString.equals("optional")) {
                        this.addInfoToMap(infoPattern, currentString);
                    }
                }
            } else if (childPattern instanceof Element) {
                this.addInfoToMap(infoPattern, "element");
            } else if (childPattern instanceof Attribute) {
                this.addInfoToMap(infoPattern, "attribute");
            }

        }
    }
}
