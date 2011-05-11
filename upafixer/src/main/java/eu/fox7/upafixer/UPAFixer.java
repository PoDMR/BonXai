package eu.fox7.upafixer;

import de.tudortmund.cs.bonxai.typeautomaton.TypeAutomaton;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import gjb.flt.regex.Regex;
import gjb.flt.treeautomata.impl.ContentAutomaton;

public interface UPAFixer {
	public void fixUPA(XSDSchema xsdSchema);
	public Regex fixUPA(ContentAutomaton contentAutomaton);
	public void fixUPA(TypeAutomaton typeAutomaton);
}
