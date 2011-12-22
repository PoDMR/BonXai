package eu.fox7.bonxai.bonxai.parser;

import eu.fox7.bonxai.bonxai.Bonxai;
import eu.fox7.bonxai.common.Namespace;

public abstract class BonxaiJJParserBase {
	protected Bonxai bonxai;
	
	protected BonxaiJJParserBase() {
		this.bonxai = new Bonxai();
	}
	
	protected Namespace getNamespace(String identifier) {
		return null;
	}
	
	
}
