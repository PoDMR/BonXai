package eu.fox7.schematoolkit.dtd.common;

import eu.fox7.schematoolkit.common.AnyPattern;
import eu.fox7.schematoolkit.common.ChoicePattern;
import eu.fox7.schematoolkit.common.CountingPattern;
import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.common.ProcessContentsInstruction;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.common.SequencePattern;
import eu.fox7.schematoolkit.dtd.common.exceptions.ContentModelCountingPatternIllegalMinValueException;
import eu.fox7.schematoolkit.dtd.common.exceptions.ContentModelCountingPatternNotAllowedDTDValueException;
import eu.fox7.schematoolkit.dtd.common.exceptions.ContentModelEmptyChildParticleListException;
import eu.fox7.schematoolkit.dtd.common.exceptions.ContentModelIllegalChildParticleException;
import eu.fox7.schematoolkit.dtd.common.exceptions.ContentModelIllegalMixedDuplicateElementException;
import eu.fox7.schematoolkit.dtd.common.exceptions.ContentModelIllegalMixedParticleException;
import eu.fox7.schematoolkit.dtd.common.exceptions.ContentModelIllegalParticleException;
import eu.fox7.schematoolkit.dtd.common.exceptions.ContentModelIllegalStringForMixedContentException;
import eu.fox7.schematoolkit.dtd.common.exceptions.ContentModelNullParticleException;
import eu.fox7.schematoolkit.dtd.common.exceptions.ContentModelStringEmptyException;
import eu.fox7.schematoolkit.dtd.common.exceptions.ContentModelStringTokenizerIllegalStateException;
import eu.fox7.schematoolkit.dtd.common.exceptions.IllegalNAMEStringException;
import eu.fox7.schematoolkit.dtd.om.DocumentTypeDefinition;
import eu.fox7.schematoolkit.dtd.om.Element;
import eu.fox7.schematoolkit.xsd.om.ElementRef;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * Helper class for extracting a particle from a given ContentModel-String or
 * in direction of generating a regular expression string from a given
 * element and its particle.
 * 
 * @author Lars Schmidt
 */
public class ElementContentModelProcessor {

    /**
     * Attribute "dtd" holds the documentTypeDefinition-object for further usage
     **/
    private DocumentTypeDefinition dtd;
    /**
     * Boolean attribute "mixed" is used for the direction from a String to a
     * particle. It represents if the ContentModel given by the string contains
     * the #PCDATA entry, so that the resulting element in the datastructure is
     * of type mixed.
     **/
    private boolean mixed;
    private boolean mixedStar;
    private String regExpString;

    /**
     * Holds an enumeration of possible values for defining the type of a
     * regular expression.
     * This is used by the method: convertTokenToCorrespondingParticle
     **/
    private enum RegularExpressionType {

        // CHOICE: The recognized regularExpression is of type: choice
        CHOICE,
        // SEQUENCE: The recognized regularExpression is of type: sequence
        SEQUENCE,
        //UNSPECIFIED: There is no value specified.
        UNSPECIFIED;
    }

    /**
     * Constructor of class ElementContentModelProcessor with parameter dtd
     * This is used in the standard case: parsing a regular expression string of
     * a DTD content model and generating the particle-structure.
     * @param dtd
     */
    public ElementContentModelProcessor(DocumentTypeDefinition dtd) {
        this.dtd = dtd;
        this.mixed = false;
        this.mixedStar = false;
    }

    /**
     * Constructor of class ElementContentModelProcessor without any parameter
     * This is used in the case: writing the content particle of a given element
     * to a regular expression string in a DTD element content model
     */
    public ElementContentModelProcessor() {
        this.dtd = new DocumentTypeDefinition();
        this.mixed = false;
        this.mixedStar = false;
    }

    /**
     * Getter for the boolean attribute "mixed"
     * Used for the conversion from String to Particle
     * @return
     */
    public boolean isMixed() {
        return this.mixed;
    }

    /**
     * Getter for the boolean attribute "mixedStar"
     * Used for the conversion from String to Particle
     * @return
     */
    public boolean isMixedStar() {
        return this.mixed && this.mixedStar;
    }

