package eu.fox7.schematoolkit.typeautomaton.factories;

import eu.fox7.schematoolkit.bonxai.om.Expression;
import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.QualifiedName;

public class TrivialTypeNameGenerator implements TypeNameGenerator {

	private int count=1;
	private Namespace namespace;
	
	public TrivialTypeNameGenerator(Namespace namespace) {
		this.namespace = namespace;
	}
	
	@Override
	public QualifiedName generateTypeName(Expression ex) {
		return new QualifiedName(namespace,"Type"+(count++));
	}

}
