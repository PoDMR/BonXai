package gjb.flt.treegrammar.generators;

import gjb.flt.FLTRuntimeException;
import gjb.flt.regex.UnknownOperatorException;
import gjb.flt.regex.generators.LanguageGenerator;
import gjb.flt.treegrammar.MaxDepthExceededException;
import gjb.flt.treegrammar.SyntaxException;
import gjb.flt.treegrammar.UserObjectNotDefinedException;
import gjb.flt.treegrammar.XMLAttributeDefinition;
import gjb.flt.treegrammar.XMLElementDefinition;
import gjb.flt.treegrammar.XMLElementNotDefinedException;
import gjb.flt.treegrammar.XMLGrammar;
import gjb.flt.treegrammar.data.DataGenerator;
import gjb.math.ExponentialIntegerDistribution;
import gjb.math.IllDefinedDistributionException;
import gjb.math.ProbabilityDistribution;
import gjb.math.UserDefinedDistribution;
import gjb.util.tree.SExpressionParseException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;

public class XMLGenerator {

    /**
     * Default maximum depth of tenerated example documents
     */
    protected static final int MAX_EXAMPLE_DEPTH = 250;
    /**
     * Maps the name of a distribution (as in the decoration) to the corresponding
     * ProbabilityDistribution
     */
    protected Map<String,ProbabilityDistribution> distributionMap = new HashMap<String,ProbabilityDistribution>();
    /**
     * Maps the name of a generator (as in the decoration) to the corresponding
     * DataGenerator
     */
    protected Map<String,DataGenerator> generatorMap = new HashMap<String,DataGenerator>();
    /**
     * ProbabilityDistribution that determines the document's depth
     */
    protected ProbabilityDistribution depthDistribution = new ExponentialIntegerDistribution(5.0);
    /**
     * Maximum depth of the generated example documents
     */
    protected int maxDepth = MAX_EXAMPLE_DEPTH;
    /**
     * Maps XMLElementDefinitions to LanguageGenerators
     */
    Map<XMLElementDefinition,LanguageGenerator> elementGenerators = new HashMap<XMLElementDefinition,LanguageGenerator>();
    Map<XMLAttributeDefinition,ProbabilityDistribution> attrDistributions = new HashMap<XMLAttributeDefinition,ProbabilityDistribution>();
    Map<XMLAttributeDefinition,DataGenerator> attrGenerators = new HashMap<XMLAttributeDefinition,DataGenerator>();

