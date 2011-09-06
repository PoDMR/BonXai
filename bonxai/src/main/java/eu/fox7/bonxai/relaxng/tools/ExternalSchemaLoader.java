package eu.fox7.bonxai.relaxng.tools;

import eu.fox7.bonxai.common.IdentifiedNamespace;
import eu.fox7.bonxai.relaxng.*;
import eu.fox7.bonxai.relaxng.parser.RNGParser;

import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * This class loads external RELAX NG schemas, parses them, and sets the
 * correct declarations to their references.
 *
 * There are two options for this class:
 *
 * a - only parse external schemas and set their generated schema objects to the
 *     externalRef or include objects or
 * b - parse external schemas and replace the include or externalRef with the
 *     correct content of the external RELAX NG schemata. This also handles the
 *     overwriting/replacing feature of start or define objects within an
 *     include-tag.
 *
 * Both variants handle the external schema parsing and rebuilding recursivly.
 * A file will only be read and parsed once.
 *
 *
 * After processing (case "a"),
 * - all externalRef objects hold a rngSchema object containing
 *   the parsed RELAX NG XSDSchema addressed by their href-Attribute and ...
 *
 * - all includeContent objects hold a rngSchema object containing the parsed
 *   RELAX NG XSDSchema addressed by their href-Attribute and the ...
 *
 *   start- and define-objects are merged into the lookupTable of the parentGrammar
 *   for a correct handling of the Relax NG Writer with respect of the redefinition
 *   (overwriting) of external patterns by the patterns defined within an
 *   include-tag in the parentGrammar.
 *
 * @author Lars Schmidt
 */
public class ExternalSchemaLoader {

    private RelaxNGSchema parentSchema;
    private HashSet<Pattern> alreadySeenPatterns;
    /**
     * Map of all already seen RELAX NG Schemas referenced by their absolute uri
     */
    private LinkedHashMap<String, RelaxNGSchema> alreadySeenSchemas;
    private final boolean replaceReferences;
    private boolean debug;

    /**
     * Constructor of class ExternalSchemaLoader
     * @param parentSchema          source RELAX NG schema as root
     * @param replaceReferences     optional boolean setting for replacing all externalReferences and includes with the corresponding content
     */
    public ExternalSchemaLoader(RelaxNGSchema parentSchema, boolean replaceReferences) {

        this.parentSchema = parentSchema;
        this.replaceReferences = replaceReferences;
        this.alreadySeenSchemas = new LinkedHashMap<String, RelaxNGSchema>();
        this.alreadySeenPatterns = new HashSet<Pattern>();
        this.debug = false;
        this.alreadySeenSchemas.put(getCanonicalPath(this.parentSchema.getAbsoluteUri()), parentSchema);
    }

    /**
     * Constructor of class ExternalSchemaLoader with debug option
     * @param parentSchema          source RELAX NG schema as root
     * @param replaceReferences     optional boolean setting for replacing all externalReferences and includes with the corresponding content
     * @param debug                 debug option for printing out some process information
     */
    public ExternalSchemaLoader(RelaxNGSchema parentSchema, boolean replaceReferences, boolean debug) {

        this.parentSchema = parentSchema;
        this.replaceReferences = replaceReferences;
        this.alreadySeenSchemas = new LinkedHashMap<String, RelaxNGSchema>();
        this.alreadySeenPatterns = new HashSet<Pattern>();
        this.debug = debug;
        this.alreadySeenSchemas.put(getCanonicalPath(this.parentSchema.getAbsoluteUri()), parentSchema);
    }

    /**
     * Handle external schemas
     * This is the public method for starting the handling of external references
     * @throws Exception
     */
    public void handleExternalSchemas() throws Exception {
        if (this.getDebug()) {
            System.out.println("\n---- ExternalSchemaLoader - start ---------------------------------------------\n");
        }
        Pattern rootPattern = parentSchema.getRootPattern();
        recursePattern(rootPattern, null, null);
        if (this.getDebug()) {
            System.out.println("---- ExternalSchemaLoader - end -----------------------------------------------\n");
        }
    }

