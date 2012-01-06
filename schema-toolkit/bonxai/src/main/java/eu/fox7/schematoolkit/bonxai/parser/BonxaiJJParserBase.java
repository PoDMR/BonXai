package eu.fox7.schematoolkit.bonxai.parser;

import eu.fox7.schematoolkit.bonxai.om.Bonxai;
import eu.fox7.schematoolkit.common.Namespace;

public abstract class BonxaiJJParserBase {
	protected Bonxai bonxai;
	
	protected BonxaiJJParserBase() {
		this.bonxai = new Bonxai();
	}
	
	protected Namespace getNamespace(String identifier) {
		return null;
	}
	
	
}
