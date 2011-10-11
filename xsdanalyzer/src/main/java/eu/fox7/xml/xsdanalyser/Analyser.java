/*
 * Created on Mar 23, 2006
 * Modified on $Date: 2010-01-29 14:40:28 $
 */
package eu.fox7.xml.xsdanalyser;

import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.NodeSerializer;
import eu.fox7.util.tree.SExpressionSerializer;
import eu.fox7.util.tree.Serializer;
import eu.fox7.util.tree.Tree;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTerm;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDWildcard;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;
import org.eclipse.xsd.util.XSDResourceImpl;

/**
 * @author gjb
 * @version $Revision: 1.20 $
 * 
 */
public class Analyser {


    private static final String ERROR_PREFIX = "### error: ";

    public static void main(String[] args) throws Exception {
        Options options = initOptions();
        CommandLine cl = initCommandLine(args, options);
        if (cl.hasOption("h")) {
            printHelp(options);
            System.exit(ExitCode.SUCCESS.getCode());
        }
        if (cl.hasOption(InputType.FILE.flag())) {
            String schemaURL = cl.getOptionValue(InputType.FILE.flag());
            XSDSchema xsd = loadSchema(schemaURL);
            if (xsd == null) {
                System.err.println(ERROR_PREFIX +
                                   ExitCode.XSD_LOAD_FAILURE.getReason());
                System.exit(ExitCode.XSD_LOAD_FAILURE.getCode());
            }
            ElementNameAnalysis elementNameAnalysis = new ElementNameAnalysis(xsd);
            if (cl.hasOption("E")) {
                showElementNames(elementNameAnalysis);
            }
            TypeNameAnalysis typeNameAnalysis = new TypeNameAnalysis(xsd);
            if (cl.hasOption("N")) {
                showTypeNames(typeNameAnalysis);
            }
            TypeAnalysis typeAnalysis = new TypeAnalysis(xsd);
            if (cl.hasOption("t") || cl.hasOption("s")) {
                if (cl.hasOption("t"))
                    showTypes(typeAnalysis);
                if (cl.hasOption("s")) {
                    if (cl.hasOption("v"))
                        showSingleTypes(typeAnalysis);
                    if (typeAnalysis.foundTrueSingleTypeDefs()) {
                        if (cl.hasOption("e"))
                            System.err.println(ExitCode.FOUND.getCode());
                        System.exit(ExitCode.FOUND.getCode());
                    } else {
                        if (cl.hasOption("e"))
                            System.err.println(ExitCode.SUCCESS.getCode());
                        System.exit(ExitCode.SUCCESS.getCode());
                    }
                }
            }
            SymbolOccurrenceAnalysis symbolOccurrenceAnalysis = new SymbolOccurrenceAnalysis(xsd, typeNameAnalysis);
            if (cl.hasOption("o") || cl.hasOption("n")) {
                if (cl.hasOption("o"))
                    showOccurrences(symbolOccurrenceAnalysis);
                if (cl.hasOption("n")) {
                    if (cl.hasOption("v"))
                        showNonSingleOccurrences(symbolOccurrenceAnalysis, typeAnalysis);
                    if (symbolOccurrenceAnalysis.foundNonSingleOccurrenceTypeDef()) {
                        if (cl.hasOption("e"))
                            System.err.println(ExitCode.FOUND.getCode());
                        System.exit(ExitCode.FOUND.getCode());
                    } else {
                        if (cl.hasOption("e"))
                            System.err.println(ExitCode.SUCCESS.getCode());
                        System.exit(ExitCode.SUCCESS.getCode());
                    }
                }
            }
            RegexAnalysis regexAnalysis = new RegexAnalysis(xsd);
            if (cl.hasOption("r")) {
                showRegexes(regexAnalysis, typeNameAnalysis, cl.hasOption("q"));
            }
            SingleTypeCandidateAnalysis singleTypeCandidateAnalysis = new SingleTypeCandidateAnalysis(regexAnalysis);
            if (cl.hasOption("c")) {
                if (cl.hasOption("v")) {
                    showSingleTypeCandidates(singleTypeCandidateAnalysis);
                    System.out.println(singleTypeCandidateAnalysis.numberOfCandidates() +
                                       " total candidates");
                }
                if (singleTypeCandidateAnalysis.hasSingleTypeCandidates()) {
                    if (cl.hasOption("e"))
                        System.err.println(ExitCode.FOUND.getCode());
                    System.exit(ExitCode.FOUND.getCode());
                } else
                    if (cl.hasOption("e"))
                        System.err.println(ExitCode.SUCCESS.getCode());
                    System.exit(ExitCode.SUCCESS.getCode());
            }
        } else if (cl.hasOption(InputType.DIR.flag())) {
            File dir = new File(cl.getOptionValue(InputType.DIR.flag()));
            if (!dir.isDirectory()) {
                System.err.println(ERROR_PREFIX +
                                   ExitCode.NO_DIR_FAILURE.getReason() + ": '" +
                                   cl.getOptionValue("f") + "'");
                System.exit(ExitCode.NO_DIR_FAILURE.getCode());
            }
            for (String fileName : dir.list(new XSDFileFilter())) {
                File file = new File(dir, fileName);
                XSDSchema xsd = loadSchema(file.getAbsolutePath());
                System.out.print(fileName + ": ");
                if (xsd == null) {
                    System.out.println("error");
                    continue;
                }
                TypeAnalysis analysis = new TypeAnalysis(xsd);
                if (analysis.foundTrueSingleTypeDefs())
                    System.out.println(ExitCode.FOUND.getCode());
                else
                    System.out.println(ExitCode.SUCCESS.getCode());
            }
        }
    }

