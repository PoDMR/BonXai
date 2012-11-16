/**
 * Copyright 2009-2012 TU Dortmund
 *
 * This file is part of FoXLib.
 *
 * FoXLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoXLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.fox7.schematoolkit.relaxng.tools;

import eu.fox7.schematoolkit.common.IdentifiedNamespace;
import eu.fox7.schematoolkit.relaxng.*;
import eu.fox7.schematoolkit.relaxng.om.AnyName;
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
import eu.fox7.schematoolkit.relaxng.om.Name;
import eu.fox7.schematoolkit.relaxng.om.NameClass;
import eu.fox7.schematoolkit.relaxng.om.NameClassChoice;
import eu.fox7.schematoolkit.relaxng.om.NotAllowed;
import eu.fox7.schematoolkit.relaxng.om.NsName;
import eu.fox7.schematoolkit.relaxng.om.OneOrMore;
import eu.fox7.schematoolkit.relaxng.om.Optional;
import eu.fox7.schematoolkit.relaxng.om.ParentRef;
import eu.fox7.schematoolkit.relaxng.om.Pattern;
import eu.fox7.schematoolkit.relaxng.om.Ref;
import eu.fox7.schematoolkit.relaxng.om.RelaxNGSchema;
import eu.fox7.schematoolkit.relaxng.om.Text;
import eu.fox7.schematoolkit.relaxng.om.Value;
import eu.fox7.schematoolkit.relaxng.om.ZeroOrMore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * This class fills up the given RELAX NG XSDSchema.
 *
 * Semantically the ns-attribute and all defined identified namespaces are
 * inherited recursivly from the parent patterns/tags.
 *
 * This class writes/copies these properties into every pattern/tag, so that another
 * lookup is not necessary for every object in further processing.
 *
 * - nsAttribute
 * - dataTypeLibrary
 * - namespaceList
 * 
 * @author Lars Schmidt
 */
public class XMLAttributeReplenisher {

    private RelaxNGSchema rngSchema;
    private HashSet<Pattern> alreadySeenPatterns;
    private HashSet<NameClass> alreadySeenNameClasses;
    private HashMap<String, String> identifiedNamespaces;
    private HashSet<String> usedNames;

    /**
     * Constructor of class XMLAttributeReplenisher
     * @param rngSchema
     */
    public XMLAttributeReplenisher(RelaxNGSchema rngSchema) {
        this.rngSchema = rngSchema;
        this.alreadySeenPatterns = new HashSet<Pattern>();
        this.alreadySeenNameClasses = new HashSet<NameClass>();
        this.identifiedNamespaces = new HashMap<String, String>();
        this.usedNames = new HashSet<String>();
    }

    /**
     * Public method for starting the replenishing of the defined xml attributes.
     */
    public void startReplenishment() {
        Pattern rootPattern = rngSchema.getRootPattern();

        for (IdentifiedNamespace currentIdentifiedNamespace: rootPattern.getNamespaceList().getNamespaces()) {
            this.addIdentifiedNamespace(currentIdentifiedNamespace.getIdentifier(), currentIdentifiedNamespace.getUri());
        }
        this.addIdentifiedNamespace("rng", RelaxNGSchema.RELAXNG_NAMESPACE);

        if (rootPattern.getNamespaceList().getNamespaceByUri(RelaxNGSchema.XML_NAMESPACE) != null && rootPattern.getNamespaceList().getNamespaceByUri(RelaxNGSchema.XML_NAMESPACE).getIdentifier() != null) {
            // Nothing to do here
        } else {
            rootPattern.getNamespaceList().addNamespace(new IdentifiedNamespace("xml", RelaxNGSchema.XML_NAMESPACE));
        }
        replenishPattern(rootPattern, null);
    }

