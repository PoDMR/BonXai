package eu.fox7.schematoolkit.converter.xsd2bonxai;

import java.util.Map;

import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.schematoolkit.bonxai.om.AncestorPattern;
import eu.fox7.schematoolkit.common.NamespaceList;
import eu.fox7.schematoolkit.typeautomaton.TypeAutomaton;

public interface AncestorPatternBuilder {

	Map<State,AncestorPattern> buildAncestorPatterns(TypeAutomaton typeAutomaton, NamespaceList namespaceList);
	
}