    protected static void showSingleTypeCandidates(SingleTypeCandidateAnalysis singleTypeCandidateAnalysis) {
        for (Iterator<String> childNameIt = singleTypeCandidateAnalysis.getCandidateIterator(); childNameIt.hasNext(); ) {
            String childName = childNameIt.next();
            Set<String> parentSet = singleTypeCandidateAnalysis.getParents(childName);
            System.out.println(childName + "[" + parentSet.size() + "]:");
            System.out.println("  " + StringUtils.join(parentSet.iterator(), ", "));
            System.out.println();
        }
    }

    protected static void showRegexes(RegexAnalysis regexAnalysis,
                                      TypeNameAnalysis typeNameAnalysis,
                                      boolean noTypes) {
        NodeSerializer<String> nodeSerializer = noTypes ? new NoTypedRegexSerializer() : new TypedRegexSerializer(typeNameAnalysis);
        Serializer<String> serializer = new SExpressionSerializer();
        for (Iterator<XSDTypeDefinition> typeDefIt = regexAnalysis.getTypeDefIterator();
             typeDefIt.hasNext(); ) {
            XSDTypeDefinition typeDef = typeDefIt.next();
            Tree regex = regexAnalysis.getRegex(typeDef);
            if (regex != null) {
                String name = typeNameAnalysis.getTypeName(typeDef);
                if (!noTypes) {
                    System.out.println(name + ":");
                    System.out.println("  " + serializer.serialize(regex, nodeSerializer));
                    System.out.println();
                    System.out.println();
                } else {
                    String regexStr = serializer.serialize(regex, nodeSerializer);
                    System.out.println(regexStr);
                }
            }
        }
    }

    protected static void showTypeNames(TypeNameAnalysis analysis) {
        for (Iterator<XSDTypeDefinition> typeDefIt = analysis.getTypeDefIterator();
             typeDefIt.hasNext(); ) {
            System.out.println(analysis.getTypeName(typeDefIt.next()));
        }
    }

    protected static void showElementNames(ElementNameAnalysis analysis) {
        for (Iterator<String> typeDefIt = analysis.getElementNameIterator(); typeDefIt.hasNext(); )
            System.out.println(typeDefIt.next());
    }
    
    protected static void showNonSingleOccurrences(SymbolOccurrenceAnalysis soAnalysis,
                                                   TypeAnalysis stAnalysis)
            throws UnimplementedFeatureException {
        for (Iterator<XSDTypeDefinition> typeDefIt = soAnalysis.getNonSingleOccurrenceTypeDefIterator();
             typeDefIt.hasNext(); ) {
            System.out.println(typeToString(typeDefIt.next(), "", stAnalysis));
        }
    }

    protected static void showOccurrences(SymbolOccurrenceAnalysis analysis) {
        for (Iterator<XSDTypeDefinition> typeDefIt = analysis.getTypeDefIterator();
             typeDefIt.hasNext(); ) {
            System.out.println(occurrencesToString(typeDefIt.next(), "", analysis));
        }
    }