    /**
     * Public method convertRegExpStringToParticle converts a given regular expression
     * string of a DTD element content model and generates the necessary
     * particle-structure for the use in die corresponding element object model.
     * @param contentModelRegExp
     * @return Particle
     * @throws Exception
     */
    public Particle convertRegExpStringToParticle(String contentModelRegExp) throws Exception {
        this.mixed = false;
        this.mixedStar = false;
        this.regExpString = contentModelRegExp;

        // If the given string is null or empty, it is a failure.
        if (contentModelRegExp == null || contentModelRegExp.length() == 0) {
            throw new ContentModelStringEmptyException();
        }

        // Switch over the different possible cases of content models in a DTD
        // element
        if (contentModelRegExp.equals("EMPTY")) {
            // Case: EMPTY
            return null;
        } else if (contentModelRegExp.equals("ANY")) {
            // Case: ANY

            // The second parameter is for the corresponding namespace. In DTD
            // there are no namespaces.
            return new AnyPattern(ProcessContentsInstruction.Strict, "");
        } else if (contentModelRegExp.equals("(#PCDATA)") || contentModelRegExp.equals("(#PCDATA)*")) {
            // Case: (#PCDATA) or (#PCDATA)*

            // Case: (#PCDATA)
            this.mixed = true;

            // Case: (#PCDATA)*
            if (contentModelRegExp.equals("(#PCDATA)*")) {
                this.mixedStar = true;
            }
            return null;
        }

        // Case: complex content model --> the given regular expression has to
        // be parsed and a special particle structure has to be generated.
        ElementContentModelStringTokenizer tokenizer = new ElementContentModelStringTokenizer(contentModelRegExp, "|,*?+() \t\r\n", true);
        String next = tokenizer.nextToken();

        // An opening bracket as the next tokenizer is not allowed.
        if (next.charAt(0) != '(') {
            throw new ContentModelStringTokenizerIllegalStateException(String.valueOf(next.charAt(0)));
        }

        // Check if the given string is conform to the specification of mixed
        // content models of an element
        if (regExpString.startsWith("(#PCDATA|") && !regExpString.endsWith(")*")) {
            throw new ContentModelIllegalStringForMixedContentException("RegExpString :\"" + regExpString + "\" does not end with \")*\"");
        }

        // Start the parsing of the regular expression and return the generated
        // structure.
        return convertTokenToCorrespondingParticle(tokenizer);

    }

