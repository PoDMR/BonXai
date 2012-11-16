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

package eu.fox7.schematoolkit.converter.relaxng2xsd;

import eu.fox7.schematoolkit.common.AllPattern;
import eu.fox7.schematoolkit.common.AnyPattern;
import eu.fox7.schematoolkit.common.AttributeParticle;
import eu.fox7.schematoolkit.common.ChoicePattern;
import eu.fox7.schematoolkit.common.CountingPattern;
import eu.fox7.schematoolkit.common.ElementRef;
import eu.fox7.schematoolkit.common.GroupReference;
import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.common.ParticleContainer;
import eu.fox7.schematoolkit.common.ProcessContentsInstruction;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.common.SequencePattern;
import eu.fox7.schematoolkit.converter.relaxng2xsd.exceptions.*;
import eu.fox7.schematoolkit.relaxng.om.*;
import eu.fox7.schematoolkit.xsd.om.ComplexContentExtension;
import eu.fox7.schematoolkit.xsd.om.ComplexContentType;
import eu.fox7.schematoolkit.xsd.om.ComplexType;
import eu.fox7.schematoolkit.xsd.om.ComplexTypeInheritanceModifier;
import eu.fox7.schematoolkit.xsd.om.ImportedSchema;
import eu.fox7.schematoolkit.xsd.om.SimpleContentExtension;
import eu.fox7.schematoolkit.xsd.om.SimpleContentType;
import eu.fox7.schematoolkit.xsd.om.SimpleType;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.schematoolkit.xsd.om.XSDSchema;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Class PatternElementConverter
 *
 * Conversion of RELAX NG elements into a XML XSDSchema particle structure
 *
 * @author Lars Schmidt
 */
public class PatternElementConverter extends ConverterBase {

    /**
     * Set of found recursive elements
     */
    private HashSet<Element> recursiveElements = new HashSet<Element>();
    /**
     * Map of already converted elements for avoidance of multiple conversion
     * of the same element
     */
    private HashMap<Element, eu.fox7.schematoolkit.common.Particle> alreadyConvertedElements = new HashMap<Element,Particle>();
    /**
     * Set of all already used toplevel group names. This helps to generate
     * unique names for new groups in the same schema.
     */
    private HashSet<QualifiedName> usedTopLevelGroupNames = new HashSet<QualifiedName>();
    /**
     * Set of all already used toplevel element names. This helps to generate
     * unique names for new elements in the same schema.
     */
    private HashSet<QualifiedName> usedTopLevelElementNames = new HashSet<QualifiedName>();
    /**
     * Used in case of element recursion
     */
    private HashMap<String, HashSet<Element>> fqNamesToRNGElements = new HashMap<String, HashSet<Element>>();
    /**
     * Used in case of complexType recursion
     *
     * Maps an RELAX NG element to its newly generated XML XSDSchema type.
     */
    private HashMap<Element, QualifiedName> elementToTypeMap = new HashMap<Element, QualifiedName>();
    /**
     * Maps an RELAX NG element to all newly generated XML XSDSchema elements.
     */
    private HashMap<Element, HashSet<eu.fox7.schematoolkit.xsd.om.Element>> rngElementToxsdElements = new HashMap<Element, HashSet<eu.fox7.schematoolkit.xsd.om.Element>>();
    /**
     * Stores references to all toplevel types in the current XML XSDSchema.
     */
    //private HashSet<QualifiedName> topLevelTypes = new HashSet<QualifiedName>();
    /**
     * Local variable for the converter of RELAX NG patterns to XML XSDSchema attributes (attributeParticles)
     */
    private PatternAttributeConverter patternAttributeConverter;
    /**
     * Local variable for the converter of RELAX NG patterns to XML XSDSchema simpleType-definitions
     */
    private PatternSimpleTypeConverter patternSimpleTypeConverter;
    /**
     *
     */
    private HashMap<String, QualifiedName> refToGroupMap = new HashMap<String, QualifiedName>();
    private HashMap<String, QualifiedName> refToAttributeGroupMap = new HashMap<String, QualifiedName>();
    private HashMap<String, ComplexType> refToTypeMap = new HashMap<String, ComplexType>();
    private HashSet<String> usedLocalGroupNames = new HashSet<String>();

    /**
     * Constructor of class PatternElementConverter
     * @param xmlSchema                 target of the conversion
     * @param relaxng                   source of the conversion
     * @param patternInformation        global map of pattern information
     * @param usedLocalNames            globale set of all used local names
     * @param usedLocalTypeNames        global set of already used local type names
     */
    public PatternElementConverter(XSDSchema xmlSchema, RelaxNGSchema relaxng, HashMap<Pattern, HashSet<String>> patternInformation, HashSet<String> usedLocalNames, HashSet<String> usedLocalTypeNames) {
        super(xmlSchema, relaxng, patternInformation, usedLocalNames, usedLocalTypeNames);
        this.patternSimpleTypeConverter = new PatternSimpleTypeConverter(xmlSchema, relaxng, patternInformation, usedLocalNames, usedLocalTypeNames);
        this.patternAttributeConverter = new PatternAttributeConverter(xmlSchema, relaxng, patternInformation, usedLocalNames, usedLocalTypeNames, this.patternSimpleTypeConverter);
        this.patternAttributeConverter.setRefToAttributeGroupMap(this.refToAttributeGroupMap);
    }

    /**
     * Method: convert
     * This is the main method of this class for starting the conversion process
     * @throws Exception
     */
    public void convert() throws Exception {
        // Find all possible root elements in the given RELAX NG root pattern
        Collection<Element> rootElements = findRootElementPatterns();

        for (Element element: rootElements) {
            if (!(this.patternInformation.get(element) != null && this.patternInformation.get(element).contains("notAllowed"))) {
                // Convert the current RELAX NG element into a list of XML schema particles holding the converted elements
                eu.fox7.schematoolkit.common.Particle currentParticle = this.convertElement(element);

                addRootParticle(currentParticle);
            }
        }
    }

    private void addRootParticle(Particle particle) {
        if (particle instanceof ChoicePattern) {
        	for (Particle innerParticle: ((ChoicePattern) particle).getParticles())
        		addRootParticle(innerParticle);
        } else if (particle instanceof eu.fox7.schematoolkit.xsd.om.Element) {
        	eu.fox7.schematoolkit.xsd.om.Element xsdElement = (eu.fox7.schematoolkit.xsd.om.Element) particle;

        	// Namespace check
        	if (xsdElement.getName().getNamespace().equals(this.xmlSchema.getTargetNamespace())) {
        		// Add the element to the list of global elements
        		this.xmlSchema.addElement(xsdElement);
        		this.usedTopLevelElementNames.add(xsdElement.getName());
        	}
        } else if (particle instanceof eu.fox7.schematoolkit.common.AnyPattern) {
        	// AnyPattern is not allowed as schema root!
        	// ==> ignore the returned AnyPattern
        }
	}

	/**
     * Method for finding the root element patterns in the given RELAX NG XSDSchema
     * @return LinkedList<Pattern>
     */
    private LinkedList<Element> findRootElementPatterns() throws RecursionWithoutElementException {
        HashSet<Pattern> alreadySeenRootElements = new HashSet<Pattern>();
        return this.findRootElements(this.relaxng.getRootPattern(), alreadySeenRootElements);
    }

    private LinkedList<Element> findRootElements(Collection<Pattern> patterns, HashSet<Pattern> alreadySeenRootElements) throws RecursionWithoutElementException {
        LinkedList<Element> resultRootPatternList = new LinkedList<Element>();
        for (Pattern pattern: patterns)
            resultRootPatternList.addAll(findRootElements(pattern, alreadySeenRootElements));

        return resultRootPatternList;
    }

    /**
     * Recursive method for finding the root element patterns
     * @param pattern                   Current start pattern for check of root elements
     * @param alreadySeenRootElements   List of already seen root elements to avoid infinit loops
     * @return LinkedList<Pattern>      List of all possible root elements
     */
    private LinkedList<Element> findRootElements(Pattern pattern, HashSet<Pattern> alreadySeenRootElements) throws RecursionWithoutElementException {
        LinkedList<Element> resultRootPatternList = new LinkedList<Element>();
        if (!alreadySeenRootElements.contains(pattern)) {
            alreadySeenRootElements.add(pattern);

            if (pattern instanceof Element) {
                resultRootPatternList.add((Element) pattern);
            } else if (pattern instanceof Choice) {
                resultRootPatternList=findRootElements(((Choice) pattern).getPatterns(), alreadySeenRootElements);
            } else if (pattern instanceof Ref) {
                Ref ref = (Ref) pattern;
                for (Define innerDefine: ref.getDefineList()) {
                    resultRootPatternList.addAll(findRootElements(innerDefine.getPatterns(), alreadySeenRootElements));
                }
            } else if (pattern instanceof ParentRef) {
                ParentRef parentRef = (ParentRef) pattern;
                for (Define innerDefine: parentRef.getDefineList()) {
                    resultRootPatternList.addAll(findRootElements(innerDefine.getPatterns(), alreadySeenRootElements));
                }
            } else if (pattern instanceof Grammar) {
                Grammar grammar = (Grammar) pattern;
                this.findRecursiveElementsInGrammar(grammar);
                resultRootPatternList.addAll(findRootElements(grammar.getStartPatterns(), alreadySeenRootElements));
            }
        }
        return resultRootPatternList;
    }

