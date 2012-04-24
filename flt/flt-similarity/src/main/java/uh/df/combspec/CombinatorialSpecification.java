package uh.df.combspec;

import gjb.flt.grammar.CFG;
import gjb.flt.grammar.NonExistingNonTerminalException;
import gjb.flt.grammar.SyntaxErrorException;

import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import uh.df.maple.MapleCommandBuilder;



/**
 * 
 * @author Dominique Fonteyn
 *
 */
public class CombinatorialSpecification extends CFG {

	protected Set<String> atoms;
	protected Set<String> epsilonSet;

	/**
	 * 
	 * @param reader
	 * @throws SyntaxErrorException
	 * @throws IOException
	 */
	public CombinatorialSpecification(Reader reader) throws IOException, SyntaxErrorException {
		super(reader);
		atoms = new HashSet<String>();
		epsilonSet = new HashSet<String>();
	}

	/**
	 * 
	 * @return
	 */
	public String toMapleFormat() {
		StringBuilder builder = new StringBuilder();
		builder.append(getName()).append(" := {");

		for (String nt : nonTerminals()) {
			try {
				Set<String[]> rules = productions(nt);
				if (rules.size() > 1) {
					builder.append(nt).append("=");
					for (Iterator<String[]> it = rules.iterator(); it.hasNext();) {
						String[] rule = it.next();
						if (it.hasNext())
							builder.append(MapleCommandBuilder.UNION + "(").append(printArray(rule)).append(", ");
						else
							builder.append(printArray(rule)).append(")");
					}
				} else {
					builder.append(nt).append("=").append(printArray(rules.iterator().next()));
				}
				builder.append(", ");
			} catch (NonExistingNonTerminalException e) {
				e.printStackTrace();
			}
		}

		for (String atom : atoms) {
			builder.append(atom.toLowerCase()).append("=Atom, ");
		}

		builder.append(MapleCommandBuilder.LEFTBRACE).append("=Atom, ").append(MapleCommandBuilder.RIGHTBRACE).append(
				"=Atom");
		builder.append("}");
		return builder.toString();
	}

	/**
	 * 
	 * @param ruleArray
	 * @return
	 */
	private String printArray(String[] ruleArray) {
		String rule = "";
		for (String r : ruleArray)
			rule += r;
		return rule;
	}

	/**
	 * 
	 * @return
	 */
	public String getName() {
		return "sys";
	}

	/**
	 * 
	 * @return
	 */
	public Set<String> getEpsilonSet() {
		return epsilonSet;
	}

	/**
	 * 
	 * @param atoms
	 */
	public void setAtoms(Set<String> atoms) {
		this.atoms = atoms;
	}

	/**
	 * 
	 * @return
	 */
	public Set<String> getAtoms() {
		return atoms;
	}

	/**
	 * 
	 * @param epsilonSet
	 */
	public void setEpsilonSet(Set<String> epsilonSet) {
		this.epsilonSet = epsilonSet;
	}
}
