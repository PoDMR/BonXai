package eu.fox7.treeautomata.converter;

import gjb.flt.automata.NotDFAException;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.StateNFA;
import gjb.flt.automata.impl.sparse.Symbol;
import gjb.flt.regex.Regex;
import gjb.flt.regex.UnknownOperatorException;
import gjb.flt.regex.infer.rwr.NoOpportunityFoundException;
import gjb.flt.regex.infer.rwr.RewriteEngine;
import gjb.flt.regex.infer.rwr.Rewriter;
import gjb.flt.regex.infer.rwr.impl.Automaton;
import gjb.flt.regex.infer.rwr.impl.GraphAutomatonFactory;
import gjb.flt.treeautomata.impl.ContentAutomaton;
import gjb.util.tree.Node;
import gjb.util.tree.SExpressionParseException;
import gjb.util.tree.Tree;

import java.util.Vector;

import de.tudortmund.cs.bonxai.common.ChoicePattern;
import de.tudortmund.cs.bonxai.common.CountingPattern;
import de.tudortmund.cs.bonxai.common.EmptyPattern;
import de.tudortmund.cs.bonxai.common.Particle;
import de.tudortmund.cs.bonxai.common.SequencePattern;
import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import de.tudortmund.cs.bonxai.typeautomaton.TypeAutomaton;
import de.tudortmund.cs.bonxai.xsd.ComplexContentType;
import de.tudortmund.cs.bonxai.xsd.ComplexType;
import de.tudortmund.cs.bonxai.xsd.Element;
import de.tudortmund.cs.bonxai.xsd.Type;

public class ContentAutomaton2TypeConverter {
	private TypeAutomaton typeAutomaton;

	public ContentAutomaton2TypeConverter(TypeAutomaton typeAutomaton) {
		this.typeAutomaton = typeAutomaton;
	}
	
	/**
	 * Converts a content automaton to an XML Schema type.
	 * The childs must have associated typeRefs in the TypeAutomaton
	 * @param contentAutomaton
	 * @return
	 */
	public Type convertContentAutomaton(ContentAutomaton contentAutomaton, String typename, State typeAutomatonState) {
		StateNFA nfa = contentAutomaton;

        GraphAutomatonFactory gaFactory = new GraphAutomatonFactory();
        System.err.println("Automaton: " + nfa.toString());
        Automaton automaton = gaFactory.create(nfa);
        RewriteEngine rewriter = new Rewriter();
        String regexStr;
        try {
			regexStr = rewriter.rewriteToRegex(automaton);
		} catch (NoOpportunityFoundException e) {
			throw new RuntimeException(e);
		}
		
		Regex regex;
		try {
			regex = new Regex(regexStr);
		} catch (UnknownOperatorException e) {
			throw new RuntimeException(e);
		} catch (SExpressionParseException e) {
			throw new RuntimeException(e);
		}

		System.err.println("Regex-Tree: "+ regex.getTree());
    	return this.convertRegex(regex, typename, typeAutomatonState);
	}
	
	
	public Type convertRegex(Regex regex, String typename, State typeAutomatonState) {	
    	// create particle from regex
    	Tree regexTree = regex.getTree();
    	Node root = regexTree.getRoot();
    	Particle particle = convertRegex(root, typeAutomatonState);

    	ComplexContentType content = new ComplexContentType(particle, true);
		Type type = new ComplexType(typename, content);
		type.setIsAnonymous(false);
		content.setMixed(true);
		return type;
	}
	
	/**
	 * converts a regular expression (FLT) to a particle structure (Bonxai)
	 * @param node
	 * @return
	 */
	private Particle convertRegex(Node node, State typeAutomatonState) {
		String key = node.getKey();
		if (key.equals(Regex.CONCAT_OPERATOR)) { //concatenation
			Vector<Particle> childParticles = new Vector<Particle>();
			for (Node child: node.getChildren()) {
				Particle childParticle = convertRegex(child, typeAutomatonState);
				childParticles.add(childParticle);
			}
			return new SequencePattern(childParticles);
		} else if (key.equals(Regex.ZERO_OR_MORE_OPERATOR)) { // kleene star
			return new CountingPattern(convertRegex(node.getChild(0), typeAutomatonState), 0, null);
		} else if (key.equals(Regex.ONE_OR_MORE_OPERATOR)) { // plus operator
			return new CountingPattern(convertRegex(node.getChild(0), typeAutomatonState), 1, null);
		} else if (key.equals(Regex.UNION_OPERATOR)) { // or
			Vector<Particle> childParticles = new Vector<Particle>();
			for (Node child: node.getChildren()) {
				Particle childParticle = convertRegex(child, typeAutomatonState);
				childParticles.add(childParticle);
			}
			return new ChoicePattern(childParticles);
		} else if (key.equals(Regex.EPSILON_SYMBOL)) {
			return new EmptyPattern();
		} else if (key.equals(Regex.EMPTY_SYMBOL)) {
			throw new RuntimeException("Empty no supported");
		} else if (key.equals(Regex.ZERO_OR_ONE_OPERATOR)) {
			return new CountingPattern(convertRegex(node.getChild(0), typeAutomatonState), 0, 1);
		} else { // label //TODO other operators
			//TODO (no namespace?)
//			String name = key.substring(key.lastIndexOf(":") + 1);
//			String namespace = key.substring(1, key.lastIndexOf(":"));
			String name = key;
			String namespace = "";
			Element element =  new Element(namespace, name);
			State childState;
			try {
				childState = this.typeAutomaton.getNextState(Symbol.create(key), typeAutomatonState);
			} catch (NotDFAException e) {
				throw new RuntimeException(e);
			}
			SymbolTableRef<Type> childType = this.typeAutomaton.getType(childState);
			element.setType(childType);
			element.setTypeAttr(true);
			return element;
		}
	}
}