    public XMLGenerator() {
    	super();
        SortedMap<Integer,Double> map = new TreeMap<Integer,Double>();
        map.put(0, 0.5);
        map.put(1, 0.5);
        try {
            distributionMap.put("default-zeroOrOne",
                                new UserDefinedDistribution(map));
        } catch (IllDefinedDistributionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to add a named ProbabilityDistribution to the document's distributions
     * @param name
     *            String the distribution's name as it occurs in decoration
     * @param distribution
     *            ProbabilityDistribution to add to the document
     */
    public void addDistribution(String name,
                                ProbabilityDistribution distribution) {
        distributionMap.put(name, distribution);
    }

    public Set<String> getDistributionNames() {
        return distributionMap.keySet();
    }

	public boolean hasDistribution(String distrName) {
        return distributionMap.containsKey(distrName);
    }

	/**
     * Method to retrieve a ProbabilityDistribution by name
     * @param name
     *            String the distribution's name as it occurs in decoration
     * @return ProbabilityDistribution
     * @throws UserObjectNotDefinedException
     *             thrown if no ProbabilityDistribution with the specified name can
     *             be found
     */
    public ProbabilityDistribution getDistribution(String name)
            throws UserObjectNotDefinedException {
        if (distributionMap.containsKey(name)) {
            return distributionMap.get(name);
        } else {
            throw new UserObjectNotDefinedException(name);
        }
    }

    /**
     * Method to add a named DataGenerator to the document's generators
     * @param name
     *            String the generator's name as it occurs in decoration
     * @param generator
     *            DataGenerator to add to the document
     */
    public void addGenerator(String name, DataGenerator generator) {
        generatorMap.put(name, generator);
    }

    public Set<String> getGeneratorNames() {
        return generatorMap.keySet();
    }

    public boolean hasGenreator(String name) {
    	return generatorMap.containsKey(name);
    }

    /**
     * Method to retrieve a DataGenerator by name
     * @param name
     *            String the generator's name as it occurs in decoration
     * @return DataGenerator
     * @throws UserObjectNotDefinedException
     *             thrown if no DataGenerator with the specified name can be found
     */
    public DataGenerator getGenerator(String name)
            throws UserObjectNotDefinedException {
        if (generatorMap.containsKey(name)) {
            return generatorMap.get(name);
        } else {
            throw new UserObjectNotDefinedException(name);
        }
    }

    /**
     * Method to set the ProbabilityDistribution governing the depth of the 
     * instantiated documents
     * @param distr
     *            ProbabilityDistribution to determine the document's depth
      */
    public void setDepthDistribution(ProbabilityDistribution distr) {
        depthDistribution = distr;
    }

    public void setDepthDistribution(String distrName)
            throws UserObjectNotDefinedException {
    	if (hasDistribution(distrName))
    		setDepthDistribution(getDistribution(distrName));
    	else
            throw new UserObjectNotDefinedException(distrName);
    }

	public boolean hasDepthDistribution() {
    	return depthDistribution != null;
    }

    /**
     * Method to retrieve the ProbabilityDistribution that determines the depth
     * @return ProbabilityDistribution that determine the document's depth
     */
    public ProbabilityDistribution getDepthDistribution() {
        return depthDistribution;
    }

    /**
     * Method to set the maximum depth of a document, an Exception is thrown during
     * the example generation if it is exceeded.
     * @param maxDepth
     *            int maximum depth of the document instances
     */
    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    /**
     * Method that returns the maximum depth of the generated example documents
     * @return int maximum depth
     */
    public int getMaxDepth() {
        return maxDepth	;
    }

    public LanguageGenerator addContentGenerator(XMLElementDefinition elemDef,
                                                 String contentModelStr)
            throws UnknownOperatorException, SExpressionParseException {
    	LanguageGenerator generator = new LanguageGenerator(contentModelStr);
		elementGenerators.put(elemDef, generator);
		return generator;
    }

    public boolean hasContentGenerator(XMLElementDefinition elemDef) {
    	return elementGenerators.containsKey(elemDef);
    }

    public LanguageGenerator getContentGenerator(XMLElementDefinition elemDef) {
    	return elementGenerators.get(elemDef);
    }

    public Set<String> getElementContentGeneratorNames() {
    	Set<String> elementIds = new HashSet<String>();
    	for (XMLElementDefinition elemDef : elementGenerators.keySet()) {
    		elementIds.add(elemDef.getName());
    	}
        return elementIds;
    }

	public void addContentGenerator(XMLAttributeDefinition attr,
                                    DataGenerator generator) {
    	attrGenerators.put(attr, generator);
    }

	public boolean hasContentGenerator(XMLAttributeDefinition attr) {
    	return attrGenerators.containsKey(attr);
    }

	public DataGenerator getContentGenerator(XMLAttributeDefinition attr) {
    	return attrGenerators.get(attr);
    }

	public void addContentDistribution(XMLAttributeDefinition attr,
                                       ProbabilityDistribution distribution) {
    	attrDistributions.put(attr, distribution);
    }

	public boolean hasContentDistribution(XMLAttributeDefinition attr) {
    	return attrDistributions.containsKey(attr);
    }

	public ProbabilityDistribution getContentDistribution(XMLAttributeDefinition attr) {
    	return attrDistributions.get(attr);
    }

	/**
     * Method to generate an XML Document conform to the model
     * @return Document instance of an XML document described by the XMLDocument
     */
    public Document generateExample(XMLGrammar xmlGrammar)
            throws SyntaxException, MaxDepthExceededException {
        XMLElementDefinition root = xmlGrammar.getRootElement();
        try {
            Element element = generateExample(xmlGrammar, root);
            return DocumentFactory.getInstance().createDocument(element);
        } catch (UnknownOperatorException e) {
            throw new SyntaxException("unknown operator", e);
        } catch (UserObjectNotDefinedException e) {
            throw new SyntaxException("undefined user object", e);
        } catch (XMLElementNotDefinedException e) {
            throw new SyntaxException(e.getMessage(), e);
        } catch (MaxDepthExceededException e) {
            throw e;
        }
    }

	/**
     * Method to generate an XML element example and add it to the given XML
     * document
     * @param doc
     *            Document XML document to add the element instance to
     * @return Element instance
     * @throws UnknownOperatorException
     *             thrown if an operator in the content model is undefined
     * @throws UserObjectNotDefinedException
     *             thrown if a generator or a distribution in the content model is
     *             undefined
     * @throws XMLElementNotDefinedException
     *             thrown if an element qname/type in the content model is undefined
     */
    protected Element generateExample(XMLGrammar xmlGrammar,
                                   XMLElementDefinition xmlElement)
            throws UnknownOperatorException, UserObjectNotDefinedException,
                   XMLElementNotDefinedException, MaxDepthExceededException {
    	Element root = DocumentFactory.getInstance().createElement("");
        return generateExample(xmlGrammar, xmlElement, root, 1);
    }

    /**
     * Method that gennerates the actual example, it keeps track of the depth in
     * the instantiated document.
     * @param doc
     *            Document XML document the example is to be part of
     * @param depth
     *            int current depth in the generated XML document
     * @return Element XML element instance
     * @throws UnknownOperatorException
     *             thrown if an operator in the content model is undefined
     * @throws UserObjectNotDefinedException
     *             thrown if a generator or a distribution in the content model is
     *             undefined
     * @throws XMLElementNotDefinedException
     *             thrown if an element qname/type in the content model is undefined
     */
    protected Element generateExample(XMLGrammar xmlGrammar,
                                      XMLElementDefinition xmlElement,
                                      Element element, int depth)
            throws UnknownOperatorException, UserObjectNotDefinedException,
                   XMLElementNotDefinedException, MaxDepthExceededException {
    	element.setQName(new QName(xmlElement.getQName()));
        /* generate instances of the attributes */
        for (XMLAttributeDefinition attr : xmlElement.getAttributes())
            setExample(element, attr);
        /* if the recursion goes beyond maxDepth, abort with an exception */
        if (depth > getMaxDepth()) {
            throw new MaxDepthExceededException(getMaxDepth());
        }
        /* determine the depth threshold from the depth probability distribution */
        int depthThreshold = getDepthDistribution().getNext();
        /* if the depth is larger than or equal to the threshold and the element
         * can be empty, generate an empty element, otherwise, generate contents
         */
        if (depth < depthThreshold || !xmlElement.acceptsEpsilon) {
            for (String elementName : getContentGenerator(xmlElement).generateRandomExample()) {
                /* if the content item is text, generate the appropriate value
                 * using the DataGenerator
                 */
                if (!XMLElementDefinition.isTextType(elementName)) {
                    XMLElementDefinition childXMLElement = xmlGrammar.getElement(elementName);
                    Element childElement = element.addElement("");
                    generateExample(xmlGrammar, childXMLElement, childElement,
                                    depth + 1);
                } else {
                    DataGenerator generator = getGenerator(elementName.substring(1));
                    element.addText(generator.getData());
                }
            }
        }
        return element;
    }

    /**
     * Method that adds the attribute to its element according to the optionality
     * and the specified distribution.  Note that this interface is an artefact of
     * the XML library implementation.
     * @param element
     *            Element XML element to add the attribute to
     */
    public void setExample(Element element, XMLAttributeDefinition attr) {
        if (attr.isOptional()) {
        	ProbabilityDistribution distribution = attrDistributions.get(attr);
            if (distribution.getNext() == 1) {
                element.addAttribute(attr.getQName(), getExampleValue(attr));
            }
        } else {
            element.addAttribute(attr.getQName(), getExampleValue(attr));
        }
    }

    /**
     * Method that computes the attribute's value according to its generator.
     * @return String attribute's value
     */
    protected String getExampleValue(XMLAttributeDefinition attr) {
    	DataGenerator generator = getContentGenerator(attr);
        return generator.getData();
    }


	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("max. depth: ").append(getMaxDepth()).append("\n");
		if (hasDepthDistribution()) {
			str.append("depth distribution: ");
			for (String distrName : getDistributionNames())
	            try {
	                if (getDepthDistribution() == getDistribution(distrName)) {
	                	str.append(distrName);
	                	break;
	                }
                } catch (UserObjectNotDefinedException e) {
	                e.printStackTrace();
	                throw new FLTRuntimeException("this can't happen", e);
                }
			str.append("\n");
		}
		str.append("distributions: ");
		str.append(StringUtils.join(getDistributionNames(), ", "));
		str.append("\n");
		str.append("generators: ");
		str.append(StringUtils.join(getGeneratorNames(), ", "));
		str.append("\n");
		str.append("content models for: ");
		str.append(StringUtils.join(getElementContentGeneratorNames(), ", "));
		str.append("\n");
		return str.toString();
	}

}