    protected static void showSingleTypes(TypeAnalysis analysis)
            throws UnimplementedFeatureException {
        for (String name : analysis.getTrueSingleTypeNames()) {
            Set<TypeAnalysis.Occurrence> occurrences = analysis.getOccurrence(name);
            System.out.println(name + ":\n");
            for (TypeAnalysis.Occurrence occurrence : occurrences) {
                XSDTypeDefinition typeDef = occurrence.getTypeDef();
                System.out.println("  context: " + occurrence.getContext());
                System.out.println(typeToString(typeDef, "  ", analysis));
            }
        }
    }

    protected static void showTypes(TypeAnalysis analysis)
            throws UnimplementedFeatureException {
        for (Iterator<XSDTypeDefinition> typeDefIt = analysis.getTypeDefIterator();
             typeDefIt.hasNext(); ) {
            System.out.println(typeToString(typeDefIt.next(), "", analysis));
        }
    }

    protected static String typeToString(XSDTypeDefinition typeDef,
                                         String indent,
                                         TypeAnalysis analysis)
            throws UnimplementedFeatureException {
        StringBuilder str = new StringBuilder();
        String typeName = analysis.getTypeName(typeDef);
        str.append(indent).append(typeName).append(":\n");
        if (typeDef instanceof XSDComplexTypeDefinition) {
            str.append(attributesToString((XSDComplexTypeDefinition) typeDef,
                                          indent + "  "));
            XSDParticle particle = typeDef.getComplexType();
            if (particle != null)
                str.append(particleToString(particle, indent + "  ", analysis));
            else {
                XSDSimpleTypeDefinition simpleType = typeDef.getSimpleType();
                if (simpleType != null) {
                    str.append(indent + "  ").append("#PCDATA");
                    str.append("[");
                    if (simpleType.getURI() != null)
                        str.append(simpleType.getURI());
                    else
                        str.append("<").append(simpleType.getRootContainer());
                    str.append("]");
                } else {
                    str.append(indent + "  ").append("EMPTY");
                }
                str.append("\n");
            }
        } else {
            if (typeDef.getURI() != null)
                str.append(indent).append("  ").append(typeDef.getURI());
            else
                str.append(indent).append("  ").append("<").append(typeDef.getRootType().getURI());
            str.append("\n");
        }
        return str.toString();
    }

    protected static String occurrencesToString(XSDTypeDefinition typeDef,
                                                String indent,
                                                SymbolOccurrenceAnalysis analysis) {
        StringBuilder str = new StringBuilder();
        String typeName = analysis.getTypeName(typeDef);
        str.append(indent).append(typeName).append(":\n");
        List<String> symbols = analysis.getSymbols(typeDef);
        if (symbols != null) {
            Map<String,Integer> occurrences = SymbolOccurrenceAnalysis.countOccurrences(symbols);
            for (String name : occurrences.keySet()) {
                str.append(indent).append("  ").append(name).append(": ");
                str.append(occurrences.get(name)).append("\n");
            }
        } else {
            str.append("\n");
        }
        return str.toString();
    }

    protected static String attributesToString(XSDComplexTypeDefinition typeDef,
                                               String indent) {
        StringBuilder str = new StringBuilder();
        for (Iterator<XSDAttributeUse> attrIt = typeDef.getAttributeUses().iterator();
             attrIt.hasNext(); ) {
            XSDAttributeUse use = attrIt.next();
            XSDAttributeDeclaration attr = use.getAttributeDeclaration();
            str.append(indent).append("@").append(attr.getURI());
            XSDSimpleTypeDefinition attrType = attr.getTypeDefinition();
            if (attrType != null) {
                String typeName = attrType.getURI() != null ? attrType.getURI() : "<" + attrType.getRootType().getURI(); 
                str.append("[").append(typeName).append("]");
            } else {
                str.append("[*]");
            }
            if (!use.isRequired())
                str.append("?");
            str.append("\n");
        }
        XSDWildcard wildcard = typeDef.getAttributeWildcard();
        if (wildcard != null) {
            String namespaces = wildcard.getNamespaceConstraint() != null ?
                    wildcard.getNamespaceConstraint().toString() : "[]";
            str.append(indent).append("@ANY").append(namespaces).append("*").append("\n");
        }
        return str.toString();
    }