    /**
     * Private method: convertTokenToCorrespondingParticle is used by the
     * convertRegExpStringToParticle method and handles the tokenized content.
     * It is a recursive method, which generates the new particle structure.
     * @param element
     * @return Particle     this is the fully generated particle structure.
     * @throws Exception
     */
    private Particle convertTokenToCorrespondingParticle(ElementContentModelStringTokenizer tokenizer) throws Exception {
        DTDNameChecker nameChecker = new DTDNameChecker();

        // Prepare the variable of type Particle for use as result of this method
        Particle particle = null;
        // Some particles have more than one direct children.
        // These will be hold in this LinkedList.
        LinkedList<Particle> childParticleList = new LinkedList<Particle>();
        // Initialize the type of the current particle to a neutral one.
        RegularExpressionType type = RegularExpressionType.UNSPECIFIED;
        // Variable for the current char
        char currentChar;
        // Variable for the next upcoming tokenizer
        String next = "";

        // The given tokenizer has to be examined in any case --> do-while-loop
        do {
            // In every loop-iteration the we have to handle the next tokenizer
            next = tokenizer.nextToken();
            currentChar = next.charAt(0);
            // If the current char is a whitespace character, it can be ignored.
            if (isWhitespaceChar(currentChar)) {
                continue;
            }

            // Case for #PCDATA
            if (currentChar == '#') {
                // All whitespace characters can be ignored at this point.
                do {
                    currentChar = tokenizer.nextToken().charAt(0);
                } while (isWhitespaceChar(currentChar));

                // Determine if the content is a "mixed"-content (DTD: #PCDATA).
                if (next != null && next.equals("#PCDATA")) {
                    this.mixed = true;
                } else {
                    this.mixed = false;
                }

                // In a DTD mixed content can only be defined by the use of a
                // choice expression.
                if (currentChar != '|') {
                    next.length();
                    throw new ContentModelIllegalStringForMixedContentException("\"" + this.regExpString + "\" with wrong char: \"" + tokenizer.nextToken() + "\"");
                }
                continue;
            } else if (currentChar == '(') {
                // Case: explore the given tokenizer recursivly (Start)
                childParticleList.add(convertTokenToCorrespondingParticle(tokenizer));
            } else if (currentChar == '|') {
                // Case: the current particle is a choice
                type = RegularExpressionType.CHOICE;
            } else if (currentChar == ',') {
                // Case: the current particle is a sequence
                type = RegularExpressionType.SEQUENCE;
            } else if (currentChar == ')') {
                // Case: end of the current particle
                break;
            } else {
                // It is necessary to register the new element in the SymbolTable for all elements in the DTD and
                // generate an ElementRef for the current particle. The element itself can be exchanged during the
                // parse process, but the reference stays always the same!
                if (!nameChecker.checkForXMLName(next)) {
                    throw new IllegalNAMEStringException("Element: " + next, next);
                }
                Element element = new Element(new QualifiedName(Namespace.EMPTY_NAMESPACE, next));
                particle = element;
                if (this.mixed){ 
                	for (Particle currentChild : childParticleList) {
                		if (currentChild instanceof ElementRef) {
                			if (((ElementRef) currentChild).getElementName().equals(element.getName())) {
                				throw new ContentModelIllegalMixedDuplicateElementException("Particle contains more than one element with name: \"" + element.getName() + "\"");
                			}
                		}
                	}
                }
                // Handle dtd-element multiplicity ...? or ...* or ...+

                // Again: all whitespace characters can be ignored.
                do {
                    next = tokenizer.nextToken();
                    currentChar = next.charAt(0);
                } while (isWhitespaceChar(currentChar));
                if (currentChar == '?') {
                    // Case: '?' --> zero or one
                    // null-value stands for "unbounded"
                    CountingPattern countingPattern = new CountingPattern(particle, 0, 1);
                    particle = countingPattern;

                } else if (currentChar == '*') {
                    // Case: '*' --> zero or more
                    // null-value stands for "unbounded"
                    CountingPattern countingPattern = new CountingPattern(particle, 0, null);
                    particle = countingPattern;

                } else if (currentChar == '+') {
                    // Case: '+' --> one or more
                    // null-value stands for "unbounded"
                    CountingPattern countingPattern = new CountingPattern(particle, 1, null);
                    particle = countingPattern;

                } else if (currentChar == ')') {
                    // Ending of the current particle.
                    // Nothing has to be done here.
                } else {
                    tokenizer.setPushback(next);
                }
                childParticleList.add(particle);
            }
            // While it is not the end of the current particle, iterate again.
        } while (currentChar != ')');

        // The type of the current particle has been determined
        if (type.equals(RegularExpressionType.CHOICE)) {
            // Case: Choice
            ChoicePattern choicePattern = new ChoicePattern();
            for (Particle currentParticle : childParticleList) {
                choicePattern.addParticle(currentParticle);
            }
            particle = choicePattern;

        } else if (type.equals(RegularExpressionType.SEQUENCE)) {
            // Case: Sequence
            SequencePattern sequencePattern = new SequencePattern();
            for (Particle currentParticle : childParticleList) {
                sequencePattern.addParticle(currentParticle);
            }
            particle = sequencePattern;

        } else {
            // CASE: (...) brackets without quantifier or redundant brackets
            if (childParticleList.size() == 1) {
                particle = childParticleList.getFirst();
            } else {
                throw new ContentModelIllegalChildParticleException("Size of child particle list: " + Integer.toString(childParticleList.size()));
            }
        }

        // Handling of the DTD element or particle multiplicity (...)+

        // Again: all whitespace characters can be ignored.
        do {
            if (tokenizer.hasMoreTokens() == false) {
                break;
            }
            next = tokenizer.nextToken();
            currentChar = next.charAt(0);
        } while (isWhitespaceChar(currentChar));

        if (currentChar == '?') {
            // Case: '?' --> zero or one
            CountingPattern countingPattern = new CountingPattern(particle, 0, 1);
            particle = countingPattern;

        } else if (currentChar == '*') {
            // Case: '*' --> zero or more
            CountingPattern countingPattern = new CountingPattern(particle, 0, null);
            particle = countingPattern;

            if (this.isMixed()) {
                this.mixedStar = true;
            }

        } else if (currentChar == '+') {
            // Case: '+' --> one or more
            CountingPattern countingPattern = new CountingPattern(particle, 1, null);
            particle = countingPattern;
        } else {
            tokenizer.setPushback(next);
        }

        // Return the generated particle
        return particle;
    }

