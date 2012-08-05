package eu.fox7.schematoolkit.bonxai;

import eu.fox7.schematoolkit.bonxai.om.Bonxai;
import eu.fox7.schematoolkit.common.Namespace;

public interface BonxaiManager {
	public Bonxai getBonxaiForNamespace(Namespace namespace);
}