    /**
     * Convert a given element pattern to the corresponding XML XSDSchema particles
     * @param element               basis of the conversion
     * @return LinkedList<eu.fox7.schematoolkit.common.Particle>      result of the conversion
     * @throws Exception
     */
    private eu.fox7.schematoolkit.common.Particle convertElement(Element element) throws Exception {
        eu.fox7.schematoolkit.common.Particle resultParticle = null;

        // Generate all possible names for the resulting structure.
        // For each name there has to be generated a new element.

        NameClassAnalyzer nameClassAnalyzer = new NameClassAnalyzer(element);
        nameClassAnalyzer.analyze();

        String localTypeName = "";

//        boolean normalElement = false;

        //        	if (elementNameInfos.get(currentNameKey) != null) {
        //        		Object currentAnyObject = elementNameInfos.get(currentNameKey);
        //        		if (currentAnyObject instanceof AnyPattern) {
        //        			AnyPattern anyPattern = (AnyPattern) currentAnyObject;
        //
        //        			if (currentNameKey.startsWith("-")) {
        //        				// Set the anyPattern into the correct XSDSchema
        //        				AnyPattern newAnyPattern = new AnyPattern(ProcessContentsInstruction.Strict, "##other");
        //
        //        				String groupName = "externalAny";
        //        				SymbolTableRef<eu.fox7.schematoolkit.xsd.om.Group> strGroup = putParticleIntoGroup(groupName, anyPattern.getNamespace(), newAnyPattern, false);
        //        				eu.fox7.bonxai.common.GroupRef groupRef = new GroupRef(strGroup);
        //        				resultParticle.add(groupRef);
        //
        //        			} else {
        //        				resultParticle.add(anyPattern);
        //        			}
        //        		}
        //        	} 

        Type type = null;
        
        if ((this.recursiveElements.contains(element) && this.elementToTypeMap.get(element) == null) || !this.recursiveElements.contains(element)) {
//        	normalElement = true;

        	// Generate a unique name for the type
        	if (this.usedLocalTypeNames.contains(localTypeName)) {
        		localTypeName = localTypeName + "_" + this.usedLocalTypeNames.size();
        	}
        	this.usedLocalTypeNames.add(localTypeName);

        	Namespace namespace = element.getAttributeNamespace();

        	type = generateTypeForElement(element, localTypeName);
      		this.elementToTypeMap.put(element, type.getName());
        }

        for(QualifiedName name: nameClassAnalyzer.getNames()) {
            eu.fox7.schematoolkit.xsd.om.Element xsdElement = new eu.fox7.schematoolkit.xsd.om.Element(name, type.getName());
        	
            if (xsdElement.getName().getNamespace().equals(this.xmlSchema.getTargetNamespace()) || xsdElement.getName().equals("")) {
            	if (this.recursiveElements.contains(element)) {
            		if (type != null) {
    					this.xmlSchema.addType(type);
        				type.setIsAnonymous(false);
            		}
            	}
            	resultParticle = xsdElement;
            } else {
            	ImportedSchema importedSchema = updateOrCreateImportedSchema(xsdElement.getName().getNamespace());
            	if (!usedTopLevelElementNames.contains(xsdElement.getName())) {
            		// Generate Imported XSDSchema and attach it to the basis schema
            		// Put the xsdElement to the correct XSDSchema

            		importedSchema.getSchema().addElement(xsdElement);
            		usedTopLevelElementNames.add(xsdElement.getName());

            		if (type != null) {
            			importedSchema.getSchema().addType(type);
            		}
            	}
        		ElementRef elementRef = new ElementRef(xsdElement.getName());
        		resultParticle = elementRef;
            }
            if (this.recursiveElements.contains(element)) {
            	this.addXSDElementToRNGElementMap(element, xsdElement);
            }
       }
        


 

        if (resultParticle!=null) {
        	this.alreadyConvertedElements.put(element, resultParticle);
        	return resultParticle;
        } else {
        	// Exception on not initialized resultParticle
        	throw new ParticleIsNullException(element.getClass().getName(), "{" + element.getAttributeNamespace() + "}" + element.getNameAttribute());
        }
    }