    /**
     * Recursive method for replenishing the defined xml attributes from one pattern (parent) to another (child)
     *
     * @param pattern           child pattern
     * @param parentPattern     parent pattern
     */
    private void replenishPattern(Pattern pattern, Pattern parentPattern) {
        if (!this.alreadySeenPatterns.contains(pattern)) {
            this.alreadySeenPatterns.add(pattern);
            if (parentPattern != null && pattern != null) {

                // Handle the attribute: "ns"
                if (pattern.getAttributeNamespace() == null && parentPattern.getAttributeNamespace() != null) {
                    pattern.setAttributeNamespace(parentPattern.getAttributeNamespace());

                    if (!this.identifiedNamespaces.containsValue(parentPattern.getAttributeNamespace()) && !parentPattern.getAttributeNamespace().equals("")) {
                        if (parentPattern.getAttributeNamespace().equals(RelaxNGSchema.RELAXNG_NAMESPACE)) {
                            this.addIdentifiedNamespace("rng", RelaxNGSchema.RELAXNG_NAMESPACE);
                        } else {
                            this.addIdentifiedNamespace("ns_" + this.identifiedNamespaces.size(), parentPattern.getAttributeNamespace().getUri());
                        }
                    }
                }

                // Handle the attribute: "datatypeLibrary"
                if (pattern.getAttributeDatatypeLibrary() == null && parentPattern.getAttributeDatatypeLibrary() != null && !(parentPattern instanceof ExternalRef)) {
                    pattern.setAttributeDatatypeLibrary(parentPattern.getAttributeDatatypeLibrary());
                }

                // Handle all attributes forming the namespace list of a pattern
                if (parentPattern.getNamespaceList() != null) {
                    if (pattern.getNamespaceList() == null) {
                        pattern.setNamespaceList(parentPattern.getNamespaceList());
                    } else {
                        for (Iterator<IdentifiedNamespace> it = parentPattern.getNamespaceList().getNamespaces().iterator(); it.hasNext();) {
                            IdentifiedNamespace currentIdentifiedNamespace = it.next();

                            if (pattern.getNamespaceList().getNamespaceByIdentifier(currentIdentifiedNamespace.getIdentifier()).getUri() == null) {
                                pattern.getNamespaceList().addNamespace(currentIdentifiedNamespace);
                                this.addIdentifiedNamespace(currentIdentifiedNamespace.getIdentifier(), currentIdentifiedNamespace.getUri());
                            }
                        }
                        if (pattern.getDefaultNamespace() == null) {
                            pattern.setDefaultNamespace(parentPattern.getDefaultNamespace());
                        }
                    }
                }
            }

            if (pattern instanceof Element) {

                // Case: Element

                Element element = (Element) pattern;

                if (element.getNameAttribute() != null) {
                    String[] nameArray = null;

                    if (element.getNameAttribute().contains(":")) {
                        nameArray = element.getNameAttribute().split(":");
                    }

                    if (nameArray != null && nameArray.length == 2) {
                        this.usedNames.add(nameArray[1]);
                    } else {
                        this.usedNames.add(element.getNameAttribute());
                    }
                }

                for (Iterator<Pattern> it = element.getPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    this.replenishPattern(innerPattern, pattern);
                }

                this.setValuesFromPatternToNameClass(element.getNameClass(), pattern);

            } else if (pattern instanceof Attribute) {

                // Case: Attribute

                Attribute attribute = (Attribute) pattern;

                if (attribute.getNameAttribute() != null) {
                    String[] nameArray = null;

                    if (attribute.getNameAttribute().contains(":")) {
                        nameArray = attribute.getNameAttribute().split(":");
                    }

                    if (nameArray != null && nameArray.length == 2) {
                        this.usedNames.add(nameArray[1]);
                    } else {
                        this.usedNames.add(attribute.getNameAttribute());
                    }
                }

                this.replenishPattern(attribute.getPattern(), pattern);
                this.setValuesFromPatternToNameClass(attribute.getNameClass(), pattern);

            } else if (pattern instanceof Group) {

                // Case: Group

                Group group = (Group) pattern;

                for (Iterator<Pattern> it = group.getPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    this.replenishPattern(innerPattern, pattern);
                }

            } else if (pattern instanceof Interleave) {
                Interleave interleave = (Interleave) pattern;

                for (Iterator<Pattern> it = interleave.getPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    this.replenishPattern(innerPattern, pattern);
                }

            } else if (pattern instanceof Choice) {

                // Case: Choice

                Choice choice = (Choice) pattern;

                for (Iterator<Pattern> it = choice.getPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    this.replenishPattern(innerPattern, pattern);
                }

            } else if (pattern instanceof Optional) {

                // Case: Optional

                Optional optional = (Optional) pattern;

                for (Iterator<Pattern> it = optional.getPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    this.replenishPattern(innerPattern, pattern);
                }

            } else if (pattern instanceof ZeroOrMore) {

                // Case: ZeroOrMore

                ZeroOrMore zeroOrMore = (ZeroOrMore) pattern;

                for (Iterator<Pattern> it = zeroOrMore.getPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    this.replenishPattern(innerPattern, pattern);
                }

            } else if (pattern instanceof OneOrMore) {

                // Case: OneOrMore

                OneOrMore oneOrMore = (OneOrMore) pattern;

                for (Iterator<Pattern> it = oneOrMore.getPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    this.replenishPattern(innerPattern, pattern);
                }

            } else if (pattern instanceof List) {

                // Case: List

                List list = (List) pattern;

                for (Iterator<Pattern> it = list.getPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    this.replenishPattern(innerPattern, pattern);
                }

            } else if (pattern instanceof Mixed) {

                // Case: Mixed

                Mixed mixed = (Mixed) pattern;

                for (Iterator<Pattern> it = mixed.getPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    this.replenishPattern(innerPattern, pattern);
                }

            } else if (pattern instanceof Ref) {

                // Case: Ref

                Ref ref = (Ref) pattern;

                if (ref.getRefName() != null) {
                    this.usedNames.add(ref.getRefName());
                }

                for (Iterator<Define> it = ref.getDefineList().iterator(); it.hasNext();) {
                    Define innerDefine = it.next();
                    for (Iterator<Pattern> it2 = innerDefine.getPatterns().iterator(); it2.hasNext();) {
                        Pattern innerPattern = it2.next();
                        this.replenishPattern(innerPattern, pattern);
                    }
                }

            } else if (pattern instanceof ParentRef) {

                // Case: ParentRef

                ParentRef parentRef = (ParentRef) pattern;

                for (Iterator<Define> it = parentRef.getDefineList().iterator(); it.hasNext();) {
                    Define innerDefine = it.next();
                    for (Iterator<Pattern> it2 = innerDefine.getPatterns().iterator(); it2.hasNext();) {
                        Pattern innerPattern = it2.next();
                        this.replenishPattern(innerPattern, pattern);
                    }
                }

            } else if (pattern instanceof Empty) {

                // Case: Empty

                Empty empty = (Empty) pattern;
            } else if (pattern instanceof Text) {

                // Case: Text

                Text text = (Text) pattern;
            } else if (pattern instanceof Value) {

                // Case: Value

                Value value = (Value) pattern;
            } else if (pattern instanceof Data) {

                // Case: Data

                Data data = (Data) pattern;
            } else if (pattern instanceof NotAllowed) {

                // Case: NotAllowed

                NotAllowed notAllowed = (NotAllowed) pattern;
            } else if (pattern instanceof ExternalRef) {

                // Case: ExternalRef

                ExternalRef externalRef = (ExternalRef) pattern;

                if (externalRef.getRngSchema() != null) {
                    this.replenishPattern(externalRef.getRngSchema().getRootPattern(), pattern);
                }

            } else if (pattern instanceof Grammar) {

                // Case: Grammar

                Grammar grammar = (Grammar) pattern;

                for (Iterator<Pattern> it = grammar.getStartPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    this.replenishPattern(innerPattern, pattern);
                }

                for (String defineName: grammar.getDefinedPatternNames()) {
                    if (defineName != null) {
                        this.usedNames.add(defineName);
                    }
                    for (Define innerDefine: grammar.getDefinedPattern(defineName)) {
                        for (Pattern innerPattern: innerDefine.getPatterns()) {
                            this.replenishPattern(innerPattern, pattern);
                        }
                    }
                }

                if (!grammar.getIncludeContents().isEmpty()) {
                    for (Iterator<IncludeContent> it = grammar.getIncludeContents().iterator(); it.hasNext();) {
                        IncludeContent currentIncludeContent = it.next();

                        for (Iterator<Pattern> it2 = currentIncludeContent.getStartPatterns().iterator(); it2.hasNext();) {
                            Pattern innerPattern = it2.next();
                            this.replenishPattern(innerPattern, pattern);
                        }

                        for (Iterator<String> it3 = currentIncludeContent.getDefinedPatternNames().iterator(); it3.hasNext();) {
                            String defineName = it3.next();
                            if (defineName != null) {
                                this.usedNames.add(defineName);
                            }
                            for (Define innerDefine: grammar.getDefinedPattern(defineName)) {
                                for (Pattern innerPattern: innerDefine.getPatterns()) {
                                    this.replenishPattern(innerPattern, pattern);
                                }
                            }
                        }

                        if (currentIncludeContent.getRngSchema() != null) {
                            replenishPattern(currentIncludeContent.getRngSchema().getRootPattern(), pattern);
                        }
                    }
                }
            }
        }
    }

