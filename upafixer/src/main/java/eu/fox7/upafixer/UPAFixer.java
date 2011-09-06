package eu.fox7.upafixer;

import eu.fox7.bonxai.typeautomaton.TypeAutomaton;
import eu.fox7.bonxai.xsd.XSDSchema;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.treeautomata.impl.ContentAutomaton;

public interface UPAFixer {
	public void fixUPA(XSDSchema xsdSchema);
	public Regex fixUPA(ContentAutomaton contentAutomaton);
	public void fixUPA(TypeAutomaton typeAutomaton);
}