    /**
     * Public method: convertParticleToRegExpString generates a regular
     * expression string for the use in a resulting DTD file from a given
     * element (particle + mixed/empty).
     * @param element
     * @return String
     * @throws Exception
     */
    public String convertParticleToRegExpString(Element element) throws Exception {
        DTDNameChecker nameChecker = new DTDNameChecker();
        String currentRegExpString = "";

        // Differentiate between the possible types of the given element
        if (element.hasAnyType()) {
            // Element has the "any"-Type
            currentRegExpString = "ANY";
        } else if (!element.getMixed() && !element.getMixedStar() && element.isEmpty()) {
            // Element is "empty"
            currentRegExpString = "EMPTY";
        } else if (element.getMixed() && !element.getMixedStar() && element.isEmpty()) {
            // Element has the "mixed"-Type and no particle (is empty)
            currentRegExpString = "(#PCDATA)";
        } else if (element.getMixed() && element.getMixedStar() && element.isEmpty()) {
            // Element has the "mixed"-Type and no particle (is empty)
            currentRegExpString = "(#PCDATA)*";
        } else if (element.getMixed() && !element.isEmpty()) {
            // Element has the "mixed"-Type and a particle (not empty)
            String mixedParticleString = "";

            // There is only a choice in a countingPattern (...|...|...)* allowed here!
            // Every particle in the choicePattern has to be an ElementRef!
            if (!(element.getParticle() instanceof CountingPattern)) {
                throw new ContentModelIllegalMixedParticleException("Element (" + element.getName() + ") content model child instance: " + element.getParticle().getClass().getName() + ". Only CountingPattern is allowed here.");
            }

            // The particle of the element has to be of type CountingPattern
            CountingPattern countingPattern = (CountingPattern) element.getParticle();

            // The min value of the countingpattern has to be at last 1
            if (!(countingPattern.getMin() == 0 && countingPattern.getMax() == null)) {
                throw new ContentModelIllegalMixedParticleException("Element \"" + element.getName() + "\": CountingPattern values (minOccurs: " + countingPattern.getMin() + ", maxOccurs: " + countingPattern.getMax() + ")");
            }

            // The two only allowed particle of the countingPattern are choicPattern or elementRef
            if (!(countingPattern.getParticle() instanceof ChoicePattern) && !(countingPattern.getParticle() instanceof ElementRef)) {
                throw new ContentModelIllegalMixedParticleException("Element \"" + element.getName() + "\": CountingPattern child instance: " + countingPattern.getParticle().getClass().getName());
            }

            if (countingPattern.getParticle() instanceof ChoicePattern) {
                // Choicepattern
                ChoicePattern choicePattern = (ChoicePattern) countingPattern.getParticle();

                // The choicePattern is not allowed to be empty or null
                if (choicePattern.getParticles() == null || choicePattern.getParticles().isEmpty()) {
                    throw new ContentModelIllegalMixedParticleException("Element \"" + element.getName() + "\": ChoicePattern is empty");
                }

                HashSet<String> elementNames = new HashSet<String>();

                // The only allowed type of the content of the choicePattern is ElementRef
                for (Particle currentParticle : choicePattern.getParticles()) {
                    if (currentParticle instanceof ElementRef) {
                        ElementRef elementRef = (ElementRef) currentParticle;
                        if (elementRef.getElementName() == null) {
                            // elementRef element is null
                            throw new ContentModelNullParticleException("ElementRef", "Element is null");
                        }
                        QualifiedName currentElementName = elementRef.getElementName();

                        if (!nameChecker.checkForXMLName(currentElementName)) {
                            throw new IllegalNAMEStringException("Element: ", currentElementName.getName());
                        }

                        if (!elementNames.contains(currentElementName)) {
                            elementNames.add(currentElementName.getName());
                            mixedParticleString += currentElementName + "|";
                        } else {
                            throw new ContentModelIllegalMixedDuplicateElementException("Element \"" + element.getName() + "\": ChoicePattern contains more than one of: \"" + currentElementName + "\"");
                        }
                    } else {
                        throw new ContentModelIllegalMixedParticleException("Element \"" + element.getName() + "\": ChoicePattern child instance: " + currentParticle.getClass().getName());
                    }
                }

            } else if (countingPattern.getParticle() instanceof ElementRef) {
            	ElementRef elementRef = (ElementRef) countingPattern.getParticle();
            	if (elementRef.getElementName() == null) {
            		// elementRef element is null
            		throw new ContentModelNullParticleException("ElementRef", "Element is null");
            	}

            	if (!nameChecker.checkForXMLName(elementRef.getElementName())) {
            		throw new IllegalNAMEStringException("Element: ", elementRef.getElementName().getName());
            	}
            	mixedParticleString += elementRef.getElementName().getName() + "|";
            }
            

            mixedParticleString = mixedParticleString.substring(0, mixedParticleString.length() - 1);

            // Combine the generated string with the standard frame for mixed content model elements
            currentRegExpString = "(#PCDATA|" + mixedParticleString + ")*";

        } else if (!element.getMixed() && !element.isEmpty()) {
            // Case: normal content model
            currentRegExpString = convertParticleToRegExpString(element.getParticle(), element.getName());
            if (!currentRegExpString.startsWith("(")) {
                currentRegExpString = "(" + currentRegExpString + ")";
            }

        }
        // return the resulting string
        this.regExpString = currentRegExpString;
        return currentRegExpString;
    }

