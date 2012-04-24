package uh.df.xsd.analysis;

import gjb.util.tree.Node;
import gjb.util.tree.Tree;
import gjb.xml.xsdanalyser.RegexAnalysis;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.map.HashedMap;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTerm;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDWildcard;

public class AdaptedRegexAnalysis extends RegexAnalysis {

	protected Map<String, Set<XSDTypeDefinition>> rules;

	public AdaptedRegexAnalysis(XSDSchema xsd) {
		super(xsd);
	}

	protected void initRulesMap() {
		if (rules == null)
			rules = new HashedMap<String, Set<XSDTypeDefinition>>();
	}

	public Map<String, Set<XSDTypeDefinition>> getRulesMap() {
		return rules;
	}

	@Override
	protected void regex(XSDElementDeclaration element) {
		initRulesMap();
		XSDTypeDefinition localTypeDef = element.getTypeDefinition();
		if (!regexes.containsKey(localTypeDef)) {
			regex(localTypeDef);
		}
		if (!rules.containsKey(element.getName()))
			rules.put(element.getName(), new HashSet<XSDTypeDefinition>());
		rules.get(element.getName()).add(localTypeDef);

	}

	@Override
	protected void regex(XSDTypeDefinition typeDef) {
		initRulesMap();
		if (typeDef instanceof XSDComplexTypeDefinition && !regexes.containsKey(typeDef) && !typesDone.contains(typeDef)) {
//			XSDComplexTypeDefinition complex = (XSDComplexTypeDefinition) typeDef;
//			System.out.println(complex.toString());
//			if (complex.getContent() != null)
//				System.out.println(complex.getContent().toString());
			
			typesDone.add(typeDef);
			Tree tree = new Tree();
			XSDParticle particle = typeDef.getComplexType();
			if (particle != null) {
//				System.out.println(particle.toString());
				String multiplicity = multiplicity(particle);
				if (multiplicity.equals(""))
					tree.setRoot(regex(particle.getTerm()));
				else {
					Node multiplicityNode = new Node(multiplicity);
					multiplicityNode.addChild(regex(particle.getTerm()));
					tree.setRoot(multiplicityNode);
				}
				regexes.put(typeDef, tree);
			}
			else {
				// wat nu?
			}
		} else {
			typesDone.add(typeDef);
			Tree tree = new Tree();
			Node node = new Node();
			node.setKey("EPSILON");
			tree.setRoot(node);
			regexes.put(typeDef, tree);
		}
	}

	@Override
	protected Node regex(XSDTerm term) {
//		System.out.println(term);

		if (term instanceof XSDElementDeclaration) {
			XSDElementDeclaration element = (XSDElementDeclaration) term;
			// System.out.println(element);
			XSDTypeDefinition localTypeDef = element.getTypeDefinition();
			// System.out.println(localTypeDef);

			/* ************************* */

			if (!rules.containsKey(element.getName()))
				rules.put(element.getName(), new HashSet<XSDTypeDefinition>());
			rules.get(element.getName()).add(localTypeDef);
			// String enaam = element.getQName();
			// String tnaam = localTypeDef.getQName().replace(':', '_');
			// System.out.println(enaam + XMLGrammar.QNAME_TYPE_SEPARATOR +
			// tnaam);

			/* ************************* */

			// System.out.println();
			regex(element);
			//return new Node(elementName(element), localTypeDef);
			return new Node(element.getName(), localTypeDef);
		} else if (term instanceof XSDModelGroup) {
			Node operatorNode = new Node();
			XSDModelGroup modelGroup = (XSDModelGroup) term;
			String operator = null;
			String compositor = modelGroup.getCompositor().getName();
			if (compositor.equals("sequence") || compositor.equals("all")) {
				operator = ".";
			} else if (compositor.equals("choice")) {
				operator = "|";
			}
			operatorNode.setKey(operator);
			for (Iterator<XSDParticle> contentsIt = modelGroup.getParticles()
					.iterator(); contentsIt.hasNext();) {
				XSDParticle contentParticle = contentsIt.next();
				String multiplicity = multiplicity(contentParticle);
				Node childNode = regex(contentParticle.getTerm());
				if (multiplicity.equals(""))
					operatorNode.addChild(childNode);
				else {
					Node multiplicityNode = new Node(multiplicity);
					multiplicityNode.addChild(childNode);
					operatorNode.addChild(multiplicityNode);
				}
			}
			return operatorNode;
		} else if (term instanceof XSDWildcard) {
			return new Node("EPSILON");
		} else {
			throw new RuntimeException();
		}
	}

}