    /**
     * Recursive method for replenishing the defined xml attributes from one nameClass object (parent) to another nameClass object (child)
     *
     * @param nameClass           child nameClass
     * @param parentNameClass     parent nameClass
     */
    private void replenishNameClass(NameClass nameClass, NameClass parentNameClass) {
        if (!this.alreadySeenNameClasses.contains(nameClass)) {
            this.alreadySeenNameClasses.add(nameClass);
            if (parentNameClass != null && nameClass != null) {

                // Handle the attribute: "ns"
                if (nameClass.getAttributeNamespace() == null && parentNameClass.getAttributeNamespace() != null) {
                    nameClass.setAttributeNamespace(parentNameClass.getAttributeNamespace());
                    if (!this.identifiedNamespaces.containsValue(parentNameClass.getAttributeNamespace())) {
                        if (parentNameClass.getAttributeNamespace().equals(RelaxNGSchema.RELAXNG_NAMESPACE)) {
                            this.addIdentifiedNamespace("rng", RelaxNGSchema.RELAXNG_NAMESPACE);
                        } else {
                            this.addIdentifiedNamespace("ns_" + this.identifiedNamespaces.size(), parentNameClass.getAttributeNamespace().getUri());
                        }
                    }
                }
                // Handle all attributes forming the namespace list of a this nameClass
                if (parentNameClass.getNamespaceList() != null) {
                    if (nameClass.getNamespaceList() == null) {
                        nameClass.setNamespaceList(parentNameClass.getNamespaceList());
                    } else {
                        for (Iterator<IdentifiedNamespace> it = parentNameClass.getNamespaceList().getNamespaces().iterator(); it.hasNext();) {
                            IdentifiedNamespace currentIdentifiedNamespace = it.next();

                            if (nameClass.getNamespaceList().getNamespaceByIdentifier(currentIdentifiedNamespace.getIdentifier()).getUri() == null) {
                                nameClass.getNamespaceList().addNamespace(currentIdentifiedNamespace);
                                this.addIdentifiedNamespace(currentIdentifiedNamespace.getIdentifier(), currentIdentifiedNamespace.getUri());
                            }
                        }
                        if (nameClass.getDefaultNamespace() == null) {
                            nameClass.setDefaultNamespace(parentNameClass.getDefaultNamespace());
                        }
                    }
                }
            }

            if (nameClass instanceof Name) {

                // Case: Name

                Name name = (Name) nameClass;
                if (name.getContent() != null) {
                    String[] nameArray = null;

                    if (name.getContent().contains(":")) {
                        nameArray = name.getContent().split(":");
                    }

                    if (nameArray != null && nameArray.length == 2) {
                        this.usedNames.add(nameArray[1]);
                    } else {
                        this.usedNames.add(name.getContent());
                    }
                }
            } else if (nameClass instanceof AnyName) {

                // Case: AnyName

                AnyName anyName = (AnyName) nameClass;

                for (Iterator<NameClass> it = anyName.getExceptNames().iterator(); it.hasNext();) {
                    NameClass innerNameClass = it.next();
                    this.replenishNameClass(innerNameClass, nameClass);
                }

            } else if (nameClass instanceof NsName) {

                // Case: NsName

                NsName nsName = (NsName) nameClass;

                for (Iterator<NameClass> it = nsName.getExceptNames().iterator(); it.hasNext();) {
                    NameClass innerNameClass = it.next();
                    this.replenishNameClass(innerNameClass, nameClass);
                }

            } else if (nameClass instanceof NameClassChoice) {

                // Case: NameClassChoice

                NameClassChoice nameClassChoice = (NameClassChoice) nameClass;

                for (Iterator<NameClass> it = nameClassChoice.getChoiceNames().iterator(); it.hasNext();) {
                    NameClass innerNameClass = it.next();
                    this.replenishNameClass(innerNameClass, nameClass);
                }

            }
        }
    }
    /**
     * Set the attribute values from a pattern to a nameClass object
     * @param nameClass     target nameClass
     * @param pattern       source pattern
     */
    private void setValuesFromPatternToNameClass(NameClass nameClass, Pattern pattern) {
        if (nameClass != null) {

            // Handle the attribute: "ns"
            if (nameClass.getAttributeNamespace() == null && pattern.getAttributeNamespace() != null) {
                nameClass.setAttributeNamespace(pattern.getAttributeNamespace());
                if (!this.identifiedNamespaces.containsValue(pattern.getAttributeNamespace())) {
                    if (pattern.getAttributeNamespace().equals(RelaxNGSchema.RELAXNG_NAMESPACE)) {
                        this.addIdentifiedNamespace("rng", RelaxNGSchema.RELAXNG_NAMESPACE);
                    } else {
                        this.addIdentifiedNamespace("ns_" + this.identifiedNamespaces.size(), pattern.getAttributeNamespace().getUri());
                    }
                }
            }
            // Handle all attributes forming the namespace list of a this pattern
            if (pattern.getNamespaceList() != null) {
                if (nameClass.getNamespaceList() == null) {
                    nameClass.setNamespaceList(pattern.getNamespaceList());
                } else {
                    for (Iterator<IdentifiedNamespace> it = pattern.getNamespaceList().getNamespaces().iterator(); it.hasNext();) {
                        IdentifiedNamespace currentIdentifiedNamespace = it.next();

                        if (nameClass.getNamespaceList().getNamespaceByIdentifier(currentIdentifiedNamespace.getIdentifier()).getUri() == null) {
                            nameClass.getNamespaceList().addNamespace(currentIdentifiedNamespace);
                            this.addIdentifiedNamespace(currentIdentifiedNamespace.getIdentifier(), currentIdentifiedNamespace.getUri());
                        }
                    }
                    if (nameClass.getDefaultNamespace() == null) {
                        nameClass.setDefaultNamespace(pattern.getDefaultNamespace());
                    }
                }
            }

            this.replenishNameClass(nameClass, null);
        }
    }

    /**
     * Generate a new identified namespace for a given namespace string and an abbreviation
     * @param abbreviation      for identification of the namespace
     * @param uri               namespace uri
     */
    private void addIdentifiedNamespace(String abbreviation, String uri) {
        if (!this.identifiedNamespaces.containsValue(uri)) {
            if (this.identifiedNamespaces.containsKey(abbreviation)) {
                this.identifiedNamespaces.put(abbreviation + "_" + this.identifiedNamespaces.size(), uri);
            } else {
                this.identifiedNamespaces.put(abbreviation, uri);
            }
        }
    }

    /**
     * Getter for the map of identified namespaces
     * @return HashMap<String, String>      the first string is namespace abbreviation, the second is the namespace uri
     *
     */
    public HashMap<String, String> getIdentifiedNamespaces() {
        return identifiedNamespaces;
    }

    /**
     * Getter for the set of all used names within this RELAX NG schema
     * @return HashSet<String>
     */
    public HashSet<String> getUsedNames() {
        return usedNames;
    }

}