    /**
     * Private method: convertParticleToRegExpString is used by the
     * convertParticleToRegExpString method and recursivly handles the particle
     * of the given Element.
     * @param particle, elementName
     * @return String
     * @throws Exception
     */
    private String convertParticleToRegExpString(Particle particle, QualifiedName elementName) throws Exception {
        DTDNameChecker nameChecker = new DTDNameChecker();
        String currentRegExpString = "";

        // Handle the normal particle content of an element content model
        if (particle instanceof AnyPattern) {
            // Case: ANY
            currentRegExpString = "ANY";
        } else if (particle instanceof ChoicePattern) {
            // Case: CHOICE
            ChoicePattern choicePattern = (ChoicePattern) particle;

            if (choicePattern == null || choicePattern.getParticles().isEmpty()) {
                throw new ContentModelEmptyChildParticleListException("choicePattern");
            }

            currentRegExpString += "(";
            for (Particle currentChildParticle : choicePattern.getParticles()) {
                currentRegExpString += convertParticleToRegExpString(currentChildParticle, elementName) + "|";
            }
            currentRegExpString = currentRegExpString.substring(0, currentRegExpString.length() - 1) + ")";
        } else if (particle instanceof SequencePattern) {
            // Case: SEQUENCE
            SequencePattern sequencePattern = (SequencePattern) particle;

            if (sequencePattern == null || sequencePattern.getParticles().isEmpty()) {
                throw new ContentModelEmptyChildParticleListException("sequencePattern");
            }

            currentRegExpString += "(";
            for (Particle currentChildParticle : sequencePattern.getParticles()) {
                currentRegExpString += convertParticleToRegExpString(currentChildParticle, elementName) + ",";
            }
            currentRegExpString = currentRegExpString.substring(0, currentRegExpString.length() - 1) + ")";
        } else if (particle instanceof CountingPattern) {
            // Case: CountingPattern
            CountingPattern countingPattern = (CountingPattern) particle;

            if (countingPattern == null || countingPattern.getParticle() == null) {
                throw new ContentModelEmptyChildParticleListException("countingPattern");
            }

            String countingPatternType = "";
            
            if (countingPattern.getMin() == 0 && countingPattern.getMax() == null) {
                countingPatternType = "*";
            } else if (countingPattern.getMin() == 1 && countingPattern.getMax() == null) {
                countingPatternType = "+";
            } else if (countingPattern.getMin() == 0 && countingPattern.getMax() == 1) {
                countingPatternType = "?";
            } else {
                throw new ContentModelCountingPatternNotAllowedDTDValueException("minOccurs: " + countingPattern.getMin() + "maxOccurs: " + countingPattern.getMax());
            }

            if ((countingPattern.getParticle() instanceof CountingPattern) || (countingPattern.getParticle() instanceof ElementRef)) {
                currentRegExpString += "(" + convertParticleToRegExpString(countingPattern.getParticle(), elementName) + ")" + countingPatternType;
            } else {
                currentRegExpString += convertParticleToRegExpString(countingPattern.getParticle(), elementName) + countingPatternType;
            }
        } else if (particle instanceof ElementRef) {
            // Case: ElementRef
            ElementRef elementRef = (ElementRef) particle;
            if (elementRef.getElementName() == null) {
                // elementRef element is null
                throw new ContentModelNullParticleException("ElementRef", "Element is null");
            }
            if (!nameChecker.checkForXMLName(elementRef.getElementName())) {
                throw new IllegalNAMEStringException("Element: " + elementRef.getElementName(), elementRef.getElementName().getName());
            }
            currentRegExpString += elementRef.getElementName();
        } else {
            if (particle == null) {
                // Particle is null
                throw new ContentModelNullParticleException(elementName.getName(), "Particle is null");
            } else {
                // Particle is of illegal type
                throw new ContentModelIllegalParticleException(elementName.getName(), particle.getClass().getName());
            }
        }

        // The regular expression string is not allowed to be empty here
        if (currentRegExpString.equals("")) {
            throw new ContentModelStringEmptyException();
        }

        // return the resulting regExpString
        return currentRegExpString;
    }

    /**
     * Helper method for the decision if a given char is equal to one of the
     * explicitly specified whitespace chars. This is used by the method:
     * convertTokenToCorrespondingParticle
     * @param testChar
     * @return
     */
    public boolean isWhitespaceChar(char testChar) {
        return (testChar == ' ' || testChar == '\t' || testChar == '\r' || testChar == '\n');
    }
}