    /**
     * Method for generating a XML XSDSchema type for a given RELAX NG element and its child-pattern
     * @param element
     * @param patternList
     * @param localTypeName
     * @param countRealElementNames
     * @return SymbolTableRef<eu.fox7.schematoolkit.xsd.om.Type>
     * @throws TypeIsNullException
     * @throws Exception
     */
    private eu.fox7.schematoolkit.xsd.om.Type generateTypeForElement(Element element, String localTypeName) throws TypeIsNullException, Exception {

        HashSet<String> currentPatternInformation = new HashSet<String>();
        LinkedList<Pattern> patternsContainingElement = new LinkedList<Pattern>();
        HashSet<Pattern> patternsContainingAttribute = new HashSet<Pattern>();
        Pattern patternContainingData = null;
        boolean refNameType = false;

        // Generate a XML XSDSchema type depending on the content patterns
        eu.fox7.schematoolkit.xsd.om.Type resultType = null;

        // Collect all information-tags about the element, which is the parent pattern
        if (this.patternInformation.get(element) != null) {
            currentPatternInformation.addAll(this.patternInformation.get(element));
        }
        LinkedList<Pattern> patternList = element.getPatterns();
        for (Pattern pattern: patternList) {
            // Collect all information-tags about the current pattern
            if (this.patternInformation.get(pattern) != null && !(pattern instanceof Element) && !(pattern instanceof Attribute)) {
                currentPatternInformation.addAll(this.patternInformation.get(pattern));
            }
            // Collect patterns with element-tags
            if ((this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element")) || pattern instanceof Element) {
                patternsContainingElement.add(pattern);
            }
            // Collect patterns with attribute-tags
            if ((this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("attribute")) || pattern instanceof Attribute) {
                patternsContainingAttribute.add(pattern);
            }
            // Collect patterns with data- or value-tags
            if (((this.patternInformation.get(pattern) != null && (this.patternInformation.get(pattern).contains("data") || this.patternInformation.get(pattern).contains("value"))) || pattern instanceof Data || pattern instanceof Value) && (!(pattern instanceof Element) && !(pattern instanceof Attribute))) {
                if (patternContainingData == null) 
                	patternContainingData = pattern;
                else
                	throw new TooManyDataOrValuePatternsUnderAnElementException(element, "Type: " + localTypeName);
            }
        }

        Namespace xsdNamespace = element.getAttributeNamespace();
        QualifiedName typeName = new QualifiedName(xsdNamespace,localTypeName);
        
        if (patternsContainingElement.isEmpty() && patternsContainingAttribute.isEmpty() && patternContainingData==null && currentPatternInformation.contains("notAllowed")) {

            /*******************************************************************
             * Case: element: not allowed
             *       --> null (no type)
             ******************************************************************/
            resultType = null;

        } else if (patternsContainingElement.isEmpty() && patternsContainingAttribute.isEmpty() && patternContainingData==null && currentPatternInformation.contains("text")) {

            /*******************************************************************
             * Case: element with only text content
             *       --> SimpleType: String
             ******************************************************************/
            Namespace namespace = this.xmlSchema.getNamespaceByURI(XSDSchema.XMLSCHEMA_NAMESPACE);

            resultType = new SimpleType(new QualifiedName(namespace, "string"), null, false);

        } else if (patternsContainingElement.isEmpty() && patternsContainingAttribute.isEmpty() && patternContainingData==null && currentPatternInformation.contains("empty")) {

            /*******************************************************************
             * Case: element with empty content
             *       --> ComplexType: empty
             ******************************************************************/

            resultType = new ComplexType(typeName, null, true);

        } else if (patternsContainingElement.isEmpty() && patternsContainingAttribute.isEmpty() && patternContainingData!=null) {

            /*******************************************************************
             * Case: element without element or attribute content,
             *       but with simpleType content
             *       --> SimpleType
             ******************************************************************/
            SimpleType simpleType = null;
            simpleType = this.patternSimpleTypeConverter.generateSimpleTypeForPattern(patternContainingData, new LinkedList<Pattern>());

            resultType = simpleType;

        } else if (patternsContainingElement.isEmpty() && !patternsContainingAttribute.isEmpty() && patternContainingData!=null) {

            /*******************************************************************
             * Case: element with attribute content and simpleType content
             *       --> ComplexType with SimpleContent
             *           --> Attributes into extension,
             *           --> simpleType as base of extension
             ******************************************************************/
            SimpleType simpleType = null;
            SimpleContentExtension simpleContentExtension = null;

            simpleType = this.patternSimpleTypeConverter.generateSimpleTypeForPattern(patternContainingData, new LinkedList<Pattern>());

            SimpleContentType simpleContentType = null;

            if (simpleType != null) {
            	if (!simpleType.getName().equals(XSDSchema.XMLSCHEMA_NAMESPACE)) {
            		simpleType.setIsAnonymous(false);
            		this.xmlSchema.addType(simpleType);
            	}
            } else {
            	// Exception: There is an unexpected null simpleType under the given element
            	throw new SimpleTypeIsNullException(element, "Type: " + localTypeName);
            }
            simpleContentExtension = new SimpleContentExtension(simpleType.getName());
            simpleContentType = new SimpleContentType();
            simpleContentType.setInheritance(simpleContentExtension);

            ComplexType complexType = new ComplexType(typeName, simpleContentType, true);

            // handle attribute content conversion
            LinkedList<eu.fox7.schematoolkit.common.AttributeParticle> attributeList = this.patternAttributeConverter.convertPatternListToAttributeParticleList(patternsContainingAttribute);

            for (AttributeParticle attributeParticle: attributeList) {
                if (simpleContentExtension != null) {
                    simpleContentExtension.addAttribute(attributeParticle);
                } else {
                    complexType.addAttribute(attributeParticle);
                }
            }

            resultType = complexType;

        } else if (patternsContainingElement.isEmpty() && !patternsContainingAttribute.isEmpty() && patternContainingData==null) {

            /*******************************************************************
             * Case: no elements, but attributes without simpleType content
             *       --> ComplexType with attributes
             ******************************************************************/

            ComplexType complexType = new ComplexType(typeName, null, true);

            // handle mixed/text content conversion
            if (currentPatternInformation.contains("mixed") || currentPatternInformation.contains("text") || patternContainingData!=null) {
                complexType.setMixed(true);
            }

            // handle attribute content conversion
            LinkedList<eu.fox7.schematoolkit.common.AttributeParticle> attributeList = this.patternAttributeConverter.convertPatternListToAttributeParticleList(patternsContainingAttribute);

            for (Iterator<AttributeParticle> it = attributeList.iterator(); it.hasNext();) {
                AttributeParticle attributeParticle = it.next();
                if (attributeParticle != null) {
                    complexType.addAttribute(attributeParticle);
                }
            }

            if (!complexType.getAttributes().isEmpty()) {
                resultType = complexType;
            } else {
                resultType = null;
            }

        } else if (!patternsContainingElement.isEmpty()) {

            /*******************************************************************
             * Case: element with element (and attribute) content
             *       --> ComplexType
             ******************************************************************/
            LinkedList<Pattern> typePattern = new LinkedList<Pattern>(patternsContainingElement);

            // Check if there is a ref that can be used as standalone type
            HashMap<Ref, LinkedList<Pattern>> typeMap = checkPatternListForRefForComplexType(typePattern);

            ComplexType refComplexType = null;

            if (typeMap != null && !typeMap.isEmpty() && typeMap.keySet() != null) {
                // Case: There is a ref pattern that can be converted as a XML XSDSchema complexType
                Ref ref = typeMap.keySet().iterator().next();
                LinkedList<Pattern> remainingPatterns = new LinkedList<Pattern>(typeMap.get(ref));

                // Generate the type for the given ref recursivly
                refComplexType = generateComplexTypeForRefRecursive(ref);

                refNameType = true;

                // Determine the pattern containing the found ref pattern
                Pattern patternWithRef = null;
                for (Iterator<Pattern> it = typePattern.iterator(); it.hasNext();) {
                    Pattern pattern = it.next();
                    if (!remainingPatterns.contains(pattern)) {
                        patternWithRef = pattern;
                        break;
                    }
                }

                // Remove the found "ref-parent-pattern" from all remaining pattern sets
                if (patternWithRef != null) {
                    LinkedList<Pattern> temp = new LinkedList<Pattern>();
                    temp.add(patternWithRef);
                    patternsContainingAttribute.removeAll(temp);
//                    patternsContainingData.removeAll(temp);
                }
                patternsContainingElement = remainingPatterns;
            }

            // Case: Normal case, but with advertence to the possibly generated baseType
            ComplexType complexType = null;
            boolean mixed = false;
            eu.fox7.schematoolkit.common.Particle xsdParticle = null;
            LinkedList<eu.fox7.schematoolkit.common.AttributeParticle> attributeList = null;

            if ((refNameType && (!patternsContainingElement.isEmpty() || !patternsContainingAttribute.isEmpty() || patternContainingData!=null)) || !refNameType) {

                complexType = new ComplexType(typeName, null, true);

                // handle attribute content conversion
                attributeList = this.patternAttributeConverter.convertPatternListToAttributeParticleList(patternsContainingAttribute);

                if (!refNameType) {
                    for (Iterator<AttributeParticle> it = attributeList.iterator(); it.hasNext();) {
                        AttributeParticle attributeParticle = it.next();
                        if (attributeParticle != null) {
                            complexType.addAttribute(attributeParticle);
                        }
                    }
                }

                // handle element content conversion

                if (patternsContainingElement.size() > 1) {
                    SequencePattern sequencePattern = new SequencePattern();
                    for (Iterator<Pattern> it = patternsContainingElement.iterator(); it.hasNext();) {
                        Pattern currentElementContentPattern = it.next();
                        addParticleToSequencePattern(sequencePattern, convertPatternToElementParticleStructure(currentElementContentPattern));
                    }
                    if (!sequencePattern.getParticles().isEmpty()) {
                        xsdParticle = sequencePattern;
                    }

                } else if (patternsContainingElement.size() == 1) {
                    eu.fox7.schematoolkit.common.Particle resultParticle = convertPatternToElementParticleStructure(patternsContainingElement.iterator().next());
                    if (resultParticle != null) {
                        if (resultParticle instanceof eu.fox7.schematoolkit.xsd.om.Element || resultParticle instanceof eu.fox7.schematoolkit.common.ElementRef) {
                            SequencePattern sequencePattern = new SequencePattern();
                            sequencePattern.addParticle(resultParticle);
                            if (!sequencePattern.getParticles().isEmpty()) {
                                xsdParticle = sequencePattern;
                            }
                        } else {
                            xsdParticle = resultParticle;
                        }
                    }
                }

                // Determine if the element is within a (XOR) choice with Attributes or simpleType content
                if (currentPatternInformation.contains("optional") && xsdParticle != null) {
                    if (xsdParticle instanceof CountingPattern) {
                        CountingPattern countingPattern = (CountingPattern) xsdParticle;
                        if (countingPattern.getMin() != 0) {
                            xsdParticle =  new CountingPattern(countingPattern.getParticle(),0, countingPattern.getMax());
                        }
                    } else {
                        xsdParticle = new CountingPattern(xsdParticle, 0, 1);
                    }
                }

                // handle mixed/text content conversion
                if (currentPatternInformation.contains("mixed") || currentPatternInformation.contains("text") || patternContainingData!=null) {
                    mixed = true;
                }

                // Adding the element particles to the content of the refComplexType
                if (!refNameType) {
                    if (xsdParticle != null) {
                        ComplexContentType complexContentType = new ComplexContentType(xsdParticle, mixed);
                        eu.fox7.schematoolkit.common.Particle myParticle = null;
                        if (xsdParticle instanceof CountingPattern) {
                            myParticle = ((CountingPattern) xsdParticle).getParticle();
                        } else {
                            myParticle = xsdParticle;
                        }
                        if (!(myParticle instanceof SequencePattern) && !(myParticle instanceof ChoicePattern) && !(myParticle instanceof AllPattern) && !(myParticle instanceof GroupReference)) {
                            SequencePattern newSequencePattern = new SequencePattern();
                            newSequencePattern.addParticle(xsdParticle);
                            complexContentType.setParticle(newSequencePattern);
                        } else {
                            complexContentType.setParticle(xsdParticle);
                        }
                        complexType.setContent(complexContentType);
                    }
                } else {
                    // Extend ComplexType with attributes
                    complexType.setIsAnonymous(false);
                    ComplexContentExtension complexContentExtension = new ComplexContentExtension(typeName);

                    for (AttributeParticle attributeParticle: attributeList) {
                        if (attributeParticle != null) {
                            complexContentExtension.addAttribute(attributeParticle);
                        }
                    }

                    ComplexContentType complexContentType = new ComplexContentType(xsdParticle, complexContentExtension, mixed);
                    eu.fox7.schematoolkit.common.Particle myParticle = null;

                    if (xsdParticle instanceof CountingPattern) {
                        myParticle = ((CountingPattern) xsdParticle).getParticle();
                    } else {
                        myParticle = xsdParticle;
                    }
                    if (xsdParticle != null && !(myParticle instanceof SequencePattern) && !(myParticle instanceof ChoicePattern) && !(myParticle instanceof AllPattern) && !(myParticle instanceof GroupReference)) {
                        SequencePattern newSequencePattern = new SequencePattern();
                        newSequencePattern.addParticle(xsdParticle);
                        complexContentType.setParticle(newSequencePattern);
                    } else {
                        complexContentType.setParticle(xsdParticle);
                    }
                    complexType.setContent(complexContentType);
                }
            } else {
            	// Ref-type case: ref-type is the used type
            	complexType = refComplexType;
            }

            if (mixed == true || xsdParticle != null || (attributeList != null && !attributeList.isEmpty()) || (complexType != null)) {
                resultType = complexType;
            } else {
                resultType = null;
            }

        } else if (patternsContainingElement.isEmpty() && patternsContainingAttribute.isEmpty() && patternContainingData==null) {

            /*******************************************************************
             * Case: no elements, no attributes and no simpleType content
             *       --> null (no type)
             ******************************************************************/
            resultType = null;
        }

        QualifiedName fullQualifiedTypeName = null;

        if (resultType != null) {
            fullQualifiedTypeName = resultType.getName();
            if (resultType instanceof ComplexType) {
                ComplexType complexType = (ComplexType) resultType;
                if (!refNameType) {
                    complexType.setIsAnonymous(true);
                }
            }
            this.xmlSchema.addType(resultType);
        }

        return resultType;
    }

    /**
     * Convert a RELAX NG pattern to the corresponding XML XSDSchema particle structure
     * @param relaxNGPattern        basis of the conversion
     * @return eu.fox7.schematoolkit.common.Particle      result of the conversion
     * @throws Exception
     */
    private eu.fox7.schematoolkit.common.Particle convertPatternToElementParticleStructure(Pattern relaxNGPattern) throws Exception {
        eu.fox7.schematoolkit.common.Particle resultParticle = null;
        if (relaxNGPattern != null && this.patternInformation.get(relaxNGPattern) != null && this.patternInformation.get(relaxNGPattern).contains("element") || relaxNGPattern instanceof Element) {
            if (relaxNGPattern instanceof RelaxNGCountingPattern) {

                // Case: CountingPattern

                RelaxNGCountingPattern countingPattern = (RelaxNGCountingPattern) relaxNGPattern;
                eu.fox7.schematoolkit.common.Particle innerParticle = null;
                
                LinkedList<Pattern> countingElementPatterns = new LinkedList<Pattern>();

                for (Pattern pattern: countingPattern.getPatterns()) {
                    if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                        countingElementPatterns.add(pattern);
                    }
                }

                if (countingElementPatterns.size() > 1) {
                    SequencePattern sequencePattern = new SequencePattern();
                    for (Pattern currentElementContentPattern: countingElementPatterns) {
                        Particle tempParticle = convertPatternToElementParticleStructure(currentElementContentPattern);
                        if (tempParticle != null) {
                            addParticleToSequencePattern(sequencePattern, tempParticle);
                        }
                    }

                    if (!sequencePattern.getParticles().isEmpty()) {
                        innerParticle = sequencePattern;
                    }
                } else if (countingElementPatterns.size() == 1) {
                    Particle tempParticle = convertPatternToElementParticleStructure(countingElementPatterns.getFirst());
                    if (tempParticle != null) {
                        innerParticle = tempParticle;
                    }
                }

                if (innerParticle!=null) {
                    resultParticle = new CountingPattern(innerParticle,countingPattern.getMinOccurs(),countingPattern.getMaxOccurs());
                }
            } else if (relaxNGPattern instanceof Element) {

                // Case: Element

                Element innerElement = (Element) relaxNGPattern;

                if (this.recursiveElements.contains(innerElement)) {
                	// Handle recursive elements

                	eu.fox7.schematoolkit.common.Particle elementParticle;
                	if (this.alreadyConvertedElements.get(innerElement) != null) {
                		elementParticle = this.alreadyConvertedElements.get(innerElement);
                	} else {
                		if (!(this.patternInformation.get(innerElement) != null && this.patternInformation.get(innerElement).contains("notAllowed"))) {
                			elementParticle=convertElement(innerElement);
                		}
                	}

                	resultParticle = elementParticle;

                } else {
                    if (!(this.patternInformation.get(innerElement) != null && this.patternInformation.get(innerElement).contains("notAllowed"))) {
                        resultParticle = convertElement(innerElement);
                    }
                }

                if (this.patternInformation.get(innerElement) != null && this.patternInformation.get(innerElement).contains("optional")) {
                    resultParticle = new CountingPattern(resultParticle, 0, 1);
                }


            } else if (relaxNGPattern instanceof AbstractRef) {

                // Case: Ref

                AbstractRef ref = (AbstractRef) relaxNGPattern;

                if (RelaxNG2XSDConverter.REF_TO_GROUP_CONVERSION && this.refToGroupMap.get(ref.getUniqueRefID()) != null) {
                    GroupReference groupRef = new GroupReference(this.refToGroupMap.get(ref.getUniqueRefID()));
                    resultParticle = groupRef;
                } else {
                    java.util.List<Define> defineList = ref.getDefineList();
                    CombineMethod combineMethod = null;

                    LinkedList<Pattern> definePatterns = new LinkedList<Pattern>();

                    for (Define define: defineList) {
                        if (define.getCombineMethod() != null) {
                            combineMethod = define.getCombineMethod();
                        }

                        LinkedList<Pattern> defineElementPatterns = new LinkedList<Pattern>();

                        for (Iterator<Pattern> it1 = define.getPatterns().iterator(); it1.hasNext();) {
                            Pattern pattern = it1.next();
                            if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                                defineElementPatterns.add(pattern);
                            }
                        }

                        if (defineElementPatterns.size() > 1) {

                            // Add all patterns of one define to a group (this is a sequence in XSD)
                            // for the correct handling of the combine-method of the define-elements
                            Group group = new Group();
                            for (Iterator<Pattern> it2 = defineElementPatterns.iterator(); it2.hasNext();) {
                                Pattern innerPattern = it2.next();
                                group.addPattern(innerPattern);
                            }
                            definePatterns.add(group);

                            PatternInformationCollector pdc = new PatternInformationCollector(this.relaxng);
                            HashSet<String> dataForNewPattern = pdc.getDataForPattern(group);
                            this.patternInformation.put(group, dataForNewPattern);

                        } else if (defineElementPatterns.size() == 1) {
                            definePatterns.add(defineElementPatterns.getFirst());
                        }
                    }



                    if (definePatterns.size() > 1) {
                    	ParticleContainer combineParticle = null;

                        LinkedList<Particle> conversionResults = new LinkedList<Particle>();
                        boolean allPatternAllowed = true;


                        for (Iterator<Pattern> it = definePatterns.iterator(); it.hasNext();) {
                            Pattern currentElementContentPattern = it.next();
                            Particle tempParticle = convertPatternToElementParticleStructure(currentElementContentPattern);
                            if (tempParticle != null) {
                                if (!RelaxNG2XSDConverter.INTERLEAVE_APPROXIMATION_OFF) {
                                    if (!(tempParticle instanceof eu.fox7.schematoolkit.xsd.om.Element) && !(tempParticle instanceof eu.fox7.schematoolkit.common.ElementRef) && !(tempParticle instanceof eu.fox7.schematoolkit.common.CountingPattern)) {
                                        allPatternAllowed = false;
                                    }

                                    if (tempParticle instanceof eu.fox7.schematoolkit.common.CountingPattern) {
                                        CountingPattern countingPattern = (CountingPattern) tempParticle;

                                        if (countingPattern.getMin() != 0 && countingPattern.getMin() != 1) {
                                            allPatternAllowed = false;
                                        }

                                        if (countingPattern.getMax() == null || (countingPattern.getMax() != null && countingPattern.getMax() != 0 && countingPattern.getMax() != 1)) {
                                            allPatternAllowed = false;
                                        }
                                    }
                                }
                                conversionResults.add(tempParticle);
                            }
                        }
                        if (!conversionResults.isEmpty()) {
                            if (combineMethod != null && combineMethod.equals(CombineMethod.choice)) {
                                combineParticle = new ChoicePattern();
                            } else if (combineMethod != null && combineMethod.equals(CombineMethod.interleave) && allPatternAllowed) {
                                combineParticle = new AllPattern();
                            } else if (combineMethod != null && combineMethod.equals(CombineMethod.interleave) && !allPatternAllowed) {
                                combineParticle = new ChoicePattern();
                            } else {
                                // Exception: There is no combineMethod defined for the current ref.
                                throw new NoCombineMethodException("ref: " + ref.getRefName());
                            }

                            for (Particle currentParticle: conversionResults) {
                                if (combineParticle instanceof ChoicePattern) {
                                    ChoicePattern tempChoicePattern = (ChoicePattern) combineParticle;
                                    addParticleToChoicePattern(tempChoicePattern, currentParticle);
                                } else if (combineParticle instanceof AllPattern) {
                                    AllPattern tempAllPattern = (AllPattern) combineParticle;
                                    tempAllPattern.addParticle(currentParticle);
                                }
                            }

                            if (combineMethod != null && combineMethod.equals(CombineMethod.interleave) && !allPatternAllowed) {
                                resultParticle = new CountingPattern(combineParticle,0, null);
                            } else {
                                resultParticle = combineParticle;
                            }
                        }
                    } else if (definePatterns.size() == 1) {
                        Particle tempParticle = convertPatternToElementParticleStructure(definePatterns.getFirst());
                        if (tempParticle != null) {
                            resultParticle = tempParticle;
                        }
                    }

                    if (RelaxNG2XSDConverter.REF_TO_GROUP_CONVERSION && resultParticle != null) {
                        // Check if it is useful to build a XML XSDSchema group from a given Define.
                        if (this.isDefinePatternListUsefulAsXSDGroup(definePatterns)) {
                        	eu.fox7.schematoolkit.xsd.om.Group strGroup = this.putParticleIntoGroup(ref.getDefaultNamespace(), ref.getRefName(), resultParticle);
                        	if (strGroup != null) {
                        		GroupReference groupRef = new GroupReference(strGroup.getName());
                        		this.refToGroupMap.put(ref.getUniqueRefID(), strGroup.getName());
                        		resultParticle = groupRef;
                        	}
                        }
                    } 
                }
            } else if (relaxNGPattern instanceof Group) {

                // Case: Group

                Group group = (Group) relaxNGPattern;

                SequencePattern sequencePattern = new SequencePattern();

                LinkedList<Pattern> groupElementPatterns = new LinkedList<Pattern>();

                for (Pattern pattern: group.getPatterns()) {
                    if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                        groupElementPatterns.add(pattern);
                    }
                }

                for (Pattern currentElementContentPattern: groupElementPatterns) {
                	Particle tempParticle = convertPatternToElementParticleStructure(currentElementContentPattern);
                	if (tempParticle != null) {
                		addParticleToSequencePattern(sequencePattern, tempParticle);
                	}
                }
                if (!sequencePattern.getParticles().isEmpty()) {
                	if (sequencePattern.getParticles().size()>1)
                		resultParticle = sequencePattern;
                	else 
                		resultParticle = sequencePattern.getParticles().getFirst();
                }

            }  else if (relaxNGPattern instanceof Choice) {

                // Case: Choice

                Choice choice = (Choice) relaxNGPattern;

                LinkedList<Pattern> choiceElementPatterns = new LinkedList<Pattern>();

                for (Iterator<Pattern> it1 = choice.getPatterns().iterator(); it1.hasNext();) {
                    Pattern pattern = it1.next();
                    if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                        choiceElementPatterns.add(pattern);
                    }
                }

