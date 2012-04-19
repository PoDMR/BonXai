package eu.fox7.treeautomata.converter;


import java.util.Vector;

import eu.fox7.bonxai.typeautomaton.TypeAutomaton;
import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateDFA;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.factories.StateEliminationFactory;
import eu.fox7.schematoolkit.common.ChoicePattern;
import eu.fox7.schematoolkit.common.CountingPattern;
import eu.fox7.schematoolkit.common.EmptyPattern;
import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.common.SequencePattern;
import eu.fox7.schematoolkit.xsd.om.ComplexContentType;
import eu.fox7.schematoolkit.xsd.om.ComplexType;
import eu.fox7.schematoolkit.xsd.om.Element;
import eu.fox7.schematoolkit.xsd.om.Type;
import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.Tree;

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
	public Type convertContentAutomaton(StateDFA contentAutomaton, QualifiedName typename, State typeAutomatonState) {
        StateEliminationFactory factory = new StateEliminationFactory();
        System.err.println("Automaton: \n" + contentAutomaton.toString());

		Regex regex = factory.create(contentAutomaton, false);

		System.err.println("Regex-Tree: \n"+ regex.getTree());
    	return this.convertRegex(regex, typename, typeAutomatonState);
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Type convertRegex(Regex regex, QualifiedName typename, State typeAutomatonState) {	
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Particle convertRegex(Node<Object> node, State typeAutomatonState) {
		String key = node.getKey();
		if (key.equals(Regex.CONCAT_OPERATOR)) { //concatenation
			Vector<Particle> childParticles = new Vector<Particle>();
			for (Node child: node.getChildren()) {
				Particle childParticle = convertRegex(child, typeAutomatonState);
				if (childParticle == null)
					return null;
				if (!(childParticle instanceof EmptyPattern))
					childParticles.add(childParticle);
			}
			if (childParticles.size()==0)
				return new EmptyPattern();
			else if (childParticles.size()==1)
				return childParticles.firstElement();
			else
				return new SequencePattern(childParticles);
		} else if (key.equals(Regex.ZERO_OR_MORE_OPERATOR) || 
				   key.equals(Regex.ONE_OR_MORE_OPERATOR) || 
				   key.equals(Regex.ZERO_OR_ONE_OPERATOR)) { // counting patterns
			Particle childParticle = convertRegex(node.getChild(0), typeAutomatonState);
			if (childParticle == null)
				return null;
			if (childParticle instanceof EmptyPattern)
				return childParticle;
			int minOccurs = (key.equals(Regex.ONE_OR_MORE_OPERATOR))?1:0;
			Integer maxOccurs = (key.equals(Regex.ZERO_OR_ONE_OPERATOR))?1:null;
			return new CountingPattern(childParticle, minOccurs, maxOccurs);
		} else if (key.equals(Regex.UNION_OPERATOR)) { // or
			Vector<Particle> childParticles = new Vector<Particle>();
			boolean epsilon = false;
			for (Node child: node.getChildren()) {
				Particle childParticle = convertRegex(child, typeAutomatonState);
				if (childParticle == null) {}
				else if (childParticle instanceof EmptyPattern)
					epsilon = true;
				else
					childParticles.add(childParticle);
			}
			Particle particle;
			if (childParticles.size()==1)
				particle = childParticles.firstElement();
			else 
				particle = new ChoicePattern(childParticles); 
			if (epsilon)
				return new CountingPattern(particle, 0, 1);
			else
				return particle;
		} else if (key.equals(Regex.EPSILON_SYMBOL)) {
			return new EmptyPattern();
		} else if (key.equals(Regex.EMPTY_SYMBOL)) {
			return null;
		} else { // label //TODO other operators
			Element element =  new Element(QualifiedName.getQualifiedNameFromFQN(key));
			State childState;
			try {
				childState = this.typeAutomaton.getNextState(Symbol.create(key), typeAutomatonState);
			} catch (NotDFAException e) {
				throw new RuntimeException(e);
			}
			QualifiedName typename = this.typeAutomaton.getTypeName(childState);
			element.setTypeName(typename);
			return element;
		}
	}
}
