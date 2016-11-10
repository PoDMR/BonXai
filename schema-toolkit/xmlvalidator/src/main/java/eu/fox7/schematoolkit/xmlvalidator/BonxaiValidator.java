package eu.fox7.schematoolkit.xmlvalidator;

import java.util.HashMap;
import java.util.Map;

import eu.fox7.schematoolkit.Schema;
import eu.fox7.schematoolkit.SchemaToolkitException;
import eu.fox7.schematoolkit.bonxai.BonxaiManager;
import eu.fox7.schematoolkit.bonxai.om.Bonxai;
import eu.fox7.schematoolkit.common.AnonymousNamespace;
import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.treeautomata.converter.Bonxai2ContextAutomatonConverter;
import eu.fox7.treeautomata.om.ExtendedContextAutomaton;

public class BonxaiValidator extends AbstractXMLValidator {
	private BonxaiManager bonxaiManager;
	private Map<String,ExtendedContextAutomaton> namespaceCAMap = new HashMap<String,ExtendedContextAutomaton>();

	private static class SimpleBonxaiManager implements BonxaiManager {
		private Bonxai bonxai;
		
		private SimpleBonxaiManager(Bonxai bonxai) {
			this.bonxai = bonxai;
		}
		
		@Override
		public Bonxai getBonxaiForNamespace(Namespace namespace) {
			return namespace.equals(bonxai.getTargetNamespace())?bonxai:null;
		}
		
	}
	
	public BonxaiValidator(Bonxai schema) {
		this.bonxaiManager = new SimpleBonxaiManager(schema);
	}
	
	public BonxaiValidator() {
	}
	
	public void setSchema(Schema schema) throws SchemaToolkitException {
		try {
			this.bonxaiManager = new SimpleBonxaiManager((Bonxai) schema);
		} catch (ClassCastException e) {
			throw new SchemaToolkitException(e);
		}
	}
	
	public void setBonxaiManager(BonxaiManager bonxaiManager) {
		this.bonxaiManager = bonxaiManager;
	}
	
	@Override
	protected ExtendedContextAutomaton getContextAutomaton(String namespace) {
		ExtendedContextAutomaton contextAutomaton = this.namespaceCAMap.get(namespace);
		
		if (contextAutomaton == null) {
			Bonxai schema = this.bonxaiManager.getBonxaiForNamespace(new AnonymousNamespace(namespace)); 
			if (schema != null) {
				Bonxai2ContextAutomatonConverter converter = new Bonxai2ContextAutomatonConverter();
				contextAutomaton = converter.convert(schema);
				this.namespaceCAMap.put(namespace, contextAutomaton);
			}
		}
		
		return contextAutomaton;
	}
}