                ChoicePattern choicePattern = new ChoicePattern();

                for (Iterator<Pattern> it = choiceElementPatterns.iterator(); it.hasNext();) {
                	Pattern currentElementContentPattern = it.next();
                	Particle tempParticle = convertPatternToElementParticleStructure(currentElementContentPattern);
                	if (tempParticle != null) {
                		addParticleToChoicePattern(choicePattern, tempParticle);
                	}
                }
                if (!choicePattern.getParticles().isEmpty()) {
                	if (choicePattern.getParticles().size()>1)
                		resultParticle = choicePattern;
                	else
                		resultParticle = choicePattern.getParticles().getFirst();
                }

            } else if (relaxNGPattern instanceof Interleave) {

                // Case: Interleave

                Interleave interleave = (Interleave) relaxNGPattern;

                LinkedList<Pattern> interleaveElementPatterns = new LinkedList<Pattern>();

                for (Iterator<Pattern> it1 = interleave.getPatterns().iterator(); it1.hasNext();) {
                    Pattern pattern = it1.next();
                    if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                        interleaveElementPatterns.add(pattern);
                    }
                }

                if (interleaveElementPatterns.size() > 1) {

                    eu.fox7.schematoolkit.common.Particle combineParticle = null;

                    LinkedList<Particle> conversionResults = new LinkedList<Particle>();
                    boolean allPatternAllowed = true;

                    for (Iterator<Pattern> it = interleaveElementPatterns.iterator(); it.hasNext();) {
                        Pattern currentElementContentPattern = it.next();
                        Particle tempParticle = convertPatternToElementParticleStructure(currentElementContentPattern);
                        if (!RelaxNG2XSDConverter.INTERLEAVE_APPROXIMATION_OFF) {
                            if (!(tempParticle instanceof eu.fox7.schematoolkit.xsd.om.Element) && !(tempParticle instanceof eu.fox7.schematoolkit.common.ElementRef) && !(tempParticle instanceof eu.fox7.schematoolkit.common.CountingPattern)) {
                                allPatternAllowed = false;
                            }

                            if (tempParticle instanceof eu.fox7.schematoolkit.common.CountingPattern) {
                                CountingPattern countingPattern = (CountingPattern) tempParticle;

                                if (countingPattern.getMin() != 0 && countingPattern.getMin() != 1) {
                                    allPatternAllowed = false;
                                }

                                if (countingPattern.getMax() == null || (countingPattern.getMax() != null && countingPattern.getMax() != 0 && countingPattern.getMax() != 1)) {
                                    allPatternAllowed = false;
                                }
                            }
                        }
                        if (tempParticle != null) {
                            conversionResults.add(tempParticle);
                        }
                    }
                    if (!conversionResults.isEmpty()) {
                        if (allPatternAllowed) {
                            combineParticle = new AllPattern();
                        } else {
                            combineParticle = new ChoicePattern();
                        }

                        for (Iterator<Particle> it = conversionResults.iterator(); it.hasNext();) {
                            Particle currentParticle = it.next();

                            if (combineParticle instanceof ChoicePattern) {
                                ChoicePattern tempChoicePattern = (ChoicePattern) combineParticle;
                                addParticleToChoicePattern(tempChoicePattern, currentParticle);
                            } else if (combineParticle instanceof AllPattern) {
                                AllPattern tempAllPattern = (AllPattern) combineParticle;
                                tempAllPattern.addParticle(currentParticle);
                            }
                        }

                        if (!allPatternAllowed) {
                            CountingPattern countingPattern = new CountingPattern(combineParticle,0, null);
                            resultParticle = countingPattern;
                        } else {
                            resultParticle = combineParticle;
                        }
                    }
                }
            } else if (relaxNGPattern instanceof Mixed) {

                // Case: Mixed

                Mixed mixed = (Mixed) relaxNGPattern;

                LinkedList<Pattern> mixedElementPatterns = new LinkedList<Pattern>();

                for (Iterator<Pattern> it1 = mixed.getPatterns().iterator(); it1.hasNext();) {
                    Pattern pattern = it1.next();
                    if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                        mixedElementPatterns.add(pattern);
                    }
                }

                if (mixedElementPatterns.size() > 1) {
                    SequencePattern sequencePattern = new SequencePattern();

                    for (Iterator<Pattern> it = mixedElementPatterns.iterator(); it.hasNext();) {
                        Pattern currentElementContentPattern = it.next();
                        Particle tempParticle = convertPatternToElementParticleStructure(currentElementContentPattern);
                        if (tempParticle != null) {
                            addParticleToSequencePattern(sequencePattern, tempParticle);
                        }
                    }
                    if (!sequencePattern.getParticles().isEmpty()) {
                        resultParticle = sequencePattern;
                    }
                } else if (mixedElementPatterns.size() == 1) {
                    Particle tempParticle = convertPatternToElementParticleStructure(mixedElementPatterns.getFirst());
                    if (tempParticle != null) {
                        resultParticle = tempParticle;
                    }
                }

            } else if (relaxNGPattern instanceof Grammar) {

                // Case: Grammar

                Grammar grammar = (Grammar) relaxNGPattern;

                this.findRecursiveElementsInGrammar(grammar);
 
                LinkedList<Pattern> grammarElementPatterns = new LinkedList<Pattern>();

                for (Iterator<Pattern> it1 = grammar.getStartPatterns().iterator(); it1.hasNext();) {
                    Pattern pattern = it1.next();
                    if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                        grammarElementPatterns.add(pattern);
                    }
                }
                CombineMethod combineMethod = grammar.getStartCombineMethod();

                LinkedList<Particle> conversionResults = new LinkedList<Particle>();
                boolean allPatternAllowed = true;

                if (grammarElementPatterns.size() > 1) {
                    eu.fox7.schematoolkit.common.Particle combineParticle = null;
                    for (Iterator<Pattern> it = grammarElementPatterns.iterator(); it.hasNext();) {
                        Pattern currentElementContentPattern = it.next();
                        Particle tempParticle = convertPatternToElementParticleStructure(currentElementContentPattern);
                        if (tempParticle != null) {
                            if (!RelaxNG2XSDConverter.INTERLEAVE_APPROXIMATION_OFF) {
                                if (!(tempParticle instanceof eu.fox7.schematoolkit.xsd.om.Element) && !(tempParticle instanceof eu.fox7.schematoolkit.common.ElementRef) && !(tempParticle instanceof eu.fox7.schematoolkit.common.CountingPattern)) {
                                    allPatternAllowed = false;
                                }

                                if (tempParticle instanceof eu.fox7.schematoolkit.common.CountingPattern) {
                                    CountingPattern countingPattern = (CountingPattern) tempParticle;

                                    if (countingPattern.getMin() != 0 && countingPattern.getMin() != 1) {
                                        allPatternAllowed = false;
                                    }

                                    if (countingPattern.getMax() == null || (countingPattern.getMax() != null && countingPattern.getMax() != 0 && countingPattern.getMax() != 1)) {
                                        allPatternAllowed = false;
                                    }
                                }
                            }
                            conversionResults.add(tempParticle);
                        }
                    }
                    if (!conversionResults.isEmpty()) {
                        if (combineMethod != null && combineMethod.equals(CombineMethod.choice)) {
                            combineParticle = new ChoicePattern();
                        } else if (combineMethod != null && combineMethod.equals(CombineMethod.interleave) && allPatternAllowed) {
                            combineParticle = new AllPattern();
                        } else if (combineMethod != null && combineMethod.equals(CombineMethod.interleave) && !allPatternAllowed) {
                            combineParticle = new ChoicePattern();
                        } else {
                            // Exception: There is no combineMethod defined for the current pef.
                            throw new NoCombineMethodException("grammar: " + "start pattern");
                        }

                        for (Iterator<Particle> it = conversionResults.iterator(); it.hasNext();) {
                            Particle currentParticle = it.next();

                            if (combineParticle instanceof ChoicePattern) {
                                ChoicePattern tempChoicePattern = (ChoicePattern) combineParticle;
                                addParticleToChoicePattern(tempChoicePattern, currentParticle);
                            } else if (combineParticle instanceof AllPattern) {
                                AllPattern tempAllPattern = (AllPattern) combineParticle;
                                tempAllPattern.addParticle(currentParticle);
                            }
                        }

                        if (combineMethod != null && combineMethod.equals(CombineMethod.interleave) && !allPatternAllowed) {
                        	resultParticle = new CountingPattern(combineParticle, 0, null);
                        } else {
                            resultParticle = combineParticle;
                        }
                    }
                } else if (grammarElementPatterns.size() == 1) {
                    Particle tempParticle = convertPatternToElementParticleStructure(grammarElementPatterns.getFirst());
                    if (tempParticle != null) {
                        resultParticle = tempParticle;
                    }
                }
            }
        }
        return resultParticle;
    }

    private QualifiedName newGroupName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
     * Find recursive elements in a given grammar
     *
     * Follow the pattern structure via a dfs like algorithm.
     * If a circle is found, all elements on this circle are recursive elements.
     *
     * @param grammar
     */
    private void findRecursiveElementsInGrammar(Grammar grammar) throws RecursionWithoutElementException {
    	for (Collection<Define> defineList: grammar.getDefinedPatterns()) {
    		for (Define define: defineList) {
    			LinkedList<Object> currentParents = new LinkedList<Object>();
    			currentParents.add(define.getName());
    			for (Pattern currentPattern: define.getPatterns()) {
    				this.findRecursiveElementsInPattern(currentPattern, currentParents);
    			}
    		}
    	}
    }

    /**
     * Recursive method for finding all recursive elements in a given RELAX NG pattern
     * @param relaxNGPattern        source for the search
     * @param currentParents        perceive information about the path in the pattern structure to avoid infinit loops
     */
    private void findRecursiveElementsInPattern(Pattern relaxNGPattern, LinkedList<Object> currentParents) throws RecursionWithoutElementException {

        if (relaxNGPattern != null) {
            if (this.patternInformation.get(relaxNGPattern) != null && this.patternInformation.get(relaxNGPattern).contains("element") || relaxNGPattern instanceof Element) {
                if (relaxNGPattern instanceof ZeroOrMore) {

                    // Case: ZeroOrMore

                    ZeroOrMore zeroOrMore = (ZeroOrMore) relaxNGPattern;
                    // System.out.println("ZeroOrMore");
                    LinkedList<Pattern> zeroOrMoreElementPatterns = new LinkedList<Pattern>();
                    for (Iterator<Pattern> it1 = zeroOrMore.getPatterns().iterator(); it1.hasNext();) {
                        Pattern pattern = it1.next();
                        if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                            zeroOrMoreElementPatterns.add(pattern);
                        }
                    }
                    if (zeroOrMoreElementPatterns.size() > 0) {
                        for (Pattern currentElementContentPattern: zeroOrMoreElementPatterns) {
                            findRecursiveElementsInPattern(currentElementContentPattern, currentParents);
                        }
                    }
                } else if (relaxNGPattern instanceof Element) {

                    // Case: Element

                    Element innerElement = (Element) relaxNGPattern;
                    // System.out.println("Element: " + (innerElement).getNameAttribute());

                    currentParents.add(innerElement);

                    LinkedList<Pattern> elementPatterns = new LinkedList<Pattern>();
                    for (Iterator<Pattern> it1 = innerElement.getPatterns().iterator(); it1.hasNext();) {
                        Pattern pattern = it1.next();
                        if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                            elementPatterns.add(pattern);
                        }
                    }
                    if (elementPatterns.size() > 0) {
                        for (Iterator<Pattern> it = elementPatterns.iterator(); it.hasNext();) {
                            Pattern currentElementContentPattern = it.next();
                            findRecursiveElementsInPattern(currentElementContentPattern, currentParents);
                        }
                    }
                } else if (relaxNGPattern instanceof Ref) {

                    // Case: Ref

                    Ref ref = (Ref) relaxNGPattern;
                    // System.out.println("Ref: " + ref.getRefName());

                    if (!currentParents.contains(ref.getUniqueRefID())) {
                        currentParents.add(ref.getUniqueRefID());
                        LinkedList<Pattern> defineElementPatterns = new LinkedList<Pattern>();

                        for (Iterator<Define> it = ref.getDefineList().iterator(); it.hasNext();) {
                            Define define = it.next();
                            for (Iterator<Pattern> it1 = define.getPatterns().iterator(); it1.hasNext();) {
                                Pattern pattern = it1.next();
                                if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                                    defineElementPatterns.add(pattern);
                                }
                            }
                        }
                        if (defineElementPatterns.size() > 0) {
                            for (Iterator<Pattern> it = defineElementPatterns.iterator(); it.hasNext();) {
                                Pattern currentElementContentPattern = it.next();
                                findRecursiveElementsInPattern(currentElementContentPattern, currentParents);
                            }
                        }
                    } else {
                        // System.out.println("Define already seen: " + ref.getRefName());

                        int firstPositionOfDefineNameInParentList = currentParents.indexOf(ref.getRefName());
                        int countElementsInLoop = 0;
                        for (int i = currentParents.size() - 1; i > firstPositionOfDefineNameInParentList; i--) {
                            Object currentParentObject = currentParents.get(i);
                            if (currentParentObject instanceof Element) {
                                recursiveElements.add((Element) currentParentObject);
                                countElementsInLoop++;
                            }
                        }

                        this.refToGroupMap.put(ref.getUniqueRefID(), null);

                        if (countElementsInLoop < 1) {
                            throw new RecursionWithoutElementException();
                        }

                    }
                } else if (relaxNGPattern instanceof Group) {

                    // Case: Group

                    Group group = (Group) relaxNGPattern;
                    // System.out.println("Group");
                    LinkedList<Pattern> groupElementPatterns = new LinkedList<Pattern>();

                    for (Iterator<Pattern> it1 = group.getPatterns().iterator(); it1.hasNext();) {
                        Pattern pattern = it1.next();
                        if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                            groupElementPatterns.add(pattern);
                        }
                    }
                    if (groupElementPatterns.size() > 0) {
                        for (Iterator<Pattern> it = groupElementPatterns.iterator(); it.hasNext();) {
                            Pattern currentElementContentPattern = it.next();
                            findRecursiveElementsInPattern(currentElementContentPattern, currentParents);
                        }
                    }
                } else if (relaxNGPattern instanceof OneOrMore) {

                    // Case: OneOrMore

                    OneOrMore oneOrMore = (OneOrMore) relaxNGPattern;
                    // System.out.println("OneOrMore");
                    LinkedList<Pattern> oneOrMoreElementPatterns = new LinkedList<Pattern>();

                    for (Iterator<Pattern> it1 = oneOrMore.getPatterns().iterator(); it1.hasNext();) {
                        Pattern pattern = it1.next();
                        if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                            oneOrMoreElementPatterns.add(pattern);
                        }
                    }
                    if (oneOrMoreElementPatterns.size() > 0) {
                        for (Iterator<Pattern> it = oneOrMoreElementPatterns.iterator(); it.hasNext();) {
                            Pattern currentElementContentPattern = it.next();
                            findRecursiveElementsInPattern(currentElementContentPattern, currentParents);
                        }
                    }
                } else if (relaxNGPattern instanceof Optional) {

                    // Case: Optional

                    Optional optional = (Optional) relaxNGPattern;
                    // System.out.println("Optional");
                    LinkedList<Pattern> optionalElementPatterns = new LinkedList<Pattern>();

                    for (Iterator<Pattern> it1 = optional.getPatterns().iterator(); it1.hasNext();) {
                        Pattern pattern = it1.next();
                        if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                            optionalElementPatterns.add(pattern);
                        }
                    }
                    if (optionalElementPatterns.size() > 0) {
                        for (Iterator<Pattern> it = optionalElementPatterns.iterator(); it.hasNext();) {
                            Pattern currentElementContentPattern = it.next();
                            findRecursiveElementsInPattern(currentElementContentPattern, currentParents);
                        }
                    }
                } else if (relaxNGPattern instanceof Choice) {

                    // Case: Choice

                    Choice choice = (Choice) relaxNGPattern;
                    // System.out.println("Choice");
                    LinkedList<Pattern> choiceElementPatterns = new LinkedList<Pattern>();

                    for (Iterator<Pattern> it1 = choice.getPatterns().iterator(); it1.hasNext();) {
                        Pattern pattern = it1.next();
                        if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                            choiceElementPatterns.add(pattern);
                        }
                    }
                    if (choiceElementPatterns.size() > 0) {
                        for (Iterator<Pattern> it = choiceElementPatterns.iterator(); it.hasNext();) {
                            Pattern currentElementContentPattern = it.next();
                            findRecursiveElementsInPattern(currentElementContentPattern, currentParents);
                        }
                    }
                } else if (relaxNGPattern instanceof Interleave) {

                    // Case: Interleave

                    Interleave interleave = (Interleave) relaxNGPattern;
                    // System.out.println("Interleave");
                    LinkedList<Pattern> interleaveElementPatterns = new LinkedList<Pattern>();

                    for (Iterator<Pattern> it1 = interleave.getPatterns().iterator(); it1.hasNext();) {
                        Pattern pattern = it1.next();
                        if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                            interleaveElementPatterns.add(pattern);
                        }
                    }
                    if (interleaveElementPatterns.size() > 0) {
                        for (Iterator<Pattern> it = interleaveElementPatterns.iterator(); it.hasNext();) {
                            Pattern currentElementContentPattern = it.next();
                            findRecursiveElementsInPattern(currentElementContentPattern, currentParents);
                        }
                    }
                } else if (relaxNGPattern instanceof ParentRef) {

                    // Case: ParentRef

                    ParentRef parentRef = (ParentRef) relaxNGPattern;
                    // System.out.println("ParentRef: " + parentRef.getRefName());

                    if (!currentParents.contains(parentRef.getUniqueRefID())) {
                        currentParents.add(parentRef.getUniqueRefID());
                        LinkedList<Pattern> defineElementPatterns = new LinkedList<Pattern>();

                        for (Iterator<Define> it = parentRef.getDefineList().iterator(); it.hasNext();) {
                            Define define = it.next();
                            for (Iterator<Pattern> it1 = define.getPatterns().iterator(); it1.hasNext();) {
                                Pattern pattern = it1.next();
                                if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                                    defineElementPatterns.add(pattern);
                                }
                            }
                        }
                        if (defineElementPatterns.size() > 0) {
                            for (Iterator<Pattern> it = defineElementPatterns.iterator(); it.hasNext();) {
                                Pattern currentElementContentPattern = it.next();
                                findRecursiveElementsInPattern(currentElementContentPattern, currentParents);
                            }
                        }
                    } else {

                        int firstPositionOfDefineNameInParentList = currentParents.indexOf(parentRef.getRefName());
                        int countElementsInLoop = 0;
                        for (int i = currentParents.size() - 1; i > firstPositionOfDefineNameInParentList; i--) {
                            Object currentParentObject = currentParents.get(i);
                            if (currentParentObject instanceof Element) {
                                recursiveElements.add((Element) currentParentObject);
                                countElementsInLoop++;
                            }
                        }

                        this.refToGroupMap.put(parentRef.getUniqueRefID(), null);

                        if (countElementsInLoop < 1) {
                            throw new RecursionWithoutElementException();
                        }
                    }
                } else if (relaxNGPattern instanceof Mixed) {

                    // Case: Mixed

                    Mixed mixed = (Mixed) relaxNGPattern;
                    // System.out.println("Mixed");
                    LinkedList<Pattern> mixedElementPatterns = new LinkedList<Pattern>();

                    for (Iterator<Pattern> it1 = mixed.getPatterns().iterator(); it1.hasNext();) {
                        Pattern pattern = it1.next();
                        if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                            mixedElementPatterns.add(pattern);
                        }
                    }
                    if (mixedElementPatterns.size() > 0) {
                        for (Iterator<Pattern> it = mixedElementPatterns.iterator(); it.hasNext();) {
                            Pattern currentElementContentPattern = it.next();
                            findRecursiveElementsInPattern(currentElementContentPattern, currentParents);
                        }
                    }
                } else if (relaxNGPattern instanceof Grammar) {

                    // Case: Grammar

                    Grammar grammar = (Grammar) relaxNGPattern;
                    // System.out.println("Mixed");
                    LinkedList<Pattern> grammarElementPatterns = new LinkedList<Pattern>();

                    for (Iterator<Pattern> it1 = grammar.getStartPatterns().iterator(); it1.hasNext();) {
                        Pattern pattern = it1.next();
                        if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                            grammarElementPatterns.add(pattern);
                        }
                    }
                    if (grammarElementPatterns.size() > 0) {
                        for (Iterator<Pattern> it = grammarElementPatterns.iterator(); it.hasNext();) {
                            Pattern currentElementContentPattern = it.next();
                            findRecursiveElementsInPattern(currentElementContentPattern, currentParents);
                        }
                    }
                }
            }
        }
    }

    /**
     * Add a XML XSDSchema particle to a sequencePattern - avoid sequences in sequences
     * @param sequencePattern
     * @param particle
     */
    private void addParticleToSequencePattern(SequencePattern sequencePattern, Particle particle) {
        if (particle != null) {
            if (particle instanceof SequencePattern) {
                SequencePattern innerSequencePattern = (SequencePattern) particle;
                for (Iterator<Particle> it = innerSequencePattern.getParticles().iterator(); it.hasNext();) {
                    Particle innerParticle = it.next();
                    sequencePattern.addParticle(innerParticle);
                }
            } else {
                sequencePattern.addParticle(particle);
            }
        }
    }

    /**
     * Add a XML XSDSchema particle to a choicePattern - avoid choices in choices
     * @param choicePattern
     * @param particle
     */
    private void addParticleToChoicePattern(ChoicePattern choicePattern, Particle particle) {
        if (particle != null) {
            if (particle instanceof ChoicePattern) {
                ChoicePattern innerChoicePattern = (ChoicePattern) particle;
                for (Particle innerParticle: innerChoicePattern.getParticles()) {
                    choicePattern.addParticle(innerParticle);
                }
            } else {
                choicePattern.addParticle(particle);
            }
        }
    }



    /**
     * Method for putting a XML XSDSchema particle into a XML XSDSchema group
     *
     * @param localGroupName
     * @param namespace
     * @param particle
     * @return eu.fox7.schematoolkit.common.GroupRef
     * @throws ParticleIsNullException
     * @throws Exception
     */
    private eu.fox7.schematoolkit.xsd.om.Group putParticleIntoGroup(String namespace, String localGroupName, eu.fox7.schematoolkit.common.Particle particle) throws ParticleIsNullException, Exception {

    	if (this.usedLocalGroupNames.contains(localGroupName)) {
    		localGroupName = localGroupName + "_" + this.usedLocalGroupNames.size();
    	}

    	this.usedLocalGroupNames.add(localGroupName);

        QualifiedName fqGroupName = new QualifiedName(namespace,localGroupName);

        ImportedSchema importedSchema = null;

        Namespace targetNamespace = this.xmlSchema.getTargetNamespace();
        if (!namespace.equals(targetNamespace)) {
            // Generate Imported XSDSchema and attach it to the basis schema
            importedSchema = updateOrCreateImportedSchema(namespace);
        }

        ParticleContainer groupParticleContainer = null;

        Particle particleForCheck = null;

        if (particle instanceof CountingPattern) {
            particleForCheck = ((CountingPattern) particle).getParticle();
        } else {
            particleForCheck = particle;
        }

        QualifiedName typename = null;
        if (particleForCheck instanceof ParticleContainer) {
            groupParticleContainer = (ParticleContainer) particle;
        } else { //if (particleForCheck instanceof eu.fox7.schematoolkit.xsd.om.Element || particleForCheck instanceof eu.fox7.schematoolkit.common.AnyPattern || particleForCheck instanceof eu.fox7.schematoolkit.common.ElementRef) {
            SequencePattern sequencePattern = new SequencePattern();
            sequencePattern.addParticle(particle);
            groupParticleContainer = sequencePattern;

            if (particleForCheck instanceof eu.fox7.schematoolkit.xsd.om.Element) {
                eu.fox7.schematoolkit.xsd.om.Element currentElement = (eu.fox7.schematoolkit.xsd.om.Element) particleForCheck;
                typename = currentElement.getTypeName();
            }
        }

        if (groupParticleContainer != null) {
            eu.fox7.schematoolkit.xsd.om.Group group = new eu.fox7.schematoolkit.xsd.om.Group(fqGroupName, groupParticleContainer);
            this.xmlSchema.addGroup(group);

//            if (!usedTopLevelGroupNames.contains(group.getName())) {
//                if (importedSchema != null) {
//                    importedSchema.getSchema().getGroupSymbolTable().setReference(group.getName(), strGroup);
//                    importedSchema.getSchema().addGroup(strGroup);
//
//                    if (type != null) {
//                        importedSchema.getSchema().getTypeSymbolTable().setReference(type.getName(), this.xmlSchema.getTypeSymbolTable().getReference(type.getName()));
//                    }
//
//                } else {
//                    this.xmlSchema.addGroup(strGroup);
//                }
//                usedTopLevelGroupNames.add(group.getName());
//            }

            return group;
        } else {
            throw new ParticleIsNullException(fqGroupName.getFullyQualifiedName(), "see inner particle");
        }
    }

    /**
     * Method for adding a XML XSDSchema element to the RELAX NG to XSD Element map
     * @param rngElement    - key of the map
     * @param xsdElement    - XML XSDSchema element to store
     */
    private void addXSDElementToRNGElementMap(Element rngElement, eu.fox7.schematoolkit.xsd.om.Element xsdElement) {
        if (this.rngElementToxsdElements.get(rngElement) != null) {
            this.rngElementToxsdElements.get(rngElement).add(xsdElement);
        } else {
            HashSet<eu.fox7.schematoolkit.xsd.om.Element> tempHashSet = new HashSet<eu.fox7.schematoolkit.xsd.om.Element>();
            tempHashSet.add(xsdElement);
            this.rngElementToxsdElements.put(rngElement, tempHashSet);
        }
    }

    /**
     * Check a given patternlist for a ref that can be converted to a XML XSDSchema
     * complexType.
     * @param patternList       Source for the check
     * @return HashMap<Ref, LinkedList<Pattern>>        returns a HashMap with only one entry.
     *
     * The key is the found ref pattern that can be converted to a XML XSDSchema
     * complexType and as value there is a list of all other patterns not
     * containing this found ref. If there is no such ref, null is returned.
     */
    private HashMap<Ref, LinkedList<Pattern>> checkPatternListForRefForComplexType(LinkedList<Pattern> patternList) {
        // Initialize the return variable
        Ref ref = null;
        // The parentPattern is only used to determine which pattern is the direct parent of the possibly found ref
        Pattern parentPattern = null;
        boolean first = true;
        for (Iterator<Pattern> it = patternList.iterator(); it.hasNext();) {
            Pattern pattern = it.next();
            if ((pattern instanceof Ref)) {

                // Case: Ref

                ref = (Ref) pattern;
            } else if (pattern instanceof Choice) {

                // Case: Choice

                Choice choice = (Choice) pattern;
                if (choice.getPatterns().size() == 1) {
                    ref = checkPatternListForRefForComplexTypeRecursive(choice.getPatterns());
                }
            } else if (pattern instanceof Group) {

                // Case: Group

                Group group = (Group) pattern;
                if (group.getPatterns().size() == 1) {
                    ref = checkPatternListForRefForComplexTypeRecursive(group.getPatterns());
                }
            } else if (pattern instanceof Interleave) {

                // Case: Interleave

                Interleave interleave = (Interleave) pattern;
                if (interleave.getPatterns().size() == 1) {
                    ref = checkPatternListForRefForComplexTypeRecursive(interleave.getPatterns());
                }
            }
            if (ref != null && first) {
                parentPattern = pattern;
                break;
            }

            if (pattern != null && this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                first = false;
            }
        }

        // Build the returnvalue
        if (ref != null && (parentPattern != null)) {
            HashMap<Ref, LinkedList<Pattern>> returnMap = new HashMap<Ref, LinkedList<Pattern>>();
            patternList = this.removePatternFromPatternList(patternList, parentPattern);
            returnMap.put(ref, patternList);
            return returnMap;
        }
        return null;
    }

    /**
     * Recursive check of a given patternlist for a ref that can be converted to a XML XSDSchema
     * complexType.
     * @param patternList       Source for the check
     * @returnRef        returns the possibly found ref
     */
    private Ref checkPatternListForRefForComplexTypeRecursive(LinkedList<Pattern> patternList) {
        Ref ref = null;
        for (Iterator<Pattern> it = patternList.iterator(); it.hasNext();) {
            Pattern pattern = it.next();
            if ((pattern instanceof Ref)) {

                // Case: Ref

                ref = (Ref) pattern;
            } else if (pattern instanceof Choice) {

                // Case: Choice

                Choice choice = (Choice) pattern;
                if (choice.getPatterns().size() == 1) {
                    ref = checkPatternListForRefForComplexTypeRecursive(choice.getPatterns());
                }
            } else if (pattern instanceof Group) {

                // Case: Group

                Group group = (Group) pattern;
                if (group.getPatterns().size() == 1) {
                    ref = checkPatternListForRefForComplexTypeRecursive(group.getPatterns());
                }
            } else if (pattern instanceof Interleave) {

                // Case: Interleave

                Interleave interleave = (Interleave) pattern;
                if (interleave.getPatterns().size() == 1) {
                    ref = checkPatternListForRefForComplexTypeRecursive(interleave.getPatterns());
                }
            }
        }
        return ref;
    }

    /**
     * Method for removing a given pattern from a patternlist, if it is in there.
     * @param patternList       basis of the search and replace process
     * @param pattern           source for the search progress
     * @return LinkedList<Pattern>  a nes patternlist without the given pattern
     */
    private LinkedList<Pattern> removePatternFromPatternList(LinkedList<Pattern> patternList, Pattern pattern) {
        LinkedList<Pattern> newPatternList = new LinkedList<Pattern>();
        for (Iterator<Pattern> it = patternList.iterator(); it.hasNext();) {
            Pattern checkPattern = it.next();
            if (checkPattern != pattern) {
                newPatternList.add(checkPattern);
            }
        }
        return newPatternList;
    }

    /**
     * Generate recursivly a complexType and extension/base structure for a given ref
     * @param ref       source for the conversion
     * @return SymbolTableRef<Type>     the generated type
     */
    private ComplexType generateComplexTypeForRefRecursive(Ref ref) throws Exception {
//        Type resultType = null;
    	ComplexType refComplexType;
        if (refToTypeMap.get(ref.getUniqueRefID()) != null) {
        	refComplexType = refToTypeMap.get(ref.getUniqueRefID());
        } else {
            HashSet<String> refInformation = this.patternInformation.get(ref);
            HashSet<Pattern> refSet = new HashSet<Pattern>();
            refSet.add(ref);

            Namespace namespace =  ref.getAttributeNamespace();

            // Avoid creating types with the same name
            String refTypeName = ref.getRefName();
            if (this.usedLocalTypeNames.contains(refTypeName)) {
                refTypeName = refTypeName + this.usedLocalTypeNames.size();
            }
            this.usedLocalTypeNames.add(refTypeName);

            // Set a dummyComplexType complexType to the symbolTable for further recursive iterations
            refComplexType = new ComplexType(new QualifiedName(namespace,refTypeName), null, false);
            refComplexType.setIsAnonymous(false);
            this.refToTypeMap.put(ref.getUniqueRefID(), refComplexType);

            // Handle mixed/text content conversion
            if (refInformation.contains("mixed") || refInformation.contains("text") || refInformation.contains("data") || refInformation.contains("value")) {
                refComplexType.setMixed(true);
            }

            // Prohibit the use of the xsi:type-attribute in the instance for choosing the elements type
            refComplexType.addBlockModifier(ComplexTypeInheritanceModifier.Extension);
            refComplexType.addBlockModifier(ComplexTypeInheritanceModifier.Restriction);

            // Fetch all child patterns of a given ref from the subordinate define pattern
            LinkedList<Pattern> definePatterns = new LinkedList<Pattern>();
            Type refBaseType = null;
            if (ref.getDefineList().size() == 1) {
                definePatterns = ref.getDefineList().iterator().next().getPatterns();

                // Check if there is a new ref within the found child patterns
                HashMap<Ref, LinkedList<Pattern>> innerTypeMap = null;
                if (!definePatterns.isEmpty()) {
                    innerTypeMap = checkPatternListForRefForComplexType(definePatterns);
                }

                // Recurse to build a new type for the inner ref pattern
                if (innerTypeMap != null && !innerTypeMap.isEmpty() && innerTypeMap.keySet() != null) {
                    Ref innerRef = innerTypeMap.keySet().iterator().next();
                    refBaseType = this.generateComplexTypeForRefRecursive(innerRef);
                    definePatterns = innerTypeMap.get(innerRef);
                }
            } else {

                // There is no valid inner ref, so handle all patterns of the define correctly regarding the defined combine method
                PatternInformationCollector pic = new PatternInformationCollector(this.relaxng);
                java.util.List<Define> defineList = ref.getDefineList();
                CombineMethod combineMethod = null;

                LinkedList<Pattern> newDefinePatterns = new LinkedList<Pattern>();
                LinkedList<Pattern> resultDefinePatterns = new LinkedList<Pattern>();

                for (Define define: defineList) {
                    if (define.getCombineMethod() != null) {
                        combineMethod = define.getCombineMethod();
                    }
                    LinkedList<Pattern> defineElementPatterns = new LinkedList<Pattern>();
                    for (Iterator<Pattern> it1 = define.getPatterns().iterator(); it1.hasNext();) {
                        Pattern pattern = it1.next();
                        if (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element") || pattern instanceof Element) {
                            defineElementPatterns.add(pattern);
                        }
                    }
                    if (defineElementPatterns.size() > 1) {

                        // Add all patterns of one define to a group (this is a sequence in XSD)
                        // for the correct handling of the combine-method of the define-elements
                        Group group = new Group();
                        for (Iterator<Pattern> it2 = defineElementPatterns.iterator(); it2.hasNext();) {
                            Pattern innerPattern = it2.next();
                            group.addPattern(innerPattern);
                        }
                        newDefinePatterns.add(group);

                        HashSet<String> dataForNewPattern = pic.getDataForPattern(group);
                        this.patternInformation.put(group, dataForNewPattern);

                    } else if (defineElementPatterns.size() == 1) {
                        newDefinePatterns.add(defineElementPatterns.getFirst());
                    }
                }

                if (combineMethod.equals(CombineMethod.choice)) {
                    Choice choice = new Choice();
                    for (Iterator<Pattern> it = newDefinePatterns.iterator(); it.hasNext();) {
                        Pattern pattern = it.next();
                        choice.addPattern(pattern);
                    }
                    HashSet<String> dataForNewChoicePattern = pic.getDataForPattern(choice);
                    this.patternInformation.put(choice, dataForNewChoicePattern);
                    resultDefinePatterns.add(choice);
                } else {
                    Interleave interleave = new Interleave();
                    for (Iterator<Pattern> it = newDefinePatterns.iterator(); it.hasNext();) {
                        Pattern pattern = it.next();
                        interleave.addPattern(pattern);
                    }
                    HashSet<String> dataForNewInterleavePattern = pic.getDataForPattern(interleave);
                    this.patternInformation.put(interleave, dataForNewInterleavePattern);
                    resultDefinePatterns.add(interleave);
                }
                definePatterns = resultDefinePatterns;
            }

            // Handle the attribute content conversion for the ref
            LinkedList<eu.fox7.schematoolkit.common.AttributeParticle> refAttributeList = this.patternAttributeConverter.convertPatternListToAttributeParticleList(new HashSet<Pattern>(definePatterns));

            if (refBaseType == null) {
                for (AttributeParticle attributeParticle: refAttributeList) {
                    if (attributeParticle != null) {
                        refComplexType.addAttribute(attributeParticle);
                    }
                }
            }

            // Handle the element content conversion for the ref
            Group group = new Group();
            for (Pattern pattern: definePatterns) {
                group.addPattern(pattern);
            }

            PatternInformationCollector patternInformationCollector = new PatternInformationCollector(relaxng);
            this.patternInformation.put(group, patternInformationCollector.getDataForPattern(group));

            eu.fox7.schematoolkit.common.Particle refXSDParticle = null;
            eu.fox7.schematoolkit.common.Particle resultParticle = convertPatternToElementParticleStructure(group);
            if (resultParticle != null) {
                if (resultParticle instanceof eu.fox7.schematoolkit.xsd.om.Element || resultParticle instanceof eu.fox7.schematoolkit.common.ElementRef) {
                    SequencePattern sequencePattern = new SequencePattern();
                    sequencePattern.addParticle(resultParticle);
                    if (!sequencePattern.getParticles().isEmpty()) {
                        refXSDParticle = sequencePattern;
                    }
                } else {
                    refXSDParticle = resultParticle;
                }
            }

            // Adding the element particles to the content of the refComplexType
            if (refBaseType == null) {
                if (refXSDParticle != null) {
                    ComplexContentType complexContentType = new ComplexContentType(refXSDParticle, refComplexType.getMixed());
                    eu.fox7.schematoolkit.common.Particle myParticle = null;
                    if (refXSDParticle instanceof CountingPattern) {
                        myParticle = ((CountingPattern) refXSDParticle).getParticle();
                    } else {
                        myParticle = refXSDParticle;
                    }
                    if (!(myParticle instanceof SequencePattern) && !(myParticle instanceof ChoicePattern) && !(myParticle instanceof AllPattern) && !(myParticle instanceof GroupReference)) {
                        SequencePattern newSequencePattern = new SequencePattern();
                        newSequencePattern.addParticle(refXSDParticle);
                        complexContentType.setParticle(newSequencePattern);
                    } else {
                        complexContentType.setParticle(refXSDParticle);
                    }
                    refComplexType.setContent(complexContentType);
                }
            } else {
                // Extend ComplexType with attributes
                refBaseType.setIsAnonymous(false);
                ComplexContentExtension complexContentExtension = new ComplexContentExtension(refBaseType.getName());

                for (AttributeParticle attributeParticle: refAttributeList) {
                    if (attributeParticle != null) {
                        complexContentExtension.addAttribute(attributeParticle);
                    }
                }

                ComplexContentType complexContentType = new ComplexContentType(refXSDParticle, complexContentExtension, refComplexType.getMixed());
                eu.fox7.schematoolkit.common.Particle myParticle = null;

                if (refXSDParticle instanceof CountingPattern) {
                    myParticle = ((CountingPattern) refXSDParticle).getParticle();
                } else {
                    myParticle = refXSDParticle;
                }
                if (refXSDParticle != null && !(myParticle instanceof SequencePattern) && !(myParticle instanceof ChoicePattern) && !(myParticle instanceof AllPattern) && !(myParticle instanceof GroupReference)) {
                    SequencePattern newSequencePattern = new SequencePattern();
                    newSequencePattern.addParticle(myParticle);
                    complexContentType.setParticle(newSequencePattern);
                } else {
                    complexContentType.setParticle(myParticle);
                }
                refComplexType.setContent(complexContentType);
            }

            // Save generated ref type to global map!
            refToTypeMap.put(ref.getUniqueRefID(), refComplexType);
            refComplexType.setIsAnonymous(false);
            this.xmlSchema.addType(refComplexType);
        }
        return refComplexType;
    }

    /**
     * Check if it is useful to build a XML XSDSchema group from a given Define.
     * This allows only groups with more than one element content pattern.
     *
     * @param definePatterns    source for the check
     * @return boolean      returns true, if there is more than one element content pattern in the given patternlist
     */
    private boolean isDefinePatternListUsefulAsXSDGroup(LinkedList<Pattern> definePatterns) {
        boolean returnValue = true;
        if (definePatterns != null) {
            if (definePatterns.size() == 1) {

                if (((definePatterns.getFirst() instanceof OneOrMore) || (definePatterns.getFirst() instanceof ZeroOrMore) || (definePatterns.getFirst() instanceof Optional) || (definePatterns.getFirst() instanceof Ref) || (definePatterns.getFirst() instanceof Element))) {
                    returnValue = false;
                }
                if (definePatterns.getFirst() instanceof Group) {
                    Group group = (Group) definePatterns.getFirst();
                    int countElementPattern = 0;
                    for (Iterator<Pattern> it = group.getPatterns().iterator(); it.hasNext();) {
                        Pattern pattern = it.next();
                        if (pattern instanceof Element || (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element"))) {
                            countElementPattern++;
                        }
                    }
                    if (countElementPattern < 2) {
                        returnValue = false;
                    }
                }
                if (definePatterns.getFirst() instanceof Choice) {
                    Choice choice = (Choice) definePatterns.getFirst();
                    int countElementPattern = 0;
                    for (Iterator<Pattern> it = choice.getPatterns().iterator(); it.hasNext();) {
                        Pattern pattern = it.next();
                        if (pattern instanceof Element || (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element"))) {
                            countElementPattern++;
                        }
                    }
                    if (countElementPattern < 2) {
                        returnValue = false;
                    }
                }
                if (definePatterns.getFirst() instanceof Interleave) {
                    Interleave interleave = (Interleave) definePatterns.getFirst();
                    int countElementPattern = 0;
                    for (Iterator<Pattern> it = interleave.getPatterns().iterator(); it.hasNext();) {
                        Pattern pattern = it.next();
                        if (pattern instanceof Element || (this.patternInformation.get(pattern) != null && this.patternInformation.get(pattern).contains("element"))) {
                            countElementPattern++;
                        }
                    }
                    if (countElementPattern < 2) {
                        returnValue = false;
                    }
                }
            }
        } else {
            returnValue = false;
        }
        return returnValue;
    }

    /**
     * Getter for the set of found recursive elements within the RELAX NG schema.
     *
     * This set contains ALL recursive elements, even defined in nested grammars.
     * This is possible, because elements with the same name defined in different
     * grammars are different java objects and can both be fetched and put into
     * this set.
     *
     * @return HashSet<Element> - set of all recursivly defined elements under grammars
     */
    public HashSet<Element> getRecursiveElements() {
        return recursiveElements;
    }

}