    protected static String particleToString(XSDParticle particle, String indent,
                                             TypeAnalysis analysis)
            throws UnimplementedFeatureException {
        StringBuilder str = new StringBuilder();
        String multiplicity = RegexAnalysis.multiplicity(particle);
        XSDTerm term = particle.getTerm();
        if (term instanceof XSDElementDeclaration) {
            XSDElementDeclaration element = (XSDElementDeclaration) term;
            str.append(indent).append(element.getURI());
            XSDTypeDefinition elementType = element.getTypeDefinition();
            str.append("[").append(analysis.getTypeName(elementType)).append("]");
            str.append(multiplicity);
            str.append("\n");
        } else if (term instanceof XSDModelGroup) {
            XSDModelGroup modelGroup = (XSDModelGroup) term;
            str.append(indent).append(modelGroup.getCompositor());
            str.append(multiplicity);
            str.append("\n");
            for (Iterator<XSDParticle> particleIt = modelGroup.getParticles().iterator();
                 particleIt.hasNext(); ) {
                XSDParticle localParticle = particleIt.next();
                str.append(particleToString(localParticle, indent + "  ", analysis));
            }
        } else if (term instanceof XSDWildcard) {
            String namespaces = "[]";
            if (((XSDWildcard) term).getNamespaceConstraint() != null) {
                namespaces = ((XSDWildcard) term).getNamespaceConstraint().toString();
            }
            str.append(indent).append("ANY").append(namespaces).append(multiplicity).append("\n");
        } else {
            throw new UnimplementedFeatureException();
        }
        return str.toString();
    }

    @SuppressWarnings({"unchecked"})
    protected static XSDSchema loadSchema(String fileName) {
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xsd", new XSDResourceFactoryImpl());
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getLoadOptions().put(XSDResourceImpl.XSD_TRACK_LOCATION,
                                         Boolean.TRUE);
        resourceSet.getResource(URI.createFileURI(fileName), true);
        for (Iterator resources = resourceSet.getResources().iterator();
             resources.hasNext(); ) {
            Resource resource = (Resource)resources.next();
            if (resource instanceof XSDResourceImpl) {
                XSDResourceImpl xsdResource = (XSDResourceImpl)resource;
                return xsdResource.getSchema();
            }
        }
        return null;
    }

    protected static Options initOptions() {
        Options options = new Options();
        
        OptionGroup inputOptions = new OptionGroup();
        inputOptions.setRequired(true);
        for (InputType inputTypeOption : InputType.values()) {
            inputOptions.addOption(new Option(inputTypeOption.flag(),
                                              inputTypeOption.longFlag(),
                                              inputTypeOption.hasValue(),
                                              inputTypeOption.comment()));
        }
        options.addOptionGroup(inputOptions);

        for (Action actionOption : Action.values()) {
            options.addOption(new Option(actionOption.flag(),
                                         actionOption.longFlag(),
                                         actionOption.hasValue(),
                                         actionOption.comment()));
        }

        Option hOption = new Option("h", "help", false,
                                    "help on using RegexCostDistribution");
        options.addOption(hOption);
        return options;
    }

    protected static CommandLine initCommandLine(String[] args,
                                                 Options options) {
        CommandLineParser clParser = new PosixParser();
        CommandLine cl = null;
        try {
            cl = clParser.parse(options, args);
        } catch (MissingOptionException e) {
            System.err.println(ERROR_PREFIX +
                               ExitCode.MISSING_OPTION.getReason() + ": '" +
                               e.getMessage() + "'");
            printHelp(options);
            System.exit(ExitCode.MISSING_OPTION.getCode());
        } catch (MissingArgumentException e) {
            System.err.println(ERROR_PREFIX +
                               ExitCode.MISSING_ARGUMENT.getReason() + ": '" +
                               e.getMessage() + "'");
            printHelp(options);
            System.exit(ExitCode.MISSING_ARGUMENT.getCode());
        } catch (UnrecognizedOptionException e) {
            System.err.println(ERROR_PREFIX +
                               ExitCode.UNRECOGNIZED_OPTION.getReason() + ": '" +
                               e.getMessage() + "'");
            printHelp(options);
            System.exit(ExitCode.UNRECOGNIZED_OPTION.getCode());
        } catch (org.apache.commons.cli.ParseException e) {
            System.err.println(ERROR_PREFIX +
                               ExitCode.PARSE_ERROR.getReason() + ": '" +
                               e.getMessage() + "'");
            printHelp(options);
            System.exit(ExitCode.PARSE_ERROR.getCode());
        }
        return cl;
    }

