package de.tudortmund.cs.bonxai.converter.relaxng2xsd;

import de.tudortmund.cs.bonxai.common.AnyAttribute;
import de.tudortmund.cs.bonxai.common.AnyPattern;
import de.tudortmund.cs.bonxai.common.IdentifiedNamespace;
import de.tudortmund.cs.bonxai.common.NamespaceList;
import de.tudortmund.cs.bonxai.common.ProcessContentsInstruction;
import de.tudortmund.cs.bonxai.converter.relaxng2xsd.exceptions.PatternNotAllowedException;
import de.tudortmund.cs.bonxai.relaxng.*;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * Class NameClassAnalyzer
 * @author Lars Schmidt
 */
public class NameClassAnalyzer {

    // The nameclass as basis of the generation
    private final NameClass nameClass;
    // Store the generated name infos
    private LinkedHashMap<String, Object> nameInfos;
    // Name set for the resulting names
    LinkedHashSet<String> nameSet = new LinkedHashSet<String>();
    // The pattern as the bases of the generation
    private Pattern pattern = null;

    /**
     * Constructor of class NameClassAnalyzer
     * @param pattern
     * @throws PatternNotAllowedException
     */
    public NameClassAnalyzer(Pattern pattern) throws PatternNotAllowedException {
        this.nameInfos = new LinkedHashMap<String, Object>();
        this.pattern = pattern;

        String nameAttribute = null;
        NamespaceList namespaceList = null;
        String namespace = null;
        NameClass localNameClass = null;

        if (pattern instanceof Attribute) {
            Attribute attribute = (Attribute) pattern;
            nameAttribute = attribute.getNameAttribute();
            namespace = attribute.getAttributeNamespace();
            namespaceList = attribute.getNamespaceList();
            localNameClass = attribute.getNameClass();
        } else if (pattern instanceof Element) {
            Element element = (Element) pattern;
            nameAttribute = element.getNameAttribute();
            namespace = element.getAttributeNamespace();
            namespaceList = element.getNamespaceList();
            localNameClass = element.getNameClass();
        } else {
            // Exception: The current pattern is not allowed in this context.
            throw new PatternNotAllowedException(pattern.getClass().getName(), "analyze attribute or element nameClasses");
        }

        if (nameAttribute != null) {
            this.nameClass = null;

            if (namespace == null) {
                namespace = "";
            }

            String[] nameArray = null;
            if (nameAttribute.contains(":")) {
                nameArray = nameAttribute.split(":");
            }

            if (nameArray != null && nameArray.length == 2) {
                // Namespace handling
                IdentifiedNamespace idNamespace = namespaceList.getNamespaceByIdentifier(nameArray[0]);
                if (idNamespace.getUri() != null) {
                    namespace = idNamespace.getUri();
                }
                this.nameInfos.put("{" + namespace + "}" + nameArray[1], null);
            } else {
                this.nameInfos.put("{" + namespace + "}" + nameAttribute, null);
            }
        } else {
            this.nameClass = localNameClass;
        }
    }

    /**
     * Method: analyze
     *
     * This is the main method of this class for starting the analysis
     *
     * @return LinkedHashMap<String, Object>
     */
    public LinkedHashMap<String, Object> analyze() {
        if (this.nameClass != null) {

            LinkedHashSet<NameClass> nameClasses = new LinkedHashSet<NameClass>();
            nameClasses.add(this.nameClass);
            this.recurseNameClasses(nameClasses, true, null);
            addNamesToNameInfoMap();

        }
        return this.nameInfos;
    }

    /**
     * Getter for the generated name information
     * @return LinkedHashMap<String, Object>    nameInfos
     */
    public LinkedHashMap<String, Object> getNameInfos() {
        return this.nameInfos;
    }

