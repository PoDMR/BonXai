package eu.fox7.upafixer;

import eu.fox7.schematoolkit.SchemaToolkitException;
import eu.fox7.schematoolkit.typeautomaton.TypeAutomaton;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.regex.Regex;

public interface UPAFixer extends eu.fox7.schematoolkit.UPAFixer {
	public Regex fixUPA(StateNFA contentAutomaton) throws SchemaToolkitException;
	public void fixUPA(TypeAutomaton typeAutomaton) throws SchemaToolkitException;
}