    protected static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java eu.fox7.xml.xsdanalyser.Analyser ( -f xsd-file | -d dir ) [-t] [-s] [-v] [-h]",
                            options);
    }

    protected static class XSDFileFilter implements FilenameFilter {

        public boolean accept(File dir, String fileName) {
            return fileName.toLowerCase().endsWith(".xsd");
        }
        
    }

    protected static class TypedRegexSerializer implements NodeSerializer<String> {

        protected TypeNameAnalysis typeNameAnalysis;

        public TypedRegexSerializer(TypeNameAnalysis typeNameAnalysis) {
            this.typeNameAnalysis = typeNameAnalysis;
        }

        public String serialize(Node node) {
            StringBuilder str = new StringBuilder();
            str.append("(");
            str.append(node.key() != null ? node.key() : "node");
            if (node.value() != null) {
                XSDTypeDefinition typeDef = (XSDTypeDefinition) node.value();
                String typeName = typeNameAnalysis.getTypeName(typeDef);
                str.append("[").append(typeName).append("]");
            }
            if (node.getNumberOfChildren() > 0)
                for (Iterator<Node> it = node.children(); it.hasNext(); )
                    str.append(" ").append(serialize(it.next()));
            str.append(")");
            return str.toString();
        }

    }

    protected static class NoTypedRegexSerializer implements NodeSerializer<String> {
        
        public String serialize(Node node) {
            StringBuilder str = new StringBuilder();
            str.append("(");
            String symbol = node.key() != null ? node.key() : "node";
            int pos = symbol.indexOf("#");
            if (pos >= 0)
                symbol = symbol.substring(pos + 1);
            str.append(symbol);
            if (node.getNumberOfChildren() > 0)
                for (Iterator<Node> it = node.children(); it.hasNext(); )
                    str.append(" ").append(serialize(it.next()));
            str.append(")");
            return str.toString();
        }
        
    }
    
    protected enum ExitCode {
        SUCCESS(0, "success"),
        FOUND(1, "found item"),
        MISSING_OPTION(-1, "missing required option;"),
        MISSING_ARGUMENT(-2, "missing required argument for option"),
        UNRECOGNIZED_OPTION(-3, "undefined option"),
        PARSE_ERROR(-4, "CLI parse error"),
        XSD_LOAD_FAILURE(-5, "can't load XSD"),
        NO_DIR_FAILURE(-6, "no directory");

        private int code;
        private String reason;

        ExitCode(int code, String reason) {
            this.code = code;
            this.reason = reason;
        }

        public int getCode() {
            return code;
        }

        public String getReason() {
            return reason;
        }

    }

    protected enum InputType {
        FILE("f", "file", true, "XSD file name"),
        DIR("d", "dir", true, "directory to scan for XSD files");

        private String flag;
        private String longFlag;    
        private boolean hasValue;
        private String comment;
        
        InputType(String flag, String longFlag, boolean hasValue, String comment) {
            this.flag = flag;
            this.longFlag = longFlag;
            this.hasValue = hasValue;
            this.comment = comment;
        }

        public String flag() {
            return flag;
        }
        
        public String longFlag() {
            return longFlag;
        }
        
        public boolean hasValue() {
            return hasValue;
        }
        
        public String comment() {
            return comment;
        }
        
    }

    protected enum Action {
        NAMES("N", "type-names", false, "show all type names"),
        ELEMENT_NAMES("E", "element-names", false, "show all element names"),
        TYPES("t", "types", false, "show all defined types"),
        REGEXES("r", "regexes", false, "show all regular expressions"),
        NO_TYPES("q", "no-types", false, "don't show types in regular expressions"),
        SINGLE_TYPES("s", "single-types", false, "test for true single types, 0 if none, 1 otherwise"),
        OCCURRENCES("o", "occurrences", false, "show the occurrences of symbols in type definitions"),
        NON_SINGLE_OCCURRENCES("n", "non-single-occurrences", false, "test for non-single-occurrence definitions, 0 if none, 1 otherwise"),
        VERBOSE("v", "verbose", false, "show true single type definitions/non single occurrence type definitions"),
        CANDIDATES("c", "candidates", false, "show candidate single type elements"),
        EXIT_CODES("e", "exit-codes", false, "show exit codes on System.err");
        
        protected static final String DECIMAL_REPR_FLAG = "d";
        protected static final String BOOLEAN_REPR_FLAG = "b";
        private String flag;
        private String longFlag;    
        private boolean hasValue;
        private String comment;

        Action(String flag, String longFlag, boolean hasValue, String comment) {
            this.flag = flag;
            this.longFlag = longFlag;
            this.hasValue = hasValue;
            this.comment = comment;
        }

        public String flag() {
            return flag;
        }

        public String longFlag() {
            return longFlag;
        }

        public boolean hasValue() {
            return hasValue;
        }

        public String comment() {
            return comment;
        }

   }

}
