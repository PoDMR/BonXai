package uh.df.xsd;

import eu.fox7.flt.treegrammar.XMLGrammar;
import gjb.util.tree.Node;
import gjb.util.tree.NodeSerializer;
import gjb.util.tree.SExpressionSerializer;
import gjb.util.tree.Serializer;
import gjb.util.tree.Tree;
import gjb.xml.xsdanalyser.Analyser;
import gjb.xml.xsdanalyser.RegexAnalysis;
import gjb.xml.xsdanalyser.TypeAnalysis;
import gjb.xml.xsdanalyser.TypeNameAnalysis;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.map.HashedMap;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;

import uh.df.grammar.MapleSupportedRegularTreeGrammarReader;
import uh.df.xsd.analysis.AdaptedRegexAnalysis;
import uh.df.xsd.analysis.AdaptedTypeNameAnalysis;
import uh.df.xsd.analysis.NoRootFoundException;

/**
 * 
 * @author Dominique Fonteyn
 * 
 */
public class XSDReader extends Analyser {

	protected Map<String, String> types;

	protected TypeAnalysis ta;

	protected RegexAnalysis ra;

	protected XSDSchema xsd;

	protected Serializer<String> serializer;

	protected NodeSerializer<String> nodeSerializer;
	
	protected String getProperElementName(String element) {
		return element.replace("#", "");
	}

	protected String getProperTypeName(XSDTypeDefinition type) {
		return types.get(ta.getTypeName(type)).replace("#", "");
	}

	protected String getProperRegex(XSDTypeDefinition type) {
		Tree regexTree = ra.getRegex(type);
		String regexStr = serializer.serialize(regexTree, nodeSerializer);
		if (!regexStr.equals("(EPSILON)")) {
			// replace types with shorter name
			for (String key : types.keySet()) {
				String searchKey = key + ")";
				if (regexStr.contains(searchKey)) {
					String replaceType = types.get(key);
					regexStr = regexStr.replace(key, replaceType);
				}
			}
			// adjust element names
			String element = null;
			for (Iterator<String> it = ta.getElementNameIterator(); it.hasNext();) {
				element = it.next();
				String searchKey = element + "#";
				if (regexStr.contains(searchKey)) {
					regexStr = regexStr.replace(searchKey, getProperElementName(element));
				}
			}

		}
		return regexStr;
	}

	private XSDSchema load(String fileName) {
		XSDSchema schema = null;
		try {
			schema = super.loadSchema(fileName);
		} catch (Exception e) {
			System.err.println("oeps");
			System.exit(-1);
		}
		return schema;
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 * @throws NoRootFoundException
	 */
	public XMLGrammar read(String dirToXsd) throws Exception {
		types = new HashedMap<String, String>();
		xsd = load(dirToXsd);
		RegexAnalysis regexAnalysis = new AdaptedRegexAnalysis(xsd); 
		TypeNameAnalysis typeNameAnalysis = new AdaptedTypeNameAnalysis(xsd);
		NodeSerializer<String> nodeSerializer = new AdaptedTypedRegexSerializer(typeNameAnalysis);
		Serializer<String> serializer = new SExpressionSerializer();
		XMLGrammar grammar = new XMLGrammar();

		Map<String, Set<XSDTypeDefinition>> rules = ((AdaptedRegexAnalysis) regexAnalysis).getRulesMap();

		StringBuilder str = new StringBuilder();
		String rootStr = identifyRoot();
		if (rootStr == null) {
			throw new NoRootFoundException();
		}
		str.append(rootStr + "\n");

		Set<String> uniqueRules = new HashSet<String>();

		for (Iterator<String> keyIt = rules.keySet().iterator(); keyIt.hasNext();) {
			String name = keyIt.next();
			for (Iterator<XSDTypeDefinition> typeDefIt = rules.get(name).iterator(); typeDefIt.hasNext();) {
				XSDTypeDefinition typeDef = typeDefIt.next();
				Tree regex = regexAnalysis.getRegex(typeDef);
				if (regex != null) {
					String tname = typeDef.getName();
					if (tname == null)
						tname = name;
					String rule = name + XMLGrammar.QNAME_TYPE_SEPARATOR + tname + " -> ; "
							+ serializer.serialize(regex, nodeSerializer) + "\n";
					uniqueRules.add(rule);

				}
			}
		}

		for (String rule : uniqueRules)
			str.append(rule);

		// print de xmlgrammar
		MapleSupportedRegularTreeGrammarReader reader = new MapleSupportedRegularTreeGrammarReader();
		StringReader strReader = new StringReader(str.toString());
		grammar = reader.readGrammar(strReader);
		return grammar;
	}

	private String identifyRoot() {
		// System.out.println("in root");
		EList<XSDElementDeclaration> elements = xsd.getElementDeclarations();
		// er is maar 1 root element --> ideaal!
		if (elements.size() == 1) {
			XSDElementDeclaration element = elements.get(0);
			String name = element.getTypeDefinition().getName();
			if (name == null)
				name = element.getName();
			return "root = " + element.getName() + XMLGrammar.QNAME_TYPE_SEPARATOR + name;
		}
		// multiple roots --> take union
		else if (elements.size() > 1) {
			Iterator<XSDElementDeclaration> it = elements.iterator();
			XSDElementDeclaration element = null;
			StringBuilder str = new StringBuilder();
			str.append("root = root#newRoot\nroot#newRoot -> ; (| ");
			while (it.hasNext()) {
				element = it.next();
				str.append("(");
				str.append(element.getName());
				str.append(XMLGrammar.QNAME_TYPE_SEPARATOR);
				String name = element.getTypeDefinition().getName();
				if (name == null)
					name = element.getName();
				str.append(name);
				str.append(") ");
			}
			str.append(")");
			return str.toString();
		}
		return null;
	}

	
	protected static class AdaptedTypedRegexSerializer implements NodeSerializer<String> {

		protected TypeNameAnalysis typeNameAnalysis;

		public AdaptedTypedRegexSerializer(TypeNameAnalysis typeNameAnalysis) {
			this.typeNameAnalysis = typeNameAnalysis;
		}

		public String serialize(Node node) {
			StringBuilder str = new StringBuilder();
			String nodeKey = node.key() != null ? node.key() : "node";
			str.append("(");
			str.append(nodeKey);
			if (node.value() != null) {
				XSDTypeDefinition typeDef = (XSDTypeDefinition) node.value();
				String typeName = typeDef.getName();
				if (typeName == null)
					typeName = nodeKey;
				str.append(XMLGrammar.QNAME_TYPE_SEPARATOR).append(typeName);
			}
			if (node.getNumberOfChildren() > 0) {
				for (Iterator<Node> it = node.children(); it.hasNext();)
					str.append(" ").append(serialize(it.next()));
			}
			str.append(")");
			return str.toString();
		}

	}	
}