    /**
     * Method for starting the recursive loading of external schemas
     * @param pattern
     * @param parentGrammar
     * @param directAncestorPattern
     * @throws Exception
     */
    private void recursePattern(Pattern pattern, Pattern parentGrammar, Pattern directAncestorPattern) throws Exception {
        this.recursePattern(pattern, parentGrammar, directAncestorPattern, this.parentSchema.getAbsoluteUri());
    }

    /**
     * Recursive method to follow the RELAX NG pattern structure and handle nested grammars or externalRefs
     * @param pattern
     * @param parentGrammar
     * @param directAncestorPattern
     * @param parentAbsolutePath
     * @throws Exception
     */
    private void recursePattern(Pattern pattern, Pattern parentGrammar, Pattern directAncestorPattern, String parentAbsolutePath) throws Exception {
        if (!this.alreadySeenPatterns.contains(pattern)) {
            this.alreadySeenPatterns.add(pattern);

            if (pattern instanceof Element) {

                // Case: Element

                Element element = (Element) pattern;

                for (Iterator<Pattern> it = element.getPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    this.recursePattern(innerPattern, parentGrammar, element);
                }

            } else if (pattern instanceof Attribute) {

                // Case: Attribute

                Attribute attribute = (Attribute) pattern;

                this.recursePattern(attribute.getPattern(), parentGrammar, attribute);

            } else if (pattern instanceof Group) {

                // Case: Group

                Group group = (Group) pattern;

                for (Iterator<Pattern> it = group.getPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    this.recursePattern(innerPattern, parentGrammar, group);
                }

            } else if (pattern instanceof Interleave) {

                // Case: Interleave

                Interleave interleave = (Interleave) pattern;

                for (Iterator<Pattern> it = interleave.getPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    this.recursePattern(innerPattern, parentGrammar, interleave);
                }

            } else if (pattern instanceof Choice) {

                // Case: Choice

                Choice choice = (Choice) pattern;

                for (Iterator<Pattern> it = choice.getPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    this.recursePattern(innerPattern, parentGrammar, choice);
                }

            } else if (pattern instanceof Optional) {

                // Case: Optional

                Optional optional = (Optional) pattern;

                for (Iterator<Pattern> it = optional.getPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    this.recursePattern(innerPattern, parentGrammar, optional);
                }

            } else if (pattern instanceof ZeroOrMore) {

                // Case: ZeroOrMore

                ZeroOrMore zeroOrMore = (ZeroOrMore) pattern;

                for (Iterator<Pattern> it = zeroOrMore.getPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    this.recursePattern(innerPattern, parentGrammar, zeroOrMore);
                }

            } else if (pattern instanceof OneOrMore) {

                // Case: OneOrMore

                OneOrMore oneOrMore = (OneOrMore) pattern;

                for (Iterator<Pattern> it = oneOrMore.getPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    this.recursePattern(innerPattern, parentGrammar, oneOrMore);
                }

            } else if (pattern instanceof List) {

                // Case: List

                List list = (List) pattern;

                for (Iterator<Pattern> it = list.getPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    this.recursePattern(innerPattern, parentGrammar, list);
                }

            } else if (pattern instanceof Data) {

                // Case: Data

                Data data = (Data) pattern;

                for (Iterator<Pattern> it = data.getExceptPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    this.recursePattern(innerPattern, parentGrammar, data);
                }

            } else if (pattern instanceof Mixed) {

                // Case: Mixed

                Mixed mixed = (Mixed) pattern;

                for (Iterator<Pattern> it = mixed.getPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    this.recursePattern(innerPattern, parentGrammar, mixed);
                }

            } else if (pattern instanceof Ref) {

                // Case: Ref

                Ref ref = (Ref) pattern;

                for (Iterator<Define> it = ref.getDefineList().iterator(); it.hasNext();) {
                    Define innerDefine = it.next();
                    for (Iterator<Pattern> it2 = innerDefine.getPatterns().iterator(); it2.hasNext();) {
                        Pattern innerPattern = it2.next();
                        this.recursePattern(innerPattern, parentGrammar, ref);
                    }
                }

            } else if (pattern instanceof ParentRef) {

                // Case: ParentRef

                ParentRef parentRef = (ParentRef) pattern;

                for (Iterator<Define> it = parentRef.getDefineList().iterator(); it.hasNext();) {
                    Define innerDefine = it.next();
                    for (Iterator<Pattern> it2 = innerDefine.getPatterns().iterator(); it2.hasNext();) {
                        Pattern innerPattern = it2.next();
                        this.recursePattern(innerPattern, parentGrammar, parentRef);
                    }
                }

            } else if (pattern instanceof ExternalRef) {

                // Case: ExternalRef

                ExternalRef externalRef = (ExternalRef) pattern;

                if (this.getDebug()) {
                    System.out.println("externalRef" + " " + parentGrammar + "\n");
                }

                if (externalRef.getRngSchema() == null) {
                    // Parse href RELAX NG XSDSchema

                    if (externalRef.getHref() != null && !externalRef.getHref().equals("")) {

                        URI uri = new URI(externalRef.getHref());
                        String absolutePath = "";
                        if (!uri.isAbsolute()) {
                            absolutePath = parentAbsolutePath.substring(0, (parentAbsolutePath.lastIndexOf("/") + 1)) + externalRef.getHref();
                        } else {
                            absolutePath = externalRef.getHref();
                        }

                        RelaxNGSchema referencedRelaxNGSchema = null;
                        // Check if the absolutePath was already seen.
                        // If this is true, avoid reparsing the same schema!
                        if (this.alreadySeenSchemas.containsKey(getCanonicalPath(absolutePath))) {
                            if (this.getDebug()) {
                                System.out.println("Already seen: " + externalRef.getHref() + "\n");
                            }
                            // alreadySeen
                            referencedRelaxNGSchema = this.alreadySeenSchemas.get(getCanonicalPath(absolutePath));
                        } else {
                            if (this.getDebug()) {
                                System.out.println("New XSDSchema: " + externalRef.getHref() + "\n");
                            }

                            // Parse the external schema
                            RNGParser rngParser = new RNGParser(getCanonicalPath(absolutePath), false, false);
                            referencedRelaxNGSchema = rngParser.getRNGSchema();

                            referencedRelaxNGSchema.setAbsoluteUri(getCanonicalPath(absolutePath));
                            this.alreadySeenSchemas.put(getCanonicalPath(absolutePath), referencedRelaxNGSchema);

                            // Recurse into the newly parsed schema
                            if (referencedRelaxNGSchema.getRootPattern() != null) {
                                this.recursePattern(referencedRelaxNGSchema.getRootPattern(), parentGrammar, externalRef, getCanonicalPath(absolutePath));
                            }
                        }

                        // Set the parsed schema as reference into the externalRef object
                        externalRef.setRngSchema(referencedRelaxNGSchema);

                        if (this.replaceReferences) {
                            if (this.getDebug()) {
                                System.out.println("Direct ancestor: " + directAncestorPattern + "\n");
                            }
                            this.replaceExternalRefWithContent(directAncestorPattern, externalRef);
                        }
                    }
                }

            } else if (pattern instanceof Grammar) {

                // Case: Grammar

                Grammar grammar = (Grammar) pattern;


                LinkedHashSet<Grammar> referencedGrammars = new LinkedHashSet<Grammar>();

                if (this.getDebug()) {
                    System.out.println("Grammar: " + parentGrammar + "\n");
                }

                for (Iterator<String> it = grammar.getDefinedPatternNames().iterator(); it.hasNext();) {
                    String defineName = it.next();
                    for (Iterator<Define> it2 = grammar.getDefinedPatternsFromLookUpTable(defineName).iterator(); it2.hasNext();) {
                        Define innerDefine = it2.next();
                        for (Iterator<Pattern> it3 = innerDefine.getPatterns().iterator(); it3.hasNext();) {
                            Pattern innerPattern = it3.next();
                            this.recursePattern(innerPattern, grammar, grammar);
                        }
                    }
                }

                for (Iterator<Pattern> it = grammar.getStartPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    this.recursePattern(innerPattern, grammar, grammar);
                }

                if (!grammar.getIncludeContents().isEmpty()) {
                    for (Iterator<IncludeContent> it = grammar.getIncludeContents().iterator(); it.hasNext();) {
                        IncludeContent currentIncludeContent = it.next();

                        for (Iterator<String> it3 = currentIncludeContent.getDefinedPatternNames().iterator(); it3.hasNext();) {
                            String defineName = it3.next();
                            for (Iterator<Define> it4 = currentIncludeContent.getDefinedPatternsFromLookUpTable(defineName).iterator(); it4.hasNext();) {
                                Define innerDefine = it4.next();
                                for (Iterator<Pattern> it5 = innerDefine.getPatterns().iterator(); it5.hasNext();) {
                                    Pattern innerPattern = it5.next();
                                    this.recursePattern(innerPattern, grammar, grammar);
                                }
                            }
                        }

                        for (Iterator<Pattern> it2 = currentIncludeContent.getStartPatterns().iterator(); it2.hasNext();) {
                            Pattern innerPattern = it2.next();
                            this.recursePattern(innerPattern, grammar, grammar);
                        }

                        if (currentIncludeContent.getRngSchema() == null) {
                            // Parse href RELAX NG XSDSchema

                            if (currentIncludeContent.getHref() != null && !currentIncludeContent.getHref().equals("")) {

                                URI uri = new URI(currentIncludeContent.getHref());
                                String absolutePath = "";
                                if (!uri.isAbsolute()) {
                                    absolutePath = parentAbsolutePath.substring(0, (parentAbsolutePath.lastIndexOf("/") + 1)) + currentIncludeContent.getHref();
                                } else {
                                    absolutePath = currentIncludeContent.getHref();
                                }

                                RelaxNGSchema referencedRelaxNGSchema = null;
                                
                                // Check if the absolutePath was already seen.
                                // If this is true, avoid reparsing the same schema!
                                if (this.alreadySeenSchemas.containsKey(getCanonicalPath(absolutePath))) {
                                    if (this.getDebug()) {
                                        System.out.println("Already seen: " + currentIncludeContent.getHref() + "\n");
                                    }
                                    // alreadySeen
                                    referencedRelaxNGSchema = this.alreadySeenSchemas.get(getCanonicalPath(absolutePath));
                                } else {
                                    if (this.getDebug()) {
                                        System.out.println("New XSDSchema: " + currentIncludeContent.getHref() + "\n");
                                    }

                                    // Parse the external schema
                                    RNGParser rngParser = new RNGParser(getCanonicalPath(absolutePath), false, false);
                                    referencedRelaxNGSchema = rngParser.getRNGSchema();

                                    referencedRelaxNGSchema.setAbsoluteUri(getCanonicalPath(absolutePath));
                                    this.alreadySeenSchemas.put(getCanonicalPath(absolutePath), referencedRelaxNGSchema);

                                    // Recurse into the newly parsed schema
                                    if (referencedRelaxNGSchema.getRootPattern() != null) {
                                        this.recursePattern(referencedRelaxNGSchema.getRootPattern(), grammar, grammar, getCanonicalPath(absolutePath));
                                    }
                                }
                                // Set the parsed schema as reference into the include object within its grammar
                                currentIncludeContent.setRngSchema(referencedRelaxNGSchema);
                            }
                        }
                        if (currentIncludeContent != null) {
                            {
                                // Is the optional setting to replace external schema references with its content set, do so.
                                if (this.replaceReferences) {
                                    if (currentIncludeContent.getRngSchema().getRootPattern() instanceof Grammar) {
                                        Grammar referencedGrammar = (Grammar) currentIncludeContent.getRngSchema().getRootPattern();

                                        for (Iterator<IdentifiedNamespace> it1 = referencedGrammar.getNamespaceList().getIdentifiedNamespaces().iterator(); it1.hasNext();) {
                                            IdentifiedNamespace identifiedNamespace = it1.next();
                                            if (grammar.getNamespaceList().getNamespaceByUri(identifiedNamespace.getUri()) != null && grammar.getNamespaceList().getNamespaceByUri(identifiedNamespace.getUri()).getIdentifier() != null) {
                                                // nothing
                                            } else {
                                                grammar.getNamespaceList().addIdentifiedNamespace(identifiedNamespace);
                                            }
                                        }

                                        if (!currentIncludeContent.getStartPatterns().isEmpty()) {
                                            for (Iterator<Pattern> it1 = currentIncludeContent.getStartPatterns().iterator(); it1.hasNext();) {
                                                Pattern startPattern = it1.next();
                                                grammar.addStartPattern(startPattern);
                                            }
                                        } else {
                                            // Add startPatterns to current grammar
                                            // Pay attention to the combineMethods.
                                            if (referencedGrammar.getStartCombineMethod() == null || grammar.getStartCombineMethod() == null || referencedGrammar.getStartCombineMethod().equals(grammar.getStartCombineMethod())) {
                                                for (Iterator<Pattern> it1 = referencedGrammar.getStartPatterns().iterator(); it1.hasNext();) {
                                                    Pattern startPattern = it1.next();
                                                    grammar.addStartPattern(startPattern);
                                                }
                                            }
                                        }

                                        // Replace the define-elements of the included schema with the given define-elements within the include-tag.
                                        if (!currentIncludeContent.getDefinedPatternNames().isEmpty()) {
                                            for (Iterator<String> it1 = currentIncludeContent.getDefinedPatternNames().iterator(); it1.hasNext();) {
                                                String definePatternName = it1.next();

                                                LinkedList<Define> tempDefineList = currentIncludeContent.getDefinedPatternsFromLookUpTable(definePatternName);

                                                referencedGrammar.getDefineLookUpTable().updateOrCreateReference(definePatternName, tempDefineList);
                                                referencedGrammar.getDefinedPatternNames().add(definePatternName);

                                            }
                                        }

                                        // Add inner defines to current grammar with respect to the redefined patterns within this include-tag
                                        // TODO: Pay attention to the combineMethods.
                                        if (!referencedGrammar.getDefinedPatternNames().isEmpty()) {
                                            for (Iterator<String> it1 = referencedGrammar.getDefinedPatternNames().iterator(); it1.hasNext();) {
                                                String definePatternName = it1.next();

                                                LinkedList<Define> tempDefineList = referencedGrammar.getDefinedPatternsFromLookUpTable(definePatternName);

                                                for (Iterator<Define> it2 = tempDefineList.iterator(); it2.hasNext();) {
                                                    Define define = it2.next();

                                                    if (this.getDebug()) {
                                                        System.out.println(currentIncludeContent.getRngSchema().getAbsoluteUri() + "\n" + "put into parent grammar:\n" + define.getName() + "\n" + define.getPatterns() + "\n");
                                                    }

                                                    grammar.addDefinePattern(define);

                                                }
                                            }
                                        }

                                        referencedGrammars.add(referencedGrammar);
                                    }
                                }
                            }
                        }
                    }

                    for (Iterator<Grammar> it = referencedGrammars.iterator(); it.hasNext();) {
                        Grammar currentReferencedGrammar = it.next();

                        // Add inner defines to current grammar with respect to the redefined patterns within this include-tag
                        if (!grammar.getDefinedPatternNames().isEmpty()) {
                            for (Iterator<String> it1 = grammar.getDefinedPatternNames().iterator(); it1.hasNext();) {
                                String definePatternName = it1.next();

                                LinkedList<Define> tempDefineList = grammar.getDefinedPatternsFromLookUpTable(definePatternName);

                                if (!tempDefineList.isEmpty() && (currentReferencedGrammar.getDefinedPatternsFromLookUpTable(definePatternName) == null || currentReferencedGrammar.getDefinedPatternsFromLookUpTable(definePatternName).isEmpty())) {

                                    if (this.getDebug()) {
                                        System.out.println("put into Referenced grammar:\n" + definePatternName + "\n");
                                    }

                                    currentReferencedGrammar.getDefineLookUpTable().updateOrCreateReference(definePatternName, tempDefineList);
//                                    currentReferencedGrammar.getDefinedPatternNames().add(definePatternName);
                                }
                            }
                        }
                    }

                    if (this.replaceReferences) {
                        grammar.setIncludeContents(new LinkedList<IncludeContent>());
                    }
                }
            }
        }
    }