    /**
     * Recursive method for generating the information about the given nameclasses
     * @param nameClasses
     * @param positive
     * @param parentNamespace
     */
    private void recurseNameClasses(LinkedHashSet<NameClass> nameClasses, boolean positive, String parentNamespace) {

        for (Iterator<NameClass> it = nameClasses.iterator(); it.hasNext();) {
            NameClass currentNameClass = it.next();

            if (currentNameClass instanceof Name) {

                //Case: Name

                if (positive) {
                    Name name = (Name) currentNameClass;
                    // NamespaceHandling for name

                    String namespace = name.getAttributeNamespace();
                    if (namespace == null) {
                        namespace = "";
                    }

                    String[] nameArray = null;
                    if (name.getContent().contains(":")) {
                        nameArray = name.getContent().split(":");
                    }

                    if (nameArray != null && nameArray.length == 2) {
                        // Namespace handling
                        IdentifiedNamespace idNamespace = name.getNamespaceList().getNamespaceByIdentifier(nameArray[0]);
                        if (idNamespace.getUri() != null) {
                            namespace = idNamespace.getUri();
                        }
                        this.nameSet.add("{" + namespace + "}" + nameArray[1]);
                    } else {
                        this.nameSet.add("{" + namespace + "}" + name.getContent());
                    }
                }
            } else if (currentNameClass instanceof NsName) {

                //Case: NsName

                if (positive) {
                    NsName nsName = (NsName) currentNameClass;

                    String namespace = nsName.getAttributeNamespace();
                    if (namespace == null || namespace.equals("")) {
                        namespace = "##local";
                    }

                    this.nameSet.add("{" + namespace + "}");

                    recurseNameClasses(nsName.getExceptNames(), !positive, namespace);
                    
                } else {
                    NsName nsName = (NsName) currentNameClass;

                    String namespace = nsName.getAttributeNamespace();
                    if (namespace == null || namespace.equals("")) {
                        namespace = "##local";
                    }

                    if (namespace.equals("##local")) {

                        LinkedHashSet<String> tempLocal = new LinkedHashSet<String>();
                        tempLocal.add("{##local}");
                        this.nameSet.removeAll(tempLocal);

                        if (!this.nameSet.contains("{##other}")) {
                            this.nameSet.add("{" + "##other" + "}");
                        }

                    } else {

                        LinkedHashSet<String> temp = new LinkedHashSet<String>();
                        temp.add("{" + namespace + "}");
                        this.nameSet.removeAll(temp);

                        if (pattern.getAttributeNamespace() != null && pattern.getAttributeNamespace().equals(namespace)) {
                            if (!this.nameSet.contains("{##other}")) {
                                this.nameSet.add("{" + "##other" + "}");
                            }
                        } else if (!namespace.equals("##local")) {
                            this.nameSet.add("{-" + namespace + "}");
                        }

                    }
                    recurseNameClasses(nsName.getExceptNames(), !positive, namespace);
                }
            } else if (currentNameClass instanceof AnyName) {

                //Case: AnyName

                if (positive) {
                    AnyName anyName = (AnyName) currentNameClass;

                    if (anyName.getExceptNames().isEmpty()) {
                        this.nameSet.add("{##any}");
                    } else {
                        recurseNameClasses(anyName.getExceptNames(), !positive, "{##any}");
                    }
                }
            } else if (currentNameClass instanceof NameClassChoice) {
                NameClassChoice nameClassChoice = (NameClassChoice) currentNameClass;
                recurseNameClasses(nameClassChoice.getChoiceNames(), positive, parentNamespace);
            }
        }
    }

    /**
     * Method for adding generated names to the name information map
     */
    private void addNamesToNameInfoMap() {

        LinkedHashSet<String> namespacesForAny = new LinkedHashSet<String>();
        LinkedHashSet<String> namespacesForExternalAny = new LinkedHashSet<String>();

        // Loop over the global set of names
        for (Iterator<String> it = nameSet.iterator(); it.hasNext();) {
            String string = it.next();

            if (string.startsWith("{") && string.endsWith("}") && !string.startsWith("{-")) {
                // Namespace
                namespacesForAny.add(string);
            } else if (string.startsWith("{-") && string.endsWith("}")) {
                namespacesForExternalAny.add("{" + string.substring(2, string.length() - 1) + "}");
            } else {
                this.nameInfos.put(string, null);
            }
        }

        // If there is an any name (wildcard)
        if (!namespacesForAny.isEmpty()) {
            boolean anyNamespace = false;
            if (this.pattern.getNamespaceList().getIdentifiedNamespaces().size() == namespacesForAny.size()) {
                anyNamespace = true;

                for (Iterator<IdentifiedNamespace> it = this.pattern.getNamespaceList().getIdentifiedNamespaces().iterator(); it.hasNext();) {
                    IdentifiedNamespace currentIdentifiedNamespace = it.next();
                    if (!namespacesForAny.contains("{" + currentIdentifiedNamespace.getUri() + "}")) {
                        anyNamespace = false;
                    }
                }
            }

            if (!anyNamespace && namespacesForAny.contains("{##any}")) {
                anyNamespace = true;
            }

            String namespaceList = "";
            if (anyNamespace) {
                namespaceList = "##any";
            } else {
                for (Iterator<String> it = namespacesForAny.iterator(); it.hasNext();) {
                    String string = it.next();
                    string = string.substring(1, string.length() - 1);
                    namespaceList = namespaceList + " " + string;
                }
            }
            String anyNameString = null;
            Object resultObject = null;

            ProcessContentsInstruction pci = ProcessContentsInstruction.Skip;
//            if (nameSet.size() > 1) {
//                pci = ProcessContentsInstruction.Skip;
//            }

            if (this.pattern instanceof Attribute) {
                anyNameString = "anyAttribute";
                resultObject = new AnyAttribute(pci, namespaceList.trim());
            } else if (this.pattern instanceof Element) {
                anyNameString = "any";
                resultObject = new AnyPattern(pci, namespaceList.trim());
            }
            if (this.nameInfos.size() > 0) {
                anyNameString = anyNameString + "_" + this.nameInfos.size();
            }
            this.nameInfos.put(anyNameString, resultObject);
        }

        // If there is an external any name (wildcard for external namespace)
        if (!namespacesForExternalAny.isEmpty()) {
            for (Iterator<String> it = namespacesForExternalAny.iterator(); it.hasNext();) {

                String string = it.next();
                String namespace = string.substring(1, string.length() - 1);

                String anyNameString = null;
                Object resultObject = null;

                ProcessContentsInstruction pci = ProcessContentsInstruction.Skip;
//                if (nameSet.size() > 1) {
//                    pci = ProcessContentsInstruction.Skip;
//                }

                if (this.pattern instanceof Attribute) {
                    anyNameString = "-anyAttribute_except";
                    resultObject = new AnyAttribute(pci, namespace.trim());
                } else if (this.pattern instanceof Element) {
                    anyNameString = "-any_except";
                    resultObject = new AnyPattern(pci, namespace.trim());
                }
                if (this.nameInfos.size() > 0) {
                    anyNameString = anyNameString + "_" + this.nameInfos.size();
                }
                this.nameInfos.put(anyNameString, resultObject);
            }
        }
    }
}
