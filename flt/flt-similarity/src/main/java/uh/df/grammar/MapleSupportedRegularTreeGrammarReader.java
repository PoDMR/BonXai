package uh.df.grammar;

import gjb.flt.regex.generators.LanguageGenerator;
import gjb.flt.treegrammar.SyntaxException;
import gjb.flt.treegrammar.XMLAttributeDefinition;
import gjb.flt.treegrammar.XMLElementDefinition;
import gjb.flt.treegrammar.XMLGrammar;
import gjb.flt.treegrammar.generators.XMLGenerator;
import gjb.flt.treegrammar.io.RegularTreeGrammarReader;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author Dominique Fonteyn
 *
 */
public class MapleSupportedRegularTreeGrammarReader extends RegularTreeGrammarReader {

	public static final String[] KEYWORDS = {
		"series", 
		"type", 
		"name", 
		"protected", 
		"limit", 
		"time", 
		"content", 
		"info", 
		"length", 
		"root", 
		"version", 
		"alias", 
		"array",
		"lb",
		"rb",
		"order",
		"point",
		"from",
		"use", 
		"description", 
		"group", 
		"list",
		"relation",
		"example",
		"product",
		"add",
		"remove",
		"diff",
		"member",
		"interface",
		"boolean", 
		"integer", 
		"string",
		"system", 
		"context",
		"apply",
		"plot",
		"package",
		"exists",
		"forall",
		"label",
		"related",
		"equation",
		"set",
		"queue",
		"cputime",
		"constant",
		"help",
		"index",
		"floor",
		"range",
		"symbol",
		"positive",
		"sum",
		"table",
		"map",
		"about",
		"symbol",
		"sec",
		"min",
		"copy",
		"print",
		"function",
		"profile"
	};
	
	protected Set<String> mapleKeywords;

	public MapleSupportedRegularTreeGrammarReader() {
		super();
		analyseMapleKeywords();
	}

	public MapleSupportedRegularTreeGrammarReader(XMLGenerator generator) {
		super(generator);
		analyseMapleKeywords();
	}

	protected void analyseMapleKeywords() {
		mapleKeywords = new HashSet<String>(KEYWORDS.length);
		for (String keyword : KEYWORDS)
			mapleKeywords.add(keyword);
	}

	@Override
	public XMLGrammar readGrammar(Reader grammarReader) throws SyntaxException {
		XMLGrammar xmlGrammar = new XMLGrammar();
		BufferedReader bReader = new BufferedReader(grammarReader);
		int lineNumber = 0;
		String line = null;
		try {
			while ((line = bReader.readLine()) != null) {
				lineNumber++;
				line = line.trim().toLowerCase();
				line = line.replace("(epsilon)", "(EPSILON)");
				if (line.matches(COMMENT_PATTERN) || line.length() == 0) {
					continue;
				} else if (line.matches(ROOT_DEF_PATTERN)) {
					line = line.substring(line.indexOf("=") + 1).trim();
					String[] parts = line.split(QNAME_TYPE_SEPARATOR, 2);
					String qName = getMapleConsistentName(parts[0]);
					String type = parts[1];
					int i = -1;
					if ((i = type.indexOf("[")) >= 0) {
						String distrName = type.substring(i + 1, type.length() - 1).trim();
						type = type.substring(0, i).trim();
						xmlGenerator.setDepthDistribution(distrName);
					}
					xmlGrammar.setRootElement(qName, type);
				} else {
					String[] parts = line.split(LHS_RHS_RULE_SEP_PATTERN, 2);
					String lhs = parts[0];
					String rhs = parts[1];
					parts = lhs.split(QNAME_TYPE_SEPARATOR, 2);
					String qName = getMapleConsistentName(parts[0]);
					String type = parts[1];
					parts = rhs.split(ATTR_CONTENT_MODEL_SEP_PATTERN, 2);
					String attrStr = parts[0];
					String contentModelStr = getMapleConsistentName(parts[1]);
					List<XMLAttributeDefinition> attributes = computeAttributes(xmlGrammar, attrStr);
					XMLElementDefinition elemDef = xmlGrammar.addGrammarRule(qName, type, attributes, contentModelStr);

					LanguageGenerator contentModel = xmlGenerator.addContentGenerator(elemDef, contentModelStr);
					for (String distrName : xmlGenerator.getDistributionNames())
						contentModel.addDistribution(distrName, xmlGenerator.getDistribution(distrName));
				}
			}
		} catch (Exception e) {
			throw new SyntaxException("exception while reading grammar at line " + lineNumber + ": " + e.getMessage(),
					e);
		}
		return xmlGrammar;
	}

	protected String getMapleConsistentName(String element) {
		if (element.contains("(")) {
			for (String keyword : mapleKeywords) {
				if (element.contains("(" + keyword + QNAME_TYPE_SEPARATOR)) {
					return (element.replace("(" + keyword + QNAME_TYPE_SEPARATOR, "(" + keyword + "_"
							+ QNAME_TYPE_SEPARATOR));
				}
			}
		} else if (mapleKeywords.contains(element))
			return (element + "_");
		return element;
	}

}