    /**
     * Replace an externalRef with its content, to remove all externalRefs in the resulting schema
     * @param directAncestorPattern
     * @param externalRef
     * @throws Exception
     */
    private void replaceExternalRefWithContent(Pattern directAncestorPattern, ExternalRef externalRef) throws Exception {

        if (externalRef.getRngSchema() != null && externalRef.getRngSchema().getRootPattern() != null) {
            Pattern contentOfExternalFile = externalRef.getRngSchema().getRootPattern();

            while (contentOfExternalFile instanceof ExternalRef) {
                contentOfExternalFile = ((ExternalRef) contentOfExternalFile).getRngSchema().getRootPattern();
            }

            if (directAncestorPattern instanceof Element) {

                // Case: Element

                Element element = (Element) directAncestorPattern;

                LinkedList<Pattern> newPatternList = new LinkedList<Pattern>();

                for (Iterator<Pattern> it = element.getPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    if (innerPattern.equals(externalRef)) {
                        newPatternList.add(contentOfExternalFile);
                    } else {
                        newPatternList.add(innerPattern);
                    }
                }

                element.setPatterns(newPatternList);

            } else if (directAncestorPattern instanceof Attribute) {

                // Case: Attribute

                Attribute attribute = (Attribute) directAncestorPattern;
                attribute.setPattern(contentOfExternalFile);

            } else if (directAncestorPattern instanceof Group) {

                // Case: Group

                Group group = (Group) directAncestorPattern;

                LinkedList<Pattern> newPatternList = new LinkedList<Pattern>();

                for (Iterator<Pattern> it = group.getPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    if (innerPattern.equals(externalRef)) {
                        newPatternList.add(contentOfExternalFile);
                    } else {
                        newPatternList.add(innerPattern);
                    }
                }

                group.setPatterns(newPatternList);

            } else if (directAncestorPattern instanceof Interleave) {

                // Case: Interleave

                Interleave interleave = (Interleave) directAncestorPattern;

                LinkedList<Pattern> newPatternList = new LinkedList<Pattern>();

                for (Iterator<Pattern> it = interleave.getPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    if (innerPattern.equals(externalRef)) {
                        newPatternList.add(contentOfExternalFile);
                    } else {
                        newPatternList.add(innerPattern);
                    }
                }

                interleave.setPatterns(newPatternList);

            } else if (directAncestorPattern instanceof Choice) {

                // Case: Choice

                Choice choice = (Choice) directAncestorPattern;

                LinkedList<Pattern> newPatternList = new LinkedList<Pattern>();

                for (Iterator<Pattern> it = choice.getPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    if (innerPattern.equals(externalRef)) {
                        newPatternList.add(contentOfExternalFile);
                    } else {
                        newPatternList.add(innerPattern);
                    }
                }

                choice.setPatterns(newPatternList);

            } else if (directAncestorPattern instanceof Optional) {

                // Case: Optional

                Optional optional = (Optional) directAncestorPattern;

                LinkedList<Pattern> newPatternList = new LinkedList<Pattern>();

                for (Iterator<Pattern> it = optional.getPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    if (innerPattern.equals(externalRef)) {
                        newPatternList.add(contentOfExternalFile);
                    } else {
                        newPatternList.add(innerPattern);
                    }
                }

                optional.setPatterns(newPatternList);

            } else if (directAncestorPattern instanceof ZeroOrMore) {

                // Case: ZeroOrMore

                ZeroOrMore zeroOrMore = (ZeroOrMore) directAncestorPattern;

                LinkedList<Pattern> newPatternList = new LinkedList<Pattern>();

                for (Iterator<Pattern> it = zeroOrMore.getPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    if (innerPattern.equals(externalRef)) {
                        newPatternList.add(contentOfExternalFile);
                    } else {
                        newPatternList.add(innerPattern);
                    }
                }

                zeroOrMore.setPatterns(newPatternList);

            } else if (directAncestorPattern instanceof OneOrMore) {

                // Case: OneOrMore

                OneOrMore oneOrMore = (OneOrMore) directAncestorPattern;

                LinkedList<Pattern> newPatternList = new LinkedList<Pattern>();

                for (Iterator<Pattern> it = oneOrMore.getPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    if (innerPattern.equals(externalRef)) {
                        newPatternList.add(contentOfExternalFile);
                    } else {
                        newPatternList.add(innerPattern);
                    }
                }

                oneOrMore.setPatterns(newPatternList);

            } else if (directAncestorPattern instanceof List) {

                // Case: List

                List list = (List) directAncestorPattern;

                LinkedList<Pattern> newPatternList = new LinkedList<Pattern>();

                for (Iterator<Pattern> it = list.getPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    if (innerPattern.equals(externalRef)) {
                        newPatternList.add(contentOfExternalFile);
                    } else {
                        newPatternList.add(innerPattern);
                    }
                }

                list.setPatterns(newPatternList);

            } else if (directAncestorPattern instanceof Data) {

                // Case: Data

                Data data = (Data) directAncestorPattern;

                LinkedList<Pattern> newPatternList = new LinkedList<Pattern>();

                for (Iterator<Pattern> it = data.getExceptPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    if (innerPattern.equals(externalRef)) {
                        newPatternList.add(contentOfExternalFile);
                    } else {
                        newPatternList.add(innerPattern);
                    }
                }

                data.setExceptPatterns(newPatternList);

            } else if (directAncestorPattern instanceof Mixed) {

                // Case: Mixed

                Mixed mixed = (Mixed) directAncestorPattern;

                LinkedList<Pattern> newPatternList = new LinkedList<Pattern>();

                for (Iterator<Pattern> it = mixed.getPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    if (innerPattern.equals(externalRef)) {
                        newPatternList.add(contentOfExternalFile);
                    } else {
                        newPatternList.add(innerPattern);
                    }
                }

                mixed.setPatterns(newPatternList);

            } else if (directAncestorPattern instanceof Grammar) {

                // Case: Grammar

                Grammar grammar = (Grammar) directAncestorPattern;
                LinkedList<Pattern> newStartPatternList = new LinkedList<Pattern>();

                for (Iterator<Pattern> it = grammar.getStartPatterns().iterator(); it.hasNext();) {
                    Pattern innerPattern = it.next();
                    if (innerPattern.equals(externalRef)) {
                        newStartPatternList.add(contentOfExternalFile);
                    } else {
                        newStartPatternList.add(innerPattern);
                    }
                }

                grammar.setStartPatterns(newStartPatternList);

                if (!grammar.getDefinedPatternNames().isEmpty()) {
                    for (Iterator<String> it1 = grammar.getDefinedPatternNames().iterator(); it1.hasNext();) {
                        String definePatternName = it1.next();

                        LinkedList<Define> tempDefineList = grammar.getDefinedPatternsFromLookUpTable(definePatternName);

                        for (Iterator<Define> it2 = tempDefineList.iterator(); it2.hasNext();) {
                            Define define = it2.next();

                            LinkedList<Pattern> newPatternList = new LinkedList<Pattern>();

                            for (Iterator<Pattern> it = define.getPatterns().iterator(); it.hasNext();) {
                                Pattern innerPattern = it.next();
                                if (innerPattern.equals(externalRef)) {
                                    newPatternList.add(contentOfExternalFile);
                                } else {
                                    newPatternList.add(innerPattern);
                                }
                            }

                            define.setPatterns(newPatternList);
                        }
                    }
                }

                if (!grammar.getIncludeContents().isEmpty()) {
                    for (Iterator<IncludeContent> itInclude = grammar.getIncludeContents().iterator(); itInclude.hasNext();) {
                        IncludeContent currentIncludeContent = itInclude.next();

                        LinkedList<Pattern> newIncludeStartPatternList = new LinkedList<Pattern>();

                        for (Iterator<Pattern> it = grammar.getStartPatterns().iterator(); it.hasNext();) {
                            Pattern innerPattern = it.next();
                            if (innerPattern.equals(externalRef)) {
                                newIncludeStartPatternList.add(contentOfExternalFile);
                            } else {
                                newIncludeStartPatternList.add(innerPattern);
                            }
                        }

                        currentIncludeContent.setStartPatterns(newIncludeStartPatternList);

                        if (!currentIncludeContent.getDefinedPatternNames().isEmpty()) {
                            for (Iterator<String> it1 = currentIncludeContent.getDefinedPatternNames().iterator(); it1.hasNext();) {
                                String definePatternName = it1.next();

                                LinkedList<Define> tempDefineList = currentIncludeContent.getDefinedPatternsFromLookUpTable(definePatternName);

                                for (Iterator<Define> it2 = tempDefineList.iterator(); it2.hasNext();) {
                                    Define define = it2.next();

                                    LinkedList<Pattern> newPatternList = new LinkedList<Pattern>();

                                    for (Iterator<Pattern> it = define.getPatterns().iterator(); it.hasNext();) {
                                        Pattern innerPattern = it.next();
                                        if (innerPattern.equals(externalRef)) {
                                            newPatternList.add(contentOfExternalFile);
                                        } else {
                                            newPatternList.add(innerPattern);
                                        }
                                    }

                                    define.setPatterns(newPatternList);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Remove all occurrences of "../" by removing the last folder, which is a
     * prefix of string-occurrence, and the occurrence itself, from the given path.
     * The result should be a canonic uri expression.
     * Occurrences of the string "./" are replaced, too.
     *
     * @param path      path, that will be cleaned
     * @return String   resulting string after cleaning of "../" and "./" appearances
     */
    private String getCanonicalPath(String path) {

        while (path.contains("/./")) {
            path = path.replaceFirst("[/][.][/]", "/");
        }

        while (path.contains("../")) {
            int pos = path.indexOf("../");
            String pathBefore = path.substring(0, pos - 1);
            String pathAfter = path.substring(pos + 2, path.length());
            int posLastSlash = pathBefore.lastIndexOf("/");
            pathBefore = pathBefore.substring(0, posLastSlash);
            path = pathBefore + pathAfter;
        }

        return path;
    }

    /**
     * Getter for the boolean debug value
     * @return boolean      debug
     */
    public boolean getDebug() {
        return debug;
    }

    /**
     * Setter for the boolean debug value
     * @param debug     boolean debug value
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
